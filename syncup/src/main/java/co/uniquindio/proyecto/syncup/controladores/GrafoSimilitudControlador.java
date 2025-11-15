package co.uniquindio.proyecto.syncup.controladores;

import co.uniquindio.proyecto.syncup.entidades.Cancion;
import co.uniquindio.proyecto.syncup.grafos.NodoCancion;
import co.uniquindio.proyecto.syncup.repositorios.CancionRepositorio;
import co.uniquindio.proyecto.syncup.servicios.GrafoSimilitudServicio;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/grafo")
public class GrafoSimilitudControlador {

    private final GrafoSimilitudServicio grafoSimilitudServicio;
    private final CancionRepositorio cancionRepositorio;

    public GrafoSimilitudControlador(GrafoSimilitudServicio grafoSimilitudServicio,
                                     CancionRepositorio cancionRepositorio) {
        this.grafoSimilitudServicio = grafoSimilitudServicio;
        this.cancionRepositorio = cancionRepositorio;
    }


    @PostMapping("/construir") //Para construir el grafo
    public ResponseEntity<String> construirGrafo() {
        List<Cancion> canciones = cancionRepositorio.findAll();
        grafoSimilitudServicio.construirGrafo(canciones);
        return ResponseEntity.ok("Grafo de similitud construido con " + canciones.size() + " canciones");
    }

    @GetMapping("/similares/{idCancion}") //Obtener similitudes
    public ResponseEntity<List<Cancion>> obtenerSimilares(@PathVariable Long idCancion) {
        NodoCancion nodo = grafoSimilitudServicio.getGrafo().obtenerNodo(cancionRepositorio.findById(idCancion)
                .orElseThrow(() -> new RuntimeException("Cancion no encontrada")));

        // Ejecutar Dijkstra
        Map<NodoCancion, Double> similitudes = grafoSimilitudServicio.dijkstra(nodo);

        // Ordenar canciones por mayor similitud
        List<Cancion> cancionesSimilares = similitudes.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .map(entry -> entry.getKey().getCancion())
                .filter(c -> !c.getId().equals(idCancion)) // excluir la canción origen
                .toList();

        return ResponseEntity.ok(cancionesSimilares);
    }
}

