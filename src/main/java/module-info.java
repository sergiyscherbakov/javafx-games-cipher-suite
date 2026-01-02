module com.example.course4 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.course4 to javafx.fxml;
    exports com.example.course4;
}