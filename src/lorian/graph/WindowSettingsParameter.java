package lorian.graph;

public class WindowSettingsParameter extends WindowSettings{
	private double Tmin, Tmax, Tstep;
	private boolean auto_calc_tstep;
	
	public WindowSettingsParameter()
	{
		super();
		auto_calc_tstep = true;
		Tstep = 0.5;
		Tmin = 0;
		Tmax = 20;
	}
	public WindowSettingsParameter(boolean auto_calc_tstep, boolean auto_calc_y)
	{
		super(auto_calc_y);
		this.auto_calc_tstep = auto_calc_tstep;
		Tstep = 0.5;
		Tmin = 0;
		Tmax = 20;
	}
	public WindowSettingsParameter(double Xmin, double Xmax, double Ymin, double Ymax, double Tmin, double Tmax, double Tstep, boolean grid, boolean auto_calc_y, boolean auto_calc_tstep)
	{
		super(Xmin, Xmax, Ymin, Ymax, grid, auto_calc_y);
		this.auto_calc_tstep = auto_calc_tstep;
		this.Tstep = Tstep;
		this.Tmin = Tmin;
		this.Tmax = Tmax;
	}
	
	public double getTmin() {
		return Tmin;
	}
	public void setTmin(double tmin) {
		Tmin = tmin;
	}
	public double getTmax() {
		return Tmax;
	}
	public void setTmax(double tmax) {
		Tmax = tmax;
	}
	public double getTstep() {
		return Tstep;
	}
	public void setTstep(double tstep) {
		this.Tstep = tstep;
	}
	public boolean AutoCalcTStep() {
		return auto_calc_tstep;
	}
	public void setAutoCalcTStep(boolean auto_calc_tstep) {
		this.auto_calc_tstep = auto_calc_tstep;
	}


	
}
