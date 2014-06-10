package lorian.graph;

import java.awt.GraphicsEnvironment;

import lorian.graph.GraphFunctionsFrame.Mode;
import lorian.graph.function.ParameterFunction;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

public class WindowSettingsPanelSwt extends Dialog {

	private Shell wsettingsShell;
	private boolean isOpen;
	
	private WindowSettings settings_func;
	private WindowSettings3D settings_3dfunc;
	private WindowSettingsParameter settings_par;
	
	private Spinner xMin, xMax, yMin, yMax, zMin, zMax; //, tStep, tMin, tMax; 
	private Button autoCalcY, grid;
	
	private Mode mode;
	private GraphSwtFrame swtFrame;
	
	public WindowSettingsPanelSwt(Shell parent)
	{
		this(parent, 0);
	}
	
	public WindowSettingsPanelSwt(Shell parent, int style) 
	{
		super(parent, style);
	}
	
	private void Apply()
	{
		switch (mode) {
		case MODE_FUNC:
			swtFrame.updateWindowSettings_func(settings_func);
			break;

		default:
			break;
		}
	}
	public void Update()
	{
		switch (mode) {
		case MODE_FUNC:
			xMin.setSelection( (int) (settings_func.getXmin() * Math.pow(10, 4)));
			xMax.setSelection( (int) (settings_func.getXmax() * Math.pow(10, 4)));
			yMin.setSelection( (int) (settings_func.getYmin() * Math.pow(10, 4)));
			yMax.setSelection( (int) (settings_func.getYmax() * Math.pow(10, 4)));
			grid.setSelection(settings_func.gridOn());
			autoCalcY.setSelection(settings_func.autoCalcY());
			break;
		case MODE_PARAMETER:
			xMin.setSelection( (int) (settings_par.getXmin() * Math.pow(10, 4)));
			xMax.setSelection( (int) (settings_par.getXmax() * Math.pow(10, 4)));
			yMin.setSelection( (int) (settings_par.getYmin() * Math.pow(10, 4)));
			yMax.setSelection( (int) (settings_par.getYmax() * Math.pow(10, 4)));
			grid.setSelection(settings_par.gridOn());
			autoCalcY.setSelection(settings_par.autoCalcY());
			break;
		default:
			break;
		}
	}
	private Spinner addSpinner(Composite comp, String labelText, double initialValue)
	{
		Label text = new Label(comp, SWT.NONE);
		text.setText(labelText);
		GridData data = new GridData(SWT.CENTER, SWT.CENTER, true, false);
		text.setLayoutData(data);
		
		
		Spinner sp = new Spinner(comp, SWT.BORDER | SWT.H_SCROLL);
		sp.setDigits(4);
		sp.setMinimum(Integer.MIN_VALUE);
		sp.setMaximum(Integer.MAX_VALUE);
		sp.setIncrement((int) Math.pow(10, 4));
		sp.setSelection( (int) (initialValue * Math.pow(10, 4))); 
		
		data = new GridData(SWT.LEFT, SWT.CENTER, true, false);
		data.widthHint = 80;
		sp.setLayoutData(data);
		return sp;
	}
	private Button addCheckBox(Composite comp, String labelText, boolean initialValue)
	{
		Label text = new Label(comp, SWT.NONE);
		text.setText(labelText);
		GridData data = new GridData(SWT.CENTER, SWT.CENTER, true, false);
		text.setLayoutData(data);
		
		Button checkBox = new Button(comp, SWT.CHECK);
		data = new GridData(SWT.LEFT, SWT.CENTER, true, false);
		checkBox.setLayoutData(data);
		checkBox.setSelection(initialValue);
		return checkBox;
	}
	private void initUI()
	{
		GridLayout shellLayout = new GridLayout(1, false);
		shellLayout.verticalSpacing = 0;
		wsettingsShell.setLayout(shellLayout); 
		Composite comp = new Composite(wsettingsShell, SWT.NONE);
		GridLayout layout = new GridLayout(2, true);
		layout.verticalSpacing = 6;
		layout.marginHeight = 15;
		layout.marginLeft = 15;
		layout.marginBottom = 5;
		layout.marginRight = 15;
		comp.setLayout(layout);
		
		switch(mode)
		{
			case MODE_FUNC:
				settings_func = swtFrame.wsettings_func;
				xMin = addSpinner(comp, "Xmin:", settings_func.getXmin());
				xMin.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						settings_func.setXmin(xMin.getSelection() / Math.pow(10, 4));
						xMax.setMinimum(xMin.getSelection()+(int)Math.pow(10, 4));
						swtFrame.updateWindowSettings_func(settings_func);
						if(settings_func.autoCalcY())
						{
							settings_func = GraphFunctionsFrame.gframe.getWindowSettings();
							yMin.setSelection((int) (settings_func.getYmin() * Math.pow(10, 4)));
							yMax.setSelection((int) (settings_func.getYmin() * Math.pow(10, 4)));
						}
					}
				});
				xMax = addSpinner(comp, "Xmax:", settings_func.getXmax());
				xMax.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						settings_func.setXmax(xMax.getSelection() / Math.pow(10, 4));
						xMin.setMaximum(xMax.getSelection()-(int)Math.pow(10, 4));
						swtFrame.updateWindowSettings_func(settings_func);
						if(settings_func.autoCalcY())
						{
							settings_func = GraphFunctionsFrame.gframe.getWindowSettings();
							yMin.setSelection((int) (settings_func.getYmin() * Math.pow(10, 4)));
							yMax.setSelection((int) (settings_func.getYmin() * Math.pow(10, 4)));
						}
					}
				});
				yMin = addSpinner(comp, "Ymin:", settings_func.getYmin());
				yMin.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						settings_func.setYmin(yMin.getSelection() / Math.pow(10, 4));
						yMax.setMinimum(yMin.getSelection()+(int)Math.pow(10, 4));
						swtFrame.updateWindowSettings_func(settings_func);
					}
				});
				yMax = addSpinner(comp, "Ymax:", settings_func.getYmax());
				yMax.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						settings_func.setYmax(yMax.getSelection() / Math.pow(10, 4));
						yMin.setMaximum(yMax.getSelection()-(int)Math.pow(10, 4));
						swtFrame.updateWindowSettings_func(settings_func);
					}
				});
				yMin.setEnabled(!settings_func.autoCalcY());
				yMax.setEnabled(!settings_func.autoCalcY());
				grid = addCheckBox(comp, GraphFunctionsFrame.localize("settings.window.grid"), settings_func.gridOn());
				grid.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						settings_func.setGrid( ((Button) e.getSource()).getSelection());
						swtFrame.updateWindowSettings_func(settings_func);
					}
				});
				autoCalcY = addCheckBox(comp, GraphFunctionsFrame.localize("settings.window.autocalcy"), settings_func.autoCalcY());
				autoCalcY.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						settings_func.setAutoCalcY( ((Button) e.getSource()).getSelection());
						yMin.setEnabled(!settings_func.autoCalcY());
						yMax.setEnabled(!settings_func.autoCalcY());
						swtFrame.updateWindowSettings_func(settings_func);
						yMin.setSelection((int) (settings_func.getYmin() * Math.pow(10, 4)));
						yMax.setSelection((int) (settings_func.getYmax() * Math.pow(10, 4)));
					}
				});
				
				xMax.setMinimum(xMin.getSelection()+(int)Math.pow(10, 4));
				xMin.setMaximum(xMax.getSelection()-(int)Math.pow(10, 4)); 
				yMax.setMinimum(yMin.getSelection()+(int)Math.pow(10, 4));
				yMin.setMaximum(yMax.getSelection()-(int)Math.pow(10, 4));
				break;
			case MODE_3DFUNC:
				settings_3dfunc = swtFrame.wsettings_3dfunc;
				xMin = addSpinner(comp, "Xmin:", settings_3dfunc.getXmin());
				xMin.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						settings_3dfunc.setXmin(xMin.getSelection() / Math.pow(10, 4));
						xMax.setMinimum(xMin.getSelection()-(int)Math.pow(10, 4));
						swtFrame.updateWindowSettings_3dfunc(settings_3dfunc);
					}
				});
				xMax = addSpinner(comp, "Xmax:", settings_3dfunc.getXmax());
				xMax.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						settings_3dfunc.setXmax(xMax.getSelection() / Math.pow(10, 4));
						xMin.setMaximum(xMax.getSelection()+(int)Math.pow(10, 4));
						swtFrame.updateWindowSettings_3dfunc(settings_3dfunc);
					}
				});
				yMin = addSpinner(comp, "Ymin:", settings_3dfunc.getYmin());
				yMin.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						settings_3dfunc.setYmin(yMin.getSelection() / Math.pow(10, 4));
						yMax.setMinimum(yMin.getSelection()-(int)Math.pow(10, 4));
						swtFrame.updateWindowSettings_3dfunc(settings_3dfunc);
					}
				});
				yMax = addSpinner(comp, "Ymax:", settings_3dfunc.getYmax());
				yMax.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						settings_3dfunc.setYmax(yMax.getSelection() / Math.pow(10, 4));
						yMin.setMaximum(yMax.getSelection()+(int)Math.pow(10, 4));
						swtFrame.updateWindowSettings_3dfunc(settings_3dfunc);
					}
				});
				zMin = addSpinner(comp, "Zmin:", settings_3dfunc.getZmin());
				zMin.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						settings_3dfunc.setZmin(zMin.getSelection() / Math.pow(10, 4));
						zMin.setMaximum(zMax.getSelection()-(int)Math.pow(10, 4));
						swtFrame.updateWindowSettings_3dfunc(settings_3dfunc);
					}
				});
				zMax = addSpinner(comp, "Zmax:", settings_3dfunc.getZmax());
				zMax.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						settings_3dfunc.setZmax(zMax.getSelection() / Math.pow(10, 4));
						zMin.setMaximum(zMax.getSelection()+(int)Math.pow(10, 4));
						swtFrame.updateWindowSettings_3dfunc(settings_3dfunc);
					}
				});
				
				xMax.setMinimum(xMin.getSelection()+(int)Math.pow(10, 4));
				xMin.setMaximum(xMax.getSelection()-(int)Math.pow(10, 4));
				yMax.setMinimum(yMin.getSelection()+(int)Math.pow(10, 4));
				yMin.setMaximum(yMax.getSelection()-(int)Math.pow(10, 4));
				zMax.setMinimum(zMin.getSelection()+(int)Math.pow(10, 4));
				zMin.setMaximum(zMax.getSelection()-(int)Math.pow(10, 4));
				
				/*
				yMin.setEnabled(!settings_3dfunc.autoCalcY());
				yMax.setEnabled(!settings_3dfunc.autoCalcY());
				grid = addCheckBox(comp, GraphFunctionsFrame.Translate("settings.window.grid"), settings_3dfunc.gridOn());
				grid.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						settings_3dfunc.setGrid( ((Button) e.getSource()).getSelection());
						swtFrame.updateWindowSettings_3dfunc(settings_3dfunc);
					}
				});
				*/
				/*
				autoCalcY = addCheckBox(comp, GraphFunctionsFrame.Translate("settings.window.autocalcy"), settings_3dfunc.autoCalcY());
				autoCalcY.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						settings_3dfunc.setAutoCalcY( ((Button) e.getSource()).getSelection());
						yMin.setEnabled(!settings_3dfunc.autoCalcY());
						yMax.setEnabled(!settings_3dfunc.autoCalcY());
						swtFrame.updateWindowSettings_3dfunc(settings_3dfunc);
						yMin.setSelection((int) (settings_3dfunc.getYmin() * Math.pow(10, 4)));
						yMax.setSelection((int) (settings_3dfunc.getYmax() * Math.pow(10, 4)));
					}
				});
				*/
				break;
			case MODE_PARAMETER:
				settings_par = swtFrame.wsettings_par;
				/*
				tMin = addSpinner(comp, "Tmin:", settings_par.getTmin());
				tMin.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						settings_par.setTmin(tMin.getSelection() / Math.pow(10, 4));
						swtFrame.updateWindowSettings_par(settings_par);
					}
				});
				tMax = addSpinner(comp, "Tmax:", settings_par.getTmax());
				tMax.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						settings_par.setTmax(tMax.getSelection() / Math.pow(10, 4));
						swtFrame.updateWindowSettings_par(settings_par);
					}
				});
				*/
				/*
				tStep = addSpinner(comp, "Tstep:", settings_par.getTstep());
				tStep.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						settings_par.setTstep(tStep.getSelection() / Math.pow(10, 4));
						swtFrame.updateWindowSettings_par(settings_par);
					}
				});
				tStep.setEnabled(!settings_par.AutoCalcTStep());
				*/
				
				xMin = addSpinner(comp, "Xmin:", settings_par.getXmin());
				xMin.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						settings_par.setXmin(xMin.getSelection() / Math.pow(10, 4));
						xMax.setMinimum(xMin.getSelection()+(int)Math.pow(10, 4));
						swtFrame.updateWindowSettings_par(settings_par);
						if(settings_par.autoCalcY())
						{
							settings_par = (WindowSettingsParameter) GraphFunctionsFrame.gparam.getWindowSettings();
							yMin.setSelection((int) (settings_par.getYmin() * Math.pow(10, 4)));
							yMax.setSelection((int) (settings_par.getYmin() * Math.pow(10, 4)));
						}
					}
				});
				xMax = addSpinner(comp, "Xmax:", settings_par.getXmax());
				xMax.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						settings_par.setXmax(xMax.getSelection() / Math.pow(10, 4));
						xMin.setMaximum(xMax.getSelection()-(int)Math.pow(10, 4));
						swtFrame.updateWindowSettings_par(settings_par);
						if(settings_par.autoCalcY())
						{
							settings_par = (WindowSettingsParameter) GraphFunctionsFrame.gparam.getWindowSettings();
							yMin.setSelection((int) (settings_par.getYmin() * Math.pow(10, 4)));
							yMax.setSelection((int) (settings_par.getYmin() * Math.pow(10, 4)));
						}
					}
				});
				yMin = addSpinner(comp, "Ymin:", settings_par.getYmin());
				yMin.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						settings_par.setYmin(yMin.getSelection() / Math.pow(10, 4));
						yMax.setMinimum(yMin.getSelection()+(int)Math.pow(10, 4));
						swtFrame.updateWindowSettings_par(settings_par);
					}
				});
				yMax = addSpinner(comp, "Ymax:", settings_par.getYmax());
				yMax.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						settings_par.setYmax(yMax.getSelection() / Math.pow(10, 4));
						yMin.setMaximum(yMax.getSelection()-(int)Math.pow(10, 4));
						swtFrame.updateWindowSettings_par(settings_par);
					}
				});
				yMin.setEnabled(!settings_par.autoCalcY());
				yMax.setEnabled(!settings_par.autoCalcY());
				grid = addCheckBox(comp, GraphFunctionsFrame.localize("settings.window.grid"), settings_par.gridOn());
				grid.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						settings_par.setGrid( ((Button) e.getSource()).getSelection());
						swtFrame.updateWindowSettings_par(settings_par);
					}
				});
				autoCalcY = addCheckBox(comp, GraphFunctionsFrame.localize("settings.window.autocalcy"), settings_par.autoCalcY());
				autoCalcY.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						settings_par.setAutoCalcY( ((Button) e.getSource()).getSelection());
						yMin.setEnabled(!settings_par.autoCalcY());
						yMax.setEnabled(!settings_par.autoCalcY());
						swtFrame.updateWindowSettings_par(settings_par);
						yMin.setSelection((int) (settings_par.getYmin() * Math.pow(10, 4)));
						yMax.setSelection((int) (settings_par.getYmax() * Math.pow(10, 4)));
					}
				});
				xMax.setMinimum(xMin.getSelection()+(int)Math.pow(10, 4));
				xMin.setMaximum(xMax.getSelection()-(int)Math.pow(10, 4));
				yMax.setMinimum(yMin.getSelection()+(int)Math.pow(10, 4));
				yMin.setMaximum(yMax.getSelection()-(int)Math.pow(10, 4));
				/*
				autoCalcTstep = addCheckBox(comp, GraphFunctionsFrame.Translate("settings.window.autocalctstep"), settings_par.AutoCalcTStep());
				autoCalcTstep .addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						settings_par.setAutoCalcTStep( ((Button) e.getSource()).getSelection());
						tStep.setEnabled(!settings_par.AutoCalcTStep());
						swtFrame.updateWindowSettings_par(settings_par);
						tStep.setSelection((int) (settings_par.getTstep() * Math.pow(10, 4)));
					}
				});
				*/
				break;
			default:
				break;
		}
		
		GridData data = new GridData(SWT.CENTER, SWT.TOP, true, false);
		Button okButton = new Button(wsettingsShell, SWT.PUSH);
		okButton.setText(GraphFunctionsFrame.localize("optionpane.ok")); 
		data.widthHint = 70;
		okButton.setLayoutData(data);
		okButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Apply();
				close();	
			}
		});
	}
	public void open(GraphSwtFrame swtFrame)
	{
		Shell parent = getParent();
		wsettingsShell = new Shell(parent, SWT.CLOSE);
		/*
		if(swtFrame.currentMode == Mode.MODE_PARAMETER)
			wsettingsShell.setSize(325, 305);
		else
		*/
			wsettingsShell.setSize(325, 260);
		
		//wsettingsShell.setMinimumSize(325, 260);
		wsettingsShell.setImages(parent.getImages());
		wsettingsShell.setText(GraphFunctionsFrame.localize("view.windowsettings"));
		
		java.awt.Rectangle clientArea = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0].getDefaultConfiguration().getBounds();
		Rectangle rect = wsettingsShell.getBounds();
		int x = clientArea.x + (clientArea.width  - rect.width) / 2;
		int y = clientArea.y + (clientArea.height - rect.height) / 2;
		wsettingsShell.setLocation(x, y);
		
		wsettingsShell.addListener(SWT.Close, new Listener() {
			@Override
			public void handleEvent(Event e) {
				onClose();
			}
		});
		
		this.swtFrame = swtFrame;
		this.mode = this.swtFrame.currentMode;
		
		initUI();
		
		isOpen  = true;
		wsettingsShell.pack();
		wsettingsShell.open();
	}
	public double[] openT(GraphSwtFrame swtFrame, double[] oldT, int numFunction)
	{
		final Shell shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		 
		final ParameterFunction function = new ParameterFunction();
		if(oldT != null)
		{
			function.setTmin(oldT[0]);
			function.setTmax(oldT[1]);
		}
		GridLayout shellLayout = new GridLayout(1, false);
		shellLayout.verticalSpacing = 0;
		shell.setLayout(shellLayout); 
		Composite comp = new Composite(shell, SWT.NONE);
		GridLayout layout = new GridLayout(2, true);
		layout.verticalSpacing = 6;
		layout.marginHeight = 10;
		layout.marginLeft = 10;
		layout.marginBottom = 10;
		layout.marginRight = 10;
		comp.setLayout(layout);
		
		xMin = addSpinner(comp, "Tmin:", function.getTmin());
		xMin.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				function.setTmin(xMin.getSelection() / Math.pow(10, 4));
				xMax.setMinimum(xMin.getSelection()+(int)Math.pow(10, 4));
			}
		});
		xMax = addSpinner(comp, "Tmax:", function.getTmax());
		xMax.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				function.setTmax(xMax.getSelection() / Math.pow(10, 4));
				xMin.setMaximum(xMax.getSelection()-(int)Math.pow(10, 4));
			}
		});
		
		GridData data = new GridData(SWT.CENTER, SWT.TOP, true, false);
		Button okButton = new Button(shell, SWT.PUSH);
		okButton.setText(GraphFunctionsFrame.localize("optionpane.ok"));
		data.widthHint = 70;
		okButton.setLayoutData(data);
		okButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.close();
				shell.dispose();
			}
		});
		
		shell.setText(String.format(GraphFunctionsFrame.localize("tmintmax.title"), numFunction));
		shell.pack();
		
		java.awt.Rectangle clientArea = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0].getDefaultConfiguration().getBounds();
		Rectangle rect = shell.getBounds();
		int x = clientArea.x + (clientArea.width  - rect.width) / 2;
		int y = clientArea.y + (clientArea.height - rect.height) / 2;
		shell.setLocation(x, y);
		
		shell.open();
		shell.forceActive();
		while(!shell.isDisposed()) { 
			if (!getParent().getDisplay().readAndDispatch()) getParent().getDisplay().sleep(); 
		} 
		
		return new double[] { function.getTmin(), function.getTmax() };
	}
	public boolean isOpen()
	{
		return this.isOpen;
	}
	
	public void onClose()
	{
		this.swtFrame = null;
		isOpen = false;
	}
	public void close()
	{
		onClose();
		wsettingsShell.close();
		wsettingsShell.dispose();
	}
	public void activate()
	{
		if(!isOpen) return;
		if(wsettingsShell != null)
			wsettingsShell.forceActive();
	}

}
