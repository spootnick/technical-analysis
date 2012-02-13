package spootnick.runtime;


import org.springframework.stereotype.Component;

import spootnick.result.Result;


@Component
public class SampleRule extends TradingRule{

	private Move move;
	
		
	@Override
	public Move next(Simulation simulation) throws InterruptedException {
		if(simulation.getBegin() == simulation.getCurrent()){
			double price = simulation.getQuoteSeries().getClose()[simulation.getBegin()];
			
			move = new Move(price, price);
		}
		return move;
	}

	
	@Override
	public String getName() {
		return "SAMPLE";
	}





}
