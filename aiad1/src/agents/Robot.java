	package agents;
 
    
	

	import java.awt.Color;

	import map.Cell.Value;


	import uchicago.src.sim.gui.SimGraphics;
	import uchicago.src.sim.space.Object2DGrid;

	public class Robot extends ArmyUnit {

    int live;
		
		public Robot(int x, int y,Object2DGrid space, int live_robot ){
			super(x,y,new Color(119,136,153),space);
			this.communicationRange = 1;
			this.live = live_robot;
		}

		@Override
		public void draw(SimGraphics g) {
			g.drawFastCircle(color);
			
		}

		public void move() {
			live-=1;
			if (live > 0)
			super.move();
			
				
				
			   
				
			
			
		}

		@Override
		public Value getValue() {
			return Value.Robot;
		}

		@Override
		public boolean canReceiveComms() {
			// TODO Auto-generated method stub
			return true;
		}
		
	}
