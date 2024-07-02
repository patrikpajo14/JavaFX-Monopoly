module hr.java.game.monopoly {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires static lombok;
    requires java.rmi;
    requires java.naming;

    opens hr.java.game.monopoly to javafx.fxml;
    exports hr.java.game.monopoly;
    opens hr.java.game.monopoly.chat to java.rmi;
}