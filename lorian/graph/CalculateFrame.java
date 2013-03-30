package lorian.graph;

import java.awt.Point;

import javax.swing.JFrame;
import javax.swing.JPanel;

import lorian.graph.function.MathChars;

public class CalculateFrame extends JPanel {
	private static final long serialVersionUID = -6709615022829676720L;
	CalculationsData data;
	
	enum Calculation
	{
		VALUE, ZERO, MINIMUM, MAXIMUM, INTERSECT, DYDX, INTEGRAL;
	}
	
	public CalculateFrame(Point point, Calculation calc)
	{
		//this.setLocationRelativeTo(null);
		//this.setLocation((int) point.getX() + 450, (int) point.getY() + 200);
		String title = "Calculate: ";
		switch(calc)
		{
		case VALUE:
			title += "Value";
			break;
		case ZERO:
			title += "Zero";
			break;
		case MINIMUM:
			title += "Minimum";
			break;
		case MAXIMUM:
			title += "Maximum";
			break;
		case INTERSECT:
			title += "Intersect";
			break;
		case DYDX:
			title += "dy/dx";
			break;
		case INTEGRAL:
			title += (MathChars.Integral.getCode() + "f(x)dx");
			break;
		default:
			break;
		}
		//this.setTitle(title);
		//this.setResizable(false);
		this.setSize(300, 300);
		this.setVisible(true);
	}
	
	
}
