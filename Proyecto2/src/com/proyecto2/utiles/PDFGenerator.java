package com.proyecto2.utiles;

import com.proyecto2.modelo.Product;
import java.io.FileOutputStream;

// Requires iText (add itextpdf jar to classpath)
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

public class PDFGenerator {

    public static void createProductReport(Product[] products, String filename) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filename));
        document.open();
        Font title = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        document.add(new Paragraph("Reporte de Productos", title));
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(5);
        table.setWidths(new int[]{2,4,3,3,2});
        table.addCell("Código");
        table.addCell("Nombre");
        table.addCell("Categoría");
        table.addCell("Atributo");
        table.addCell("Stock");

        for (Product p : products) {
            table.addCell(p.getCodigo());
            table.addCell(p.getNombre());
            table.addCell(p.getCategoria());
            table.addCell(p.getAtributo());
            table.addCell(String.valueOf(p.getStock()));
        }

        document.add(table);
        document.close();
    }
}
