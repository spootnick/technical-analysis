package spootnick.runtime;

import java.util.List;
import java.util.Random;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import spootnick.data.Quote;
import spootnick.data.QuoteSeries;
import spootnick.data.QuoteSeriesFactory;

//@Component
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
	//private int index;
	private int current;
	private Random random = new Random();
	private boolean used;

	private void checkUsed(){
		if(used)
			throw new IllegalStateException("instance already used");
	}
	
	public QuoteSeries getQuoteSeries() {
		return quoteSeries;
	}

	public int getStart() {
		return start;
	}

	public int getCurrent() {
		return current;
	}
	
	public int getStop(){
		return start + windowSize + quoteCount;
	}
	
	public void setWindowSize(int windowSize) {
		checkUsed();
		this.windowSize = windowSize;
	}

	public int getWindowSize() {
		return windowSize;
	}

	public void setQuoteCount(int quoteCount) {
		checkUsed();
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
		return current >= start + quoteCount + windowSize;
	}

	public String reset() {

		used = true;
		int dataSize = data.size();
		quoteSeries = data.get(random.nextInt(dataSize));

		int currentTry = 0;
		int maxTry = 10;
		
		// losowanie odpowiednio d³ugich danych
		int length = quoteCount + windowSize;
		while (quoteSeries.getLength() < length) {
			if(currentTry == maxTry)
				throw new RuntimeException(length+" long quoteSeries not found after "+currentTry+" tries");
			quoteSeries = data.get(random.nextInt(dataSize));
		}

		// losowanie punktu startowego na konkretnym wykresie
		start = random.nextInt(quoteSeries.getLength() - quoteCount
				- windowSize + 1);

		String name = quoteSeries.getName();

		current = start + windowSize - 1;
		afterReset(name);

		if (log.isDebugEnabled()) {
			log.debug(
					"reset, name: {}, windowSize: {}, quoteCount: {}, start: {}, current: {}",
					new Object[] { name, windowSize, quoteCount, start, current });
		}

		return name;
	}

	protected void afterReset(String name) {

	}

	public boolean update() {
		current++;
		if (finished()) {
			--current;
			return false;
		}
		afterUpdate(getQuote());
		return true;
	}

	protected void afterUpdate(Quote quote) {

	}

	public Quote getQuote() {
		return quoteSeries.getQuote(current);
	}
}
