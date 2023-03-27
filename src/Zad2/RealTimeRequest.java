package Zad2;

public class RealTimeRequest extends Request {

    int deadLine;

    public RealTimeRequest(int diskSize) {
        super(diskSize);
        deadLine = rand.nextInt((int) (0.5*diskSize),(int) (1.2*diskSize));

    }

    public RealTimeRequest(RealTimeRequest rt){
        super(rt);
        deadLine = rt.deadLine;
    }

    public int getDeadLine() {
        return deadLine;
    }

    public void setDeadLine(int deadLine) {
        this.deadLine = deadLine;
    }

    @Override
    public String toString() {
        return "RealTimeRequest{" +
                "position=" + this.getPosition() +
                ", deadLine=" + deadLine +
                ", arrivalTime=" + this.getArrivalTime() +
                '}';
    }
}
