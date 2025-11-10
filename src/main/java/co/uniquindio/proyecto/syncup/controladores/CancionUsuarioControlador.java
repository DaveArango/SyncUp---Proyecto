package co.uniquindio.proyecto.syncup.controladores;

import co.uniquindio.proyecto.syncup.entidades.Cancion;
import co.uniquindio.proyecto.syncup.entidades.Usuario;
import co.uniquindio.proyecto.syncup.repositorios.UsuarioRepositorio;
import co.uniquindio.proyecto.syncup.servicios.CancionServicio;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Queue;

@RestController
@RequestMapping("/api/usuario/canciones")
public class CancionUsuarioControlador {

    private final CancionServicio cancionServicio;
    private final UsuarioRepositorio usuarioRepositorio;

    public CancionUsuarioControlador(CancionServicio cancionServicio,
                                     UsuarioRepositorio usuarioRepositorio) {
        this.cancionServicio = cancionServicio;
        this.usuarioRepositorio = usuarioRepositorio;
    }

    @GetMapping("/descubrimiento/{username}")
    public ResponseEntity<List<Cancion>> descubrimiento(@PathVariable String username) {
        Usuario u = usuarioRepositorio.findById(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return ResponseEntity.ok(cancionServicio.descubrimientoSemanal(u, 10));
    }

    @GetMapping("/radio/{idCancion}")
    public ResponseEntity<Queue<Cancion>> radio(@PathVariable Long idCancion) {
        Cancion c = cancionServicio.listarTodas().stream()
                .filter(can -> can.getId().equals(idCancion))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Canción no encontrada"));
        return ResponseEntity.ok(cancionServicio.iniciarRadio(c, 10));
    }

    @GetMapping("/autocompletar")
    public ResponseEntity<List<String>> autocompletar(@RequestParam String prefijo) {
        return ResponseEntity.ok(cancionServicio.autocompletar(prefijo));
    }

    @GetMapping("/exportarCSV/{username}")
    public ResponseEntity<String> exportarCSV(@PathVariable String username) {
        Usuario u = usuarioRepositorio.findById(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        try {
            cancionServicio.exportarFavoritosCSV(u, username + "_favoritos.csv");
            return ResponseEntity.ok("CSV generado correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error generando CSV: " + e.getMessage());
        }
    }

    @GetMapping("/reproducir/{id}")
    public ResponseEntity<Resource> reproducir(@PathVariable Long id) throws IOException {
        Cancion c = cancionServicio.listarTodas().stream()
                .filter(can -> can.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Canción no encontrada"));

        Path path = Paths.get(c.getRutaArchivo());
        Resource resource = new UrlResource(path.toUri());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + path.getFileName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

}
