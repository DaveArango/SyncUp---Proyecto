package co.uniquindio.proyecto.syncup.grafos;

import co.uniquindio.proyecto.syncup.entidades.Cancion;

import java.util.HashMap;
import java.util.Map;

public class GrafoCancion {
    private Map<Long, NodoCancion> nodos = new HashMap<>();

    public void agregarNodo(Cancion cancion) {
        nodos.putIfAbsent(Long.valueOf(cancion.getId()),
                new NodoCancion(cancion)); //Solo si no existe la clave (id de la cancion)
    }


    public void agregarArista(Cancion a, Cancion b, double similitud) {
        NodoCancion nodoA = nodos.get(a.getId());
        NodoCancion nodoB = nodos.get(b.getId());

        if (nodoA == null || nodoB == null) {
            throw new IllegalArgumentException("Nodo no existe");
        }

        nodoA.agregarVecino(nodoB, similitud);
        nodoB.agregarVecino(nodoA, similitud);
    }

    public NodoCancion obtenerNodo(Cancion cancion) {
        return nodos.get(cancion.getId());
    }

    public Map<Long, NodoCancion> getNodos() {
        return nodos;
    }
}

