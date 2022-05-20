module com.example.offlinemessenger {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens com.example.doodlejumpwithmp to javafx.fxml;
    exports com.example.doodlejumpwithmp;
}