package main;

import java.awt.AWTException;
import java.awt.Button;
import java.awt.Color;
import java.awt.Frame;
import java.awt.MouseInfo;
import java.awt.PopupMenu;
import java.awt.Robot;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.text.NumberFormat;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.text.NumberFormatter;

public class Awake extends Frame implements WindowListener, ActionListener
{
    private static final long serialVersionUID = 1L;

	private static long time = 10000;

	private static boolean appRunning = false;
	private static final boolean MOUSE_MOVE = true;
	private static final boolean KEY_PRESS = false;

	private static final int X_LIMIT = 50;
	private static final int Y_LIMIT = 50;

	private static JLabel timeLabel = new JLabel("Enter time (minutes)");
	private static JFormattedTextField timeValue = new JFormattedTextField();
	private static Button start = new Button("Start");

	private static TrayIcon trayIcon = null;
	private static SystemTray tray = SystemTray.getSystemTray();

	private static Thread awakeThread;
	private static Robot robot;
	private static Random random;

	public static Awake myWindow;

	public static void main(String[] args) throws AWTException, InterruptedException
	{
		myWindow = new Awake("Awake");
		myWindow.setSize(320, 70);
		myWindow.setBackground(Color.WHITE);
		myWindow.setResizable(false);
		myWindow.setVisible(true);
	}

	public Awake(String title) throws AWTException
	{
		super(title);
		setLayout(null);

		NumberFormatter timeFormat = new NumberFormatter(NumberFormat.getIntegerInstance());
		timeFormat.setValueClass(Long.class);
		timeFormat.setAllowsInvalid(true);

		timeValue = new JFormattedTextField(timeFormat);

		timeLabel.setBounds(20, 30, 120, 30);
		timeValue.setBounds(160, 30, 60, 30);
		timeValue.setText("3");
		start.setBounds(240, 30, 60, 30);

		timeLabel.setBackground(Color.WHITE);
		timeValue.setBackground(Color.WHITE);
		start.setBackground(Color.WHITE);

		add(timeLabel);
		add(timeValue);
		add(start);

		robot = new Robot();
		random = new Random();

		addWindowListener(this);

		start.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					time = Long.parseUnsignedLong(timeValue.getText()) * 1000 * 60;
				}

				catch (NumberFormatException exception)
				{
					return;
				}

				if (appRunning)
				{
					appRunning = false;
					start.setLabel("Start");

				}
				else
				{
					appRunning = true;
					awakeThread = new Thread(new PressKey());
					awakeThread.start();
					start.setLabel("Stop");
				}
			}
		});

	}

	private class PressKey implements Runnable
	{

		PressKey()
		{

		}

		public void run()
		{
			try
			{
				while (appRunning)
				{
					if (MOUSE_MOVE)
					{

						int x = (int) (MouseInfo.getPointerInfo().getLocation().getX() + random.nextInt(X_LIMIT + X_LIMIT) - X_LIMIT);
						int y = (int) (MouseInfo.getPointerInfo().getLocation().getY() + random.nextInt(Y_LIMIT + Y_LIMIT) - Y_LIMIT);

						robot.mouseMove(x, y);
					}
					if (KEY_PRESS)
					{
						robot.keyPress(KeyEvent.VK_ENTER);
						Thread.sleep(100);
						robot.keyRelease(KeyEvent.VK_ENTER);
					}
					Thread.sleep(time);
				}
			}
			catch (InterruptedException e)
			{
			}
		}
	}

	private static void miniTray()
	{

		ImageIcon trayImg = new ImageIcon(ClassLoader.getSystemResource("resources/TrayIcon.png"));

		trayIcon = new TrayIcon(trayImg.getImage(), "Y", new PopupMenu());
		trayIcon.setImageAutoSize(true);

		trayIcon.addMouseListener(new MouseAdapter()
		{

			public void mouseClicked(MouseEvent e)
			{

				if (e.getClickCount() == 1)
				{// single click 1 double click 2

					tray.remove(trayIcon);
					myWindow.setVisible(true);
					myWindow.setExtendedState(JFrame.NORMAL);
					myWindow.toFront();
				}

			}

		});

		try
		{

			tray.add(trayIcon);

		}
		catch (AWTException e1)
		{
			e1.printStackTrace();
		}

	}

	public void windowClosing(WindowEvent e)
	{
		dispose();
		System.exit(0);
	}

	public void windowOpened(WindowEvent e)
	{
	}

	public void windowActivated(WindowEvent e)
	{
	}

	public void windowIconified(WindowEvent e)
	{
		myWindow.setVisible(false);
		Awake.miniTray();
	}

	public void windowDeiconified(WindowEvent e)
	{
	}

	public void windowDeactivated(WindowEvent e)
	{
	}

	public void windowClosed(WindowEvent e)
	{
	}

	public void actionPerformed(ActionEvent arg0)
	{

	}
}