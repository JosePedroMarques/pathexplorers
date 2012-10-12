package map;

import map.Cell.Value;

public class Map {

	private Cell[][] map;
	private int x;
	private int y;
	public Map(int x, int y){
		this.setX(x);
		this.setY(y);
		map = new Cell[y][x];

		for(int i =0 ; i <y;i++ )
			for(int j = 0; j < x; j++)
				map[i][j] = new Cell(Value.Unknown); 
	}
	
	public void setPosition(int x, int y, Cell pos){
		map[y][x] = pos;
	}
	
	
	public Cell getPosition(int x, int y){
		return map[y][x];
	}
	
	@Override
	public String toString(){
		String r = "";
		
		for(int i =0 ; i <getY();i++ ){
			for(int j = 0; j < getX(); j++)
				r+= map[i][j].toString() +" ";
			r+= "\n";
		}
		
		
		return r;
		
		
	}
	public String getStringMap(){
		return toString();
	}

	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}

	/**
	 * @param x the x to set
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
	 * @param y the y to set
	 */
	public void setY(int y) {
		this.y = y;
	}
}
