package spootnick;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.annotation.PostConstruct;
import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;

import spootnick.data.Quote;
import spootnick.result.Action.Side;
import spootnick.result.Result;
import spootnick.result.ResultBuilder;
import spootnick.result.ResultDao;

@Component
public class ApplicationThread extends Thread implements KeyListener {

	private Logger log = LoggerFactory.getLogger(ApplicationThread.class);

	@Autowired
	private ResultDao dao;
	@Autowired
	private ChartFrame frame;
	@Autowired
	private ResultBuilder builder;
	@Autowired
	@Value("${delay}")
	private long delay;

	private Side side;
	private boolean pause;

	
	@PostConstruct
	public void init() {
		frame.init();
		log.debug("frame initialized");
		frame.addKeyListener(this);
		this.start();

	}

	@Override
	public void run() {
		try {
			log.debug("started");
			for (;;) {
				String name = frame.reset();
				Quote start = frame.getQuote();
				frame.setSide(builder.start(start, name, frame.getWindowSize(),
						frame.getQuoteCount()));
				// JOptionPane.showMessageDialog(player,"ok");
				while (frame.update()) {
					
					try {
						Thread.sleep(delay);
					} catch (InterruptedException e) {
						return;
					}
					
					if (pause) {
						Object[] options = { "Buy", "Sell",
								"Cancel" };
						int result = JOptionPane.showOptionDialog(frame,
								"message", "title",
								JOptionPane.YES_NO_CANCEL_OPTION,
								JOptionPane.QUESTION_MESSAGE, null, options,
								options[2]);
						if (result == JOptionPane.NO_OPTION) {
							side = Side.SHORT;
						} else if (result == JOptionPane.YES_OPTION) {
							side = Side.LONG;
						} else {
							side = null;
						}
						pause = false;
					}
					if (side != null) {
						Quote quote = frame.getQuote();
						frame.setSide(builder.update(quote, side));

						side = null;
					}

					
				}
				Quote stop = frame.getQuote();
				Result result = builder.stop(stop);
				dao.save(result);
				frame.display(result);

				if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(
						frame, "symbol: " + result.getSymbol() + ", change: "
								+ result.getChange() + ", priceChange: "
								+ result.getPriceChange() + ", next?", null,
						JOptionPane.YES_NO_OPTION))
					break;

			}
		} finally {
			frame.dispose();
			log.debug("finished");
		}

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
}
