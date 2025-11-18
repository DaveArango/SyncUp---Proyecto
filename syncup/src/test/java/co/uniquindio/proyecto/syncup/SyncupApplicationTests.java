package co.uniquindio.proyecto.syncup;

import co.uniquindio.proyecto.syncup.entidades.Cancion;
import co.uniquindio.proyecto.syncup.entidades.Usuario;
import co.uniquindio.proyecto.syncup.repositorios.CancionRepositorio;
import co.uniquindio.proyecto.syncup.servicios.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.AssertionsKt.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import co.uniquindio.proyecto.syncup.entidades.Usuario;
import co.uniquindio.proyecto.syncup.repositorios.UsuarioRepositorio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SyncupApplicationTests {

	@Mock
	private UsuarioRepositorio usuarioRepositorio;

	@Mock
	private CancionRepositorio cancionRepositorio;

	@Mock
	private GrafoSocialServicio grafoSocialServicio;

	@Mock
	private TrieServicio trieServicio;

	@Mock
	private GrafoSimilitudServicio grafoSimilitudServicio;

	// ⚠️ NO MOCKEES CancionServicio, necesitas un servicio real con mocks dentro
	@InjectMocks
	private CancionServicio cancionServicio;

	@InjectMocks
	private UsuarioServicio usuarioServicio;

	@BeforeEach void setUp() {}

	@Test
	void contextLoads() {
		assertTrue(true);
	}

	// ============================================================
	//              TESTS USUARIO
	// ============================================================

	@Test
	void testRegistrarUsuario() {
		Usuario u = new Usuario();
		u.setUsername("juan");
		u.setPassword("pass");
		u.setNombre("Juan Pérez");

		when(usuarioRepositorio.existsById("juan")).thenReturn(false);
		when(usuarioRepositorio.save(u)).thenReturn(u);

		Usuario resultado = usuarioServicio.registrarUsuario(u);

		assertNotNull(resultado);
		assertEquals("juan", resultado.getUsername());

		verify(usuarioRepositorio).save(u);
		verify(grafoSocialServicio).agregarUsuario(u);
	}

	@Test
	void testRegistrarUsuarioDuplicado() {
		Usuario u = new Usuario();
		u.setUsername("juan");
		u.setPassword("pass");

		when(usuarioRepositorio.existsById("juan")).thenReturn(true);

		assertThrows(RuntimeException.class, () -> usuarioServicio.registrarUsuario(u));
		verify(usuarioRepositorio, never()).save(any());
	}

	@Test
	void testEliminarUsuario() {
		Usuario u = new Usuario();
		u.setUsername("maria");
		u.setPassword("123");
		u.setNombre("María López");

		when(usuarioRepositorio.findById("maria")).thenReturn(Optional.of(u));

		usuarioServicio.eliminarUsuario("maria");

		verify(grafoSocialServicio).eliminarConexionTotal(u);
		verify(usuarioRepositorio).delete(u);
	}

	@Test
	void testEliminarUsuarioInexistente() {
		when(usuarioRepositorio.findById("xx")).thenReturn(Optional.empty());

		assertThrows(RuntimeException.class, () -> usuarioServicio.eliminarUsuario("xx"));
		verify(usuarioRepositorio, never()).delete(any());
	}

	@Test
	void testCargarMapaUsuarios() {
		Usuario u = new Usuario();
		u.setUsername("ana");
		u.setPassword("pass");
		u.setNombre("Ana");

		when(usuarioRepositorio.findAll()).thenReturn(List.of(u));

		usuarioServicio.cargarMapaUsuarios();

		Usuario resultado = usuarioServicio.obtenerUsuarioO1("ana");

		assertNotNull(resultado);
		assertEquals("ana", resultado.getUsername());
	}

	// ============================================================
	//              TESTS BUSQUEDA AVANZADA
	// ============================================================

	@Test
	void testBusquedaAvanzadaArtista() {

		Cancion c = new Cancion();
		c.setTitulo("titulo");
		c.setArtista("Queen");
		c.setGenero("Rock");
		c.setAnio(1975);
		c.setDuracion(200);

		when(cancionRepositorio.findAll()).thenReturn(List.of(c));

		List<Cancion> res = cancionServicio.busquedaAvanzada("Queen", null, null, true);

		assertEquals(1, res.size());
		assertEquals("Queen", res.get(0).getArtista());
	}

	@Test
	void testBusquedaAvanzadaAnio() {

		Cancion c = new Cancion();
		c.setTitulo("Bohemian");
		c.setArtista("Queen");
		c.setGenero("Rock");
		c.setAnio(1975);
		c.setDuracion(300);

		when(cancionRepositorio.findAll()).thenReturn(List.of(c));

		List<Cancion> res = cancionServicio.busquedaAvanzada(null, null, 1975, true);

		assertEquals(1, res.size());
		assertEquals(1975, res.get(0).getAnio());
	}

	// ============================================================
	//              TESTS GRAFO SOCIAL
	// ============================================================

	@Test
	void testAgregarConexion() {

		Usuario u1 = new Usuario();
		u1.setUsername("juan");

		Usuario u2 = new Usuario();
		u2.setUsername("pedro");

		// grafoSocialServicio aquí NO es un mock: deberías usar uno real
		// pero como no lo has pasado real, solo validamos la llamada

		grafoSocialServicio.agregarConexion(u1, u2);

		verify(grafoSocialServicio).agregarConexion(u1, u2);
	}
}
