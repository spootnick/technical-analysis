package spootnick;

import java.util.Iterator;
import java.util.SortedSet;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import spootnick.result.Action;
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
		//test(context);

	}

	private static void test(ClassPathXmlApplicationContext context) {
		ResultDao dao = context.getBean(ResultDao.class);

		SortedSet<Action> actions = null;//dao.test().getActions();
		Iterator<Action> it = actions.iterator();
		while (it.hasNext()) {
			System.out.println(it.next().getQuoteDate());
		}
	}
}
