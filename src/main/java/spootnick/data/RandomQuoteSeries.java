package spootnick.data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RandomQuoteSeries /*implements QuoteSeries*/ {

	private static final int SIZE = 2000;

	private Logger log = LoggerFactory.getLogger(RandomQuoteSeries.class);

	private Random random = new Random();
	//private List<Quote> data = new ArrayList<Quote>(SIZE);

	public RandomQuoteSeries() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(0);
		double price = random.nextInt(1000) + 1000;
		for (int i = 0; i < SIZE; ++i) {
			Date date = calendar.getTime();
			//data.add(new Quote(date, 0, 0, 0, price, 0));
			log.trace("price: {}, date: {} added", price, date);
			calendar.add(Calendar.DAY_OF_YEAR, 1);
			int n = 99;
			price += price * (random.nextInt(n) - n / 2) / 1000d;
		}
	}

	//@Override
	public String getName() {
		return "WIG-R";
	}

	//@Override
	//public List<Quote> getData() {
	//	return data;
	//}

}
