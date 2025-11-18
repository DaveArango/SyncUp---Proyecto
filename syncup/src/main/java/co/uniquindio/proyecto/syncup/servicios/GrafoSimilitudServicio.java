package co.uniquindio.proyecto.syncup.servicios;

import co.uniquindio.proyecto.syncup.entidades.Cancion;
import co.uniquindio.proyecto.syncup.grafos.GrafoCancion;
import co.uniquindio.proyecto.syncup.grafos.NodoCancion;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.*;
/**
 * Servicio que mantiene un grafo ponderado de similitud entre canciones.
 * Se utiliza Dijkstra para obtener canciones similares.
 */
@Service
@Getter
public class GrafoSimilitudServicio {
    private GrafoCancion grafo = new GrafoCancion();

    public void construirGrafo(List<Cancion> canciones) {
        canciones.forEach(grafo::agregarNodo);

        // Calcular similitudes entre todas las canciones y agregar aristas
        for (int i = 0; i < canciones.size(); i++) {
            for (int j = i + 1; j < canciones.size(); j++) {
                double similitud = calcularSimilitud(canciones.get(i), canciones.get(j));
                if (similitud > 0) {
                    grafo.agregarArista(canciones.get(i), canciones.get(j), similitud);
                }
            }
        }
    }

    private double calcularSimilitud(Cancion a, Cancion b) {
        double score = 0; // +1 si mismo género, +0.5 si mismo artista
        if (a.getGenero() != null && a.getGenero().equalsIgnoreCase(b.getGenero())) score += 1.0;
        if (a.getArtista() != null && a.getArtista().equalsIgnoreCase(b.getArtista())) score += 0.5;
        return Math.min(score, 1.0); // normalizamos a 1
    }

    public Map<NodoCancion, Double> dijkstra(NodoCancion inicio) {
        Map<NodoCancion, Double> distancias = new HashMap<>();
        Set<NodoCancion> visitados = new HashSet<>();
        PriorityQueue<NodoCancion> cola = new PriorityQueue<>(Comparator.comparing(distancias::get));

        // Inicializar distancias
        for (NodoCancion nodo : grafo.getNodos().values()) {
            distancias.put(nodo, Double.MAX_VALUE);
        }
        distancias.put(inicio, 0.0);
        cola.add(inicio);

        while (!cola.isEmpty()) {
            NodoCancion actual = cola.poll();
            if (!visitados.add(actual)) continue;

            for (Map.Entry<NodoCancion, Double> vecino : actual.getVecinos().entrySet()) {
                double nuevoCosto = distancias.get(actual) + (1 - vecino.getValue()); // peso = 1 - similitud
                if (nuevoCosto < distancias.get(vecino.getKey())) {
                    distancias.put(vecino.getKey(), nuevoCosto);
                    cola.add(vecino.getKey());
                }
            }
        }
        return distancias;
    }

    public void agregarNodo(Cancion cancion) {
        if (grafo.obtenerNodo(cancion) == null) {
            grafo.agregarNodo(cancion);
        }
    }
}

