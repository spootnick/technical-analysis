package spootnick.result;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import spootnick.data.Quote;

@Component
public class ResultBuilder {

	public enum Side {
		LONG, SHORT
	};

	private static final double INITIAL_MONEY = 100;

	private Logger log = LoggerFactory.getLogger(ResultBuilder.class);

	private double money;
	private double quantity;
	private double startPrice;
	private Side side;
	private Result result;
	

	public Side start(Quote quote,String symbol,int windowSize, int quoteCount) {
		startPrice = quote.getClose();
		quantity = INITIAL_MONEY / startPrice;
		money = 0;
		side = Side.LONG;

		result = new Result();
		result.setSymbol(symbol);
		result.setWindowSize(windowSize);
		result.setQuoteCount(quoteCount);
		result.setCreateTime(new Date());
		result.setFirstQuote(quote.getDate());
		
		if(log.isDebugEnabled()){
			log.debug("start, date: "+quote.getDate()+", startPrice: "+startPrice+", quantity: "+quantity);
		}
		return side;
	}

	public Side update(Quote quote,Side side) {
		if (this.side == side)
			return side;
		double price = quote.getClose();
		if (side == Side.LONG) {
			// buy
			quantity = money / price;
			money = 0;
		} else {
			money = quantity * price;
			quantity = 0;
		}
		this.side = side;
		if(log.isDebugEnabled()){
			log.debug("update, date: "+quote.getDate()+", price: "+price+", money: "+money+", quantity: "+quantity);
		}
		return side;
	}

	public Result stop(Quote quote) {

		result.setPriceChange(quote.getClose() / startPrice - 1);
		if (side == Side.LONG) {
			money = quantity * quote.getClose();
		}
		result.setChange(money / INITIAL_MONEY - 1);
		log.debug("stop, date: "+quote.getDate()+", price: "+quote.getClose());
		return result;
	}
}
