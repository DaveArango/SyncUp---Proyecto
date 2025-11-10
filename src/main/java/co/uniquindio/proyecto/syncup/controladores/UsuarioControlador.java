package co.uniquindio.proyecto.syncup.controladores;

import co.uniquindio.proyecto.syncup.entidades.Cancion;
import co.uniquindio.proyecto.syncup.entidades.Usuario;
import co.uniquindio.proyecto.syncup.servicios.UsuarioServicio;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuario")
public class UsuarioControlador {

    private final UsuarioServicio usuarioServicio;

    public UsuarioControlador(UsuarioServicio usuarioServicio) {
        this.usuarioServicio = usuarioServicio;
    }

    // ================= AUTENTICACIÓN =================

    @PostMapping("/registrar")
    public ResponseEntity<Usuario> registrar(@RequestBody Usuario usuario) {
        return ResponseEntity.ok(usuarioServicio.registrarUsuario(usuario));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
        try {
            if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Debe ingresar usuario y contraseña"));
            }

            Usuario usuario = usuarioServicio.iniciarSesion(username, password);

            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Usuario o contraseña incorrectos"));
            }

            // Puedes devolver solo los datos necesarios (sin la contraseña)
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Inicio de sesión exitoso");
            respuesta.put("usuario", Map.of(
                    "username", usuario.getUsername(),
                    "nombre", usuario.getNombre()
            ));

            return ResponseEntity.ok(respuesta);

        } catch (IllegalArgumentException e) {
            // Si el servicio lanza una excepción por validación
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));

        } catch (RuntimeException e) {
            // Si hay un problema lógico (p.ej. usuario no encontrado)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));

        } catch (Exception e) {
            // Cualquier otra excepción inesperada
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor: " + e.getMessage()));
        }
    }



    // ================= PERFIL =================


    @PutMapping("/perfil/{username}")
    public ResponseEntity<?> actualizarPerfil(
            @PathVariable String username,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String password
    ) {
        try {
            Usuario usuarioActualizado = usuarioServicio.actualizarPerfil(username, nombre, password);
            return ResponseEntity.ok(usuarioActualizado);

        } catch (RuntimeException e) {
            // Si el servicio lanza un error controlado (por ejemplo usuario no encontrado)
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));

        } catch (Exception e) {
            // Cualquier otro error no previsto
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno: " + e.getMessage()));
        }
    }


    // ================= FAVORITOS =================

    @PostMapping("/{username}/favoritos")
    public ResponseEntity<Usuario> agregarFavorito(
            @PathVariable String username,
            @RequestBody Cancion cancion
    ) {
        return ResponseEntity.ok(usuarioServicio.agregarFavorito(username, cancion));
    }

    @DeleteMapping("/{username}/favoritos")
    public ResponseEntity<Usuario> eliminarFavorito(
            @PathVariable String username,
            @RequestBody Cancion cancion
    ) {
        return ResponseEntity.ok(usuarioServicio.eliminarFavorito(username, cancion));
    }

    // ================= RED SOCIAL =================

    @PostMapping("/{username}/seguir/{seguido}")
    public ResponseEntity<Void> seguirUsuario(
            @PathVariable String username,
            @PathVariable String seguido
    ) {
        usuarioServicio.seguirUsuario(username, seguido);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{username}/dejarDeSeguir/{seguido}")
    public ResponseEntity<Void> dejarDeSeguir(
            @PathVariable String username,
            @PathVariable String seguido
    ) {
        usuarioServicio.dejarDeSeguir(username, seguido);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{username}/sugerencias")
    public ResponseEntity<List<Usuario>> sugerencias(@PathVariable String username) {
        return ResponseEntity.ok(usuarioServicio.sugerirUsuarios(username));
    }

    // ================= LISTAR USUARIOS =================

    @GetMapping("/todos")
    public ResponseEntity<?> listarTodos() {
        try {
            return ResponseEntity.ok(usuarioServicio.listarTodos());
        } catch (Exception e) {
            e.printStackTrace(); // 👈 imprime la causa real en consola
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getClass().getSimpleName(), "detalle", e.getMessage()));
        }
    }


}