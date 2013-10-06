package lorian.graph;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class GraphAboutDialog extends Dialog implements SelectionListener {

	private Shell shell, licenseShell, creditsShell;
	
	public GraphAboutDialog(Shell parent, int style)
	{
		super(parent, style);
	}
	public GraphAboutDialog(Shell parent) {
		this(parent, 0);
	}
	private static boolean isopen = false;
	
	private static Image loadImage(int width, int height, String path, boolean inJar)
	{ 
	    Image newImage = null;

	    try
	    {
	        if(inJar)
	        {
	            newImage = new Image(null, GraphFunctionsFrame.class.getResourceAsStream(path));
	        }
	        else
	        {
	            newImage = new Image(null, path);
	        }
	    }
	    catch (IllegalArgumentException e)
	    {
	    	System.err.println("Couldn't find " + path);
		    return null;
	    }
	    catch(SWTException e)
	    {
	        System.err.println("Couldn't find " + path);
	        return null;
	    }


	    Image scaledImage = new Image(null, newImage.getImageData().scaledTo(width, height)); 
	    return scaledImage;
	}
	public void open()
	{
		Shell parent = getParent();
		if(isopen) return;  
		isopen = true;   
		
		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);    
		
		shell.setImages(parent.getImages());
		if(GraphFunctionsFrame.OS == GraphFunctionsFrame.OperatingSystem.OS_WINDOWS)  
			shell.setSize(425, 295);
			//shell.setSize(400, 315);
		else
			shell.setSize(425, 305);
		

		
		if(GraphFunctionsFrame.OS == GraphFunctionsFrame.OperatingSystem.OS_MAC || GraphFunctionsFrame.OS == GraphFunctionsFrame.OperatingSystem.OS_WINDOWS)
		{
			java.awt.Rectangle clientArea = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0].getDefaultConfiguration().getBounds();
			Rectangle rect = shell.getBounds();
			int x = clientArea.x + (clientArea.width  - rect.width) / 2;
			int y = clientArea.y + (clientArea.height - rect.height) / 2;
			shell.setLocation(x, y);
		}
		
		shell.setText(String.format(GraphFunctionsFrame.Translate("about.title"), "Graph"));
		
		GridLayout gridlayout = new GridLayout();
		gridlayout.numColumns = 1;
		gridlayout.marginTop = 20;
		gridlayout.marginBottom = 5;
		gridlayout.marginLeft = 10;
		gridlayout.marginRight = 10;
		//gridlayout.verticalSpacing = 15;
		shell.setLayout(gridlayout);
		
		Image program_icon = loadImage(64, 64, "/res/icon64.png", true);
		
		Label program_icon_label = new Label(shell, SWT.NONE);
		program_icon_label.setImage(program_icon); 
		program_icon_label.setSize(48, 48);
		program_icon_label.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false));
		
		Label name = new Label(shell, SWT.NONE);
		name.setText("\nGraph\n");	
		Font bold = new Font(parent.getDisplay(), new FontData(name.getFont().getFontData()[0].getName(), name.getFont().getFontData()[0].getHeight(), SWT.BOLD));
		name.setFont(bold);
		name.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false));
		
		if(GraphFunctionsFrame.OS == GraphFunctionsFrame.OperatingSystem.OS_WINDOWS)
			new Label(shell, SWT.NONE).setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false)); 
		
		Label version = new Label(shell, SWT.NONE); 
		version.setText(GraphFunctionsFrame.version + "\n");
		version.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false));
		
		if(GraphFunctionsFrame.OS == GraphFunctionsFrame.OperatingSystem.OS_WINDOWS)
			new Label(shell, SWT.NONE).setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false)); 
		
		String description_s = GraphFunctionsFrame.Translate("about.description");
		if(description_s.contains("\n"))
		{
			
			String[] lines = description_s.split("\n");
			for(String line: lines)
			{
				Label description = new Label(shell, SWT.NONE);
				description.setText(line); 
				description.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false));
			}
		}
		else
		{
			Label description = new Label(shell, SWT.NONE);
			description.setText(description_s); 
			description.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false));
		}
		
		Label copyright = new Label(shell, SWT.NONE);
		int smallfont_size = copyright.getFont().getFontData()[0].getHeight() - 1;
		if(GraphFunctionsFrame.OS == GraphFunctionsFrame.OperatingSystem.OS_WINDOWS)
		{
			smallfont_size = copyright.getFont().getFontData()[0].getHeight() - 2;
		}
		Font small =new Font(parent.getDisplay(), new FontData(copyright.getFont().getFontData()[0].getName(), smallfont_size, SWT.NONE));
		copyright.setFont(small);
		copyright.setText(String.format("Copyright %c 2013 Lorian Coltof", 0xA9));
		copyright.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false));
		
		
		
		String buttonname_spacer = "   "; 
		if(GraphFunctionsFrame.OS == GraphFunctionsFrame.OperatingSystem.OS_LINUX)
				buttonname_spacer = "    ";
		
		Composite buttons = new Composite(shell, SWT.NONE);
		buttons.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, true));
		
		RowLayout layout = new RowLayout(SWT.HORIZONTAL);
		layout.wrap = true;
		layout.fill = false;
		layout.justify = true;
		buttons.setLayout(layout);

		Button credits = new Button(buttons, SWT.PUSH);
		credits.setText(buttonname_spacer + GraphFunctionsFrame.Translate("about.credits") + buttonname_spacer);
		credits.setData("credits");
		credits.addSelectionListener(this);
		
		Button license = new Button(buttons, SWT.PUSH);
		license.setText(buttonname_spacer + GraphFunctionsFrame.Translate("about.license") + buttonname_spacer);
		license.setData("license");
		license.addSelectionListener(this);
		
		if(GraphFunctionsFrame.OS != GraphFunctionsFrame.OperatingSystem.OS_MAC)
		{
			Label spacer = new Label(buttons, SWT.NONE); 
			spacer.setVisible(false);
			RowData r = new RowData();
			r.width = 120;
			spacer.setLayoutData(r);
		}
		
		Button close = new Button(buttons, SWT.PUSH | SWT.RIGHT_TO_LEFT);
		close.setText(buttonname_spacer + GraphFunctionsFrame.Translate("buttons.close") + buttonname_spacer + " ");
		close.addSelectionListener(new SelectionAdapter() {
			 @Override
			 public void widgetSelected(SelectionEvent e) {
				 shell.dispose();
			 }
		});
			
		close.forceFocus();
		
		if(GraphFunctionsFrame.OS == GraphFunctionsFrame.OperatingSystem.OS_WINDOWS)
			shell.pack();
		
		shell.open();
		shell.forceActive();
		while(!shell.isDisposed()) { 
			if (!parent.getDisplay().readAndDispatch()) parent.getDisplay().sleep(); 
			} 
		isopen = false;
	}
	
	public static void open_swing(Point mainLocation, Dimension mainSize)
	{
		// TODO Just a temporary solution
		Display d = new Display();
		Shell parent = new Shell(d);
		parent.setLocation(mainLocation.x, mainLocation.y );
		parent.setSize(mainSize.width, mainSize.height);
		new GraphAboutDialog(parent).open();
		parent.dispose();
		d.dispose();
	}
	@Override
	public void widgetSelected(SelectionEvent e) {
		if(((Button) e.getSource()).getData() == "credits")
		{
			creditsShell = new Shell(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
			creditsShell.setSize(450, 200);
			
			if(GraphFunctionsFrame.OS == GraphFunctionsFrame.OperatingSystem.OS_MAC || GraphFunctionsFrame.OS == GraphFunctionsFrame.OperatingSystem.OS_WINDOWS)
			{
				creditsShell.setLocation(shell.getLocation().x + (shell.getSize().x - creditsShell.getSize().x) / 2, shell.getLocation().y + 30);
			}
			
			creditsShell.open();
			creditsShell.forceActive();
			while(!creditsShell.isDisposed()) { 
				if (!getParent().getDisplay().readAndDispatch()) getParent().getDisplay().sleep(); 
			} 
		}
		else if(((Button) e.getSource()).getData() == "license")
		{
			licenseShell = new Shell(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
			licenseShell.setSize(450, 350);
			licenseShell.setText(GraphFunctionsFrame.Translate("about.license"));
			licenseShell.setImages(shell.getImages());
			
			if(GraphFunctionsFrame.OS == GraphFunctionsFrame.OperatingSystem.OS_MAC || GraphFunctionsFrame.OS == GraphFunctionsFrame.OperatingSystem.OS_WINDOWS)
			{
				licenseShell.setLocation(shell.getLocation().x + (shell.getSize().x - licenseShell.getSize().x) / 2, shell.getLocation().y + 30);
			}
			GridLayout licenseLayout = new GridLayout(1, false);
			licenseLayout.marginHeight = 10;
			licenseLayout.marginWidth = 10;
			licenseShell.setLayout(licenseLayout);
			Text licenseText = new Text(licenseShell, SWT.MULTI | SWT.READ_ONLY| SWT.BORDER);
			licenseText.setText("licence and shit\ntest\ntest\ntest\ntest\ntest\ntest");
			licenseText.setBackground(new Color(null, 0xff, 0xff, 0xff));
			licenseText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			
			
			Composite closeButtonComp = new Composite(licenseShell, SWT.RIGHT_TO_LEFT);
			closeButtonComp.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, true, false));
			closeButtonComp.setLayout(new GridLayout(4, false));
			
			//new Button(closeButtonComp, SWT.PUSH).setVisible(false);
			
			Button closeButton = new Button(closeButtonComp, SWT.PUSH);
			closeButton.setText("Close");
			GridData data = new GridData(SWT.FILL, SWT.FILL, false, true);
			data.horizontalSpan = 3;
			closeButton.setLayoutData(data);
			closeButton.addSelectionListener(new SelectionAdapter() { 
				@Override
				public void widgetSelected(SelectionEvent e) {
					licenseShell.close();
					licenseShell.dispose();
				}
			});
			licenseShell.open();
			licenseShell.forceActive();
			while(!licenseShell.isDisposed()) { 
				if (!getParent().getDisplay().readAndDispatch()) getParent().getDisplay().sleep(); 
			} 
		}
	}
	@Override
	public void widgetDefaultSelected(SelectionEvent e) {}
	

}
