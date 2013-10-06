package lorian.graph.lang;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class LangFileReader {
	
	
	
	private String filename;
	private List<Item> items;
	private boolean read = false;
	public LangFileReader(String filename)
	{
		this.filename = filename;
		this.items = new ArrayList<Item>();
	}
	
	private void parseItem(String line) throws IOException
	{
		line = line.trim();
		if(line.startsWith("#") || !line.contains("=")) return;
		
		String name, value;
		int i=0;
		while(line.charAt(i) != '=')
			i++;
		
		name = line.substring(0, i);
		value = line.substring(i+1);
		value = value.replace("\\n", "\n");
		value = value.replace("\\\\", "\\"); 
		items.add(new Item(name, value));
		//System.out.println(new String("blablabla\\nhahaha").replace("\\n", "\n"));
	}
	public void read() throws IOException, UnsupportedEncodingException
	{
		InputStream is = getClass().getResourceAsStream(filename);
		if(is == null) throw new FileNotFoundException(filename + " does not exist");
		String line;
		BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8"))); 
		
		/*
		String filename_msg = filename.substring(filename.lastIndexOf('/')+1);
		//BufferedReader br_tmp = new BufferedReader(new InputStreamReader(is));
		if(line.substring(0, 3).equalsIgnoreCase(new String(new char[] { 0xEF, 0xBB, 0xBF } ))) // the UTF-8 BOM
		{
			line = line.substring(3);
			System.out.println(filename_msg + " has an UTF-8 encoding: OK");
			br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8"))); 
		}
		else if(line.substring(0, 2).equalsIgnoreCase(new String(new char[] { 0xFE, 0xFF } ))) // UTF-16 Big Endian
		{
			System.out.println(filename_msg + " has an UTF-16 (Big Endian) encoding: not supported");
			throw new UnsupportedEncodingException(".lang files should use UTF-8 encoding");
		}
		else if(line.substring(0, 2).equalsIgnoreCase(new String(new char[] { 0xFE, 0xFF } ))) // UTF-16 Little Endian
		{
			System.out.println(filename_msg + " has an UTF-16 (Little Endian) encoding: not supported");
			throw new UnsupportedEncodingException(".lang files should use UTF-8 encoding");
		}
		else
		{
			System.out.println("Could not detect encoding of " + filename_msg);
			br = new BufferedReader(new InputStreamReader(is));
		}
	*/
		while((line = br.readLine()) != null)
		{
			parseItem(line);
		}
		
		br.close();
		read = true;
	}
	public boolean isAvailable(String key)
	{
		if(!read) return false;
		for(Item i: items)
		{
			if(i.getName().equalsIgnoreCase(key))
			{
				return true;
			}
		}
		return false;
	}
	public String getValue(String name)
	{
		if(!read) return name;
		
		for(Item i: items)
		{
			if(i.getName().equalsIgnoreCase(name))
			{
				return i.getValue();
			}
		}
		
		return name;
	}
	public String getName(String value)
	{
		if(!read) return value;
		
		for(Item i: items)
		{
			if(i.getValue().equalsIgnoreCase(value))
			{
				return i.getName();
			}
		}

		return value;
	}
	
	
}
