module com.example.paintproject {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.paintproject to javafx.fxml;
    exports com.example.paintproject;
}