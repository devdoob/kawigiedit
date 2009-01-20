package kawigi.problem;
import kawigi.language.*;

/**
 *	Represents the method to be implemented for a problem.
 **/
public class MethodDecl
{
	/**
	 *	The name of the method.
	 **/
	private String methodName;
	/**
	 *	The return type of the method.
	 **/
	private EditorDataType returnType;
	/**
	 *	The names of the parameters of the method.
	 **/
	private String[] paramNames;
	/**
	 *	The types of the parameters of the method.
	 **/
	private EditorDataType[] paramTypes;
	
	/**
	 *	Constructs a MethodDecl with the given parameters.
	 **/
	public MethodDecl(String methodName, EditorDataType returnType, EditorDataType[] paramTypes, String[] paramNames)
	{
		this.methodName = methodName;
		this.returnType = returnType;
		this.paramTypes = paramTypes;
		this.paramNames = paramNames;
	}
	
	/**
	 *	Returns the name of this method.
	 **/
	public String getName()
	{
		return methodName;
	}
	
	/**
	 *	Returns the return type of this method.
	 **/
	public EditorDataType getReturnType()
	{
		return returnType;
	}
	
	/**
	 *	Returns the names of the parameters to this method.
	 *	
	 *	Element i of this array corresponds to element i of the array returned
	 *	by getParamTypes.
	 **/
	public String[] getParamNames()
	{
		return paramNames;
	}
	
	/**
	 *	Returns the types of the parameters to this method.
	 *	
	 *	Element i of this array corresponds to element i of the array returned
	 *	by getParamNames.
	 **/
	public EditorDataType[] getParamTypes()
	{
		return paramTypes;
	}
}
