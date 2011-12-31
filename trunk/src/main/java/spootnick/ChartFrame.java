package spootnick;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import javax.annotation.PreDestroy;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.ohlc.OHLCSeries;
import org.jfree.data.time.ohlc.OHLCSeriesCollection;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.springframework.stereotype.Component;

import spootnick.data.Quote;
import spootnick.result.Action;
import spootnick.result.Action.Side;
import spootnick.result.Result;
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
	private boolean displayed;

	public JFrame getFrame() {
		return frame;
	}

	public ChartFrame() {
		init();
	}

	@PreDestroy
	public void dispose() throws InterruptedException,
			InvocationTargetException {
		SwingUtilities.invokeAndWait(new Runnable() {

			@Override
			public void run() {
				frame.dispose();

			}
		});

	}

	private void init() {
		// data = factory.create();

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
		if (!displayed) {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {

					@Override
					public void run() {
						frame.pack();
						RefineryUtilities.centerFrameOnScreen(frame);
						frame.setVisible(true);
						displayed = true;

					}
				});
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

		}
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

	private void add(OHLCSeries series, Quote quote, int index) {
		double close = quote.getClose();
		series.add(new FixedMillisecond(index), quote.getOpen(),
				quote.getHigh(), quote.getLow(), close);

		ValueAxis axis = chart.getXYPlot().getRangeAxis();
		values.add(close);
		if (values.size() > series.getMaximumItemCount()) {
			values.remove(0);
		}

		axis.setRange(Collections.min(values), Collections.max(values));
	}

	private void clear() {
		series.clear();
		buySeries.clear();
		sellSeries.clear();
	}

	public void setSide(final Side side) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {

				@Override
				public void run() {
					XYItemRenderer renderer = chart.getXYPlot().getRenderer();
					if (side == null)
						renderer.setSeriesPaint(0, Color.BLUE);
					else
						renderer.setSeriesPaint(0,
								side == Side.LONG ? Color.GREEN : Color.RED);

				}
			});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void afterReset(final String name) {
		setSide(null);
		try {
			SwingUtilities.invokeAndWait(new Runnable() {

				@Override
				public void run() {

					series.setMaximumItemCount(windowSize);
					values.clear();
					clear();
					series.setKey(name);
					//int index = 0;
					for (int i = 0 ; i < getWindowSize() ; ++i) {
						add(series, quoteSeries.getQuote(getStart()+i), i);
					}

				}
			});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	protected void afterUpdate(final Quote quote) {

		final int index = getIndex();
		
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				add(series, quote,index);
			}
		});

	}

	public void display(final Result result) {
		display();
		setSide(null);
		try {
			SwingUtilities.invokeAndWait(new Runnable() {

				@Override
				public void run() {

					series.setMaximumItemCount(windowSize + quoteCount);
					buySeries.setMaximumItemCount(series.getMaximumItemCount());
					sellSeries.setMaximumItemCount(series.getMaximumItemCount());
					clear();
					Iterator<Action> it = result.getActions().iterator();
					Action action = null;
					if (it.hasNext()) {
						action = it.next();
					}
					for (int i = 0; i < getWindowSize() + getQuoteCount(); ++i) {
						Quote quote = quoteSeries.getQuote(getStart() + i);
						add(series, quote, i);
						if (action != null
								&& quote.getDate()
										.equals(action.getQuoteDate())) {
							if (action.getSide() == Side.SHORT) {
								add(sellSeries, quote, i);
							} else {
								add(buySeries, quote, i);
							}
							if (it.hasNext())
								action = it.next();
							else
								action = null;
						}
					}

				}
			});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
}