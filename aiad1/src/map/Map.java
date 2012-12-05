package map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import org.apache.commons.collections.BinaryHeap;

import astar.AStarMap;
import astar.AStarNode;

import map.Cell.Value;

public class Map extends AStarMap {

	private Cell[][] map;
	private int x;
	private int y;
	private Cell exit = null;

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
			exit = map[y][x];
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

	Collection<Cell> reachableValues;

	public Collection<Cell> getReachableValues(int x, int y, int sightRange,
			Collection<Value> v) {

	
		
		reachableValues = new ArrayList<Cell>();
		takeALook(x, y, v);
		// Look left
		int searched = 0;
		int xS = x;
		int yS = y;
		int maxSightRangeUp = sightRange;
		int maxSightRangeBottom = sightRange;
		int maxSightRange = sightRange;
		while (xS > 0 && searched <= maxSightRange) {

			maxSightRangeUp = beamSearch(xS, yS, 0, -1, maxSightRangeUp, v);
			maxSightRangeBottom = beamSearch(xS, yS, 0, 1, maxSightRangeBottom,
					v);
			searched++;
			if (!takeALook(xS, yS, v)){
				maxSightRange = searched;
				break;
				
			}
			xS--;

		}
		// Look right
		searched = 0;
		xS = x;
		yS = y;
		maxSightRangeUp = sightRange;
		maxSightRangeBottom = sightRange;
		maxSightRange = sightRange;
		while (xS < getX() && searched <= maxSightRange) {

			maxSightRangeUp = beamSearch(xS, yS, 0, -1, maxSightRangeUp, v);
			maxSightRangeBottom = beamSearch(xS, yS, 0, 1, maxSightRangeBottom,
					v);
			searched++;
			if (!takeALook(xS, yS, v)){
				maxSightRange = searched;
				break;
			}
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

		return reachableValues;

	}

	private int beamSearch(int xi, int yi, int xDir, int yDir,
			int maxSightRange, Collection<Value> v) {

		int searched = 0;
		int x = xi + xDir;
		int y = yi + yDir;
		while (isInside(x, y) && searched < maxSightRange && takeALook(x, y, v)) {
			y += yDir;
			x += xDir;
			searched++;

		}
		return Math.min(searched + 1,maxSightRange);

	}

	private boolean isInside(int x, int y) {
		return x >= 0 && y >= 0 && x < this.x && y < this.y;
	}

	private boolean takeALook(int xS, int yS, Collection<Value> v) {

		Cell c = map[yS][xS];
		if (v.contains(c.getValue()) && !reachableValues.contains(c))
			reachableValues.add(c);

		else if (c.getValue() == Value.Wall)
			return false;

		return true;

	}

	@Override
	public LinkedList<AStarNode> getReachableNodes(AStarNode position) {
		LinkedList<AStarNode> reachableNodes = new LinkedList<AStarNode>();
		int x = position.getX();
		int y = position.getY();

		if (x - 1 >= 0 && canMaybeGoInto(x-1,y))
			reachableNodes.add(map[y][x - 1]);
		if (x + 1 < this.x && canMaybeGoInto(x+1,y))
			reachableNodes.add(map[y][x + 1]);
		if (y - 1 >= 0 && canMaybeGoInto(x,y-1))
			reachableNodes.add(map[y - 1][x]);
		if (y + 1 < this.y && canMaybeGoInto(x,y+1))
			reachableNodes.add(map[y + 1][x]);

		return reachableNodes;
	}

	public boolean canGoInto(int x, int y) {
		// TODO Auto-generated method stub
		return map[y][x].getValue() == Value.Empty || map[y][x].getValue() == Value.Visited || map[y][x].getValue() == Value.Exit ;
	}
	
	public boolean canMaybeGoInto(int x, int y) {
		// TODO Auto-generated method stub
		return map[y][x].getValue() != Value.Wall;
	}

	@Override
	public int calculateH(AStarNode currentPosition, AStarNode goal) {

		return 10 * (Math.abs(currentPosition.getX() - goal.getX()) + Math
				.abs(currentPosition.getY() - goal.getY()));
	}

	public void printAStarWorking(LinkedList<AStarNode> closedList,
			BinaryHeap openList) {
		for (int i = 0; i < getY(); i++) {
			for (int j = 0; j < getX(); j++) {
				AStarNode a = map[i][j];
				if (closedList.contains(a))
					System.out.print("C ");
				else if (openList.contains(a))
					System.out.print("O ");
				else
					System.out.print(map[i][j].toString() + " ");
			}

			System.out.println();
		}
	}

	public Cell getExit() {

		return exit;
	}

	public ArrayList<Cell> getCellsAtRadius(int x, int y, int radius, Value v) {
		ArrayList<Cell> cells = new ArrayList<Cell>();
		if(v != Value.Empty &&v != Value.Unknown )
			System.out.println("WPOOT");
		int maxLeft = Math.max(0, x - radius);
		int maxRight = Math.min(getX()-1, x + radius);
		int maxUp = Math.max(0, y - radius);
		int maxDown = Math.min(getY()-1, y + radius);

		// recolhe as da esquerda e as da direita
		for (int i = maxUp; i <= maxDown; i++) {
			if (map[i][maxLeft].getValue() == v)
				cells.add(map[i][maxLeft]);
			if (map[i][maxRight].getValue() == v)
				cells.add(map[i][maxRight]);

		}
		// recolhe as de baixo e as de cima
		for (int i = maxLeft+1; i < maxRight-1; i++) {
			if (map[maxUp][i].getValue() == v)
				cells.add(map[maxUp][i]);
			if (map[maxDown][i].getValue() == v)
				cells.add(map[maxDown][i]);

		}

		/* for (int j = maxLeft; j<maxRight;j++) */

		return cells;

	}

}
