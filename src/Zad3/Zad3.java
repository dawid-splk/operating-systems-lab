package Zad3;

import java.util.ArrayList;
import java.util.Random;

public class Zad3 {

    ArrayList<Page> callString = new ArrayList<>();
    int frames;
    int pageNumber;
    ReplacePage simulation;
    Random rand = new Random();

    public static void main(String[] args) {
        Zad3 test = new Zad3(51, 50, 1000, new OPT());
        test.simulate();
        test.simulation = new FIFO();
        test.simulate();
        test.simulation = new LRU();
        test.simulate();
        test.simulation = new RAND();
        test.simulate();
        test.simulation = new APX_LRU();
        test.simulate();
    }

    public Zad3(int numberOfFrames, int numberOfPages, int callStringLength, ReplacePage algorithm){
        frames = numberOfFrames;
        pageNumber = numberOfPages;
        createCallString(numberOfPages, callStringLength);
        simulation = algorithm;
    }

    private void simulate(){
        simulation.simulate(frames, callString);
        for (int i = 0; i < callString.size(); i++) {
            callString.get(i).setUtil(0);
        }
    }

    private void createCallString(int pages, int calls){
        ArrayList<Page> list = new ArrayList<>();

        for (int i = 0; i < pages; i++) {
            list.add(new Page(i));
        }

        for (int i = 0; i < 0.2 * calls; i++) {
            callString.add(list.get(rand.nextInt(list.size())));
        }

        Page[] bracket1 = new Page[pages/2];
        Page[] bracket2 = new Page[pages - pages/2];
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

        for (int i = 0; i < 0.1 * calls; i++) {
            callString.add((bracket1[rand.nextInt(bracket1.length)]));
        }
        for (int i = 0; i < 0.2 * calls; i++) {
            callString.add((bracket2[rand.nextInt(bracket2.length)]));
        }
        for (int i = 0; i < 0.1 * calls; i++) {
            callString.add((bracket1[rand.nextInt(bracket1.length)]));
        }
        for (int i = 0; i < 0.2 * calls; i++) {
            callString.add((bracket2[rand.nextInt(bracket2.length)]));
        }
        for (int i = 0; i < 0.2 * calls; i++) {
            callString.add((bracket1[rand.nextInt(bracket1.length)]));
        }

        System.out.print("Call string: ");
        for (int i = 0; i < callString.size(); i++) {
            System.out.print(callString.get(i).getPageNumber() + "\t");
        }
        System.out.print("\n");
    }
}
