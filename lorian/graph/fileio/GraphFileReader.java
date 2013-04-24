package lorian.graph.fileio;

import java.awt.Canvas;
import java.awt.Color;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import javax.print.CancelablePrintJob;

import lorian.graph.GraphFunctionsFrame;
import lorian.graph.WindowSettings;
import lorian.graph.function.Factor;
import lorian.graph.function.Function;


public class GraphFileReader {
	private final char argumentChar = 'x';
	private final byte magic[] = {'L', 'G', 'F'};
	private boolean read = false;
	private DataInputStream ds;
	private String filename;
	
	private short file_major_version, file_minor_version;
	private WindowSettings wsettings;
	private short functionCount;
	private FunctionData[] functiondata;
	
	public GraphFileReader(String filename)
	{
		this.filename = filename;
		wsettings = new WindowSettings();
		
	}
	
	private boolean readWindowSettings() throws IOException 
	{
		wsettings.setXmin(ds.readLong()); 
		wsettings.setXmax(ds.readLong());
		wsettings.setYmin(ds.readLong());
		wsettings.setYmax(ds.readLong());
		wsettings.setGrid(ds.readBoolean());
		
		return true;
	}
	private String readString() throws IOException 
	{
		int size = ds.readInt();
		String s = "";
		for(int i=0;i<size;i++)
		{
			s += ds.readChar();
		}
		return s;
	}
	private FactorData readFactor() throws IOException
	{
		FactorData factor = new FactorData();
		factor.type = Factor.Type.values()[ds.readByte()];
		switch(factor.type)
		{
			case CONSTANT:
			{
				factor.value = ds.readDouble();
				break;
			}
			case ARGUMENT:
			{
				factor.exponentfunc = readFunction(false);
				break;
			}
			case PARENTHESES:
			{
				factor.value = ds.readDouble();
				factor.basefunc = readFunction(false);
				factor.exponentfunc = readFunction(false);
				break;
			}
			case FUNCTION:
			{
				factor.functionname = readString();
				factor.functionargscount = ds.readInt();

				if(factor.functionname.equalsIgnoreCase("const") || factor.functionname.equalsIgnoreCase("dydx")|| factor.functionname.equalsIgnoreCase("deriv"))
				{
					factor.functionargs_str = new String[factor.functionargscount];
					for(int i=0;i<factor.functionargscount;i++)
					{
						factor.functionargs_str[i] = readString();
					}
				}
				else
				{
					factor.functionargs = new FunctionData[factor.functionargscount];
					for(int i=0;i<factor.functionargscount;i++)
					{
						factor.functionargs[i] = readFunction(false);
					}
				}
				break;
			}
		}
		return factor;
	}
	private TermData readTerm() throws IOException
	{
		TermData term = new TermData();
		term.factorscount = ds.readInt();
		term.factors = new FactorData[term.factorscount];
		for(int i=0;i<term.factorscount;i++)
		{
			term.factors[i] = readFactor();
		}
		return term;
	} 
	private FunctionData readFunction(boolean readExtra) throws IOException
	{
		FunctionData fd = new FunctionData();
		if(readExtra)
		{
			fd.index = ds.readShort();
			fd.draw  = ds.readBoolean();
			
			int r = ds.read();
			int g = ds.read();
			int b = ds.read();
			fd.color = new Color(r, g, b);
		}
		fd.termscount = ds.readInt();
		fd.terms = new TermData[fd.termscount];
		for(int i=0;i<fd.termscount;i++)
		{
			fd.terms[i] = readTerm();
		}
		return fd;
	}
	private boolean readFunctions() throws IOException
	{
		functionCount = ds.readShort();
		functiondata = new FunctionData[functionCount];
		
		for(short i=0;i<functionCount;i++)
		{
			functiondata[i] = readFunction(true);
		}
		return true;
	}

	public boolean read() throws IOException
	{
		ds = new DataInputStream(new FileInputStream(filename));  
		byte[] readmagic = new byte[3];
		if(ds.read(readmagic)!=3)
		{
			System.out.println("Invalid magic!");
			ds.close();
			return false;
		}
		if(readmagic[0] != magic[0] || readmagic[1] != magic[1] || readmagic[2] != magic[2])
		{
			System.out.println("Invalid magic!");
			ds.close();
			return false;
		}
		file_major_version = ds.readShort();
		file_minor_version = ds.readShort();
		
		if(file_major_version != GraphFunctionsFrame.major_version || file_minor_version != GraphFunctionsFrame.minor_version)
		{
			System.out.println("Invalid version!");
			ds.close();
			return false;
		}
		
		if(!readWindowSettings()) return false;
		if(!readFunctions()) return false;
		
		ds.close();
		read = true;
		return true;
	}
	public WindowSettings getWindowSettings()
	{
		if(read)
			return wsettings;
		else
			return null;
	}
	
	private String reconstructFactor(FactorData fd)
	{
		String s = "";
		switch(fd.type)
		{
			case CONSTANT:
			{
				if(Math.rint(fd.value) == fd.value)
					s += (int) Math.rint(fd.value);
				else
					s += fd.value;
				
				break;
			}
			case ARGUMENT:
			{
				s += "" + argumentChar + '^';
				if(fd.exponentfunc.termscount==0)
				{
					s = "" + argumentChar;
				}
				else if(fd.exponentfunc.termscount==1 && fd.exponentfunc.terms[0].factorscount==1)
				{
					s += reconstructFunction(fd.exponentfunc);
					if(s.endsWith("^1"))
					{
						s = s.substring(0, s.length()-2);
					}
					else if(s.endsWith("^-1"))
					{
						s = String.format("1/%s", s.subSequence(0, s.length()-3)); 
					}
				}
				else
				{
					s += "(" + reconstructFunction(fd.exponentfunc) + ")";
				}
				break;
			}
			case PARENTHESES:
			{
				if(fd.value == -1)
				{
					s += '-';
				}
				
				if(fd.basefunc.termscount>1)
				{
					s += "(";
					s += reconstructFunction(fd.basefunc);
					s += ")^";
				}
				else
				{
					s += reconstructFunction(fd.basefunc) + "^";
				}
				if(fd.exponentfunc.termscount==1 && fd.exponentfunc.terms[0].factorscount==1)
				{
					s += reconstructFunction(fd.exponentfunc);
					if(s.endsWith("^1"))
					{
						s = s.substring(0, s.length()-2);
					}
					else if(s.endsWith("^-1"))
					{
						if((new Function(s.substring(0, s.length()-3))).getTerms().size() == 1)
							s = String.format("1/%s", s.subSequence(1, s.length()-4)); 
						else
						s = String.format("1/%s", s.subSequence(0, s.length()-3)); 
					}
					
				}
				else
				{
					s += "(" + reconstructFunction(fd.exponentfunc) + ")";

				}
				
				break;
			}
			case FUNCTION:
			{
				s += fd.functionname + "(";
				if(fd.functionname.equalsIgnoreCase("const") || fd.functionname.equalsIgnoreCase("dydx")|| fd.functionname.equalsIgnoreCase("deriv"))
				{
					for(int i = 0; i< fd.functionargscount; i++)
					{
						s += fd.functionargs_str[i];
						if(i < fd.functionargscount - 1)
							s += ", ";
					}
				}
				else
				{
					for(int i = 0; i< fd.functionargscount; i++)
					{
						s += reconstructFunction(fd.functionargs[i]);
						if(i < fd.functionargscount - 1)
							s += ", ";
					}
				}
				s += ")";
				break;
			}
		}
		return s;
	}
	private String reconstructTerm(TermData td)
	{
		String s = "";
		Factor.Type previousType = null;
		boolean nextNegative=  false;
		for(int i=0;i<td.factorscount;i++)
		{
			FactorData fd = td.factors[i];
			
			if(td.factorscount > 1 && s.trim().length() > 0 && !(previousType == Factor.Type.CONSTANT && (fd.type == Factor.Type.ARGUMENT || fd.type == Factor.Type.FUNCTION || fd.type == Factor.Type.PARENTHESES)))
			{	
				
				if((previousType == fd.type || s.trim().length() > 0) && !s.endsWith("*"))
					s += "*";
			
					
			}
						
			String tmp =  reconstructFactor(fd);
			if(tmp.startsWith("1/") && s.length() > 0 && !s.endsWith("*"))
				s += "*";
			
			if(tmp.equalsIgnoreCase("1"))
			{
				if(td.factorscount == 1) s += tmp;
			}
			else if(tmp.equalsIgnoreCase("-1"))
			{
				if(i < td.factorscount-1)
				{
					nextNegative = !nextNegative;
				}
				else 
				{
					if(nextNegative)
					{
						s += '-';
						nextNegative = false;
					}
					s += tmp;
				}
			}
			else 
			{
				if(nextNegative)
				{
					s += '-';
					nextNegative = false;
				}
				s += tmp;
			}
			
			
			previousType = fd.type;
		}
		
		return s;
	}
	private String reconstructFunction(FunctionData fd)
	{
		String s = "";
	
		for(int i=0;i<fd.termscount;i++)
		{
			String tmp =  reconstructTerm(fd.terms[i]);
			if(!tmp.startsWith("-") && i > 0)
				s += String.format("+%s", tmp);
			else
				s += tmp;
		}
		return s;
	}
	public String[] getReconstructedFunctionStrings()
	{
		String functions[] = new String[this.functionCount];
		for(int i=0;i<this.functionCount; i++)
		{
			if(functiondata[i].termscount > 0)
			{
				functions[i] = reconstructFunction(functiondata[i]);
			}
		}
		return functions;
	} 
	public Function[] getReconstructedFunctions()
	{
		Function[] functions = new Function[this.functionCount];
		String[] fstrings = getReconstructedFunctionStrings();
		for(int i=0;i<functionCount;i++)
		{
			Function f = new Function();
			f.Parse(fstrings[i]);
			f.setColor(this.functiondata[i].color);
			f.setDraw(this.functiondata[i].draw);
			functions[i] = f;
		}
		return functions;
	}
	public short[] getFunctionIndexes()
	{
		short[] indexes = new short[this.functionCount];
	
		for(int i=0;i<functionCount;i++)
		{
			indexes[i] = functiondata[i].index;
		}
		return indexes;
	}
}
