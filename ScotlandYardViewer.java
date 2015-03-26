package solution;
import java.io.File;
import scotlandyard.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener; 
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.io.*;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

public class ScotlandYardViewer extends JFrame {
	JButton exit = new JButton("Exit");
	JComboBox list;
	JButton save = new JButton("Save Game");
	JButton select = new JButton("Done");
	JButton showAll = new JButton("All Moves");
	JLabel current = new JLabel();
	JLabel currentPos = new JLabel();
	List<Move> currentMoves  = new ArrayList<Move>();
	JPanel bar = new JPanel();
	Box box;
	public GameView game = new GameView();
	static Timer timer;
	int tw,intervalw;
	int interval = intervalw =  60;
	JLabel time;
	int i = 1;
	int delay = 1000;
    int period = 1000;
	InputStream in;
	SidePanel side;
	XLog xlog;
	AudioStream audioStream;
	FileWriter writer;
	PrintWriter printer;
	int t = tw = 0;
	static int random;
	static ScotlandYardApplication controller;
	public MainMenu mainMenu = new MainMenu();
	Map<Integer, List<Integer>> coordinateMap = new HashMap<Integer, List<Integer>>();	//coordonates from pos file
	/**
	 * Initialises the frame and position file
	 * @param controller 
	 */
	ScotlandYardViewer(ScotlandYardApplication controller) {
		super("Scotland Yard Game V0.001");
		list = new JComboBox();
		time = new JLabel();
		this.controller = controller;
		this.add(mainMenu);
		this.pack();
		this.setLocationByPlatform(true);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setVisible(true);
		
		try {
			audioStream = new AudioStream(new FileInputStream("./resources/Elitsa_Alexandrova_-_Assassin_39_s_Creed_Rogue_Mai.wav"));
		}
		catch(Exception e) {
			System.err.println("ErrorH");
		}	
		playSound();
		readPosFile();

	}
	
	/**
	 * Initialises the game map and creates the layout for the actual game
	 */
	public void startGame() {
		
		JScrollPane scrollPane = new JScrollPane(game);
		scrollPane.setBackground(Color.BLACK);
		current.setFont(new Font("Castellar",Font.PLAIN,20));
		current.setForeground(Color.GREEN);
		currentPos.setFont(new Font("Verdana",Font.PLAIN,20));
		currentPos.setForeground(Color.YELLOW);
		time.setFont(new Font("Georgia",Font.PLAIN,20));
		time.setForeground(Color.RED);
		bar.add(currentPos);
		bar.add(current);
		bar.add(showAll);
		bar.add(list);
		bar.add(save);
		bar.add(select);
		bar.add(exit);
		bar.add(time);
		exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }});
		add(bar,BorderLayout.SOUTH);
		bar.setBackground(Color.BLACK);
		bar.setBorder( BorderFactory.createLineBorder(Color.BLUE,5));
		
		side = new SidePanel(controller);
		side.setBorder( BorderFactory.createLineBorder(Color.BLUE,5));
		JScrollPane scrollPane1 = new JScrollPane(side);
		xlog = new XLog(controller.model.getRounds().size());
		xlog.addPossibleListener(controller.new PossibleListener());
		xlog.setBorder( BorderFactory.createLineBorder(Color.BLUE,5));
		add(xlog,BorderLayout.NORTH);
  		box = Box.createHorizontalBox();
  		box.add(scrollPane);
  		box.add(scrollPane1);
  		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);  
  		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);  
  		AudioPlayer.player.stop(audioStream);
		this.getContentPane().remove(mainMenu);
		this.getContentPane().add(box);
		this.setSize(1024,800);
		this.revalidate();
		this.repaint();
		try {		
			writer = new FileWriter("./resources/replay.txt");
			printer = new PrintWriter(writer);
			for(Colour colour: controller.model.getPlayers())
				printer.print(colour.toString() + " ");	
			printer.println();	
			printer.flush();
		} catch(Exception e) { System.out.println("Inexistent file");}
	}
	
	/**
	 * Plays sound on button click
	 */
	public void playSound() {
		AudioPlayer.player.start(audioStream);
	}
	
	/**
	 * Reads and creates a map with the (x,y) coordinates of the game nodes
	 */
	public void readPosFile() {
		File file = new File("./resources/pos.txt");	
		Scanner in = null;
        try {
			in = new Scanner(file);
		} 
        catch (FileNotFoundException e) {
			System.out.println(e);
		}
        // get the number of nodes
        String topLine = in.nextLine();
        int numberOfNodes = Integer.parseInt(topLine);  
        for(int i = 0; i < numberOfNodes; i++) {
        	String line = in.nextLine();
        	String[] parts = line.split(" ");
        	List<Integer> pos = new ArrayList<Integer>();
        	pos.add(Integer.parseInt(parts[1]));
        	pos.add(Integer.parseInt(parts[2]));
        	Integer key = Integer.parseInt(parts[0]);
        	coordinateMap.put(key, pos);
        }
        
	}
	
	/**
	 * get the coordinates to draw players on map;
	 * null if position is 0 and it is taken care of inside GameViewDraw
	 * @param positions List containing all players positions
	 */
	public void drawPlayer(List<Integer> positions) {			
			List<Point> coords = new ArrayList<Point>(); 
			List<Integer> aux;
			for(Integer position:positions) {
				if(position == 0) {
					coords.add(null);
					
				}
				else {
				aux = coordinateMap.get(position);
				
				coords.add(new Point(aux.get(0), aux.get(1)));
				}
			}
			saveCoordsAndMoveToFile(coords);
			game.paintPlayers(coords);
		}
	
	public void addSaveListener(ActionListener listenStartButton) { //registers observer to DONE/Select button
 		save.addActionListener(listenStartButton);
 	} 
	
	/**
	 * Updates the combo box with the current moves
	 * @param moves list of moves
	 */
	protected void currentMoveList(List<Move> moves) {				//displays the move list to comboBox
		list.removeAllItems();
		for(Move move : moves) {
			list.addItem(move.toString());			
		}
		current.setText("Current Player : "+controller.model.getCurrentPlayer().toString()+"   ");
		if(controller.model.getCurrentPlayer().equals(Colour.Black))
			currentPos.setText("Position : "+controller.model.mrxActual()+"   ");
		else
			currentPos.setText("Position : "+controller.model.getPlayerLocation(controller.model.getCurrentPlayer())+"   ");
		side.updateTicket();	
	}
	
	public void timer() {
		random = currentMoves.size();
		 timer = new Timer();	    
		    timer.scheduleAtFixedRate(new TimerTask() {
		        public void run() {
		        	int count = setInterval();
		        	if(t==0 && count<10)
		        		time.setText("    Time Left:  0"+t+":0"+Integer.toString(count));
		        	else if(count<10)
		        		time.setText("    Time Left: "+t+":0"+Integer.toString(count));
		        	else if(t==0)
		        		time.setText("    Time Left:  0"+t+":"+Integer.toString(count));
		        	else
		        		time.setText("    Time Left:  "+t+":"+Integer.toString(count));
		        }
		    }, delay, period);
	}
	
	private final int setInterval() {
	    if (interval == 1 && t==0) {
	        timer.cancel();
	        t = tw;
	        interval = intervalw;
	        Random rand = new Random();
	        controller.index = rand.nextInt(random);
	    }
	    if(interval == 1) {
	    	t--;
	    	interval = intervalw;
	    }
	    	return --interval;
	}
	
	/**
	 * Saves the player coordinates and moves to file for replay
	 * @param coords list of point(x,y) coordinates
	 */
	void saveCoordsAndMoveToFile(List<Point> coords) {
			for(Point point: coords)
				if(point != null)
					printer.print(point.getX() + " " + point.getY() + " ");
				else
					printer.print(0 + " " + 0 + " ");
			printer.print(controller.model.getCurrentMove());
			printer.println();
			printer.flush();
		}
	
	/**
	 * Draws all the players to the specified locations
	 * @param locations	where to draw players
	 */
	public void drawPossibleLocation(Set<Integer> locations) {
		List<Point> coords = new ArrayList<Point>();
		List<Integer> aux;
		for(Integer location: locations) {
			aux = coordinateMap.get(location);
			coords.add(new Point(aux.get(0), aux.get(1)));
		}
		game.setValidLocations(3, coords);
	}
	
	/**
	 * Draws all the possible locations for MrX
	 * @param locations where can MrX go
	 */
	public void drawMrXPossibleLocation(Set<Integer> locations) {
		List<Point> coords = new ArrayList<Point>();
		List<Integer> aux;
		for(Integer location: locations) {
			aux = coordinateMap.get(location);
			coords.add(new Point(aux.get(0), aux.get(1)));
		}
		game.setValidLocations(4, coords);
	}
	
 	public void addSelectListener(ActionListener listenSelectButton) {
 		select.addActionListener(listenSelectButton);
 	} 
 	
 	public void addShowAllListener(ActionListener listenSelectButton) {
 		showAll.addActionListener(listenSelectButton);
 	} 
}