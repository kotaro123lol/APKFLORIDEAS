package Models;

public class Cliente {
    private String ClienteID;
    private String apellidos;
    private String nombres;
    private String dni;
    private String direccion;
    private String telefono;
    private String correo;
    private String contrasena;

    // Constructor vacío necesario para Firebase
    public Cliente() {
        // Este constructor debe estar presente
    }


    public Cliente(String clienteID, String nombres, String apellidos, String dni, String direccion, String telefono, String correo, String contrasena) {
        ClienteID = clienteID;
        this.apellidos = apellidos;
        this.nombres = nombres;
        this.dni = dni;
        this.direccion = direccion;
        this.telefono = telefono;
        this.correo = correo;
        this.contrasena = contrasena;
    }


    public String getClienteID() {return ClienteID;}
    public String getApellidos() {
        return apellidos;
    }
    public String getNombres() {
        return nombres;
    }
    public String getDni() {
        return dni;
    }
    public String getDireccion() {
        return direccion;
    }
    public String getTelefono() { return telefono; }
    public String getCorreo() {
        return correo;
    }
    public String getContrasena() {
        return contrasena;
    }

    public void setClienteID(String clienteID) { this.ClienteID = clienteID; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public void setDni(String dni) { this.dni = dni; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setCorreo(String correo) { this.correo = correo; }
    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }


    @Override
    public String toString() {
        return "Clientes{" +
                "Nombre='" + nombres + ' ' + apellidos + '\'' +
                ", DNI='" + dni + '\'' +
                ", Direccion='" + direccion + '\'' +
                ", Telefono='" + telefono + '\'' +
                ", Correo='" + correo + '\'' +
                ", Contraseña='" + contrasena + '\'' +
                '}';
    }

}
