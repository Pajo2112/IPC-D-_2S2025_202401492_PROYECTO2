package com.proyecto2.utiles;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Bitacora {
    private File file;

    public Bitacora(String filename){
        file = new File(filename);
    }

    public synchronized void append(String accion, String detalle){
        try (FileWriter fw = new FileWriter(file, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            bw.write(ts + " | " + accion + " | " + detalle);
            bw.newLine();
        } catch(Exception e){ e.printStackTrace(); }
    }
}

