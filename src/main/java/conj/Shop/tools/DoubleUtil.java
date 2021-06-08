package conj.Shop.tools;

import java.text.NumberFormat;

public class DoubleUtil {
   public static String toString(Double value) {
      return NumberFormat.getInstance().format(value);
   }
}
