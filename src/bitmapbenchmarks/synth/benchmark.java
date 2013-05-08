package bitmapbenchmarks.synth;

import it.uniroma3.mat.extendedset.intset.ConciseSet;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Iterator;
import org.devbrat.util.WAHBitSet;
import sparsebitmap.SparseBitmap;
import com.googlecode.javaewah.EWAHCompressedBitmap;
import com.googlecode.javaewah32.EWAHCompressedBitmap32;

public class benchmark {

  public static void main(String args[]) {
    test(10, 18, 10);
  }
  public static long testWAH32(int[][] data, int repeat, DecimalFormat df) {
    System.out.println("# WAH 32 bit using the compressedbitset library");
    System.out
      .println("# size, construction time, time to recover set bits, time to compute unions (without and with uncompression) and intersections (with uncompression)");
    long bef, aft;
    String line = "";
    long bogus = 0;
    int N = data.length;
    bef = System.currentTimeMillis();
    WAHBitSet[] bitmap = new WAHBitSet[N];
    int size = 0;
    for (int r = 0; r < repeat; ++r) {
      size = 0;
      for (int k = 0; k < N; ++k) {
        bitmap[k] = new WAHBitSet();
        for (int x = 0; x < data[k].length; ++x) {
          bitmap[k].set(data[k][x]);
        }
        size += bitmap[k].memSize() * 4;
      }
    }
    aft = System.currentTimeMillis();
    line += "\t" + size / 1024;
    line += "\t" + df.format((aft - bef) / 1000.0);
    // uncompressing
    bef = System.currentTimeMillis();
    for (int r = 0; r < repeat; ++r)
      for (int k = 0; k < N; ++k) {
        int[] array = new int[bitmap[k].cardinality()];
        int c = 0;
        for (@SuppressWarnings("unchecked")
        Iterator<Integer> i = bitmap[k].iterator(); i.hasNext(); array[c++] = i
          .next().intValue()) {
        }
      }
    aft = System.currentTimeMillis();
    line += "\t" + df.format((aft - bef) / 1000.0);
    // logical or
    bef = System.currentTimeMillis();
    for (int r = 0; r < repeat; ++r)
      for (int k = 0; k < N; ++k) {
        WAHBitSet bitmapor = bitmap[0];
        for (int j = 1; j < k; ++j) {
          bitmapor = bitmapor.or(bitmap[j]);
        }
      }
    aft = System.currentTimeMillis();
    line += "\t" + df.format((aft - bef) / 1000.0);
    // logical or + extraction
    bef = System.currentTimeMillis();
    for (int r = 0; r < repeat; ++r)
      for (int k = 0; k < N; ++k) {
        WAHBitSet bitmapor = bitmap[0];
        for (int j = 1; j < k; ++j) {
          bitmapor = bitmapor.or(bitmap[j]);
        }
        int[] array = new int[bitmapor.cardinality()];
        int c = 0;
        for (@SuppressWarnings("unchecked")
        Iterator<Integer> i = bitmapor.iterator(); i.hasNext(); array[c++] = i
          .next().intValue()) {
        }
        bogus += array[array.length-1];
      }
    aft = System.currentTimeMillis();
    line += "\t" + df.format((aft - bef) / 1000.0);

    // logical and + extraction
    bef = System.currentTimeMillis();
    for (int r = 0; r < repeat; ++r)
      for (int k = 0; k < N; ++k) {
        WAHBitSet bitmapand = bitmap[0];
        for (int j = 1; j < k; ++j) {
          bitmapand = bitmapand.and(bitmap[j]);
        }
        int[] array = new int[bitmapand.cardinality()];
        int c = 0;
        for (@SuppressWarnings("unchecked")
        Iterator<Integer> i = bitmapand.iterator(); i.hasNext(); array[c++] = i
          .next().intValue()) {
        }
        if(array.length>0) bogus += array[array.length-1];
      }
    aft = System.currentTimeMillis();
    line += "\t" + df.format((aft - bef) / 1000.0);
    
    System.out.println(line);
    return bogus;
  }


  public static long testInts(int[][] data, int repeat, DecimalFormat df) {
    System.out.println("# Ints");
    System.out
      .println("# size, construction time, time to recover set bits, time to compute unions (without and with uncompression) and intersections (with uncompression)");
    long bef, aft;
    String line = "";
    long bogus = 0;
    int N = data.length;
    bef = System.currentTimeMillis();
    int size = 0;
      for (int k = 0; k < N; ++k) {
        size += data[k].length * 4;
      }
    
    aft = System.currentTimeMillis();
    line += "\t" + size / 1024;
    line += "\t" + df.format((aft - bef) / 1000.0);
    // uncompressing
    bef = System.currentTimeMillis();
    aft = System.currentTimeMillis();
    line += "\t" + df.format((aft - bef) / 1000.0);
    // logical or
    bef = System.currentTimeMillis();
    for (int r = 0; r < repeat; ++r)
      for (int k = 0; k < N; ++k) {
        int[][] b = Arrays.copyOf(data,k+1);
        int[] union = IntUtil.unite(b);
        bogus += union.length;
      }
    aft = System.currentTimeMillis();
    line += "\t" + df.format((aft - bef) / 1000.0);
    // logical or + retrieval
    bef = System.currentTimeMillis();
    for (int r = 0; r < repeat; ++r)
      for (int k = 0; k < N; ++k) {
        int[][] b = Arrays.copyOf(data,k+1);
        int[] union = IntUtil.unite(b);
        bogus += union[union.length-1];
      }
    aft = System.currentTimeMillis();
    line += "\t" + df.format((aft - bef) / 1000.0);
    // logical and + retrieval
    bef = System.currentTimeMillis();
    for (int r = 0; r < repeat; ++r)
      for (int k = 0; k < N; ++k) {
        int[][] b = Arrays.copyOf(data,k+1);
        int[] inter = IntUtil.intersect(b);
        if(inter.length>0) bogus += inter[inter.length-1];
      }
    aft = System.currentTimeMillis();
    line += "\t" + df.format((aft - bef) / 1000.0);
    
    System.out.println(line);
    return bogus;
  }
  
  
  public static long testConciseSet(int[][] data, int repeat, DecimalFormat df) {
    System.out.println("# ConciseSet 32 bit using the extendedset_2.2 library");
    System.out
      .println("# size, construction time, time to recover set bits, time to compute unions (without and with uncompression) and intersections (with uncompression)");
    long bef, aft;
    String line = "";
    long bogus = 0;
    int N = data.length;
    bef = System.currentTimeMillis();
    ConciseSet[] bitmap = new ConciseSet[N];
    int size = 0;
    for (int r = 0; r < repeat; ++r) {
      size = 0;
      for (int k = 0; k < N; ++k) {
        bitmap[k] = new ConciseSet();
        for (int x = 0; x < data[k].length; ++x) {
          bitmap[k].add(data[k][x]);
        }
        size += (int) (bitmap[k].size() * bitmap[k]
          .collectionCompressionRatio()) * 4;
      }
    }
    aft = System.currentTimeMillis();
    line += "\t" + size / 1024;
    line += "\t" + df.format((aft - bef) / 1000.0);
    // uncompressing
    bef = System.currentTimeMillis();
    for (int r = 0; r < repeat; ++r)
      for (int k = 0; k < N; ++k) {
        int[] array = bitmap[k].toArray();
        bogus += array.length;
      }
    aft = System.currentTimeMillis();
    line += "\t" + df.format((aft - bef) / 1000.0);
    // logical or
    bef = System.currentTimeMillis();
    for (int r = 0; r < repeat; ++r)
      for (int k = 0; k < N; ++k) {
        ConciseSet bitmapor = bitmap[0];
        for (int j = 1; j < k; ++j) {
          bitmapor = bitmapor.union(bitmap[j]);
        }
      }
    aft = System.currentTimeMillis();
    line += "\t" + df.format((aft - bef) / 1000.0);
    // logical or + retrieval
    bef = System.currentTimeMillis();
    for (int r = 0; r < repeat; ++r)
      for (int k = 0; k < N; ++k) {
        ConciseSet bitmapor = bitmap[0];
        for (int j = 1; j < k; ++j) {
          bitmapor = bitmapor.union(bitmap[j]);
        }
        int[] array = bitmapor.toArray();
        bogus += array[array.length-1];
      }
    aft = System.currentTimeMillis();
    line += "\t" + df.format((aft - bef) / 1000.0);
    // logical and + retrieval
    bef = System.currentTimeMillis();
    for (int r = 0; r < repeat; ++r)
      for (int k = 0; k < N; ++k) {
        ConciseSet bitmapand = bitmap[0];
        for (int j = 1; j < k; ++j) {
          bitmapand = bitmapand.intersection(bitmap[j]);
        }
        int[] array = bitmapand.toArray();
        if(array!=null) if(array.length>0) bogus += array[array.length-1];
      }
    aft = System.currentTimeMillis();
    line += "\t" + df.format((aft - bef) / 1000.0);

    System.out.println(line);
    return bogus;
  }


  public static long testSparse(int[][] data, int repeat, DecimalFormat df) {
    System.out.println("# simple sparse bitmap implementation");
    System.out
      .println("# size, construction time, time to recover set bits, time to compute unions (without and with uncompression) and intersections (with uncompression)");
    long bef, aft;
    String line = "";
    long bogus = 0;
    int N = data.length;
    bef = System.currentTimeMillis();
    SparseBitmap[] bitmap = new SparseBitmap[N];
    int size = 0;
    for (int r = 0; r < repeat; ++r) {
      size = 0;
      for (int k = 0; k < N; ++k) {
        bitmap[k] = new SparseBitmap();
        for (int x = 0; x < data[k].length; ++x) {
          bitmap[k].set(data[k][x]);
        }
        size += bitmap[k].sizeInBytes();
      }
    }
    aft = System.currentTimeMillis();
    line += "\t" + size / 1024;
    line += "\t" + df.format((aft - bef) / 1000.0);
    // uncompressing
    bef = System.currentTimeMillis();
    for (int r = 0; r < repeat; ++r)
      for (int k = 0; k < N; ++k) {
        int[] array = bitmap[k].toArray();
        bogus += array.length;
      }
    aft = System.currentTimeMillis();
    line += "\t" + df.format((aft - bef) / 1000.0);
    // logical or
    bef = System.currentTimeMillis();
    for (int r = 0; r < repeat; ++r)
      for (int k = 0; k < N; ++k) {
        SparseBitmap bitmapor = SparseBitmap.or(Arrays.copyOfRange(bitmap, 0, k+1));
        bogus += bitmapor.cardinality();
      }
    aft = System.currentTimeMillis();
    line += "\t" + df.format((aft - bef) / 1000.0);
    // logical or + retrieval
    bef = System.currentTimeMillis();
    for (int r = 0; r < repeat; ++r)
      for (int k = 0; k < N; ++k) {
        SparseBitmap bitmapor = SparseBitmap.or(Arrays.copyOfRange(bitmap, 0, k+1));
        int[] array = bitmapor.toArray();
        bogus += array[array.length-1];
      }
    aft = System.currentTimeMillis();
    line += "\t" + df.format((aft - bef) / 1000.0);
    // logical and + retrieval
    bef = System.currentTimeMillis();
    for (int r = 0; r < repeat; ++r)
      for (int k = 0; k < N; ++k) {
    	  SparseBitmap bitmapand = SparseBitmap.materialize( SparseBitmap.fastand(Arrays.copyOfRange(bitmap, 0, k+1)) );
        int[] array = bitmapand.toArray();
        if(array!=null) if(array.length>0) bogus += array[array.length-1];
      }
    aft = System.currentTimeMillis();
    line += "\t" + df.format((aft - bef) / 1000.0);

    
    System.out.println(line);
    return bogus;
  }

  
  public static long testBitSet(int[][] data, int repeat, DecimalFormat df) {
    System.out.println("# BitSet");
    System.out
      .println("# size, construction time, time to recover set bits, time to compute unions (without and with uncompression) and intersections (with uncompression)");
    long bef, aft;
    String line = "";
    long bogus = 0;
    int N = data.length;
    bef = System.currentTimeMillis();
    BitSet[] bitmap = new BitSet[N];
    int size = 0;
    for (int r = 0; r < repeat; ++r) {
      size = 0;
      for (int k = 0; k < N; ++k) {
        bitmap[k] = new BitSet();
        for (int x = 0; x < data[k].length; ++x) {
          bitmap[k].set(data[k][x]);
        }
        size += bitmap[k].size()/8;
      }
    }
    aft = System.currentTimeMillis();
    line += "\t" + size / 1024;
    line += "\t" + df.format((aft - bef) / 1000.0);
    // uncompressing
    bef = System.currentTimeMillis();
    for (int r = 0; r < repeat; ++r)
      for (int k = 0; k < N; ++k) {
        int[] array = new int[bitmap[k].cardinality()];
        int pos = 0;
        for(int i=bitmap[k].nextSetBit(0); i>=0; i=bitmap[k].nextSetBit(i+1)) { 
          array[pos++] = i;
        }
      }
    aft = System.currentTimeMillis();
    line += "\t" + df.format((aft - bef) / 1000.0);
    // logical or
    bef = System.currentTimeMillis();
    for (int r = 0; r < repeat; ++r)
      for (int k = 0; k < N; ++k) {
        BitSet bitmapor = (BitSet) bitmap[0].clone();
        for (int j = 1; j < k; ++j) {
          bitmapor.or(bitmap[j]);
        }        
      }
    aft = System.currentTimeMillis();
    line += "\t" + df.format((aft - bef) / 1000.0);
    // logical or + retrieval
    bef = System.currentTimeMillis();
    for (int r = 0; r < repeat; ++r)
      for (int k = 0; k < N; ++k) {
        BitSet bitmapor = (BitSet) bitmap[0].clone();
        for (int j = 1; j < k; ++j) {
          bitmapor.or(bitmap[j]);
        }
        int[] array = new int[bitmapor.cardinality()];
        int pos = 0;
        for(int i=bitmapor.nextSetBit(0); i>=0; i=bitmapor.nextSetBit(i+1)) { 
          array[pos++] = i;
        }
        bogus += array[array.length-1];
      }
    aft = System.currentTimeMillis();
    line += "\t" + df.format((aft - bef) / 1000.0);
    // logical and + retrieval
    bef = System.currentTimeMillis();
    for (int r = 0; r < repeat; ++r)
      for (int k = 0; k < N; ++k) {
        BitSet bitmapand = (BitSet) bitmap[0].clone();
        for (int j = 1; j < k; ++j) {
          bitmapand.and(bitmap[j]);
        }
        int[] array = new int[bitmapand.cardinality()];
        int pos = 0;
        for(int i=bitmapand.nextSetBit(0); i>=0; i=bitmapand.nextSetBit(i+1)) { 
          array[pos++] = i;
        }
        if(array.length>0)
          bogus += array[array.length-1];
      }
    aft = System.currentTimeMillis();
    line += "\t" + df.format((aft - bef) / 1000.0);

    System.out.println(line);
    return bogus;
  }

  
  public static long testEWAH64(int[][] data, int repeat, DecimalFormat df) {
    System.out.println("# EWAH using the javaewah library");
    System.out
      .println("# size, construction time, time to recover set bits, time to compute unions (without and with uncompression) and intersections (with uncompression)");
    long bef, aft;
    String line = "";
    long bogus = 0;
    int N = data.length;
    bef = System.currentTimeMillis();
    EWAHCompressedBitmap[] ewah = new EWAHCompressedBitmap[N];
    int size = 0;
    for (int r = 0; r < repeat; ++r) {
      size = 0;
      for (int k = 0; k < N; ++k) {
        ewah[k] = new EWAHCompressedBitmap();
        for (int x = 0; x < data[k].length; ++x) {
          ewah[k].set(data[k][x]);
        }
        size += ewah[k].sizeInBytes();
      }
    }
    aft = System.currentTimeMillis();
    line += "\t" + size / 1024;
    line += "\t" + df.format((aft - bef) / 1000.0);
    // uncompressing
    bef = System.currentTimeMillis();
    for (int r = 0; r < repeat; ++r)
      for (int k = 0; k < N; ++k) {
        int[] array = ewah[k].toArray();
        bogus += array.length;
      }
    aft = System.currentTimeMillis();
    line += "\t" + df.format((aft - bef) / 1000.0);
    
    
    // fast logical or
    bef = System.currentTimeMillis();
    for (int r = 0; r < repeat; ++r)
      for (int k = 0; k < N; ++k) {
        EWAHCompressedBitmap bitmapor = EWAHCompressedBitmap.or(Arrays.copyOf(ewah,k+1));
        bogus += bitmapor.sizeInBits();
      }
    aft = System.currentTimeMillis();
    line += "\t" + df.format((aft - bef) / 1000.0);
    // fast logical or + retrieval
    bef = System.currentTimeMillis();
    for (int r = 0; r < repeat; ++r)
      for (int k = 0; k < N; ++k) {
        EWAHCompressedBitmap bitmapor = EWAHCompressedBitmap.or(Arrays.copyOf(ewah,k+1));
        int[] array = bitmapor.toArray();
        bogus += array[array.length - 1];
      }
    aft = System.currentTimeMillis();
    line += "\t" + df.format((aft - bef) / 1000.0);
    
    
    // fast logical and + retrieval
    bef = System.currentTimeMillis();
    for (int r = 0; r < repeat; ++r)
      for (int k = 0; k < N; ++k) {
        EWAHCompressedBitmap bitmapand = EWAHCompressedBitmap.and(Arrays.copyOf(ewah,k+1));
        int[] array = bitmapand.toArray();
        if(array.length>0) bogus += array[array.length - 1];
      }
    aft = System.currentTimeMillis();
    line += "\t" + df.format((aft - bef) / 1000.0);
    System.out.println(line);
    return bogus;
  }

  
  public static long testEWAH32(int[][] data, int repeat, DecimalFormat df) {
    System.out.println("# EWAH 32-bit using the javaewah library");
    System.out
      .println("# size, construction time, time to recover set bits, time to compute unions (without and with uncompression) and intersections (with uncompression)");
    long bef, aft;
    String line = "";
    long bogus = 0;
    int N = data.length;
    bef = System.currentTimeMillis();
    EWAHCompressedBitmap32[] ewah = new EWAHCompressedBitmap32[N];
    int size = 0;
    for (int r = 0; r < repeat; ++r) {
      size = 0;
      for (int k = 0; k < N; ++k) {
        ewah[k] = new EWAHCompressedBitmap32();
        for (int x = 0; x < data[k].length; ++x) {
          ewah[k].set(data[k][x]);
        }
        size += ewah[k].sizeInBytes();
      }
    }
    aft = System.currentTimeMillis();
    line += "\t" + size / 1024;
    line += "\t" + df.format((aft - bef) / 1000.0);
    // uncompressing
    bef = System.currentTimeMillis();
    for (int r = 0; r < repeat; ++r)
      for (int k = 0; k < N; ++k) {
        int[] array = ewah[k].toArray();
        bogus += array.length;
      }
    aft = System.currentTimeMillis();
    line += "\t" + df.format((aft - bef) / 1000.0);
    

    
    // fast logical or
    bef = System.currentTimeMillis();
    for (int r = 0; r < repeat; ++r)
      for (int k = 0; k < N; ++k) {
        EWAHCompressedBitmap32 bitmapor = EWAHCompressedBitmap32.or(Arrays.copyOf(ewah,k+1));
        bogus += bitmapor.sizeInBits();
      }
    aft = System.currentTimeMillis();
    line += "\t" + df.format((aft - bef) / 1000.0);
    // fast logical or + retrieval
    bef = System.currentTimeMillis();
    for (int r = 0; r < repeat; ++r)
      for (int k = 0; k < N; ++k) {
        EWAHCompressedBitmap32 bitmapor = EWAHCompressedBitmap32.or(Arrays.copyOf(ewah,k+1));
        int[] array = bitmapor.toArray();
        bogus += array[array.length - 1];
      }
    aft = System.currentTimeMillis();
    line += "\t" + df.format((aft - bef) / 1000.0);
    
    // fast logical and + retrieval
    bef = System.currentTimeMillis();
    for (int r = 0; r < repeat; ++r)
      for (int k = 0; k < N; ++k) {
        EWAHCompressedBitmap32 bitmapand = EWAHCompressedBitmap32.and(Arrays.copyOf(ewah,k+1));
        int[] array = bitmapand.toArray();
        if(array.length>0) bogus += array[array.length - 1];
      }
    aft = System.currentTimeMillis();
    line += "\t" + df.format((aft - bef) / 1000.0);
    
    System.out.println(line);
    return bogus;
  }

  
  public static void test(int N, int nbr, int repeat) {
    DecimalFormat df = new DecimalFormat("0.###");
    ClusteredDataGenerator cdg = new ClusteredDataGenerator();
    System.out
      .println("# For each instance, we report the size, the construction time, ");
    System.out.println("# the time required to recover the set bits,");
    System.out
      .println("# and the time required to compute logical ors (unions) between lots of bitmaps.");
    for (int sparsity = 1; sparsity < 31 - nbr; sparsity += 4) {
      System.out.println("# sparsity " + sparsity
        + " average set bit per 32-bit word = " + (1 << nbr) * 32.0
        / (1 << (nbr + sparsity)));
      int[][] data = new int[N][];
      int Max = (1 << (nbr + sparsity));
      System.out.println("# generating random data...");
      for (int k = 0; k < N; ++k)
        data[k] = cdg.generateClustered(1 << nbr, Max);
      System.out.println("# generating random data... ok.");
      // building
      testInts(data, repeat, df);
      testBitSet(data, repeat, df);
      testSparse(data,repeat,df);
      testConciseSet(data, repeat, df);
      testWAH32(data, repeat, df);
      testEWAH64(data, repeat, df);
      testEWAH32(data, repeat, df);
      System.out.println();

    }
  }
}
