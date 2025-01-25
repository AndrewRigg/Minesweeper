package application.model;

import java.util.*;

public class Cell {

    final static int MAX_NEIGHBOURS = 8;

    int neighbouringMines;
    boolean minePresent;
    boolean markRaised;
    boolean flagRaised;
    int xPosition;
    int yPosition;
    CellState thisCellState;
    CellContents thisCellContents;
    EndGame thisCellEndGame;
    Board board;

    CellContents [] neighboursContents = new CellContents[MAX_NEIGHBOURS];

    enum CellState{
        UNMARKED,
        MARKED,
        FLAGGED,
        REVEALED
    }

    enum CellContents{
        UNASSIGNED,
        EMPTY,
        ONE,
        TWO,
        THREE,
        FOUR,
        FIVE,
        SIX,
        SEVEN,
        EIGHT,
        MINE
    }

    enum EndGame{
        UNCHANGEDED,
        UNFLAGGED_MINE_REVEALED,
        FLAGGED_NON_MINE_REVEALED
    }

    public Cell(int xPosition, int yPosition, Board board) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        thisCellState = CellState.UNMARKED;
        thisCellContents = CellContents.UNASSIGNED;
        thisCellEndGame = EndGame.UNCHANGEDED;
        Arrays.fill(neighboursContents, CellContents.UNASSIGNED);
        this.board = board;
    }

    private void cellEndGame(int i, int j) {
        if(!flagRaised && minePresent) {
            thisCellEndGame = EndGame.UNFLAGGED_MINE_REVEALED;
        }else if(flagRaised && !minePresent) {
            thisCellEndGame = EndGame.FLAGGED_NON_MINE_REVEALED;
        }
    }
}
