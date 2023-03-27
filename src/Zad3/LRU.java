package Zad3;

import java.util.List;

public class LRU implements ReplacePage {

    int pageFaults;

    @Override
    public void simulate(int frameNumber, List<Page> callString) {
        Page[] frames = new Page[frameNumber];
        pageFaults = 0;
        int longestWait;
        int idx = 0;
        boolean shouldLookForFrame;

        for (Page p : callString) {
            shouldLookForFrame = true;
            longestWait = 0;
            for (int i = 0; i < frames.length; i++) {
                if (frames[i] != null) {
                    if (frames[i].equals(p)) {
                        frames[i].setUtil(0);           //gdy strona jest w ramce resetujemy jej pole util
                        shouldLookForFrame = false;     //nie trzeba szukac dla niej ramki
                    } else {
                        frames[i].setUtil(frames[i].getUtil() + 1);   //zwiekszanie czasu ostatniego uzycia
                    }
                }
            }
            if (shouldLookForFrame) {
                pageFaults++;
                for (int i = 0; i < frames.length; i++) {
                    if (frames[i] == null) {
                        frames[i] = p;
                        shouldLookForFrame = false;
                        break;
                    } else {
                        if (frames[i].getUtil() > longestWait) {
                            longestWait = frames[i].getUtil();
                            idx = i;
                        }
                    }
                }
                if (shouldLookForFrame) {
                    frames[idx].setUtil(0);
                    frames[idx] = p;
                }
            }
//            for (int i = 0; i < frames.length; i++) {
//                if (frames[i] != null) {
//                    System.out.print(frames[i].getPageNumber() + "\t");
//                } else {
//                    System.out.print("-" + "\t");
//                }
//            }
//            System.out.print("\n");
        }
        System.out.println("LRU page faults: " + pageFaults);
    }
}
