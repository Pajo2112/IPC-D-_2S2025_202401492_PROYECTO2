package com.proyecto2.modelo;

import java.io.Serializable;

public class Product implements Serializable {
    private static final long serialVersionUID = 1L;
    private String codigo;
    private String nombre;
    private String categoria;
    private String atributo;
    private int stock;

    public Product(String codigo, String nombre, String categoria, String atributo, int stock) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.categoria = categoria;
        this.atributo = atributo;
        this.stock = stock;
    }

    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public String getCategoria() { return categoria; }
    public String getAtributo() { return atributo; }
    public int getStock() { return stock; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public void setAtributo(String atributo) { this.atributo = atributo; }
    public void setStock(int stock) { this.stock = stock; }

    @Override
    public String toString() {
        return codigo + " | " + nombre + " | " + categoria + " | stock: " + stock;
    }
}
