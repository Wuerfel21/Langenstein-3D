/**
 * 
 */
package net.irq_interactive.langenstein3D.launcher;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.plaf.metal.MetalLookAndFeel;

import net.irq_interactive.langenstein3D.GameConstants;
import net.irq_interactive.langenstein3D.Puke;
import net.irq_interactive.langenstein3D.game.Loader;
import net.irq_interactive.langenstein3D.launcher.Launcher.StartButtonListener.LaunchThingymagic;
import net.irq_interactive.langenstein3D.launcher.Launcher.StartButtonListener.NumberLock;

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

		public static class LaunchThingymagic<I, J> {
			final I i;
			final J j;

			public LaunchThingymagic(I i, J j) {
				this.i = i;
				this.j = j;
			}

			public I get1() {
				return i;
			}

			public J get2() {
				return j;
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
	public static void main(String[] args) throws Exception {
		System.setProperty("sun.java2d.opengl", "True"); // This allows using VSYNC and page flipping. TODO: Make configurable
		MetalLookAndFeel.setCurrentTheme(new Puke());
		JDialog.setDefaultLookAndFeelDecorated(true);
        JFrame.setDefaultLookAndFeelDecorated(true);
		try {
			Launchable l;
			Map m;
			if (args.length != 0){
				//TODO: FIX!
				l = getGame();
				m = new HashMap<>();
			}else{
				LaunchThingymagic<Launchable,Map<String, Object>> mag = launcherGui();
				l = mag.get1();
				m = mag.get2();
			}
			System.exit(l.launch(m));
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

	}

	protected static LaunchThingymagic<Launchable, Map<String, Object>> launcherGui() throws Exception {
		frame = new JFrame(GameConstants.GAME + " Launcher " + GameConstants.VERSION);
		frame.setIconImages(Loader.getIcons());
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
		JButton editorButton = new JButton("Editor");
		editorButton.addActionListener(new StartButtonListener(lock, 3));
		frame.add(editorButton);
		JLabel copyright = new JLabel("© 2016 IRQ Interactive");
		frame.add(copyright);

		frame.setResizable(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		try {
			synchronized (lock) {
				lock.wait();
			}
		} catch (InterruptedException e1) {
		}

		frame.dispose();
		Map<String, Object> map = new HashMap<>();
		switch (lock.getNum()) {
		default:
		case 0:
			return new LaunchThingymagic<Launchable, Map<String, Object>>(getGame(), map);
		case 1:
			map.put("render.fullscreen", false);
			return new LaunchThingymagic<Launchable, Map<String, Object>>(getGame(), map);
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
			customDiag.setResizable(false);
			customDiag.pack();
			customDiag.setLocationRelativeTo(null);
			customDiag.setVisible(true);
			try {
				synchronized (customLock) {
					customLock.wait();
				}
			} catch (InterruptedException e1) {
			}

			customDiag.dispose();
			map.put("render.fullscreen", customFull.isSelected());
			map.put("render.width", ((Long) customWidth.getValue()).intValue());
			map.put("render.height", ((Long) customHeight.getValue()).intValue());
			return new LaunchThingymagic<Launchable, Map<String, Object>>(getGame(), map);
		case 3:
			return new LaunchThingymagic<Launchable, Map<String, Object>>(getEditor(), map);// new String[] { "editor" }; TODO: fix
		}
	}

	@SuppressWarnings("unchecked")
	protected static Launchable getGame() throws Exception {
		Class<Launchable> gameClass = (Class<Launchable>) ClassLoader.getSystemClassLoader().loadClass("net.irq_interactive.langenstein3D.game.Starter");
		return gameClass.newInstance();
	}

	@SuppressWarnings("unchecked")
	protected static Launchable getEditor() throws Exception {
		Class<Launchable> gameClass = (Class<Launchable>) ClassLoader.getSystemClassLoader().loadClass("net.irq_interactive.langenstein3D.editor.EditorMain");
		return gameClass.newInstance();
	}

}
