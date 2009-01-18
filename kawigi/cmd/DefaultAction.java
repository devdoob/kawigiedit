package kawigi.cmd;
import javax.swing.*;
import java.util.*;
import java.beans.*;
import java.awt.*;
import java.awt.event.*;

/**
 *	Superclass of all KawigiEdit actions.
 **/
public abstract class DefaultAction extends AbstractAction
{
	// Constant names for properties recognized specifically by KawigiEdit
	// actions that aren't from Java's Action interface.
	/**
	 *	Image property for icon if it should show a larger (24x24 instead of
	 *	16x16) icon.
	 **/
	public static final String LARGE_ICON = "LargeIcon";
	/**
	 *	Color property for the currently selected color in a color-selection
	 *	control.
	 **/
	public static final String COLOR = "Color";
	/**
	 *	Boolean visibility property to show or hide controls.
	 **/
	public static final String VISIBLE = "Visible";
	/**
	 *	String property to override the text of a text box.
	 **/
	public static final String TEXT = "Text";
	/**
	 *	Boolean property of the state of a check box or something similar.
	 **/
	public static final String SELECTED = "Selected";
	/**
	 *	Font property stored for a kawigi.widget.FontPanel
	 **/
	public static final String FONT = "Font";
	/**
	 *	SpinnerModel property (provides an object that implements SpinnerModel)
	 **/
	public static final String SPINNER_MODEL = "SpinnerModel";
	/**
	 *	Value of a spinner - the type depends on the SpinnerModel implementation
	 **/
	public static final String SPINNER_VALUE = "SpinnerValue";
	
	/**
	 *	The ActID for this Action.
	 **/
	protected ActID cmdid;
	
	/**
	 *	Visibility state
	 **/
	protected boolean visible;
	
	/**
	 *	Last set value for enable property.
	 **/
	protected boolean enableSet;
	
	/**
	 *	Last set value for visible property.
	 **/
	protected boolean visibleSet;
	
	/**
	 *	Constructor for DefaultAction - requires an ActID.
	 *	
	 *	It also sets a bunch of properties based on the values in the ActID.
	 **/
	protected DefaultAction(ActID cmdid)
	{
		this.cmdid = cmdid;
		if (cmdid.label != null)
			putValue(NAME, cmdid.label);
		if (cmdid.tooltip != null)
			putValue(SHORT_DESCRIPTION, cmdid.tooltip);
		if (cmdid.iconFile != null)
			putValue(SMALL_ICON, new ImageIcon(cmdid.iconFile.replaceAll("\\?", "16")));
		if (cmdid.accelerator != null)
			putValue(ACCELERATOR_KEY, cmdid.accelerator);
		if (cmdid.mnemonic != null)
			putValue(MNEMONIC_KEY, cmdid.mnemonic);
		visible = true;
		enabled = true;
		enableSet = false;
		visibleSet = false;
	}
	
	/**
	 *	Refreshes the values of all properties.
	 **/
	public void UIRefresh()
	{
		Object[] keys = getKeys();
		for (int i=0; i<keys.length; i++)
		{
			Object setVal = super.getValue(keys[i].toString());
			Object realVal = getValue(keys[i].toString());
			if (setVal != realVal && (setVal == null || realVal == null || !setVal.equals(realVal)))
				putValue(keys[i].toString(), realVal);
		}
		// Yeah, this is a bit of a hack to make sure that controls know what
		// state they're in and Actions don't *have* to override isEnabled()
		// and isVisible().
		boolean tempEnabled = isEnabled();
		boolean tempVisible = isVisible();
		if (!enableSet)
		{
			enableSet = true;
			setEnabled(!tempEnabled);
		}
		if (!visibleSet)
		{
			visibleSet = true;
			setVisible(!tempVisible);
		}
		setEnabled(tempEnabled);
		setVisible(tempVisible);
	}
	
	/**
	 *	returns the ActID for this Action.
	 **/
	public ActID getID()
	{
		return cmdid;
	}
	
	/**
	 *	Override to allow the control to hide dynamically.
	 *	
	 *	This won't work on any control, but it will work on many of the controls
	 *	in kawigi.widget.
	 **/
	public boolean isVisible()
	{
		return visible;
	}
	
	/**
	 *	Sets the visibility property.
	 **/
	public void setVisible(boolean newValue)
	{
		boolean oldValue = visible;
		if (oldValue != newValue)
		{
	    	visible = newValue;
	    	firePropertyChange(VISIBLE, Boolean.valueOf(oldValue), Boolean.valueOf(newValue));
		}
	}
	
	/**
	 *	Must override to define what the action <i>does</i> when it launches
	 *	an event.
	 **/
	public abstract void actionPerformed(ActionEvent e);
	
	/**
	 *	Prints a stack trace to the console and if it's not a warning, brings
	 *	up an error dialog.
	 **/
	protected void reportError(Throwable t, boolean warning)
	{
		t.printStackTrace();
		if (!warning)
			try
			{
				JOptionPane.showMessageDialog(Dispatcher.getWindow(), t, "Error: " + cmdid + " in " + getClass(), JOptionPane.ERROR_MESSAGE);
			}
			catch (HeadlessException ex)
			{
			}
	}
	
	/**
	 *	Determines if this action is equal to another one.
	 **/
	public boolean equals(Object o)
	{
		return getClass().equals(o.getClass()) && ((DefaultAction)o).cmdid == cmdid;
	}
}
