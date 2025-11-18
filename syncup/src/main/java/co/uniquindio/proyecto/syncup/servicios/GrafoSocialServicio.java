package co.uniquindio.proyecto.syncup.servicios;

import co.uniquindio.proyecto.syncup.entidades.Usuario;
import co.uniquindio.proyecto.syncup.grafos.GrafoUsuario;
import co.uniquindio.proyecto.syncup.grafos.NodoUsuario;
import co.uniquindio.proyecto.syncup.repositorios.UsuarioRepositorio;
import jakarta.transaction.Transactional;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Getter
public class GrafoSocialServicio {

    private final UsuarioRepositorio usuarioRepositorio;
    private GrafoUsuario grafo = new GrafoUsuario();

    public GrafoSocialServicio(UsuarioRepositorio usuarioRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
    }

    @Transactional
    public void construirGrafo() {
        List<Usuario> usuarios = usuarioRepositorio.findAll();
        usuarios.forEach(grafo::agregarNodo);

        for (Usuario u : usuarios) {
            NodoUsuario nodoU = grafo.obtenerNodo(u);
            u.getAmigos().forEach(f -> {
                NodoUsuario nodoF = grafo.obtenerNodo(f);
                if (nodoF != null) {
                    grafo.agregarArista(u, f);
                }
            });
        }
    }

    public Set<Usuario> amigosDeAmigos(Usuario usuario,
                                       int niveles) {
        NodoUsuario inicio = grafo.obtenerNodo(usuario);
        Set<Usuario> resultado = new HashSet<>();
        Set<NodoUsuario> visitados = new HashSet<>();
        Queue<NodoUsuario> cola = new LinkedList<>();
        Map<NodoUsuario, Integer> nivel = new HashMap<>();

        cola.add(inicio);
        visitados.add(inicio);
        nivel.put(inicio, 0);

        while (!cola.isEmpty()) {
            NodoUsuario actual = cola.poll();
            int nivelActual = nivel.get(actual);
            if (nivelActual >= niveles) continue;

            for (NodoUsuario amigo : actual.getAmigos()) {
                if (!visitados.contains(amigo)) {
                    visitados.add(amigo);
                    cola.add(amigo);
                    nivel.put(amigo, nivelActual + 1);
                    resultado.add(amigo.getUsuario());
                }
            }
        }

        resultado.remove(usuario);
        return resultado;
    }

    // Agregar un nuevo usuario como nodo
    public void agregarUsuario(Usuario usuario) {
        grafo.agregarNodo(usuario);
    }

    // Conectar dos usuarios (seguir / dejar de seguir)
    public void agregarConexion(Usuario u1, Usuario u2) {
        grafo.agregarArista(u1, u2);
    }

    // Eliminar conexión entre usuarios
    public void eliminarConexion(Usuario u1, Usuario u2) {
        NodoUsuario nodoA = grafo.obtenerNodo(u1);
        NodoUsuario nodoB = grafo.obtenerNodo(u2);
        if (nodoA != null && nodoB != null) {
            nodoA.getAmigos().remove(nodoB);
            nodoB.getAmigos().remove(nodoA);
        }
    }

    public Set<Usuario> obtenerAmigos(Usuario usuario) {
        NodoUsuario nodo = grafo.obtenerNodo(usuario);
        if (nodo == null) return new HashSet<>();
        Set<Usuario> amigos = new HashSet<>();
        nodo.getAmigos().forEach(a -> amigos.add(a.getUsuario()));
        return amigos;
    }

    public List<Usuario> sugerirAmigos(Usuario usuario) {
        Set<Usuario> amigos = obtenerAmigos(usuario);
        Set<Usuario> sugerencias = amigosDeAmigos(usuario, 2); // nivel 2 = amigos de amigos
        sugerencias.removeAll(amigos);
        sugerencias.remove(usuario);
        return new ArrayList<>(sugerencias);
    }

    public void eliminarConexionTotal(Usuario usuario) {
        NodoUsuario nodo = grafo.obtenerNodo(usuario);
        if (nodo == null) return;

        // Eliminar conexiones desde otros nodos
        for (NodoUsuario n : grafo.getNodos().values()) {
            n.getAmigos().remove(nodo);
        }

        // Eliminar el nodo del grafo
        grafo.getNodos().remove((long) usuario.getUsername().hashCode());
    }

    

}