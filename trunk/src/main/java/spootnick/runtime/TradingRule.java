package spootnick.runtime;

import spootnick.result.Action.Side;
import spootnick.result.Result;

public interface TradingRule {

	public class Move{
		
		public static final double OVER = Double.MAX_VALUE;
		public static final double UNDER = -1;
		
		private double low = UNDER;
		private double high = OVER;
		
		public static boolean notBoundary(double value){
			return value != OVER && value != UNDER;
		}
		
		public Move(){
			
		}
		
		public Move(Side side){
			if(side == Side.LONG)
				high = UNDER;
			else
				low = OVER;
		}
		
		public Move(double low, double high){
			this.high = high;
			this.low = low;
		}

		public Side getSide(Simulation simulation){
			Side ret = null;
			double price = simulation.getQuote().getClose();
			if(price > high)
				ret = Side.LONG;
			else if(price < low)
				ret = Side.SHORT;
			return ret;
		}
		
		public double getLow() {
			return low;
		}

		public void setLow(double low) {
			this.low = low;
		}

		public double getHigh() {
			return high;
		}

		public void setHigh(double high) {
			this.high = high;
		}
		
		
	}
	
	public Move start(Simulation simulation);
	
	public Move next(Simulation simulation) throws InterruptedException;
	
	public String getName();
	
	public boolean finished(Result result);
}
