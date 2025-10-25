package com.proyecto2;

import javax.swing.SwingUtilities;
import com.proyecto2.modelo.DataStore;
import com.proyecto2.controlador.MainController;
import com.proyecto2.utiles.*;
import com.proyecto2.vista.MainWindow;

public class Main {
    private static SystemMonitor systemMonitor;
    
    public static void main(String[] args) {
        // Cargar datastore
        DataStore store = DataStore.loadFromFile("datastore.ser");
        Bitacora bitacora = new Bitacora("bitacora.txt");
        MainController controller = new MainController(store, bitacora);
        
        // Iniciar monitores del sistema
        systemMonitor = new SystemMonitor(controller);
        systemMonitor.startAllMonitors();
        
        System.out.println("🚀 Sistema Sancarlista Shop iniciado");
        System.out.println("📊 Monitores del sistema activados:");
        System.out.println("   • Sesiones activas cada 10 segundos");
        System.out.println("   • Pedidos pendientes cada 8 segundos");  
        System.out.println("   • Estadísticas en vivo cada 15 segundos");
        System.out.println("==========================================");

        // Hilo autosave cada 60 segundos
        AutoSaveThread auto = new AutoSaveThread(store, 60);
        auto.setDaemon(true);
        auto.start();

        SwingUtilities.invokeLater(() -> {
            MainWindow mw = new MainWindow(controller, bitacora);
            mw.setVisible(true);
            mw.setLocationRelativeTo(null);
        });

        // Guardar al salir
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            store.saveToFile("datastore.ser");
            if (systemMonitor != null) {
                systemMonitor.stopAllMonitors();
            }
            System.out.println("✅ Sistema cerrado correctamente. Datos guardados.");
        }));
    }
}
