package spootnick.main;

import java.util.Iterator;
import java.util.SortedSet;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import spootnick.result.ResultDao;
import spootnick.runtime.RuleRunner;

public final class QuotePlayer {

	private QuotePlayer() {

	}

	public static void main(final String[] args) throws InterruptedException {

		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				"spring.xml");
		
		RuleRunner runner = context.getBean(RuleRunner.class);
		
		runner.join();
		context.destroy();

	}

	
}
