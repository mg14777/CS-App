package player;

import scotlandyard.Move;
import scotlandyard.Player;
import scotlandyard.ScotlandYardView;
import scotlandyard.ScotlandYardGraphReader;
import scotlandyard.*;
import scotlandyard.Graph;
import scotlandyard.Colour;
import scotlandyard.MoveTicket;
import java.util.Random;
import java.util.Set;

/**
 * The RandomPlayer class is an example of a very simple AI that
 * makes a random move from the given set of moves. Since the
 * RandomPlayer implements Player, the only required method is
 * notify(), which takes the location of the player and the
 * list of valid moves. The return value is the desired move,
 * which must be one from the list.
 */
public class RandomPlayer implements Player {
    ScotlandYardView view;
    private Graph map;
    public int score (Set<Move> move) {
            int score = 0;
            int danger = 0;
            final int mapCorner = 5;
            final int mapMain = 10;
            final int movePoint = 2;
            score = score + (move.size()*2);
            int neighbours = map.getEdges(view.getPlayerLocation(Colour.Black)).size();
            if(neighbours < 4)
            	score += mapCorner;
            else
            	score += mapMain;
            /*for(Colour player: view.getPlayers()) {
                for(Move singleMove: move) 
                    if(singleMove instanceof MoveTicket)
                        if(singleMove.target == view.getPlayerLocation(player))
                            danger++;
            }
            */
            
            return score;
            
    }
    public RandomPlayer(ScotlandYardView view, String graphFilename) {
        //TODO: A better AI makes use of `view` and `graphFilename`.
        this.view = view;
        ScotlandYardGraphReader read = new ScotlandYardGraphReader();
        try {map = read.readGraph(graphFilename);}
        catch(Exception e) { System.out.println("Inexistent file");}
    }

    @Override
    public Move notify(int location, Set<Move> moves) {
        //TODO: Some clever AI here ...

        int choice = new Random().nextInt(moves.size());
        for (Move move : moves) {
            if (choice == 0) {
                return move;
            }
            choice--;
        }

        return null;
    }
}
