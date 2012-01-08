package spootnick.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DefaultQuoteSeries implements QuoteSeries {

	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
	
	private String name;
	private Date[] date;
	private double[] open;
	private double[] close;
	private double[] high;
	private double[] low;
	private double[] volume;

	private DefaultQuoteSeries(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Date[] getDate() {
		return date;
	}

	@Override
	public double[] getOpen() {
		return open;
	}

	@Override
	public double[] getClose() {
		return close;
	}

	@Override
	public double[] getHigh() {
		return high;
	}

	@Override
	public double[] getLow() {
		return low;
	}

	@Override
	public double[] getVolume() {
		return volume;
	}

	@Override
	public int getLength() {
		return date.length;
	}
	@Override
	public Quote getQuote(int i) {
		Quote quote = new Quote(date[i], open[i], high[i], low[i], close[i], volume[i]);
		return quote;
	}
	
	
	private static double[] createArray(List<Double> data){
		double[] ret = new double[data.size()];
		for(int i = 0 ; i < data.size() ; ++i){
			ret[i] = data.get(i);
		}
		return ret;
	}
	
	public static DefaultQuoteSeries parse(BufferedReader reader) {
		try {
			String line = reader.readLine();
			DefaultQuoteSeries ret = null;
			List<Date> date = new ArrayList<Date>();
			List<Double> open = new ArrayList<Double>();
			List<Double> close = new ArrayList<Double>();
			List<Double> high = new ArrayList<Double>();
			List<Double> low = new ArrayList<Double>();
			List<Double> volume = new ArrayList<Double>();
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split(",");
				if(ret == null){
					ret = new DefaultQuoteSeries(parts[0]);
				}
				date.add(DATE_FORMAT.parse(parts[1]));
				open.add(Double.parseDouble(parts[2]));
				high.add(Double.parseDouble(parts[3]));
				low.add(Double.parseDouble(parts[4]));
				close.add(Double.parseDouble(parts[5]));
				volume.add(Double.parseDouble(parts[6]));
				
				//ret.data.add(new Quote(date, open, high, low, close, volume));
			}
			
			int size = date.size();
			ret.date = new Date[size];
			ret.date = date.toArray(ret.date);
			ret.open = createArray(open);
			ret.close = createArray(close);
			ret.high = createArray(high);
			ret.low = createArray(low);
			ret.volume = createArray(volume);
			
			
			return ret;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	


	
}
