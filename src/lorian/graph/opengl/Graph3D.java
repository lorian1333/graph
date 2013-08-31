package lorian.graph.opengl;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.List;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.SwingUtilities;
import lorian.graph.WindowSettings3D;
import lorian.graph.function.Function2Var;

public class Graph3D extends GLCanvas implements MouseMotionListener, MouseWheelListener {
	private static final long serialVersionUID = 3444564625810716017L;

	
	private Point oldmouselocation;
	private Renderer renderer;
	//private List<Function2Var> functions;
	//private WindowSettings3D settings;
	
	public Graph3D(int width, int height)
	{
		super(new GLCapabilities(GLProfile.get(GLProfile.GL2)));

    	renderer = new Renderer();
    	this.addGLEventListener(renderer);
    	this.addMouseMotionListener(this);
    	this.addMouseWheelListener(this);
    	this.setSize(width, height);

        if(this.getMousePosition() != null)
        	oldmouselocation = new Point(this.getMousePosition());
        else 
        	oldmouselocation = new Point(0, 0);
	}
	
	public void Update(List<Function2Var> functions)
	{
		//this.functions = functions;
		renderer.Update(functions);
		this.repaint();
	}
	
	public void UpdateWindowSettings(WindowSettings3D windowsettings)
	{
		//this.settings = windowsettings;
		renderer.UpdateWindowSettings(windowsettings);
	}
	
	@Override
	public void mouseDragged(MouseEvent e)
	{
		int MouseDX = (int) (oldmouselocation.getX() - e.getPoint().getX());
		int MouseDY = (int) (oldmouselocation.getY() - e.getPoint().getY());
		oldmouselocation = e.getPoint();
		if(SwingUtilities.isLeftMouseButton(e))
			renderer.handleMouseInputLeftClick(MouseDX, MouseDY);
		else if(SwingUtilities.isRightMouseButton(e))
			renderer.handleMouseInputRightClick(MouseDX, MouseDY);
		this.repaint();
	}
	
	@Override
	public void mouseMoved(MouseEvent e)
	{
		oldmouselocation = e.getPoint();
	}
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		
		renderer.handleMouseWheelInput(e.getPreciseWheelRotation());
		this.repaint();
	}
	
}
