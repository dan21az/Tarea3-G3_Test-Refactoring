package com.example;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.Composite.Propiedad;
import com.example.Composite.Unidad;
import com.example.Singleton.BaseDatosSingleton;
import com.example.dominio.incidentes.Incidente;
import com.example.dominio.reservas.Reserva;
import com.example.dominio.usuarios.Huesped;

class GestionBDSingletonTest {

    private BaseDatosSingleton db;

    @BeforeEach
    void setUp() {
        db = BaseDatosSingleton.getInstance();
        db.limpiar();
    }

    @Test
    @DisplayName("G037 - getInstance() devuelve siempre la misma instancia (Singleton)")
    void g037_getInstance_siempreMismaInstancia() {
        BaseDatosSingleton instancia1 = BaseDatosSingleton.getInstance();
        BaseDatosSingleton instancia2 = BaseDatosSingleton.getInstance();
        BaseDatosSingleton instancia3 = BaseDatosSingleton.getInstance();

        assertSame(instancia1, instancia2);
        assertSame(instancia2, instancia3);
        assertSame(instancia1, instancia3);
    }

    @Test
    @DisplayName("G038 - guardarPropiedad con propiedad válida la añade al catálogo")
    void g038_guardarPropiedad_propiedadValida() {
        Propiedad propiedad = new Propiedad("Casa Azul", "Bogotá", "No mascotas");

        db.guardarPropiedad(propiedad);

        assertEquals(1, db.getCatalogo().size());
        assertTrue(db.getCatalogo().contains(propiedad));
    }

    @Test
    @DisplayName("G039 - guardarPropiedad con propiedad duplicada no la añade (validación de duplicados)")
    void g039_guardarPropiedad_propiedadDuplicada_noGuardada() {
        Propiedad propiedad1 = new Propiedad("Casa Azul", "Bogotá", "No mascotas");
        Propiedad propiedad2 = new Propiedad("Casa Azul", "Bogotá", "Regla diferente");

        db.guardarPropiedad(propiedad1);
        db.guardarPropiedad(propiedad2);

        assertEquals(1, db.getCatalogo().size());
        assertTrue(db.getCatalogo().contains(propiedad1));
        assertFalse(db.getCatalogo().contains(propiedad2));
    }

    @Test
    @DisplayName("G040 - guardarPropiedad con propiedad nula no se guarda y no lanza excepción")
    void g040_guardarPropiedad_propiedadNula_noLanzaExcepcion() {
        assertDoesNotThrow(() -> db.guardarPropiedad(null));

        assertTrue(db.getCatalogo().isEmpty());
    }

    @Test
    @DisplayName("G041 - guardarReserva con reserva válida la añade a la lista de reservas")
    void g041_guardarReserva_reservaValida() {
        Huesped huesped = new Huesped("H1", "Laura", "laura@mail.com", "123");
        Unidad unidad = new Unidad("U-100", "Casa", 120.0);
        Reserva reserva = new Reserva(new Date(), new Date(), huesped, unidad);

        db.guardarReserva(reserva);

        assertEquals(1, db.getReservas().size());
        assertTrue(db.getReservas().contains(reserva));
    }

    @Test
    @DisplayName("G042 - guardarIncidente con incidente válido lo añade a la lista de incidentes")
    void g042_guardarIncidente_incidenteValido() {
        Reserva reserva = new Reserva(new Date(), new Date(), null, null);
        Incidente incidente = new Incidente("INC-001", "Problema de agua", reserva);

        db.guardarIncidente(incidente);

        assertEquals(1, db.getIncidentes().size());
        assertTrue(db.getIncidentes().contains(incidente));
    }

    @Test
    @DisplayName("G043 - buscarPropiedadPorNombre con nombre existente devuelve la propiedad encontrada")
    void g043_buscarPropiedadPorNombre_nombreExistente() {
        Propiedad propiedad = new Propiedad("Casa Azul", "Bogotá", "No mascotas");
        db.guardarPropiedad(propiedad);

        Propiedad result = db.buscarPropiedadPorNombre("Casa Azul");

        assertNotNull(result);
        assertEquals("Casa Azul", result.getNombre());
        assertSame(propiedad, result);
    }

    @Test
    @DisplayName("G044 - buscarPropiedadPorNombre con nombre nulo devuelve null")
    void g044_buscarPropiedadPorNombre_nombreNulo_devuelveNull() {
        Propiedad propiedad = new Propiedad("Casa Azul", "Bogotá", "No mascotas");
        db.guardarPropiedad(propiedad);

        Propiedad result = db.buscarPropiedadPorNombre(null);

        assertNull(result);
    }

    @Test
    @DisplayName("G045 - limpiar con BD con datos deja las listas vacías")
    void g045_limpiar_bdConDatos_listasVacias() {
        Propiedad propiedad = new Propiedad("Casa Azul", "Bogotá", "No mascotas");
        Unidad unidad = new Unidad("U-100", "Casa", 120.0);
        Reserva reserva = new Reserva(new Date(), new Date(), null, unidad);
        Incidente incidente = new Incidente("INC-001", "Problema de agua", reserva);

        db.guardarPropiedad(propiedad);
        db.guardarReserva(reserva);
        db.guardarIncidente(incidente);

        assertEquals(1, db.getCatalogo().size());
        assertEquals(1, db.getReservas().size());
        assertEquals(1, db.getIncidentes().size());

        db.limpiar();

        assertTrue(db.getCatalogo().isEmpty());
        assertTrue(db.getReservas().isEmpty());
        assertTrue(db.getIncidentes().isEmpty());
    }
}
