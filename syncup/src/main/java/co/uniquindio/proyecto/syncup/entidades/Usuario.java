package co.uniquindio.proyecto.syncup.entidades;

import co.uniquindio.proyecto.syncup.enums.RolUsuario;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "usuarios", uniqueConstraints = @UniqueConstraint(columnNames = "username"))
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter
@EqualsAndHashCode(of = "username")
public class Usuario {
    @Id
    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nombre;

    @ManyToMany
    @JoinTable(
            name="usuario_favoritos", // Tabla intermedia
            joinColumns = @JoinColumn(name="usuario_id"), //Apunta al usuario
            inverseJoinColumns = @JoinColumn(name="cancion_id") //Apunta a la cancion
    )
    @JsonIgnoreProperties("oyentes")
    @JsonIgnore
    private List<Cancion> listaFavoritos = new LinkedList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RolUsuario rol;

    @ManyToMany
    @JoinTable(
            name = "usuario_amigos",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "amigo_id")
    )
    @JsonIgnoreProperties("amigos")
    @JsonIgnore
    private List<Usuario> amigos = new LinkedList<>();

}
