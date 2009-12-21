/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bobble.utils;

import java.util.Vector;

/**
 *
 * 
 */
public class MyArrays
{//排序采用了sun jdk java.util.Arrays sort的源码

    private static final int INSERTIONSORT_THRESHOLD = 7;

    public static void sort(Object[] a)
    {
        Object[] aux = new Object[a.length];
        for(int i = 0; i < a.length; i ++)
        {
            aux[i] = a[i];
        }
        mergeSort(aux, a, 0, a.length, 0);
    }
    //归并排序
    private static void mergeSort(Object[] src,
                      Object[] dest,
                      int low,
                      int high,
                      int off)
    {
        int length = high - low;

	// Insertion sort on smallest arrays
        if (length < INSERTIONSORT_THRESHOLD) {
            for (int i=low; i<high; i++)
                for (int j=i; j>low &&
			 ((MyComparable) dest[j-1]).compareTo(dest[j])>0; j--)
                    swap(dest, j, j-1);
            return;
        }

        // Recursively sort halves of dest into src
        int destLow  = low;
        int destHigh = high;
        low  += off;
        high += off;
        int mid = (low + high) >>> 1;
        mergeSort(dest, src, low, mid, -off);
        mergeSort(dest, src, mid, high, -off);

        // If list is already sorted, just copy from src to dest.  This is an
        // optimization that results in faster sorts for nearly ordered lists.
        if (((MyComparable)src[mid-1]).compareTo(src[mid]) <= 0) {
            System.arraycopy(src, low, dest, destLow, length);
            return;
        }

        // Merge sorted halves (now in src) into dest
        for(int i = destLow, p = low, q = mid; i < destHigh; i++) {
            if (q >= high || p < mid && ((MyComparable)src[p]).compareTo(src[q])<=0)
                dest[i] = src[p++];
            else
                dest[i] = src[q++];
        }
    }

    /**
     * Swaps x[a] with x[b].
     */
    private static void swap(Object[] x, int a, int b)
    {
        Object t = x[a];
        x[a] = x[b];
        x[b] = t;
    }

   public static void sort(Vector list)
   {
        Object[] a = new Object[list.size()];
        list.copyInto(a);
        sort(a);
        for (int j=0; j<a.length; j++)
        {
            list.setElementAt(a[j], j);
        }
    }
}
