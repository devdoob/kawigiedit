package kawigi.problem;
import kawigi.language.*;
import kawigi.properties.*;
import java.io.*;
import java.util.regex.*;

/**
 *	This is the class that converts a ClassDecl and language into skeleton code
 *	from a template.
 **/
public class TemplateGenerator
{
	/**
	 *	Generates the output of the code template with the given ClassDecl in
	 *	the given language.
	 **/
	public static Skeleton getSkeleton(ClassDecl cl, EditorLanguage language)
	{
		String template = getTemplate(language);
		Matcher matcher = Pattern.compile("<%: *([a-zA-Z0-9_-]+) *%>").matcher(template);
		String output = "";
		int lastIndex = 0;
		int caretIndex = 0;
		while (matcher.find())
		{
			int start = matcher.start(), end = matcher.end();
			if (start > lastIndex)
			{
				output += template.substring(lastIndex, start);
				lastIndex = end;
			}
			String tagname = matcher.group(1);
			//Ok, one special case here:
			if (tagname.equals("set-caret"))
				caretIndex = output.length();
			else
				output += cl.evaluateTag(tagname, language);
		}
		output += template.substring(lastIndex);
		return new Skeleton(output, caretIndex);
	}
	
	/**
	 *	Returns the (default or customized) template for the given language.
	 **/
	public static String getTemplate(EditorLanguage language)
	{
		PrefProxy prefs = PrefFactory.getPrefs();
		String override = prefs.getProperty("kawigi.language." + language.toString().toLowerCase() + ".override");
		try
		{
			BufferedReader in = null;
			if (override != null)
			{
				try
				{
					File f = new File(override);
					if (f.exists())
						in = new BufferedReader(new FileReader(f));
				}
				catch (IOException ex)
				{
					//In this case, we're just handing the error by using the resource version.
				}
			}
			if (in == null)
				in = new BufferedReader(new InputStreamReader(TemplateGenerator.class.getResource("/rc/templates/" + language.toString() + ".ket").openStream()));
			String ret = "";
			String line;
			while ((line = in.readLine()) != null)
				ret += line + "\n";
			in.close();
			return ret.substring(0, ret.length()-1);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return "";
		}
	}
}
