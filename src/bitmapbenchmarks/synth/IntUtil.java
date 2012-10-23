package bitmapbenchmarks.synth;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;


public final class IntUtil {
  
  public static int[] unite(int[]... set) {
    if(set.length == 0) throw new RuntimeException("nothing");
    PriorityQueue<int[]> pq = new PriorityQueue<int[]>(set.length,
      new Comparator<int[]>(){
        public int compare(int[] a, int[] b) {
          return a.length - b.length;
        }}
     );
    int[] buffer = new int[32];
    for(int[] x : set) 
      pq.add(x);
    while(pq.size()>1) {
    int[] x1 = pq.poll();
    int[] x2 = pq.poll();
    if(buffer.length<x1.length+x2.length)
      buffer =  new int[x1.length+x2.length];
    int [] a = unite2by2(x1,x2,buffer);
    pq.add(a);
    } 
    return pq.poll();
  }
  
  static public int[] unite2by2(final int[] set1, final int[] set2, final int[] buffer) {
    int pos = 0;
    int k1 = 0, k2 = 0;
    if(0==set1.length)
      return Arrays.copyOf(set2, set2.length);
    if(0==set2.length)
      return Arrays.copyOf(set1, set1.length);
    while(true) {
      if(set1[k1]<set2[k2]) {
        buffer[pos++] = set1[k1];
        ++k1;
        if(k1>=set1.length) {
          for(; k2<set2.length;++k2)
            buffer[pos++] = set2[k2];
          break;
        }
      } else if (set1[k1]==set2[k2]) {
        buffer[pos++] = set1[k1];
        ++k1;
        ++k2;
        if(k1>=set1.length) {
          for(; k2<set2.length;++k2)
            buffer[pos++] = set2[k2];
          break;
        }
        if(k2>=set2.length) {
          for(; k1<set1.length;++k1)
            buffer[pos++] = set1[k1];
          break;
        }
      } else {//if (set1[k1]>set2[k2]) {
        buffer[pos++] = set2[k2];
        ++k2;
        if(k2>=set2.length) {
          for(; k1<set1.length;++k1)
            buffer[pos++] = set1[k1];
          break;
        }
      }
    }
    return Arrays.copyOf(buffer, pos);
  }


  public static int[] intersect(int[]... set) {
    if(set.length == 0) throw new RuntimeException("nothing");
    int[] answer = set[0];
    int[] buffer = new int[32];
    for(int k = 1; k<set.length;++k) {
      if(buffer.length<answer.length+set[k].length)
        buffer =  new int[answer.length+set[k].length];
      answer = intersect(answer, set[k], buffer);
    }
    return answer;
  }

  public static int[] intersect2by2(final int[] set1, final int[] set2, final int[] buffer) {
    int pos = 0;
    for(int k1 = 0, k2 = 0; k1 <set1.length; ++k1) {
      while(set2[k2]<set1[k1] && (k2+1 < set2.length)) {
        ++k2;          
      }
      if(k2 < set2.length) {
        if(set2[k2]==set1[k1]) {
          buffer[pos++] = set1[k1];
        }
      } else break;
    }
    return Arrays.copyOf(buffer, pos);
  }
  
}
