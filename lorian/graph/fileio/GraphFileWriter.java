package lorian.graph.fileio;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import lorian.graph.GraphFunctionsFrame;
import lorian.graph.WindowSettings;
import lorian.graph.function.Factor;
import lorian.graph.function.Function;
import lorian.graph.function.Term;

public class GraphFileWriter {
	private final byte magic[] = {'L', 'G', 'F'};
	
	private String filename;
	private WindowSettings wsettings;
	private Function[] functions;
	
	private DataOutputStream ds;
	public GraphFileWriter(String filename)
	{
		this.filename = filename;
		this.functions = new Function[20];
		
	}
	
	public void setWindowSettings(WindowSettings wsettings)
	{
		this.wsettings = wsettings;
	}
	
	public void addFunction(Function f, int index)
	{
		functions[index] = f;
	}
	
	private void writeString(String s) throws IOException 
	{
		ds.writeInt(s.length()); 
		ds.writeChars(s);
	}
	private void writeFactor(Factor f) throws IOException
	{
		Factor.Type type = f.getType();
		ds.write(type.ordinal());
		
		switch(type)
		{
			case CONSTANT:
			{
				ds.writeDouble(f.Calc(0)); 
				break;
			}
			case ARGUMENT:
			{
				writeFunction(f.getExponentFunction());
				break;
			}
			case PARENTHESES:
			{
				ds.writeDouble(f.getValue());
				writeFunction(f.getBaseFunction());
				writeFunction(f.getExponentFunction());
				break;
			}
			case SPECIAL:
			{
				ds.writeDouble(f.getValue());
				writeFactor(f.getSpecialFac());
				break;
			}
			case FUNCTION:
			{
				writeString(f.getFunctionName());
				ds.writeInt(f.getFunctionArgs().size());
				if(f.getFunctionName().equalsIgnoreCase("const") || f.getFunctionName().equalsIgnoreCase("deriv") ||f.getFunctionName().equalsIgnoreCase("dydx"))
				{
					for(String arg: f.getFunctionArgs())
					{
						writeString(arg);
					}
				}
				else
				{
					for(String arg: f.getFunctionArgs())
					{
						writeFunction(new Function(arg));
					}
				}
				break;
			}
		}
	}
	private void writeTerm(Term t) throws IOException 
	{
		List<Factor> factors = t.getFactors(); 
		ds.writeInt(factors.size());
		for(Factor f: factors)
		{
			writeFactor(f);
		}
	}
	private void writeFunction(Function f) throws IOException
	{
		List<Term> terms = f.getTerms();
		ds.writeInt(terms.size());
		for(Term t: terms)
		{
			writeTerm(t);
		}
		
	}
	private void writeFunctionCount() throws IOException
	{
		short i = 0;
		for(Function f: functions)
		{
			if(f!=null) i++;
		}
		ds.writeShort(i);
	}
	public boolean write() throws IOException 
	{
		if(wsettings == null) return false;
		
		File tmpfile = new File(filename);
		if(tmpfile.exists())
		{
			tmpfile.delete();
		}
		ds = new DataOutputStream(new FileOutputStream(filename));
		
		
		ds.write(magic);
		ds.writeShort(GraphFunctionsFrame.major_version);
		ds.writeShort(GraphFunctionsFrame.minor_version);
		
		ds.writeLong(wsettings.getXmin());
		ds.writeLong(wsettings.getXmax());
		ds.writeLong(wsettings.getYmin());
		ds.writeLong(wsettings.getYmax());
		ds.writeBoolean(wsettings.gridOn());
		
		writeFunctionCount();
		short i=0;
		for(Function f: functions)
		{
			if(f==null)
			{
				i++;
				continue;
			}
				
			ds.writeShort(i);
			ds.writeBoolean(f.drawOn());

			ds.write(f.getColor().getRed());
			ds.write(f.getColor().getGreen());
			ds.write(f.getColor().getBlue());
			
			writeFunction(f);
			
			i++;
		}
		ds.close();
		return true;
	}
	
}
