package kawigi.problem;

/**
 *	Interface implemented by the TopCoder problem converter and the problem
 *	statement parser.
 **/
public interface ClassDeclGenerator
{
	/**
	 *	Returns the ClassDecl of the current problem.
	 **/
	public ClassDecl getClassDecl(Object ... parameters);
}
