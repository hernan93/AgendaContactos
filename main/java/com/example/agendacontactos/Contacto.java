package com.example.agendacontactos;

public class Contacto {

    String id, image, nombre, apellido, email, direccion, numeroMovil,addedTime, updateTime;

    public Contacto(String id, String image, String nombre, String apellido, String email,
                    String direccion, String numeroMovil, String addedTime, String updateTime) {
        this.id = id;
        this.image = image;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.direccion = direccion;
        this.numeroMovil = numeroMovil;
        this.addedTime = addedTime;
        this.updateTime = updateTime;
    }

    public Contacto() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getNumeroMovil() {
        return numeroMovil;
    }

    public void setNumeroMovil(String numeroMovil) {
        this.numeroMovil = numeroMovil;
    }

    public String getAddedTime() { return addedTime; }

    public void setAddedTime(String addedTime) { this.addedTime = addedTime; }

    public String getUpdateTime() { return updateTime; }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
