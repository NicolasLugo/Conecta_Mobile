package com.example.conectamobile;

public class Contacto {
    private String nombre;
    private String email;
    private String Id;

    public Contacto(String nombre, String email, String id) {
        this.nombre = nombre;
        this.email = email;
        Id = id;
    }

    public Contacto() {
    }

    public Contacto(String nombre) {
        this.nombre = nombre;
    }

    public Contacto(String nombre, String email) {
        this.nombre = nombre;
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    @Override
    public String toString() {
        return nombre + "\n" + email;
    }
}