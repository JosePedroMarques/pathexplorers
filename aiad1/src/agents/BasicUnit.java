package agents;

import java.awt.Color;

import map.Cell.Value;

import uchicago.src.sim.gui.Drawable;


public abstract class BasicUnit implements Drawable {

	int x;
	int y;
	Color color;
	
	public BasicUnit(){};
	
	public BasicUnit(int x, int y, Color color){
	
		this.x =x;
		this.y= y;
		this.color=color;
	}
	
	
	@Override
	public int getX() {
		// TODO Auto-generated method stub
		return x;
	}

	@Override
	public int getY() {
		// TODO Auto-generated method stub
		return y;
	}

	public abstract Value getValue() ;

	public abstract boolean canReceiveComms();

}
