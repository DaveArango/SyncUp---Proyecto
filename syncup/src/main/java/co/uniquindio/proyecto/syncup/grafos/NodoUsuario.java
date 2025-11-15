package co.uniquindio.proyecto.syncup.grafos;

import co.uniquindio.proyecto.syncup.entidades.Usuario;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public class NodoUsuario {
    private Usuario usuario;
    private Set<NodoUsuario> amigos = new HashSet<>();

    public NodoUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public void agregarAmigo(NodoUsuario amigo) {
        amigos.add(amigo);
    }
}

