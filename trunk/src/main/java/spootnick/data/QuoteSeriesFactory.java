package spootnick.data;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class QuoteSeriesFactory implements FactoryBean<List<QuoteSeries>> {

	private Logger log = LoggerFactory.getLogger(QuoteSeriesFactory.class);
	private Random random = new Random();

	public static final String RANDOM = "RANDOM";
	public static final String TEST = "TEST";

	public QuoteSeriesFactory() {

	}

	public QuoteSeriesFactory(String url) {
		this.url = url;
	}

	// @Autowired
	@Value("${quoteUrl}")
	private String url;
	// @Autowired
	@Value("${filter}")
	private String filter;
	@Value("${special}")
	private String special;

	public void setFilter(String filter) {
		this.filter = filter;
	}

	private BufferedReader createReader(){
		boolean test = false;
		if(!RANDOM.equals(special) && !TEST.equals(special))
			return null;
		else
			test = TEST.equals(special);
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		Calendar calendar = Calendar.getInstance();
		int size = 2000;
		double price = test? 100 : random.nextInt(1000) + 1000;
		for (int i = 0 ; i < size; ++i) {
			calendar.add(Calendar.DAY_OF_YEAR, 1);
			//double val;
			if(!test){
				int n = 99;
				price += price * (random.nextInt(n) - n / 2) / 1000d;
				//val = price;
			} else if(i % 10 == 0){
				price += random.nextInt(3) - 1;
			}
			sb.append(special+","
					+ DefaultQuoteSeries.DATE_FORMAT.format(calendar
							.getTime()) + ",0,0,0,"+ price + ",0\n");
		}
		return new BufferedReader(
				new StringReader(sb.toString()));
	}
	
	@Override
	public List<QuoteSeries> getObject() {
		try {
			List<QuoteSeries> ret = new ArrayList<QuoteSeries>();

			log.info("url: {}, filter: {}", url, filter);

			Pattern pattern = null;

			if (filter != null && !filter.isEmpty())
				pattern = Pattern.compile(filter);

			InputStream is = new URL(url).openStream();
			ZipInputStream zin = new ZipInputStream(new BufferedInputStream(is));

			final BufferedReader br = new BufferedReader(new InputStreamReader(
					zin));

			ZipEntry entry;
			while ((entry = zin.getNextEntry()) != null) {
				String name = entry.getName();
				name = name.substring(0, name.length() - 4);
				if (pattern != null && !pattern.matcher(name).matches()) {
					log.info("{} skipped", name);
					continue;
				}
				DefaultQuoteSeries series = DefaultQuoteSeries.parse(br);
				ret.add(series);
				log.info("{} series added", name);
			}
			br.close();
			BufferedReader reader = createReader();
			if(reader != null){
				ret.add(DefaultQuoteSeries.parse(reader));
				log.info("{} added",special);
			}
			
			if(ret.size() == 0)
				throw new RuntimeException("no QuoteSeries created");
			
			return ret;
		} catch (IOException e) {
			throw new RuntimeException("couldn't create series", e);
		}
	}

	@Override
	public Class<?> getObjectType() {
		return List.class;
	}

	@Override
	public boolean isSingleton() {

		return false;
	}
}
