package us.idinfor.opencv.gui;

import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.objdetect.CascadeClassifier;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class FaceDetect extends JFrame {

	private JPanel contentPane;
	private final JButton btnPlay = new JButton("Play");
	private final JButton btnPause = new JButton("Pause");

	private DaemonThread myThread = null;
	int count = 0;
	VideoCapture webSource = null;
	Mat frame = new Mat();
	MatOfByte mem = new MatOfByte();
	CascadeClassifier faceDetector = new CascadeClassifier(getClass()
			.getResource("haarcascade_frontalface_alt.xml").getPath().substring(1));
	MatOfRect faceDetections = new MatOfRect();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FaceDetect frame = new FaceDetect();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public FaceDetect() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 785, 554);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JPanel panel = new JPanel();
		panel.setBounds(10, 11, 749, 445);
		panel.setLayout(null);
		contentPane.add(panel);
		//Play button event
		btnPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				webSource = new VideoCapture(0); // video capture from default
													// cam
				myThread = new DaemonThread(); // create object of threat class
				Thread t = new Thread(myThread);
				t.setDaemon(true);
				myThread.runnable = true;
				t.start(); // start thrad
				btnPlay.setEnabled(false); // deactivate start button
				btnPause.setEnabled(true); // activate stop button
			}
		});
		btnPlay.setBounds(179, 467, 120, 31);
		contentPane.add(btnPlay);
		//Pause button event
		btnPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				myThread.runnable = false; // stop thread
				btnPause.setEnabled(false); // activate start button
				btnPlay.setEnabled(true); // deactivate stop button

				webSource.release(); // stop caturing fron cam
			}
		});
		btnPause.setBounds(435, 467, 120, 31);
		contentPane.add(btnPause);
	}

	class DaemonThread implements Runnable {
		protected volatile boolean runnable = false;

		public void run() {
			synchronized (this) {
				while (runnable) {
					if (webSource.grab()) {
						try {
							webSource.retrieve(frame);
							Graphics g = contentPane.getGraphics();
							faceDetector
									.detectMultiScale(frame, faceDetections);
							for (Rect rect : faceDetections.toArray()) {
								// System.out.println("ttt");
								Core.rectangle(frame,
										new Point(rect.x, rect.y), new Point(
												rect.x + rect.width, rect.y
														+ rect.height),
										new Scalar(0, 255, 0));
							}
							Highgui.imencode(".bmp", frame, mem);
							Image im = ImageIO.read(new ByteArrayInputStream(
									mem.toArray()));
							BufferedImage buff = (BufferedImage) im;
							if (g.drawImage(buff, 0, 0, getWidth(),
									getHeight() - 150, 0, 0, buff.getWidth(),
									buff.getHeight(), null)) {
								if (runnable == false) {
									System.out.println("Paused ..... ");
									this.wait();
								}
							}
						} catch (Exception ex) {
							System.out.println("Error");
						}
					}
				}
			}
		}
	}

}
