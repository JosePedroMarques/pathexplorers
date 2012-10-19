package agents;

import java.awt.Color;

import map.Cell.Value;


import uchicago.src.sim.gui.SimGraphics;
import uchicago.src.sim.space.Object2DGrid;
import uchicago.src.sim.util.Random;

public class Captain extends ArmyUnit{
	
	
	public Captain(int x, int y,Object2DGrid space){
		
		super(x,y,new Color(255,0,0),space);
		this.communicationRange = 7;
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

}
