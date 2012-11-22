package utils;

import java.util.ArrayList;

import uchicago.src.sim.util.Random;

public class DirectionList implements Comparable<DirectionList>{

	private int gainValue= 0;
	private ArrayList<Pair<Integer, Integer>> directions;

	
	public DirectionList(int gain,ArrayList<Pair<Integer, Integer>> directions ){
		this.setGainValue(gain);
		this.setDirections(directions);
		
	}


	/**
	 * @return the gainValue
	 */
	public int getGainValue() {
		return gainValue;
	}


	/**
	 * @param gainValue the gainValue to set
	 */
	public void setGainValue(int gainValue) {
		this.gainValue = gainValue;
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
}
 