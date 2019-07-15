package controller;

import model.Game;
import model.Board;
import model.Location;
import model.NotImplementedException;
import model.Player;

/**
 * A DumbAI is a Controller that always chooses the blank space with the
 * smallest column number from the row with the smallest row number.
 */
public class DumbAI extends Controller {

	public DumbAI(Player me) {
		super(me);
		// TODO Auto-generated constructor stub
		//throw new NotImplementedException();	
	}

	/**
	 * Return the Location of the next move this player should make in
	 * game g, or null if the player cannot play.
	 * 
	 * Precondition, it is this player's turn.
	 */
	protected @Override Location nextMove(Game g) {
		// Note: Calling delay here will make the CLUI work a little more
		//delay(); delay(); delay(); delay(); delay(); delay(); delay(); delay(); delay(); delay();
		// nicely when competing different AIs against each other.
		
		// TODO Auto-generated method stub
		//throw new NotImplementedException();
		
		int emptyRow = -1;
		int emptyCol = -1;
		
		Board currentBoard = g.getBoard();
		
		rowIteration:	
		for(int r = 0; r < 9; r += 1) {
			for(int c = 0; c < 9; c += 1) {
				if(currentBoard.get(r, c) == null) {
					emptyRow = r;
					emptyCol = c;
					break rowIteration;
				}
			}
		}
		
		if(emptyRow != -1 && emptyCol != -1) {
			Location nextMove = new Location(emptyRow, emptyCol);		
			return nextMove;
		} else {
			return null;
		}
	}
}
