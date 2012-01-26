package spootnick.runtime;

import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;

import spootnick.data.Quote;
import spootnick.main.ChartFrame;
import spootnick.result.Position.Side;
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
	@Value("${showResult}")
	private boolean showResult;
	@Value("${tradingRule}")
	private String ruleName;
	@Value("${iterations}")
	private int iterations;
	//@Autowired
	private TradingRule tradingRule;
	@Autowired
	private TradingRule[] rules;
	@Autowired
	private VisualDecoratorRule decorator;

	@PostConstruct
	@Override
	public synchronized void start() {
		log.info("ruleName: {}, registeredRules: {}",ruleName, Arrays.toString(rules));
		for(TradingRule rule: rules){
			String name = rule.getName();
			if(name != null && name.equals(ruleName)){
				tradingRule = showResult ? decorator.decorate(rule) : rule;
				tradingRule.init();
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
		log.info("started");
		int iteration = 0;
		for (;;) {
			String symbol = simulation.reset();
			
			ResultBuilder builder = new ResultBuilder(simulation,symbol,tradingRule.getName());
					
			
			builder.start(tradingRule.start(simulation));
			
			while (simulation.update()) {

				Move move;
				
				try {
					move = tradingRule.next(simulation);
				} catch (InterruptedException e) {
					return;
				}

				

				builder.update( move);


			}
			// Quote stop = simulation.getQuote();
			Result result = builder.stop();
			if (saveResult)
				dao.save(result);
			//simulation.display(result);

			boolean ruleFinished = tradingRule.finished(result);
			if(iterations > 0){
				if(++iteration == iterations)
					break;
			}else{
				if(ruleFinished)
					break;
			}
			
			log.info("{}/{} finished",iteration,iterations);

		}
		log.info("finished");
		
	}



	public Simulation getSimulation() {
		return simulation;
	}



	public void setSimulation(Simulation simulation) {
		this.simulation = simulation;
	}



	public String getRuleName() {
		return ruleName;
	}



	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}



	public TradingRule[] getRules() {
		return rules;
	}



	public void setRules(TradingRule[] rules) {
		this.rules = rules;
	}



	public boolean isSaveResult() {
		return saveResult;
	}



	public void setSaveResult(boolean saveResult) {
		this.saveResult = saveResult;
	}



	public ResultDao getDao() {
		return dao;
	}



	public void setDao(ResultDao dao) {
		this.dao = dao;
	}

}
