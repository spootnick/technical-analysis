package spootnick.runtime;

import org.springframework.stereotype.Component;

import spootnick.result.Action.Side;
import spootnick.result.Result;

@Component
public class NoOpRule implements TradingRule {

	private Move move = new Move(Side.SHORT);
	
	private int count = 0;
	
	@Override
	public void init() {
		

	}

	@Override
	public Move start(Simulation simulation) {
		return move;
	}

	@Override
	public Move next(Simulation simulation) throws InterruptedException {
		return move;
	}

	@Override
	public String getName() {
		return "NOOP";
	}

	@Override
	public boolean finished(Result result) {
		count++;
		return count == 1000;
	}

}
