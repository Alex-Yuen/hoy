package ws.hoyland.st.df;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.*;

import ws.hoyland.st.OutputMonitor;

public class DefaultMonitor implements OutputMonitor {

	private List<Object[]> close;
	private List<Object[]> assets;
	private List<Object[]> cash;

	public DefaultMonitor() {
		this.close = new ArrayList<Object[]>();
		this.assets = new ArrayList<Object[]>();
		this.cash = new ArrayList<Object[]>();
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
		this.cash.add(new Object[] { msg[3], "Cash", date });
	}

	@Override
	public void draw() {

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		DefaultCategoryDataset datasetofassets = new DefaultCategoryDataset();
//		DefaultCategoryDataset datasetofcash = new DefaultCategoryDataset();
		
		for (Object[] obj : this.close) {
			dataset.addValue(Double.parseDouble((obj[0].toString())),
					obj[1].toString(), obj[2].toString());
		}

		for (Object[] obj : this.assets) {
			datasetofassets.addValue(Double.parseDouble((obj[0].toString())),
					obj[1].toString(), obj[2].toString());
		}
		
		for (Object[] obj : this.cash) {
			datasetofassets.addValue(Double.parseDouble((obj[0].toString())),
					obj[1].toString(), obj[2].toString());
		}

		JFreeChart chart = ChartFactory.createLineChart("My Strategy", // chart title
				"Date", // domain axis label
				"Close", // range axis label
				dataset, // data
				PlotOrientation.VERTICAL, // orientation
				true, // include legend
				true, // tooltips
				false // urls
				);
		
		CategoryPlot plot = chart.getCategoryPlot();
		
		//第一个Y轴
		NumberAxis rangeAxis = (NumberAxis)plot.getRangeAxis();
		//rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		//rangeAxis.setAutoRangeIncludesZero(true);
		//rangeAxis.setUpperMargin(0.20);
		//rangeAxis.setLabelAngle(Math.PI / 2.0);
		rangeAxis.setAxisLinePaint(Color.RED);  
		rangeAxis.setLabelPaint(Color.RED);  
		rangeAxis.setTickLabelPaint(Color.RED);  
		
		//第二个Y轴		   
        NumberAxis axisofassets = new NumberAxis("Benefit");        
        axisofassets.setAxisLinePaint(Color.BLUE);  
        axisofassets.setLabelPaint(Color.BLUE);  
        axisofassets.setTickLabelPaint(Color.BLUE);          
        plot.setRangeAxis(1, axisofassets);  
        plot.setDataset(1, datasetofassets);  
        plot.mapDatasetToRangeAxis(1, 1);
        
        CategoryItemRenderer renderofassets = new LineAndShapeRenderer();
//        renderofassets.setBaseShape(new java.awt.geom.Rectangle2D.Double());
//        renderofassets.setBaseStroke(new BasicStroke());
        //System.out.println(plot.getRenderer().getBaseStroke().getClass().getName());
        renderofassets.setSeriesPaint(0, Color.BLUE);
        renderofassets.setSeriesPaint(1, Color.GREEN);
        plot.setRenderer(1, renderofassets);
        
		//第三个Y轴		   
//        NumberAxis axisofcash = new NumberAxis("Cash");        
//        axisofcash.setAxisLinePaint(Color.MAGENTA);  
//        axisofcash.setLabelPaint(Color.MAGENTA);  
//        axisofcash.setTickLabelPaint(Color.MAGENTA);          
//        plot.setRangeAxis(2, axisofcash);  
//        plot.setDataset(2, datasetofcash);  
//        plot.mapDatasetToRangeAxis(2, 2);
//        
//        CategoryItemRenderer renderofcash = new LineAndShapeRenderer();
//        renderofcash.setSeriesPaint(0, Color.MAGENTA);
//        plot.setRenderer(2, renderofcash);
        
		ChartFrame frame = new ChartFrame("折线图", chart);
		frame.setPreferredSize(new Dimension(1024, 768));
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize(); // 取得当前屏幕属性
		int w = d.width; // 获取屏幕宽度
		int h = d.height; // 获取屏幕高度
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
