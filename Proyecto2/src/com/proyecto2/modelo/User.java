package com.proyecto2.modelo;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private String codigo;
    private String nombre;
    private String password;
    private String rol; // "ADMIN", "VENDEDOR", "CLIENTE"

    public User(String codigo, String nombre, String password, String rol) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.password = password;
        this.rol = rol;
    }

    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public String getPassword() { return password; }
    public String getRol() { return rol; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setPassword(String password) { this.password = password; }
    public void setRol(String rol) { this.rol = rol; }

    @Override
    public String toString() {
        return codigo + " - " + nombre + " (" + rol + ")";
    }
}

