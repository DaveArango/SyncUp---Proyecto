package co.uniquindio.proyecto.syncup.Trie;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class NodoTrie {
    private char c;
    private Map<Character, NodoTrie> hijos = new HashMap<>();
    private boolean esFinDePalabra;
    private String palabraCompleta;

    public NodoTrie() {}

    public NodoTrie(char c) {
        this.c = c;
    }

    public boolean isFinDePalabra() {
        return esFinDePalabra;
    }

    public void setFinDePalabra(boolean finDePalabra) {
        esFinDePalabra = finDePalabra;
    }
}

