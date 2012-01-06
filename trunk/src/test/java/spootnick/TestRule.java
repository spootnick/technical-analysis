package spootnick;

import static org.testng.Assert.*;

import spootnick.result.Action.Side;
import spootnick.result.Result;
import spootnick.runtime.Simulation;
import spootnick.runtime.TradingRule;

public class TestRule implements TradingRule {

	@Override
	public Move start(Simulation simulation) {
		assertEquals(simulation.getIndex(), simulation.getWindowSize()-1);
		return new Move(Side.LONG);
	}

	@Override
	public Move next(Simulation simulation) throws InterruptedException {
		return new Move();
	}

	@Override
	public String getName() {
		return "TEST";
	}

	@Override
	public boolean finished(Result result) {
		return true;
	}

}
