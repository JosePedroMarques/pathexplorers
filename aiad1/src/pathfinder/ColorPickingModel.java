package pathfinder;

import uchicago.src.reflector.ListPropertyDescriptor;
import uchicago.src.sim.engine.BasicAction;
import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimModelImpl;
import uchicago.src.sim.engine.SimInit;
import uchicago.src.sim.gui.DisplaySurface;
import uchicago.src.sim.gui.Object2DDisplay;
import uchicago.src.sim.space.Object2DGrid;
import uchicago.src.sim.space.Object2DTorus;
import uchicago.src.sim.util.Random;
import uchicago.src.sim.util.SimUtilities;
import uchicago.src.sim.analysis.OpenSequenceGraph;
import uchicago.src.sim.analysis.Sequence;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import agents.BasicAgent;
import agents.Captain;
import agents.Soldier;
import agents.Wall;



public class ColorPickingModel extends SimModelImpl {
	private static final char WALL = '#';
	private static final char SOLDIER = 's';
	private static final char CAPTAIN = 'c';
	private static final char EXIT = 'e';
	private ArrayList<BasicAgent> agentList;
	private Schedule schedule;
	private DisplaySurface dsurf;
	private Object2DGrid space;
	private OpenSequenceGraph plot;
	private int xSize;
	private int ySize;

	

	private int numberOfAgents, spaceSize;
	

	private Hashtable<Color, Integer> agentColors;

	public ColorPickingModel() {
		this.numberOfAgents = 100;
		this.spaceSize = 100;
		
	}

	public String getName() {
		return "Color Picking Model";
	}

	public String[] getInitParam() {
		return new String[] { "numberOfAgents", "spaceSize", "movingMode"};
	}

	public Schedule getSchedule() {
		return schedule;
	}

	public int getNumberOfAgents() {
		return numberOfAgents;
	}

	public void setNumberOfAgents(int numberOfAgents) {
		this.numberOfAgents = numberOfAgents;
	}

	public int getSpaceSize() {
		return spaceSize;
	}

	public void setSpaceSize(int spaceSize) {
		this.spaceSize = spaceSize;
	}



	public void setup() {
		schedule = new Schedule();
		if (dsurf != null) dsurf.dispose();
		dsurf = new DisplaySurface(this, "Color Picking Display");
		registerDisplaySurface("Color Picking Display", dsurf);
		/*
		// property descriptors
		Vector<MovingMode> vMM = new Vector<MovingMode>();
		for(int i=0; i<MovingMode.values().length; i++) {
			vMM.add(MovingMode.values()[i]);
		}
		descriptors.put("MovingMode", new ListPropertyDescriptor("MovingMode", vMM));*/
		
	}

	public void begin() {
		
		buildModel();
		buildDisplay();
		buildSchedule();
	}

	public void buildModel() {
		agentList = new ArrayList<BasicAgent>();
		readMap("textfile.txt");
		
		/*for (int i = 0; i<numberOfAgents; i++) {
			int x, y;
			do {
				x = Random.uniform.nextIntFromTo(0, space.getSizeX() - 1);
				y = Random.uniform.nextIntFromTo(0, space.getSizeY() - 1);
			} while (space.getObjectAt(x, y) != null);
			Color color =  new Color(Random.uniform.nextIntFromTo(0,255), Random.uniform.nextIntFromTo(0,255), Random.uniform.nextIntFromTo(0,255));
			ColorPickingAgent agent = new ColorPickingAgent(x, y, color, space);
			space.putObjectAt(x, y, agent);
			agentList.add(agent);
		}*/
	}

	private void buildDisplay() {
		// space and display surface
		Object2DDisplay display = new Object2DDisplay(space);
		display.setObjectList(agentList);
		dsurf.addDisplayableProbeable(display, "Agents Space");
		dsurf.display();

		
	}

	private void buildSchedule() {
		schedule.scheduleActionBeginning(0, new MainAction());
		schedule.scheduleActionAtInterval(1, dsurf, "updateDisplay", Schedule.LAST);
		schedule.scheduleActionAtInterval(1, plot, "step", Schedule.LAST);
	}


	class MainAction extends BasicAction {

		public void execute() {
		/*	// prepare agent colors hashtable
			agentColors = new Hashtable<Color,Integer>();

			// shuffle agents
			SimUtilities.shuffle(agentList);

			// iterate through all agents
			for(int i = 0; i < agentList.size(); i++) {
				ColorPickingAgent agent = (ColorPickingAgent) agentList.get(i);
				if(movingMode == MovingMode.Walk) {
					agent.walk();
				} else {
					agent.jump();
				}
				Color c = agent.recolor();
				int nAgentsWithColor = (agentColors.get(c) == null ? 1 : agentColors.get(c)+1); 
				agentColors.put(c, nAgentsWithColor);
			}*/
		}

	}


	public static void main(String[] args) {
		SimInit init = new SimInit();
		init.loadModel(new ColorPickingModel(), null, false);
	}
	public void readMap(String filename) {
		
		try{
			  int x = 0;
			  FileInputStream fstream = new FileInputStream(filename);
			  FileInputStream fstream2 = new FileInputStream(filename);
			  
			  DataInputStream in = new DataInputStream(fstream);
			  DataInputStream in2 = new DataInputStream(fstream2);
			  
			  BufferedReader br = new BufferedReader(new InputStreamReader(in));
			  BufferedReader br2 = new BufferedReader(new InputStreamReader(in2));
			  
			  String firstLine = br2.readLine();
			  
			  x = firstLine.length();
			  int y = 1;
			  while((firstLine = br2.readLine()) != null) {
					if(firstLine.length()> x)
						x = firstLine.length();
					y++;
				}

			  
			  xSize = x;
			  ySize = y;
			  space = new Object2DGrid(xSize, ySize);
			  int i = 0, j = 0;
			  String line;
			  while ((line = br.readLine()) != null)   {
				  while(line.length() > j) {
					 switch(line.charAt(j)) {
						 case WALL:
							 Wall w =new Wall(j,i);
							 agentList.add(w);
							 space.putObjectAt(j, i, w);
							
							 break;
						 case SOLDIER:
							 Soldier s =new Soldier(j,i);
							 agentList.add(s);
							 space.putObjectAt(j, i, s);
							 break;
						 case CAPTAIN:
							 Captain c =new Captain(j,i);
							 agentList.add(c);
							 space.putObjectAt(j, i, c);
							 break;
						 case EXIT:
							 break;
						default:
							break;
							 
						 
					 }
					  j++;
						 
					 
				  }
				 
				  j=0;
				  i++;  
			  }
			  
			  in.close();
			  in2.close();
			    }catch (Exception e){
			  System.err.println("Error: " + e.getMessage());
			}
	}

}
