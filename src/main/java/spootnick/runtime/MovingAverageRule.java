package spootnick.runtime;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;

import spootnick.result.Result;

@Component
public class MovingAverageRule implements TradingRule {

	private Logger log = LoggerFactory.getLogger(MovingAverageRule.class);
	
	private Core core = new Core();
	private double[] average;
	private MInteger begin = new MInteger();

	@Override
	public void init() {

	}

	@Override
	public Move start(Simulation simulation) {
		int days = 30;
		average = new double[simulation.getQuoteSeries().getLength()];
		MInteger length = new MInteger();
		RetCode code = core.sma(simulation.getStart(), simulation.getStop(), simulation.getQuoteSeries().getClose(), days, begin, length, average);
		log.debug("code: {}, begin: {}, length: {}, average: {}",new Object[]{code,begin.value,length.value, Arrays.toString(average)});
		return getMove(simulation);
	}

	@Override
	public Move next(Simulation simulation) throws InterruptedException {
		return getMove(simulation);
	}

	private Move getMove(Simulation simulation){
		int index = simulation.getCurrent()-begin.value;
		double value = average[index];

		log.debug("index: {}, value: {}",index,value);
		
		return new Move(value, value);
	}
	
	@Override
	public String getName() {
		return "MOVING_AVERAGE";
	}

	@Override
	public boolean finished(Result result) {
		return true;
	}

}
