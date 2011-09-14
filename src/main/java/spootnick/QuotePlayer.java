package spootnick;

import java.util.Iterator;
import java.util.SortedSet;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import spootnick.result.Action;
import spootnick.result.ResultDao;

public final class QuotePlayer {

	private QuotePlayer() {

	}

	public static void main(final String[] args) {

		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				"spring.xml");
		
		//test(context);

	}

	private static void test(ClassPathXmlApplicationContext context) {
		ResultDao dao = context.getBean(ResultDao.class);

		SortedSet<Action> actions = dao.test().getActions();
		Iterator<Action> it = actions.iterator();
		while (it.hasNext()) {
			System.out.println(it.next().getQuoteDate());
		}
	}
}
