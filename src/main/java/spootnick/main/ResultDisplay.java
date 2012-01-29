package spootnick.main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import spootnick.result.Result;
import spootnick.result.ResultDao;

public class ResultDisplay {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("display.xml");

		ResultDao dao = context.getBean(ResultDao.class);
		ChartFrame frame = context.getBean(ChartFrame.class);

		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

		String line;
		String message = "query, empty to quit: ";
		System.out.println(message);
		while (!(line = reader.readLine()).isEmpty()) {

			try {
				List result = dao.executeQuery(line);
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
