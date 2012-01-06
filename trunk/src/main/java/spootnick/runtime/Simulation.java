package spootnick.runtime;

import java.util.List;
import java.util.Random;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import spootnick.data.Quote;
import spootnick.data.QuoteSeries;
import spootnick.data.QuoteSeriesFactory;

public class Simulation {

	private Logger log = LoggerFactory.getLogger(Simulation.class);

	// @Autowired
	// protected QuoteSeriesFactory factory;
	@Value("${windowSize}")
	protected int windowSize;
	@Value("${quoteCount}")
	protected int quoteCount;

	private List<QuoteSeries> data;
	protected QuoteSeries quoteSeries;
	// private ArrayList<Double> values = new ArrayList<Double>();
	// protected Quote quote;
	private int start;
	private int index;
	private Random random = new Random();

	public QuoteSeries getQuoteSeries() {
		return quoteSeries;
	}

	public int getStart() {
		return start;
	}

	public int getIndex() {
		return index;
	}

	public void setWindowSize(int windowSize) {
		if (this.windowSize != 0)
			throw new IllegalStateException("already set: " + this.windowSize);
		this.windowSize = windowSize;
	}

	public int getWindowSize() {
		return windowSize;
	}

	public void setQuoteCount(int quoteCount) {
		if (this.quoteCount != 0)
			throw new IllegalStateException("already set: " + this.quoteCount);
		this.quoteCount = quoteCount;
	}

	public int getQuoteCount() {
		return quoteCount;
	}

	@Resource(name = "quoteSeriesFactory")
	public void setData(List<QuoteSeries> data) {
		this.data = data;
	}

	private boolean finished() {
		return index >= quoteCount + windowSize;
	}

	public String reset() {

		int dataSize = data.size();
		quoteSeries = data.get(random.nextInt(dataSize));

		// losowanie odpowiednio d³ugich danych
		while (quoteSeries.getLength() < quoteCount + windowSize) {
			quoteSeries = data.get(random.nextInt(dataSize));
		}

		// losowanie punktu startowego na konkretnym wykresie
		start = random.nextInt(quoteSeries.getLength() - quoteCount
				- windowSize + 1);

		String name = quoteSeries.getName();

		index = windowSize - 1;
		afterReset(name);

		if (log.isDebugEnabled()) {
			log.debug(
					"reset, name: {}, windowSize: {}, quoteCount: {}, start: {}, index: {}",
					new Object[] { name, windowSize, quoteCount, start, index });
		}

		return name;
	}

	protected void afterReset(String name) {

	}

	public boolean update() {
		index++;
		if (finished()) {
			--index;
			return false;
		}
		afterUpdate(getQuote());
		return true;
	}

	protected void afterUpdate(Quote quote) {

	}

	public Quote getQuote() {
		return quoteSeries.getQuote(start + index);
	}
}
