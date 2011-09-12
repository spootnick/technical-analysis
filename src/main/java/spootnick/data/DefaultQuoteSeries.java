package spootnick.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DefaultQuoteSeries implements QuoteSeries {

	private String name;
	private List<Quote> data = new ArrayList<Quote>();

	private DefaultQuoteSeries(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<Quote> getData() {
		return data;
	}

	public static DefaultQuoteSeries parse(BufferedReader reader) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			String line = reader.readLine();
			DefaultQuoteSeries ret = null;
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split(",");
				if(ret == null){
					ret = new DefaultQuoteSeries(parts[0]);
				}
				Date date = format.parse(parts[1]);
				double open = Double.parseDouble(parts[2]);
				double high = Double.parseDouble(parts[3]);
				double low = Double.parseDouble(parts[4]);
				double close = Double.parseDouble(parts[5]);
				double volume = Double.parseDouble(parts[6]);
				
				ret.data.add(new Quote(date, open, high, low, close, volume));
			}
			
			return ret;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
}
