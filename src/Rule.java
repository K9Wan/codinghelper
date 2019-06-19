
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
/***
 * K9Wan's Custom Regex placeholder;
 * @author ProDuct0339
 */
public class Rule extends Macro implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3026725069646278851L;

	public static final String DEFAULT_DOCSTRING = "DocString for this rule not added";

	public static ArrayList<Rule> ruleList = new ArrayList<Rule>(30);
	
	public Rule() {
		this(getGeneratedName(), "input goes here", "output goes here");
	}
	
	public Rule(String name) {
		this(name, "input goes here", "output goes here");
	}
	
	public Rule(String input, String output) {
		this(getGeneratedName(), input, output);
	}
	
	public Rule(String name, String input, String output) {
		this(name, input, output, false, DEFAULT_DOCSTRING, true);
	}

	public Rule(String name, String input, String output, boolean useRegex, String helpDoc) {
		super(name, input, output, useRegex, helpDoc, true);
	}
	
	public Rule(String name, String input, String output, boolean useRegex, String helpDoc, boolean active) {
		super(name, input, output, useRegex, helpDoc, active);
	}
	
	public boolean getActive() { return getEnabled(); }
	public void setActive(boolean active) { setEnabled(active); }
	
	public static String getGeneratedName() {
		SimpleDateFormat formatter= new SimpleDateFormat("yyMMddHHmmssSSS");  
		Date date = new Date(System.currentTimeMillis());
		return "Rule " + formatter.format(date);
	}
	
	@Override
	public String toString() {
		return "Rule " + name; // + ", input: " + input + ", output: " + output;
	}
	
	public boolean sameName(Rule rule) {
		if (rule.name.equals(name)) return true;
		return false;
	}
	
	public boolean sameName(String rule) {
		if (rule.equals(name)) return true;
		return false;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Rule)) return false;
		Rule rule = (Rule)obj;
		if (rule.useRegex != useRegex) return false;
		boolean res = true;
		if (rule.input != input) res = false;
		if (rule.output != output) res = false;
		if (rule.name != name) res = false;
		return res;
	}
	
	public static void predefine() {	//will be invoked when program starts;
		ruleList.add(new Rule(
				"malloc2d",
				"(\\w+)( ?\\* ?\\* ?)(\\w+)( ?= ?)\\w?alloc\\[(\\w+)\\]\\[(\\w+)\\];",
				"$1$2$3$4malloc($5*sizeof *$3 + $6*$5*sizeof **$3);\nfor(int i=0;i<$5;i++)\n{\n\t$3[i]$4($1 *)($3+$5)+i*n;\n}\n",
				true,
				"Use to allocate 2D array.\nExample: uint_8 ** matrix = malloc[6][cols];"));
		ruleList.add(new Rule(
				"clear DEBUG",
				"#if.+DEBUG\\r?\\n([^#]*)#endif",
				"",
				true,
				"Eliminates code for debugging.\nIt will work for ifdef DEBUG, if defined DEBUG, etc.",
				false));
	}
	
	public static String execute(String str) {
		for(Rule r:ruleList) {
			if(r.enabled) {
				str = r.replace(str);
			}
		}
		return str;
	}
	
	public static Rule findByName(String name) {
		for(Rule r:ruleList) {
			if(r.sameName(name)){
				return r;
			}
		}
		return null;
	}
}
