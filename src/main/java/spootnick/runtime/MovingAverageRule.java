package spootnick.runtime;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;

import spootnick.result.Position.Side;
import spootnick.result.Result;

@Component
public class MovingAverageRule implements TradingRule {

	private Logger log = LoggerFactory.getLogger(MovingAverageRule.class);
	
	private static final int SLOW_DAYS = 30;
	private static final int FAST_DAYS = 10;
	
	private Core core = new Core();
	private double[] slow;
	private double[] fast;
	private MInteger begin = new MInteger();
	private int count;

	@Override
	public void init() {

	}

	@Override
	public Move start(Simulation simulation) {
		count = 0;
		int size = simulation.getStop()-simulation.getStart();
		slow = new double[size];
		fast = new double[size];
		
		begin = new MInteger();
		MInteger length = new MInteger();
		RetCode code = core.sma(simulation.getStart(), simulation.getStop()-1, simulation.getQuoteSeries().getClose(), SLOW_DAYS, begin, length, slow);
		log.debug("code: {}, begin: {}, length: {}, slow: {}",new Object[]{code,begin.value,length.value, Arrays.toString(slow)});
		
		code = core.sma(simulation.getStart(), simulation.getStop()-1, simulation.getQuoteSeries().getClose(), FAST_DAYS, begin, length, fast);
		log.debug("code: {}, begin: {}, length: {}, fast: {}",new Object[]{code,begin.value,length.value, Arrays.toString(fast)});
		return getMove(simulation);
	}

	@Override
	public Move next(Simulation simulation) throws InterruptedException {
		return getMove(simulation);
	}

	private Move getMove(Simulation simulation){
		int index = simulation.getCurrent()-begin.value;
		double slowValue = slow[index];
		double fastValue = fast[index];

		log.debug("index: {}, slowValue: {}, fastValue: {}",new Object[]{index,slowValue,fastValue});
		
		Side side = null;
		
		if(fastValue > slowValue)
			side = Side.LONG;
		else if(fastValue < slowValue)
			side = Side.SHORT;
			
	
		return new Move(side,slowValue,fastValue);
	}
	
	@Override
	public String getName() {
		return "MOVING_AVERAGE";
	}

	@Override
	public boolean finished(Result result) {
		count++;
		return count == 1000;
	}

}
