package bitmapbenchmarks.synth;

import it.uniroma3.mat.extendedset.intset.ConciseSet;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;

import org.devbrat.util.WAHBitSet;

import com.googlecode.javaewah.EWAHCompressedBitmap;

public class WhenBitmapsBetter {

  public static void main(String args[]) {
    test(10, 18, 10);
  }

  public static void test(int N, int nbr, int repeat) {
    DecimalFormat df = new DecimalFormat("0.###");
    ClusteredDataGenerator cdg = new ClusteredDataGenerator();
    System.out
      .println("# For each instance, we report the sparsity, the speed (union, intersection) for both the naive and bitmap approach"
        +"(we also add two other bitmap approaches WAH and ConciseSet that you can ignore)");
    for (int sparsity = 1; sparsity < 8; sparsity++) {
      double sparse = (1 << nbr)
        * 1.0 / (1 << (nbr + sparsity));
      System.out.println("#"
        + " proba. that any given integer in the range appears = " + sparse);
      int[][] data = new int[N][];
      int Max = (1 << (nbr + sparsity));
      System.out.println("# generating random data...");
      for (int k = 0; k < N; ++k)
        data[k] = cdg.generateClustered(1 << nbr, Max);
      System.out.println("# generating random data... ok.");
      // building
      System.out
        .println("# report speeds for union and intersection in millions of integers processed per second");
      System.out.println(sparse+" "+testInts(data, repeat, df)+" "+testEWAH(data, repeat, df)+" "+testWAH(data, repeat, df)+" "+testConcise(data, repeat, df));

    }
  }

  public static String testInts(int[][] data, int repeat, DecimalFormat df) {
    long bef, aft;
    String line = "";
    long bogus = 0;
    int N = data.length;
    int[] buffer = new int[IntUtil.unite(data).length];
    // union
    bef = System.currentTimeMillis();
    long total = 0;
    for (int k = 0; k < N - 1; ++k)
      total += data[k].length + data[k + 1].length;
    for (int r = 0; r < repeat; ++r)
      for (int k = 0; k < N - 1; ++k) {
        int[] union = IntUtil.unite2by2(data[k], data[k + 1], buffer);
        bogus += union[union.length - 1];
      }
    aft = System.currentTimeMillis();
    line += "\t" + df.format(total * 1.0 * repeat / (1000.0 * (aft - bef)));
    // intersection
    bef = System.currentTimeMillis();
    for (int r = 0; r < repeat; ++r)
      for (int k = 0; k < N - 1; ++k) {
        int[] intersection = IntUtil
          .intersect2by2(data[k], data[k + 1], buffer);
        bogus += intersection[intersection.length - 1];
      }
    aft = System.currentTimeMillis();
    line += "\t" + df.format(total * 1.0 * repeat / (1000.0 * (aft - bef)));
    System.out.println("#bogus = "+bogus);
    return line;

  }

  public static String testEWAH(int[][] data, int repeat, DecimalFormat df) {
    long bef, aft;
    String line = "";
    long bogus = 0;
    int N = data.length;
    long total = 0;
    for (int k = 0; k < N - 1; ++k)
      total += data[k].length + data[k + 1].length;
    bef = System.currentTimeMillis();
    EWAHCompressedBitmap[] ewah = new EWAHCompressedBitmap[N];
    for (int k = 0; k < N; ++k) {
      ewah[k] = new EWAHCompressedBitmap();
      for (int x = 0; x < data[k].length; ++x) {
        ewah[k].set(data[k][x]);
      }
    }
    // union
    bef = System.currentTimeMillis();
    for (int r = 0; r < repeat; ++r)
      for (int k = 0; k < N-1; ++k) {
        EWAHCompressedBitmap bitmapor = ewah[k].or(ewah[k+1]);
        int[] array = bitmapor.toArray();
        bogus += array[array.length - 1];
      }
    aft = System.currentTimeMillis();
    line += "\t" + df.format(total * 1.0 * repeat / (1000.0 * (aft - bef)));
    // intersection
    bef = System.currentTimeMillis();
    for (int r = 0; r < repeat; ++r)
      for (int k = 0; k < N-1; ++k) {
        EWAHCompressedBitmap bitmapand = ewah[k].and(ewah[k+1]);
        int[] array = bitmapand.toArray();
        bogus += array[array.length - 1];
        if (array.length > 0)
          bogus += array[array.length - 1];
      }
    aft = System.currentTimeMillis();
    line += "\t" + df.format(total * 1.0 * repeat / (1000.0 * (aft - bef)));
    System.out.println("#bogus = "+bogus);
    return line;
  }


  public static String testWAH(int[][] data, int repeat, DecimalFormat df) {
    long bef, aft;
    String line = "";
    long bogus = 0;
    int N = data.length;
    long total = 0;
    for (int k = 0; k < N - 1; ++k)
      total += data[k].length + data[k + 1].length;
    bef = System.currentTimeMillis();
    WAHBitSet[] wah = new WAHBitSet[N];
    for (int k = 0; k < N; ++k) {
      wah[k] = new WAHBitSet();
      for (int x = 0; x < data[k].length; ++x) {
        wah[k].set(data[k][x]);
      }
    }
    // union
    bef = System.currentTimeMillis();
    for (int r = 0; r < repeat; ++r)
      for (int k = 0; k < N-1; ++k) {
        WAHBitSet bitmapor = wah[k].or(wah[k+1]);
        int[] array = new int[bitmapor.cardinality()];
        int c = 0;
        for (@SuppressWarnings("unchecked")
        Iterator<Integer> i = bitmapor.iterator(); i.hasNext(); array[c++] = i
          .next().intValue()) {
        }
        bogus += array[array.length - 1];
      }
    aft = System.currentTimeMillis();
    line += "\t" + df.format(total * 1.0 * repeat / (1000.0 * (aft - bef)));
    // intersection
    bef = System.currentTimeMillis();
    for (int r = 0; r < repeat; ++r)
      for (int k = 0; k < N-1; ++k) {
        WAHBitSet bitmapand = wah[k].and(wah[k+1]);
        int[] array = new int[bitmapand.cardinality()];
        int c = 0;
        for (@SuppressWarnings("unchecked")
        Iterator<Integer> i = bitmapand.iterator(); i.hasNext(); array[c++] = i
          .next().intValue()) {
        }
        bogus += array[array.length - 1];
        if (array.length > 0)
          bogus += array[array.length - 1];
      }
    aft = System.currentTimeMillis();
    line += "\t" + df.format(total * 1.0 * repeat / (1000.0 * (aft - bef)));
    System.out.println("#bogus = "+bogus);
    return line;
  }


  public static String testConcise(int[][] data, int repeat, DecimalFormat df) {
    long bef, aft;
    String line = "";
    long bogus = 0;
    int N = data.length;
    long total = 0;
    for (int k = 0; k < N - 1; ++k)
      total += data[k].length + data[k + 1].length;
    bef = System.currentTimeMillis();
    ConciseSet[] concisebitmap = new ConciseSet[N];
    for (int k = 0; k < N; ++k) {
      concisebitmap[k] = new ConciseSet();
      for (int x = 0; x < data[k].length; ++x) {
        concisebitmap[k].add(data[k][x]);
      }
    }
    // union
    bef = System.currentTimeMillis();
    for (int r = 0; r < repeat; ++r)
      for (int k = 0; k < N-1; ++k) {
        ConciseSet bitmapor = concisebitmap[k].union(concisebitmap[k+1]);
        int[] array = bitmapor.toArray();
        bogus += array[array.length - 1];
      }
    aft = System.currentTimeMillis();
    line += "\t" + df.format(total * 1.0 * repeat / (1000.0 * (aft - bef)));
    // intersection
    bef = System.currentTimeMillis();
    for (int r = 0; r < repeat; ++r)
      for (int k = 0; k < N-1; ++k) {
        ConciseSet bitmapand = concisebitmap[k].intersection(concisebitmap[k+1]);
        int[] array = bitmapand.toArray();
        bogus += array[array.length - 1];
        if (array.length > 0)
          bogus += array[array.length - 1];
      }
    aft = System.currentTimeMillis();
    line += "\t" + df.format(total * 1.0 * repeat / (1000.0 * (aft - bef)));
    System.out.println("#bogus = "+bogus);
    return line;
  }

}
