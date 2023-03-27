package Zad4;

import java.util.ArrayList;
import java.util.Iterator;

public class WWS implements AlotFrames {

    int timeRange;
    int max;

    public WWS(int max, int timeRange) {
        this.timeRange = timeRange;
        this.max = max;
    }

    @Override
    public void simulate(int frameNumber, ArrayList<Process> processes, ArrayList<Page> globalCallString) {
        LRU lru = new LRU();
        int time = 0;
        boolean serveGlobalQueue;
        int d;
        int freezings = 0;
        int thrashing = 0;
        ArrayList<Page> frozenCalls = new ArrayList<>();
        Iterator<Page> iter = globalCallString.iterator();

        for (int i = 0; i < frameNumber; i++) {
            processes.get(i % processes.size()).increaseFrames();
        }
        for (int i = 0; i < processes.size(); i++) {
            processes.get(i).setFramesArray(processes.get(i).getFramesGotten());
        }

        while (!frozenCalls.isEmpty() || iter.hasNext()) {
            time++;
            d = 0;
            if (time % timeRange == 0) {
                for (Process proc : processes) {
                    if (!proc.isFrozen()) {
                        if (proc.getPageFaults() - proc.getLastCheck() > this.max) {
                            thrashing++;
                        }
                        proc.setLastCheck(proc.getPageFaults());
                    }
                }
                for (Process proc : processes) {
                    if(proc.isFrozen()){
                        proc.setFrozen(false);
                        proc.setUtil(proc.getUtil());
                    }
                    for (int i = 0; i < proc.getRecentlyUsed().size(); i++) {
                        if (!proc.getWorkingSet().contains(proc.getRecentlyUsed().get(i))) {
                            proc.getWorkingSet().add(proc.getRecentlyUsed().get(i));
                        }
                    }
                    if(!proc.isDone()) {
                        proc.setUtil(proc.getWorkingSet().size());
                        d += proc.getUtil();
                        proc.getWorkingSet().clear();
                    } else {
                        proc.setUtil(0);
                        proc.getWorkingSet().clear();
                    }
                }

                if (d <= frameNumber) {
                    for (Process proc : processes) {
                        proc.resizeArray(proc.getUtil());
                    }
                } else {
                    while (d > frameNumber) {
                        int max = 0;
                        int idx = 0;
                        for (int i = 0; i < processes.size(); i++) {
                            if (!processes.get(i).isFrozen() && processes.get(i).getUtil() > max) {
                                max = processes.get(i).getUtil();
                                idx = i;
                            }
                        }
                        processes.get(idx).setFrozen(true);
                        freezings++;
                        d -= processes.get(idx).getUtil();
                    }
                    int rest = frameNumber - d;
                    double sum = 0;
                    for(Process proc : processes){
                        if(!proc.isFrozen())
                            sum += proc.getUtil();
                    }
                    for (Process proc : processes) {
                        if(!proc.isFrozen()) {
                            double fraction = proc.getUtil() / sum;
                            proc.setUtil((int) (proc.getUtil() + (fraction * (frameNumber - d))));
                            rest -= (int) (fraction * (frameNumber - d));
                        }
                    }

                    int max=0;
                    int idx=0;
                    for (int i = 0; i < processes.size(); i++) {
                        if(!processes.get(i).isFrozen() && processes.get(i).getUtil() > max){
                            max = processes.get(i).getUtil();
                            idx = i;
                        }
                    }
                    processes.get(idx).setUtil(processes.get(idx).getUtil() + rest);
                }
                for (Process proc : processes) {
                    if(!proc.isFrozen()){
                        proc.resizeArray(proc.getUtil());
                        proc.setUtil(0);
                    }
                }
            }

            serveGlobalQueue = true;
            ArrayList<Page> frozenTemp = new ArrayList<>(frozenCalls);
            for (Page p : frozenTemp) {
                if (!p.getProcess().isFrozen()) {
                    lru.servePageLRU(p.getProcess(), p);
                    frozenCalls.remove(p);
                    serveGlobalQueue = false;
                    break;
                }
            }

            if(serveGlobalQueue && iter.hasNext()){
                Page page = iter.next();
                while(page.getProcess().isFrozen()){
                    frozenCalls.add(page);
                    if(iter.hasNext()) {
                        page = iter.next();
                        if (page.getProcess().getRecentlyUsed().size() >= 2 * timeRange) {
                            page.getProcess().getRecentlyUsed().remove(0);
                        }
                        page.getProcess().getRecentlyUsed().add(page);
                    } else {
                        break;
                    }
                }
                if(!page.getProcess().isFrozen()) {
                    lru.servePageLRU(page.getProcess(), page);
                    for (Process proc : processes) {
                        if (page.getProcess() == proc) {
                            if (time >= 2 * timeRange) {
                                proc.getRecentlyUsed().remove(0);
                            }
                            proc.getRecentlyUsed().add(page);
                        }
                    }
                }
            }
            if(!iter.hasNext() && serveGlobalQueue){
                for(Process proc : processes){
                    if(!proc.isFrozen()){
                        proc.setDone(true);
                    }
                }
            }
        }
        System.out.println("\n\nFreezings: " + freezings);
        System.out.println("Occurances of trashing: " + thrashing);

    }
}
