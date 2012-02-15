package spootnick.runtime.my;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import spootnick.runtime.Simulation;
import spootnick.runtime.Simulation.State;
import spootnick.runtime.TradingRule;

@Component
public class ChannelRule extends TradingRule {

	private List<Double> values = new LinkedList<Double>();;

	private Logger log = LoggerFactory.getLogger(ChannelRule.class);

	private int size;

	private Move next(double price) {
		Move ret = new Move();
		if (values.size() == size) {
			double max = Collections.max(values);
			double min = Collections.min(values);

			values.remove(0);
			ret = new Move(min, max);
		}
		values.add(price);
		return ret;
	}

	@Override
	public Move next(Simulation simulation) {
		State state = simulation.getState();
		if (state == State.BEGIN) {
			size = simulation.getWindowSize() / 3;
			values.clear();
		}
		return next(simulation.getQuote().getClose());
	}

	@Override
	public String getName() {
		return "CHANNEL";
	}

}
