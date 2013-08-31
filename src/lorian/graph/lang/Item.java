package lorian.graph.lang;

public class Item {
	private String itemName, itemValue;
	
	public Item(String name, String value)
	{
		this.setName(name);
		this.setValue(value);
	}

	public String getValue() {
		return itemValue;
	}

	public void setValue(String itemValue) {
		this.itemValue = itemValue;
	}

	public String getName() {
		return itemName;
	}

	public void setName(String itemName) {
		this.itemName = itemName;
	}
}
