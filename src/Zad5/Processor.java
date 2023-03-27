package Zad5;

import java.util.ArrayList;
import java.util.Random;

public class Processor {

    private int load;
    private byte type;
    private int pause;
    private ArrayList<Process> allProcesses;
    private ArrayList<Process> workingProcesses;
    private int overloadTime;
    Random rand = new Random();


    public Processor(byte type){
        allProcesses = new ArrayList<>();
        workingProcesses = new ArrayList<>();
        this.type = type;
        switch(type){
            case 0:
                pause = rand.nextInt(4,7);
                break;
            case 1:
                pause = rand.nextInt(7, 10);
                break;
            default:
                pause = rand.nextInt(4,10);
                break;
        }
    }

    public Processor(Processor proc){
        type = proc.type;
        pause = proc.pause;
        workingProcesses = new ArrayList<>();
        overloadTime = 0;
        allProcesses = new ArrayList<>();

        for (int i = 0; i < proc.allProcesses.size(); i++) {
            allProcesses.add(new Process(proc.allProcesses.get(i)));
        }
    }

    public void startNewProcess(Process p){
        workingProcesses.add(p);
        load += p.getLoad();
    }

    public void startInitialProcesses(){
        for (int i = 0; i < 2; i++) {
            workingProcesses.add(allProcesses.get(0));
            load += allProcesses.get(0).getLoad();
            allProcesses.remove(0);
        }
    }

    public byte getType() {
        return type;
    }

    public ArrayList<Process> getAllProcesses() {
        return allProcesses;
    }

    public int getPause() {
        return pause;
    }

    public ArrayList<Process> getWorkingProcesses() {
        return workingProcesses;
    }

    public int getLoad() {
        return load;
    }

    public void setLoad(int load) {
        this.load = load;
    }

    public int getOverloadTime() {
        return overloadTime;
    }

    public void setOverloadTime(int overloadTime) {
        this.overloadTime = overloadTime;
    }
}
