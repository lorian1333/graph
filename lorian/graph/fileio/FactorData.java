package lorian.graph.fileio;

import lorian.graph.function.Factor;

public class FactorData {
	public Factor.Type type;
	
	public double value;
	public FunctionData basefunc;
	public FunctionData exponentfunc;
	
	public FactorData specialfac; // type = SPECIAL
	
	public String functionname; // type = FUNCTION
	public int functionargscount;
	public String[] functionargs_str;
	public FunctionData[] functionargs;
}
