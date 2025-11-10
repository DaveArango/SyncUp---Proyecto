package co.uniquindio.proyecto.syncup.servicios;

import co.uniquindio.proyecto.syncup.entidades.Cancion;
import co.uniquindio.proyecto.syncup.entidades.Usuario;

import co.uniquindio.proyecto.syncup.grafos.NodoCancion;
import co.uniquindio.proyecto.syncup.repositorios.CancionRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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


    public Cancion actualizarCancion(Long id,
                                     Cancion nuevaCancion) {
        Cancion c = cancionRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Canción no encontrada"));
        c.setTitulo(nuevaCancion.getTitulo());
        c.setArtista(nuevaCancion.getArtista());
        c.setGenero(nuevaCancion.getGenero());
        c.setAnio(nuevaCancion.getAnio());
        c.setDuracion(nuevaCancion.getDuracion());
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

    public List<Cancion> descubrimientoSemanal(Usuario usuario,
                                               int max) {
        Set<Cancion> favoritos = new HashSet<>(usuario.getListaFavoritos());
        Set<Cancion> recomendadas = new LinkedHashSet<>();
        for (Cancion fav : favoritos) {
            NodoCancion nodo = grafoSimilitudServicio.getGrafo()
                    .obtenerNodo(fav);
            Map<NodoCancion, Double> similares = grafoSimilitudServicio.dijkstra(nodo);
            similares.entrySet().stream()
                    .sorted((a, b) -> Double.compare(b.getValue(), a.getValue())) // mayor similitud primero
                    .map(entry -> entry.getKey().getCancion())
                    .filter(c -> !favoritos.contains(c))
                    .forEach(recomendadas::add);

            if (recomendadas.size() >= max) break;
        }
        return new ArrayList<>(recomendadas).subList(0, Math.min(max, recomendadas.size()));
    }

    public Queue<Cancion> iniciarRadio(Cancion cancionInicio, int max) {
        Queue<Cancion> cola = new LinkedList<>();
        cola.add(cancionInicio);
        NodoCancion nodo = grafoSimilitudServicio.getGrafo()
                .obtenerNodo(cancionInicio);
        Map<NodoCancion, Double> similares = grafoSimilitudServicio.dijkstra(nodo);
        similares.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .map(entry -> entry.getKey().getCancion())
                .filter(c -> !c.equals(cancionInicio))
                .limit(max)
                .forEach(cola::add);

        return cola;
    }

    public List<String> autocompletar(String prefijo) {
        return trieServicio.autocompletar(prefijo);
    }

    public void exportarFavoritosCSV(Usuario usuario, String rutaArchivo) throws Exception {
        try (PrintWriter writer = new PrintWriter(new FileWriter(rutaArchivo))) {
            writer.println("Titulo,Artista,Genero,Anio,Duracion");
            for (Cancion c : usuario.getListaFavoritos()) {
                writer.printf("%s,%s,%s,%d,%f%n",
                        c.getTitulo(),
                        c.getArtista(),
                        c.getGenero(),
                        c.getAnio(),
                        c.getDuracion());
            }
        }
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