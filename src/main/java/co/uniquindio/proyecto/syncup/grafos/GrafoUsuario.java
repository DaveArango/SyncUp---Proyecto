package co.uniquindio.proyecto.syncup.grafos;

import co.uniquindio.proyecto.syncup.entidades.Usuario;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class GrafoUsuario {
    private Map<Long, NodoUsuario> nodos = new HashMap<>();

    public void agregarNodo(Usuario usuario) {
        nodos.putIfAbsent((long) usuario.getUsername().hashCode(), new NodoUsuario(usuario));
    }

    public void agregarArista(Usuario a, Usuario b) {
        NodoUsuario nodoA = nodos.get((long) a.getUsername().hashCode());
        NodoUsuario nodoB = nodos.get((long) b.getUsername().hashCode());

        if (nodoA == null || nodoB == null) {
            throw new IllegalArgumentException("Usuario no existe en el grafo");
        }

        nodoA.agregarAmigo(nodoB);
        nodoB.agregarAmigo(nodoA);
    }

    public NodoUsuario obtenerNodo(Usuario usuario) {
        return nodos.get((long) usuario.getUsername().hashCode());
    }

    public Map<Long, NodoUsuario> getNodos() {
        return nodos;
    }
}

