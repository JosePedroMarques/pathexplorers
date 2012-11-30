package agents;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import astar.AStar;
import astar.AStarNode;

import map.Cell;
import map.Cell.Value;
import map.Map;

import uchicago.src.sim.space.Object2DGrid;
import uchicago.src.sim.util.Random;
import utils.DirectionList;
import utils.NegotiationOffer;
import utils.Pair;

public abstract class ArmyUnit extends BasicAgent {

	protected int communicationRange = 20;
	int sightRange = 5;
	protected Object2DGrid space;
	private Map map;
	protected Stack<Pair<Integer, Integer>> movesDone;
	protected LinkedList<AStarNode> aStarPath = null;
	protected boolean hasReachedExit = false;
	private final int EMPTYWEIGHT = 2;
	private final int UNKOWNWEIGHT = 1;
	private final int DISPERSIONWEIGHT = 5;
	boolean DEBUG = true;

	public ArmyUnit(int x, int y, Color color, Object2DGrid space) {

		super(x, y, color);
		this.space = space;
		setMap(new Map(space.getSizeX(), space.getSizeY()));
		movesDone = new Stack<Pair<Integer, Integer>>();
		movesDone.push(new Pair<Integer, Integer>(x, y));
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
				Object o = space.getObjectAt(j, i);
				if (o == null) { // Espaco vazio, posso andar

					int noUnknowns = map.getReachableValues(j, i, range, v)
							.size();
					DirectionList dl;
					if (dlMap.containsKey(noUnknowns))
						dl = dlMap.get(noUnknowns);
					else
						dl = new DirectionList(noUnknowns,
								new ArrayList<Pair<Integer, Integer>>());

					dl.addDirection(new Pair<Integer, Integer>(j, i));
					dlMap.put(noUnknowns, dl);

				}
			}
		PriorityQueue<DirectionList> dlQueue = new PriorityQueue<DirectionList>();
		for (Entry<Integer, DirectionList> dl : dlMap.entrySet()) {
			dlQueue.add(dl.getValue());
		}
		return dlQueue;
	}

	public void move() {
		if(DEBUG){
			System.out.println("IM A FREAKING " + getValue() + " at (" + x+ " , " + y + " )");
			System.out.println("MY ASTARLIST IS LIKE THIS:");
			System.out.println(aStarPath);
			System.out.println(map);
		}
		Pair<Integer, Integer> nextMove;
		Cell exit = map.getExit();
		// se estou a percorrer um caminho ja delineado, ou se sei onde é a
		// saida....
		if (exit != null || aStarPath != null) {
			if (aStarPath == null) // calcular o caminho para saida
				aStarPath = AStar.run(map.getPosition(x, y), exit, map);
			AStarNode nextNode = aStarPath.removeFirst();
			
			if (aStarPath.isEmpty())
				aStarPath = null;
			if (exit != null && nextNode.equals(exit))
				hasReachedExit = true;
			nextMove = new Pair<Integer, Integer>(nextNode.getX(),
					nextNode.getY());
			movesDone.push(nextMove);
		}// senao e preciso calcular o proximo passo
		else {
			//ArrayList<Cell> unitsInSight = getUnitsInSight();
			PriorityQueue<DirectionList> moves = getOrderedListOfMoves();

			if (moves == null)
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
				movesDone.push(nextMove);
			}

		}
		if(DEBUG)
			System.out.println("DEICDED TO MOVE TO " + nextMove);
		doMove(nextMove);

	}

	/*
	 * private void negotiateMove(ArrayList<Cell> unitsInSight) { boolean[]
	 * dirUsed = { false, false, false, false }; Pair int usedDir = 0;
	 * ArrayList<ArmyUnit> armyUnits = getArmyUnits(unitsInSight); // fazer a
	 * minha lista de direções ordenada PriorityQueue<DirectionList> myPrefs =
	 * getOrderedListOfMoves(); HashMap<ArmyUnit, PriorityQueue<DirectionList>>
	 * dirMap = new HashMap<ArmyUnit, PriorityQueue<DirectionList>>();
	 * HashMap<ArmyUnit, DirectionList> maxDirMap = new HashMap<ArmyUnit,
	 * DirectionList>(); dirMap.put(this, myPrefs); maxDirMap.put(this,
	 * myPrefs.peek()); // pedir a todos -> responde com null se ja tiver algo
	 * planeado for (ArmyUnit a : armyUnits) { PriorityQueue<DirectionList>
	 * prefs = a.getOrderedListOfMoves(); if (prefs != null) { dirMap.put(a,
	 * prefs); maxDirMap.put(a, prefs.peek()); }
	 * 
	 * }
	 * 
	 * boolean negotiationComplete = false; // loop
	 * 
	 * while (!negotiationComplete) { // escolher a melhor
	 * PriorityQueue<NegotiationOffer> queueOffer = new
	 * PriorityQueue<NegotiationOffer>();
	 * 
	 * for (Entry<ArmyUnit, PriorityQueue<DirectionList>> e : dirMap
	 * .entrySet()) { ArmyUnit a = e.getKey(); PriorityQueue<DirectionList>
	 * prefs = e.getValue(); // ficou sem mais direcoes, escolhe a original if
	 * (prefs == null) { a.sendDirection(maxDirMap.get(a).getRandomDirection());
	 * continue; } NegotiationOffer offer = new NegotiationOffer(a,
	 * prefs.peek()); queueOffer.add(offer);
	 * 
	 * }
	 * 
	 * // avisar esse, retirar aos outros
	 * 
	 * boolean dirChosen = false; while (!dirChosen && !queueOffer.isEmpty()) {
	 * NegotiationOffer winner = queueOffer.poll(); dirChosen = false; while
	 * (!dirChosen && !winner.getPreferences().getDirections().isEmpty()) {
	 * Pair<Integer, Integer> direction = winner.getPreferences()
	 * .getRandomDirection();
	 * 
	 * int dir = getDir(winner.getOwner(), direction); if (!dirUsed[dir]) {
	 * winner.getOwner().sendDirection(direction); dirUsed[dir] = true;
	 * dirChosen = true; usedDir++; dirMap.remove(winner.getOwner());
	 * 
	 * }else winner.getPreferences().removeDirection(direction);
	 * 
	 * 
	 * } } if (usedDir == 4 || queueOffer.isEmpty()) {
	 * 
	 * for (Entry<ArmyUnit, PriorityQueue<DirectionList>> e : dirMap
	 * .entrySet()) { ArmyUnit a = e.getKey();
	 * a.sendDirection(maxDirMap.get(a).getRandomDirection()); }
	 * negotiationComplete = true;
	 * 
	 * } }
	 * 
	 * }
	 * 
	 * private int getDir(ArmyUnit winner, Pair<Integer, Integer> direction) {
	 * 
	 * int xi = winner.getX(); int yi = winner.getY(); int xf =
	 * direction.getFirst(); int yf = direction.getSecond();
	 * 
	 * // up ou down if (xf == xi) { if (yf > yi) return DOWN; return UP;
	 * 
	 * } if (xf > xi) return RIGHT; return LEFT;
	 * 
	 * }
	 */
	private void sendDirection(Pair<Integer, Integer> randomDirection) {
		// TODO Auto-generated method stub

	}

	public PriorityQueue<DirectionList> getOrderedListOfMoves() {

		// ja tenho algo planeado, ignoro a negociação
		if (aStarPath != null)
			return null;
		ArrayList<Value> obj = new ArrayList<Value>();
		obj.add(Value.Empty);
		PriorityQueue<DirectionList> dirEmpties = searchSpaceFor(obj, 0);
		for (DirectionList dl : dirEmpties)
			dl.setGainValue(dl.getGainValue() * EMPTYWEIGHT);
		obj.clear();
		obj.add(Value.Unknown);
		PriorityQueue<DirectionList> dirUnknowns = searchSpaceFor(obj, 0);
		for (DirectionList dl : dirUnknowns)
			dl.setGainValue(dl.getGainValue() * UNKOWNWEIGHT);
		dirEmpties.addAll(dirUnknowns);
		obj.clear();
		obj.add(Value.Soldier);
		obj.add(Value.Captain);
		obj.add(Value.Robot);
		PriorityQueue<DirectionList> dirDisperse = searchSpaceFor(obj, 0);
		for (DirectionList dl : dirDisperse)
			dl.setGainValue(dl.getGainValue() * DISPERSIONWEIGHT);
		dirEmpties.addAll(dirDisperse);

		// ja nao posso andar para a frente, preciso de andar para tras

		if (dirEmpties.isEmpty() || dirEmpties.peek().getGainValue() == 0)
			return null;
		// posso andar para a frente, com estas prioridades
		return dirEmpties;
	}

	private ArrayList<ArmyUnit> getArmyUnits(ArrayList<Cell> unitsInSight) {
		ArrayList<ArmyUnit> armyUnits = new ArrayList<ArmyUnit>();
		for (Cell c : unitsInSight) {
			armyUnits.add((ArmyUnit) space.getObjectAt(c.getX(), c.getY()));
		}
		return armyUnits;
	}

	private ArrayList<Cell> getUnitsInSight() {

		ArrayList<Value> arrayUnits = new ArrayList<Value>();
		arrayUnits.add(Value.Captain);
		arrayUnits.add(Value.Soldier);
		arrayUnits.add(Value.Robot);
		return (ArrayList<Cell>) map.getReachableValues(x, y, sightRange,
				arrayUnits);

	}

	public Pair<Integer, Integer> backtraceStep() {
		if(DEBUG){
			System.out.println("NEEDED TO BACKTRACK");
			System.out.println("MY STACK LOOKS LIKE THIS:");
			System.out.println(movesDone);
		}
		if (!movesDone.isEmpty()) { // posso voltar por onde vim
			Pair<Integer, Integer> lastDirection = movesDone.peek();
			if (map.canGoInto(lastDirection.getFirst(),
					lastDirection.getSecond())){
				movesDone.pop();
				return lastDirection;
			}
			
				
		}
		if(DEBUG)
			System.out.println("SEEMS I WILL ASTAR TO A PREVIOUS LOCATION");
		AStarNode node = tryMovingTo(Value.Empty);
		if (node == null)
			node = tryMovingTo(Value.Unknown);
		if (node == null) {
			System.out.println("Woot? No empties nor unknowns and no exit??");
			return new Pair<Integer, Integer>(x, y);
		}
		return new Pair<Integer, Integer>(node.getX(), node.getY());

	}

	/*
	 * public Pair<Integer, Pair<Integer, Integer>> getPreferedMove() {
	 * Pair<Integer, Integer> nextMove; DirectionList noEmpties =
	 * searchSpaceFor(Value.Empty, 0).poll(); if (noEmpties == null) return new
	 * Pair<Integer, Pair<Integer, Integer>>(0, null); int maxE =
	 * noEmpties.getGainValue();
	 * 
	 * if (maxE > 0) { nextMove = noEmpties.getRandomDirection();
	 * 
	 * return new Pair<Integer, Pair<Integer, Integer>>(maxE, nextMove); }
	 * 
	 * DirectionList noUnknowns = searchSpaceFor(Value.Unknown, sightRange)
	 * .poll(); int maxU = noUnknowns.getGainValue();
	 * 
	 * if (maxU > 0) { nextMove = noUnknowns.getRandomDirection();
	 * 
	 * return new Pair<Integer, Pair<Integer, Integer>>(maxU, nextMove); } //
	 * não posso andar para a frente. need to backtrack return new Pair<Integer,
	 * Pair<Integer, Integer>>(0, null);
	 * 
	 * }
	 */

	private AStarNode tryMovingTo(Value v) {
		if(DEBUG){
			System.out.println("TRYING TO FIND A PLACE TO GO");
			System.out.println(map);
		}
		// encontrar o empty mais proximo e a* para la
		Pair<Integer, ArrayList<Cell>> nearestEmpty = findNearest(1, v);
		if (nearestEmpty == null)
			return null;
		if(DEBUG)	
			System.out.println("I MIGHT HAVE A PLACE TO GO NOW");

		while (aStarPath == null) {
			int radius = nearestEmpty.getFirst();
			ArrayList<Cell> destinations = nearestEmpty.getSecond();
			while (aStarPath == null && !destinations.isEmpty()) {
				Cell destination = destinations.get(0);
				if(DEBUG)
					System.out.println("I'm at (" + x + ", " + y
						+ ") stuck, nowhere to go. Backtracking to "
						+ destination + " at (" + destination.getX() + ", "
						+ destination.getY() + ")");
				aStarPath = AStar.run(map.getPosition(x, y), destination, map);
				destinations.remove(0);
				if (aStarPath == null && DEBUG)
					System.out
							.println("UPS no path found....tryng to find another place to go");
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

		Cell oldCell = map.getPosition(x, y);
		map.setPosition(x, y, new Cell(getValue(), x, y));
		Vector v = space.getMooreNeighbors(x, y, getCommunicationRange(),
				getCommunicationRange(), false);
		for (Object o : v) {

			if (canCommunicate(o)) {
				ArmyUnit a = (ArmyUnit) o;
				a.receiveComm(this.map, this.getValue());
			}
		}
		map.setPosition(x, y, oldCell);

	}

	private void receiveComm(Map map2, Value value) {
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

	private boolean canCommunicate(Object o) {

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

}
