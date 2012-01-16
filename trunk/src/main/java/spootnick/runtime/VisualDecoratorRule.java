package spootnick.runtime;

import org.springframework.stereotype.Component;

@Component
public class VisualDecoratorRule extends AbstractVisualRule {

	private TradingRule rule;
	
	public VisualDecoratorRule decorate(TradingRule rule){
		this.rule = rule;
		return this;
	}
	
	@Override
	public void init() {
		rule.init();

	}

	@Override
	public Move start(Simulation simulation) {
		return rule.start(simulation);
	}

	@Override
	public Move next(Simulation simulation) throws InterruptedException {
		return rule.next(simulation);
	}

	@Override
	public String getName() {
		return rule == null ? null : rule.getName();
	}

}
