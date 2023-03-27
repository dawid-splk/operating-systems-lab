package Zad4;

import Zad3.LRU;
import Zad3.ReplacePage;

import java.util.ArrayList;
import java.util.Random;

public class Zad4 {

    int frameNumber;
    ArrayList<Process> processes;
    ArrayList<Page> globalCallString;
    AlotFrames alotAlgorithm;
    ReplacePage swapAlgorithm;
    Random rand = new Random();

    public Zad4(int howManyFrames, int howManyProcesses, int pagesLowerBound, int pagesUpperBound, int singularCallStringLength){
        frameNumber = howManyFrames;
        swapAlgorithm = new LRU();
        processes = new ArrayList<>();
        globalCallString = new ArrayList<>();
        createProcesses(howManyProcesses, pagesLowerBound, pagesUpperBound, singularCallStringLength);
        createGlobalCallString(5);
    }

    public static void main(String[] args) {
        Zad4 test = new Zad4(40,4,35,45,100);
        for (int i = 0; i < test.globalCallString.size(); i++) {
            System.out.print(test.globalCallString.get(i) + "\t");
        }
        test.setAlotAlgorithm(new EQ(4, 40));
        test.runSimulation();
        test.setAlotAlgorithm(new PROP(4, 40));
        test.runSimulation();
        test.setAlotAlgorithm(new PPF(40, 3, 4));
        test.runSimulation();
        test.setAlotAlgorithm(new WWS(4,40));
        test.runSimulation();

    }

    public void setAlotAlgorithm(AlotFrames alotAlgorithm) {
        this.alotAlgorithm = alotAlgorithm;
    }

    private void runSimulation(){
        alotAlgorithm.simulate(frameNumber, processes, globalCallString);
        System.out.print("\nStats for " + alotAlgorithm.getClass().getName() + ":");
        for (Process proc : processes) {
            System.out.print("\nPage faults for process " + proc.getNumber() + "(" + proc.getProcessCalls().size()
                                + " calls/" + proc.getPagesQuantity() + " pages): " + proc.getPageFaults());
            proc.setPageFaults(0);
            proc.setFramesGotten(0);
            proc.setUtil(0);
            proc.setFrozen(false);
            proc.setDone(false);
            proc.setLastCheck(0);
        }
        for (int i = 0; i < globalCallString.size(); i++) {
            globalCallString.get(i).setUtil(0);
        }
    }

    private void createProcesses(int procNumber, int pagesLBound, int pagesUBound, int csLength){
        for (int i = 0; i < procNumber; i++) {
            Process proc = new Process(i);
            processes.add(proc);
            int calls = csLength + rand.nextInt((int) -(csLength*0.8), (int) (csLength*0.4));
            int pages = rand.nextInt(pagesLBound, pagesUBound);
            proc.setPagesQuantity(pages);
            createSingularCallString(proc, calls, pages);
        }
    }

    private void createGlobalCallString(int space) {
        boolean shouldContinue = true;
        int iter = 0;

        while(shouldContinue){
            shouldContinue = false;
            for (int i = 0; i < processes.size(); i++) {
                for (int j = 0; j < space; j++) {
                    if(iter + j < processes.get(i).getProcessCalls().size()) {
                        globalCallString.add(processes.get(i).getProcessCalls().get(iter + j));
                        shouldContinue = true;
                    }
                }
            }
            iter += space;
        }
    }

    private void createSingularCallString(Process proc, int calls, int pages){
        ArrayList<Page> list = new ArrayList<>();

        for (int i = 0; i < pages; i++) {
            list.add(new Page(proc, i));
        }

        Page[] bracket1 = new Page[pages/3];
        Page[] bracket2 = new Page[pages/3];
        Page[] bracket3 = new Page[pages - 2*pages/3];
        int idx;

        for (int i = 0; i < bracket1.length; i++) {
            idx = rand.nextInt(0,list.size());
            bracket1[i] = list.get(idx);
            list.remove(idx);
        }
        for (int i = 0; i < bracket2.length; i++) {
            idx = rand.nextInt(0,list.size());
            bracket2[i] = list.get(idx);
            list.remove(idx);
        }
        for (int i = 0; i < bracket3.length; i++) {
            idx = rand.nextInt(0,list.size());
            bracket3[i] = list.get(idx);
            list.remove(idx);
        }

        for (int i = 0; i < 0.2 * calls; i++) {
            proc.getProcessCalls().add((bracket1[rand.nextInt(bracket1.length)]));
        }
        for (int i = 0; i < 0.1 * calls; i++) {
            proc.getProcessCalls().add((bracket2[rand.nextInt(bracket2.length)]));
        }
        for (int i = 0; i < 0.3 * calls; i++) {
            proc.getProcessCalls().add((bracket3[rand.nextInt(bracket3.length)]));
        }
        for (int i = 0; i < 0.2 * calls; i++) {
            proc.getProcessCalls().add((bracket2[rand.nextInt(bracket2.length)]));
        }
        for (int i = 0; i < 0.1 * calls; i++) {
            proc.getProcessCalls().add((bracket1[rand.nextInt(bracket1.length)]));
        }
        int rest = (int) (calls - 2*(calls*0.1) - 2*(calls*0.2) - (calls*0.3));
        for (int i = 0; i < rest; i++) {
            proc.getProcessCalls().add((bracket3[rand.nextInt(bracket3.length)]));
        }

        System.out.print("Call string: ");
        for (int i = 0; i < proc.getProcessCalls().size(); i++) {
            System.out.print(proc.getProcessCalls().get(i).getPageNumber() + "\t");
        }
        System.out.print("\n");
    }

}
