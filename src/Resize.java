
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

/***
 * inspired by ComponentResizer by Rob Camick and other resizer classes
 * https://tips4java.wordpress.com/2009/09/13/resizing-components/
 */

public class Resize extends MouseAdapter {
	
	private int screenMinimumWidth = 50;
	private int screenMinimumHeight = 50;
	
	private boolean devMode = false;
	public boolean getDevMode() { return devMode; }
	public void setDevMode(boolean devMode) { this.devMode = devMode; }
	
	@SuppressWarnings("unused")
	private Rectangle getScreenMinimumRange() { return new Rectangle(screenMinimumWidth, screenMinimumHeight); }
	@SuppressWarnings("unused")
	private void setScreenMinimum(int width, int height) { screenMinimumWidth = width; screenMinimumHeight = height; }
	@SuppressWarnings("unused")
	private void setScreenMinimum(Rectangle dim) { screenMinimumWidth = dim.x; screenMinimumHeight = dim.y; }
	
	private int direction = 0;
	private Cursor cursorNow = null;
	private boolean clicked = false;
	
	private Component comp = null;
	public Component getComponent() { return comp; }
	public void setComponent(Component comp) { this.comp = comp; }
	
	private Point lastClick;
	public Point getLastClick() { return lastClick; }
	// public void setLastClick() {}
	
	private int border = 5;
	public int getBorder() { return border; }
	public void setBorder(int border) { this.border = border; }
	
	public Resize() {
		System.err.println("Resizer element not initialized with a Component. use setComponent() or you might encounter errors.");
	}
	
	public Resize(Component comp) {
		comp.addMouseListener(this);
		comp.addMouseMotionListener(this);
		setComponent(comp);
		if (devMode) System.out.println("Component Linked: " + comp);
	}
	
	public Resize(Resize copy) {
		screenMinimumWidth = copy.screenMinimumWidth;
		screenMinimumHeight = copy.screenMinimumHeight;
		devMode = copy.devMode;
		direction = copy.direction;
		cursorNow = new Cursor(copy.cursorNow.getType());
		clicked = copy.clicked;
		lastClick = new Point(copy.lastClick);
		border = copy.border;
		if (copy.comp == null) System.err.println("Resizer element not initialized with a Component. use setComponent() or you might encounter errors.");
	}
	
	private void setCursor(int cursor) {
		comp.setCursor(Cursor.getPredefinedCursor(cursor));
		if (devMode) System.out.println("Cursor set: " + Cursor.getPredefinedCursor(cursor));
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		Point point = e.getPoint();
		
		direction = 0;
		if (point.y < border) 						 direction += 1; // north
		if (point.x > comp.getWidth() - 1 - border)  direction += 2; // east 
		if (point.y > comp.getHeight() - 1 - border) direction += 4; // south
		if (point.x < border) 						 direction += 8; // west
		
		switch (direction) {
			case 1:	setCursor(Cursor.N_RESIZE_CURSOR);  break;
			case 2:	setCursor(Cursor.E_RESIZE_CURSOR);  break;
			case 3:	setCursor(Cursor.NE_RESIZE_CURSOR); break;
			case 4:	setCursor(Cursor.S_RESIZE_CURSOR);  break;
			case 6:	setCursor(Cursor.SE_RESIZE_CURSOR); break;
			case 8:	setCursor(Cursor.W_RESIZE_CURSOR);  break;
			case 9:	setCursor(Cursor.NW_RESIZE_CURSOR); break;
			case 12:setCursor(Cursor.SW_RESIZE_CURSOR); break;
			default:  comp.setCursor(cursorNow);		break;
		}
		
		if (devMode) System.out.println("Mouse Moved, State: " + direction + ", xy = " + point);
	}
	
	@Override
	public void mouseEntered (MouseEvent e) {
		if (direction != 0) cursorNow = comp.getCursor();
		if (devMode) System.out.println("Mouse enter, Cursor: " + cursorNow);
	}
	
	@Override
	public void mousePressed (MouseEvent e) {
		if (direction == 0) return; 
		clicked = true;
		lastClick = e.getPoint();
		SwingUtilities.convertPointToScreen(lastClick, comp);
		if (devMode) System.out.println("MouseClick Detected, Location: " + lastClick);
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		clicked = false;
		if (devMode) System.out.println("MouseRelease Detected, Location: " + e.getPoint());
	}
	
	private boolean bitSet(int bit, int n) { return (bit & (int)(Math.pow(2, n))) == (int)(Math.pow(2, n)); }
	
	@Override
	public void mouseDragged (MouseEvent e) {
		Rectangle bounds = comp.getBounds();
		Point currentClick = e.getPoint();
		
		int dist = 0;
		
		if (bitSet(direction, 0)) { // north
			dist = lastClick.y - currentClick.y;
			if (dist + bounds.height < screenMinimumHeight) dist = screenMinimumHeight;
			bounds.y -= dist;
			bounds.height -= dist;
		}
		if (bitSet(direction, 1)) { // east
			dist = currentClick.x - lastClick.x;
			if (dist + bounds.width < screenMinimumWidth) dist = screenMinimumWidth;
			bounds.width -= dist;
		}
		if (bitSet(direction, 2)) { // south
			dist = currentClick.y - lastClick.y;
			if (dist + bounds.height < screenMinimumHeight) dist = screenMinimumHeight;
			bounds.height -= dist;
		}
		if (bitSet(direction, 3)) { // west
			dist = lastClick.x - currentClick.x;
			if (dist + bounds.width < screenMinimumWidth) dist = screenMinimumWidth;
			bounds.x -= dist;
			bounds.width -= dist;
		}
		
		comp.setBounds(bounds);
		comp.setSize(bounds.width, bounds.height);
		if (comp instanceof MainGUI) ((MainGUI)comp).validate();
		else comp.validate();
		
		if (devMode) System.out.println("Resize, Bounds: " + bounds);
	}
	
	@Override
	public Resize clone() {
		Resize copy = new Resize(comp);
		copy.screenMinimumWidth = screenMinimumWidth;
		copy.screenMinimumHeight = screenMinimumHeight;
		copy.devMode = devMode;
		copy.direction = direction;
		copy.cursorNow = new Cursor(cursorNow.getType());
		copy.clicked = clicked;
		copy.lastClick = new Point(lastClick);
		copy.border = border;
		return copy;
	}
}
