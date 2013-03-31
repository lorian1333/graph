package lorian.graph;

import lorian.graph.function.*;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.Spring;
import javax.swing.SpringLayout;

public class GraphFrame extends JPanel {
	private static final long serialVersionUID = -741311884013992607L;
	private List<Function> functions;
	private WindowSettings settings;
	private int YaxisX, XaxisY;
	Dimension size;

	public boolean windowerror = false;
	private JPanel CalcPanel;
	private boolean CalcPanelVisible = false;
	
	GraphFrame(List<Function> functions, WindowSettings settings, Dimension size) {
		super();
		this.size = size;
		this.functions = functions;
		this.settings = settings;
		this.setPreferredSize(size);
		this.setBackground(Color.WHITE);
		this.setOpaque(false);
		
		InitCalcPanel();	
		CalculateAxes();
	}
	private void InitCalcPanel()
	{
		CalcPanel  = new JPanel();
		SpringLayout layout = new SpringLayout();
		this.setLayout(layout);
	
		CalcPanel.setPreferredSize(new Dimension(275, 200));
		CalcPanel.setVisible(CalcPanelVisible);
		
		this.add(CalcPanel);
		
		SpringLayout.Constraints cons = layout.getConstraints(CalcPanel);
		//cons.setX(Spring.constant((int) (size.getWidth() - CalcPanel.getPreferredSize().getWidth())));
		cons.setX(Spring.constant(0));
		cons.setY(Spring.constant((int) (size.getHeight() - CalcPanel.getPreferredSize().getHeight()))); 
	}
	private void CalculateAxes()
	{
		YaxisX = (int) (size.getWidth() * ((double) -settings.getXmin()) / ((double) (settings.getXmax() - settings.getXmin()))) - 1;
		XaxisY = (int) size.getHeight() - (int) (size.getHeight() * ((double) -settings.getYmin()) / ((double) (settings.getYmax() - settings.getYmin()))) - 1;
		windowerror = false;
	}
	public void UpdateWindowSettings(WindowSettings settings)
	{
			this.settings = settings;
			if(settings.getXmax() <= settings.getXmin() || settings.getYmax() <= settings.getYmin())
			{
				System.out.println("Error in windowsettings");
				windowerror = true;
			}
			else windowerror = false;
			this.repaint();
	}
	
	
	private void drawAxes(Graphics g) {
		
		int pix;
		for(long x=settings.getXmin()+1;x<settings.getXmax();x++)
		{
			if(x==0) continue;
			pix = (int) ((x-settings.getXmin()) * (size.getWidth() / (settings.getXmax() - settings.getXmin())));
			if(pix==0) continue;
			g.setColor(Color.BLACK);
			g.drawLine(pix, XaxisY - 5, pix, XaxisY + 5);
			
		}
		
		for(long y=settings.getYmin()+1;y<settings.getYmax();y++)
		{
			if(y==0) continue;
			pix = (int) size.getHeight() -  (int) ((y-settings.getYmin()) * (size.getHeight() / (settings.getYmax() - settings.getYmin())));
			if(pix==0) continue;
			g.setColor(Color.BLACK);
			g.drawLine(YaxisX - 5, pix, YaxisX + 5, pix);
		
		}
		
		g.setColor(Color.BLACK);
		g.drawLine(YaxisX, 0, YaxisX, (int) size.getHeight());
		g.drawLine(0, XaxisY, (int) size.getWidth(), XaxisY);
	}

	private void drawGrid(Graphics g)
	{
		int pix;
		Color gridColor = new Color(0, 186, 0xff);
		for(long x=settings.getXmin()+1;x<settings.getXmax();x++)
		{
			if(x==0) continue;
			pix = (int) ((x-settings.getXmin()) * (size.getWidth() / (settings.getXmax() - settings.getXmin())));
			if(pix==0) continue;
			//grid
			g.setColor(gridColor);
			g.drawLine(pix, 0, pix, (int) size.getHeight());
		
		}
		for(long y=settings.getYmin()+1;y<settings.getYmax();y++)
		{
			if(y==0) continue;
			pix = (int) size.getHeight() -  (int) ((y-settings.getYmin()) * (size.getHeight() / (settings.getYmax() - settings.getYmin())));
			if(pix==0) continue;
			//grid
			g.setColor(gridColor);
			g.drawLine(0, pix, (int) size.getWidth(), pix);
		}
	}
	private void drawFunction(Function f, Graphics g) 
	{
		if(f.isEmpty()) return;
		g.setColor(f.getColor());
		int xpix, ypix;
		double x,y;
		double step = ((double) (settings.getXmax() - settings.getXmin())) / size.getWidth();
		
		Point previous = new Point();
		
		for(xpix = -1, x = settings.getXmin(); xpix < (int) size.getWidth(); xpix++, x+=step)
		{
			
			y = f.Calc(x);
			if(Double.isNaN(y)) 
			{ 
				previous.setLocation(size.getWidth() / 2, size.getHeight() / 2);
				continue;
			}
			ypix = (int) ((settings.getYmax() - y) * (size.getHeight() / (settings.getYmax() - settings.getYmin())));
			if(xpix > -1)
			{
				if(xpix - previous.x < size.getWidth() && ypix - previous.y < size.getHeight())
				{
					g.drawLine(previous.x, previous.y, xpix, ypix);
				}
			}
			previous.setLocation(xpix, ypix);
		}
		g.setColor(Color.BLACK);
	}
	private void drawCalcPanelBorders(Graphics g)
	{
		g.setColor(Color.BLACK);
		((Graphics2D) g).setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
		//int xoff = (int) (this.getWidth() - CalcPanel.getWidth()) - 1, yoff = (int) (this.getHeight() - CalcPanel.getHeight()) - 2;
		int xoff = CalcPanel.getWidth() +1, yoff = (int) (this.getHeight() - CalcPanel.getHeight()) - 2;
		g.drawLine(xoff, yoff, xoff, this.getHeight());
		g.drawLine(0, yoff, xoff, yoff);
		
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		g.clearRect(0, 0, (int) this.getWidth(), (int) this.getHeight());
		
		if(windowerror) return;
		CalculateAxes();
		if(settings.gridOn()) drawGrid(g);
		((Graphics2D) g).setStroke(new BasicStroke(1.1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
		for (int i = 0; i < functions.size(); i++) 
		{
			drawFunction(functions.get(i), g);
		}
		((Graphics2D) g).setStroke(new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
		drawAxes(g);
		if(CalcPanelVisible)
			drawCalcPanelBorders(g);
	}

	
	public void Update(List<Function> functions) {
		this.functions = functions;
		this.repaint();
	}

	public void Update(int functionindex, Function function) {
		this.functions.set(functionindex, function);
		this.repaint();
	}

	public boolean CalcPanelIsVisible() {
		return CalcPanelVisible;
	}

	public void setCalcPanelVisible(boolean calcPanelVisible) {
		CalcPanelVisible = calcPanelVisible;
		this.CalcPanel.setVisible(calcPanelVisible);
		this.repaint();
	}
	public void setCalcPanel(JPanel panel)
	{
		CalcPanel.removeAll();
		CalcPanel.add(panel);	
		this.paintAll(this.getGraphics());
	}




}
