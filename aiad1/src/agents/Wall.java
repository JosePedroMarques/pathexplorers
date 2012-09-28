package agents;

import java.awt.Color;
import java.awt.image.ColorModel;

import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;

public class Wall extends BasicAgent {

	
	public Wall(int x,int y)
	{
		super(x,y,new Color(0,255,0));
	}
	@Override
	public void draw(SimGraphics g) {
		g.drawFastCircle(color);
	
		
	}

	

}