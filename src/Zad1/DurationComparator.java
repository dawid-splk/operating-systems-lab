package Zad1;

import java.util.Comparator;

public class DurationComparator implements Comparator<Task> {

    @Override
    public int compare(Task t1, Task t2){
        if(t1.getDuration() == t2.getDuration()){
            return 0;
        } else {
            return t1.getDuration() < t2.getDuration() ? -1 : 1;
        }
    }
}
