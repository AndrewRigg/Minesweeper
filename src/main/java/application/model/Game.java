package application.model;

import application.controller.*;
import application.view.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

import static application.model.Board.NEIGHBOR_OFFSETS;

public class Game {

    Timer timerT;
    final static int DEFAULT_NO_OF_MINES = 40;
    int numberOfMines = DEFAULT_NO_OF_MINES;
    public int mineCounter = numberOfMines;
    boolean gameOver = false,
            gameWon = false,
            firstTime = true;

    BoardView boardView;
    Board board;
    MainView mainView;
    GameController controller;

    public Game(int numberOfMines, Board board, MainView mainView, GameController controller) {
        timerT = new Timer();
        this.board = board;
        boardView = new BoardView(board, board.tileSize, this);
        this.mainView = mainView;
        this.controller = controller;
    }

    public BoardView getBoardView() {
        return boardView;
    }

    public void setUpCells(GridPane gridPane) {
        iterateOverAllCells((i, j) -> mainView.setUpButtonsFirstTime(i, j, gridPane));
    }

    private void iterateOverAllCells(BiConsumer<Integer, Integer> action) {
        System.out.println("GridPane x: " + board.getGridWidth() + " y: " + board.getGridHeight());
        for (int i = 0; i < board.getGridWidth(); i++) {
            for (int j = 0; j < board.getGridHeight(); j++) {
                action.accept(i, j);
            }
        }
    }

	private void checkWon() {
		if(!gameOver) {
			if(board.cellsExposed.size() == board.gridHeight * board.gridWidth - mineCounter) {
				gameWon = true;
				gameWon();
			}
		}
	}

	private void gameWon() {
		timerT.cancel();
		iterateOverAllCells(this::turnOverRemainingFlags);
		updateButton(mainView.smiley, mainView.smileyImageViews[2]);
	}

    private void turnOverRemainingFlags(int i, int j) {
        ArrayList<Integer> cell = board.setUpCell(i, j);
        if(!board.cellsExposed.contains(cell)) {
            updateButton(board.tiles[i][j], boardView.tileImageViews[11]);
        }
    }

	private void gameOver(int i, int j) {
		iterateOverAllCells(this::checkCellCombinations);
		updateButton(board.tiles[i][j], boardView.tileImageViews[10]);
		timerT.cancel();
		gameOver = true;
	}

    private void checkCellCombinations(int i, int j) {
        if(!board.flags[i][j] && board.minesLocations[i][j]) {
            updateButton(board.tiles[i][j], boardView.tileImageViews[14]);
        }else if(board.flags[i][j] && !board.minesLocations[i][j]) {
            updateButton(board.tiles[i][j], boardView.tileImageViews[12]);
        }
    }

    public void updateButton(Button button, ImageView imageView) {
        button.setGraphic(copyImageView(imageView));
    }

    public static ImageView copyImageView(ImageView original) {
        ImageView copy = new ImageView(original.getImage());
        copy.setFitWidth(original.getFitWidth());
        copy.setFitHeight(original.getFitHeight());
        copy.setViewport(original.getViewport());
        return copy;
    }

    public void newGame(int width, int height, int mines) {
        board.setGridWidth(width);
        board.setGridHeight(height);
        numberOfMines = mines;
        board.setUpGrids();
        mainView.updateStage();
        resetBoard();
    }

    public void resetBoard() {
		updateButton(mainView.smiley, mainView.smileyImageViews[0]);
		iterateOverAllCells(this::resetBoardHelper);
		timerT.cancel();
        mainView.time = -1;
        mainView.updateLabel();
        mainView.updateStage();
		timerT = new Timer();
		mineCounter = numberOfMines;
        mainView.mineCounterStr = mainView.fillZeroes(mineCounter);
        mainView.scoreboard.setText(mainView.mineCounterStr);
        board.emptyCells.clear();
        board.nonEmptyCells.clear();
        board.cellsExposed.clear();
		for (int r = 0; r < board.gridWidth; r++) {
			Arrays.fill(board.mineFrequencies[r], 0);
			Arrays.fill(board.flags[r], false);
			Arrays.fill(board.minesLocations[r], false);
		}
		firstTime = true;
		gameOver = false;
		gameWon = false;
    }

    private void resetBoardHelper(int i, int j) {
		updateButton(board.tiles[i][j], boardView.tileImageViews[9]);
    }


    private void populateMineFrequencies() {
        iterateOverAllCells(this::populateMineFrequenciesHelper);
    }

    private void populateMineFrequenciesHelper(int i, int j) {
        AtomicInteger mineCount = new AtomicInteger(0);
		getNeighbouringCells(i, j, (_, coordinates) -> {
			if (board.minesLocations[coordinates.get(0)][coordinates.get(1)]) {
				mineCount.incrementAndGet();
			}
		});
        if (board.minesLocations[i][j]) {
            board.mineFrequencies[i][j] = -1;
        } else {
            board.mineFrequencies[i][j] = mineCount.get();
        }
    }

    private void collectEmptyCells(int i, int j) {
		ArrayList<ArrayList<Integer>> cells = getNeighbouringCells(i, j, (a, b) -> board.checkCellEmpty(a, b));
		for (ArrayList<Integer> cell: cells)
		{
			collectEmptyCells(cell.get(0), cell.get(1));
		}
    }

    private void collectNonEmptyCells() {
        for (ArrayList<Integer> cell: board.emptyCells) {
			getNeighbouringCells(cell.get(0), cell.get(1), (a, b) -> board.checkCellNotEmpty(a, b));
        }
    }

    private ArrayList<ArrayList<Integer>> getNeighbouringCells(int i, int j, BiConsumer<ArrayList<ArrayList<Integer>>, ArrayList<Integer>> action) {
        ArrayList<ArrayList<Integer>> cells = new ArrayList<>();
        for (int[] offset : NEIGHBOR_OFFSETS) {
            int neighbourX = i + offset[0];
            int neighbourY = j + offset[1];
            if (board.isValidNeighbour(neighbourX, neighbourY)) {
                ArrayList<Integer> cell = board.setUpCell(neighbourX, neighbourY);
                action.accept(cells, cell);
            }
        }
        return cells;
    }

    public void setUpTile(int i, int j) {
        ArrayList<Integer> cell = board.setUpCell(i, j);
        board.tiles[i][j].setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                if(!gameOver && !gameWon && !board.flags[i][j]) {
                    if(firstTime) {
                        board.generateMines(i, j, numberOfMines);
                        populateMineFrequencies();
                        firstTime = false;
                    }
                    if(board.minesLocations[i][j]) {
                        gameOver(i, j);
                    }
                    else if(!board.cellsExposed.contains(cell)) {
                        ImageView thisImage = board.updateImageView(i, j);
                        board.flags[i][j] = false;
                        board.validAdd(board.cellsExposed, cell);
                        if(board.mineFrequencies[i][j] == 0) {
                            board.emptyCells.clear();
                            board.mineFrequencies[i][j] = -1;
                            collectEmptyCells(i, j);
                            collectNonEmptyCells() ;
                            for (ArrayList<Integer> thisCell: board.emptyCells)
                            {
                                int x = thisCell.get(0);
                                int y = thisCell.get(1);
                                updateButton(board.tiles[x][y], boardView.updateTile(x, y));
                                board.validAdd(board.cellsExposed, thisCell);
                            }
                            for (ArrayList<Integer> nonEmpty: board.nonEmptyCells) {
                                int x = nonEmpty.get(0);
                                int y = nonEmpty.get(1);
                                updateButton(board.tiles[x][y], boardView.updateTile(x, y));
                                board.validAdd(board.cellsExposed, nonEmpty);
                            }
                        }
                        updateButton(board.tiles[i][j], boardView.updateTile(i, j));
                    }
                }
                checkWon();
            } else if (event.getButton() == MouseButton.SECONDARY) {
                if(!gameOver && !gameWon) {
                    if(!board.cellsExposed.contains(cell) && !board.flags[i][j]) {
                        updateButton(board.tiles[i][j], boardView.tileImageViews[11]);
                        mineCounter--;
                        mainView.mineCounterStr = mainView.fillZeroes(mineCounter);
                        mainView.scoreboard.setText(mainView.mineCounterStr);
                        board.cellsExposed.add(cell);
                        board.flags[i][j] = true;
                    }else if(board.flags[i][j]){
                        updateButton(board.tiles[i][j], boardView.tileImageViews[9]);
                        mineCounter++;
                        mainView.mineCounterStr = mainView.fillZeroes(mineCounter);
                        mainView.scoreboard.setText(mainView.mineCounterStr);
                        board.flags[i][j] = false;
                        board.cellsExposed.remove(cell);
                    }
                }
            }
        });
        board.tiles[i][j].setOnMousePressed(event -> {
            if(!gameOver && !gameWon) {
                if (event.getButton() == MouseButton.PRIMARY) {
                    updateButton(mainView.smiley, mainView.smileyImageViews[1]);
                }
            }
        });
        board.tiles[i][j].setOnMouseReleased(event -> {
            if(!gameOver && !gameWon) {
                if (event.getButton() == MouseButton.PRIMARY) {
                    if (board.minesLocations[i][j]) {
                        if(!board.flags[i][j]) {
                            updateButton(mainView.smiley, mainView.smileyImageViews[3]);
                        }else {
                            updateButton(mainView.smiley, mainView.smileyImageViews[0]);
                        }
                    }
                    else {
                        updateButton(mainView.smiley, mainView.smileyImageViews[0]);
                        if (mainView.time == 0 && firstTime) {
                            timerT.scheduleAtFixedRate(controller.setTimerTask(), 0, 1000);
                        }
                    }
                }
            }
        });
    }
}
