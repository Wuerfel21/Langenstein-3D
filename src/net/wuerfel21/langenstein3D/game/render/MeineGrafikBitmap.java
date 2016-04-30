package net.wuerfel21.langenstein3D.game.render;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import java.awt.geom.*;
import java.net.*;

public class MeineGrafikBitmap {
	public JFrame frame;
	public CanvasPane canvas;
	public BufferedImage canvasImage;
	public Color hintergrundfarbe, originalfarbe;
	public Graphics2D grafik;
	// public static MeineGrafikBitmap singleton;
	public JPanel steuerungOst, steuerungSued, steuerungWest, steuerungNord;
	public Dimension size;

	public MeineGrafikBitmap(String titel, Dimension dim) {
		frame = new JFrame();
		canvas = new CanvasPane();
		size = new Dimension(dim);
		canvas.setPreferredSize(size);
		frame.getContentPane().add(canvas, BorderLayout.CENTER);
		frame.setTitle(titel);
		steuerungOst = new JPanel();
		steuerungSued = new JPanel();
		steuerungWest = new JPanel();
		steuerungNord = new JPanel();
		steuerungOst.setLayout(new BoxLayout(steuerungOst, BoxLayout.Y_AXIS));
		steuerungSued.setLayout(new BoxLayout(steuerungSued, BoxLayout.X_AXIS));
		steuerungWest.setLayout(new BoxLayout(steuerungWest, BoxLayout.Y_AXIS));
		steuerungNord.setLayout(new BoxLayout(steuerungNord, BoxLayout.X_AXIS));
		frame.getContentPane().add(steuerungOst, BorderLayout.EAST);
		frame.getContentPane().add(steuerungSued, BorderLayout.SOUTH);
		frame.getContentPane().add(steuerungWest, BorderLayout.WEST);
		frame.getContentPane().add(steuerungNord, BorderLayout.NORTH);
		/*GraphicsDevice d = GraphicsEnvironment
				.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		if (d.isFullScreenSupported())
			frame.setUndecorated(true);
			d.setFullScreenWindow(frame);*/
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		zeige();
	}

	public void zeige() {
		if (grafik == null) {
			Dimension size = canvas.getSize();
			canvasImage = (BufferedImage) canvas.createImage(size.width, size.height);
			grafik = (Graphics2D) canvasImage.getGraphics();
			originalfarbe = grafik.getColor();
			hintergrundfarbe = Color.white;
			grafik.setColor(hintergrundfarbe);
			grafik.fillRect(0, 0, size.width, size.height);
			grafik.setColor(Color.black);
		}
		frame.setVisible(true);
	}

	public class CanvasPane extends JLabel {
		private static final long serialVersionUID = -4652697174056250142L;

		public void paint(Graphics g) {
			g.drawImage(canvasImage, 0, 0, null);
		}
	}

	public void komponenteHinzufuegen(JComponent element, String position) {
		if (position == "rechts")
			steuerungOst.add(element);
		else if (position == "unten")
			steuerungSued.add(element);
		else if (position == "links")
			steuerungWest.add(element);
		else if (position == "oben")
			steuerungNord.add(element);
		frame.pack();
	}

	public void zeichneStrecke(int x1, int y1, int x2, int y2) {
		grafik.drawLine(x1, y1, x2, y2);
	}

	public void loescheStrecke(int x1, int y1, int x2, int y2) {
		grafik.setColor(hintergrundfarbe);
		grafik.drawLine(x1, y1, x2, y2);
		grafik.setColor(originalfarbe);
	}

	public void zeichneRechteck(int px, int py, int breite, int hoehe) {
		grafik.drawRect(px, py, breite, hoehe);
	}

	public void fuelleRechteck(int px, int py, int b, int h, int fuell) {
		grafik.setColor(intColor(fuell));
		grafik.fillRect(px, py, b, h);
		grafik.setColor(originalfarbe);
	}

	private Color intColor(int farbe) {
		Color f;
		switch (farbe) {
		case 1: {
			f = Color.blue;
			break;
		}
		case 2: {
			f = Color.cyan;
			break;
		}
		case 3: {
			f = Color.green;
			break;
		}
		case 4: {
			f = Color.yellow;
			break;
		}
		case 5: {
			f = Color.orange;
			break;
		}
		case 6: {
			f = Color.red;
			break;
		}
		case 7: {
			f = Color.pink;
			break;
		}
		case 8: {
			f = Color.magenta;
			break;
		}
		case 9: {
			f = Color.gray;
			break;
		}
		case 10: {
			f = Color.white;
			break;
		}
		default: {
			f = Color.black;
		}
		}
		return f;
	}

	public void zeichneKreis(int mx, int my, int radius) {
		grafik.drawOval(mx - radius, my - radius, 2 * radius, 2 * radius);
	}

	public void fuelleKreis(int mx, int my, int radius, int fuell) {
		grafik.setColor(intColor(fuell));
		grafik.fillOval(mx - radius, my - radius, 2 * radius, 2 * radius);
		grafik.setColor(originalfarbe);
	}

	public void loescheKreis(int mx, int my, int radius) {
		loesche(new Ellipse2D.Double(mx - radius, my - radius, 2 * radius, 2 * radius));
	}

	public void fuelleEllipse(int mx, int my, int gHachse, int kHachse, int fuell) {
		grafik.setColor(intColor(fuell));
		grafik.fillOval(mx - gHachse, my - kHachse, 2 * gHachse, 2 * kHachse);
		grafik.setColor(originalfarbe);
	}

	public void loescheEllipse(int mx, int my, int gHachse, int kHachse) {
		loesche(new Ellipse2D.Double(mx - gHachse, my - kHachse, 2 * gHachse, 2 * kHachse));
	}

	public void zeichneDreieck(int x1, int y1, int x2, int y2, int x3, int y3) {
		grafik.drawPolygon(erzeugeDreieck(x1, y1, x2, y2, x3, y3));
		this.repaint();
	}

	public void fuelleDreieck(int x1, int y1, int x2, int y2, int x3, int y3, int fuell) {
		grafik.setColor(intColor(fuell));
		grafik.fillPolygon(erzeugeDreieck(x1, y1, x2, y2, x3, y3));
		grafik.setColor(originalfarbe);
	}

	public void loescheDreieck(int x1, int y1, int x2, int y2, int x3, int y3) {
		grafik.setColor(hintergrundfarbe);
		grafik.fillPolygon(erzeugeDreieck(x1, y1, x2, y2, x3, y3));
		grafik.setColor(originalfarbe);
	}

	public Polygon erzeugeDreieck(int x1, int y1, int x2, int y2, int x3, int y3) {
		Polygon p = new Polygon();
		p.addPoint(x1, y1);
		p.addPoint(x2, y2);
		p.addPoint(x3, y3);
		return p;
	}

	public void zeichneText(String text, int x, int y) {
		grafik.drawString(text, x, y);
	}

	public void erzeugeFont(String art, int stil, int size) {
		grafik.setFont(new Font(art, stil, size));
	}

	public boolean zeichneBild(String url, int x, int y) {
		boolean result = false;
		try {
			URL u = new URL(url);
			ImageIcon bild = new ImageIcon(u);
			Image im = bild.getImage();
			result = grafik.drawImage(im, x, y, null);
		} catch (MalformedURLException ue) {
			System.err.println("falsche URL");
		}
		return result;
	}

	public boolean zeichneBild(Image im, int x, int y) {
		return grafik.drawImage(im, x, y, null);
	}

	public void setzeFarbe(int fneu) {
		grafik.setColor(intColor(fneu));
	}

	public void setzeHintergrund(int fneu) {
		grafik.setColor(intColor(fneu));
		hintergrundfarbe = intColor(fneu);
		loescheAlles();
	}

	public void loescheAlles() {
		grafik.setColor(hintergrundfarbe);
		grafik.fillRect(0, 0, 800, 600);
		grafik.setColor(originalfarbe);
	}

	public void warte(int zeit) {// Zeit in Millisekunden
		try {
			Thread.sleep(zeit);
		} catch (InterruptedException e) {
		}
	}

	public void loesche(Shape shape) {
		grafik.setColor(hintergrundfarbe);
		grafik.fill(shape);
		grafik.setColor(originalfarbe);
	}

	public void repaint() {
		canvas.repaint();
	}
}
