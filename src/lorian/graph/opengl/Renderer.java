package lorian.graph.opengl;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import lorian.graph.GraphFunctionsFrame;
import lorian.graph.WindowSettings3D;
import lorian.graph.function.Function2Var;
import lorian.graph.function.Util;

import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.gl2.GLUT;




class Renderer implements GLEventListener {
	private float fieldOfView = 45f;
	private float near = 1.0f;
	private float far = 1500.0f;

	private float camXRot = 17f, camYRot;
	private float originXRot = 14f, originYRot = 120f;
	private float camXSpeed, camYSpeed, camZSpeed;
	private float camXPos = 0f, camYPos = 1.5f, camZPos = 5.0f;

	private int Xlength = 100,  Ylength = 100,  Zlength = 100;
	//private int Xlength = 140,  Ylength = 200,  Zlength = 70;
	// OpenGL Y = Graph Z and OpenGL Z = Graph Y
	
	private double zoomLevel = 0.1;
	private final double coneLength = 10;
	
	private int width, height;
	
	//private List<Function2Var> functions;
	private WindowSettings3D settings;
	private List<FunctionRenderData> renderdata;
	
	private boolean initedPosition = false;
	public Renderer()
	{
		super();
		//functions = new ArrayList<Function2Var>();
		settings = new WindowSettings3D(-10, 10, -10, 10, -10, 10, false);
		renderdata = new ArrayList<FunctionRenderData>();
	}
	private void initGL(GLAutoDrawable gLDrawable, int width, int height) {
		this.width = width;
		this.height = height;
				
		final GL2 gl = gLDrawable.getGL().getGL2();
		gl.glViewport(0, 0, width, height);

		gl.glMatrixMode(GL2.GL_PROJECTION); // Select The Projection Matrix
		gl.glLoadIdentity();

		float aspectRatio = (width > height) ? (float) (width) / (float) (height) : (float) (height) / (float) (width);
		float fH = (float) (Math.tan((float) (fieldOfView / 360.0f * Math.PI)) * near);
		float fW = fH * aspectRatio;
		gl.glFrustum(-fW, fW, -fH, fH, near, far);

		gl.glMatrixMode(GL2.GL_MODELVIEW); // Select The Modelview Matrix
		gl.glLoadIdentity();
		gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f); // White Background
		gl.glShadeModel(GL2.GL_SMOOTH); // Enable Smooth Shading
		gl.glEnable(GL2.GL_DEPTH_TEST); // Enables Depth Testing
		gl.glClearDepth(1.0f); // Depth Buffer Setup
		gl.glDepthFunc(GL2.GL_LEQUAL); // The Type Of Depth Testing To Do

		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
		
		//gl.glEnable(GL2.GL_CULL_FACE);
		gl.glEnable(GL2.GL_BLEND);
		gl.glEnable(GL2.GL_LINE_SMOOTH);
		gl.glEnable(GL2.GL_POLYGON_SMOOTH);
		gl.glEnable(GL2.GL_POINT_SMOOTH);
		gl.glHint(GL2.GL_POINT_SMOOTH_HINT, GL2.GL_NICEST);
		gl.glHint(GL2.GL_LINE_SMOOTH_HINT, GL2.GL_NICEST);
		gl.glHint(GL2.GL_POLYGON_SMOOTH_HINT, GL2.GL_NICEST);
		//gl.glEnable(GL2.GL_MULTISAMPLE);
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

		
		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		

		
		if(!initedPosition)
		{
			handleMouseWheelInput(60);
			initedPosition = true;
		}
	}

	public void drawAxes(GL2 gl) {
		gl.glPushMatrix();
		gl.glLineWidth(2f);
		gl.glTranslated(Xlength * 0.5, -Zlength * 0.5, -Ylength * 0.5);
		gl.glBegin(GL2.GL_LINES);
		
			gl.glColor3d(60.0/255.0, 1.0, 0.0); // Light green
			
			// Y Axis
			gl.glVertex3i(0, 0, 0);
			gl.glVertex3d(0, 0, Ylength - coneLength);
			
			gl.glColor3d(0, 54.0/255.0, 1.0); // Blue
			
			// Z Axis
			gl.glVertex3i(0, 0, 0);
			gl.glVertex3d(0, Zlength - coneLength, 0);
			
			gl.glColor3d(220.0/255.0, 0, 0); // Red
			
			// X Axis
			gl.glVertex3i(0, 0, 0);
			gl.glVertex3d(-(Xlength - coneLength), 0, 0);

			
			gl.glColor3d(0.5, 0.5, 0.5);
			
			gl.glVertex3i(-Xlength, 0, 0);
			gl.glVertex3i(-Xlength, 0, Ylength);
						
			gl.glVertex3i(-Xlength, 0, 0);
			gl.glVertex3i(-Xlength, Zlength, 0);
										
			gl.glVertex3i(0, Zlength, 0);
			gl.glVertex3i(-Xlength, Zlength, 0);
			
			gl.glVertex3i(0, 0, Ylength);
			gl.glVertex3i(-Xlength, 0, Ylength);
			
			gl.glVertex3i(-Xlength, 0, Ylength);
			gl.glVertex3i(-Xlength, Zlength, Ylength);
			
			gl.glVertex3i(0, 0, Ylength);
			gl.glVertex3i(0, Zlength, Ylength);
			
			gl.glVertex3i(0, Zlength, 0);
			gl.glVertex3i(0, Zlength, Ylength);
			
			gl.glVertex3i(-Xlength, Zlength, 0);
			gl.glVertex3i(-Xlength, Zlength, Ylength);
			
			gl.glVertex3i(0, Zlength, Ylength);
			gl.glVertex3i(-Xlength,  Zlength, Ylength);
			
		gl.glEnd();
		gl.glPopMatrix();
	}
	
	private void DrawCone(GL2 gl, int res, double radius, double height)
	{
		double d = ((2 * Math.PI) / res) ;
		double a = 0;
		//gl.glBegin(GL2.GL_TRIANGLE_FAN);
		gl.glBegin(GL2.GL_POLYGON);
		
		gl.glVertex3d(0.0, 0.0, 0.0);
		for (int i = 0 ; i <= res ; i++ )
		{
			gl.glVertex3d(radius*Math.cos(a), radius * Math.sin(a), 0.0);
			a += d ; 
		}
		gl.glEnd() ;
		a = 0.0 ;
		
		
		//gl.glBegin(GL2.GL_TRIANGLE_FAN);
		gl.glBegin(GL2.GL_POLYGON);
		
		gl.glVertex3d(0.0, 0.0, height);
		for (int i = 0 ; i <= res ; i++ )
		{
			gl.glVertex3d(radius*Math.cos(a), radius*Math.sin(a), 0.0);
			a += d ;
		}
		gl.glEnd();
		
	}
	
	private void drawAxisCones(GL2 gl)
	{
		gl.glPushMatrix();
		gl.glColor3d(220.0/255.0, 0, 0); // Red
		gl.glRotated(-90.0, 0.0, 1.0, 0.0);
		gl.glTranslated(0.5 * -Ylength, 0.5 * -Zlength, (0.5 * Xlength - coneLength));
		DrawCone(gl, 100, 3, coneLength);
		//gl.glTranslated(0.5 * Ylength, 0.5 * Zlength, -(0.5 * Xlength - coneLength));
		//gl.glRotated(90.0, 0.0, 1.0, 0.0);
		gl.glPopMatrix();
		
		
		gl.glPushMatrix();
		gl.glColor3d(60.0/255.0, 1.0, 0.0); // Light green
		gl.glTranslated(0.5 * Xlength, 0.5 * -Zlength, 0.5 * Ylength - coneLength);
		DrawCone(gl, 100, 3, coneLength);
		//gl.glTranslated(-0.5 * Xlength, 0.5 * Zlength, -( 0.5 * Ylength - coneLength));
		gl.glPopMatrix();
		
		gl.glPushMatrix();
		gl.glColor3d(0, 54.0/255.0, 1.0); // Blue
		gl.glRotated(-90.0, 1.0, 0.0, 0.0);
		gl.glTranslated(0.5 * Xlength, 0.5 * Ylength, (0.5 * Zlength - coneLength));
		DrawCone(gl, 100, 3, coneLength);
		//gl.glTranslated(-0.5 * Xlength, -0.5 * Ylength, -(0.5 * Zlength - coneLength));
		//gl.glRotated(90.0, 0.0, 1.0, 0.0);
		gl.glPopMatrix();
	}
	/*
	private void drawAxisNames(GL2 gl)
	{
		TextRenderer renderer = new TextRenderer(new Font("SansSerif", 0, 10));
		renderer.begin3DRendering();
		
		
		renderer.setColor(new Color(0, 54, 0xff)); // Blue
		
		renderer.draw("Z", 0, 0);
		
		renderer.setColor(new Color(220, 0, 0)); // Red
		renderer.draw("X", 0, 10);
		
		renderer.setColor(new Color(60, 0xff, 0)); // Light green
		renderer.draw("Y", 0, 20);
		
		renderer.end3DRendering();
		
	}
	private void drawRect(GL2 gl, Point3D p1, Point3D p2, Point3D p3, Point3D p4)
	{
		gl.glBegin(GL2.GL_POLYGON);
		//gl.glBegin(GL2.GL_LINES);
			gl.glVertex3d(p1.getX(), p1.getY(), p1.getZ());
			gl.glVertex3d(p2.getX(), p2.getY(), p2.getZ());
			gl.glVertex3d(p4.getX(), p4.getY(), p4.getZ());
			gl.glVertex3d(p3.getX(), p3.getY(), p3.getZ());
		gl.glEnd();
	}
	*/
	private void addFunctionToArray(Function2Var f)
	{
		FunctionRenderData fdata = new FunctionRenderData();
		fdata.color = f.getColor();
		fdata.data = new float[Xlength * Ylength * 3 / 2 ];
		
		int xpix, ypix, zpix;
		double x, y, z;
		
		// Use 2 pixels instead of 1 per step makes the graph more accurate
		double stepX = ((double) (settings.getXmax() - settings.getXmin())) / (Xlength / 2);
		double stepY = ((double) (settings.getYmax() - settings.getYmin())) / (Ylength / 2);
		int i=0;
		for(xpix = -1, x = settings.getXmin(); xpix < Xlength; xpix+=2, x += stepX)
		{
			for(ypix = -1, y = settings.getYmin(); ypix < Ylength; ypix+=2, y += stepY)
			{
				z = f.Calc( settings.getXmin() + settings.getXmax()-x , y);
				
				if(Double.isNaN(z))
				{
					continue;
				}
				else
				{
					
				}
				zpix = Zlength - (int) ((settings.getZmax() - z) * (Zlength / (settings.getZmax() - settings.getZmin())));
				if(xpix > -1 && ypix > -1 && zpix >= 0 && zpix <= Zlength)
				{
					/*
					gl.glBegin(GL2.GL_POINTS);
						gl.glVertex3d(xpix, zpix, ypix);
					gl.glEnd();
						*/	
					fdata.data[i++] = xpix;
					fdata.data[i++] = zpix;
					fdata.data[i++] = ypix;
							
							/*
							previous1.setLocation(xpix, Zlength - (int) ((settings.getZmax() - f.Calc(x, y-stepY)) * (Zlength / (settings.getZmax() - settings.getZmin()))), ypix-1);
							previous2.setLocation(xpix-1, Zlength - (int) ((settings.getZmax() - f.Calc(x-stepX, y)) * (Zlength / (settings.getZmax() - settings.getZmin()))), ypix);
							previous3.setLocation(xpix-1, Zlength - (int) ((settings.getZmax() - f.Calc(x-stepX, y-stepY)) * (Zlength / (settings.getZmax() - settings.getZmin()))), ypix-1);
							drawRect(gl, new Point3D(xpix, zpix, ypix), previous1, previous2, previous3);
							*/
				}
				
			}
					
					
		}
		renderdata.add(fdata);
	}
	private void drawAllFunctions(GL2 gl)
	{
		
		gl.glPushMatrix();
		gl.glTranslated(Xlength * -0.5 + 1, Zlength * -0.5, Ylength * -0.5 + 1);
		gl.glPointSize(5.0f);
		
		for(FunctionRenderData f: renderdata)
		{
			
			gl.glColor3d(f.color.getRed() / 255.0, f.color.getGreen() / 255.0, f.color.getBlue() / 255.0);
			gl.glVertexPointer(3, GL2.GL_FLOAT, 0, Util.toFloatBuffer(f.data));
			gl.glDrawArrays(GL2.GL_POINTS, 0, f.data.length / 3); 
			
		}
		gl.glPopMatrix();
		
	}
	private void drawFunction(GL2 gl, Function2Var f)
	{
		if(f.isEmpty()) return;	
		gl.glPushMatrix();
		gl.glTranslated(Xlength * -0.5 + 1, Zlength * -0.5, Ylength * -0.5 + 1);
		gl.glPointSize(5.0f);
		
		Color color = f.getColor();
		gl.glColor3d(color.getRed() / 255.0, color.getGreen() / 255.0, color.getBlue() / 255.0);
		
		int xpix, ypix, zpix;
		double x, y, z;
		boolean inNaN = false, WaitForRealNumber = false;
		Point3D previous1 = new Point3D();
		Point3D previous2 = new Point3D();
		Point3D previous3 = new Point3D();
		
		//int Xlength = this.Xlength;
		//int Ylength = this.Ylength;
		//int Zlength = this.Zlength;
		
		double stepX = ((double) (settings.getXmax() - settings.getXmin())) / Xlength;
		double stepY = ((double) (settings.getYmax() - settings.getYmin())) / Ylength;
		Random rand = new Random(System.currentTimeMillis());
		
		//gl.glColor3d(rand.nextDouble(), rand.nextDouble(), rand.nextDouble());
		for(xpix = -1, x = settings.getXmin(); xpix < Xlength; xpix++, x += stepX)
		{
			for(ypix = -1, y = settings.getYmin(); ypix < Ylength; ypix++, y += stepY)
			{
				z = f.Calc(x, y);
				
				if(Double.isNaN(z))
				{
					continue;
				}
				else
				{
					
				}
				
				zpix = Zlength - (int) ((settings.getZmax() - z) * (Zlength / (settings.getZmax() - settings.getZmin())));
				if(xpix > -1 && ypix > -1 && zpix >= 0 && zpix <= Zlength)
				{
					
					gl.glBegin(GL2.GL_POINTS);
						gl.glVertex3d(xpix, zpix, ypix);
					gl.glEnd();
					
					
					/*
					previous1.setLocation(xpix, Zlength - (int) ((settings.getZmax() - f.Calc(x, y-stepY)) * (Zlength / (settings.getZmax() - settings.getZmin()))), ypix-1);
					previous2.setLocation(xpix-1, Zlength - (int) ((settings.getZmax() - f.Calc(x-stepX, y)) * (Zlength / (settings.getZmax() - settings.getZmin()))), ypix);
					previous3.setLocation(xpix-1, Zlength - (int) ((settings.getZmax() - f.Calc(x-stepX, y-stepY)) * (Zlength / (settings.getZmax() - settings.getZmin()))), ypix-1);
					drawRect(gl, new Point3D(xpix, zpix, ypix), previous1, previous2, previous3);
					*/
				}
				
			}
			
			
		}
		gl.glPopMatrix();
		
	}
	public void display(GLAutoDrawable gLDrawable) {
		// System.out.println("display() called");

		final GL2 gl = gLDrawable.getGL().getGL2();
		
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT); 
		gl.glLoadIdentity();

		gl.glRotatef(camXRot, 1.0f, 0.0f, 0f);
		gl.glRotatef(camYRot, 0.0f, 1.0f, 0.0f);
		gl.glTranslatef(-camXPos, -camYPos, -camZPos);
		gl.glRotatef(originXRot, 1.0f, 0.0f, 0f);
		gl.glRotatef(originYRot, 0.0f, 1.0f, 0.0f);
		/*
		for(Function2Var f: functions)
		{
			if(!f.isEmpty())
				//drawFunction(gl, f);
				addFunctionToArray(f);
		}
		*/
		drawAllFunctions(gl);
		
		drawAxisCones(gl);
		drawAxes(gl);
		//drawAxisNames(gl);
		
	
	}

	public void displayChanged(GLAutoDrawable gLDrawable, boolean modeChanged, boolean deviceChanged) {
		//System.out.println("displayChanged called");
	}

	public void init(GLAutoDrawable gLDrawable) {
		//System.out.println("init() called");
		GL2 gl = gLDrawable.getGL().getGL2();
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		gl.glShadeModel(GL2.GL_SMOOTH);
	}

	public void reshape(GLAutoDrawable gLDrawable, int x, int y, int width, int height) {
		//System.out.println("reshape() called: x = " + x + ", y = " + y + ", width = " + width + ", height = " + height);
		initGL(gLDrawable, width, height);

	}

	public void dispose(GLAutoDrawable arg0) {
		//System.out.println("dispose() called");
	}
	public void Update(List<Function2Var> functions)
	{
		//this.functions = functions;
		renderdata.clear();
		for(Function2Var f: functions)
		{
			if(!f.isEmpty())
				addFunctionToArray(f);
		}
	}
	public void UpdateWindowSettings(WindowSettings3D windowsettings)
	{
		this.settings = windowsettings;
	}
	public void handleMouseInputLeftClick(int mouseDX, int mouseDY)
	{
		
		float vertMouseSensitivity = 5.0f;
		float horizMouseSensitivity = 5.0f;

		originXRot -= mouseDY / vertMouseSensitivity;
		originYRot -= mouseDX / horizMouseSensitivity;

		if (originXRot < -90.0f)
			originXRot = -90f;

		if (originXRot > 90f)
			originXRot = 90f;
		
		
		
		//System.out.println(originYRot);
	}
	public void handleMouseInputRightClick(int mouseDX, int mouseDY) {
		float vertMouseSensitivity = 5.0f;
		float horizMouseSensitivity = 5.0f;

		camXRot += mouseDY / vertMouseSensitivity;
		camYRot += mouseDX / horizMouseSensitivity;

		if (camXRot < -90.0f)
			camXRot = -90f;

		if (camXRot > 90f)
			camXRot = 90f;
		

	}
	private void moveCamera(int delta)
	{
		camXPos += delta * camXSpeed;
		camYPos += delta * camYSpeed;
		camZPos += delta * camZSpeed;
	
	}

	
	public void handleMouseWheelInput(double amount) {
		
		float camMovementXComponent = 0f;
		float camMovementYComponent = 0f;
		float camMovementZComponent = 0f;

		float movementSpeedFactor = (float) Math.abs(amount);//.1f;
		if (amount < 0) {
			
			if(zoomLevel >= 0)
			{
				movementSpeedFactor -= (float) Math.abs(amount);
				//return;
			}
			else
			{
				zoomLevel += (-amount);
			}
			
			double pitchFactor = Math.cos(Math.toRadians(camXRot));
			camMovementXComponent += (movementSpeedFactor * (float) (Math.sin(Math.toRadians(camYRot)))) * pitchFactor;
			camMovementYComponent += movementSpeedFactor * (float) (Math.sin(Math.toRadians(camXRot))) * -1.0f;
			float yawFactor = (float) (Math.cos(Math.toRadians(camXRot)));
			camMovementZComponent += (movementSpeedFactor * (float) (Math.cos(Math.toRadians(camYRot))) * -1.0f) * yawFactor;
			
			
			
			
		} else if (amount > 0) {
			double pitchFactor = Math.cos(Math.toRadians(camXRot));
			camMovementXComponent += (movementSpeedFactor * (float) (Math.sin(Math.toRadians(camYRot))) * -1.0f) * pitchFactor;
			camMovementYComponent += movementSpeedFactor * (float) (Math.sin(Math.toRadians(camXRot)));
			float yawFactor = (float) (Math.cos(Math.toRadians(camXRot)));
			camMovementZComponent += (movementSpeedFactor * (float) (Math.cos(Math.toRadians(camYRot)))) * yawFactor;
			zoomLevel -= amount;
			
		}
		//System.out.println(zoomLevel);
		camXSpeed = camMovementXComponent;
		camYSpeed = camMovementYComponent;
		camZSpeed = camMovementZComponent;

		if (camXSpeed > movementSpeedFactor) {
			camXSpeed = movementSpeedFactor;
		}
		if (camXSpeed < -movementSpeedFactor) {
			camXSpeed = -movementSpeedFactor;
		}

		if (camYSpeed > movementSpeedFactor) {
			camYSpeed = movementSpeedFactor;
		}
		if (camYSpeed < -movementSpeedFactor) {
			camYSpeed = -movementSpeedFactor;
		}

		if (camZSpeed > movementSpeedFactor) {
			camZSpeed = movementSpeedFactor;
		}
		if (camZSpeed < -movementSpeedFactor) {
			camZSpeed = -movementSpeedFactor;
		}
		
		moveCamera(5);

	}
}