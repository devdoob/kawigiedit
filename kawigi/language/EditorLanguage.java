package kawigi.language;
import kawigi.properties.*;
import java.util.*;
import java.io.File;

public enum EditorLanguage
{
	Java("$PROBLEM$.java", "javac $PROBLEM$.java", "java $PROBLEM$", "String","int","double","long","char","String[]","int[]","double[]","long[]","char[]"),
	CPP("$PROBLEM$.cpp", "g++ $PROBLEM$.cpp", File.separatorChar == '/' ? "./a.out" : "$CWD$\\a.exe", "string","int","double","long long","char","vector <string>","vector <int>","vector <double>","vector <long long>","vector <char>"),
	CSharp("$PROBLEM$.cs", "csc $PROBLEM$.cs", File.separatorChar == '/' ? "mono $PROBLEM$.exe" : "$CWD$\\$PROBLEM$.exe", "string","int","double","long","char","string[]","int[]","double[]","long[]","char[]"),
	VB("$PROBLEM$.vb", "vbc $PROBLEM$.vb", File.separatorChar == '/' ? "mono $PROBLEM$.exe" : "$CWD$\\$PROBLEM$.exe", "String","Integer","Double","Long","Char","String()","Integer()","Double()","Long()","Char()");

	private Map<EditorDataType,String> typeNames;
	// if this is static, it makes us more flexible in parsing.
	private static Map<String,EditorDataType> types;
	private String defaultFileName, defaultCompileCommand, defaultExecuteCommand;

	private EditorLanguage(String fileName, String compileCommand, String executeCommand, String... propTypes)
	{
		defaultFileName = fileName;
		defaultCompileCommand = compileCommand;
		defaultExecuteCommand = executeCommand;
		typeNames = new HashMap<EditorDataType,String>();
		int i = 0;
		for (EditorDataType type : EditorDataType.values())
		{
			typeNames.put(type, propTypes[i++]);
		}
		reverseMap(propTypes);
	}

	private void reverseMap(String[] propTypes)
	{
		if (types == null)
			types = new HashMap<String,EditorDataType>();
		int i = 0;
		for (EditorDataType type : EditorDataType.values())
		{
			types.put(propTypes[i++], type);
		}
	}

	public String getName(EditorDataType type)
	{
		return typeNames.get(type);
	}

	public EditorDataType getType(String name)
	{
		// The replaceAll is because I guess I get a space in C++ vector type
		// names from TC.
		return types.get(name);
	}

	public String fixLiteral(String lit, EditorDataType type)
	{
		String suffix = "";
		if (type.isType(EditorDataType.Double))
		{
			if (this != VB && this != CPP)
				suffix = "D";
		}
		else if (type.isType(EditorDataType.Long))
		{
			if (this == CPP)
				suffix = "LL";
			else if (this == Java || this == CSharp)
				suffix = "L";
		}
		// Note - I should never have to fix strings or characters, but if I
		// did, this wouldn't work.
		if (suffix.length() > 0)
			lit = lit.replaceAll("([0-9.Ee-]+)", "$1" + suffix);

		// Fix escape sequences in VB.
		if (this == VB && (type.isType(EditorDataType.Character) || type.isType(EditorDataType.String)))
		{
			String newlit = "";
			for (int i=0; i<lit.length(); i++)
				if (lit.charAt(i) == '\\')
				{
					i++;
					switch(lit.charAt(i))
					{
						case '\"':
							newlit += "\"\"";
							break;
						case '\\':
							newlit += "\\";
							break;
						case '\'':
							newlit += "\'";
							break;
						case 'n':
							if (type.isType(EditorDataType.Character))
							{
								if (newlit.endsWith("\'"))
								{
									newlit = newlit.substring(0, newlit.length()-1);
									newlit += "ControlChars.Lf";
									i++;
								}
							}
							else
							{
								newlit += "\" & ControlChars.Lf & \"";
							}
							break;
						case 'r':
							if (type.isType(EditorDataType.Character))
							{
								if (newlit.endsWith("\'"))
								{
									newlit = newlit.substring(0, newlit.length()-1);
									newlit += "ControlChars.Cr";
									i++;
								}
							}
							else
							{
								newlit += "\" & ControlChars.Cr & \"";
							}
							break;
						// I'm not sure that TC ever escapes tabs, but I'd rather
						// be prepared.
						case 't':
							if (type.isType(EditorDataType.Character))
							{
								if (newlit.endsWith("\'"))
								{
									newlit = newlit.substring(0, newlit.length()-1);
									newlit += "ControlChars.Tab";
									i++;
								}
							}
							else
							{
								newlit += "\" & ControlChars.Tab & \"";
							}
							break;
						default:
							i--;
							break;
					}
				}
				else if (lit.charAt(i) == '\'' && type.isType(EditorDataType.Character))
					newlit += "\"";
				else
					newlit += lit.charAt(i);
			lit = newlit;
		}
		return lit;
	}

	public String getPropertyCategory()
	{
		return "kawigi.language." + toString().toLowerCase();
	}

	public String getFileName(String className)
	{
		String filename = PrefFactory.getPrefs().getProperty(getPropertyCategory() + ".filename", defaultFileName);
		filename = filename.replaceAll("\\$PROBLEM\\$", className);
		return filename;
	}

	public String getCompileCommand(String className)
	{
		String command = PrefFactory.getPrefs().getProperty(getPropertyCategory() + ".compiler", defaultCompileCommand);
		command = command.replaceAll("\\$PROBLEM\\$", className);
		return command;
	}

	public String getRunCommand(String className, String cwd)
	{
		String command = PrefFactory.getPrefs().getProperty(getPropertyCategory() + ".run", defaultExecuteCommand);
		command = command.replaceAll("\\$CWD\\$", cwd.replaceAll("\\\\", "\\\\\\\\"));
		command = command.replaceAll("\\$PROBLEM\\$", className);
		return command;
	}
}
