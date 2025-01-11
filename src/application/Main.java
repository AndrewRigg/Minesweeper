package application;

import java.util.ArrayList;
import java.util.Arrays;
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

	final static int DEFAULT_TILE_SIZE = 16;
	final static int DEFAULT_NO_OF_MINES = 40;
	int gridWidth = DEFAULT_TILE_SIZE;
	int gridHeight = DEFAULT_TILE_SIZE;
	int tileSize = DEFAULT_TILE_SIZE;
	int numberOfMines = DEFAULT_NO_OF_MINES;
	int smileySize = 18;
	int smileySizeBackground = 31;
	int imgPxs = 32;
	int smileyPxs = 409;
	int rows = 5;
	int columns = 10;
	int time = 0, mineCounter = numberOfMines;
	int customWidth = 16, customHeight = 16, customMines = 40;
	String mineCounterStr = fillZeroes(mineCounter);
	ArrayList<ArrayList<Integer>> mineCoords = new ArrayList<ArrayList<Integer>>();
	ArrayList<ArrayList<Integer>> emptyCells = new ArrayList<ArrayList<Integer>>(), nonEmptyCells = new ArrayList<ArrayList<Integer>>(), cellsExposed = new ArrayList<ArrayList<Integer>>();
	boolean[][] flags = new boolean[gridWidth][gridHeight], minesLocations = new boolean[gridWidth][gridHeight];
	int[][] mineFrequencies = new int[gridWidth][gridHeight];
	boolean firstTime = true;
	boolean gameOver = false, gameWon = false;
	Label scoreboard, timer;
	BorderPane bPane;
	GridPane gridPane;
	BorderPane scorePane;
	StackPane sPane;
	Button smiley, smileyBackground;
	Button [][] tiles = new Button[gridWidth][gridHeight];
	private static final int[][] NEIGHBOR_OFFSETS = {
	    {-1, -1}, {0, -1}, {1, -1}, // Top row
	    {-1,  0},          {1,  0}, // Middle row (skip centre)
	    {-1,  1}, {0,  1}, {1,  1}  // Bottom row
	};
	Font font = Font.loadFont("file:res/fonts/Segment7Standard.otf", 25);
//	TimerTask task;
	Timer timerT;
	Alert alert;
	
	final static int SMILEYS = 4, SMILEYS_ROW = 2;
	final static int SPRITES = 16, SPRITES_ROW = 4;
	final static int BACKGROUNDS = 2;
	final static int maxWidth = 99, maxHeight = 99, maxMines = 9800;
	
	Rectangle2D [] smileyViewPorts = new Rectangle2D[SMILEYS];
	Rectangle2D [] tileViewPorts = new Rectangle2D[SPRITES];
	Image smileyImages = new Image("smileys.png");
	Image tileImages = new Image("tileSprites.png");
	ImageView [] smileyImageViews = new ImageView[SMILEYS];
	ImageView [] tileImageViews = new ImageView[SPRITES];
	ImageView [] smileyBackgroundViews = new ImageView[BACKGROUNDS];
	private Stage mainStage;	
	Popup customPopup = new Popup();
	
	
	public void start(Stage primaryStage) {
				
		this.mainStage = primaryStage;
		MenuBar mb = setUpMenus();
		initialiseViewPorts();
		initialiseImageViews();
		smiley = setUpButton(smileySize);
		smileyBackground = setUpButton(smileySizeBackground);
		updateButton(smiley, smileyImageViews[0]);
		updateButton(smileyBackground, smileyBackgroundViews[0]);
		timerT = new Timer();
		setUpSmiley();

		
		// Outer pane (light 3D edge)
		StackPane outerPane = new StackPane();
		outerPane.setStyle(
		    "-fx-background-color: #C0C0C0;" +
		    "-fx-border-style: solid;" +
		    // White top/left, dark gray bottom/right
		    "-fx-border-color: white gray gray white;" +
		    "-fx-border-width: 3;"
		);

		// Inner pane (slightly darker edge inside)
		StackPane innerPane = new StackPane();
		innerPane.setStyle(
		    "-fx-border-style: solid;" +
		    // Mid-gray top/left, white bottom/right
		    "-fx-border-color: #808080 #FFFFFF #FFFFFF #808080;" +
		    "-fx-border-width: 2;" +
		    "-fx-background-color: #C0C0C0;" 
		);
		outerPane.getChildren().add(innerPane);
		bPane = new BorderPane();
		sPane = new StackPane();
		scorePane = new BorderPane();
		gridPane = new GridPane();
		scoreboard = setUpDisplay(mineCounterStr);
		timer = setUpDisplay(""+fillZeroes(time));
		scoreboard.setBackground(new Background(new BackgroundFill(Color.BLACK, new CornerRadii(0), new Insets(0))));
		timer.setBackground(new Background(new BackgroundFill(Color.BLACK, new CornerRadii(0), new Insets(0))));
		setUpCells();
		sPane.getChildren().add(smileyBackground);
		sPane.getChildren().add(smiley);
		StackPane.setAlignment(smileyBackground, Pos.CENTER);
		StackPane.setAlignment(smiley, Pos.CENTER);
		scorePane.setLeft(scoreboard);
		scorePane.setCenter(sPane);
		scorePane.setRight(timer);
		
		ColumnConstraints cc = new ColumnConstraints();
		VBox topContainer = new VBox();
		topContainer.getChildren().addAll(mb, scorePane);
		
		bPane.setTop(topContainer);
		bPane.setCenter(gridPane);		
		
		innerPane.getChildren().add(bPane);
		Scene scene = new Scene(outerPane);
		
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setResizable(true);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Minesweeper");
		primaryStage.getIcons().add(new Image("Minesweeper.jpg"));
//		primaryStage.initStyle(StageStyle.UNDECORATED);
		primaryStage.show();
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent t) {
				Platform.exit();
				System.exit(0);
			}
		});
	}
	
	private MenuBar setUpMenus() {
		Menu game = new Menu("_Game");
		game.setMnemonicParsing(true);
		MenuItem newGame = new MenuItem("_New"); 
		newGame.setMnemonicParsing(true);
		newGame.setAccelerator(new KeyCodeCombination(KeyCode.F2));
		
		ToggleGroup difficulty = new ToggleGroup();
        RadioMenuItem beginner = new RadioMenuItem("_Beginner");
        beginner.setSelected(true);
        beginner.setMnemonicParsing(true);

        RadioMenuItem intermediate = new RadioMenuItem("_Intermediate");
        intermediate.setSelected(false);
        intermediate.setMnemonicParsing(true);

        RadioMenuItem expert = new RadioMenuItem("_Expert");
        expert.setSelected(false);
        expert.setMnemonicParsing(true);

        RadioMenuItem custom = new RadioMenuItem("_Custom...");
        custom.setSelected(false);
        custom.setMnemonicParsing(true);
        
        beginner.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
            	newGame(9, 9, 10);
            }
        });
        intermediate.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
            	newGame(16, 16, 40);
            }
        });
        expert.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
            	newGame(30, 16, 99);
            }
        });
        custom.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
            	showCustomDialog();
            }
        });
        beginner.setToggleGroup(difficulty);
        intermediate.setToggleGroup(difficulty);
        expert.setToggleGroup(difficulty);
        custom.setToggleGroup(difficulty);
        
        MenuItem marks = new MenuItem("_Marks (?)");
        marks.setMnemonicParsing(true);
        MenuItem color = new MenuItem("_Color");
        color.setMnemonicParsing(true);
        MenuItem sound = new MenuItem("_Sound");
        sound.setMnemonicParsing(true);
        MenuItem times = new MenuItem("Best _Times...");
        times.setMnemonicParsing(true);
        MenuItem exit = new MenuItem("E_xit");
        exit.setMnemonicParsing(true);
        
    	newGame.setOnAction(e -> {
    		resetBoard();
    	});
        
        exit.setOnAction(e -> {
			Platform.exit();
			System.exit(0);
        });
        
		Menu help = new Menu("_Help");
		help.setMnemonicParsing(true);
        MenuItem index = new MenuItem("_Index"); 
        index.setMnemonicParsing(true);
		MenuItem play = new MenuItem("_How to Play"); 
		index.setAccelerator(new KeyCodeCombination(KeyCode.F1));

		play.setMnemonicParsing(true);
		MenuItem commands = new MenuItem("_Commands"); 
		commands.setMnemonicParsing(true);
		MenuItem usingHelp = new MenuItem("Using _Help"); 
		usingHelp.setMnemonicParsing(true);
		MenuItem about = new MenuItem("_About Minesweeper..."); 

		about.setMnemonicParsing(true);
        game.getItems().addAll(newGame, beginner, intermediate, expert, custom, marks, color, sound, times, exit); 
        
        help.getItems().addAll(index, play, commands, usingHelp, about);
  
	    SeparatorMenuItem sep = new SeparatorMenuItem();
	    SeparatorMenuItem sep2 = new SeparatorMenuItem();
	    SeparatorMenuItem sep3 = new SeparatorMenuItem();
	    SeparatorMenuItem sep4 = new SeparatorMenuItem();
	    SeparatorMenuItem sep5 = new SeparatorMenuItem();

	    game.getItems().add(1, sep);
	    game.getItems().add(6, sep2);
	    game.getItems().add(10, sep3);
	    game.getItems().add(12, sep4);
	    
	    help.getItems().add(4, sep5);
        MenuBar mb = new MenuBar();
        mb.getMenus().addAll(game, help);
        return mb;
	}
	
	
	private void showCustomDialog() {
		
		Stage dialog = new Stage();
		dialog.setTitle("Custom Field");
		dialog.initModality(Modality.APPLICATION_MODAL);
		
		GridPane gp = new GridPane();
		gp.setHgap(10);
		gp.setVgap(10);
		gp.setPadding(new Insets(10));
		
		Label widthLabel = new Label("_Width:");
		widthLabel.setMnemonicParsing(true);
		Label heightLabel = new Label("_Height:");
		heightLabel.setMnemonicParsing(true);
		Label minesLabel = new Label("_Mines:");
		minesLabel.setMnemonicParsing(true);
		TextField widthField = new TextField("" + customWidth);
		TextField heightField = new TextField("" + customHeight);
		TextField minesField = new TextField("" + customMines);
		widthField.setMaxWidth(50);
		heightField.setMaxWidth(50);
		minesField.setMaxWidth(50);
		minesField.setOnMouseClicked(e -> minesField.setStyle("-fx-text-fill: black;"));
		widthField.setTextFormatter(setTextFormat(maxWidth));
		heightField.setTextFormatter(setTextFormat(maxHeight));
		minesField.setTextFormatter(setTextFormat(maxMines));
		
		Button okButton = new Button("OK");
		Button cancelButton = new Button("Cancel");

		cancelButton.setOnAction(e -> {
			dialog.close();
        });
		
		okButton.setOnAction(e -> {
			customWidth = Integer.valueOf(widthField.getText());
			customHeight = Integer.valueOf(heightField.getText());
			customMines = Integer.valueOf(minesField.getText());
			if(validCustom()) {
				resizeBoard();
				initialiseImageViews();
				newGame(customWidth, customHeight, customMines);
				dialog.close();
			}else {
				System.out.println("Invalid custom parameters");
				minesField.setStyle("-fx-text-fill: red;");
			}
		});
		
		HBox buttonBox = new HBox(10, okButton, cancelButton);

		
		gp.add(heightLabel, 0, 0);
		gp.add(widthLabel, 0, 1);
		gp.add(minesLabel, 0, 2);
		gp.add(heightField, 1, 0);
		gp.add(widthField, 1, 1);
		gp.add(minesField, 1, 2);
		gp.add(okButton, 2, 0);
		gp.add(cancelButton, 2, 1);
		
		Scene scene = new Scene(gp, 200, 150);
		dialog.setScene(scene);
		dialog.showAndWait();
	
	}
	
	private void resizeBoard() {
		int divider = customWidth > 1.78 * customHeight ? (int)(customWidth / 1.78) : customHeight;
		if(900 / divider < 16) {
			tileSize = 900 / divider;
		}else {
			tileSize = 16;
		}
	}
	
	private boolean validCustom() {
		return customMines + 9 <= customWidth * customHeight;
	}
	
	
	private TextFormatter<Object> setTextFormat(int max) {
		return new TextFormatter<>(change -> {
		    // Only allow digits
		    String newText = change.getControlNewText();
		    if (!newText.matches("\\d*")) {
		        return null; // Reject non-digit input
		    }
		    // Enforce numeric range, e.g. 1–99
		    try {
		    	if (newText.length() > String.valueOf(max).length()) {
		    		return null;
		    	}
		        int value = Integer.parseInt(newText);
		        if (value < 1 || value > max) {
		            return null; // Reject out-of-range
		        }
		    } catch (NumberFormatException e) {
		        // Empty string or invalid number
		        if (!newText.isEmpty()) {
		            return null;
		        }
		    }
		    return change; // Accept
		});
	}
	
	private void newGame(int width, int height, int mines) {
    	gridWidth = width;
    	gridHeight = height;
    	numberOfMines = mines;
    	this.minesLocations = new boolean[width][height];
    	this.flags = new boolean[width][height];
    	this.mineFrequencies = new int[width][height];
    	this.tiles = new Button[width][height];
    	bPane.getChildren().remove(gridPane);
    	gridPane = new GridPane();
    	setUpCells();
    	bPane.setCenter(gridPane);
    	Scene scene = bPane.getScene();
    	System.out.println("scene x: " + scene.getX() + " scene y: " + scene.getY());
       	mainStage.sizeToScene();
    	resetBoard();
	}
	
	private void initialiseViewPorts() {
		for(int i = 0; i < SMILEYS; i++) {
			Rectangle2D smiley = setTileRectangle(i % SMILEYS_ROW, i / SMILEYS_ROW, smileyPxs);
			smileyViewPorts[i] = smiley;
		}
	
		for(int j = 0; j < SPRITES; j++) {
			Rectangle2D sprite = setTileRectangle(j % SPRITES_ROW, j / SPRITES_ROW, imgPxs);
			tileViewPorts[j] = sprite;
		}
	}
	
	private void initialiseImageViews() {
		for (int i = 0; i < SMILEYS; i++) {
			smileyImageViews[i] = setImageView(smileyImages, smileySize, smileyViewPorts[i]);
		}
		for (int i = 0; i < SPRITES; i++) {
			tileImageViews[i] = setImageView(tileImages, tileSize, tileViewPorts[i]);
		}
		smileyBackgroundViews[0] = setImageView(tileImages, smileySizeBackground, tileViewPorts[9]);
		smileyBackgroundViews[1] = setImageView(tileImages, smileySizeBackground, tileViewPorts[0]);
	}
	
	private ImageView setImageView(Image image, int size, Rectangle2D viewPort) {
		ImageView imageView = setImageView(image, size);
		imageView.setViewport(viewPort);
		return imageView;
	}
	
	private ImageView setImageView(Image image, int size) {
		ImageView imageView = new ImageView(image);
		imageView.setFitWidth(size);
		imageView.setFitHeight(size);
		return imageView;
	}
	
	public Image setImage(String path) {
		Image image = new Image(path);
		return image;
	}
	
	public ImageView setImageView(String path, int buttonSize, int x, int y, int size) {
		Image image = new Image(path);
		ImageView imageView = new ImageView(image);
		Rectangle2D viewPort = new Rectangle2D(x, y, size, size);
		imageView.setFitWidth(buttonSize);
		imageView.setFitHeight(buttonSize);
		imageView.setViewport(viewPort);
		return imageView;
	}
	
	public static ImageView copyImageView(ImageView original) {
	    ImageView copy = new ImageView(original.getImage());
	    copy.setFitWidth(original.getFitWidth());
	    copy.setFitHeight(original.getFitHeight());
	    copy.setViewport(original.getViewport());
	    return copy;
	}
	
	private void updateButton (Button button, ImageView imageView) {
		button.setGraphic(copyImageView(imageView));
	}
		
	private Rectangle2D setTileRectangle(int i, int j, int pxs) {
		return new Rectangle2D(i * pxs, j * pxs, pxs, pxs);
	}
	
	
	private void setUpCells() {
		iterateOverAllCells((i, j) -> setUpButtonsFirstTime(i, j));
	}
	
	private void setUpButtonsFirstTime(int i, int j) {
		tiles[i][j] = setUpButton(tileSize);
		updateButton(tiles[i][j], tileImageViews[9]);
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
		return label;
	}

	private void setUpSmiley() {
		updateButton(smiley, smileyImageViews[0]);
		updateButton(smileyBackground, smileyBackgroundViews[0]);
		smiley.setOnMousePressed(event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				updateButton(smileyBackground, smileyBackgroundViews[1]);
			}
		});
		smiley.setOnMouseReleased(event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				updateButton(smileyBackground, smileyBackgroundViews[0]);
				resetBoard();
			}
		});
		smileyBackground.setOnMousePressed(event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				updateButton(smileyBackground, smileyBackgroundViews[1]);
			}
		});
		smileyBackground.setOnMouseReleased(event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				updateButton(smileyBackground, smileyBackgroundViews[0]);
			}
		});
	}

	TimerTask setTimerTask() {
		TimerTask task = new TimerTask() {
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

	public Button setUpButton(int buttonSize) {
		Button button = new Button();
		button.setMaxSize(buttonSize, buttonSize);
		button.setMinSize(buttonSize, buttonSize);
		button.setStyle("-fx-focus-color: transparent;");
		button.setStyle("-fx-faint-focus-color: transparent;");
		button.setStyle("-fx-background-color: transparent;");
		return button;
	}


	public Node getNodeByRowColumnIndex(final int row, final int column) {
		Node result = null;
		ObservableList<Node> childrens = gridPane.getChildren();
		for (Node node : childrens) {
			if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == column) {
				result = node;
				break;
			}
		}
		return result;
	}
	
	private ArrayList<Integer> setUpCell(int i, int j){
		ArrayList<Integer> cell = new ArrayList<Integer>();
		cell.add(i);
		cell.add(j);
		return cell;
	}
	
	private void setUpTile(int i, int j) {
		ArrayList<Integer> cell = setUpCell(i, j);
		tiles[i][j].setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				if(!gameOver && !gameWon && !flags[i][j]) {
					if(firstTime) {
						generateMines(i, j);
						populateMineFrequencies();
						firstTime = false;
					}
					if(minesLocations[i][j]) {
						gameOver(i, j);
					}
					else if(!cellsExposed.contains(cell)) {
						ImageView thisImage = updateImageView(i, j);
						flags[i][j] = false;
						validAdd(cellsExposed, cell);
						if(mineFrequencies[i][j] == 0) {
							emptyCells.clear();
							mineFrequencies[i][j] = -1;
							collectEmptyCells(i, j); 
							collectNonEmptyCells() ;
							for (ArrayList<Integer> thisCell: emptyCells)
							{
								int x = thisCell.get(0);
								int y = thisCell.get(1);
								updateButton(tiles[x][y], updateTile(x, y));
								validAdd(cellsExposed, thisCell);
							}
							for (ArrayList<Integer> nonEmpty: nonEmptyCells) {
								int x = nonEmpty.get(0);
								int y = nonEmpty.get(1);
								updateButton(tiles[x][y], updateTile(x, y));
								validAdd(cellsExposed, nonEmpty);
							}
						}
						updateButton(tiles[i][j], updateTile(i, j));
					}
				}
				checkWon();
			} else if (event.getButton() == MouseButton.SECONDARY) {
				if(!gameOver && !gameWon) {
					if(!cellsExposed.contains(cell) && !flags[i][j]) {
						updateButton(tiles[i][j], tileImageViews[11]);
						mineCounter--;
						mineCounterStr = fillZeroes(mineCounter);
						scoreboard.setText(mineCounterStr);
						cellsExposed.add(cell);
						flags[i][j] = true;
					}else if(flags[i][j]){
						updateButton(tiles[i][j], tileImageViews[9]);
						mineCounter++;
						mineCounterStr = fillZeroes(mineCounter);
						scoreboard.setText(mineCounterStr);
						flags[i][j] = false;
						cellsExposed.remove(cell);
					}
				}
			}
		});
		tiles[i][j].setOnMousePressed(event -> {
			if(!gameOver && !gameWon) {
				if (event.getButton() == MouseButton.PRIMARY) {
					updateButton(smiley, smileyImageViews[1]);
				}
			}
		});
		tiles[i][j].setOnMouseReleased(event -> {
			if(!gameOver && !gameWon) {
				if (event.getButton() == MouseButton.PRIMARY) {
					if (minesLocations[i][j]) {
						if(!flags[i][j]) {
							updateButton(smiley, smileyImageViews[3]);
						}else {
							updateButton(smiley, smileyImageViews[0]);
						}
					} 
					else {
						updateButton(smiley, smileyImageViews[0]);
						if (time == 0 && firstTime) {
							timerT.scheduleAtFixedRate(setTimerTask(), 0, 1000);
						}
					}
				}
			}
		});
	}
	
	private void checkWon() {
		if(!gameOver) {
			if(cellsExposed.size() == gridHeight * gridWidth - mineCounter) {
				gameWon = true;
				gameWon();
			}
		}
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
		Rectangle2D viewPort;
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
	
	private ImageView updateTile(int i, int j) {
		ImageView tile = new ImageView();
		if (minesLocations[i][j]) {
			tile = tileImageViews[14];
		} 
		else if (mineFrequencies[i][j] == -1) {
			tile = tileImageViews[0];
		} 
		else {
			tile = tileImageViews[mineFrequencies[i][j]];
		}
		return tile;
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
			getNeighbouringCells(cell.get(0), cell.get(1), (a, b) -> checkCellNotEmpty(a, b));
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
					ArrayList<Integer> cell = setUpCell(neighbourX, neighbourY);
					action.accept(cells, cell);
				}
		}
		return cells;
	}
	
	private void generateMines(int x, int y) {
		ArrayList<Integer> mines = new ArrayList<>();
		ArrayList<Integer> excluded = new ArrayList<Integer>();
		excluded.add(y * gridWidth + x);
		for (int[] offset : NEIGHBOR_OFFSETS) {
			if (isValidNeighbour(x + offset[0], y + offset[1])) {
				excluded.add((y + offset[1]) * gridWidth + (x + offset[0]));
			}
		}
		mines = getRandomMineExcluding(excluded);
		for (int i = 0; i < numberOfMines; i++) {		
			minesLocations[mines.get(i) % gridWidth][mines.get(i) / gridWidth] = true;
			ArrayList<Integer> mine = setUpCell(mines.get(i) % gridWidth, mines.get(i) / gridWidth);
			validAdd(mineCoords, mine);
		}
	}
	
	private ArrayList<Integer> getRandomMineExcluding(ArrayList<Integer> excluded) {
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
	
	private void resetBoard() {
		updateButton(smiley, smileyImageViews[0]);
		iterateOverAllCells((i, j) -> resetBoardHelper(i, j));
		timerT.cancel();
		time = -1;
		updateLabel();
		timerT = new Timer();
		mineCounter = numberOfMines;
		mineCounterStr = fillZeroes(mineCounter);
		scoreboard.setText(mineCounterStr);
		emptyCells.clear();
		nonEmptyCells.clear();
		cellsExposed.clear();
		for (int r = 0; r < gridWidth; r++) {
			Arrays.fill(mineFrequencies[r], 0);
			Arrays.fill(flags[r], false);
			Arrays.fill(minesLocations[r], false);
		}
		firstTime = true;
		gameOver = false;
		gameWon = false;
	}
	
	private void resetBoardHelper(int i, int j) {
		updateButton(tiles[i][j], tileImageViews[9]);
	}
	
	private void gameOver(int i, int j) {
		iterateOverAllCells((a, b) -> checkCellCombinations(a, b));
		updateButton(tiles[i][j], tileImageViews[10]);
		timerT.cancel();
		gameOver = true;
	}
	
	private void checkCellCombinations(int i, int j) {
		if(!flags[i][j] && minesLocations[i][j]) {
			updateButton(tiles[i][j], tileImageViews[14]);
		}else if(flags[i][j] && !minesLocations[i][j]) {
			updateButton(tiles[i][j], tileImageViews[12]);
		}
	}
	
	private void turnOverRemainingFlags(int i, int j) {
		ArrayList<Integer> cell = setUpCell(i, j);
		if(!cellsExposed.contains(cell)) {
			updateButton(tiles[i][j], tileImageViews[11]);
		}
	}
	
	private void gameWon() {
		timerT.cancel();
		iterateOverAllCells((i, j) -> turnOverRemainingFlags(i, j));
		updateButton(smiley, smileyImageViews[2]);

	}

	public static void main(String[] args) {
		launch(args);
	}
}
