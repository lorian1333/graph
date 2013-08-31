package lorian.graph.opengl;

import java.awt.Point;

public class Point3D extends Point {
	private static final long serialVersionUID = 1830852758529315907L;
	public int z;
	
	public Point3D()
	{
		super();
		this.z = 0;
	}
	public Point3D(int x, int y, int z)
	{
		super(x, y);
		this.z = z;
	}
	
	public Point3D(Point3D p)
	{
		super((Point) p);
		this.z = (int) p.getZ();
	}
	
	public Point3D getLocation()
	{
		return this;
	}
	
	public double getZ()
	{
		return this.z;
	}
	
	public void move(int x, int y, int z)
	{
		super.move(x, y);
		this.z = z;
	}
	public void setLocation(int x, int y, int z)
	{
		super.setLocation(x, y);
		this.z = z;
	}
	public void setLocation(double x, double y, double z)
	{
		this.setLocation((int)x, (int)y, (int)z);
	}
	public void setLocation(Point3D p)
	{
		setLocation(p.getX(), p.getY(), p.getZ());
	}
	
	@Override
	public String toString()
	{
		return this.getClass().getName()+ String.format("[x=%d,y=%d,z=%d]", super.x, super.y, this.z);
	}
	
	public void translate(int dx, int dy, int dz)
	{
		super.translate(dx, dy);
		this.z += dz;
		
	}
}
