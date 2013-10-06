package lorian.graph.function;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Function2Var extends Function {
	
	protected char argumentChar2 = 'y';
	protected List<Term2Var> terms;
	
	public Function2Var()
	{
		terms = new ArrayList<Term2Var>();
		setColor(Color.BLACK);
		variables = new ArrayList<Variable>();
	}
	public Function2Var(char argumentChar, char argumentChar2)
	{
		this.argumentChar = ("" + argumentChar).toLowerCase().charAt(0);
		this.argumentChar2 =  ("" + argumentChar2).toLowerCase().charAt(0);
		terms = new ArrayList<Term2Var>();
		variables = new ArrayList<Variable>();
		setColor(Color.BLACK);
		
	}
	public Function2Var(char argumentChar, char argumentChar2, String s)
	{
		this.argumentChar = ("" + argumentChar).toLowerCase().charAt(0);
		this.argumentChar2 =  ("" + argumentChar2).toLowerCase().charAt(0);
		terms = new ArrayList<Term2Var>();
		variables = new ArrayList<Variable>();
		setColor(Color.BLACK);
		Parse(s);
	}
	public Function2Var(String s)
	{
		terms = new ArrayList<Term2Var>();
		variables = new ArrayList<Variable>();
		setColor(Color.BLACK);
		Parse(s);
	}
	
	public boolean Parse(String s)
	{
		try
		{
		RawInputString = s;
		s = Util.removeWhiteSpace(s);
		s = FillInVariables(s); 
		s = s.toLowerCase();
		s = PreProcess(s);
		
		if(!checkForUnclosedParentheses(s)) return false;
		
		String termstr = "";
		
		int start=0,index=0;
		
		if(s.charAt(0)=='-' || s.charAt(0)=='+')
			index++;
		
		while(index < s.length())
		{
			Term2Var term = new Term2Var(this.argumentChar, this.argumentChar2);  
			int funcdepth=0;
			boolean ignoreminplus = true;
			while(true)
			{
				if(index == s.length())
				{
					break;
				}
				else if(s.charAt(index)=='-' || s.charAt(index)=='+')
				{
					if(funcdepth==0 && !ignoreminplus) break;
				}
				else if(s.charAt(index)=='*' || s.charAt(index)=='^' || s.charAt(index) == ')')
				{
					ignoreminplus = true;
				}
				else ignoreminplus = false;
				
				if(s.charAt(index)=='(')
				{
					funcdepth++;
				}
				else if(s.charAt(index)==')')
				{
					if(funcdepth==0)
					{
						return false;
					}
					else {
						funcdepth--;
						ignoreminplus = false;
					}
				}
				index++;
			}
			
			
			termstr = s.substring(start, index);
			//System.out.println(termstr);
			
			if(!term.Parse(termstr)) 
			{
				clear();
				return false;
			}
			terms.add(term);
			
			//System.out.printf("Term: %s\n", term.toString());
			
			start = index;
			index++;
		}
		isEmpty = false;
		return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	@Override 
	public double Calc(double arg)
	{
		return Calc(arg, 1);
	}
	public double Calc(double arg1, double arg2)
	{
		if(isEmpty) return 0;
		try
		{
			double sum = 0;
			for(Term2Var term: terms)
			{
				sum += term.Calc(arg1, arg2);
			}
			return sum;
		}
		catch (Exception e)
		{
			return 0;
		}
	}
}
