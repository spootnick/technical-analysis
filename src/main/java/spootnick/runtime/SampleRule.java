package spootnick.runtime;

import org.springframework.stereotype.Component;

import spootnick.result.Result;
import spootnick.result.Action.Side;

//@Component
public class SampleRule implements TradingRule {

	private int counter;
	
	@Override
	public Side start(Simulation simulation) {
		return Side.LONG;
	}

	@Override
	public Side next(Simulation simulation) throws InterruptedException {
		return null;
	}

	@Override
	public String getName() {
		return "SAMPLE";
	}

	@Override
	public boolean finished(Result result) {
		++counter;
		return counter >= 10;
	}

}
