package lorian.graph;

import lorian.graph.fileio.ExtensionFileFilter;
import lorian.graph.fileio.JFileChooserWithConfirmation;
import lorian.graph.function.*;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.filechooser.FileFilter;

public class GraphFrame extends JPanel implements  ActionListener, MouseListener,  MouseMotionListener {
	private static final long serialVersionUID = -741311884013992607L;
	private List<Function> functions;
	
	private List<VisualPoint> vpoints;
	private List<Point> vmovablepoints;
	private boolean vpointsVisible = false;
	private Image pointimg, movablepointimg;
	private int MovingVPointIndex = -1;
	private int MovingPointIndex = -1;
	private Point MouseStart;
	
	private WindowSettings settings;
	private int YaxisX, XaxisY;
	Dimension size;
	
	public boolean windowerror = false;
	private JPanel CalcPanel;
	private boolean CalcPanelVisible = false;
	private boolean clearOnlyCorner = false;
	
	private int FillFunctionIndex = -1;
	private double FillLowX = 0, FillUpX = 0;
	
	GraphFrame(List<Function> functions, WindowSettings settings, Dimension size) {
		super();
		this.size = size;
		this.functions = functions;
		this.settings = settings;
		this.setPreferredSize(size);
		this.setBackground(Color.WHITE);
		this.setOpaque(false);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		
		vpoints = new ArrayList<VisualPoint>();
		vmovablepoints = new ArrayList<Point>();
		
		try
		{
			pointimg = ImageIO.read(getClass().getResource("/res/point.png")).getScaledInstance(25, 25, 0);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		try
		{
			movablepointimg = ImageIO.read(getClass().getResource("/res/movablepoint.png")).getScaledInstance(25, 25, 0);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		InitPopupMenu();
		InitCalcPanel();	
		CalculateAxes();
	}
	private void InitCalcPanel()
	{
		CalcPanel  = new JPanel();
		SpringLayout layout = new SpringLayout();
		this.setLayout(layout);
		
		CalcPanel.setPreferredSize(new Dimension(275, 215));
		CalcPanel.setVisible(CalcPanelVisible);
		
		this.add(CalcPanel);
		
		SpringLayout.Constraints cons = layout.getConstraints(CalcPanel);
		//cons.setX(Spring.constant((int) (size.getWidth() - CalcPanel.getPreferredSize().getWidth())));
		cons.setX(Spring.constant(0));
		cons.setY(Spring.constant((int) (size.getHeight() - CalcPanel.getPreferredSize().getHeight()) + 10)); 
	}
	private void InitPopupMenu()
	{
		final JPopupMenu menu = new JPopupMenu();
		JMenuItem screenShotItem = new JMenuItem(GraphFunctionsFrame.Translate("menu.savescreenshot"));
		screenShotItem.setName("screenshot");
		menu.add(screenShotItem);
		screenShotItem.addActionListener(this);
		this.addMouseListener(new MouseListener()
		{
			@Override
			public void mouseClicked(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}

			public void mousePressed( MouseEvent e )  
            { checkForTriggerEvent( e ); }  

			public void mouseReleased( MouseEvent e )  
            { checkForTriggerEvent( e ); }  
			
			 private void checkForTriggerEvent( MouseEvent e )  
	            {  
	               if(e.isPopupTrigger())  
	                  menu.show(e.getComponent(), e.getX(), e.getY());  
	            }  

		}
		);
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
				System.out.println(GraphFunctionsFrame.Translate("message.windowsettingserror"));
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
	private void drawPart(Graphics g, double x1, double y1, double x2, double y2)
	{
		g.drawLine((int)x1, (int)y1, (int)x2, (int)y2);
		//Graphics2D g2d = (Graphics2D) g;
		//QuadCurve2D q = new QuadCurve2D.Double();
		//q.setCurve(x1, y1, (x1 + 0.5), y1, x2, y2);
		//Point2D p = new Point2D.Double();
		
		//g2d.draw(q);
	}
	private void drawFunction(Function f, boolean fill, Graphics g) 
	{
		if(f.isEmpty()) return;
		g.setColor(f.getColor());
		int xpix, ypix;
		boolean inNaN = false;
		double x,y;
		double step = ((double) (settings.getXmax() - settings.getXmin())) / size.getWidth();
		
		Point previous = new Point();
		boolean WaitForRealNumber = false;
		
		
		for(xpix = -1, x = settings.getXmin(); xpix < (int) size.getWidth(); xpix++, x+=step)
		{
			y = f.Calc(x);
			if(Double.isNaN(y)) 
			{ 
				if(inNaN) continue;
				inNaN = true;
				double tmpX = Calculate.FindLastXBeforeNaN(f, x - step);
				if(!Double.isNaN(tmpX))
				{
					double tmpY =  f.Calc(tmpX);
					ypix = (int) ((settings.getYmax() - tmpY) * (size.getHeight() / (settings.getYmax() - settings.getYmin())));
					//g.drawLine(previous.x, previous.y, xpix, ypix);
					//QuadCurve2D q = new QuadCurve2D.Double();
					//q.setCurve(previous.x, previous.y, (previous.x + 0.5), previous.y, xpix, ypix);
					//g2d.draw(q);
					drawPart(g, previous.x, previous.y, xpix, ypix);
					
				}
				
				previous = null;
				if(!WaitForRealNumber)
				{
					WaitForRealNumber = true;
				}
				continue;
			}
			else {
				if(inNaN)
				{
					double tmpX = Calculate.FindFirstXAfterNaN(f, x - step);
					if(!Double.isNaN(tmpX))
					{
						double tmpY =  f.Calc(tmpX);
						ypix = (int) ((settings.getYmax() - tmpY) * (size.getHeight() / (settings.getYmax() - settings.getYmin())));
						g.drawRect(xpix, ypix, 1, 1);
						if(previous == null) previous = new Point();
						previous.setLocation(xpix, ypix);
					}
					
					
					inNaN = false;
					continue;
					
				}
				
				if(WaitForRealNumber) WaitForRealNumber = false;
				
			}
			
			ypix = (int) ((settings.getYmax() - y) * (size.getHeight() / (settings.getYmax() - settings.getYmin())));
			if(xpix > -1)
			{
				if(previous == null)
				{
					g.drawRect(xpix, ypix, 1, 1);
				}
				else if(Math.abs(xpix - previous.x) < size.getWidth() && Math.abs(ypix - previous.y) < size.getHeight())
				{
					//g.drawLine(previous.x, previous.y, xpix, ypix);
					drawPart(g, previous.x, previous.y, xpix, ypix);
				}
				
				if(fill && x >= FillLowX - 0.01 && x <= FillUpX + 0.01)
				{
					g.setColor(Util.lighter(f.getColor(), true) );
					((Graphics2D) g).setStroke(new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL));
					if(y > 0)
					{
						if(ypix < 0)
							g.drawLine(xpix, 0, xpix, this.XaxisY);
						else
							g.drawLine(xpix, ypix-1, xpix, this.XaxisY);
					}
					else if(y < 0)
					{
						if(ypix > size.getHeight())
							//g.drawLine(xpix, (int) size.getHeight(), xpix, this.XaxisY);
							drawPart(g, xpix, (int) size.getHeight(), xpix, this.XaxisY);
						else
							//g.drawLine(xpix, ypix+1, xpix, this.XaxisY);
							drawPart(g, xpix, ypix+1, xpix, this.XaxisY);
					}
					((Graphics2D) g).setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
					g.setColor(f.getColor());
				}
			}
			if(previous == null) previous = new Point();
			previous.setLocation(xpix, ypix);
		}
		g.setColor(Color.BLACK);
	}
	private void drawCalcPanelBorders(Graphics g)
	{
		g.setColor(Color.BLACK);
		((Graphics2D) g).setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
		//int xoff = (int) (this.getWidth() - CalcPanel.getWidth()) - 1, yoff = (int) (this.getHeight() - CalcPanel.getHeight()) - 2;
		int xoff = CalcPanel.getWidth() +1, yoff = (int) (this.getHeight() - CalcPanel.getHeight()) - 2 + 10;
		g.drawLine(xoff, yoff, xoff, this.getHeight());
		g.drawLine(0, yoff, xoff, yoff);
		
	}
	private String getRightCoordinateString(VisualPoint p)
	{
		if(p.isMovable())
			return String.format("(%.4f, %.4f)", Util.round(p.getPoint().getX(), 4),  Util.round(p.getPoint().getY(), 4));
		else
			return String.format("(%s, %s)", Util.GetString(p.getPoint().getX()), Util.GetString(p.getPoint().getY()));
	}
	private void drawVisualPoints(Graphics g)
	{
		int x,y;
		int i=0;
		
		for(VisualPoint p: vpoints)
		{
			x = (int) (((p.getPoint().getX()  - settings.getXmin()) / (settings.getXmax() - settings.getXmin()) * size.getWidth())) - 13;
			if(Double.isNaN(p.getPoint().getY())) p.setPoint(new PointXY(p.getPoint().getX(), 0.0));
			y =  (int) size.getHeight() - (int) (((p.getPoint().getY()  - settings.getYmin()) / (settings.getYmax() - settings.getYmin()) * size.getHeight())) - 13;
			
			
			if(MovingVPointIndex == i)
			{
				vmovablepoints.set(MovingPointIndex, new Point(x, y));
			}
			if(p.isMovable())
			{
				g.drawImage(movablepointimg, x, y, null);
			}
			else
			{
				g.drawImage(pointimg, x, y, null);
			}
			
			y += 15;
			x += 25;
			
			String text;
			if(p.coordinatesOn())
			{
				if(p.getLabel().length() > 0)
					text = p.getLabel() + " " + getRightCoordinateString(p);
				else
					text = getRightCoordinateString(p);
			}
			else
			{
				text = p.getLabel();
			}
			
			g.setFont(g.getFont().deriveFont(13.0f));
			FontMetrics metrics = g.getFontMetrics(g.getFont()); 
			g.setColor(new Color(0xff, 0xff, 0xff, 200)); 
			g.fillRect(x, y - metrics.getHeight(), Util.getStringWidth(metrics, text), metrics.getHeight() + 2);
			g.setColor(Color.BLACK);
			g.drawString(text, x, y); 
			
			i++;
				
		}
	}
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g); 
		
		if(!clearOnlyCorner)
		{
			((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.clearRect(0, 0, (int) this.getWidth(), (int) this.getHeight());
		
			if(windowerror) return;
			CalculateAxes();
			if(settings.gridOn()) drawGrid(g);
			
			((Graphics2D) g).setStroke(new BasicStroke(1.3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
			for (int i = 0; i < functions.size(); i++) 
			{
				if(functions.get(i).drawOn())
					drawFunction(functions.get(i), (i == this.FillFunctionIndex), g);
			}
			((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			((Graphics2D) g).setStroke(new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
			drawAxes(g);
			
			if(vpointsVisible)
			{
				drawVisualPoints(g);
			}
			
			if(CalcPanelVisible)
				drawCalcPanelBorders(g);
		
		
		}
	}

	public void SetFillFunctionIndex(int index)
	{
		this.FillFunctionIndex = index;
		this.repaint();
	}
	public void SetFillLowerLimit(double lowx)
	{
		this.FillLowX = lowx;
	}
	public void SetFillUpperLimit(double upx)
	{
		this.FillUpX = upx;
	}
	public void SetFillFunction(boolean on)
	{
		if(!on) SetFillFunctionIndex(-1);
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
		clearOnlyCorner = true;
		this.repaint();
		clearOnlyCorner = false;
	}
	public void setCalcPanel(JPanel panel)
	{
		CalcPanel.removeAll();
		CalcPanel.add(panel);	
		clearOnlyCorner = true;
		this.paintAll(this.getGraphics());
		clearOnlyCorner = false;
	}
	public void SetVisualPointsVisible(boolean visible)
	{
		this.vpointsVisible = visible;
		if(visible == false)
			ClearVisualPoints();
		
		this.repaint();
	}
	public void ClearVisualPoints()
	{
		vpoints.clear();
		vmovablepoints.clear();
		this.repaint();
	}
	public boolean VisualPointsAreVisible()
	{
		return this.vpointsVisible;
	}
	public void AddVisualPoint(VisualPoint p)
	{
		vpoints.add(p);
		if(p.isMovable())
		{
			int x = (int) (((p.getPoint().getX()  - settings.getXmin()) / (settings.getXmax() - settings.getXmin()) * size.getWidth())) - 13;
			if(Double.isNaN(p.getPoint().getY())) p.setPoint(new PointXY(p.getPoint().getX(), 0.0));
			int y =  (int) size.getHeight() - (int) (((p.getPoint().getY()  - settings.getYmin()) / (settings.getYmax() - settings.getYmin()) * size.getHeight())) - 13;
			vmovablepoints.add(new Point(x, y));
		}
		
		this.repaint();
	}
	public PointXY GetMovableVisualPointLocationByLabel(String label)
	{
		for(VisualPoint vp: vpoints)
		{
			if(vp.getLabel().equals(label)) return vp.getPoint();
		}
		return null;
	}
	public void SetMovableVisualPointLocationByLabel(String label, PointXY newlocation)
	{
		int i=0;
		for(VisualPoint vp: vpoints)
		{
			if(vp.getLabel().equals(label))
			{
				vp.setPoint(newlocation);
				if(Double.isNaN(vp.getPoint().getY())) vp.setPoint(new PointXY(vp.getPoint().getX(), 0.0));
				
				int x = (int) (((vp.getPoint().getX()  - settings.getXmin()) / (settings.getXmax() - settings.getXmin()) * size.getWidth())) - 13;
				int y =  (int) size.getHeight() - (int) (((vp.getPoint().getY()  - settings.getYmin()) / (settings.getYmax() - settings.getYmin()) * size.getHeight())) - 13;
				int pointindex = GetMovableVisualPointIndex(i);
				vmovablepoints.set(pointindex, new Point(x, y));
				break; 
			}
			i++;
		}
		this.repaint();
	}
	private int GetMovableVisualPointIndex(int PointIndex)
	{
		int icount = 0;
		//System.out.println(PointIndex);
		for(int i=0; i < vpoints.size(); i++)
		{
			if(vpoints.get(i).isMovable())
			{
				if(icount == PointIndex) 
				{
					return i;
				}
				icount++;
			}
			
			
		}
		return -1;
	}
	private void TakeScreenShot(boolean clearStuff)
	{
		if(clearStuff)
		{
			this.setCalcPanelVisible(false);
			this.setCalcPanelVisible(false);
			this.SetVisualPointsVisible(false);
			this.ClearVisualPoints();
			this.SetVisualPointsVisible(false);
			this.SetFillFunction(false);
		}
		
		BufferedImage bImg = new BufferedImage((int)size.getWidth(), (int)size.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics gImg = bImg.getGraphics();
		gImg.setColor(Color.WHITE);
		gImg.clearRect(0, 0, bImg.getWidth(), bImg.getHeight());
		this.paintAll(gImg);
		
		try
		{
			JFileChooserWithConfirmation saveFile;
			if(!GraphFunctionsFrame.FilePathPresent)
				saveFile = new JFileChooserWithConfirmation(System.getProperty("user.dir"));
			else
				saveFile = new JFileChooserWithConfirmation(new File(GraphFunctionsFrame.FilePath));
			 
			FileFilter saveFilter = new ExtensionFileFilter("PNG Image", new String[] { "PNG" });
			saveFile.setFileFilter(saveFilter);
			
			int saveOption = saveFile.showSaveDialog(this);
			if(saveOption != JFileChooser.APPROVE_OPTION)
			{
				System.out.println("Canceled by user");
				return;
			}
			 
			File output;
			if(saveFile.getSelectedFile().getAbsolutePath().toLowerCase().endsWith(".png")) output = saveFile.getSelectedFile();
			else output = new File(saveFile.getSelectedFile().getAbsolutePath() + ".png");
			
			ImageIO.write(bImg,"png", output);
			System.out.println("Done");
			JOptionPane.showMessageDialog(this, GraphFunctionsFrame.Translate("message.screenshotsaved"), "Graph", JOptionPane.INFORMATION_MESSAGE);

		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		//System.out.println(e.getPoint());
		if(this.MovingVPointIndex == -1 || this.vpoints.size() == 0) return;
		int deltax = (int) ( MouseStart.getX() -e.getPoint().getX() );
		//System.out.println(deltax);
		//return;
		
		double add = (deltax / size.getWidth()) * (settings.getXmax() - settings.getXmin()) * -1 * 0.5;
		
		VisualPoint vp = this.vpoints.get(MovingVPointIndex);
		Point p = this.vmovablepoints.get(MovingPointIndex);
		if(vp.getFunctionIndex() != -1)
		{
			if(Double.isNaN(vp.getPoint().getY())) vp.setPoint(new PointXY(vp.getPoint().getX(), 0.0));
			
			if(vp.getFunctionIndex() < functions.size())
			{
				Function f = this.functions.get(vp.getFunctionIndex());
				vp.setPoint(new PointXY(vp.getPoint().getX() + add, f.Calc(vp.getPoint().getX() + add)));
			
				int y =  (int) size.getHeight() - (int) (((vp.getPoint().getY()  - settings.getYmin()) / (settings.getYmax() - settings.getYmin()) * size.getHeight())) - 13;
				p.setLocation(p.getX() + deltax , y);
			} 
			else
			{
				vp.setPoint(new PointXY(vp.getPoint().getX() + add, vp.getPoint().getY()));
				p.setLocation(p.getX() + deltax , p.getY());
			}
		}
		else
		{
			vp.setPoint(new PointXY(vp.getPoint().getX() + add, vp.getPoint().getY()));
			p.setLocation(p.getX() + deltax , p.getY());
		}
		
		
		 
		
		MouseStart = e.getPoint();
		
		this.repaint();
		
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		int i=0;
		MouseStart = e.getPoint();
		for(Point p: vmovablepoints)
		{
			if(e.getPoint().getX() < p.getX() || e.getPoint().getX() > p.getX() + 25  || e.getPoint().getY() < p.getY() || e.getPoint().getY() > p.getY() + 25)
			{
				i++;
				continue;
			}
			MovingPointIndex = i;
			MovingVPointIndex = GetMovableVisualPointIndex(i);
			MouseStart = e.getPoint();	
			return;
		}
			
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		for(Point p: vmovablepoints)
		{
			if(e.getPoint().getX() < p.getX() || e.getPoint().getX() > p.getX() + 25  || e.getPoint().getY() < p.getY() || e.getPoint().getY() > p.getY() + 25)
			{
				continue;
				
			}
			setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR)); 
			return;
		}
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)); 
	
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(((JMenuItem) e.getSource()).getName().equalsIgnoreCase("screenshot"))
		{
			System.out.println("Taking screenshot");
			TakeScreenShot(false);
		}
		
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		if(CalcPanelVisible)
			((CalculateFrame) this.CalcPanel.getComponent(0)).Update();
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		MovingVPointIndex = -1;
		MovingPointIndex = -1;
		MouseStart = null;
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	
}
