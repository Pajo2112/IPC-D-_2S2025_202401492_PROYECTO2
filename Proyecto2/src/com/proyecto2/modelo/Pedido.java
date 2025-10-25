package com.proyecto2.modelo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Pedido implements Serializable {
    private static final long serialVersionUID = 1L;
    private String codigo;
    private String codigoCliente;
    private String nombreCliente;
    private Product[] productos;
    private int[] cantidades;
    private int productCount;
    private double total;
    private LocalDateTime fechaGeneracion;
    private boolean confirmado;
    private String codigoVendedor;

    public Pedido(String codigo, String codigoCliente, String nombreCliente) {
        this.codigo = codigo;
        this.codigoCliente = codigoCliente;
        this.nombreCliente = nombreCliente;
        this.productos = new Product[50];
        this.cantidades = new int[50];
        this.productCount = 0;
        this.total = 0;
        this.fechaGeneracion = LocalDateTime.now();
        this.confirmado = false;
    }

    public void agregarProducto(Product producto, int cantidad) {
        if (productCount < 50) {
            productos[productCount] = producto;
            cantidades[productCount] = cantidad;
            total += producto.getPrecio() * cantidad;
            productCount++;
        }
    }

    // Getters
    public String getCodigo() { return codigo; }
    public String getCodigoCliente() { return codigoCliente; }
    public String getNombreCliente() { return nombreCliente; }
    public double getTotal() { return total; }
    public LocalDateTime getFechaGeneracion() { return fechaGeneracion; }
    public boolean isConfirmado() { return confirmado; }
    public int getProductCount() { return productCount; }
    public Product[] getProductos() { return productos; }
    public int[] getCantidades() { return cantidades; }
    public String getCodigoVendedor() { return codigoVendedor; }

    public void setConfirmado(boolean confirmado) { this.confirmado = confirmado; }
    public void setCodigoVendedor(String codigoVendedor) { this.codigoVendedor = codigoVendedor; }

    public String getTotalFormateado() {
        return String.format("Q%.2f", total);
    }

    public String getFechaFormateada() {
        return fechaGeneracion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }
}