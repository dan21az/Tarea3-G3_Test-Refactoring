package com.example;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.ChainOfResponsibility.AnfitrionHandler;
import com.example.ChainOfResponsibility.ManejadorIncidente;
import com.example.ChainOfResponsibility.ModeradorHandler;
import com.example.ChainOfResponsibility.SoporteLegalHandler;
import com.example.Composite.Propiedad;
import com.example.Composite.Unidad;
import com.example.Singleton.BaseDatosSingleton;
import com.example.dominio.incidentes.EstadoIncidente;
import com.example.dominio.incidentes.Incidente;
import com.example.dominio.reservas.Reserva;
import com.example.dominio.usuarios.Anfitrion;
import com.example.dominio.usuarios.Huesped;
import com.example.dominio.usuarios.Moderador;
import com.example.dominio.usuarios.SoporteLegal;

class ServicioIncidenteTest {

    private BaseDatosSingleton db;

    @BeforeEach
    void setUp() throws Exception {
        db = BaseDatosSingleton.getInstance();
        db.limpiar();
        resetSecuenciaIncidente();
    }

    private void resetSecuenciaIncidente() throws Exception {
        Field field = Incidente.class.getDeclaredField("SECUENCIA");
        field.setAccessible(true);
        AtomicLong seq = (AtomicLong) field.get(null);
        seq.set(1);
    }

    private Reserva crearReserva() {
        Unidad unidad = new Unidad("U-1", "Casa", 100.0);
        Huesped huesped = new Huesped("H1", "Laura", "laura@mail.com", "123");
        return new Reserva(new Date(), new Date(), huesped, unidad);
    }

    static class HandlerPrueba extends ManejadorIncidente {
        boolean fueLlamado = false;

        @Override
        public void manejar(Incidente incidente) {
            manejar(incidente, false);
        }

        @Override
        public String getRol() {
            return "Prueba";
        }

        @Override
        public void resolverIncidente(Incidente incidente) {
            fueLlamado = true;
        }

        @Override
        public void noResolverIncidente(Incidente incidente) {
            fueLlamado = true;
        }
    }

    // ===== I001-I013: Incidente (estados) =====

    @Test
    @DisplayName("I001 - resolver incidente ABIERTO pasa a RESUELTO")
    void i001_resolver_incidenteAbierto_pasaAResuelto() {
        Incidente incidente = new Incidente("Problema wifi", crearReserva());

        incidente.resolver();

        assertEquals(EstadoIncidente.RESUELTO, incidente.getEstado());
    }

    @Test
    @DisplayName("I002 - resolver incidente RESUELTO no cambia de estado")
    void i002_resolver_incidenteResuelto_noCambia() {
        Incidente incidente = new Incidente("Problema wifi", crearReserva());
        incidente.resolver();

        incidente.resolver();

        assertEquals(EstadoIncidente.RESUELTO, incidente.getEstado());
    }

    @Test
    @DisplayName("I003 - resolver incidente CRITICO no cambia de estado")
    void i003_resolver_incidenteCritico_noCambia() {
        Incidente incidente = new Incidente("Problema wifi", crearReserva());
        incidente.cambiarEstado(EstadoIncidente.CRITICO);

        incidente.resolver();

        assertEquals(EstadoIncidente.CRITICO, incidente.getEstado());
    }

    @Test
    @DisplayName("I004 - resolverConRevision de CRITICO pasa a RESUELTO")
    void i004_resolverConRevision_incidenteCritico_pasaAResuelto() {
        Incidente incidente = new Incidente("Problema wifi", crearReserva());
        incidente.cambiarEstado(EstadoIncidente.CRITICO);

        incidente.resolverConRevision();

        assertEquals(EstadoIncidente.RESUELTO, incidente.getEstado());
    }

    @Test
    @DisplayName("I005 - resolverConRevision de ABIERTO pasa a RESUELTO")
    void i005_resolverConRevision_incidenteAbierto_pasaAResuelto() {
        Incidente incidente = new Incidente("Problema wifi", crearReserva());

        incidente.resolverConRevision();

        assertEquals(EstadoIncidente.RESUELTO, incidente.getEstado());
    }

    @Test
    @DisplayName("I006 - resolverConRevision de RESUELTO no cambia de estado")
    void i006_resolverConRevision_incidenteResuelto_noCambia() {
        Incidente incidente = new Incidente("Problema wifi", crearReserva());
        incidente.resolver();

        incidente.resolverConRevision();

        assertEquals(EstadoIncidente.RESUELTO, incidente.getEstado());
    }

    @Test
    @DisplayName("I007 - escalar incidente ABIERTO pasa a ESCALADO")
    void i007_escalar_incidenteAbierto_pasaAEScalado() {
        Incidente incidente = new Incidente("Problema wifi", crearReserva());

        incidente.escalar();

        assertEquals(EstadoIncidente.ESCALADO, incidente.getEstado());
    }

    @Test
    @DisplayName("I008 - escalar incidente RESUELTO no cambia de estado")
    void i008_escalar_incidenteResuelto_noCambia() {
        Incidente incidente = new Incidente("Problema wifi", crearReserva());
        incidente.resolver();

        incidente.escalar();

        assertEquals(EstadoIncidente.RESUELTO, incidente.getEstado());
    }

    @Test
    @DisplayName("I009 - escalar incidente CRITICO no cambia de estado")
    void i009_escalar_incidenteCritico_noCambia() {
        Incidente incidente = new Incidente("Problema wifi", crearReserva());
        incidente.cambiarEstado(EstadoIncidente.CRITICO);

        incidente.escalar();

        assertEquals(EstadoIncidente.CRITICO, incidente.getEstado());
    }

    @Test
    @DisplayName("I010 - cambiarEstado ABIERTO a EN_REVISION cambia de estado")
    void i010_cambiarEstado_abiertoAEnRevision_cambia() {
        Incidente incidente = new Incidente("Problema wifi", crearReserva());

        incidente.cambiarEstado(EstadoIncidente.EN_REVISION);

        assertEquals(EstadoIncidente.EN_REVISION, incidente.getEstado());
    }

    @Test
    @DisplayName("I011 - cambiarEstado RESUELTO a ABIERTO no modifica estado")
    void i011_cambiarEstado_resueltoAAbierto_noCambia() {
        Incidente incidente = new Incidente("Problema wifi", crearReserva());
        incidente.resolver();

        incidente.cambiarEstado(EstadoIncidente.ABIERTO);

        assertEquals(EstadoIncidente.RESUELTO, incidente.getEstado());
    }

    @Test
    @DisplayName("I012 - cambiarEstado CRITICO a RESUELTO no modifica estado")
    void i012_cambiarEstado_criticoAResuelto_noCambia() {
        Incidente incidente = new Incidente("Problema wifi", crearReserva());
        incidente.cambiarEstado(EstadoIncidente.CRITICO);

        incidente.cambiarEstado(EstadoIncidente.RESUELTO);

        assertEquals(EstadoIncidente.CRITICO, incidente.getEstado());
    }

    @Test
    @DisplayName("I013 - cambiarEstado ABIERTO a null establece estado nulo")
    void i013_cambiarEstado_abiertoANull_estadoNulo() {
        Incidente incidente = new Incidente("Problema wifi", crearReserva());

        incidente.cambiarEstado(null);

        assertNull(incidente.getEstado());
    }

    // ===== I014-I018: ServicioIncidente.manejar (Chain of Responsibility) =====

    @Test
    @DisplayName("I014 - manejar: Anfitrion no resuelve, escala a ESCALADO y pasa al siguiente")
    void i014_manejar_anfitrionNoResuelve_escalaYPasaAlSiguiente() {
        AnfitrionHandler anfitrionHandler = new AnfitrionHandler(
                new Anfitrion("A1", "Carlos", "carlos@mail.com", "123"));
        HandlerPrueba handlerPrueba = new HandlerPrueba();
        anfitrionHandler.setSiguiente(handlerPrueba);

        Incidente incidente = new Incidente("Problema wifi", crearReserva());

        anfitrionHandler.manejar(incidente, false);

        assertEquals(EstadoIncidente.ESCALADO, incidente.getEstado());
        assertTrue(handlerPrueba.fueLlamado);
    }

    @Test
    @DisplayName("I015 - manejar: Anfitrion resuelve el incidente")
    void i015_manejar_anfitrionResuelve_incidenteResuelto() {
        AnfitrionHandler anfitrionHandler = new AnfitrionHandler(
                new Anfitrion("A1", "Carlos", "carlos@mail.com", "123"));

        Incidente incidente = new Incidente("Problema wifi", crearReserva());

        anfitrionHandler.manejar(incidente, true);

        assertEquals(EstadoIncidente.RESUELTO, incidente.getEstado());
    }

    @Test
    @DisplayName("I016 - manejar: incidente RESUELTO no se reprocesa")
    void i016_manejar_incidenteResuelto_noReprocesa() {
        AnfitrionHandler anfitrionHandler = new AnfitrionHandler(
                new Anfitrion("A1", "Carlos", "carlos@mail.com", "123"));
        HandlerPrueba handlerPrueba = new HandlerPrueba();
        anfitrionHandler.setSiguiente(handlerPrueba);

        Incidente incidente = new Incidente("Problema wifi", crearReserva());
        incidente.resolver();

        anfitrionHandler.manejar(incidente, false);

        assertEquals(EstadoIncidente.RESUELTO, incidente.getEstado());
        assertFalse(handlerPrueba.fueLlamado);
    }

    @Test
    @DisplayName("I017 - manejar: Moderador no resuelve, escala a CRITICO")
    void i017_manejar_moderadorNoResuelve_pasaACritico() {
        ModeradorHandler moderadorHandler = new ModeradorHandler(new Moderador("Ana"));

        Incidente incidente = new Incidente("Problema wifi", crearReserva());
        incidente.cambiarEstado(EstadoIncidente.ESCALADO);

        moderadorHandler.manejar(incidente, false);

        assertEquals(EstadoIncidente.CRITICO, incidente.getEstado());
    }

    @Test
    @DisplayName("I018 - manejar: SoporteLegal resuelve con revision")
    void i018_manejar_soporteLegalResuelve_conRevision() {
        SoporteLegalHandler soporteHandler = new SoporteLegalHandler(
                new SoporteLegal("Abg. Pedro"));

        Incidente incidente = new Incidente("Problema wifi", crearReserva());
        incidente.cambiarEstado(EstadoIncidente.ESCALADO);

        soporteHandler.manejar(incidente, true);

        assertEquals(EstadoIncidente.RESUELTO, incidente.getEstado());
    }

    // ===== I019-I021: ManejadorIncidente (cadena) =====

    @Test
    @DisplayName("I019 - buscarSiguiente encuentra ModeradorHandler en la cadena")
    void i019_buscarSiguiente_encuentraModeradorEnCadena() {
        AnfitrionHandler anfitrionHandler = new AnfitrionHandler(
                new Anfitrion("A1", "Carlos", "carlos@mail.com", "123"));
        ModeradorHandler moderadorHandler = new ModeradorHandler(new Moderador("Ana"));
        SoporteLegalHandler soporteHandler = new SoporteLegalHandler(
                new SoporteLegal("Abg. Pedro"));
        anfitrionHandler.setSiguiente(moderadorHandler);
        moderadorHandler.setSiguiente(soporteHandler);

        ModeradorHandler result = anfitrionHandler.buscarSiguiente(ModeradorHandler.class);

        assertNotNull(result);
        assertSame(moderadorHandler, result);
    }

    @Test
    @DisplayName("I020 - buscarSiguiente devuelve null cuando no hay siguiente")
    void i020_buscarSiguiente_noHaySiguiente_devuelveNull() {
        AnfitrionHandler anfitrionHandler = new AnfitrionHandler(
                new Anfitrion("A1", "Carlos", "carlos@mail.com", "123"));

        ModeradorHandler result = anfitrionHandler.buscarSiguiente(ModeradorHandler.class);

        assertNull(result);
    }

    @Test
    @DisplayName("I021 - setSiguiente enlaza la cadena correctamente")
    void i021_setSiguiente_enlazaCadena() {
        AnfitrionHandler anfitrionHandler = new AnfitrionHandler(
                new Anfitrion("A1", "Carlos", "carlos@mail.com", "123"));
        ModeradorHandler moderadorHandler = new ModeradorHandler(new Moderador("Ana"));

        anfitrionHandler.setSiguiente(moderadorHandler);

        assertSame(moderadorHandler, anfitrionHandler.getSiguiente());
    }

    // ===== I022-I027: reporte / busqueda =====

    @Test
    @DisplayName("I022 - constructor con descripcion genera ID automatico INC-001")
    void i022_constructor_descripcion_generaIdAutomatico() {
        Reserva reserva = crearReserva();

        Incidente incidente = new Incidente("Problema wifi", reserva);

        assertEquals("INC-001", incidente.getIdIncidente());
    }

    @Test
    @DisplayName("I023 - constructor con ID explicito mantiene ID y estado ABIERTO")
    void i023_constructor_idExplicito_mantieneEstadoAbierto() {
        Reserva reserva = crearReserva();

        Incidente incidente = new Incidente("INC-99", "Problema wifi", reserva);

        assertEquals("INC-99", incidente.getIdIncidente());
        assertEquals(EstadoIncidente.ABIERTO, incidente.getEstado());
    }

    @Test
    @DisplayName("I024 - reportarIncidente crea incidente en estado ABIERTO")
    void i024_reportarIncidente_creaIncidenteAbierto() {
        Huesped huesped = new Huesped("H1", "Laura", "laura@mail.com", "123");
        Reserva reserva = crearReserva();

        Incidente incidente = huesped.reportarIncidente("Problema wifi", reserva);

        assertNotNull(incidente);
        assertEquals(EstadoIncidente.ABIERTO, incidente.getEstado());
    }

    @Test
    @DisplayName("I025 - revisarIncidentesDeSusPropiedades filtra por propiedades del anfitrion")
    void i025_revisarIncidentes_filtraPorPropiedadesDelAnfitrion() {
        Unidad unidad = new Unidad("U-100", "Casa", 100.0);
        Propiedad propiedad = new Propiedad("Casa Azul", "Bogota", "r");
        propiedad.añadirUnidad(unidad);

        Anfitrion anfitrion = new Anfitrion("A1", "Carlos", "carlos@mail.com", "123");
        anfitrion.registrarPropiedad(propiedad);

        Huesped huesped = new Huesped("H1", "Laura", "laura@mail.com", "123");
        Reserva reserva = new Reserva(new Date(), new Date(), huesped, unidad);
        Incidente incidente = new Incidente("Problema wifi", reserva);
        db.guardarIncidente(incidente);

        List<Incidente> resultado = anfitrion.revisarIncidentesDeSusPropiedades();

        assertEquals(1, resultado.size());
        assertSame(incidente, resultado.get(0));
    }

    @Test
    @DisplayName("I026 - buscarIncidentesPorPropiedad devuelve incidentes asociados")
    void i026_buscarIncidentesPorPropiedad_devuelveIncidentesAsociados() {
        Unidad unidad = new Unidad("U-100", "Casa", 100.0);
        Propiedad propiedad = new Propiedad("Casa Azul", "Bogota", "r");
        propiedad.añadirUnidad(unidad);

        Huesped huesped = new Huesped("H1", "Laura", "laura@mail.com", "123");
        Reserva reserva = new Reserva(new Date(), new Date(), huesped, unidad);
        Incidente incidente = new Incidente("Problema wifi", reserva);
        db.guardarIncidente(incidente);

        List<Incidente> resultado = db.buscarIncidentesPorPropiedad(propiedad);

        assertEquals(1, resultado.size());
        assertSame(incidente, resultado.get(0));
    }

    @Test
    @DisplayName("I027 - buscarIncidentesPorPropiedad con propiedad nula devuelve lista vacia")
    void i027_buscarIncidentesPorPropiedad_propiedadNula_listaVacia() {
        List<Incidente> resultado = db.buscarIncidentesPorPropiedad(null);

        assertTrue(resultado.isEmpty());
    }
}
