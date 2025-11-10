package co.uniquindio.proyecto.syncup.controladores;

import co.uniquindio.proyecto.syncup.servicios.TrieServicio;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trie")
public class TrieControlador {

    private final TrieServicio trieServicio;

    public TrieControlador(TrieServicio trieServicio) {
        this.trieServicio = trieServicio;
    }

    @GetMapping("/autocompletar") // Autocompletar
    public ResponseEntity<List<String>> autocompletar(@RequestParam String prefijo) {
        List<String> resultados = trieServicio.autocompletar(prefijo);
        return ResponseEntity.ok(resultados);
    }
}

