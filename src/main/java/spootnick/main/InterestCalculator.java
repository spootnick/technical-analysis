package spootnick.main;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import spootnick.result.Result;
import spootnick.result.ResultDao;

public class InterestCalculator {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				"persistence.xml");
		
		ResultDao dao = context.getBean(ResultDao.class);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date date = null;
		if(args.length > 0)
			date = sdf.parse(args[0]);
		Collection<Result> results = dao.load(date,"WIG");
		
		BigDecimal interest = BigDecimal.ONE;
		BigDecimal interestPrice = BigDecimal.ONE;

		int days = 0;
		
		for(Result result : results){
			BigDecimal change = BigDecimal.valueOf(result.getChange()).add(BigDecimal.ONE);
			BigDecimal price = BigDecimal.valueOf(result.getPriceChange()).add(BigDecimal.ONE);
			days += result.getQuoteCount();
			interest = interest.multiply(change);
			interestPrice = interestPrice.multiply(price);
		}
		double years = days / 250.0;
		interest = interest.setScale(2,RoundingMode.CEILING).subtract(BigDecimal.ONE);
		interestPrice = interestPrice.setScale(2,RoundingMode.CEILING).subtract(BigDecimal.ONE);
		
		double yearInterest = Math.pow(interest.doubleValue(), 1/years) - 1;
		
		System.out.println("interest: "+interest+", interestPrice: "+interestPrice+", years: "+years+", yearInterest: "+yearInterest);
	}

}
