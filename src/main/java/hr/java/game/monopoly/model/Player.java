package hr.java.game.monopoly.model;

import javafx.scene.control.Label;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Setter
@Getter
public class Player {
    private int id;
    private Label playerFigure;
    private String name;
    private int wallet;
    private int currentField = 0;
    private ArrayList<Field> fieldsOwns = new ArrayList<>();

    public Player(int id, Label playerFigure, String name, int wallet) {
        this.id = id;
        this.playerFigure = playerFigure;
        this.name = name;
        this.wallet = wallet;
    }

    public void buyField(int withdrawAmount, Field field) {
        if(withdrawAmount > this.wallet) {
            this.wallet = 0;
            System.out.println("Player "+ this.id + " went bankrupt!");
        } else {
            if(this.fieldsOwns.isEmpty()){
                this.fieldsOwns.add(field);
            }else{
                for (Field ownedField : fieldsOwns) {
                    System.out.println("Owned field "+ ownedField.getTitle());
                    if (ownedField.getId() != field.getId()) {
                        this.fieldsOwns.add(field);
                        break;
                    }else{
                        break;
                    }
                }
            }
            this.wallet -= withdrawAmount;
        }
    }

    public void addToWallet(int walletAmount) {
        this.wallet += walletAmount;
    }
}
