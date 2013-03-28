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
	public static double round(double valueToRound, int numberOfDecimalPlaces)
	{
	    double multipicationFactor = Math.pow(10, numberOfDecimalPlaces);
	    double interestedInZeroDPs = valueToRound * multipicationFactor;
	    return Math.round(interestedInZeroDPs) / multipicationFactor;
	}
	
}
