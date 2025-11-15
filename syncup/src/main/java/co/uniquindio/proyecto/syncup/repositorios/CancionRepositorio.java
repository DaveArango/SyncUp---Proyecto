package co.uniquindio.proyecto.syncup.repositorios;

import co.uniquindio.proyecto.syncup.entidades.Cancion;
import co.uniquindio.proyecto.syncup.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CancionRepositorio extends JpaRepository<Cancion, Long> {
    Cancion findByTitulo(String titulo);
    List<Cancion> findByArtista(String artista);
    boolean existsByTitulo(String titulo);
}
