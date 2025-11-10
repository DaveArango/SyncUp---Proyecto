package co.uniquindio.proyecto.syncup.servicios;

import co.uniquindio.proyecto.syncup.entidades.Cancion;
import co.uniquindio.proyecto.syncup.entidades.Usuario;
import co.uniquindio.proyecto.syncup.repositorios.UsuarioRepositorio;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UsuarioServicio {

    private final UsuarioRepositorio usuarioRepositorio;
    private final GrafoSocialServicio grafoSocialServicio;

    public UsuarioServicio(UsuarioRepositorio usuarioRepositorio,
                           GrafoSocialServicio grafoSocialServicio) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.grafoSocialServicio = grafoSocialServicio;
    }

    // =============== AUTENTICACIÓN ===============

    public Usuario registrarUsuario(Usuario usuario) {
        if (usuarioRepositorio.existsById(usuario.getUsername())) {
            throw new RuntimeException("El nombre de usuario ya existe");
        }
        Usuario nuevo = usuarioRepositorio.save(usuario);
        grafoSocialServicio.agregarUsuario(nuevo);
        return nuevo;
    }

    public Usuario iniciarSesion(String username, String password) {
        Usuario usuario = usuarioRepositorio.findById(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        if (!usuario.getPassword().equals(password)) {
            throw new RuntimeException("Contraseña incorrecta");
        }
        return usuario;
    }

    // =============== PERFIL Y FAVORITOS ===============

    public Usuario actualizarPerfil(String username, String nombre, String password) {
        Usuario usuario = usuarioRepositorio.findById(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        // Actualiza solo lo que se envía
        if (nombre != null && !nombre.trim().isEmpty()) {
            usuario.setNombre(nombre);
        }
        if (password != null && !password.trim().isEmpty()) {
            usuario.setPassword(password);
        }
        // Guarda los cambios en la base de datos
        return usuarioRepositorio.save(usuario);
    }


    public Usuario agregarFavorito(String username, Cancion cancion) {
        Usuario usuario = usuarioRepositorio.findById(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.getListaFavoritos().add(cancion);
        return usuarioRepositorio.save(usuario);
    }

    public Usuario eliminarFavorito(String username, Cancion cancion) {
        Usuario usuario = usuarioRepositorio.findById(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.getListaFavoritos().remove(cancion);
        return usuarioRepositorio.save(usuario);
    }

    // =============== RED SOCIAL ===============

    public void seguirUsuario(String username, String seguido) {
        Usuario u1 = usuarioRepositorio.findById(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Usuario u2 = usuarioRepositorio.findById(seguido)
                .orElseThrow(() -> new RuntimeException("Usuario a seguir no encontrado"));

        grafoSocialServicio.agregarConexion(u1, u2);
    }

    public void dejarDeSeguir(String username, String seguido) {
        Usuario u1 = usuarioRepositorio.findById(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Usuario u2 = usuarioRepositorio.findById(seguido)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        grafoSocialServicio.eliminarConexion(u1, u2);
    }

    public List<Usuario> sugerirUsuarios(String username) {
        Usuario usuario = usuarioRepositorio.findById(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return grafoSocialServicio.sugerirAmigos(usuario);
    }

    public List<Usuario> listarTodos() {
        try {
            return usuarioRepositorio.findAll();
        } catch (Exception e) {
            e.printStackTrace(); // 👈 imprime el error en consola
            throw e;             // lo vuelve a lanzar
        }
    }

}
