/*
 * This class displays an image in a new window and allows to save it as a PNG file.
 */

package edu.cg;

import javax.imageio.ImageIO;

import java.io.File;
import java.io.IOException;

import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentAdapter;

import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
public class ImageWindow extends JFrame {

	private static BufferedImage img;
	private JPanel contentPane;

	/**
	 * Create the window.
	 */
	public ImageWindow(BufferedImage img, String title) {
		ImageWindow.img = img;

		setTitle(title);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		JButton btnSaveAs = new JButton("Save as...");
		btnSaveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				int ret = fileChooser.showSaveDialog(ImageWindow.this);
				if (ret == JFileChooser.APPROVE_OPTION)
					save(fileChooser.getSelectedFile());
			}
		});
		contentPane.add(btnSaveAs, BorderLayout.NORTH);

		JPanel panelImage = new ImagePanel();
		contentPane.add(panelImage, BorderLayout.CENTER);

		pack();

		/**
		 * BONUS BONUS BONUS BONUS BONUS BONUS BONUS BONUS BONUS BONUS
		 */
		// Added an event listener that listens to the resizing of the window.
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent evt) {
				int width = getWidth();
				int height = getHeight() - 70;

				// Resize the picture to the new size of the window.
				// First stretch horizontally then vertically.
				// Pretty basic but it _HAS_ the same functionality of what that
				// was requested as a bonus.
				ImageWindow.img = new Retargeter(ImageWindow.img, Math.abs(ImageWindow.img.getWidth() - width), false)
						.retarget(width);
				ImageWindow.img = new Retargeter(ImageWindow.img, Math.abs(ImageWindow.img.getHeight() - height), true)
						.retarget(height);
			}
		});
	}

	private class ImagePanel extends JPanel {
		public ImagePanel() {
			setPreferredSize(new Dimension(img.getWidth(), img.getHeight()));
		}

		@Override
		protected void paintComponent(Graphics g) {
			g.drawImage(img, 0, 0, null);
		}
	}

	private void save(File file) {
		try {
			ImageIO.write(img, "png", file);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Can't save file!", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}
