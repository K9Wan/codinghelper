package com.prod.project;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
/***
 * K9Wan's Custom Regex placeholder;
 * @author ProDuct0339
 */
public class Rule implements Serializable {
	
	private String input;
	private String output;
	private String name;
	private boolean active;
	
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
		setName(name);
		setInput(input);
		setOutput(output);
		active = true;
	}
	
	public String getName() { return name; }
	public void setName(String name) { this.name = name; } 
	public String getInput() { return input; }
	public void setInput(String input) { this.input = input; }
	public String getOutput() { return output; }
	public void setOutput(String output) { this.output = output; }
	public boolean getActive() { return active; }
	public void setActive(boolean active) { this.active = active; }
	
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
		boolean res = true;
		if (rule.input != input) res = false;
		if (rule.output != output) res = false;
		if (rule.name != name) res = false;
		return res;
	}
}
