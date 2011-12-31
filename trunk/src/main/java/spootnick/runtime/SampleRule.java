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

	private int counter;
	private List<Double> values = new ArrayList<Double>();
	private int past = 3;
	
	@Override
	public Move start(Simulation simulation) {
		values.clear();
		int count = simulation.getWindowSize() / 3;
		for(int i = count - 1 + past; i >= past ; --i){
			values.add(simulation.getQuoteSeries().getClose()[i]);
		}
		Side ret = decision(simulation.getQuoteSeries().getClose()[0]);
		if(ret == null)
			ret = Side.SHORT;
		
		return null;
	}

	private Side decision(double price){
		double max = Collections.max(values);
		double min = Collections.min(values);
		
		if(price > max)
			return Side.LONG;
		else if(price < min)
			return Side.SHORT;
		else
			return null;
	}
	
	@Override
	public Move next(Simulation simulation) throws InterruptedException {
		double pastPrice = simulation.getQuoteSeries().getClose()[past];
		values.remove(0);
		values.add(pastPrice);
		
		//return decision(simulation.getQuoteSeries().getClose()[0]);
		return null;
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
