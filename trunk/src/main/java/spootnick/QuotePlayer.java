package spootnick;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import spootnick.result.ResultDao;

public final class QuotePlayer {
	
	private QuotePlayer(){
		
	}
	
	public static void main(final String[] args) {

		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				"spring.xml");


	}
}
