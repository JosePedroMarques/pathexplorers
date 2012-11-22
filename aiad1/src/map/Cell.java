package map;





import astar.AStarNode;

public class Cell extends AStarNode  {
	
	
	
	
	
	public enum Value {
		Wall("W"),Empty("E"),Visited("V"), Captain("C"),Soldier("S"),Me("M"),Exit("X"),Unknown("U");
		
		private final String text;

		  Value(String text) {
		    this.text = text;
		  }
		  @Override
		  public String toString() {
		    return this.text;
		  }
	}
	private Value value;
	public Cell (){};
	public Cell(Value value,int x, int y){
		this.x = x;
		this.y = y;
		this.setValue(value);
		this.setG(10);
	}
	/**
	 * @return the value
	 */
	public Value getValue() {
		return value;
	}
	
	/**
	 * @param value the value to set
	 */
	public void setValue(Value value) {
		this.value = value;
	}

	public int getIntValue(){
		
		
		

		switch(value){
		case Wall : 
			return Integer.MAX_VALUE;
		case Empty:
			return 0;
		case Visited: 
			return 1;
		case Captain:
		case Me:
		case Soldier:
			return 1;
		case Exit: 
			return -1;
		default:
			return -2;
		
		}
	}
	
	@Override
	public String toString(){
		return this.value.toString();
	}
	
	
	


}
