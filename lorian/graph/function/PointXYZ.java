package lorian.graph.function;

public class PointXYZ extends PointXY {
	
	private double z;
	
	public PointXYZ()
	{
		super(0, 0);
		this.z = 0;
	}
	public PointXYZ(double x, double y, double z)
	{
		super(x, y);
		this.z = z;
	}
	public double getZ()
	{
		return this.z;
	}
	public void setZ(double z)
	{
		this.z = z;
	}
	@Override
	public String toString()
	{
		return String.format("%s[x=%f,y=%f,z=%f]", this.getClass().getName(), super.getX(), super.getY(), z);
	}
	
	
}
