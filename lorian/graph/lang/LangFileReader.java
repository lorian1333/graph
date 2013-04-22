package lorian.graph.lang;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
		items.add(new Item(name, value)); 
	}
	public void read() throws IOException
	{
		InputStream is = getClass().getResourceAsStream(filename);
		if(is == null) return;
		BufferedReader br = new BufferedReader(new InputStreamReader(is)); 
		
		String line;
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
