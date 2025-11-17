package co.uniquindio.proyecto.syncup.repositorios;

import co.uniquindio.proyecto.syncup.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, String> {
    //No se agrega el de obtener por username por que @Repository lo tiene implicito
    boolean existsByUsername(String username);
    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.listaFavoritos WHERE u.username = :username")
    Optional<Usuario> findByIdConFavoritos(@Param("username") String username);
    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.amigos WHERE u.username = :username")
    Optional<Usuario> findByIdConAmigos(@Param("username") String username);
    
}
