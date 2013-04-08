package lorian.graph;

import javax.swing.JApplet;

public class GraphApplet extends JApplet {
	private static final long serialVersionUID = 7736572177263287101L;
	@Override
	public void init()
	{
		GraphFunctionsFrame gfframe = new GraphFunctionsFrame(true);
		this.setJMenuBar(gfframe.menuBar);
		this.add(gfframe.MainPanel);
	}
	
	
}
