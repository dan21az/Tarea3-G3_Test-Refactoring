package com.example;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.Composite.CompPropiedad;
import com.example.Composite.Propiedad;
import com.example.Composite.Unidad;
import com.example.Singleton.Repositorio;
import com.example.Singleton.RepositorioMemoria;
import com.example.State.Disponible;
import com.example.State.EstadoUnidad;
import com.example.State.Mantenimiento;
import com.example.dominio.usuarios.Anfitrion;

class GestionUnidadesReglasTest {

    private Repositorio db;
    private Anfitrion anfitrion;

    @BeforeEach
    void setUp() {
        db = new RepositorioMemoria();
        db.limpiar();
        anfitrion = new Anfitrion("A1", "Juan Pérez", "juan@mail.com", "pass123");
    }

    private void registrarCasaAzul() {
        Propiedad prop = new Propiedad("Casa Azul", "Bogotá", "No mascotas");
        prop.añadirUnidad(new Unidad("U-100", "Casa", 90.0));
        anfitrion.registrarPropiedad(prop, db);
    }

    /* ---------- G001 - G007: Anfitrion (registro de propiedades) ---------- */

    @Test
    @DisplayName("G001 - Registro exitoso de propiedad con unidades")
    void g001_registrarNuevaPropiedadConUnidades_casoNormal() {
        List<CompPropiedad> unidades = new ArrayList<>();
        unidades.add(new Unidad("U-100", "Casa", 90.0));
        List<String> servicios = Arrays.asList("WiFi", "Piscina");

        Propiedad result = anfitrion.registrarNuevaPropiedadConUnidades(
                "Casa Azul", "Bogotá", "No mascotas", unidades, servicios, db);

        assertNotNull(result);
        assertEquals("Casa Azul", result.getNombre());
        assertTrue(result.esValida());
        assertTrue(anfitrion.getPropiedades().contains(result));
        assertTrue(db.getCatalogo().contains(result));
    }

    @Test
    @DisplayName("G002 - No registrar propiedad con lista de unidades vacía")
    void g002_registrarNuevaPropiedadConUnidades_listaVacia() {
        List<CompPropiedad> unidades = new ArrayList<>();
        List<String> servicios = Arrays.asList("WiFi");

        Propiedad result = anfitrion.registrarNuevaPropiedadConUnidades(
                "Casa Azul", "Bogotá", "No mascotas", unidades, servicios, db);

        assertNull(result);
        assertTrue(anfitrion.getPropiedades().isEmpty());
        assertTrue(db.getCatalogo().isEmpty());
    }

    @Test
    @DisplayName("G003 - No registrar propiedad con lista de unidades nula")
    void g003_registrarNuevaPropiedadConUnidades_listaNula() {
        List<String> servicios = Arrays.asList("WiFi");

        Propiedad result = anfitrion.registrarNuevaPropiedadConUnidades(
                "Casa Azul", "Bogotá", "No mascotas", null, servicios, db);

        assertNull(result);
        assertTrue(anfitrion.getPropiedades().isEmpty());
    }

    @Test
    @DisplayName("G004 - Unidad nula en la lista no valida la propiedad")
    void g004_registrarNuevaPropiedadConUnidades_unidadNula() {
        List<CompPropiedad> unidades = new ArrayList<>();
        unidades.add(null);
        List<String> servicios = Arrays.asList("WiFi");

        Propiedad result = anfitrion.registrarNuevaPropiedadConUnidades(
                "Casa Azul", "Bogotá", "No mascotas", unidades, servicios, db);

        assertNull(result);
        assertTrue(anfitrion.getPropiedades().isEmpty());
    }

    @Test
    @DisplayName("G005 - No registrar propiedad nula")
    void g005_registrarPropiedad_nula() {
        anfitrion.registrarPropiedad(null, db);

        assertTrue(anfitrion.getPropiedades().isEmpty());
        assertTrue(db.getCatalogo().isEmpty());
    }

    @Test
    @DisplayName("G006 - No registrar propiedad inválida (sin unidades)")
    void g006_registrarPropiedad_invalida() {
        Propiedad prop = new Propiedad("Casa Azul", "Bogotá", "No mascotas");
        assertFalse(prop.esValida());

        anfitrion.registrarPropiedad(prop, db);

        assertTrue(anfitrion.getPropiedades().isEmpty());
        assertTrue(db.getCatalogo().isEmpty());
    }

    @Test
    @DisplayName("G007 - Registro válido de propiedad con unidades")
    void g007_registrarPropiedad_valida() {
        Propiedad prop = new Propiedad("Casa Azul", "Bogotá", "No mascotas");
        prop.añadirUnidad(new Unidad("U-100", "Casa", 90.0));

        anfitrion.registrarPropiedad(prop, db);

        assertEquals(1, anfitrion.getPropiedades().size());
        assertTrue(anfitrion.getPropiedades().contains(prop));
        assertTrue(db.getCatalogo().contains(prop));
    }

    /* ---------- G008 - G012: buscarPropiedadPorNombre ---------- */

    @Test
    @DisplayName("G008 - Búsqueda por nombre exitosa")
    void g008_buscarPropiedadPorNombre_existente() {
        registrarCasaAzul();

        Propiedad result = anfitrion.buscarPropiedadPorNombre("Casa Azul");

        assertNotNull(result);
        assertEquals("Casa Azul", result.getNombre());
    }

    @Test
    @DisplayName("G009 - Propiedad no encontrada por nombre")
    void g009_buscarPropiedadPorNombre_inexistente() {
        registrarCasaAzul();

        Propiedad result = anfitrion.buscarPropiedadPorNombre("Casa Inexistente");

        assertNull(result);
    }

    @Test
    @DisplayName("G010 - Búsqueda con nombre nulo devuelve null")
    void g010_buscarPropiedadPorNombre_nulo() {
        registrarCasaAzul();

        Propiedad result = anfitrion.buscarPropiedadPorNombre(null);

        assertNull(result);
    }

    @Test
    @DisplayName("G011 - Búsqueda insensible a mayúsculas")
    void g011_buscarPropiedadPorNombre_minusculas() {
        registrarCasaAzul();

        Propiedad result = anfitrion.buscarPropiedadPorNombre("casa azul");

        assertNotNull(result);
        assertEquals("Casa Azul", result.getNombre());
    }

    @Test
    @DisplayName("G012 - Búsqueda con nombre con espacios en blanco")
    void g012_buscarPropiedadPorNombre_conEspacios() {
        registrarCasaAzul();

        Propiedad result = anfitrion.buscarPropiedadPorNombre(" Casa Azul ");

        assertNotNull(result);
        assertEquals("Casa Azul", result.getNombre());
    }

    /* ---------- G013 - G015: buscarUnidadEnPropiedad ---------- */

    @Test
    @DisplayName("G013 - Búsqueda de unidad dentro de propiedad existente")
    void g013_buscarUnidadEnPropiedad_existente() {
        registrarCasaAzul();

        Unidad result = anfitrion.buscarUnidadEnPropiedad("Casa Azul", "U-100");

        assertNotNull(result);
        assertEquals("U-100", result.getIdUnidad());
    }

    @Test
    @DisplayName("G014 - Búsqueda de unidad con propiedad inexistente devuelve null")
    void g014_buscarUnidadEnPropiedad_propiedadInexistente() {
        Unidad result = anfitrion.buscarUnidadEnPropiedad("Casa Inexistente", "U-100");

        assertNull(result);
    }

    @Test
    @DisplayName("G015 - Búsqueda de unidad con ID inexistente devuelve null")
    void g015_buscarUnidadEnPropiedad_unidadInexistente() {
        registrarCasaAzul();

        Unidad result = anfitrion.buscarUnidadEnPropiedad("Casa Azul", "U-999");

        assertNull(result);
    }

    /* ---------- G016 - G019: gestión de estados y reglas ---------- */

    @Test
    @DisplayName("G016 - Cambio de estado a mantenimiento")
    void g016_actualizarEstadoUnidad_aMantenimiento() {
        Unidad unidad = new Unidad("U-100", "Casa", 90.0);
        EstadoUnidad mantenimiento = new Mantenimiento();

        anfitrion.actualizarEstadoUnidad(unidad, mantenimiento);

        assertTrue(unidad.getEstado() instanceof Mantenimiento);
    }

    @Test
    @DisplayName("G017 - Cambio de estado a disponible")
    void g017_actualizarEstadoUnidad_aDisponible() {
        Unidad unidad = new Unidad("U-100", "Casa", 90.0);
        unidad.cambiarEstado(new Mantenimiento());

        anfitrion.actualizarEstadoUnidad(unidad, new Disponible());

        assertTrue(unidad.getEstado() instanceof Disponible);
    }

    @Test
    @DisplayName("G018 - Estado de unidad nula no lanza excepción")
    void g018_actualizarEstadoUnidad_unidadNula() {
        assertDoesNotThrow(() -> anfitrion.actualizarEstadoUnidad(null, new Mantenimiento()));
    }

    @Test
    @DisplayName("G019 - Actualización de reglas de propiedad")
    void g019_gestionarReglas_actualizaReglas() {
        Propiedad prop = new Propiedad("Casa Azul", "Bogotá", "No mascotas");
        String nuevasReglas = "No fumar, sin mascotas";

        anfitrion.gestionarReglas(prop, nuevasReglas);

        assertEquals(nuevasReglas, prop.getReglas());
    }

    /* ---------- G021 - G025: horarios ---------- */

    @Test
    @DisplayName("G021 - Actualización de horarios de propiedad")
    void g021_actualizarHorarioDePropiedad_valida() {
        Propiedad prop = new Propiedad("Casa Azul", "Bogotá", "No mascotas");

        anfitrion.actualizarHorarioDePropiedad(prop, "15:00", "12:00");

        assertEquals("15:00", prop.getCheckIn());
        assertEquals("12:00", prop.getCheckOut());
    }

    @Test
    @DisplayName("G022 - Propiedad nula en horarios no hace nada")
    void g022_actualizarHorarioDePropiedad_nula() {
        assertDoesNotThrow(() -> anfitrion.actualizarHorarioDePropiedad(null, "15:00", "12:00"));
    }

    @Test
    @DisplayName("G023 - Actualización parcial: solo checkIn")
    void g023_actualizarHorarios_soloCheckIn() {
        Propiedad prop = new Propiedad("Casa Azul", "Bogotá", "No mascotas");

        prop.actualizarHorarios("16:00", null);

        assertEquals("16:00", prop.getCheckIn());
        assertNull(prop.getCheckOut());
    }

    @Test
    @DisplayName("G024 - Horarios nulos no modifican valores previos")
    void g024_actualizarHorarios_nulosNoModifican() {
        Propiedad prop = new Propiedad("Casa Azul", "Bogotá", "No mascotas");
        prop.actualizarHorarios("10:00", "11:00");

        prop.actualizarHorarios(null, null);

        assertEquals("10:00", prop.getCheckIn());
        assertEquals("11:00", prop.getCheckOut());
    }

    @Test
    @DisplayName("G025 - String en blanco no actualiza checkIn")
    void g025_actualizarHorarios_checkInBlanco() {
        Propiedad prop = new Propiedad("Casa Azul", "Bogotá", "No mascotas");
        prop.actualizarHorarios("10:00", "11:00");

        prop.actualizarHorarios("", "13:00");

        assertEquals("10:00", prop.getCheckIn());
        assertEquals("13:00", prop.getCheckOut());
    }

    /* ---------- G026 - G031: composite Propiedad ---------- */

    @Test
    @DisplayName("G026 - Añadir unidad válida al composite")
    void g026_añadirUnidad_valida() {
        Propiedad prop = new Propiedad("Edificio", "Calle 1", "Regla");
        Unidad unidad = new Unidad("U-100", "Casa", 100.0);

        prop.añadirUnidad(unidad);

        assertEquals(1, prop.getChildren().size());
        assertSame(unidad, prop.getChildren().get(0));
        assertSame(prop, unidad.getPropiedad());
    }

    @Test
    @DisplayName("G027 - Añadir unidad nula al composite")
    void g027_añadirUnidad_nula() {
        Propiedad prop = new Propiedad("Edificio", "Calle 1", "Regla");

        prop.añadirUnidad(null);

        assertEquals(1, prop.getChildren().size());
        assertNull(prop.getChildren().get(0));
        assertFalse(prop.esValida());
    }

    @Test
    @DisplayName("G028 - Remover unidad existente del composite")
    void g028_removerUnidad_existente() {
        Propiedad prop = new Propiedad("Edificio", "Calle 1", "Regla");
        Unidad unidad = new Unidad("U-100", "Casa", 100.0);
        prop.añadirUnidad(unidad);

        prop.removerUnidad(unidad);

        assertTrue(prop.getChildren().isEmpty());
    }

    @Test
    @DisplayName("G029 - Remover unidad inexistente no lanza excepción")
    void g029_removerUnidad_inexistente() {
        Propiedad prop = new Propiedad("Edificio", "Calle 1", "Regla");
        Unidad unidad = new Unidad("U-100", "Casa", 100.0);
        prop.añadirUnidad(unidad);
        Unidad otra = new Unidad("U-999", "Casa", 50.0);

        assertDoesNotThrow(() -> prop.removerUnidad(otra));

        assertEquals(1, prop.getChildren().size());
        assertSame(unidad, prop.getChildren().get(0));
    }

    @Test
    @DisplayName("G030 - Costo total del composite con 2 unidades")
    void g030_costo_dosUnidades() {
        Propiedad prop = new Propiedad("Edificio", "Calle 1", "Regla");
        Unidad u1 = new Unidad("U-100", "Casa", 100.0);
        Unidad u2 = new Unidad("U-200", "Casa", 150.0);
        prop.añadirUnidad(u1);
        prop.añadirUnidad(u2);

        assertEquals(250.0, prop.costo(), 0.0);
    }

    @Test
    @DisplayName("G031 - Costo sin unidades es 0.0")
    void g031_costo_sinUnidades() {
        Propiedad prop = new Propiedad("Edificio", "Calle 1", "Regla");

        assertEquals(0.0, prop.costo(), 0.0);
    }
}
