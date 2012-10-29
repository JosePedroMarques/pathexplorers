package astar;

public class AStarNode implements Comparable<AStarNode> {

	protected int x;
	protected int y;
	private int gValue;
	private int hValue;
	private AStarNode parent;

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public AStarNode() {
	};

	public AStarNode(AStarNode otherNode) {

		this.setG(otherNode.getG());
		this.setParent(otherNode.getParent());
		this.x = otherNode.getX();
		this.y = otherNode.getY();
	}

	/**
	 * @return the g
	 */
	public int getG() {
		return gValue;
	}

	/**
	 * @param g
	 *            the g to set
	 */
	public void setG(int g) {
		gValue = g;
	}

	/**
	 * @return the f
	 */
	public int getH() {
		return hValue;
	}

	/**
	 * @param f
	 *            the f to set
	 */
	public void setH(int h) {
		hValue = h;
	}

	/**
	 * @return the parent
	 */
	public AStarNode getParent() {
		return parent;
	}

	/**
	 * @param parent
	 *            the parent to set
	 */
	public void setParent(AStarNode parent) {
		this.parent = parent;
	}

	@Override
	public int compareTo(AStarNode aThat) {
		int thisF = getG() + getH();
		int thatF = aThat.getG() + aThat.getH();
		if (thisF < thatF)
			return -1;
		if (thisF > thatF)
			return 1;
		return 0;

	}

	@Override
	public boolean equals(Object aThat) {

		AStarNode otherNode = (AStarNode) aThat;
		return this.getX() == otherNode.getX()
				&& this.getY() == otherNode.getY();

	}

	@Override
	public int hashCode() {
		int result = this.getX()
				* (int) Math.pow(10, (this.getY() + "").length()) + this.getY();
		return result;
	}

	public void setY(int y) {
		// TODO Auto-generated method stub
		this.y = y;
	}

	public void setX(int x) {
		// TODO Auto-generated method stub
		this.x = x;
	}
	
	

	public String getCoords() {
		return "( " + this.x+ " , " + this.y+" )";
	}

}
