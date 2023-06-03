package com.checkers.board;

import com.checkers.utils.ColorPalette;
import com.checkers.network.PlayerID;
import com.checkers.utils.Point;
import javafx.scene.effect.Bloom;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

import static com.checkers.utils.Constants.BOARD_SIZE;
import static com.checkers.utils.Constants.TILE_SIZE;

public class Field extends StackPane {
    Rectangle tile;
    Piece piece;
    public Point position;
    Field(Point position,boolean doRender){
        this.position = position;
        if(doRender) {
            this.tile = new Rectangle(TILE_SIZE,TILE_SIZE);
            relocate(position.x * TILE_SIZE, position.y * TILE_SIZE);
            tile.setFill(position.isOdd() ? ColorPalette.light : ColorPalette.dark);
            getChildren().add(tile);
        }
        this.piece = newPiece(position,doRender);
    }
    private Piece newPiece(Point position, boolean doRender) {
        if(position.isOdd())
            return null;
        Piece newPiece = null;
        if(position.y<=2)
            newPiece = new Piece(PlayerID.PLAYER1,doRender);
        if(position.y>=BOARD_SIZE-3)
            newPiece = new Piece(PlayerID.PLAYER2,doRender);

        if(newPiece!=null && doRender)
            getChildren().add(newPiece);
        return newPiece;
    }

    public void addPiece(Piece piece, boolean doRender)
    {
        if(hasPiece())
            throw new RuntimeException("piece juz istnieje");
        this.piece = piece;
        if(doRender)
            getChildren().add(piece);
    }
    public void removePiece()
    {
        if(hasPiece()) {
            getChildren().remove(this.piece);
            this.piece = null;
        }
    }

    public boolean hasPiece() {
        return piece!=null;
    }

    public void setSelect(boolean b) {

        tile.setEffect(new Bloom(b ? 0 : 1));
    }
    public Piece getPiece() {
        return piece;
    }

    @Override
    public String toString() {
        return hasPiece() ? piece.toString() : "[]";
    }
}
