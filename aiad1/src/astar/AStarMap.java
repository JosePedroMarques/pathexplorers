package astar;

import java.util.LinkedList;

import org.apache.commons.collections.BinaryHeap;

public abstract class AStarMap {

	
	public abstract  LinkedList<AStarNode> getReachableNodes(AStarNode position);
	
	public abstract  int calculateH(AStarNode currentPosition,AStarNode goal);
	public abstract void printAStarWorking(LinkedList<AStarNode> closedList,BinaryHeap openList);
}
