package lorian.graph;

public class WindowSettings {

	private long Xmin, Xmax, Ymin, Ymax;
	public WindowSettings()
	{
		setXmin(-10);
		setXmax(10);
		setYmin(-10);
		setYmax(10);
	}
	public WindowSettings(int Xmin, int Xmax, int Ymin, int Ymax)
	{
		setXmin(Xmin);
		setXmax(Xmax);
		setYmin(Ymin);
		setYmax(Ymax);
	}
	
	public long getYmin() {
		return Ymin;
	}
	public void setYmin(long ymin) {
		Ymin = ymin;
	}
	public long getXmin() {
		return Xmin;
	}
	public void setXmin(long xmin) {
		Xmin = xmin;
	}
	public long getXmax() {
		return Xmax;
	}
	public void setXmax(long xmax) {
		Xmax = xmax;
	}
	public long getYmax() {
		return Ymax;
	}
	public void setYmax(long ymax) {
		Ymax = ymax;
	}
	
}
