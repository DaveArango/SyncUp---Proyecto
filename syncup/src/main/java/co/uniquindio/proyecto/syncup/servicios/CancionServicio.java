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
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class CancionServicio {

    private final String MEDIA_DIR = "media/";
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
            // Crear carpeta si no existe
            Path carpeta = Paths.get(MEDIA_DIR).toAbsolutePath().normalize();
            if (!Files.exists(carpeta)) {
                Files.createDirectories(carpeta);
            }
            // Guardar archivo en la carpeta media
            Path destino = carpeta.resolve(archivo.getOriginalFilename());
            Files.copy(archivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
            // *** SE GUARDA SOLO EL NOMBRE ***
            return archivo.getOriginalFilename();
        } catch (IOException e) {
            throw new RuntimeException("Error guardando archivo: " + e.getMessage());
        }
    }


    public List<Cancion> busquedaAvanzada(String artista,
                                          String genero,
                                          Integer anio,
                                          boolean esAnd) {

        List<Cancion> todas = cancionRepositorio.findAll();

        List<Cancion> resultadoArtista = new CopyOnWriteArrayList<>();
        List<Cancion> resultadoGenero = new CopyOnWriteArrayList<>();
        List<Cancion> resultadoAnio = new CopyOnWriteArrayList<>();

        List<Thread> hilos = new ArrayList<>();

        if (artista != null && !artista.isBlank()) {
            Thread hiloArtista = new Thread(() -> {
                for (Cancion c : todas) {
                    if (c.getArtista().equalsIgnoreCase(artista)) {
                        resultadoArtista.add(c);
                    }
                }
            });
            hilos.add(hiloArtista);
            hiloArtista.start();
        }

        if (genero != null && !genero.isBlank()) {
            Thread hiloGenero = new Thread(() -> {
                for (Cancion c : todas) {
                    if (c.getGenero().equalsIgnoreCase(genero)) {
                        resultadoGenero.add(c);
                    }
                }
            });
            hilos.add(hiloGenero);
            hiloGenero.start();
        }

        if (anio != null) {
            Thread hiloAnio = new Thread(() -> {
                for (Cancion c : todas) {
                    if (c.getAnio() != null && c.getAnio() == anio) {
                        resultadoAnio.add(c);
                    }
                }
            });
            hilos.add(hiloAnio);
            hiloAnio.start();
        }

        for (Thread t : hilos) {
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException("Error en concurrencia: " + e.getMessage());
            }
        }

        if (esAnd) {
            List<List<Cancion>> listas = List.of(resultadoArtista, resultadoGenero, resultadoAnio);

            return listas.stream()
                    .filter(l -> !l.isEmpty())
                    .reduce((l1, l2) -> l1.stream().filter(l2::contains).toList())
                    .orElse(List.of());
        }

        Set<Cancion> union = new HashSet<>();
        union.addAll(resultadoArtista);
        union.addAll(resultadoGenero);
        union.addAll(resultadoAnio);
        return new ArrayList<>(union);
    }

}