package utils;

import java.util.ArrayList;

import uchicago.src.sim.util.Random;

public class DirectionList implements Comparable<DirectionList>{

	private float gainValue= 0;
	private ArrayList<Pair<Integer, Integer>> directions;

	
	public DirectionList(int gain,ArrayList<Pair<Integer, Integer>> directions ){
		this.setGainValue(gain);
		this.setDirections(directions);
		
	}


	/**
	 * @return the gainValue
	 */
	public float getGainValue() {
		return gainValue;
	}


	/**
	 * @param f the gainValue to set
	 */
	public void setGainValue(float f) {
		this.gainValue = f;
	}


	/**
	 * @return the directions
	 */
	public ArrayList<Pair<Integer, Integer>> getDirections() {
		return directions;
	}


	/**
	 * @param directions the directions to set
	 */
	public void setDirections(ArrayList<Pair<Integer, Integer>> directions) {
		this.directions = directions;
	}
	
	public Pair<Integer,Integer> getRandomDirection(){
		return directions.get(Random.uniform.nextIntFromTo(0,
					directions.size() - 1));
	}

	public void addDirection(Pair<Integer,Integer> d){
		directions.add(d);
	}

	@Override
	public int compareTo(DirectionList o) {
		if(gainValue < o.getGainValue() )
			return 1;
		if(gainValue > o.getGainValue() )
			return -1;
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public String toString(){

		return gainValue + " : " + directions.toString();
	
	}


	public void removeDirection(Pair<Integer, Integer> direction) {
		directions.remove(direction);
		
	}
}
 