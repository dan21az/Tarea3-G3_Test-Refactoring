package com.example;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.Composite.Unidad;
import com.example.dominio.resenas.Resena;
import com.example.dominio.resenas.ServicioResena;
import com.example.dominio.reservas.Reserva;
import com.example.dominio.usuarios.Huesped;

class ServicioResenaTest {

    private ServicioResena servicio;
    private Huesped huesped;
    private String comentario;
    private Reserva reserva;

    @BeforeEach
    void setUp() {
        servicio = new ServicioResena();
        huesped = new Huesped("H1", "Laura", "laura@mail.com", "123");
        comentario = "Excelente estancia";
        Date inicio = new GregorianCalendar(2026, Calendar.SEPTEMBER, 1).getTime();
        Date fin = new GregorianCalendar(2026, Calendar.SEPTEMBER, 5).getTime();
        Unidad unidad = new Unidad("U-100", "Casa", 120.0);
        reserva = new Reserva(inicio, fin, huesped, unidad);
    }

    @Test
    @DisplayName("G050 - Creación de reseña válida")
    void g050_crearResena_valida_casoNormal() {
        Resena resena = servicio.crearResena(huesped, comentario, 4, reserva);

        assertNotNull(resena);
        assertEquals(huesped, resena.getHuesped());
        assertEquals(comentario, resena.getComentario());
        assertEquals(4, resena.getPuntuacion());
        assertEquals(reserva, resena.getReserva());
        assertTrue(servicio.getResenas().contains(resena));
    }

    @Test
    @DisplayName("G051 - Reseña sin huésped")
    void g051_crearResena_huespedNulo_casoError() {
        Resena resena = servicio.crearResena(null, comentario, 4, reserva);

        assertNull(resena);
        assertTrue(servicio.getResenas().isEmpty());
    }

    @Test
    @DisplayName("G052 - Reseña con comentario en blanco")
    void g052_crearResena_comentarioBlanco_casoError() {
        Resena resena = servicio.crearResena(huesped, "   ", 4, reserva);

        assertNull(resena);
        assertTrue(servicio.getResenas().isEmpty());
    }

    @Test
    @DisplayName("G053 - Comentario nulo")
    void g053_crearResena_comentarioNulo_casoError() {
        Resena resena = servicio.crearResena(huesped, null, 4, reserva);

        assertNull(resena);
        assertTrue(servicio.getResenas().isEmpty());
    }

    @Test
    @DisplayName("G054 - Puntuación mínima inválida (0)")
    void g054_crearResena_puntuacionCero_casoError() {
        Resena resena = servicio.crearResena(huesped, comentario, 0, reserva);

        assertNull(resena);
        assertTrue(servicio.getResenas().isEmpty());
    }

    @Test
    @DisplayName("G055 - Puntuación máxima inválida (6)")
    void g055_crearResena_puntuacionSeis_casoError() {
        Resena resena = servicio.crearResena(huesped, comentario, 6, reserva);

        assertNull(resena);
        assertTrue(servicio.getResenas().isEmpty());
    }

    @Test
    @DisplayName("G056 - Puntuación mínima válida (1)")
    void g056_crearResena_puntuacionUno_casoLimite() {
        Resena resena = servicio.crearResena(huesped, comentario, 1, reserva);

        assertNotNull(resena);
        assertEquals(1, resena.getPuntuacion());
        assertTrue(servicio.getResenas().contains(resena));
    }

    @Test
    @DisplayName("G057 - Puntuación máxima válida (5)")
    void g057_crearResena_puntuacionCinco_casoLimite() {
        Resena resena = servicio.crearResena(huesped, comentario, 5, reserva);

        assertNotNull(resena);
        assertEquals(5, resena.getPuntuacion());
        assertTrue(servicio.getResenas().contains(resena));
    }

    @Test
    @DisplayName("G058 - Reseña sin reserva asociada")
    void g058_crearResena_sinReserva_casoLimite() {
        Resena resena = servicio.crearResena(huesped, comentario, 4);

        assertNotNull(resena);
        assertNull(resena.getReserva());
        assertTrue(servicio.getResenas().contains(resena));
    }
}