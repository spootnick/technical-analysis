package sample;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import spootnick.data.Quote;
import spootnick.data.QuoteSeries;
import spootnick.data.QuoteSeriesFactory;

public class ImageSample {

	private static Random random = new Random();

	private static List<QuoteSeries> list;
	private static int index = 1;
	private static int seriesIndex = 0;

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		int size = 512;
		BufferedImage image = new BufferedImage(size, size,
				BufferedImage.TYPE_BYTE_BINARY);

		for (int i = 0; i < size; ++i)
			for (int j = 0; j < size; ++j) {
				image.setRGB(i, j, randomStock() ? Color.WHITE.getRGB()
						: Color.BLACK.getRGB());
			}

		ImageIO.write(image, "bmp", new File("sample.bmp"));

		System.out.println("OK");
	}

	private static boolean randomNormal() {
		return random.nextBoolean();
	}

	private static boolean randomStock() {
		if (list == null) {
			QuoteSeriesFactory factory = new QuoteSeriesFactory(
					"file:D:/maklerka/player/mstzgr.zip");

			//factory.setFilter("DJIA");

			list = factory.create();

			System.out.println(list.get(seriesIndex).getData().size());
		}

		List<Quote> data = list.get(seriesIndex).getData();

		if (index >= data.size()) {
			index = 1;
			++seriesIndex;
			if (seriesIndex == list.size()) {
				seriesIndex = 0;
			}
			data = list.get(seriesIndex).getData();
		}

		boolean ret = false;

		try {
			ret = data.get(index - 1).getClose() > data.get(index).getClose();
		} catch (RuntimeException e) {
			System.err.println(list.get(seriesIndex).getName());
//			throw e;
		}

		index++;

		return ret;
	}
}
