package lorian.graph.function;

public class Calculate {

	
	private static double returnRounded(double d)
	{
		double r = Util.round(d, 0);
		if(Math.abs(r - d) < 0.0001)
			return r;
		else 
			return d;
	}
	
	public static PointXY Zero(Function f, double LowX, double UpX)
	{
		if(UpX <= LowX) return new PointXY(Double.NaN, Double.NaN);
		double step = 10 / 10000000.0;
		double closest = 1, closestX = LowX - 1;
		for(double x = LowX + 0.001; x < UpX; x += step)
		{
			if(f.Calc(x)==0) return new PointXY(x, 0);
			else if(Math.abs(f.Calc(x)) < Math.abs(closest))
			{
				closest = Math.abs(f.Calc(x));
				closestX = x;
			}
		}
		if(closestX == LowX-1)
			return new PointXY(Double.NaN, Double.NaN);
		else
			return new PointXY(returnRounded(Util.round(closestX, 6)), 0);
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
			if(Double.isInfinite(-val)) return new PointXY(returnRounded(Util.round(x, 6)), val);
			if(val < lowest)
			{
				lowest = val;
				lowestX = x;
			}
		}
		return new PointXY(returnRounded(Util.round(lowestX, 6)), returnRounded(f.Calc(lowestX)));
	}
	public static PointXY Maximum(Function f, double LowX, double UpX)
	{
		double dx = 0.000001;
		double highestX = LowX - 1;
		double highest = -9999999;
		double val;
		for(double x = LowX; x < UpX; x += dx)
		{
			val = f.Calc(x);
			if(Double.isInfinite(val)) return new PointXY(returnRounded(Util.round(x, 6)), val);
			if(val > highest)
			{
				highest = val;
				highestX = x;
			}
		}
		return new PointXY(returnRounded(Util.round(highestX, 6)), returnRounded(f.Calc(highestX)));
	}
	public static PointXY Intersect(Function f, Function g, double LowX, double UpX)
	{
		double smallestDelta = 1000000000;
		double currentDelta;
		double step = 10 / 10000000.0;
		double x = 0, x2 = 0;
		 
	
		for(x = LowX + 0.001; x < UpX;x+=0.1)
		{
			currentDelta = Math.abs(g.Calc(x) - f.Calc(x));
			if(currentDelta < 1)
			{
				x2 = x;
				break;
			}
		}
		
		for(x = x2; x < x2 + 1 && x < UpX; x+= 0.001)
		{
			currentDelta = Math.abs(g.Calc(x) - f.Calc(x));
			if(currentDelta < 0.01)
			{
				x2 = x;
				break;
			}
		}
		
		for(x = x2; x < x2 + 1 && x < UpX; x+= step)
		{
			currentDelta = Math.abs(g.Calc(x) - f.Calc(x));
			if(smallestDelta > currentDelta)
			{
				smallestDelta = currentDelta;
				x2 = x;
			}
			
		}
		if(smallestDelta < 0.1)
			return new PointXY(Util.round(x2, 6), Util.round(f.Calc(x2), 6));
		else 
			return new PointXY(Double.NaN, Double.NaN);
	}	

	
	public static double DyDx(Function f, double x)
	{
		if(Double.isNaN(x)) return Double.NaN;
		//double dx = 0.000000001;
		double dx   = 0.000001;
		
		double dydx;
		double dy = (f.Calc(x+dx) - f.Calc(x));
		
		dydx = dy / dx;
		
		return returnRounded(Util.round(dydx, 6));
		//return Util.round(dydx, 6);
	}	
	public static double DyDx(Function f, double x, double dx)
	{
		if(Double.isNaN(x) || Double.isNaN(dx)) return Double.NaN;
		double dydx;
		double dy = (f.Calc(x+dx) - f.Calc(x));
		
		dydx = dy / dx;
		return dydx;
	
	}
	
	public static double Integral(Function f, double LowX, double UpX)
	{
		double dx = 0.00001;
		//double dx =   0.000001;
		double lowersum=0, uppersum=0;
		double val;
		for(double x = LowX; x < UpX; x += dx)
		{
			val = f.Calc(x);
			if(Double.isNaN(val)) return Double.NaN;
			if(x != LowX)
			{
				uppersum += val * dx;
			}
			if(x != (UpX - dx))
			{
				lowersum += val * dx;
			}
		}
		double average = (lowersum + uppersum) / 2;
		average = Util.round(average, 6);
		return returnRounded(average);
	}
	public static double FindLastXBeforeNaN(Function f, double Xstart)
	{
		double dx = 0.0001;
		if(Double.isNaN(f.Calc(Xstart))) return Double.NaN;
		
		double Xtmp = 0;
		for(double x = Xstart; x < Xstart + 50 ; x += 0.001)
		{
			if(Double.isNaN(f.Calc(x))) {
				Xtmp = x - 0.001;
				break;
			}
		}

		for(double x = Xtmp; x < Xtmp + 1; x += dx)
		{
			if(Double.isNaN(f.Calc(x)))
			{
				return (x - dx);
			}
		}
		
		return Double.NaN;
	}
	public static double FindFirstXAfterNaN(Function f, double Xstart)
	{
		double dx = 0.0001;
		if(!Double.isNaN(f.Calc(Xstart))) return Xstart;
		
		double Xtmp = 0;
		for(double x = Xstart; x < Xstart + 50 ; x += 0.001)
		{
			if(!Double.isNaN(f.Calc(x))) {
				Xtmp = x - 0.001;
				break;
			}
		}

		for(double x = Xtmp; x < Xtmp + 1; x += dx)
		{
			if(!Double.isNaN(f.Calc(x)))
			{
				return x;
			}
		}
		
		return Double.NaN;
	}
	
	public static int Factorial(int n)
	{
		/*
		int result = 1;
		if(n == 0) return 1;
		if(n < 0) return -1;
		
		while(n > 0)
		{
			result *= n;
			n--;
		}
		return result;
		*/
		return (int) Product('i', 1, n, "i");
	}
	
	public static int BinomialCoefficient(int n, int k)
	{
		if(k <0 || k > n) return 0;
		return (Factorial(n) / (Factorial(k) * Factorial (n-k)));
	}
	
	public static double Summation(char index, int start, int stop, String body)
	{
		body = body.toLowerCase();
		index = ("" + index).toLowerCase().charAt(0);
		Function f = new Function(index);
		if(!f.Parse(body))
		{
			System.out.println("Error parsing '" + body + "'");
			return Double.NaN;
		}
		double sum = 0;
		for(int i=start; i <= stop; i++)
		{
			sum += f.Calc(i);
		}
		return sum;
	}
	/*
	 Calculates PI:
	 Summation('k', 0, 1000, "1/(16^k)(4/(8k+1)-2/(8k+4)-1/(8k+5)-1/(8k+6))") 
		
	 Calculates E:
	 Summation('k', 0, 1000, "1/fac(k)")
	*/
	public static double Product(char index, int start, int stop, String body)
	{
		body = body.toLowerCase();
		index = ("" + index).toLowerCase().charAt(0);
		Function f = new Function(index);
		if(!f.Parse(body))
		{
			System.out.println("Error parsing '" + body + "'");
			return Double.NaN;
		}
		double product = 1;
		for(int i=start; i <= stop; i++)
		{
			product *= f.Calc(i);
		}
		return product;
	}
	public static void main(String[] args)
	{
		//(4+5)^6
		int x = 4;
		int y = 5;
		int n = 6;
		String bodystr = String.format("bin(%d, k) * %d^(%d-k) * %d^k", n, x, n, y);
		System.out.println(Summation('k', 0, n, bodystr));
	}
	
	
	
}