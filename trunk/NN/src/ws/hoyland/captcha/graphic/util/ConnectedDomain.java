package ws.hoyland.captcha.graphic.util;

import java.util.ArrayList;

import ws.hoyland.captcha.graphic.util.GetConnecteddomain.Line;

//连通域
public class ConnectedDomain {

	private ArrayList<Line> linelist;// 存放线段
	private int pixelcount;// 像素数量
	private double[][] bit;// 像素方式输出
	private int left;
	private int top;
	private int bot;
	private int right;

	protected void setLeft(int left) {

		this.left = left;

	}

	public int getLeft() {

		return this.left;

	}

	protected void setRight(int right) {

		this.right = right;
	}

	public int getRight() {

		return this.right;
	}

	protected void setTop(int top) {

		this.top = top;
	}

	public int getTop() {

		return this.top;
	}

	protected void setBot(int bot) {

		this.bot = bot;
	}

	public int getBot() {

		return this.bot;
	}

	public int getPixelcount() {

		return this.pixelcount;

	}

	public void addConnectedDomain(ConnectedDomain domain) {

		for (int l = 0; l < domain.linelist.size(); l++)
			this.addNewline(domain.linelist.get(l));

	}

	public boolean belongtodomain(Line newline) {
		boolean belogto = false;
		for (int j = 0; j < this.linelist.size(); j++) {

			Line preline = this.linelist.get(j);
			if (preline.linenum != newline.linenum - 1)
				continue;
			if ((newline.start >= preline.start && newline.start <= preline.end)
					|| (newline.end >= preline.start && newline.end <= preline.end)
					|| (preline.start >= newline.start && preline.start <= newline.end)
					|| (preline.end >= newline.end && preline.end <= newline.end)) {
				belogto = true;
				break;
			}

		}
		return belogto;

	}

	protected ConnectedDomain() {
		this.left = Integer.MAX_VALUE;// 设置成不可能的最右边
		this.right = -1;// 设置成不可能的最左边
		this.top = Integer.MAX_VALUE;
		this.bot = -1;
		linelist = new ArrayList<Line>();
	}

	public void addNewline(Line newline) {
		linelist.add(newline);
		this.pixelcount = this.pixelcount + newline.end - newline.start + 1;
		this.setLeft(this.left > newline.start ? newline.start : this.getLeft());
		this.setRight(this.getRight() < newline.end ? newline.end : this
				.getRight());
		this.setTop(this.getTop() > newline.linenum ? newline.linenum : this
				.getTop());
		this.setBot(this.getBot() < newline.linenum ? newline.linenum : this
				.getBot());
	}

	public double[][] getBit() {
		if (bit == null)
			bit = new double[this.bot - this.top + 1][this.right - this.left
					+ 1];

		for (int i = 0; i < linelist.size(); i++) {
			Line line = linelist.get(i);
			for (int j = line.getStart(); j <= line.getEnd(); j++) {
				bit[line.getLinenum() - this.top][j - this.left] = 1.0;

			}

		}

		return bit;
	}
}
