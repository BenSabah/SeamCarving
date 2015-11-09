package edu.cg;

import java.awt.image.BufferedImage;

public class Retargeter {

	private BufferedImage img;
	private int k;
	private int[][] grayScaleMatrix;
	private boolean isVertical;
	private int[][] costMatrix;
	private int[][] seamOrderMatrix;
	private int[][] helperMatrix;
	private int WIDTH_OF_RELEVANCE;
	private final int LARGE_NUMBER = 0xffff;
	private final int RED_COLOR = ImageProc.calcRGB(255, 255, 0, 0);

	// Does all necessary pre-processing, including the calculation of the seam
	// order matrix. k is the amount of seams to pre-process isVertical tells
	// whether the resizing is vertical or horizontal
	public Retargeter(BufferedImage img, int k, boolean isVertical) {
		this.isVertical = isVertical;
		this.k = k;
		this.img = (isVertical) ? ImageProc.rotatePictureCW(img) : img;

		// Create the helper matrix, and get the gray-scale image as a matrix.
		startHelperMatrix();
		grayScaleMatrix = ImageProc.convertBufImgToIntArr(ImageProc.grayScale(this.img));

		// Calculate the starting costs matrix.
		calculateCostsMatrix();

		// Calculate the amount of requested seams.
		calculateSeamsOrderMatrix();
	}

	private void startHelperMatrix() {
		int width = img.getWidth();
		int height = img.getHeight();
		helperMatrix = new int[height][width];

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				helperMatrix[y][x] = x;
			}
		}
	}

	// Does the actual resizing of the image
	public BufferedImage retarget(int newSize) {
		int width = img.getWidth();
		int height = img.getHeight();
		BufferedImage res = new BufferedImage(newSize, height, img.getType());

		// Check if squeezing or stretching;
		boolean squeeze = (newSize <= width);

		if (squeeze) {
			// Squeeze.
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < WIDTH_OF_RELEVANCE; x++) {
					res.setRGB(x, y, img.getRGB(helperMatrix[y][x], y));
				}
			}
		} else {
			// Stretch.
			int targetX = 0;
			for (int y = 0; y < height; y++) {
				targetX = 0;
				for (int x = 0; x < width; x++) {

					// If stepped on a seam duplicate it.
					if (seamOrderMatrix[y][x] > 0) {
						res.setRGB(targetX++, y, img.getRGB(x, y));
					}
					res.setRGB(targetX++, y, img.getRGB(x, y));
				}
			}
		}

		// If Vertical we need to rotate it to original position.
		return (isVertical) ? ImageProc.rotatePictureCCW(res) : res;
	}

	// Colors the seams pending for removal/duplication
	public BufferedImage showSeams(int newSize) {
		int gap = Math.abs(((isVertical) ? img.getHeight() : img.getWidth()) - newSize);

		// Making sure we are not asked for more seams than we have.
		gap = (gap > k) ? k : gap;

		int width = img.getWidth();
		int height = img.getHeight();
		BufferedImage res = new BufferedImage(width, height, img.getType());

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int val = seamOrderMatrix[y][x];

				// If there's a seam, color it RED, else, copy the origial color
				if (val != 0 && val <= gap) {
					res.setRGB(x, y, RED_COLOR);
				} else {
					res.setRGB(x, y, img.getRGB(x, y));
				}
			}
		}
		return (isVertical) ? ImageProc.rotatePictureCCW(res) : res;
	}

	// Calculates the cost matrix for a given image width (w).
	// To be used inside calculateSeamsOrderMatrix().
	private void calculateCostsMatrix() {
		int height = img.getHeight();
		int width = img.getWidth();
		costMatrix = new int[height][width];
		WIDTH_OF_RELEVANCE = width;

		// Setting the 2 sides of the picture to max integer.
		for (int y = 0; y < height; y++) {
			costMatrix[y][0] = LARGE_NUMBER;
			costMatrix[y][width - 1] = LARGE_NUMBER;
		}

		// Calculate the first row energies.
		for (int x = 1; x < width - 1; x++) {
			costMatrix[0][x] = Math.abs(grayScaleMatrix[0][x - 1] - grayScaleMatrix[0][x + 1]);
		}

		// Calculating the rest of the values of the cost matrix.
		for (int y = 1; y < height; y++) {
			for (int x = 1; x < width - 1; x++) {
				int center = Math.abs(grayScaleMatrix[y][x + 1] - grayScaleMatrix[y][x - 1]);
				int left = center + Math.abs(grayScaleMatrix[y - 1][x] - grayScaleMatrix[y][x - 1]);
				int right = center + Math.abs(grayScaleMatrix[y][x + 1] - grayScaleMatrix[y - 1][x]);

				int cCost = costMatrix[y - 1][x] + center;
				int lCost = costMatrix[y - 1][x - 1] + left;
				int rCost = costMatrix[y - 1][x + 1] + right;

				costMatrix[y][x] = threeWayMin(cCost, lCost, rCost);
			}
		}
	}

	// Calculates the order in which seams are extracted
	private void calculateSeamsOrderMatrix() {
		int height = img.getHeight();
		int width = img.getWidth();
		seamOrderMatrix = new int[height][width];

		for (int i = 1; i <= k; i++) {
			// Start by finding the lowest cost at the bottom line.
			int curIndex = indexOfMinInt(costMatrix[height - 1]);
			seamOrderMatrix[height - 1][curIndex] = i;
			pullPixels(curIndex, height - 1);

			for (int y = height - 2; y >= 0; y--) {

				// Get the cost of each pixel.
				int leftVal = costMatrix[y][curIndex - 1];
				int centerVal = costMatrix[y][curIndex];
				int rightVal = costMatrix[y][curIndex + 1];

				int min = threeWayMin(leftVal, centerVal, rightVal);

				// Update the Seam-Order matrix.
				if (min == leftVal) {
					curIndex = curIndex - 1;
				} else if (min == centerVal) {
					// curIndex = curIndex;
				} else if (min == rightVal) {
					curIndex = curIndex + 1;
				}
				seamOrderMatrix[y][helperMatrix[y][curIndex]] = i;
				pullPixels(curIndex, y);
			}
			WIDTH_OF_RELEVANCE--;
			recalculateCosts();
		}
	}

	// This method pulls the values of the asked "pixel", essentially removing
	// the fact that it was there.
	private void pullPixels(int xPoint, int yPoint) {
		for (int x = xPoint; x < WIDTH_OF_RELEVANCE - 1; x++) {
			grayScaleMatrix[yPoint][x] = grayScaleMatrix[yPoint][x + 1];
			helperMatrix[yPoint][x] = helperMatrix[yPoint][x + 1];
		}
	}

	private void recalculateCosts() {
		int height = img.getHeight();

		// Setting the 2 sides of the picture to max integer.
		for (int y = 0; y < height; y++) {
			costMatrix[y][0] = LARGE_NUMBER;
			costMatrix[y][WIDTH_OF_RELEVANCE - 1] = LARGE_NUMBER;
		}

		// Re-Calculate the first row energies.
		for (int x = 1; x < WIDTH_OF_RELEVANCE - 1; x++) {
			costMatrix[0][x] = Math.abs(grayScaleMatrix[0][x - 1] - grayScaleMatrix[0][x + 1]);
		}

		// Re-calculating the values of the cost matrix.
		for (int y = 1; y < height; y++) {
			for (int x = 1; x < WIDTH_OF_RELEVANCE - 1; x++) {

				int center = Math.abs(grayScaleMatrix[y][x + 1] - grayScaleMatrix[y][x - 1]);
				int left = center + Math.abs(grayScaleMatrix[y - 1][x] - grayScaleMatrix[y][x - 1]);
				int right = center + Math.abs(grayScaleMatrix[y - 1][x] - grayScaleMatrix[y][x + 1]);

				int cCost = costMatrix[y - 1][x] + center;
				int lCost = costMatrix[y - 1][x - 1] + left;
				int rCost = costMatrix[y - 1][x + 1] + right;

				costMatrix[y][x] = threeWayMin(cCost, lCost, rCost);
			}
		}
	}

	// For debugging purposes.
	public int[][] getCostMatrix() {
		return costMatrix;
	}

	// For debugging purposes.
	public int[][] getSeamOrderMatrix() {
		return seamOrderMatrix;
	}

	// Do i really need to explain ?
	private int threeWayMin(int a, int b, int c) {
		return Math.min(Math.min(a, b), c);
	}

	// This method returns the index of the lowest value of the given array.
	private int indexOfMinInt(int[] costs) {
		int indexOfLowest = 0;
		for (int x = 1; x < costs.length; x++) {
			if (costs[x] < costs[indexOfLowest]) {
				indexOfLowest = x;
			}
		}

		return indexOfLowest;
	}
}
