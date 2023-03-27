package Zad4;

import java.util.NoSuchElementException;

public class LRU {

    public void servePageLRU(Process proc, Page p){
        if(proc == null)
            throw new NoSuchElementException();

        Page[] frames = proc.getFramesArray();
        boolean shouldLookForFrame = true;
        int longestWait = 0;
        int idx = 0;

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
            proc.setPageFaults(proc.getPageFaults() + 1);
            for (int i = 0; i < frames.length; i++) {
                if (frames[i] == null) {
                    frames[i] = p;
                    shouldLookForFrame = false;
                    return;
                } else {
                    if (frames[i].getUtil() > longestWait) {
                        longestWait = frames[i].getUtil();
                        idx = i;
                    }
                }
            }
            frames[idx].setUtil(0);
            frames[idx] = p;
        }
    }

    public void throwOutPage(Process proc){
        int longestWait = 0;
        int idx = 0;

        for (int i = 0; i < proc.getFramesArray().length; i++) {
            if (proc.getFramesArray()[i] == null) {
                return;
            } else {
                if (proc.getFramesArray()[i].getUtil() > longestWait) {
                    longestWait = proc.getFramesArray()[i].getUtil();
                    idx = i;
                }
            }
        }
        proc.getFramesArray()[idx].setUtil(0);
        proc.getFramesArray()[idx] = null;
    }

    public void throwOutPages(Process proc, int quantity){
        for (int i = 0; i < quantity; i++) {
            proc.getFramesArray()[i] = null;
        }
//        for (int i = proc.getFramesArray().length - 1; i >= proc.getFramesArray().length - quantity; i--) {
//            proc.getFramesArray()[i] = null;
//            System.out.println("frame nulled");
//        }
    }
}
