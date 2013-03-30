package lorian.graph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JLabel;
import javax.swing.JPanel;

import lorian.graph.function.MathChars;

public class CalculateFrame extends JPanel {
	private static final long serialVersionUID = -6709615022829676720L;
	private Calculation calc;
	private CalculationsData data;
	private String title;
	enum Calculation
	{
		VALUE, ZERO, MINIMUM, MAXIMUM, INTERSECT, DYDX, INTEGRAL;
	}
	
	private void initUI()
	{
		JLabel titlelabel = new JLabel(title);
		titlelabel.setFont(titlelabel.getFont().deriveFont(13.0f));
		this.add(titlelabel);
	} 
	public CalculateFrame(Calculation calc)
	{
		this.calc = calc;
		title = "Calculate: ";
		switch(this.calc)
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
		initUI();
		this.setPreferredSize(new Dimension(275, 200));
		this.setVisible(true);
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
	}
}
