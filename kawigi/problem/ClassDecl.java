package kawigi.problem;
import kawigi.language.*;

/**
 *	This class represents the code side of a problem statement.
 *
 *	This means it has the class name, the information the method you are
 *	required to write, and a list of example tests.
 *
 *	This serves many of the same purposes that TopCoder's ProblemComponent does,
 *	and most of the data from a ProblemComponent is converted into one of these
 *	objects for KawigiEdit's use.  The reason I don't just use the
 *	ProblemComponent instead of having this class is so that I can have this
 *	class independent of TopCoder's classes and use it in stadalone mode.  Then,
 *	assuming I can properly parse a problem statement (which I can't all the
 *	time, it's on the list), I can generate code the exact same way I would in
 *	plugin mode, and standalone won't suffer from having re-implemented code
 *	generation (it will just suffer from inadequate problem parsing).
 **/
public class ClassDecl
{
	/**
	 *	Name of the class you need to implement.
	 **/
	private String className;
	/**
	 *	Information on the method you need to implement.
	 **/
	private MethodDecl method;

	/**
	 *	Constructs a new ClassDecl with the given name and method.
	 **/
	public ClassDecl(String className, MethodDecl method)
	{
		this.className = className;
		this.method = method;
	}

	/**
	 *	Returns the name of this class.
	 **/
	public String getName()
	{
		return className;
	}

	/**
	 *	Returns the method declaration that needs to be implemented in this
	 *	class.
	 **/
	public MethodDecl getMethod()
	{
		return method;
	}

	/**
	 *	Evaluates a KawigiEdit tag from a template.
	 *
	 *	Certain tags aren't handled here because they shouldn't be expanded yet
	 *	in the code edited by the user (for instance, the testing-code tag is
	 *	left alone here, because it isn't evaluated until either TopCoder asks
	 *	for your code or your code gets saved locally.
	 **/
	public String evaluateTag(String tag, EditorLanguage language)
	{
		if (tag.equalsIgnoreCase("class-name"))
			return className;
		else if (tag.equalsIgnoreCase("return-type"))
			return language.getName(method.getReturnType());
		else if (tag.equalsIgnoreCase("method-name"))
			return method.getName();
		else if (tag.equalsIgnoreCase("param-type-list"))
		{
			EditorDataType[] types = method.getParamTypes();
			String ret = language.getName(types[0]);
			for (int i=1; i<types.length; i++)
				ret += ", " + language.getName(types[i]);
			return ret;
		}
		else if (tag.equalsIgnoreCase("param-list"))
		{
			EditorDataType[] types = method.getParamTypes();
			String[] names = method.getParamNames();
			String ret = language.getName(types[0]) + " " + names[0];
			for (int i=1; i<types.length; i++)
				ret += ", " + language.getName(types[i]) + " " + names[i];
			return ret;
		}
		else if (tag.equalsIgnoreCase("byref-param-list"))
		{
			EditorDataType[] types = method.getParamTypes();
			String[] names = method.getParamNames();
			String ret = "ByRef " + names[0] + " As " + language.getName(types[0]);
			for (int i=1; i<types.length; i++)
				ret += ", ByRef " + names[i] + " As " + language.getName(types[i]);
			return ret;
		}
		else if (tag.equalsIgnoreCase("byval-param-list"))
		{
			EditorDataType[] types = method.getParamTypes();
			String[] names = method.getParamNames();
			String ret = "ByVal " + names[0] + " As " + language.getName(types[0]);
			for (int i=1; i<types.length; i++)
				ret += ", ByVal " + names[i] + " As " + language.getName(types[i]);
			return ret;
		}
		else if (tag.equalsIgnoreCase("vb-param-list"))
		{
			EditorDataType[] types = method.getParamTypes();
			String[] names = method.getParamNames();
			String ret = names[0] + " As " + language.getName(types[0]);
			for (int i=1; i<types.length; i++)
				ret += ", " + names[i] + " As " + language.getName(types[i]);
			return ret;
		}
		return "";
	}
}
