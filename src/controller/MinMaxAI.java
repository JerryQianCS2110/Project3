package controller;

import org.eclipse.jdt.annotation.NonNull;
import model.Victory;
import model.Board;
import model.Board.State;
import model.Game;
import model.Location;
import model.NotImplementedException;
import model.Player;

/**
 * A MinMaxAI is a controller that uses the minimax algorithm to select the next
 * move.  The minimax algorithm searches for the best possible next move, under
 * the assumption that your opponent will also always select the best possible
 * move.
 *
 * <p>The minimax algorithm assigns a score to each possible game configuration
 * g.  The score is assigned recursively as follows: if the game g is over and
 * the player has won, then the score is infinity.  If the game g is over and
 * the player has lost, then the score is negative infinity.  If the game is a
 * draw, then the score is 0.
 * 
 * <p>If the game is not over, then there are many possible moves that could be
 * made; each of these leads to a new game configuration g'.  We can
 * recursively find the score for each of these configurations.
 * 
 * <p>If it is the player's turn, then they will choose the action that
 * maximizes their score, so the score for g is the maximum of all the scores
 * of the g's.  However, if it is the opponent's turn, then the opponent will
 * try to minimize the score for the player, so the score for g is the
 * <em>minimum</em> of all of the scores of the g'.
 * 
 * <p>You can think of the game as defining a tree, where each node in the tree
 * represents a game configuration, and the children of g are all of the g'
 * reachable from g by taking a turn.  The minimax algorithm is then a
 * particular traversal of this tree.
 * 
 * <p>In practice, game trees can become very large, so we apply a few
 * strategies to narrow the set of paths that we search.  First, we can decide
 * to only consider certain kinds of moves.  For five-in-a-row, there are
 * typically at least 70 moves available at each step; but it's (usually) not
 * sensible to go on the opposite side of the board from where all of the other
 * pieces are; by restricting our search to only part of the board, we can
 * reduce the space considerably.
 * 
 * <p>A second strategy is that we can look only a few moves ahead instead of
 * planning all the way to the end of the game.  This requires us to be able to
 * estimate how "good" a given board looks for a player.
 * 
 * <p>This class implements the minimax algorithm with support for these two
 * strategies for reducing the search space.  The abstract method {@link
 * #moves(Board)} is used to list all of the moves that the AI is willing to
 * consider, while the abstract method {@link #estimate(Board)} returns
 * the estimation of how good the board is for the given player.
 */
public abstract class MinMaxAI extends Controller {

	/**
	 * Return an estimate of how good the given board is for me.
	 * A result of infinity means I have won.  A result of negative infinity
	 * means that I have lost.
	 */
	protected abstract int estimate(Board b);
	
	/**
	 * Return the set of moves that the AI will consider when planning ahead.
	 * Must contain at least one move if there are any valid moves to make.
	 */
	protected abstract Iterable<Location> moves(Board b);
	
	/**
	 * Create an AI that will recursively search for the next move using the
	 * minimax algorithm.  When searching for a move, the algorithm will look
	 * depth moves into the future.
	 *
	 * <p>choosing a higher value for depth makes the AI smarter, but requires
	 * more time to select moves.
	 */
	protected MinMaxAI(Player me, int depth) {
		super(me);
		// TODO Auto-generated method stub
		//throw new NotImplementedException();
	}
	
	/**
	 * Return the move that maximizes the score according to the minimax
	 * algorithm described above.
	 */
	
	 /** <p>If it is the player's turn, then they will choose the action that
	 * maximizes their score, so the score for g is the maximum of all the scores
	 * of the g's.  However, if it is the opponent's turn, then the opponent will
	 * try to minimize the score for the player, so the score for g is the
	 * <em>minimum</em> of all of the scores of the g'.
	 * 
	 * <p>You can think of the game as defining a tree, where each node in the tree
	 * represents a game configuration, and the children of g are all of the g'
	 * reachable from g by taking a turn.  The minimax algorithm is then a
	 * particular traversal of this tree.
	 * 
	 * <p>In practice, game trees can become very large, so we apply a few
	 * strategies to narrow the set of paths that we search.  First, we can decide
	 * to only consider certain kinds of moves.  For five-in-a-row, there are
	 * typically at least 70 moves available at each step; but it's (usually) not
	 * sensible to go on the opposite side of the board from where all of the other
	 * pieces are; by restricting our search to only part of the board, we can
	 * reduce the space considerably.
	 * 
	 * <p>A second strategy is that we can look only a few moves ahead instead of
	 * planning all the way to the end of the game.  This requires us to be able to
	 * estimate how "good" a given board looks for a player.
	 * 
	 * <p>This class implements the minimax algorithm with support for these two
	 * strategies for reducing the search space.  The abstract method {@link
	 * #moves(Board)} is used to list all of the moves that the AI is willing to
	 * consider, while the abstract method {@link #estimate(Board)} returns
	 * the estimation of how good the board is for the given player.
	 */
	/**
	 * A particular line of 5 adjacent cells is "winnable" for player p if
	 * it does not contain any of the opponent's marks.
	 * 
	 * <p>We measure goodness by counting the number of winnable lines, and
	 * scoring them based on the number of the player's marks as follows:
	 * 
	 * <table><tr><th> Number of marks </th> <th>  Score    </th></tr>
	 *        <tr><td>     0           </td> <td>  0        </td></tr>
	 *        <tr><td>     1           </td> <td>  1        </td></tr>
	 *        <tr><td>     2           </td> <td>  10       </td></tr>
	 *        <tr><td>     3           </td> <td>  100      </td></tr>
	 *        <tr><td>     4           </td> <td>  1000     </td></tr>
	 *        <tr><td>     5           </td> <td>  10000    </td></tr>
	 * </table>
	 *
	 * <p>Note that overlapping segments will be counted multiple times, so
	 * that, for example, the following board:
	 * <pre> 
	 *       O O
	 *       OXO
	 *       O O
	 * </pre>
	 * will count as 5 points for X, since there are 5 vertical line segments
	 * passing through X, while
	 * <pre>
	 *       OOO
	 *       OXO
	 *       O O
	 * </pre>
	 * will only count for 1 point, since only the line segment proceeding down
	 * from X is winnable.
	 * 
	 * The estimate is the difference between the player's score and his
	 * opponent's
	 */
	
	private int getScore(Location l, Game g) {
		
		
		
		//temporary placeholder
		return 0;
	}
	
	protected @Override Location nextMove(Game g) {
		// TODO Auto-generated method stub
		//throw new NotImplementedException();
		
		int score = 0;
		
		if(gameEnded(g) == "playerWin")             //player wins
			score = 99999999; 						//essentially infinity
		else if(gameEnded(g) == "playerNotWin")     //player doesn't win
			score = -999999999; 					//essentially negative infinity
		else if(gameEnded(g) == "draw")             //draw
			score = 0;
		else {
			//propose a move
			//calculate score
			//get the maximized score for you, and minimized score for opponent
			//recursive step
			//then call nextMove with the updated game G
			
			
			//use moves and estimate
			/**
			 * Return an estimate of how good the given board is for me.
			 * A result of infinity means I have won.  A result of negative infinity
			 * means that I have lost.
			 */
			//protected abstract int estimate(Board b);
			
			/**
			 * Return the set of moves that the AI will consider when planning ahead.
			 * Must contain at least one move if there are any valid moves to make.
			 */
			//protected abstract Iterable<Location> moves(Board b);
			
			Board currentBoard = g.getBoard();
			
			Iterable<Location> possibleMoves = moves(currentBoard);
			Iterator<Location> moveIterator = possibleMoves.iterator();
			Location optimumLocation = null;
			int highestScore = 0;
			
			while(moveIterator.hasNext()) {
				Location nextMove = moveIterator.next();
				Board withMove = currentBoard.update(this.me, nextMove);
				int boardScore = estimate(withMove);
				
				//need a way to minimize opponent score
				
				if(boardScore > highestScore) {
					highestScore = boardScore;
					optimumLocation = nextMove;
				}
			}
			
			Board withBestNextMove = currentBoard.update(this.me, optimumLocation);
			
			//make a new game with the new board
			
			
		}
		
		
		
		//temporary placeholder
		return null;
	}
	
	/**
	 * Another way to check whether the game ended
	 * @param g
	 * @return
	 */
	private String gameEnded(Game g) {
		Board b = g.getBoard();

		State gameState = b.getState();
		
		if(gameState == State.HAS_WINNER) {
			Victory winPerson = b.getWinner();
			Player winner = winPerson.winner;
			if(winner == this.me)
				return "playerWin";
			else
				return "playerNotWin";
		}else if(gameState == State.DRAW) {
			return "draw";
		} else {
			return "notOver";
		}
	}
}
