package lorian.graph.function;

public class Factor {
	enum Type
	{
		CONSTANT, ARGUMENT, PARENTHESES, FUNCTION
	}
	Type type;
	
	private char argumentChar = 'x';
	private double value;
	
	private int index = 0;
	
	Function basefunc, exponentfunc;
	public Factor()
	{
		basefunc = new Function();
		exponentfunc = new Function();
	}
	public Factor(char argumentChar)
	{
		this.argumentChar = argumentChar;
		basefunc = new Function();
		exponentfunc = new Function();
	}
	public Factor(char argumentChar, String s)
	{
		this.argumentChar = argumentChar;
		basefunc = new Function();
		exponentfunc = new Function();
		Parse(s);
		
	}
	public Factor(String s)
	{
		basefunc = new Function();
		exponentfunc = new Function();
		Parse(s);
	}
	
	private double ParseConstantBetweenParentheses(String s)
	{
		Function tmpfunc = new Function();
		String forparse = "";
		boolean neg = false;
		while(s.charAt(index) == '+' || s.charAt(index) == '-')
		{
			if(s.charAt(index) == '-') neg = !neg;
			index++;
		}
		if(neg) forparse += '-';
		forparse += s.substring(index+1, s.length()-2);
		if(!tmpfunc.Parse(forparse)) return 0;
		double tmpvalue = tmpfunc.Calc(0);
		return tmpvalue;
		//TODO Make sure this works!
	}
	private boolean ParseConstant(String s)
	{
		if(!Util.StringContains(s, '^'))
		{
			if(Util.StringContains(s, '('))
			{
				value = ParseConstantBetweenParentheses(s);
				return true;
			}
			else
			{
				String forparse = "";
				boolean neg = false;
				while(s.charAt(index) == '+' || s.charAt(index) == '-')
				{
					if(s.charAt(index) == '-') neg = !neg;
					index++;
				}
				if(neg) forparse += '-';
				forparse += s.substring(index);
				value = Double.parseDouble(forparse);
				return true;
			}
		}
		
		String forparse = "";
		boolean neg = false;
		while(s.charAt(index) == '+' || s.charAt(index) == '-')
		{
			if(s.charAt(index) == '-') neg = !neg;
			index++;
		}
		if(neg) forparse += '-';
		forparse += s.substring(index, s.indexOf('^')); 
		value = Double.parseDouble(forparse);
		
		
		String exponentstr = s.substring(s.indexOf('^')+1);
		index = 0;
		double exponent;
		if(Util.StringContains(exponentstr, '('))
		{
			exponent = ParseConstantBetweenParentheses(exponentstr);
		}
		else
		{
			forparse = "";
			neg = false;
			
			while(exponentstr.charAt(index) == '+' || exponentstr.charAt(index) == '-')
			{
				if(exponentstr.charAt(index) == '-') neg = !neg;
				index++;
			}
			if(neg) forparse += '-';
			forparse += exponentstr.substring(index);
			exponent = Double.parseDouble(forparse);
		}
		value = Math.pow(value, exponent);
		System.out.println(value);
		return true;
	}
	private boolean ParseX(String s)
	{
		if(!Util.StringContains(s, '^'))
		{
			exponentfunc.Parse("1");
			//CHILLLAAAA!!!!!
			return true;
		} 
		String exponentstr = s.substring(s.indexOf('^')+1);
		return exponentfunc.Parse(exponentstr);
		//return true;
	}
	private boolean ParseParentheses(String s)
	{
		exponentfunc.Parse("1");
		return basefunc.Parse(s.substring(1, s.length() - 1));
		//return true;
	} 
	public boolean Parse(String s)
	{
		
		if(!Util.StringContains(s, argumentChar))
		{
			type = Type.CONSTANT;
			return ParseConstant(s);
		}
		else if(s.charAt(0) == argumentChar)
		{
			type = Type.ARGUMENT;
			return ParseX(s);
		}
		else if(s.charAt(s.length() - 1) == ')')
		{
			return ParseParentheses(s);
		}
		else
		{
			return false;
		}
		
			

	}
}

