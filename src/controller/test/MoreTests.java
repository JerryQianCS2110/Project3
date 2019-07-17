package controller.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import controller.SmartAI;
import model.Board;
import model.Location;
import model.Player;
import model.Game;
import clui.BoardPrinter;

public class MoreTests {

	@Test
	public void test() {
		SmartAI playerX = new SmartAI(Player.X);
		
		Game situation1 = new Game(Player.X);
		situation1.submitMove(Player.X, new Location(7, 5));
		situation1.submitMove(Player.O, new Location(0, 0));
		situation1.submitMove(Player.X, new Location(2, 4));
		situation1.submitMove(Player.O, new Location(0, 1));
		situation1.submitMove(Player.X, new Location(5, 6));
		situation1.submitMove(Player.O, new Location(0, 2));
		situation1.submitMove(Player.X, new Location(8, 8));
		situation1.submitMove(Player.O, new Location(0, 3));
		
		BoardPrinter bp = new BoardPrinter();
		bp.printBoard(situation1.getBoard());
		Location xNextMove = playerX.nextMoveForTest(situation1);
		
		assertEquals(true, xNextMove.equals(new Location(0, 4)));
	}

}
