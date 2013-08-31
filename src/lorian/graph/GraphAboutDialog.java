package lorian.graph;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class GraphAboutDialog  {

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
	public static void open(Point mainLocation, Dimension mainSize)
	{
		if(isopen) return;  
		///asdasdadasda 
		isopen = true;   
		Display d = new Display();
		final Shell shell = new Shell(d, SWT.DIALOG_TRIM);    
		shell.setImage(loadImage(16, 16, "/res/icon16.png", true));
		List<Image> icons = new ArrayList<Image>();
		icons.add(loadImage(16, 16, "/res/icon16.png", true)); 
		icons.add(loadImage(32, 32, "/res/icon32.png", true)); 
		icons.add(loadImage(64, 64, "/res/icon64.png", true)); 
		icons.add(loadImage(128, 128, "/res/icon128.png", true));
		Image[] icons_array = icons.toArray(new Image[4]);
		shell.setImages(icons_array);
		
		if(GraphFunctionsFrame.OS == GraphFunctionsFrame.OperatingSystem.OS_WINDOWS)  
			shell.setSize(425, 295);
		else
			shell.setSize(425, 305);
		
		Rectangle rect = shell.getBounds();
		int x = mainLocation.x + (mainSize.width - rect.width) / 2;
		int y = mainLocation.y + (mainSize.height - rect.height) / 2;
		shell.setLocation(x, y);
		  
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
		Font bold = new Font(d, new FontData(name.getFont().getFontData()[0].getName(), name.getFont().getFontData()[0].getHeight(), SWT.BOLD));
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
		Font small =new Font(d, new FontData(copyright.getFont().getFontData()[0].getName(), smallfont_size, SWT.NONE));
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
		credits.addSelectionListener(new SelectionAdapter() {
			 @Override
			 public void widgetSelected(SelectionEvent e) {
			 }
		});
		
		Button license = new Button(buttons, SWT.PUSH);
		license.setText(buttonname_spacer + GraphFunctionsFrame.Translate("about.license") + buttonname_spacer);
		license.addSelectionListener(new SelectionAdapter() {
			 @Override
			 public void widgetSelected(SelectionEvent e) {
			 }
		});

		Label spacer = new Label(buttons, SWT.NONE); 
		spacer.setVisible(false);
		RowData r = new RowData();
		r.width = 120;
		spacer.setLayoutData(r);
		
		Button close = new Button(buttons, SWT.PUSH | SWT.RIGHT);
		close.setText(buttonname_spacer + GraphFunctionsFrame.Translate("about.close") + buttonname_spacer + " ");
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
			if (!d.readAndDispatch()) d.sleep(); 
			} 
		isopen = false;
		d.dispose(); 
	}
	
	

}
