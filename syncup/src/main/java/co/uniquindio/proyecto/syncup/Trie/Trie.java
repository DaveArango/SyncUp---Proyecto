package co.uniquindio.proyecto.syncup.Trie;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Trie {

    private NodoTrie raiz;

    public Trie() {
        raiz = new NodoTrie();
    }

    public void insertar(String palabra) {
        NodoTrie nodoActual = raiz;
        for (char c : palabra.toLowerCase().toCharArray()) {
            nodoActual.getHijos().putIfAbsent(c, new NodoTrie(c));
            nodoActual = nodoActual.getHijos().get(c);
        }
        nodoActual.setFinDePalabra(true);
        nodoActual.setPalabraCompleta(palabra);
    }

    public List<String> buscarPorPrefijo(String prefijo) {
        NodoTrie nodoActual = raiz;
        for (char c : prefijo.toLowerCase().toCharArray()) {
            nodoActual = nodoActual.getHijos().get(c);
            if (nodoActual == null) {
                return new ArrayList<>(); // prefijo no existe
            }
        }
        return listarPalabrasDesdeNodo(nodoActual);
    }

    private List<String> listarPalabrasDesdeNodo(NodoTrie nodo) { //Listar palabras desde un nodo
        List<String> resultado = new ArrayList<>();
        if (nodo.isFinDePalabra()) {
            resultado.add(nodo.getPalabraCompleta());
        }
        for (Map.Entry<Character, NodoTrie> hijo : nodo.getHijos().entrySet()) {
            resultado.addAll(listarPalabrasDesdeNodo(hijo.getValue()));
        }
        return resultado;
    }
}
