import java.awt.HeadlessException;

import javax.swing.JFrame;
import javax.swing.JTextArea;

public class HelperGUI extends JFrame {
	
	private JTextArea originalSourceField;
	private JTextArea convertedSourceField;
	private JTextArea helpDoc;
	
	public static final int WIDTH = 1280;
	public static final int HEIGHT = 960;

	public HelperGUI() throws HeadlessException {
		// TODO Auto-generated constructor stub
		this("C Coding Helper");
	}

	public HelperGUI(String arg0) throws HeadlessException {
		super(arg0);
		// TODO Auto-generated constructor stub
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(WIDTH, HEIGHT);
		
		
		
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		HelperGUI gui = new HelperGUI();
		gui.setVisible(true);
	}
	
	private static int multFloor(int multiplicand, double ratio)	//for easy resizing GUI, panel, etc
	{
		return (int) Math.floor(multiplicand * ratio);
	}
	private static int multFloor(int multiplicand, int percentage)	//overloaded for use of percentage
	{
		return (int) Math.floor(multiplicand * percentage / 100);
	}
	
}
