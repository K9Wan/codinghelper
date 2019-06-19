import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.*;	//Matcher MatchResult Pattern PatterSyntaxException

public class MacroConvertor {

	private boolean hasArgs;
	private boolean multiLines;
	private Macro replace;
	
	public MacroConvertor() {
		// TODO Auto-generated constructor stub
	}
	
	public MacroConvertor(boolean hasArgs, boolean multiLines, Macro macro) {
		this.hasArgs = hasArgs;
		this.multiLines = multiLines;
		this.replace = macro;
	}
	
	private String convert(String str) {
		if(replace != null) {
			return replace.replace(str);
		}
		return null;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		test();
	}
	public static void p(Object s) {
		System.out.println(s);
	}
	
	private static String identifier(String str) {		//replacement only for whole words: prevents replacements like i3t, pri7tf
		return "(?<![\\w])"+str+"(?![\\w])";
	}

	public static String expand(String str) {
		try {
		str=str.replaceAll("#expand (\\w+)(\\r?\\n)", "#expand $1 $2");
		str=str.replaceAll("#expand (\\w+\\([^\\)]+\\))(\\r?\\n)", "#expand $1 $2");
		//above two lines makes macro have trailing ' ' to match regex when macro is defined to empty and no trailing ' '; 
		ArrayList<MacroConvertor> expandList = new ArrayList<MacroConvertor>(30);
		String[] list = str.split("#expand");
		ArrayList<String> stringList = new ArrayList<String>(30);
		Collections.addAll(stringList, list);
		stringList.remove(0);
		for(int i=0;i<stringList.size();i++) {
			stringList.set(i, "#expand"+stringList.get(i));
		}//now stringList contains strings each starting with #expand
		
		String s1 = "#expand (\\w+) ([^\\r\\n]*)\\r?\\n";
		Pattern p1 = Pattern.compile(s1);	//no args;
		//$1:name, $2:firstline - may have trailing backslash if has multiline;
		String s2 = "#expand (\\w+)\\(([^\\)]+)\\) ([^\\r\\n]*)\\r?\\n";
		Pattern p2 = Pattern.compile(s2);	//has args;
		//$1:name, $2:arg,arg,... , $3:firstline - may have trailing backslash if has multiline;
		String s3 = "#expand (\\w+)(\\(([^\\)]+)\\))? ([^\\r\\n]*)\\\\\\r?\\n";
		Pattern p3 = Pattern.compile(s3);		//multi lines;
		//$1:name, $2:(arg,arg,...) (or undefined), $3:arg,arg,... (or undefined), $4:firstline
		String s4 = "\\\\\\r?\\n([^\\r\\n\\\\]+)";
		Pattern p4 = Pattern.compile(s4);	//matches other lines;
		//$1:another line
		boolean hasArgs=false,hasMultipleLines=false;
		for(String s:stringList) {
			//p(s);
			if(p1.matcher(s).find()) {
				hasArgs = false;
			}else if(p2.matcher(s).find()) {
				hasArgs = true;
			}
			if(p3.matcher(s).find()) {
				hasMultipleLines = true;
			}else {
				hasMultipleLines = false;
			}
			//p(""+hasArgs+" "+hasMultipleLines);
			String result = "";
			if(hasMultipleLines) {
				Matcher matcherFirstLine = p3.matcher(s);
				matcherFirstLine.find();
				result = matcherFirstLine.group(4);		//result now contains firstline
				Matcher matcherRest = p4.matcher(s);
				while(matcherRest.find()) {		//find rest of mulitple lines and append
					result+="\n"+matcherRest.group(1);
				}
			}else {
				if(hasArgs) {
					Matcher m2 = p2.matcher(s);
					m2.find();
					result = m2.group(3);
				}else {
					Matcher m1 = p1.matcher(s);
					m1.find();
					result = m1.group(2);
				}
			}		//result now contains macro declaration without macro name and backslash;
			//TODO what if macro contains string literal including backslash
			//p(result);
			String org = "";	//original str for Macro.replace
			String rst = "";	//result str for Macro.replace
			if(!hasArgs) {
				Matcher m1 = p1.matcher(s);
				m1.find();
				org = m1.group(1);
				org = identifier(org);
				org = "(?<!#expand )"+org;		//keep #expand definition
				rst = result;
			}else {
				Matcher m2 = p2.matcher(s);
				m2.find();
				org = m2.group(1);
				org = identifier(org);
				org = "(?<!#expand )"+org;		//keep #expand definition
				String[] args = m2.group(2).split(" *, *");		//parse arguments
				String[] w = new String[args.length];
				Arrays.fill(w, "(\\w+)");
				org = org+"\\("+String.join(" *, *", w)+"\\)";	//will match macroname(identifier, identifier, ...) 
				Map<String, String> mapping = new HashMap<String, String>();
				for(int i=0;i<args.length;i++) {
					mapping.put(args[i], "\\$"+(i+1));
				}
				//p(mapping);
				for(Map.Entry<String, String> entry: mapping.entrySet()) {
					result = result.replaceAll(identifier(entry.getKey()), entry.getValue());
				}		//a+b+c will be $1+$2+$3
				rst = result;
			}
			//p("org: "+org+"\nrst: "+result+"\n");
			expandList.add(new MacroConvertor(hasArgs, hasMultipleLines, new Macro(org, rst, true)));	//add to list
		}
		for(MacroConvertor macro: expandList) {
			str = macro.convert(str);
		}		//expand each macro
		//p(str);
		}catch (java.lang.IllegalStateException e1){
			e1.printStackTrace();
		}
		return str;
	}
	
	private static void test() {
		String e1 = "#include <stdio.h>\n#expand null NULL\n#expand ADD(a,b, c) a+b+c\n#expand print(ary, n) do {\\\nfor(int i=0;i<n;i++)\\\n{\\\nprintf(\"%d \",ary[i]);\\\n}puts(\"\");\\\n}while(0)\n\nvoid nullify(int ** p)\n{\n\t*p = NULL;\n}\n\nint main(void)\n{\n\tint * a = null;\n\tchar b = ADD(3, 5,7);\n\tchar * null_pointer = null;\n\tnullify(&a);\n\tint ary1[] = {3,5,b};\n\tprint(ary1, 3);\n}";
		
		/*	example testcase
#include <stdio.h>
#expand null NULL
#expand ADD(a,b, c) a+b+c
#expand print(ary, n) do {\
for(int i=0;i<n;i++)\
{\
printf("%d ",ary[i]);\
}puts("");\
}while(0)

void nullify(int ** p)
{
	*p = NULL;
}

int main(void)
{
	int * a = null;
	char b = ADD(3, 5,7);
	char * null_pointer = null;
	nullify(&a);
	int ary1[] = {3,5,b};
	print(ary1, 3);
}
		 */
		p(e1);
		
		ArrayList<MacroConvertor> ml = new ArrayList<MacroConvertor>(30);
		String[] list = e1.split("#expand");
		ArrayList<String> sl = new ArrayList<String>(30);
		Collections.addAll(sl, list);
		sl.remove(0);
		for(String s:sl) {
			p("\n\n#expand"+s);
		}
		/*/
		Pattern p = Pattern.compile("\\n#expand (\\w+)");
		Matcher m = p.matcher(e1);
		MatchResult r = m.toMatchResult();
		while(m.find()) {
			p(m.group(1));
		}
		/*/

		for(int i=0;i<sl.size();i++) {
			sl.set(i, "#expand"+sl.get(i));
		}
		String s1 = "#expand (\\w+) ([^\\r\\n]+)\\r?\\n";
		Pattern p1 = Pattern.compile(s1);	//no args;
		//$1:name, $2:firstline - may have trailing backslash;
		String s2 = "#expand (\\w+)\\(([^\\)]+)\\) ([^\\r\\n]+)\\r?\\n";
		Pattern p2 = Pattern.compile(s2);	//has args;
		//$1:name, $2:arg,arg,... , $3:firstline - may have trailing backslash;
		String s3 = "#expand (\\w+)(\\(([^\\)]+)\\))? ([^\\r\\n]+)\\\\\\r?\\n";
		Pattern p3 = Pattern.compile(s3);		//multi lines;
		//$1:name, $2:(arg,arg,...) (or undefined), $3:arg,arg,... (or undefined), $4:firstline
		String s4 = "\\\\\\r?\\n([^\\r\\n\\\\]+)";
		Pattern p4 = Pattern.compile(s4);	//matches other lines;
		//$1:another line
		boolean ar=false,mu=false;
		for(String s:sl) {
			//p(s);
			if(p1.matcher(s).find()) {
				ar = false;
			}else if(p2.matcher(s).find()) {
				ar = true;
			}
			if(p3.matcher(s).find()) {
				mu = true;
			}else {
				mu = false;
			}
			p(""+ar+" "+mu);
			String result = "";
			if(mu) {
				Matcher mf = p3.matcher(s);
				p(p3.matcher(s).regionStart());
				mf.find();
				result = mf.group(4);
				Matcher mm = p4.matcher(s);
				while(mm.find()) {
					result+="\n"+mm.group(1);
				}
			}else {
				if(ar) {
					Matcher m2 = p2.matcher(s);
					m2.find();
					result = m2.group(3);
				}else {
					Matcher m1 = p1.matcher(s);
					m1.find();
					result = m1.group(2);
				}
			}
			p(result);
			String org = "";
			String rst = "";
			if(!ar) {
				Matcher m1 = p1.matcher(s);
				m1.find();
				org = m1.group(1);
				org = identifier(org);
				org = "(?<!#expand )"+org;
				rst = result;
			}else {
				Matcher m2 = p2.matcher(s);
				m2.find();
				org = m2.group(1);
				org = identifier(org);
				org = "(?<!#expand )"+org;
				String[] args = m2.group(2).split(" *, *");
				String[] w = new String[args.length];
				Arrays.fill(w, "(\\w+)");
				org = org+"\\("+String.join(" *, *", w)+"\\)";
				Map<String, String> mapping = new HashMap<String, String>();
				for(int i=0;i<args.length;i++) {
					mapping.put(args[i], "\\$"+(i+1));
				}
				p(mapping);
				for(Map.Entry<String, String> entry: mapping.entrySet()) {
					result = result.replaceAll(identifier(entry.getKey()), entry.getValue());
				}
				rst = result;
			}
			p("org: "+org+"\nrst: "+result+"\n");
			ml.add(new MacroConvertor(ar, mu, new Macro(org, rst, true)));
		}
		for(MacroConvertor mc: ml) {
			e1 = mc.convert(e1);
		}
		p(e1);
	}

}
