package lorian.graph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;


public class ParseResultIcon extends JPanel {
	private static final long serialVersionUID = 726131162085481022L;
	private Image ok, error;
	public enum State
	{
		OK, ERROR, EMPTY
	};
	private State state;
	public ParseResultIcon()
	{
		this.setPreferredSize(new Dimension(24, 24));
		this.setOpaque(false);
		this.setBackground(new Color(0, 0, 0, 0)); 

		try {
			ok = ImageIO.read(getClass().getResource("/res/ok.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			error = ImageIO.read(getClass().getResource("/res/error.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void setState(State s)
	{
		this.state = s;
		this.repaint();
	}
	public State getState()
	{
		return this.state;
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponents(g);

		if(state == State.OK)
		{
			g.drawImage(ok, 4, 4, null);
		}
		else if(state == State.ERROR)
		{
			g.drawImage(error, 4, 4, null);
		}
		
		
	}
	
}
