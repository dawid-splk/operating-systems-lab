package Zad5;

import java.util.ArrayList;
import java.util.Comparator;

public class HelpfulAlg implements ServeProcesses {

    int min;
    int max;
    int requests = 0;
    int transfers = 0;
    int totalOLTime = 0;
    int avrgLoad;
    double loadDeviation;
    Comparator<Process> comp;

    public HelpfulAlg(int min, int max){
        this.min = min;
        this.max = max;
        comp = (o1, o2) -> {
            return Integer.compare(o2.getDuration(), o1.getDuration());     //od najdluzszych
        };
    }

    @Override
    public void simulate(ArrayList<Processor> processors) {
        boolean shouldContinue = true;
        int time = 0;
        ArrayList<Processor> copy = new ArrayList<>();
        ArrayList<Integer> averageLoads = new ArrayList<>();
        ArrayList<Double> loadDeviations = new ArrayList<>();
        int goodDeeds = 0;

        for (int i = 0; i < processors.size(); i++) {
            copy.add(new Processor(processors.get(i)));
        }

        for(Processor proc : copy){
            proc.startInitialProcesses();
        }

        while(shouldContinue){
            time++;

            for(Processor sender : copy){
                boolean notFoundProcessor = true;
                if(time % sender.getPause() == 0 && !sender.getAllProcesses().isEmpty()){
                    if(sender.getLoad() < max){
                        sender.startNewProcess(sender.getAllProcesses().get(0));
                        sender.getAllProcesses().remove(0);
                        notFoundProcessor = false;
                    }
                    if(notFoundProcessor) {
                        for (Processor recipient : copy) {
                            if (recipient != sender) {
                                requests++;
                                if (recipient.getLoad() < max) {
                                    recipient.startNewProcess(sender.getAllProcesses().get(0));
                                    sender.getAllProcesses().remove(0);
                                    notFoundProcessor = false;
                                    transfers++;
                                    break;
                                }
                            }
                        }
                    }
                    if(notFoundProcessor){
                        sender.startNewProcess(sender.getAllProcesses().get(0));
                        sender.getAllProcesses().remove(0);
                    }
                }
            }

            for(Processor sender : copy){
                if(sender.getLoad() < min){
                    for (Processor recipient : copy) {
                        if (recipient != sender) {
                            requests++;
                            if (recipient.getLoad() > max) {
                                recipient.getWorkingProcesses().sort(comp);     //nadjluzsze procesy
                                sender.startNewProcess(recipient.getWorkingProcesses().get(0)); //sprawdzic czy dobrze sortuje
                                recipient.setLoad(recipient.getLoad() - recipient.getWorkingProcesses().get(0).getLoad());
                                recipient.getWorkingProcesses().remove(0);
                                transfers++;
                                goodDeeds++;
                                break;
                            }
                        }
                    }
                }
            }

            for (Processor proc : copy){
                if(proc.getLoad() > 100) {
                    proc.setOverloadTime(proc.getOverloadTime() + 1);
                }
            }

            for(Processor proc : copy){
                for (int i = 0; i < proc.getWorkingProcesses().size(); i++) {
                    proc.getWorkingProcesses().get(i).setDuration(proc.getWorkingProcesses().get(i).getDuration()-1);
                    if(proc.getWorkingProcesses().get(i).getDuration() == 0){
                        proc.setLoad(proc.getLoad() - proc.getWorkingProcesses().get(i).getLoad());
                        proc.getWorkingProcesses().remove(proc.getWorkingProcesses().get(i));
//                        i--;
                    }
                }
            }

            shouldContinue = false;
            for(Processor proc : copy){
                if(!proc.getAllProcesses().isEmpty() || !proc.getWorkingProcesses().isEmpty()){
                    shouldContinue = true;
                    break;
                }
            }

            if(time % 130 == 0){     //130 - longest process duration
                int totalLoad = 0;
                int singleLoad;
                for (Processor proc : copy){
                    totalLoad += proc.getLoad();
                }
                averageLoads.add(totalLoad / copy.size());

                double totalDev = 0;
                for (Processor proc : copy){
                    totalDev += Math.pow(proc.getLoad() - ((double)(totalLoad / copy.size())), 2);
                }
                loadDeviations.add(Math.sqrt(totalDev / copy.size()));
            }
        }

        int totalLoad = 0;
        for (int i = 0; i < averageLoads.size(); i++) {
            totalLoad += averageLoads.get(i);
        }
        avrgLoad = totalLoad / averageLoads.size();

        double totalDev = 0;
        for (int i = 0; i < loadDeviations.size(); i++) {
            totalDev += loadDeviations.get(i);
        }
        loadDeviation = totalDev / loadDeviations.size();

        for (Processor proc : copy){
            totalOLTime += proc.getOverloadTime();
        }
        printStats();
//        System.out.print("\nTime: " + time);
        System.out.println("\nGood deeds: " + goodDeeds);
    }


    private void printStats() {

        System.out.print("\n\nStats for Helpful Algorithm: " +
                "\nNumber of requests: " + requests +
                "\nNumber of transfers: " + transfers +
                "\nTotal overload time (sum): " + totalOLTime +
                "\nAverage load: " + avrgLoad +
                "\nLoad deviation: ");
        System.out.printf("%4.2f",loadDeviation);
    }

}
