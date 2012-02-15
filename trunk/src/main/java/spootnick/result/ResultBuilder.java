package spootnick.result;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spootnick.data.Quote;
import spootnick.result.Position.Side;
import spootnick.runtime.Simulation;
import spootnick.runtime.Simulation.State;
import spootnick.runtime.TradingRule.Move;

public class ResultBuilder {

	private Logger log = LoggerFactory.getLogger(ResultBuilder.class);

	private double startPrice;
	private double buyPrice;
	private double change = 1;
	private Side side = Side.LONG;
	private Result result;
	private Simulation simulation;
	private Position position;

	public ResultBuilder(Simulation simulation, String symbol, String name) {

		this.simulation = simulation;

		result = new Result(simulation.getWindowSize(), simulation.getQuoteCount());
		result.setSymbol(symbol);
		result.setExecutionDate(new Date());
		result.setQuoteDate(simulation.getQuoteSeries().getDate()[simulation.getBegin()]);

		result.setName(name);
	}

	private void updateLowHigh(Move move) {
		double low = move.getLow();
		double high = move.getHigh();
		if (Move.notBoundary(low))
			result.getLow()[simulation.getCurrent() - simulation.getBegin()] = low;
		if (Move.notBoundary(high))
			result.getHigh()[simulation.getCurrent() - simulation.getBegin()] = high;
	}


	private void openPosition(Quote quote) {
		position = new Position();
		position.setOpenDate(quote.getDate());
		position.setResult(result);
		buyPrice = quote.getClose();
		if (log.isDebugEnabled()) {
			log.debug("opened, date: " + quote.getDate() + ", price: " + buyPrice );
		}
	}

	private void closePosition(Quote quote) {
		double price = quote.getClose();
		double positionChange = price / buyPrice;
		change = change * positionChange;
		position.setCloseDate(quote.getDate());
		position.setChange(positionChange - 1);
		result.getPositions().add(position);
		position = null;
		if (log.isDebugEnabled()) {
			log.debug("closed, date: " + quote.getDate() + ", price: " + price + ", change: " + change);
		}

	}

	public void update(Move move) {
		updateLowHigh(move);
		Side side = move.getSide(simulation);
		Quote quote = simulation.getQuote();
		State state = simulation.getState();
		if ((state == State.BEGIN || state == State.NOT_STARTED ) && side != null) {
			this.side = side;
			return;
		} else if (state == State.START) {
			side = side != null ? side : this.side;
			startPrice = quote.getClose();
			if (log.isDebugEnabled()) {
				log.debug("start, date: " + quote.getDate() + ", startPrice: " + startPrice + ", side: " + side);
			}
			if(side == Side.SHORT){
				this.side = side;
				return;
			}
				
		} else if (side == null || this.side == side)
			return;
		double price = quote.getClose();
		this.side = side;
		if (side == Side.LONG) {
			// buy
			openPosition(quote);
		} else {
			closePosition(quote);

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
