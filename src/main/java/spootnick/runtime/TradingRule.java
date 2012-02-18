package spootnick.runtime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spootnick.result.Position.Side;
import spootnick.result.Result;

public abstract class TradingRule {

	public static class Move {

		//private Logger log = LoggerFactory.getLogger(Move.class);

		public static final double OVER = Double.MAX_VALUE;
		public static final double UNDER = -1;

		private double low = UNDER;
		private double high = OVER;
		private Side side;
		private boolean explicitSide;

		public static boolean notBoundary(double value) {
			return value != OVER && value != UNDER;
		}

		public Move() {
			explicitSide = true;

		}

		public Move(Side side) {
			this.side = side;
			explicitSide = true;
		}

		public Move(Side side,double low, double high) {
			this.side = side;
			this.high = high;
			this.low = low;
			explicitSide = true;
		}
		
		public Move(double low, double high) {
			this.high = high;
			this.low = low;
		}

		private Side getSide(double price) {
			Side ret = null;
			//double price = simulation.getQuote().getClose();
			if(explicitSide)
				ret = side;
			else if (price > high)
				ret = Side.LONG;
			else if (price < low)
				ret = Side.SHORT;
			return ret;
		}
		
		public Side getSide(){
			if(!explicitSide)
				throw new IllegalStateException("not explicit side");
			return side;
		}
		
		public Side getSide(Simulation simulation) {
			double price = simulation.getQuote().getClose();
			return getSide(price);
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

		@Override
		public String toString() {
			return "low: " + low + ", high: " + high;
		}

	}

	public void init(){
		
	}

	//public abstract Move start(Simulation simulation);

	public abstract Move next(Simulation simulation) throws InterruptedException;

	public abstract String getName();

	public boolean finished(Result result){
		return true;
	}
}
