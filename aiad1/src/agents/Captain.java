package agents;

import java.awt.Color;

import map.Cell.Value;
import uchicago.src.sim.gui.SimGraphics;
import uchicago.src.sim.space.Object2DGrid;
import utils.Config;
import utils.Pair;
import astar.AStarNode;

public class Captain extends ArmyUnit{
	
	
	public Captain(int x, int y,Object2DGrid space,Config conf){
		
		super(x,y,new Color(255,0,0),space,conf);
		this.communicationRange = Math.max(space.getSizeX(),space.getSizeY());
	}

	@Override
	public void draw(SimGraphics g) {
		g.drawFastCircle(color);
		
		
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
