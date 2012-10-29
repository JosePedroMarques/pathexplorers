package astar;

import java.util.LinkedList;

public abstract class AStarMap {

	
	public abstract  LinkedList<AStarNode> getReachableNodes(AStarNode position);
	
	public abstract  int calculateH(AStarNode currentPosition,AStarNode goal);
}
