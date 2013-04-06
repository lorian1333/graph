package lorian.graph.function;

import java.util.ArrayList;
import java.util.List;

import lorian.graph.GraphFunctionsFrame;

public class MathFunctions {

	public static boolean functionExists(String functionname)
	{
		// Exceptions where the test below does not apply
		if(functionname.equalsIgnoreCase("log") || functionname.equalsIgnoreCase("const")) return true;
		
		List<String> args = new ArrayList<String>();
		args.add("1");
		args.add("1");
		
		if(Double.isNaN(Calculate(functionname, args, 1))) return false;
		else return true;
	}
	public static double Calculate(String functionname, List<String> args, double value)
	{	
		Function f = new Function(); 
		if(args.size()<1) return 0;
		
		//Goniometric functions
		if(functionname.equalsIgnoreCase("sin"))
		{
			if(!f.Parse(args.get(0))) return 0;
			return Math.sin(f.Calc(value));
		}
		else if(functionname.equalsIgnoreCase("cos"))
		{
			if(!f.Parse(args.get(0))) return 0;
			return Math.cos(f.Calc(value));
		}
		else if(functionname.equalsIgnoreCase("tan") || functionname.equalsIgnoreCase("tg"))
		{
			if(!f.Parse(args.get(0))) return 0;
			return Math.tan(f.Calc(value));
		}
		else if(functionname.equalsIgnoreCase("sec"))
		{
			if(!f.Parse(args.get(0))) return 0;
			return 1 / Math.cos(f.Calc(value));
		}
		else if(functionname.equalsIgnoreCase("csc") || functionname.equalsIgnoreCase("cosec"))
		{
			if(!f.Parse(args.get(0))) return 0;
			return 1 / Math.sin(f.Calc(value));
		}
		else if(functionname.equalsIgnoreCase("cot"))
		{
			if(!f.Parse(args.get(0))) return 0;
			return 1 / Math.tan(f.Calc(value));
		}
		
		// Cyclometric functions
		else if(functionname.equalsIgnoreCase("asin") || functionname.equalsIgnoreCase("arcsin") || functionname.equalsIgnoreCase("bgsin"))
		{
			if(!f.Parse(args.get(0))) return 0;
			return Math.asin(f.Calc(value));
		}
		else if(functionname.equalsIgnoreCase("acos") || functionname.equalsIgnoreCase("arccos") || functionname.equalsIgnoreCase("bgcos"))
		{
			if(!f.Parse(args.get(0))) return 0;
			return Math.acos(f.Calc(value));
		}
		else if(functionname.equalsIgnoreCase("atan") || functionname.equalsIgnoreCase("arctan") || functionname.equalsIgnoreCase("bgtan"))
		{
			if(!f.Parse(args.get(0))) return 0;
			return Math.atan(f.Calc(value));
		}
		else if(functionname.equalsIgnoreCase("acot") || functionname.equalsIgnoreCase("arccot") || functionname.equalsIgnoreCase("bgcot"))
		{
			if(!f.Parse(args.get(0))) return 0;
			return (Math.PI / 2) - Math.atan(f.Calc(value));
		}
		else if(functionname.equalsIgnoreCase("asec") || functionname.equalsIgnoreCase("arcsec") || functionname.equalsIgnoreCase("bgsec"))
		{
			if(!f.Parse(args.get(0))) return 0;
			return Math.acos(1 / f.Calc(value));
		}
		else if(functionname.equalsIgnoreCase("acsc") || functionname.equalsIgnoreCase("arccsc") || functionname.equalsIgnoreCase("bgcsc"))
		{
			if(!f.Parse(args.get(0))) return 0;
			return (Math.PI / 2) - Math.acos(1 / f.Calc(value));
		}
		
		// Hyperbolic functions
		
		else if(functionname.equalsIgnoreCase("sinh"))
		{
			if(!f.Parse(args.get(0))) return 0;
			return Math.sinh(f.Calc(value)); 
		}
		else if(functionname.equalsIgnoreCase("cosh"))
		{
			if(!f.Parse(args.get(0))) return 0;
			return Math.cosh(f.Calc(value)); 
		}
		else if(functionname.equalsIgnoreCase("tanh"))
		{
			if(!f.Parse(args.get(0))) return 0;
			return Math.tanh(f.Calc(value)); 
		}
		else if(functionname.equalsIgnoreCase("coth"))
		{
			if(!f.Parse(args.get(0))) return 0;
			return Math.cosh(f.Calc(value)) / Math.sinh(f.Calc(value));
		}
		else if(functionname.equalsIgnoreCase("sech"))
		{
			if(!f.Parse(args.get(0))) return 0;
			return 1 / Math.cosh(f.Calc(value));
		}
		else if(functionname.equalsIgnoreCase("csch"))
		{
			if(!f.Parse(args.get(0))) return 0;
			return 1 / Math.sinh(f.Calc(value));
		}
		
		// Logaritms
		else if(functionname.equalsIgnoreCase("log"))
		{
			if(args.size()==1) //10log;
			{
				if(!f.Parse(args.get(0))) return 0;
				return Math.log10(f.Calc(value));
			}
			else //logBASE
			{
				if(args.size()<2) return 0;
				Function g = new Function();
				if(!f.Parse(args.get(1)) || !g.Parse(args.get(0)))
					return 0;
				return (Math.log10(f.Calc(value)) / Math.log10(g.Calc(value)));
			}
		}
		else if(functionname.equalsIgnoreCase("ln"))
		{
			if(!f.Parse(args.get(0))) return 0;
			return Math.log(f.Calc(value));
		}
		
		// Constants
		else if(functionname.equalsIgnoreCase("const"))
		{
			String e = "" + (MathChars.e);
			if(args.get(0).equalsIgnoreCase("pi")) return Math.PI;
			else if(args.get(0).equalsIgnoreCase(e)) return Math.E;
		}
		
		// Roots
		else if(functionname.equalsIgnoreCase("sqrt"))
		{
			if(!f.Parse(args.get(0))) return 0;
			return Math.sqrt(f.Calc(value));
		}
		else if(functionname.equalsIgnoreCase("cbrt"))
		{
			if(!f.Parse(args.get(0))) return 0;
			return Math.cbrt(f.Calc(value));
		}
		else if(functionname.equalsIgnoreCase("4rt"))
		{
			if(!f.Parse(args.get(0))) return 0;
			return Math.pow(f.Calc(value), 1/4);
		}
		else if(functionname.equalsIgnoreCase("root"))
		{
			if(args.size()<2) return 0;
			Function g = new Function();
			if(!f.Parse(args.get(1)) || !g.Parse(args.get(0))) return 0;
			return Math.pow(f.Calc(value), 1 / g.Calc(value));
		}
		
		// Power
		else if(functionname.equalsIgnoreCase("pow"))
		{
			if(args.size()<2) return 0;
			Function g = new Function();
			if(!f.Parse(args.get(0)) || !g.Parse(args.get(1))) return 0;
			return Math.pow(f.Calc(value), g.Calc(value));
		}
		
		// Absolute value
		else if(functionname.equalsIgnoreCase("abs"))
		{
			if(!f.Parse(args.get(0))) return 0;
			return Math.abs(f.Calc(value));
		}
		
		// Other functions
		else if(functionname.charAt(0) == 'y')
		{
			int funcindex;
			try
			{
				funcindex = Integer.parseInt(functionname.substring(1)) - 1;
			}
			catch (NumberFormatException e)
			{
				return 0;
			}
			if(!f.Parse(args.get(0))) return 0;
			if(GraphFunctionsFrame.functions.size() <= funcindex) return 0;
			return GraphFunctionsFrame.functions.get(funcindex).Calc(f.Calc(value)); 
		}
		
		// Derivative
		else if(functionname.equalsIgnoreCase("dydx") || functionname.equalsIgnoreCase("deriv"))
		{
			String otherfunction = args.get(0);
			//if(otherfunction.contains("dydx")|| otherfunction.contains("deriv")) return Double.NaN;
			
			if(otherfunction.charAt(0) != 'y' || otherfunction.endsWith(")")) 
			{
				if(!f.Parse(otherfunction)) return 0;
				return Calculate.DyDx(f, value);
			}
			int funcindex;
			try
			{
				funcindex = Integer.parseInt(otherfunction.substring(1)) - 1;
			}
			catch (NumberFormatException e)
			{
				return 0;
			}
			if(GraphFunctionsFrame.functions.size() <= funcindex) return 0;
			
			
			return Calculate.DyDx(GraphFunctionsFrame.functions.get(funcindex), value);
		}
		
		return Double.NaN;
	}
}
