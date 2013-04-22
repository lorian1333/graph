package lorian.graph.lang;

import java.io.IOException;
import java.util.Locale;

public class Language  {
	private LangFileReader reader;
	public Language()
	{
		
	}
	
	public void read(String langcode) throws IOException
	{
		reader = new LangFileReader("/languages/" + langcode + ".lang");
		reader.read();
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
		read(locale.getLanguage(), locale.getCountry());
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
