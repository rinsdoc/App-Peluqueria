package com.example.peluqueria;

import javafx.collections.ObservableList;

import java.sql.SQLException;

public interface ClienteDAO {
    ObservableList<Cliente> searchCliente(String text) throws SQLException;
    void insertCliente(int telefono, String nombre, String ape1, String ape2,
                       String estado, String fecha_cita, String hora) throws SQLException;
    void updateCliente(Cliente cliente) throws SQLException;
    void deleteCliente(Cliente cliente);
}