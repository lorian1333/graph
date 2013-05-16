package lorian.graph.fileio;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import lorian.graph.GraphFunctionsFrame;
import lorian.graph.WindowSettings;
import lorian.graph.function.Function;

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

	private void writeFunction(Function f) throws IOException
	{
		writeString(f.toString());
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
