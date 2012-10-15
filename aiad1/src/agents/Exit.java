package agents;

import java.awt.Color;

import map.Cell.Value;
import uchicago.src.sim.gui.SimGraphics;
import uchicago.src.sim.space.Object2DGrid;

public class Exit extends BasicAgent {

	public Exit(int x, int y){
		super(x,y,new Color(255,255,255));
	}

	@Override
	public void draw(SimGraphics g) {
		g.drawFastCircle(color);
		
	}

	@Override
	public Value getValue() {
		return Value.Exit;
	}

	@Override
	public boolean canReceiveComms() {
		// TODO Auto-generated method stub
		return false;
	}

}
