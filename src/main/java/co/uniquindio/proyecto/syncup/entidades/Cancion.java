package co.uniquindio.proyecto.syncup.entidades;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "canciones")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Cancion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // para que se autoincremente
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String artista;

    @Column(nullable = false)
    private String genero;

    private Integer anio;
    private Integer duracion;

    @Lob
    @Column(name="audio")
    private byte[] audio;

    @ManyToMany(mappedBy = "listaFavoritos")
    @JsonIgnoreProperties("listaFavoritos")
    @JsonIgnore
    private List<Usuario> oyentes = new ArrayList<>();

    private String rutaArchivo;
}
