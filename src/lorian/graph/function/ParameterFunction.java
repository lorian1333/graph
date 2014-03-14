package lorian.graph.function;

import java.awt.Color;

public class ParameterFunction {
	
	/* Batman in parametric functions:
	 * http://chrisbouchard.deviantart.com/art/TI-83-Graph-Batman-30736942
	 */
	
	private Function funcX, funcY;
	private char argumentChar;
	protected Color color;
	protected boolean draw = true;
	protected boolean parseOkX = true, parseOkY = true;
	protected double Tmin = 0, Tmax = 10;
	
	public ParameterFunction()
	{
		this('t');
	}
	public ParameterFunction(char argumentChar)
	{
		funcX = new Function(argumentChar);
		funcY = new Function(argumentChar);
		this.argumentChar = argumentChar;
	}
	public ParameterFunction(char argumentChar, String xtext, String ytext)
	{
		this(argumentChar);
		Parse(xtext, ytext);
	}
	public ParameterFunction(String xtext, String ytext)
	{
		this('t', xtext, ytext);
	}
	public boolean Parse(String xtext, String ytext)
	{
		parseOkX = funcX.Parse(xtext);
		parseOkY = funcY.Parse(ytext);
		return parseOkX && parseOkY;
	}
	public boolean ParseX(String s)
	{
		parseOkX = funcX.Parse(s);
		return parseOkX;
	}
	public boolean ParseY(String s)
	{
		parseOkY = funcY.Parse(s);
		return parseOkY;
	}
	public boolean parseError()
	{
		return !(parseOkX && parseOkY);
	}
	public PointXY Calc(double arg)
	{
		return new PointXY(funcX.Calc(arg), funcY.Calc(arg));
	} 
	public boolean drawOn() {
		return draw;
	}
	public void setDraw(boolean draw) {
		this.draw = draw;
	}
	public boolean isEmpty()
	{
		return funcX.isEmpty() && funcY.isEmpty();
	}
	public void setColor(Color color)
	{
		this.color = color;
	}
	public Color getColor()
	{
		return this.color;
	}
	
	public double getTmin() {
		return Tmin;
	}
	public void setTmin(double tmin) {
		Tmin = tmin;
	}
	public double getTmax() {
		return Tmax;
	}
	public void setTmax(double tmax) {
		Tmax = tmax;
	}
	public char getArgumentChar()
	{
		return argumentChar;
	}
	
	public String[] getInputStrings()
	{
		return new String[] { funcX.getInputString(), funcY.getInputString() };
	}
	
	@Override
	public String toString()
	{
		return this.getClass().getName() +  "[x=" + funcX.toString() +", y=" + funcY.toString() + "]";
	}
	
}
