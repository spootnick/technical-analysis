package spootnick.runtime;

import java.util.List;
import java.util.Random;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

	private List<QuoteSeries> data;
	protected QuoteSeries quoteSeries;
	//private ArrayList<Double> values = new ArrayList<Double>();
	//protected Quote quote;
	private int start;
	private int index;
	private Random random = new Random();
	
	public int getStart(){
		return start;
	}
	
	public int getIndex(){
		return index;
	}
	
	public int getWindowSize() {
		return windowSize;
	}

	public int getQuoteCount() {
		return quoteCount;
	}
	
	@Resource(name="quoteSeriesFactory")
	public void setData(List<QuoteSeries> data){
		this.data = data;
	}
	
	private boolean finished(){
		return index >= quoteCount + windowSize;
	}
	
	public String reset() {

		int dataSize = data.size();
		quoteSeries = data.get(random.nextInt(dataSize));

		//losowanie odpowiednio d³ugich danych
		while (quoteSeries.getData().size() < quoteCount + windowSize) {
			quoteSeries = data.get(random.nextInt(dataSize));
		}

		//losowanie punktu startowego na konkretnym wykresie
		start = random.nextInt(quoteSeries.getData().size() - quoteCount
				- windowSize);


		String name = quoteSeries.getName();

		index = windowSize - 1;
		afterReset(name);
		return name;
	}

	protected void afterReset(String name){
		
	}
	
	public boolean update() {
		index++;
		if (finished()) {
			// quoteSeries = null;
			return false;
		}
		afterUpdate();
		return true;
	}

	protected void afterUpdate(){
		
	}
	
	public Quote getQuote() {
		return getQuote(0);
	}
	
	public Quote getQuote(int past){
		if(finished())
			throw new IllegalStateException("finished");
		else if(past > windowSize - 1)
			throw new IllegalArgumentException("past: "+past+", windowSize: "+windowSize);
	
		int i = start + index - past;
		return quoteSeries.getData().get(i);
	}
}
