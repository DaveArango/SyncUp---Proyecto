package co.uniquindio.proyecto.syncup.controladores;

import co.uniquindio.proyecto.syncup.entidades.Cancion;
import co.uniquindio.proyecto.syncup.entidades.Usuario;
import co.uniquindio.proyecto.syncup.repositorios.UsuarioRepositorio;
import co.uniquindio.proyecto.syncup.servicios.CancionServicio;
import jakarta.transaction.Transactional;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
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
    public ResponseEntity<?> descubrimiento(@PathVariable String username) {
        try {
            Usuario u = usuarioRepositorio.findByIdConFavoritos(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));
            var resultado = cancionServicio.descubrimientoSemanal(u, 10);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                    "error", e.getMessage(),
                    "tipo", e.getClass().getSimpleName()
            ));
        }
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

    @GetMapping("/exportarCSV/{username}/descargar")
    @Transactional
    public ResponseEntity<Resource> descargarCSV(@PathVariable String username) throws IOException {
        Usuario u = usuarioRepositorio.findById(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        u.getListaFavoritos().size();

        String nombreArchivo = username + "_favoritos.csv";
        Path path = Paths.get(nombreArchivo);
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(path))) {
            writer.println("Titulo,Artista,Genero,Anio,Duracion");
            for (Cancion c : u.getListaFavoritos()) {
                writer.printf("%s,%s,%s,%s,%s%n",
                        c.getTitulo(),
                        c.getArtista(),
                        c.getGenero(),
                        c.getAnio() != null ? c.getAnio() : "",
                        c.getDuracion() != null ? c.getDuracion() : "");
            }
        }

        Resource resource = new UrlResource(path.toUri());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombreArchivo + "\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);
    }


    @GetMapping("/reproducir/{id}")
    public ResponseEntity<Resource> reproducir(@PathVariable Long id) throws IOException {
        Cancion c = cancionServicio.listarTodas().stream()
                .filter(can -> can.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Canción no encontrada"));
        Path currentDir = Paths.get("").toAbsolutePath();
        Path projectDir = currentDir;
        while (projectDir != null && !projectDir.getFileName().toString().equals("SyncUp---Proyecto")) {
            projectDir = projectDir.getParent();
        }
        if (projectDir == null) {
            throw new RuntimeException("❌ No se pudo encontrar la carpeta SyncUp---Proyecto en la ruta: " + currentDir);
        }
        Path mediaDir = projectDir.resolve("syncup").resolve("media");
        Path path = mediaDir.resolve(c.getRutaArchivo()).normalize();
        System.out.println("🟦 Buscando archivo en: " + path);
        if (!Files.exists(path)) {
            throw new RuntimeException("Archivo NO encontrado en: " + path);
        }
        Resource resource = new UrlResource(path.toUri());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + path.getFileName() + "\"")
                .contentType(MediaType.parseMediaType("audio/mpeg"))
                .body(resource);
    }





    @GetMapping("/reproducir-nombre")
    public ResponseEntity<Resource> reproducirPorNombre(@RequestParam String nombre) throws IOException {
        // Buscar coincidencias por autocompletar
        List<String> coincidencias = cancionServicio.autocompletar(nombre);
        if (coincidencias.isEmpty()) {
            return ResponseEntity.status(404).body(null);
        }
        String nombreCancion = coincidencias.get(0);
        // Buscar la canción por título exacto
        Cancion c = cancionServicio.listarTodas().stream()
                .filter(can -> can.getTitulo().equalsIgnoreCase(nombreCancion))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Canción no encontrada: " + nombreCancion));
        // 📌 Ruta relativa correcta
        Path baseDir = Paths.get("syncup", "media").toAbsolutePath().normalize();
        Path path = baseDir.resolve(c.getRutaArchivo()).normalize();
        System.out.println("🟩 Reproduciendo por nombre desde: " + path);
        if (!Files.exists(path)) {
            throw new RuntimeException("Archivo no encontrado en: " + path);
        }
        Resource resource = new UrlResource(path.toUri());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + path.getFileName() + "\"")
                .contentType(MediaType.parseMediaType("audio/mpeg"))
                .body(resource);
    }

    @GetMapping("/buscar-por-nombre")
    public ResponseEntity<List<Cancion>> buscarPorNombre(@RequestParam String nombre) {
        // Obtener todas las canciones
        List<Cancion> todas = cancionServicio.listarTodas();

        // Filtrar por coincidencia de nombre (ignore case)
        List<Cancion> coincidencias = todas.stream()
                .filter(c -> c.getTitulo().toLowerCase().contains(nombre.toLowerCase()))
                .toList();

        // Si no hay coincidencias, podemos devolver 404 o una lista vacía
        if (coincidencias.isEmpty()) {
            return ResponseEntity.status(404).body(coincidencias);
        }

        return ResponseEntity.ok(coincidencias);
    }



    @GetMapping("/busqueda-avanzada")
    public ResponseEntity<List<Cancion>> busquedaAvanzada(
            @RequestParam(required = false) String artista,
            @RequestParam(required = false) String genero,
            @RequestParam(required = false) Integer anio,
            @RequestParam(defaultValue = "true") boolean esAnd
    ) {
        return ResponseEntity.ok(
                cancionServicio.busquedaAvanzada(artista, genero, anio, esAnd)
        );
    }
    @GetMapping("/listar")
    public ResponseEntity<List<Cancion>> listar() {
        return ResponseEntity.ok(cancionServicio.listarTodas());
    }
}
