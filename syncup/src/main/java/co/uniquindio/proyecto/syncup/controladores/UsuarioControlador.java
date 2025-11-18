package co.uniquindio.proyecto.syncup.controladores;

import co.uniquindio.proyecto.syncup.entidades.Cancion;
import co.uniquindio.proyecto.syncup.entidades.Usuario;
import co.uniquindio.proyecto.syncup.repositorios.UsuarioRepositorio;
import co.uniquindio.proyecto.syncup.servicios.GrafoSocialServicio;
import co.uniquindio.proyecto.syncup.servicios.UsuarioServicio;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuario")
public class UsuarioControlador {

    private final UsuarioServicio usuarioServicio;
    private final GrafoSocialServicio grafoSocialServicio;
    private final UsuarioRepositorio usuarioRepositorio;

    public UsuarioControlador(UsuarioServicio usuarioServicio,
                              GrafoSocialServicio grafoSocialServicio,
                              UsuarioRepositorio usuarioRepositorio) {
        this.usuarioServicio = usuarioServicio;
        this.grafoSocialServicio = grafoSocialServicio;
        this.usuarioRepositorio = usuarioRepositorio;
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
            @RequestBody Map<String, Long> body
    ) {
        Long idCancion = body.get("id");
        return ResponseEntity.ok(usuarioServicio.agregarFavorito(username, idCancion));
    }


    @DeleteMapping("/{username}/favoritos/eliminar")
    public ResponseEntity<Usuario> eliminarFavorito(
            @PathVariable String username,
            @RequestBody Map<String, Long> body
    ) {
        Long idCancion = body.get("id");
        return ResponseEntity.ok(usuarioServicio.eliminarFavorito(username, idCancion));
    }

    // ================= RED SOCIAL =================

    @PostMapping("/{username}/seguir/{seguido}")
    public ResponseEntity<String> seguirUsuario(
            @PathVariable String username,
            @PathVariable String seguido
    ) {
        try {
            usuarioServicio.seguirUsuario(username, seguido);
            return ResponseEntity.ok("Usuario seguido correctamente");
        } catch (RuntimeException e) {
            // Puedes afinar el mensaje según la excepción
            if (e.getMessage().equals("Usuario a seguir no encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno");
            }
        }
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
        Usuario usuario = usuarioServicio.obtenerUsuario(username); // método en UsuarioServicio
        List<Usuario> sugerencias = grafoSocialServicio.sugerirAmigos(usuario);
        return ResponseEntity.ok(sugerencias);
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

    @GetMapping("/{username}/amigos")
    public ResponseEntity<?> obtenerAmigos(@PathVariable String username) {
        try {
            List<Usuario> amigos = usuarioServicio.obtenerAmigos(username);
            return ResponseEntity.ok(amigos);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno: " + e.getMessage()));
        }
    }

    @GetMapping("/{username}/favoritas")
    @Transactional(readOnly = true)
    public ResponseEntity<List<Cancion>> obtenerFavoritas(@PathVariable String username) {
        Usuario usuario = usuarioRepositorio.findByIdConFavoritos(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return ResponseEntity.ok(usuario.getListaFavoritos());
    }

    @DeleteMapping("/eliminar/{username}")
    public ResponseEntity<?> eliminar(@PathVariable String username) {
        usuarioServicio.eliminarUsuario(username);
        return ResponseEntity.ok(Map.of("mensaje", "Usuario eliminado"));
    }


}