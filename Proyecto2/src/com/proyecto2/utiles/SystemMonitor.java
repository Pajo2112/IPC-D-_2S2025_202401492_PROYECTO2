package com.proyecto2.utiles;

import com.proyecto2.controlador.MainController;
import com.proyecto2.modelo.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SystemMonitor {
    private MainController controller;
    private volatile boolean running = true;
    private int usuariosActivos = 0;
    
    public SystemMonitor(MainController controller) {
        this.controller = controller;
    }
    
    public void startAllMonitors() {
        // Hilo 1: Monitor de Sesiones Activas
        Thread sesionesThread = new Thread(() -> {
            while (running) {
                try {
                    // Simular usuarios activos (en un sistema real esto vendría de alguna sesión)
                    int activosSimulados = (int) (Math.random() * 10) + 1;
                    usuariosActivos = activosSimulados;
                    
                    String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                    System.out.println("🔹 [SESIONES] Usuarios Activos: " + usuariosActivos + " - Última actividad: " + timestamp);
                    
                    Thread.sleep(10000); // 10 segundos
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        sesionesThread.setDaemon(true);
        sesionesThread.start();
        
        // Hilo 2: Simulador de Pedidos Pendientes
        Thread pedidosThread = new Thread(() -> {
            while (running) {
                try {
                    Pedido[] pedidosPendientes = controller.getPedidosPendientes();
                    int cantidadPendientes = pedidosPendientes != null ? pedidosPendientes.length : 0;
                    
                    String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                    System.out.println("📦 [PEDIDOS] Pedidos Pendientes: " + cantidadPendientes + " - Procesando... " + timestamp);
                    
                    Thread.sleep(8000); // 8 segundos
                } catch (InterruptedException e) {
                    break;
                } catch (Exception e) {
                    // Continuar ejecución si hay error
                }
            }
        });
        pedidosThread.setDaemon(true);
        pedidosThread.start();
        
        // Hilo 3: Generador de Estadísticas en Vivo
        Thread estadisticasThread = new Thread(() -> {
            while (running) {
                try {
                    Product[] productos = controller.listProducts();
                    Pedido[] pedidosConfirmados = controller.getPedidosConfirmados();
                    
                    int totalProductos = productos != null ? productos.length : 0;
                    int ventasHoy = pedidosConfirmados != null ? pedidosConfirmados.length : 0;
                    
                    String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                    System.out.println("📊 [ESTADÍSTICAS] Ventas del día: " + ventasHoy + " | Productos registrados: " + totalProductos + " | " + timestamp);
                    
                    Thread.sleep(15000); // 15 segundos
                } catch (InterruptedException e) {
                    break;
                } catch (Exception e) {
                    // Continuar ejecución si hay error
                }
            }
        });
        estadisticasThread.setDaemon(true);
        estadisticasThread.start();
    }
    
    public void stopAllMonitors() {
        running = false;
    }
    
    public int getUsuariosActivos() {
        return usuariosActivos;
    }
}
