package lorian.graph.function;

import java.util.ArrayList;
import java.util.List;

public class Term {
	private boolean parsed = false;
	private boolean coefficientnegative = false;
	private boolean exponentnegative = false;
	private boolean coefficienthasfloatpart = false;
	private boolean exponenthasfloatpart = false;
	private boolean isconstant = false;
	private char argumentChar = 'x';
	double coefficient = 0;
	double exponent = 0;
	
	int index = 0;
	
	List<Double> constants;
	
	public Term()
	{
		constants = new ArrayList<Double>();
	}
	public Term(char argumentChar)
	{
		this.argumentChar = argumentChar;
	}
	
	private boolean ParseConstant(String s)
	{
		int index = 0;
		if(s.charAt(index)=='-')
		{
			coefficientnegative = true;
			index++;
		}
		else if(s.charAt(index)=='+')
		{
			coefficientnegative = false;
			index++;
		}
		int i=1;
		for( ;index<s.length();index++)
		{
			if(!(s.charAt(index) >= '0' && s.charAt(index) <= '9'))
			{
				if(s.charAt(index)==',')
				{
					continue;
				}
				else if(s.charAt(index)=='.')
				{
					this.coefficienthasfloatpart = true;
					continue;
				}
				else
				{
					//System.err.printf("Error: Unknown char '%c' in term '%s'\n", s.charAt(index), s);
					return false;
				}
				
			}
			else
			{
				if(coefficienthasfloatpart)
				{
					coefficient += ((s.charAt(index) - '0')) / Math.pow(10, i++);
				}
				else
				{
					coefficient *= 10;
					coefficient += ((s.charAt(index) - '0'));
				}
			}
		}
		if(coefficientnegative) coefficient *= -1;
		
		/*
		if(coefficienthasfloatpart)
			System.out.printf("Constant: %f\n", coefficient);
		else
			System.out.printf("Constant: %d\n", (int) coefficient);
		*/
		parsed = true;
		return true;
	}
	private boolean ParseAxN(String s)
	{
		boolean skipNext = false;

		if(s.charAt(index)=='-')
		{
			coefficientnegative = true;
			index++;
		}
		else if(s.charAt(index)=='+')
		{
			coefficientnegative = false;
			index++;
		}
		
		if(!(s.charAt(index) >= '0' && s.charAt(index) <= '9'))
		{
			if(s.charAt(index) == argumentChar)
			{
				skipNext = true;
				coefficient = 1;
				index++;
				if(index < s.length())
				{
					if(s.charAt(index)!='^')
					{
						//System.err.printf("Error: Unknown char '%c' in term '%s'\n", s.charAt(index), s);
						return false;
					}
					index++;
				}
					
			}
			else
			{
				//System.err.printf("Error: Unknown char '%c' in term '%s'\n", s.charAt(index), s);
				return false;
			}
		}
		
		
	    double i=1;  
	    if(index == s.length()) skipNext = true;
		while(true)
		{
			if(skipNext) 
			{
				if(index < s.length()) skipNext = false;
				else exponent = 1;
				break;
			}
			
			if(!(s.charAt(index) >= '0' && s.charAt(index) <= '9'))
			{
				if(s.charAt(index)==argumentChar)
				{

					index++;
					
					if(s.length()==index)
					{						
						skipNext = true;
						exponent = 1;
						break;
						/*
						if(coefficienthasfloatpart)
							System.out.printf("Coefficient: %f\n", coefficient);
						else
							System.out.printf("Coefficient: %d\n", (int) coefficient);
						
						return true;
						*/
					}
					
					if(s.charAt(index)=='^')
					{
						index++;
						break;
					}
					else
					{
						//System.err.printf("Error: Unknown char '%c' in term '%s'\n", s.charAt(index), s);
						return false;
					}
				}
				else if(s.charAt(index)==',')
				{
					index++;
				}
				else if(s.charAt(index)=='.')
				{
					if(this.coefficienthasfloatpart)
					{
						//System.err.printf("Error: Unknown char '%c' in term '%s'\n", s.charAt(index), s);
						return false;
					}
					this.coefficienthasfloatpart = true;
					index++;
				}
				else 
				{
					//System.err.printf("Error: Unknown char '%c' in term '%s'\n", s.charAt(index), s);
					return false;
				}
			}
			else
			{
				if(coefficienthasfloatpart)
				{
					coefficient += ((s.charAt(index) - '0')) / Math.pow(10, i++);
				}
				else
				{
					coefficient *= 10;
					coefficient += ((s.charAt(index) - '0'));
				}
				index++;
			}

		}
	
		if(index < s.length())
		{
			if(s.charAt(index)=='-')
			{
				exponentnegative = true;
				index++;
			}
			else if(s.charAt(index)=='+')
			{
				exponentnegative = false;
				index++;
			}
		}
		i=1;
		while(index < s.length())
		{
			if(skipNext) 
			{
				skipNext = false;
				break;
			}
			
			if(!(s.charAt(index) >= '0' && s.charAt(index) <= '9'))
			{
				if(s.charAt(index)==',')
				{
					index++;
				}
				else if(s.charAt(index)=='.')
				{
					if(this.exponenthasfloatpart)
					{
						//System.err.printf("Error: Unknown char '%c' in term '%s'\n", s.charAt(index), s);
						return false;
					}
					this.exponenthasfloatpart = true;
					index++;
				}
				else 
				{
					//System.err.printf("Error: Unknown char '%c' in term '%s'\n", s.charAt(index), s);
					return false;
				}
			}
			else
			{
				if(exponenthasfloatpart)
				{
					exponent += ((s.charAt(index) - '0')) / Math.pow(10, i++);
				}
				else
				{
					exponent *= 10;
					exponent += ((s.charAt(index) - '0'));
				}
				index++;
			}
			
		}
		if(coefficientnegative) coefficient *= -1;
		if(exponentnegative) exponent *= -1;
		
		/*
		if(coefficienthasfloatpart)
			System.out.printf("Coefficient: %f, ", coefficient);
		else
			System.out.printf("Coefficient: %d, ", (int) coefficient);

		if(exponenthasfloatpart)
			System.out.printf("Exponent: %f\n", exponent);
		else
			System.out.printf("Exponent: %d\n", (int) exponent);
			*/
		
	
		parsed = true;
		return true;
	}
	private boolean ContainsFunction(String s)
	{
		s = Util.removeWhiteSpace(s.trim());
		for(int i=0;i<s.length(); i++)
		{
			char ch = s.charAt(i);
			if(ch < '0' && ch > '9' && ch != this.argumentChar)
			{
				
			}
		}
		return false;
	}
	public boolean ParseTerm(String s)
	{
		int constantstart=-1;
		int state = 0; //0 = constant, 1 = x, 2 = function
		double constant;
		String tmp;
		boolean neg=false;
		char ch;
		while(index<s.length())
		{
			ch = s.charAt(index);
			if(state==0)
			{
				if((ch >= '0' && ch <= '9') || ch == '.')
				{
					if(constantstart == -1)
					{
						constantstart = index;
					}
				}
				else if(ch == '-')
				{
					neg = !neg;
				}
				else if(ch == '+')
				{
					
				}
				else if(ch == '*')
				{
					tmp = s.substring(constantstart, index-1);
					constant = Double.parseDouble(tmp);
				}
				else
				{
					
				}
			}
			index++;
		}
		return true;
	}
	public boolean Parse(String s)
	{
		s = Util.removeWhiteSpace(s).toLowerCase();
		coefficientnegative = false;
		exponentnegative = false;
		coefficienthasfloatpart = false;
		exponenthasfloatpart = false;
		isconstant = false;
		coefficient = 0;
		exponent = 0;
		

		if(!Util.StringContains(s, argumentChar))
		{
			isconstant = true;
			exponent = 0;
			return ParseConstant(s);
		}
		
		return ParseAxN(s);
		//return ParseTerm(s);
		
	}
	public double Calc(double arg)
	{
		if(!parsed) return 0;
		return (coefficient * Math.pow(arg, exponent));
	}
	@Override
	public String toString()
	{
		if(!parsed) return super.toString();
		
		if(isconstant)
		{
			return String.valueOf(coefficient);
		}
		else
		{
			String s = "";
			if(coefficient!=0)
			{
				if(coefficienthasfloatpart)
					s += coefficient;
				else s += (int) coefficient;
			}
			s += argumentChar;
			if(exponent != 1)
			{
				s += "^";
				if(exponenthasfloatpart)
					s += exponent;
				else 
					s += (int) exponent;
			}
			return s;
		}
	}
}
