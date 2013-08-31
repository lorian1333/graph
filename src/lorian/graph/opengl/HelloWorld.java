package lorian.graph.opengl;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.MouseMotionListener;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
 
public class HelloWorld extends JFrame implements MouseMotionListener, MouseWheelListener
{
	private static final long serialVersionUID = -6458866625249891155L;
	
	
	private final int WIDTH = 800, HEIGHT = 800;
	
	private Point oldmouselocation;
	private Renderer renderer;
	private GLCanvas glcanvas;
	public HelloWorld()
	{
		// setup OpenGL Version 2
    	GLProfile profile = GLProfile.get(GLProfile.GL2);
    	GLCapabilities capabilities = new GLCapabilities(profile);
 
    	// The canvas is the widget that's drawn in the JFrame
    	//glcanvas = new GLCanvas(capabilities);
    	glcanvas = new GLCanvas();
    	renderer = new Renderer();
    	glcanvas.addGLEventListener(renderer);
    	glcanvas.addMouseMotionListener(this);
    	glcanvas.addMouseWheelListener(this);
    	glcanvas.setSize(WIDTH, HEIGHT);
    
    	
    	

        this.setTitle( "Hello World" );
        this.add( glcanvas);
        
        // shutdown the program on windows close event
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
                System.exit(0);
            }
        });
 
        this.setSize(this.getContentPane().getPreferredSize() );
        this.setLocationRelativeTo(null);
        this.setVisible( true );
        if(glcanvas.getMousePosition() != null)
        	oldmouselocation = new Point(glcanvas.getMousePosition());
        else 
        	oldmouselocation = new Point(0, 0);
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
		glcanvas.repaint();
	}
	
	@Override
	public void mouseMoved(MouseEvent e)
	{
		oldmouselocation = e.getPoint();
	}
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		
		renderer.handleMouseWheelInput(e.getPreciseWheelRotation());
		glcanvas.repaint();
	}
    public static void main(String[] args) 
    {
    	new HelloWorld();
    }

	
}