package spootnick.result;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spootnick.data.Quote;
import spootnick.result.Action.Side;

public class ResultBuilder {

	private static final double INITIAL_MONEY = 100;

	private Logger log = LoggerFactory.getLogger(ResultBuilder.class);

	private double money;
	private double quantity;
	private double startPrice;
	private Side side;
	private Result result;

	public ResultBuilder(String symbol, String name,int windowSize,
			int quoteCount){
		result = new Result();
		result.setSymbol(symbol);
		result.setWindowSize(windowSize);
		result.setQuoteCount(quoteCount);
		result.setExecutionDate(new Date());
		
		result.setName(name);
	}
	
	public void start(Quote quote, final Side side) {
		startPrice = quote.getClose();
		this.side = side;
		if (side == Side.LONG) {
			quantity = INITIAL_MONEY / startPrice;
			money = 0;
		} else {
			quantity = 0;
			money = INITIAL_MONEY;
		}


		Action action = new Action();
		action.setQuoteDate(quote.getDate());
		action.setSide(side);
		action.setResult(result);
		result.getActions().add(action);

		if (log.isDebugEnabled()) {
			log.debug("start, date: " + quote.getDate() + ", startPrice: "
					+ startPrice + ", quantity: " + quantity);
		}
		//return side;
	}

	public void update(Quote quote, final Side side) {
		if (this.side == side)
			return;
		double price = quote.getClose();
		if (side == Side.LONG) {
			// buy
			quantity = money / price;
			money = 0;
		} else {
			money = quantity * price;
			quantity = 0;
		}
		Action action = new Action();
		action.setQuoteDate(quote.getDate());
		action.setSide(side);
		action.setResult(result);
		result.getActions().add(action);
		this.side = side;
		if (log.isDebugEnabled()) {
			log.debug("update, date: " + quote.getDate() + ", price: " + price
					+ ", money: " + money + ", quantity: " + quantity);
		}
		//return side;
	}

	public Result stop(Quote quote) {

		result.setPriceChange(quote.getClose() / startPrice - 1);
		if (side == Side.LONG) {
			money = quantity * quote.getClose();
		}
		result.setChange(money / INITIAL_MONEY - 1);
		log.debug("stop, date: " + quote.getDate() + ", price: "
				+ quote.getClose());
		return result;
	}
}
