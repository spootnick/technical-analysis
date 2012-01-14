package spootnick.runtime;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.annotation.PostConstruct;
import javax.swing.JOptionPane;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import spootnick.data.Quote;
import spootnick.result.Action.Side;

@Component
public class InteractiveRule extends AbstractVisualRule implements KeyListener {

	private Move move;
	private boolean pause;
	@Value("${delay}")
	private long delay;

	public void init() {
		frame.getFrame().addKeyListener(this);
		frame.display();
	}

	@Override
	public Move start(Simulation simulation) {
		
		int ret = JOptionPane.showConfirmDialog(frame.getFrame(), "Buy?", "Start",
				JOptionPane.YES_NO_OPTION);
		Move move = ret == JOptionPane.YES_OPTION ? new Move(Side.LONG) : new Move(Side.SHORT);

		frame.setSide(move.getSide(simulation));
		
		return move;
	}

	@Override
	public Move next(Simulation simulation) throws InterruptedException {
		Thread.sleep(delay);

		if (pause) {
			Object[] options = { "Buy", "Sell", "Cancel" };
			int ret = JOptionPane.showOptionDialog(frame.getFrame(), "message", "title",
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, options, options[2]);
			if (ret == JOptionPane.NO_OPTION) {
				move = new Move(Side.SHORT);
			} else if (ret == JOptionPane.YES_OPTION) {
				move = new Move(Side.LONG);
			} else {
				move = null;
			}
			pause = false;
		}

		Move ret = move;
		if(ret != null)
			frame.setSide(ret.getSide(simulation));
		move = null;
		return ret != null ? ret : new Move();
	}


	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		if (code == KeyEvent.VK_UP) {
			move = new Move(Side.LONG);
		} else if (code == KeyEvent.VK_DOWN) {
			move = new Move(Side.SHORT);
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
