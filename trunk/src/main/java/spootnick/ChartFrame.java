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

//import org.jfree.ui.Spacer;

@Component
public class ChartFrame extends ApplicationFrame {

	@Autowired
	private QuoteSeriesFactory factory;
	@Autowired
	@Value("${windowSize}")
	private int windowSize;
	@Autowired
	@Value("${quoteCount}")
	private int quoteCount;

	private OHLCSeries series;
	private OHLCSeries buySeries;
	private OHLCSeries sellSeries;
	private JFreeChart chart;
	private List<QuoteSeries> data;
	private QuoteSeries quoteSeries;
	private ArrayList<Double> values = new ArrayList<Double>();
	private Quote quote;
	private int start;
	private int index;
	private Random random = new Random();

	int getWindowSize() {
		return windowSize;
	}

	public int getQuoteCount() {
		return quoteCount;
	}

	/**
	 * Creates a new demo.
	 * 
	 * @param title
	 *            the frame title.
	 */
	public ChartFrame() {

		super("Quote Player");

	}

	public void init() {
		data = factory.create();

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
		setContentPane(chartPanel);

		pack();
		RefineryUtilities.centerFrameOnScreen(this);
		setVisible(true);

		// t.start();
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

	public String reset() {
		setSide(null);
		series.setMaximumItemCount(windowSize);

		int dataSize = data.size();
		quoteSeries = data.get(random.nextInt(dataSize));

		while (quoteSeries.getData().size() < quoteCount + windowSize) {
			quoteSeries = data.get(random.nextInt(dataSize));
		}

		start = random.nextInt(quoteSeries.getData().size() - quoteCount
				- windowSize);

		clear();
		String name = quoteSeries.getName();
		series.setKey(name);
		index = 0;
		values.clear();
		for (int i = 0; i < windowSize; ++i) {
			update();
		}
		return name;
	}

	public boolean update() {
		if (index >= quoteCount + windowSize) {
			// quoteSeries = null;
			return false;
		}
		int i = index + start;
		quote = quoteSeries.getData().get(i);
		/*
		 * if(i > 0){ Quote last = quoteSeries.getData().get(i-1); double change
		 * = (quote.getClose()/last.getClose() - 1) * 100;
		 * chart.setTitle(Double.toString(change)); }
		 */
		add(series, quote);
		// series.add(new Day(date), close);

		ValueAxis axis = chart.getXYPlot().getRangeAxis();

		// series.setKey(index);
		values.add(quote.getClose());
		if (values.size() > series.getMaximumItemCount()) {
			values.remove(0);
		}

		axis.setRange(Collections.min(values), Collections.max(values));
		index++;
		return true;
	}

	public Quote getQuote() {
		return quote;
	}

	public void display(Result result) {
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
