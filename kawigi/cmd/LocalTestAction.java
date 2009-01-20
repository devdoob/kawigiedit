package kawigi.cmd;
import kawigi.problem.*;
import kawigi.util.*;
import kawigi.editor.*;
import kawigi.properties.*;
import kawigi.language.*;
import java.io.*;
import java.awt.event.*;

/**
 *	Action implementation for actions related to local testing.
 *
 *	This includes most of the buttons across the bottom of the KawigiEdit panel.
 **/
public class LocalTestAction extends DefaultAction
{
	/**
	 *	Constructs a new LocalTestAction for the given ActID.
	 **/
	public LocalTestAction(ActID cmdid)
	{
		super(cmdid);
	}

	/**
	 *	Returns true if this action can be executed.
	 **/
	public boolean isEnabled()
	{
		if (ProblemContext.getCurrentClass() == null)
		{
			// In standalone, we can always generate code (because we ask for
			// the problem statement whenever the button is pushed).  In plugin
			// mode, we can only generate code if we already have a
			// ProblemComponent from the plugin interface.
			return cmdid == ActID.actGenerateCode && AppEnvironment.getEnvironment() != AppEnvironment.PluginMode;
		}
		return true;
	}

	/**
	 *	Runs the action!
	 **/
	public void actionPerformed(ActionEvent e)
	{
		ClassDecl cl = ProblemContext.getCurrentClass();
		CodePane codePane = Dispatcher.getCodePane();
		switch (cmdid)
		{
			case actGenerateCode:
			{
				if (AppEnvironment.getEnvironment() != AppEnvironment.PluginMode)
				{
					ProblemContext.setCurrentClass(cl = ClassDeclFactory.getClassDecl());
				}
				EditorLanguage lang = ProblemContext.getLanguage();
				Skeleton code = TemplateGenerator.getSkeleton(cl, lang);
				codePane.setContentType("text/" + lang.toString().toLowerCase());
				codePane.readdUndoListener();
				codePane.setText(code.getSource());
				codePane.setCaretPosition(code.getCaret());
				break;
			}
			case actSaveLocal:
				saveLocal();
				break;
			case actLoad:
			{
				String filename = ProblemContext.getLanguage().getFileName(cl.getName());
				File f = new File(PrefFactory.getPrefs().getWorkingDirectory(), filename);
				if (f.exists())
					try
					{
						BufferedReader in = new BufferedReader(new FileReader(f));
						String line;
						String text = in.readLine();
						while ((line = in.readLine()) != null)
							text += "\n" + line;
						in.close();
						codePane.setText(text);
					}
					catch (IOException ex)
					{
						reportError(ex, false);
					}
				break;
			}
		}
		Dispatcher.getGlobalDispatcher().UIRefresh();
	}

	/**
	 *	Saves the current problem to the local test directory.
	 **/
	public void saveLocal()
	{
		String source = Dispatcher.getCodePane().getText();
		source = source.replaceAll("<%:start-tests%>", "");
		source = source.replaceAll("<%:end-tests%>", "");
		String filename = ProblemContext.getLanguage().getFileName(ProblemContext.getCurrentClass().getName());
		File f = new File(PrefFactory.getPrefs().getWorkingDirectory(), filename);
		try
		{
			PrintWriter out = new PrintWriter(new FileWriter(f));
			String[] sourceArray = source.split("\\r?\\n");
			for (String aSourceArray : sourceArray)
				out.println(aSourceArray);
			out.flush();
			out.close();
		}
		catch (IOException ex)
		{
			reportError(ex, false);
		}
	}
}
