package controller;

import org.eclipse.jdt.annotation.NonNull;
import model.Victory;
import model.Board;
import model.Board.State;
import model.Game;
import model.Location;
import model.NotImplementedException;
import model.Player;

import java.util.ArrayList;
import java.util.Iterator;
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

	private int depth;
	
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
		
		this.depth = depth;
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
	
	protected @Override Location nextMove(Game g) {
		Board current = g.getBoard();
		Iterable<Location> iterableMoves = moves(current);
		Iterator<Location> movesIter = iterableMoves.iterator();
		
		while(movesIter.hasNext()) {
			//recursive call, and pass in movesIter.next();
		}
	}
	

	
	
	/*protected @Override Location nextMove(Game g) {
		System.out.println("in next move");
		delay(); delay(); delay(); delay(); delay(); delay(); delay(); delay(); delay(); delay();
		//throw new NotImplementedException();
		//code block
		// TODO Auto-generated method stub
		Board current = g.getBoard();
		Iterable<Location> moveIterable = moves(current);
		Iterator<Location> moveIterator = moveIterable.iterator();
		
		
		
		//creation of tree
		ArrayList<locationNode> roots = new ArrayList<locationNode>();
		
		//need to change the scoring system to then be difference between the two
		
		//making the roots
		while(moveIterator.hasNext()) {
			Location nm = moveIterator.next();
			int score = estimate(current.update(g.nextTurn(), nm));
			locationNode n = new locationNode(null, nm, null, score);
			roots.add(n);
		}
		
		ArrayList<locationNode> leaves = new ArrayList<locationNode>();
		
		for(int i = 1; i < this.depth; i++) {
			//adding each successive possible move to each node
			//shouldn't be in roots, should be in the next.	
			for(int j = 0; j < roots.size(); j++) {
				locationNode curr = roots.get(j);
				Board updatedBoard = current.update(g.nextTurn(), curr.getData());
				
				Iterable<Location> nextMoves = moves(updatedBoard);
				Iterator<Location> nextMovesIter = nextMoves.iterator();
				
				ArrayList<locationNode> succNodes = new ArrayList<locationNode>();
				while(nextMovesIter.hasNext()) {
					Location nxt = nextMovesIter.next();
					
					int yourScore = 0;
					int oppScore = 0;
					
					try {
						yourScore = estimate(updatedBoard.update(g.nextTurn(), nxt));
						oppScore = estimate(updatedBoard.update(g.nextTurn().opponent(), nxt));
					} catch(IllegalStateException e) {
						yourScore = 999999999;
					}
					
					//one depth is one move, not one cycle???
					//write some tests
					//set up some boards, get score of move, make sure next move is the correct next move.
					
					int score = Math.abs(yourScore - oppScore);
					succNodes.add(new locationNode(curr, nxt, null, java.lang.Math.max(score, curr.getScore())));
					
					if(i == this.depth - 1) {
						leaves.add(new locationNode(curr, nxt, null, java.lang.Math.max(score, curr.getScore())));
					}
				}
				
				curr.setSubNodes(succNodes);
			}
		}
		System.out.println(roots);
		//look through all the leaves and find the one with the biggest score
		//then return the root of that specific leaf
		int maxScore = 0;
		locationNode maxScoreLocNode = null;
		
		for(int i = 0; i < leaves.size(); i++) {
			locationNode currentLeaf = leaves.get(i);
			if(currentLeaf.getScore() > maxScore) {
				maxScore = currentLeaf.getScore();
				maxScoreLocNode = currentLeaf;
			}
		}
		
		//System.out.println(maxScoreLocNode.getData());
		//System.out.println(leaves.size());
		
		locationNode nextMoveNode = maxScoreLocNode;
		while(nextMoveNode.getParent() != null)
			nextMoveNode = nextMoveNode.getParent();
		
		//System.out.println(nextMoveNode.getData());
		//delay();delay();delay();delay();delay();delay();delay();delay();delay();delay();
		System.out.println("at end of next move");
		return nextMoveNode.getData();
	
	}*/
		
	/*private class locationNode {
		private locationNode parent;				//null if root
		private Location data;
		private ArrayList<locationNode> subNodes;
		private int score;
		
		public locationNode(locationNode parent, Location d, ArrayList<locationNode> sn, int sc) {
			this.parent = parent;
			this.data = d;
			this.subNodes = sn;
			this.score = sc;
		}
		
		public locationNode getParent() { return this.parent; }
		
		public void setSubNodes(ArrayList<locationNode> sn) { this.subNodes = sn;}
		
		public ArrayList<locationNode> getSubNodes() { return this.subNodes; }
		
		public Location getData() { return this.data; }
		
		public int getScore() { return this.score; }
		
		public void setScore(int s) { this.score = s; }
		
		public String toString() { return this.data.toString() + " sc: " + this.score; }
	
	}*/
	/*protected @Override Location nextMove(Game g) {
		// TODO Auto-generated method stub
		//throw new NotImplementedException();
		
		
		/*delay();delay();delay();delay();delay();delay();delay();delay();delay();delay();
		
		int score = 0;
		
		if(gameEnded(g) == "playerWin") {             //player wins
			score = 99999999; 						  //essentially infinity
			return null;
		}
		else if(gameEnded(g) == "playerNotWin") {     //player doesn't win
			score = -999999999; 					  //essentially negative infinity
			return null;
		}
		else if(gameEnded(g) == "draw") {             //draw
			score = 0;
			return null;
		}
		else {*/
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
			
			
			//for loop from 0 to depth, just the same one as declared in constructor. Can access by this.depth
						
			//System.out.println(moves(g.getBoard()));
			
			//assuming opponent moves like you
			/*Board current = g.getBoard();
			//need a find a way to recurse throw the multiple depths
			//for(int i = 0; i < this.depth; i++) {
			Iterable<Location> possibleMoves = moves(current);
			Iterator<Location> moveIterator = possibleMoves.iterator();
			Location firstMove = null;
			if(moveIterator.hasNext())
				firstMove = moveIterator.next();
				
			current = current.update(this.me, firstMove);

			//best move at depth d
			Location goodMove = getBestMove(g.nextTurn(), current, firstMove, this.depth - 1);
			
			return goodMove;
		}*/
	//}
	
	/*private Location getBestMove(Player p, Board b, Location l, int d) {
		if(d == 0) {
			return null;
		} else if(d == 1) {
			return l;
		} else {
			Board current = b;
			Iterable<Location> possibleMoves = moves(current);
			Iterator<Location> moveIterator = possibleMoves.iterator();
			Location optimumLocation = null;
			
			Player lastMove = this.me;
			
			int scoreDifference = 0;
			
			while(moveIterator.hasNext()) {
				Location possibleNext = moveIterator.next();
				Board withMove = current.update(this.me, possibleNext);
				int yourScore = estimate(withMove);
				
				Iterable<Location> oppMovesIter = moves(withMove);
				Iterator<Location> oppIter = oppMovesIter.iterator();
				//System.out.println("got here");
				
				while(oppIter.hasNext()) {
					Location possibleOppNext = oppIter.next();
					Board withOppMove = withMove.update(this.me.opponent(), possibleOppNext);
						
					int oppScore = estimate(withOppMove);
						
					int tempScoreDiff = yourScore - oppScore;
					//System.out.println("your score: " + yourScore);
					//System.out.println("opp score: " + oppScore);
						
					if(tempScoreDiff > scoreDifference) {
						scoreDifference = tempScoreDiff;
						if(p == this.me) {
							optimumLocation = possibleNext;
							p = this.me.opponent();
						}
						else {
							optimumLocation = possibleOppNext;
							p = this.me;
						}
					}
				}
				//System.out.println("optimum location: " +optimumLocation);
			}
			return getBestMove(p, b.update(p, optimumLocation), optimumLocation, d - 1);
		}
	}

	
	private Location bestLocAtDepth(Player p, Board b, int d) {		
		if(d == 0) {
			return null;
		} else{
			Iterable<Location> possibleMoves = moves(b);
			Iterator<Location> moveIterator = possibleMoves.iterator();
			
			while(moveIterator.hasNext()) {
				Location nextPossMove = moveIterator.next();
				Board adjusted =  b.update(p, nextPossMove);
				return bestLocAtDepth(p.opponent(), adjusted, d - 1);
			}
		}
	}*/
	
	
	
	/**
	 * Another way to check whether the game ended
	 * @param g The game
	 * @return A string determining the state of the game
	 */
	/*private String gameEnded(Game g) {
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
	}*/
}
