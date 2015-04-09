package player;

import scotlandyard.*;
import solution.ScotlandYardModel;
import scotlandyard.MoveTicket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    String graphFilename;
    private Graph map;
    List<RealPlayer> simulatedPlayers = new ArrayList<RealPlayer>();
    public int score (List<Move> move) {
            int score = 0;
            int danger = 0;
            final int mapCorner = 5;
            final int mapMain = 10;
            final int movePoint = 2;
            score = score + (move.size()*movePoint);
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
        this.graphFilename = graphFilename;
        ScotlandYardGraphReader read = new ScotlandYardGraphReader();
        try {map = read.readGraph(graphFilename);}
        catch(Exception e) { System.out.println("Inexistent file");}
    }
    public void initialise(int mrxLocation) {
    	for(Colour colour :view.getPlayers()) {
    		Map<Ticket, Integer> tickets = new HashMap<Ticket, Integer>();
    		if(colour.equals(Colour.Black)) {
    			tickets.put(Ticket.Bus, view.getPlayerTickets(Colour.Black, Ticket.Bus));
    			tickets.put(Ticket.Taxi, view.getPlayerTickets(Colour.Black, Ticket.Taxi));
    			tickets.put(Ticket.Underground, view.getPlayerTickets(Colour.Black, Ticket.Underground));
    			tickets.put(Ticket.Double, view.getPlayerTickets(Colour.Black, Ticket.Double));
    			tickets.put(Ticket.Secret, view.getPlayerTickets(Colour.Black, Ticket.Secret));
    			simulatedPlayers.add(new RealPlayer(Colour.Black,mrxLocation,tickets));
    		}
    		else {
    			tickets.put(Ticket.Bus, view.getPlayerTickets(colour, Ticket.Bus));
    			tickets.put(Ticket.Taxi, view.getPlayerTickets(colour, Ticket.Taxi));
    			tickets.put(Ticket.Underground, view.getPlayerTickets(colour, Ticket.Underground));
    			simulatedPlayers.add(new RealPlayer(colour,view.getPlayerLocation(colour),tickets));
    		}
    		
    	}
    	
    }
    public void modify(Colour colour,Move move) {
    	for(Colour colour1: view.getPlayers()) {
    		if(move instanceof MoveDouble) {
				getPlayer(colour).location = ((MoveDouble) move).move2.target;
				int valdb = getPlayer(colour).tickets.get(Ticket.Double);
				getPlayer(colour).tickets.put(Ticket.Double, valdb-1);
				int val1 = getPlayer(colour).tickets.get(((MoveDouble) move).move1.ticket);
				getPlayer(colour).tickets.put(((MoveDouble) move).move1.ticket, val1-1);
				int val2 = getPlayer(colour).tickets.get(((MoveDouble) move).move2.ticket);
				getPlayer(colour).tickets.put(((MoveDouble) move).move2.ticket, val2-1);
			}
    		else if(move instanceof MoveTicket) {
    			getPlayer(colour).location = ((MoveTicket) move).target;
    			int val = getPlayer(colour).tickets.get(((MoveTicket) move).ticket);
    			getPlayer(colour).tickets.put(((MoveTicket) move).ticket, val-1);
    		}
    		else
    			;
    	}
    			
    		
    }
    public RealPlayer getPlayer(Colour colour) {
        for(RealPlayer player: simulatedPlayers)
            if(player.colour.equals(colour))
                return player;
        return null;
    }
    @Override
    public Move notify(int location, Set<Move> moves) {
        //TODO: Some clever AI here ...
    	int score = 0;
    	Move selectMove = MoveTicket.instance(Colour.Black,Ticket.Bus, 0);
    	System.out.println("Size : "+moves.size());
    	for(Move move : moves) {
    		simulatedPlayers.clear();
    		initialise(location);
    		modify(Colour.Black,move);
    		Model testModel = new Model(view,simulatedPlayers,graphFilename,view.getRounds());
    		
    		int newScore = score(testModel.validMoves(Colour.Black));
    		if(newScore > score) {
    			
    			if(move instanceof MoveDouble)
    				selectMove = MoveDouble.instance(Colour.Black,((MoveDouble) move).move1,((MoveDouble) move).move2);
    			else if(move instanceof MoveTicket)
    				selectMove = MoveTicket.instance(Colour.Black,((MoveTicket) move).ticket,((MoveTicket) move).target);
    			else
    				;
    			score = newScore;
    		}
    		System.out.println(score + "  "+selectMove.toString());
    	}
        /*int choice = new Random().nextInt(moves.size());
        for (Move move : moves) {
            if (choice == 0) {
                return move;
            }
            choice--;
        }*/
    	System.out.println("AI Move " +selectMove.toString());
        return selectMove;
    }
    
    
    	    
}
