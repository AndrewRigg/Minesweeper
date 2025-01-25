package application.model;

import java.util.*;
import java.util.function.BiConsumer;

import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;

public class Board {

    static final int DEFAULT_TILE_SIZE = 16;
    int tileSize = DEFAULT_TILE_SIZE,
            gridWidth,
            gridHeight;
    GridPane grid;
    public Button [][] tiles;
    int [][] mineFrequencies;
    public boolean [][] minesLocations,
            flags;
    ArrayList<ArrayList<Integer>> nonEmptyCells = new ArrayList<>(),
            emptyCells = new ArrayList<>(),
            mineCoordinates = new ArrayList<>(),
            cellsExposed = new ArrayList<>();
    final static int[][] NEIGHBOR_OFFSETS = {
            {-1, -1}, {0, -1}, {1, -1}, // Top row
            {-1,  0},          {1,  0}, // Middle row (skip centre)
            {-1,  1}, {0,  1}, {1,  1}  // Bottom row
    };
    public Rectangle2D [] tileViewPorts = new Rectangle2D[DEFAULT_TILE_SIZE];


    public Board(int gridWidth, int gridHeight, GridPane grid) {
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.grid = grid;
        setUpGrids();
    }

    public void setUpGrids(){
        System.out.println("gridWidth: " + gridWidth + " gridHeight: " + gridHeight);
        minesLocations = new boolean[gridWidth][gridHeight];
        mineFrequencies = new int[gridWidth][gridHeight];
        flags = new boolean[gridWidth][gridHeight];
        tiles = new Button[gridWidth][gridHeight];
    }

    public GridPane getGridPane(){
        return grid;
    }

    public void checkCellNotEmpty(ArrayList<ArrayList<Integer>> currentCells, ArrayList<Integer> coords) {
        int a = coords.get(0);
        int b = coords.get(1);
        if (mineFrequencies[a][b] != 0 && mineFrequencies[a][b] != -1) {
            ArrayList<Integer> newCell = setUpCell(a, b);
            currentCells.add(newCell);
            validAdd(nonEmptyCells, newCell);
        }
    }

    public ArrayList<Integer> setUpCell(int i, int j){
        ArrayList<Integer> cell = new ArrayList<>();
        cell.add(i);
        cell.add(j);
        return cell;
    }

    public void generateMines(int x, int y, int numberOfMines) {
        ArrayList<Integer> mines = new ArrayList<>();
        ArrayList<Integer> excluded = new ArrayList<Integer>();
        excluded.add(y * gridWidth + x);
        for (int[] offset : NEIGHBOR_OFFSETS) {
            if (isValidNeighbour(x + offset[0], y + offset[1])) {
                excluded.add((y + offset[1]) * gridWidth + (x + offset[0]));
            }
        }
        mines = getRandomMineExcluding(excluded, numberOfMines);
        for (int i = 0; i < numberOfMines; i++) {
            minesLocations[mines.get(i) % gridWidth][mines.get(i) / gridWidth] = true;
            ArrayList<Integer> mine = setUpCell(mines.get(i) % gridWidth, mines.get(i) / gridWidth);
            validAdd(mineCoordinates, mine);
        }
    }

    private ArrayList<Integer> getRandomMineExcluding(ArrayList<Integer> excluded, int numberOfMines) {
        ArrayList<Integer> mines = new ArrayList<>();
        int mine;
        for (int i = 0; i < numberOfMines; i++) {
            do {
                mine = new Random().nextInt(gridWidth * gridHeight);
            } while (excluded.contains(mine));
            mines.add(mine);
            excluded.add(mine);
        }
        return mines;
    }

    public void resizeBoard(int width, int height, int size) {
        int divider = width > 1.78 * height ? (int)(width / 1.78) : height;
        size = Math.min(900 / divider, 16);
    }

    public boolean validCustom(int width, int height, int mines) {
        return mines + 9 <= width * height;
    }

    public ImageView updateImageView(int i, int j) {
        Image image = new Image("images/tileSprites.png");
        ImageView imageView = new ImageView(image);
		imageView.setFitHeight(tileSize);
		imageView.setFitWidth(tileSize);
		imageView.setViewport(setViewPort(i, j));
        return imageView;
    }

    private Rectangle2D setViewPort(int i, int j) {
        Rectangle2D viewPort = null;
		if (minesLocations[i][j]) {
			viewPort = tileViewPorts[2];
		}
		else if (mineFrequencies[i][j] == 0 || mineFrequencies[i][j] == -1) {
			viewPort = tileViewPorts[3];
		} else {
			viewPort = tileViewPorts[mineFrequencies[i][j]+3];
		}
        return viewPort;
    }

    public void checkCellEmpty(ArrayList<ArrayList<Integer>> currentCells, ArrayList<Integer> coords) {
        int a = coords.get(0);
        int b = coords.get(1);
        if (mineFrequencies[a][b] == 0) {
            ArrayList<Integer> newCell = setUpCell(a, b);
            currentCells.add(newCell);
            if (!emptyCells.contains(newCell)) {
                emptyCells.add(newCell);
                mineFrequencies[a][b] = -1;
            }
        }
    }

    public boolean isValidNeighbour(int i, int j) {
        return i >= 0 && i < gridWidth && j >= 0 && j < gridHeight;
    }

    public void validAdd(ArrayList<ArrayList<Integer>> list, ArrayList<Integer> cell) {
        if(!list.contains(cell)) {
            list.add(cell);
        }
    }

    public int getGridWidth() {
        return gridWidth;
    }

    public void setGridWidth(int gridWidth) {
        this.gridWidth = gridWidth;
    }

    public int getGridHeight() {
        return gridHeight;
    }

    public void setGridHeight(int gridHeight) {
        this.gridHeight = gridHeight;
    }

    public Button[][] getTiles() {
        return tiles;
    }

    public void setTiles(Button[][] tiles) {
        this.tiles = tiles;
    }

    public int[][] getMineFrequencies() {
        return mineFrequencies;
    }

    public void setMineFrequencies(int[][] mineFrequencies) {
        this.mineFrequencies = mineFrequencies;
    }

    public boolean[][] getMinesLocations() {
        return minesLocations;
    }

    public void setMinesLocations(boolean[][] minesLocations) {
        this.minesLocations = minesLocations;
    }
}
