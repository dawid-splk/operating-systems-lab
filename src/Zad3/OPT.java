package Zad3;

import java.util.ArrayList;
import java.util.List;

public class OPT implements ReplacePage {

    int pageFaults;

    @Override
    public void simulate(int frameNumber, List<Page> callString) {
        Page[] frames = new Page[frameNumber];
        pageFaults = 0;
        int maxNotUsed;
        int idx = 0;
        int n = 0;
        boolean shouldLookForFrame;
        boolean notFoundInString = true;
        ArrayList<Page> cs = new ArrayList<>();

        for (int i = 0; i < callString.size(); i++) {
            cs.add(new Page(callString.get(i)));
        }

        for (Page p : callString) {
            n++;
            idx = 0;
            maxNotUsed = 0;
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
                    } else {
                        notFoundInString = true;
                        for (int j = n; j < callString.size(); j++) {
                            if(frames[i].equals(callString.get(j))){
                                notFoundInString = false;
                                if(j > maxNotUsed){
                                    maxNotUsed = j;
                                    idx = i;
                                }
                                break;
                            }
                        }
                        if(notFoundInString){
                            idx = i;
                            maxNotUsed = 999999999;
                        }
                    }
                }
                if(shouldLookForFrame) {
                    frames[idx] = p;
                }
            }

//            for (Page page : frames) {
//                if (page != null) {
//                    System.out.print(page.getPageNumber() + "\t");
//                } else {
//                    System.out.print("-" + "\t");
//                }
//            }
//            System.out.print("\n");
        }
        System.out.println("OPT page faults: " + pageFaults);
    }
}
