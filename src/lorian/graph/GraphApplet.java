package lorian.graph;

import javax.swing.JApplet;

public class GraphApplet extends JApplet {
	private static final long serialVersionUID = 7736572177263287101L;
	@Override
	public void init()
	{
		/*
		try {
			GraphMain.applet_Init();
		} catch (Throwable e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage(), "Launching Applet Failed", 0);
			return;
		}
		*/
		
		GraphFunctionsFrame gfframe = new GraphFunctionsFrame(true, false, false); 
		/*
		String smallVal = this.getParameter("small");
		if(smallVal == null ) 
			gfframe = new GraphFunctionsFrame(true);
		else if(smallVal.equalsIgnoreCase("true"))
		{
			gfframe = new GraphFunctionsFrame(true, true, true);
		}
		else
		{
			gfframe = new GraphFunctionsFrame(true);
		}
		*/
		
		this.setJMenuBar(gfframe.menuBar);
		this.add(gfframe.MainPanel);
	}
	
	
}
