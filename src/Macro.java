import java.util.ArrayList;
import java.util.regex.*;	//Matcher MatchResult Pattern PatterSyntaxException

public class Macro {
	
	public static ArrayList<Macro> macroList = new ArrayList<Macro>(30);
	private String pattern;				//인식할 패턴
	private String pattern_result;		//바뀔 패턴
	private boolean useRegex;			//정규표현식 사용여부
	private String helpDoc;				//어떤 패턴을 어떻게 바꾸는지 알려줄 수 있는 독스트링을 위한 필드;
	
	public Macro(String original, String result, boolean useRegex) {
		// TODO Auto-generated constructor stub
		this.pattern = original;
		this.pattern_result = result;
		this.useRegex = useRegex;
	}
	
	public Macro(String original, String result) {
		this(original, result, false);
	}
	
	public String replace(String targetStr) {
		if(useRegex) {
			return targetStr.replaceAll(pattern, pattern_result);
		}
		else {
			return targetStr.replace(pattern, pattern_result);
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		test();
	}
	
	private static void test() {	//will be used to see how regex and replacement works-테스트용;
		Macro matrix = new Macro(
				"(\\w+)( ?\\* ?\\* ?)(\\w+)( ?= ?)\\w?alloc\\[(\\w+)\\]\\[(\\w+)\\];",
				"$1$2$3$4malloc($5*sizeof *$3 + $6*$5*sizeof **$3);\nfor(int i=0;i<$5;i++)\n{\n\t$3[i]$4($1 *)($3+$5)+i*n;\n}\n",
				true);
		String test = "_Bool * * cases = malloc[two_n][n];aaaa\n_Bool * * cases = malloc[two_n][n];";
		Pattern matrixPattern = Pattern.compile(matrix.pattern);	//make Pattern object;
		System.out.println(matrixPattern);		//will print regex string that was used to make Pattern object;
		Matcher m = matrixPattern.matcher(test);
		System.out.println(m.replaceFirst(matrix.pattern_result));	//replace only one pattern
		String result = m.replaceAll(matrix.pattern_result);	
		String result2 = test.replaceAll(matrix.pattern, matrix.pattern_result);
		
		
		//System.out.println(Matcher.quoteReplacement(matrix.pattern)+'\n'+matrix.pattern);	//ignore
		//System.out.println(result+"\n\n"+String.format("%s", result2));					//무시(그냥 테스트용);
		
		
		System.out.println(result.equals(result2));		//"""result와 result2의 결과가 같음(true 출력함)"""
		

		/*/무시(그냥 테스트);
		String test2 = "aaaa"; String[] s = {"aa\naa","aa\\naa","aa\\\naa","aa\\\\naa"};
		for(String n:s) {
			System.out.println(test2.replaceAll("a+", n)+"\t---");
		}
		/*/
		System.out.println(Pattern.compile(matrix.pattern).matcher(test).replaceAll(matrix.pattern_result).equals(test.replaceAll(matrix.pattern, matrix.pattern_result)));
		//result result2를 한줄로 비교
		//Pattern.compile(regex).matcher(target str).replaceAll(replace regex)) 
		String str = "ababerbbabbb";
		System.out.println(str.replace("ab", "cc"));	//regex 없는 단순 대체; useRegex field가 false일때 사용될 예정
		System.out.println(matrix.replace(test));
		Macro abcc = new Macro("ab","cc");
		System.out.println(abcc.replace(str));
	}

}
