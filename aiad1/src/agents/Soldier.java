package agents;

import java.awt.Color;

import map.Cell.Value;

import uchicago.src.sim.gui.SimGraphics;
import uchicago.src.sim.space.Object2DGrid;

public class Soldier extends ArmyUnit {

	
	public Soldier(int x, int y,Object2DGrid space){
		super(x,y,new Color(0,0,255),space);
	}

	@Override
	public void draw(SimGraphics g) {
		g.drawFastCircle(color);
		
	}

	@Override
	public void move() {
		// TODO Auto-generated method stub
		
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
