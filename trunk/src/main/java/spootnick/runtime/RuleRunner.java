package spootnick.runtime;


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

@Component
public class RuleRunner extends Thread {

	private Logger log = LoggerFactory.getLogger(RuleRunner.class);

	@Autowired
	private ResultDao dao;
	@Autowired
	private ChartFrame simulation;
	@Autowired
	private ResultBuilder builder;
	@Value("${saveResult}")
	private boolean saveResult;
	@Autowired
	private TradingRule tradingRule;
	
	@PostConstruct
	@Override
	public synchronized void start() {
		super.start();
	}
	
	@Override
	public void run() {
		try {
			log.debug("started");
			for (;;) {
				String name = simulation.reset();
				Quote start = simulation.getQuote();
				simulation.display();
				
				builder.start(start, tradingRule.start(), name,
						simulation.getWindowSize(), simulation.getQuoteCount());
				// JOptionPane.showMessageDialog(player,"ok");
				while (simulation.update()) {

					Side side;
					
					try {
						side = tradingRule.next();
					} catch (InterruptedException e) {
						return;
					}

					if (side != null) {
						Quote quote = simulation.getQuote();
						builder.update(quote, side);
						side = null;
					}

				}
				Quote stop = simulation.getQuote();
				Result result = builder.stop(stop);
				if(saveResult)
					dao.save(result);
				simulation.display(result);

				if(tradingRule.finished(result))
					break;
				
			}
		} finally {
			simulation.getFrame().dispose();
			log.debug("finished");
		}

	}


}
