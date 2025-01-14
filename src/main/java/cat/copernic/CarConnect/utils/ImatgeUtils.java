/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.CarConnect.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 *
 * @author TheRe
 */
public class ImatgeUtils {

    public static void convertirBytesAImagen(byte[] imagenBytes, String rutaSalida) throws IOException {
        Path ruta = Path.of(rutaSalida);
        Files.write(ruta, imagenBytes); // Escribe los bytes en un archivo
        System.out.println("Imagen guardada en: " + rutaSalida);
    }

    public static byte[] convertirImagenABytes(String rutaImagen) throws IOException {
        Path ruta = Path.of(rutaImagen);
        return Files.readAllBytes(ruta); // Lee el archivo en formato byte array
    }
}
