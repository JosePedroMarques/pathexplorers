package agents;

import java.awt.Color;

import map.Cell.Value;

import uchicago.src.sim.gui.SimGraphics;
import uchicago.src.sim.space.Object2DGrid;

public class Soldier extends ArmyUnit {

	
	public Soldier(int x, int y,Object2DGrid space, int communication_Range){
		super(x,y,new Color(0,0,255),space);
		this.communicationRange = communication_Range;
	}

	@Override
	public void draw(SimGraphics g) {
		g.drawFastCircle(color);
		
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
	
}
