package com.example.peluqueria;

import java.sql.Time;
import java.util.Date;

//Clase que representa al cliente de la peluquería

public class Cliente {
    private int idCliente, telefono;
    private String nombre, ape1, ape2;
    private String estado;
    private Date fecha_cita;
    private Time hora;


    public Cliente(int idCliente, int telefono, String nombre, String ape1, String ape2, String estado, Date fecha_cita, Time hora) {
        this.idCliente = idCliente;
        this.telefono = telefono;
        this.nombre = nombre;
        this.ape1 = ape1;
        this.ape2 = ape2;
        this.estado = estado;
        this.fecha_cita = fecha_cita;
        this.hora = hora;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public int getTelefono() {
        return telefono;
    }

    public void setTelefono(int telefono) {
        this.telefono = telefono;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApe1() {
        return ape1;
    }

    public void setApe1(String ape1) {
        this.ape1 = ape1;
    }

    public String getApe2() {
        return ape2;
    }

    public void setApe2(String ape2) {
        this.ape2 = ape2;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getFecha_cita() {
        return fecha_cita;
    }

    public void setFecha_cita(Date fecha_cita) {
        this.fecha_cita = fecha_cita;
    }

    public Time getHora() {
        return hora;
    }

    public void setHora(Time hora) {
        this.hora = hora;
    }
}