package com.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ConsoleUI {

    private static final BufferedReader READER = new BufferedReader(new InputStreamReader(System.in));

    public static String leerTexto(String mensaje) {
        System.out.print(mensaje);
        try {
            return READER.readLine().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public static double leerDouble(String mensaje) {
        while (true) {
            try {
                return Double.parseDouble(leerTexto(mensaje));
            } catch (NumberFormatException e) {
                System.out.println("Error: Ingrese un valor numérico decimal válido.");
            }
        }
    }

    public static int leerInt(String mensaje) {
        while (true) {
            try {
                return Integer.parseInt(leerTexto(mensaje));
            } catch (NumberFormatException e) {
                System.out.println("Error: Ingrese un número entero válido.");
            }
        }
    }

    public static void mostrarLinea() {
        System.out.println();
    }

    public static void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
    }
}
