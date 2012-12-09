	package agents;
 
    
	

	import java.awt.Color;

import astar.AStarNode;

	import map.Cell.Value;


	import uchicago.src.sim.gui.SimGraphics;
import uchicago.src.sim.space.Object2DGrid;
import utils.Config;
import utils.Pair;

	public class Robot extends ArmyUnit {

    int live;
		
		public Robot(int x, int y,Object2DGrid space, Config conf ){
			super(x,y,new Color(119,136,153),space,conf);
			this.communicationRange = 1;
			this.live = conf.getLive_robot();
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

		@Override
		protected Pair<Integer, Integer> onExitFoundAction(AStarNode nextNode) {
			if(hasReachedExit){
				if(hasCommunicatedWithCaptain){
					hasExited = true;
					return  new Pair<Integer, Integer>(x,
							y);
				}
				aStarPath.clear();
				aStarPath.push(nextNode);
				waitTime++;
				return  new Pair<Integer, Integer>(x,
							y);
			}
			return  new Pair<Integer, Integer>(nextNode.getX(),
					nextNode.getY());
			
		
		}

		@Override
		public void resetColor() {
			this.color = new Color(119,136,153);
			
		}
		
	}
