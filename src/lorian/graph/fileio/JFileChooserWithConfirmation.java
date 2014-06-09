package lorian.graph.fileio;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import lorian.graph.GraphFunctionsFrame;

public class JFileChooserWithConfirmation extends JFileChooser {
	private static final long serialVersionUID = -1143953136496778913L;

	public JFileChooserWithConfirmation(File currentDirectory)
	{
		super(currentDirectory);		
	}
	public JFileChooserWithConfirmation(String currentFilePath)
	{
		super(currentFilePath);
	}

	
	@Override 
    public void approveSelection(){
		File f = getSelectedFile();
        if(f.exists() && getDialogType() == SAVE_DIALOG)
        {
			int n = JOptionPane.showConfirmDialog (this, String.format(GraphFunctionsFrame.localize("message.confirmoverwrite_0") + '\n' + GraphFunctionsFrame.localize("message.confirmoverwrite_1"), f.getName()), "Graph", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if(n == JOptionPane.YES_OPTION)
			{
				super.approveSelection();
			}
			else return;
		}
        else
        	super.approveSelection();
	}

}
