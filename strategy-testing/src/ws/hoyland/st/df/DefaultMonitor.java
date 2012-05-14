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
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.*;

import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;

import ws.hoyland.st.OutputMonitor;

public class DefaultMonitor implements OutputMonitor {

	private List<Object[]> close;
	private List<Object[]> assets;
	private List<Object[]> cash;
	private List<Object[]> volumn;

	public DefaultMonitor() {
		this.close = new ArrayList<Object[]>();
		this.assets = new ArrayList<Object[]>();
		this.cash = new ArrayList<Object[]>();
		this.volumn = new ArrayList<Object[]>();
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
		this.volumn.add(new Object[] { msg[4], "Volumn", date });
	}

	@Override
	public void draw() {
		double[] cc = new double[this.close.size()];
		int i = 0;
		for (Object[] obj : this.close) {
			cc[i++] = Double.parseDouble((obj[0].toString()));
		}
		
		Core ta = new Core();
		MInteger mi_begin = new MInteger();
		MInteger mi_length = new MInteger();
		
//		System.out.println(ret.name());
//		System.out.println(ret);
		int period = 120;
		double[] ema = new double[this.close.size()];
		ta.ema(0, cc.length-1, cc, period, mi_begin, mi_length, ema);
		
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		DefaultCategoryDataset datasetofassets = new DefaultCategoryDataset();
		DefaultCategoryDataset datasetofvolumn = new DefaultCategoryDataset();
		
		i = 0;
		for (Object[] obj : this.close) {
			dataset.addValue(Double.parseDouble((obj[0].toString())),
					obj[1].toString(), obj[2].toString());
			if(i<period){
				dataset.addValue(0,
						"EMA", obj[2].toString());
			}else{
				dataset.addValue(ema[i-period],
						"EMA", obj[2].toString());
			}
			i++;
		}

		for (Object[] obj : this.assets) {
			datasetofassets.addValue(Double.parseDouble((obj[0].toString())),
					obj[1].toString(), obj[2].toString());
		}
		
		for (Object[] obj : this.cash) {
			datasetofassets.addValue(Double.parseDouble((obj[0].toString())),
					obj[1].toString(), obj[2].toString());
		}
		
		for (Object[] obj : this.volumn) {
			datasetofvolumn.addValue(Double.parseDouble((obj[0].toString())),
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
		
		plot.getRenderer().setSeriesPaint(1, Color.GREEN);
		
		//第二个Y轴		   
        NumberAxis axisofassets = new NumberAxis("Benefit");        
        axisofassets.setAxisLinePaint(Color.BLUE);  
        axisofassets.setLabelPaint(Color.BLUE);  
        axisofassets.setTickLabelPaint(Color.BLUE);          
        plot.setRangeAxis(1, axisofassets);  
        plot.setDataset(1, datasetofassets);  
        plot.mapDatasetToRangeAxis(1, 1);
        
        LineAndShapeRenderer renderofassets = new LineAndShapeRenderer();
//        renderofassets.setBaseShape(new java.awt.geom.Rectangle2D.Double());
        //renderofassets.setBaseShape(new Rectangle2D.Double(-1.5, -1.5, 3, 3));
        renderofassets.setBaseShapesVisible(false);
        //renderofassets.setShapesVisible(false); 
        //renderofassets.setDrawOutlines(true);
        //renderofassets.setUseFillPaint(true); 
        //renderofassets.setBaseStroke(new BasicStroke(7.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
        //System.out.println(plot.getRenderer().getBaseStroke().getClass().getName());
        renderofassets.setSeriesPaint(0, Color.BLUE);
        renderofassets.setSeriesPaint(1, Color.WHITE);
        plot.setRenderer(1, renderofassets);
        
		//第三个Y轴		   
        NumberAxis axisofvolumn = new NumberAxis("Volumn");        
        axisofvolumn.setAxisLinePaint(Color.GRAY);  
        axisofvolumn.setLabelPaint(Color.GRAY);  
        axisofvolumn.setTickLabelPaint(Color.GRAY);
        axisofvolumn.setRange(0, 220000000*4);
        plot.setRangeAxis(3, axisofvolumn);
        plot.setDataset(3, datasetofvolumn);
        plot.mapDatasetToRangeAxis(3, 3);
        
        CategoryItemRenderer renderofvolumn = new BarRenderer();
        renderofvolumn.setSeriesPaint(0, Color.GRAY);
        plot.setRenderer(3, renderofvolumn);
        
		ChartFrame frame = new ChartFrame("测试结果", chart);
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
