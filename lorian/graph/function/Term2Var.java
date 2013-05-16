package lorian.graph.function;

import java.util.ArrayList;
import java.util.List;

public class Term2Var extends Term {

	protected char argumentChar2 = 'y';
	protected List<Factor2Var> factors;
	
	public Term2Var()
	{
		factors = new ArrayList<Factor2Var>();
	}
	
	public Term2Var(char argumentChar,  char argumentChar2) {
		this.argumentChar = argumentChar;
		this.argumentChar2 = argumentChar2;
		factors = new ArrayList<Factor2Var>();
	}
	
	@Override
	protected List<String> SplitIntoFactors(String s)
	{
		List<String> factors = new ArrayList<String>();
		char ch;
		String newfactor = "";
		boolean ignoreminplus = true;
		boolean inexponent = false;
		boolean division = false;
		while(index < s.length())
		{
			ch = s.charAt(index); 

				if(!((ch >= '0' && ch <= '9') || ch == '.'))
				{
					if(ch=='*' || ch == '/')
					{
						if(division) newfactor = exponentTimesMinusOne(newfactor);
						if(newfactor.length() > 0) factors.add(newfactor);
						newfactor = "";
						ignoreminplus = true;
						if(ch == '/') division = true;
						else division = false;
					}
					
					else if(ch==argumentChar || ch==argumentChar2)
					{
						if(inexponent)
						{
							newfactor += ch;
						}
						else if(newfactor.length() > 0)
						{
							if(!ignoreminplus)
							{
								if(division) newfactor = exponentTimesMinusOne(newfactor);
								if(newfactor.length() > 0) factors.add(newfactor);
								newfactor = "";
								division = false;
								ignoreminplus = true;
								inexponent = false;
								continue;
							}
							else
							{
								boolean neg = false;
								int i=0;
								String tmp = "";
								while(i < newfactor.length())
								{
									if(newfactor.charAt(i) == '-') neg = !neg;
									i++;
								}
								if(neg) tmp += "-";
								tmp += "1";
								if(division) tmp = exponentTimesMinusOne(tmp);
								factors.add(tmp);
								newfactor = "";
								division = false;
								ignoreminplus = true;
								continue;
							}
						}
						
						else
						{
							newfactor += ch;
							if(index+1 < s.length())
							{
								if(s.charAt(index+1) != '^')
								{
									if(division) newfactor = exponentTimesMinusOne(newfactor);
									if(newfactor.length() > 0) factors.add(newfactor);
									division = false;
									newfactor = "";
									ignoreminplus = true;
								}
								
							}
							
							
							
						}
					}
					
					
					else if(ch=='+' || ch=='-')
					{
						if(!ignoreminplus)
						{
							if(division) newfactor = exponentTimesMinusOne(newfactor);
							if(newfactor.length() > 0) factors.add(newfactor);
							division = false;
							newfactor = "";
							ignoreminplus = true;
							inexponent = false;
							continue;
						}
						else newfactor += ch;
					}
					else if(ch == '(')
					{
						if(!Util.StringContains(newfactor, Util.LowercaseAlphabethWithout(argumentChar, argumentChar2)) && !Util.StringContains(newfactor, "^"))
						{
							if(newfactor.length() > 0) 
							{
								if(division) newfactor = exponentTimesMinusOne(newfactor);
								factors.add(newfactor);
								division = false;
								newfactor = "";
							}
						}
						newfactor += GetEverythingBetweenParentheses(s);
						if(index+1 < s.length())
						{
							if(s.charAt(index+1) == '^')
							{
								index++;
								continue;
							}
						}
						if(division) newfactor = exponentTimesMinusOne(newfactor);
						if(newfactor.length() > 0) factors.add(newfactor);
						division = false;
						newfactor = "";
						ignoreminplus = true;
						inexponent = false;
						
					}
					else if(ch == '^')
					{
						ignoreminplus = true;
						inexponent = true;
						newfactor += ch;
						index++;
						continue;
					}

					else if(Util.StringContains("" + ch, Util.LowercaseAlphabethWithout(argumentChar, argumentChar2) + "()+-^"))
					{
						if(division) newfactor = exponentTimesMinusOne(newfactor);
						if(newfactor.length() > 0) factors.add(newfactor);
						int tmpindex = s.indexOf('(', index);
						newfactor = s.substring(index, tmpindex);
						index = tmpindex;
						newfactor += GetEverythingBetweenParentheses(s);
						if(index+1 < s.length())
						{
							if(s.charAt(index+1) == '^')
							{
								index++;
								continue;
							}
						}
						if(division) newfactor = exponentTimesMinusOne(newfactor);
						if(newfactor.length() > 0) factors.add(newfactor);
						division = false;
						newfactor = "";
						ignoreminplus = true;
						inexponent = false;
						
					}
					
					else
						newfactor += ch;
				}
				else
				{
					newfactor += ch;
				}
				if(ignoreminplus && newfactor.length() > 0 && !(ch == '+' || ch == '-')) ignoreminplus = false;
			
			index++;
		}
		if(division) newfactor = exponentTimesMinusOne(newfactor);
		if(newfactor.length() > 0) factors.add(newfactor);
		return factors;
	}
	
	@Override
	public boolean Parse(String s)
	{
		s = Util.removeWhiteSpace(s).toLowerCase();
		boolean result = true;
		List<String> factorstrs = SplitIntoFactors(s);
		for(String factorstr: factorstrs)
		{
			Factor2Var factor = new Factor2Var(this.argumentChar, this.argumentChar2);
			if(!factor.Parse(factorstr)) { 
				result = false;
				factors.add(null);
			}
			else
				factors.add(factor);
		}
		parsed = result;
		return result;
	}
	
	@Override 
	public double Calc(double arg)
	{
		return Calc(arg, 1);
	}
	
	public double Calc(double arg1, double arg2)
	{
		if(!parsed) return 0;
		double product = 1;
		for(Factor2Var fac: factors)
		{
			product *= fac.Calc(arg1, arg2);
		}
		return product;
		
	}
	

	public List<Factor2Var> getFactors2Var()
	{
		return this.factors;
	}
}
