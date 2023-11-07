package model;

public class Parent extends Dependent {

    protected int gender;

    public Parent(int gender, int age) {
        super(age);
        this.gender = gender;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

}
