package lorian.graph.fileio;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import lorian.graph.GraphFunctionsFrame;
import lorian.graph.WindowSettings;
import lorian.graph.function.Function;


public class GraphFileReader {
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
		/*
		fd.termscount = ds.readInt();
		fd.terms = new TermData[fd.termscount];
		for(int i=0;i<fd.termscount;i++)
		{
			fd.terms[i] = readTerm();
		}
		*/
		fd.functionString = readString();
		System.out.println(fd.functionString);
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
	

	
	public String[] getFunctionStrings()
	{
		String functions[] = new String[this.functionCount];
		for(int i=0;i<this.functionCount; i++)
		{
			if(functiondata[i].functionString != null)
				functions[i] = functiondata[i].functionString;
		}
		return functions;
	} 
	public Function[] getFunctions()
	{
		Function[] functions = new Function[this.functionCount];
		String[] fstrings = getFunctionStrings();
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
