package kawigi.language;

/**
 *	This enum represents all the data types supported by TopCoder as input or
 *	output types.
 *	
 *	Technically, CharacterArray probably won't be used.
 **/
public enum EditorDataType
{
	/**
	 *	String type.
	 **/
	String(null),
	/**
	 *	int type.
	 **/
	Integer(null),
	/**
	 *	double type.
	 **/
	Double(null),
	/**
	 *	long (or long long) type.
	 **/
	Long(null),
	/**
	 *	char type.
	 **/
	Character(null),
	/**
	 *	String[] or vector<string> type.
	 **/
	StringArray(String),
	/**
	 *	int[] or vector<int> type.
	 **/
	IntegerArray(Integer),
	/**
	 *	double[] or vector<double> type.
	 **/
	DoubleArray(Double),
	/**
	 *	long[] or vector<long long> type.
	 **/
	LongArray(Long),
	/**
	 *	char[] or vector<char> type.
	 **/
	CharacterArray(Character);
	
	/**
	 *	If this type is an array type, this is the type of its elements.  If it
	 *	isn't an array type, this is null.
	 **/
	private EditorDataType primitiveType;
	
	/**
	 *	Constructs enum values.
	 **/
	private EditorDataType(EditorDataType primitiveType)
	{
		this.primitiveType = primitiveType;
	}
	
	/**
	 *	Returns the type of elements of this type if this is an array type.
	 **/
	public EditorDataType getPrimitiveType()
	{
		return primitiveType;
	}
	
	/**
	 *	Returns true if this type represents an array type.
	 **/
	public boolean isArrayType()
	{
		return primitiveType != null;
	}
	
	/**
	 *	Returns true if type is the same as this type, or if either this type or
	 *	the given type represents an array/vector of the other.
	 **/
	public boolean isType(EditorDataType type)
	{
		return this == type || (type.isArrayType() && type.getPrimitiveType() == this) || (isArrayType() && getPrimitiveType() == type);
	}
}
