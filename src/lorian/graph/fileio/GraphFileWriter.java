package lorian.graph.fileio;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.DeflaterOutputStream;

import lorian.graph.GraphFunctionsFrame;
import lorian.graph.WindowSettings;
import lorian.graph.WindowSettings3D;
import lorian.graph.WindowSettingsParameter;
import lorian.graph.function.Function;
import lorian.graph.function.Function2Var;
import lorian.graph.function.ParameterFunction;

public class GraphFileWriter {
	private final byte magic[] = {'L', 'G', 'F'};
	
	private String filename;
	private WindowSettings wsettings_func;
	private WindowSettings3D wsettings_3dfunc;
	private WindowSettingsParameter wsettings_par;
	private Function[] functions_func;
	private Function2Var[] functions_3dfunc;
	private ParameterFunction[] functions_par;
	
	private DataOutputStream ds;
	public GraphFileWriter(String filename)
	{
		this.filename = filename;
		this.functions_func = new Function[GraphFunctionsFrame.MaxFunctions];
		this.functions_3dfunc = new Function2Var[GraphFunctionsFrame.MaxFunctions];
		this.functions_par = new ParameterFunction[(int) (GraphFunctionsFrame.MaxFunctions * 0.5)];
		
		wsettings_func = new WindowSettings();
		wsettings_3dfunc = new WindowSettings3D();
		wsettings_par = new WindowSettingsParameter();
	}
	
	public void setWindowSettings_func(WindowSettings wsettings)
	{
		if(wsettings != null) this.wsettings_func = wsettings;
	}
	public void setWindowSettings_3dfunc(WindowSettings3D wsettings)
	{
		if(wsettings != null) this.wsettings_3dfunc = wsettings;
	}
	public void setWindowSettings_par(WindowSettingsParameter wsettings)
	{
		if(wsettings != null) this.wsettings_par = wsettings;
	}
	public void setFunctions_func(List<Function> functions)
	{
		functions.toArray(functions_func);
	}
	public void setFunctions_3dfunc(List<Function2Var> functions)
	{
		functions.toArray(functions_3dfunc);
	}
	public void setFunctions_par(List<ParameterFunction> functions)
	{
		functions.toArray(functions_par);
	}

	public boolean write() throws IOException 
	{
		if(wsettings_func == null) return false;
		
		File tmpfile = new File(filename);
		if(tmpfile.exists())
		{
			tmpfile.delete();
		}
		FileOutputStream fos = new FileOutputStream(filename);
		ds = new DataOutputStream(fos);
		ds.write(magic);
		ds.writeShort(GraphFunctionsFrame.major_version);
		ds.writeShort(GraphFunctionsFrame.minor_version);
		ds = new DataOutputStream(new DeflaterOutputStream(fos));
		
		writeWindowSettings();
		writeFunctions();
		
		ds.close();
		return true;
	}
	private void writeWindowSettings() throws IOException
	{
		ds.writeDouble(wsettings_func.getXmin());
		ds.writeDouble(wsettings_func.getXmax());
		ds.writeDouble(wsettings_func.getYmin());
		ds.writeDouble(wsettings_func.getYmax());
		ds.writeBoolean(wsettings_func.gridOn());
		ds.writeBoolean(wsettings_func.autoCalcY());
		
		ds.writeDouble(wsettings_3dfunc.getXmin());
		ds.writeDouble(wsettings_3dfunc.getXmax());
		ds.writeDouble(wsettings_3dfunc.getYmin());
		ds.writeDouble(wsettings_3dfunc.getYmax());
		ds.writeDouble(wsettings_3dfunc.getZmin());
		ds.writeDouble(wsettings_3dfunc.getZmax());
		ds.writeBoolean(wsettings_3dfunc.gridOn());
		ds.writeBoolean(wsettings_3dfunc.autoCalcY());
		
		ds.writeDouble(wsettings_par.getXmin());
		ds.writeDouble(wsettings_par.getXmax());
		ds.writeDouble(wsettings_par.getYmin());
		ds.writeDouble(wsettings_par.getYmax());
		ds.writeDouble(wsettings_par.getTmin());
		ds.writeDouble(wsettings_par.getTmax());
		ds.writeDouble(wsettings_par.getTstep());
		ds.writeBoolean(wsettings_par.gridOn());
		ds.writeBoolean(wsettings_par.autoCalcY());
		ds.writeBoolean(wsettings_par.AutoCalcTStep());
	}
	private void writeFunctions() throws IOException
	{
		short i = 0;
		for(Function f: functions_func)
		{
			if(f!=null)
			{
				if(!f.isEmpty())
				i++;
			}
		}
		ds.writeShort(i);
		
		i = 0;
		for(Function2Var f: functions_3dfunc)
		{
			if(f!=null)
			{
				if(!f.isEmpty())
				i++;
			}
		}
		ds.writeShort(i);
		
		i = 0;
		for(ParameterFunction f: functions_par)
		{
			if(f!=null)
			{
				if(!f.isEmpty())
				i++;
			}
		}
		ds.writeShort(i);
		
		
		for(i = 0; i < functions_func.length; i++)
		{
			Function f = functions_func[i];
			if(f==null)
				continue;
			if(f.isEmpty())
				continue;
			ds.writeShort(i);
			ds.writeBoolean(f.drawOn());
			ds.write(f.getColor().getRed());
			ds.write(f.getColor().getGreen());
			ds.write(f.getColor().getBlue());
			writeString(f.getInputString());
		}
		for(i = 0; i < functions_3dfunc.length; i++)
		{
			Function2Var f = functions_3dfunc[i];
			if(f==null)
				continue;
			if(f.isEmpty())
				continue;
			ds.writeShort(i);
			ds.writeBoolean(f.drawOn());
			ds.write(f.getColor().getRed());
			ds.write(f.getColor().getGreen());
			ds.write(f.getColor().getBlue());
			writeString(f.getInputString());
		}
		for(i = 0; i < functions_par.length; i++)
		{
			ParameterFunction f = functions_par[i];
			if(f==null)
				continue;
			if(f.isEmpty())
				continue;
			ds.writeShort(i);
			ds.writeBoolean(f.drawOn());
			ds.write(f.getColor().getRed());
			ds.write(f.getColor().getGreen());
			ds.write(f.getColor().getBlue());
			writeString(f.getInputStrings()[0]);
			writeString(f.getInputStrings()[1]);
		}
	}
	
	private void writeString(String s) throws IOException 
	{
		ds.writeInt(s.length()); 
		ds.writeChars(s);
	}

	@Deprecated
	public void addFunction(Function f, int index)
	{
		functions_func[index] = f;
	}
	
	
	
}
