package lorian.graph.function;

import java.util.ArrayList;
import java.util.List;

public class VisualPoint {
	private PointXY point;
	private boolean movable;
	private boolean showCoordinates;
	private String label;
	private int functionIndex;
	private List<VisualPointLocationChangeListener> listeners;
	
	public VisualPoint(PointXY p, boolean movable)
	{
		listeners = new ArrayList<VisualPointLocationChangeListener>();
		setPoint(p);
		setMovable(movable);
		setShowCoordinates(false);
		setLabel("");
		setFunctionIndex(-1);
		
	}
	public VisualPoint(PointXY p, int funcindex, boolean movable)
	{
		listeners = new ArrayList<VisualPointLocationChangeListener>();
		setPoint(p);
		setMovable(movable);
		setShowCoordinates(false);
		setLabel("");
		setFunctionIndex(funcindex);
		
	}
	public VisualPoint(PointXY p, int funcindex,  boolean movable, boolean showCoordinates)
	{
		listeners = new ArrayList<VisualPointLocationChangeListener>();
		setPoint(p);
		setMovable(movable);
		setShowCoordinates(showCoordinates);
		setFunctionIndex(funcindex);
		setLabel("");
		
	}
	public VisualPoint(PointXY p, int funcindex, boolean movable, boolean showCoordinates, String label)
	{
		listeners = new ArrayList<VisualPointLocationChangeListener>();
		setPoint(p);
		setMovable(movable);
		setShowCoordinates(showCoordinates);
		setFunctionIndex(funcindex);
		setLabel(label);
	}
	public PointXY getPoint() {
		return point;
	}
	public void setPoint(PointXY point) {
		this.point = point;
		
		for(VisualPointLocationChangeListener l: listeners)
		{
			l.OnLocationChange(this);
		}
	}
	public void setPoint(PointXY point, boolean informListeners) {
		this.point = point;
		if(informListeners)
		{
			for(VisualPointLocationChangeListener l: listeners)
			{
				l.OnLocationChange(this);
			}
		}
	}
	public boolean isMovable() {
		return movable;
	}
	public void setMovable(boolean movable) {
		this.movable = movable;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public boolean coordinatesOn() {
		return showCoordinates;
	}
	public void setShowCoordinates(boolean showCoordinates) {
		this.showCoordinates = showCoordinates;
	}
	public int getFunctionIndex() {
		return functionIndex;
	}
	public void setFunctionIndex(int functionIndex) {
		this.functionIndex = functionIndex;
	}
	public void addLocationChangedListener(VisualPointLocationChangeListener listener)
	{
		this.listeners.add(listener);
	}
}

