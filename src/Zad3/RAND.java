package Zad3;

import java.util.List;
import java.util.Random;

public class RAND implements ReplacePage {

    int pageFaults;

    @Override
    public void simulate(int frameNumber, List<Page> callString) {
        Page[] frames = new Page[frameNumber];
        pageFaults = 0;
        Random rand = new Random();
        boolean shouldLookForFrame;

        for (Page p : callString) {
            shouldLookForFrame = true;
            for (int i = 0; i < frames.length; i++) {
                if (frames[i] != null && frames[i].equals(p)) {
                    shouldLookForFrame = false;     //gdy strona jest w ramce nie trzeba szukac dla niej ramki
                }
            }
            if(shouldLookForFrame){
                pageFaults++;
                for (int i = 0; i < frames.length; i++) {
                    if (frames[i] == null) {
                        frames[i] = p;
                        shouldLookForFrame = false;
                        break;
                    }
                }
                if(shouldLookForFrame){
                    frames[rand.nextInt(frames.length)] = p;
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
        System.out.println("RAND page faults: " + pageFaults);
    }
}
