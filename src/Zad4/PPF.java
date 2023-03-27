package Zad4;

import java.util.ArrayList;
import java.util.Iterator;

public class PPF implements AlotFrames{

    int timeRange;
    int max;
    int min;
    int unusedFrames;

    public PPF(int timeRange, int min, int max){
        this.timeRange = timeRange;
        this.min = min;
        this.max = max;
    }

    @Override
    public void simulate(int frameNumber, ArrayList<Process> processes, ArrayList<Page> globalCallString) {
        int time = 0;
        LRU lru = new LRU();
        int temp = frameNumber;
        double pageSum = 0;
        boolean serveGlobalQueue;
        int freezings = 0;

        for (Process proc : processes) {
            pageSum += proc.getPagesQuantity();
        }

        for (Process proc : processes) {
            double fraction = proc.getPagesQuantity() / pageSum;
            proc.setFramesGotten((int) (fraction * frameNumber));
            temp -= proc.getFramesGotten();
        }

        int max=0;
        int idx=0;
        for (int i = 0; i < processes.size(); i++) {
            if(processes.get(i).getPagesQuantity() > max){
                max = processes.get(i).getPagesQuantity();
                idx = i;
            }
        }
        processes.get(idx).setFramesGotten(processes.get(idx).getFramesGotten() + temp);

        for (Process proc : processes) {
            proc.setFramesArray(proc.getFramesGotten());
        }

        unusedFrames = 0;
        ArrayList<Page> frozenCalls = new ArrayList<>();
        ArrayList<Process> frozenProcesses = new ArrayList<>();
        Iterator<Page> iter = globalCallString.iterator();
        int thrashing = 0;

        while(!frozenCalls.isEmpty() || iter.hasNext()){
            time++;
            if(time % timeRange == 0){                          //co pewien czas sprawdza czy czy przybyle bledy sa
//                System.out.println("\n\nTime: " + time);      //wieksze/mniejsze niz max/min i ustawia STAN
                for (Process proc : processes) {
                    if (!proc.isFrozen()) {
                        if (proc.getPageFaults() - proc.getLastCheck() > this.max) {
                            proc.setUtil(1);
                            thrashing++;
                        } else {
                            if (proc.getPageFaults() - proc.getLastCheck() < min) {
                                proc.setUtil(2);
                                proc.reduceFramesArray();       //1. zmniejszanie ilosci ramek procesow
                                unusedFrames++;                 //  (zwieksza sie unusedFrames)
                            } else {
                                proc.setUtil(0);
                            }
                        }
                        proc.setLastCheck(proc.getPageFaults());
                    }
                }
                ArrayList<Process> frozenTemp = new ArrayList<>(frozenProcesses);
                for (Process proc : frozenTemp){       //2. wskrzeszanie zamrozonych procesow gdy sa ramki
                    if(unusedFrames >= proc.getFramesArray().length + 1){    //  (zmniejsza sie unusedFrames)
                        frozenProcesses.remove(proc);
                        proc.setFrozen(false);
//                        System.out.println("\n\nDefrosted process " + proc.getNumber());
                        proc.increaseFramesArray();
                        unusedFrames -= proc.getFramesArray().length;
                        proc.setUtil(0);
                    }
                }
                for (Process proc : processes){             //3. zwiekszanie ilosci ramek lub zamrazanie procesow
                    if(!proc.isFrozen()) {
                        if (proc.getUtil() == 1) {               //  (zmniejsza sie unusedFrames)
                            if (unusedFrames > 0) {
                                proc.increaseFramesArray();
                                unusedFrames--;
                            } else {
                                proc.setFrozen(true);
                                unusedFrames += proc.getFramesArray().length;
                                frozenProcesses.add(proc);
                                freezings++;
//                                System.out.println("\n\nFroze process " + proc.getNumber());
                            }
                        }
                    }
                }
            }

            serveGlobalQueue = true;
            ArrayList<Page> frozenTemp = new ArrayList<>(frozenCalls);
            for (Page page : frozenTemp) {
                if (!page.getProcess().isFrozen()) {
                    lru.servePageLRU(page.getProcess(), page);
                    frozenCalls.remove(page);
                    showChanges(page, frozenProcesses, frozenCalls);
//                    time++;
                    serveGlobalQueue = false;
                    break;
                }
            }

            if(serveGlobalQueue && iter.hasNext()){
                Page page = iter.next();
                while(page.getProcess().isFrozen() && iter.hasNext()){
                    frozenCalls.add(page);
                    page = iter.next();
                }
                lru.servePageLRU(page.getProcess(), page);
                showChanges(page, frozenProcesses, frozenCalls);
//                time++;
            }
        }
        System.out.println("\n\nFreezings: " + freezings);
        System.out.println("Occurances of trashing: " + thrashing);
    }

    private void showChanges(Page page, ArrayList<Process> frozenProcesses, ArrayList<Page> frozenCalls){
//        Process proc = page.getProcess();
//        if(proc.getNumber() == 0){
//            System.out.print("\n");
//            for (int i = 0; i < proc.getFramesArray().length; i++) {
//                Page pg = proc.getFramesArray()[i];
//                if(pg != null){
//                    System.out.printf("[ %5s ]\t", pg);
//                } else {
//                    System.out.printf("[ %5s ]\t", "");
//                }
//            }
//        }
//        System.out.println("\n\nFrozen processes: ");
//        for (Process frozenProcess : frozenProcesses) {
//            System.out.print(frozenProcess + "\t");
//        }
//        System.out.println("\n\nFrozen calls: ");
//        for (Page frozenCall : frozenCalls) {
//            System.out.print(frozenCall + "\t");
//        }
    }
}
