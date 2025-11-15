package co.uniquindio.proyecto.syncup.controladores;

import co.uniquindio.proyecto.syncup.entidades.Usuario;
import co.uniquindio.proyecto.syncup.grafos.NodoUsuario;
import co.uniquindio.proyecto.syncup.repositorios.UsuarioRepositorio;
import co.uniquindio.proyecto.syncup.servicios.GrafoSocialServicio;
import jakarta.annotation.PostConstruct;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/grafo-social")
public class GrafoSocialControlador {

    private final GrafoSocialServicio grafoSocialServicio;
    private final UsuarioRepositorio usuarioRepositorio;

    public GrafoSocialControlador(GrafoSocialServicio grafoSocialServicio,
                                 UsuarioRepositorio usuarioRepositorio) {
        this.grafoSocialServicio = grafoSocialServicio;
        this.usuarioRepositorio = usuarioRepositorio;
    }

    @PostConstruct
    public void init() {
        construirGrafo(); // llena el grafo con todos los usuarios
    }


    @PostMapping("/construir")
    public ResponseEntity<String> construirGrafo() {
        grafoSocialServicio.construirGrafo();
        return ResponseEntity.ok("Grafo social construido con éxito");
    }

    @GetMapping("/amigos-de-amigos/{username}")
    public ResponseEntity<Set<Usuario>> amigosDeAmigos(
            @PathVariable String username,
            @RequestParam(defaultValue = "2") int niveles // hasta 2 niveles por defecto
    ) {
        Usuario usuario = usuarioRepositorio.findById(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Set<Usuario> resultado = grafoSocialServicio.amigosDeAmigos(usuario, niveles);
        return ResponseEntity.ok(resultado);
    }
}