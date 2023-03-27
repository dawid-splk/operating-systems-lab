package Zad5;

import java.util.ArrayList;
import java.util.Random;

public class Zad5 {

    ArrayList<Processor> allProcessors;
    ServeProcesses algorithm;
    int processNumber;

    public static void main(String[] args) {
        Zad5 test = new Zad5(20,4000);
        test.setAlgorithm(new GreedyAlg(70));
        test.runSimulation();
        test.setAlgorithm(new HardWorkingAlg(70));
        test.runSimulation();
        test.setAlgorithm(new HelpfulAlg(30,70));
        test.runSimulation();
    }

    private void runSimulation() {
        algorithm.simulate(allProcessors);
    }

    public Zad5(int numberOfProcessors, int numberOfProcesses){
        processNumber = numberOfProcesses;
        allProcessors = new ArrayList<>();
        Random rand = new Random();

        for (int i = 0; i < numberOfProcessors; i++) {
            allProcessors.add(new Processor((byte) rand.nextInt(0,2)));
        }

        int i = 0;
        while(numberOfProcesses > 0) {
            i++;
            for (Processor proc : allProcessors) {
                if(i % proc.getPause() == 0 && numberOfProcesses > 0){
                    proc.getAllProcesses().add(new Process(proc));
                    numberOfProcesses--;
                }
            }
        }
    }

    public void setAlgorithm(ServeProcesses algorithm) {
        this.algorithm = algorithm;
    }
}
