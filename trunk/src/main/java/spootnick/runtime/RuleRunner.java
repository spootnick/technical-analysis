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
	private ChartFrame frame;
	@Autowired
	private ResultBuilder builder;
	@Value("${saveResult}")
	private boolean saveResult;
	@Autowired
	private TradingRule tradingRule;
	

	//private Side side;
	//private boolean pause;

	@PostConstruct
	public void init() {
		frame.init();
		log.debug("frame initialized");
		//frame.addKeyListener(this);
		this.start();

	}

	@Override
	public void run() {
		try {
			log.debug("started");
			for (;;) {
				String name = frame.reset();
				Quote start = frame.getQuote();
				frame.display();
				
				frame.setSide(builder.start(start, tradingRule.start(), name,
						frame.getWindowSize(), frame.getQuoteCount()));
				// JOptionPane.showMessageDialog(player,"ok");
				while (frame.update()) {

					Side side;
					
					try {
						side = tradingRule.next();
					} catch (InterruptedException e) {
						return;
					}

					if (side != null) {
						Quote quote = frame.getQuote();
						frame.setSide(builder.update(quote, side));
						side = null;
					}

				}
				Quote stop = frame.getQuote();
				Result result = builder.stop(stop);
				if(saveResult)
					dao.save(result);
				frame.display(result);

				if(tradingRule.finished(result))
					break;
				
			}
		} finally {
			frame.dispose();
			log.debug("finished");
		}

	}


}
