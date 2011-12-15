package spootnick.data;

import java.util.Date;
import java.util.List;

public interface QuoteSeries {

	public Date[] getDate();
	public double[] getOpen();
	public double[] getClose();
	public double[] getHigh();
	public double[] getLow();
	public double[] getVolume();
	
	public Quote getQuote(int index);
	
	public String getName();
	public int getLength();
	
}
