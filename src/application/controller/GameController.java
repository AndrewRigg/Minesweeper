package application.controller;

import java.util.ArrayList;
import java.util.TimerTask;

import application.view.MainView;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class GameController {
	
	ArrayList<ArrayList<Integer>> cellsExposed = new ArrayList<ArrayList<Integer>>();
	MainView mainView;
	
	public GameController(MainView mainView) {
		this.mainView = mainView;
	}
	boolean firstTime = true;

	TimerTask setTimerTask() {
		TimerTask task = new TimerTask() {
			public void run() {
				Platform.runLater(new Runnable() {
					public void run() {
						mainView.updateLabel();
					}
				});
			}
		};
		return task;
	}
	
	private ImageView updateImageView(int i, int j) {
		Image image = new Image("sprites.jpg");
		ImageView imageView = new ImageView(image);
//		imageView.setFitHeight(tileSize);
//		imageView.setFitWidth(tileSize);
//		imageView.setViewport(setViewPort(i, j));
		return imageView;
	}
	
	private Rectangle2D setViewPort(int i, int j) {
		Rectangle2D viewPort = null;
//		if (minesLocations[i][j]) {
//			viewPort = tileViewPorts[2];
//		} 
//		else if (mineFrequencies[i][j] == 0 || mineFrequencies[i][j] == -1) {
//			viewPort = tileViewPorts[3];
//		} else {
//			viewPort = tileViewPorts[mineFrequencies[i][j]+3];
//		}
		return viewPort;
	}

	private ArrayList<Integer> setUpCell(int i, int j){
		ArrayList<Integer> cell = new ArrayList<Integer>();
		cell.add(i);
		cell.add(j);
		return cell;
	}

//	private void setUpTile(int i, int j) {
//		ArrayList<Integer> cell = setUpCell(i, j);
//		tiles[i][j].setOnMouseClicked(event -> {
//			if (event.getButton() == MouseButton.PRIMARY) {
//				if(!gameOver && !gameWon && !flags[i][j]) {
//					if(firstTime) {
//						generateMines(i, j);
//						populateMineFrequencies();
//						firstTime = false;
//					}
//					if(minesLocations[i][j]) {
//						gameOver(i, j);
//					}
//					else if(!cellsExposed.contains(cell)) {
//						ImageView thisImage = updateImageView(i, j);
//						flags[i][j] = false;
//						validAdd(cellsExposed, cell);
//						if(mineFrequencies[i][j] == 0) {
//							emptyCells.clear();
//							mineFrequencies[i][j] = -1;
//							collectEmptyCells(i, j); 
//							collectNonEmptyCells() ;
//							for (ArrayList<Integer> thisCell: emptyCells)
//							{
//								int x = thisCell.get(0);
//								int y = thisCell.get(1);
//								updateButton(tiles[x][y], updateTile(x, y));
//								validAdd(cellsExposed, thisCell);
//							}
//							for (ArrayList<Integer> nonEmpty: nonEmptyCells) {
//								int x = nonEmpty.get(0);
//								int y = nonEmpty.get(1);
//								updateButton(tiles[x][y], updateTile(x, y));
//								validAdd(cellsExposed, nonEmpty);
//							}
//						}
//						updateButton(tiles[i][j], updateTile(i, j));
//					}
//				}
//				checkWon();
//			} else if (event.getButton() == MouseButton.SECONDARY) {
//				if(!gameOver && !gameWon) {
//					if(!cellsExposed.contains(cell) && !flags[i][j]) {
//						updateButton(tiles[i][j], tileImageViews[11]);
//						mineCounter--;
//						mineCounterStr = fillZeroes(mineCounter);
//						scoreboard.setText(mineCounterStr);
//						cellsExposed.add(cell);
//						flags[i][j] = true;
//					}else if(flags[i][j]){
//						updateButton(tiles[i][j], tileImageViews[9]);
//						mineCounter++;
//						mineCounterStr = fillZeroes(mineCounter);
//						scoreboard.setText(mineCounterStr);
//						flags[i][j] = false;
//						cellsExposed.remove(cell);
//					}
//				}
//			}
//		});
//		tiles[i][j].setOnMousePressed(event -> {
//			if(!gameOver && !gameWon) {
//				if (event.getButton() == MouseButton.PRIMARY) {
//					updateButton(smiley, smileyImageViews[1]);
//				}
//			}
//		});
//		tiles[i][j].setOnMouseReleased(event -> {
//			if(!gameOver && !gameWon) {
//				if (event.getButton() == MouseButton.PRIMARY) {
//					if (minesLocations[i][j]) {
//						if(!flags[i][j]) {
//							updateButton(smiley, smileyImageViews[3]);
//						}else {
//							updateButton(smiley, smileyImageViews[0]);
//						}
//					} 
//					else {
//						updateButton(smiley, smileyImageViews[0]);
//						if (time == 0 && firstTime) {
//							timerT.scheduleAtFixedRate(setTimerTask(), 0, 1000);
//						}
//					}
//				}
//			}
//		});
//	}
}

