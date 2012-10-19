package pathfinder;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import uchicago.src.sim.engine.BasicAction;
import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimInit;
import uchicago.src.sim.engine.SimModelImpl;
import uchicago.src.sim.gui.DisplaySurface;
import uchicago.src.sim.gui.Object2DDisplay;
import uchicago.src.sim.space.Object2DGrid;
import uchicago.src.sim.util.SimUtilities;
import agents.ArmyUnit;
import agents.BasicAgent;
import agents.Captain;
import agents.Exit;
import agents.Soldier;
import agents.Wall;



public class ArmyPathFinderModel extends SimModelImpl {
	private static final char WALL = '#';
	private static final char SOLDIER = 's';
	private static final char CAPTAIN = 'c';
	private static final char EXIT = 'E';
	private ArrayList<ArmyUnit> agentList;
	private Schedule schedule;
	private DisplaySurface dsurf;
	private Object2DGrid space;
	private int xSize;
	private int ySize;

	

	private int numberOfAgents;

	public ArmyPathFinderModel() {
		this.numberOfAgents = 100;
		
	}

	public String getName() {
		return "Color Picking Model";
	}

	public String[] getInitParam() {
		return new String[] { "numberOfAgents"};
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

	



	public void setup() {
		schedule = new Schedule();
		if (dsurf != null) dsurf.dispose();
		dsurf = new DisplaySurface(this, "Army PathFinder");
		dsurf.getBounds().width = 600;
		dsurf.getBounds().height = 200;
		registerDisplaySurface("Army PathFinder", dsurf);
		
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
		agentList = new ArrayList<ArmyUnit>();
		readMap("textfile2.txt");
		
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
		
		dsurf.addDisplayableProbeable(display, "Agents Space");
		dsurf.display();

		
	}

	private void buildSchedule() {
		schedule.scheduleActionBeginning(0, new MainAction());
		schedule.scheduleActionAtInterval(1, dsurf, "updateDisplay", Schedule.LAST);
	//	schedule.scheduleActionAtInterval(1, plot, "step", Schedule.LAST);
	}


	class MainAction extends BasicAction {

		public void execute() {
			

			// shuffle agents
			SimUtilities.shuffle(agentList);

			// iterate through all agents
			for(int i = 0; i < agentList.size(); i++) {
				ArmyUnit agent = (ArmyUnit) agentList.get(i);
				
				agent.lookAround();
				agent.broadcastMap();
				agent.move();
			}
		}

	}


	public static void main(String[] args) {
		SimInit init = new SimInit();
		init.loadModel(new ArmyPathFinderModel(), null, false);
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
							 
							 space.putObjectAt(j, i, w);
							
							 break;
						 case SOLDIER:
							 Soldier s =new Soldier(j,i,space);
							 agentList.add(s);
							 space.putObjectAt(j, i, s);
							 break;
						 case CAPTAIN:
							 Captain c =new Captain(j,i,space);
							 agentList.add(c);
							 space.putObjectAt(j, i, c);
							 break;
						 case EXIT:
							 Exit e = new Exit(j,i);
							 space.putObjectAt(j, i, e);
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
