package lorian.graph;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.Spring;
import javax.swing.SpringLayout;

import lorian.graph.function.Function;
import lorian.graph.function.MathChars;
import lorian.graph.function.PointXY;
import lorian.graph.function.Util;
import lorian.graph.function.VisualPoint;
import lorian.graph.function.VisualPointLocationChangeListener;

public class CalculateFrame extends JPanel implements ActionListener, VisualPointLocationChangeListener {
	private static final long serialVersionUID = -6709615022829676720L;
	private Calculation calc;
	//private CalculationsData data;
	private String title;
	private SpringLayout layout;
	private int height = 5;
	
	private JComboBox<String> funcComboBox, funcComboBox2;
	private JSpinner x1, x2;
	private JLabel resultLabel;
	
	private VisualPoint lowx, upx;
	enum Calculation
	{
		VALUE, ZERO, MINIMUM, MAXIMUM, INTERSECT, DYDX, INTEGRAL;
	}
	
	private void initGeneralUI()
	{
		layout = new SpringLayout();
		this.setLayout(layout);

		JLabel titlelabel = new JLabel(title, JLabel.CENTER);
		titlelabel.setFont(titlelabel.getFont().deriveFont(13.0f).deriveFont(Font.BOLD));
		this.add(titlelabel);
		
		SpringLayout.Constraints titleCons = layout.getConstraints(titlelabel);
		titleCons.setX(Spring.sum(Spring.constant(40), titleCons.getConstraint(SpringLayout.HORIZONTAL_CENTER)));
		titleCons.setY(Spring.constant(height));
		
		
		JButton closeButton = new JButton();
		closeButton.setPreferredSize(new Dimension(25, 25));
		closeButton.setName("close");
		closeButton.addActionListener(this);
		try
		{
			Image img = ImageIO.read(getClass().getResource("/res/close.png"));
			closeButton.setIcon(new ImageIcon(img));
		}
		catch (IOException e)
		{
			e.printStackTrace();
			closeButton.setText("x");
		}
				
		this.add(closeButton);
		SpringLayout.Constraints closeButtonCons = layout.getConstraints(closeButton);
		closeButtonCons.setX(Spring.sum(Spring.constant(220), closeButtonCons.getConstraint(SpringLayout.EAST)));
		closeButtonCons.setY(Spring.constant(0)); 
		
		height += titlelabel.getPreferredSize().getHeight() + 10;
	}  
	private void AddCalculateButton(int height)
	{

		JButton calcButton = new JButton("Calculate");
		calcButton.addActionListener(this);
		calcButton.setName("calculate");
		this.add(calcButton);
		
		SpringLayout.Constraints calcButtonCons = layout.getConstraints(calcButton);
		calcButtonCons.setX(Spring.constant(80));
		calcButtonCons.setY(Spring.constant(height));
		height += calcButton.getPreferredSize().getHeight() + 15;
		
		resultLabel = new JLabel("Result");
		resultLabel.setFont(resultLabel.getFont().deriveFont(13.0f));
		resultLabel.setVisible(false);
		GraphFunctionsFrame.gframe.SetVisualPointsVisible(false);
		this.add(resultLabel);
		
		SpringLayout.Constraints resultCons = layout.getConstraints(resultLabel);
		resultCons.setX(Spring.constant(80));
		resultCons.setY(Spring.constant(height));
	}
	private void initMovablePoints()
	{
		int funcindex;
		try
		{
			funcindex = Integer.parseInt(((String) funcComboBox.getSelectedItem()).substring(1)) - 1;
		}
		catch (Exception e)
		{
			System.out.println("Error parsing function index");
			return;
		}
		
		Function f = GraphFunctionsFrame.functions.get(funcindex);
		
		lowx = new VisualPoint(new PointXY(-2, f.Calc(-2)), funcindex, true, false, "Lower limit");
		upx = new VisualPoint(new PointXY(2, f.Calc(2)), funcindex, true, false, "Upper limit");
		lowx.addLocationChangedListener(this);
		upx.addLocationChangedListener(this);
		GraphFunctionsFrame.gframe.ClearVisualPoints();
		GraphFunctionsFrame.gframe.AddVisualPoint(lowx);
		GraphFunctionsFrame.gframe.AddVisualPoint(upx);
		GraphFunctionsFrame.gframe.SetVisualPointsVisible(true);
	}
	private JComboBox<String> initFunctionCombobox(String comboboxName, String labelText)
	{	
		JComboBox<String> ComboBox = new JComboBox<String>(GetActiveFunctions());
		JLabel functionLabel = new JLabel(labelText);
		functionLabel.setFont(functionLabel.getFont().deriveFont(13.0f));
		ComboBox = new JComboBox<String>(GetActiveFunctions());
		ComboBox.setName(comboboxName);
		ComboBox.addActionListener(this);
			

		this.add(functionLabel);
		this.add(ComboBox);
		
		SpringLayout.Constraints labelCons = layout.getConstraints(functionLabel);
		labelCons.setX(Spring.constant(60));
		labelCons.setY(Spring.constant(height));
			
		SpringLayout.Constraints comboboxCons = layout.getConstraints(ComboBox);
		//comboboxCons.setX(Spring.constant(120));
		comboboxCons.setX(Spring.sum(labelCons.getConstraint(SpringLayout.EAST), Spring.constant(10))); 
		comboboxCons.setY(Spring.constant(height));
				
		height += functionLabel.getPreferredSize().getHeight() + 10;
		return  ComboBox;
	}
	private void initLowUpX()
	{
		
	}
	
	
	
	
	private void initValueUI()
	{
		initGeneralUI();
		funcComboBox = initFunctionCombobox("function1", "Function: "); 
		// X
		JLabel xLabel = new JLabel("X:");
		xLabel.setFont(xLabel.getFont().deriveFont(13.0f));
		SpinnerNumberModel sModel = new SpinnerNumberModel(1.0,  Long.MIN_VALUE, Long.MAX_VALUE, 1.0); 
		x1 = new JSpinner(sModel);
		x1.setPreferredSize(new Dimension(80, (int) x1.getPreferredSize().getHeight()));

		this.add(xLabel);
		this.add(x1);
		
		SpringLayout.Constraints labelCons = layout.getConstraints(xLabel);
		labelCons.setX(Spring.constant(60));
		labelCons.setY(Spring.constant(height));
		SpringLayout.Constraints spinnerCons = layout.getConstraints(x1);
		spinnerCons.setX(Spring.constant(126));
		spinnerCons.setY(Spring.constant(height));
		
		height += xLabel.getPreferredSize().getHeight() + 15;
		
		// Calculate button	
		AddCalculateButton(height);
		
	}
	
	private void initZeroUI()
	{
		initGeneralUI();
		funcComboBox = initFunctionCombobox("function1", "Function: "); 
		initLowUpX();
		AddCalculateButton(height);
		initMovablePoints();
	}
	private void initMinOrMaxUI()
	{
		initGeneralUI();
		AddCalculateButton(height);
		initMovablePoints();
	}
	private void initIntersectUI()
	{
		initGeneralUI();
		funcComboBox = initFunctionCombobox("function1", "Function 1:");
		funcComboBox2 = initFunctionCombobox("function2", "Function 2:");
		AddCalculateButton(height);
		initMovablePoints();
		
	}
	private void initDyDxUI()
	{
		
	}
	private void initIntegralUI()
	{
		
	}
	
	private String[] GetActiveFunctions()
	{
		List<String> activefunctions = new ArrayList<String>();
		for(int i=0;i< GraphFunctionsFrame.functions.size(); i++)
		{
			if(GraphFunctionsFrame.functions.get(i).isEmpty()) continue;
			activefunctions.add("Y" + (i+1));
		}
		return activefunctions.toArray(new String[activefunctions.size()]);
	}
	public CalculateFrame(Calculation calc)
	{
		this.calc = calc;
		title = "Calculate: ";
		switch(this.calc)
		{
		case VALUE:
			title += "Value";
			initValueUI();
			break;
		case ZERO:
			title += "Zero";
			initZeroUI();
			break;
		case MINIMUM:
			title += "Minimum";
			initMinOrMaxUI();
			break;
		case MAXIMUM:
			title += "Maximum";
			initMinOrMaxUI();
			break;
		case INTERSECT:
			title += "Intersect";
			initIntersectUI();
			break;
		case DYDX:
			title += "dy/dx";
			initDyDxUI();
			break;
		case INTEGRAL:
			title += (MathChars.Integral.getCode() + "f(x)dx");
			initIntegralUI();
			break;
		default:
			break;
		}
		this.setPreferredSize(new Dimension(275, 200));
		this.setVisible(true);
	}
	
	private void Calculate()
	{
		if(this.funcComboBox.getItemCount() == 0 || x1 == null) return;
		int func1index = Integer.parseInt(((String) funcComboBox.getSelectedItem()).substring(1)) - 1;
		double x1val = (Double) x1.getValue();
		
		switch(this.calc)
		{
		case VALUE:
			double result = GraphFunctionsFrame.functions.get(func1index).Calc(x1val);
			String resultstr = String.format("X = %s, Y = %s", Util.GetString(x1val), Util.GetString(result));
			resultLabel.setText(resultstr);
			GraphFunctionsFrame.gframe.ClearVisualPoints();
			if(!Double.isInfinite(result) && !Double.isNaN(result))
			{
				GraphFunctionsFrame.gframe.SetVisualPointsVisible(true);
				GraphFunctionsFrame.gframe.AddVisualPoint(new VisualPoint(new PointXY(x1val, result), func1index, false, true));
			}
			
			resultLabel.setVisible(true);
			break;
		case ZERO:
			break;
		case MINIMUM:
			break;
		case MAXIMUM:
			break;
		case INTERSECT:
			break;
		case DYDX:
			break;
		case INTEGRAL:
			break;
		default:
			break;
		}
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() instanceof JButton)
		{
			JButton source = (JButton) e.getSource();
			if(source.getName().equalsIgnoreCase("calculate"))
			{
				Calculate();
			}
			else if(source.getName().equalsIgnoreCase("close"))
			{
				this.setVisible(false);
				resultLabel.setVisible(false);
				GraphFunctionsFrame.gframe.setCalcPanelVisible(false);
				GraphFunctionsFrame.gframe.SetVisualPointsVisible(false);
			}
		}
		
		else if(e.getSource() instanceof JComboBox<?>)
		{
			@SuppressWarnings("unchecked")
			JComboBox<String> source = (JComboBox<String>) e.getSource();
			
			if(source.getName().equalsIgnoreCase("function1"))
			{
				resultLabel.setVisible(false);
				GraphFunctionsFrame.gframe.ClearVisualPoints();
				if(this.calc != Calculation.VALUE && this.calc != Calculation.DYDX)
					initMovablePoints();
			}
		}
		
		
	}
	
	@Override
	public void OnLocationChange(VisualPoint p) {
		if(p.getLabel().equalsIgnoreCase("lower limit"))
		{
			if(p.getPoint().getX() >= upx.getPoint().getX())
			{
				p.setPoint(new PointXY(upx.getPoint().getX(), p.getPoint().getY()), false); 
			}
			
		}
		else if(p.getLabel().equalsIgnoreCase("upper limit"))
		{
			if(p.getPoint().getX() <= lowx.getPoint().getX())
			{
				p.setPoint(new PointXY(lowx.getPoint().getX(), p.getPoint().getY()), false); 
			}
			
		}
		
	}
	
	public void Update()
	{
		if(funcComboBox != null) funcComboBox.setModel(new JComboBox<String>(GetActiveFunctions()).getModel());
		if(funcComboBox2 != null) {
			funcComboBox2.setModel(new JComboBox<String>(GetActiveFunctions()).getModel());
			funcComboBox2.setSelectedIndex(1);
		}
		resultLabel.setVisible(false);
		GraphFunctionsFrame.gframe.SetVisualPointsVisible(false);
		if(this.calc != Calculation.VALUE && this.calc != Calculation.DYDX)
			initMovablePoints();
		
		/*
		switch(this.calc)
		{
		case VALUE:
			break;
		case ZERO:
			break;
		case MINIMUM:
			break;
		case MAXIMUM:
			break;
		case INTERSECT:
			break;
		case DYDX:
			break;
		case INTEGRAL:
			break;
		default:
			break;
		}
		*/
	}
	
	
}