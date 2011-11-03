package spootnick.runtime;

import spootnick.result.Action.Side;
import spootnick.result.Result;

public interface TradingRule {

	public Side start(Simulation simulation);
	
	public Side next(Simulation simulation) throws InterruptedException;
	
	public String getName();
	
	public boolean finished(Result result);
}
