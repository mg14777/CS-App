package solution;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import scotlandyard.*;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import java.util.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.*;
import javax.swing.border.*;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.*;
class ScotlandYardApplication  {
	int numberOfDetectives = 1 ;	//test detectives
	Integer index = -1;
	Integer index1 = 0;
	List<Boolean> rounds = new ArrayList<Boolean>(); //test rounds
	ScotlandYardViewer view;
	ScotlandYardModel model;
	MainMenu mainMenu;
	NewGame newgame;
	Join join;
	GameOver over;
	Replay replay;
	List<Integer> locations = new ArrayList<Integer>(Arrays.asList(13,123,138,141,155,174,29,34,117,94,112,26,50,91,103,53));
	List<Integer> toBeEnabled = new ArrayList<Integer>();
	List<Move> currentMoves;
	AudioStream audioStream;
	LoadMenu menu;
	int loadFlag;
	ScotlandYardApplication() {
		this.view = new ScotlandYardViewer(ScotlandYardApplication.this);
		this.mainMenu = this.view.mainMenu;
		mainMenu.addNewGameListener(new NewGameListener());
		mainMenu.addLoadGameListener(new LoadListener());
		mainMenu.addOptionListener(new OptionsListener());
		mainMenu.addExitGameListener(new ExitGameListener());
		
	}
	/**
	 * 
	 * @return List of Valid Moves for Current Player
	 */
	protected List<Move> currentMoves() {
		return model.validMoves(model.getCurrentPlayer());
	}
   
	class SelectListener implements ActionListener {
		public void actionPerformed(ActionEvent actionEvent) {
			view.interval = view.intervalw;
			view.t = view.tw;
			view.timer.cancel();
			playSound();
			index = view.list.getSelectedIndex();
		}
	}
	
	class PossibleListener implements ActionListener {
		public void actionPerformed(ActionEvent actionEvent) {
			Set aux = model.MrXpossibleLocations();
			if(aux!= null&&!aux.contains(0))
				view.drawMrXPossibleLocation(aux);
		}
	}
	class NewGameListener extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			newgame = new NewGame();
			newgame.addOKListener(new OKListener());	
			playSound();
			loadFlag = 0;
		}
	}
	class OptionsListener extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			Options option = new Options();
			option.addOnListener(new OnListener());
			option.addOffListener(new OffListener());
			option.addBackListener(new BackListener(option));
			view.getContentPane().remove(mainMenu);
			view.getContentPane().add(option);
			view.revalidate();
			view.repaint();
			view.pack();
			playSound();
		}
	}
	class OnListener extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			AudioPlayer.player.start(view.audioStream);
			playSound();
		}
	}
	class OffListener extends MouseAdapter {
			public void mouseClicked(MouseEvent e) {
				AudioPlayer.player.stop(view.audioStream);
				playSound();
			}
			
	}
	class BackListener extends MouseAdapter {
		Options option;
		BackListener(Options option) {
			this.option = option;
		}
		public void mouseClicked(MouseEvent e) {
			view.getContentPane().remove(option);
			view.getContentPane().add(mainMenu);
			view.revalidate();
			view.repaint();
			playSound();
		}
		
	}
	class ExitGameListener extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			playSound();
			System.exit(0);			
		}
	}
	
	class OKListener implements ActionListener {
		public void actionPerformed(ActionEvent actionEvent) {
			playSound();
			numberOfDetectives = Integer.parseInt(newgame.field.getText());
			newgame.frame.dispatchEvent(new WindowEvent(newgame.frame, WindowEvent.WINDOW_CLOSING));
			for(int i=0;i<10;i++) {
				if((i>=3)&&  (((i-3)%5) == 0))
					rounds.add(true);
				else
					rounds.add(false);
			}
			try{
				model = new ScotlandYardModel(numberOfDetectives, rounds, "graph.txt");
				} 
			catch(Exception i) { 
				System.out.println("Error!");
			}
			for(int i=1;i<6;i++)
				toBeEnabled.add(i);
			join = new Join(); 
			join.join.addActionListener(new JoinListener(Colour.Black,0));
			join.join1.addActionListener(new JoinListener(Colour.Green,1));
			join.join2.addActionListener(new JoinListener(Colour.Blue,2));
			join.join3.addActionListener(new JoinListener(Colour.White,3));
			join.join4.addActionListener(new JoinListener(Colour.Red,4));
			join.join5.addActionListener(new JoinListener(Colour.Yellow,5));
			
			join.njoin1.addActionListener(new RemoveListener(new JoinListener(Colour.Green,1)));
			join.njoin2.addActionListener(new RemoveListener(new JoinListener(Colour.Blue,2)));
			join.njoin3.addActionListener(new RemoveListener(new JoinListener(Colour.White,3)));
			join.njoin4.addActionListener(new RemoveListener(new JoinListener(Colour.Red,4)));
			join.njoin5.addActionListener(new RemoveListener(new JoinListener(Colour.Yellow,5)));
		}
			
	}
	class LoadListener extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {	//loads game info from file
			loadFlag = 1;
			menu = new LoadMenu();
			String workingDir = System.getProperty("user.dir");
			menu.createFile(menu.textFiles(workingDir));
			menu.fileBox.addActionListener( new ActionListener() {
	    		public void actionPerformed(ActionEvent e) {
	    			menu.preLoadFile(menu.fileBox.getSelectedItem().toString());
	    		}
			}	);
			menu.addBackListener(new BackLoadListener());
			menu.addOKListener(new OkLoadListener(ScotlandYardApplication.this));
			view.getContentPane().remove(mainMenu);
			view.getContentPane().add(menu);
			view.revalidate();
			view.repaint();
			playSound();
			
		}
		
		class BackLoadListener extends MouseAdapter {
			public void mouseClicked(MouseEvent e) {
				view.getContentPane().remove(menu);
				view.getContentPane().add(mainMenu);
				view.revalidate();
				view.repaint();
				playSound();
			}
		}
	}
	class JoinListener implements ActionListener {
		
		
		Colour colour;
		List<JButton> buttons = new ArrayList<JButton>();
		List<JButton> nbuttons = new ArrayList<JButton>();
		int i,location;
		
		Map<Ticket,Integer> tickets;
		Player human;
		List<Colour> colours;
		JoinListener(Colour colour,int i) {
			this.colour = colour;
			buttons.add(join.join);
			buttons.add(join.join1);
			buttons.add(join.join2);
			buttons.add(join.join3);
			buttons.add(join.join4);
			buttons.add(join.join5);
			nbuttons.add(join.njoin);
			nbuttons.add(join.njoin1);
			nbuttons.add(join.njoin2);
			nbuttons.add(join.njoin3);
			nbuttons.add(join.njoin4);
			nbuttons.add(join.njoin5);
			this.i = i;
			initialise(colour);
			
		}
		public void initialise(Colour colour) {
			if(colour.equals(Colour.Black)) {
				human = new HumanPlayer();
				Random rand = new Random();
				int[] locations = {78,45,35,71,132,51,106,104,127,146,166,170,172};
				this.location = locations[rand.nextInt(5)];
				this.colour = Colour.Black;
				this.tickets = new HashMap<Ticket, Integer>();
				this.tickets.put(Ticket.Bus, 3);
				this.tickets.put(Ticket.Taxi, 4);
				this.tickets.put(Ticket.Underground, 3);
				this.tickets.put(Ticket.DoubleMove, 2);
				this.tickets.put(Ticket.SecretMove, numberOfDetectives);  //As many secret moves as detectives 
				
			}
			else {
				human = new HumanPlayer();
				Random rand = new Random();
				int delindex = rand.nextInt(locations.size());
				this.location = locations.get(delindex);
				locations.remove(delindex);
				this.colour = colour;
				this.tickets = new HashMap<Ticket, Integer>();
				this.tickets.put(Ticket.Bus, 8);
				this.tickets.put(Ticket.Taxi, 10);
				this.tickets.put(Ticket.Underground, 4);
				this.tickets.put(Ticket.DoubleMove, 0);
				this.tickets.put(Ticket.SecretMove, 0); 
				
			}
				
		}
		JoinListener(Colour colour,int i, Map<Ticket,Integer> tickets,int location) {
			this.colour = colour;
			this.tickets = tickets;
			this.location = location;
			this.i = i;
			buttons.add(join.join);
			buttons.add(join.join1);
			buttons.add(join.join2);
			buttons.add(join.join3);
			buttons.add(join.join4);
			buttons.add(join.join5);
			nbuttons.add(join.njoin);
			nbuttons.add(join.njoin1);
			nbuttons.add(join.njoin2);
			nbuttons.add(join.njoin3);
			nbuttons.add(join.njoin4);
			nbuttons.add(join.njoin5);
		
			if(colour.equals(Colour.Black)) {
				human = new HumanPlayer();
				
			}
			else {
				human = new HumanPlayer();
			}
			
		}
	
		public void actionPerformed(ActionEvent actionEvent) {	
			playSound();
			if(human instanceof HumanPlayer)
				((HumanPlayer) human).register(ScotlandYardApplication.this);
			buttons.get(i).setEnabled(false);
			nbuttons.get(i).setEnabled(true);
			if(colour.equals(Colour.Black)) {
				
				model.join(human,colour,location,tickets);
				for(int i: toBeEnabled) {
					buttons.get(i).setEnabled(true);
				}
			}
			else 
				model.join(human,colour,location,tickets);
			if(model.isReady()) {
				join.frame.dispatchEvent(new WindowEvent(join.frame, WindowEvent.WINDOW_CLOSING));
				toBeEnabled.clear();
				buttons.clear();
				if(loadFlag == 1)
					view.getContentPane().remove(menu);
				view.startGame();
				view.side.updateRound(-1,model.getRound());
				view.side.updateTicket();
				view.addShowAllListener(new ShowAllListener());
				view.addSelectListener(new SelectListener());
				view.game.addMapClickedListener(new clickOnMap());
				view.addSaveListener(new SaveListener(model,numberOfDetectives));
				index1 = 1;
			}
		}
	}
	class RemoveListener implements ActionListener {
		JoinListener join;
		RemoveListener(JoinListener join) {
			this.join = join;
		}
		public void actionPerformed(ActionEvent actionEvent) {
			playSound();
			join.nbuttons.get(join.i).setEnabled(false);
			join.buttons.get(join.i).setEnabled(true);
			model.removePlayer(join.colour);
		}
	}
	//creates a list of the locations that have to be updated and sends it to the view
	public void locationToDraw() {		
		view.game.repaint();
		List<Integer> x = new ArrayList<Integer>();
		for(Colour colour : model.getPlayers()) {
			Integer aux = model.getPlayerLocation(colour);
				x.add(aux);
		}
		view.drawPlayer(x);	
	}
	
	public List<Move> getCurrentMoves() {
		return currentMoves;
	}
	public void setCurrentMoves(List<Move> currentMoves) {
		this.currentMoves = currentMoves;
	}
	
	//goes through all points and sees if the click was in the radius of a point
	class clickOnMap extends MouseAdapter {	   //Event for mapClick drawing
		int radius = 10;
		private void CheckClickAndDraw(int node, int nodeX, int nodeY) {		//1 for valid, 2 invalid and 0 for not drawing
			int valid = 2;
			List<Move> movesList = currentMoves(), newList = new ArrayList<Move>();
			for(Integer target: model.possibleDestinations(movesList)) 
				if(target.equals(node)) {
					valid = 1;
					break;
				}
			for(Move move: movesList)
	    		if(move instanceof MoveTicket&&((MoveTicket) move).target == node) 
	    			newList.add(move);
	    		else if(move instanceof MoveDouble&&((MoveTicket)(((MoveDouble) move).moves.get(1))).target==node) 
	    			newList.add(move);
			if(movesList!=null)
			view.currentMoveList(newList);
			currentMoves = newList;
			view.game.drawMapClick(valid, nodeX, nodeY);
		}
		public void mouseClicked(MouseEvent e) {
			
			int clickX=e.getX();
		    int clickY=e.getY();
		    for(Integer node: view.coordinateMap.keySet()) {
		    	int x = view.coordinateMap.get(node).get(0);
		    	int y = view.coordinateMap.get(node).get(1);
		    	if((Math.pow((clickX-x), 2.0) + Math.pow((clickY-y), 2.0)<=Math.pow(radius, 2.0))) {
		    		CheckClickAndDraw(node, x, y);
		    		break;
		    	}
		    }
		}
	}   
	
	class ReplayListener implements ActionListener {
		public void actionPerformed(ActionEvent actionEvent) {
			replay = new Replay(view);
			replay.addPlayListener(new PlayListener());
			replay.addForwardListener(new ForwardListener());
			replay.addBackPlayListener(new BackPlayListener());
			replay.loadReplay();
			playSound();
		}
	}
	class PlayListener implements ActionListener {
		public void actionPerformed(ActionEvent actionEvent) {
			replay.playOn = !replay.playOn;
			if(replay.playOn) 
				replay.play.setText("Stop");
			else {
				replay.play.setText("Play");
				replay.timer.cancel();
			}
			replay.displayOnMap();
		}
	}
	class ForwardListener implements ActionListener {
		public void actionPerformed(ActionEvent actionEvent) {
			replay.displayOnMap();
		}
	}
	class BackPlayListener implements ActionListener {
		public void actionPerformed(ActionEvent actionEvent) {
			if(replay.currentLineNumber>=2) {
				replay.currentLineNumber--;
				replay.currentLineNumber--;
			}
			replay.displayOnMap();
		}
	}
	class OkOverListener implements ActionListener {
		public void actionPerformed(ActionEvent actionEvent) {
			System.exit(0);
		}
	}
	public void paintValidLocations() {
		Set<Integer> loc = model.possibleDestinations(model.validMoves(model.getCurrentPlayer()));
		if(loc!=null)
			view.drawPossibleLocation(loc);
	}
	class ShowAllListener implements ActionListener {
		public void actionPerformed(ActionEvent actionEvent) {
			paintValidLocations();
			view.currentMoveList(model.validMoves(model.getCurrentPlayer()));
		}
	}
	 public void updateViewerList() {
	    	view.currentMoves = model.validMoves(model.getCurrentPlayer());
	    }
	public void playSound() {
		try {
			String soundName = "./resources/click.wav";    
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(soundName).getAbsoluteFile());
			Clip clip = AudioSystem.getClip();
			clip.open(audioInputStream);
			clip.start();
			}
			catch(Exception e) {
			System.err.println("ErrorH");
			}
	}
	
	public static void main(String[] args) throws Exception {
		ScotlandYardApplication c = new ScotlandYardApplication();
		while(c.index1 == 0) {
			try{Thread.sleep(10);}catch(Exception e){ System.out.println("Error");}
		}
		c.view.game.initialisePlayers(c.model.getPlayers());
		c.locationToDraw();
			
		c.model.start();
		c.over = new GameOver(c.model.getWinningPlayers());
		c.over.addOkOverListener(c.new OkOverListener());
		c.over.addReplayListener(c.new ReplayListener());
		
		
	}
}