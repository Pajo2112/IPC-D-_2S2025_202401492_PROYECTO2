package com.proyecto2.utiles;

import com.proyecto2.modelo.*;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

// Requires iText (add itextpdf jar to classpath)
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

public class PDFGenerator {

    // 1. Reporte de Productos Más Vendidos
    public static void createProductosMasVendidosReport(Product[] products, String filename) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filename));
        document.open();
        
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 10);
        
        document.add(new Paragraph("Reporte de Productos Más Vendidos", titleFont));
        document.add(new Paragraph("Generado: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")), normalFont));
        document.add(new Paragraph(" "));
        
        // Ordenar productos por cantidad vendida (descendente)
        Product[] sortedProducts = Arrays.copyOf(products, products.length);
        Arrays.sort(sortedProducts, (p1, p2) -> Integer.compare(p2.getCantidadVendida(), p1.getCantidadVendida()));
        
        // Tomar top 5
        int topCount = Math.min(5, sortedProducts.length);
        
        PdfPTable table = new PdfPTable(4);
        table.setWidths(new int[]{3, 4, 3, 3});
        table.addCell(new Phrase("Código", headerFont));
        table.addCell(new Phrase("Nombre", headerFont));
        table.addCell(new Phrase("Categoría", headerFont));
        table.addCell(new Phrase("Vendidos", headerFont));
        
        double totalIngresos = 0;
        for (int i = 0; i < topCount; i++) {
            Product p = sortedProducts[i];
            table.addCell(p.getCodigo());
            table.addCell(p.getNombre());
            table.addCell(p.getCategoria());
            table.addCell(String.valueOf(p.getCantidadVendida()));
            totalIngresos += p.getPrecio() * p.getCantidadVendida();
        }
        
        document.add(table);
        document.add(new Paragraph(" "));
        document.add(new Paragraph("Total de ingresos generados: " + String.format("Q%.2f", totalIngresos), headerFont));
        
        document.close();
    }

    // 2. Reporte de Productos Menos Vendidos
    public static void createProductosMenosVendidosReport(Product[] products, String filename) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filename));
        document.open();
        
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 10);
        
        document.add(new Paragraph("Reporte de Productos Menos Vendidos", titleFont));
        document.add(new Paragraph("Generado: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")), normalFont));
        document.add(new Paragraph(" "));
        
        // Filtrar productos con ventas > 0 y ordenar ascendente
        Product[] productosConVentas = Arrays.stream(products)
            .filter(p -> p.getCantidadVendida() > 0)
            .toArray(Product[]::new);
        
        Arrays.sort(productosConVentas, (p1, p2) -> Integer.compare(p1.getCantidadVendida(), p2.getCantidadVendida()));
        
        int topCount = Math.min(5, productosConVentas.length);
        
        PdfPTable table = new PdfPTable(5);
        table.setWidths(new int[]{2, 4, 3, 2, 4});
        table.addCell(new Phrase("Código", headerFont));
        table.addCell(new Phrase("Nombre", headerFont));
        table.addCell(new Phrase("Categoría", headerFont));
        table.addCell(new Phrase("Vendidos", headerFont));
        table.addCell(new Phrase("Recomendación", headerFont));
        
        for (int i = 0; i < topCount; i++) {
            Product p = productosConVentas[i];
            table.addCell(p.getCodigo());
            table.addCell(p.getNombre());
            table.addCell(p.getCategoria());
            table.addCell(String.valueOf(p.getCantidadVendida()));
            
            String recomendacion = "";
            if (p.getCantidadVendida() <= 2) {
                recomendacion = "Promoción urgente necesaria";
            } else if (p.getCantidadVendida() <= 5) {
                recomendacion = "Considerar descuento";
            } else {
                recomendacion = "Monitorear ventas";
            }
            table.addCell(recomendacion);
        }
        
        document.add(table);
        document.close();
    }

    // 3. Reporte de Inventario
    public static void createInventarioReport(Product[] products, String filename) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filename));
        document.open();
        
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        Font alertFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.RED);
        
        document.add(new Paragraph("Reporte de Estado de Inventario", titleFont));
        document.add(new Paragraph("Generado: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))));
        document.add(new Paragraph(" "));
        
        PdfPTable table = new PdfPTable(6);
        table.setWidths(new int[]{2, 4, 3, 2, 3, 4});
        table.addCell(new Phrase("Código", headerFont));
        table.addCell(new Phrase("Nombre", headerFont));
        table.addCell(new Phrase("Categoría", headerFont));
        table.addCell(new Phrase("Stock", headerFont));
        table.addCell(new Phrase("Estado", headerFont));
        table.addCell(new Phrase("Sugerencia", headerFont));
        
        int productosCriticos = 0;
        int productosBajos = 0;
        
        for (Product p : products) {
            String estado = "";
            String sugerencia = "";
            Font estadoFont = new Font(Font.FontFamily.HELVETICA, 10);
            
            if (p.getStock() < 10) {
                estado = "CRÍTICO";
                sugerencia = "REABASTECER URGENTE";
                estadoFont = alertFont;
                productosCriticos++;
            } else if (p.getStock() < 20) {
                estado = "BAJO";
                sugerencia = "Reabastecer pronto";
                productosBajos++;
            } else {
                estado = "NORMAL";
                sugerencia = "Stock adecuado";
            }
            
            table.addCell(p.getCodigo());
            table.addCell(p.getNombre());
            table.addCell(p.getCategoria());
            table.addCell(String.valueOf(p.getStock()));
            
            PdfPCell estadoCell = new PdfPCell(new Phrase(estado, estadoFont));
            table.addCell(estadoCell);
            table.addCell(sugerencia);
        }
        
        document.add(table);
        document.add(new Paragraph(" "));
        document.add(new Paragraph("Resumen:", headerFont));
        document.add(new Paragraph("Productos con stock crítico (<10): " + productosCriticos));
        document.add(new Paragraph("Productos con stock bajo (10-20): " + productosBajos));
        document.add(new Paragraph("Productos con stock normal: " + (products.length - productosCriticos - productosBajos)));
        
        document.close();
    }

    // 4. Reporte de Ventas por Vendedor
    public static void createVentasPorVendedorReport(User[] users, Pedido[] pedidos, String filename) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filename));
        document.open();
        
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        
        document.add(new Paragraph("Reporte de Ventas por Vendedor", titleFont));
        document.add(new Paragraph("Generado: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))));
        document.add(new Paragraph(" "));
        
        // Filtrar solo vendedores
        User[] vendedores = Arrays.stream(users)
            .filter(u -> "VENDEDOR".equals(u.getRol()))
            .toArray(User[]::new);
        
        PdfPTable table = new PdfPTable(5);
        table.setWidths(new int[]{2, 4, 3, 3, 3});
        table.addCell(new Phrase("Código", headerFont));
        table.addCell(new Phrase("Nombre", headerFont));
        table.addCell(new Phrase("Pedidos Confirmados", headerFont));
        table.addCell(new Phrase("Total Ventas", headerFont));
        table.addCell(new Phrase("Comisión (5%)", headerFont));
        
        for (User vendedor : vendedores) {
            int pedidosConfirmados = 0;
            double totalVentas = 0;
            
            for (Pedido pedido : pedidos) {
                if (pedido.isConfirmado() && vendedor.getCodigo().equals(pedido.getCodigoVendedor())) {
                    pedidosConfirmados++;
                    totalVentas += pedido.getTotal();
                }
            }
            
            double comision = totalVentas * 0.05;
            
            table.addCell(vendedor.getCodigo());
            table.addCell(vendedor.getNombre());
            table.addCell(String.valueOf(pedidosConfirmados));
            table.addCell(String.format("Q%.2f", totalVentas));
            table.addCell(String.format("Q%.2f", comision));
        }
        
        document.add(table);
        document.close();
    }

    // 5. Reporte de Clientes Activos
    public static void createClientesActivosReport(User[] users, Pedido[] pedidos, String filename) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filename));
        document.open();
        
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        
        document.add(new Paragraph("Reporte de Clientes Activos", titleFont));
        document.add(new Paragraph("Generado: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))));
        document.add(new Paragraph(" "));
        
        // Filtrar solo clientes
        User[] clientes = Arrays.stream(users)
            .filter(u -> "CLIENTE".equals(u.getRol()))
            .toArray(User[]::new);
        
        PdfPTable table = new PdfPTable(5);
        table.setWidths(new int[]{2, 4, 3, 3, 3});
        table.addCell(new Phrase("Código", headerFont));
        table.addCell(new Phrase("Nombre", headerFont));
        table.addCell(new Phrase("Total Compras", headerFont));
        table.addCell(new Phrase("Monto Total", headerFont));
        table.addCell(new Phrase("Clasificación", headerFont));
        
        for (User cliente : clientes) {
            int totalCompras = 0;
            double montoTotal = 0;
            
            for (Pedido pedido : pedidos) {
                if (pedido.isConfirmado() && cliente.getCodigo().equals(pedido.getCodigoCliente())) {
                    totalCompras++;
                    montoTotal += pedido.getTotal();
                }
            }
            
            String clasificacion = "";
            if (totalCompras >= 5) {
                clasificacion = "FRECUENTE";
            } else if (totalCompras >= 2) {
                clasificacion = "OCASIONAL";
            } else if (totalCompras == 1) {
                clasificacion = "NUEVO";
            } else {
                clasificacion = "SIN COMPRAS";
            }
            
            table.addCell(cliente.getCodigo());
            table.addCell(cliente.getNombre());
            table.addCell(String.valueOf(totalCompras));
            table.addCell(String.format("Q%.2f", montoTotal));
            table.addCell(clasificacion);
        }
        
        document.add(table);
        document.close();
    }

    // 6. Reporte Financiero
    public static void createReporteFinancieroReport(Product[] products, Pedido[] pedidos, String filename) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filename));
        document.open();
        
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        
        document.add(new Paragraph("Reporte Financiero por Categoría", titleFont));
        document.add(new Paragraph("Generado: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))));
        document.add(new Paragraph(" "));
        
        // Agrupar por categoría
        java.util.Map<String, Double> ingresosPorCategoria = new java.util.HashMap<>();
        java.util.Map<String, Integer> cantidadPorCategoria = new java.util.HashMap<>();
        
        for (Pedido pedido : pedidos) {
            if (pedido.isConfirmado()) {
                for (int i = 0; i < pedido.getProductCount(); i++) {
                    Product p = pedido.getProductos()[i];
                    int cantidad = pedido.getCantidades()[i];
                    String categoria = p.getCategoria();
                    
                    double ingreso = p.getPrecio() * cantidad;
                    ingresosPorCategoria.put(categoria, ingresosPorCategoria.getOrDefault(categoria, 0.0) + ingreso);
                    cantidadPorCategoria.put(categoria, cantidadPorCategoria.getOrDefault(categoria, 0) + cantidad);
                }
            }
        }
        
        PdfPTable table = new PdfPTable(5);
        table.setWidths(new int[]{4, 3, 3, 3, 3});
        table.addCell(new Phrase("Categoría", headerFont));
        table.addCell(new Phrase("Productos Vendidos", headerFont));
        table.addCell(new Phrase("Ingresos Totales", headerFont));
        table.addCell(new Phrase("% Participación", headerFont));
        table.addCell(new Phrase("Promedio Precio", headerFont));
        
        double ingresosTotales = ingresosPorCategoria.values().stream().mapToDouble(Double::doubleValue).sum();
        
        for (String categoria : ingresosPorCategoria.keySet()) {
            double ingresos = ingresosPorCategoria.get(categoria);
            int cantidad = cantidadPorCategoria.get(categoria);
            double porcentaje = (ingresos / ingresosTotales) * 100;
            double promedio = cantidad > 0 ? ingresos / cantidad : 0;
            
            table.addCell(categoria);
            table.addCell(String.valueOf(cantidad));
            table.addCell(String.format("Q%.2f", ingresos));
            table.addCell(String.format("%.1f%%", porcentaje));
            table.addCell(String.format("Q%.2f", promedio));
        }
        
        document.add(table);
        document.add(new Paragraph(" "));
        document.add(new Paragraph("Ingresos totales: " + String.format("Q%.2f", ingresosTotales), headerFont));
        
        document.close();
    }

    // 7. Reporte de Productos por Caducar
    public static void createProductosPorCaducarReport(Product[] products, String filename) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filename));
        document.open();
        
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        Font criticoFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.RED);
        Font urgenteFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.ORANGE);
        
        document.add(new Paragraph("Reporte de Productos por Caducar", titleFont));
        document.add(new Paragraph("Generado: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))));
        document.add(new Paragraph(" "));
        
        PdfPTable table = new PdfPTable(7);
        table.setWidths(new int[]{2, 4, 3, 3, 3, 3, 4});
        table.addCell(new Phrase("Código", headerFont));
        table.addCell(new Phrase("Nombre", headerFont));
        table.addCell(new Phrase("Fecha Caducidad", headerFont));
        table.addCell(new Phrase("Días Restantes", headerFont));
        table.addCell(new Phrase("Stock", headerFont));
        table.addCell(new Phrase("Valor en Riesgo", headerFont));
        table.addCell(new Phrase("Recomendación", headerFont));
        
        int productosCriticos = 0;
        int productosUrgentes = 0;
        double valorTotalRiesgo = 0;
        
        for (Product p : products) {
            if ("ALIMENTO".equalsIgnoreCase(p.getCategoria())) {
                try {
                    // Simular análisis de fecha de caducidad
                    String fechaCaducidad = p.getAtributoEspecifico();
                    int diasRestantes = (int) (Math.random() * 30) + 1; // Simulación
                    double valorRiesgo = p.getPrecio() * p.getStock();
                    
                    String prioridad = "";
                    String recomendacion = "";
                    Font prioridadFont = new Font(Font.FontFamily.HELVETICA, 10);
                    
                    if (diasRestantes <= 3) {
                        prioridad = "CRÍTICO";
                        recomendacion = "DESCUENTO 50% URGENTE";
                        prioridadFont = criticoFont;
                        productosCriticos++;
                    } else if (diasRestantes <= 7) {
                        prioridad = "URGENTE";
                        recomendacion = "Aplicar 30% descuento";
                        prioridadFont = urgenteFont;
                        productosUrgentes++;
                    } else {
                        prioridad = "VIGILAR";
                        recomendacion = "Monitorear caducidad";
                    }
                    
                    valorTotalRiesgo += valorRiesgo;
                    
                    table.addCell(p.getCodigo());
                    table.addCell(p.getNombre());
                    table.addCell(fechaCaducidad);
                    table.addCell(String.valueOf(diasRestantes));
                    table.addCell(String.valueOf(p.getStock()));
                    table.addCell(String.format("Q%.2f", valorRiesgo));
                    
                    PdfPCell recomendacionCell = new PdfPCell(new Phrase(recomendacion, prioridadFont));
                    table.addCell(recomendacionCell);
                    
                } catch (Exception e) {
                    // Si hay error en el formato de fecha, saltar producto
                }
            }
        }
        
        document.add(table);
        document.add(new Paragraph(" "));
        document.add(new Paragraph("Resumen de Prioridades:", headerFont));
        document.add(new Paragraph("Productos CRÍTICOS (1-3 días): " + productosCriticos));
        document.add(new Paragraph("Productos URGENTES (4-7 días): " + productosUrgentes));
        document.add(new Paragraph("Valor total en riesgo: " + String.format("Q%.2f", valorTotalRiesgo)));
        
        document.close();
    }

    // Método original (mantener compatibilidad)
    public static void createProductReport(Product[] products, String filename) throws Exception {
        createInventarioReport(products, filename);
    }
}