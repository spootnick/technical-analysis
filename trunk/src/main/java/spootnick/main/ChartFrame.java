package spootnick.main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
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
import spootnick.data.QuoteSeries;
import spootnick.result.Position;
import spootnick.result.Position.Side;
import spootnick.result.Result;
import spootnick.runtime.Simulation;
import spootnick.runtime.TradingRule.Move;

//import org.jfree.ui.Spacer;

@Component
public class ChartFrame extends Simulation {

	private int PRICE = 0;
	private int BUY = 1;
	private int SELL = 2;
	private int HIGH = 3;
	private int LOW = 4;

	private ApplicationFrame frame = new ApplicationFrame("Quote Player");

	private OHLCSeries[] series;

	// private OHLCSeries series;
	// private OHLCSeries buySeries;
	// private OHLCSeries sellSeries;
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
	public void dispose() throws InterruptedException, InvocationTargetException {
		SwingUtilities.invokeAndWait(new Runnable() {

			@Override
			public void run() {
				frame.dispose();

			}
		});

	}

	private void init() {
		// data = factory.create();

		series = new OHLCSeries[5];
		final OHLCSeriesCollection dataset = new OHLCSeriesCollection();
		series[PRICE] = new OHLCSeries("test");
		series[BUY] = new OHLCSeries("buy");
		series[SELL] = new OHLCSeries("sell");
		series[HIGH] = new OHLCSeries("high");
		series[LOW] = new OHLCSeries("low");
		// series.setMaximumItemCount(windowSize);
		for (OHLCSeries s : series)
			dataset.addSeries(s);

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
			displayed = true;
			try {
				SwingUtilities.invokeAndWait(new Runnable() {

					@Override
					public void run() {
						frame.pack();
						RefineryUtilities.centerFrameOnScreen(frame);
						frame.setVisible(true);

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
		renderer.setSeriesShapesVisible(PRICE, false);
		renderer.setSeriesLinesVisible(BUY, false);
		renderer.setSeriesLinesVisible(SELL, false);
		renderer.setSeriesShapesVisible(HIGH, false);
		renderer.setSeriesShapesVisible(LOW, false);
		plot.setRenderer(renderer);
		chart.getXYPlot().getRenderer().setSeriesPaint(BUY, Color.GREEN);
		chart.getXYPlot().getRenderer().setSeriesPaint(SELL, Color.RED);
		chart.getXYPlot().getRenderer().setSeriesPaint(HIGH, Color.GREEN);
		chart.getXYPlot().getRenderer().setSeriesPaint(LOW, Color.RED);

	}

	private void add(OHLCSeries series, Quote quote, int index, boolean updateRange) {
		double close = quote.getClose();
		series.add(new FixedMillisecond(index), quote.getOpen(), quote.getHigh(), quote.getLow(), close);

		if (updateRange) {
			ValueAxis axis = chart.getXYPlot().getRangeAxis();
			values.add(close);
			if (values.size() > series.getMaximumItemCount()) {
				values.remove(0);
			}

			axis.setRange(Collections.min(values), Collections.max(values));
		}
	}

	private void clear() {
		for (OHLCSeries s : series)
			s.clear();
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
						renderer.setSeriesPaint(0, side == Side.LONG ? Color.GREEN : Color.RED);

				}
			});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void afterReset(final String name) {
		if (!displayed)
			return;
		setSide(null);
		try {
			SwingUtilities.invokeAndWait(new Runnable() {

				@Override
				public void run() {

					series[PRICE].setMaximumItemCount(windowSize);
					values.clear();
					clear();
					series[PRICE].setKey(name);
					// int index = 0;
					for (int i = 0; i < getWindowSize(); ++i) {
						add(series[PRICE], getQuoteSeries().getQuote(getStart() + i), i, true);
					}

				}
			});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	protected void afterUpdate(final Quote quote) {
		if (!displayed)
			return;
		final int index = getCurrent() - getStart();

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				add(series[PRICE], quote, index, true);
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

					for (OHLCSeries s : series)
						s.setMaximumItemCount(result.getWindowSize() + result.getQuoteCount());

					QuoteSeries qs = getQuoteSeries(result.getSymbol());
							
					if(qs == null)
						throw new RuntimeException(result.getSymbol()+" not found");
					
					
					int start = -1;
					for(int i = 0; i < qs.getLength() ; ++i)
						if(qs.getDate()[i].equals(result.getQuoteDate())){
							start = i-result.getWindowSize()+1;
							break;
						}
							
					if(start < 0){
						throw new RuntimeException("start not found");
					}
				
					
					clear();
					Iterator<Position> it = result.getPositions().iterator();
					Position position = null;
					if (it.hasNext()) {
						position = it.next();
					}
					for (int i = 0; i < result.getWindowSize() + result.getQuoteCount(); ++i) {
						Quote quote = qs.getQuote(start + i);
						add(series[PRICE], quote, i, true);

						double[] values = result.getHigh();
						double value = values != null ? values[i] : Move.OVER;
						if (Move.notBoundary(value))
							add(series[HIGH], new Quote(null, value, value, value, value, value), i, false);
						values = result.getLow();
						value = values != null ? values[i] : Move.OVER;
						if (Move.notBoundary(value))
							add(series[LOW], new Quote(null, value, value, value, value, value), i, false);

						if (position != null){
							Date date = quote.getDate();
							if(date.equals(position.getOpenDate())){
								add(series[BUY], quote, i, false);
							}else if(date.equals(position.getCloseDate())){
								add(series[SELL], quote, i, false);
								position = it.hasNext() ? it.next() : null;
							}
						}
							
							
					}

				}
			});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
}
