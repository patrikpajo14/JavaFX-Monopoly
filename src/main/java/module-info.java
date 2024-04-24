module hr.java.game.monopoly {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires static lombok;


    opens hr.java.game.monopoly to javafx.fxml;
    exports hr.java.game.monopoly;
}