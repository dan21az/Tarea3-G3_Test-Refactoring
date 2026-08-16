package com.example;

import com.example.Composite.Propiedad;
import com.example.Composite.Unidad;
import com.example.Singleton.Repositorio;
import com.example.dominio.CriterioBusqueda;
import com.example.dominio.usuarios.Huesped;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class HuespedBusquedaUI {

    private final Huesped huesped;
    private final Repositorio repositorio;
    private final Scanner scanner;

    public HuespedBusquedaUI(Huesped huesped, Repositorio repositorio, Scanner scanner) {
        this.huesped = huesped;
        this.repositorio = repositorio;
        this.scanner = scanner;
    }

    public void buscarPropiedadesPorCriterios() {
        mostrarMensaje("\n--- Búsqueda de Propiedades por Criterios ---");
        String ciudad = leerTexto("Ubicación (Ciudad): ");
        double min = leerDouble("Precio mínimo por noche: ");
        double max = leerDouble("Precio máximo por noche: ");
        String tipo = leerTexto("Tipo de alojamiento (ej. Casa, Apartamento): ");
        String serviciosStr = leerTexto("Servicios requeridos (separados por coma, ej: WiFi, Estacionamiento): ");

        List<String> servicios = serviciosStr.isEmpty() ? List.of() : Arrays.asList(serviciosStr.split("\\s*,\\s*"));
        CriterioBusqueda criterio = new CriterioBusqueda(ciudad, min, max, tipo, servicios);

        List<Propiedad> resultados = huesped.buscarPropiedad(criterio, repositorio);
        desplegarPropiedadesYUnidades(resultados);
    }

    public void mostrarCatalogoCompleto() {
        mostrarMensaje("\n--- Catálogo General de Propiedades ---");
        List<Propiedad> todos = repositorio.getCatalogo();
        desplegarPropiedadesYUnidades(todos);
    }

    public void desplegarPropiedadesYUnidades(List<Propiedad> listaPropiedades) {
        mostrarMensaje("\nPropiedades encontradas: " + listaPropiedades.size());
        if (listaPropiedades.isEmpty()) {
            mostrarMensaje("No se encontraron propiedades que coincidan con la búsqueda.");
            return;
        }

        for (Propiedad p : listaPropiedades) {
            mostrarMensaje("\n- Propiedad: " + p.getNombre() + " | Ubicación: " + p.getDireccion() + " | Servicios: " + p.getServicios());
            if (p.getChildren().isEmpty()) {
                mostrarMensaje("  (Sin unidades asociadas)");
                continue;
            }

            for (var child : p.getChildren()) {
                if (child instanceof Unidad u) {
                    mostrarMensaje("  * Unidad ID: " + u.getIdUnidad() + " | Tipo: " + u.getTipo() + " | Precio base: $" + u.getPrecio() + "/noche");
                }
            }
        }
    }

    private void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
    }

    private String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine().trim();
    }

    private double leerDouble(String mensaje) {
        while (true) {
            try {
                return Double.parseDouble(leerTexto(mensaje));
            } catch (NumberFormatException e) {
                System.out.println("Error: Ingrese un valor numérico decimal válido.");
            }
        }
    }
}
