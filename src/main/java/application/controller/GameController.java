package application.controller;

import application.view.MainView;
import javafx.application.Platform;

import java.util.TimerTask;

public class GameController {

    MainView mainView;

    public GameController(MainView mainView){
        this.mainView = mainView;
    }

    public TimerTask setTimerTask() {
        return new TimerTask() {
            public void run() {
                Platform.runLater(new Runnable() {
                    public void run() {
                        mainView.updateLabel();
                    }
                });
            }
        };
    }
}
