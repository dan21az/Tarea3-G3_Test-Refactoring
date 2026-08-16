package com.example;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.Composite.CompPropiedad;
import com.example.Composite.Unidad;
import com.example.Decorator.TarifaExtra;
import com.example.Decorator.TarifaSeguridad;
import com.example.FactoryMethod.CasaFactory;
import com.example.FactoryMethod.DepartamentoFactory;
import com.example.FactoryMethod.HabitacionPrivadaFactory;

class FactoryTarifaTest {

    @Test
    @DisplayName("G032 - Creación de unidad tipo Casa")
    void g032_crearUnidad_tipoCasa_casoNormal() {
        CasaFactory factory = new CasaFactory();

        CompPropiedad result = factory.crearUnidad("U-1", 100.0);

        assertNotNull(result);
        assertTrue(result instanceof Unidad);
        assertEquals("Casa", ((Unidad) result).getTipo());
        assertEquals(100.0, result.costo(), 0.0);
    }

    @Test
    @DisplayName("G033 - Creación de unidad tipo Departamento")
    void g033_crearUnidad_tipoDepartamento_casoNormal() {
        DepartamentoFactory factory = new DepartamentoFactory();

        CompPropiedad result = factory.crearUnidad("U-2", 200.0);

        assertNotNull(result);
        assertTrue(result instanceof Unidad);
        assertEquals("Departamento Completo", ((Unidad) result).getTipo());
        assertEquals(200.0, result.costo(), 0.0);
    }

    @Test
    @DisplayName("G034 - Creación de unidad tipo Habitación")
    void g034_crearUnidad_tipoHabitacionPrivada_casoNormal() {
        HabitacionPrivadaFactory factory = new HabitacionPrivadaFactory();

        CompPropiedad result = factory.crearUnidad("U-3", 50.0);

        assertNotNull(result);
        assertTrue(result instanceof Unidad);
        assertEquals("Habitación Privada", ((Unidad) result).getTipo());
        assertEquals(50.0, result.costo(), 0.0);
    }

    @Test
    @DisplayName("G035 - Precio límite en cero")
    void g035_crearUnidad_precioCero_casoLimite() {
        CasaFactory factory = new CasaFactory();

        CompPropiedad result = factory.crearUnidad("U-4", 0.0);

        assertNotNull(result);
        assertTrue(result instanceof Unidad);
        assertEquals("Casa", ((Unidad) result).getTipo());
        assertEquals(0.0, result.costo(), 0.0);
    }

    @Test
    @DisplayName("G036 - Precio negativo")
    void g036_crearUnidad_precioNegativo_casoLimite() {
        CasaFactory factory = new CasaFactory();

        CompPropiedad result = factory.crearUnidad("U-5", -50.0);

        assertNotNull(result);
        assertTrue(result instanceof Unidad);
        assertEquals(-50.0, result.costo(), 0.0);
    }

    @Test
    @DisplayName("G046 - Aplicación de tarifa extra")
    void g046_tarifaExtra_costo_casoNormal() {
        Unidad unidadBase = new Unidad("U-1", "Casa", 100.0);

        CompPropiedad conExtra = new TarifaExtra(unidadBase);

        assertEquals(115.0, conExtra.costo(), 0.0);
    }

    @Test
    @DisplayName("G047 - Aplicación de depósito de seguridad")
    void g047_tarifaSeguridad_costo_casoNormal() {
        Unidad unidadBase = new Unidad("U-1", "Casa", 100.0);

        CompPropiedad conSeguridad = new TarifaSeguridad(unidadBase);

        assertEquals(125.0, conSeguridad.costo(), 0.0);
    }

    @Test
    @DisplayName("G048 - Anidación de decoradores")
    void g048_tarifaExtraYseguridad_anidados_casoNormal() {
        Unidad unidadBase = new Unidad("U-1", "Casa", 100.0);

        CompPropiedad conExtra = new TarifaExtra(unidadBase);
        CompPropiedad conSeguridad = new TarifaSeguridad(conExtra);

        assertEquals(140.0, conSeguridad.costo(), 0.0);
    }

    @Test
    @DisplayName("G049 - Tarifa extra sobre precio cero")
    void g049_tarifaExtra_costoCero_casoLimite() {
        Unidad unidadBase = new Unidad("U-1", "Casa", 0.0);

        CompPropiedad conExtra = new TarifaExtra(unidadBase);

        assertEquals(15.0, conExtra.costo(), 0.0);
    }
}
