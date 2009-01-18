package kawigi.widget;
import kawigi.cmd.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.beans.*;
import java.io.*;

/**
 *	A simple panel that has fields to enter a font face and size.
 **/
public class FilePanel extends JPanel implements ActionListener
{
	/**
	 *	Text field that the user can enter the file path into.
	 **/
	private ActionTextField fileField;
	/**
	 *	Button that brings up the file dialog.
	 **/
	private JButton browseButton;
	
	/**
	 *	Constructs a new FontPanel linked to the given Action.
	 **/
	public FilePanel(Action a)
	{
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		fileField = new ActionTextField(a);
		fileField.setColumns(20);
		browseButton = new JButton("Browse");
		browseButton.addActionListener(this);
		add(fileField);
		add(browseButton);
	}
	
	/**
	 *	Notifies us that the "Browse" button was pushed.
	 **/
	public void actionPerformed(ActionEvent e)
	{
		JFileChooser fileChooser = Dispatcher.getFileChooser();
		int oldmode = fileChooser.getFileSelectionMode();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
			fileField.setText(fileChooser.getSelectedFile().getPath());
		fileChooser.setFileSelectionMode(oldmode);
	}
}
