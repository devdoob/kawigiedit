package kawigi.language;
import kawigi.problem.*;

public class CPPTestGenerator implements TestGenerator
{
	/**
	 *	Returns the testing code for the problem statement's default testing
	 *	cases.
	 **/
	public String getCode(ClassDecl cl)
	{
		String testcode = "";
		boolean stringType = cl.getMethod().getReturnType().isType(EditorDataType.String);
		EditorDataType[] paramTypes = cl.getMethod().getParamTypes();
		testcode += "<%:start-tests%>\n";
		for (int i=0; i<cl.countTests(); i++)
		{
			testcode += "double test" + i + "() {\n";
			String[] input = cl.getTest(i).getParameters();
			for (int j=0; j<input.length; j++)
				testcode += makeParameter(input[j], paramTypes[j], j);
			testcode += "\t" + cl.getName() + " * obj = new " + cl.getName() + "();\n";
			testcode += "\tclock_t start = clock();\n";
			testcode += "\t" + EditorLanguage.CPP.getName(cl.getMethod().getReturnType()) + " my_answer = obj->" + cl.getMethod().getName() + "(";
			testcode += "p0";
			for (int j=1; j<input.length; j++)
			{
				testcode += ", p" + j;
			}
			testcode += ");\n";
			testcode += "\tclock_t end = clock();\n";
			testcode += "\tdelete obj;\n";
			testcode += "\tcout <<\"Time: \" <<(double)(end-start)/CLOCKS_PER_SEC <<\" seconds\" <<endl;\n";
			testcode += makeParameter(cl.getTest(i).getAnswer(), cl.getMethod().getReturnType(), input.length);
			if (cl.getMethod().getReturnType().isArrayType())
			{
				testcode += "\tcout <<\"Desired answer: \" <<endl;\n";
				testcode += "\tcout <<\"\\t{ \";\n";
				testcode += "\tif (p" + input.length + ".size() > 0) {\n";
				testcode += "\t\tcout " + (stringType ? "<<\"\\\"\"" : "") + "<<p" + input.length + "[0]" + (stringType ? "<<\"\\\"\"" : "") + ";\n";
				testcode += "\t\tfor (int i=1; i<p" + input.length + ".size(); i++)\n";
				testcode += "\t\t\tcout <<\", " + (stringType ? "\\\"\"" : "\"") + " <<p" + input.length + "[i]" + (stringType ? "<<\"\\\"\"" : "") + ";\n";
				testcode += "\t\tcout <<\" }\" <<endl;\n";
				testcode += "\t}\n";
				testcode += "\telse\n";
				testcode += "\t\tcout <<\"}\" <<endl;\n";
				testcode += "\tcout <<endl <<\"Your answer: \" <<endl;\n";
				testcode += "\tcout <<\"\\t{ \";\n";
				testcode += "\tif (my_answer.size() > 0) {\n";
				testcode += "\t\tcout " + (stringType ? "<<\"\\\"\"" : "") + "<<my_answer[0]" + (stringType ? "<<\"\\\"\"" : "") + ";\n";
				testcode += "\t\tfor (int i=1; i<my_answer.size(); i++)\n";
				testcode += "\t\t\tcout <<\", " + (stringType ? "\\\"\"" : "\"") + " <<my_answer[i]" + (stringType ? "<<\"\\\"\"" : "") + ";\n";
				testcode += "\t\tcout <<\" }\" <<endl;\n";
				testcode += "\t}\n";
				testcode += "\telse\n";
				testcode += "\t\tcout <<\"}\" <<endl;\n";
				testcode += "\tif (my_answer != p" + input.length + ") {\n";
			}
			else
			{
				testcode += "\tcout <<\"Desired answer: \" <<endl;\n";
				testcode += "\tcout <<\"\\t" + (stringType ? "\\\"\"" : "\"") + " << p" + input.length + (stringType ? " <<\"\\\"\"" : "") + " <<endl;\n";
				testcode += "\tcout <<\"Your answer: \" <<endl;\n";
				testcode += "\tcout <<\"\\t" + (stringType ? "\\\"\"" : "\"") + " << my_answer" + (stringType ? "<<\"\\\"\"" : "") + " <<endl;\n";
				testcode += "\tif (p" + input.length + " != my_answer) {\n";
			}
			testcode += "\t\tcout <<\"DOESN'T MATCH!!!!\" <<endl <<endl;\n";
			testcode += "\t\treturn -1;\n";
			testcode += "\t}\n";
			testcode += "\telse {\n";
			testcode += "\t\tcout <<\"Match :-)\" <<endl <<endl;\n";
			testcode += "\t\treturn (double)(end-start)/CLOCKS_PER_SEC;\n";
			testcode += "\t}\n";
			testcode += "}\n";
		}
		testcode += "<%:end-tests%>\n";
		testcode += "int main() {\n";
		testcode += "\tint time;\n";
		testcode += "\tbool errors = false;\n\t\n";
		for (int i=0; i<cl.countTests(); i++)
		{
			testcode += "\ttime = test" + i + "();\n";
			testcode += "\tif (time < 0)\n";
			testcode += "\t\terrors = true;\n\t\n";
		}
		testcode += "\tif (!errors)\n";
		testcode += "\t\tcout <<\"You\'re a stud (at least on the example cases)!\" <<endl;\n";
		testcode += "\telse\n";
		testcode += "\t\tcout <<\"Some of the test cases had errors.\" <<endl;\n";
		testcode += "}\n";
		return testcode;
	}
	
	/**
	 *	Declares an array of the same type as a vector.
	 **/
	protected String toArray(EditorDataType type, String name)
	{
		EditorDataType t = type.getPrimitiveType();
		return EditorLanguage.CPP.getName(t) + " " + name + "[]";
	}
	
	/**
	 *	Declares a variable called p&lt;number&gt; from the type and value given.
	 **/
	protected String makeParameter(String param, EditorDataType type, int number)
	{
		if (type.isArrayType())
		{
			if (param.replaceAll("[\n\r\t {}]", "").length() == 0)
			{
				return "\t" + EditorLanguage.CPP.getName(type) + " p" + number + ";\n";
			}
			else
			{
				String decl = "\t" + toArray(type, "t" + number) + " = " + EditorLanguage.CPP.fixLiteral(param, type) + ";\n";
				decl += "\t" + EditorLanguage.CPP.getName(type) + " p" + number + "(t" + number + ", t" + number + "+sizeof(t" + number + ")/sizeof(" + EditorLanguage.CPP.getName(type.getPrimitiveType()) + "));\n";
				return decl;
			}
		}
		else
		{
			return "\t" + EditorLanguage.CPP.getName(type) + " p" + number + " = " + EditorLanguage.CPP.fixLiteral(param, type) + ";\n";
		}
	}
}
