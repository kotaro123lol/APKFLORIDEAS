package com.practica.florideasapp;

import org.junit.Test;

import static org.junit.Assert.*;
import Models.Productos;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void testNombreProducto() {
        Productos producto = new Productos("Arreglo de Girasoles", "Arreglo floral", 1500.0, 10, "ruta/imagen");
        assertEquals("Arreglo de Girasoles", producto.getNombre());
    }

}