package spootnick.runtime;


import org.springframework.stereotype.Component;

import spootnick.result.Result;


@Component
public class SampleRule extends TradingRule{

	private Move move;
	
	
	@Override
	public Move start(Simulation simulation) {
		double price = simulation.getQuoteSeries().getClose()[simulation.getStart()];
		
		move = new Move(price, price);
		
		return move;
	}

	
	@Override
	public Move next(Simulation simulation) throws InterruptedException {
		return move;
	}

	
	@Override
	public String getName() {
		return "SAMPLE";
	}





}
