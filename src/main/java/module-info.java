module com.example.warcaby_odnowa {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.checkers to javafx.fxml;
    exports com.checkers.network.server;
    opens com.checkers.network.server to javafx.fxml;
    exports com.checkers.network.client;
    opens com.checkers.network.client to javafx.fxml;
    exports com.checkers.board;
    opens com.checkers.board to javafx.fxml;
    exports com.checkers.network;
    opens com.checkers.network to javafx.fxml;
    exports com.checkers.utils;
    opens com.checkers.utils to javafx.fxml;
}