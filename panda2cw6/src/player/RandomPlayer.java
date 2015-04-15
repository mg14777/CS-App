package player;

import scotlandyard.*;
import solution.ScotlandYardModel;
import scotlandyard.MoveTicket;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.io.*;
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
    final int totalNodes;
    final int INF = 10000;
    public int depth = 2; 
    public Move selectMove = MoveTicket.instance(Colour.Black,Ticket.Bus, 0);
    int [][] distanceMatrix; //= new int[totalNodes][totalNodes];
    List<RealPlayer> simulatedPlayers = new ArrayList<RealPlayer>();
    public int score (List<Move> move) {
    		System.out.println("Score Move size: "+move.size());
            int score = 0;
            int distance = 0;
            final int mapCorner = 5;
            final int mapMain = 10;
            final int movePoint = 2;
            score = score + (move.size()*movePoint);
            int neighbours = map.getEdges(view.getPlayerLocation(Colour.Black)).size();
            if(neighbours < 4)
            	score += mapCorner;
            else
            	score += mapMain;
            for(Colour colour: view.getPlayers()) {
            	if(!colour.equals(Colour.Black)) {
            		 //System.out.println("Location(Black,Detective) :   "+(getPlayer(Colour.Black).location-1)+" "+(getPlayer(colour).location-1));
            		score += distanceMatrix[getPlayer(Colour.Black).location-1][getPlayer(colour).location-1];
            		distance += distanceMatrix[getPlayer(Colour.Black).location-1][getPlayer(colour).location-1];
            	}
            }
            //System.out.println(distance);
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
        totalNodes = map.getNodes().size();
        this.distanceMatrix = new int[totalNodes][totalNodes];
        
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
    			System.out.println("X Location "+mrxLocation);
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
    	//for(Colour colour1: view.getPlayers()) {
    		if(move instanceof MoveDouble) {
    			
				getPlayer(colour).location = ((MoveDouble) move).move2.target;
				int valdb = getPlayer(colour).tickets.get(Ticket.Double);
				getPlayer(colour).tickets.put(Ticket.Double, valdb-1);
				int val1 = getPlayer(colour).tickets.get(((MoveDouble) move).move1.ticket);
				getPlayer(colour).tickets.put(((MoveDouble) move).move1.ticket, val1-1);
				int val2 = getPlayer(colour).tickets.get(((MoveDouble) move).move2.ticket);
				getPlayer(colour).tickets.put(((MoveDouble) move).move2.ticket, val2-1);
				System.out.println(colour.toString() + " Double " +getPlayer(colour).tickets.get(Ticket.Double));
			}
    		else if(move instanceof MoveTicket) {
    			getPlayer(colour).location = ((MoveTicket) move).target;
    			int val = getPlayer(colour).tickets.get(((MoveTicket) move).ticket);
    			getPlayer(colour).tickets.put(((MoveTicket) move).ticket, val-1);
    			System.out.println(colour.toString() + " Single "+getPlayer(colour).tickets.get(((MoveTicket) move).ticket));
    		}
    		else
    			;
    	//}
    			
    		
    }
    public void modifyModel(Model model,Move move) {
    	if(move instanceof MoveDouble) {
			model.getPlayer(move.colour).location = ((MoveDouble) move).move2.target;
			int valdb = model.getPlayer(move.colour).tickets.get(Ticket.Double);
			model.getPlayer(move.colour).tickets.put(Ticket.Double, valdb-1);
			int val1 = model.getPlayer(move.colour).tickets.get(((MoveDouble) move).move1.ticket);
			model.getPlayer(move.colour).tickets.put(((MoveDouble) move).move1.ticket, val1-1);
			int val2 = model.getPlayer(move.colour).tickets.get(((MoveDouble) move).move2.ticket);
			model.getPlayer(move.colour).tickets.put(((MoveDouble) move).move2.ticket, val2-1);
			System.out.println(move.colour.toString() + " Double " +model.getPlayer(move.colour).tickets.get(Ticket.Double));
		}
		else if(move instanceof MoveTicket) {
			model.getPlayer(move.colour).location = ((MoveTicket) move).target;
			int val = model.getPlayer(move.colour).tickets.get(((MoveTicket) move).ticket);
			model.getPlayer(move.colour).tickets.put(((MoveTicket) move).ticket, val-1);
			System.out.println(move.colour.toString() + " Single "+model.getPlayer(move.colour).tickets.get(((MoveTicket) move).ticket));
		}
		else
			;
    	 if(move.colour!= Colour.Black)                                          //if detective -> add used ticket to MrX
             model.getPlayer(Colour.Black).tickets.put(((MoveTicket) move).ticket, model.getPlayerTickets(Colour.Black,((MoveTicket) move).ticket) + 1);
         else
             model.roundCounter++;                                                     //if MrX -> go to next round
    	
    }
    public List<Integer> neighbours(int node) {
    	List<Integer> neighbours = new ArrayList<Integer>();
    	List<Edge<Integer,Route>> edges = new ArrayList<Edge<Integer,Route>>(map.getEdges(node));
    	for(Edge<Integer,Route> edge: edges) {
    		if(edge.source()==node)
    			neighbours.add(edge.target());
    		else
    			neighbours.add(edge.source());
    	}
    	return neighbours;
    }
    public void floydMarshall() {
    	//Initialize the matrix
    	for(int i=0;i<totalNodes;i++) {
    		List<Integer> neighbours = new ArrayList<Integer>(neighbours(i+1));
    		for(int j=0;j<totalNodes;j++) {
    			if(neighbours.contains(j+1))
    				distanceMatrix[i][j] = 1;
    			else if(i==j)
    				distanceMatrix[i][j] = 0;
    			else
    				distanceMatrix[i][j] = INF;
    		}
    	}
    	//Iterating through intermediate vertex(k)
    	for(int k=0;k<totalNodes;k++) {
    		//Iterating through source vertex(i)
    		for(int i=0;i<totalNodes;i++) {
    			//Iterating through destination vertex(j)
    			for(int j=0;j<totalNodes;j++) {
    				if(distanceMatrix[i][k]+distanceMatrix[k][j] < distanceMatrix[i][j])
    					distanceMatrix[i][j] = distanceMatrix[i][k]+distanceMatrix[k][j];
    			}
    				
    		}
    	}
    	File file = new File("out.txt");
    	try {
    	file.createNewFile();
    	PrintWriter writer = new PrintWriter(file);
    	for(int i=0;i<totalNodes;i++) {
    		writer.write(Integer.toString(i+1)+"       ");
    		for(int j=0;j<totalNodes;j++) {
    			writer.write(Integer.toString(distanceMatrix[i][j])+" ");
    		}
    		writer.println("");
    	}
    	writer.flush();
    	writer.close();
    	}
    	catch(Exception e) { System.out.println("Inexistent file");}
    }
    public RealPlayer getPlayer(Colour colour) {
        for(RealPlayer player: simulatedPlayers)
            if(player.colour.equals(colour))
                return player;
        return null;
    }
    public int minMove(Model model,int currentDepth) {
    	int bestScore = 90000;
    	int score = 0;
    	if(model.isGameOver()) {
    		if(model.winnerMrX)
    			return 10000;
    		else 
    			return -10000;
    	}
    	for(Move move1: model.validMoves(Colour.Blue)) {
    		Model testModel = new Model(model.getPlayers(),graphFilename,view.getRounds(),model.roundCounter);
    		modifyModel(testModel,move1);
    		for(Move move2: testModel.validMoves(Colour.Green)) {
    			modifyModel(testModel,move2);
    			for(Move move3: testModel.validMoves(Colour.Red)) {
        			modifyModel(testModel,move3);
        			for(Move move4: testModel.validMoves(Colour.White)) {
            			modifyModel(testModel,move4);
            			for(Move move5: testModel.validMoves(Colour.Yellow)) {
            				Model testModelReal = new Model(model.getPlayers(),graphFilename,view.getRounds(),model.roundCounter);
            				modifyModel(testModelReal,move1);
            				modifyModel(testModelReal,move2);
            				modifyModel(testModelReal,move3);
            				modifyModel(testModelReal,move4);
            				modifyModel(testModelReal,move5);
            				if(currentDepth == depth){
            	        		score = score(testModelReal.validMoves(Colour.Black));
            	        	}
            	    		else
            	    			score = maxMove(testModelReal,currentDepth+1);
            	    		
            	    		if(score < bestScore) {
            	    			bestScore = score;
            	    		}
            			}
        			}
    			}
    		}
    	}
    	return bestScore;
    }
    public int maxMove(Model model,int currentDepth) {
    	int bestScore = 0;
    	int score = 0;
    	if(model.isGameOver()) {
    		if(model.winnerMrX)
    			return 10000;
    		else 
    			return -10000;
    	}
    	
    	for(Move move : model.validMoves(Colour.Black)) {
    		Model testModel = new Model(model.getPlayers(),graphFilename,view.getRounds(),model.roundCounter);
    		modifyModel(testModel,move);
    		if(currentDepth == depth){
        		score = score(testModel.validMoves(Colour.Black));
        	}
    		else
    			score = minMove(testModel,currentDepth+1);
    		
    		if(score > bestScore) {
    			bestScore = score;
    			if(move instanceof MoveDouble)
    				selectMove = MoveDouble.instance(Colour.Black,((MoveDouble) move).move1,((MoveDouble) move).move2);
    			else if(move instanceof MoveTicket)
    				selectMove = MoveTicket.instance(Colour.Black,((MoveTicket) move).ticket,((MoveTicket) move).target);
    			else
    				;
    		}
    	}
    		
    	return bestScore;
    }
    @Override
    public Move notify(int location, Set<Move> moves) {
        //TODO: Some clever AI here ...
    	int score = 0;
    	int i = 0;
    	Move selectMove = MoveTicket.instance(Colour.Black,Ticket.Bus, 0);
    	System.out.println("Size : "+moves.size());
    	floydMarshall();
    	initialise(location);
    	Model testModel1 = new Model(simulatedPlayers,graphFilename,view.getRounds(),view.getRound());
		//System.out.println(testModel1.getPlayerLocation(Colour.Blue));
    	for(Move move1: moves ) {
    		int flag = 0;
    		for(Move move2: testModel1.validMoves(Colour.Black)) {
    			if(move1.equals(move2)) {
    				flag++;
    			}
    		}
    		if(flag>1)
    			System.out.println(move1.toString());
    	}
    	System.out.println("before this ");
    	Model root = new Model(simulatedPlayers,graphFilename,view.getRounds(),view.getRound());
    	// MINI MAX CALL
    	maxMove(root,1);
    	
		
    	/*
    	for(Move move : moves) {
    		simulatedPlayers.clear();
    		initialise(location);
    		modify(Colour.Black,move);
    		Model testModel = new Model(simulatedPlayers,graphFilename,view.getRounds(),view.getRound());
    		System.out.println(testModel.getPlayerLocation(Colour.Black));
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
    		i++;
    		//System.out.println(score + "  "+selectMove.toString());
    	}
    	*/
       
    	System.out.println("Int i: "+i);
    	System.out.println("AI Move " +selectMove.toString());
        return selectMove;
    }
    
    
    	    
}
