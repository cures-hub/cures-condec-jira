package de.uhd.ifi.se.decision.management.jira.extraction.view.reports.plotlib;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;

/**
 * @see https://stackoverflow.com/a/35814571/230513
 */
public class Plotter {

	public static BufferedImage getBoxPlot(String name, String yLabel, List<?> dataSet) {
		DefaultBoxAndWhiskerCategoryDataset data = new DefaultBoxAndWhiskerCategoryDataset();
		data.add(dataSet, "", "");
		JFreeChart chart = ChartFactory.createBoxAndWhiskerChart(name, "", yLabel, data, false);

		chart.setBackgroundPaint(Color.white);
		chart.getPlot().setBackgroundPaint(Color.white);

		return chart.createBufferedImage(200, 400,BufferedImage.TYPE_INT_RGB,null);
	}

	private static PieDataset createDataset(Map<String, Integer> dkeCount) {
		DefaultPieDataset dataset = new DefaultPieDataset();

		for (String key : dkeCount.keySet()) {
			dataset.setValue(key, dkeCount.get(key));
		}
		return dataset;
	}

	public static BufferedImage getPieChart(String string, Map<String, Integer> dkeCount) {
		JFreeChart chart = ChartFactory.createPieChart(string, // chart title
				createDataset(dkeCount), // data
				true, // include legend
				true, false);

		PiePlot plot = (PiePlot) chart.getPlot();
		plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
		plot.setNoDataMessage("No data available");
		plot.setCircular(false);
		plot.setLabelGap(0.02);
		chart.setBackgroundPaint(Color.white);

		return chart.createBufferedImage(400, 400,BufferedImage.TYPE_INT_RGB,null);
	}
}