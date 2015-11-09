/*
 * This is the main application window.
 */

package edu.cg;

import java.util.Vector;
import javax.imageio.ImageIO;

import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.UIManager;
import javax.swing.JTextArea;
import javax.swing.BoxLayout;
import javax.swing.JTextField;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JFileChooser;
import javax.swing.border.EmptyBorder;
import javax.swing.JFormattedTextField;
import javax.swing.border.EtchedBorder;

import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.awt.event.ActionListener;

@SuppressWarnings("serial")
public class MainWindow extends JFrame {

	private BufferedImage img;
	private StringBuffer imgTitle;
	private Vector<JButton> operationButtons = new Vector<JButton>();

	private JPanel contentPane;
	private JTextField txtFilename;
	private JFormattedTextField txtRetargetHeight;
	private JFormattedTextField txtRetargetWidth;
	private JTextArea txtStatus;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		JFrame wnd = new MainWindow();
		wnd.setVisible(true);
	}

	/**
	 * Create the frame.
	 */
	public MainWindow() {
		// Fix the program ugly look.
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}

		setTitle("ex1: Image Processing Application");
		// The following line makes sure that all application threads are
		// terminated when this window is closed.
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		// Upper panel
		JPanel panelFileSelection = new JPanel();
		panelFileSelection.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		contentPane.add(panelFileSelection, BorderLayout.NORTH);
		panelFileSelection.setLayout(new BoxLayout(panelFileSelection, BoxLayout.X_AXIS));

		txtFilename = new JTextField();
		txtFilename.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				open(txtFilename.getText());
			}
		});
		panelFileSelection.add(txtFilename);
		txtFilename.setColumns(40);

		JButton btnBrowse = new JButton("Browse...");
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser = new JFileChooser();
				int ret = fileChooser.showOpenDialog(MainWindow.this);
				if (ret == JFileChooser.APPROVE_OPTION) {
					String filename = fileChooser.getSelectedFile().getPath();
					txtFilename.setText(filename);
					open(filename);
				}
			}
		});
		panelFileSelection.add(btnBrowse);

		JButton btnReload = new JButton("Reset to original");
		btnReload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				open(txtFilename.getText());
			}
		});
		panelFileSelection.add(btnReload);

		JPanel panelOperations = new JPanel();
		contentPane.add(panelOperations, BorderLayout.CENTER);
		panelOperations.setLayout(new GridLayout(0, 1, 0, 0));

		// Retarget horizontal
		JPanel panelRetargetH = new JPanel();
		panelRetargetH.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panelOperations.add(panelRetargetH);
		panelRetargetH.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JButton btnRetargetH = new JButton("Retarget Horizontal");
		btnRetargetH.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int width = (Integer) txtRetargetWidth.getValue();
				try {
					BufferedImage img = ImageProc.retargetHorizontal(MainWindow.this.img, width);
					MainWindow.this.img = img;
					present("RetargetH(w=[" + width + "])");
				} catch (Exception ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(MainWindow.this,
							"Error in Horizontal Retargeting!\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		btnRetargetH.setEnabled(false);
		panelRetargetH.add(btnRetargetH);
		operationButtons.add(btnRetargetH);

		JButton btnShowSeamsH = new JButton("Show seams");
		btnShowSeamsH.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int width = (Integer) txtRetargetWidth.getValue();

				try {
					BufferedImage img = ImageProc.showSeamsHorizontal(MainWindow.this.img, width);
					MainWindow.this.img = img;
					present("ShowSeamsH(w=[" + width + "])");
				} catch (Exception ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(MainWindow.this,
							"Error Showing Horizontal Seams!\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		btnShowSeamsH.setEnabled(false);
		panelRetargetH.add(btnShowSeamsH);
		operationButtons.add(btnShowSeamsH);

		JLabel lblRetargetWidth = new JLabel("Width:");
		panelRetargetH.add(lblRetargetWidth);

		txtRetargetWidth = new JFormattedTextField(new Integer(640));
		txtRetargetWidth.setColumns(5);
		panelRetargetH.add(txtRetargetWidth);

		// Retarget vertical
		JPanel panelRetargetV = new JPanel();
		panelRetargetV.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panelOperations.add(panelRetargetV);
		panelRetargetV.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JButton btnRetargetV = new JButton("Retarget Vertical");
		btnRetargetV.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int height = (Integer) txtRetargetHeight.getValue();

				try {
					BufferedImage img = ImageProc.retargetVertical(MainWindow.this.img, height);
					MainWindow.this.img = img;
					present("RetargetV(h=[" + height + "])");
				} catch (Exception ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(MainWindow.this,
							"Error in Vertical Retargeting!\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		btnRetargetV.setEnabled(false);
		panelRetargetV.add(btnRetargetV);
		operationButtons.add(btnRetargetV);

		JButton btnShowSeamsV = new JButton("Show seams");
		btnShowSeamsV.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int height = (Integer) txtRetargetHeight.getValue();

				try {
					BufferedImage img = ImageProc.showSeamsVertical(MainWindow.this.img, height);
					MainWindow.this.img = img;
					present("ShowSeamsV(h=[" + height + "])");
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(MainWindow.this, "Error Showing Vertical Seams!\n" + ex.getMessage(),
							"Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		btnShowSeamsV.setEnabled(false);
		panelRetargetV.add(btnShowSeamsV);
		operationButtons.add(btnShowSeamsV);

		JLabel lblRetargetHeight = new JLabel("Height:");
		panelRetargetV.add(lblRetargetHeight);

		txtRetargetHeight = new JFormattedTextField(new Integer(480));
		txtRetargetHeight.setColumns(5);
		panelRetargetV.add(txtRetargetHeight);

		// Multi-Sizing.
		JPanel panelBonus = new JPanel();
		panelBonus.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panelBonus.setLayout(new FlowLayout(FlowLayout.CENTER, 2, 5));
		panelOperations.add(panelBonus);

		String text = "<HTML><CENTER>BONUS:<BR>it's not exacly what you asked for, but,<BR>try to resize the image-window itself and it will resize on its own</CENTER></HTML>";
		JLabel lblBonusMsg = new JLabel(text);
		panelBonus.add(lblBonusMsg, BorderLayout.CENTER);

		// Gray-scale, gradient-magnitude & Rotations.
		JPanel panelMisc = new JPanel();
		panelMisc.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panelOperations.add(panelMisc);
		panelMisc.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JButton btnGrayScale = new JButton("Gray scale");
		btnGrayScale.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					img = ImageProc.grayScale(img);
					present("GrayScale");
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(MainWindow.this, "Error in gray scale!\n" + ex.getMessage(), "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		btnGrayScale.setEnabled(false);
		panelMisc.add(btnGrayScale);
		operationButtons.add(btnGrayScale);

		JButton btnGradientMag = new JButton("Gradient magnitude");
		btnGradientMag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					img = ImageProc.gradientMagnitude(img);
					present("GradientMag");
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(MainWindow.this, "Error in gradient magnitude!\n" + ex.getMessage(),
							"Error", JOptionPane.ERROR_MESSAGE);
				}

			}
		});
		btnGradientMag.setEnabled(false);
		panelMisc.add(btnGradientMag);
		operationButtons.add(btnGradientMag);

		JButton rotateCW = new JButton("Rotate CW");
		rotateCW.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				img = ImageProc.rotatePictureCW(img);
				present("Rotated counter-clockwise");
			}
		});
		rotateCW.setEnabled(false);
		panelMisc.add(rotateCW);
		operationButtons.add(rotateCW);

		JButton rotateCCW = new JButton("Rotate CCW");
		rotateCCW.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				img = ImageProc.rotatePictureCCW(img);
				present("Rotated clockwise");
			}
		});
		rotateCCW.setEnabled(false);
		panelMisc.add(rotateCCW);
		operationButtons.add(rotateCCW);

		// Lower panel
		JPanel panelStatus = new JPanel();
		panelStatus.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panelOperations.add(panelStatus);
		panelStatus.setLayout(new BorderLayout(0, 0));

		JLabel lblStatus = new JLabel(" Current image:   ");
		panelStatus.add(lblStatus, BorderLayout.WEST);

		txtStatus = new JTextArea();
		txtStatus.setEditable(false);
		txtStatus.setLineWrap(true);
		JScrollPane scrollStatus = new JScrollPane(txtStatus);
		panelStatus.add(scrollStatus);

		pack();
		setLocation((getScreenWidth() - getWidth()) / 2, (getsScreenHeight() - getHeight()) / 2);
	}

	void open(String filename) {
		try {
			BufferedImage img = ImageIO.read(new File(filename));
			if (img == null)
				throw new NullPointerException();
			this.img = img;
			imgTitle = new StringBuffer();
			present("Opened " + new File(filename).getName());
			txtRetargetHeight.setValue(img.getHeight());
			txtRetargetWidth.setValue(img.getWidth());
			for (JButton btn : operationButtons)
				btn.setEnabled(true);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Can't open file!", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	void present(String txt) {
		imgTitle.append(txt + "; ");
		txtStatus.setText(imgTitle.toString());
		new ImageWindow(img, imgTitle.toString()).setVisible(true);
	}

	static int getScreenWidth() {
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width;
	}

	static int getsScreenHeight() {
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height;
	}

	static boolean writeStringToFile(File outputFile, String string) {
		PrintWriter pw;
		try {
			pw = new PrintWriter(outputFile);
			pw.print(string);
			pw.flush();
			pw.close();
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	static String twoDArrToString(int[][] arr) {
		StringBuilder sb = new StringBuilder();
		String newLine = System.lineSeparator();

		for (int y = 0; y < arr.length; y++) {
			for (int x = 0; x < arr[y].length; x++) {
				sb.append(String.format("%3s,", arr[y][x]));
			}
			sb.append(newLine);
		}

		return sb.toString();
	}
}
