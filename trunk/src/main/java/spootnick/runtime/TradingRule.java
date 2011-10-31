package spootnick.runtime;

import spootnick.result.Action.Side;
import spootnick.result.Result;

public interface TradingRule {

	public Side start();
	
	public Side next() throws InterruptedException;
	
	public boolean finished(Result result);
}
