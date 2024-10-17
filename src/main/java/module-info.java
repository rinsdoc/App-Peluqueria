module com.example.peluqueria {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;

    opens com.example.peluqueria to javafx.fxml;
    exports com.example.peluqueria;
}