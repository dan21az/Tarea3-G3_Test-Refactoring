package com.example.dominio.usuarios;
public abstract class Usuario {

    protected String id;
    protected String nombre;
    protected String correo;
    protected String password;

    public Usuario(String id, String nombre, String correo, String password) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.password = password;
    }

    public boolean iniciarSesion(String correo, String password) {
        System.out.println(nombre + " ha iniciado sesión.");
        return true;
    }

    public void cerrarSesion() {
        System.out.println(nombre + " cerró sesión.");
    }

    public String getNombre() {
        return nombre;
    }
}
