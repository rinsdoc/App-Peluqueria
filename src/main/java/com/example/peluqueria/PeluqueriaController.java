package com.example.peluqueria;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
import java.io.IOException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.EventObject;

import com.example.peluqueria.ClienteDAO;
import com.example.peluqueria.ClienteDAOImpl;

// Controlador de la aplicación de la peluquería
public class PeluqueriaController {

    @FXML
    private TextField searchField;
    @FXML
    private Object SQLException;
    @FXML
    private TableView<Cliente> tableView;
    @FXML
    private TableColumn<Cliente, Integer> idClienteColumn;
    @FXML
    private TableColumn<Cliente, String> nombreColumn;
    @FXML
    private TableColumn<Cliente, String> ape1Column;
    @FXML
    private TableColumn<Cliente, String> ape2Column;
    @FXML
    private TableColumn<Cliente, Integer> telefonoColumn;
    @FXML
    private TableColumn<Cliente, String> estadoColumn;
    @FXML
    private TableColumn<Cliente, String> fechaColumn;
    @FXML
    private TableColumn<Cliente, String> horaColumn;
    @FXML
    private TextField nombreField;
    @FXML
    private TextField ape1Field;
    @FXML
    private TextField ape2Field;
    @FXML
    private TextField telefonoField;
    @FXML
    private TextField fechaCitaField;
    @FXML
    private TextField horaCitaField;
    @FXML
    private ComboBox<String> estadoField;

    private int idCliente;
    private ClienteDAO clienteDAO = new ClienteDAOImpl();

    public void searchCliente() throws java.sql.SQLException {
        ObservableList<Cliente> clientes = clienteDAO.searchCliente(searchField.getText());
        llenarTabla(clientes);
    }

    public void deleteCliente(ActionEvent actionEvent) throws SQLException {
        Cliente cliente = tableView.getSelectionModel().getSelectedItem();
        clienteDAO.deleteCliente(cliente);
        searchCliente();
    }

    public void insertCliente(ActionEvent actionEvent) throws SQLException, IOException {
        clienteDAO.insertCliente(Integer.parseInt(telefonoField.getText()), nombreField.getText(), ape1Field.getText(),
                ape2Field.getText(), "Pendiente", fechaCitaField.getText(), horaCitaField.getText());
        // Redirecciona a la página consultaCitas.
        consultaCitas(actionEvent);
    }

    public void updateCliente(ActionEvent actionEvent) throws SQLException, IOException {
        Cliente cliente = new Cliente(idCliente, Integer.parseInt(telefonoField.getText()), nombreField.getText(),
                ape1Field.getText(), ape2Field.getText(), estadoField.getValue(), Date.valueOf(fechaCitaField.getText()),
                Time.valueOf(horaCitaField.getText()));
        clienteDAO.updateCliente(cliente);
        // Redirecciona a la página consultaCitas.
        consultaCitas(actionEvent);
    }

    public void llenarTabla(ObservableList<Cliente> clientes) throws SQLException {
        // Esta línea serviría para mostrar el id del cliente en la tabla.
        //idClienteColumn.setCellValueFactory(new PropertyValueFactory<>("idCliente"));
        nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        ape1Column.setCellValueFactory(new PropertyValueFactory<>("ape1"));
        ape2Column.setCellValueFactory(new PropertyValueFactory<>("ape2"));
        telefonoColumn.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        estadoColumn.setCellValueFactory(new PropertyValueFactory<>("estado"));
        fechaColumn.setCellValueFactory(new PropertyValueFactory<>("fecha_cita"));
        horaColumn.setCellValueFactory(new PropertyValueFactory<>("hora"));
        tableView.setItems(clientes);
    }


    //Método que rellena los campos de la pantalla de modificación de datos
    // con los datos del cliente seleccionado basándose en su id.
    public void setCliente(Cliente selectedCliente) throws SQLException {
        nombreField.setText(selectedCliente.getNombre());
        ape1Field.setText(selectedCliente.getApe1());
        ape2Field.setText(selectedCliente.getApe2());
        telefonoField.setText(String.valueOf(selectedCliente.getTelefono()));
        fechaCitaField.setText(String.valueOf(selectedCliente.getFecha_cita()));
        horaCitaField.setText(String.valueOf(selectedCliente.getHora()));
        estadoField.getItems().addAll("Pendiente", "Confirmada");
        estadoField.setValue(selectedCliente.getEstado());
        idCliente = selectedCliente.getIdCliente();
    }

    //Método que lleva a la página de modificación de citas con los datos del cliente seleccionado en la tabla.
    public void updateRow(ActionEvent event) {
        // Obtén el cliente seleccionado
        Cliente selectedCliente = tableView.getSelectionModel().getSelectedItem();

        if (selectedCliente != null) {
            try {
                // Carga el formulario de actualización
                FXMLLoader loader = new FXMLLoader(getClass().getResource("modificarCita.fxml"));
                Parent root = loader.load();

                // Obtiene el controlador del formulario de actualización
                PeluqueriaController controller = loader.getController();
                // Pasa los datos del cliente al formulario de actualización
                controller.setCliente(selectedCliente);

                // Muestra el formulario de actualización
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setTitle("Modificar Cita");
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (java.sql.SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    //Método que lleva a la página de inicio
    public void goToHome(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("home.fxml"));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setTitle("Home");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Método que lleva a la página de consulta de citas
    public void consultaCitas(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(PeluqueriaController.class.getResource("consultaCitas.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Consulta de Citas");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Método que lleva a la página de creación de citas
    public void crearCita(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("crearCita.fxml"));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setTitle("Crear Cita");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Método que lleva a la página de login.
    public void goToLogin(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setTitle("Login Page");
            stage.setScene(new Scene(root));
            stage.show();
            ClienteDAOImpl.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}