package co.uniquindio.proyecto.syncup.servicios;

import co.uniquindio.proyecto.syncup.repositorios.CancionRepositorio;
import co.uniquindio.proyecto.syncup.repositorios.UsuarioRepositorio;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AdministradorServicio {

    private final UsuarioRepositorio usuarioRepositorio;
    private final CancionRepositorio cancionRepositorio;

    public AdministradorServicio(UsuarioRepositorio usuarioRepositorio,
                                 CancionRepositorio cancionRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.cancionRepositorio = cancionRepositorio;
    }

    public Map<String, Object> obtenerEstadisticas() {
        Map<String, Object> estadisticas = new HashMap<>();

        estadisticas.put("usuariosTotales", usuarioRepositorio.count());
        estadisticas.put("cancionesTotales", cancionRepositorio.count());

        // Agrupación por género
        Map<String, Long> cancionesPorGenero = new HashMap<>();
        cancionRepositorio.findAll().forEach(c ->
                cancionesPorGenero.merge(c.getGenero(), 1L, Long::sum)
        );
        estadisticas.put("cancionesPorGenero", cancionesPorGenero);

        // Agrupación por artista
        Map<String, Long> cancionesPorArtista = new HashMap<>();
        cancionRepositorio.findAll().forEach(c ->
                cancionesPorArtista.merge(c.getArtista(), 1L, Long::sum)
        );
        estadisticas.put("cancionesPorArtista", cancionesPorArtista);

        return estadisticas;
    }
}

