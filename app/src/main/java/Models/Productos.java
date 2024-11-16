package Models;

public class Productos {
    private String ProductoID;
    private String Nombre;
    private String Descripcion;
    private String Precio;
    private String Stock;
    private String RutaImagen;

    public Productos() {
    }

    public Productos(String nombre, String descripcion, double precio, int stock, String rutaImagen) {
        this.Nombre = nombre;
        this.Descripcion = descripcion;
        this.Precio = String.valueOf(precio);
        this.Stock = String.valueOf(stock);
        this.RutaImagen = rutaImagen;
    }

    public String getProductoID() {
        return ProductoID;
    }
    public void setProductoID(String productoID) {
        ProductoID = productoID;
    }
    public String getNombre() {
        return Nombre;
    }
    public void setNombre(String nombre) {
        Nombre = nombre;
    }
    public String getDescripcion() {
        return Descripcion;
    }
    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }
    public String getPrecio() {
        return Precio;
    }
    public void setPrecio(String precio) {
        Precio = precio;
    }
    public String getStock() {
        return Stock;
    }
    public void setStock(String stock) {
        Stock = stock;
    }
    public String getRutaImagen() {
        return RutaImagen;
    }
    public void setRutaImagen(String rutaImagen) {
        RutaImagen = rutaImagen;
    }

    @Override
    public String toString() {
        return "Productos{" +
                "Nombre='" + Nombre + '\'' +
                ", Descripcion='" + Descripcion + '\'' +
                ", Precio=" + Precio +
                ", Stock=" + Stock +
                ", RutaImagen='" + RutaImagen + '\'' +
                '}';
    }
}