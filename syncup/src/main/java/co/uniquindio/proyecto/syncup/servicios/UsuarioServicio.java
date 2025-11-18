package co.uniquindio.proyecto.syncup.servicios;

import co.uniquindio.proyecto.syncup.entidades.Cancion;
import co.uniquindio.proyecto.syncup.entidades.Usuario;
import co.uniquindio.proyecto.syncup.repositorios.CancionRepositorio;
import co.uniquindio.proyecto.syncup.repositorios.UsuarioRepositorio;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Servicio encargado de las operaciones relacionadas con usuarios:
 * registro, eliminación, y acceso en memoria mediante un HashMap para O(1).
 *
 * Mantiene además la sincronización con la base de datos y el grafo social.
 */

@Service
public class UsuarioServicio {

    private final UsuarioRepositorio usuarioRepositorio;
    private final GrafoSocialServicio grafoSocialServicio;
    private final CancionRepositorio cancionRepositorio;
    private Map<String, Usuario> mapaUsuarios = new HashMap<>();



    // Cargar mapa al iniciar el servicio
    @PostConstruct
    public void cargarMapaUsuarios() {
        usuarioRepositorio.findAll()
                .forEach(u -> mapaUsuarios.put(u.getUsername(), u));
    }


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

        // RF-016: Insertar en HashMap
        mapaUsuarios.put(nuevo.getUsername(), nuevo);

        // Agregar al grafo social
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


    /*@Transactional
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
    }*/
    @Transactional
    public void dejarDeSeguir(String username, String seguido) {

        Usuario u1 = usuarioRepositorio.findById(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));

        Usuario u2 = usuarioRepositorio.findById(seguido)
                .orElseThrow(() -> new RuntimeException("Usuario a dejar de seguir no encontrado: " + seguido));

        // Revisa si u2 está en la lista de amigos/seguidos
        if (u1.getAmigos().contains(u2)) {

            u1.getAmigos().remove(u2);   // lo elimina correctamente gracias a equals/hashCode
            usuarioRepositorio.save(u1);

            System.out.println(username + " dejó de seguir a " + seguido);

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


    @Transactional
    public void eliminarUsuario(String username) {

        Usuario usuario = usuarioRepositorio.findById(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));

        // Eliminar relaciones en grafo
        grafoSocialServicio.eliminarConexionTotal(usuario);

        usuarioRepositorio.delete(usuario);

        mapaUsuarios.remove(username);

    }

    public Usuario obtenerUsuarioO1(String username) {
        return mapaUsuarios.get(username);
    }





}
