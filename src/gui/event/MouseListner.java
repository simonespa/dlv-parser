package gui.event;

import gui.Editor;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;

public class MouseListner extends MouseAdapter {

	@Override
	public void mouseEntered(MouseEvent e) {
		super.mouseEntered(e);
		JMenuItem menu = (JMenuItem) e.getComponent();
		maximizeIcon(menu);
		menu.setArmed(true);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		super.mouseExited(e);
		JMenuItem menu = (JMenuItem) e.getComponent();
		minimizeIcon(menu);
		menu.setArmed(false);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		super.mousePressed(e);
		JMenuItem menu = (JMenuItem) e.getComponent();
		menu.setSelected(true);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		super.mouseReleased(e);
		JMenuItem menu = (JMenuItem) e.getComponent();
		menu.setSelected(false);
	}
	
	private void maximizeIcon(JMenuItem menu){
		String icon = menu.getIcon().toString();
		int begin = icon.indexOf("file");
		int end = icon.indexOf("24.png");
		icon = icon.substring(begin, end);
		String[] path = icon.split("/");
		menu.setIcon(new ImageIcon(Editor.class.getResource("ico/"+path[path.length-1]+"32.png")));
	}
	
	private void minimizeIcon(JMenuItem menu){
		String icon = menu.getIcon().toString();
		int begin = icon.indexOf("file");
		int end = icon.indexOf("32.png");
		icon = icon.substring(begin, end);
		String[] path = icon.split("/");
		menu.setIcon(new ImageIcon(Editor.class.getResource("ico/"+path[path.length-1]+"24.png")));
	}

}
