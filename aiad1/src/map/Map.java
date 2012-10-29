package map;

import java.util.LinkedList;

import astar.AStarMap;
import astar.AStarNode;

import utils.Pair;
import map.Cell.Value;

public class Map extends AStarMap {

	private int noReachableUnkowns = 0;
	private Cell[][] map;
	private int x;
	private int y;
	private Pair<Integer, Integer> exitLocation = new Pair<Integer, Integer>(
			-1, -1);

	public Map(int x, int y) {
		this.setX(x);
		this.setY(y);
		map = new Cell[y][x];

		for (int i = 0; i < y; i++)
			for (int j = 0; j < x; j++)
				map[i][j] = new Cell(Value.Unknown, j, i);
	}

	public void setPosition(int x, int y, Cell pos) {
		map[y][x] = pos;
		if (pos.getValue() == Value.Exit)
			exitLocation = new Pair<Integer, Integer>(x, y);
	}

	public Cell getPosition(int x, int y) {
		return map[y][x];
	}

	@Override
	public String toString() {
		String r = "";

		for (int i = 0; i < getY(); i++) {
			for (int j = 0; j < getX(); j++)
				r += map[i][j].toString() + " ";
			r += "\n";
		}

		return r;

	}

	public String getStringMap() {
		return toString();
	}

	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}

	/**
	 * @param x
	 *            the x to set
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}

	/**
	 * @param y
	 *            the y to set
	 */
	public void setY(int y) {
		this.y = y;
	}

	public int getNumberOfUnknowns(int x, int y, int range) {
		int sum = 0;
		for (int i = Math.max(0, y - range); i <= Math.min(y + range,
				this.y - 1); i++)
			for (int j = Math.max(0, x - range); j <= Math.min(x + range,
					this.x - 1); j++)
				if (map[i][j].getValue() == Value.Unknown)
					sum += 2;
				else if (map[i][j].getValue() == Value.Empty)
					sum++;

		return sum;
	}

	public Pair<Integer, Integer> getExitLocation() {
		if (exitLocation.getFirst() == -1)
			return null;
		else
			return exitLocation;
	}

	public int getNumberOfReachableValues(int x, int y, int sightRange, Value v) {

		noReachableUnkowns = 0;
		takeALook(x, y, v);
		// Look left
		int searched = 0;
		int xS = x;
		int yS = y;
		int maxSightRangeUp = sightRange;
		int maxSightRangeBottom = sightRange;
		int maxSightRange = sightRange;
		while (xS > 0 && searched < maxSightRange) {

			maxSightRangeUp = beamSearch(xS, yS, 0, -1, maxSightRangeUp, v);
			maxSightRangeBottom = beamSearch(xS, yS, 0, 1, maxSightRangeBottom,
					v);
			searched++;
			if (!takeALook(xS, yS, v))
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
		while (xS < getX() && searched < maxSightRange) {

			maxSightRangeUp = beamSearch(xS, yS, 0, -1, maxSightRangeUp, v);
			maxSightRangeBottom = beamSearch(xS, yS, 0, 1, maxSightRangeBottom,
					v);
			searched++;
			if (!takeALook(xS, yS, v))
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

			maxSightRangeUp = beamSearch(xS, yS, -1, 0, maxSightRangeUp, v);
			maxSightRangeBottom = beamSearch(xS, yS, 1, 0, maxSightRangeBottom,
					v);
			searched++;
			if (!takeALook(xS, yS, v))
				maxSightRange = searched;
			yS--;

		}
		// Look down
		searched = 0;
		xS = x;
		yS = y;
		maxSightRangeUp = sightRange;
		maxSightRange = sightRange;
		while (yS < getY() && searched < maxSightRange) {

			maxSightRangeUp = beamSearch(xS, yS, -1, 0, maxSightRangeUp, v);
			maxSightRangeBottom = beamSearch(xS, yS, 1, 0, maxSightRangeBottom,
					v);
			searched++;
			if (!takeALook(xS, yS, v))
				maxSightRange = searched;
			yS++;

		}

		return noReachableUnkowns;

	}

	private int beamSearch(int xi, int yi, int xDir, int yDir,
			int maxSightRange, Value v) {

		int searched = 0;
		int x = xi + xDir;
		int y = yi + yDir;
		while (isInside(x, y) && searched < maxSightRange && takeALook(x, y, v)) {
			y += yDir;
			x += xDir;
			searched++;

		}
		return searched + 1;

	}

	private boolean isInside(int x, int y) {
		return x >= 0 && y >= 0 && x < this.x && y < this.y;
	}

	private boolean takeALook(int xS, int yS, Value v) {

		Cell c = map[yS][xS];
		if (c.getValue() == v)
			noReachableUnkowns++;

		else if (c.getValue() == Value.Wall)
			return false;

		return true;

	}

	@Override
	public  LinkedList<AStarNode> getReachableNodes(AStarNode position) {
		LinkedList<AStarNode> reachableNodes = new LinkedList<AStarNode>();
		int x = position.getX();
		int y = position.getY();
		
		if(x-1>=0 && map[y][x-1].getValue()!=Value.Wall)
			reachableNodes.add(map[y][x-1]);
		if(x+1<this.x && map[y][x+1].getValue()!=Value.Wall)
			reachableNodes.add(map[y][x+1]);
		if(y-1>=0 && map[y-1][x].getValue()!=Value.Wall)
			reachableNodes.add(map[y-1][x]);
		if(y+1<this.y && map[y+1][x].getValue()!=Value.Wall)
			reachableNodes.add(map[y+1][x]);
		
			
		
		return reachableNodes;
	}

	@Override
	public int calculateH(AStarNode currentPosition, AStarNode goal) {
		
		return 10 * (Math.abs(currentPosition.getX()-goal.getX()) + Math.abs(currentPosition.getY()-goal.getY()));
	}

	

}
