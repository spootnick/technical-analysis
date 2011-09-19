package spootnick.data;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class QuoteSeriesFactory {

	private Logger log = LoggerFactory.getLogger(QuoteSeriesFactory.class);

	public QuoteSeriesFactory(){
		
	}
	
	public QuoteSeriesFactory(String url){
		this.url = url;
	}
	
	// @Autowired
	@Value("${quoteUrl}")
	private String url;
	// @Autowired
	@Value("${filter}")
	private String filter;
	@Value("${addRandom}")
	private boolean addRandom;
	
	public void setFilter(String filter){
		this.filter = filter;
	}
	
	public List<QuoteSeries> create() {
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
			if (addRandom) {
				ret.add(new RandomQuoteSeries());
				log.info("random added");
			}
			return ret;
		} catch (IOException e) {
			throw new RuntimeException("couldn't create series", e);
		}
	}
}
