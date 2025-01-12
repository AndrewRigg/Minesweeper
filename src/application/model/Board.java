package application.model;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

import javafx.scene.control.Button;

public class Board {

	int gridWidth;
	int gridHeight;
	public Button 	[][] tiles;
	int 	[][] mineFrequencies;
	public boolean	[][] minesLocations;
	public boolean	[][] flags;
	ArrayList<ArrayList<Integer>> nonEmptyCells = new ArrayList<ArrayList<Integer>>();
	ArrayList<ArrayList<Integer>> emptyCells = new ArrayList<ArrayList<Integer>>();

	public Board(int gridWidth, int gridHeight) {
		this.gridWidth = gridWidth;
		this.gridHeight = gridHeight;
		
		minesLocations = new boolean[gridWidth][gridHeight];
		mineFrequencies = new int[gridWidth][gridHeight];
		flags = new boolean[gridWidth][gridHeight];
		tiles = new Button[gridWidth][gridHeight];
	}

	private void checkCellNotEmpty(ArrayList<ArrayList<Integer>> currentCells, ArrayList<Integer> coords) {
		int a = coords.get(0);
		int b = coords.get(1);
		if (mineFrequencies[a][b] != 0 && mineFrequencies[a][b] != -1) {
			ArrayList<Integer> newCell = setUpCell(a, b);
			currentCells.add(newCell);
			validAdd(nonEmptyCells, newCell);
		}
	}
	
	private ArrayList<Integer> setUpCell(int i, int j){
		ArrayList<Integer> cell = new ArrayList<Integer>();
		cell.add(i);
		cell.add(j);
		return cell;
	}
	

	
	private void iterateOverAllCells(BiConsumer<Integer, Integer> action) {
		for (int i = 0; i < gridWidth; i++) {
			for (int j = 0; j < gridHeight; j++) {
				action.accept(i, j);
			}
		}
	}
	
	private void populateMineFrequencies() {
		iterateOverAllCells((i, j) -> populateMineFrequenciesHelper(i, j));
	}
	
	private void populateMineFrequenciesHelper(int i, int j) {
	    AtomicInteger mineCount = new AtomicInteger(0);
//		getNeighbouringCells(i, j, (cells, coords) -> {
//			if (minesLocations[coords.get(0)][coords.get(1)]) {
//				mineCount.incrementAndGet();
//			}
//		});
		if (minesLocations[i][j]) {
			mineFrequencies[i][j] = -1;
		} else {
			mineFrequencies[i][j] = mineCount.get();
		}
	}
	
	private void checkCellEmpty(ArrayList<ArrayList<Integer>> currentCells, ArrayList<Integer> coords) {
		int a = coords.get(0);
		int b = coords.get(1);
		if (mineFrequencies[a][b] == 0) {
			ArrayList<Integer> newCell = setUpCell(a, b);
			currentCells.add(newCell);
			if(!emptyCells.contains(newCell))
			{
				emptyCells.add(newCell);
				mineFrequencies[a][b] = -1;
			}
		}
	}

	private void collectEmptyCells(int i, int j) {
//		ArrayList<ArrayList<Integer>> cells = getNeighbouringCells(i, j, (a, b) -> checkCellEmpty(a, b));
//		for (ArrayList<Integer> cell: cells)
//		{
//			collectEmptyCells(cell.get(0), cell.get(1));
//		}
	}
	
	private void collectNonEmptyCells() {
		for (ArrayList<Integer> cell: emptyCells) {
//			getNeighbouringCells(cell.get(0), cell.get(1), (a, b) -> checkCellNotEmpty(a, b));
		}
	}
	
	private void validAdd(ArrayList<ArrayList<Integer>> list, ArrayList<Integer> cell) {
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
