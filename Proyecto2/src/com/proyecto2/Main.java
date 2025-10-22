package com.proyecto2;

import javax.swing.SwingUtilities;
import com.proyecto2.modelo.DataStore;
import com.proyecto2.controlador.MainController;
import com.proyecto2.utiles.*;
import com.proyecto2.vista.MainWindow;

public class Main {
    public static void main(String[] args) {
        // cargar datastore
        DataStore store = DataStore.loadFromFile("datastore.ser");
        Bitacora bitacora = new Bitacora("bitacora.txt");
        MainController controller = new MainController(store, bitacora);

        // hilo autosave cada 60 segundos
        AutoSaveThread auto = new AutoSaveThread(store, 60);
        auto.setDaemon(true);
        auto.start();

        SwingUtilities.invokeLater(() -> {
            MainWindow mw = new MainWindow(controller, bitacora);
            mw.setVisible(true);
        });

        // guardar al salir
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            store.saveToFile("datastore.ser");
            System.out.println("Guardado al salir.");
        }));
    }
}

