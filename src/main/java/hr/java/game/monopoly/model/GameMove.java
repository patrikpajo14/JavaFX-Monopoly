package hr.java.game.monopoly.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class GameMove implements Serializable {
    private PlayerTurn playerTurn;
    private Integer oldPosition;
    private Integer newPosition;
    private LocalDateTime localDateTime;
}
