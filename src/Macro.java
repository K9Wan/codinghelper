import java.util.ArrayList;
import java.util.regex.*;	//Matcher MatchResult Pattern PatterSyntaxException

public class Macro {
	
	public static ArrayList<Macro> macroList = new ArrayList<Macro>(30);
	protected String name;				//이름
	protected String input;				//인식할 패턴
	protected String output;		//바뀔 패턴
	protected boolean useRegex;			//정규표현식 사용여부
	protected String helpDoc;				//어떤 패턴을 어떻게 바꾸는지 알려줄 수 있는 독스트링을 위한 필드;
	protected boolean enabled;			//사용 여부
	
	public Macro(String original, String result, boolean useRegex) {
		// TODO Auto-generated constructor stub
		this.input = original;
		this.output = result;
		this.useRegex = useRegex;
	}
	
	public Macro(String name, String original, String result, boolean useRegex, String helpDoc, boolean enabled) {
		// TODO Auto-generated constructor stub
		this.name = name;
		this.input = original;
		this.output = result;
		this.useRegex = useRegex;
		this.helpDoc = helpDoc;
		this.enabled = enabled;
	}
	
	public Macro(String name, String original, String result, boolean useRegex, String helpDoc) {
		// TODO Auto-generated constructor stub
		this(name, original, result, useRegex, helpDoc, true);
	}

	public Macro(String name, String original, String result, boolean useRegex) {
		// TODO Auto-generated constructor stub
		this(name, original, result, useRegex, "helpDoc not Added");
	}

	public Macro(String name, String original, String result, String helpDoc) {
		// TODO Auto-generated constructor stub
		this(name, original, result, false, helpDoc);
	}
	
	public Macro(String original, String result) {
		this(original, result, false);
	}
	
	public String replace(String targetStr) {
		if(useRegex) {
			return targetStr.replaceAll(input, output);
		}
		else {
			return targetStr.replace(input, output);
		}
	}
	
	public String getName() {
		return name;
	}
	
	public String getInput() {
		return input;
	}
	
	public String getOutput() {
		return output;
	}
	
	public boolean getUseRegex() {
		return useRegex;
	}
	
	public String getHelpDoc() {
		return helpDoc;
	}
	
	public boolean getEnabled() {
		return enabled;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setInput(String pattern) {
		this.input = pattern;
	}
	
	public void setOutput(String pattern_result) {
		this.output = pattern_result;
	}
	
	public void setUseRegex(boolean useRegex) {
		this.useRegex = useRegex;
	}
	
	public void setHelpDoc(String helpDoc) {
		this.helpDoc = helpDoc;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public String info() {
		return "name: "+name+"\nhelp:\n"+helpDoc+"\nenabled: "+(enabled?"enabled":"disabled")+"\nfrom:\n"+input+"\nto:\n"+output+"\nuse regex: "+useRegex;
	}
	
	public static void predefine() {	//will be invoked when program starts;
		macroList.add(new Macro(
				"malloc2d",
				"(\\w+)( ?\\* ?\\* ?)(\\w+)( ?= ?)\\w?alloc\\[(\\w+)\\]\\[(\\w+)\\];",
				"$1$2$3$4malloc($5*sizeof *$3 + $6*$5*sizeof **$3);\nfor(int i=0;i<$5;i++)\n{\n\t$3[i]$4($1 *)($3+$5)+i*n;\n}\n",
				true,
				"Use to allocate 2D array.\nExample: uint_8 ** matrix = malloc[6][cols];"));
		macroList.add(new Macro(
				"clear DEBUG",
				"#if.+DEBUG\\r?\\n([^#]*)#endif",
				"",
				true,
				"Eliminates code for debugging.\nIt will work for ifdef DEBUG, if defined DEBUG, etc.",
				false));
	}
	
	public static String execute(String str) {
		for(Macro m:macroList) {
			if(m.enabled) {
				str = m.replace(str);
			}
		}
		return str;
	}
	
	public static Macro findByName(String name) {
		for(Macro m:macroList) {
			if(m.name.equals(name)) {
				return m;
			}
		}
		return null;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//test();
		predefine();
	}
	
	private static void test() {	//will be used to see how regex and replacement works-테스트용;
		Macro matrix = new Macro(
				"(\\w+)( ?\\* ?\\* ?)(\\w+)( ?= ?)\\w?alloc\\[(\\w+)\\]\\[(\\w+)\\];",
				"$1$2$3$4malloc($5*sizeof *$3 + $6*$5*sizeof **$3);\nfor(int i=0;i<$5;i++)\n{\n\t$3[i]$4($1 *)($3+$5)+i*n;\n}\n",
				true);
		String test = "_Bool * * cases = malloc[two_n][n];aaaa\n_Bool * * cases = malloc[two_n][n];";
		Pattern matrixPattern = Pattern.compile(matrix.input);	//make Pattern object;
		System.out.println(matrixPattern);		//will print regex string that was used to make Pattern object;
		Matcher m = matrixPattern.matcher(test);
		System.out.println(m.replaceFirst(matrix.output));	//replace only one pattern
		String result = m.replaceAll(matrix.output);	
		String result2 = test.replaceAll(matrix.input, matrix.output);
		
		
		//System.out.println(Matcher.quoteReplacement(matrix.pattern)+'\n'+matrix.pattern);	//ignore
		//System.out.println(result+"\n\n"+String.format("%s", result2));					//무시(그냥 테스트용);
		
		
		System.out.println(result.equals(result2));		//"""result와 result2의 결과가 같음(true 출력함)"""
		

		/*/무시(그냥 테스트);
		String test2 = "aaaa"; String[] s = {"aa\naa","aa\\naa","aa\\\naa","aa\\\\naa"};
		for(String n:s) {
			System.out.println(test2.replaceAll("a+", n)+"\t---");
		}
		/*/
		System.out.println(Pattern.compile(matrix.input).matcher(test).replaceAll(matrix.output).equals(test.replaceAll(matrix.input, matrix.output)));
		//result result2를 한줄로 비교
		//Pattern.compile(regex).matcher(target str).replaceAll(replace regex)) 
		String str = "ababerbbabbb";
		System.out.println(str.replace("ab", "cc"));	//regex 없는 단순 대체; useRegex field가 false일때 사용될 예정
		System.out.println(matrix.replace(test));
		Macro abcc = new Macro("ab","cc");
		System.out.println(abcc.replace(str));
	}

}
