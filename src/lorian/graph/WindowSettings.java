package lorian.graph;

public class WindowSettings {

	private double Xmin, Xmax, Ymin, Ymax;
	private boolean auto_calc_y, grid;
	
	public WindowSettings()
	{
		setXmin(-10);
		setXmax(10);
		setYmin(-10);
		setYmax(10);
		setGrid(false);
		setAutoCalcY(true);
	}
	public WindowSettings(boolean auto_calc_y)
	{
		this();
		setAutoCalcY(auto_calc_y);
	}
	public WindowSettings(double Xmin, double Xmax, double Ymin, double Ymax, boolean grid, boolean auto_calc_y)
	{
		setXmin(Xmin);
		setXmax(Xmax);
		setYmin(Ymin);
		setYmax(Ymax);
		setGrid(grid);
		setAutoCalcY(auto_calc_y);
	}
	
	public double getYmin() {
		return Ymin;
	}
	public void setYmin(double ymin) {
		Ymin = ymin;
	}
	public double getXmin() {
		return Xmin;
	}
	public void setXmin(double xmin) {
		Xmin = xmin;
	}
	public double getXmax() {
		return Xmax;
	}
	public void setXmax(double xmax) {
		Xmax = xmax;
	}
	public double getYmax() {
		return Ymax;
	}
	public void setYmax(double ymax) {
		Ymax = ymax;
	}
	public boolean gridOn() {
		return grid;
	}
	public boolean autoCalcY()
	{
		return auto_calc_y;
	}
	public void setAutoCalcY(boolean calc)
	{
		this.auto_calc_y = calc;
	}
	public void setGrid(boolean grid) {
		this.grid = grid;
	}
	
}
