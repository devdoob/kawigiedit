package kawigi.problem;

/**
 *	Represents the combination of a String of source code and a location for the
 *	caret to be set.
 **/
public class Skeleton
{
	/**
	 *	String representation of the skeleton code.
	 **/
	private String source;
	/**
	 *	Index within source that the caret should be in the beginning.
	 **/
	private int caretLocation;
	
	/**
	 *	Creates a new Skeleton object with the given parameters for the code and
	 *	caret location.
	 **/
	public Skeleton(String source, int caretLocation)
	{
		this.source = source;
		this.caretLocation = caretLocation;
	}
	
	/**
	 *	Returns the source code for this skeleton.
	 **/
	public String getSource()
	{
		return source;
	}
	
	/**
	 *	Returns the index within the skeleton at which the caret should start
	 *	out.
	 **/
	public int getCaret()
	{
		return caretLocation;
	}
}
