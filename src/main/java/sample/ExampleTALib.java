package sample;
import java.util.Arrays;

import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;

public class ExampleTALib {

  /**
   * The total number of periods to generate data for.
   */
  public static final int TOTAL_PERIODS = 100;

  /**
    * The number of periods to average together.
    */
  public static final int PERIODS_AVERAGE = 2;

    public static void main(String[] args) {
      double[] closePrice = new double[TOTAL_PERIODS];
      double[] out = new double[TOTAL_PERIODS];
      MInteger begin = new MInteger();
      MInteger length = new MInteger();

      for (int i = 0; i < closePrice.length; i++) {
        closePrice[i] = (double) i;
      }

      Core c = new Core();
      RetCode retCode = c.sma(0, closePrice.length - 1, closePrice, PERIODS_AVERAGE, begin, length, out);

      if (retCode == RetCode.Success) {
        System.out.println("Output Begin:" + begin.value);
        System.out.println("Output Length:" + length.value);
        System.out.println("Output:"+Arrays.toString(out));

        for (int i = begin.value; i < length.value; i++) {
          StringBuilder line = new StringBuilder();
          line.append("Period #");
          line.append(i+1);
          line.append(" close= ");
          line.append(closePrice[i]);
          line.append(" mov avg=");
          line.append(out[i-begin.value]);
          System.out.println(line.toString());
      }
    }
    else {
      System.out.println("Error");
    }
  }
}