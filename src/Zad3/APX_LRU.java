package Zad3;

import java.util.ArrayList;
import java.util.List;

public class APX_LRU implements ReplacePage {

    int pageFaults;


    @Override
    public void simulate(int frameNumber, List<Page> callString) {
        ArrayList<Page> queue = new ArrayList<>();
        Page[] frames = new Page[frameNumber];
        pageFaults = 0;
        boolean shouldLookForFrame;

        for (Page p : callString) {
            shouldLookForFrame = true;
            for (int i = 0; i < frames.length; i++) {
                if(frames[i] != null && frames[i].equals(p)){
                    shouldLookForFrame = false;
                    for (int j = 0; j < queue.size(); j++) {
                        if(queue.get(j).equals(p)){
                            queue.get(j).setUtil(1);
                        }
                    }
                }
            }
            if(shouldLookForFrame) {
                pageFaults++;
                for (int i = 0; i < frames.length; i++) {
                    if (frames[i] == null) {
                        frames[i] = p;
                        p.setUtil(1);
                        queue.add(p);
                        shouldLookForFrame = false;
                        break;
                    }
                }
                while (shouldLookForFrame) {
                    if(!queue.isEmpty()) {
                        if (queue.get(0).getUtil() == 0) {
                            p.setUtil(1);
                            queue.add(p);
                            for (int i = 0; i < frames.length; i++) {
                                if(frames[i] != null && frames[i].equals(queue.get(0)))
                                    frames[i] = p;
                            }
                            queue.remove(0);
                            shouldLookForFrame = false;
                        } else {
                            queue.get(0).setUtil(0);
                            queue.add(queue.get(0));
                            queue.remove(0);
                        }
                    }
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
        System.out.println("Aproximate LRU page faults: " + pageFaults);
    }
}
