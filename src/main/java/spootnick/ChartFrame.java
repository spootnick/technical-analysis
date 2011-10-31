package spootnick;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.ohlc.OHLCSeries;
import org.jfree.data.time.ohlc.OHLCSeriesCollection;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import sample.SampleZip;
import spootnick.data.Quote;
import spootnick.data.QuoteSeries;
import spootnick.data.QuoteSeriesFactory;
import spootnick.result.Action;
import spootnick.result.Action.Side;
import spootnick.result.Result;
import spootnick.result.ResultDao;
import spootnick.runtime.Simulation;

//import org.jfree.ui.Spacer;

@Component
public class ChartFrame extends Simulation {

	private ApplicationFrame frame = new ApplicationFrame("Quote Player");
	

	private OHLCSeries series;
	private OHLCSeries buySeries;
	private OHLCSeries sellSeries;
	private JFreeChart chart;
	private ArrayList<Double> values = new ArrayList<Double>();
	private boolean displayFull = true;

	public JFrame getFrame(){
		return frame;
	}
	
	public ChartFrame(){
		init();
	}
	
	private void init() {
		//data = factory.create();

		final OHLCSeriesCollection dataset = new OHLCSeriesCollection();
		series = new OHLCSeries("test");
		buySeries = new OHLCSeries("buy");
		sellSeries = new OHLCSeries("sell");
		// series.setMaximumItemCount(windowSize);
		dataset.addSeries(series);
		dataset.addSeries(buySeries);
		dataset.addSeries(sellSeries);
		// dataset = createDataset();
		createChart(dataset);
		// Thread t = new ApplicationThread(this);

		final ChartPanel chartPanel = new ChartPanel(chart);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		double scale = 0.9;
		int width = (int) (dim.getWidth() * scale);
		int height = (int) (dim.getHeight() * scale);
		chartPanel.setPreferredSize(new Dimension(width, height));
		frame.setContentPane(chartPanel);

		// t.start();
	}

	public void display() {
		frame.pack();
		RefineryUtilities.centerFrameOnScreen(frame);
		frame.setVisible(true);
	}

	private void createChart(final OHLCDataset dataset) {

		// create the chart...
		chart = ChartFactory.createXYLineChart(null, // chart
														// title
				"time", // x axis label
				"price", // y axis label
				dataset, // data
				PlotOrientation.VERTICAL, false, false, false// tooltips,
				);
		XYPlot plot = chart.getXYPlot();
		plot.setBackgroundPaint(Color.WHITE);
		plot.setDomainGridlinePaint(Color.BLACK);
		plot.setRangeGridlinePaint(Color.BLACK);

		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		// renderer.setSeriesLinesVisible(0, false);
		renderer.setSeriesShapesVisible(0, false);
		renderer.setSeriesLinesVisible(1, false);
		renderer.setSeriesLinesVisible(2, false);
		plot.setRenderer(renderer);
		chart.getXYPlot().getRenderer().setSeriesPaint(1, Color.GREEN);
		chart.getXYPlot().getRenderer().setSeriesPaint(2, Color.RED);

	}

	private void add(OHLCSeries series, Quote quote) {
		series.add(new FixedMillisecond(index), quote.getOpen(),
				quote.getHigh(), quote.getLow(), quote.getClose());
	}

	private void clear() {
		series.clear();
		buySeries.clear();
		sellSeries.clear();
	}

	public void setSide(Side side) {
		XYItemRenderer renderer = chart.getXYPlot().getRenderer();
		if (side == null)
			renderer.setSeriesPaint(0, Color.BLUE);
		else
			renderer.setSeriesPaint(0, side == Side.LONG ? Color.GREEN
					: Color.RED);
	}

	@Override
	public String reset() {
		//displayFull = false;
		setSide(null);
		series.setMaximumItemCount(windowSize);
		values.clear();	
		clear();
		String ret = super.reset();		
		series.setKey(ret);		
		return ret;
	}

	@Override
	public boolean update() {
		boolean ret = super.update();
		if(ret){
			add(series, quote);
			
			ValueAxis axis = chart.getXYPlot().getRangeAxis();

			double close = quote.getClose();
			values.add(close);
			if (values.size() > series.getMaximumItemCount()) {
				values.remove(0);
			}

			if (displayFull) {
				axis.setRange(Collections.min(values), Collections.max(values));
			} else {
				double change = 0.3;
				axis.setRange((1 - change) * close, (1 + change) * close);
			}
		}
		return ret;
	}


	public void display(Result result) {
		//displayFull = true;
		series.setMaximumItemCount(windowSize + quoteCount);
		buySeries.setMaximumItemCount(series.getMaximumItemCount());
		sellSeries.setMaximumItemCount(series.getMaximumItemCount());
		clear();
		index = 0;
		setSide(null);
		Iterator<Action> it = result.getActions().iterator();
		Action action = null;
		if (it.hasNext()) {
			action = it.next();
		}
		while (update()) {
			if (action != null && quote.getDate().equals(action.getQuoteDate())) {
				if (action.getSide() == Side.SHORT) {
					add(sellSeries, quote);
				} else {
					add(buySeries, quote);
				}
				if (it.hasNext())
					action = it.next();
				else
					action = null;
			}
		}
	}
}
