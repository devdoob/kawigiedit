package kawigi.problem;

/**
 *	Represents a test case.
 *	
 *	Normally when one writes a class called "Test", it's not meant to be in the
 *	final code base, but this case is an exception :-)
 **/
public class Test
{
	/**
	 *	The value of the intended answer, as a string.
	 **/
	private String answer;
	/**
	 *	The values of the parameters for the test.
	 **/
	private String[] parameters;
	
	/**
	 *	Constructs a new Test with the given answer and parameters.
	 **/
	public Test(String answer, String[] parameters)
	{
		this.answer = answer;
		this.parameters = parameters;
	}
	
	/**
	 *	Returns the intended output for this test.
	 **/
	public String getAnswer()
	{
		return answer;
	}
	
	/**
	 *	Returns the input parameters for this test.
	 **/
	public String[] getParameters()
	{
		return parameters;
	}
}
