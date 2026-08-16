package com.example;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.Composite.Propiedad;
import com.example.Composite.Unidad;
import com.example.Singleton.Repositorio;
import com.example.Singleton.RepositorioMemoria;
import com.example.dominio.CriterioBusquedaEvaluator;
import com.example.dominio.CriterioBusqueda;
import com.example.dominio.usuarios.Huesped;

class BusquedaPropiedadTest {

    private Repositorio db;
    private Huesped huesped;

    @BeforeEach
    void setUp() {
        db = new RepositorioMemoria();
        db.limpiar();
        huesped = new Huesped("H1", "Laura", "laura@mail.com", "123");
    }

    @Test
    @DisplayName("B001 - Busqueda por todos los criterios devuelve propiedad coincidente")
    void b001_buscarPropiedad_todosCriterios() {
        Propiedad propiedad = new Propiedad("Casa Azul", "Bogotá", "No mascotas");
        propiedad.añadirServicio("WiFi");
        propiedad.añadirServicio("Estacionamiento");
        Unidad unidad = new Unidad("U-100", "Casa", 90.0);
        propiedad.añadirUnidad(unidad);
        db.guardarPropiedad(propiedad);

        Propiedad otra = new Propiedad("Depto Rojo", "Medellín", "No fumar");
        otra.añadirServicio("Gym");
        Unidad unidad2 = new Unidad("U-200", "Apartamento", 80.0);
        otra.añadirUnidad(unidad2);
        db.guardarPropiedad(otra);

        CriterioBusqueda criterio = new CriterioBusqueda("Bogotá", 50.0, 150.0, "Casa", Arrays.asList("WiFi"));

        List<Propiedad> resultados = huesped.buscarPropiedad(criterio, db);

        assertEquals(1, resultados.size());
        assertEquals("Casa Azul", resultados.get(0).getNombre());
    }

    @Test
    @DisplayName("B002 - Busqueda insensible a mayúsculas en ubicación")
    void b002_buscarPropiedad_minusculas() {
        Propiedad propiedad = new Propiedad("Casa Azul", "Bogotá", "No mascotas");
        propiedad.añadirServicio("WiFi");
        Unidad unidad = new Unidad("U-100", "Casa", 90.0);
        propiedad.añadirUnidad(unidad);
        db.guardarPropiedad(propiedad);

        CriterioBusqueda criterio = new CriterioBusqueda("bogotá", 50.0, 150.0, "Casa", Arrays.asList("WiFi"));

        List<Propiedad> resultados = huesped.buscarPropiedad(criterio, db);

        assertEquals(1, resultados.size());
        assertEquals("Casa Azul", resultados.get(0).getNombre());
    }

    @Test
    @DisplayName("B003 - Busqueda sin resultados para ubicación inexistente")
    void b003_buscarPropiedad_sinResultados() {
        Propiedad propiedad = new Propiedad("Casa Azul", "Bogotá", "No mascotas");
        Unidad unidad = new Unidad("U-100", "Casa", 90.0);
        propiedad.añadirUnidad(unidad);
        db.guardarPropiedad(propiedad);

        CriterioBusqueda criterio = new CriterioBusqueda("Cali", 0.0, 0.0, null, null);

        List<Propiedad> resultados = huesped.buscarPropiedad(criterio, db);

        assertEquals(0, resultados.size());
    }

    @Test
    @DisplayName("B004 - Coincidencia parcial de ubicación por subcadena")
    void b004_buscarPropiedad_subcadenaUbicacion() {
        Propiedad propiedad = new Propiedad("Casa Azul", "Bogotá", "No mascotas");
        Unidad unidad = new Unidad("U-100", "Casa", 90.0);
        propiedad.añadirUnidad(unidad);
        db.guardarPropiedad(propiedad);

        CriterioBusqueda criterio = new CriterioBusqueda("Bogo", 0.0, 0.0, null, null);

        List<Propiedad> resultados = huesped.buscarPropiedad(criterio, db);

        assertEquals(1, resultados.size());
        assertEquals("Casa Azul", resultados.get(0).getNombre());
    }

    @Test
    @DisplayName("B005 - Sin filtro de precio devuelve todas las propiedades")
    void b005_buscarPropiedad_sinFiltroPrecio() {
        Propiedad prop1 = new Propiedad("Casa Azul", "Bogotá", "No mascotas");
        Unidad unidad1 = new Unidad("U-100", "Casa", 90.0);
        prop1.añadirUnidad(unidad1);
        db.guardarPropiedad(prop1);

        Propiedad prop2 = new Propiedad("Depto Rojo", "Medellín", "No fumar");
        Unidad unidad2 = new Unidad("U-200", "Apartamento", 80.0);
        prop2.añadirUnidad(unidad2);
        db.guardarPropiedad(prop2);

        CriterioBusqueda criterio = new CriterioBusqueda(null, 0.0, 0.0, null, null);

        List<Propiedad> resultados = huesped.buscarPropiedad(criterio, db);

        assertEquals(2, resultados.size());
    }

    @Test
    @DisplayName("B006 - Sin filtro de servicios devuelve todas las propiedades")
    void b006_buscarPropiedad_sinFiltroServicios() {
        Propiedad prop1 = new Propiedad("Casa Azul", "Bogotá", "No mascotas");
        Unidad unidad1 = new Unidad("U-100", "Casa", 90.0);
        prop1.añadirUnidad(unidad1);
        db.guardarPropiedad(prop1);

        Propiedad prop2 = new Propiedad("Depto Rojo", "Medellín", "No fumar");
        Unidad unidad2 = new Unidad("U-200", "Apartamento", 80.0);
        prop2.añadirUnidad(unidad2);
        db.guardarPropiedad(prop2);

        CriterioBusqueda criterio = new CriterioBusqueda(null, 0.0, 0.0, null, Collections.emptyList());

        List<Propiedad> resultados = huesped.buscarPropiedad(criterio, db);

        assertEquals(2, resultados.size());
    }

    @Test
    @DisplayName("B007 - Criterio nulo devuelve todas las propiedades")
    void b007_buscarPropiedad_criterioNulo() {
        Propiedad prop1 = new Propiedad("Casa Azul", "Bogotá", "No mascotas");
        Unidad unidad1 = new Unidad("U-100", "Casa", 90.0);
        prop1.añadirUnidad(unidad1);
        db.guardarPropiedad(prop1);

        Propiedad prop2 = new Propiedad("Depto Rojo", "Medellín", "No fumar");
        Unidad unidad2 = new Unidad("U-200", "Apartamento", 80.0);
        prop2.añadirUnidad(unidad2);
        db.guardarPropiedad(prop2);

        List<Propiedad> resultados = huesped.buscarPropiedad(null, db);

        assertEquals(2, resultados.size());
    }

    @Test
    @DisplayName("B008 - Rango de precio inválido no devuelve resultados")
    void b008_buscarPropiedad_rangoPrecioInvalido() {
        Propiedad propiedad = new Propiedad("Casa Azul", "Bogotá", "No mascotas");
        Unidad unidad = new Unidad("U-100", "Casa", 90.0);
        propiedad.añadirUnidad(unidad);
        db.guardarPropiedad(propiedad);

        CriterioBusqueda criterio = new CriterioBusqueda(null, 50.0, 40.0, null, null);

        List<Propiedad> resultados = huesped.buscarPropiedad(criterio, db);

        assertEquals(0, resultados.size());
    }

    @Test
    @DisplayName("B009 - Buscar unidad por ID existente")
    void b009_obtenerUnidadPorId_existente() {
        Propiedad propiedad = new Propiedad("Casa Azul", "Bogotá", "No mascotas");
        Unidad unidad = new Unidad("U-100", "Casa", 90.0);
        propiedad.añadirUnidad(unidad);

        Unidad resultado = propiedad.obtenerUnidadPorId("U-100");

        assertNotNull(resultado);
        assertEquals("U-100", resultado.getIdUnidad());
    }

    @Test
    @DisplayName("B010 - Buscar unidad por ID inexistente devuelve null")
    void b010_obtenerUnidadPorId_inexistente() {
        Propiedad propiedad = new Propiedad("Casa Azul", "Bogotá", "No mascotas");
        Unidad unidad = new Unidad("U-100", "Casa", 90.0);
        propiedad.añadirUnidad(unidad);

        Unidad resultado = propiedad.obtenerUnidadPorId("U-999");

        assertNull(resultado);
    }

    @Test
    @DisplayName("B011 - Buscar unidad por ID nulo devuelve null")
    void b011_obtenerUnidadPorId_nulo() {
        Propiedad propiedad = new Propiedad("Casa Azul", "Bogotá", "No mascotas");
        Unidad unidad = new Unidad("U-100", "Casa", 90.0);
        propiedad.añadirUnidad(unidad);

        Unidad resultado = propiedad.obtenerUnidadPorId(null);

        assertNull(resultado);
    }

    @Test
    @DisplayName("B012 - Buscar unidad por ID en blanco devuelve null")
    void b012_obtenerUnidadPorId_enBlanco() {
        Propiedad propiedad = new Propiedad("Casa Azul", "Bogotá", "No mascotas");
        Unidad unidad = new Unidad("U-100", "Casa", 90.0);
        propiedad.añadirUnidad(unidad);

        Unidad resultado = propiedad.obtenerUnidadPorId("  ");

        assertNull(resultado);
    }

    @Test
    @DisplayName("B013 - Buscar unidad por ID sin distinción de mayúsculas")
    void b013_obtenerUnidadPorId_caseInsensitive() {
        Propiedad propiedad = new Propiedad("Casa Azul", "Bogotá", "No mascotas");
        Unidad unidad = new Unidad("U-100", "Casa", 90.0);
        propiedad.añadirUnidad(unidad);

        Unidad resultado = propiedad.obtenerUnidadPorId("u-100");

        assertNotNull(resultado);
        assertEquals("U-100", resultado.getIdUnidad());
    }

    @Test
    @DisplayName("B014 - Buscar unidad por ID con espacios en blanco")
    void b014_obtenerUnidadPorId_conEspacios() {
        Propiedad propiedad = new Propiedad("Casa Azul", "Bogotá", "No mascotas");
        Unidad unidad = new Unidad("U-100", "Casa", 90.0);
        propiedad.añadirUnidad(unidad);

        Unidad resultado = propiedad.obtenerUnidadPorId(" U-100 ");

        assertNotNull(resultado);
        assertEquals("U-100", resultado.getIdUnidad());
    }

    @Test
    @DisplayName("B015 - Verificar existencia de unidad con ID existente")
    void b015_tieneUnidadConId_existente() {
        Propiedad propiedad = new Propiedad("Casa Azul", "Bogotá", "No mascotas");
        Unidad unidad = new Unidad("U-100", "Casa", 90.0);
        propiedad.añadirUnidad(unidad);

        assertTrue(propiedad.tieneUnidadConId("U-100"));
    }

    @Test
    @DisplayName("B016 - Verificar existencia de unidad con ID inexistente")
    void b016_tieneUnidadConId_inexistente() {
        Propiedad propiedad = new Propiedad("Casa Azul", "Bogotá", "No mascotas");
        Unidad unidad = new Unidad("U-100", "Casa", 90.0);
        propiedad.añadirUnidad(unidad);

        assertFalse(propiedad.tieneUnidadConId("U-999"));
    }

    @Test
    @DisplayName("B017 - Coincidencia por tipo de alojamiento")
    void b017_coincidir_tipoCasa() {
        Propiedad propiedad = new Propiedad("Casa Azul", "Bogotá", "No mascotas");
        Unidad unidad = new Unidad("U-100", "Casa", 90.0);
        propiedad.añadirUnidad(unidad);

        CriterioBusqueda criterio = new CriterioBusqueda(null, 0.0, 0.0, "Casa", null);

        assertTrue(new CriterioBusquedaEvaluator().coincide(propiedad, criterio));
    }

    @Test
    @DisplayName("B018 - No coincide por tipo de alojamiento distinto")
    void b018_coincidir_tipoNoCoincide() {
        Propiedad propiedad = new Propiedad("Depto Rojo", "Medellín", "No fumar");
        Unidad unidad = new Unidad("U-200", "Apartamento", 80.0);
        propiedad.añadirUnidad(unidad);

        CriterioBusqueda criterio = new CriterioBusqueda(null, 0.0, 0.0, "Casa", null);

        assertFalse(new CriterioBusquedaEvaluator().coincide(propiedad, criterio));
    }

    @Test
    @DisplayName("B019 - Coincidencia por servicios ofrecidos")
    void b019_coincidir_servicioPiscina() {
        Propiedad propiedad = new Propiedad("Casa Azul", "Bogotá", "No mascotas");
        propiedad.añadirServicio("Piscina");
        propiedad.añadirServicio("WiFi");
        Unidad unidad = new Unidad("U-100", "Casa", 90.0);
        propiedad.añadirUnidad(unidad);

        CriterioBusqueda criterio = new CriterioBusqueda(null, 0.0, 0.0, null, Arrays.asList("Piscina"));

        assertTrue(new CriterioBusquedaEvaluator().coincide(propiedad, criterio));
    }

    @Test
    @DisplayName("B020 - No coincide por servicios no ofrecidos")
    void b020_coincidir_servicioNoCoincide() {
        Propiedad propiedad = new Propiedad("Casa Azul", "Bogotá", "No mascotas");
        propiedad.añadirServicio("WiFi");
        propiedad.añadirServicio("Estacionamiento");
        Unidad unidad = new Unidad("U-100", "Casa", 90.0);
        propiedad.añadirUnidad(unidad);

        CriterioBusqueda criterio = new CriterioBusqueda(null, 0.0, 0.0, null, Arrays.asList("Gimnasio"));

        assertFalse(new CriterioBusquedaEvaluator().coincide(propiedad, criterio));
    }

    @Test
    @DisplayName("B021 - Precio fuera de rango no coincide")
    void b021_coincidir_precioFueraDeRango() {
        Propiedad propiedad = new Propiedad("Casa Azul", "Bogotá", "No mascotas");
        Unidad unidad = new Unidad("U-100", "Casa", 90.0);
        propiedad.añadirUnidad(unidad);

        CriterioBusqueda criterio = new CriterioBusqueda(null, 200.0, 0.0, null, null);

        assertFalse(new CriterioBusquedaEvaluator().coincide(propiedad, criterio));
    }

    @Test
    @DisplayName("B022 - Propiedad con al menos una unidad es válida")
    void b022_esValida_conUnidades() {
        Propiedad propiedad = new Propiedad("Casa Azul", "Bogotá", "No mascotas");
        Unidad unidad = new Unidad("U-100", "Casa", 90.0);
        propiedad.añadirUnidad(unidad);

        assertTrue(propiedad.esValida());
    }

    @Test
    @DisplayName("B023 - Propiedad sin unidades es inválida")
    void b023_esValida_sinUnidades() {
        Propiedad propiedad = new Propiedad("Casa Azul", "Bogotá", "No mascotas");

        assertFalse(propiedad.esValida());
    }
}
