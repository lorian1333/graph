package lorian.graph.lang;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Locale;

public class Language  {
	private LangFileReader reader;
	public Language()
	{
		
	}
	
	public void read(String langcode) throws IOException
	{
		reader = new LangFileReader("/languages/" + langcode + ".lang");
		try
		{
			reader.read();
		}
		catch (FileNotFoundException e)
		{
			System.out.println(langcode  + ".lang is not available. Trying to get " + langcode + " from another country...");
			read_other_country(langcode);
		}
	}
	private void read_other_country(String langcode) throws IOException
	{
		URL url = getClass().getResource("/languages/");
		File dir = null; 
		try {
			dir = new File(url.toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return;
		}
		for(File f: dir.listFiles())
		{
			if(f.getName().startsWith(langcode))
			{
				System.out.println("Success: got " + f.getName());
				reader = new LangFileReader("/languages/" + f.getName());
				reader.read();
				
			}
		}
	}
	public void read(String langcode, String countrycode) throws IOException
	{
		reader = new LangFileReader("/languages/" + langcode + "_" + countrycode.toUpperCase() + ".lang");
		reader.read();
	}
	
	public boolean isAvailable(String key)
	{
		return reader.isAvailable(key);
	}
	public void readDefault() throws IOException
	{
		Locale locale = Locale.getDefault();
		try
		{
			read(locale.getLanguage(), locale.getCountry());
		}
		catch (FileNotFoundException e)
		{
			System.out.println(locale.getLanguage() + "_" + locale.getCountry() + ".lang is not available. Trying to get " + locale.getLanguage() + ".lang...");
			read(locale.getLanguage());
		}
		
	
	}
	
	public String getValue(String name)
	{
		return reader.getValue(name);
	}
	public String getName(String value)
	{
		return reader.getName(value);
	}
	
}
