package agents;

import java.awt.Color;

import uchicago.src.sim.gui.SimGraphics;

public class Captain extends ArmyUnit{
	
	
	public Captain(int x, int y){
		
	super(x,y,new Color(255,0,0));
	}

	@Override
	public void draw(SimGraphics g) {
		g.drawOval(color);
		
	}

}
