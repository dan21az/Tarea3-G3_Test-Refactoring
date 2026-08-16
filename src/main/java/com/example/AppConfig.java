package com.example;

import com.example.Composite.Propiedad;
import com.example.Composite.Unidad;
import com.example.FactoryMethod.CasaFactory;
import com.example.FactoryMethod.UnidadFactory;
import com.example.Singleton.BaseDatosSingleton;
import com.example.dominio.reservas.PoliticaCancelacion;
import com.example.dominio.usuarios.*;

public class AppConfig {

    public static Huesped huesped;
    public static Anfitrion anfitrion;
    public static Moderador moderadorActivo;
    public static SoporteLegal soporteLegalActivo;
    public static Propiedad propiedad;
    public static Unidad unidad;

    public static void inicializarDatosPrueba() {
        BaseDatosSingleton db = BaseDatosSingleton.getInstance();
        db.limpiar();

        // 👇 Usuarios de prueba
        huesped = new Huesped("H-001", "Laura", "laura@mail.com", "123");
        anfitrion = new Anfitrion("A-001", "Carlos", "carlos@mail.com", "123");
        moderadorActivo = new Moderador("Ana");
        soporteLegalActivo = new SoporteLegal("Abg. Pedro");

        // 👇 Propiedad y unidad de prueba
        propiedad = new Propiedad("Casa Azul", "Bogotá", "No mascotas");
        propiedad.setPoliticaCancelacion(new PoliticaCancelacion("Flexible", 2, 0.2));
        propiedad.setCheckIn("15:00");
        propiedad.setCheckOut("12:00");
        propiedad.añadirServicio("WiFi");
        propiedad.añadirServicio("Estacionamiento");
        propiedad.añadirRestriccion("No fumar");

        UnidadFactory casaFactory = new CasaFactory();
        unidad = (Unidad) casaFactory.crearUnidad("U-001", 120.0);

        propiedad.añadirUnidad(unidad);
        anfitrion.registrarPropiedad(propiedad);
    }
}
