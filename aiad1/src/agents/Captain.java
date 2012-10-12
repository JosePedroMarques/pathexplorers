package agents;

import java.awt.Color;

import map.Cell.Value;


import uchicago.src.sim.gui.SimGraphics;
import uchicago.src.sim.space.Object2DGrid;
import uchicago.src.sim.util.Random;

public class Captain extends ArmyUnit{
	
	
	public Captain(int x, int y,Object2DGrid space){
		
	super(x,y,new Color(255,0,0),space);
	}

	@Override
	public void draw(SimGraphics g) {
		g.drawFastCircle(color);
		
	}

	@Override
	public void move() {
		
		int xMove = Random.uniform.nextIntFromTo(0, 2)-1;
		int yMove = Random.uniform.nextIntFromTo(0, 2)-1;
		if(space.getObjectAt(this.x+xMove, this.y+yMove) == null) {
			space.putObjectAt(this.x, this.y,null);
			this.x += xMove;
			this.y += yMove;
			space.putObjectAt(this.x, this.y, this);
		}
		
	}

	@Override
	public Value getValue() {
		return Value.Captain;
	}

}
