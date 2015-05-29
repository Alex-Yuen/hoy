package ws.hoyland.captcha.graphic.util;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

//获得连通域

public class GetConnecteddomain {

	private int[][] initail; // 原始二值图
	private Hashtable<Integer, ConnectedDomain> connecteddomaintable = new Hashtable<Integer, ConnectedDomain>(); // 存放连通域；
	private int connecteddomainindex = 0;// 连通域标识

	public class Line {
		int linenum; // 行数
		int start; // 开始点
		int end; // 结束点

		public int getLinenum() {

			return linenum;

		}

		public int getStart() {

			return start;

		}

		public int getEnd() {

			return end;

		}

		ArrayList<Integer> belongto = new ArrayList<Integer>();// 所属的连通域 临时用
	}

	public void setInitail(int[][] initail) {

		this.initail = initail;

	}

	public Hashtable<Integer, ConnectedDomain> getConnecteddomain() {

		int width = initail.length;
		int height = initail[0].length;
		for (int i = 0; i < width; i++) {//
			// 开始扫描线段
			boolean inaline = false;
			Line newline = null;
			for (int j = 0; j < height; j++) {
				if (initail[i][j] == 1) {
					if (!inaline && j != height - 1) {// 前一个点不是1 这个点是1
						// 并且这个点不是最后一点
						// 说明开始了一条新线段
						newline = new Line();
						newline.linenum = i;
						newline.start = j;
						inaline = true;
					}
					if (!inaline && j == height - 1) {// 前一个点不是1 这个点是1
						// 同时这个点是这行的结束
						// 这个点是最后一点
						newline = new Line();
						newline.linenum = i;
						newline.start = j;
						newline.end = j;
						this.addlinetoconnecteddomain(newline);

					}
					if (inaline && j == height - 1) {

						newline.end = j;
						this.addlinetoconnecteddomain(newline);
					}

				}
				if (initail[i][j] == 0) {

					if (inaline) {// 前一个点是1 这个点不是1 说明是线段的结束

						newline.end = j - 1;
						addlinetoconnecteddomain(newline);
						inaline = false;
					}

				}

			}
			// 扫描完一个线段

		}
		// 过滤连通区域
		return this.connecteddomaintable;

	}

	// 将线段放入连通区域
	public void addlinetoconnecteddomain(Line newline) {

		Iterator<Integer> itr = this.connecteddomaintable.keySet().iterator();

		while (itr.hasNext()) {
			Integer index = itr.next();
			ConnectedDomain connecteddomain = connecteddomaintable.get(index);
			if (connecteddomain.belongtodomain(newline))
				newline.belongto.add(index);

		}

		// 如果该线段不属于任何连通区域 新增加一个连通通域

		if (newline.belongto.size() == 0) {

			ConnectedDomain connecteddomain = new ConnectedDomain();
			connecteddomain.addNewline(newline);

			this.connecteddomaintable.put(new Integer(connecteddomainindex++),
					connecteddomain);
		}

		// 合并连通区域

		else {

			ConnectedDomain connecteddomain = null;
			for (int k = 0; k < newline.belongto.size(); k++) {
				if (connecteddomain == null) {
					connecteddomain = this.connecteddomaintable
							.get(newline.belongto.get(k));
				} else {
					ConnectedDomain connecteddomaink = this.connecteddomaintable
							.get(newline.belongto.get(k));
					connecteddomain.addConnectedDomain(connecteddomaink);
					this.connecteddomaintable.remove(newline.belongto.get(k));// 删除掉被合并的连通域

				}

			}
			connecteddomain.addNewline(newline);

		}

	}

}
