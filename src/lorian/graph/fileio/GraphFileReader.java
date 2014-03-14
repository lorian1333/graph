package lorian.graph.fileio;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.zip.InflaterInputStream;

import lorian.graph.GraphFunctionsFrame;
import lorian.graph.WindowSettings;
import lorian.graph.WindowSettings3D;
import lorian.graph.WindowSettingsParameter;
import lorian.graph.function.Function;
import lorian.graph.function.Function2Var;
import lorian.graph.function.ParameterFunction;



public class GraphFileReader {
	private final byte magic[] = {'L', 'G', 'F'};
	private boolean read = false;
	private DataInputStream ds;
	private String filename;
	
	private short file_major_version, file_minor_version;
	private WindowSettings wsettings_func;
	private WindowSettings3D wsettings_3dfunc;
	private WindowSettingsParameter wsettings_par;
	private short functionCount_func, functionCount_3dfunc, functionCount_par;
	private FunctionData[] functiondata_func, functiondata_3dfunc;
	private FunctionDataParameter[] functiondata_par;
	
	public GraphFileReader(String filename)
	{
		this.filename = filename;
		wsettings_func = new WindowSettings();
		wsettings_3dfunc = new WindowSettings3D();
		wsettings_par = new WindowSettingsParameter();	
	}
	
	public boolean read() throws IOException
	{
		read = false;
		FileInputStream fis = new FileInputStream(filename);
		ds = new DataInputStream(fis);  
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
		
		ds = new DataInputStream(new InflaterInputStream(fis));
		
		
		if(!readWindowSettings())
		{
			ds.close();
			return false;
		}
		if(!readFunctions())
		{
			ds.close();
			return false;
		}
		
		ds.close();
		read = true;
		return true;
	}
	
	private boolean readWindowSettings() throws IOException 
	{
		wsettings_func.setXmin(ds.readDouble()); 
		wsettings_func.setXmax(ds.readDouble());
		wsettings_func.setYmin(ds.readDouble());
		wsettings_func.setYmax(ds.readDouble());
		wsettings_func.setGrid(ds.readBoolean());
		wsettings_func.setAutoCalcY(ds.readBoolean());
		
		wsettings_3dfunc.setXmin(ds.readDouble()); 
		wsettings_3dfunc.setXmax(ds.readDouble());
		wsettings_3dfunc.setYmin(ds.readDouble());
		wsettings_3dfunc.setYmax(ds.readDouble());
		wsettings_3dfunc.setZmin(ds.readDouble());
		wsettings_3dfunc.setZmax(ds.readDouble());
		wsettings_3dfunc.setGrid(ds.readBoolean());
		wsettings_3dfunc.setAutoCalcY(ds.readBoolean());
		
		wsettings_par.setXmin(ds.readDouble()); 
		wsettings_par.setXmax(ds.readDouble());
		wsettings_par.setYmin(ds.readDouble());
		wsettings_par.setYmax(ds.readDouble());
		wsettings_par.setTmin(ds.readDouble());
		wsettings_par.setTmax(ds.readDouble());
		wsettings_par.setTstep(ds.readDouble());
		wsettings_par.setGrid(ds.readBoolean());
		wsettings_par.setAutoCalcY(ds.readBoolean());
		wsettings_par.setAutoCalcTStep(ds.readBoolean());
		
		return true;
	}
	
	private boolean readFunctions() throws IOException
	{
		functionCount_func = ds.readShort();
		functionCount_3dfunc = ds.readShort();
		functionCount_par = ds.readShort();
		
		functiondata_func = new FunctionData[functionCount_func];
		functiondata_3dfunc = new FunctionData[functionCount_3dfunc];
		functiondata_par = new FunctionDataParameter[functionCount_par];
		
		for(short i=0;i<functionCount_func;i++)
		{
			functiondata_func[i] = readFunction_2d_or_3d_func();
		}
		for(short i=0;i<functionCount_3dfunc;i++)
		{
			functiondata_3dfunc[i] = readFunction_2d_or_3d_func();
		}
		for(short i=0;i<functionCount_par;i++)
		{
			functiondata_par[i] = readFunction_par();
		}
		return true;
	}
	
	private FunctionData readFunction_2d_or_3d_func() throws IOException
	{
		FunctionData fd = new FunctionData();
		fd.index = ds.readShort();
		fd.draw  = ds.readBoolean();
		fd.color = new Color(ds.read(), ds.read(), ds.read());
		fd.functionString = readString();
		return fd;
	}
	
	private FunctionDataParameter readFunction_par() throws IOException
	{
		FunctionDataParameter fd = new FunctionDataParameter();
		fd.index = ds.readShort();
		fd.draw  = ds.readBoolean();
		fd.color = new Color(ds.read(), ds.read(), ds.read());
		fd.functionString_x = readString();
		fd.functionString_y = readString();
		return fd;
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
	
	public WindowSettings getWindowSettings_func()
	{
		if(read)
			return wsettings_func;
		else
			return null;
	}
	
	public WindowSettings3D getWindowSettings_3dfunc()
	{
		if(read)
			return wsettings_3dfunc;
		else
			return null;
	}
	public WindowSettingsParameter getWindowSettings_par()
	{
		if(read)
			return wsettings_par;
		else
			return null;
	}
	
	public String[] getFunctionStrings_func()
	{
		String functions[] = new String[this.functionCount_func];
		for(int i=0;i<this.functionCount_func; i++)
		{
			if(functiondata_func[i].functionString != null)
				functions[i] = functiondata_func[i].functionString;
		}
		return functions;
	} 
	public String[] getFunctionStrings_3dfunc()
	{
		String functions[] = new String[this.functionCount_3dfunc];
		for(int i=0;i<this.functionCount_3dfunc; i++)
		{
			if(functiondata_3dfunc[i].functionString != null)
				functions[i] = functiondata_3dfunc[i].functionString;
		}
		return functions;
	} 
	public String[][] getFunctionStrings_par()
	{
		String functions[][] = new String[this.functionCount_par][2];
		for(int i=0;i<this.functionCount_par; i++)
		{
			if(functiondata_par[i].functionString_x != null && functiondata_par[i].functionString_y != null)
			{
				functions[i][0] = functiondata_par[i].functionString_x;
				functions[i][1] = functiondata_par[i].functionString_y;
				
			}
		}
		return functions;
	} 
	
	public HashMap<Short, Function> getFunctions_func() 
	{
		HashMap<Short, Function> functions = new HashMap<Short, Function>();
		String[] fstrings = getFunctionStrings_func();
		for(int i=0;i<functionCount_func;i++)
		{
			Function f = new Function();
			if(!fstrings[i].isEmpty()) f.Parse(fstrings[i]);
			f.setColor(this.functiondata_func[i].color);
			f.setDraw(this.functiondata_func[i].draw);
			functions.put(this.functiondata_func[i].index, f);
		}
		return functions;
	}
	
	public HashMap<Short, Function2Var> getFunctions_3dfunc()
	{ 
		HashMap<Short, Function2Var> functions = new HashMap<Short, Function2Var>();
		String[] fstrings = getFunctionStrings_3dfunc();
		for(int i=0;i<functionCount_3dfunc;i++)
		{
			Function2Var f = new Function2Var();
			if(!fstrings[i].isEmpty()) f.Parse(fstrings[i]);
			f.setColor(this.functiondata_3dfunc[i].color);
			f.setDraw(this.functiondata_3dfunc[i].draw);
			functions.put(this.functiondata_3dfunc[i].index, f);
		}
		return functions;
	}
	public HashMap<Short, ParameterFunction> getFunctions_par()
	{
		HashMap<Short, ParameterFunction> functions = new HashMap<Short, ParameterFunction>();
		String[][] fstrings = getFunctionStrings_par();
		for(int i=0;i<functionCount_par;i++)
		{
			ParameterFunction f = new ParameterFunction();
			if(!(fstrings[i][0].isEmpty() || fstrings[i][1].isEmpty())) 
				f.Parse(fstrings[i][0], fstrings[i][1]);
			f.setColor(this.functiondata_par[i].color);
			f.setDraw(this.functiondata_par[i].draw);
			functions.put(this.functiondata_par[i].index, f);
		}
		return functions;
	}
	/*
	@Deprecated
	public short[] getFunctionIndexes()
	{
		short[] indexes = new short[this.functionCount_func];
	
		for(int i=0;i<functionCount_func;i++)
		{
			indexes[i] = functiondata_func[i].index;
		}
		return indexes;
	}
	
	@Deprecated
	public Function[] getFunctions_func_arr()
	{
		Function[] functions = new Function[this.functionCount_func];
		String[] fstrings = getFunctionStrings_func();
		for(int i=0;i<functionCount_func;i++)
		{
			Function f = new Function();
			f.Parse(fstrings[i]);
			f.setColor(this.functiondata_func[i].color);
			f.setDraw(this.functiondata_func[i].draw);
			functions[i] = f;
		}
		return functions;
	} */
}
