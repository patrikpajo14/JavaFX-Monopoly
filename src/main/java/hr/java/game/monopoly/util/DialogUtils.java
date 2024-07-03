package hr.java.game.monopoly.util;
import hr.java.game.monopoly.model.Player;
import javafx.scene.control.Alert;

public class DialogUtils {

    public static void showWinnerDialog(Player winner) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(null);
        alert.setContentText("The winner is: " + winner.getName());
        alert.showAndWait();
    }

    public static void showSuccessDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("The action was successful!");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
