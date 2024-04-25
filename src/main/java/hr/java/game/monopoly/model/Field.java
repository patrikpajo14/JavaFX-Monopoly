package hr.java.game.monopoly.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Field implements Serializable {
    private int id;
    private String title;
    private int price;
    private int rentPrice;
    private Player owner;

    public Field(int id, String title, int price, int rentPrice) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.rentPrice = rentPrice;
    }
}
