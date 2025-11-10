package co.uniquindio.proyecto.syncup.controladores;

import co.uniquindio.proyecto.syncup.servicios.AdministradorServicio;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdministradorControlador {

    private final AdministradorServicio administradorServicio;

    public AdministradorControlador(AdministradorServicio administradorServicio) {
        this.administradorServicio = administradorServicio;
    }

    @GetMapping("/estadisticas")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas() {
        return ResponseEntity.ok(administradorServicio.obtenerEstadisticas());
    }
}

