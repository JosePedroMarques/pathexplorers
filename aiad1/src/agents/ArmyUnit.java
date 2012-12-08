package agents;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Stack;
import java.util.Vector;

import org.apache.poi.poifs.storage.BATBlock;

import map.Cell;
import map.Cell.Value;
import map.Map;
import uchicago.src.sim.space.Object2DGrid;
import uchicago.src.sim.util.Random;
import utils.Config;
import utils.DirectionList;
import utils.Pair;
import astar.AStar;
import astar.AStarNode;

public abstract class ArmyUnit extends BasicAgent {

	private static final int VISITEDPENALTY = 10;
	protected int communicationRange = 20;
	int sightRange = 5;
	protected Object2DGrid space;
	protected Map map;
	protected LinkedList<AStarNode> aStarPath = null;
	protected boolean hasReachedExit = false;
	private float EMPTYWEIGHT = 2;
	private float UNKOWNWEIGHT = 1;
	private float DISPERSIONWEIGHT = 3;
	boolean VERBOSE = false;
	protected int TIMEOUT = 100;
	protected int waitTime = 0;
	private boolean knowsExitLocation = false;
	protected boolean hasCommunicatedWithCaptain = false;
	protected boolean hasExited = false;
	protected boolean justBacktracked = false;
	protected boolean backtrackedLast = false;
	protected int radioBattery;
	protected boolean stoppedBacktracking;
	protected Color commColor;
	protected boolean visualizeComm;
	
	public ArmyUnit(int x, int y, Color color, Object2DGrid space, Config conf) {

		super(x, y, color);
		this.space = space;
		setMap(new Map(space.getSizeX(), space.getSizeY()));
		this.EMPTYWEIGHT = conf.getEMPTYWEIGHT();
		this.DISPERSIONWEIGHT = conf.getDISPERSIONWEIGHT();
		this.UNKOWNWEIGHT = conf.getUNKOWNWEIGHT();
		this.VERBOSE = conf.isVERBOSE();
		this.TIMEOUT = conf.getTIMEOUT();
		this.radioBattery = conf.getRadioBattery();
		this.visualizeComm = conf.isVisualizeComm();
		
	}

	public PriorityQueue<DirectionList> searchSpaceFor(ArrayList<Value> v,
			int range) {
		HashMap<Integer, DirectionList> dlMap = new HashMap<Integer, DirectionList>();
		// List of possible directions

		for (int i = Math.max(0, y - 1); i <= Math.min(y + 1, map.getY() - 1); i++)
			for (int j = Math.max(0, x - 1); j <= Math.min(x + 1,
					map.getX() - 1); j++) {
				int yMove = y - i;
				int xMove = x - j;
				if (xMove == yMove || xMove == -yMove)
					continue;
				
				if (map.canGoInto(j, i)) { // Espaco vazio, posso andar

					int noUnknowns = map.getReachableValues(j, i, range, v)
							.size();
					int penalty = map.getPosition(j, i).getValue() == Value.Visited ? VISITEDPENALTY:0;
					DirectionList dl;
					int value = noUnknowns-penalty;
					if (dlMap.containsKey(value))
						dl = dlMap.get(value);
					else
						dl = new DirectionList(value,
								new ArrayList<Pair<Integer, Integer>>());

					dl.addDirection(new Pair<Integer, Integer>(j, i));
					dlMap.put(value, dl);

				}
			}
		PriorityQueue<DirectionList> dlQueue = new PriorityQueue<DirectionList>();
		for (Entry<Integer, DirectionList> dl : dlMap.entrySet()) {
			dlQueue.add(dl.getValue());
		}
		return dlQueue;
	}

	public void move() {
		if(VERBOSE){
			System.out.println("IM A FREAKING " + getValue() + " at (" + x+ " , " + y + " )");
			System.out.println("MY ASTARLIST IS LIKE THIS:");
			System.out.println(aStarPath);
			System.out.println(map);
		}
		
		justBacktracked = false;
		Pair<Integer, Integer> nextMove;
		Cell exit = map.getExit();
		//encontrei a saida pela primeira vez, ignore o caminho que tinha planeado
		if(exit!=null && knowsExitLocation == false){
			aStarPath = AStar.run(map.getPosition(x, y), exit, map);
			knowsExitLocation = true;
			
		}
		// se estou a percorrer um caminho ja delineado, ou se sei onde é a
				// saida....
		if (exit != null || aStarPath != null) {
			if (aStarPath == null) // calcular o caminho para saida
				aStarPath = AStar.run(map.getPosition(x, y), exit, map);
			AStarNode nextNode = aStarPath.removeFirst();
			
			
			if (exit != null && nextNode.equals(exit))
				hasReachedExit = true;
			nextMove = onExitFoundAction(nextNode);
			if (aStarPath.isEmpty())
				aStarPath = null;
		}// senao e preciso calcular o proximo passo
		else {
			
			PriorityQueue<DirectionList> moves = getOrderedListOfMoves();
			//se o meu melhor movimento tem ganho nao positivo
			//e preciso andar para tras
			
			if (moves== null || moves.peek().getGainValue() <=0 )
				nextMove = backtraceStep();
			else {
				// se nao estiver sozinho
				// if (!unitsInSight.isEmpty()) {
				// negotiateMove(unitsInSight);
				// AStarNode nextNode = aStarPath.removeFirst();
				// nextMove = new Pair<Integer, Integer>(nextNode.getX(),
				// nextNode.getY());

				// }
				nextMove = moves.peek().getRandomDirection();
			}

		}
		if(VERBOSE)
			System.out.println("DECIDED TO MOVE TO " + nextMove);
		doMove(nextMove);
		
		stoppedBacktracking = backtrackedLast && !justBacktracked;
		
		backtrackedLast = justBacktracked;
	}

	protected abstract Pair<Integer, Integer> onExitFoundAction(AStarNode nextNode); 

	
	public PriorityQueue<DirectionList> getOrderedListOfMoves() {

		// ja tenho algo planeado, ignoro a negociação
		if (aStarPath != null)
			return null;
		ArrayList<Value> obj = new ArrayList<Value>();
		obj.add(Value.Empty);
		PriorityQueue<DirectionList> dirEmpties = searchSpaceFor(obj, sightRange);
		
		if(VERBOSE){
			
			System.out.println("THIS IS MY DIRECTION LIST - EMPTY");
			System.out.println(dirEmpties);
		}
		obj.clear();
		obj.add(Value.Unknown);
		PriorityQueue<DirectionList> dirUnknowns = searchSpaceFor(obj, sightRange);
		
		if(VERBOSE){
			
			System.out.println("THIS IS MY DIRECTION LIST - UNKNOWN");
			System.out.println(dirUnknowns);
		}
		
		obj.clear();
		obj.add(Value.Soldier);
		obj.add(Value.Captain);
		obj.add(Value.Robot);
		PriorityQueue<DirectionList> dirDisperse = searchSpaceFor(obj, 1);
		if(VERBOSE){
			
			System.out.println("THIS IS MY DIRECTION LIST - DISPERSE");
			System.out.println(dirDisperse);
		}
		PriorityQueue<DirectionList> dirDisperseInv = new PriorityQueue<DirectionList>();
		if(!dirDisperse.isEmpty()){
		
			float max = Math.max(dirDisperse.peek().getGainValue(),Math.max(dirEmpties.peek().getGainValue(),dirUnknowns.peek().getGainValue()));
			for (DirectionList dl : dirDisperse){
				float currentValue = dl.getGainValue();
				float inverseValue = currentValue < 0 ? currentValue: max-currentValue;
				dl.setGainValue(inverseValue);
				dirDisperseInv.add(dl);
			}
			if(VERBOSE){
				
				System.out.println("THIS IS MY DIRECTION LIST - DISPERSE INV");
				System.out.println(dirDisperseInv);
			}
			
		}
		// ja nao posso andar para a frente, preciso de andar para tras

		if (dirEmpties.isEmpty() || dirEmpties.peek().getGainValue() == 0)
			return null;
		// posso andar para a frente, com estas prioridades
	
		PriorityQueue<DirectionList> allDirs = new PriorityQueue<DirectionList>();
		for (DirectionList dl : dirEmpties)
			dl.setGainValue(dl.getGainValue() * EMPTYWEIGHT);
		for (DirectionList dl : dirUnknowns)
			dl.setGainValue(dl.getGainValue() * UNKOWNWEIGHT);
		for (DirectionList dl : dirDisperseInv)
			dl.setGainValue(dl.getGainValue() * DISPERSIONWEIGHT);
		allDirs.addAll(dirEmpties);
		allDirs.addAll(dirUnknowns);
		allDirs.addAll(dirDisperseInv);
		return allDirs;
	}

	

	public Pair<Integer, Integer> backtraceStep() {
		if(VERBOSE){
			System.out.println("NEEDED TO BACKTRACK");
			System.out.println("MY STACK LOOKS LIKE THIS:");
		}
	
		justBacktracked = true;
		if(VERBOSE)
			System.out.println("SEEMS I WILL ASTAR TO A PREVIOUS LOCATION");
		AStarNode node = tryMovingTo(Value.Empty);
		if (node == null)
			node = tryMovingTo(Value.Unknown);
		if (node == null) {
			System.out.println(map);
			System.out.println("Woot? No empties nor unknowns and no exit??");
			return new Pair<Integer, Integer>(x, y);
		}
		return new Pair<Integer, Integer>(node.getX(), node.getY());

	}


	private AStarNode tryMovingTo(Value v) {
		if(VERBOSE){
			System.out.println("TRYING TO FIND A PLACE TO GO");
			System.out.println(map);
		}
		// encontrar o empty mais proximo e a* para la
		Pair<Integer, ArrayList<Cell>> nearestEmpty = findNearest(1, v);
		if (nearestEmpty == null)
			return null;
		if(VERBOSE)	
			System.out.println("I MIGHT HAVE A PLACE TO GO NOW");

		while (aStarPath == null) {
			int radius = nearestEmpty.getFirst();
			ArrayList<Cell> destinations = nearestEmpty.getSecond();
			while (aStarPath == null && !destinations.isEmpty()) {
				Cell destination = destinations.get(0);
				if(VERBOSE)
					System.out.println("I'm at (" + x + ", " + y
						+ ") stuck, nowhere to go. Backtracking to "
						+ destination + " at (" + destination.getX() + ", "
						+ destination.getY() + ")");
				aStarPath = AStar.run(map.getPosition(x, y), destination, map);
				destinations.remove(0);
				if (aStarPath == null){
					destination.setValue(Value.Unreachable);
					map.setPosition(destination.getX(), destination.getY(), destination);
					
					if(VERBOSE)
						System.out.println("UPS no path found....tryng to find another place to go");
					
				}
			}
			nearestEmpty = findNearest(radius, v);
			if (nearestEmpty == null)
				return null;
		}
		return aStarPath.removeFirst();
	}

	private Pair<Integer, ArrayList<Cell>> findNearest(int radius, Value v) {

		ArrayList<Cell> cells = new ArrayList<Cell>();

		for (; cells.isEmpty()
				&& (x - radius >= 0 || x + radius < map.getX()
						|| y - radius >= 0 || y + radius < map.getY()); radius++) {
			cells = (ArrayList<Cell>) map.getCellsAtRadius(x, y, radius, v);

		}
		if (cells.isEmpty())
			return null;
		return new Pair<Integer, ArrayList<Cell>>(radius, cells);
	}

	public void doMove(Pair<Integer, Integer> direction) {

		Cell exit = map.getExit();

		space.putObjectAt(this.x, this.y, new Visited(this.x,this.y));
		map.setPosition(this.x, this.y, new Cell(Value.Visited, this.x, this.y));
		this.x = direction.getFirst();
		this.y = direction.getSecond();
		if (exit != null && exit.getX() == direction.getFirst()
				&& exit.getY() == direction.getSecond()) {
			return;
		}
		space.putObjectAt(this.x, this.y, this);

	}

	/**
	 * @return the map
	 */
	public Map getMap() {
		return map;
	}

	/**
	 * @param map
	 *            the map to set
	 */
	public void setMap(Map map) {
		this.map = map;
	}

	public void lookAround() {
		hasCommunicatedWithCaptain = false;
		
		
		// Look left
		int searched = 0;
		int xS = x;
		int yS = y;
		int maxSightRangeUp = sightRange;
		int maxSightRangeBottom = sightRange;
		int maxSightRange = sightRange;
		while (xS > 0 && searched < maxSightRange) {

			maxSightRangeUp = beamSearch(xS, yS, 0, -1, maxSightRangeUp);
			maxSightRangeBottom = beamSearch(xS, yS, 0, 1, maxSightRangeBottom);
			searched++;
			if (!takeALook(xS, yS))
				maxSightRange = searched;
			xS--;

		}
		// Look right
		searched = 0;
		xS = x;
		yS = y;
		maxSightRangeUp = sightRange;
		maxSightRangeBottom = sightRange;
		maxSightRange = sightRange;
		while (xS < map.getX() && searched < maxSightRange) {

			maxSightRangeUp = beamSearch(xS, yS, 0, -1, maxSightRangeUp);
			maxSightRangeBottom = beamSearch(xS, yS, 0, 1, maxSightRangeBottom);
			searched++;
			if (!takeALook(xS, yS))
				maxSightRange = searched;
			xS++;

		}

		// Look up
		searched = 0;
		xS = x;
		yS = y;
		maxSightRangeUp = sightRange;
		maxSightRangeBottom = sightRange;
		maxSightRange = sightRange;
		while (yS > 0 && searched < maxSightRange) {

			maxSightRangeUp = beamSearch(xS, yS, -1, 0, maxSightRangeUp);
			maxSightRangeBottom = beamSearch(xS, yS, 1, 0, maxSightRangeBottom);
			searched++;
			if (!takeALook(xS, yS))
				maxSightRange = searched;
			yS--;

		}
		// Look down
		searched = 0;
		xS = x;
		yS = y;
		maxSightRangeUp = sightRange;
		maxSightRange = sightRange;
		while (yS < map.getY() && searched < maxSightRange) {

			maxSightRangeUp = beamSearch(xS, yS, -1, 0, maxSightRangeUp);
			maxSightRangeBottom = beamSearch(xS, yS, 1, 0, maxSightRangeBottom);
			searched++;
			if (!takeALook(xS, yS))
				maxSightRange = searched;
			yS++;

		}

		map.setPosition(x, y, new Cell(Value.Me, x, y));

		// System.out.println(map);

		if (aStarPath != null && !aStarPath.isEmpty())// verificar se nao temos
														// paredes no caminho
			// planeado
			updatePath();
	}

	private void updatePath() {
		AStarNode endNode = aStarPath.getLast();
		for (AStarNode n : aStarPath) {
			if (map.getPosition(n.getX(), n.getY()).getValue() == Value.Wall) {
				aStarPath = AStar.run(map.getPosition(x, y), endNode, map);
				break;
			}
		}

	}

	private int beamSearch(int xi, int yi, int xDir, int yDir, int maxSightRange) {

		int searched = 0;
		int x = xi + xDir;
		int y = yi + yDir;
		while (searched < maxSightRange && takeALook(x, y)) {
			y += yDir;
			x += xDir;
			searched++;

		}
		return searched + 1;

	}

	private boolean takeALook(int xS, int yS) {
		if (xS >= space.getSizeX() || xS < 0 || yS >= space.getSizeY()
				|| yS < 0)
			return false;
		Object o = space.getObjectAt(xS, yS);
		if (o == null) {
			if (map.getPosition(xS, yS).getValue() != Value.Visited) {

				map.setPosition(xS, yS, new Cell(Value.Empty, xS, yS));
			}
		} else {
			BasicAgent a = (BasicAgent) o;
			map.setPosition(xS, yS, new Cell(a.getValue(), xS, yS));
			if (a.getValue() == Value.Wall)
				return false;

		}
		return true;

	}

	public void broadcastMap() {
		hasCommunicatedWithCaptain = false;
		Cell oldCell = map.getPosition(x, y);
		map.setPosition(x, y, new Cell(getValue(), x, y));
		Vector v = space.getMooreNeighbors(x, y, getCommunicationRange(),
				getCommunicationRange(), false);
		Color cColor;
		if(visualizeComm){
			int rnd = Random.uniform.nextIntFromTo(0,255);
			 cColor = new Color(rnd,0,rnd);
		}
		else
			cColor = this.color;
			
		this.color = cColor;
		for (Object o : v) {

			if (canCommunicate(o)) {
				ArmyUnit a = (ArmyUnit) o;
				hasCommunicatedWithCaptain = a.getValue() == Value.Captain ||hasCommunicatedWithCaptain ;
				
				a.receiveComm(this.map, this.getValue(),cColor);
			}
		}
		map.setPosition(x, y, oldCell);

	}

	protected void receiveComm(Map map2, Value value,Color color) {
		this.color = color;
		for (int i = 0; i < map.getY(); i++)
			for (int j = 0; j < map.getX(); j++)
				if (map.getPosition(j, i).getValue() == Value.Unknown) {
					switch (map2.getPosition(j, i).getValue()) {
					case Me:
						map.setPosition(j, i, new Cell(value, j, i));
						break;
					default:
						map.setPosition(j, i, map2.getPosition(j, i));
						break;

					}

				} else if (map.getPosition(j, i).getValue() == Value.Empty) {
					switch (map2.getPosition(j, i).getValue()) {
					case Me:
						map.setPosition(j, i, new Cell(value, j, i));
						break;
					case Visited:
						map.setPosition(j, i, new Cell(Value.Visited, j, i));
						break;
					default:
						break;

					}

				}

	}

	protected boolean canCommunicate(Object o) {

		if (((BasicAgent) o).canReceiveComms()) {
			ArmyUnit a = (ArmyUnit) o;
			return Math.abs(x - a.getX()) <= a.getCommunicationRange()
					&& Math.abs(y - a.getY()) <= a.getCommunicationRange();

		}

		return false;
	}

	/**
	 * @return the communicationRange
	 */
	public int getCommunicationRange() {
		return communicationRange;
	}

	/**
	 * @param communicationRange
	 *            the communicationRange to set
	 */
	public void setCommunicationRange(int communicationRange) {
		this.communicationRange = communicationRange;
	}

	public boolean hasReachedExit() {
		// TODO Auto-generated method stub
		return hasReachedExit;
	}

	public boolean hasExited() {
		// TODO Auto-generated method stub
		return hasExited ;
	}

	/**
	 * @return the knowsExitLocation
	 */
	public boolean knowsExitLocation() {
		return knowsExitLocation;
	}
	
	public int getBatteryLife(){
		return radioBattery;
	}
	public void setBatteryLife(int bl){
	 radioBattery = bl;
	}
	
	
	public int getSight_Range() {
		return sightRange;
	}

	/**
	 * @param sight_Range the sight_Range to set
	 */
	public void setSight_Range(int sight_Range) {
		this.sightRange = sight_Range;
	}

	public abstract void resetColor();

	

	
}
