package lorian.graph;

import java.awt.GraphicsEnvironment;

import lorian.graph.GraphFunctionsFrame.OperatingSystem;
import lorian.graph.function.Calculate;
import lorian.graph.function.Function;
import lorian.graph.function.MathChars;
import lorian.graph.function.ParameterFunction;
import lorian.graph.function.PointXY;
import lorian.graph.function.Util;
import lorian.graph.function.VisualPoint;
import lorian.graph.function.VisualPointLocationChangeListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

public class CalcPanelSwt extends Dialog {

	private Shell calcPanel;
	private boolean isOpen = false, calculated = false;
	private Calculations calcType;

	private Object[] functions;

	private Combo funcList, funcList2;
	private Spinner x1, x2;
	private Button calcButton;
	private Label resultLabel;

	private double x1val_old, x2val_old;

	private boolean useMovablePoints = false, useFillFunction = false;
	private VisualPoint resultPoint, lowerLimitPoint, upperLimitPoint;

	private Thread calculationsThread;

	public enum Calculations {
		NONE, FUNC_VALUE, FUNC_ZERO, FUNC_MINIMUM, FUNC_MAXIMUM, FUNC_INTERSECT, FUNC_DYDX, FUNC_INTEGRAL, FUNC3D_VALUE, FUNC3D_ZERO, FUNC3D_MINIMUM, FUNC3D_MAXIMUM, FUNC3D_INTERSECT, // FUNC3D_DERIV,
																																														// FUNC3D_INTEGRAL,
		PAR_VALUE, PAR_DYDX, PAR_DYDT, PAR_DXDT;

	};

	public CalcPanelSwt(Shell parent, int style) {
		super(parent, style);
	}

	public CalcPanelSwt(Shell parent) {
		this(parent, 0);
	}

	public boolean isOpen() {
		return isOpen;
	}

	private void updateResultLabel(final String text) {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				if (text.isEmpty()) {
					resultLabel.setVisible(false);
				} else {
					resultLabel.setText(text);
					resultLabel.setVisible(true);
				}
				resultLabel.update();
				resultLabel.redraw();
			}
		});
	}

	private void updateCalcButtonText(final String text) {

		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				calcButton.setText(text);
				calcButton.update();
				calcButton.redraw();
			}
		});
	}

	private void Calculate() {
		final double x;
		final double x1val;
		final double x2val;
		final int index, index2;
		switch (calcType) {
		case FUNC_VALUE:
			x = (double) x1.getSelection() / Math.pow(10, 4);
			if (funcList.getText().isEmpty())
				return;

			index = Integer.parseInt(funcList.getText().substring(1)) - 1;
			double y = ((Function[]) functions)[index].Calc(x);
			GraphFunctionsFrame.gframe.SetVisualPointsVisible(true);
			GraphFunctionsFrame.gframe.ClearVisualPoints();
			if (Double.isNaN(y)) {
				updateResultLabel(String.format(GraphFunctionsFrame.localize("calcframe.message.nosolutions"), Util.GetString(x)));
			} else {
				resultPoint = new VisualPoint(new PointXY(x, y), index, false, true);
				GraphFunctionsFrame.gframe.AddVisualPoint(resultPoint);
				GraphFunctionsFrame.gframe.repaint();
				updateResultLabel(String.format("%s: X = %s, Y = %s", GraphFunctionsFrame.localize("calc.value"), Util.GetString(x), Util.GetString(y)));
			}

			break;
		case FUNC_ZERO:
			x1val = (double) x1.getSelection() / Math.pow(10, 4);
			x2val = (double) x2.getSelection() / Math.pow(10, 4);
			if (funcList.getText().isEmpty())
				return;
			index = Integer.parseInt(funcList.getText().substring(1)) - 1;
			GraphFunctionsFrame.gframe.FreezeMovablePoints();
			calculationsThread = new Thread(new Runnable() {
				@Override
				public void run() {
					final PointXY zeroPoint = Calculate.Zero(((Function[]) functions)[index], x1val, x2val);
					if (Double.isNaN(zeroPoint.getX())) {
						updateResultLabel(GraphFunctionsFrame.localize("calcframe.message.calczeroerror"));
					} else {
						resultPoint = new VisualPoint(zeroPoint, index, false, true);
						GraphFunctionsFrame.gframe.ClearVisualPoints();
						GraphFunctionsFrame.gframe.AddVisualPoint(resultPoint);
						GraphFunctionsFrame.gframe.repaint();
						updateResultLabel(String.format("%s: X = %s, Y = %s", GraphFunctionsFrame.localize("calc.zero"), Util.GetString(zeroPoint.getX()), Util.GetString(zeroPoint.getY())));
						updateCalcButtonText(GraphFunctionsFrame.localize("calcframe.again"));
						calculated = true;
					}
					Display.getDefault().syncExec(new Runnable() {
						@Override
						public void run() {
							calcButton.setEnabled(true);
							x1.setEnabled(true);
							x2.setEnabled(true);
							funcList.setEnabled(true);
						}
					});
				}
			});
			calculationsThread.start();
			calcButton.setEnabled(false);
			x1.setEnabled(false);
			x2.setEnabled(false);
			funcList.setEnabled(false);
			updateResultLabel(GraphFunctionsFrame.localize("calcframe.message.calculating"));
			break;
		case FUNC_MINIMUM:
			x1val = (double) x1.getSelection() / Math.pow(10, 4);
			x2val = (double) x2.getSelection() / Math.pow(10, 4);
			if (funcList.getText().isEmpty())
				return;
			index = Integer.parseInt(funcList.getText().substring(1)) - 1;
			GraphFunctionsFrame.gframe.FreezeMovablePoints();
			calculationsThread = new Thread(new Runnable() {
				@Override
				public void run() {
					final PointXY minimumPoint = Calculate.Minimum(((Function[]) functions)[index], x1val, x2val);
					resultPoint = new VisualPoint(minimumPoint, index, false, true);
					GraphFunctionsFrame.gframe.ClearVisualPoints();
					GraphFunctionsFrame.gframe.AddVisualPoint(resultPoint);
					GraphFunctionsFrame.gframe.repaint();
					updateResultLabel(String.format("%s: X = %s, Y = %s", GraphFunctionsFrame.localize("calc.min"), Util.GetString(minimumPoint.getX()), Util.GetString(minimumPoint.getY())));
					updateCalcButtonText(GraphFunctionsFrame.localize("calcframe.again"));
					calculated = true;

					Display.getDefault().syncExec(new Runnable() {
						@Override
						public void run() {
							calcButton.setEnabled(true);
							x1.setEnabled(true);
							x2.setEnabled(true);
							funcList.setEnabled(true);
						}
					});
				}
			});
			calculationsThread.start();
			calcButton.setEnabled(false);
			x1.setEnabled(false);
			x2.setEnabled(false);
			funcList.setEnabled(false);
			updateResultLabel(GraphFunctionsFrame.localize("calcframe.message.calculating"));
			break;
		case FUNC_MAXIMUM:
			x1val = (double) x1.getSelection() / Math.pow(10, 4);
			x2val = (double) x2.getSelection() / Math.pow(10, 4);
			if (funcList.getText().isEmpty())
				return;
			index = Integer.parseInt(funcList.getText().substring(1)) - 1;
			GraphFunctionsFrame.gframe.FreezeMovablePoints();
			calculationsThread = new Thread(new Runnable() {
				@Override
				public void run() {
					final PointXY maximumPoint = Calculate.Maximum(((Function[]) functions)[index], x1val, x2val);
					resultPoint = new VisualPoint(maximumPoint, index, false, true);
					GraphFunctionsFrame.gframe.ClearVisualPoints();
					GraphFunctionsFrame.gframe.AddVisualPoint(resultPoint);
					GraphFunctionsFrame.gframe.repaint();
					updateResultLabel(String.format("%s: X = %s, Y = %s", GraphFunctionsFrame.localize("calc.max"), Util.GetString(maximumPoint.getX()), Util.GetString(maximumPoint.getY())));
					updateCalcButtonText(GraphFunctionsFrame.localize("calcframe.again"));
					calculated = true;

					Display.getDefault().syncExec(new Runnable() {
						@Override
						public void run() {
							calcButton.setEnabled(true);
							x1.setEnabled(true);
							x2.setEnabled(true);
							funcList.setEnabled(true);
						}
					});
				}
			});
			calculationsThread.start();
			calcButton.setEnabled(false);
			x1.setEnabled(false);
			x2.setEnabled(false);
			funcList.setEnabled(false);
			updateResultLabel(GraphFunctionsFrame.localize("calcframe.message.calculating"));
			break;
		case FUNC_INTERSECT:
			x1val = (double) x1.getSelection() / Math.pow(10, 4);
			x2val = (double) x2.getSelection() / Math.pow(10, 4);
			if (funcList.getText().isEmpty() || funcList2.getText().isEmpty())
				return;

			index = Integer.parseInt(funcList.getText().substring(1)) - 1;
			index2 = Integer.parseInt(funcList2.getText().substring(1)) - 1;
			if (index == index2) {
				updateResultLabel(GraphFunctionsFrame.localize("calcframe.message.calcintersectsamefuncerror"));
				return;
			}
			GraphFunctionsFrame.gframe.FreezeMovablePoints();
			calculationsThread = new Thread(new Runnable() {
				@Override
				public void run() {
					final PointXY intersectPoint = Calculate.Intersect(((Function[]) functions)[index], ((Function[]) functions)[index2], x1val, x2val);
					if (Double.isNaN(intersectPoint.getX())) {
						updateResultLabel(GraphFunctionsFrame.localize("calcframe.message.calcintersecterror"));
						GraphFunctionsFrame.gframe.UnfreezeMovablePoints();
					} else {
						resultPoint = new VisualPoint(intersectPoint, index, false, true);
						GraphFunctionsFrame.gframe.ClearVisualPoints();
						GraphFunctionsFrame.gframe.AddVisualPoint(resultPoint);
						GraphFunctionsFrame.gframe.repaint();
						updateResultLabel(String.format("%s: X = %s, Y = %s", GraphFunctionsFrame.localize("calc.intersect"), Util.GetString(intersectPoint.getX()), Util.GetString(intersectPoint.getY())));
						updateCalcButtonText(GraphFunctionsFrame.localize("calcframe.again"));
						calculated = true;
					}
					Display.getDefault().syncExec(new Runnable() {
						@Override
						public void run() {
							calcButton.setEnabled(true);
							x1.setEnabled(true);
							x2.setEnabled(true);
							funcList.setEnabled(true);
							funcList2.setEnabled(true);
						}
					});
				}
			});
			calculationsThread.start();
			calcButton.setEnabled(false);
			x1.setEnabled(false);
			x2.setEnabled(false);
			funcList.setEnabled(false);
			funcList2.setEnabled(false);
			updateResultLabel(GraphFunctionsFrame.localize("calcframe.message.calculating"));
			break;
		case FUNC_DYDX:
			x = (double) x1.getSelection() / Math.pow(10, 4);
			if (funcList.getText().isEmpty())
				return;

			index = Integer.parseInt(funcList.getText().substring(1)) - 1;
			calculationsThread = new Thread(new Runnable() {
				@Override
				public void run() {
					double dydx = Calculate.DyDx(((Function[]) functions)[index], x);
					if (Double.isNaN(dydx)) {
						updateResultLabel(String.format(GraphFunctionsFrame.localize("calcframe.message.calcdydxerror"), Util.GetString(x)));
						GraphFunctionsFrame.gframe.SetVisualPointsVisible(false);
						GraphFunctionsFrame.gframe.repaint();
						Display.getDefault().syncExec(new Runnable() {
							@Override
							public void run() {
								calcButton.setEnabled(true);
								x1.setEnabled(true);
								funcList.setEnabled(true);
							}
						});
					} else {
						GraphFunctionsFrame.gframe.SetVisualPointsVisible(true);
						String result = String.format("%s: %s", GraphFunctionsFrame.localize("calc.deriv"), Util.GetString(dydx));
						resultPoint = new VisualPoint(new PointXY(x, ((Function[]) functions)[index].Calc(x)), index, false, false, result);
						GraphFunctionsFrame.gframe.ClearVisualPoints();
						GraphFunctionsFrame.gframe.AddVisualPoint(resultPoint);
						GraphFunctionsFrame.gframe.repaint();
						updateResultLabel(result);
						Display.getDefault().syncExec(new Runnable() {
							@Override
							public void run() {
								calcButton.setEnabled(true);
								x1.setEnabled(true);
								funcList.setEnabled(true);
							}
						});
					}
				}
			});

			calculationsThread.start();
			calcButton.setEnabled(false);
			x1.setEnabled(false);
			funcList.setEnabled(false);
			updateResultLabel(GraphFunctionsFrame.localize("calcframe.message.calculating"));
			break;
		case FUNC_INTEGRAL:
			x1val = (double) x1.getSelection() / Math.pow(10, 4);
			x2val = (double) x2.getSelection() / Math.pow(10, 4);
			if (funcList.getText().isEmpty())
				return;
			index = Integer.parseInt(funcList.getText().substring(1)) - 1;
			GraphFunctionsFrame.gframe.FreezeMovablePoints();
			calculationsThread = new Thread(new Runnable() {
				@Override
				public void run() {
					double integral = Calculate.Integral(((Function[]) functions)[index], x1val, x2val);
					GraphFunctionsFrame.gframe.SetVisualPointsVisible(false);
					GraphFunctionsFrame.gframe.repaint();
					updateResultLabel(String.format("%cf(x)dx: %s", MathChars.Integral.getCode(), Util.GetString(integral)));
					updateCalcButtonText(GraphFunctionsFrame.localize("calcframe.again"));
					calculated = true;

					Display.getDefault().syncExec(new Runnable() {
						@Override
						public void run() {
							calcButton.setEnabled(true);
							x1.setEnabled(true);
							x2.setEnabled(true);
							funcList.setEnabled(true);
						}
					});
				}
			});
			calculationsThread.start();
			calcButton.setEnabled(false);
			x1.setEnabled(false);
			x2.setEnabled(false);
			funcList.setEnabled(false);
			updateResultLabel(GraphFunctionsFrame.localize("calcframe.message.calculating"));
			break;
		case PAR_VALUE:
			x = (double) x1.getSelection() / Math.pow(10, 4);
			if (funcList.getText().isEmpty())
				return;

			index = Integer.parseInt(funcList.getText().substring(1)) - 1;
			PointXY xy = ((ParameterFunction[]) functions)[index].Calc(x);
			GraphFunctionsFrame.gparam.SetVisualPointsVisible(true);
			GraphFunctionsFrame.gparam.ClearVisualPoints();
			if (Double.isNaN(xy.getX()) || Double.isNaN(xy.getY())) {
				updateResultLabel(String.format(GraphFunctionsFrame.localize("calcframe.message.nosolutions.par"), Util.GetString(x)));
			} else {

				resultPoint = new VisualPoint(xy, index, false, true);
				GraphFunctionsFrame.gparam.AddVisualPoint(resultPoint);
				GraphFunctionsFrame.gparam.repaint();

				updateResultLabel(String.format("%s: T = %s, X = %s, Y = %s", GraphFunctionsFrame.localize("calc.value"), Util.GetString(x), Util.GetString(xy.getX()), Util.GetString(xy.getY())));
			}

			break;
		case PAR_DYDX:
			x = (double) x1.getSelection() / Math.pow(10, 4);
			if (funcList.getText().isEmpty())
				return;

			index = Integer.parseInt(funcList.getText().substring(1)) - 1;
			calculationsThread = new Thread(new Runnable() {
				@Override
				public void run() {
					double dydx = Calculate.DyDx(((ParameterFunction[]) functions)[index], x);
					if (Double.isNaN(dydx)) {
						updateResultLabel(String.format(GraphFunctionsFrame.localize("calcframe.message.calcderiverror"), 'y', 'x', Util.GetString(x)));
						GraphFunctionsFrame.gparam.SetVisualPointsVisible(false);
						GraphFunctionsFrame.gparam.repaint();
					} else {
						GraphFunctionsFrame.gparam.SetVisualPointsVisible(true);
						String result = String.format("%s: %s", GraphFunctionsFrame.localize("calc.deriv"), Util.GetString(dydx));
						resultPoint = new VisualPoint(((ParameterFunction[]) functions)[index].Calc(x), index, false, false, result);
						GraphFunctionsFrame.gparam.ClearVisualPoints();
						GraphFunctionsFrame.gparam.AddVisualPoint(resultPoint);
						GraphFunctionsFrame.gparam.repaint();
						updateResultLabel(result);
						Display.getDefault().syncExec(new Runnable() {
							@Override
							public void run() {
								calcButton.setEnabled(true);
								x1.setEnabled(true);
								funcList.setEnabled(true);
							}
						});
					}
				}
			});
			calculationsThread.start();
			calcButton.setEnabled(false);
			x1.setEnabled(false);
			funcList.setEnabled(false);
			updateResultLabel(GraphFunctionsFrame.localize("calcframe.message.calculating"));
			break;
		case PAR_DYDT:
			x = (double) x1.getSelection() / Math.pow(10, 4);
			if (funcList.getText().isEmpty())
				return;

			index = Integer.parseInt(funcList.getText().substring(1)) - 1;
			calculationsThread = new Thread(new Runnable() {
				@Override
				public void run() {
					double dydt = Calculate.DyDt(((ParameterFunction[]) functions)[index], x);
					if (Double.isNaN(dydt)) {
						updateResultLabel(String.format(GraphFunctionsFrame.localize("calcframe.message.calcderiverror"), 'y', 't', Util.GetString(x)));
						GraphFunctionsFrame.gparam.SetVisualPointsVisible(false);
						GraphFunctionsFrame.gparam.repaint();
					} else {
						GraphFunctionsFrame.gparam.SetVisualPointsVisible(true);
						String result = String.format("%s: %s", GraphFunctionsFrame.localize("calc.dydt"), Util.GetString(dydt));
						resultPoint = new VisualPoint(((ParameterFunction[]) functions)[index].Calc(x), index, false, false, result);
						GraphFunctionsFrame.gparam.ClearVisualPoints();
						GraphFunctionsFrame.gparam.AddVisualPoint(resultPoint);
						GraphFunctionsFrame.gparam.repaint();
						updateResultLabel(result);
						Display.getDefault().syncExec(new Runnable() {
							@Override
							public void run() {
								calcButton.setEnabled(true);
								x1.setEnabled(true);
								funcList.setEnabled(true);
							}
						});
					}
				}
			});
			calculationsThread.start();
			calcButton.setEnabled(false);
			x1.setEnabled(false);
			funcList.setEnabled(false);
			updateResultLabel(GraphFunctionsFrame.localize("calcframe.message.calculating"));
			break;
		case PAR_DXDT:
			x = (double) x1.getSelection() / Math.pow(10, 4);
			if (funcList.getText().isEmpty())
				return;

			index = Integer.parseInt(funcList.getText().substring(1)) - 1;
			calculationsThread = new Thread(new Runnable() {
				@Override
				public void run() {
					double dxdt = Calculate.DxDt(((ParameterFunction[]) functions)[index], x);
					if (Double.isNaN(dxdt)) {
						updateResultLabel(String.format(GraphFunctionsFrame.localize("calcframe.message.calcderiverror"), 'x', 't', Util.GetString(x)));
						GraphFunctionsFrame.gparam.SetVisualPointsVisible(false);
						GraphFunctionsFrame.gparam.repaint();
					} else {
						GraphFunctionsFrame.gparam.SetVisualPointsVisible(true);
						String result = String.format("%s: %s", GraphFunctionsFrame.localize("calc.dxdt"), Util.GetString(dxdt));
						resultPoint = new VisualPoint(((ParameterFunction[]) functions)[index].Calc(x), index, false, false, result);
						GraphFunctionsFrame.gparam.ClearVisualPoints();
						GraphFunctionsFrame.gparam.AddVisualPoint(resultPoint);
						GraphFunctionsFrame.gparam.repaint();
						updateResultLabel(result);
						Display.getDefault().syncExec(new Runnable() {
							@Override
							public void run() {
								calcButton.setEnabled(true);
								x1.setEnabled(true);
								funcList.setEnabled(true);
							}
						});
					}
				}
			});
			calculationsThread.start();
			calcButton.setEnabled(false);
			x1.setEnabled(false);
			funcList.setEnabled(false);
			updateResultLabel(GraphFunctionsFrame.localize("calcframe.message.calculating"));
			break;
		default:
			break;

		}

	}

	public void updateFunctions(Object[] functions) {
		if (!isOpen)
			return;
		updateFunctions_internal(functions);
	}

	private void updateFunctions_internal(Object[] functions) {
		this.functions = functions;
		if (calcType.toString().startsWith("FUNC_")) {
			updateFunctionsList_func((Function[]) functions);
			if (useFillFunction)
				initMovablePointsIntegral();
			else if (useMovablePoints)
				initMovablePoints();
		} else if (calcType.toString().startsWith("PAR_")) {
			updateFunctionsList_par((ParameterFunction[]) functions);

		}

	}

	private void updateFunctionsList_func(Function[] functions) {
		String[] items = new String[functions.length];
		int j = 0;
		for (int i = 0; i < functions.length; i++) {
			if (functions[i] == null)
				continue;
			if (functions[i].drawOn()) {
				items[i] = "Y" + (i + 1);
				j++;
			}
		}
		String[] items2 = new String[j];
		for (int i = 0, k = 0; i < items.length; i++) {
			if (items[i] != null) {
				items2[k++] = items[i];
			}
		}
		funcList.setItems(items2);
		funcList.select(0);
		funcList.update();
		if (funcList2 != null) {
			funcList2.setItems(items2);
			funcList2.select(1);
			funcList2.update();
		}
		resetMovablePoints();
	}

	private void updateFunctionsList_par(ParameterFunction[] functions) {
		String[] items = new String[functions.length];
		int j = 0;
		for (int i = 0; i < functions.length; i++) {
			if (functions[i] == null)
				continue;
			if (functions[i].drawOn()) {
				items[i] = "P" + (i + 1);
				j++;
			}
		}
		String[] items2 = new String[j];
		for (int i = 0, k = 0; i < items.length; i++) {
			if (items[i] != null) {
				items2[k++] = items[i];
			}
		}
		funcList.setItems(items2);
		funcList.select(0);
		funcList.update();
		if (funcList2 != null) {
			funcList2.setItems(items2);
			funcList2.select(1);
			funcList2.update();
		}
		resetMovablePoints();
	}

	private void addFuncList(Composite calcPanel, boolean addOne) {
		Label label = new Label(calcPanel, SWT.NONE);
		label.setText(addOne ? String.format(GraphFunctionsFrame.localize("calcframe.functionnumber") + ":", 1) : GraphFunctionsFrame.localize("calcframe.function") + ":");
		GridData data = new GridData(SWT.RIGHT, SWT.TOP, true, false);
		label.setLayoutData(data);

		funcList = new Combo(calcPanel, SWT.READ_ONLY | SWT.H_SCROLL);
		// updateFunctionsList_func((Function[]) functions);
		updateFunctions_internal(functions);
		data = new GridData(SWT.LEFT, SWT.TOP, true, false);
		if (GraphFunctionsFrame.OS == GraphFunctionsFrame.OperatingSystem.OS_WINDOWS)
			data.widthHint = 25;
		else
			data.widthHint = 50;
		data.horizontalIndent = 10;
		funcList.setLayoutData(data);
		funcList.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				resetMovablePoints();
				calculated = false;
				updateResultLabel("");
				updateCalcButtonText(GraphFunctionsFrame.localize("calcframe.calculate"));
			}
		});
	}

	private void addFuncList2(Composite calcPanel) {
		Label label = new Label(calcPanel, SWT.NONE);
		label.setText(String.format(GraphFunctionsFrame.localize("calcframe.functionnumber") + ":", 2));
		GridData data = new GridData(SWT.RIGHT, SWT.TOP, true, false);
		label.setLayoutData(data);

		funcList2 = new Combo(calcPanel, SWT.READ_ONLY | SWT.H_SCROLL);
		// updateFunctionsList_func((Function[]) functions);
		updateFunctions_internal(functions);
		data = new GridData(SWT.LEFT, SWT.TOP, true, false);
		if (GraphFunctionsFrame.OS == GraphFunctionsFrame.OperatingSystem.OS_WINDOWS)
			data.widthHint = 25;
		else
			data.widthHint = 50;
		data.horizontalIndent = 10;
		funcList2.setLayoutData(data);
		funcList2.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				resetMovablePoints();
				calculated = false;
				updateResultLabel("");
				updateCalcButtonText(GraphFunctionsFrame.localize("calcframe.calculate"));
			}
		});
	}

	private void addXorT(Composite calcPanel) {
		Label label = new Label(calcPanel, SWT.NONE);
		if (calcType.toString().startsWith("PAR_"))
			label.setText("T:");
		else
			label.setText("X:");
		GridData data = new GridData(SWT.RIGHT, SWT.BEGINNING, true, false);
		label.setLayoutData(data);

		x1 = new Spinner(calcPanel, SWT.BORDER | SWT.H_SCROLL);
		x1.setDigits(4);
		x1.setMinimum(Integer.MIN_VALUE);
		x1.setMaximum(Integer.MAX_VALUE);

		x1.setIncrement((int) Math.pow(10, 4));
		data = new GridData(SWT.LEFT, SWT.BEGINNING, true, false);
		data.widthHint = 65;
		data.horizontalIndent = 10;
		data.verticalSpan = 0;
		x1.setLayoutData(data);
	}

	private void addLimits(Composite calcPanel) {
		Label label = new Label(calcPanel, SWT.NONE);
		label.setText(GraphFunctionsFrame.localize("calcframe.lowerlimit") + ": ");
		GridData data = new GridData(SWT.RIGHT, SWT.BEGINNING, true, false);
		label.setLayoutData(data);

		x1 = new Spinner(calcPanel, SWT.BORDER | SWT.H_SCROLL);
		x1.setDigits(4);
		x1.setMinimum(Integer.MIN_VALUE);
		x1.setMaximum(Integer.MAX_VALUE);

		x1.setIncrement((int) Math.pow(10, 4));
		data = new GridData(SWT.LEFT, SWT.BEGINNING, true, false);
		data.widthHint = 65;
		data.horizontalIndent = 10;
		data.verticalSpan = 0;
		x1.setLayoutData(data);
		x1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				if (calculated) {
					calculated = false;
					updateResultLabel("");
					updateCalcButtonText(GraphFunctionsFrame.localize("calcframe.calculate"));
					resetMovablePoints();
				}

				double value = x1.getSelection() / Math.pow(10, 4);
				int index = Integer.parseInt(funcList.getText().substring(1)) - 1;
				Function f = (Function) functions[index];
				GraphFunctionsFrame.gframe.SetMovableVisualPointLocationByLabel(GraphFunctionsFrame.localize("calcframe.lowerlimit"), new PointXY(value, f.Calc(value)));

			}
		});

		label = new Label(calcPanel, SWT.NONE);
		label.setText(GraphFunctionsFrame.localize("calcframe.upperlimit") + ": ");
		data = new GridData(SWT.RIGHT, SWT.BEGINNING, true, false);
		label.setLayoutData(data);

		x2 = new Spinner(calcPanel, SWT.BORDER | SWT.H_SCROLL);
		x2.setDigits(4);
		x2.setMinimum(Integer.MIN_VALUE);
		x2.setMaximum(Integer.MAX_VALUE);

		x2.setIncrement((int) Math.pow(10, 4));
		data = new GridData(SWT.LEFT, SWT.BEGINNING, true, false);
		data.widthHint = 65;
		data.horizontalIndent = 10;
		data.verticalSpan = 0;
		x2.setLayoutData(data);
		x2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				if (calculated) {
					calculated = false;
					updateResultLabel("");
					updateCalcButtonText(GraphFunctionsFrame.localize("calcframe.calculate"));
					resetMovablePoints();
				}

				double value = x2.getSelection() / Math.pow(10, 4);
				int index = Integer.parseInt(funcList.getText().substring(1)) - 1;
				Function f = (Function) functions[index];
				GraphFunctionsFrame.gframe.SetMovableVisualPointLocationByLabel(GraphFunctionsFrame.localize("calcframe.upperlimit"), new PointXY(value, f.Calc(value)));

			}
		});

	}

	private void initMovablePoints() {
		WindowSettings wsettings = GraphFunctionsFrame.gframe.getWindowSettings();
		double lowx = (wsettings.getXmax() - wsettings.getXmin()) / 2 - 2 + wsettings.getXmin(), upx = (wsettings.getXmax() - wsettings.getXmin()) / 2 + 2 + wsettings.getXmin();
		initMovablePoints(lowx, upx);
	}

	private void initMovablePoints(double lowx, double upx) {
		useMovablePoints = true;
		if (funcList.getItemCount() == 0) {
			GraphFunctionsFrame.gframe.ClearVisualPoints();
			return;
		}

		GraphFunctionsFrame.gframe.ClearVisualPoints();
		GraphFunctionsFrame.gframe.SetVisualPointsVisible(true);
		int funcindex = Integer.parseInt(funcList.getText().substring(1)) - 1;
		Function f = (Function) functions[funcindex];

		lowerLimitPoint = new VisualPoint(new PointXY(lowx, f.Calc(lowx)), funcindex, true, false, GraphFunctionsFrame.localize("calcframe.lowerlimit"));
		upperLimitPoint = new VisualPoint(new PointXY(upx, f.Calc(upx)), funcindex, true, false, GraphFunctionsFrame.localize("calcframe.upperlimit"));
		GraphFunctionsFrame.gframe.AddVisualPoint(lowerLimitPoint);
		GraphFunctionsFrame.gframe.AddVisualPoint(upperLimitPoint);

		x1.setSelection((int) (lowx * Math.pow(10, x1.getDigits())));
		x2.setSelection((int) (upx * Math.pow(10, x2.getDigits())));
		lowerLimitPoint.addLocationChangedListener(new VisualPointLocationChangeListener() {
			@Override
			public void OnLocationChange(final VisualPoint p) {
				lowerLimitPoint = p;
				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						x1.setSelection((int) (p.getPoint().getX() * Math.pow(10, x1.getDigits())));
					}
				});
				if (lowerLimitPoint.getPoint().getX() > upperLimitPoint.getPoint().getX()) {
					lowerLimitPoint.setPoint(upperLimitPoint.getPoint());
				}

				calculated = false;
				updateResultLabel("");
				updateCalcButtonText(GraphFunctionsFrame.localize("calcframe.calculate"));

			}
		});
		upperLimitPoint.addLocationChangedListener(new VisualPointLocationChangeListener() {
			@Override
			public void OnLocationChange(final VisualPoint p) {
				upperLimitPoint = p;
				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						x2.setSelection((int) (p.getPoint().getX() * Math.pow(10, x2.getDigits())));
					}
				});
				if (upperLimitPoint.getPoint().getX() < lowerLimitPoint.getPoint().getX()) {
					upperLimitPoint.setPoint(lowerLimitPoint.getPoint());
				}

				calculated = false;
				updateResultLabel("");
				updateCalcButtonText(GraphFunctionsFrame.localize("calcframe.calculate"));
			}
		});

	}

	private void initMovablePointsIntegral() {
		WindowSettings wsettings = GraphFunctionsFrame.gframe.getWindowSettings();
		double lowx = (wsettings.getXmax() - wsettings.getXmin()) / 2 - 2 + wsettings.getXmin(), upx = (wsettings.getXmax() - wsettings.getXmin()) / 2 + 2 + wsettings.getXmin();
		initMovablePointsIntegral(lowx, upx);
	}

	private void initMovablePointsIntegral(double lowx, double upx) {
		useMovablePoints = true;
		useFillFunction = true;
		if (funcList.getItemCount() == 0) {
			GraphFunctionsFrame.gframe.SetFillFunction(false);
			GraphFunctionsFrame.gframe.ClearVisualPoints();
			return;
		}

		GraphFunctionsFrame.gframe.SetFillFunction(true);
		GraphFunctionsFrame.gframe.SetFillFunctionIndex(Integer.parseInt(funcList.getText().substring(1)) - 1);

		GraphFunctionsFrame.gframe.ClearVisualPoints();
		GraphFunctionsFrame.gframe.SetVisualPointsVisible(true);
		int funcindex = Integer.parseInt(funcList.getText().substring(1)) - 1;
		Function f = (Function) functions[funcindex];

		lowerLimitPoint = new VisualPoint(new PointXY(lowx, f.Calc(lowx)), funcindex, true, false, GraphFunctionsFrame.localize("calcframe.lowerlimit"));
		upperLimitPoint = new VisualPoint(new PointXY(upx, f.Calc(upx)), funcindex, true, false, GraphFunctionsFrame.localize("calcframe.upperlimit"));
		GraphFunctionsFrame.gframe.AddVisualPoint(lowerLimitPoint);
		GraphFunctionsFrame.gframe.AddVisualPoint(upperLimitPoint);
		GraphFunctionsFrame.gframe.SetFillLowerLimit(lowerLimitPoint.getPoint().getX());
		GraphFunctionsFrame.gframe.SetFillUpperLimit(upperLimitPoint.getPoint().getX());

		x1.setSelection((int) (lowx * Math.pow(10, x1.getDigits())));
		x2.setSelection((int) (upx * Math.pow(10, x2.getDigits())));
		lowerLimitPoint.addLocationChangedListener(new VisualPointLocationChangeListener() {
			@Override
			public void OnLocationChange(final VisualPoint p) {
				lowerLimitPoint = p;
				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						x1.setSelection((int) (p.getPoint().getX() * Math.pow(10, x1.getDigits())));
					}
				});
				if (lowerLimitPoint.getPoint().getX() > upperLimitPoint.getPoint().getX()) {
					lowerLimitPoint.setPoint(upperLimitPoint.getPoint());
				}
				GraphFunctionsFrame.gframe.SetFillLowerLimit(lowerLimitPoint.getPoint().getX());

				if (calculated) {
					calculated = false;
					updateResultLabel("");
					resetMovablePoints();
					updateCalcButtonText(GraphFunctionsFrame.localize("calcframe.calculate"));
				}
			}
		});
		upperLimitPoint.addLocationChangedListener(new VisualPointLocationChangeListener() {
			@Override
			public void OnLocationChange(final VisualPoint p) {
				upperLimitPoint = p;
				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						x2.setSelection((int) (p.getPoint().getX() * Math.pow(10, x2.getDigits())));
					}
				});
				if (upperLimitPoint.getPoint().getX() < lowerLimitPoint.getPoint().getX()) {
					upperLimitPoint.setPoint(lowerLimitPoint.getPoint());
				}
				GraphFunctionsFrame.gframe.SetFillUpperLimit(upperLimitPoint.getPoint().getX());
				if (calculated) {
					calculated = false;
					updateResultLabel("");
					resetMovablePoints();
					updateCalcButtonText(GraphFunctionsFrame.localize("calcframe.calculate"));
				}
			}
		});
	}

	private void addCalculateButton(Composite calcPanel) {
		// new Label(calcPanel, SWT.NONE).setLayoutData(new GridData(SWT.RIGHT,
		// SWT.BEGINNING, true, false));
		calcButton = new Button(calcPanel, SWT.PUSH);
		calcButton.setText(GraphFunctionsFrame.localize("calcframe.calculate"));
		GridData data = new GridData(SWT.CENTER, SWT.TOP, true, true);
		// data.minimumHeight = 20;
		// data.verticalIndent = 2;
		calcButton.setLayoutData(data);
		calcButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!calculated)
					Calculate();
				else {
					resetMovablePoints();
					updateResultLabel("");
					calcButton.setText(GraphFunctionsFrame.localize("calcframe.calculate"));
					calculated = false;
				}
			}
		});
	}

	private void addCloseButton(Composite calcPanel) {
		Button closeButton = new Button(calcPanel, SWT.PUSH);
		closeButton.setText(GraphFunctionsFrame.localize("buttons.close"));
		GridData data = new GridData(SWT.RIGHT, SWT.TOP, true, true);
		closeButton.setLayoutData(data);
		closeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				close();
			}
		});
	}

	private void addResultLabel(Composite calcPanel) {
		resultLabel = new Label(calcPanel, SWT.CENTER);
		resultLabel.setVisible(false);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		resultLabel.setLayoutData(data);
	}

	private void resetMovablePoints() {
		if (useMovablePoints) {
			x1val_old = x1.getSelection() / Math.pow(10, x1.getDigits());
			x2val_old = x2.getSelection() / Math.pow(10, x2.getDigits());

			GraphFunctionsFrame.gframe.ClearVisualPoints();
			Display.getDefault().syncExec(new Runnable() {

				@Override
				public void run() {
					if (useFillFunction)
						initMovablePointsIntegral(x1val_old, x2val_old);
					else
						initMovablePoints(x1val_old, x2val_old);

					// x1.setSelection(x1val_old);
					// x2.setSelection(x2val_old);

				}
			});

		}
	}

	private void initUI(Object[] functions) {
		GridLayout calcPanelLayout = new GridLayout(1, false);
		calcPanel.setLayout(calcPanelLayout);

		Composite itemComp = new Composite(calcPanel, SWT.NONE);

		GridLayout itemsLayout = new GridLayout(2, false);
		// calcPanelLayout.numColumns = 3;
		itemsLayout.marginTop = 5;
		itemsLayout.marginBottom = 15;
		itemsLayout.marginLeft = 10;
		itemsLayout.marginRight = 10;

		itemsLayout.verticalSpacing = 10;

		itemComp.setLayout(itemsLayout);

		if (calcType.toString().startsWith("FUNC_"))
			this.functions = (Function[]) functions;
		else if (calcType.toString().startsWith("PAR_"))
			this.functions = (ParameterFunction[]) functions;

		switch (calcType) {
		case FUNC_VALUE:
		case PAR_VALUE:
			calcPanel.setText(GraphFunctionsFrame.localize("calcframe.calculate") + ": " + GraphFunctionsFrame.localize("calc.value"));
			addFuncList(itemComp, false);
			addXorT(itemComp);
			break;
		case FUNC_ZERO:
			calcPanel.setText(GraphFunctionsFrame.localize("calcframe.calculate") + ": " + GraphFunctionsFrame.localize("calc.zero"));
			addFuncList(itemComp, false);
			addLimits(itemComp);
			initMovablePoints();
			break;
		case FUNC_MINIMUM:
			calcPanel.setText(GraphFunctionsFrame.localize("calcframe.calculate") + ": " + GraphFunctionsFrame.localize("calc.min"));
			addFuncList(itemComp, false);
			addLimits(itemComp);
			initMovablePoints();
			break;
		case FUNC_MAXIMUM:
			calcPanel.setText(GraphFunctionsFrame.localize("calcframe.calculate") + ": " + GraphFunctionsFrame.localize("calc.max"));
			addFuncList(itemComp, false);
			addLimits(itemComp);
			initMovablePoints();
			break;
		case FUNC_INTERSECT:
			calcPanel.setText(GraphFunctionsFrame.localize("calcframe.calculate") + ": " + GraphFunctionsFrame.localize("calc.intersect"));
			addFuncList(itemComp, true);
			addFuncList2(itemComp);
			addLimits(itemComp);
			initMovablePoints();
			break;
		case FUNC_DYDX:
		case PAR_DYDX:
			calcPanel.setText(GraphFunctionsFrame.localize("calcframe.calculate") + ": " + GraphFunctionsFrame.localize("calc.deriv"));
			addFuncList(itemComp, false);
			addXorT(itemComp);
			break;
		case PAR_DYDT:
			calcPanel.setText(GraphFunctionsFrame.localize("calcframe.calculate") + ": " + GraphFunctionsFrame.localize("calc.dydt"));
			addFuncList(itemComp, false);
			addXorT(itemComp);
			break;
		case PAR_DXDT:
			calcPanel.setText(GraphFunctionsFrame.localize("calcframe.calculate") + ": " + GraphFunctionsFrame.localize("calc.dxdt"));
			addFuncList(itemComp, false);
			addXorT(itemComp);
			break;
		case FUNC_INTEGRAL:
			calcPanel.setText(String.format("%s: %cf(x)dx", GraphFunctionsFrame.localize("calcframe.calculate"), MathChars.Integral.getCode()));
			addFuncList(itemComp, false);
			addLimits(itemComp);
			initMovablePointsIntegral();
			break;
		default:
			break;
		}
		
		if (GraphFunctionsFrame.OS == OperatingSystem.OS_LINUX) {
			Composite buttons = new Composite(calcPanel, SWT.NONE);
			buttons.setLayout(new GridLayout(2, false));
			GridData data = new GridData(SWT.CENTER, SWT.TOP, true, true);
			buttons.setLayoutData(data);
			addCalculateButton(buttons);
			addCloseButton(buttons); 
		}
		else
			addCalculateButton(calcPanel);
		
		addResultLabel(calcPanel);
	}

	public Calculations getCalcType() {
		if (!isOpen)
			return Calculations.NONE;
		return this.calcType;
	}

	public void open(Calculations calcType, Object[] functions) {
		if (isOpen)
			return;
		System.out.println("Opening calculate panel: " + calcType);
		this.calcType = calcType;
		Shell parent = getParent();
		calcPanel = new Shell(parent, SWT.CLOSE);

		calcPanel.setLayout(new FillLayout());
		calcPanel.setSize(300, 200);
		calcPanel.setMinimumSize(260, 200);

		java.awt.Rectangle clientArea = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0].getDefaultConfiguration().getBounds();
		Rectangle rect = calcPanel.getBounds();
		int x = clientArea.x + (clientArea.width - rect.width) / 2;
		int y = clientArea.y + (clientArea.height - rect.height) / 2;
		calcPanel.setLocation(x, y);

		if (GraphSwtFrame.icons != null) {
			Image[] icons_array = GraphSwtFrame.icons.toArray(new Image[GraphSwtFrame.icons.size()]);
			calcPanel.setImages(icons_array);
		}
		calcPanel.addListener(SWT.Close, new Listener() {
			@Override
			public void handleEvent(Event e) {
				onClose();
			}
		});

		initUI(functions);
		isOpen = true;
		calcPanel.pack();
		calcPanel.open();
	}

	public void onClose() {
		if (calculationsThread != null)
			if (calculationsThread.isAlive()) {
				calculationsThread.interrupt();

			}
		System.out.println("Closing calculate panel");
		isOpen = false;
		if (GraphFunctionsFrame.gframe != null) {
			GraphFunctionsFrame.gframe.ClearVisualPoints();
			GraphFunctionsFrame.gframe.SetVisualPointsVisible(false);
			GraphFunctionsFrame.gframe.SetFillFunction(false);
		}
		if (GraphFunctionsFrame.gparam != null) {
			GraphFunctionsFrame.gparam.ClearVisualPoints();
			GraphFunctionsFrame.gparam.SetVisualPointsVisible(false);
			GraphFunctionsFrame.gparam.SetFillFunction(false);
		}
	}

	public void close() {
		onClose();
		calcPanel.close();
		calcPanel.dispose();
	}

	public void activate() {
		if (!isOpen)
			return;
		if (calcPanel != null)
			calcPanel.forceActive();
	}

}
