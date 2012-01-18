package spootnick.runtime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spootnick.result.Position.Side;
import spootnick.result.Result;

public interface TradingRule {

	public class Move {

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

		}

		public Move(Side side) {
			if (side == Side.LONG)
				high = UNDER;
			else
				low = OVER;
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

		public Side getSide(Simulation simulation) {
			Side ret = null;
			double price = simulation.getQuote().getClose();
			if(explicitSide)
				ret = side;
			else if (price > high)
				ret = Side.LONG;
			else if (price < low)
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

		@Override
		public String toString() {
			return "low: " + low + ", high: " + high;
		}

	}

	public void init();

	public Move start(Simulation simulation);

	public Move next(Simulation simulation) throws InterruptedException;

	public String getName();

	public boolean finished(Result result);
}
