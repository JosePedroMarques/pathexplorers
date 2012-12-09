package agents;

import java.awt.Color;

import map.Cell.Value;

import uchicago.src.sim.gui.SimGraphics;

public class Wall extends BasicUnit {

	
	public Wall(int x,int y)
	{
		super(x,y,new Color(0,255,0));
	}
	@Override
	public void draw(SimGraphics g) {
		g.drawFastCircle(color);
	
		
	}
	@Override
	public Value getValue() {
		return Value.Wall;
	}
	@Override
	public boolean canReceiveComms() {
		// TODO Auto-generated method stub
		return false;
	}


	

}
