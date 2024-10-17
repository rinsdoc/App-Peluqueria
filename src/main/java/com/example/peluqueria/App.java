package com.example.peluqueria;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

//Maneja la conexión a la base de datos y el inicio de la aplicación
public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("Login Page");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) throws SQLException {
        ClienteDAOImpl.Database.connect();
        if (ClienteDAOImpl.Database.getConnection().isClosed()) {
            System.out.println("Connection is closed");
        } else {
            System.out.println("Connection is open");
        }
        launch();
        ClienteDAOImpl.disconnect();
        System.out.println("Connection is closed");
    }
}