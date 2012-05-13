package ws.hoyland.st.df;

import java.io.FileOutputStream;
import java.util.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.*;

import ws.hoyland.st.OutputMonitor;

public class DefaultMonitor implements OutputMonitor {
	
	private List<Object[]> close;
	private List<Object[]> assets;
	
	public DefaultMonitor(){
		this.close = new ArrayList<Object[]>();
		this.assets = new ArrayList<Object[]>();
	}
	
	@Override
	public void put(String date, String message) {
		//System.out.println("["+date+"]:"+message);
		String[] msg = message.split(":");
		//System.out.println(message+">"+msg.length);
		this.close.add(new Object[]{msg[0], "close", date});
		this.assets.add(new Object[]{Float.valueOf(msg[1])*Float.valueOf(msg[2])+Float.valueOf(msg[3]), "assets", date});
	}
	
	@Override
	public void draw(){
		 
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for(Object[] obj : this.close){
			dataset.addValue((Double)(obj[0]), obj[1].toString(), obj[2].toString());
		}
		
		for(Object[] obj : this.assets){
			dataset.addValue((Double)(obj[0]), obj[1].toString(), obj[2].toString());
		}

		JFreeChart chart = ChartFactory.createLineChart("My Strategy", // chart title
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
		
		FileOutputStream jpg = null;
		try {
			jpg = new FileOutputStream("C:\\fruit.jpg");
			//第二个参数是设置图片清晰度，从0.1f到1.0f
			ChartUtilities.writeChartAsJPEG(jpg, 1.0f, chart, 1024, 768, null);
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			try {
				jpg.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
