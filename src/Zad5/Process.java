package Zad5;

import java.util.Random;

public class Process {

    private Processor processor;
    private int load;
    private int duration;
    Random rand = new Random();

    public Process(Processor processor) {
        this.processor = processor;
        switch(processor.getType()){
            case 0:
                this.load = rand.nextInt(3,7);
                this.duration = rand.nextInt(50,80);
                break;
            case 1:
                this.load = rand.nextInt(7, 11);
                this.duration = rand.nextInt(80,130);
                break;
            default:
                this.load = rand.nextInt(3,11);
                this.duration = rand.nextInt(5,30);
        }
    }

    public Process(Process proc){
        processor = proc.processor;
        load = proc.load;
        duration = proc.duration;
    }

    public Processor getProcessor() {
        return processor;
    }

    public void setProcessor(Processor processor) {
        this.processor = processor;
    }

    public int getLoad() {
        return load;
    }

    public void setLoad(int load) {
        this.load = load;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
