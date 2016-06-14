/**
 * 
 */
package net.irq_interactive.langenstein3D.editor;

import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import net.irq_interactive.langenstein3D.game.GameConstants;

/**
 * @author Wuerfel_21
 *
 */
public class EditorMainOld {
	
	protected JFrame mainFrame;
	protected JDesktopPane desk;
	
	public EditorMainOld(String[] args) {
		mainFrame = new JFrame(GameConstants.GAME + " Editor " + GameConstants.VERSION);
		mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		mainFrame.setLayout(new BorderLayout());
		desk = new JDesktopPane();
		mainFrame.add(desk,BorderLayout.CENTER);
		
		mainFrame.addWindowListener(new EditorCloseListener());
		mainFrame.pack();
		mainFrame.setVisible(true);
	}
	
	protected class EditorCloseListener implements WindowListener {

		@Override
		public void windowActivated(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowClosed(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowClosing(WindowEvent e) {
			if(JOptionPane.showConfirmDialog(mainFrame, "Really close editor?\nALL UNSAVED DATA WILL BE LOST!", "", JOptionPane.OK_CANCEL_OPTION,JOptionPane.WARNING_MESSAGE,null) == 0) { //TODO: localization
				System.exit(0);
			}
			
		}

		@Override
		public void windowDeactivated(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowDeiconified(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowIconified(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowOpened(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}

}
