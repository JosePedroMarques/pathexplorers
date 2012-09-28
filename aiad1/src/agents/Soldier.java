package agents;

import java.awt.Color;

import uchicago.src.sim.gui.SimGraphics;

public class Soldier extends ArmyUnit {

	
	public Soldier(int x, int y){
		super(x,y,new Color(0,0,255));
	}

	@Override
	public void draw(SimGraphics g) {
		g.drawOval(color);
		
	}
	
}
