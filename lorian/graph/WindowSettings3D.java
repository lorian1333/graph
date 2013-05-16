package lorian.graph;

public class WindowSettings3D extends WindowSettings {
	private long Zmin, Zmax;
	
	public WindowSettings3D()
	{
		super();
		setZmin(-10);
		setZmax(10);
		setGrid(false);
	}
	public WindowSettings3D(long Xmin, long Xmax, long Ymin, long Ymax, long Zmin, long Zmax, boolean grid)
	{
		super(Xmin, Xmax, Ymin, Ymax, grid);
		setZmin(Zmin);
		setZmax(Zmax);
	}
	
	public long getZmin() {
		return Zmin;
	}
	public void setZmin(long zmin) {
		Zmin = zmin;
	}
	public long getZmax() {
		return Zmax;
	}
	public void setZmax(long zmax) {
		Zmax = zmax;
	}
	

}
