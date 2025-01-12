package application.view;

import java.util.function.BiConsumer;

import application.model.Board;
import application.model.Game;
import javafx.application.Platform;
import javafx.event.*;
import javafx.geometry.Insets;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import javafx.stage.*;

public class MainView{


	final static int MAX_WIDTH = 99;
	final static int MAX_HEIGHT = 99;
	final static int MAX_MINES = 9800;
	
	final static int DEFAULT_TILE_SIZE = 16;
	final static int SMILEYS = 4;
	final static int SMILEYS_ROW = 2;
	final static int SPRITES = 16;
	final static int SPRITES_ROW = 4;
	final static int BACKGROUNDS = 2;
	final static int DEFAULT_WIDTH = 16;
	final static int DEFAULT_HEIGHT = 16;
	int customWidth = DEFAULT_WIDTH;
	int customHeight = DEFAULT_HEIGHT;
	final static int DEFAULT_NO_OF_MINES = 40;
	int customMines = DEFAULT_NO_OF_MINES;
	int tileSize = DEFAULT_TILE_SIZE;
	int smileySize = 18;
	int smileySizeBackground = 31;
	int time = 0;
	int imgPxs = 32;
	int smileyPxs = 409;
	private Stage mainStage;
	Label scoreboard, scoreboard2, timer;
	BorderPane bPane;
	GridPane gridPane;
	BorderPane scorePane;
	StackPane sPane, boardSPane;
	Button smiley, smileyBackground;
	Rectangle2D [] smileyViewPorts = new Rectangle2D[SMILEYS];
	Rectangle2D [] tileViewPorts = new Rectangle2D[SPRITES];
	Image smileyImages = new Image("smileys.png");
	Image tileImages = new Image("tileSprites.png");
	ImageView [] smileyImageViews = new ImageView[SMILEYS];
	ImageView [] tileImageViews = new ImageView[SPRITES];
	ImageView [] smileyBackgroundViews = new ImageView[BACKGROUNDS];
	Game minesweeperGame;
	String mineCounterStr = fillZeroes(DEFAULT_NO_OF_MINES);
	Font font = Font.loadFont("file:res/fonts/DSEG7Classic-Bold.ttf", 23);	//Update this to be more like classic
//	Font font = Font.loadFont("file:res/fonts/Segment7Standard.otf", 25);	//Update this to be more like classic
	Scene scene;
	Board board;
	
	public MainView(Stage stage) {
		this.mainStage = stage;
		minesweeperGame = new Game(40);
		board = new Board(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		buildStage();
	}

	public Scene getScene() {
		return scene;
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
	
	private void buildStage() {
		MenuBar mb = setUpMenus();
		initialiseViewPorts();
		initialiseImageViews();
		smiley = setUpButton(smileySize);
		smileyBackground = setUpButton(smileySizeBackground);
		updateButton(smiley, smileyImageViews[0]);
		updateButton(smileyBackground, smileyBackgroundViews[0]);
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
		boardSPane = new StackPane();
		scorePane = new BorderPane();
		gridPane = new GridPane();
		scoreboard = setUpDisplay(mineCounterStr);
		scoreboard2 = setUpDisplay("888");
		scoreboard2.setTextFill(Color.rgb(168, 0, 87));
		timer = setUpDisplay(""+fillZeroes(time));
		scoreboard.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(0), new Insets(0))));
		scoreboard.setStyle("-fx-letter-spacing: -5px;");
		scoreboard2.setStyle("-fx-letter-spacing: -5px;");

		scoreboard2.setBackground(new Background(new BackgroundFill(Color.BLACK, new CornerRadii(0), new Insets(0))));
		timer.setBackground(new Background(new BackgroundFill(Color.BLACK, new CornerRadii(0), new Insets(0))));
		setUpCells();
		
		boardSPane.getChildren().add(scoreboard2);
		boardSPane.getChildren().add(scoreboard);
		sPane.getChildren().add(smileyBackground);
		sPane.getChildren().add(smiley);
		StackPane.setAlignment(smileyBackground, Pos.CENTER);
		StackPane.setAlignment(smiley, Pos.CENTER);
		scorePane.setLeft(boardSPane);
		scorePane.setCenter(sPane);
		scorePane.setRight(timer);
		ColumnConstraints cc = new ColumnConstraints();
		VBox topContainer = new VBox();
		topContainer.getChildren().addAll(mb, scorePane);
		bPane.setTop(topContainer);
		bPane.setCenter(gridPane);		
		innerPane.getChildren().add(bPane);
		scene = new Scene(outerPane);
//		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
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
			minesweeperGame.resetBoard();
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
	
	private void updateButton (Button button, ImageView imageView) {
		button.setGraphic(copyImageView(imageView));
	}
		
	private Rectangle2D setTileRectangle(int i, int j, int pxs) {
		return new Rectangle2D(i * pxs, j * pxs, pxs, pxs);
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
            	minesweeperGame.newGame(9, 9, 10);
            }
        });
        intermediate.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
            	minesweeperGame.newGame(16, 16, 40);
            }
        });
        expert.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
            	minesweeperGame.newGame(30, 16, 99);
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
    		minesweeperGame.resetBoard();
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
		widthField.setTextFormatter(setTextFormat(MAX_WIDTH));
		heightField.setTextFormatter(setTextFormat(MAX_HEIGHT));
		minesField.setTextFormatter(setTextFormat(MAX_MINES));
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
				minesweeperGame.newGame(customWidth, customHeight, customMines);
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
	
	private Label setUpDisplay(String str) {
		Label label = new Label(str);
		label.setTextFill(Color.RED);
		label.setFont(font);
		return label;
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
	
	
	
	private void updateStage() {
		bPane.getChildren().remove(gridPane);
		gridPane = new GridPane();
		setUpCells();
		bPane.setCenter(gridPane);
		Scene scene = bPane.getScene();
	   	mainStage.sizeToScene();
	}
	
	private void setUpCells() {
		iterateOverAllCells((i, j) -> setUpButtonsFirstTime(i, j));
	}
	
	private void iterateOverAllCells(BiConsumer<Integer, Integer> action) {
		for (int i = 0; i < board.getGridWidth(); i++) {
			for (int j = 0; j < board.getGridHeight(); j++) {
				action.accept(i, j);
			}
		}
	}
	
	private void setUpButtonsFirstTime(int i, int j) {
		board.tiles[i][j] = setUpButton(tileSize);
		updateButton(board.tiles[i][j], tileImageViews[9]);
//		setUpTile(i, j);
		gridPane.add(board.tiles[i][j], i, j);
	}
	
	private void resizeBoard() {
		int divider = customWidth > 1.78 * customHeight ? (int)(customWidth / 1.78) : customHeight;
		if(900 / divider < 16) {
			tileSize = 900 / divider;
		}else {
			tileSize = 16;
		}
	}
	
	public void updateLabel() {
		if (time < 999) {
			time++;
		}
		timer.setText(fillZeroes(time));
	}
	
	private boolean validCustom() {
		return customMines + 9 <= customWidth * customHeight;
	}
}
