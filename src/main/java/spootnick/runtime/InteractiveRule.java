package spootnick.runtime;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.Field;

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
	// private transient boolean pause;
	private transient boolean wait;
	@Value("${delay}")
	private long delay;
	@Value("${keyLong}")
	private String keyLong;
	@Value("${keyShort}")
	private String keyShort;
	@Value("${keyZoomIn}")
	private String keyZoomIn;
	@Value("${keyZoomOut}")
	private String keyZoomOut;
	@Value("${keyPause}")
	private String keyPause;
	@Value("${keySpeedUp}")
	private String keySpeedUp;
	@Value("${keySlowDown}")
	private String keySlowDown;
	private transient long currentDelay;

	private int codeLong;
	private int codeShort;
	private int codeZoomIn;
	private int codeZoomOut;
	private int codePause;
	private int codeSpeedUp;
	private int codeSlowDown;

	private int size;

	private Simulation simulation;

	private ChannelRule channel = new ChannelRule();

	public void init() {
		frame.getFrame().addKeyListener(this);
		frame.display();

		codeLong = findCode(keyLong);
		codeShort = findCode(keyShort);
		codeZoomIn = findCode(keyZoomIn);
		codeZoomOut = findCode(keyZoomOut);
		codePause = findCode(keyPause);
		codeSpeedUp = findCode(keySpeedUp);
		codeSlowDown = findCode(keySlowDown);
	}

	private int findCode(String key) {
		try {
			String f = "VK_" + key.toUpperCase();
			Field field = KeyEvent.class.getField(f);
			int code = field.getInt(null);
			log.debug("{}: {}", key, code);
			return code;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private Move start(Simulation simulation) throws InterruptedException {

		// int ret = JOptionPane.showConfirmDialog(frame.getFrame(), "Buy?",
		// "Start", JOptionPane.YES_NO_OPTION);
		// Move move = ret == JOptionPane.YES_OPTION ? new Move(Side.LONG) : new
		// Move(Side.SHORT);

		// frame.setSide(move.getSide(simulation));

		currentDelay = delay;
		this.simulation = simulation;
		size = simulation.getWindowSize();

		Side side = Side.LONG;
		move = new Move(side);
		frame.setSide(side);
		wait = true;

		return running(simulation);
		//return null;
		// return move;
	}

	private Move running(Simulation simulation) throws InterruptedException {
		
		synchronized (this) {
			if (wait) {
				frame.setTitle("Paused, long: " + keyLong + ", short: " + keyShort + ", pause: " + keyPause + ", zoomIn: " + keyZoomIn + ", zoomOut: " + keyZoomOut + ", speedUp: "
						+ keySpeedUp + ", slowDown: " + keySlowDown);
				this.wait();
			} else{
				Thread.sleep(currentDelay);
			}
		}

		// if (pause) {
		// Object[] options = { "Buy", "Sell", "Cancel" };
		// int ret = JOptionPane.showOptionDialog(frame.getFrame(), "message",
		// "title", JOptionPane.YES_NO_CANCEL_OPTION,
		// JOptionPane.QUESTION_MESSAGE, null, options, options[2]);
		// if (ret == JOptionPane.NO_OPTION) {
		// move = new Move(Side.SHORT);
		// } else if (ret == JOptionPane.YES_OPTION) {
		// move = new Move(Side.LONG);
		// } else {
		// move = null;
		// }
		// pause = false;
		// }

		Move ret = move;
		if (ret != null)
			frame.setSide(ret.getSide(simulation));
		move = null;
		return ret != null ? ret : new Move();
	}

	private void changeSize(int size) {
		this.size = Math.min(simulation.getWindowSize(), Math.max(2, size));
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
			ret = start(simulation);
		else if (simulation.getState() == State.STARTED)
			ret = running(simulation);
		return new Move(ret.getSide(), low, high);
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		if (code == codeLong) {
			move = new Move(Side.LONG);
		} else if (code == codeShort) {
			move = new Move(Side.SHORT);
		} else if (code == codeSpeedUp) {
			currentDelay /= 2;
		} else if (code == codeSlowDown) {
			currentDelay *= 2;
		} else if (code == codeZoomIn) {
			changeSize(size / 2);
		} else if (code == codeZoomOut) {
			changeSize(size * 2);
		} else if (code == codePause) {
			if (wait) {
				synchronized (this) {
					wait = false;
					frame.setTitle(null);
					this.notify();
				}
			} else {
				wait = true;
			}
		}

		log.trace("currentDelay: {}", currentDelay);
	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	@Override
	public String getName() {
		return "INTERACTIVE";
	}

}
