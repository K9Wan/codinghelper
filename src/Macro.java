import java.util.ArrayList;
import java.util.regex.*;	//Matcher MatchResult Pattern PatterSyntaxException

public class Macro {
	
	public static ArrayList<Macro> macroList = new ArrayList<Macro>(30);
	private String pattern;
	private String pattern_result;
	private boolean useRegex;
	private String helpDoc;
	
	public Macro(String original, String result, boolean useRegex) {
		// TODO Auto-generated constructor stub
		this.pattern = original;
		this.pattern_result = result;
		this.useRegex = useRegex;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		test();
	}
	
	private static void test() {	//will be used to see how regex and replacement works.
		Macro matrix = new Macro(
				"(\\w+)( ?\\* ?\\* ?)(\\w+)( ?= ?)\\w?alloc\\[(\\w+)\\]\\[(\\w+)\\];",
				"$1$2$3$4malloc($5*sizeof *$3 + $6*$5*sizeof **$3);\nfor(int i=0;i<$5;i++)\n{\n\t$3[i]$4($1 *)($3+$5)+i*n;\n}\n",
				true);
		String test = "_Bool * * cases = malloc[two_n][n];aaaa\n_Bool * * cases = malloc[two_n][n];";
		Pattern matrixPattern = Pattern.compile(matrix.pattern);
		System.out.println(matrixPattern);
		Matcher m = matrixPattern.matcher(test);
		System.out.println(m.replaceFirst(matrix.pattern_result));
		String result = m.replaceAll(matrix.pattern_result);
		String result2 = test.replaceAll(matrix.pattern, matrix.pattern_result);
		System.out.println(Matcher.quoteReplacement(matrix.pattern)+'\n'+matrix.pattern);
		System.out.println(result+"\n\n"+String.format("%s", result2));
		String test2 = "aaaa"; String n = "aa\\\naa";
		System.out.println(test2.replaceAll("a+", n));
		
		//Pattern.compile(regex).matcher(str).replaceAll(repl) 
	}

}
