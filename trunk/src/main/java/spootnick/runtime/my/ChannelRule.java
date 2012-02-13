package spootnick.runtime.my;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import spootnick.runtime.Simulation;
import spootnick.runtime.TradingRule;

@Component
public class ChannelRule extends TradingRule {

	private List<Double> values = new LinkedList<Double>();;

	private Logger log = LoggerFactory.getLogger(ChannelRule.class);

	//@Override
	public Move start(Simulation simulation) {
		values.clear();
		double[] close = simulation.getQuoteSeries().getClose();
		int start = simulation.getBegin();
		int stop = start + simulation.getWindowSize() / 3;
		int current = simulation.getCurrent();
		log.debug("start: {}, stop: {}, current: {}", new Object[] { start, stop, current });
		for (int i = start; i < stop; ++i) {
			values.add(close[i]);
		}

		Move ret = new Move();

		for (int i = stop; i <= current; ++i) {
			double price = close[i];
			Move move = next(price);
			if (move.getSide(price) != null)
				ret = move;
		}

		return ret;

	}

	private Move next(double price) {
		double max = Collections.max(values);
		double min = Collections.min(values);

		values.remove(0);
		values.add(price);

		return new Move(min, max);
	}

	@Override
	public Move next(Simulation simulation) {
		return next(simulation.getQuote().getClose());
	}

	@Override
	public String getName() {
		return "CHANNEL";
	}

}
