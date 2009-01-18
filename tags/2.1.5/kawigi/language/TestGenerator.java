package kawigi.language;
import kawigi.problem.*;

/**
 *	Interface implemented for each language to generate testing code.
 **/
public interface TestGenerator
{
	/**
	 *	Returns the testing code for this problem.
	 **/
	public String getCode(ClassDecl cl);
}
