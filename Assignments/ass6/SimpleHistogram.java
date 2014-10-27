// For week 3
// sestoft@itu.dk * 2014-09-04

import javax.annotation.concurrent.GuardedBy;

class SimpleHistogram {
    public static void main(String[] args) {
//        final Histogram histogram = new Histogram1(30);
//        histogram.increment(7);
//        histogram.increment(13);
//        histogram.increment(7);
//        dump(histogram);
    }

    public static void dump(Histogram histogram) {
        int totalCount = 0;
        for (int item=0; item<histogram.getSpan(); item++) {
            System.out.printf("%4d: %9d%n", item, histogram.getCount(item));
            totalCount += histogram.getCount(item);
        }
        System.out.printf("      %9d%n", totalCount);
    }
}

interface Histogram {
    public void increment(int item);
    public int getCount(int item);
    public int getSpan();
    public void addAll(Histogram2 hist);
}

//class Histogram1 implements Histogram {
//    private int[] counts;
//    public Histogram1(int span) {
//        this.counts = new int[span];
//    }
//    public void increment(int item) {
//        counts[item] = counts[item] + 1;
//    }
//    public int getCount(int item) {
//        return counts[item];
//    }
//    public int getSpan() {
//        return counts.length;
//    }
//}

class Histogram2 implements Histogram {
    @GuardedBy("this")
    private final int[] counts;

    public Histogram2(int span) {
        this.counts = new int[span];
    }

    public synchronized void increment(int item) {
        counts[item] = counts[item] + 1;
    }

    public synchronized int getCount(int item) {
        return counts[item];
    }
    public int getSpan() {
        return counts.length;
    }

//    @Override
//    public void addAll(Histogram hist) {
//        if (hist.getSpan() != getSpan()) {
//            throw new RuntimeException();
//        }
//
//        synchronized (this) {
//            for(int i = 0; i < counts.length; i++) {
//                counts[i] += hist.getCount(i);
//            }
//        }
//    }

    @Override
    public void addAll(Histogram2 hist) {
        if (hist.getSpan() != getSpan()) {
            throw new RuntimeException();
        }

        synchronized (this) {
            for(int i = 0; i < counts.length; i++) {
                counts[i] += hist.counts[i];
            }
        }
    }
}

