package spootnick.main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private Logger log = LoggerFactory.getLogger(ChartFrame.class);

	private int PRICE = 0;
	private int BUY = 1;
	private int SELL = 2;
	private int HIGH = 3;
	private int LOW = 4;

	private ApplicationFrame frame = new ApplicationFrame("Quote Player");

	private OHLCSeries[] series;

	private JFreeChart chart;
	private List<Double> values = new ArrayList<Double>();
	private boolean displayed;
	private List<Double> lows = new ArrayList<Double>();
	private List<Double> highs = new ArrayList<Double>();
	private String title;

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
		renderer.setSeriesPaint(BUY, Color.GREEN);
		renderer.setSeriesPaint(SELL, Color.RED);
		renderer.setSeriesPaint(HIGH, Color.GREEN);
		renderer.setSeriesPaint(LOW, Color.RED);
		renderer.setSeriesPaint(PRICE, Color.BLUE);
		plot.setRenderer(renderer);

	}

	private void add(OHLCSeries series, Quote quote, int index, boolean updateRange) {
		double close = quote.getClose();
		series.add(new FixedMillisecond(index), quote.getOpen(), quote.getHigh(), quote.getLow(), close);
		// series.add(new FixedMillisecond(index), 0, 0, 0, close);

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
		chart.setTitle(title);
	}

	public void setTitle(final String title) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				ChartFrame.this.title = title;
				chart.setTitle(title);
			}
		});
	}

	public void setSide(final Side side) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {

				@Override
				public void run() {
					XYItemRenderer renderer = chart.getXYPlot().getRenderer();
					if (side == null || side == Side.SHORT)
						// renderer.setSeriesPaint(PRICE, Color.BLUE);
						renderer.setSeriesStroke(PRICE, new BasicStroke(1));
					else
						// renderer.setSeriesPaint(PRICE, side == Side.LONG ?
						// Color.GREEN : Color.RED);
						renderer.setSeriesStroke(PRICE, new BasicStroke(2));

				}
			});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void setWindow(final int size) {

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {

				setMaximumItemCount(size);
				Quote quote = new Quote();
				int current = getCurrent();
				int begin = getBegin();
				double[] close = getQuoteSeries().getClose();
				for (int i = current - size + 1; i <= current; ++i) {
					int index = i - begin;
					quote.setClose(close[i]);
					add(series[PRICE], quote, index, true);
					if (!lows.isEmpty())
						internalAddLowHigh(lows.get(index), highs.get(index), index);
				}

			}
		});

	}

	private void setMaximumItemCount(int count) {
		for (OHLCSeries s : series)
			s.setMaximumItemCount(count);
		values.clear();
		clear();
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

					setMaximumItemCount(getWindowSize());
					series[PRICE].setKey(name);
					lows.clear();
					highs.clear();
					// int index = 0;
					// for (int i = 0; i < getWindowSize(); ++i) {
					// add(series[PRICE], getQuoteSeries().getQuote(getBegin() +
					// i), i, true);
					// }
					// add(series[PRICE], getQuoteSeries().getQuote(getBegin()
					// ), 0, true);

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
		final int index = getCurrent() - getBegin();

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				add(series[PRICE], quote, index, true);
			}
		});

	}

	public void addLowHigh(final double low, final double high, final int index) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				ChartFrame.this.lows.add(index, low);
				ChartFrame.this.highs.add(index, high);

				internalAddLowHigh(low, high, index);
			}
		});

	}

	private void internalAddLowHigh(double low, double high, int index) {
		if (Move.notBoundary(high))
			add(series[HIGH], new Quote(null, high, high, high, high, high), index, false);
		if (Move.notBoundary(low))
			add(series[LOW], new Quote(null, low, low, low, low, low), index, false);
	}

	public void display(final Result result) {
		display();
		setSide(null);
		try {
			SwingUtilities.invokeAndWait(new Runnable() {

				@Override
				public void run() {

					clear();

					for (OHLCSeries s : series)
						s.setMaximumItemCount(result.getWindowSize() + result.getQuoteCount());

					QuoteSeries qs = getQuoteSeries(result.getSymbol());

					if (qs == null)
						throw new RuntimeException(result.getSymbol() + " not found");

					int start;
					for (start = 0; start < qs.getLength(); ++start)
						if (qs.getDate()[start].equals(result.getQuoteDate()))
							break;

					int openChangeIndex = start + result.getWindowSize() - 1;
					int closeChangeIndex = openChangeIndex + result.getQuoteCount();

					log.debug("start: {}, openChangeIndex: {}, closeChangeIndex: {}", new Object[] { start, openChangeIndex, closeChangeIndex });

					double priceChange = qs.getClose()[closeChangeIndex] / qs.getClose()[openChangeIndex] - 1;
					double resultPriceChange = result.getPriceChange();
					if (priceChange != resultPriceChange)
						throw new RuntimeException("priceChange: " + priceChange + ", resultPriceChange: " + resultPriceChange + ", probably bug");

					chart.setTitle(result.toString(true));

					Iterator<Position> it = result.getPositions().iterator();
					Position position = null;
					if (it.hasNext()) {
						position = it.next();
					}

					for (int i = 0; i < result.getWindowSize() + result.getQuoteCount(); ++i) {
						Quote quote = qs.getQuote(start + i);
						add(series[PRICE], quote, i, true);

						double[] values = result.getHigh();
						double high = values != null ? values[i] : Move.OVER;
						values = result.getLow();
						double low = values != null ? values[i] : Move.OVER;
						addLowHigh(low, high, i);

						if (position != null) {
							Date date = quote.getDate();
							if (date.equals(position.getOpenDate())) {
								add(series[BUY], quote, i, false);
							} else if (date.equals(position.getCloseDate())) {
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
