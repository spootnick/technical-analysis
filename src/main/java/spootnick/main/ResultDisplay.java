package spootnick.main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import spootnick.result.Result;
import spootnick.result.ResultDao;

public class ResultDisplay {

	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		Map<String, String> queries = new HashMap<String, String>();
		queries.put("1", "select count(*) from Result");
		queries.put("2", "select count(*) from Result where change > priceChange");
		queries.put("3", "select min(change) from Result");
		queries.put("4", "select min(priceChange) from Result");
		
		
		
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("display.xml");

		ResultDao dao = context.getBean(ResultDao.class);
		ChartFrame frame = context.getBean(ChartFrame.class);

		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

		for(Entry<String , String> entry : queries.entrySet()){
			System.out.println(entry.getKey()+"->"+entry.getValue());
		}
		
		String line;
		String message = "query, empty to quit: ";
		System.out.println(message);
		while (!(line = reader.readLine()).isEmpty()) {

			try {
				String query = queries.get(line);
				List result = dao.executeQuery(query != null ? query : line);
				if(result.size() == 1 && result.get(0) instanceof Result)
					frame.display((Result)result.get(0));
				else
					System.out.println(result);
			} catch (RuntimeException e) {
				e.printStackTrace();
			}

			System.out.println(message);
		}

		context.destroy();
	}

}
