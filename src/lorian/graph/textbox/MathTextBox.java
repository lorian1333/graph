package lorian.graph.textbox;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.PrintStream;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import lorian.graph.function.Util;

public class MathTextBox extends JPanel
  implements FocusListener, MouseListener, KeyListener
{
  private static final long serialVersionUID = -8728154029064099554L;
  private String text;
  private boolean listenToKeyboard = false;
  private boolean paintCursor = true;
  private int index = 3;
  private java.util.Timer t;
  
  public MathTextBox(String text) {
    this.text = text;
    this.setSize(new Dimension(100, 30));
    this.setPreferredSize(getSize());
    this.setFocusable(true);
    this.addMouseListener(this);
    this.addFocusListener(this);
    this.addKeyListener(this);
   
    t = new Timer();
    t.scheduleAtFixedRate(new TimerTask() {
    	@Override
    	public void run()
    	{
    		onTick();
    	}
    }, 485, 485);
    
    /*
    t = new Timer(485);
    t.addTickListener(this);
    t.start();
    */
  }

  private void resetTimer()
  {
	  t.cancel();
	  t = new Timer();
	    t.scheduleAtFixedRate(new TimerTask() {
	    	@Override
	    	public void run()
	    	{
	    		onTick();
	    	}
	    }, 485, 485);
  }
  public void paintComponent(Graphics g)
  {
	 ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.clearRect(0, 0, getWidth(), getHeight());
    g.setColor(Color.BLACK);
    g.setFont(g.getFont().deriveFont(15.0f));
    int y = getHeight() / 2 + g.getFontMetrics().getHeight() / 3;
    g.drawString(this.text, 5, y);
    if(paintCursor && listenToKeyboard)
    {
    	int x = Util.getStringWidth(g.getFontMetrics(), text.substring(0, index+1));
    	
    	Graphics2D g2d = ((Graphics2D)g);
    	//g2d.setStroke(new BasicStroke(.5f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL));
    	g.drawLine(x, y+3, x, y-15);
    }
  }
  
  public static void main(String[] args)
  {
	  JFrame frame = new JFrame("Test frame");
	  frame.setPreferredSize(new Dimension(100, 100));
	  frame.setSize(frame.getPreferredSize());
	  frame.setLayout(new GridLayout(0, 1));
	  
	  MathTextBox textbox = new MathTextBox("test");
	  JTextField txt = new JTextField("textfield");
	  txt.setFont(txt.getFont().deriveFont(15.0f));
	
	 // frame.setLayout(new SpringLayout());
	  frame.add(textbox);
	  frame.add(txt);
	  frame.setLocationRelativeTo(null);
	  frame.pack();
	  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	  frame.setVisible(true);
			 
  }
  @Override
  public void focusGained(FocusEvent e)
  {
	if(listenToKeyboard) return;
    this.listenToKeyboard = true;
    this.paintCursor = true;
    this.repaint();
    System.out.println("focusGained");
  }
  @Override
  public void focusLost(FocusEvent e)
  {
	if(!listenToKeyboard) return;
    this.listenToKeyboard = false;
    this.paintCursor = false;
   // t.stop();
    this.repaint();
    System.out.println("focusLost");
  }

  public void onTick()
  {
	  paintCursor = !paintCursor;
	  this.repaint();
  }
  @Override
  public void mouseClicked(MouseEvent e) {
	  this.requestFocus();
  }

	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		//System.out.println(e.getKeyCode());
		if(e.getKeyCode() == 37) 
		{
			if(index>0) index--;
			//t.reset();
		}
		else if(e.getKeyCode() == 39)
		{
			if(index<text.length()-1) index++;
			//t.reset();
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}