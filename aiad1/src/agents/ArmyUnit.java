package agents;

import java.awt.Color;
import java.util.Vector;

import map.Cell;
import map.Cell.Value;
import map.Map;

import uchicago.src.sim.space.Object2DGrid;

public abstract class ArmyUnit extends BasicAgent {

	private int communicationRange = 7;
	int sightRange = 5;
	protected Object2DGrid space;
	private Map map;

	public abstract void move();

	public ArmyUnit(int x, int y, Color color, Object2DGrid space) {

		super(x, y, color);
		this.space = space;
		setMap(new Map(space.getSizeX(), space.getSizeY()));

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
		while (searched < sightRange && takeALook(xS, yS)) {

			maxSightRangeUp = beamSearch(xS, yS, 0, -1, maxSightRangeUp);
			maxSightRangeBottom = beamSearch(xS, yS, 0, 1, maxSightRangeBottom);

			searched++;
			xS--;

		}
		// Look right
		searched = 0;
		xS = x;
		yS = y;
		maxSightRangeUp = sightRange;
		maxSightRangeBottom = sightRange;
		while (searched < sightRange && takeALook(xS, yS)) {

			maxSightRangeUp = beamSearch(xS, yS, 0, -1, maxSightRangeUp);
			maxSightRangeBottom = beamSearch(xS, yS, 0, 1, maxSightRangeBottom);

			searched++;
			xS++;

		}

		// Look up
		searched = 0;
		xS = x;
		yS = y;
		maxSightRangeUp = sightRange;
		maxSightRangeBottom = sightRange;
		while (searched < sightRange && takeALook(xS, yS)) {

			maxSightRangeUp = beamSearch(xS, yS, -1, 0, maxSightRangeUp);
			maxSightRangeBottom = beamSearch(xS, yS, 1, 0, maxSightRangeBottom);

			searched++;
			yS--;

		}
		// Look down
		searched = 0;
		xS = x;
		yS = y;
		maxSightRangeUp = sightRange;
		maxSightRangeBottom = sightRange;
		while (searched < sightRange && takeALook(xS, yS)) {

			maxSightRangeUp = beamSearch(xS, yS, -1, 0, maxSightRangeUp);
			maxSightRangeBottom = beamSearch(xS, yS, 1, 0, maxSightRangeBottom);

			searched++;
			yS++;

		}

		map.setPosition(x, y, new Cell(Value.Me));
		System.out.println(map);
	}

	private int beamSearch(int xi, int yi, int xDir, int yDir, int maxSightRange) {

		int searched = 0;
		int x = xi;
		int y = yi;
		while (searched < maxSightRange && takeALook(x, y)) {
			y += yDir;
			x += xDir;
			searched++;

		}
		return searched + 1;

	}

	private boolean takeALook(int xS, int yS) {
		Object o = space.getObjectAt(xS, yS);
		if (o == null) {
			map.setPosition(xS, yS, new Cell(Value.Empty));
		} else {
			BasicAgent a = (BasicAgent) o;
			map.setPosition(xS, yS, new Cell(a.getValue()));
			if (a.getValue() == Value.Wall)
				return false;

		}
		return true;

	}
	
	public void broadcastMap(){
		
		
		Vector v = space.getMooreNeighbors(x, y, getCommunicationRange(), getCommunicationRange(), false);
		for (Object o : v){
			
			if(canCommunicate(o)){
				ArmyUnit a = (ArmyUnit)o;
				a.communicate(this.map);
			}
		}
		
		
	}

	private void communicate(Map map2) {
		for(int i = 0; i < map.getY(); i++)
			for(int j = 0; j< map.getX(); j++)
				if(map.getPosition(j, i).getValue() == Value.Unknown)
					map.setPosition(j, i, map2.getPosition(j, i));
		
	}

	

	private boolean canCommunicate(Object o) {
		
		if(((BasicAgent)o).canReceiveComms()){
			ArmyUnit a = (ArmyUnit) o;
			return Math.abs(x-a.getX()) <= a.getCommunicationRange() && Math.abs(y-a.getY()) <= a.getCommunicationRange();
		
		}return false;
	}

	/**
	 * @return the communicationRange
	 */
	public int getCommunicationRange() {
		return communicationRange;
	}

	/**
	 * @param communicationRange the communicationRange to set
	 */
	public void setCommunicationRange(int communicationRange) {
		this.communicationRange = communicationRange;
	}

}
