package spootnick.runtime;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.annotation.PostConstruct;
import javax.swing.JOptionPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import spootnick.ChartFrame;
import spootnick.result.Action.Side;
import spootnick.result.Result;

@Component
public class InteractiveRule implements TradingRule, KeyListener {

	private Side side;
	private boolean pause;
	@Autowired
	private ChartFrame frame;
	@Value("${delay}")
	private long delay;

	@PostConstruct
	protected void add() {
		frame.getFrame().addKeyListener(this);
	}

	@Override
	public Side start(Simulation simulation) {
		frame.display();
		int ret = JOptionPane.showConfirmDialog(frame.getFrame(), "Buy?", "Start",
				JOptionPane.YES_NO_OPTION);
		Side startSide = ret == JOptionPane.YES_OPTION ? Side.LONG : Side.SHORT;

		frame.setSide(startSide);
		
		return startSide;
	}

	@Override
	public Side next(Simulation simulation) throws InterruptedException {
		Thread.sleep(delay);

		if (pause) {
			Object[] options = { "Buy", "Sell", "Cancel" };
			int ret = JOptionPane.showOptionDialog(frame.getFrame(), "message", "title",
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, options, options[2]);
			if (ret == JOptionPane.NO_OPTION) {
				side = Side.SHORT;
			} else if (ret == JOptionPane.YES_OPTION) {
				side = Side.LONG;
			} else {
				side = null;
			}
			pause = false;
		}

		Side ret = side;
		if(ret != null)
			frame.setSide(ret);
		side = null;
		return ret;
	}

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

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		if (code == KeyEvent.VK_UP) {
			side = Side.LONG;
		} else if (code == KeyEvent.VK_DOWN) {
			side = Side.SHORT;
		} else if (code == KeyEvent.VK_ENTER) {
			pause = true;
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	@Override
	public String getName() {
		return "INTERACTIVE";
	}

}
