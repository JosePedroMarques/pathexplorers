package agents;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Stack;
import java.util.Vector;

import astar.AStar;
import astar.AStarNode;

import map.Cell;
import map.Cell.Value;
import map.Map;

import uchicago.src.sim.space.Object2DGrid;
import uchicago.src.sim.util.Random;
import utils.Pair;

public abstract class ArmyUnit extends BasicAgent {

	protected int communicationRange = 20;
	int sightRange = 5;
	protected Object2DGrid space;
	private Map map;
	protected Stack<Pair<Integer, Integer>> movesDone;
	protected LinkedList<AStarNode> aStarPath = null;
	protected boolean hasReachedExit = false;

	public ArmyUnit(int x, int y, Color color, Object2DGrid space) {

		super(x, y, color);
		this.space = space;
		setMap(new Map(space.getSizeX(), space.getSizeY()));
		movesDone = new Stack<Pair<Integer, Integer>>();
		movesDone.push(new Pair<Integer, Integer>(x, y));
	}

	public Pair<Integer, ArrayList<Pair<Integer, Integer>>> searchSpaceFor(
			Value v, int range) {
		// List of possible directions
		int max = Integer.MIN_VALUE;
		ArrayList<Pair<Integer, Integer>> directions = new ArrayList<Pair<Integer, Integer>>();

		for (int i = Math.max(0, y - 1); i <= Math.min(y + 1, map.getY() - 1); i++)
			for (int j = Math.max(0, x - 1); j <= Math.min(x + 1,
					map.getX() - 1); j++) {
				int yMove = y - i;
				int xMove = x - j;
				if (xMove == yMove || xMove == -yMove)
					continue;
				Object o = space.getObjectAt(j, i);
				if (o == null) { // Espaco vazio, posso andar
					ArrayList<Value> arrayV = new ArrayList<Value>();
					arrayV.add(v);
					int noUnknowns = map.getReachableValues(j, i,
							range, arrayV ).size();

					if (noUnknowns > max) {

						max = noUnknowns;
						directions.clear();
						directions.add(new Pair<Integer, Integer>(j, i));

					} else if (noUnknowns == max) {
						directions.add(new Pair<Integer, Integer>(j, i));
					}
				}
			}

		return new Pair<Integer, ArrayList<Pair<Integer, Integer>>>(max,
				directions);
	}

	public void move() {
		Pair<Integer, Integer> nextMove;
		Cell exit = map.getExit();
		// se estou a percorrer um caminho ja delineado, ou se sei onde é a
		// saida....
		if (exit != null || aStarPath != null) {
			if (aStarPath == null) // calcular o caminho para saida
				aStarPath = AStar.run(map.getPosition(x, y), exit, map);
			AStarNode nextNode = aStarPath.removeFirst();
			if (nextNode.equals(exit))
				hasReachedExit = true;
			nextMove = new Pair<Integer, Integer>(nextNode.getX(),
					nextNode.getY());
			movesDone.push(nextMove);
		}// senao e preciso calcular o proximo passo
		else {
			ArrayList<Cell> unitsInSight = getUnitsInSight();
			//se estiver sozinho
			if (unitsInSight.isEmpty()) {
				nextMove = getPreferedMove().getSecond();// tentar andar para
															// frente
				if (nextMove == null)
					nextMove = backtraceStep();
				else
					movesDone.push(nextMove);

			} else
				nextMove = negotiateMove(unitsInSight);
		}
		doMove(nextMove);

	}

	private Pair<Integer, Integer> negotiateMove(ArrayList<Cell> unitsInSight) {
		ArrayList<ArmyUnit> armyUnits= getArmyUnits(unitsInSight);
		
		return null;
	}

	private ArrayList<ArmyUnit> getArmyUnits(ArrayList<Cell> unitsInSight) {
		ArrayList<ArmyUnit> armyUnits = new ArrayList<ArmyUnit>();
		for(Cell c: unitsInSight){
			armyUnits.add((ArmyUnit)space.getObjectAt(c.getX(), c.getY()));
		}
		return armyUnits;
	}

	private ArrayList<Cell> getUnitsInSight() {
		
		ArrayList<Value> arrayUnits = new ArrayList<Value>();
		arrayUnits.add(Value.Captain);
		arrayUnits.add(Value.Soldier);	
		arrayUnits.add(Value.Robot);	
		return (ArrayList<Cell>) map.getReachableValues(x,y,sightRange,arrayUnits);
		
	}

	public Pair<Integer, Integer> backtraceStep() {
		if (!movesDone.isEmpty()) // posso voltar por onde vim
			return movesDone.pop();
		else {
			// encontrar o empty mais proximo e a* para la
			System.out.println("Estou encurralado, sem nada na stack");
			return new Pair<Integer, Integer>(this.x, this.y);
		}

	}

	public Pair<Integer, Pair<Integer, Integer>> getPreferedMove() {
		Pair<Integer, Integer> nextMove;
		Pair<Integer, ArrayList<Pair<Integer, Integer>>> noEmpties = searchSpaceFor(
				Value.Empty, 0);
		int maxE = noEmpties.getFirst();
		ArrayList<Pair<Integer, Integer>> directions = noEmpties.getSecond();

		if (maxE > 0) {
			nextMove = directions.get(Random.uniform.nextIntFromTo(0,
					directions.size() - 1));

			return new Pair<Integer, Pair<Integer, Integer>>(maxE, nextMove);
		}

		Pair<Integer, ArrayList<Pair<Integer, Integer>>> noUnknowns = searchSpaceFor(
				Value.Unknown, sightRange);
		int maxU = noUnknowns.getFirst();
		directions = noUnknowns.getSecond();

		if (maxU > 0) {
			nextMove = directions.get(Random.uniform.nextIntFromTo(0,
					directions.size() - 1));

			return new Pair<Integer, Pair<Integer, Integer>>(maxU, nextMove);
		}
		// não posso andar para a frente. need to backtrack
		return new Pair<Integer, Pair<Integer, Integer>>(0, null);

	}

	public void doMove(Pair<Integer, Integer> direction) {

		Cell exit = map.getExit();

		space.putObjectAt(this.x, this.y, null);
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

		if (aStarPath != null)// verificar se nao temos paredes no caminho
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
				a.receiveComm(this.map);
			}
		}
		map.setPosition(x, y, oldCell);

	}

	private void receiveComm(Map map2) {
		for (int i = 0; i < map.getY(); i++)
			for (int j = 0; j < map.getX(); j++)
				if (map.getPosition(j, i).getValue() == Value.Unknown) {
					switch (map2.getPosition(j, i).getValue()) {
					case Me:
						map.setPosition(j, i, new Cell(Value.Empty, j, i));
						break;
					default:
						map.setPosition(j, i, map2.getPosition(j, i));
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
