package ws.hoyland.st.df;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.*;

import ws.hoyland.st.OutputMonitor;

public class DefaultMonitor implements OutputMonitor {

	private List<Object[]> close;
	private List<Object[]> assets;

	public DefaultMonitor() {
		this.close = new ArrayList<Object[]>();
		this.assets = new ArrayList<Object[]>();
	}

	@Override
	public void put(String date, String message) {
		// System.out.println("["+date+"]:"+message);
		String[] msg = message.split(":");
		// System.out.println(message+">"+msg.length);
		this.close.add(new Object[] { msg[0], "Close", date });
		this.assets.add(new Object[] {
				Float.valueOf(msg[0]) * Float.valueOf(msg[2])
						+ Float.valueOf(msg[3]), "Assets", date });
	}

	@Override
	public void draw() {

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		// System.out.println(close.size());
		for (Object[] obj : this.close) {
			dataset.addValue(Double.parseDouble((obj[0].toString())),
					obj[1].toString(), obj[2].toString());
		}

		for (Object[] obj : this.assets) {
			dataset.addValue(Double.parseDouble((obj[0].toString())),
					obj[1].toString(), obj[2].toString());
		}

		JFreeChart chart = ChartFactory.createLineChart("My Strategy", // chart
																		// title
				"Date", // domain axis label
				"C&A", // range axis label
				dataset, // data
				PlotOrientation.VERTICAL, // orientation
				true, // include legend
				true, // tooltips
				false // urls
				);
		CategoryPlot line = chart.getCategoryPlot();
		// customise the range axis...
		NumberAxis rangeAxis = (NumberAxis) line.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		rangeAxis.setAutoRangeIncludesZero(true);
		rangeAxis.setUpperMargin(0.20);
		rangeAxis.setLabelAngle(Math.PI / 2.0);

		line.setRangeAxis(rangeAxis);

		ChartFrame frame = new ChartFrame("����ͼ", chart);
		frame.setPreferredSize(new Dimension(1024, 768));
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize(); // ȡ�õ�ǰ��Ļ����
		int w = d.width; // ��ȡ��Ļ���
		int h = d.height; // ��ȡ��Ļ�߶�
		//System.out.println(w+":"+h);
		frame.setBounds((w - 1024) / 2, (h - 768) / 2, 1024, 768);
		frame.pack();
		frame.setVisible(true);

		//
		// FileOutputStream png = null;
		// try {
		// png = new FileOutputStream("out/result.png");
		// ChartUtilities.writeChartAsPNG(png, chart, 1024, 768, null);
		// }catch(Exception e){
		// e.printStackTrace();
		// }finally {
		// try {
		// png.close();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
	}
}
