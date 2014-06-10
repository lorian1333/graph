package lorian.graph;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Panel;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JRootPane;

import lorian.graph.CalcPanelSwt.Calculations;
import lorian.graph.GraphFunctionsFrame.Mode;
import lorian.graph.fileio.GraphFileReader;
import lorian.graph.fileio.GraphFileWriter;
import lorian.graph.function.Function;
import lorian.graph.function.Function2Var;
import lorian.graph.function.ParameterFunction;
import lorian.graph.opengl.Graph3D;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class GraphSwtFrame implements SelectionListener {

	protected Display display;
	protected static Shell shell;
	protected boolean small;
	protected Point WindowSize = new Point(1257, 859);
	// protected Point WindowSizeSmall = new Point(1004, 685);

	private MenuItem calcMenu;
	
	private final Color[] defaultColors = { new Color(null, 37, 119, 255), new Color(null, 255, 0, 0), new Color(null, 255, 0, 255), new Color(null, 0, 225, 255), new Color(null, 0, 255, 90), new Color(null, 255, 255, 0), new Color(null, 255, 120, 0) };

	protected Button[] functionVisibility_func, functionVisibility_3dfunc, functionVisibility_par;
	protected Label[] functionColors_func, functionColors_3dfunc, functionColors_par;
	protected Text[] functionStrings_func, functionStrings_3dfunc, functionStrings_par_x, functionStrings_par_y;
	protected Canvas[] functionParseResults_func, functionParseResults_3dfunc, functionParseResults_par_x, functionParseResults_par_y;

	protected InputData[] backups_func, backups_3dfunc;
	protected InputDataParameter[] backups_par;

	protected List<Function> functions_func;
	protected List<Function2Var> functions_3dfunc;
	protected List<ParameterFunction> functions_par;

	protected double[][] tBounds;
	
	protected WindowSettings wsettings_func;
	protected WindowSettings3D wsettings_3dfunc;
	protected WindowSettingsParameter wsettings_par;

	protected GraphFunctionsFrame.Mode currentMode = GraphFunctionsFrame.Mode.MODE_FUNC;
	protected java.awt.Container graphContainer;

	protected CalcPanelSwt calcPanel;
	protected WindowSettingsPanelSwt wSettingsPanel;
	
	private Image okIcon, errorIcon, accoladeOpen, accoladeClose;

	private boolean UIinitted_func = false, UIinitted_3dfunc = false, UIinitted_par = false;

	protected static List<Image> icons;

	public GraphSwtFrame(Display display, String language) {

		Display.setAppName(GraphFunctionsFrame.appname);
		Display.setAppVersion(GraphFunctionsFrame.version);

		this.display = display;
		if(language != null)
		{
			GraphFunctionsFrame.current_lang_name = language;
		}
		GraphFunctionsFrame.funcframe = new GraphFunctionsFrame(false, false, true);
		if (GraphFunctionsFrame.OS == GraphFunctionsFrame.OperatingSystem.OS_MAC) {
			System.setProperty("apple.laf.useScreenMenuBar", "true");
			System.setProperty("com.apple.mrj.application.apple.menu.about.name", GraphFunctionsFrame.appname);
		}
		if (GraphFunctionsFrame.OS == GraphFunctionsFrame.OperatingSystem.OS_WINDOWS) {
			System.setProperty("sun.awt.noerasebackground", "true");
		}

		okIcon = new Image(null, GraphSwtFrame.class.getResourceAsStream("/res/ok.png"));
		errorIcon = new Image(null, GraphSwtFrame.class.getResourceAsStream("/res/error.png"));
		accoladeOpen = new Image(null, GraphSwtFrame.class.getResourceAsStream("/res/accolade_open.png"));
		accoladeClose = new Image(null, GraphSwtFrame.class.getResourceAsStream("/res/accolade_close.png"));

		functions_func = new ArrayList<Function>();
		functions_3dfunc = new ArrayList<Function2Var>();
		functions_par = new ArrayList<ParameterFunction>();

		backups_func = new InputData[GraphFunctionsFrame.MaxFunctions];
		backups_3dfunc = new InputData[GraphFunctionsFrame.MaxFunctions];
		backups_par = new InputDataParameter[(int) (GraphFunctionsFrame.MaxFunctions * 0.5)];

		for (int i = 0; i < GraphFunctionsFrame.MaxFunctions; i++) {
			Function dummy_func = new Function();
			dummy_func.setDraw(false);
			functions_func.add(dummy_func);
			Function2Var dummy_3dfunc = new Function2Var();
			dummy_3dfunc.setDraw(false);
			functions_3dfunc.add(dummy_3dfunc);
			if(i < (int) (GraphFunctionsFrame.MaxFunctions * 0.5))
			{
				ParameterFunction dummy_par = new ParameterFunction();
				dummy_par.setDraw(false);
				functions_par.add(dummy_par);
			}
		}

		initShell();
		
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	private void Exit() {
		if (!GraphFunctionsFrame.FileSaved && !GraphFunctionsFrame.NoChangesMade) {
			if (!ConfirmFileChanges()) {
				return;
			}
		}
		
		System.out.println("Exiting...");
		//if(GraphFunctionsFrame.gframe != null) GraphFunctionsFrame.gframe.setEnabled(false);
		//if(GraphFunctionsFrame.g3d != null) GraphFunctionsFrame.g3d.setEnabled(false);
		//if(GraphFunctionsFrame.gparam != null) GraphFunctionsFrame.gparam.setEnabled(false);
		
		shell.dispose();
		display.dispose();
		System.exit(0);
	}

	private void initShell() {
		int shellflags = SWT.SHELL_TRIM;// & (~SWT.MAX) & (~SWT.RESIZE);
		// final Display display = Display.getDefault();
		shell = new Shell(display, shellflags);

		initIcons();
		initMenu();
		initUI();

		updateTitle();
		// shell.setSize(WindowSize);
		java.awt.Rectangle clientArea = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0].getDefaultConfiguration().getBounds();
		Rectangle rect = shell.getBounds();
		int x = clientArea.x + (clientArea.width - rect.width) / 2;
		int y = clientArea.y + (clientArea.height - rect.height) / 2;
		shell.setLocation(x, y);
		shell.addListener(SWT.Close, new Listener() {

			@Override
			public void handleEvent(Event event) {
				event.doit = false;
				Exit();

			}
		});
		
		//shell.setMaximized(true);
		resizeGraph(shell.getSize());
	}

	private void resizeGraph(Point size) {
		switch (currentMode) {
		case MODE_FUNC:
			GraphFunctionsFrame.gframe.setSize(size.x, size.y);
			break;
		case MODE_3DFUNC:
			GraphFunctionsFrame.g3d.setSize(size.x, size.y);
			break;
		case MODE_PARAMETER:
			GraphFunctionsFrame.gparam.setSize(size.x, size.y);
			break;
		default:
			break;
		}
		
		if(wSettingsPanel != null)
		{
			if(wSettingsPanel.isOpen())
				wSettingsPanel.Update();
		}

	}

	private void saveScreenShot() {
		System.out.println("Saving screenshot");
		BufferedImage bImg;
		Graphics gImg;
		switch (currentMode) {
			case MODE_FUNC:
				bImg = new BufferedImage((int) GraphFunctionsFrame.gframe.getWidth(), (int) GraphFunctionsFrame.gframe.getHeight(), BufferedImage.TYPE_INT_RGB);
				gImg = bImg.getGraphics();
				gImg.setColor(java.awt.Color.WHITE);
				gImg.clearRect(0, 0, bImg.getWidth(), bImg.getHeight());
				GraphFunctionsFrame.gframe.paintAll(gImg);
	
				try {
					FileDialog savedialog = new FileDialog(shell, SWT.SAVE);
					savedialog.setOverwrite(true);
					savedialog.setFilterNames(new String[] { GraphFunctionsFrame.localize("files.pngimage") });
					savedialog.setFilterExtensions(new String[] { "*.png" });
					String FilePath = savedialog.open();
					if (FilePath == null) {
						System.out.println("Canceled by user.");
						return;
					}
	
					if (!FilePath.toLowerCase().endsWith(".png")) {
						FilePath += ".png";
					}
					File output = new File(FilePath);
					ImageIO.write(bImg, "png", output);
	
					System.out.println("Done");
	
					MessageBox message = new MessageBox(shell);
					message.setMessage(GraphFunctionsFrame.localize("message.screenshotsaved"));
					message.setText("Graph");
					message.open();
				} catch (IOException e) {
					e.printStackTrace();
				}
	
				break;
			case MODE_3DFUNC:
				/*
				bImg = new BufferedImage((int) GraphFunctionsFrame.g3d.getWidth(), (int) GraphFunctionsFrame.g3d.getHeight(), BufferedImage.TYPE_INT_RGB);
				gImg = bImg.getGraphics();
				gImg.setColor(java.awt.Color.WHITE);
				gImg.clearRect(0, 0, bImg.getWidth(), bImg.getHeight());
				*/
				
				bImg = GraphFunctionsFrame.g3d.toImage();
	
				try {
					FileDialog savedialog = new FileDialog(shell, SWT.SAVE);
					savedialog.setOverwrite(true);
					savedialog.setFilterNames(new String[] { GraphFunctionsFrame.localize("files.pngimage") });
					savedialog.setFilterExtensions(new String[] { "*.png" });
					String FilePath = savedialog.open();
					if (FilePath == null) {
						System.out.println("Canceled by user.");
						return;
					}
	
					if (!FilePath.toLowerCase().endsWith(".png")) {
						FilePath += ".png";
					}
					File output = new File(FilePath);
					ImageIO.write(bImg, "png", output);
	
					System.out.println("Done");
	
					MessageBox message = new MessageBox(shell);
					message.setMessage(GraphFunctionsFrame.localize("message.screenshotsaved"));
					message.setText("Graph");
					message.open();
				} catch (IOException e) {
					e.printStackTrace();
				}
	
				break;
			case MODE_PARAMETER:
				bImg = new BufferedImage((int) GraphFunctionsFrame.gparam.getWidth(), (int) GraphFunctionsFrame.gparam.getHeight(), BufferedImage.TYPE_INT_RGB);
				gImg = bImg.getGraphics();
				gImg.setColor(java.awt.Color.WHITE);
				gImg.clearRect(0, 0, bImg.getWidth(), bImg.getHeight());
				GraphFunctionsFrame.gparam.paintAll(gImg);
	
				try {
					FileDialog savedialog = new FileDialog(shell, SWT.SAVE);
					savedialog.setOverwrite(true);
					savedialog.setFilterNames(new String[] { GraphFunctionsFrame.localize("files.pngimage") });
					savedialog.setFilterExtensions(new String[] { "*.png" });
					String FilePath = savedialog.open();
					if (FilePath == null) {
						System.out.println("Canceled by user.");
						return;
					}
	
					if (!FilePath.toLowerCase().endsWith(".png")) {
						FilePath += ".png";
					}
					File output = new File(FilePath);
					ImageIO.write(bImg, "png", output);
	
					System.out.println("Done");
	
					MessageBox message = new MessageBox(shell);
					message.setMessage(GraphFunctionsFrame.localize("message.screenshotsaved"));
					message.setText("Graph");
					message.open();
				} catch (IOException e) {
					e.printStackTrace();
				}
	
				break;
			default:
				break;
		}
	}

	private void initUI() {
		switch (currentMode) {
		case MODE_FUNC:
			initUI_funcmode();
			UIinitted_func = true;
			break;
		case MODE_3DFUNC:
			initUI_func3dmode();
			UIinitted_3dfunc = true;
			break;
		case MODE_PARAMETER:
			initUI_parmode();
			UIinitted_par = true;
			break;
		default:
			break;
		}

	}

	private void initUI_funcmode() {

		FillLayout fillLayout = new FillLayout();
		fillLayout.type = SWT.HORIZONTAL;
		shell.setLayout(fillLayout);

		Composite outer = new Composite(shell, SWT.NONE);

		FormLayout formLayout = new FormLayout();
		outer.setLayout(formLayout);

		Composite functionInputHolder = new Composite(outer, SWT.NONE);
		GridLayout divideLayout = new GridLayout();
		divideLayout.numColumns = 1;
		functionInputHolder.setLayout(divideLayout);

		FormData fData = new FormData();
		fData.top = new FormAttachment(0);
		fData.left = new FormAttachment(0);
		// fData.right = new FormAttachment(36);
		fData.bottom = new FormAttachment(100);
		functionInputHolder.setLayoutData(fData);
		// functionInputHolder.setSize(300, 0);

		Composite comp = new Composite(functionInputHolder, SWT.NONE);
		GridLayout gridLayoutInput = new GridLayout(4, false);
		gridLayoutInput.horizontalSpacing = 10;
		gridLayoutInput.verticalSpacing = 4;
		comp.setLayout(gridLayoutInput);
		comp.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

		functionVisibility_func = new Button[GraphFunctionsFrame.MaxFunctions];
		functionColors_func = new Label[GraphFunctionsFrame.MaxFunctions];
		functionStrings_func = new Text[GraphFunctionsFrame.MaxFunctions];
		functionParseResults_func = new Canvas[GraphFunctionsFrame.MaxFunctions];
		
		for (int i = 0; i < GraphFunctionsFrame.MaxFunctions; i++) {
			final int numFunction = i + 1;
			Button functionVisible = new Button(comp, SWT.CHECK);
			functionVisible.setToolTipText(String.format(GraphFunctionsFrame.localize("tooltip.changefunctionvisibility"), 'Y', numFunction));
			if (!UIinitted_func)
				functionVisible.setSelection(true);
			else
				functionVisible.setSelection(backups_func[i].visible);

			functionVisible.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					Plot();
				}
			});

			final Label functionName = new Label(comp, SWT.NO_FOCUS);
			if (numFunction < 10)
				functionName.setText("  Y" + numFunction + "   = ");
			else
				functionName.setText("  Y" + numFunction + " = ");
			if (!UIinitted_func)
				functionName.setBackground(defaultColors[i % defaultColors.length]);
			else
				functionName.setBackground(backups_func[i].color);

			functionName.setToolTipText(String.format(GraphFunctionsFrame.localize("tooltip.changefunctioncolor"), 'Y', numFunction));

			functionName.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					ColorDialog dlg = new ColorDialog(shell);
					Color currentColor = functionName.getBackground();
					dlg.setRGB(currentColor.getRGB());
					dlg.setText(String.format(GraphFunctionsFrame.localize("colorchooser.functioncolor.title"), 'Y', numFunction));
					RGB rgb = dlg.open();
					if (rgb != null) {
						functionName.setBackground(new Color(currentColor.getDevice(), rgb));
						Plot();
					}
				}

			});

			final Text inputText = new Text(comp, SWT.SINGLE | SWT.BORDER);
			Font textFont = inputText.getFont();
			int bigfont_size = textFont.getFontData()[0].getHeight() + 2;
			Font big = new Font(null, new FontData(textFont.getFontData()[0].getName(), bigfont_size, SWT.NONE));
			inputText.setFont(big);
			inputText.setToolTipText(String.format(GraphFunctionsFrame.localize("tooltip.enterfunctionequotation"), 'Y', numFunction));
			inputText.addListener(SWT.Traverse, new Listener() {
				@Override
				public void handleEvent(Event e) {
					if (e.detail == SWT.TRAVERSE_RETURN)
						Plot();

				}
			});
			GridData inputData = new GridData(SWT.FILL, SWT.FILL, true, true);
			inputData.widthHint = 250;
			inputText.setLayoutData(inputData);
			if (UIinitted_func) {
				inputText.setText(backups_func[i].text);
			}

			final Canvas functionParseResult = new Canvas(comp, SWT.NO_REDRAW_RESIZE | SWT.NO_FOCUS);

			GridData iconData = new GridData();
			iconData.widthHint = 16;
			iconData.heightHint = 16;
			functionParseResult.setLayoutData(iconData);

			functionParseResult.setData(null);
			functionParseResult.addPaintListener(new PaintListener() {
				@Override
				public void paintControl(PaintEvent e) {
					if (functionParseResult.getData() == null)
						return;
					Image img = (Image) functionParseResult.getData();
					e.gc.drawImage(img, 0, 0);
				}
			});

			functionParseResult.update();

			functionVisibility_func[i] = functionVisible;
			functionColors_func[i] = functionName;
			functionStrings_func[i] = inputText;
			functionParseResults_func[i] = functionParseResult;
		}

		functionStrings_func[0].forceFocus();

		Composite plotButtonHolder = new Composite(functionInputHolder, SWT.NONE);
		FillLayout plotButtonHolderLayout = new FillLayout();
		plotButtonHolderLayout.marginWidth = 78;
		plotButtonHolder.setLayout(plotButtonHolderLayout);

		Button plotButton = new Button(plotButtonHolder, SWT.PUSH);
		plotButton.setText("  " + GraphFunctionsFrame.localize("buttons.draw") + "  ");
		plotButton.addSelectionListener(this);

		try {
			// UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			// SwingUtilities.updateComponentTreeUI(root);
		} catch (Exception e) {
			e.printStackTrace();
		}

		final Composite gframe_holder = new Composite(outer, SWT.EMBEDDED);

		final Menu menu = new Menu(shell, SWT.POP_UP);
		MenuItem item = new MenuItem(menu, SWT.PUSH);
		item.setText(GraphFunctionsFrame.localize("menu.savescreenshot"));
		item.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Display.getDefault().syncExec(new Runnable() {

					@Override
					public void run() {
						saveScreenShot();

					}
				});

			}
		});
		gframe_holder.addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(Event e) {
				resizeGraph(gframe_holder.getSize());
			}
		});
		gframe_holder.setLayout(fillLayout);

		Frame gframe_frame = SWT_AWT.new_Frame(gframe_holder);

		Panel panel = new Panel(new BorderLayout());
		gframe_frame.setBackground(java.awt.Color.WHITE);

		gframe_frame.add(panel);
		JRootPane root = new JRootPane();
		panel.add(root);
		graphContainer = root.getContentPane();

		if (wsettings_func == null)
			wsettings_func = new WindowSettings();
		GraphFunctionsFrame.gframe = new GraphFrame(functions_func, wsettings_func, GraphFunctionsFrame.WindowSize, false);

		GraphFunctionsFrame.gframe.addMouseListener(new java.awt.event.MouseAdapter() {

			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				if (e.getButton() == java.awt.event.MouseEvent.BUTTON3) {
					Display.getDefault().syncExec(new Runnable() {

						@Override
						public void run() {
							menu.setVisible(true);

						}
					});
				}
			}

		});
		graphContainer.add(GraphFunctionsFrame.gframe);

		fData = new FormData();
		fData.top = new FormAttachment(0);
		fData.left = new FormAttachment(functionInputHolder);
		fData.right = new FormAttachment(100);
		fData.bottom = new FormAttachment(100);

		gframe_holder.setLayoutData(fData);

	}

	private void initUI_func3dmode() {

		FillLayout fillLayout = new FillLayout();
		fillLayout.type = SWT.HORIZONTAL;
		shell.setLayout(fillLayout);

		Composite outer = new Composite(shell, SWT.NONE);

		FormLayout formLayout = new FormLayout();
		outer.setLayout(formLayout);

		Composite functionInputHolder = new Composite(outer, SWT.NONE);
		GridLayout divideLayout = new GridLayout();
		divideLayout.numColumns = 1;
		functionInputHolder.setLayout(divideLayout);

		FormData fData = new FormData();
		fData.top = new FormAttachment(0);
		fData.left = new FormAttachment(0);
		// fData.right = new FormAttachment(36);
		fData.bottom = new FormAttachment(100);
		functionInputHolder.setLayoutData(fData);

		Composite comp = new Composite(functionInputHolder, SWT.NONE);
		GridLayout gridLayoutInput = new GridLayout(4, false);
		gridLayoutInput.horizontalSpacing = 10;
		gridLayoutInput.verticalSpacing = 4;
		comp.setLayout(gridLayoutInput);
		comp.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

		
		functionVisibility_3dfunc = new Button[GraphFunctionsFrame.MaxFunctions];
		functionColors_3dfunc = new Label[GraphFunctionsFrame.MaxFunctions];
		functionStrings_3dfunc = new Text[GraphFunctionsFrame.MaxFunctions];
		functionParseResults_3dfunc = new Canvas[GraphFunctionsFrame.MaxFunctions];
		
		for (int i = 0; i < GraphFunctionsFrame.MaxFunctions; i++) {
			final int numFunction = i + 1;
			Button functionVisible = new Button(comp, SWT.CHECK);
			functionVisible.setToolTipText(String.format(GraphFunctionsFrame.localize("tooltip.changefunctionvisibility"), 'Z', numFunction));
			if (!UIinitted_3dfunc)
				functionVisible.setSelection(true);
			else
				functionVisible.setSelection(backups_3dfunc[i].visible);

			functionVisible.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					Plot();
				}
			});

			final Label functionName = new Label(comp, SWT.NO_FOCUS);
			if (numFunction < 10)
				functionName.setText("  Z" + numFunction + "   = ");
			else
				functionName.setText("  Z" + numFunction + " = ");
			if (!UIinitted_3dfunc)
				functionName.setBackground(defaultColors[i % defaultColors.length]);
			else
				functionName.setBackground(backups_3dfunc[i].color);

			functionName.setToolTipText(String.format(GraphFunctionsFrame.localize("tooltip.changefunctioncolor"), 'Z', numFunction));

			functionName.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					ColorDialog dlg = new ColorDialog(shell);
					Color currentColor = functionName.getBackground();
					dlg.setRGB(currentColor.getRGB());
					dlg.setText(String.format(GraphFunctionsFrame.localize("colorchooser.functioncolor.title"), 'Z', numFunction));
					RGB rgb = dlg.open();
					if (rgb != null) {
						functionName.setBackground(new Color(currentColor.getDevice(), rgb));
						Plot();
					}

				}
			});

			final Text inputText = new Text(comp, SWT.SINGLE | SWT.BORDER);
			Font textFont = inputText.getFont();
			int bigfont_size = textFont.getFontData()[0].getHeight() + 2;
			Font big = new Font(null, new FontData(textFont.getFontData()[0].getName(), bigfont_size, SWT.NONE));
			inputText.setFont(big);
			inputText.setToolTipText(String.format(GraphFunctionsFrame.localize("tooltip.enterfunctionequotation"), 'Z', numFunction));
			inputText.addListener(SWT.Traverse, new Listener() {
				@Override
				public void handleEvent(Event e) {
					if (e.detail == SWT.TRAVERSE_RETURN)
						Plot();

				}
			});

			GridData inputData = new GridData(SWT.FILL, SWT.FILL, true, true);
			inputData.widthHint = 250;
			inputText.setLayoutData(inputData);
			if (UIinitted_3dfunc)
				inputText.setText(backups_3dfunc[i].text);

			final Canvas functionParseResult = new Canvas(comp, SWT.NO_REDRAW_RESIZE | SWT.NO_FOCUS);

			GridData iconData = new GridData();
			iconData.widthHint = 16;
			iconData.heightHint = 16;
			functionParseResult.setLayoutData(iconData);

			functionParseResult.setData(null);
			functionParseResult.addPaintListener(new PaintListener() {
				@Override
				public void paintControl(PaintEvent e) {
					if (functionParseResult.getData() == null)
						return;
					Image img = (Image) functionParseResult.getData();
					e.gc.drawImage(img, 0, 0);
				}
			});

			functionParseResult.update();

			functionVisibility_3dfunc[i] = functionVisible;
			functionColors_3dfunc[i] = functionName;
			functionStrings_3dfunc[i] = inputText;
			functionParseResults_3dfunc[i] = functionParseResult;
		}

		functionStrings_3dfunc[0].forceFocus();

		Composite plotButtonHolder = new Composite(functionInputHolder, SWT.NONE);

		FillLayout plotButtonHolderLayout = new FillLayout();
		plotButtonHolderLayout.marginWidth = 78;
		plotButtonHolder.setLayout(plotButtonHolderLayout);

		Button plotButton = new Button(plotButtonHolder, SWT.PUSH);
		plotButton.setText("  " + GraphFunctionsFrame.localize("buttons.draw") + "  ");
		plotButton.addSelectionListener(this);

		final Composite gframe_holder = new Composite(outer, SWT.EMBEDDED);

		final Menu menu = new Menu(shell, SWT.POP_UP);
		MenuItem item = new MenuItem(menu, SWT.PUSH);
		item.setText(GraphFunctionsFrame.localize("menu.savescreenshot"));
		item.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Display.getDefault().syncExec(new Runnable() {

					@Override
					public void run() {
						saveScreenShot();

					}
				});

			}
		});

		gframe_holder.addListener(SWT.Resize, new Listener() {

			@Override
			public void handleEvent(Event e) {
				resizeGraph(gframe_holder.getSize());
			}
		});

		gframe_holder.setLayout(new GridLayout(1, true));
		Frame gframe_frame = SWT_AWT.new_Frame(gframe_holder);
		Panel panel = new Panel(new BorderLayout());
		// gframe_frame.setBackground(java.awt.Color.WHITE);

		gframe_frame.add(panel);
		JRootPane root = new JRootPane();
		panel.add(root);
		graphContainer = root.getContentPane();

		if (wsettings_3dfunc == null)
			wsettings_3dfunc = new WindowSettings3D();

		GraphFunctionsFrame.g3d = new Graph3D(this.wsettings_3dfunc);
		GraphFunctionsFrame.g3d.addMouseListener(new java.awt.event.MouseAdapter() {

			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				if (e.getButton() == java.awt.event.MouseEvent.BUTTON3) {
					Display.getDefault().syncExec(new Runnable() {

						@Override
						public void run() {
							menu.setVisible(true);

						}
					});
				}
			}

		});

		graphContainer.add(GraphFunctionsFrame.g3d);

		fData = new FormData();
		fData.top = new FormAttachment(0);
		fData.left = new FormAttachment(functionInputHolder);
		fData.right = new FormAttachment(100);
		fData.bottom = new FormAttachment(100);
		gframe_holder.setLayoutData(fData);

	}

	private void initUI_parmode() {

		FillLayout fillLayout = new FillLayout();
		fillLayout.type = SWT.HORIZONTAL;
		shell.setLayout(fillLayout);

		tBounds = new double[(int) (GraphFunctionsFrame.MaxFunctions * 0.5)][];
		
		Composite outer = new Composite(shell, SWT.NONE);

		FormLayout formLayout = new FormLayout();
		outer.setLayout(formLayout);

		Composite functionInputHolder = new Composite(outer, SWT.NONE);
		GridLayout divideLayout = new GridLayout();
		divideLayout.numColumns = 1;
		functionInputHolder.setLayout(divideLayout);

		FormData fData = new FormData();
		fData.top = new FormAttachment(0);
		fData.left = new FormAttachment(0);
		fData.bottom = new FormAttachment(100);
		functionInputHolder.setLayoutData(fData);

		Composite comp = new Composite(functionInputHolder, SWT.NONE);
		GridLayout gridLayoutInput = new GridLayout(5, false);
		gridLayoutInput.horizontalSpacing = 5;
		gridLayoutInput.verticalSpacing = 4;
		comp.setLayout(gridLayoutInput);
		comp.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

		functionVisibility_par = new Button[(int) (GraphFunctionsFrame.MaxFunctions * 0.5)];
		functionColors_par = new Label[(int) (GraphFunctionsFrame.MaxFunctions * 0.5)];
		functionStrings_par_x = new Text[(int) (GraphFunctionsFrame.MaxFunctions * 0.5)];
		functionStrings_par_y = new Text[(int) (GraphFunctionsFrame.MaxFunctions * 0.5)];
		functionParseResults_par_x = new Canvas[(int) (GraphFunctionsFrame.MaxFunctions * 0.5)];
		functionParseResults_par_y = new Canvas[(int) (GraphFunctionsFrame.MaxFunctions * 0.5)];
		

		for (int i = 0; i < (int) (GraphFunctionsFrame.MaxFunctions * 0.5); i++) {
			final int numFunction = i + 1;
			Button functionVisible = new Button(comp, SWT.CHECK);
			functionVisible.setToolTipText(String.format(GraphFunctionsFrame.localize("tooltip.changefunctionvisibility"), 'P', numFunction));
			if (!UIinitted_par)
				functionVisible.setSelection(true);
			else
				functionVisible.setSelection(backups_par[i].visible);

			functionVisible.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					Plot();
				}
			});

			final Label functionName = new Label(comp, SWT.NO_FOCUS);
			if (numFunction < 10)
				functionName.setText("  P" + numFunction + "   = ");
			else
				functionName.setText("  P" + numFunction + " = ");
			if (!UIinitted_par)
				functionName.setBackground(defaultColors[i % defaultColors.length]);
			else
				functionName.setBackground(backups_par[i].color);

			GridData accoladeData = new GridData();
			accoladeData.widthHint = 14;
			accoladeData.heightHint = 53;
			Canvas accoladeCanvas = new Canvas(comp, SWT.NO_REDRAW_RESIZE | SWT.NO_FOCUS);
			accoladeCanvas.setLayoutData(accoladeData);
			accoladeCanvas.addPaintListener(new PaintListener() {
				@Override
				public void paintControl(PaintEvent e) {
					e.gc.drawImage(accoladeOpen, 0, 0);
				}
			});
			accoladeCanvas.update();

			functionName.setToolTipText(String.format(GraphFunctionsFrame.localize("tooltip.changefunctioncolor.par"), numFunction));

			Composite betweenAccolades = new Canvas(comp, SWT.NONE);
			GridLayout betweenAccoladesLayout = new GridLayout(3, false);
			betweenAccoladesLayout.verticalSpacing = 5;
			betweenAccoladesLayout.horizontalSpacing = 10;
			betweenAccoladesLayout.marginLeft = 0;
			betweenAccolades.setLayout(betweenAccoladesLayout);

			final Label functionNameX = new Label(betweenAccolades, SWT.NO_FOCUS);
			functionNameX.setText("X   = ");
			/*
			if (numFunction < 10)
				functionNameX.setText("X" + numFunction + "   = ");
			else
				functionNameX.setText("X" + numFunction + " = ");
			*/
			
			final Text inputTextX = new Text(betweenAccolades, SWT.SINGLE | SWT.BORDER);
			Font textFont = inputTextX.getFont();
			int bigfont_size = textFont.getFontData()[0].getHeight() + 2;
			Font big = new Font(null, new FontData(textFont.getFontData()[0].getName(), bigfont_size, SWT.NONE));
			inputTextX.setFont(big);
			inputTextX.setToolTipText(String.format(GraphFunctionsFrame.localize("tooltip.enterfunctionequotation"), 'X', numFunction));
			inputTextX.addListener(SWT.Traverse, new Listener() {
				@Override
				public void handleEvent(Event e) {
					if (e.detail == SWT.TRAVERSE_RETURN)
						Plot();

				}
			});
			GridData inputData = new GridData(SWT.FILL, SWT.FILL, true, true);
			inputData.widthHint = 250;
			inputTextX.setLayoutData(inputData);
			if (UIinitted_par)
				inputTextX.setText(backups_par[i].text_x);

			final Canvas functionParseResultX = new Canvas(betweenAccolades, SWT.NO_REDRAW_RESIZE | SWT.NO_FOCUS);

			GridData iconData = new GridData();
			iconData.widthHint = 16;
			iconData.heightHint = 16;
			functionParseResultX.setLayoutData(iconData);
			
			functionParseResultX.setData(null);
			functionParseResultX.addPaintListener(new PaintListener() {
				@Override
				public void paintControl(PaintEvent e) {
					if (functionParseResultX.getData() == null)
						return;
					Image img = (Image) functionParseResultX.getData();
					e.gc.drawImage(img, 0, 0);
				}
			});

			functionParseResultX.update();

			final Label functionNameY = new Label(betweenAccolades, SWT.NO_FOCUS);
			functionNameY.setText("Y   = ");
			/*
			if (numFunction < 10)
				functionNameY.setText("Y" + numFunction + "   = ");
			else
				functionNameY.setText("Y" + numFunction + " = ");
			*/
			//functionName.setMenu();
			final Menu menu = new Menu(functionName);
			MenuItem item = new MenuItem(menu, SWT.PUSH);
			item.setText(GraphFunctionsFrame.localize("color.color")); 
			item.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					
					ColorDialog dlg = new ColorDialog(shell);
			 		Color currentColor = functionName.getBackground();
					dlg.setRGB(currentColor.getRGB());
					dlg.setText(String.format(GraphFunctionsFrame.localize("colorchooser.functioncolor.title"), 'P', numFunction));
					RGB rgb = dlg.open();
					if (rgb != null) {
						functionName.setBackground(new Color(currentColor.getDevice(), rgb));
						Plot();
					}
					
				}
			});
			item = new MenuItem(menu, SWT.PUSH);
			item.setText("Tmin/Tmax"); 
			item.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					double[] t = new WindowSettingsPanelSwt(shell).openT(GraphSwtFrame.this, tBounds[numFunction - 1], numFunction);
					tBounds[numFunction - 1] = t;
					Plot();
					
				} 
			});
			
			functionName.addMouseListener(new MouseAdapter() { 
				@Override
				public void mouseDown(MouseEvent e) {
					menu.setVisible(true);
					
				}
			});

			final Text inputTextY = new Text(betweenAccolades, SWT.SINGLE | SWT.BORDER);
			inputTextY.setFont(big);
			inputTextY.setToolTipText(String.format(GraphFunctionsFrame.localize("tooltip.enterfunctionequotation"), 'Y', numFunction));
			inputTextY.addListener(SWT.Traverse, new Listener() {
				@Override
				public void handleEvent(Event e) {
					if (e.detail == SWT.TRAVERSE_RETURN)
						Plot();

				}
			});
			inputTextY.setLayoutData(inputData);
			if (UIinitted_par)
				inputTextY.setText(backups_par[i].text_y);

			final Canvas functionParseResultY = new Canvas(betweenAccolades, SWT.NO_REDRAW_RESIZE | SWT.NO_FOCUS);

			functionParseResultY.setLayoutData(iconData);

			functionParseResultY.setData(null);
			functionParseResultY.addPaintListener(new PaintListener() {
				@Override
				public void paintControl(PaintEvent e) {
					if (functionParseResultY.getData() == null)
						return;
					Image img = (Image) functionParseResultY.getData();
					e.gc.drawImage(img, 0, 0);
				}
			});

			functionParseResultY.update();

			accoladeCanvas = new Canvas(comp, SWT.NO_REDRAW_RESIZE | SWT.NO_FOCUS);
			accoladeCanvas.setLayoutData(accoladeData);
			accoladeCanvas.addPaintListener(new PaintListener() {
				@Override
				public void paintControl(PaintEvent e) {
					e.gc.drawImage(accoladeClose, 0, 0);
				}
			});
			accoladeCanvas.update();

			functionVisibility_par[i] = functionVisible;
			functionColors_par[i] = functionName;
			functionStrings_par_x[i] = inputTextX;
			functionStrings_par_y[i] = inputTextY;
			functionParseResults_par_x[i] = functionParseResultX;
			functionParseResults_par_y[i] = functionParseResultY;
		}

		functionStrings_par_x[0].forceFocus();

		Composite plotButtonHolder = new Composite(functionInputHolder, SWT.NONE);
		FillLayout plotButtonHolderLayout = new FillLayout();
		plotButtonHolderLayout.marginWidth = 78;
		plotButtonHolder.setLayout(plotButtonHolderLayout);

		Button plotButton = new Button(plotButtonHolder, SWT.PUSH);
		plotButton.setText("  " + GraphFunctionsFrame.localize("buttons.draw") + "  ");
		plotButton.addSelectionListener(this);

		final Composite gframe_holder = new Composite(outer, SWT.EMBEDDED);

		final Menu menu = new Menu(shell, SWT.POP_UP);
		MenuItem item = new MenuItem(menu, SWT.PUSH);
		item.setText(GraphFunctionsFrame.localize("menu.savescreenshot"));
		item.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Display.getDefault().syncExec(new Runnable() {

					@Override
					public void run() {
						saveScreenShot();

					}
				});

			}
		});

		gframe_holder.addListener(SWT.Resize, new Listener() {

			@Override
			public void handleEvent(Event e) {
				resizeGraph(gframe_holder.getSize());
			}
		});
		Menu screenShotMenu = new Menu(gframe_holder);
		MenuItem takeScreenshot = new MenuItem(screenShotMenu, SWT.PUSH);
		takeScreenshot.setText(GraphFunctionsFrame.localize("menu.savescreenshot"));
		gframe_holder.setMenu(screenShotMenu);

		gframe_holder.setLayout(fillLayout);
		Frame gframe_frame = SWT_AWT.new_Frame(gframe_holder);
		Panel panel = new Panel(new BorderLayout());
		gframe_frame.setBackground(java.awt.Color.WHITE);

		gframe_frame.add(panel);
		JRootPane root = new JRootPane();
		panel.add(root);
		graphContainer = root.getContentPane();

		if (wsettings_par == null)
			wsettings_par = new WindowSettingsParameter(true, true);

		GraphFunctionsFrame.gparam = new GraphParameter(functions_par, wsettings_par, GraphFunctionsFrame.WindowSize, false);

		GraphFunctionsFrame.gparam.addMouseListener(new java.awt.event.MouseAdapter() {

			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				if (e.getButton() == java.awt.event.MouseEvent.BUTTON3) {
					Display.getDefault().syncExec(new Runnable() {

						@Override
						public void run() {
							menu.setVisible(true);

						}
					});
				}
			}

		});

		graphContainer.add(GraphFunctionsFrame.gparam);

		fData = new FormData();
		fData.top = new FormAttachment(0);
		fData.left = new FormAttachment(functionInputHolder);
		fData.right = new FormAttachment(100);
		fData.bottom = new FormAttachment(100);
		gframe_holder.setLayoutData(fData);
	}

	private void initMenu() {
		Menu menuBar = new Menu(shell, SWT.BAR);
		Menu fileMenu, modeMenu;

		MenuItem menuItem = new MenuItem(menuBar, SWT.CASCADE);
		menuItem.setText(GraphFunctionsFrame.funcframe.MenuStrings[0]);
		fileMenu = new Menu(shell, SWT.DROP_DOWN);
		// GraphFunctionsFrame.OS = GraphFunctionsFrame.OperatingSystem.OS_MAC;
		int i = 0;
		for (String menuItemText : GraphFunctionsFrame.funcframe.FileMenuStrings) {

			if (GraphFunctionsFrame.OS == GraphFunctionsFrame.OperatingSystem.OS_MAC && i == 5)
				continue;
			else if (i == 5)
				new MenuItem(fileMenu, SWT.SEPARATOR).addSelectionListener(this);

			MenuItem item = new MenuItem(fileMenu, SWT.NONE);
			switch (i) {
			case 0:
				item.setAccelerator(SWT.MOD1 + 'N');
				item.setText(getMenuItemTextCtrl(menuItemText, "N"));

				break;
			case 1:
				item.setAccelerator(SWT.MOD1 + 'O');
				item.setText(getMenuItemTextCtrl(menuItemText, "O"));
				break;
			case 2:
				item.setAccelerator(SWT.MOD1 + 'S');
				item.setText(getMenuItemTextCtrl(menuItemText, "S"));
				break;
			case 3:
				item.setAccelerator(SWT.MOD1 + SWT.MOD2 + 'S');
				if (GraphFunctionsFrame.OS == GraphFunctionsFrame.OperatingSystem.OS_MAC)
					item.setText(String.format("%s\t%c%cS", menuItemText, 0x2318, 0x21E7));
				else
					item.setText(menuItemText + "\tCtrl+Shift+S");
				new MenuItem(fileMenu, SWT.SEPARATOR).addSelectionListener(this);
				break;
				/*
			case 4:
				item.setAccelerator(SWT.MOD3 + 'S');
				if (GraphFunctionsFrame.OS == GraphFunctionsFrame.OperatingSystem.OS_MAC)
					item.setText(String.format("%s\t%cS", menuItemText, 0x2325));
				else
					item.setText(menuItemText + "\tAlt+S");
				break;
				*/
			case 4:
				item.setAccelerator(SWT.MOD3 + SWT.F4);
				item.setText(menuItemText + "\tAlt+F4");
				break;
			}

			item.addSelectionListener(this);

			i++;
		}
		menuItem.setMenu(fileMenu);

		calcMenu = new MenuItem(menuBar, SWT.CASCADE);
		calcMenu.setText(GraphFunctionsFrame.funcframe.MenuStrings[1]);
		initCalcMenu();
		
		menuItem = new MenuItem(menuBar, SWT.CASCADE);
		menuItem.setText(GraphFunctionsFrame.funcframe.MenuStrings[2]);
		
		modeMenu = new Menu(shell, SWT.DROP_DOWN);
		MenuItem toggle = new MenuItem(modeMenu, SWT.RADIO);
		toggle.setText("y=f(x)");
		toggle.setSelection(currentMode == Mode.MODE_FUNC);
		toggle.addSelectionListener(this);
		MenuItem toggle3d = new MenuItem(modeMenu, SWT.RADIO);
		toggle3d.setText("z=f(x,y)");
		toggle3d.setSelection(currentMode == Mode.MODE_3DFUNC);
		toggle3d.addSelectionListener(this);
		MenuItem toggleParameter = new MenuItem(modeMenu, SWT.RADIO);
		toggleParameter.setText("x=f(t), y=g(t)");
		toggleParameter.setSelection(currentMode == Mode.MODE_PARAMETER);
		toggleParameter.addSelectionListener(this);
		menuItem.setMenu(modeMenu);

		menuItem = new MenuItem(menuBar, SWT.CASCADE);
		menuItem.setText(GraphFunctionsFrame.funcframe.MenuStrings[3]);
		menuItem.setMenu(getMenu(GraphFunctionsFrame.funcframe.viewMenuStrings)); 
		
		menuItem = new MenuItem(menuBar, SWT.CASCADE);
		menuItem.setText(GraphFunctionsFrame.funcframe.MenuStrings[4]);
		menuItem.setMenu(getMenu(GraphFunctionsFrame.funcframe.HelpMenuStrings));

		shell.setMenuBar(menuBar);

	}
	private void initCalcMenu()
	{
		switch (currentMode) {
		case MODE_FUNC:
			calcMenu.setMenu(getMenu(GraphFunctionsFrame.funcframe.calcMenuStrings_func));
			break;
		case MODE_3DFUNC:
			calcMenu.setMenu(getMenu(GraphFunctionsFrame.funcframe.calcMenuStrings_3dfunc));
			break;
		case MODE_PARAMETER:
			calcMenu.setMenu(getMenu(GraphFunctionsFrame.funcframe.calcMenuStrings_par));
			break;
		default:
			break;
		}
	}
	private void initIcons() {
		int[] iconSizes = { 16, 22, 24, 32, 36, 48, 64, 72, 96, 128, 192, 256 };

		icons = new ArrayList<Image>();

		for (int size : iconSizes) {
			Image img = new Image(null, GraphSwtFrame.class.getResourceAsStream(String.format("/res/icon%d.png", size)));
			// System.out.println(img.getBounds());
			icons.add(img);
		}
		Image[] icons_array = icons.toArray(new Image[icons.size()]);

		if (GraphFunctionsFrame.OS != GraphFunctionsFrame.OperatingSystem.OS_LINUX)
			shell.setImages(icons_array);

	}

	private String getMenuItemTextCtrl(String text, String after) {
		if (GraphFunctionsFrame.OS == GraphFunctionsFrame.OperatingSystem.OS_MAC)
			return String.format("%s\t%c%s", text, 0x2318, after);
		else
			return text + "\tCtrl+" + after;
	}

	private Menu getMenu(String[] menuItems) {

		Menu m = new Menu(shell, SWT.DROP_DOWN);
		for (String s : menuItems) {
			MenuItem item = new MenuItem(m, SWT.PUSH);
			item.setText(s);
			item.addSelectionListener(this);
		}
		return m;
	}

	public void Plot() {
		System.out.println("Parsing functions... ");
		switch (currentMode) {
		case MODE_FUNC:
			Parse_func();
			break;
		case MODE_3DFUNC:
			Parse_3dfunc();
			break;
		case MODE_PARAMETER:
			Parse_par();
			break;
		default:
			break;
		}
		Render();
		
		GraphFunctionsFrame.NoChangesMade = false;
		GraphFunctionsFrame.FileSaved = false;
		updateCalcPanel();
		updateTitle();
		
		System.out.println("Done");
	}
	/*
	private boolean CheckChanges()
	{
		boolean changed = false;
		for(int i = 0; i < GraphFunctionsFrame.MaxFunctions; i++)
		{
			
		}
		return changed;
	}
	*/
	public void Parse_func() {
		for (int i = 0; i < GraphFunctionsFrame.MaxFunctions; i++) {
			String input = functionStrings_func[i].getText().trim();
			Function f = new Function();
			if (input.isEmpty()) {
				functionParseResults_func[i].setData(null);
				functionParseResults_func[i].setToolTipText("");
				functionParseResults_func[i].redraw();
				f.setDraw(false);
				functions_func.set(i, f);
				continue;
			}

			if (!f.Parse(input)) {
				functionParseResults_func[i].setData((Object) errorIcon);
				functionParseResults_func[i].setToolTipText(String.format(GraphFunctionsFrame.localize("tooltip.functionparseerror"), 'Y', i + 1));
				functionParseResults_func[i].redraw();
				f.setDraw(false);
				functions_func.set(i, f);
				continue;
			}
			functionParseResults_func[i].setData((Object) okIcon);
			functionParseResults_func[i].setToolTipText(String.format(GraphFunctionsFrame.localize("tooltip.functionparsedsuccessfully"), 'Y', i + 1));
			functionParseResults_func[i].redraw();

			RGB rgb = functionColors_func[i].getBackground().getRGB();

			f.setColor(new java.awt.Color(rgb.red, rgb.green, rgb.blue));
			f.setDraw(functionVisibility_func[i].getSelection());
			// GraphFunctionsFrame.functions.add(f);
			// functions_func.add(f);
			functions_func.set(i, f);
			System.out.println("Added function Y" + (i + 1) + " with color " + rgb.red + "," + rgb.green + "," + rgb.blue);

		}
	}

	public void Parse_3dfunc() {
		for (int i = 0; i < GraphFunctionsFrame.MaxFunctions; i++) {
			String input = functionStrings_3dfunc[i].getText().trim();
			Function2Var f = new Function2Var();
			if (input.isEmpty()) {
				functionParseResults_3dfunc[i].setData(null);
				functionParseResults_3dfunc[i].setToolTipText("");
				functionParseResults_3dfunc[i].redraw();
				f.setDraw(false);
				functions_3dfunc.set(i, f);
				continue;
			}

			if (!f.Parse(input)) {
				functionParseResults_3dfunc[i].setData((Object) errorIcon);
				functionParseResults_3dfunc[i].setToolTipText(String.format(GraphFunctionsFrame.localize("tooltip.functionparseerror"), 'Z', i + 1));
				functionParseResults_3dfunc[i].redraw();
				f.setDraw(false);
				functions_3dfunc.set(i, f);
				continue;
			}
			functionParseResults_3dfunc[i].setData((Object) okIcon);
			functionParseResults_3dfunc[i].setToolTipText(String.format(GraphFunctionsFrame.localize("tooltip.functionparsedsuccessfully"), 'Z', i + 1));
			functionParseResults_3dfunc[i].redraw();

			RGB rgb = functionColors_3dfunc[i].getBackground().getRGB();

			f.setColor(new java.awt.Color(rgb.red, rgb.green, rgb.blue));
			f.setDraw(functionVisibility_3dfunc[i].getSelection());
			// functions_3dfunc.add(f);
			functions_3dfunc.set(i, f);
			System.out.println("Added function Z" + (i + 1) + " with color " + rgb.red + "," + rgb.green + "," + rgb.blue);

		}
	}

	public void Parse_par() {
		for (int i = 0; i < (int) (GraphFunctionsFrame.MaxFunctions * 0.5); i++) {
			String inputX = functionStrings_par_x[i].getText().trim();
			String inputY = functionStrings_par_y[i].getText().trim();
			ParameterFunction f = new ParameterFunction();
			
			if(tBounds[i] != null)
			{
				f.setTmin(tBounds[i][0]);
				f.setTmax(tBounds[i][1]);
			}
			
			if (inputX.isEmpty()) {
				functionParseResults_par_x[i].setData(null);
				functionParseResults_par_x[i].setToolTipText("");
				functionParseResults_par_x[i].redraw();
			} else if (!f.ParseX(inputX)) {
				functionParseResults_par_x[i].setData((Object) errorIcon);
				functionParseResults_par_x[i].setToolTipText(String.format(GraphFunctionsFrame.localize("tooltip.functionparseerror"), 'X', i + 1));
				functionParseResults_par_x[i].redraw();
			} else {
				functionParseResults_par_x[i].setData((Object) okIcon);
				functionParseResults_par_x[i].setToolTipText(String.format(GraphFunctionsFrame.localize("tooltip.functionparsedsuccessfully"), 'X', i + 1));
				functionParseResults_par_x[i].redraw();
			}

			if (inputY.isEmpty()) {
				functionParseResults_par_y[i].setData(null);
				functionParseResults_par_y[i].setToolTipText("");
				functionParseResults_par_y[i].redraw();
			} else if (!f.ParseY(inputY)) {
				functionParseResults_par_y[i].setData((Object) errorIcon);
				functionParseResults_par_y[i].setToolTipText(String.format(GraphFunctionsFrame.localize("tooltip.functionparseerror"), 'Y', i + 1));
				functionParseResults_par_y[i].redraw();
			} else {
				functionParseResults_par_y[i].setData((Object) okIcon);
				functionParseResults_par_y[i].setToolTipText(String.format(GraphFunctionsFrame.localize("tooltip.functionparsedsuccessfully"), 'Y', i + 1));
				functionParseResults_par_y[i].redraw();
			}

			if (inputX.isEmpty() || inputY.isEmpty() || f.parseError()) {
				f.setDraw(false);
				functions_par.set(i, f);
				continue;
			}

			RGB rgb = functionColors_par[i].getBackground().getRGB();

			f.setColor(new java.awt.Color(rgb.red, rgb.green, rgb.blue));
			f.setDraw(functionVisibility_par[i].getSelection());
			functions_par.set(i, f);
			System.out.println("Added function P" + (i + 1) + " with color " + rgb.red + "," + rgb.green + "," + rgb.blue);

		}

	}

	public void Render() {
		if (currentMode == GraphFunctionsFrame.Mode.MODE_FUNC) {
			GraphFunctionsFrame.gframe.Update(functions_func);
			GraphFunctionsFrame.gframe.repaint();
		} else if (currentMode == GraphFunctionsFrame.Mode.MODE_3DFUNC) {
			GraphFunctionsFrame.g3d.Update(functions_3dfunc);
			// GraphFunctionsFrame.g3d.repaint();
		} else if (currentMode == GraphFunctionsFrame.Mode.MODE_PARAMETER) {
			GraphFunctionsFrame.gparam.Update(functions_par);
			GraphFunctionsFrame.gparam.repaint();
		}

	}

	private void backupInputData() {
		switch (currentMode) {
		case MODE_FUNC:
			for (int i = 0; i < GraphFunctionsFrame.MaxFunctions; i++) {
				backups_func[i] = new InputData(functionVisibility_func[i].getSelection(), functionColors_func[i].getBackground(), functionStrings_func[i].getText());
			}
			break;
		case MODE_3DFUNC:
			for (int i = 0; i < GraphFunctionsFrame.MaxFunctions; i++) {
				backups_3dfunc[i] = new InputData(functionVisibility_3dfunc[i].getSelection(), functionColors_3dfunc[i].getBackground(), functionStrings_3dfunc[i].getText());
			}
			break;
		case MODE_PARAMETER:
			for (int i = 0; i < (int) (GraphFunctionsFrame.MaxFunctions * 0.5); i++) {
				backups_par[i] = new InputDataParameter(functionVisibility_par[i].getSelection(), functionColors_par[i].getBackground(), functionStrings_par_x[i].getText(), functionStrings_par_y[i].getText());
			}
			break;
		}
	}

	private void switchMode(Mode newMode) {
		if (currentMode == newMode)
			return;
		if (calcPanel != null) {
			if (calcPanel.isOpen())
				calcPanel.close();
			calcPanel = null;
		}
		if(wSettingsPanel != null)
		{
			if(wSettingsPanel.isOpen())
				wSettingsPanel.close();
			wSettingsPanel = null;
		}
		
		backupInputData();
		currentMode = newMode;

		
		for(Control c: shell.getChildren())
			c.dispose();	
		
		initUI();
		initCalcMenu();
		
		shell.layout();
		
		System.out.println("Switching to mode " + newMode);

		if(GraphFunctionsFrame.NoChangesMade)
		{
			Plot();
			//GraphFunctionsFrame.FileSaved = true;
			GraphFunctionsFrame.NoChangesMade = true;
		}
		else 
			Plot();
		
		updateTitle();
		
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	private void openWindowSettings()
	{
		if(wSettingsPanel != null)
		{
			if(wSettingsPanel.isOpen())
			{
				wSettingsPanel.activate();
				return;
			}
		}
		wSettingsPanel = new WindowSettingsPanelSwt(shell);
		wSettingsPanel.open(this);
	}
	public void updateWindowSettings_func(WindowSettings wsettings)
	{
		this.wsettings_func = wsettings;
		if(GraphFunctionsFrame.gframe != null)
			GraphFunctionsFrame.gframe.UpdateWindowSettings(wsettings_func);
	}
	public void updateWindowSettings_3dfunc(WindowSettings3D wsettings)
	{
		this.wsettings_3dfunc = wsettings;
		if(GraphFunctionsFrame.g3d != null)
			GraphFunctionsFrame.g3d.UpdateWindowSettings(wsettings_3dfunc);
	}
	public void updateWindowSettings_par(WindowSettingsParameter wsettings)
	{
		this.wsettings_par = wsettings;
		if(GraphFunctionsFrame.gparam != null)
			GraphFunctionsFrame.gparam.UpdateWindowSettings(wsettings_par);
	}
	private void openCalcPanel(CalcPanelSwt.Calculations calcType) {
		if (calcPanel != null) {
			if (calcPanel.isOpen()) {
				if (calcPanel.getCalcType() == calcType)
				{
					calcPanel.activate();
					return;
				}
				else {
					calcPanel.close();
					calcPanel = null;
					openCalcPanel(calcType);
					return;
				}
			}
		}
		calcPanel = new CalcPanelSwt(shell);
		if (calcType.toString().startsWith("FUNC_")) {
			Function[] functions = functions_func.toArray(new Function[GraphFunctionsFrame.MaxFunctions]);
			calcPanel.open(calcType, (Object[]) functions);
		} else if(calcType.toString().startsWith("PAR_")) {
			ParameterFunction[] functions = functions_par.toArray(new ParameterFunction[GraphFunctionsFrame.MaxFunctions]);
			calcPanel.open(calcType, (Object[]) functions);
		}
		

	}

	
	private void updateCalcPanel() {
		if (calcPanel == null)
			return;
		switch (currentMode) {
		case MODE_FUNC:
			calcPanel.updateFunctions((Object[]) functions_func.toArray(new Function[GraphFunctionsFrame.MaxFunctions]));
			break;
		case MODE_3DFUNC:
			calcPanel.updateFunctions((Object[]) functions_3dfunc.toArray(new Function2Var[GraphFunctionsFrame.MaxFunctions]));
			break;
		case MODE_PARAMETER:
			calcPanel.updateFunctions((Object[]) functions_par.toArray(new ParameterFunction[GraphFunctionsFrame.MaxFunctions]));
		default:
			break;
		}
	}

	private void updateTitle()
	{
		shell.setText(String.format("%s%s - Graph v%s", GraphFunctionsFrame.FileName, GraphFunctionsFrame.NoChangesMade ? "" : " *", GraphFunctionsFrame.version));
	}
	
	private Color toSwtColor(java.awt.Color awtColor)
	{
		return new Color(null, new RGB(awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue()));
	}
	private void ClearAll()
	{
		//if(GraphFunctionsFrame.NoChangesMade) return;
		
		if (calcPanel != null) {
			if (calcPanel.isOpen())
				calcPanel.close();
			calcPanel = null;
		}
		if(wSettingsPanel != null)
		{
			if(wSettingsPanel.isOpen())
				wSettingsPanel.close();
			wSettingsPanel = null;
		}
		

		switch (currentMode)
		{
			case MODE_FUNC:
				for(int i = 0; i < GraphFunctionsFrame.MaxFunctions; i++)
				{
					functionVisibility_func[i].setSelection(true);
					functionColors_func[i].setBackground(defaultColors[i % defaultColors.length]);
					functionStrings_func[i].setText("");
					functionParseResults_func[i].setData(null);
					functionParseResults_func[i].redraw();
				}
				UIinitted_3dfunc = false;
				UIinitted_par = false;
				backups_3dfunc = new InputData[GraphFunctionsFrame.MaxFunctions];
				backups_par = new InputDataParameter[(int) (GraphFunctionsFrame.MaxFunctions * 0.5)];
				functionStrings_func[0].forceFocus();
				break;
			case MODE_3DFUNC:
				for(int i = 0; i < GraphFunctionsFrame.MaxFunctions; i++)
				{
					functionVisibility_3dfunc[i].setSelection(true);
					functionColors_3dfunc[i].setBackground(defaultColors[i % defaultColors.length]);
					functionStrings_3dfunc[i].setText("");
					functionParseResults_3dfunc[i].setData(null);
					functionParseResults_3dfunc[i].redraw();
				}
				UIinitted_func = false;
				UIinitted_par = false;
				backups_func = new InputData[GraphFunctionsFrame.MaxFunctions];
				backups_par = new InputDataParameter[(int) (GraphFunctionsFrame.MaxFunctions * 0.5)];
				functionStrings_3dfunc[0].forceFocus();
				break;
			case MODE_PARAMETER:
				for(int i = 0; i < (int) (GraphFunctionsFrame.MaxFunctions * 0.5); i++)
				{
					functionVisibility_par[i].setSelection(true);
					functionColors_par[i].setBackground(defaultColors[i % defaultColors.length]);
					functionStrings_par_x[i].setText("");
					functionStrings_par_y[i].setText("");
					functionParseResults_par_x[i].setData(null);
					functionParseResults_par_x[i].redraw();
					functionParseResults_par_y[i].setData(null);
					functionParseResults_par_y[i].redraw();
				}
				UIinitted_func = false;
				UIinitted_3dfunc = false;
				backups_func = new InputData[GraphFunctionsFrame.MaxFunctions];
				backups_3dfunc = new InputData[GraphFunctionsFrame.MaxFunctions];
				functionStrings_par_x[0].forceFocus();
				break;
			default:
				break;
		}

		functions_func.clear();
		functions_3dfunc.clear();
		functions_par.clear();
		for (int i = 0; i < GraphFunctionsFrame.MaxFunctions; i++) {
			Function dummy_func = new Function();
			dummy_func.setDraw(false);
			functions_func.add(dummy_func);
			Function2Var dummy_3dfunc = new Function2Var();
			dummy_3dfunc.setDraw(false);
			functions_3dfunc.add(dummy_3dfunc);
			ParameterFunction dummy_par = new ParameterFunction();
			dummy_par.setDraw(false);
			functions_par.add(dummy_par);
		}
		
		if(GraphFunctionsFrame.g3d != null)
		{
			GraphFunctionsFrame.g3d.Update(functions_3dfunc);
		} 
		updateWindowSettings_func(new WindowSettings());
		updateWindowSettings_3dfunc(new WindowSettings3D());
		updateWindowSettings_par(new WindowSettingsParameter());
		
		Render();
		
		GraphFunctionsFrame.FileName = GraphFunctionsFrame.localize("files.untitled");
		GraphFunctionsFrame.FileSaved = false;
		GraphFunctionsFrame.NoChangesMade = true;
		GraphFunctionsFrame.FilePathPresent = false; 
		updateTitle();
		
	}
	private boolean SaveFile()
	{
		GraphFileWriter gfw = new GraphFileWriter(GraphFunctionsFrame.FilePath);
		gfw.setFunctions_func(functions_func);
		gfw.setFunctions_3dfunc(functions_3dfunc);
		gfw.setFunctions_par(functions_par);
		gfw.setWindowSettings_func(wsettings_func);
		gfw.setWindowSettings_3dfunc(wsettings_3dfunc);
		gfw.setWindowSettings_par(wsettings_par);
		
		try {
			boolean rt =  gfw.write();
			GraphFunctionsFrame.FileSaved = true;
			GraphFunctionsFrame.NoChangesMade = true;
			updateTitle();
			return rt;
		} catch (IOException e) {
			e.printStackTrace();
			GraphFunctionsFrame.FileSaved = false;
			updateTitle();
			return false;
		}
		
		
	}
	private boolean SaveFileAs()
	{
		FileDialog savedialog = new FileDialog(shell, SWT.SAVE);
		savedialog.setFilterNames(new String[] { GraphFunctionsFrame.localize("files.graphfiles") });
		savedialog.setFilterExtensions(new String[] { String.format("*.%s", GraphFunctionsFrame.FileExt) });
		GraphFunctionsFrame.FilePath = savedialog.open();
		if (GraphFunctionsFrame.FilePath == null) {
			return false;
		}
		if (!GraphFunctionsFrame.FilePath.endsWith("." + GraphFunctionsFrame.FileExt)) {
			GraphFunctionsFrame.FilePath += "." + GraphFunctionsFrame.FileExt;
		}
		GraphFunctionsFrame.FilePathPresent = true;
		GraphFunctionsFrame.FileName = (new File(GraphFunctionsFrame.FilePath)).getName();
		return SaveFile();
	}
	
	private boolean OpenFile(String filepath)
	{
		System.out.println("Loading " + filepath);
		GraphFileReader gfr = new GraphFileReader(filepath);
		try
		{
			if(!gfr.read())
			{
				System.out.println("Failed to open " + filepath);	
				return false;
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.out.println("Failed to open " + filepath);	
			return false;
		}
		ClearAll();
		
		
		updateWindowSettings_func(gfr.getWindowSettings_func());
		updateWindowSettings_3dfunc(gfr.getWindowSettings_3dfunc());
		updateWindowSettings_par(gfr.getWindowSettings_par());
		
		switch(currentMode)
		{
		case MODE_FUNC:
			for(short i = 0; i < GraphFunctionsFrame.MaxFunctions; i++)
			{
				Function f_func = gfr.getFunctions_func().get(i);
				if(f_func != null)
				{
					functionVisibility_func[i].setSelection(f_func.drawOn());
					functionColors_func[i].setBackground(toSwtColor(f_func.getColor()));
					functionStrings_func[i].setText(f_func.getInputString());
				}
				Function2Var f_3dfunc = gfr.getFunctions_3dfunc().get(i);
				if(f_3dfunc != null)
				{
					backups_3dfunc[i] = new InputData(f_3dfunc.drawOn(), toSwtColor(f_3dfunc.getColor()), f_3dfunc.getInputString());
				}
				else
				{
					backups_3dfunc[i] = new InputData(true, defaultColors[i % defaultColors.length], "");
				}
				
				if(i < (int) (GraphFunctionsFrame.MaxFunctions * 0.5))
				{
					ParameterFunction f_par = gfr.getFunctions_par().get(i);
					if(f_par != null)
					{
						backups_par[i] = new InputDataParameter(f_par.drawOn(), toSwtColor(f_par.getColor()), f_par.getInputStrings()[0], f_par.getInputStrings()[1]);
					}
					else
					{
						backups_par[i] = new InputDataParameter(true, defaultColors[i % defaultColors.length], "", "");
					}
				}
			}
			UIinitted_3dfunc = true;
			UIinitted_par = true;
			break;
		case MODE_3DFUNC:
			for(short i = 0; i < GraphFunctionsFrame.MaxFunctions; i++)
			{
				Function f_func = gfr.getFunctions_func().get(i);
				if(f_func != null)
				{
					backups_func[i] = new InputData(f_func.drawOn(), toSwtColor(f_func.getColor()), f_func.getInputString());
				}
				else
				{
					backups_func[i] = new InputData(true, defaultColors[i % defaultColors.length], "");
				}
				
				Function2Var f_3dfunc = gfr.getFunctions_3dfunc().get(i);
				if(f_3dfunc != null)
				{
					functionVisibility_3dfunc[i].setSelection(f_3dfunc.drawOn());
					functionColors_3dfunc[i].setBackground(toSwtColor(f_3dfunc.getColor()));
					functionStrings_3dfunc[i].setText(f_3dfunc.getInputString());
				}
				if(i < (int) (GraphFunctionsFrame.MaxFunctions * 0.5))
				{
					ParameterFunction f_par = gfr.getFunctions_par().get(i);
					if(f_par != null)
					{
						backups_par[i] = new InputDataParameter(f_par.drawOn(), toSwtColor(f_par.getColor()), f_par.getInputStrings()[0], f_par.getInputStrings()[1]);
					}
					else
					{
						backups_par[i] = new InputDataParameter(true, defaultColors[i % defaultColors.length], "", "");
					}
				}
			}
			UIinitted_func = true;
			UIinitted_par = true;
			break;
		case MODE_PARAMETER:
			for(short i = 0; i < GraphFunctionsFrame.MaxFunctions; i++)
			{
				Function f_func = gfr.getFunctions_func().get(i);
				if(f_func != null)
				{
					backups_func[i] = new InputData(f_func.drawOn(), toSwtColor(f_func.getColor()), f_func.getInputString());
				}
				else
				{
					backups_func[i] = new InputData(true, defaultColors[i % defaultColors.length], "");
				}
				
				Function2Var f_3dfunc = gfr.getFunctions_3dfunc().get(i);
				if(f_3dfunc != null)
				{
					backups_3dfunc[i] = new InputData(f_3dfunc.drawOn(), toSwtColor(f_3dfunc.getColor()), f_3dfunc.getInputString());
				}
				else
				{
					backups_3dfunc[i] = new InputData(true, defaultColors[i % defaultColors.length], "");
				}
				
				if(i < (int) (GraphFunctionsFrame.MaxFunctions * 0.5))
				{
					ParameterFunction f_par = gfr.getFunctions_par().get(i);
					if(f_par != null)
					{
						functionVisibility_par[i].setSelection(f_par.drawOn());
						functionColors_par[i].setBackground(toSwtColor(f_par.getColor()));
						functionStrings_par_x[i].setText(f_par.getInputStrings()[0]);
						functionStrings_par_y[i].setText(f_par.getInputStrings()[1]);
					}
				}
			}
			UIinitted_func = true;
			UIinitted_3dfunc = true;
			break;
		default:
			break;
		
		}
		Plot();
		GraphFunctionsFrame.FileName = new File(filepath).getName();
		GraphFunctionsFrame.FilePath = filepath;
		GraphFunctionsFrame.FilePathPresent = true;
		GraphFunctionsFrame.FileSaved = true;
		GraphFunctionsFrame.NoChangesMade = true;
		updateTitle();
		System.out.println("Done");		
		return true;
	}
	private boolean ShowOpenFileDialog() {
		FileDialog opendialog = new FileDialog(shell, SWT.OPEN);
		opendialog.setFilterNames(new String[] { GraphFunctionsFrame.localize("files.graphfiles") });
		opendialog.setFilterExtensions(new String[] { String.format("*.%s", GraphFunctionsFrame.FileExt) });
		String filepath = opendialog.open();
		if (filepath == null) {
			System.out.println("Canceled by user.");
			return false;
		}

		return OpenFile(filepath);
	}
	
	private boolean ConfirmFileChanges()
	{
		MessageBox confirmNoSave = new MessageBox(shell, SWT.ICON_QUESTION| SWT.YES | SWT.NO | SWT.CANCEL);
		confirmNoSave.setText("Graph");
		confirmNoSave.setMessage(String.format(GraphFunctionsFrame.localize("message.confirmchanges"), GraphFunctionsFrame.FileName));
		int n = confirmNoSave.open();
		switch(n)
		{
			case SWT.YES:
				if (GraphFunctionsFrame.FilePathPresent)
					return SaveFile();
				else
					return SaveFileAs();
			case SWT.NO:
					return true;
			case SWT.CANCEL: return false;
			default: return false;
		}
		
	}
	@Override
	public void widgetSelected(SelectionEvent e) {
		if (e.getSource() instanceof MenuItem) {
			MenuItem menuItemSrc = (MenuItem) e.getSource();
			String text = menuItemSrc.getText();
			if (text.indexOf('\t') >= 0)
				text = (String) text.subSequence(0, text.indexOf('\t'));
			if (text.equalsIgnoreCase(GraphFunctionsFrame.funcframe.FileMenuStrings[0])) // New
			{
				if (!GraphFunctionsFrame.FileSaved && !GraphFunctionsFrame.NoChangesMade) {
					if (!ConfirmFileChanges()) {
						return;
					}
				}
				ClearAll();
			} else if (text.equalsIgnoreCase(GraphFunctionsFrame.funcframe.FileMenuStrings[1])) // Open
			{
				System.out.println("Opening file...");
				if (!GraphFunctionsFrame.FileSaved && !GraphFunctionsFrame.NoChangesMade) {
					if (!ConfirmFileChanges()) {
						System.out.println("Canceled by user");
						return;
					}
				}
				ShowOpenFileDialog();
			} else if (text.equalsIgnoreCase(GraphFunctionsFrame.funcframe.FileMenuStrings[2])) // Save
			{
				System.out.println("Saving file...");
				if (GraphFunctionsFrame.FilePathPresent)
					SaveFile();
				else
					SaveFileAs();
			} else if (text.equalsIgnoreCase(GraphFunctionsFrame.funcframe.FileMenuStrings[3])) // Save																					// as
			{
				System.out.println("Saving file as...");
				if (!GraphFunctionsFrame.FileName.equals(GraphFunctionsFrame.localize("files.untitled")) && !GraphFunctionsFrame.FileSaved && !GraphFunctionsFrame.NoChangesMade) {
					if (!ConfirmFileChanges()) {
						System.out.println("Canceled by user");
						return;
					}
				}
				SaveFileAs();

			} else if (text.equalsIgnoreCase(GraphFunctionsFrame.funcframe.FileMenuStrings[4])) // Exit
			{
				Exit();
			} else if(text.equalsIgnoreCase(GraphFunctionsFrame.funcframe.viewMenuStrings[0])) // Windowsettings 
			{
				openWindowSettings();
			} else if (text.equalsIgnoreCase("y=f(x)") && menuItemSrc.getSelection()) {
				switchMode(Mode.MODE_FUNC);

			} else if (text.equalsIgnoreCase("z=f(x,y)") && menuItemSrc.getSelection()) {
				switchMode(Mode.MODE_3DFUNC);

			} else if (text.equalsIgnoreCase("x=f(t), y=g(t)") && menuItemSrc.getSelection()) {
				switchMode(Mode.MODE_PARAMETER);

			} else if (currentMode == Mode.MODE_FUNC && text.equalsIgnoreCase(GraphFunctionsFrame.funcframe.calcMenuStrings_func[0])) // Value
			{
				openCalcPanel(Calculations.FUNC_VALUE);
			} else if (currentMode == Mode.MODE_FUNC && text.equalsIgnoreCase(GraphFunctionsFrame.funcframe.calcMenuStrings_func[1])) // Zero
			{
				openCalcPanel(Calculations.FUNC_ZERO);
			} else if (currentMode == Mode.MODE_FUNC && text.equalsIgnoreCase(GraphFunctionsFrame.funcframe.calcMenuStrings_func[2])) // Minimum
			{
				openCalcPanel(Calculations.FUNC_MINIMUM);
			} else if (currentMode == Mode.MODE_FUNC && text.equalsIgnoreCase(GraphFunctionsFrame.funcframe.calcMenuStrings_func[3])) // Maximum
			{
				openCalcPanel(Calculations.FUNC_MAXIMUM);
			} else if (currentMode == Mode.MODE_FUNC && text.equalsIgnoreCase(GraphFunctionsFrame.funcframe.calcMenuStrings_func[4])) // Intersect
			{
				openCalcPanel(Calculations.FUNC_INTERSECT);
			} else if (currentMode == Mode.MODE_FUNC && text.equalsIgnoreCase(GraphFunctionsFrame.funcframe.calcMenuStrings_func[5])) // dy/dx
			{
				openCalcPanel(Calculations.FUNC_DYDX);
			} else if (currentMode == Mode.MODE_FUNC && text.equalsIgnoreCase(GraphFunctionsFrame.funcframe.calcMenuStrings_func[6])) // Integral
			{
				openCalcPanel(Calculations.FUNC_INTEGRAL);
			} else if (currentMode == Mode.MODE_PARAMETER  && text.equalsIgnoreCase(GraphFunctionsFrame.funcframe.calcMenuStrings_par[0])) // Value
			{
				openCalcPanel(Calculations.PAR_VALUE);
			} else if (currentMode == Mode.MODE_PARAMETER  && text.equalsIgnoreCase(GraphFunctionsFrame.funcframe.calcMenuStrings_par[1])) // dy/dx
			{
				openCalcPanel(Calculations.PAR_DYDX);
			} else if (currentMode == Mode.MODE_PARAMETER  && text.equalsIgnoreCase(GraphFunctionsFrame.funcframe.calcMenuStrings_par[2])) // dy/dt
			{
				openCalcPanel(Calculations.PAR_DYDT);
			} else if (currentMode == Mode.MODE_PARAMETER  && text.equalsIgnoreCase(GraphFunctionsFrame.funcframe.calcMenuStrings_par[3])) // dx/dt
			{
				openCalcPanel(Calculations.PAR_DXDT);
			} else if (text.equalsIgnoreCase(GraphFunctionsFrame.funcframe.HelpMenuStrings[0])) // About
			{
				new GraphAboutDialog(shell).open();
			}
		} else if (e.getSource() instanceof Button) {
			Button buttonSrc = (Button) e.getSource();
			String text = buttonSrc.getText();
			if (text.trim().equalsIgnoreCase(GraphFunctionsFrame.localize("buttons.draw"))) {
				Plot();
			}
		}

	}

	private class InputData {
		public boolean visible;
		public Color color;
		public String text;

		public InputData(boolean visible, Color color, String text) {
			this.visible = visible;
			this.color = color;
			this.text = text;
		}
	}

	private class InputDataParameter {
		public boolean visible;
		public Color color;
		public String text_x, text_y;

		public InputDataParameter(boolean visible, Color color, String text_x, String text_y) {
			this.visible = visible;
			this.color = color;
			this.text_x = text_x;
			this.text_y = text_y;
		}
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {}
}
