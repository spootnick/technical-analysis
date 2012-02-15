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

	public enum State {
		BEGIN, NOT_STARTED, START, STARTED
	}

	private Logger log = LoggerFactory.getLogger(Simulation.class);

	// @Autowired
	// protected QuoteSeriesFactory factory;
	@Value("${windowSize}")
	protected int windowSize;
	@Value("${quoteCount}")
	protected int quoteCount;

	private List<QuoteSeries> data;
	private QuoteSeries quoteSeries;
	// private ArrayList<Double> values = new ArrayList<Double>();
	// protected Quote quote;
	private int begin;
	private int start;
	private int current;
	private Random random = new Random();
	private boolean used;

	private void checkUsed() {
		if (used)
			throw new IllegalStateException("instance already used");
	}

	public QuoteSeries getQuoteSeries() {
		return quoteSeries;
	}

	public QuoteSeries getQuoteSeries(String name) {
		for (QuoteSeries qs : data) {
			if (name.equals(qs.getName()))
				return qs;
		}
		return null;
	}

	public int getBegin() {
		return begin;
	}

	public int getCurrent() {
		return current;
	}

	public int getStart() {
		return start;
	}

	public State getState() {
		State ret;
		int diff = current - start;
		if(current == begin)
			ret = State.BEGIN;
		else if(diff == 0)
			ret = State.START;
		else if(diff < 0)
			ret = State.NOT_STARTED;
		else
			ret = State.STARTED;
		return ret;
	}

	public int getEnd() {
		return begin + windowSize + quoteCount;
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
		return current >= begin + quoteCount + windowSize;
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
			if (currentTry == maxTry)
				throw new RuntimeException(length + " long quoteSeries not found after " + currentTry + " tries");
			quoteSeries = data.get(random.nextInt(dataSize));
		}

		// losowanie punktu startowego na konkretnym wykresie
		begin = random.nextInt(quoteSeries.getLength() - quoteCount - windowSize + 1);

		String name = quoteSeries.getName();

		current = begin - 1;

		start = begin + windowSize - 1;
		afterReset(name);

		if (log.isDebugEnabled()) {
			log.debug("reset, name: {}, windowSize: {}, quoteCount: {}, begin: {}, current: {}, start: {}", new Object[] { name, windowSize, quoteCount, begin, current, start });
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
