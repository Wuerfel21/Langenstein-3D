/**
 * 
 */
package net.wuerfel21.langenstein3D.game.render;

import java.awt.BufferCapabilities;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.ImageCapabilities;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import net.wuerfel21.langenstein3D.game.GameConstants;
import sun.java2d.pipe.hw.ExtendedBufferCapabilities;
import sun.java2d.pipe.hw.ExtendedBufferCapabilities.VSyncType;

/**
 * @author Wuerfel_21
 *
 */
public class RenderWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8981782575834557082L;

	public Canvas canvas;
	protected BufferStrategy strategy;
	protected GraphicsEnvironment env;
	protected GraphicsDevice dev;
	protected GraphicsConfiguration conf;
	
	public RenderWindow(Dimension d, boolean fullscreen) {
		super(GameConstants.GAME + " " + GameConstants.VERSION);
		this.setIgnoreRepaint(true);
		if (fullscreen) this.setUndecorated(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		canvas = new Canvas();
		canvas.setIgnoreRepaint(true);
		canvas.setSize(d);
		this.add(canvas);
		this.pack();
		this.setVisible(true);
		env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		dev = env.getDefaultScreenDevice();
		conf = dev.getDefaultConfiguration();
		if (fullscreen) {
			dev.setFullScreenWindow(this);
			if (dev.isDisplayChangeSupported()) {
				try {
					dev.setDisplayMode(new DisplayMode(d.width, d.height, 32, 70));
				} catch(Exception e) {
					System.out.println("Could not get 70Hz mode: "+e);
					dev.setDisplayMode(new DisplayMode(d.width, d.height, 32, DisplayMode.REFRESH_RATE_UNKNOWN));
				}
			}
		}
		try {
			canvas.createBufferStrategy(2,new ExtendedBufferCapabilities(new BufferCapabilities(new ImageCapabilities(false), new ImageCapabilities(true), BufferCapabilities.FlipContents.UNDEFINED), VSyncType.VSYNC_ON));
		} catch (Exception e) {
			System.out.println("Could not get VSYNC mode: "+e);
			canvas.createBufferStrategy(2);
		}
		strategy = canvas.getBufferStrategy();
		System.out.println(strategy.getCapabilities().isPageFlipping());
	}
	
	public void showFrame(BufferedImage frame) {
		Graphics graph = strategy.getDrawGraphics();
		graph.drawImage(frame, 0, 0, null);
		if(!strategy.contentsLost())
			strategy.show();
		graph.dispose();
	}
	
}
