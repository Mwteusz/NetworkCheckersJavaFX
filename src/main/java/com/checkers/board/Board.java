package com.checkers.board;

import com.checkers.network.PlayerID;
import com.checkers.utils.Point;
import javafx.scene.Group;

import java.util.Arrays;
import java.util.function.Consumer;

import static com.checkers.utils.Constants.BOARD_SIZE;

public class Board extends Group {
    private Field selected;
    private Field[][] fields = new Field[BOARD_SIZE][BOARD_SIZE];
    private MoveListener listener;


    public Board(boolean doRender){
        for(int y=0;y<BOARD_SIZE;y++){
            for(int x=0;x<BOARD_SIZE;x++){
                Field newField = new Field(new Point(x,y),doRender);
                fields[x][y] = newField;
                if(doRender){
                    newField.setOnMouseClicked(mouseEvent -> selectField(newField));
                    getChildren().add(newField);
                }
            }
        }
    }

    public void addBoardListener(MoveListener listener) {
        this.listener = listener;
    }
    public void movePiece(Point src, Point dst, boolean doRender){
        getField(dst).removePiece();
        getField(dst).addPiece(getField(src).piece, doRender);
        getField(src).removePiece();
    }
    public void selectField(Field field) {

        if (field.position.isOdd())
            return;
        if (selected == null) {
            selected = field;
            selected.setSelect(true);
        } else if (selected.hasPiece() && !field.hasPiece()) {
            listener.inputMove(this, selected.position, field.position);
            selected.setSelect(false);
            selected = null;
        } else {
            selected.setSelect(false);
            selected = field;
            selected.setSelect(true);
        }
    }

    public void forEach(PlayerID playerID, Consumer<Field> action) {
        for (int y = 0; y < BOARD_SIZE; y++) {
            for (int x = 0; x < BOARD_SIZE; x++) {
                Field field = getField(new Point(x,y));
                if(field.hasPiece() && field.getPiece().getState().equals(playerID))
                    action.accept(field);
            }
        }
    }


    public Field getField(Point p) {
        return fields[p.x][p.y];
    }

    @Override
    public String toString() {
        return Arrays.toString(fields);
    }
    public int getBoardSize() {
        return BOARD_SIZE;
    }
}
