package application.view;

import javafx.scene.image.ImageView;

public class BoardView {

	public BoardView() {
		
	}
	
//	public Node getNodeByRowColumnIndex(final int row, final int column) {
//		Node result = null;
//		ObservableList<Node> childrens = gridPane.getChildren();
//		for (Node node : childrens) {
//			if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == column) {
//				result = node;
//				break;
//			}
//		}
//		return result;
//	}
	
	private ImageView updateTile(int i, int j) {
		ImageView tile = new ImageView();
//		if (minesLocations[i][j]) {
//			tile = tileImageViews[14];
//		} 
//		else if (mineFrequencies[i][j] == -1) {
//			tile = tileImageViews[0];
//		} 
//		else {
//			tile = tileImageViews[mineFrequencies[i][j]];
//		}
		return tile;
	}
	
	private void resetBoardHelper(int i, int j) {
//		updateButton(tiles[i][j], tileImageViews[9]);
	}

	private void turnOverRemainingFlags(int i, int j) {
//		ArrayList<Integer> cell = setUpCell(i, j);
//		if(!cellsExposed.contains(cell)) {
//			updateButton(tiles[i][j], tileImageViews[11]);
//		}
	}

}
