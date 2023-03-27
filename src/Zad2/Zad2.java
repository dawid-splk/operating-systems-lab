package Zad2;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class Zad2 {

    int quantity;
    int diskSize;
    int headPosition;
    ArrayList<Request> initialQueue = new ArrayList<>();
    ArrayList<Request> futureTasks = new ArrayList<>();
    ArrayList<RealTimeRequest> rtTasks = new ArrayList<>();
    Random rand = new Random();

    public static void main(String[] args) {
        Zad2 test = new Zad2(100, 250, 2000);
        test.createInitialAndFutureProcesses();
        test.firstComeFirstServed();
        test.shortestSeekTimeFirst();
        test.scan();
        test.cScan();
        test.scanEDF();
        test.scanFDscan();
    }

    public Zad2(int howManyProcesses, int howManyRTProcesses, int diskSize){
        this.quantity = howManyProcesses;
        this.diskSize = diskSize;
        for (int i = 0; i < howManyRTProcesses; i++) {
            rtTasks.add(new RealTimeRequest(diskSize));
        }
    }


//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~First Come First Served~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public void firstComeFirstServed(){
        headPosition = diskSize/2;
        int headTotalDistance = 0;
        int toAdd;              //zmienna, która wskazuje ile nowych procesów dodać - przypisywana jest wart. rand.
        float min = 0.9f;       //zmienna do decydowania czy tworzyć nowy proces -> if(random.nextDouble > min){

        ArrayList<Request> queueFCFS = new ArrayList<>();              //kolejka główna FCFS
        for (Request task : initialQueue){
            queueFCFS.add(new Request(task));
        }
        ArrayList<Request> tasksToAdd = new ArrayList<>();             //lista nowych procesów do dodania
        for (Request futureTask : futureTasks) {
            tasksToAdd.add(new Request(futureTask));
        }


        for (int i = 0; i < queueFCFS.size(); i++) {
            Request t = queueFCFS.get(i);                              //zbliżanie się do wybranego procesu
            while(headPosition != t.getPosition()){
                headTotalDistance++;
                if(t.getPosition() > headPosition){
                    headPosition++;
                } else {
                    headPosition--;
                }

                if(condition(min, tasksToAdd)){                     //dynamiczne dodawanie procesu/procesów
                    toAdd = rand.nextInt(1, 4);
                    for (int j = 0; j < toAdd; j++) {
                        if(!tasksToAdd.isEmpty()) {
                            tasksToAdd.get(0).setArrivalTime(headTotalDistance);
                            queueFCFS.add(tasksToAdd.get(0));
                            tasksToAdd.remove(0);
                        }
                    }
                    min = 0.9f;
                } else {
//                    min -= 0.01;        //zwiększenie prawdopodobieństwa dodania procesu po niespełnieniu warunku
                }
            }
            t.setWaitingTime(headTotalDistance - t.getArrivalTime());
        }

        int sum = 0;
        int i = 0;
        int tempMax = 0;
        for (Request t : queueFCFS){
            sum += t.getWaitingTime();
            i++;
            if(t.getWaitingTime() > tempMax){
                tempMax = t.getWaitingTime();
            }
        }
        System.out.println("\nFCFS - Average waiting time: " + sum/i);
        System.out.println("FCFS - Head total distance: " + headTotalDistance);
        System.out.println("FCFS - Longest waiting time: " + tempMax + "\nRequests served: " + i);
    }

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Shortest Seek Time Fist~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public void shortestSeekTimeFirst(){
        headPosition = diskSize/2;
        int headTotalDistance = 0;
        int toAdd;
        float min = 0.90f;
        int sortCounter = 1;
        Comparator<Request> comp = new PositionComparator();           //komparator - najmniejsza odległość od głowicy

        ArrayList<Request> queueSSTF = new ArrayList<>();              //kolejka główna SSTF
        for (Request task : initialQueue){
            queueSSTF.add(new Request(task));
        }
        queueSSTF.sort(comp);

        ArrayList<Request> tasksToAdd = new ArrayList<>();             //lista nowych procesów do dodania
        for (Request futureTask : futureTasks) {
            tasksToAdd.add(new Request(futureTask));
        }
        ArrayList<Request> doneTasks = new ArrayList<>();


        while(!queueSSTF.isEmpty() || !tasksToAdd.isEmpty()) {
            if (queueSSTF.isEmpty()) {
                if (condition(min, tasksToAdd)) {
                    toAdd = rand.nextInt(1, 3);
                    for (int j = 0; j < toAdd; j++) {
                        if (!tasksToAdd.isEmpty()) {
                            tasksToAdd.get(0).setArrivalTime(headTotalDistance);
                            queueSSTF.add(tasksToAdd.get(0));
                            tasksToAdd.remove(0);
                        }
                    }
                    min = 0.9f;
                } else {
//                    min -= 0.02;
                }
            } else {
                Request t = queueSSTF.get(0);
                while (headPosition != t.getPosition()) {
                    headTotalDistance++;
                    if (t.getPosition() > headPosition) {
                        headPosition++;
                    } else {
                        headPosition--;
                    }

                    if (condition(min, tasksToAdd)) {
                        toAdd = rand.nextInt(1, 3);
                        for (int j = 0; j < toAdd; j++) {
                            if (!tasksToAdd.isEmpty()) {
                                tasksToAdd.get(0).setArrivalTime(headTotalDistance);
                                queueSSTF.add(tasksToAdd.get(0));
                                tasksToAdd.remove(0);
                            }
                        }
                        min = 0.9f;
                    } else {
//                        min -= 0.02;
                    }
                }
                t.setWaitingTime(headTotalDistance - t.getArrivalTime());
                t.setServed(true);
                doneTasks.add(t);
                queueSSTF.remove(0);
                queueSSTF.sort(comp);
                sortCounter++;
            }
        }

        int sum = 0;
        int i = 0;
        int tempMax = 0;
        for (Request t : doneTasks){
            sum += t.getWaitingTime();
            i++;
            if(t.getWaitingTime() > tempMax){
                tempMax = t.getWaitingTime();
            }
        }
        System.out.println("\nSSTF - Average waiting time: " + sum/i);
        System.out.println("SSTF - Head total distance: " + headTotalDistance);
        System.out.println("SSTF - Number of sorts: " + sortCounter);
        System.out.println("SSTF - Longest waiting time: " + tempMax + "\nRequests served: " + i);

    }

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~SCAN~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    public void scan(){
        headPosition = diskSize/2;
        int headTotalDistance = 0;
        int toAdd;
        float min = 0.9f;
        boolean goRight = false;       //określa kierunek w którym głowica się porusza
        boolean allServed = false;     //czy wszystkie żądania w kolejce głównej są obsłużone

        ArrayList<Request> queueSCAN = new ArrayList<>();              //kolejka główna SCAN
        for (Request task : initialQueue){
            queueSCAN.add(new Request(task));
        }

        ArrayList<Request> tasksToAdd = new ArrayList<>();             //lista nowych procesów do dodania
        for (Request futureTask : futureTasks) {
            tasksToAdd.add(new Request(futureTask));
        }


        while(!allServed || !tasksToAdd.isEmpty()) {
            allServed = true;
            headTotalDistance++;
            if(goRight){
                headPosition++;
            } else {
                headPosition--;
            }

            for (Request t : queueSCAN) {
                if(t.getPosition() == headPosition && !t.isServed()){
                    t.setWaitingTime(headTotalDistance - t.getArrivalTime());
                    t.setServed(true);
                }
                if(!t.isServed())
                    allServed = false;
            }

            if(condition(min, tasksToAdd)){                     //dynamiczne dodawanie procesu/procesów
                toAdd = rand.nextInt(1, 3);
                for (int j = 0; j < toAdd; j++) {
                    if(!tasksToAdd.isEmpty()) {
                        tasksToAdd.get(0).setArrivalTime(headTotalDistance);
                        queueSCAN.add(tasksToAdd.get(0));
                        tasksToAdd.remove(0);
                    }
                }
                min = 0.9f;
            } else {
                min -= 0.02;
            }
            if(headPosition == diskSize - 1)
                goRight = false;
            if(headPosition == 0)
                goRight = true;
        }

        int sum = 0;
        int i = 0;
        int tempMax = 0;
        for (Request t : queueSCAN){
            sum += t.getWaitingTime();
            i++;
            if(t.getWaitingTime() > tempMax){
                tempMax = t.getWaitingTime();
            }
        }
        System.out.println("\nSCAN - Average waiting time: " + sum/i);
        System.out.println("SCAN - Head total distance: " + headTotalDistance);
        System.out.println("SCAN - Longest waiting time: " + tempMax + "\nRequests served: " + i);
    }

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~C-SCAN~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public void cScan(){
        headPosition = diskSize/2;
        int headTotalDistance = 0;
        int toAdd;
        float min = 0.9f;
        boolean allServed = false;
        int resetCounter = 0;

        ArrayList<Request> queueCSCAN = new ArrayList<>();              //kolejka główna C-SCAN
        for (Request task : initialQueue){
            queueCSCAN.add(new Request(task));
        }

        ArrayList<Request> tasksToAdd = new ArrayList<>();             //lista nowych procesów do dodania
        for (Request futureTask : futureTasks) {
            tasksToAdd.add(new Request(futureTask));
        }

        while(!allServed || !tasksToAdd.isEmpty()) {
            allServed = true;
            headTotalDistance++;
            headPosition++;

            for (Request t : queueCSCAN) {
                if(t.getPosition() == headPosition && !t.isServed()){
                    t.setWaitingTime(headTotalDistance - t.getArrivalTime());
                    t.setServed(true);
                }
                if(!t.isServed())
                    allServed = false;
            }

            if(condition(min, tasksToAdd)){                     //dynamiczne dodawanie procesów
                toAdd = rand.nextInt(1, 3);
                for (int j = 0; j < toAdd; j++) {
                    if(!tasksToAdd.isEmpty()) {
                        tasksToAdd.get(0).setArrivalTime(headTotalDistance);
                        queueCSCAN.add(tasksToAdd.get(0));
                        tasksToAdd.remove(0);
                    }
                }
                min = 0.9f;
            } else {
                min -= 0.02;
            }
            if(headPosition == diskSize - 1) {
                headPosition = -1;
                resetCounter++;
            }
        }

        int sum = 0;
        int i = 0;
        int tempMax = 0;
        for (Request t : queueCSCAN){
            sum += t.getWaitingTime();
            i++;
            if(t.getWaitingTime() > tempMax){
                tempMax = t.getWaitingTime();
            }
        }
        System.out.println("\nC-SCAN - Average waiting time: " + sum/i);
        System.out.println("C-SCAN - Head total distance: " + headTotalDistance);
        System.out.println("C-SCAN - Longest waiting time: " + tempMax);
        System.out.println("C-SCAN - Number of resets: " + resetCounter + "\nRequests served: " + i);
    }

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~SCAN EDF~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public void scanEDF(){
        headPosition = diskSize/2;
        int headTotalDistance = 0;
        int toAdd;
        float min = 0.9f;
        boolean goRight = false;
        boolean allServed = false;

        ArrayList<Request> queueEDF = new ArrayList<>();
        for (Request task : initialQueue){
            queueEDF.add(new Request(task));
        }

        ArrayList<Request> tasksToAdd = new ArrayList<>();
        for (Request futureTask : futureTasks) {
            tasksToAdd.add(new Request(futureTask));
        }

        ArrayList<RealTimeRequest> queueRT = new ArrayList<>();

        ArrayList<RealTimeRequest> futureRT = new ArrayList<>();
        for (RealTimeRequest rtRequest : rtTasks) {
            futureRT.add(new RealTimeRequest(rtRequest));
        }

        while(!allServed || !tasksToAdd.isEmpty() || !futureRT.isEmpty()) {
            allServed = true;
            headTotalDistance++;

            if(!queueRT.isEmpty()){
                if(!queueRT.get(0).isServed()) {        //to oznacza, że wszystkie późniejsze są także obsłużone
                    if (headPosition < queueRT.get(0).getPosition())    //przesuwanie głowicy w kierunku żądania RT
                        headPosition++;
                    if (headPosition > queueRT.get(0).getPosition())
                        headPosition--;
                } else {
                    if (goRight) {              //przesuwanie głowicy w kierunku zwykłego żądania
                        headPosition++;
                    } else {
                        headPosition--;
                    }
                }
            } else {
                if (goRight) {                  //przesuwanie głowicy w kierunku zwykłego żądania
                    headPosition++;
                } else {
                    headPosition--;
                }
            }

            for (Request t : queueEDF) {
                if(t.getPosition() == headPosition && !t.isServed()){
                    t.setWaitingTime(headTotalDistance - t.getArrivalTime());
                    t.setServed(true);
                }
                if(!t.isServed())
                    allServed = false;
            }

            for (RealTimeRequest t : queueRT) {
                if(!t.isServed()){
                    if(t.getPosition() == headPosition){
                        t.setWaitingTime(headTotalDistance - t.getArrivalTime());
                        t.setServed(true);
                    } else {
                        t.setDeadLine(t.deadLine - 1);
                    }
                    if(t.deadLine == 0){
                        t.setServed(true);  //nieobłużony, na niepowodzenie będzie wskazywał waiting time = 0
                        t.setWaitingTime(-1);
                    }
                }
                if(!t.isServed())       //bo mogł wyżej został obsłużony
                    allServed = false;
            }

            if(condition(min, tasksToAdd)){
                toAdd = rand.nextInt(1, 3);
                for (int j = 0; j < toAdd; j++) {
                    if(!tasksToAdd.isEmpty()) {
                        tasksToAdd.get(0).setArrivalTime(headTotalDistance);
                        queueEDF.add(tasksToAdd.get(0));
                        tasksToAdd.remove(0);
                    }
                }
                min = 0.9f;
            } else {
                min -= 0.02;
            }

            if(rand.nextDouble() > min && !futureRT.isEmpty()){
                futureRT.get(0).setArrivalTime(headTotalDistance);
                queueRT.add(futureRT.get(0));
                futureRT.remove(0);
            }

            if(headPosition >= diskSize - 1)
                goRight = false;
            if(headPosition <= 0)
                goRight = true;

            queueRT.sort(new EDFComparator());
        }

        int failedRT = 0;
        int sum = 0;
        int i = 0;
        int tempMax = 0;
        for (Request t : queueEDF){
            sum += t.getWaitingTime();
            i++;
            if(t.getWaitingTime() > tempMax){
                tempMax = t.getWaitingTime();
            }
        }
        for (RealTimeRequest t : queueRT){
            if(t.getWaitingTime() >= 0) {
                sum += t.getWaitingTime();
                i++;
            } else {
                failedRT++;
            }
        }
        System.out.println("\nSCAN-EDF - Average waiting time: " + sum/i);
        System.out.println("SCAN-EDF - Head total distance: " + headTotalDistance);
        System.out.println("SCAN-EDF - Longest waiting time: " + tempMax);
        System.out.println("SCAN-EDF - Failed RT Requests: " + failedRT + "\nRequests served: " + i);
    }

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~SCAN FD SCAN~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public void scanFDscan() {
        headPosition = diskSize/2;
        int headTotalDistance = 0;
        int toAdd;
        float min = 0.9f;
        boolean goRight = false;
        boolean allServed = false;

        ArrayList<Request> queueFD = new ArrayList<>();
        for (Request task : initialQueue) {
            queueFD.add(new Request(task));
        }

        ArrayList<Request> tasksToAdd = new ArrayList<>();
        for (Request futureTask : futureTasks) {
            tasksToAdd.add(new Request(futureTask));
        }

        ArrayList<RealTimeRequest> queueRT = new ArrayList<>();

        ArrayList<RealTimeRequest> futureRT = new ArrayList<>();
        for (RealTimeRequest rtRequest : rtTasks) {
            futureRT.add(new RealTimeRequest(rtRequest));
        }

        while (!allServed || !tasksToAdd.isEmpty() || !futureRT.isEmpty()) {
            allServed = true;
            headTotalDistance++;

            if(!queueRT.isEmpty()){
                RealTimeRequest rt = queueRT.get(0);
                if(!rt.isServed() && rt.deadLine  >= Math.abs(rt.getPosition() - headPosition)){
                    if (headPosition < rt.getPosition())
                        headPosition++;
                    if (headPosition > rt.getPosition())
                        headPosition--;
                } else {
                    if (goRight) {
                        headPosition++;
                    } else {
                        headPosition--;
                    }
                }
            } else {
                if (goRight) {
                    headPosition++;
                } else {
                    headPosition--;
                }
            }

            for (Request t : queueFD) {
                if (t.getPosition() == headPosition && !t.isServed()) {
                    t.setWaitingTime(headTotalDistance - t.getArrivalTime());
                    t.setServed(true);
                }
                if (!t.isServed())
                    allServed = false;
            }

            for (RealTimeRequest rt : queueRT) {
                if (!rt.isServed()) {
                    if (headPosition == rt.getPosition()) {
                        rt.setWaitingTime(headTotalDistance - rt.getArrivalTime());
                        rt.setServed(true);
                    } else {
                        rt.setDeadLine(rt.deadLine - 1);
                    }
                    if (rt.deadLine == 0) {
                        rt.setWaitingTime(-1);
                        rt.setServed(true);
                    }
                }
                if (!rt.isServed())
                    allServed = false;
            }
            if(condition(min, tasksToAdd)){
                toAdd = rand.nextInt(1, 3);
                for (int j = 0; j < toAdd; j++) {
                    if(!tasksToAdd.isEmpty()) {
                        tasksToAdd.get(0).setArrivalTime(headTotalDistance);
                        queueFD.add(tasksToAdd.get(0));
                        tasksToAdd.remove(0);
                    }
                }
                min = 0.9f;
            } else {
                min -= 0.02;
            }

            if(rand.nextDouble() > min && !futureRT.isEmpty()){
                futureRT.get(0).setArrivalTime(headTotalDistance);
                queueRT.add(futureRT.get(0));
                futureRT.remove(0);
            }

            if(headPosition >= diskSize - 1)
                goRight = false;
            if(headPosition <= 0)
                goRight = true;

            queueRT.sort(new FDComparator());
        }

        int failedRT = 0;
        int sum = 0;
        int i = 0;
        int tempMax = 0;
        for (Request t : queueFD){
            sum += t.getWaitingTime();
            i++;
            if(t.getWaitingTime() > tempMax){
                tempMax = t.getWaitingTime();
            }
        }
        for (RealTimeRequest t : queueRT){
            if(t.getWaitingTime() >= 0) {
                sum += t.getWaitingTime();
                i++;
            } else {
                failedRT++;
            }
        }
        System.out.println("\nSCAN-FDSCAN - Average waiting time: " + sum/i);
        System.out.println("SCAN-FDSCAN - Head total distance: " + headTotalDistance);
        System.out.println("SCAN-FDSCAN - Longest waiting time: " + tempMax);
        System.out.println("SCAN-FDSCAN - Failed RT Requests: " + failedRT + "\nRequests served: " + i);
    }


    public void createInitialAndFutureProcesses(){
        for (int i = 0; i < 0.4 * quantity; i++) {                  //procesy na start
            initialQueue.add(new Request((byte) 0, diskSize, 0));
        }

        for (int i = 0; i < 0.15 * quantity; i++) {                 //procesy do dynamicznego dodawania (krótkie)
            futureTasks.add(new Request((byte) 1, diskSize, 0));
        }
        for (int i = 0; i < 0.3 * quantity; i++) {                  //procesy do dynamicznego dodawania (różne)
            futureTasks.add(new Request((byte) 0, diskSize, 0));
        }
        for (int i = 0; i < 0.15 * quantity; i++) {                 //procesy do dynamicznego dodawania (dłuższe)
            futureTasks.add(new Request((byte) 2, diskSize, 0));
        }

//        initialQueue.add(new Request(98));
//        initialQueue.add(new Request(183));
//        initialQueue.add(new Request(37));
//        initialQueue.add(new Request(122));
//        initialQueue.add(new Request(14));
//        initialQueue.add(new Request(124));
//        initialQueue.add(new Request(65));
//        initialQueue.add(new Request(67));


//        System.out.println("\nInitial tasks: ");
//        for (int i = 0; i < initialQueue.size(); i++) {
//            System.out.print(initialQueue.get(i) + "\t");
//        }
//
//        System.out.println("\nFuture tasks: ");
//        for (int i = 0; i < futureTasks.size(); i++) {
//            System.out.print(futureTasks.get(i) + "\t");
//        }
    }

    public boolean condition(float fraction, ArrayList<Request> tasksToAdd){
        return rand.nextDouble() > fraction && !tasksToAdd.isEmpty();
    }

    class PositionComparator implements Comparator<Request>{
        public int compare(Request t1, Request t2){          //obsłużone żądania trafiają na koniec kolejki
            if(t1.isServed() && !t2.isServed())
                return 1;
            if(!t1.isServed() && t2.isServed())
                return -1;
            return Integer.compare(Math.abs(headPosition - t1.getPosition()), Math.abs(headPosition - t2.getPosition()));
        }
    }

    class EDFComparator implements Comparator<RealTimeRequest>{
        public int compare(RealTimeRequest t1, RealTimeRequest t2){
            if(t1.isServed() && !t2.isServed())         ////obsłużone żądania trafiają na koniec kolejki
                return 1;
            if(!t1.isServed() && t2.isServed())
                return -1;
            return Integer.compare(t1.deadLine, t2.deadLine);
        }
    }

    class FDComparator implements Comparator<RealTimeRequest>{
        public int compare(RealTimeRequest t1, RealTimeRequest t2){
            if(t1.isServed() && !t2.isServed())         //obsłużone żądania trafiają na koniec kolejki
                return 1;
            if(!t1.isServed() && t2.isServed())
                return -1;
            if(t1.deadLine >= Math.abs(t1.getPosition() - headPosition) && t2.deadLine < Math.abs(t2.getPosition() - headPosition)) {
                return -1;
            }
            if(t1.deadLine < Math.abs(t1.getPosition() - headPosition) && t2.deadLine >= Math.abs(t2.getPosition() - headPosition)) {
                return 1;
            }

            return Integer.compare(t1.deadLine, t2.deadLine);
        }
    }

}
//            if(result == 0){
//                if(Math.abs(t1.getPosition() - headPosition) >= Math.abs(t2.getPosition() - headPosition)){
//                    return -1;
//                } else {
//                    return 1;
//                }
//            }