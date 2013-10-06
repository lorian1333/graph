package lorian.graph;

public class WindowSettings3D extends WindowSettings {
	private double Zmin, Zmax;
	
	public WindowSettings3D()
	{
		super(false);
		setZmin(-10);
		setZmax(10);
		setGrid(false);
	}
	public WindowSettings3D(double Xmin, double Xmax, double Ymin, double Ymax, double Zmin, double Zmax, boolean grid)
	{
		super(Xmin, Xmax, Ymin, Ymax, grid, false);
		setZmin(Zmin);
		setZmax(Zmax);
	}
	
	public double getZmin() {
		return Zmin;
	}
	public void setZmin(double zmin) {
		Zmin = zmin;
	}
	public double getZmax() {
		return Zmax;
	}
	public void setZmax(double zmax) {
		Zmax = zmax;
	}
	

}
