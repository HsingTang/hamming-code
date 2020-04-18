module view {
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires coder;

    exports UI;
    opens UI;
}