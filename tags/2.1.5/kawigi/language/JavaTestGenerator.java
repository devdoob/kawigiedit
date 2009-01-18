package kawigi.language;
import kawigi.problem.*;

public class JavaTestGenerator implements TestGenerator
{
	/**
	 *	Returns Java testing code for the problem statement's example test
	 *	cases.
	 **/
	public String getCode(ClassDecl cl)
	{
		String testcode = "public static void main(String[] args)\n\t{\n";
		testcode += "\t\tlong time;\n";
		EditorDataType returnType = cl.getMethod().getReturnType();
		testcode += "\t\t" + EditorLanguage.Java.getName(returnType) + " answer;\n";
		testcode += "\t\tboolean errors = false;\n";
		boolean stringType = returnType.isType(EditorDataType.String);
		testcode += "\t\t" + EditorLanguage.Java.getName(returnType) + " desiredAnswer;\n\t\t\n";
		if (returnType.isArrayType())
			testcode += "\t\tboolean same;\n";
		testcode += "\t\t<%:start-tests%>\n";
		for (int i=0; i<cl.countTests(); i++)
		{
			testcode += "\t\ttime = System.currentTimeMillis();\n";
			testcode += "\t\tanswer = " + toJavaTest(cl.getTest(i), cl.getMethod().getParamTypes(), "new " + cl.getName() + "()." + cl.getMethod().getName() + "(");
			testcode += "\t\tSystem.out.println(\"Time: \" + (System.currentTimeMillis()-time)/1000.0 + \" seconds\");\n";
			testcode += "\t\tdesiredAnswer = " + translateObject(returnType, cl.getTest(i).getAnswer()) + ";\n";
			testcode += "\t\tSystem.out.println(\"Your answer:\");\n";
			
			//If writing code that writes code isn't confusing enough, maybe writing code that writes code that writes code-like constructs is
			if (returnType.isArrayType())
			{
				testcode += "\t\tif (answer.length > 0)\n";
				testcode += "\t\t{\n";
				testcode += "\t\t\tSystem.out.print(\"\\t{ " + (stringType ? "\\\"\" + answer[0] + \"\\\"\"" : "\" + answer[0]") + ");\n";
				testcode += "\t\t\tfor (int i=1; i<answer.length; i++)\n";
				testcode += "\t\t\t\tSystem.out.print(\"," + (stringType ? "\\n\\t  \\\"" : " ") + "\" + answer[i]" + (stringType ? " + \"\\\"\"": "") + ");\n";
				testcode += "\t\t\tSystem.out.println(\" }\");\n";
				testcode += "\t\t}\n";
				testcode += "\t\telse\n";
				testcode += "\t\t\tSystem.out.println(\"\\t{ }\");\n";
				testcode += "\t\tSystem.out.println(\"Desired answer:\");\n";
				testcode += "\t\tif (desiredAnswer.length > 0)\n";
				testcode += "\t\t{\n";
				testcode += "\t\t\tSystem.out.print(\"\\t{ " + (stringType ? "\\\"\" + desiredAnswer[0] + \"\\\"\"" : "\" + desiredAnswer[0]") + ");\n";
				testcode += "\t\t\tfor (int i=1; i<desiredAnswer.length; i++)\n";
				testcode += "\t\t\t\tSystem.out.print(\"," + (stringType ? "\\n\\t  \\\"" : " ") + "\" + desiredAnswer[i]" + (stringType ? " + \"\\\"\"": "") + ");\n";
				testcode += "\t\t\tSystem.out.println(\" }\");\n";
				testcode += "\t\t}\n";
				testcode += "\t\telse\n";
				testcode += "\t\t\tSystem.out.println(\"\\t{ }\");\n";
				testcode += "\t\tsame = desiredAnswer.length == answer.length;\n";
				testcode += "\t\tfor (int i=0; i<answer.length && same; i++)\n";
				testcode += "\t\t\tif (" + (stringType ? "!answer[i].equals(desiredAnswer[i])" : "answer[i] != desiredAnswer[i]") + ")\n";
				testcode += "\t\t\t\tsame = false;\n";
				testcode += "\t\tif (!same)\n";
			}
			else
			{
				testcode += "\t\tSystem.out.println(\"\\t" + (stringType ? "\\\"" : "") + "\" + answer" + (stringType ? " + \"\\\"\"" : "") + ");\n";
				testcode += "\t\tSystem.out.println(\"Desired answer:\");\n";
				testcode += "\t\tSystem.out.println(\"\\t" + (stringType ? "\\\"" : "") + "\" + desiredAnswer" + (stringType ? " + \"\\\"\"" : "") + ");\n";
				testcode += "\t\tif (" + (stringType ? "!answer.equals(desiredAnswer)" : "answer != desiredAnswer") + ")\n";
			}
			testcode += "\t\t{\n";
			testcode += "\t\t\terrors = true;\n";
			testcode += "\t\t\tSystem.out.println(\"DOESN'T MATCH!!!!\");\n";
			testcode += "\t\t}\n";
			testcode += "\t\telse\n";
			testcode += "\t\t\tSystem.out.println(\"Match :-)\");\n";
			testcode += "\t\tSystem.out.println();\n";
		}
		testcode += "\t\t<%:end-tests%>\n";
		testcode += "\t\t\n\t\tif (errors)\n";
		testcode += "\t\t\tSystem.out.println(\"Some of the test cases had errors :-(\");\n";
		testcode += "\t\telse\n";
		testcode += "\t\t\tSystem.out.println(\"You're a stud (at least on the test data)! :-D \");\n";
		testcode += "\t}\n";
		return testcode;
	}
	
	/**
	 *	Quick utility function to provide the code that should return the answer.
	 **/
	protected String toJavaTest(Test test, EditorDataType[] types, String start)
	{
		String ret = start;
		String[] input = test.getParameters();
		ret += translateObject(types[0], input[0]);
		for (int i=1; i<input.length; i++)
			ret += ", " + translateObject(types[i], input[i]);
		ret = ret.replaceAll("\\n", "");
		return ret + ");\n";
	}
	
	/**
	 *	Quck utility function to convert a value to code that creates that value
	 *	in Java.
	 **/
	protected String translateObject(EditorDataType type, String value)
	{
		if (type.isArrayType())
		{
			return "new " + EditorLanguage.Java.getName(type) + EditorLanguage.Java.fixLiteral(value, type);
		}
		else
			return EditorLanguage.Java.fixLiteral(value, type);
	}
}