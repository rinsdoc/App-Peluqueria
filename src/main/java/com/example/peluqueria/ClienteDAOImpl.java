package com.example.peluqueria;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.EventObject;
import java.util.Optional;

public class ClienteDAOImpl implements ClienteDAO {

    private static int idCliente;

    // Clase que maneja la conexión a la base de datos
    public class Database {
        private static Connection connection;
        //Para ambas conexiones hay que añadir las dependencias en el pom.xml
        //Conexión con oracle
        public static void connect() throws SQLException {
        if (connection == null || connection.isClosed()) {
        connection = DriverManager.getConnection(
                "jdbc:oracle:thin:@localhost:1521:xe",
                "c##lola",
                "1234"
            );
        }
        }
        //Conexión con mysql
        /*public static void connect() throws SQLException {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/peluqueria",
                        "root",
                        "1234"
                );
            }
        }*/

        public static Connection getConnection() {
            return connection;
        }
    }

    //Método que cierra la conexión con la base de datos.
    public static void disconnect() {
        try {
            Database.getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public ObservableList<Cliente> searchCliente(String searchText) throws java.sql.SQLException {
        String sql = "SELECT cliente.idCliente, cliente.nombre, cliente.ape1, cliente.ape2, " +
                "cliente.telefono, pide_cita.estado, pide_cita.fecha_cita, pide_cita.hora " +
                "FROM cliente " +
                "LEFT JOIN pide_cita ON cliente.idCliente = pide_cita.idCliente_fk " +
                "WHERE cliente.nombre LIKE ? OR cliente.ape1 LIKE ? OR cliente.ape2 LIKE ? " +
                "OR cliente.telefono LIKE ? OR pide_cita.estado LIKE ?" +
                "ORDER BY pide_cita.fecha_cita ASC, pide_cita.hora ASC";
        Connection connection = Database.getConnection();
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, "%" + searchText + "%");
        ps.setString(2, "%" + searchText + "%");
        ps.setString(3, "%" + searchText + "%");
        ps.setString(4, "%" + searchText + "%");
        ps.setString(5, "%" + searchText + "%");
        ResultSet rs = ps.executeQuery();
        ObservableList<Cliente> clientes = FXCollections.observableArrayList();
        while (rs.next()) {
            int idCliente = rs.getInt("idCliente");
            int telefono = rs.getInt("telefono");
            String nombre = rs.getString("nombre");
            String ape1 = rs.getString("ape1");
            String ape2 = rs.getString("ape2");
            String estado = rs.getString("estado");
            Date fecha_cita = rs.getDate("fecha_cita");
            Time hora = rs.getTime("hora");
            Cliente cliente = new Cliente(idCliente, telefono, nombre, ape1, ape2, estado, fecha_cita, hora);
            clientes.add(cliente);
        }
        return clientes;
    }

    @Override
    public void insertCliente(int telefono, String nombre, String ape1, String ape2,
                              String estado, String fecha_cita, String hora) throws SQLException {
        if (!nombre.matches("[a-zA-Z]+")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, introduce un nombre válido.");
            alert.showAndWait();
            return;
        }

        if (!ape1.matches("[a-zA-Z]+")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, introduce un primer apellido válido.");
            alert.showAndWait();
            return;
        }

        if (ape2 != null && !ape2.isEmpty() && !ape2.matches("[a-zA-Z]*")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, introduce un segundo apellido válido.");
            alert.showAndWait();
            return;
        }

        if ((int) Math.log10(Math.abs(telefono)) + 1 != 9) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("El teléfono debe ser un número de 9 dígitos.");
            alert.showAndWait();
            return;
        }

        try {
            LocalDate fechaIngresada = LocalDate.parse(fecha_cita, DateTimeFormatter.ISO_LOCAL_DATE);
            LocalDate fechaActual = LocalDate.now();
            LocalDate fechaFutura = fechaActual.plusYears(100);

            if (fechaIngresada.isBefore(fechaActual) || fechaIngresada.isAfter(fechaFutura)) {
                throw new IllegalArgumentException("La fecha debe estar entre hoy y 100 años en el futuro.");
            }
        } catch (DateTimeParseException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, introduce una fecha válida en el formato YYYY-MM-DD en el campo de fecha.");
            alert.showAndWait();
            return;
        } catch (IllegalArgumentException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }

        if (!hora.matches("([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, introduce una hora válida en el formato HH:MM:SS.");
            alert.showAndWait();
            return;
        }

        //Se generan las sentencias SQL para añadir los datos a la base de datos
        String sqlAdd = "INSERT INTO cliente (nombre, ape1, ape2, telefono) VALUES (?, ?, ?, ?)";
        String sqlAddCita = "INSERT INTO pide_cita (fecha_cita, hora, idCliente_fk, estado) VALUES (?, ?, ?, 'Pendiente')";

        try {
            Database.connect();
            Connection connection = Database.getConnection();

            // Inserta los datos en la tabla cliente
            PreparedStatement pstmt = connection.prepareStatement(sqlAdd, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, nombre);
            pstmt.setString(2, ape1);
            pstmt.setString(3, ape2);
            pstmt.setInt(4, telefono);
            pstmt.executeUpdate();

            // Obtiene el ID del cliente que acaba de ser insertado
            ResultSet rs = pstmt.getGeneratedKeys();
            int idCliente = 0;
            if (rs.next()) {
                idCliente = rs.getInt(1);
            }

            // Inserta los datos en la tabla pide_cita usando el ID del cliente
            PreparedStatement pstmtCita = connection.prepareStatement(sqlAddCita);
            pstmtCita.setDate(1, Date.valueOf(fecha_cita));
            pstmtCita.setTime(2, Time.valueOf(hora));
            pstmtCita.setInt(3, idCliente);
            pstmtCita.executeUpdate();

            // Muestra un mensaje de confirmación
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Confirmación");
            alert.setHeaderText(null);
            alert.setContentText("Los datos se han añadido correctamente.");
            alert.showAndWait();

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Error de base de datos: " + e.getMessage());
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Los campos no pueden estar vacíos.");
            alert.showAndWait();
        }
    }

    @Override
    public void updateCliente(Cliente cliente) throws SQLException {
        if (!cliente.getNombre().matches("[a-zA-Z]+")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, introduce un nombre válido.");
            alert.showAndWait();
            return;
        }

        if (!cliente.getApe1().matches("[a-zA-Z]+")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, introduce un primer apellido válido.");
            alert.showAndWait();
            return;
        }

        if (cliente.getApe2() != null && !cliente.getApe2().isEmpty() && !cliente.getApe2().matches("[a-zA-Z]*")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, introduce un segundo apellido válido.");
            alert.showAndWait();
            return;
        }

        if ((int) Math.log10(Math.abs(cliente.getTelefono())) + 1 != 9) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("El teléfono debe ser un número de 9 dígitos.");
            alert.showAndWait();
            return;
        }

        try {
            LocalDate fechaIngresada = LocalDate.parse(cliente.getFecha_cita().toString(), DateTimeFormatter.ISO_LOCAL_DATE);
            LocalDate fechaActual = LocalDate.now();
            LocalDate fechaFutura = fechaActual.plusYears(100);

            if (fechaIngresada.isBefore(fechaActual) || fechaIngresada.isAfter(fechaFutura)) {
                throw new IllegalArgumentException("La fecha debe estar entre hoy y 100 años en el futuro.");
            }
        } catch (DateTimeParseException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, introduce una fecha válida en el formato YYYY-MM-DD en el campo de fecha.");
            alert.showAndWait();
            return;
        } catch (IllegalArgumentException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }

        if (!cliente.getHora().toString().isEmpty()) {
            try {
                LocalTime localTime = LocalTime.parse(cliente.getHora().toString(), DateTimeFormatter.ISO_LOCAL_TIME);
                Time horaCitaBD = Time.valueOf(localTime);
            } catch (DateTimeParseException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Por favor, introduce una hora válida en el formato HH:MM:SS.");
                alert.showAndWait();
                return;
            }
        }

        try {
            Database.connect();
            Connection connection = Database.getConnection();
            // Actualización en la tabla cliente
            String sqlCliente = "UPDATE cliente SET nombre = ?, ape1 = ?, ape2 = ?, telefono = ? WHERE idCliente = ?";
            PreparedStatement pstmt = connection.prepareStatement(sqlCliente);
            pstmt.setString(1, cliente.getNombre());
            pstmt.setString(2, cliente.getApe1());
            pstmt.setString(3, cliente.getApe2());
            pstmt.setInt(4, cliente.getTelefono());
            pstmt.setInt(5, cliente.getIdCliente());

            pstmt.executeUpdate();

            // Actualización en la tabla pide_cita
            String sqlPedirCita = "UPDATE pide_cita SET fecha_cita = ?, hora = ?, estado = ? WHERE idCliente_fk = ?";
            pstmt = connection.prepareStatement(sqlPedirCita);
            pstmt.setDate(1, new Date(cliente.getFecha_cita().getTime()));
            pstmt.setTime(2, cliente.getHora());
            pstmt.setString(3, cliente.getEstado());
            pstmt.setInt(4, cliente.getIdCliente());


            pstmt.executeUpdate();

            //PopUp con mensaje de confirmación de la operación.
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Confirmación");
            alert.setHeaderText(null);
            alert.setContentText("Los datos se han actualizado correctamente.");
            alert.showAndWait();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteCliente(Cliente cliente) {

        try {
            Database.connect();
            Connection connection = Database.getConnection();

            // Borra la fila en la tabla pide_cita
            String sqlDeleteCita = "DELETE FROM pide_cita WHERE IDCLIENTE_FK = ?";
            PreparedStatement pstmtCita = connection.prepareStatement(sqlDeleteCita);
            pstmtCita.setInt(1, cliente.getIdCliente());
            pstmtCita.executeUpdate();

            // Borra la fila en la tabla cliente
            String sqlDelete = "DELETE FROM cliente WHERE idCliente = ?";
            PreparedStatement pstmt = connection.prepareStatement(sqlDelete);
            pstmt.setInt(1, cliente.getIdCliente());
            pstmt.executeUpdate();

            //PopUp con mensaje de confirmación de la operación.
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Confirmación");
            alert.setHeaderText(null);
            alert.setContentText("Los datos se han borrado correctamente.");
            alert.showAndWait();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}