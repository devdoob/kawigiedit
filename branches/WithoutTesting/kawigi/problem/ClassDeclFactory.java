package kawigi.problem;
import kawigi.util.*;

/**
 *	This class takes some set of parameters and generates a ClassDecl from it.
 **/
public class ClassDeclFactory
{
	/**
	 *	Determines the type of ClassDeclGenerator that should be used according
	 *	to the current application mode.
	 *	
	 *	If we are in standalone mode, this returns the problem parser.  If we
	 *	are in plugin mode, this returns an object that converts TopCoder's
	 *	ProblemComponent into a ClassDecl.
	 **/
	public static ClassDeclGenerator getGenerator()
	{
		if (AppEnvironment.getEnvironment() == AppEnvironment.PluginMode)
			return new TCProblemConverter();
		else
			return new ProblemParser();
	}
	
	/**
	 *	Generates a ClassDecl given some parameters.
	 *	
	 *	This calls getClassDecl on the ClassDeclGenerator returned by
	 *	getGenerator.  If the application is in standalone mode, the parameters
	 *	will be ignored.  If we are in plugin mode, the first parameter
	 *	<b>needs</b> to be a TopCoder ProblemComponent and the second parameter
	 *	needs to be a TopCoder Language.  If either is not the right type or
	 *	there are fewer than two parameters, things will crash and be generally
	 *	unhappy.
	 **/
	public static ClassDecl getClassDecl(Object ... params)
	{
		return getGenerator().getClassDecl(params);
	}
}
