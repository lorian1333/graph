package lorian.graph.function;



public class Factor2Var extends Factor {

	public enum Type
	{
		CONSTANT, ARGUMENT1, ARGUMENT2, PARENTHESES, FUNCTION
	}
	
	protected Type type;
	protected char argumentChar2 = 'y';
	protected Function2Var basefunc, exponentfunc;
	
	public Factor2Var(char argumentChar, char argumentChar2) {
		this.argumentChar = argumentChar;
		this.argumentChar2 = argumentChar2;
		basefunc = new Function2Var(argumentChar, argumentChar2);
		exponentfunc = new Function2Var(argumentChar, argumentChar2);
	}

	public Factor2Var()
	{
		basefunc = new Function2Var(argumentChar, argumentChar2);
		exponentfunc = new Function2Var(argumentChar, argumentChar2);
	}
	public Factor2Var(char argumentChar, char argumentChar2, String s)
	{
		this.argumentChar = argumentChar;
		this.argumentChar2 = argumentChar2;
		basefunc = new Function2Var(argumentChar, argumentChar2);
		exponentfunc = new Function2Var(argumentChar, argumentChar2);
		Parse(s);
		
	}
	public Factor2Var(String s)
	{
		basefunc = new Function2Var(argumentChar, argumentChar2);
		exponentfunc = new Function2Var(argumentChar, argumentChar2);
		Parse(s);
	}
	
	@Override
	protected boolean ParseParentheses(String s)
	{
		String basestr = ParseParanthesesBase(s); 
		if(index == s.length()-1)
		{
			exponentfunc.Parse("1");
			return basefunc.Parse(basestr);
		}
		String exponentstr = ParseParanthesesExponent(s);
		if(!basefunc.Parse(basestr) || !exponentfunc.Parse(exponentstr)) 
		{
			return false;
		}
		else return true;
	}
	
	@Override
	protected boolean ParseFunction(String s) 
	{
		functionname = s.substring(0, s.indexOf('(')); 
		if(!MathFunctions.functionExistsFor2Var(functionname)) return false;
		String functionargsstr = s.substring(s.indexOf('(') + 1, s.length()-1);
		functionargs = SplitArgs(functionargsstr);
		
		
		return true;
	}
	
	@Override
	protected boolean ParseExponentX(String s)
	{
		int tmpindex = getFirstPower(s);
		String basestr, exponentstr;
		if(tmpindex == -1)
		{
			basestr = s;
			exponentstr = "1";
		}
		else
		{
			basestr = s.substring(0, tmpindex);
			exponentstr = s.substring(tmpindex + 1);
		}

		if(!basefunc.Parse(basestr) || !exponentfunc.Parse(exponentstr)) 
		{
			return false;
		}
		else return true;
	}
	
	@Override
	protected boolean ParseX(String s) 
	{
		if(!Util.StringContains(s, '^'))
		{
			value = 1;
			return true;
		} 
		value = 0;
		String exponentstr = s.substring(s.indexOf('^')+1);
		return exponentfunc.Parse(exponentstr);
	}
	

	
	@Override
	public boolean Parse(String s)
	{
		if(s.charAt(0) == argumentChar)
		{
			type = Type.ARGUMENT1;
			parsed = ParseX(s);
			return parsed;				
		}
		else if(s.charAt(0) == argumentChar2)
		{
			type = Type.ARGUMENT2;
			parsed = ParseX(s);
			return parsed;				
		}
		else if(s.charAt(s.length() - 1) == ')')
		{
			if(s.charAt(0) == '(')
			{
				type = Type.PARENTHESES;
				value = 1;
				parsed = ParseParentheses(s);
			}
			else if(Util.StringContains(s, Util.LowercaseAlphabethWithout(argumentChar, argumentChar2)))
			{
				type = Type.FUNCTION; 
				parsed = ParseFunction(s);
			}
			else
			{
				parsed = ParseOther(s);
			}
			return parsed;
		}
		else if(s.charAt(0) == '(')
		{
			type = Type.PARENTHESES;
			value = 1;
			parsed =  ParseParentheses(s);
			return parsed;
		}
		else if(!Util.StringContains(s, "abcdefghijklmnopqrstuvwxyz")) 
		{
			type = Type.CONSTANT;
			parsed = ParseConstant(s);
			return parsed;
		}
		else
		{
			parsed = ParseOther(s);
			return parsed;
		}
		
	}
	
	@Override 
	public double Calc(double arg)
	{
		return Calc(arg, 1);
	}
	
	public double Calc(double arg1, double arg2)
	{
		if(!parsed )return Double.NaN;
		
		if(type == Type.CONSTANT)
		{
			return value;
		}
		else if(type == Type.ARGUMENT1)
		{
			if(value == 0)
				return Math.pow(arg1, exponentfunc.Calc(arg1));
			else return arg1;
		}
		else if(type == Type.ARGUMENT2)
		{
			if(value == 0)
				return Math.pow(arg2, exponentfunc.Calc(arg2));
			else return arg2;
		}
		else if(type == Type.PARENTHESES)
		{
			return value * Math.pow(basefunc.Calc(arg1, arg2), exponentfunc.Calc(arg1, arg2));
		}
		
		// TODO Add function support
		
		else if(type == Type.FUNCTION)
		{
			return MathFunctions.Calculate2Var(functionname, functionargs, arg1, arg2, argumentChar, argumentChar2);
		}
		
		else return 0;
	}
	
	public Type getType2Var() 
	{
		return this.type;
	}
	
	public Function2Var getBaseFunction()
	{
		return this.basefunc;
	}
	public Function2Var getExponentFunction() 
	{
		return this.exponentfunc;
	}
}
