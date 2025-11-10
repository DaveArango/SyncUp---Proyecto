package co.uniquindio.proyecto.syncup.servicios;


import co.uniquindio.proyecto.syncup.Trie.Trie;
import co.uniquindio.proyecto.syncup.entidades.Cancion;
import co.uniquindio.proyecto.syncup.repositorios.CancionRepositorio;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrieServicio {

    private final CancionRepositorio cancionRepositorio;
    private Trie trie = new Trie();

    public TrieServicio(CancionRepositorio cancionRepositorio) {
        this.cancionRepositorio = cancionRepositorio;
    }

    @PostConstruct
    public void construirTrie() {
        List<Cancion> canciones = cancionRepositorio.findAll();
        for (Cancion c : canciones) {
            trie.insertar(c.getTitulo());
        }
    }

    public void insertarTitulo(String titulo) {
        trie.insertar(titulo);
    }

    public List<String> autocompletar(String prefijo) {
        return trie.buscarPorPrefijo(prefijo);
    }
}

