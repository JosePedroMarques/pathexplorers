package agents;

import java.awt.Color;

import astar.AStarNode;

import map.Cell.Value;

import uchicago.src.sim.gui.SimGraphics;
import uchicago.src.sim.space.Object2DGrid;
import utils.Config;
import utils.Pair;

public class Soldier extends ArmyUnit {

	
	
	
	public Soldier(int x, int y,Object2DGrid space,Config conf){
		super(x,y,new Color(0,0,255),space,conf);
		this.commColor = new Color(255,255,255);
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
					System.out.println("Decided to comunicate");
				super.broadcastMap();
				radioBattery--;
			}
			
			
		}
	}

	@Override
	public Value getValue() {
		return Value.Soldier;
	}

	@Override
	public boolean canReceiveComms() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected Pair<Integer, Integer> onExitFoundAction(AStarNode nextNode) {
	
		if(hasReachedExit){
			if(hasCommunicatedWithCaptain || waitTime >= TIMEOUT){
				hasExited = true;
				return  new Pair<Integer, Integer>(x,
						y);
			}
			aStarPath.clear();
			aStarPath.push(nextNode);
			waitTime++;
			return  new Pair<Integer, Integer>(x,
					y);
		}
		waitTime = 0;
		return  new Pair<Integer, Integer>(nextNode.getX(),
				nextNode.getY());
		
	}
	public void resetColor() {
		this.color = new Color(0,0,255);
		
	}
	
}
