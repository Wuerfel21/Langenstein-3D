package net.irq_interactive.langenstein3D.editor;

import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import net.irq_interactive.langenstein3D.GameConstants;
import net.irq_interactive.langenstein3D.game.Loader;
import net.irq_interactive.langenstein3D.launcher.Launchable;

/**
 * Main window of the editor. EDIT WITH WINDOWBUILDER!!!!!!
 * 
 * @author Wuerfel_21
 *
 */
public class EditorMain extends Launchable {

	public JFrame frame;

	/**
	 * Create the application.
	 */
	public EditorMain() {
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	@Override
	public int launch(Map<String, Object> args) throws Exception {
		initialize();
		synchronized (frame) {
			frame.setVisible(true);
			frame.wait();
		}
		return 0;
	}

	private void exit() {
		if (JOptionPane.showConfirmDialog(frame, "Really quit the editor?\nALL UNSAVED DATA WILL BE LOST!", "", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.WARNING_MESSAGE, null) == 0) { // TODO: localization
			synchronized (frame) {
				frame.notify();
			}
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame(GameConstants.GAME + " Editor " + GameConstants.VERSION);
		frame.setIconImages(Loader.getIcons());
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				exit();
			}
		});
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		JDesktopPane desktopPane = new JDesktopPane();
		frame.getContentPane().add(desktopPane, BorderLayout.CENTER);

		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.setAction(exitAction);

		JMenuItem mntmLoad = new JMenuItem("Load...");
		mnFile.add(mntmLoad);

		JMenuItem mntmLoadTestLevel = new JMenuItem("Load Test Level");
		mntmLoadTestLevel.setAction(loadTestAction);
		mnFile.add(mntmLoadTestLevel);
		mnFile.add(mntmExit);

		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);

		JMenuItem mntmAbout = new JMenuItem("About...");
		mntmAbout.setAction(aboutAction);
		mnHelp.add(mntmAbout);
	}

	private final Action exitAction = new AbstractAction("Exit") {

		private static final long serialVersionUID = -8078636636744307355L;

		@Override
		public void actionPerformed(ActionEvent e) {
			exit();
		}
	};

	private final Action aboutAction = new AbstractAction("About...") {

		private static final long serialVersionUID = 5509903434921281697L;

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				JOptionPane.showMessageDialog(frame, "Langenstein 3D editor - Version " + GameConstants.VERSION + "\n© 2016 IRQ Interactive", "About",
						JOptionPane.INFORMATION_MESSAGE, new ImageIcon(ImageIO.read(Loader.getInternalloader().get("/assets/internal/irq_crop.png"))));
			} catch (HeadlessException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	};

	private final Action loadTestAction = new AbstractAction("Load test level") {

		private static final long serialVersionUID = 5509903434921281697L;

		@Override
		public void actionPerformed(ActionEvent e) {
			TestLoadDialog diag = new TestLoadDialog(frame);
			diag.setVisible(true);
			if (diag.isCanceled()) return;
			JOptionPane.showMessageDialog(frame, Integer.toString(diag.getSpinnerVal()));
		}
	};
}
