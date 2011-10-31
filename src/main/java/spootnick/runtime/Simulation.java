package spootnick.runtime;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import spootnick.data.Quote;
import spootnick.data.QuoteSeries;
import spootnick.data.QuoteSeriesFactory;

public class Simulation {

	//@Autowired
	//protected QuoteSeriesFactory factory;
	@Value("${windowSize}")
	protected int windowSize;
	@Value("${quoteCount}")
	protected int quoteCount;

	protected List<QuoteSeries> data;
	protected QuoteSeries quoteSeries;
	//private ArrayList<Double> values = new ArrayList<Double>();
	protected Quote quote;
	protected int start;
	protected int index;
	protected Random random = new Random();
	protected boolean displayFull = true;
	
	public int getWindowSize() {
		return windowSize;
	}

	public int getQuoteCount() {
		return quoteCount;
	}
	
	@Autowired
	public void setData(QuoteSeriesFactory factory){
		this.data = factory.create();
	}
	
	public String reset() {
		//displayFull = false;
		//setSide(null);
		//series.setMaximumItemCount(windowSize);

		int dataSize = data.size();
		quoteSeries = data.get(random.nextInt(dataSize));

		while (quoteSeries.getData().size() < quoteCount + windowSize) {
			quoteSeries = data.get(random.nextInt(dataSize));
		}

		start = random.nextInt(quoteSeries.getData().size() - quoteCount
				- windowSize);

		//clear();
		String name = quoteSeries.getName();
		//series.setKey(name);
		index = 0;
		//values.clear();
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

		//add(series, quote);

		//ValueAxis axis = chart.getXYPlot().getRangeAxis();

		// series.setKey(index);
		double close = quote.getClose();
		//values.add(close);
		//if (values.size() > series.getMaximumItemCount()) {
		//	values.remove(0);
		//}

		//if (displayFull) {
		//	axis.setRange(Collections.min(values), Collections.max(values));
		//} else {
		//	double change = 0.3;
		//	axis.setRange((1 - change) * close, (1 + change) * close);
		//}
		index++;
		return true;
	}

	public Quote getQuote() {
		return quote;
	}
}
