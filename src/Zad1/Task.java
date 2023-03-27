package Zad1;

import java.util.Random;

public class Task {

    private int duration;
    private int waitingTime;
    private int arrivalTime;
    private int rounds;


    Random rand = new Random();

    public Task(byte category, int arrivalTime){
        switch (category) {
            case 0 -> duration = rand.nextInt(1, 30);
            case 1 -> duration = rand.nextInt(1, 5);
            case 2 -> duration = rand.nextInt(25, 30);
        }
        this.arrivalTime = arrivalTime;
        this.rounds = 0;
    }

    public Task(Task t){
        this.duration = t.duration;
        this.arrivalTime = t.arrivalTime;
        this.rounds = t.rounds;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(int arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public int getRounds() {
        return rounds;
    }

    public void setRounds(int rounds) {
        this.rounds = rounds;
    }

    @Override
    public String toString() {
        return "Task{" +
                "duration=" + duration +
                ", waitingTime=" + waitingTime +
                ", arrivalTime=" + arrivalTime +
                '}';
    }
}
