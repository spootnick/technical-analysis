package spootnick;

import static org.testng.Assert.*;

import spootnick.data.QuoteSeries;
import spootnick.result.Action.Side;
import spootnick.result.Result;
import spootnick.runtime.Simulation;
import spootnick.runtime.TradingRule;

public class TestRule implements TradingRule {

	private Move move;
	
	@Override
	public Move start(Simulation simulation) {
		assertEquals(simulation.getIndex(), simulation.getWindowSize()-1);
		QuoteSeries series = simulation.getQuoteSeries();
		double avg = (series.getClose()[0]+series.getClose()[1])/2;
		move = new Move(avg,avg);
		return move;
	}

	@Override
	public Move next(Simulation simulation) throws InterruptedException {
		return move;
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
