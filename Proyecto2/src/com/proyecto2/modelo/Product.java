package com.proyecto2.modelo;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Product implements Serializable {
    private static final long serialVersionUID = 1L;
    private String codigo;
    private String nombre;
    private String categoria;
    private double precio;
    private int stock;
    private String atributoEspecifico;
    private int cantidadVendida;

    public Product(String codigo, String nombre, String categoria, double precio, int stock, String atributoEspecifico) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.categoria = categoria;
        this.precio = precio;
        this.stock = stock;
        this.atributoEspecifico = atributoEspecifico;
        this.cantidadVendida = 0;
    }

    // Getters y Setters
    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public String getCategoria() { return categoria; }
    public double getPrecio() { return precio; }
    public int getStock() { return stock; }
    public String getAtributoEspecifico() { return atributoEspecifico; }
    public int getCantidadVendida() { return cantidadVendida; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public void setPrecio(double precio) { this.precio = precio; }
    public void setStock(int stock) { this.stock = stock; }
    public void setAtributoEspecifico(String atributoEspecifico) { this.atributoEspecifico = atributoEspecifico; }
    public void setCantidadVendida(int cantidadVendida) { this.cantidadVendida = cantidadVendida; }

    public void vender(int cantidad) {
        this.stock -= cantidad;
        this.cantidadVendida += cantidad;
    }

    public String getPrecioFormateado() {
        return String.format("Q%.2f", precio);
    }

    public String getDetalleCategoria() {
        switch(categoria.toUpperCase()) {
            case "TECNOLOGIA":
                return "Este producto tiene " + atributoEspecifico + " meses de garantía";
            case "ALIMENTO":
                return "Este producto vence el: " + atributoEspecifico;
            case "GENERAL":
                return "Este producto está hecho de: " + atributoEspecifico;
            default:
                return "Atributo: " + atributoEspecifico;
        }
    }

    @Override
    public String toString() {
        return codigo + " | " + nombre + " | " + categoria + " | " + getPrecioFormateado() + " | stock: " + stock;
    }
}