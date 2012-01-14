package spootnick;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;

import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

import static org.testng.Assert.*;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import spootnick.data.DefaultQuoteSeries;
import spootnick.data.QuoteSeries;
import spootnick.result.Result;
import spootnick.result.ResultDao;
import spootnick.runtime.RuleRunner;
import spootnick.runtime.Simulation;
import spootnick.runtime.TradingRule;
import spootnick.runtime.TradingRule.Move;

@Test
public class IntegrationTest {

	private class TestRule implements TradingRule {

		private Move move;
		private int startIndex;
		private int count;
		
		@Override
		public Move start(Simulation simulation) {
			startIndex = simulation.getIndex();
			count = 0;
			QuoteSeries series = simulation.getQuoteSeries();
			double avg = (series.getClose()[0]+series.getClose()[1])/2;
			move = new Move(avg,avg);
			return move;
		}

		@Override
		public Move next(Simulation simulation) throws InterruptedException {
			++count;
			return move;
		}

		@Override
		public String getName() {
			return "TEST";
		}

		@Override
		public boolean finished(Result result) {
			return true;
		}

		@Override
		public void init() {
			// TODO Auto-generated method stub
			
		}

	}
	
	private Simulation simulation;
	private RuleRunner runner;
	private TestRule rule;
	private boolean exception;
	
	private ResultDao dao = Mockito.mock(ResultDao.class);
	
	@BeforeMethod
	public void init(){
		exception = false;
		InputStream is = getClass().getResourceAsStream("test.txt");
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		
		DefaultQuoteSeries series = DefaultQuoteSeries.parse(reader);
		
		simulation = new Simulation();
		simulation.setData(Collections.<QuoteSeries>singletonList(series));
		simulation.setWindowSize(4);
		simulation.setQuoteCount(series.getLength()-simulation.getWindowSize());
		
		
		runner = new RuleRunner();
		runner.setSimulation(simulation);
		runner.setSaveResult(true);
		runner.setDao(dao);
		rule = new TestRule();
		runner.setRuleName(rule.getName());
		runner.setRules(new TradingRule[]{rule});
		
	}
	
	public void testException() throws Exception{
		simulation.setQuoteCount(Integer.MAX_VALUE);
		runner.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				exception = true;
				
			}
		});
		
		runner.start();
		runner.join();
		
		assertTrue(exception);
	}
	
	public void test() throws Exception{
	
		runner.start();
		runner.join();
		
		assertEquals(rule.startIndex, simulation.getWindowSize()-1);
		assertEquals(rule.count, simulation.getQuoteCount());
		
		ArgumentCaptor<Result> captor = ArgumentCaptor.forClass(Result.class); 
		verify(dao,times(1)).save(captor.capture());
		
		Result result = captor.getValue();
		assertEquals(result.getWindowSize(), simulation.getWindowSize());
		assertEquals(result.getQuoteCount(), simulation.getQuoteCount());
		assertEquals(result.getPriceChange(),0d);
		assertEquals(result.getChange(),-0.875);
		
	}
	
	
}
