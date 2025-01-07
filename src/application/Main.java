package application;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

import javafx.application.*;
import javafx.collections.ObservableList;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.*;

public class Main extends Application {

	final int gridWidth = 9, gridHeight = 9, tileSize = 20, smileySize = 18, smileySizeBackground = 31, imgPxs = 266,
			smileyPxs = 409, numberOfMines = 10, rows = 5, columns = 10;
	int time = 0, mineCounter = numberOfMines;
	String mineCounterStr = fillZeroes(mineCounter);
	int[][] mineFrequencies = new int[gridWidth][gridHeight];
	ArrayList<ArrayList<Integer>> emptyCells = new ArrayList<ArrayList<Integer>>(), nonEmptyCells = new ArrayList<ArrayList<Integer>>(), cellsExposed = new ArrayList<ArrayList<Integer>>();
	int[] mines = new int[numberOfMines];
	boolean[][] minesLocations = new boolean[gridWidth][gridHeight];
	boolean firstTime = true;
//	boolean[][] zeroesLocations = new boolean[gridWidth][gridHeight];
	Label scoreboard, timer;
	BorderPane bPane;
	GridPane gridPane, scorePane;
	StackPane sPane;
	Button smiley, smileyBackground;
	Button [][] tiles = new Button[gridWidth][gridHeight];
	private static final int[][] NEIGHBOR_OFFSETS = {
	    {-1, -1}, {0, -1}, {1, -1}, // Top row
	    {-1,  0},          {1,  0}, // Middle row (skip centre)
	    {-1,  1}, {0,  1}, {1,  1}  // Bottom row
	};
	Font font = Font.loadFont("file:res/fonts/Segment7Standard.otf", 25);
	TimerTask task;
	Timer timerT;
	Alert alert;

	public void start(Stage primaryStage) {
		generateMines();
		populateMineFrequencies();
		smiley = setUpButton(smileySize, 12, 12);
		smileyBackground = setUpButton(smileySizeBackground, 5, 5);
		setButtonImage(smiley, "smileys.png", smileySize, 0, 0, smileyPxs);
		setButtonImage(smileyBackground, "sprites.jpg", smileySizeBackground, 0, 0, imgPxs);
		timerT = new Timer();
		task = setTimerTask();
		setUpSmiley();
		bPane = new BorderPane();
		sPane = new StackPane();
		scorePane = new GridPane();
		gridPane = new GridPane();
		scoreboard = setUpDisplay(mineCounterStr);
		timer = setUpDisplay(""+fillZeroes(time));
		scoreboard.setBackground(new Background(new BackgroundFill(Color.BLACK, new CornerRadii(0), new Insets(0))));
		timer.setBackground(new Background(new BackgroundFill(Color.BLACK, new CornerRadii(0), new Insets(0))));
		timer.setAlignment(Pos.TOP_RIGHT);
		setUpCells();
		sPane.getChildren().add(smileyBackground);
		sPane.getChildren().add(smiley);
		StackPane.setAlignment(smileyBackground, Pos.TOP_LEFT);
		StackPane.setAlignment(smiley, Pos.TOP_LEFT);
		scorePane.add(scoreboard, 0, 0);
		scorePane.add(sPane, 1, 0);
		sPane.setAlignment(Pos.TOP_RIGHT);
		scorePane.setHgap(25);
		scorePane.setAlignment(Pos.CENTER);
		scorePane.add(timer, 2, 0);
		ColumnConstraints cc = new ColumnConstraints();
		cc.setPercentWidth(100 / 3);
		scorePane.getColumnConstraints().add(cc);
		scorePane.getColumnConstraints().add(cc);
		scorePane.getColumnConstraints().add(cc);
		bPane.setTop(scorePane);
		bPane.setCenter(gridPane);
		Scene scene = new Scene(bPane, tileSize * gridWidth + 2 * 5,
				tileSize * gridHeight + 2 * 5 + smileySizeBackground + 5);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.setTitle("Minesweeper");
		primaryStage.getIcons().add(new Image("mine.jpg"));
		primaryStage.show();
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent t) {
				Platform.exit();
				System.exit(0);
			}
		});
	}
	
	private void setUpCells() {
		iterateOverAllCells((i, j) -> setUpButtonsFirstTime(i, j));
	}
	
	private void resetBoard() {
		gridPane.getChildren().clear();
		iterateOverAllCells((i, j) -> resetBoardHelper(i, j));
	}
	
	private void setUpButtonsFirstTime(int i, int j) {
		tiles[i][j] = setUpButton(tileSize, 5, 10);
		setButtonImage(tiles[i][j], "sprites.jpg", tileSize, 0, 0, imgPxs);
		setUpTile(i, j);
		gridPane.add(tiles[i][j], i, j);
	}
	
	private void iterateOverAllCells(BiConsumer<Integer, Integer> action) {
		for (int i = 0; i < gridWidth; i++) {
			for (int j = 0; j < gridHeight; j++) {
				action.accept(i, j);
			}
		}
	}
	
	private Label setUpDisplay(String str) {
		Label label = new Label(str);
		label.setTextFill(Color.RED);
		label.setFont(font);
		label.setTranslateX(5);
		label.setTranslateY(5);
		return label;
	}

	private void setUpSmiley() {
		setButtonImage(smiley, "smileys.png", smileySize, 0, 0, smileyPxs);
		setButtonImage(smileyBackground, "sprites.jpg", smileySizeBackground, 0, 0, imgPxs);
		smiley.setOnMouseClicked(event -> {
			System.out.println("Mouse Clicked, resetting board...");
			if (event.getButton() == MouseButton.SECONDARY) {
				gameComplete();
			}
		});
		smiley.setOnMousePressed(event -> {
//			System.out.println("Mouse Pressed");
			if (event.getButton() == MouseButton.PRIMARY) {
				setButtonImage(smileyBackground, "sprites.jpg", smileySizeBackground, 3 * imgPxs, 0, imgPxs);
			}
		});
		smiley.setOnMouseReleased(event -> {
//			System.out.println("Mouse Released");
			if (event.getButton() == MouseButton.PRIMARY) {
				setButtonImage(smileyBackground, "sprites.jpg", smileySizeBackground, 0, 0, imgPxs);
				timerT.cancel();
				time = 0;
				timer.setText(fillZeroes(time));
				resetBoard();
			}
		});
		smileyBackground.setOnMousePressed(event -> {
//			System.out.println("Mouse Pressed");
			if (event.getButton() == MouseButton.PRIMARY) {
				setButtonImage(smileyBackground, "sprites.jpg", smileySizeBackground, 3 * imgPxs, 0, imgPxs);
			}
		});
		smileyBackground.setOnMouseReleased(event -> {
			System.out.println("Mouse Released");
			if (event.getButton() == MouseButton.PRIMARY) {
				setButtonImage(smileyBackground, "sprites.jpg", smileySizeBackground, 0, 0, imgPxs);
			}
		});
	}

	TimerTask setTimerTask() {
		task = new TimerTask() {
			public void run() {
				Platform.runLater(new Runnable() {
					public void run() {
						updateLabel();
					}
				});
			}
		};
		return task;
	}

	public void updateLabel() {
		if (time < 999) {
			time++;
		}
		timer.setText(fillZeroes(time));
	}

	private String fillZeroes(int mineCounter) {
		String leadingZeroes;
		if (mineCounter >= 100 || mineCounter < 0) {
			leadingZeroes = "";
		} else if (mineCounter >= 10) {
			leadingZeroes = "0";
		} else {
			leadingZeroes = "00";
		}
		return leadingZeroes + mineCounter;
	}

	public Button setUpButton(int buttonSize, int x, int y) {
		Button button = new Button();
		button.setMaxSize(buttonSize, buttonSize);
		button.setMinSize(buttonSize, buttonSize);
		button.setTranslateX(x);
		button.setTranslateY(y);
		button.setStyle("-fx-focus-color: transparent;");
		button.setStyle("-fx-faint-focus-color: transparent;");
		button.setStyle("-fx-background-color: transparent;");
		return button;
	}

	public void setButtonImage(Button button, String path, int buttonSize, int x, int y, int size) {
		Image image = new Image(path);
		ImageView imageView = new ImageView(image);
		Rectangle2D viewPort = new Rectangle2D(x, y, size, size);
		imageView.setFitWidth(buttonSize);
		imageView.setFitHeight(buttonSize);
		imageView.setViewport(viewPort);
		button.setGraphic(imageView);
	}

	public Node getNodeByRowColumnIndex(final int row, final int column) {
		Node result = null;
		ObservableList<Node> childrens = gridPane.getChildren();
		for (Node node : childrens) {
			if (gridPane.getRowIndex(node) == row && gridPane.getColumnIndex(node) == column) {
				result = node;
				break;
			}
		}
		return result;
	}

	private void resetBoardHelper(int i, int j) {
		Button tile = setUpButton(tileSize, rows, columns);
		setButtonImage(tile, "sprites.jpg", tileSize, 0, 0, imgPxs);
		gridPane.add(tile, i, j);
	}
	
	private ArrayList<Integer> setUpCell(int i, int j){
		ArrayList<Integer> cell = new ArrayList<Integer>();
		cell.add(i);
		cell.add(j);
		return cell;
	}
	
	private ArrayList<Integer> setUpCell(int i, int j, ArrayList<Integer> cell){
		cell.add(i);
		cell.add(j);
		return cell;
	}
	
	private void setUpTile(int i, int j) {
		ArrayList<Integer> cell = setUpCell(i, j);
		ImageView thisImage = updateImageView(i, j);
		tiles[i][j].setOnMouseClicked(event -> {
			System.out.println("This cell: (" + i + ", " + j + ")");
			System.out.println("Cell in Exposed Cells: " + cellsExposed.contains(cell));
			System.out.println("Exposed Cell:");
			for (ArrayList<Integer> exposed: cellsExposed)
			{
				System.out.println(" (" + exposed.get(0) + ", " + exposed.get(1) + ")");
			}
			if (event.getButton() == MouseButton.PRIMARY) {
				validAdd(cellsExposed, cell);
//				if(firstTime) {
//					generateMines();
//					populateMineFrequencies();
//					setUpCells();
//					firstTime = false;
//				}
				if(mineFrequencies[i][j] == 0) {
					emptyCells.clear();
					mineFrequencies[i][j] = -1;
					collectEmptyCells(i, j); 
					collectNonEmptyCells() ;
//					System.out.println("All elements in connected group of empty cells:");
					for (ArrayList<Integer> thisCell: emptyCells)
					{
//						System.out.println(" (" + thisCell[0] + ", " + thisCell[1] + ")");
						tiles[thisCell.get(0)][thisCell.get(1)].setGraphic(updateImageView(thisCell.get(0), thisCell.get(1)));
						validAdd(cellsExposed, thisCell);
					}
//					System.out.println("All elements in connected group of non-empty cells:");
					for (ArrayList<Integer> nonEmpty: nonEmptyCells) {
//						System.out.println(" (" + nonEmpty[0] + ", " + nonEmpty[1] + ")");
						tiles[nonEmpty.get(0)][nonEmpty.get(1)].setGraphic(updateImageView(nonEmpty.get(0), nonEmpty.get(1)));
						validAdd(cellsExposed, nonEmpty);
					}
				}
				tiles[i][j].setGraphic(thisImage);
			} else if (event.getButton() == MouseButton.SECONDARY) {
				if(!cellsExposed.contains(cell)) {
					setButtonImage(tiles[i][j], "sprites.jpg", tileSize, imgPxs, 0, imgPxs);
					mineCounter--;
					System.out.println("Mines: " + mineCounter);
					mineCounterStr = fillZeroes(mineCounter);
					scoreboard.setText(mineCounterStr);
					cellsExposed.add(cell);
				}
			}
		});
		tiles[i][j].setOnMousePressed(event -> {
//			System.out.println("Mouse Pressed");
			if (event.getButton() == MouseButton.PRIMARY) {
				setButtonImage(smiley, "smileys.png", smileySize, smileyPxs, 0, smileyPxs);
				if (firstTime) {
					setButtonImage(tiles[i][j], "sprites.jpg", tileSize, 3 * imgPxs, 0, imgPxs);
				}
			}
		});
		tiles[i][j].setOnMouseReleased(event -> {
//			System.out.println("Mouse Released");
			if (event.getButton() == MouseButton.PRIMARY) {
				if (minesLocations[i][j]) {
					setButtonImage(smiley, "smileys.png", smileySize, smileyPxs, smileyPxs, smileyPxs);
				} 
				else {
					setButtonImage(smiley, "smileys.png", smileySize, 0, 0, smileyPxs);
					if (time == 0) {
						timerT.scheduleAtFixedRate(task, 2, 1000);
					}
				}
			}
		});
	}

	private ImageView updateImageView(int i, int j) {
		Image image = new Image("sprites.jpg");
		ImageView imageView = new ImageView(image);
		imageView.setFitHeight(tileSize);
		imageView.setFitWidth(tileSize);
		imageView.setViewport(setViewPort(i, j));
		return imageView;
	}
	
	private Rectangle2D setViewPort(int i, int j) {
		Rectangle2D viewportRect2;
		if (minesLocations[i][j]) {
			viewportRect2 = setTileRectangle(2, 0);
		} 
		else if (mineFrequencies[i][j] == 0 || mineFrequencies[i][j] == -1) {
			viewportRect2 = setTileRectangle(3, 0);
		} else {
			viewportRect2 = new Rectangle2D(((mineFrequencies[i][j] - 1) % 4) * imgPxs, (1 + (mineFrequencies[i][j] - 1) / 4) * imgPxs, imgPxs, imgPxs);
		}
		return viewportRect2;
	}
	
	private void collectEmptyCells(int i, int j) {
		ArrayList<ArrayList<Integer>> cells = getNeighbouringCells(i, j, (a, b) -> checkCellEmpty(a, b));
		for (ArrayList<Integer> cell: cells)
		{
			collectEmptyCells(cell.get(0), cell.get(1));
		}
	}
	
	private void collectNonEmptyCells() {
		for (ArrayList<Integer> cell: emptyCells) {
			ArrayList<ArrayList<Integer>> cells = getNeighbouringCells(cell.get(0), cell.get(1), (a, b) -> checkCellNotEmpty(a, b));
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
	
	private void validAdd(ArrayList<ArrayList<Integer>> list, ArrayList<Integer> cell) {
		if(!list.contains(cell)) {
			list.add(cell);
		}
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
	
	private boolean isValidNeighbour(int i, int j) {
		return i >= 0 && i < gridWidth && j >= 0 && j < gridHeight;
	}
	
	private ArrayList<ArrayList<Integer>> getNeighbouringCells(int i, int j, BiConsumer<ArrayList<ArrayList<Integer>>, ArrayList<Integer>> action) {
		ArrayList<ArrayList<Integer>> cells = new ArrayList<>();
		for (int[] offset : NEIGHBOR_OFFSETS) {
			int neighbourX = i + offset[0];
			int neighbourY = j + offset[1];
				if (isValidNeighbour(neighbourX, neighbourY)) {
//				if (!(x == 0 && y == 0) && (i + x) >= 0 && (i + x) < gridWidth && (j + y) >= 0 && (j + y) < gridHeight) {
					ArrayList<Integer> cell = setUpCell(neighbourX, neighbourY);
					action.accept(cells, cell);
				}
		}
		return cells;
	}
	
//	private ArrayList<int[]> getNeighbouringCells(int i, int j, BiConsumer<ArrayList<int[]>, ArrayList<Integer>> action) {
//		ArrayList<int[]> cells = new ArrayList<>();
//		for (int y = -1; y <= 1; y++) {
//			for (int x = -1; x <= 1; x++) {
//				if (isValidNeighbour(i, j, x, y)) {
////				if (!(x == 0 && y == 0) && (i + x) >= 0 && (i + x) < gridWidth && (j + y) >= 0 && (j + y) < gridHeight) {
//					ArrayList<Integer> cell = new ArrayList<Integer>();
//					cell.add(i+x);
//					cell.add(j+y);
//					action.accept(cells, cell);
//				}
//			}			
//		}
//		return cells;
//	}
	
	private Rectangle2D setTileRectangle(int i, int j) {
		return new Rectangle2D(i * imgPxs, j * imgPxs, imgPxs, imgPxs);
	}

	private void generateMines() {
		Random random = new Random();
		mines = random.ints(0, gridWidth * gridHeight).distinct().limit(numberOfMines).toArray();
		for (int i = 0; i < mines.length; i++) {
			minesLocations[mines[i] % gridWidth][mines[i] / gridWidth] = true;
		}
	}
	
	private void populateMineFrequencies() {
		iterateOverAllCells((i, j) -> populateMineFrequenciesHelper(i, j));
	}

	private void populateMineFrequenciesHelper(int i, int j) {
	    AtomicInteger mineCount = new AtomicInteger(0);
		getNeighbouringCells(i, j, (cells, coords) -> {
			if (minesLocations[coords.get(0)][coords.get(1)]) {
				mineCount.incrementAndGet();
			}
		});
		if (minesLocations[i][j]) {
			mineFrequencies[i][j] = -1;
		} else {
			mineFrequencies[i][j] = mineCount.get();
		}
	}

	private void gameComplete() {
		setButtonImage(smiley, "smileys.png", smileySize, 0, smileyPxs, smileyPxs);
	}

	public static void main(String[] args) {
		launch(args);
	}
}
