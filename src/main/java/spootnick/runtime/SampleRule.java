package spootnick.runtime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import spootnick.data.Quote;
import spootnick.result.Result;
import spootnick.result.Action.Side;

@Component
public class SampleRule extends AbstractVisualRule {

	private Move move;
	
	
	@Override
	public Move start(Simulation simulation) {
		double price = simulation.getQuoteSeries().getClose()[simulation.getStart()];
		
		move = new Move(price*0.95, price*1.05);
		
		return move;
	}

	
	@Override
	public Move next(Simulation simulation) throws InterruptedException {
		return move;
	}

	//@Override
	//public boolean finished(Result result) {
	//	counter++;
	//	return counter == 1000;
	//}
	
	@Override
	public String getName() {
		return "SAMPLE";
	}


}
