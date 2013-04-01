package lorian.graph.function;

public class VisualPoint {
	private PointXY point;
	private boolean movable;
	
	public VisualPoint(PointXY p, boolean movable)
	{
		setPoint(p);
		setMovable(movable);
	}
	public PointXY getPoint() {
		return point;
	}
	public void setPoint(PointXY point) {
		this.point = point;
	}
	public boolean isMovable() {
		return movable;
	}
	public void setMovable(boolean movable) {
		this.movable = movable;
	}
}
