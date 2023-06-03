package com.checkers.board;

import com.checkers.network.PlayerID;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;

import static com.checkers.utils.Constants.TILE_SIZE;

public class Piece extends StackPane {

    private final PlayerID playerID;
    private Circle shape;
    private boolean king = false;
    private ImageView crownTexture;

    Piece(PlayerID playerID, boolean doRender){
        this.playerID = playerID;
        if(doRender) {
            this.shape = new Circle(TILE_SIZE*0.5*0.75);
            this.shape.setEffect(new Lighting());
            this.shape.setFill(playerID.color);
            this.shape.setStrokeWidth(TILE_SIZE*0.05);
            this.shape.setStrokeType(StrokeType.INSIDE);
            getChildren().add(shape);
        }
    }

    public PlayerID getState() {
        return playerID;
    }

    @Override
    public String toString() {
        return String.valueOf(playerID.getIndex());
    }

    public void setSelect(boolean b) {
        shape.setStroke(b ? Color.WHITE : Color.TRANSPARENT);
    }

    public void promote() {
        this.king = true;
        try {
            crownTexture = new ImageView(new Image("file:src/main/resources/com/crown50px.png", 20, 20, true, false));
            getChildren().add(crownTexture);
        }
        catch (Exception e) {
            System.out.println("blad ladowania grafiki: "+e.getMessage());
        }
    }

    public boolean isKing() {
        return king;
    }
}
