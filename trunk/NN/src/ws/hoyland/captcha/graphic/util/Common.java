package ws.hoyland.captcha.graphic.util;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class Common {

	// 获得灰度图
	public static int[][] getGray(BufferedImage bi) {
		int w = bi.getWidth();
		int h = bi.getHeight();
		int[][] gray = new int[w][h];
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				gray[i][j] = getGray(bi.getRGB(i, j));
			}
		}
		return gray;
	}

	// 将数组转为字符串。
	public static String arraytoString(double[] in) {
		String out = "";
		for (int i = 0; i < in.length; i++) {
			out = out + in[i];
			if (i != in.length - 1)
				out = out + ",";
		}
		return out;
	}

	public static int[][] get01bit(int[][] gray) {
		int w = gray.length;
		int h = gray[0].length;
		int[][] bit01 = new int[w][h];
		int SW = 160;
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				if (Common.getAverageColor(gray, x, y, w, h) > SW) {
					bit01[x][y] = 0;
				} else {
					bit01[x][y] = 1;
				}
			}
		}

		return bit01;

	}

	public static int getGray(int rgb) {
		String str = Integer.toHexString(rgb);
		int r = Integer.parseInt(str.substring(2, 4), 16);
		int g = Integer.parseInt(str.substring(4, 6), 16);
		int b = Integer.parseInt(str.substring(6, 8), 16);
		// or 直接new个color对象
		Color c = new Color(rgb);
		r = c.getRed();
		g = c.getGreen();
		b = c.getBlue();
		int top = (r + g + b) / 3;
		return (int) (top);
	}

	public static int getAverageColor(int[][] gray, int x, int y, int w, int h) {
		int rs = gray[x][y] + (x == 0 ? 255 : gray[x - 1][y])
				+ (x == 0 || y == 0 ? 255 : gray[x - 1][y - 1])
				+ (x == 0 || y == h - 1 ? 255 : gray[x - 1][y + 1])
				+ (y == 0 ? 255 : gray[x][y - 1])
				+ (y == h - 1 ? 255 : gray[x][y + 1])
				+ (x == w - 1 ? 255 : gray[x + 1][y])
				+ (x == w - 1 || y == 0 ? 255 : gray[x + 1][y - 1])
				+ (x == w - 1 || y == h - 1 ? 255 : gray[x + 1][y + 1]);
		return rs / 9;
	}

	public static int[][] transsize(int[][] graph, int width, int height) {
		int fheight = graph.length;
		int fwidth = graph[0].length;
		int[][] tgraph = new int[height][width];
		// 首先在宽度
		for (int i = 0; i < height; i++) {

			for (int j = 0; j < width; j++) {

				tgraph[i][j] = graph[i * fheight / height][j * fwidth / width];
			}
		}

		return tgraph;
	}

	public static double[][] transsize(double[][] graph, int width, int height) {
		int fheight = graph.length;
		int fwidth = graph[0].length;
		double[][] tgraph = new double[height][width];
		// 首先在宽度
		for (int i = 0; i < height; i++) {

			for (int j = 0; j < width; j++) {

				tgraph[i][j] = graph[i * fheight / height][j * fwidth / width];

			}

		}

		return tgraph;
	}

}
