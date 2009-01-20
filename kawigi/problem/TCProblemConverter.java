package kawigi.problem;
import com.topcoder.shared.language.*;
import com.topcoder.shared.problem.*;
import kawigi.language.*;
import kawigi.cmd.*;

/**
 *	This is perhaps one of the most important source files in KawigiEdit,
 *	because it converts TopCoder's problem statements into a format that allows
 *	KawigiEdit to generate skeleton and test code in plugin mode.
 **/
public class TCProblemConverter implements ClassDeclGenerator
{
	/**
	 *	Generates a ClassDecl from the given parameters.
	 *
	 *	In this implementation, the first parameter <b>must</b> be a TopCoder
	 *	ProblemComponent, and the second parameter <b>must</b> be a TopCoder
	 *	Language.
	 *
	 *	As a side effect, the KawigiEdit language corresponding to the TopCoder
	 *	language given is set as the current language in the ProblemContext
	 *	class.
	 **/
	public ClassDecl getClassDecl(Object ... parameters)
	{
		ProblemComponent component = (ProblemComponent)parameters[0];
		Language tclang = (Language)parameters[1];

		EditorLanguage lang;
		if (tclang.getId() == CPPLanguage.ID)
			lang = EditorLanguage.CPP;
		else if (tclang.getId() == JavaLanguage.ID)
			lang = EditorLanguage.Java;
		else if (tclang.getId() == CSharpLanguage.ID)
			lang = EditorLanguage.CSharp;
		else
			lang = EditorLanguage.VB;
		ProblemContext.setLanguage(lang);
		EditorDataType returnType = lang.getType(component.getReturnType().getDescriptor(tclang));
		DataType[] tcParamTypes = component.getParamTypes();
		EditorDataType[] paramTypes = new EditorDataType[tcParamTypes.length];
		for (int i=0; i<tcParamTypes.length; i++)
			paramTypes[i] = lang.getType(tcParamTypes[i].getDescriptor(tclang));
		return new ClassDecl(component.getClassName(), new MethodDecl(component.getMethodName(), returnType, paramTypes, component.getParamNames()));
	}
}
