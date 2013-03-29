package lorian.graph.function;

public class Util {
	public static String removeWhiteSpace(String s)
	{
		s = s.trim();
		String ss = ""; 
		for(int i=0;i<s.length();i++)
		{
			if(s.charAt(i) != ' ' && s.charAt(i) != '\t' && s.charAt(i) != '\n')
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
	public static boolean StringContains(String s, String chars)
	{
		for(int i=0;i<chars.length();i++)
		{
			if(StringContains(s, chars.charAt(i))) return true;
		}
		return false;
	}
	public static String LowercaseAlphabethWithout(char character)
	{
		String s = "";
		for(char ch = 'a'; ch <= 'z'; ch++)
		{
			if(ch != character) s += ch;
		}
		return s;
	}
	public static int StringIndexNotOf(String s, String chars)
	{
		int i;
		char ch;
		for(i=0;i<s.length();i++)
		{
			ch = s.charAt(i);
			if(!StringContains(chars, ch))
			{
				return i;
			}
		}
		return -1;
	}
	public static double round(double valueToRound, int numberOfDecimalPlaces)
	{
	    double multipicationFactor = Math.pow(10, numberOfDecimalPlaces);
	    double interestedInZeroDPs = valueToRound * multipicationFactor;
	    return Math.round(interestedInZeroDPs) / multipicationFactor;
	}

}
