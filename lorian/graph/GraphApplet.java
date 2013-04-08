package lorian.graph;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JApplet;
import javax.swing.JPanel;

public class GraphApplet extends JApplet {
	private static final long serialVersionUID = 7736572177263287101L;
	@Override
	public void init()
	{
		GraphFunctionsFrame gfframe = new GraphFunctionsFrame(true);
		//JPanel panel = new JPanel();
		//panel.setBackground(Color.cyan);
		//this.add(panel);
		this.setJMenuBar(gfframe.menuBar);
		this.add(gfframe.MainPanel);
	}
	/*
	@Override
	public void paint(Graphics g)
	{
		//g.drawLine(0, 0, 10, 10);
	}
	*/
	
}
