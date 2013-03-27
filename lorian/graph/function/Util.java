package lorian.graph.function;

public class Util {
	public static String removeWhiteSpace(String s)
	{
		s = s.trim();
		String ss = ""; 
		for(int i=0;i<s.length();i++)
		{
			if(s.charAt(i) != ' ' && s.charAt(i) != '\t')
				ss += s.charAt(i);
		}
		return ss;
	}
	public static boolean StringContains(String s, char ch)
	{
		
		for(int i=0;i<s.length();i++)
		{
			if(s.charAt(i) == ch) return true;
		}
		return false;
	}
	public static PointXY Intersect(Function f, Function g, PointXY min, double width)
	{
		double smallestDelta = 1000000000;
		double currentDelta;
		double step = (width / 10000000.0);
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
}
