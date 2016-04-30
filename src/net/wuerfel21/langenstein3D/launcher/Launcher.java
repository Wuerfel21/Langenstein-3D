/**
 * 
 */
package net.wuerfel21.langenstein3D.launcher;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.NumberFormat;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import net.wuerfel21.langenstein3D.game.GameConstants;
import net.wuerfel21.langenstein3D.launcher.Launcher.StartButtonListener.NumberLock;
import sun.reflect.Reflection;

/**
 * @author Wuerfel_21
 *
 */
public class Launcher {

	protected static JFrame frame;

	public static class StartButtonListener implements ActionListener {

		public static class NumberLock {
			private int num = -1;

			public synchronized int getNum() {
				return num;
			}

			public synchronized void setNum(int num) {
				this.num = num;
			}

			public NumberLock() {
				// TODO Auto-generated constructor stub
			}

		}

		protected int num;
		protected NumberLock lock;

		public StartButtonListener(NumberLock lock, int num) {
			this.lock = lock;
			this.num = num;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			synchronized (lock) {
				lock.setNum(num);
				lock.notify();
			}
		}

	}

	/**
	 * @param args
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) {
		System.setProperty("sun.java2d.opengl", "True"); // This allows using VSYNC and page flipping. TODO: Make configurable
		try {
			Class gameClass = ClassLoader.getSystemClassLoader().loadClass("net.wuerfel21.langenstein3D.game.Starter");
			Method mainMethod = gameClass.getDeclaredMethod("main", new Class[] { String[].class });
			if (args.length != 0)
				mainMethod.invoke(null,(Object) args);
			else
				mainMethod.invoke(null, launcherGui());
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

	}

	protected static Object launcherGui() {
		frame = new JFrame(GameConstants.GAME + " Launcher " + GameConstants.VERSION);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new FlowLayout());
		NumberLock lock = new NumberLock();

		JButton fullscreenButton = new JButton("Fullscreen");
		fullscreenButton.addActionListener(new StartButtonListener(lock, 0));
		frame.add(fullscreenButton);
		JButton windowedButton = new JButton("Windowed");
		windowedButton.addActionListener(new StartButtonListener(lock, 1));
		frame.add(windowedButton);
		JButton customButton = new JButton("Custom");
		customButton.addActionListener(new StartButtonListener(lock, 2));
		frame.add(customButton);

		frame.pack();
		frame.setVisible(true);
		try {
			synchronized (lock) {
				lock.wait();
			}
		} catch (InterruptedException e1) {
		}

		frame.dispose();
		switch (lock.getNum()) {
		default:
		case 0:
			return new String[0];
		case 1:
			return new String[] { "window" };
		case 2:
			JFrame customDiag = new JFrame("Custom Resolution");
			customDiag.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			customDiag.setLayout(new BoxLayout(customDiag.getContentPane(), BoxLayout.PAGE_AXIS));
			NumberFormat nf = NumberFormat.getIntegerInstance();
			nf.setGroupingUsed(false);
			JFormattedTextField customWidth = new JFormattedTextField(nf);
			JFormattedTextField customHeight = new JFormattedTextField(nf);
			customDiag.add(customWidth);
			customDiag.add(customHeight);
			JCheckBox customFull = new JCheckBox("Fullscreen", true);
			customDiag.add(customFull);
			NumberLock customLock = new NumberLock();
			JButton customStart = new JButton("Launch");
			customStart.addActionListener(new StartButtonListener(customLock, 0));
			customDiag.add(customStart);
			customDiag.pack();
			customDiag.setVisible(true);
			try {
				synchronized (customLock) {
					customLock.wait();
				}
			} catch (InterruptedException e1) {
			}

			customDiag.dispose();
			if (customFull.isSelected())
				return new String[] { customWidth.getValue().toString(), customHeight.getValue().toString() };
			else
				return new String[] { customWidth.getValue().toString(), customHeight.getValue().toString(), "window" };
		}
	}

}
