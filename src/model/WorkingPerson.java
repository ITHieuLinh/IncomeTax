package model;

import java.util.ArrayList;

public class WorkingPerson {

    private double totalIncome;
    private ArrayList<Children> dependents;

    public WorkingPerson(double totalIncome, ArrayList<Children> dependents) {
        this.totalIncome = totalIncome;
        this.dependents = dependents;
    }

    public double getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(double totalIncome) {
        this.totalIncome = totalIncome;
    }

    public ArrayList<Children> getDependents() {
        return dependents;
    }

    public void setDependents(ArrayList<Children> dependents) {
        this.dependents = dependents;
    }

}
