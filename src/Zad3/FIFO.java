package Zad3;

import java.util.List;

public class FIFO implements ReplacePage {

    int pageFaults;

    @Override
    public void simulate(int frameNumber, List<Page> callString) {
        Page[] frames = new Page[frameNumber];
        pageFaults = 0;
        int maxTime;
        int idx = 0;
        boolean shouldLookForFrame;

        for (Page p : callString) {
            shouldLookForFrame = true;
            maxTime = 0;
            for (int i = 0; i < frames.length; i++) {
                if (frames[i] != null) {
                    frames[i].setUtil(frames[i].getUtil() + 1);   //zwiekszanie czasu w ramce
                    if (frames[i].equals(p))
                        shouldLookForFrame = false;     //gdy strona jest w ramce nie trzeba szukac dla niej ramki
                }
            }
            if(shouldLookForFrame) {
                pageFaults++;
                for (int i = 0; i < frames.length; i++) {
                    if (frames[i] == null) {
                        frames[i] = p;
                        shouldLookForFrame = false;
                        break;
                    } else {
                        if (frames[i].getUtil() > maxTime) {
                            maxTime = frames[i].getUtil();
                            idx = i;
                        }
                    }
                }
                if(shouldLookForFrame){
                    frames[idx].setUtil(0);
                    frames[idx] = p;
                }
            }
//            for (int i = 0; i < frames.length; i++) {
//                if(frames[i] != null){
//                    System.out.print(frames[i].getPageNumber() + "\t");
//                } else {
//                    System.out.print("-" + "\t");
//                }
//            }
//            System.out.print("\n");
        }
        System.out.println("FIFO page faults: " + pageFaults);
    }
}
