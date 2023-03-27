package Zad1;

import java.util.ArrayList;
import java.util.Random;

public class Zad1 {

    int quantity;
    Random rand = new Random();
    ArrayList<Task> initialQueue = new ArrayList<>();
    ArrayList<Task> futureTasks = new ArrayList<>();

    public static void main(String[] args) {
        Zad1 test = new Zad1(1000);
        test.createInitialAndFutureProcesses();
        test.FirstComeFirstServed();
        test.ShortestJobFirst();
        test.roundRobin();
    }

    public Zad1 (int howManyProcesses){
        this.quantity = howManyProcesses;
    }

    public void createInitialAndFutureProcesses(){
        for (int i = 0; i < 0.4 * quantity; i++) {                  //procesy na start
            initialQueue.add(new Task((byte) 0, 0));
        }

        for (int i = 0; i < 0.15 * quantity; i++) {                 //procesy do dynamicznego dodawania (krótkie)
            futureTasks.add(new Task((byte) 1,0));
        }
        for (int i = 0; i < 0.3 * quantity; i++) {                  //procesy do dynamicznego dodawania (różne)
            futureTasks.add(new Task((byte) 0,0));
        }
        for (int i = 0; i < 0.15 * quantity; i++) {                 //procesy do dynamicznego dodawania (dłuższe)
            futureTasks.add(new Task((byte) 2,0));
        }
        futureTasks.get(0).setDuration(50);
    }


    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~FCFS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    public void FirstComeFirstServed(){
        int time = 0;
        int toAdd;              //zmienna, która wskazuje ile nowych procesów dodać - przypisywana jest wart. rand.
        float min = 0.9f;       //zmienna do decydowania czy tworzyć nowy proces -> if(random.nextDouble > min){

        ArrayList<Task> queueFcfs = new ArrayList<>();              //kolejka główna FCFS
        for (Task task : initialQueue){
            queueFcfs.add(new Task(task));
        }
        ArrayList<Task> tasksToAdd = new ArrayList<>();             //lista nowych procesów do dodania
        for (Task futureTask : futureTasks) {
            tasksToAdd.add(new Task(futureTask));
        }


        for (int i = 0; i < queueFcfs.size(); i++) {
            Task t = queueFcfs.get(i);                              //operacje na wywołanym procesie
            t.setWaitingTime(time - t.getArrivalTime());
            while(t.getDuration() != 0){
                time++;
                t.setDuration(t.getDuration() - 1);

                if(condition(min, tasksToAdd)){                     //dynamiczne dodawanie procesu/procesów
                    toAdd = rand.nextInt(1, 4);
                    for (int j = 0; j < toAdd; j++) {
                        if(!tasksToAdd.isEmpty()) {
                            tasksToAdd.get(0).setArrivalTime(time);
                            queueFcfs.add(tasksToAdd.get(0));
                            tasksToAdd.remove(0);
                        }
                    }
                    min = 0.9f;
                } else {
                    min -= 0.02;        //zwiększenie prawdopodobieństwa dodania procesu po niespełnieniu warunku
                }
            }
        }

        int sum = 0;
        int i = 0;
        int tempMax = 0;
        for (Task t : queueFcfs){
            sum += t.getWaitingTime();
            i++;
            if(t.getWaitingTime() > tempMax){
                tempMax = t.getWaitingTime();
            }
        }
        System.out.println("\nFCFS - Average waiting time: " + sum/i);
        System.out.println("FCFS - Total time: " + time);
        System.out.println("FCFS - Longest waiting time: " + tempMax);
    }


    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~SJF~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    public void ShortestJobFirst(){
        int time = 0;
        int toAdd;
        float min = 0.9f;

        ArrayList<Task> queueSjf = new ArrayList<>();               //kolejka główna SJF
        for (Task task : initialQueue){
            queueSjf.add(new Task(task));
        }
        queueSjf.sort(new DurationComparator());                    //posortowanie wstępnych procesów

        ArrayList<Task> tasksToAdd = new ArrayList<>();             //lista nowych procesów do dodania
        for (Task futureTask : futureTasks) {
            tasksToAdd.add(new Task(futureTask));
        }


        for (int i = 0; i < queueSjf.size(); i++) {
            Task t = queueSjf.get(i);                           //operacje na procesie
            t.setWaitingTime(time);
            while (t.getDuration() != 0) {
                time++;
                t.setDuration(t.getDuration() - 1);

                if(condition(min, tasksToAdd)){                 //dynamiczne dodawanie procesów
                    toAdd = rand.nextInt(1, 4);
                    for (int j = 0; j < toAdd; j++) {
                        if(!tasksToAdd.isEmpty()){
                            Task newTask = tasksToAdd.get(0);
                            newTask.setArrivalTime(time);
                            int k = i + 1;
                        while(k < queueSjf.size() && newTask.getDuration() >= queueSjf.get(k).getDuration()) {
                            k++;
                        }
                        queueSjf.add(k, newTask);            //wstawianie procesu w odpowiednie miejsce (index = k)
                        tasksToAdd.remove(0);          //usunięcie dodanego procesu z listy "do dodania"
                        }
                    }
                    min = 0.9f;
                } else {
                    min -= 0.02;
                }
            }
        }

        int sum = 0;
        int i = 0;
        int tempMax = 0;
        for (Task t : queueSjf){
            if(t.getWaitingTime() > tempMax){
                tempMax = t.getWaitingTime();
            }
            sum += t.getWaitingTime();
            i++;
        }
        System.out.println("\nSJF - Average waiting time: " + sum/i);
        System.out.println("SJF - Total time: " + time);
        System.out.println("SJF - Longest waiting time: " + tempMax);
        System.out.println("SJF - Sortings performed: " + 1+ futureTasks.size());

    }


    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~RR~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    public void roundRobin() {
        int time = 0;
        int quantum = 30;        //kwant czasu, który dajemy każdemu z procesów w kolejce
        int toAdd;
        float min = 0.9f;
        boolean shouldContinue = true;      //wartość logiczna, która sugeruje czy w poprzedniej kolejce były wykonywane operacje na procesach

        ArrayList<Task> queueRr = new ArrayList<>();            //kolejka główna RR
        for (Task task : initialQueue){
            queueRr.add(new Task(task));
        }
        ArrayList<Task> tasksToAdd = new ArrayList<>();         //lista nowych procesów do dodania
        for (Task futureTask : futureTasks) {
            tasksToAdd.add(new Task(futureTask));
        }


        while (shouldContinue) {
            shouldContinue = false;                         //aby w razie braku operacji wyjść z pętli
            for (int i = 0; i < queueRr.size(); i++) {
                Task t = queueRr.get(i);
                if (t.getDuration() != 0) {                 //obsługiwanie procesu
                    if (t.getDuration() < quantum) {
                        t.setWaitingTime(time - t.getArrivalTime() - (t.getRounds() * quantum));
                        time += t.getDuration();
                    } else {
                        t.setWaitingTime(time - t.getArrivalTime() - (t.getRounds() * quantum));
                        time += quantum;
                    }
                    t.setDuration(Math.max(t.getDuration() - quantum, 0));      //aby uniknąć wartości ujemnych
                    t.setRounds(t.getRounds() + 1);   //liczba przełączeń kontekstu na ten proces zostaje zwiększona
                    shouldContinue = true;

                    for (int j = 0; j < quantum; j++) {      //tyle powtórzeń ile jednostek czasu liczy kwant czasu
                        if (condition(min, tasksToAdd)) {               //dynamiczne dodawanie procesu/procesów
                            toAdd = rand.nextInt(1, 4);
                            for (int k = 0; k < toAdd; k++) {
                                if (!tasksToAdd.isEmpty()) {
                                    tasksToAdd.get(0).setArrivalTime(time);
                                    queueRr.add(tasksToAdd.get(0));
                                    tasksToAdd.remove(0);
                                }
                            }
                            min = 0.9f;
                        } else {
                            min -= 0.02;
                        }
                    }
                }

            }
        }

        int sum = 0;
        int i = 0;
        int tempMax = 0;
        int contextSwitches = 0;
        for (Task t : queueRr){
            sum += t.getWaitingTime();
            i++;
            if(t.getWaitingTime() > tempMax){
                tempMax = t.getWaitingTime();
            }
            contextSwitches += t.getRounds();
        }
        System.out.println("\nRR - Average waiting time: " + sum/i);
        System.out.println("RR - Total time: " + time);
        System.out.println("RR - Longest waiting time: " + tempMax);
        System.out.println("RR - Context switches performed: " + contextSwitches);
    }

    public boolean condition(float fraction, ArrayList<Task> tasksToAdd){
        return rand.nextDouble() > fraction && !tasksToAdd.isEmpty();
    }

}


//średni czas wykonania taska

