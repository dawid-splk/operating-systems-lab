package Zad4;

import java.util.ArrayList;

public class PROP implements AlotFrames{

    int max;
    int timeRange;

    public PROP(int max, int timeRange){
        this.max = max;
        this.timeRange = timeRange;
    }

    @Override
    public void simulate(int frameNumber, ArrayList<Process> processes, ArrayList<Page> globalCallString) {
        LRU lru = new LRU();
        double pageSum = 0;
        int temp = frameNumber;

        for (int i = 0; i < processes.size(); i++) {
            pageSum += processes.get(i).getPagesQuantity();
        }

        for (int i = 0; i < processes.size(); i++) {
            double fraction = processes.get(i).getPagesQuantity()/pageSum;
            processes.get(i).setFramesGotten((int) (fraction*frameNumber));
            temp -= processes.get(i).getFramesGotten();
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
//                    Page pg = proc.getFramesArray()[i];
//                    if(pg != null){
//                        System.out.printf("[ %5s ]\t", pg);
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
