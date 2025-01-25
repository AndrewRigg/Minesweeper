package application.view;

import java.util.*;

import application.controller.*;
import application.model.*;
import javafx.application.Platform;
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
    final static int MAX_WIDTH = 99,
            MAX_HEIGHT = 99,
            MAX_MINES = 9800,
            DEFAULT_TILE_SIZE = 16,
            DEFAULT_WIDTH = 16,
            DEFAULT_HEIGHT = 16,
            DEFAULT_NO_OF_MINES = 40,
            SMILEYS = 4,
            SMILEYS_ROW = 2,
            BACKGROUNDS = 2,
            SMILEY_BACKGROUNDS = 2;
    int customWidth = DEFAULT_WIDTH;
    int customHeight = DEFAULT_HEIGHT;
    int customMines = DEFAULT_NO_OF_MINES;
    int tileSize = DEFAULT_TILE_SIZE;
    public int time = 0;
    int smileySize = 18;
    int smileySizeBackground = 31;
    int smileyPxs = 409;
    int getSmileyBackgroundPxs = 32;
    private double xOffset = 0;
    private double yOffset = 0;
    boolean maximised = false;
    ArrayList<Menu> menus = new ArrayList<>();
    ArrayList<ArrayList<MenuItem>> menuItems = new ArrayList<>();
    ToggleGroup group = new ToggleGroup();
    Game game;
    LinkedHashMap<String, Runnable> subGameMenuMapping = new LinkedHashMap<>();
    LinkedHashMap<String, Runnable> subHelpMenuMapping = new LinkedHashMap<>();
    LinkedHashMap<String, LinkedHashMap<String, Runnable>> completeMenu = new LinkedHashMap<>();
    ArrayList<ArrayList<Integer>> menuSeparatorPositions = new ArrayList<>(){{
            add(new ArrayList<>(List.of(1, 6, 10, 12)));
            add(new ArrayList<>(List.of(4)));
        }};
    public String mineCounterStr = fillZeroes(DEFAULT_NO_OF_MINES);
            Rectangle2D [] smileyViewPorts = new Rectangle2D[SMILEYS];
    Image   minesweeperTextImage = new Image("/images/MinesweeperText.png"),
            mineImage = new Image("/images/mine.png"),
            smileyImages = new Image("images/smileys.png"),
            SmileyBackgroundImages = new Image("images/tileSprites.png");
    ImageView title = new ImageView(minesweeperTextImage),
            minesweeperIcon = new ImageView(mineImage);
    public ImageView [] smileyImageViews = new ImageView[SMILEYS];
    ImageView [] smileyBackgroundViews = new ImageView[BACKGROUNDS];
    Rectangle2D [] smileyBackgroundViewPorts = new Rectangle2D[SMILEY_BACKGROUNDS];
    Label scoreboard_bottom_layer = new Label("888");
    public Label scoreboard = new Label(mineCounterStr);
    Label timer_bottom_layer = new Label("888");
    Label timer = new Label(fillZeroes(time));
    GridPane gridPane = new GridPane();
    Button  smileyBackground = new Button();
    public Button smiley = new Button();
    Button closeButton = new Button(),
            minimizeButton = new Button(),
            maximizeButton = new Button();
    StackPane sPane = new StackPane(smileyBackground, smiley),
            boardSPane = new StackPane(scoreboard_bottom_layer, scoreboard),
            boardSPane2 = new StackPane(timer_bottom_layer, timer),
            boardSPaneBack = new StackPane(boardSPane),
            boardSPaneBack2 = new StackPane(boardSPane2);
    HBox min_max_buttons = new HBox(minimizeButton, maximizeButton),
            buttons = new HBox(min_max_buttons, closeButton),
            titleContent = new HBox(minesweeperIcon, title),
            titleBar = new HBox(titleContent, buttons);
    MenuBar mb = new MenuBar();
    BorderPane scorePane = new BorderPane();
    VBox centreBox = new VBox(scorePane, gridPane),
            topContainer = new VBox(titleBar, mb);
    BorderPane bPane = createBorderPane(topContainer, centreBox);
    StackPane innerPane = new StackPane(bPane),
            innerOuter = new StackPane(innerPane),
            outerPane = new StackPane(innerOuter);
    Font font = Font.loadFont(Objects.requireNonNull(getClass().getResource("/fonts/DSEG7Classic-Bold.ttf")).toExternalForm(), 21);
    Scene scene;
    Board board;
    GameController controller;

    private final Stage mainStage;

    public MainView(Stage stage) {
        this.mainStage = stage;
        controller = new GameController(this);
        board = new Board(DEFAULT_WIDTH, DEFAULT_HEIGHT, gridPane);
        game = new Game(40, board, this, controller);
        buildStage();
    }

    public Scene getScene() {
        return scene;
    }

    public void setUpButtonsFirstTime(int i, int j, GridPane gridPane) {
        System.out.println("tiles width: " + board.tiles.length + " tiles height: " + board.tiles[0].length);
        board.tiles[i][j] = new Button();
        setUpButton(board.tiles[i][j], tileSize);
        game.updateButton(board.tiles[i][j], game.getBoardView().tileImageViews[9]);
        game.setUpTile(i, j);
        gridPane.add(board.tiles[i][j], i, j);
    }

    public void setUpButton(Button button, int buttonSize) {
        button.setMaxSize(buttonSize, buttonSize);
        button.setMinSize(buttonSize, buttonSize);
        button.setStyle("-fx-focus-color: transparent;");
        button.setStyle("-fx-faint-focus-color: transparent;");
        button.setStyle("-fx-background-color: transparent;");
    }

    private static void openUsingHelpPage() {
        try {
            java.awt.Desktop.getDesktop().browse(new java.net.URI("https://minesweepergame.com/"));
        }catch(Exception e){
            System.err.println("Opening Webpage failed");
        }
    }

    private void setUpMap(){
        subGameMenuMapping.put("_New", () -> game.resetBoard());
        subGameMenuMapping.put("radio_Beginner", () -> game.newGame(9, 9, 10));
        subGameMenuMapping.put("radio_Intermediate", () -> game.newGame(16, 16, 40));
        subGameMenuMapping.put("radio_Expert", () -> game.newGame(30, 16, 99));
        subGameMenuMapping.put("radio_Custom...", this::showCustomDialog);
        subGameMenuMapping.put("_Marks (?)", () -> System.out.println("Adding MARKS!"));
        subGameMenuMapping.put("_Color", () -> System.out.println("Changing Color"));
        subGameMenuMapping.put("_Sound", () -> System.out.println("Sound OFF"));
        subGameMenuMapping.put("Best _Times...", () -> System.out.println("Show Best Times page"));
        subGameMenuMapping.put("E_xit", () -> { Platform.exit(); System.exit(0); });

        subHelpMenuMapping.put("_Index", () ->  System.out.println("Index Page"));
        subHelpMenuMapping.put("_How to Play", () -> System.out.println("How to Play Page"));
        subHelpMenuMapping.put("_Commands", () -> System.out.println("Commands Page"));
        subHelpMenuMapping.put("Using _Help", () -> {openUsingHelpPage();System.out.println("Using Help");});
        subHelpMenuMapping.put("_About Minesweeper...", () -> System.out.println("About Minesweeper Page"));

        completeMenu.put("_Game", subGameMenuMapping);
        completeMenu.put("_Help", subHelpMenuMapping);
    }

    private void buildStage() {
        setUpMap();
        setUpMenus(mb);
        setUpButton(smiley, smileySize);
        setUpButton(smileyBackground, smileySizeBackground);
        initialiseSmileyViewPorts();
        initialiseSmileys();
        setUpSmiley();
        game.updateButton(smiley, smileyImageViews[0]);
        game.updateButton(smileyBackground, smileyBackgroundViews[0]);
        titleBar.setStyle("-fx-background-color: #0000A8; -fx-padding: 1;");
        title.setTranslateY(2);
        title.setTranslateX(4);
        minesweeperIcon.setFitHeight(16);
        minesweeperIcon.setFitWidth(16);
        HBox.setHgrow(titleContent, Priority.ALWAYS);
        closeButton.setOnAction(e -> { Platform.exit(); System.exit(0); });
        minimizeButton.setOnAction(e -> mainStage.setIconified(true));
        maximizeButton.setOnAction(e -> {
            mainStage.setFullScreen(!maximised);
            maximised = !maximised;
        });
        setUpTitleBarButtons(closeButton, "close");
        setUpTitleBarButtons(minimizeButton, "minimise");
        setUpTitleBarButtons(maximizeButton, "maximise");
        min_max_buttons.setSpacing(1);
        buttons.setSpacing(3);

        titleBar.setOnMousePressed((MouseEvent event) -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        titleBar.setOnMouseDragged((MouseEvent event) -> {
            mainStage.setX(event.getScreenX() - xOffset);
            mainStage.setY(event.getScreenY() - yOffset);
        });
        setUpDisplay(scoreboard);
        setUpDisplay(scoreboard_bottom_layer);
        setUpDisplay(timer);
        setUpDisplay(timer_bottom_layer);
        scoreboard_bottom_layer.setTextFill(Color.rgb(168, 0, 87));
        timer_bottom_layer.setTextFill(Color.rgb(168, 0, 87));
        StackPane.setAlignment(smileyBackground, Pos.CENTER);
        StackPane.setAlignment(smiley, Pos.CENTER);
        scorePane.setLeft(boardSPaneBack);
        scorePane.setCenter(sPane);
        scorePane.setRight(boardSPaneBack2);
        ColumnConstraints cc = new ColumnConstraints();
        gridPane.setTranslateY(6);
        setBackgrounds();
        setSpacing();
        setContainerStyles();
        game.setUpCells(gridPane);
        scene = new Scene(outerPane);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/titlebar.css")).toExternalForm());
    }

    private void initialiseSmileys() {
        for (int i = 0; i < SMILEYS; i++) {
            smileyImageViews[i] = game.getBoardView().setUpImageView(smileyImages, smileySize, smileyViewPorts[i]);
        }
        smileyBackgroundViews[0] = game.getBoardView().setUpImageView(SmileyBackgroundImages, smileySizeBackground, smileyBackgroundViewPorts[1]);
        smileyBackgroundViews[1] = game.getBoardView().setUpImageView(SmileyBackgroundImages, smileySizeBackground, smileyBackgroundViewPorts[0]);
    }

    private void initialiseSmileyViewPorts(){
        for(int i = 0; i < SMILEYS; i++) {
            smileyViewPorts[i] = game.getBoardView().setTileRectangle(i % SMILEYS_ROW, i / SMILEYS_ROW, smileyPxs);
        }
        smileyBackgroundViewPorts[0] = game.getBoardView().setTileRectangle(0, 0, getSmileyBackgroundPxs);
        smileyBackgroundViewPorts[1] = game.getBoardView().setTileRectangle(1, 2, getSmileyBackgroundPxs);
    }

    private BorderPane createBorderPane(Node top, Node center) {
        BorderPane pane = new BorderPane();
        pane.setTop(top);
        pane.setCenter(center);
        return pane;
    }

    private void setBackgrounds(){
        scoreboard.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(0), Insets.EMPTY)));
        scoreboard_bottom_layer.setBackground(new Background(new BackgroundFill(Color.BLACK, new CornerRadii(0),  Insets.EMPTY)));
        timer.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(0),  Insets.EMPTY)));
        timer_bottom_layer.setBackground(new Background(new BackgroundFill(Color.BLACK, new CornerRadii(0),  Insets.EMPTY)));
    }

    private void setSpacing(){
        boardSPane.setPadding(Insets.EMPTY);
        boardSPane2.setPadding(Insets.EMPTY);
        boardSPaneBack.setPadding(Insets.EMPTY);
        boardSPaneBack2.setPadding(Insets.EMPTY);
        scoreboard.setPadding(Insets.EMPTY);
        scoreboard_bottom_layer.setPadding(Insets.EMPTY);
        buttons.setPadding(new Insets(1,2,0,0));
        scorePane.setPadding(new Insets(1, 2, 1, 2));
        centreBox.setPadding(new Insets(6,6,12,6));
    }

    private void setContainerStyles(){
        innerPane.getStyleClass().add("inner-border");
        innerOuter.getStyleClass().add("inner-outer-border");
        outerPane.getStyleClass().add("outside-border");
        boardSPane.getStyleClass().add("scoreboard");
        boardSPane2.getStyleClass().add("scoreboard");
        boardSPaneBack.getStyleClass().add("scoreboard");
        boardSPaneBack2.getStyleClass().add("scoreboard");
        scorePane.getStyleClass().addAll("shadow-effect-concave", "border-size-medium");
        gridPane.getStyleClass().addAll("shadow-effect-concave", "border-size-large");
        centreBox.getStyleClass().addAll("shadow-effect-convex", "border-size-large");
        boardSPaneBack.getStyleClass().addAll("shadow-effect-concave", "border-size-small", "board-spane");
        boardSPaneBack2.getStyleClass().addAll("shadow-effect-concave", "border-size-small", "board-spane");
    }

    private void setUpSmiley() {
        game.updateButton(smiley, smileyImageViews[0]);
        game.updateButton(smileyBackground, smileyBackgroundViews[0]);
        smiley.setOnMousePressed(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                game.updateButton(smileyBackground, smileyBackgroundViews[1]);
            }
        });
        smiley.setOnMouseReleased(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                game.updateButton(smileyBackground, smileyBackgroundViews[0]);
                game.resetBoard();
            }
        });
        smileyBackground.setOnMousePressed(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                game.updateButton(smileyBackground, smileyBackgroundViews[1]);
            }
        });
        smileyBackground.setOnMouseReleased(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                game.updateButton(smileyBackground, smileyBackgroundViews[0]);
            }
        });
    }

    private void setUpTitleBarButtons(Button button, String image){
        button.getStyleClass().clear();
        button.getStyleClass().add("title-button");
        double buttonSize = 13;
        button.setPrefSize(buttonSize +2, buttonSize);
        button.setMinSize(buttonSize +2, buttonSize); // Force smaller height
        button.setMaxSize(buttonSize +2, buttonSize); // Prevent stretching
        Image closeImage = new Image("images/" + image + ".png"); // Load the image
        ImageView closeImageView = new ImageView(closeImage);            // Create an ImageView
        button.setGraphic(closeImageView);                          // Set the ImageView as the button's graphic
    }

    private void setUpMenus(MenuBar mb) {
        createMenuItems(completeMenu);
        setUpSeparators(menus, menuSeparatorPositions);
        menuItems.get(0).getFirst().setAccelerator(new KeyCodeCombination(KeyCode.F2));
        menuItems.get(1).getFirst().setAccelerator(new KeyCodeCombination(KeyCode.F1));
        mb.setBackground(new Background(new BackgroundFill(Color.rgb(192,199,200), new CornerRadii(0), Insets.EMPTY)));
    }

    public void addActionToMenuItem(MenuItem menuItem, Runnable action) {
        menuItem.setOnAction(e -> action.run());
    }

    private void setUpSeparators(ArrayList<Menu> menus, ArrayList<ArrayList<Integer>> menuSeparators){
        for (ArrayList<Integer> list : menuSeparators){
            for (Integer thisInt: list){
                addSeparators(menus.get(menuSeparators.indexOf(list)), thisInt);
            }
        }
    }

    private void addSeparators(Menu menu, int position){
        SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
        menu.getItems().add(position, separatorMenuItem);
    }

    private void createMenuItems(LinkedHashMap<String, LinkedHashMap<String, Runnable>> maps) {
        for (Map.Entry<String, LinkedHashMap<String, Runnable>> entry: maps.entrySet()) {
            String key = entry.getKey();
            Menu thisMenu = setUpMainMenuItems(entry.getKey(), entry.getValue());
            menus.add(thisMenu);
            mb.getMenus().add(thisMenu);
        }
    }

    private Menu setUpMainMenuItems(String name, LinkedHashMap<String, Runnable> submenu){
        Menu menu = new Menu(name);
        menu.setMnemonicParsing(true);
        ArrayList<MenuItem> items = createSubMenuItems(submenu);
        menuItems.add(items);
        menu.getItems().addAll(menuItems.getLast());
        return menu;
    }

    private ArrayList<MenuItem> createSubMenuItems(LinkedHashMap<String, Runnable> map){
        ArrayList<MenuItem> items = new ArrayList<>();
        for (Map.Entry<String, Runnable> item : map.entrySet()) {
            String key = item.getKey();
            if(key.contains("radio")){
                items.add(setUpRadioMenuItems(key.replace("radio", ""), key.contains("Intermediate"), map.get(key)));
            }else{
                items.add(setUpSubMenuItems(key, map.get(key)));
            }
        }
        return items;
    }

    private MenuItem setUpSubMenuItems(String name, Runnable action){
        MenuItem item = new MenuItem(name);
        item.setMnemonicParsing(true);
        addActionToMenuItem(item, action);
        return item;
    }

    private RadioMenuItem setUpRadioMenuItems(String name, boolean difficulty, Runnable action){
        RadioMenuItem item = new RadioMenuItem(name);
        item.setMnemonicParsing(true);
        item.setSelected(difficulty);
        item.setToggleGroup(group);
        addActionToMenuItem(item, action);
        return item;
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
            customWidth = Integer.parseInt(widthField.getText());
            customHeight = Integer.parseInt(heightField.getText());
            customMines = Integer.parseInt(minesField.getText());
            if(board.validCustom(customWidth, customHeight, customMines)) {
                board.resizeBoard(customWidth, customHeight, tileSize);
                game.getBoardView().initialiseImageViews();
                game.newGame(customWidth, customHeight, customMines);
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

    private void setUpDisplay(Label label) {
        label.setTextFill(Color.RED);
        label.setFont(font);
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

    public String fillZeroes(int mineCounter) {
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

    public void updateStage() {
        int gridPaneIndex = centreBox.getChildren().indexOf(gridPane); // Find the index of the old gridPane
        GridPane newGridPane = new GridPane();
//        board = new Board(board.getGridWidth(), board.getGridHeight(), newGridPane);
        game.setUpCells(newGridPane);
        if (gridPaneIndex != -1) {
            centreBox.getChildren().set(gridPaneIndex, newGridPane); // Replace the old gridPane
        }
        centreBox.requestLayout();
        gridPane = newGridPane;
//        Scene scene = bPane.getScene();
        mainStage.sizeToScene();
    }

    public void updateLabel() {
        if (time < 999) {
            time++;
        }
        timer.setText(fillZeroes(time));
    }
}
