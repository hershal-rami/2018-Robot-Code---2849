package org.usfirst.frc.team2849.path;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Event;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.usfirst.frc.team2849.robot.UrsaRobot;

public class PathMaker {
	// take points accumulate dist between for path length
	// angle from arctan
	// to get left and right, add left and right perpendicular to heading
	// be able to insert points at specific locations
	
	//load a path to display

	public static void main(String[] argsokcharlie) {
		PathMaker.init();
	}
	static JFrame frame;
	static String[] presets = new String[] { "auto1", "auto2", "auto3", "auto4" };//later some function to look at names of files
	static BufferedImage field;//picture of field
	static BufferedImage overfield;//picture of obstacles
	static BufferedImage overlay = new BufferedImage(400, 800, BufferedImage.TYPE_4BYTE_ABGR);//image drawn on to make path
	
	static JPanel pointpanel;
	static ArrayList<PointonPath> path = new ArrayList<PointonPath>();

	static int slow = 0;
	
	static int[] prev = new int[] { 0, 0 };// for mouse motion ignore
	static boolean once = true;// for mouse motion ignore
	static boolean once2 = true;// for mouse motion ignore
	static int prevscroll = 0;
	static int pointpaneltranslate = 0;
	
	public static void output() {
		double totalDist = 0;
		ArrayList<PointonPath> output = new ArrayList<PointonPath>();
		output.add(
				new PointonPath(totalDist,
						negmod(Math.atan2(path.get(1).yft - path.get(0).yft,
								path.get(1).xft - path.get(0).xft), Math.PI * 2) * (180 / Math.PI),
						path.get(0).xft, path.get(0).yft));
		for (int i = 1; i < path.size(); i++) {
			totalDist += Math.sqrt(Math.pow(path.get(i).xft - path.get(i - 1).xft, 2)
					+ Math.pow(path.get(i).yft - path.get(i - 1).yft, 2));
			output.add(
					new PointonPath(totalDist,
							negmod(Math.atan2(path.get(i).yft - path.get(i - 1).yft,
									path.get(i).xft - path.get(i - 1).xft), Math.PI * 2) * (180 / Math.PI),
							path.get(i).xft, path.get(i).yft));
		}
		Path mapped = new Path("output", output);
		new PathWriter(new Path[] {mapped, mapped}, "outpath.txt");
		mapped.mapVelocity();
		new PathWriter(new Path[] {mapped, mapped}, "outmapped.txt");
		new PathWriter(mapped.separate(1.067), "outsepped.txt");
		System.out.println("outputed");
	}
	public static void input() {
		ArrayList<PointonPath> pathl = new PathReader("outsepped.txt",false).getLeftPath().getPoints();
		ArrayList<PointonPath> pathr = new PathReader("outsepped.txt",false).getLeftPath().getPoints();
		ArrayList<PointonPath> copy = new ArrayList<PointonPath>();
		for(int i=0;i<pathl.size();i++) {
			copy.add(new PointonPath((pathl.get(i).x+pathr.get(i).x)/2,(pathl.get(i).y+pathr.get(i).y)/2,copy.size()));
		}
		path=copy;
		PathMaker.overlay=new BufferedImage(400, 800, BufferedImage.TYPE_4BYTE_ABGR);
		pointpaneltranslate = 0;
		frame.repaint();
		frame.dispatchEvent(new MouseWheelEvent (frame,0,0,0,0,0,0,false, 3,1,0));
	}
	//starting point set, set te startin point by text eild with distance from left wall
	//clear button
	//also get around to loading specific ones
	static void init() {
		frame = new JFrame() ;
		frame.setLayout(null);
		frame.setSize(1000, 850);
		frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
		frame.addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {
//				System.out.println(e.getModifiers());
//				System.out.println(e.getClickCount());
//				System.out.println(e.getScrollType());
//				System.out.println(e.getScrollAmount());
//				System.out.println(e.getWheelRotation());
				double sig = e.getWheelRotation();
				pointpaneltranslate -= sig * 1.2 * Math.PI / 4 * PointonPath.h;
				if (pointpaneltranslate > 0) {
					pointpaneltranslate = 0;
				}
				if (-pointpaneltranslate > PointonPath.h * (path.size() - 800 / PointonPath.h + 1)) {
					if (path.size() > 800 / PointonPath.h + 1)
						pointpaneltranslate = -PointonPath.h * (path.size() - 800 / PointonPath.h + 1);
					else
						pointpaneltranslate = 0;
				}
				frame.repaint();
			}
		});
		
		importimages();
		
		JPanel fieldPanel = new JPanel() {
			public void paint(Graphics g) {
				g.drawImage(field, 0, 0, 400, 800, null);
				g.drawImage(overlay, 0, 0, 400, 800, null);
				g.drawImage(overfield, 0, 0, 400, 800, null);
			}
		};
		frame.add(fieldPanel);
		fieldPanel.setSize(400, 850);
		fieldPanel.setLocation(200, 0);
		fieldPanel.addMouseMotionListener(new MouseMotionListener() {
			public void mouseDragged(MouseEvent e) {
				if (once) {
					prev = new int[] { e.getX(), e.getY() };
					once = false;
				}
				if (slow % 2 == 0) {
					path.add(new PointonPath(e.getX(), e.getY(), path.size()));
					frame.repaint();
				}
				slow++;
				slow %= 2;
			}
			public void mouseMoved(MouseEvent e) {}
		});
		fieldPanel.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				path.add(new PointonPath(e.getX(), e.getY(), path.size()));
				frame.repaint();
			}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {
				once = true;
				frame.repaint();
			}
		});
		
		JPanel Scrollpanel = new JPanel() {
			public void paint(Graphics g) {
				if (path.size() > 0 & PointonPath.h * (path.size() - 800 / PointonPath.h + 1) != 0) {
					int h = 800 / path.size() * (800 / PointonPath.h);
					g.fillRect(0, -pointpaneltranslate * 800 / (PointonPath.h * path.size()), 50, h);
				}
			}
		};
		frame.add(Scrollpanel);
		Scrollpanel.setSize(50, 800);
		Scrollpanel.setLocation(950, 50);
		Scrollpanel.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {
				once2 = true;
			}
		});
		Scrollpanel.addMouseMotionListener(new MouseMotionListener() {
			public void mouseDragged(MouseEvent e) {
				if (once2) {
					prevscroll = e.getY();
					once2 = false;
				}
				pointpaneltranslate += -e.getY() + prevscroll;
				prevscroll = e.getY();
				if (pointpaneltranslate > 0) {
					pointpaneltranslate = 0;
				}
				if (-pointpaneltranslate > PointonPath.h * (path.size() - 800 / PointonPath.h + 1)) {
					if (path.size() > 800 / PointonPath.h + 1)
						pointpaneltranslate = -PointonPath.h * (path.size() - 800 / PointonPath.h + 1);
					else
						pointpaneltranslate = 0;
				}
				frame.repaint();
			}
			public void mouseMoved(MouseEvent e) {}
		});
		
		pointpanel = new JPanel() {
			public void paint(Graphics g) {
				g.translate(0, pointpaneltranslate);
				g.setColor(Color.white);
				g.fillRect(0, 0, 325, PointonPath.h * path.size());
				for (int i = 0; i < path.size(); i++) {
					path.get(i).paint(g, i);
				}
			}
		};
		frame.add(pointpanel);
		pointpanel.setLocation(625, 50);
		pointpanel.setSize(325, 800);
		
		
		JPanel menupanel = new JPanel() {
			public void paint(Graphics g) {
				g.fillRect(0, 0, 325, 50);
				
				g.setColor(Color.white);
				g.fill3DRect(10, 10, 50, 30, true);
				g.setColor(Color.black);
				g.drawString("Output",15,30);
				
				g.setColor(Color.white);
				g.fill3DRect(70, 10, 50, 30, true);
				g.setColor(Color.black);
				g.drawString("Read",75,30);
				
				g.setColor(Color.white);
				g.fill3DRect(130, 10, 50, 30, true);
				g.setColor(Color.black);
				g.drawString("Clear",135,30);
			}
		};
		frame.add(menupanel);
		menupanel.setSize(325, 50);
		menupanel.setLocation(625, 0);
		menupanel.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				if (e.getX() > 10 & e.getX() < 10+50 & e.getY() > 10 & e.getY() < 40) {
					output();
				}
				if (e.getX() > 70 & e.getX() < 70+50 & e.getY() > 10 & e.getY() < 40) {
					input();
				}
				if (e.getX() > 130 & e.getX() < 130+50 & e.getY() > 10 & e.getY() < 40) {
					clearpath();
				}
			}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
		});
		
		
		JPanel presetpanel = new JPanel();
		presetpanel.setLayout(null);
		frame.add(presetpanel);
		presetpanel.setSize(200, 500);
		presetpanel.setLocation(0, 0);
		
		JTextField distfromwall = new JTextField();
		presetpanel.add(distfromwall);
		distfromwall.setBounds(20,130,160,25);
		distfromwall.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					int dist = Integer.parseInt(distfromwall.getText());
					path.set(0,new PointonPath((leftornot?dist:27-dist)/PointonPath.xconv,UrsaRobot.Robotdepth/2/PointonPath.yconv,0));
					PathMaker.overlay = new BufferedImage(400, 800, BufferedImage.TYPE_4BYTE_ABGR);
					PathMaker.frame.repaint();
				}catch(Exception E) {}
			}
		});
		
		JLabel distfromwallstring = new JLabel("Distance from left wall:");
		presetpanel.add(distfromwallstring);
		distfromwallstring.setBounds(20,100,160,35);
		JButton swapbutton = new JButton("Swap side");
		swapbutton.setBounds(50,165,100,15);
		presetpanel.add(swapbutton);
		swapbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				leftornot=!leftornot;
				distfromwallstring.setText("Distance from "+(leftornot?"left":"right")+" wall:");
			}
		});
		
		JRadioButton leftorright = new JRadioButton();
		presetpanel.add(leftorright);
		
		JComboBox preset = new JComboBox(presets);
		presetpanel.add(preset);
		preset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		path.add(new PointonPath(27d/2/PointonPath.xconv,UrsaRobot.Robotdepth/2/PointonPath.yconv,0));
		
		frame.setVisible(true);
	}
	static boolean leftornot = true;
	public static void clearpath() {
		for(int i=1;i<path.size();) {
			PathMaker.frame.repaint();
			PathMaker.path.remove(i);
			PathMaker.overlay = new BufferedImage(400, 800, BufferedImage.TYPE_4BYTE_ABGR);
			if (-PathMaker.pointpaneltranslate > PointonPath.h
					* (PathMaker.path.size() - 800 / PointonPath.h + 1)) {
				if (PathMaker.path.size() > 800 / PointonPath.h + 1)
					PathMaker.pointpaneltranslate = -PointonPath.h
							* (PathMaker.path.size() - 800 / PointonPath.h + 1);
				else
					PathMaker.pointpaneltranslate = 0;
			}
		}
		frame.repaint();
	}
	private static double negmod(double atan2, double modvalue) {
		while (atan2 < 0) {
			atan2 += modvalue;
		}
		return atan2 % modvalue;
	}
	private static void importimages() {
		try {
			field = ImageIO.read(new File(System.getProperty("user.dir") + "/field2.png"));
			overfield = ImageIO.read(new File(System.getProperty("user.dir") + "/Transparentoverfield.png"));
		} catch (Exception E) {
			E.printStackTrace();
		}
	}
}