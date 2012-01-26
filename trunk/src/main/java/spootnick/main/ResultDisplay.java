package spootnick.main;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import spootnick.result.ResultDao;

public class ResultDisplay {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				"display.xml");
		
		ResultDao dao = context.getBean(ResultDao.class);
		ChartFrame frame = context.getBean(ChartFrame.class); 
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		
		String line;
		while(!(line = reader.readLine()).isEmpty()){
			Integer id = Integer.valueOf(line);
			frame.display(dao.load(id));
		}

		context.destroy();
	}

}
