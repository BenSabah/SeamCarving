/*
 * This class defines some static methods of image processing.
 */

package edu.cg;

import java.awt.image.BufferedImage;

public class ImageProc {

	// Creates a reduced size image from the original image by taking every
	// <factor>-th pixel in x and y direction
	public static BufferedImage scaleDown(BufferedImage img, int factor) {
		if (factor <= 0)
			throw new IllegalArgumentException();
		int newHeight = img.getHeight() / factor;
		int newWidth = img.getWidth() / factor;
		BufferedImage out = new BufferedImage(newWidth, newHeight, img.getType());
		for (int x = 0; x < newWidth; x++)
			for (int y = 0; y < newHeight; y++)
				out.setRGB(x, y, img.getRGB(x * factor, y * factor));
		return out;
	}

	// Runs the seam carving algorithm to resize an image horizontally (change
	// width)
	public static BufferedImage retargetHorizontal(BufferedImage img, int width) {
		return new Retargeter(img, Math.abs(img.getWidth() - width), false).retarget(width);
	}

	// Runs the seam carving algorithm to resize an image vertically (change
	// height)
	public static BufferedImage retargetVertical(BufferedImage img, int height) {
		return new Retargeter(img, Math.abs(img.getHeight() - height), true).retarget(height);
	}

	// Runs the horizontal seam carving algorithm to present the seams for
	// removal/duplication
	public static BufferedImage showSeamsHorizontal(BufferedImage img, int width) {
		return new Retargeter(img, Math.abs(img.getWidth() - width), false).showSeams(width);
	}

	// Runs the vertical seam carving algorithm to present the seams for
	// removal/duplication
	public static BufferedImage showSeamsVertical(BufferedImage img, int height) {
		return new Retargeter(img, Math.abs(img.getHeight() - height), true).showSeams(height);
	}

	// Converts an image to gray scale
	public static BufferedImage grayScale(BufferedImage img) {
		int height = img.getHeight();
		int width = img.getWidth();
		BufferedImage res = new BufferedImage(width, height, img.getType());

		int rgb;
		int val;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				rgb = img.getRGB(x, y);
				val = (int) (getR(rgb) * 0.2989 + getG(rgb) * 0.587 + getB(rgb) * 0.114);
				val = calcRGB(getA(rgb), val, val, val);
				res.setRGB(x, y, val);
			}
		}
		return res;
	}

	// Calculates the magnitude of gradient at each pixel
	public static BufferedImage gradientMagnitude(BufferedImage img) {
		int height = img.getHeight();
		int width = img.getWidth();
		BufferedImage res = new BufferedImage(width, height, img.getType());

		int rgb, val;
		int above, current, left;
		for (int y = 1; y < height; y++) {
			for (int x = 1; x < width; x++) {
				rgb = img.getRGB(x, y);
				current = getR(rgb);
				above = getR(img.getRGB(x, y - 1));
				left = getR(img.getRGB(x - 1, y));
				val = (int) Math.sqrt(Math.pow(left - current, 2) + Math.pow(above - current, 2));
				res.setRGB(x, y, calcRGB(getA(rgb), val, val, val));
			}
		}

		return res;
	}

	static int getA(int rgb) {
		return (rgb >> 24) & 0xff;
	}

	static int getR(int rgb) {
		return (rgb >> 16) & 0xff;
	}

	static int getG(int rgb) {
		return (rgb >> 8) & 0xff;
	}

	static int getB(int rgb) {
		return rgb & 0xff;
	}

	static int calcRGB(int a, int r, int g, int b) {
		return ((a & 0xff) << 24) | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
	}

	static int[][] convertBufImgToIntArr(BufferedImage img) {
		int width = img.getWidth();
		int height = img.getHeight();
		int[][] res = new int[height][width];

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				res[y][x] = img.getRGB(x, y) & 0xff;
			}
		}
		return res;
	}

	static BufferedImage rotatePictureCCW(BufferedImage img) {
		int width = img.getWidth();
		int height = img.getHeight();
		BufferedImage res = new BufferedImage(height, width, img.getType());

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				res.setRGB(y, width - x - 1, img.getRGB(x, y));
			}
		}
		return res;
	}

	static BufferedImage rotatePictureCW(BufferedImage img) {
		int width = img.getWidth();
		int height = img.getHeight();
		BufferedImage res = new BufferedImage(height, width, img.getType());

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				res.setRGB(height - y - 1, x, img.getRGB(x, y));
			}
		}
		return res;
	}

}
