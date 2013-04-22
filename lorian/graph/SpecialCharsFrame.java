package lorian.graph;

import java.awt.Point;

import javax.swing.JFrame;

public class SpecialCharsFrame extends JFrame {
	private static final long serialVersionUID = -8434823391936911560L;

	public SpecialCharsFrame(Point point) 
	{
		this.setLocationRelativeTo(null);
		this.setLocation((int) point.getX() + 450, (int) point.getY() + 200);
		this.setTitle(GraphFunctionsFrame.Translate("specialchars.title"));
		this.setResizable(false); 
		this.setSize(300, 300);
		Restore();
	}
	public void Restore()
	{
		this.setVisible(true);
	}
	
}
