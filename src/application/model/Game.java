package application.model;

import java.util.Timer;

public class Game {
	
	Timer timerT;
//	ArrayList<ArrayList<Integer>> mineCoords = new ArrayList<ArrayList<Integer>>();
	final static int DEFAULT_NO_OF_MINES = 40;
	final static int DEFAULT_WIDTH = 16;
	final static int DEFAULT_HEIGHT = 16;
	int numberOfMines = DEFAULT_NO_OF_MINES;
	public int mineCounter = numberOfMines;
	boolean gameOver = false;
	boolean gameWon = false;

	Board gameBoard;
	
	public Game(int numberOfMines) {
		timerT = new Timer();
		gameBoard = new Board(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}
		
	public void newGame(int width, int height, int mines) {
		gameBoard.setGridWidth(width);
    	gameBoard.setGridHeight(height);
    	numberOfMines = mines;
//    	updateStage();
    	resetBoard();
	}
	
//	private void generateMines(int x, int y) {
//		ArrayList<Integer> mines = new ArrayList<>();
//		ArrayList<Integer> excluded = new ArrayList<Integer>();
//		excluded.add(y * gridWidth + x);
//		for (int[] offset : NEIGHBOR_OFFSETS) {
//			if (isValidNeighbour(x + offset[0], y + offset[1])) {
//				excluded.add((y + offset[1]) * gridWidth + (x + offset[0]));
//			}
//		}
//		mines = getRandomMineExcluding(excluded);
//		for (int i = 0; i < numberOfMines; i++) {		
//			minesLocations[mines.get(i) % gridWidth][mines.get(i) / gridWidth] = true;
//			ArrayList<Integer> mine = setUpCell(mines.get(i) % gridWidth, mines.get(i) / gridWidth);
////			validAdd(mineCoords, mine);
//		}
//	}
	
//	private ArrayList<Integer> getRandomMineExcluding(ArrayList<Integer> excluded) {
//		ArrayList<Integer> mines = new ArrayList<>();
//		int mine;
//		for (int i = 0; i < numberOfMines; i++) {
//			do {
//				mine = new Random().nextInt(gridWidth * gridHeight);
//			} while (excluded.contains(mine));
//			mines.add(mine);
//			excluded.add(mine);
//		}
//		return mines;
//	}
//	
//	private void checkWon() {
//		if(!gameOver) {
//			if(cellsExposed.size() == gridHeight * gridWidth - mineCounter) {
//				gameWon = true;
//				gameWon();
//			}
//		}
//	}
	
//	private void gameWon() {
//		timerT.cancel();
//		iterateOverAllCells((i, j) -> turnOverRemainingFlags(i, j));
//		updateButton(smiley, smileyImageViews[2]);
//
//	}
//	
//	private void gameOver(int i, int j) {
//		iterateOverAllCells((a, b) -> checkCellCombinations(a, b));
//		updateButton(tiles[i][j], tileImageViews[10]);
//		timerT.cancel();
//		gameOver = true;
//	}
//	
	public void resetBoard() {
//		updateButton(smiley, smileyImageViews[0]);
//		iterateOverAllCells((i, j) -> resetBoardHelper(i, j));
//		timerT.cancel();
//		time = -1;
//		updateLabel();
//		timerT = new Timer();
//		mineCounter = numberOfMines;
//		mineCounterStr = fillZeroes(mineCounter);
//		scoreboard.setText(mineCounterStr);
//		emptyCells.clear();
//		nonEmptyCells.clear();
//		cellsExposed.clear();
//		for (int r = 0; r < gridWidth; r++) {
//			Arrays.fill(mineFrequencies[r], 0);
//			Arrays.fill(flags[r], false);
//			Arrays.fill(minesLocations[r], false);
//		}
//		firstTime = true;
//		gameOver = false;
//		gameWon = false;
	}
}
