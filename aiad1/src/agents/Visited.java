package agents;

import java.awt.Color;

import map.Cell.Value;
import uchicago.src.sim.gui.SimGraphics;

public class Visited extends BasicUnit{

	public Visited(int x,int y)
	{
		super(x,y,new Color(55,71,71));
	}
	@Override
	public void draw(SimGraphics g) {
		g.drawFastCircle(color);
	
		
	}
	@Override
	public Value getValue() {
		return Value.Visited;
	}
	@Override
	public boolean canReceiveComms() {
		// TODO Auto-generated method stub
		return false;
	}


}
