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
    if(set.length == 1) return set[0];
    int[] buffer = new int[minlength(set)];
    int[] answer = set[0];
    int answerlength = answer.length;
	answerlength = intersect2by2(set[0],answerlength, set[1],set[1].length, buffer);
    for(int k = 2; k<set.length;++k) {
    	answerlength = intersect2by2(buffer, answerlength, set[k],set[k].length, buffer);
    }
    return Arrays.copyOf(buffer, answerlength);
  }

  /**
   * 
   * @param set1
   * @param set2
   * @param buffer
   * @return how much of the buffer is used for the intersection
   */
  public static int intersect2by2(final int[] set1, final int length1, final int[] set2, final int length2, final int[] buffer) {
    int pos = 0;
    for(int k1 = 0, k2 = 0; k1 <length1; ++k1) {
      while(set2[k2]<set1[k1] && (k2+1 < length2)) {
        ++k2;          
      }
      if(k2 < length2) {
        if(set2[k2]==set1[k1]) {
          buffer[pos++] = set1[k1];
        }
      } else break;
    }
    return pos;
  }

	public static int maxlength(int[]... set) {
		int m = 0;
		for (int k = 0; k < set.length; ++k)
			if (m < set[k].length)
				m = set[k].length;
		return m;
	}
	public static int minlength(int[]... set) {
		int m = set[0].length;
		for (int k = 1; k < set.length; ++k)
			if (m > set[k].length)
				m = set[k].length;
		return m;
	}


	public static int[] frogintersect(int[]... set) {
		if (set.length == 0)
			throw new RuntimeException("nothing");
		if (set.length == 1)
			return set[0];
		int[] answer = set[0];
		int answerlength = answer.length;
		int[] buffer = new int[minlength(set)];
		answerlength = frogintersect2by2(set[0],answerlength, set[1],set[1].length, buffer);
		for (int k = 2; k < set.length; ++k) {
			answerlength = frogintersect2by2(buffer,answerlength, set[k],set[k].length, buffer);
		}
		return Arrays.copyOf(buffer, answerlength);
	}
	
	/**
	 * 
	 * @param set1
	 * @param set2
	 * @param buffer
	 * @return how much of the buffer is used for the intersection
	 */
	public static int frogintersect2by2(final int[] set1, final int length1, final int[] set2,
			final int length2, 
			final int[] buffer) {
		if ((0 == length1) || (0 == length2))
			return 0;
		int k1 = 0;
		int k2 = 0;
		int pos = 0;
		mainwhile: while (true) {
			if (set1[k1] < set2[k2]) {
				k1 = advanceUntil(set1,k1,set2[k2]);
				if (k1 == set1.length)
					break mainwhile;
			}
			if (set2[k2] < set1[k1]) {
				k2 = advanceUntil(set2,k2,set1[k1]);
				if (k2 == set2.length)
						break mainwhile;
			} else {
				// (set2[k2] == set1[k1])
				buffer[pos++] = set1[k1];
				++k1;
				if (k1 == length1)
					break;
				++k2;
				if (k2 == length2)
					break;

			}

		}
		return pos;
	}

	/**
	 * Find the smallest integer larger than pos such 
	 * that array[pos]>= min.
	 * If none can be found, return array.length.
	 * Based on code by O. Kaser.
	 * 
	 * @param array
	 * @param pos
	 * @param min
	 * @return
	 */
	public static int advanceUntil(int[] array, int pos, int min) {
		int lower = pos+1;
		
		// special handling for a possibly common sequential case
		if (lower >= array.length || array[lower] >= min) {
		    return lower;
		}

		int spansize=1;  // could set larger
		// bootstrap an upper limit
	       
		while (lower+spansize < array.length && array[lower+spansize] < min) 
		    spansize *= 2;  // hoping for compiler will reduce to shift
		int upper = (lower+spansize < array.length) ? lower+spansize : array.length-1;
		
		// maybe we are lucky (could be common case when the seek ahead expected to be small and sequential will otherwise make us look bad)
		if (array[upper] == min) {
		    return upper;
		}
		
		if (array[upper] < min) {// means array has no item >= min
		    //pos = array.length;
		    return array.length;
		}

		// we know that the next-smallest span was too small
		lower += (spansize/2);

		// else begin binary search
		// invariant: array[lower]<min && array[upper]>min
		int mid=0;
		while (lower+1 != upper) {
		    mid = (lower+upper)/2;
		    if (array[mid] == min) {
			return mid;
		    } else
			if (array[mid] < min) 
			    lower = mid;
			else
			    upper = mid;
		}
		return upper;

	}
 
}
