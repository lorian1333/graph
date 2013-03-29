package lorian.graph.function;

import java.util.List;

import lorian.graph.GraphFunctionsFrame;

public class MathFunctions {
	public static double Calculate(String functionname, List<String> args, double value)
	{	
		Function f = new Function(); 
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
		else if(functionname.equalsIgnoreCase("tan"))
		{
			if(!f.Parse(args.get(0))) return 0;
			return Math.tan(f.Calc(value));
		}
		else if(functionname.equalsIgnoreCase("log"))
		{
			if(args.size()==1) //10log;
			{
				if(!f.Parse(args.get(0))) return 0;
				return Math.log10(f.Calc(value));
			}
			else //logBASE
			{
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
		else if(functionname.equalsIgnoreCase("const"))
		{
			String e = "" + (MathChars.e);
			if(args.get(0).equalsIgnoreCase("pi")) return Math.PI;
			else if(args.get(0).equalsIgnoreCase(e)) return Math.E;
		}
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
		else if(functionname.equalsIgnoreCase("root"))
		{
			Function g = new Function();
			if(!f.Parse(args.get(1)) || !g.Parse(args.get(0))) return 0;
			return Math.pow(f.Calc(value), 1 / g.Calc(value));
		}
		else if(functionname.equalsIgnoreCase("pow"))
		{
			Function g = new Function();
			if(!f.Parse(args.get(0)) || !g.Parse(args.get(1))) return 0;
			return Math.pow(f.Calc(value), g.Calc(value));
		}
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
		return 0;
	}
}
