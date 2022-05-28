module com.example.offlinemessenger {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires com.alibaba.fastjson2;

    opens com.example.doodlejumpwithmp to javafx.fxml;
    exports com.example.doodlejumpwithmp;
    exports com.example.doodlejumpwithmp.model.platform;
    opens com.example.doodlejumpwithmp.model.platform to javafx.fxml;
    exports com.example.doodlejumpwithmp.model.doodle;
    opens com.example.doodlejumpwithmp.model.doodle to javafx.fxml;
    exports com.example.doodlejumpwithmp.model.serverwork;
    opens com.example.doodlejumpwithmp.model.serverwork to javafx.fxml;
    exports com.example.doodlejumpwithmp.model;
    opens com.example.doodlejumpwithmp.model to javafx.fxml;
}