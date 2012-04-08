package spootnick.runtime;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.annotation.PostConstruct;
import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import spootnick.data.Quote;
import spootnick.result.Position.Side;
import spootnick.runtime.Simulation.State;
import spootnick.runtime.my.ChannelRule;

@Component
public class InteractiveRule extends AbstractVisualRule implements KeyListener {

	private static boolean CHANNEL = false;

	private Logger log = LoggerFactory.getLogger(getClass());
	
	private Move move;
	//private transient boolean pause;
	private transient boolean wait;
	@Value("${delay}")
	private long delay;
	private transient long currentDelay;

	private int size;
	
	private Simulation simulation;

	private ChannelRule channel = new ChannelRule();

	public void init() {
		frame.getFrame().addKeyListener(this);
		frame.display();
	}

	private void start(Simulation simulation) {

		//int ret = JOptionPane.showConfirmDialog(frame.getFrame(), "Buy?", "Start", JOptionPane.YES_NO_OPTION);
		//Move move = ret == JOptionPane.YES_OPTION ? new Move(Side.LONG) : new Move(Side.SHORT);

		//frame.setSide(move.getSide(simulation));

		currentDelay = delay;
		this.simulation = simulation;
		size = simulation.getWindowSize();

		move = new Move(Side.LONG);
		wait = true;
		
		//return move;
	}

	private Move running(Simulation simulation) throws InterruptedException {
		Thread.sleep(currentDelay);
		synchronized (this) {
			if (wait){
				frame.setTitle("Paused");
				this.wait();
			}
		}

		//if (pause) {
		//	Object[] options = { "Buy", "Sell", "Cancel" };
		//	int ret = JOptionPane.showOptionDialog(frame.getFrame(), "message", "title", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[2]);
		//	if (ret == JOptionPane.NO_OPTION) {
		//		move = new Move(Side.SHORT);
		//	} else if (ret == JOptionPane.YES_OPTION) {
		//		move = new Move(Side.LONG);
		//	} else {
		//		move = null;
		//	}
		//	pause = false;
		//}

		Move ret = move;
		if (ret != null)
			frame.setSide(ret.getSide(simulation));
		move = null;
		return ret != null ? ret : new Move();
	}

	private void changeSize(int size) {
		this.size = Math.min(simulation.getWindowSize(), Math.max(2,size));
		frame.setWindow(this.size);
	}

	@Override
	public Move next(Simulation simulation) throws InterruptedException {
		double low = Move.UNDER;
		double high = Move.OVER;
		Move ret;
		if (CHANNEL) {
			ret = channel.next(simulation);
			int index = simulation.getCurrent() - simulation.getBegin();
			low = ret.getLow();
			high = ret.getHigh();
			frame.addLowHigh(low, high, index);
		}
		ret = new Move();
		if (simulation.getState() == State.START)
			start(simulation);
		if (simulation.getState() == State.STARTED)
			ret = running(simulation);
		return new Move(ret.getSide(), low, high);
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		switch (code) {
		case KeyEvent.VK_UP:
			move = new Move(Side.LONG);
			break;
		case KeyEvent.VK_DOWN:
			move = new Move(Side.SHORT);
			break;
		//case KeyEvent.VK_ENTER:
		//	pause = true;
		//	break;
		case KeyEvent.VK_RIGHT:
			currentDelay /= 2;
			break;
		case KeyEvent.VK_LEFT:
			currentDelay *= 2;
			break;
		case KeyEvent.VK_A:
			changeSize(size / 2);
			break;
		case KeyEvent.VK_D:
			changeSize(size * 2);
			break;
		case KeyEvent.VK_S:
			if (wait) {
				synchronized (this) {
					wait = false;
					frame.setTitle(null);
					this.notify();
				}
			} else {
				wait = true;
			}
			break;

		}
		
		log.trace("currentDelay: {}",currentDelay);
	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	@Override
	public String getName() {
		return "INTERACTIVE";
	}

}
