package spootnick.runtime;

import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;

import spootnick.ChartFrame;
import spootnick.data.Quote;
import spootnick.result.Action.Side;
import spootnick.result.Result;
import spootnick.result.ResultBuilder;
import spootnick.result.ResultDao;
import spootnick.runtime.TradingRule.Move;

@Component
public class RuleRunner extends Thread {

	private Logger log = LoggerFactory.getLogger(RuleRunner.class);

	@Autowired
	private ResultDao dao;
	@Autowired
	private Simulation simulation;
	@Value("${saveResult}")
	private boolean saveResult;
	@Value("${tradingRule}")
	private String ruleName;
	//@Autowired
	private TradingRule tradingRule;
	@Autowired
	private TradingRule[] rules;

	@PostConstruct
	@Override
	public synchronized void start() {
		log.info("ruleName: {}, registeredRules: {}",ruleName, Arrays.toString(rules));
		for(TradingRule rule: rules){
			if(rule.getName().equals(ruleName)){
				tradingRule = rule;
				break;
			}
		}
		if(tradingRule == null){
			throw new RuntimeException(ruleName+" not found");
		}
		super.start();
	}

	
	
	@Override
	public void run() {
		// try {
		log.debug("started");
		for (;;) {
			String symbol = simulation.reset();
			Quote start = simulation.getQuote();
			//simulation.display();

			ResultBuilder builder = new ResultBuilder(symbol,tradingRule.getName(),
					simulation.getWindowSize(), simulation.getQuoteCount());
			
			builder.start(start, tradingRule.start(simulation).getSide(simulation) );
			// JOptionPane.showMessageDialog(player,"ok");
			Quote quote = null;
			while (simulation.update()) {

				Side side;
				quote = simulation.getQuote();
				
				try {
					side = tradingRule.next(simulation).getSide(simulation);
				} catch (InterruptedException e) {
					return;
				}

				
				if (side != null) {
					builder.update(quote, side);
					side = null;
				}

			}
			// Quote stop = simulation.getQuote();
			Result result = builder.stop(quote);
			if (saveResult)
				dao.save(result);
			//simulation.display(result);

			if (tradingRule.finished(result))
				break;

		}
		log.debug("finished");
		// } finally {
		// simulation.getFrame().dispose();
		// log.debug("finished");
		// }

	}

}
