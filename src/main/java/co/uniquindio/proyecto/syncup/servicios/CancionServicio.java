package co.uniquindio.proyecto.syncup.servicios;

import co.uniquindio.proyecto.syncup.entidades.Cancion;
import co.uniquindio.proyecto.syncup.entidades.Usuario;

import co.uniquindio.proyecto.syncup.grafos.NodoCancion;
import co.uniquindio.proyecto.syncup.repositorios.CancionRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
public class CancionServicio {

    private final CancionRepositorio cancionRepositorio;
    private final TrieServicio trieServicio;
    private final GrafoSimilitudServicio grafoSimilitudServicio;

    public CancionServicio(CancionRepositorio cancionRepositorio,
                           TrieServicio trieServicio,
                          GrafoSimilitudServicio grafoSimilitudServicio) {
        this.cancionRepositorio = cancionRepositorio;
        this.trieServicio = trieServicio;
        this.grafoSimilitudServicio = grafoSimilitudServicio;
        construirGrafoSimilitud();
    }

    public void construirGrafoSimilitud() {
        List<Cancion> canciones = cancionRepositorio.findAll();
        grafoSimilitudServicio.construirGrafo(canciones);
    }


    public Cancion agregarCancion(Cancion cancion) {
        Cancion cGuardada = cancionRepositorio.save(cancion);
        try {
            trieServicio.insertarTitulo(cGuardada.getTitulo());
        } catch (Exception e) {
            System.err.println("Error al insertar en trie: " + e.getMessage());
        }

        try {
            grafoSimilitudServicio.agregarNodo(cGuardada);
        } catch (Exception e) {
            System.err.println("Error al agregar nodo al grafo: " + e.getMessage());
        }

        System.out.println("Canción guardada con ID: " + cGuardada.getId());
        return cGuardada;
    }


    public Cancion actualizarCancion(Long id, Cancion nuevaCancion) {
        Cancion c = cancionRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Canción no encontrada"));
        if (nuevaCancion.getTitulo() != null) c.setTitulo(nuevaCancion.getTitulo());
        if (nuevaCancion.getArtista() != null) c.setArtista(nuevaCancion.getArtista());
        if (nuevaCancion.getGenero() != null) c.setGenero(nuevaCancion.getGenero());
        if (nuevaCancion.getAnio() != null) c.setAnio(nuevaCancion.getAnio());
        if (nuevaCancion.getDuracion() != null) c.setDuracion(nuevaCancion.getDuracion());
        if (nuevaCancion.getRutaArchivo() != null) c.setRutaArchivo(nuevaCancion.getRutaArchivo());
        cancionRepositorio.save(c);
        construirGrafoSimilitud();
        return c;
    }


    public void eliminarCancion(Long id) {
        cancionRepositorio.deleteById(id);
        construirGrafoSimilitud();
    }

    public List<Cancion> listarTodas() {
        return cancionRepositorio.findAll();
    }

    public List<Cancion> descubrimientoSemanal(Usuario usuario, int max) {
        // Evitar error si no tiene favoritos
        if (usuario.getListaFavoritos() == null || usuario.getListaFavoritos().isEmpty()) {
            return new ArrayList<>();
        }

        Set<Cancion> favoritos = new HashSet<>(usuario.getListaFavoritos());
        Set<Cancion> recomendadas = new LinkedHashSet<>();

        for (Cancion fav : favoritos) {
            // Evitar error si el grafo no está inicializado
            if (grafoSimilitudServicio.getGrafo() == null) {
                throw new RuntimeException("El grafo de similitud no está inicializado");
            }

            NodoCancion nodo = grafoSimilitudServicio.getGrafo().obtenerNodo(fav);
            if (nodo == null) continue; // Si la canción no está en el grafo

            Map<NodoCancion, Double> similares = grafoSimilitudServicio.dijkstra(nodo);

            similares.entrySet().stream()
                    .sorted((a, b) -> Double.compare(b.getValue(), a.getValue())) // mayor similitud primero
                    .map(entry -> entry.getKey().getCancion())
                    .filter(c -> !favoritos.contains(c))
                    .forEach(recomendadas::add);

            if (recomendadas.size() >= max) break;
        }

        List<Cancion> listaFinal = new ArrayList<>(recomendadas);
        return listaFinal.subList(0, Math.min(max, listaFinal.size()));
    }

    public Queue<Cancion> iniciarRadio(Cancion cancionInicio, int max) {
        Queue<Cancion> cola = new LinkedList<>();
        cola.add(cancionInicio);
        NodoCancion nodo = grafoSimilitudServicio.getGrafo()
                .obtenerNodo(cancionInicio);
        Map<NodoCancion, Double> similares = grafoSimilitudServicio.dijkstra(nodo);
        similares.entrySet().stream()
                .sorted(Map.Entry.comparingByValue()) // ordena de menor a mayor
                .map(entry -> entry.getKey().getCancion())
                .filter(c -> !c.equals(cancionInicio))
                .limit(max)
                .forEach(cola::add);

        return cola;
    }

    public List<String> autocompletar(String prefijo) {
        return trieServicio.autocompletar(prefijo);
    }

    public String guardarArchivo(MultipartFile archivo) {
        try {
            Path destino = Paths.get("C:/Users/MI PC/Desktop/quinto_semestre/estructura de datos/proyectoFinal/syncup/media")
                    .resolve(archivo.getOriginalFilename());
            Files.copy(archivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
            return destino.toString();
        } catch (IOException e) {
            throw new RuntimeException("Error guardando archivo: " + e.getMessage());
        }
    }

}