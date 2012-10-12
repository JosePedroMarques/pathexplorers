package map;

public class Cell {
	
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
	Cell (){};
	public Cell(Value value){
		this.setValue(value);
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
