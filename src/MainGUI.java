
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Stack;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;

public class MainGUI extends JFrame implements ActionListener, KeyListener, MouseListener {
	
	private static final long serialVersionUID = 1L;
	
	private static final String tempUntransActionCommand = "untrans.alternative";
	
	final JFileChooser fc = new JFileChooser();
	
	private boolean rulesActive;
	private boolean toolBarActive;
	private boolean rulesIncluded;
	private boolean toolBarIncluded;
	private File latestFile;
	private File latestRuleFile;
	private Dimension previousWindow;
	private int selectedRule;
	private String selectedText;
	private JTextComponent selectedArea;
	private boolean isSynced;
	private boolean shift;
	private boolean ctrl;
	private boolean alt;
	
	private JMenuBar toolBar;
	private ArrayList<Rule> rules;
	private Key shortcut[][] = {
			{null,null,null,null,null,null,null,null,null,null,null,null},
			{null,null,null,null,null,null,null,null,null,null,null},
			{null,null,null,null,null},
			{null,null,null,null,null,null,null,null},
			{null,null,null,null},
			{null,null,null},
			{null,null}
		};
	private Stack<String> history = new Stack<String>();
	private String previous;
	
	private JPanel mainPane;
		private JPanel transPane;
			private TrackingJTextArea pseudoCArea;
				private JScrollPane pseudoCScroll;
			private JPanel transButtonWrapper;
			private JTextArea normalCArea;
				private JScrollPane normalCScroll;
		private JPanel rulePane;
			private JPanel ruleListWrapper;
				private JScrollPane ruleListScroll;
			private JPanel ruleEditorWrapper;
				private JTextField ruleNameArea;
				private JTextArea ruleInArea;
					private JScrollPane ruleInScroll;
				private JTextArea ruleOutArea;
					private JScrollPane ruleOutScroll;
				private JTextArea ruleDocArea;
					private JScrollPane ruleDocScroll;
				private JCheckBox ruleUseRegexToggleButton;
	
	public static void main(String args[]) {
		MainGUI gui = new MainGUI();
		gui.setVisible(true);
	}
	
	public MainGUI() {
		initiate();
	}
	
	private void initiate() {
		rulesActive = false;
		toolBarActive = true;
		rulesIncluded = false;
		toolBarIncluded = true;
		latestFile = null;
		latestRuleFile = null;
		previousWindow = new Dimension(800, 600);
		selectedRule = 0;
		selectedText = "";
		selectedArea = pseudoCArea;
		isSynced = false;
		shift = false;
		ctrl = false;
		alt = false;
		
		toolBar = new JMenuBar();
		rules = Rule.ruleList;
		Rule.predefine();
		history.push("");
		shortcut[0][0] = new Key(true, true, false, KeyEvent.VK_N);
		shortcut[0][1] = new Key(false, true, false, KeyEvent.VK_O);
		shortcut[0][2] = new Key(true, true, false, KeyEvent.VK_O);
		shortcut[0][3] = new Key(false, true, false, KeyEvent.VK_S);
		shortcut[0][4] = new Key(true, true, false, KeyEvent.VK_S);
		shortcut[0][5] = new Key(true, true, true, KeyEvent.VK_N);
		shortcut[0][6] = new Key(false, true, true, KeyEvent.VK_O);
		shortcut[0][7] = new Key(true, true, true, KeyEvent.VK_O);
		shortcut[0][8] = new Key(false, true, true, KeyEvent.VK_S);
		shortcut[0][9] = new Key(true, true, true, KeyEvent.VK_S);
		shortcut[0][10] = new Key(true, false, false, KeyEvent.VK_F5);
		shortcut[0][11] = new Key(true, false, false, KeyEvent.VK_ESCAPE);
		
		shortcut[1][0] = new Key(false, true, false, KeyEvent.VK_Z);
		shortcut[1][1] = new Key(true, true, false, KeyEvent.VK_Z);
		shortcut[1][2] = new Key(false, true, false, KeyEvent.VK_X);
		shortcut[1][3] = new Key(false, true, false, KeyEvent.VK_C);
		shortcut[1][4] = new Key(false, true, false, KeyEvent.VK_V);
		shortcut[1][5] = new Key(false, false, false, KeyEvent.VK_DELETE);
		shortcut[1][6] = new Key(false, true, false, KeyEvent.VK_A);
		shortcut[1][7] = new Key(false, true, false, KeyEvent.VK_F);
		shortcut[1][8] = new Key(false, true, false, KeyEvent.VK_H);
		shortcut[1][9] = new Key(false, true, false, KeyEvent.VK_RIGHT);
		shortcut[1][10] = new Key(false, true, false, KeyEvent.VK_LEFT);
		
		shortcut[2][0] = new Key(false, true, false, KeyEvent.VK_ENTER);
		shortcut[2][1] = new Key(true, true, false, KeyEvent.VK_ENTER);
		shortcut[2][2] = new Key(false, true, false, KeyEvent.VK_E);
		shortcut[2][3] = new Key(false, false, true, KeyEvent.VK_S);
		shortcut[2][4] = new Key(false, false, false, KeyEvent.VK_F6);
		
		shortcut[3][0] = new Key(false, false, true, KeyEvent.VK_E);
		shortcut[3][1] = new Key(false, false, true, KeyEvent.VK_ENTER);
		shortcut[3][2] = new Key(false, false, true, KeyEvent.VK_HOME);
		shortcut[3][3] = new Key(false, false, true, KeyEvent.VK_END);
		shortcut[3][4] = new Key(false, false, true, KeyEvent.VK_DOWN);
		shortcut[3][5] = new Key(false, false, true, KeyEvent.VK_UP);
		shortcut[3][6] = new Key(false, false, true, KeyEvent.VK_SPACE);
		shortcut[3][7] = new Key(true, false, false, KeyEvent.VK_DELETE);
		
		shortcut[5][0] = new Key(false, false, true, KeyEvent.VK_R);
		shortcut[5][1] = new Key(false, false, false, KeyEvent.VK_F4);
		shortcut[5][2] = new Key(false, false, false, KeyEvent.VK_F2);
		
		shortcut[6][0] = new Key(true, true, false, KeyEvent.VK_H);
		int i, j;
		for (i = 0; i < shortcut.length; i++) {
			for (j = 0; j < shortcut[i].length; j++) {
				if (shortcut[i][j] != null) continue;
				shortcut[i][j] = new Key();
			}
		}
		mainPane = new JPanel();
			transPane = new JPanel();
				pseudoCArea = new TrackingJTextArea(5, 5);
					pseudoCScroll = new JScrollPane(pseudoCArea);
				transButtonWrapper = new JPanel();
				normalCArea = new JTextArea(5, 5);
					normalCScroll = new JScrollPane(normalCArea);
			rulePane = new JPanel();
				ruleListWrapper = new JPanel();
					ruleListScroll = new JScrollPane(ruleListWrapper);
				ruleEditorWrapper = new JPanel(new BorderLayout());
					ruleNameArea = new JTextField(10);
					ruleInArea = new JTextArea(5, 5);
						ruleInScroll = new JScrollPane(ruleInArea);
					ruleOutArea = new JTextArea(5, 5);
						ruleOutScroll = new JScrollPane(ruleOutArea);
					ruleDocArea = new JTextArea(5, 5);
						ruleDocScroll = new JScrollPane(ruleDocArea);
		
		new Resize(this);
		fc.setMultiSelectionEnabled(false);
		fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
		pseudoCArea.addKeyListener(this);
		normalCArea.addKeyListener(this);
		ruleNameArea.addKeyListener(this);
		ruleInArea.addKeyListener(this);
		ruleOutArea.addKeyListener(this);
		
		pseudoCArea.addMouseListener(this);
		normalCArea.addMouseListener(this);
		ruleNameArea.addMouseListener(this);
		ruleInArea.addMouseListener(this);
		ruleOutArea.addMouseListener(this);
		
		pseudoCArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_H, KeyEvent.CTRL_DOWN_MASK, false), "none");
		normalCArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_H, KeyEvent.CTRL_DOWN_MASK, false), "none");
		ruleNameArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_H, KeyEvent.CTRL_DOWN_MASK, false), "none");
		ruleOutArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_H, KeyEvent.CTRL_DOWN_MASK, false), "none");
		ruleInArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_H, KeyEvent.CTRL_DOWN_MASK, false), "none");
		
		setTitle("EasyC - Pseudo-C Transpiler");
		setSize(800, 600);
		setLayout(new BorderLayout());
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		JMenu tb[] = {new JMenu("File"), new JMenu("Edit"), new JMenu("Transpiler"), new JMenu("Rules"), new JMenu("Setting"), new JMenu("Window"), new JMenu("Help")};
		JMenuItem tbitem[][] = {
				{new JMenuItem("New File") ,new JMenuItem("Open File") ,new JMenuItem("Open File from URL...") ,new JMenuItem("Save File") ,new JMenuItem("Save File as...") ,new JMenuItem("New Rule") ,new JMenuItem("Open Rule") ,new JMenuItem("Open Rule from URL...") ,new JMenuItem("Save Rule") ,new JMenuItem("Save Rule as...") ,new JMenuItem("Restart Program") ,new JMenuItem("Exit Program")}, 
				{new JMenuItem("Undo") ,new JMenuItem("Redo") ,new JMenuItem(new DefaultEditorKit.CutAction()) ,new JMenuItem(new DefaultEditorKit.CopyAction()) ,new JMenuItem(new DefaultEditorKit.PasteAction()) ,new JMenuItem("Delete") ,new JMenuItem("Select All") ,new JMenuItem("Find") ,new JMenuItem("Replace") ,new JMenuItem("Select Next") ,new JMenuItem("Select Previous")},
				{new JMenuItem("Transpile") ,new JMenuItem("Reverse Transpile"), new JMenuItem("Expand Macros") ,new JMenuItem("Sync/Desync..."), new JMenuItem("Swap Texts")},
				{new JMenuItem("Edit Selected Rule") ,new JMenuItem("Confirm Selected Rule") ,new JMenuItem("Select First Rule"),new JMenuItem("Select Last Rule"), new JMenuItem("Select Next Rule") ,new JMenuItem("Select Previous Rule"), new JMenuItem("Activate/Deactivate Selected Rule") ,new JMenuItem("Delete Selected Rule")},
				{new JMenuItem("Shortcuts..."), new JMenuItem("Coloring"), new JMenuItem("Fonts"), new JMenuItem("Features")},
				{new JMenuItem("Show/Hide Rule Tab"), new JMenuItem("Fullscreen/Windowed"), new JMenuItem("Show/Hide Toolbar")},
				{new JMenuItem("Help"), new JMenuItem("About")}
				};
		i = 0; j = 0;
		for (JMenuItem list[]: tbitem) {
			j = 0;
			for (JMenuItem e: list) {
				e.addActionListener(this);
				e.setText(e.getActionCommand());
				e.setActionCommand("action." + tb[i].getText().toLowerCase() + "." + j);
				tb[i].add(e);
				if (!shortcut[i][j].isUnset()) e.setAccelerator(shortcut[i][j].toKeyStroke());
				j++;
			}
			tb[i].addActionListener(this);
			toolBar.add(tb[i]);
			i++;
		}
		tbitem[1][2].setText("Cut");
		tbitem[1][3].setText("Copy");
		tbitem[1][4].setText("Paste");
		add(toolBar, BorderLayout.NORTH);
		
		//tbitem[2][3].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.SHIFT_DOWN_MASK|KeyEvent.ALT_DOWN_MASK|KeyEvent.CTRL_DOWN_MASK));
		
		mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.LINE_AXIS));
		transPane.setLayout(new BoxLayout(transPane, BoxLayout.PAGE_AXIS));
		
		pseudoCScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		pseudoCScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		normalCScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		normalCScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		transButtonWrapper.setLayout(new FlowLayout());
		
		updateRatios(mainPane, pseudoCScroll, normalCScroll, transButtonWrapper);
		updateShows();
		
		JButton transButton = new JButton("▼");
		transButton.setToolTipText("Transpile");
		JButton untransButton = new JButton("△");
		untransButton.setToolTipText("Untranspile");
		JButton expandButton = new JButton("⇩");
		expandButton.setToolTipText("Expand");
		JButton swapButton = new JButton("↑↓");
		swapButton.setToolTipText("Swap");
		
		transButton.addActionListener(this);
		untransButton.addActionListener(this);
		expandButton.addActionListener(this);
		swapButton.addActionListener(this);
		transButton.setActionCommand("action.transpiler.0");
		untransButton.setActionCommand("action.transpiler.1");
		expandButton.setActionCommand("action.transpiler.2");
		swapButton.setActionCommand("action.transpiler.4");
		
		transButtonWrapper.add(transButton);
		transButtonWrapper.add(untransButton);
		transButtonWrapper.add(expandButton);
		transButtonWrapper.add(swapButton);
		
		pseudoCArea.setToolTipText("EasyC code goes here...");
		normalCArea.setToolTipText("ordinary C code goes here...");
		
		transPane.add(Box.createRigidArea(new Dimension(5, 5)));
		transPane.add(pseudoCScroll);
		transPane.add(Box.createVerticalGlue());
		transPane.add(Box.createRigidArea(new Dimension(5, 5)));
		transPane.add(transButtonWrapper);
		transPane.add(Box.createRigidArea(new Dimension(5, 5)));
		transPane.add(Box.createVerticalGlue());
		transPane.add(normalCScroll);
		transPane.add(Box.createRigidArea(new Dimension(5, 5)));
		
		rulePane.setLayout(new BoxLayout(rulePane, BoxLayout.PAGE_AXIS));
		
		ruleListScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		ruleListScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		JPanel ruleEditorPane = new JPanel();
		JPanel ruleButtonWrapper = new JPanel(new BorderLayout());

		JButton ruleConfirmButton = new JButton("✓");
		ruleConfirmButton.addActionListener(this);
		ruleConfirmButton.setActionCommand("action.rules.1");
		ruleConfirmButton.setToolTipText("Confirm this Rule");
		
		//ruleUseRegexToggleButton = new JToggleButton("This uses regex");
		ruleUseRegexToggleButton = new JCheckBox("This uses regex");
		ruleUseRegexToggleButton.setToolTipText("Check this if your rule uses Regular Expression");
		
		ruleNameArea.setToolTipText("Rule name");
		ruleInArea.setToolTipText("What to detect...");
		ruleOutArea.setToolTipText("What to turn into...");
		ruleDocArea.setToolTipText("Description for this rule...");
		ruleDocArea.setText(Rule.DEFAULT_DOCSTRING);
		
		ruleButtonWrapper.add(ruleUseRegexToggleButton, BorderLayout.WEST);
		ruleButtonWrapper.add(ruleConfirmButton, BorderLayout.EAST);
		ruleInScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		ruleInScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		ruleOutScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		ruleOutScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		updateRules();
		
		ruleEditorPane.setLayout(new BoxLayout(ruleEditorPane, BoxLayout.PAGE_AXIS));
		ruleEditorPane.add(Box.createRigidArea(new Dimension(5, 5)));
		ruleEditorPane.add(ruleNameArea);
		ruleEditorPane.add(Box.createRigidArea(new Dimension(2, 2)));
		ruleEditorPane.add(Box.createVerticalGlue());
		ruleEditorPane.add(ruleInScroll);
		ruleEditorPane.add(Box.createVerticalGlue());
		ruleEditorPane.add(ruleOutScroll);
		ruleEditorPane.add(Box.createRigidArea(new Dimension(2, 2)));
		ruleEditorPane.add(ruleDocScroll);
		ruleEditorPane.add(Box.createRigidArea(new Dimension(5, 5)));
		
		ruleEditorWrapper.add(ruleEditorPane, BorderLayout.CENTER);
		ruleEditorWrapper.add(ruleButtonWrapper, BorderLayout.SOUTH);
		
		rulePane.add(Box.createRigidArea(new Dimension(5, 5)));
		rulePane.add(ruleListScroll);
		rulePane.add(Box.createVerticalGlue());
		rulePane.add(ruleEditorWrapper);
		rulePane.add(Box.createRigidArea(new Dimension(5, 5)));
		
		mainPane.add(Box.createRigidArea(new Dimension(5, 5)));
		mainPane.add(transPane);
		mainPane.add(Box.createHorizontalGlue());
//		mainPane.add(rulePane);
		mainPane.add(Box.createRigidArea(new Dimension(5, 5)));
		
		add(mainPane, BorderLayout.CENTER);
	}
	
	private void updateRatios(JPanel mainPane, JScrollPane pseudoCScroll, JScrollPane normalCScroll, JPanel transButtonWrapper) {
		int newWidth = (int)(rulesActive ? getBounds().width * 0.6 : getBounds().width );
		int newHeight = (int)(toolBarActive ? getBounds().height - toolBar.getHeight() : getBounds().height);
		mainPane.setPreferredSize(getDimentionRatio(newWidth, newHeight, 1, 1));
		pseudoCScroll.setPreferredSize(getDimentionRatio(newWidth, newHeight, 0.8, 0.4));
		normalCScroll.setPreferredSize(getDimentionRatio(newWidth, newHeight, 0.8, 0.4));
		transButtonWrapper.setPreferredSize(getDimentionRatio(newWidth, newHeight, 0.5, 0.1));
		if (rulesActive) {
			ruleInScroll.setPreferredSize(getDimentionRatio(getBounds().width - newWidth, newHeight, 0.8, 0.2));
			ruleOutScroll.setPreferredSize(getDimentionRatio(getBounds().width - newWidth, newHeight, 0.8, 0.2));
			ruleDocScroll.setPreferredSize(getDimentionRatio(getBounds().width - newWidth, newHeight, 0.8, 0.2));
			ruleListScroll.setPreferredSize(getDimentionRatio(getBounds().width - newWidth, newHeight, 0.8, 0.3));
			ruleEditorWrapper.setPreferredSize(getDimentionRatio(getBounds().width - newWidth, newHeight, 0.8, 0.7));
		}
	}
	
	private void updateShows() {
		if (!toolBarActive && toolBarIncluded) {
			remove(toolBar);
			toolBarIncluded = false;
		}
		if (toolBarActive && !toolBarIncluded) {
			add(toolBar, BorderLayout.NORTH);
			toolBarIncluded = true;
		}
		if (!rulesActive && rulesIncluded) {
			mainPane.remove(rulePane);
			rulesIncluded = false;
		}
		if (rulesActive && !rulesIncluded) {
			mainPane.add(rulePane, BorderLayout.EAST);
			rulesIncluded = true;
		}
		SwingUtilities.updateComponentTreeUI(this);
		updateRules();
	}
	
	@SuppressWarnings("serial")
	private class JRule extends JButton {
		Rule rule;
		@SuppressWarnings("unused")
		public JRule() {
			this(0, new Rule(), null);
		}
		@SuppressWarnings("unused")
		public JRule(int num) {
			this(num, new Rule(), null);
		}
		@SuppressWarnings("unused")
		public JRule(int num, ActionListener comp) {
			this(num, new Rule(), comp);
		}
		public JRule(int num, Rule rule, ActionListener comp) {
			setRule(rule);
			JLabel title = new JLabel(rule.getName());
			JButton edit = new JButton("E");
			JButton activate = new JButton("!");
			JButton delete = new JButton("━");
			
			edit.setToolTipText("Edit this rule");
			activate.setToolTipText("Toggle enable/disable");
			delete.setToolTipText("Delete this rule");
			
			edit.addActionListener(comp);
			activate.addActionListener(comp);
			delete.addActionListener(comp);
			addActionListener(comp);
			edit.setActionCommand("action.rules.0." + num);
			activate.setActionCommand("action.rules.6." + num);
			delete.setActionCommand("action.rules.7." + num);
			setActionCommand("action.rules.4." + num);
			edit.setMargin(new Insets(0, 0, 0, 0));
			activate.setMargin(new Insets(0, 0, 0, 0));
			delete.setMargin(new Insets(0, 0, 0, 0));
			setMargin(new Insets(2, 2, 2, 2));
			
			setLayout(new BorderLayout());
			setBorder(new CompoundBorder(
					BorderFactory.createLineBorder(Color.black),
					BorderFactory.createEmptyBorder(10, 10, 10, 10)
					));
			if (selectedRule == num) {
				if (rule.getActive()) setBackground(Color.getHSBColor((1f / 12), 0.3f, 1));
				else setBackground(Color.getHSBColor((1f / 12), 0.3f, 0.7f));
			} else {
				if (rule.getActive()) setBackground(Color.WHITE);
				else setBackground(Color.LIGHT_GRAY);
			}
			
			title.setPreferredSize(new Dimension(70, 30));
			edit.setPreferredSize(new Dimension(20, 30));
			activate.setPreferredSize(new Dimension(20, 30));
			delete.setPreferredSize(new Dimension(20, 30));
			
			setPreferredSize(new Dimension(100, 30));
			
			setMaximumSize(new Dimension(rulePane.getMaximumSize().width, 50));
			
			JPanel buttons = new JPanel(new GridLayout(1, 4));
			buttons.setAlignmentY(RIGHT_ALIGNMENT);
			buttons.add(edit);
			buttons.add(activate);
			buttons.add(delete);
			buttons.add(Box.createRigidArea(new Dimension(20, 30)));
			
			add(title, BorderLayout.CENTER);
			add(buttons, BorderLayout.EAST);
		}
		@SuppressWarnings("unused")
		public Rule getRule() { return rule; }
		
		public void setRule(Rule rule) { this.rule = rule; }
	}
	
	private class Key {
		private boolean shift;
		private boolean ctrl;
		private boolean alt;
		private int key;
		
		private static final int KEY_UNDEFINED = 5000;
		
		public Key() {
			this(false, false, false, KEY_UNDEFINED);
		}
		
		public Key(int key) {
			this(false, false, false, key);
		}
		
		public Key(int n, int key) {
			this(((n & 1) != 0), ((n & 2) != 0), ((n & 4) != 0), key);
		}
		
		public Key(boolean shift, boolean ctrl, boolean alt, int key) {
			setShift(shift);
			setCtrl(ctrl);
			setAlt(alt);
			setKey(key);
		}
		
		public void setShift(boolean shift) { this.shift = shift; }
		public void setCtrl(boolean ctrl) { this.ctrl = ctrl; }
		public void setAlt(boolean alt) { this.alt = alt; }
		public void setKey(int key) { this.key = key; }
		
		public boolean getShift() { return shift; }
		public boolean getCtrl() { return ctrl; }
		public boolean getAlt() { return alt; }
		public int getKey() { return key; }
		
		public boolean isUnset() {
			return (getKey() == KEY_UNDEFINED);
		}
		
		@Override
		public String toString() {
			if (getKey() == KEY_UNDEFINED) return "";
			return "(" + (getCtrl() ? "Ctrl+" : "") + (getShift() ? "Shift+" : "") + (getAlt() ? "Alt+" : "") + KeyEvent.getKeyText(getKey()) + ")";
		}
		
		public KeyStroke toKeyStroke() {
			return KeyStroke.getKeyStroke(key, (shift?KeyEvent.SHIFT_DOWN_MASK:0)|(ctrl?KeyEvent.CTRL_DOWN_MASK:0)|(alt?KeyEvent.ALT_DOWN_MASK:0));
		}
	}
	
	private void updateRules() {
		ruleListWrapper.setLayout(new GridLayout(rules.size(), 1));
		ruleListWrapper.setLayout(new BoxLayout(ruleListWrapper, BoxLayout.Y_AXIS));
		ruleListWrapper.setPreferredSize(new Dimension(ruleListScroll.getWidth(), Math.max(50 * rules.size(), ruleListScroll.getHeight())));
		int i = 0; JRule temp;
		ruleListWrapper.removeAll();
		for (Rule e: rules) {
			temp = new JRule(i, e, this);
			ruleListWrapper.add(temp);
			i++;
		}
		SwingUtilities.updateComponentTreeUI(this);
	}
	
	private Dimension getDimentionRatio(double width, double height, double xscale, double yscale) {
		return new Dimension((int)(width * xscale), (int)(height * yscale));
	}
	
	@Override
	public void validate() {
		updateRatios(mainPane, pseudoCScroll, normalCScroll, transButtonWrapper);
		super.validate();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		if (action == null) return;
		if (action.equals(tempUntransActionCommand)) {
			if (JOptionPane.showConfirmDialog(this, "This feature is not implemented.\nDo you want to copy text from below area to above area?", "NotImpemented", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				pseudoCArea.setText(normalCArea.getText());
			}
			return;
		}
		String actions[] = action.split("\\.");
		int subaction = -1;
		try {
			subaction = Integer.parseInt(actions[2]);
		} catch (NumberFormatException err) {
			System.err.println("Unrecognised Subaction Recieved: " + actions[2]);
		}
		int res, i;
		String url;
		switch (actions[1]) {
		case "file":
			switch (subaction) {
			case 0:
				if (JOptionPane.showConfirmDialog(this, "This action will overwrite your transpiler.\nAre you sure?", "New File", JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
					pseudoCArea.setText("");
					normalCArea.setText("");
				}
				break;
			case 1:
				res = fc.showOpenDialog(this);
				if (res == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					if (file.canRead()) {
						try {
							FileInputStream stream = new FileInputStream(file);
							String txt = "";
							while (stream.available() > 0) txt += (char)stream.read();
							String extension = file.getName().split("\\.")[file.getName().split("\\.").length - 1];
							if (extension.equals("c")) normalCArea.setText(txt);
							if (extension.equals("easyc") || extension.equals("txt")) pseudoCArea.setText(txt);
							stream.close();
						} catch (FileNotFoundException e1) {
							JOptionPane.showMessageDialog(this, "The file you selected does not exist.", "File Error", JOptionPane.ERROR_MESSAGE);
							e1.printStackTrace();
						} catch (IOException e1) {
							JOptionPane.showMessageDialog(this, "The file you selected seems to be corrupted\n or not c, easyc or txt file.", "File Error", JOptionPane.ERROR_MESSAGE);
							e1.printStackTrace();
						}
					}
				}
				break;
			case 2:
				url = JOptionPane.showInputDialog(this, "Input the URL.", "Open File from URL", JOptionPane.INFORMATION_MESSAGE);
				if (url == null) return;
				try {
					URL link = new URL(url);
					BufferedReader in = new BufferedReader(new InputStreamReader(link.openStream()));
		             
		            String line, txt = "";
		            while ((line = in.readLine()) != null) {
		            	txt += line;
		            }
		            pseudoCArea.setText(txt);
		            in.close();
				} catch (MalformedURLException e1) {
					JOptionPane.showMessageDialog(this, "The URL you typed won't connect. check for any typos.", "URL Error", JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(this, "The URL you typed won't connect. check for any typos.", "URL Error", JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				}
				break;
			case 3:
				if (latestFile != null) {
					try {
						normalCArea.write(new OutputStreamWriter(new FileOutputStream(latestFile), "utf-8"));
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(this, "The file you selected seems to be corrupted\n or not c, easyc or txt file.", "File Error", JOptionPane.ERROR_MESSAGE);
						e1.printStackTrace();
					}
					break;
				}
			case 4:
				res = fc.showSaveDialog(this);
				if (res == JFileChooser.APPROVE_OPTION) {
					File file = new File(fc.getSelectedFile() + ".c");
					if (!file.getName().trim().isEmpty()) {
						try {
							if (file.exists()) {
							    if (JOptionPane.showConfirmDialog(this, "Do you want to replace the existing file?", "Save File", JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) {
							        break;
							    }
							}
							latestFile = file;
							normalCArea.write(new OutputStreamWriter(new FileOutputStream(file), "utf-8"));
						} catch (IOException e1) {
							JOptionPane.showMessageDialog(this, "The file you selected seems to be corrupted\n or not c, easyc or txt file.", "File Error", JOptionPane.ERROR_MESSAGE);
							e1.printStackTrace();
						}
					}
				}
				break;
			case 5:
				if (JOptionPane.showConfirmDialog(this, "This action will overwrite your rule editor.\nAre you sure?", "New Rule", JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
					ruleNameArea.setText("");
					ruleInArea.setText("");
					ruleOutArea.setText("");
					ruleDocArea.setText("");
				}
				break;
			case 6:
				res = fc.showOpenDialog(this);
				if (res == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					if (file.canRead()) {
						try {
							ObjectInputStream stream = new ObjectInputStream(new FileInputStream(file));
							while (stream.available() > 0) rules.add((Rule)stream.readObject());
							stream.close();
							updateRules();
						} catch (FileNotFoundException e1) {
							JOptionPane.showMessageDialog(this, "The file you selected does not exist.", "File Error", JOptionPane.ERROR_MESSAGE);
							e1.printStackTrace();
						} catch (IOException e1) {
							JOptionPane.showMessageDialog(this, "The file you selected seems to be corrupted\n or not c, easyc or txt file.", "File Error", JOptionPane.ERROR_MESSAGE);
							e1.printStackTrace();
						} catch (ClassNotFoundException e1) {
							e1.printStackTrace();
						}
					}
				}
				break;
			case 7:
				url = JOptionPane.showInputDialog(this, "Input the URL.", "Open File from URL", JOptionPane.INFORMATION_MESSAGE);
				if (url == null) return;
				try {
					URL link = new URL(url);
					ObjectInputStream in = new ObjectInputStream(link.openStream());
		            while (in.available() > 0) {
		            	rules.add((Rule)in.readObject());
		            }
		            in.close();
				} catch (MalformedURLException e1) {
					JOptionPane.showMessageDialog(this, "The URL you typed won't connect. check for any typos.", "URL Error", JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(this, "The URL you typed won't connect. check for any typos.", "URL Error", JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				}
				break;
			case 8:
				if (latestRuleFile != null) {
					try {
						ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(latestRuleFile));
						for (Rule d: rules) {
							out.writeObject(d);
						}
						out.close();
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(this, "The file you selected seems to be corrupted\n or not c, easyc or txt file.", "File Error", JOptionPane.ERROR_MESSAGE);
						e1.printStackTrace();
					}
					break;
				}
			case 9:
				res = fc.showSaveDialog(this);
				if (res == JFileChooser.APPROVE_OPTION) {
					File file = new File(fc.getSelectedFile() + ".rules");
					if (!file.getName().trim().isEmpty()) {
						try {
							if (file.exists()) {
							    if (JOptionPane.showConfirmDialog(this, "Do you want to replace the existing file?", "Save Rules", JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) {
							        break;
							    }
							}
							latestRuleFile = file;
							ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
							for (Rule d: rules) {
								out.writeObject(d);
							}
							out.close();
						} catch (IOException e1) {
							JOptionPane.showMessageDialog(this, "The file you selected seems to be corrupted\n or not c, easyc or txt file.", "File Error", JOptionPane.ERROR_MESSAGE);
							e1.printStackTrace();
						}
					}
				}
				break;
			case 10:
				if (JOptionPane.showConfirmDialog(this, "Are you sure?", "Restart Program", JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
					remove(toolBar);
					remove(mainPane);
					rules.clear();
					initiate();
					revalidate();
				}
				break;
			case 11:
				if (JOptionPane.showConfirmDialog(this, "Are you sure?", "Exit Program", JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
					dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
				}
				break;
			default:
				System.err.println("Unrecognised " + actions[1] + " subaction Recieved: " + action);
				break;
			}
			break;
		case "edit":
			switch (subaction) {
			case 0:
				previous = pseudoCArea.getText();
				try {
					pseudoCArea.setTextUntracking(history.pop());
				}catch (java.util.EmptyStackException emptyStackE) {
					pseudoCArea.setTextUntracking("");
				}
				break;
			case 1:
				//MacroConvertor.p("redo"+previous);
				history.push(pseudoCArea.getText());
				if (!previous.isEmpty()) {
					pseudoCArea.setTextUntracking(previous);
					previous = "";
				}
				break;
			case 2:
			case 3:
			case 4:
				//MacroConvertor.p(pseudoCArea.getText());
				history.push(pseudoCArea.getText());
				break;
			case 5:
				selectedArea.replaceSelection("");
				break;
			case 6:
				selectedArea.selectAll();
				break;
			default:
				System.err.println("Unrecognised " + actions[1] + " subaction Recieved: " + action);
				break;
			}
			break;
		case "transpiler":
			switch (subaction) {
			case 0:
				transpile();
				break;
			case 1:
				untranspile();
				break;
			case 2:
				expand();
				break;
			case 3:
				isSynced = !isSynced;
				break;
			case 4:
				String temp = pseudoCArea.getText();
				pseudoCArea.setText(normalCArea.getText());
				normalCArea.setText(temp);
				break;
			default:
				System.err.println("Unrecognised " + actions[1] + " subaction Recieved: " + action);
				break;
			}
			break;
		case "rules":
			int listnum = 0;
			switch (subaction) {
			case 0:
				if (rules.isEmpty()) break;
				try {
					listnum = Integer.parseInt(actions[3]);
				} catch (NumberFormatException err) {
					System.err.println("Unrecognised Rule number Recieved: " + actions[3]);
				}
				ruleNameArea.setText(rules.get(listnum).getName());
				ruleInArea.setText(rules.get(listnum).getInput());
				ruleOutArea.setText(rules.get(listnum).getOutput());
				ruleDocArea.setText(rules.get(listnum).getHelpDoc());
				ruleUseRegexToggleButton.setSelected(rules.get(listnum).useRegex);
				break;
			case 1:
				if (ruleNameArea.getText().trim().isEmpty()) {
					JOptionPane.showMessageDialog(this, "Name is empty!", "Confirm Rule", JOptionPane.ERROR_MESSAGE);
					break;
				}
				else if (ruleInArea.getText()/*/.trim()/*/.isEmpty()) {
					JOptionPane.showMessageDialog(this, "Input is empty!", "Confirm Rule", JOptionPane.ERROR_MESSAGE);
					break;
				}/*/
				else if (ruleOutArea.getText().trim().isEmpty()) {
					JOptionPane.showMessageDialog(this, "Output is empty!", "Confirm Rule", JOptionPane.ERROR_MESSAGE);
					break;
				}/*/
				boolean useRegex = ruleUseRegexToggleButton.isSelected();
				Rule temp = new Rule(ruleNameArea.getText(), ruleInArea.getText(), ruleOutArea.getText(), useRegex, ruleDocArea.getText());
				i = 0;
				for (Rule d: rules) {
					if (d.sameName(ruleNameArea.getText())) {
						rules.set(i, d);
						selectedRule = i;
						break;
					}
					i++;
				}
				if (i == rules.size()) {
					selectedRule = rules.size();
					rules.add(temp);
				}else {
					int choice = JOptionPane.showConfirmDialog(this, "Are you sure to edit this rule?", "Edit Rule", JOptionPane.OK_CANCEL_OPTION);
					if (choice == JOptionPane.OK_OPTION) {
						rules.set(i, temp);
					}
				}
				updateRules();
				break;
			case 2:
				if (rules.isEmpty()) break;
				selectedRule = 0;
				updateRules();
				break;
			case 3:
				if (rules.isEmpty()) break;
				selectedRule = rules.size() - 1;
				updateRules();
				break;
			case 4:
				if (rules.isEmpty()) break;
				try {
					selectedRule = Integer.parseInt(actions[3]);
				} catch (NumberFormatException err) {
					System.err.println("Unrecognised Rule number Recieved: " + actions[3]);
				} catch (IndexOutOfBoundsException err) {
					if (rules.isEmpty()) break;
					selectedRule += 1;
				}
				if (0 > selectedRule) selectedRule = 0;
				else if (selectedRule >= rules.size()) selectedRule = rules.size() - 1;
				updateRules();
				break;
			case 5:
				if (rules.isEmpty()) break;
				selectedRule -= 1;
				if (0 > selectedRule) selectedRule = 0;
				else if (selectedRule >= rules.size()) selectedRule = rules.size() - 1;
				updateRules();
				break;
			case 6:
				if (rules.isEmpty()) break;
				try {
					listnum = Integer.parseInt(actions[3]);
					rules.get(listnum).setActive(!(rules.get(listnum).getActive()));
				} catch (NumberFormatException err) {
					System.err.println("Unrecognised Rule number Recieved: " + actions[3]);
				} catch (IndexOutOfBoundsException err) {
					rules.get(selectedRule).setActive(!(rules.get(selectedRule).getActive()));
				}
				updateRules();
				break;
			case 7:
				if (rules.isEmpty()) break;
				try {
					rules.remove(Integer.parseInt(actions[3]));
				} catch (NumberFormatException err) {
					System.err.println("Unrecognised Rule number Recieved: " + actions[3]);
				} catch (IndexOutOfBoundsException err) {
					rules.remove(selectedRule);
					selectedRule--;
					if (selectedRule < 0) selectedRule = 0;
				}
				updateRules();
				break;
			default:
				System.err.println("Unrecognised " + actions[1] + " subaction Recieved: " + action);
				break;
			}
			break;
		case "setting":
			switch (subaction) {
			default:
				System.err.println("Unrecognised " + actions[1] + " subaction Recieved: " + action);
				break;
			}
			break;
		case "window":
			switch (subaction) {
			case 0:
				rulesActive = !rulesActive;
				updateShows();
				updateRatios(mainPane, pseudoCScroll, normalCScroll, transButtonWrapper);
				break;
			case 1:
				if (getExtendedState() == JFrame.MAXIMIZED_BOTH) {
					setBounds(getBounds().width / 2 - previousWindow.width / 2, getBounds().height / 2 - previousWindow.height / 2, previousWindow.width, previousWindow.height);
					setSize(previousWindow);
					setExtendedState(JFrame.NORMAL);
				}
				else {
					previousWindow = getSize();
					setExtendedState(JFrame.MAXIMIZED_BOTH); 
				}
				break;
			case 2:
				toolBarActive = !toolBarActive;
				updateShows();
				updateRatios(mainPane, pseudoCScroll, normalCScroll, transButtonWrapper);
				break;
			default:
				System.err.println("Unrecognised " + actions[1] + " subaction Recieved: " + action);
				break;
			}
			break;
		case "help":
			switch (subaction) {
			case 1:
				JOptionPane.showMessageDialog(this, "EasyC Transpiler 1.0\nDeveloped by K9Wan & prodzpod", "About...", JOptionPane.INFORMATION_MESSAGE);
				break;
			default:
				System.err.println("Unrecognised " + actions[1] + " subaction Recieved: " + action);
				break;
			}
			break;
		default:
			System.err.println("Unrecognised Action Recieved: " + action);
			break;
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e) {/*/
		if (e.getKeyCode() == KeyEvent.VK_SHIFT) shift = true;
		else if (e.getKeyCode() == KeyEvent.VK_CONTROL) ctrl = true;
		else if (e.getKeyCode() == KeyEvent.VK_ALT) alt = true;
		String menu[] = {"file", "edit", "transpiler", "rules", "setting", "window", "help"};
		// SHORTCUTS
		int i = 0, j;
		for (Key list[]: shortcut) {
			j = 0;
			for (Key d: list) {
				if (d == null) continue;
				if (d.getAlt() == alt && d.getCtrl() == ctrl && d.getShift() == shift && d.getKey() == e.getKeyCode()) {
					this.actionPerformed(new ActionEvent(this, 1, "action." + menu[i] + "." + String.valueOf(j)));
				}
				j++;
			}
			i++;
		}/*/
		// something related to undo/redo
		if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
			//MacroConvertor.p(pseudoCArea.getText());
			history.push(pseudoCArea.getText());
		}
		if (e.isControlDown()) {
			switch(e.getKeyCode()) {
			case KeyEvent.VK_X:
			case KeyEvent.VK_C:
			case KeyEvent.VK_V:
				//MacroConvertor.p(pseudoCArea.getText());
				history.push(pseudoCArea.getText());
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {/*/
		if (e.getKeyCode() == KeyEvent.VK_SHIFT) shift = false;
		else if (e.getKeyCode() == KeyEvent.VK_CONTROL) ctrl = false;
		else if (e.getKeyCode() == KeyEvent.VK_ALT) alt = false;/*/
		// something related to undo/redo
		if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
			MacroConvertor.p(pseudoCArea.getText());
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		if (isSynced) {
			if (e.getComponent() == pseudoCArea) transpile();
			else if (e.getComponent() == normalCArea) untranspile();
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getComponent() instanceof JTextComponent) {
			selectedArea = (JTextComponent) (e.getComponent());
			selectedText = selectedArea.getSelectedText();
		}
	}
	
	private void transpile() {
		// transpiler here
		normalCArea.setText(Rule.execute(pseudoCArea.getText()));
	}
	
	private void untranspile() {
		// reverse transpiler here
		this.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, tempUntransActionCommand));
	}
	
	private void expand() {
		normalCArea.setText(MacroConvertor.expand(pseudoCArea.getText()));
	}
	
	private class TrackingJTextArea extends JTextArea{
		public TrackingJTextArea(int i, int j) {
			super(i, j);
		}

		@Override
		public void setText(String t) {
			history.push(this.getText());
			super.setText(t);
		}
		
		public void setTextUntracking(String t) {
			super.setText(t);
		}
	}
}
