package spootnick.result;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spootnick.data.Quote;
import spootnick.result.Position.Side;
import spootnick.runtime.Simulation;
import spootnick.runtime.TradingRule.Move;

public class ResultBuilder {

	private Logger log = LoggerFactory.getLogger(ResultBuilder.class);

	private double startPrice;
	private double buyPrice;
	private double change = 1;
	private Side side;
	private Result result;
	private Simulation simulation;
	private Position position;

	public ResultBuilder(Simulation simulation, String symbol, String name) {

		this.simulation = simulation;

		result = new Result(simulation.getWindowSize(), simulation.getQuoteCount());
		result.setSymbol(symbol);
		result.setExecutionDate(new Date());
		result.setQuoteDate(simulation.getQuote().getDate());

		result.setName(name);
	}

	private void updateLowHigh(Move move) {
		double low = move.getLow();
		double high = move.getHigh();
		if (Move.notBoundary(low))
			result.getLow()[simulation.getCurrent() - simulation.getStart()] = low;
		if (Move.notBoundary(high))
			result.getHigh()[simulation.getCurrent() - simulation.getStart()] = high;
	}

	public void start(Move move) {
		updateLowHigh(move);
		Quote quote = simulation.getQuote();
		startPrice = quote.getClose();
		side = move.getSide(simulation);
		if (side == null) {
			side = Side.LONG;
			log.debug("using default start side: {}", side);
		}
		if (side == Side.LONG) {
			openPosition(quote);
		}

		if (log.isDebugEnabled()) {
			log.debug("start, date: " + quote.getDate() + ", startPrice: " + startPrice + ", side: " + side);
		}
		// return side;
	}

	private void openPosition(Quote quote) {
		position = new Position();
		position.setOpenDate(quote.getDate());
		buyPrice = quote.getClose();
	}

	private void closePosition(Quote quote) {
		double positionChange = quote.getClose() / buyPrice;
		change = change * positionChange;
		position.setCloseDate(quote.getDate());
		position.setChange(positionChange - 1);
		result.getPositions().add(position);
		position = null;
		
	}

	public void update(Move move) {
		updateLowHigh(move);
		Side side = move.getSide(simulation);
		Quote quote = simulation.getQuote();
		if (side == null || this.side == side)
			return;
		double price = quote.getClose();
		if (side == Side.LONG) {
			// buy
			openPosition(quote);
		} else {
			closePosition(quote);

		}
		this.side = side;
		if (log.isDebugEnabled()) {
			log.debug("update, date: " + quote.getDate() + ", price: " + price + ", side: " + side + ", change: " + change);
		}
		// return side;
	}

	public Result stop() {

		Quote quote = simulation.getQuote();

		result.setPriceChange(quote.getClose() / startPrice - 1);
		if (side == Side.LONG) {
			closePosition(quote);
		}
		result.setChange(change - 1);
		log.debug("stop, date: " + quote.getDate() + ", price: " + quote.getClose());
		return result;
	}
}
