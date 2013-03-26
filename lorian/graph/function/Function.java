package lorian.graph.function;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Function {
	private char argumentChar = 'x';
	private List<Term> terms;
	private Color color;
	private boolean isEmpty = true;
	
	public Function()
	{
		terms = new ArrayList<Term>();
		setColor(Color.BLACK);
	}
	public Function(char argumentChar)
	{
		String s = ""; 
		s += argumentChar;
		this.argumentChar = s.toLowerCase().charAt(0);
		terms = new ArrayList<Term>();
		setColor(Color.BLACK);
	}
	
	public boolean Parse(String s)
	{
		s = Util.removeWhiteSpace(s).toLowerCase();
		for(int i=0;i<s.length();i++)
		{
			int ch = (int) s.charAt(i);
			//System.out.println(ch);
		}
		String termstr = "";
		
		
		int start=0,index=0;
		
		if(s.charAt(0)=='-' || s.charAt(0)=='+')
			index++;
		
		while(index < s.length())
		{
			Term term = new Term(this.argumentChar);
			int funcdepth=0;
			boolean ignoreminplus = true;
			//while(!(s.charAt(index)=='-' || s.charAt(index)=='+') && funcdepth == 0 && onlyplusminus == true)
			while(true)
			{
				index++;
				if(index == s.length())
				{
					break;
				}
				else if(s.charAt(index)=='-' || s.charAt(index)=='+')
				{
					if(funcdepth==0 && !ignoreminplus) break;
				}
				else if(s.charAt(index)=='*' || s.charAt(index)=='^')
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
					else funcdepth--;
				}
			}
			
			
			termstr = s.substring(start, index);
			System.out.println(termstr);
			
			if(!term.Parse(termstr)) 
			{
				clear();
				//return false;
			}
			terms.add(term);
			
			//System.out.printf("Term: %s\n", term.toString());
			
			start = index;
			index++;
		}
		isEmpty = false;
		return true;
	}
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	public boolean isEmpty()
	{
		return this.isEmpty;
	}
	public void clear()
	{
		setColor(Color.BLACK);
		terms.clear();
		isEmpty = true;
	}
	public double Calc(double arg)
	{
		if(isEmpty) return 0;
		
		double sum = 0;
		for(int i = 0; i < terms.size(); i++)
		{
			sum += terms.get(i).Calc(arg);
		}
		return sum;
	}
}
