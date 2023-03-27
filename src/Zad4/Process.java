package Zad4;

import java.util.ArrayList;

public class Process {

    private int number;
    private ArrayList<Page> processCalls;
    private int pagesQuantity;
    private int framesGotten;
    private Page[] framesArray;
    private int pageFaults;

    private boolean frozen;
    private int lastCheck;
    private int util;       //dla PPF: 0 - bez zmian, 1 - za malo ramek, 2 - za duzo ramek
                            //dla WWS: rozmiar zbioru roboczego
    private ArrayList<Page> recentlyUsed;
    private ArrayList<Page> workingSet;
    private boolean done;

    public Process(int number){
        processCalls = new ArrayList<>();
        this.number = number;
        pageFaults = 0;
        frozen = false;
        lastCheck = 0;
        util = 0;
        recentlyUsed = new ArrayList<>();
        workingSet = new ArrayList<>();
        done = false;
    }

    public void reduceFramesArray(){
        LRU lru = new LRU();
        int i = 0;
        lru.throwOutPage(this);
        Page[] newFrames = new Page[framesArray.length - 1];
        for (int j = 0; j < framesArray.length; j++) {
            if(framesArray[j] != null){
                newFrames[i] = framesArray[j];
                i++;
            }
        }
//        System.out.print("\nReduced frames for process " + this.number + " from " + framesArray.length + " to " + newFrames.length);
        framesGotten = newFrames.length;
        framesArray = newFrames;
    }

    public void increaseFramesArray(){
        Page[] newFrames = new Page[framesArray.length + 1];
        for (int j = 0; j < framesArray.length; j++) {
            newFrames[j] = framesArray[j];
        }
//        System.out.print("\nIncreased frames for process " + this.number + " from " + framesArray.length + " to " + newFrames.length);

        framesGotten = newFrames.length;
        framesArray = newFrames;
    }

    public void resizeArray(int newSize){
        LRU lru = new LRU();
        int i = 0;
        Page[] newFrames;

        if(newSize == framesArray.length)
            return;

        if(newSize < framesArray.length) {
            int n = framesArray.length - newSize;
            lru.throwOutPages(this, n);     //poprawic by robilo miejsce na n nullów a nie jeden i ten sam

            newFrames = new Page[newSize];
            for (int j = 0; j < framesArray.length; j++) {
                if (framesArray[j] != null) {
                    newFrames[i] = framesArray[j];
                    i++;
                }
            }
        } else {
//            int n = newSize - framesArray.length;
            newFrames = new Page[newSize];
            for (int j = 0; j < framesArray.length; j++) {
                newFrames[j] = framesArray[j];
            }
        }
//        System.out.print("\nReduced frames for process " + this.number + " from " + framesArray.length + " to " + newFrames.length);
        framesGotten = newFrames.length;
        framesArray = newFrames;
    }

    public void increaseFramesArray(int n){
        Page[] newFrames = new Page[framesArray.length + n];
        for (int j = 0; j < framesArray.length; j++) {
            newFrames[j] = framesArray[j];
        }
//        System.out.print("\nIncreased frames for process " + this.number + " from " + framesArray.length + " to " + newFrames.length);

        framesGotten = newFrames.length;
        framesArray = newFrames;
    }

    public ArrayList<Page> getProcessCalls() {
        return processCalls;
    }

    public int getFramesGotten() {
        return framesGotten;
    }

    public void setFramesGotten(int framesGotten) {
        this.framesGotten = framesGotten;
    }

    public void increaseFrames(){
        framesGotten = framesGotten + 1;
    }

    public Page[] getFramesArray() {
        return framesArray;
    }

    public void setFramesArray(int howManyFrames) {
        this.framesArray = new Page[howManyFrames];
    }

    public int getPageFaults() {
        return pageFaults;
    }

    public void setPageFaults(int pageFaults) {
        this.pageFaults = pageFaults;
    }

    public int getNumber() {
        return number;
    }

    public int getPagesQuantity() {
        return pagesQuantity;
    }

    public void setPagesQuantity(int pagesQuantity) {
        this.pagesQuantity = pagesQuantity;
    }

    public boolean isFrozen() {
        return frozen;
    }

    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
    }

    public int getLastCheck() {
        return lastCheck;
    }

    public void setLastCheck(int lastCheck) {
        this.lastCheck = lastCheck;
    }

    public int getUtil() {
        return util;
    }

    public void setUtil(int util) {
        this.util = util;
    }

    public ArrayList<Page> getRecentlyUsed() {
        return recentlyUsed;
    }

    public ArrayList<Page> getWorkingSet() {
        return workingSet;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String toString(){
        return "P" + number;
    }
}
