package kawigi.cmd;
import kawigi.problem.*;
import kawigi.util.*;
import kawigi.editor.*;
import kawigi.properties.*;
import kawigi.language.*;
import javax.swing.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;

/**
 *	Action implementation for actions related to local testing.
 *
 *	This includes most of the buttons across the bottom of the KawigiEdit panel.
 **/
public class LocalTestAction extends DefaultAction
{
	/**
	 *	Global process - we only allow one process to be running from KawigiEdit
	 *	at a time, so we don't go crazy messing things up on people's machines.
	 **/
	private static ProcessContainer proc;
	
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
			if ((cmdid == ActID.actGenerateCode && AppEnvironment.getEnvironment() != AppEnvironment.PluginMode) || cmdid == ActID.actOpenLocal)
				return true;
			return false;
		}
		if (cmdid == ActID.actKillProcess)
			return proc != null && !proc.isDone();
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
				CodePane testCodePane = Dispatcher.getTestCodePane();
				testCodePane.setContentType("text/" + lang.toString().toLowerCase());
				testCodePane.readdUndoListener();
				testCodePane.setText(lang.getTestGenerator().getCode(cl));
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
			case actRunTests:
				saveLocal();
				try
				{
					if (compileLocal())
					{
						Dispatcher.getCompileComponent().println("Compiling finished");
						Dispatcher.getTabbedPane().setSelectedComponent(Dispatcher.getOutputComponent());
						
						if (proc == null || proc.isDone())
						{
							Process p = Runtime.getRuntime().exec(ProblemContext.getLanguage().getRunCommand(cl.getName(), PrefFactory.getPrefs().getWorkingDirectory().getPath()), null, PrefFactory.getPrefs().getWorkingDirectory());
							proc = new ProcessContainer(p, Dispatcher.getOutputComponent(), Dispatcher.getOutputComponent());
							proc.start();
						}
						else
						{
							Dispatcher.getOutputComponent().println("Error: Can't start new process while another is running.");
						}
					}
					else
					{
						Dispatcher.getCompileComponent().println("Compiling errors");
						Dispatcher.getTabbedPane().setSelectedComponent(Dispatcher.getCompileComponent());
					}
				}
				catch (Exception ex)
				{
					reportError(ex, false);
				}
				break;
			case actKillProcess:
				if (proc != null && !proc.isDone())
					proc.kill();
				// On occasion, we don't actually successfully kill the process,
				// and trying again probably won't help.  This leaves the user
				// in a state where they can't do any local compilation and
				// testing until they close down the arena and open it and log
				// back in again, hardly a state you want to be in for a match,
				// regardless of how rare it might be.  In this case, we've done
				// due diligence to kill the process but the OS or the program
				// the user wrote won't let us, so we just nullify the proc so
				// that the user can continue to work anyways.
				proc = null;
				break;
			case actOpenLocal:
				if (Dispatcher.getFileChooser().showOpenDialog(Dispatcher.getTabbedPane()) == JFileChooser.APPROVE_OPTION)
				{
					File f = Dispatcher.getFileChooser().getSelectedFile();
					try
					{
						BufferedReader inFile = new BufferedReader(new FileReader(f));
						String text = "";
						String line;
						while ((line = inFile.readLine()) != null)
							text += line + "\n";
						inFile.close();
						String filename = f.getName();
						EditorLanguage lang = LanguageFactory.getLanguage(filename.substring(filename.lastIndexOf('.')+1));
						CodePane localCodePane = Dispatcher.getLocalCodePane();
						localCodePane.setContentType("text/" + lang.toString().toLowerCase());
						localCodePane.setText(text);
						((JViewport)localCodePane.getParent()).setViewPosition(new Point(0, 0));
					}
					catch (IOException ex)
					{
						Dispatcher.getLocalCodePane().setText("IOException thrown!");
					}
					Dispatcher.getTabbedPane().setSelectedComponent(Dispatcher.getLocalCodeEditorPanel());
				}
				break;
		}
		Dispatcher.getGlobalDispatcher().UIRefresh();
	}
	
	/**
	 *	Saves the current problem to the local test directory.
	 **/
	public void saveLocal()
	{
		String source = Dispatcher.getCodePane().getText();
		source = source.replace("<%:testing-code%>", Dispatcher.getTestCodePane().getText());
		source = source.replaceAll("<%:start-tests%>", "");
		source = source.replaceAll("<%:end-tests%>", "");
		String filename = ProblemContext.getLanguage().getFileName(ProblemContext.getCurrentClass().getName());
		File f = new File(PrefFactory.getPrefs().getWorkingDirectory(), filename);
		try
		{
			PrintWriter out = new PrintWriter(new FileWriter(f));
			String[] sourceArray = source.split("\\r?\\n");
			for (int i=0; i<sourceArray.length; i++)
				out.println(sourceArray[i]);
			out.flush();
			out.close();
		}
		catch (IOException ex)
		{
			reportError(ex, false);
		}
	}
	
	/**
	 *	Compiles the saved code for this problem.
	 **/
	public boolean compileLocal() throws Exception
	{
		if (proc == null || proc.isDone())
		{
			Process p = Runtime.getRuntime().exec(ProblemContext.getLanguage().getCompileCommand(ProblemContext.getCurrentClass().getName()), null, PrefFactory.getPrefs().getWorkingDirectory());
			proc = new ProcessContainer(p, Dispatcher.getCompileComponent(), Dispatcher.getCompileComponent(), false);
			proc.start();
			p.waitFor();
			return proc.endVal() == 0;
		}
		else
		{
			Dispatcher.getCompileComponent().println("Error: Can't compile while another process is running");
			return false;
		}
	}
}
