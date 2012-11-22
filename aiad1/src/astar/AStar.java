package astar;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

import org.apache.commons.collections.BinaryHeap;

public class AStar {

	public static <T extends AStarNode, M extends AStarMap> LinkedList<AStarNode> run(T initialPosition, T endPosition, M map){
		
		LinkedList<AStarNode> closedList = new LinkedList<AStarNode>();
		
		BinaryHeap openList = new BinaryHeap();
		initialPosition.setG(0);
		for (Object adjO: map.getReachableNodes(initialPosition)){
			AStarNode adj = (AStarNode) adjO;
			adj.setParent(initialPosition);
			adj.setG(initialPosition.getG()+adj.getG());
			adj.setH(map.calculateH(adj,endPosition));
			openList.add(adj);
		}
		
		closedList.add(initialPosition);
		while(!openList.isEmpty()){
			
			AStarNode currentNode = (AStarNode)openList.remove();
			if(currentNode.equals(endPosition)){
				LinkedList<AStarNode> path = getPath(currentNode);
				return path;
			}
			closedList.add(currentNode);
			for (Object adjO: map.getReachableNodes(currentNode)){
				AStarNode adj = (AStarNode) adjO;
				if(!closedList.contains(adj) ){
					if(!openList.contains(adj)){
						AStarNode newAdj = new AStarNode(adj);
						newAdj.setParent(currentNode);
						newAdj.setG(currentNode.getG()+adj.getG());
						newAdj.setH(map.calculateH(adj,endPosition));
						openList.add(newAdj);
					}else {
						
						AStarNode otherNode = findNode(openList,adj);
						
						if(adj.getG()+currentNode.getG() < otherNode.getG()){
							openList.remove(otherNode);
							otherNode.setParent(currentNode);
							otherNode.setG(currentNode.getG()+adj.getG());
							otherNode.setH(map.calculateH(otherNode, endPosition));
							openList.add(otherNode);
							
						}
					}
				}
			}
		}
		return null;
		
		
	}

	private static LinkedList<AStarNode> getPath(AStarNode currentNode) {

		LinkedList<AStarNode> path = new LinkedList<AStarNode>();
		Stack<AStarNode> revPath = new Stack<AStarNode>();
		
		revPath.push(currentNode);
		AStarNode parent = currentNode.getParent();
		while(parent!=null){
			revPath.push(parent);
			parent = parent.getParent();
		}
		while(!revPath.isEmpty())
			path.add(revPath.pop());
		return path;
	}

	private static AStarNode findNode(BinaryHeap openList,AStarNode adjacentNode) {
		Iterator i = openList.iterator();
		while(i.hasNext()){
			AStarNode n = (AStarNode)i.next();
			if (n.equals(adjacentNode))
				return n;
		}
		return null;
	}
	
}
