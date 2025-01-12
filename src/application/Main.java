package application;

import application.view.MainView;
import javafx.application.*;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.stage.*;

public class Main extends Application {
	
	public void start(Stage primaryStage) {
				
		MainView mainView = new MainView(primaryStage);
		primaryStage.setResizable(true);
		primaryStage.setScene(mainView.getScene());
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
	
	public static void main(String[] args) {
		launch(args);
	}
}
