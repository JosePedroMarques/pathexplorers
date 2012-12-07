package agents;

import java.awt.Color;
import java.util.Vector;

import map.Cell;
import map.Cell.Value;
import uchicago.src.sim.gui.SimGraphics;
import uchicago.src.sim.space.Object2DGrid;
import utils.Config;
import utils.Pair;
import astar.AStarNode;

public class Captain extends ArmyUnit{
	
	
	public Captain(int x, int y,Object2DGrid space,Config conf){
		
		super(x,y,new Color(255,0,0),space,conf);
		this.communicationRange = conf.getCommunication_Range();
	}

	@Override
	public void draw(SimGraphics g) {
		g.drawFastCircle(color);
		
		
	}

	@Override
	public void broadcastMap(){
		
		
		if(this.radioBattery > 0){
			if(knowsExitLocation() || stoppedBacktracking){
				if(VERBOSE)
					System.out.println("Decided to comunicate via radio");
				super.broadcastMap();
				radioBattery--;
			}			
		}
		
		
		communicateWithCaptains();
		
	}
	

	private void communicateWithCaptains() {
		Cell oldCell = map.getPosition(x, y);
		map.setPosition(x, y, new Cell(getValue(), x, y));
	
		Vector v = space.getMooreNeighbors(x, y, map.getX(),
				map.getY(), false);
		for (Object o : v) {
			BasicAgent ba = (BasicAgent)o;
			if (ba.canReceiveComms()) {
				ArmyUnit a = (ArmyUnit) o;
				if(a.getValue() == Value.Captain)
					a.receiveComm(this.map, this.getValue());
			}
		}
		map.setPosition(x, y, oldCell);
		
	}

	@Override
	public Value getValue() {
		return Value.Captain;
	}

	@Override
	public boolean canReceiveComms() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected Pair<Integer, Integer> onExitFoundAction(AStarNode nextNode) {
		// TODO Auto-generated method stub
		if(hasReachedExit)
			hasExited = true;
		return  new Pair<Integer, Integer>(nextNode.getX(),
				nextNode.getY());
		
	}

}
