module hr.java.game.monopoly {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens hr.java.game.monopoly to javafx.fxml;
    exports hr.java.game.monopoly;
}