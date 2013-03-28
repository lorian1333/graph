package lorian.graph.function;

import java.text.DecimalFormat;

public class Calculate {

	
	public static PointXY Intersect(Function f, Function g, PointXY min, double width)
	{
		double smallestDelta = 1000000000;
		double currentDelta;
		double step = 10 / 10000000.0;
		double x = 0, x2 = 0;
		 
	
		for(x = min.getX(); x < width;x+=0.1)
		{
			currentDelta = Math.abs(g.Calc(x) - f.Calc(x));
			if(currentDelta < 1)
			{
				x2 = x;
				break;
			}
		}
		
		for(x = x2; x < x2 + 1; x+= 0.001)
		{
			currentDelta = Math.abs(g.Calc(x) - f.Calc(x));
			if(currentDelta < 0.01)
			{
				x2 = x;
				break;
			}
		}
		
		for(x = x2; x < x2 + 1; x+= step)
		{
			currentDelta = Math.abs(g.Calc(x) - f.Calc(x));
			if(smallestDelta > currentDelta)
			{
				smallestDelta = currentDelta;
				x2 = x;
			}
			
		}
		return new PointXY(x2, f.Calc(x2));
	}
	
	public static double DyDx(Function f, double x)
	{
		//0.0000001
		double dx = 0.000000001;
		double dydx = (f.Calc(x+dx) - f.Calc(x)) / dx;
		
		return Util.round(dydx, 6);
	}
	
	public static double Integral(Function f, double LowX, double UpX)
	{
		long starttime = System.currentTimeMillis();
		double dx = 0.00001;
		double lowersum=0, uppersum=0;
		for(double x = LowX; x < UpX; x += dx)
		{
			if(x != LowX)
			{
				uppersum += f.Calc(x) * dx;
			}
			if(x != (UpX - dx))
			{
				lowersum += f.Calc(x) * dx;
			}
		}
		double average = (lowersum + uppersum) / 2;
		System.out.println("Passed: " + (System.currentTimeMillis() - starttime) + "ms");
		return Util.round(average, 6);
	}
	
	public static PointXY Minimum(Function f, double LowX, double UpX)
	{
		double dx = 0.000001;
		double lowestX = LowX - 1;
		double lowest = 9999999;
		double val;
		for(double x = LowX + dx; x < UpX; x += dx)
		{
			val = f.Calc(x);
			if(val < lowest)
			{
				lowest = val;
				lowestX = x;
			}
		}
		return new PointXY(Util.round(lowestX, 6), f.Calc(lowestX));
	}
	
	public static PointXY Maximim(Function f, double LowX, double UpX)
	{
		double dx = 0.000001;
		double highestX = LowX - 1;
		double highest = -9999999;
		double val;
		for(double x = LowX; x < UpX; x += dx)
		{
			val = f.Calc(x);
			if(val > highest)
			{
				highest = val;
				highestX = x;
			}
		}
		return new PointXY(Util.round(highestX, 6), f.Calc(highestX));
	}
}
