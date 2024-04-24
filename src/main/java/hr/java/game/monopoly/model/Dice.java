package hr.java.game.monopoly.model;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.util.Random;

public class Dice {
    private int diceNumber;

    private final ImageView diceImage;

    public Dice(ImageView diceImage, int diceNumber) {
        this.diceNumber = diceNumber;
        this.diceImage = diceImage;
    }

    public int getDiceNumber() {
        return diceNumber;
    }

    public void setDiceNumber(int diceNumber) {
        this.diceNumber = diceNumber;
    }

    public void roll() {
        Random random = new Random();
        // Generate a random number between 1 and the number of sides of the dice
        int tmpNumber = random.nextInt(6) + 1;
        File file = new File("src/main/resources/images/dice/dice-" + (tmpNumber) +".png");
        this.diceImage.setImage(new Image(file.toURI().toString()));
        this.diceNumber = tmpNumber;
    }
}
