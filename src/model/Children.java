package model;

public class Children extends Dependent {

    protected int WorkingStatus;

    public Children(int WorkingStatus, int age) {
        super(age);
        this.WorkingStatus = WorkingStatus;
    }

    public int getWorkingStatus() {
        return WorkingStatus;
    }

    public void setWorkingStatus(int WorkingStatus) {
        this.WorkingStatus = WorkingStatus;
    }

    public int getDeductionAmount() {
        if (WorkingStatus == 2) {
            return 0;
        } else {
            if (age <= 18) {
                return 4400000;
            } else {
                return 6000000;
            }
        }
    }

}
