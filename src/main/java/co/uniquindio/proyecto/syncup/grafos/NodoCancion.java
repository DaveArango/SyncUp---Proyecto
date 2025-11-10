package co.uniquindio.proyecto.syncup.grafos;

import co.uniquindio.proyecto.syncup.entidades.Cancion;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class NodoCancion {
    private Cancion cancion;
    private Map<NodoCancion, Double> vecinos = new HashMap<>();

    public NodoCancion(Cancion cancion) {
        this.cancion = cancion;
    }

    public void agregarVecino(NodoCancion vecino, double similitud) {
        vecinos.put(vecino, similitud);
    }
}

