package application.view;

import application.model.*;
import javafx.geometry.*;
import javafx.scene.image.*;

public class BoardView {
    Board board;
    int tileSize,
            imgPxs = 32;
    final static int SPRITES = 16,
            SPRITES_ROW = 4;
    public ImageView[] tileImageViews = new ImageView[SPRITES];
    Image tileImages = new Image("images/tileSprites.png");
    Game game;

    public BoardView(Board board, int tileSize, Game game){
         this.board = board;
         this.tileSize = tileSize;
        initialiseViewPorts();
        initialiseImageViews();
        this.game = game;
    }

    private void initialiseViewPorts() {
        for (int j = 0; j < SPRITES; j++) {
            Rectangle2D sprite = setTileRectangle(j % SPRITES_ROW, j / SPRITES_ROW, imgPxs);
            board.tileViewPorts[j] = sprite;
        }
    }

    public void initialiseImageViews(){
        for (int i = 0; i < SPRITES; i++) {
            tileImageViews[i] = setUpImageView(tileImages, tileSize, board.tileViewPorts[i]);
        }
    }

    public Rectangle2D setTileRectangle(int i, int j, int pxs) {
        return new Rectangle2D(i * pxs, j * pxs, pxs, pxs);
    }

    public ImageView setUpImageView(Image image, int size, Rectangle2D viewPort) {
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

//    public ImageView setImageView(String path, int buttonSize, int x, int y, int size) {
//        Image image = new Image(path);
//        ImageView imageView = new ImageView(image);
//        Rectangle2D viewPort = new Rectangle2D(x, y, size, size);
//        imageView.setFitWidth(buttonSize);
//        imageView.setFitHeight(buttonSize);
//        imageView.setViewport(viewPort);
//        return imageView;
//    }

    public ImageView updateTile(int i, int j) {
        ImageView tile = new ImageView();
		if (board.minesLocations[i][j]) {
			tile = tileImageViews[14];
		}
		else if (board.getMineFrequencies()[i][j] == -1) {
			tile = tileImageViews[0];
		}
		else {
			tile = tileImageViews[board.getMineFrequencies()[i][j]];
		}
        return tile;
    }
}
