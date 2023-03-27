package Zad2;

import java.util.Random;

public class Request {

    private int position;
    private int arrivalTime;
    private int waitingTime;
    private boolean served;

    Random rand = new Random();

//    public Task(int diskSize, int arrivalTime){
//        position = rand.nextInt(0, diskSize);
//        this.arrivalTime = arrivalTime;
//    }

    public Request(byte category, int diskSize, int arrivalTime){
        switch (category) {
            case 0 -> position = rand.nextInt(1, diskSize);
            case 1 -> position = rand.nextInt(0, (int) (diskSize*0.1));
            case 2 -> position = rand.nextInt((int) (diskSize - 0.1*diskSize), diskSize);
        }
        this.arrivalTime = arrivalTime;
        served = false;
    }

    public Request(int diskSize){
        this.position = rand.nextInt(0, (diskSize));
        served = false;
    }

//    public Request(int pos){
//        this.position = pos;
//        this.arrivalTime = 0;
//    }

    public Request(RealTimeRequest rt){
        position = rt.getPosition();
        arrivalTime = rt.getArrivalTime();
    }

    public Request(Request t){
        position = t.position;
        arrivalTime = t.arrivalTime;
        served = t.served;
    }


    public int getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(int arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isServed() {
        return served;
    }

    public void setServed(boolean served) {
        this.served = served;
    }

    @Override
    public String toString() {
        return "Task{" +
                "position=" + position +
                ", waitingTime=" + waitingTime +
                ", arrivalTime=" + arrivalTime +
                '}';
    }
}
