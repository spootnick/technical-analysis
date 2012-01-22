package spootnick.runtime;

import org.springframework.stereotype.Component;

import spootnick.result.Position.Side;
import spootnick.result.Result;

@Component
public class NoOpRule extends TradingRule {

	private Move move = new Move(Side.SHORT);
	
	private int count = 0;
	
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

}
