package agents;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Stack;
import java.util.Vector;

import map.Cell;
import map.Cell.Value;
import map.Map;

import uchicago.src.sim.space.Object2DGrid;
import uchicago.src.sim.util.Random;
import utils.Pair;

public abstract class ArmyUnit extends BasicAgent {

	protected int communicationRange = 7;
	int sightRange = 5;
	protected Object2DGrid space;
	private Map map;
	protected Stack<Pair<Integer,Integer>> movesDone;

	

	public ArmyUnit(int x, int y, Color color, Object2DGrid space) {

		super(x, y, color);
		this.space = space;
		setMap(new Map(space.getSizeX(), space.getSizeY()));
		movesDone = new Stack<Pair<Integer,Integer>>();
		movesDone.push(new Pair<Integer,Integer>(x,y));
	}
	
	
	public Pair<Integer,ArrayList<Pair<Integer, Integer>>> searchSpaceFor(Value v,int range){
		//List of possible directions
		int max = Integer.MIN_VALUE;	
		ArrayList<Pair<Integer, Integer>> directions = new ArrayList<Pair<Integer, Integer>>();
		
		for (int i = Math.max(0, y - 1); i <= Math.min(y + 1,
				map.getY() - 1); i++)
			for (int j = Math.max(0, x - 1); j <= Math.min(x + 1,
					map.getX() - 1); j++) {
				int yMove = y-i;
				int xMove = x-j;
				if(xMove == yMove || xMove == -yMove)
					continue;
				Object o = space.getObjectAt(j, i);
				if (o == null) { //Espaco vazio, posso andar
					int noUnknowns = map.getNumberOfReachableValues(j, i,
							range,v);

					if (noUnknowns > max) {

						max = noUnknowns;
						directions.clear();
						directions.add(new Pair<Integer, Integer>(j, i));

					} else if (noUnknowns == max) {
						directions.add(new Pair<Integer, Integer>(j, i));
					}
				}
			}
		
		return new Pair<Integer,ArrayList<Pair<Integer, Integer>>>(max,directions);
	}
	
	public void move() {

		if (map.getExitLocation() != null) {
			// move to exit
			System.out.println("I should move to the exit");
		} else {
			Pair<Integer,ArrayList<Pair<Integer, Integer>>> noUnknowns = searchSpaceFor(Value.Empty,0);
			int maxU = noUnknowns.getFirst();
			ArrayList<Pair<Integer, Integer>> directions = noUnknowns.getSecond();
			Pair<Integer,Integer> nextMove;
			System.out.println("Empty = " + maxU);
			if(maxU > 0){
				nextMove = directions.get(Random.uniform.nextIntFromTo(0,
						directions.size() - 1));
				
				movesDone.push(nextMove);
			}else{
				Pair<Integer,ArrayList<Pair<Integer, Integer>>> noEmpties = searchSpaceFor(Value.Unknown,sightRange);
				int maxE = noEmpties.getFirst();
				ArrayList<Pair<Integer, Integer>> directionsE = noEmpties.getSecond();
				
				System.out.println("Unknown = " + maxE);
				if(maxE > 0){
					nextMove = directionsE.get(Random.uniform.nextIntFromTo(0,
							directionsE.size() - 1));
					
					movesDone.push(nextMove);
				}else
					nextMove = movesDone.pop();
			}
			
			
			
			doMove(nextMove);
		}

	}

	public void doMove(Pair<Integer, Integer> direction) {

		space.putObjectAt(this.x, this.y, null);
		map.setPosition(this.x, this.y, new Cell(Value.Visited,this.x,this.y));
		this.x = direction.getFirst();
		this.y = direction.getSecond();
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
		while (xS > 0 && searched < maxSightRange ) {

			maxSightRangeUp = beamSearch(xS, yS, 0, -1, maxSightRangeUp);
			maxSightRangeBottom = beamSearch(xS, yS, 0, 1, maxSightRangeBottom);				
			searched++;
			if(!takeALook(xS, yS))
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
		while (xS < map.getX() && searched < maxSightRange ) {

			maxSightRangeUp = beamSearch(xS, yS, 0, -1, maxSightRangeUp);
			maxSightRangeBottom = beamSearch(xS, yS, 0, 1, maxSightRangeBottom);				
			searched++;
			if(!takeALook(xS, yS))
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
		while (yS > 0 && searched < maxSightRange ) {

			maxSightRangeUp = beamSearch(xS, yS, -1, 0, maxSightRangeUp);
			maxSightRangeBottom = beamSearch(xS, yS, 1, 0, maxSightRangeBottom);				
			searched++;
			if(!takeALook(xS, yS))
				maxSightRange = searched;
			yS--;

		}
		// Look down
		searched = 0;
		xS = x;
		yS = y;
		maxSightRangeUp = sightRange;
		maxSightRange = sightRange;
		while (yS < map.getY() && searched < maxSightRange ) {

			maxSightRangeUp = beamSearch(xS, yS, -1, 0, maxSightRangeUp);
			maxSightRangeBottom = beamSearch(xS, yS, 1, 0, maxSightRangeBottom);				
			searched++;
			if(!takeALook(xS, yS))
				maxSightRange = searched;
			yS++;

		}

		map.setPosition(x, y, new Cell(Value.Me,x,y));
		
		System.out.println(map);
	}

	private int beamSearch(int xi, int yi, int xDir, int yDir, int maxSightRange) {

		int searched = 0;
		int x = xi+xDir;
		int y = yi+yDir;
		while (searched < maxSightRange && takeALook(x, y)) {
			y += yDir;
			x += xDir;
			searched++;

		}
		return searched + 1;

	}

	private boolean takeALook(int xS, int yS) {
		Object o = space.getObjectAt(xS, yS);
		if (o == null){ 
			if(map.getPosition(xS, yS).getValue()!=Value.Visited) {
		
				map.setPosition(xS, yS, new Cell(Value.Empty,xS,yS));
			}
		} else {
			BasicAgent a = (BasicAgent) o;
			map.setPosition(xS, yS, new Cell(a.getValue(),xS,yS));
			if (a.getValue() == Value.Wall)
				return false;

		}
		return true;

	}

	public void broadcastMap() {

		Vector v = space.getMooreNeighbors(x, y, getCommunicationRange(),
				getCommunicationRange(), false);
		for (Object o : v) {

			if (canCommunicate(o)) {
				ArmyUnit a = (ArmyUnit) o;
				a.receiveComm(this.map);
			}
		}

	}

	private void receiveComm(Map map2) {
		for (int i = 0; i < map.getY(); i++)
			for (int j = 0; j < map.getX(); j++)
				if (map.getPosition(j, i).getValue() == Value.Unknown){
					if(map2.getPosition(j, i).getValue() != Value.Visited)
						map.setPosition(j, i, map2.getPosition(j, i));
					else
						map.setPosition(j, i, new Cell(Value.Empty,j,i));
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

}
