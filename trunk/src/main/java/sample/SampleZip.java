package sample;

import java.io.BufferedInputStream;
import java.io.BufferedReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;



public class SampleZip {

	//private static final String EXPECTED = "GETIN.mst";

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		//parse();
	}

	public static void parse() throws Exception {

		String location;
		location = "file:D:\\java\\test\\test.zip";
		//location = "http://bossa.pl/pub/ciagle/mstock/mstcgl.zip";
		URL url = new URL(location);

		InputStream is = url.openStream();
		ZipInputStream zin = new ZipInputStream(new BufferedInputStream(is));

		final BufferedReader br = new BufferedReader(new InputStreamReader(zin));



		ZipEntry entry;
		while ((entry = zin.getNextEntry()) != null) {
			String name = entry.getName();
			if(name.equals("ala.txt"))
				continue;
			System.out.println(name);
			String line;

			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}

			

		}
		br.close();
	}
}
