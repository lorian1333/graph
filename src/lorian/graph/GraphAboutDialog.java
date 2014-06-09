package lorian.graph;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

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

	private Shell shell, licenseShell;

	public GraphAboutDialog(Shell parent, int style) {
		super(parent, style);
	}

	public GraphAboutDialog(Shell parent) {
		this(parent, 0);
	}

	private static boolean isopen = false;
	private String buttonname_spacer;
	private static Image loadImage(int width, int height, String path,
			boolean inJar) {
		Image newImage = null;

		try {
			if (inJar) {
				newImage = new Image(null,
						GraphFunctionsFrame.class.getResourceAsStream(path));
			} else {
				newImage = new Image(null, path);
			}
		} catch (IllegalArgumentException e) {
			System.err.println("Couldn't find " + path);
			return null;
		} catch (SWTException e) {
			System.err.println("Couldn't find " + path);
			return null;
		}

		Image scaledImage = new Image(null, newImage.getImageData().scaledTo(
				width, height));
		return scaledImage;
	}

	public void open() {
		Shell parent = getParent();
		if (isopen)
			return;
		isopen = true;

		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

		shell.setImages(parent.getImages());
		if (GraphFunctionsFrame.OS == GraphFunctionsFrame.OperatingSystem.OS_WINDOWS)
			shell.setSize(425, 295);
		// shell.setSize(400, 315);
		else
			shell.setSize(425, 305);

		if (GraphFunctionsFrame.OS == GraphFunctionsFrame.OperatingSystem.OS_MAC
				|| GraphFunctionsFrame.OS == GraphFunctionsFrame.OperatingSystem.OS_WINDOWS) {
			java.awt.Rectangle clientArea = GraphicsEnvironment
					.getLocalGraphicsEnvironment().getScreenDevices()[0]
					.getDefaultConfiguration().getBounds();
			Rectangle rect = shell.getBounds();
			int x = clientArea.x + (clientArea.width - rect.width) / 2;
			int y = clientArea.y + (clientArea.height - rect.height) / 2;
			shell.setLocation(x, y);
		}

		shell.setText(String.format(
				GraphFunctionsFrame.localize("about.title"), "Graph"));

		GridLayout gridlayout = new GridLayout();
		gridlayout.numColumns = 1;
		gridlayout.marginTop = 20;
		gridlayout.marginBottom = 5;
		gridlayout.marginLeft = 10;
		gridlayout.marginRight = 10;
		// gridlayout.verticalSpacing = 15;
		shell.setLayout(gridlayout);

		Image program_icon = loadImage(64, 64, "/res/icon64.png", true);

		Label program_icon_label = new Label(shell, SWT.NONE);
		program_icon_label.setImage(program_icon);
		program_icon_label.setSize(48, 48);
		program_icon_label.setLayoutData(new GridData(SWT.CENTER, SWT.TOP,
				true, false));

		Label name = new Label(shell, SWT.NONE);
		name.setText("\nGraph\n");
		Font bold = new Font(parent.getDisplay(), new FontData(name.getFont()
				.getFontData()[0].getName(),
				name.getFont().getFontData()[0].getHeight(), SWT.BOLD));
		name.setFont(bold);
		name.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false));

		if (GraphFunctionsFrame.OS == GraphFunctionsFrame.OperatingSystem.OS_WINDOWS)
			new Label(shell, SWT.NONE).setLayoutData(new GridData(SWT.CENTER,
					SWT.TOP, true, false));

		Label version = new Label(shell, SWT.NONE);
		version.setText(GraphFunctionsFrame.version + "\n");
		version.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false));

		if (GraphFunctionsFrame.OS == GraphFunctionsFrame.OperatingSystem.OS_WINDOWS)
			new Label(shell, SWT.NONE).setLayoutData(new GridData(SWT.CENTER,
					SWT.TOP, true, false));

		String description_s = GraphFunctionsFrame
				.localize("about.description");
		if (description_s.contains("\n")) {

			String[] lines = description_s.split("\n");
			for (String line : lines) {
				Label description = new Label(shell, SWT.NONE);
				description.setText(line);
				description.setLayoutData(new GridData(SWT.CENTER, SWT.TOP,
						true, false));
			}
		} else {
			Label description = new Label(shell, SWT.NONE);
			description.setText(description_s);
			description.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true,
					false));
		}
		new Label(shell, SWT.NONE).setText("\n");

		Label copyright = new Label(shell, SWT.NONE);
		int smallfont_size = copyright.getFont().getFontData()[0].getHeight() - 1;
		if (GraphFunctionsFrame.OS == GraphFunctionsFrame.OperatingSystem.OS_WINDOWS) {
			smallfont_size = copyright.getFont().getFontData()[0].getHeight() - 2;
		}
		Font small = new Font(parent.getDisplay(),
				new FontData(copyright.getFont().getFontData()[0].getName(),
						smallfont_size, SWT.NONE));
		copyright.setFont(small);
		copyright.setText(String.format("Copyright %c 2013-%d Lorian Coltof \n",
				0xA9, GraphFunctionsFrame.version_year));
		copyright.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false));

		Label build_info = new Label(shell, SWT.NONE);
		build_info.setFont(small);
		Calendar built_date = getBuiltDate();
		SimpleDateFormat format = new SimpleDateFormat("dd MMMMM yyyy HH:mm:ss", GraphFunctionsFrame.getCurrentLocale());
		
		//build_info.setText(String.format(GraphFunctionsFrame.Translate("about.buildinfo"), 
		//		getManifestAttributeValue("Built-By"), built_date.getDate(), "maand", built_date.getYear()+1900, 
		//				built_date.getHours(), built_date.getMinutes(), built_date.getSeconds()
		//		));
		
		build_info.setText(String.format(GraphFunctionsFrame.localize("about.buildinfo"), 
				getManifestAttributeValue("Built-By"), format.format(built_date.getTime())
				));
		
		build_info.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false));

		
		buttonname_spacer = "   ";
		if (GraphFunctionsFrame.OS == GraphFunctionsFrame.OperatingSystem.OS_LINUX)
			buttonname_spacer = "    ";

		Composite buttons = new Composite(shell, SWT.NONE);
		buttons.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, true));

		RowLayout layout = new RowLayout(SWT.HORIZONTAL);
		layout.wrap = true;
		layout.fill = false;
		layout.justify = true;
		buttons.setLayout(layout);
		/*
		Button credits = new Button(buttons, SWT.PUSH);
		credits.setText(buttonname_spacer
				+ GraphFunctionsFrame.Translate("about.credits")
				+ buttonname_spacer);
		credits.setData("credits");
		credits.addSelectionListener(this);
		*/
		Button license = new Button(buttons, SWT.PUSH);
		license.setText(buttonname_spacer
				+ GraphFunctionsFrame.localize("about.license")
				+ buttonname_spacer);
		license.setData("license");
		license.addSelectionListener(this);

		if (GraphFunctionsFrame.OS != GraphFunctionsFrame.OperatingSystem.OS_MAC) {
			Label spacer = new Label(buttons, SWT.NONE);
			spacer.setVisible(false);
			RowData r = new RowData();
			r.width = 250; // 120
			spacer.setLayoutData(r);
		}

		Button close = new Button(buttons, SWT.PUSH | SWT.RIGHT_TO_LEFT);
		close.setText(buttonname_spacer
				+ GraphFunctionsFrame.localize("buttons.close")
				+ buttonname_spacer + " ");
		close.addSelectionListener(this);
		close.setData("close");
		close.forceFocus();

		if (GraphFunctionsFrame.OS == GraphFunctionsFrame.OperatingSystem.OS_WINDOWS)
			shell.pack();

		shell.open();
		shell.forceActive();
		while (!shell.isDisposed()) {
			if (!parent.getDisplay().readAndDispatch())
				parent.getDisplay().sleep();
		}
		isopen = false;
	}

	public static void open_swing(Point mainLocation, Dimension mainSize) {
		// TODO Just a temporary solution
		Display d = new Display();
		Shell parent = new Shell(d);
		parent.setLocation(mainLocation.x, mainLocation.y);
		parent.setSize(mainSize.width, mainSize.height);
		new GraphAboutDialog(parent).open();
		parent.dispose();
		d.dispose();
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		/*
		if (((Button) e.getSource()).getData() == "credits") {
			creditsShell = new Shell(shell, SWT.DIALOG_TRIM
					| SWT.APPLICATION_MODAL);
			creditsShell.setSize(450, 200);

			if (GraphFunctionsFrame.OS == GraphFunctionsFrame.OperatingSystem.OS_MAC
					|| GraphFunctionsFrame.OS == GraphFunctionsFrame.OperatingSystem.OS_WINDOWS) {
				creditsShell
						.setLocation(
								shell.getLocation().x
										+ (shell.getSize().x - creditsShell
												.getSize().x) / 2,
								shell.getLocation().y + 30);
			}

			creditsShell.open();
			creditsShell.forceActive();
			while (!creditsShell.isDisposed()) {
				if (!getParent().getDisplay().readAndDispatch())
					getParent().getDisplay().sleep();
			}
		} else */
		if (((Button) e.getSource()).getData() == "license") {
			licenseShell = new Shell(shell, SWT.DIALOG_TRIM
					| SWT.APPLICATION_MODAL | SWT.RESIZE);
			licenseShell.setSize(650, 430);
			licenseShell
					.setText(GraphFunctionsFrame.localize("about.license"));
			licenseShell.setImages(shell.getImages());

			if (GraphFunctionsFrame.OS == GraphFunctionsFrame.OperatingSystem.OS_MAC
					|| GraphFunctionsFrame.OS == GraphFunctionsFrame.OperatingSystem.OS_WINDOWS) {
				licenseShell.setLocation(
								shell.getLocation().x + (shell.getSize().x - licenseShell.getSize().x) / 2,
								shell.getLocation().y + 30);
			}
			GridLayout licenseLayout = new GridLayout(1, false);
			licenseLayout.marginHeight = 10;
			licenseLayout.marginWidth = 10;
			licenseShell.setLayout(licenseLayout);
			Text licenseText = new Text(licenseShell, SWT.MULTI | SWT.READ_ONLY
					| SWT.BORDER | SWT.V_SCROLL);
			licenseText.setText(getLicenseText());
			licenseText.setBackground(new Color(null, 0xff, 0xff, 0xff));
			licenseText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
					true));
			FontData[] fd = licenseText.getFont().getFontData();
			for(int i=0; i < fd.length; i++)
			{
				fd[i].setName("consolas");
			}
			licenseText.setFont(new Font(Display.getCurrent(), fd));
			Composite closeButtonComp = new Composite(licenseShell,
					SWT.RIGHT_TO_LEFT);
			closeButtonComp.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM,
					true, false));
			closeButtonComp.setLayout(new GridLayout(7, true));

			Button closeButton = new Button(closeButtonComp, SWT.PUSH);
			closeButton.setText(buttonname_spacer + GraphFunctionsFrame.localize("buttons.close") + buttonname_spacer);
			GridData data = new GridData(SWT.FILL, SWT.FILL, false, true);	
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
			while (!licenseShell.isDisposed()) {
				if (!getParent().getDisplay().readAndDispatch())
					getParent().getDisplay().sleep();
			}
		}
		else if (((Button) e.getSource()).getData() == "close")
		{
			shell.dispose();
		}
	}

	private static String getLicenseText() {
		try {
			URL url = GraphAboutDialog.class.getResource("/res/LICENSE.md");
			BufferedReader br = new BufferedReader(new FileReader(new File(url.toURI())));

			String license = "", line = "";
			while ((line = br.readLine()) != null) {
				license += line + "\n";
			}
			br.close();
			return license;
		} catch (Exception e) {
			e.printStackTrace();
			return "<error reading license file>";
		}
	}
	private static String getManifestAttributeValue(String attribute)
	{
		Class<GraphAboutDialog> clazz = GraphAboutDialog.class;
		/*
		String className = clazz.getSimpleName() + ".class";
		String classPath = clazz.getResource(className).toString();
		
		if (!classPath.startsWith("jar")) {
			System.err.println("Error getting manifest attribute value: not a JAR file");
			return "<error>";
		} 
		*/
		Manifest manifest;
		try {
			manifest = new Manifest(clazz.getResourceAsStream("/META-INF/MANIFEST.MF"));
			Attributes attr = manifest.getMainAttributes(); 
			String value = attr.getValue(attribute);
			if(value == null)
				throw new IllegalArgumentException("Attribute '" + attribute + "' was not found in MANIFEST.MF");
			return value;
		} catch (MalformedURLException e) {
			e.printStackTrace(); 
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "<error>";
		
	}

	private static Calendar getBuiltDate()
	{
		String builtdate = getManifestAttributeValue("Built-Date");
		String[] tmp = builtdate.split(" ");
		String[] date_split = tmp[0].split("-");
		String[] time_split = tmp[1].split(":");
		return new GregorianCalendar(
				Integer.parseInt(date_split[2]),
				Integer.parseInt(date_split[1]),
				Integer.parseInt(date_split[0]),
				Integer.parseInt(time_split[0]),
				Integer.parseInt(time_split[1]),
				Integer.parseInt(time_split[2])
				);
	}
	
	@Override
	public void widgetDefaultSelected(SelectionEvent e) {}

}
