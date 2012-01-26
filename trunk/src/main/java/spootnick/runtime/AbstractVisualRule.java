package spootnick.runtime;

import javax.swing.JOptionPane;

import org.springframework.beans.factory.annotation.Autowired;

import spootnick.main.ChartFrame;
import spootnick.result.Result;

public abstract class AbstractVisualRule extends TradingRule {

	@Autowired
	protected ChartFrame frame;
	
	@Override
	public boolean finished(Result result) {
		frame.display(result);
		return JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(
				frame.getFrame(),
				"symbol: " + result.getSymbol() + ", change: "
						+ result.getChange() + ", priceChange: "
						+ result.getPriceChange(), "Next?",
				JOptionPane.YES_NO_OPTION);
	}
}
