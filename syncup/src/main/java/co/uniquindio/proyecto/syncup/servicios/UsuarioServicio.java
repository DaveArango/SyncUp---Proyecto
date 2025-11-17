package co.uniquindio.proyecto.syncup.servicios;

import co.uniquindio.proyecto.syncup.entidades.Cancion;
import co.uniquindio.proyecto.syncup.entidades.Usuario;
import co.uniquindio.proyecto.syncup.repositorios.CancionRepositorio;
import co.uniquindio.proyecto.syncup.repositorios.UsuarioRepositorio;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UsuarioServicio {

    private final UsuarioRepositorio usuarioRepositorio;
    private final GrafoSocialServicio grafoSocialServicio;
    private final CancionRepositorio cancionRepositorio;


    public UsuarioServicio(UsuarioRepositorio usuarioRepositorio,
                           GrafoSocialServicio grafoSocialServicio,
                           CancionRepositorio cancionRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.grafoSocialServicio = grafoSocialServicio;
        this.cancionRepositorio = cancionRepositorio;
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

    @Transactional
    public Usuario agregarFavorito(String username, Long idCancion) {
        Usuario usuario = usuarioRepositorio.findById(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Cancion cancion = cancionRepositorio.findById(idCancion)
                .orElseThrow(() -> new RuntimeException("Canción no encontrada"));

        usuario.getListaFavoritos().add(cancion);
        return usuarioRepositorio.save(usuario);
    }


    @Transactional
    public Usuario eliminarFavorito(String username, Long idCancion) {
        Usuario usuario = usuarioRepositorio.findById(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Cancion cancion = cancionRepositorio.findById(idCancion)
                .orElseThrow(() -> new RuntimeException("Canción no encontrada"));

        usuario.getListaFavoritos().remove(cancion);
        return usuarioRepositorio.save(usuario);
    }

    // =============== RED SOCIAL ===============

    @Transactional
    public void seguirUsuario(String username, String seguido) {
        Usuario u1 = usuarioRepositorio.findById(username).orElse(null);
        Usuario u2 = usuarioRepositorio.findById(seguido).orElse(null);

        if (u1 == null) throw new RuntimeException("Usuario no encontrado: " + username);
        if (u2 == null) throw new RuntimeException("Usuario a seguir no encontrado: " + seguido);

        u1.getAmigos().add(u2);
        usuarioRepositorio.save(u1);
    }


    @Transactional
    public void dejarDeSeguir(String username, String seguido) {
        Usuario u1 = usuarioRepositorio.findById(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));
        Usuario u2 = usuarioRepositorio.findById(seguido)
                .orElseThrow(() -> new RuntimeException("Usuario a dejar de seguir no encontrado: " + seguido));

        if (u1.getListaFavoritos().contains(u2)) {
            u1.getListaFavoritos().remove(u2);
            usuarioRepositorio.save(u1);
        } else {
            System.out.println("El usuario " + username + " no sigue a " + seguido);
        }
    }


    public List<Usuario> sugerirUsuarios(String username) {
        Usuario usuario = usuarioRepositorio.findById(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return grafoSocialServicio.sugerirAmigos(usuario);
    }

    public List<Usuario> listarTodos() {
        List<Usuario> lista = usuarioRepositorio.findAll();
        System.out.println("Usuarios en BD:");
        lista.forEach(u -> System.out.println(" - " + u.getUsername()));
        return lista;
    }

    public Usuario obtenerUsuario(String username) {
        return usuarioRepositorio.findById(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public List<Usuario> obtenerAmigos(String username) {
        Usuario usuario = usuarioRepositorio.findByIdConAmigos(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return usuario.getAmigos();
    }


}
