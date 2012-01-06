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

@Test
public class IntegrationTest {

	private Simulation simulation;
	private RuleRunner runner;
	
	private ResultDao dao = Mockito.mock(ResultDao.class);
	
	@BeforeMethod
	public void init(){
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
		TradingRule rule = new TestRule();
		runner.setRuleName(rule.getName());
		runner.setRules(new TradingRule[]{rule});
		
	}
	
	public void test() throws Exception{
	
		runner.start();
		runner.join();
		
		ArgumentCaptor<Result> captor = ArgumentCaptor.forClass(Result.class); 
		verify(dao,times(1)).save(captor.capture());
		
		Result result = captor.getValue();
		assertEquals(result.getWindowSize(), simulation.getWindowSize());
		assertEquals(result.getQuoteCount(), simulation.getQuoteCount());
		assertEquals(result.getPriceChange(),0d);
		assertEquals(result.getChange(),-0.875);
		
	}
	
	
}
