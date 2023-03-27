package Zad4;

import java.util.ArrayList;

public class EQ implements AlotFrames {

    int max;
    int timeRange;

    public EQ(int max, int timeRange){
        this.max = max;
        this.timeRange = timeRange;
    }

    @Override
    public void simulate(int frameNumber, ArrayList<Process> processes, ArrayList<Page> globalCallString) {
        LRU lru = new LRU();

        for (int i = 0; i < frameNumber; i++) {
            processes.get(i % processes.size()).increaseFrames();
        }
        for (int i = 0; i < processes.size(); i++) {
            processes.get(i).setFramesArray(processes.get(i).getFramesGotten());
        }

        int thrashing = 0;
        int time = 0;

        for(Page page : globalCallString){
            if(time % timeRange == 0) {
                for (Process proc : processes) {
                    if (proc.getPageFaults() - proc.getLastCheck() > this.max) {
                        thrashing++;
                    }
                    proc.setLastCheck(proc.getPageFaults());
                }
            }
            Process proc = null;
            for (int i = 0; i < processes.size(); i++) {
                if(page.getProcess() == processes.get(i))       //.equals()?
                    proc = processes.get(i);
            }
            lru.servePageLRU(proc, page);
//            if(proc.getNumber() == 0){
//                System.out.print("\n");
//                for (int i = 0; i < proc.getFramesArray().length; i++) {
//                    Page temp = proc.getFramesArray()[i];
//                    if(temp != null){
//                        System.out.printf("[ %5s ]\t", temp);
//                    } else {
//                        System.out.printf("[ %5s ]\t", "");
//                    }
//                }
//            }
            time++;
        }
        System.out.println("\n\nOccurances of trashing: " + thrashing);
    }
}
