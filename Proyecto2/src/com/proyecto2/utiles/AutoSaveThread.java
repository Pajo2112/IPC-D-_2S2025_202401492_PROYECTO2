package com.proyecto2.utiles;

import com.proyecto2.modelo.DataStore;

public class AutoSaveThread extends Thread {
    private DataStore store;
    private int intervalSeconds;
    private volatile boolean running = true;

    public AutoSaveThread(DataStore store, int intervalSeconds){
        this.store = store;
        this.intervalSeconds = intervalSeconds;
    }

    public void terminate(){ running = false; }

    @Override
    public void run(){
        while (running){
            try {
                Thread.sleep(intervalSeconds * 1000L);
                store.saveToFile("datastore.ser");
                System.out.println("AutoSave: datastore.ser guardado.");
            } catch(InterruptedException e){ break; }
        }
    }
}
