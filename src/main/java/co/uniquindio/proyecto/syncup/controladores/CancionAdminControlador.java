package co.uniquindio.proyecto.syncup.controladores;

import co.uniquindio.proyecto.syncup.entidades.Cancion;
import co.uniquindio.proyecto.syncup.servicios.CancionServicio;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/canciones")
public class CancionAdminControlador {

    private final CancionServicio cancionServicio;

    public CancionAdminControlador(CancionServicio cancionServicio) {
        this.cancionServicio = cancionServicio;
    }

    @PostMapping("/subir")
    public ResponseEntity<Cancion> subirCancion(
            @RequestParam("titulo") String titulo,
            @RequestParam("artista") String artista,
            @RequestParam("genero") String genero,
            @RequestParam("anio") int anio,
            @RequestParam("archivo") MultipartFile archivo
    ) {

        // Guardar archivo en disco
        String rutaArchivo = cancionServicio.guardarArchivo(archivo);

        // Crear entidad Cancion
        Cancion nueva = new Cancion();
        nueva.setTitulo(titulo);
        nueva.setArtista(artista);
        nueva.setGenero(genero);
        nueva.setAnio(anio);
        nueva.setRutaArchivo(rutaArchivo);

        // Calcular duración
        try {
            com.mpatric.mp3agic.Mp3File mp3file = new com.mpatric.mp3agic.Mp3File(rutaArchivo);
            nueva.setDuracion((int) mp3file.getLengthInSeconds());
        } catch (Exception ex) {
            System.err.println("No se pudo calcular duración del MP3: " + ex.getMessage());
            nueva.setDuracion(0);
        }

        // Guardar en DB de manera segura
        Cancion cGuardada = cancionServicio.agregarCancion(nueva);

        return ResponseEntity.ok(cGuardada);
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<Cancion> actualizar(@PathVariable Long id, @RequestBody Cancion c) {
        return ResponseEntity.ok(cancionServicio.actualizarCancion(id, c));
    }


    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Map<String, String>> eliminar(@PathVariable Long id) {
        cancionServicio.eliminarCancion(id);
        return ResponseEntity.ok(Map.of("mensaje", "Canción eliminada correctamente"));
    }


    @GetMapping("/listar")
    public ResponseEntity<List<Cancion>> listar() {
        return ResponseEntity.ok(cancionServicio.listarTodas());
    }
}

