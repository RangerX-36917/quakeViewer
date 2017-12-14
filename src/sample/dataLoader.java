package sample;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class dataLoader {
    public ArrayList<earthQuake> quakes = new ArrayList<>();

    public void printElement() {
        Iterator iter = quakes.iterator();
        while (iter.hasNext()) {
            System.out.println(iter.next().toString());
        }
    }

    public dataLoader () {
        long startTime = System.currentTimeMillis();
        loadData();
        long endTime = System.currentTimeMillis();
        Collections.sort(quakes, new magnitudeComparator());
        //printElement(quakes);

        System.out.println(endTime - startTime + "ms");
        System.out.println(quakes.size());
    }
    private void loadData() {
        String word;
        int count = 0;

        try {
            Tokenizer tok = new Tokenizer("earthquakes.csv");
            for (int i = 0; i < 7; i++) {
                word = tok.nextToken();
                System.out.println(word);
            }

            while ((word = tok.nextToken()) != null) {
                earthQuake e = new earthQuake(word, tok.nextToken(), tok.nextToken(), tok.nextToken(), tok.nextToken(), tok.nextToken(), tok.nextToken());
                count++;
                quakes.add(e);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

    }
    static class idComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            earthQuake e1 = (earthQuake) o1;
            earthQuake e2 = (earthQuake) o2;
            return new Integer(e1.getId()).compareTo(new Integer(e2.getId()));
        }
    }
    static class latitudeComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            earthQuake e1 = (earthQuake) o1;
            earthQuake e2 = (earthQuake) o2;
            return new Float(e1.getLatitude()).compareTo(new Float(e2.getLatitude()));
        }
    }
    static class langitudeComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            earthQuake e1 = (earthQuake) o1;
            earthQuake e2 = (earthQuake) o2;
            return new Float(e1.getLongitude()).compareTo(new Float(e2.getLongitude()));
        }
    }
    static class depthComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            earthQuake e1 = (earthQuake) o1;
            earthQuake e2 = (earthQuake) o2;
            return new Float(e1.getDepth()).compareTo(new Float(e2.getDepth()));
        }
    }

    static class magnitudeComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            earthQuake e1 = (earthQuake) o1;
            earthQuake e2 = (earthQuake) o2;
            return new Float(e1.getMagnitude()).compareTo(new Float(e2.getMagnitude()));
        }
    }
    static class dateComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            earthQuake e1 = (earthQuake) o1;
            earthQuake e2 = (earthQuake) o2;
            return e1.getUTC_date().compareTo(e2.getUTC_date());
        }
    }

    static class regionComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            earthQuake e1 = (earthQuake) o1;
            earthQuake e2 = (earthQuake) o2;
            return e1.getRegion().compareTo(e2.getRegion());
        }
    }

}
