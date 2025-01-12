package application.model;

import java.util.Arrays;

public class Cell {
	
	final static int MAX_NEIGHBOURS = 8;
	final static int[][] NEIGHBOR_OFFSETS = {
		    {-1, -1}, {0, -1}, {1, -1}, // Top row
		    {-1,  0},          {1,  0}, // Middle row (skip centre)
		    {-1,  1}, {0,  1}, {1,  1}  // Bottom row
		};
	int neighbouringMines;
	boolean minePresent;
	boolean markRaised;
	boolean flagRaised;
	int xPosition;
	int yPosition;
	CellState thisCellState;
	CellContents thisCellContents;
	EndGame thisCellEndGame;
	Board gameBoard;
	
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
	
	public Cell(int xPosition, int yPosition, Board gameBoard) {
		this.xPosition = xPosition;
		this.yPosition = yPosition;
		thisCellState = CellState.UNMARKED;
		thisCellContents = CellContents.UNASSIGNED;
		thisCellEndGame = EndGame.UNCHANGEDED;
		Arrays.fill(neighboursContents, CellContents.UNASSIGNED);
		this.gameBoard = gameBoard;
	}
	
	private void cellEndGame(int i, int j) {
		if(!flagRaised && minePresent) {
			thisCellEndGame = EndGame.UNFLAGGED_MINE_REVEALED;
		}else if(flagRaised && !minePresent) {
			thisCellEndGame = EndGame.FLAGGED_NON_MINE_REVEALED;
		}
	}
	
//	private void checkCellCombinations() {
//		if(!flags[i][j] && minesLocations[i][j]) {
//			updateButton(tiles[i][j], tileImageViews[14]);
//		}else if(flags[i][j] && !minesLocations[i][j]) {
//			updateButton(tiles[i][j], tileImageViews[12]);
//		}
//	}
	
//	private ArrayList<ArrayList<Integer>> getNeighbouringCells(int i, int j, BiConsumer<ArrayList<ArrayList<Integer>>, ArrayList<Integer>> action) {
//		ArrayList<ArrayList<Integer>> cells = new ArrayList<>();
//		for (int[] offset : NEIGHBOR_OFFSETS) {
//			int neighbourX = i + offset[0];
//			int neighbourY = j + offset[1];
//				if (isValidNeighbour(neighbourX, neighbourY)) {
//					ArrayList<Integer> cell = setUpCell(neighbourX, neighbourY);
//					action.accept(cells, cell);
//				}
//		}
//		return cells;
//	}
	
	
//	private boolean isValidNeighbour(int i, int j) {
//		return i >= 0 && i < gridWidth && j >= 0 && j < gridHeight;
//	}
	
	private boolean isValidNeighbour(int i, int j) {
		return i >= 0 && i < gameBoard.gridWidth && j >= 0 && j < gameBoard.gridHeight;
	}
	

	

}
