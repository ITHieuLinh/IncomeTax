package DataAccess;

import common.Library;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import model.Children;
import model.Parent;
import model.WorkingPerson;

public class IncomeTaxDAO {

    private static IncomeTaxDAO instance = null;
    Library l;

    public IncomeTaxDAO() {
        l = new Library();
    }

    public static IncomeTaxDAO Instance() {
        if (instance == null) {
            synchronized (IncomeTaxDAO.class) {
                if (instance == null) {
                    instance = new IncomeTaxDAO();
                }
            }
        }
        return instance;
    }

    public void addFamily() {
        System.out.println("=========== FAMILIA TAX ===========");
        ArrayList<WorkingPerson> family = new ArrayList<>();
        while (true) {
            ArrayList<Parent> list = addParent();
            while (true) {
                System.out.println("---------- Input income ----------");
                double income = l.checkInputDouble("Input Income: ");
                ArrayList<Children> listChildren = addChildren();
                WorkingPerson wp = new WorkingPerson(income, listChildren);
                family.add(wp);
                System.out.println("---------- Input income ----------");
                if (!l.checkInputYN("Do you want to continue input Income(Y/N): ")) {
                    HashMap<Integer, ArrayList<Double>> listTax = calculateTaxableIncome(family, list);
                    printTaxDetails(listTax);
                    return;
                }
            }

        }

    }

    public ArrayList<Parent> addParent() {
        System.out.println("---------- Input parents ----------");
        ArrayList<Parent> list = new ArrayList<>();
        boolean choice = l.checkInputYN("Do you want to input parent(Y/N)?: ");
        int count = 1;
        if (choice) {
            while (true) {
                int gender = l.getInt("Father(1) - Mother(2)?: ", 1, 2);
                while (!checkParent(gender, list)) {
                    gender = l.getInt("Father(1) - Mother(2)?: ", 1, 2);
                }
                int age = l.getIntNoLimit("Input age: ");
                Parent parent = new Parent(gender, age);
                list.add(parent);
                if (count == 2) {
                    return list;
                }
                if (!l.checkInputYN("Do you want to continue input parent(Y/N): ")) {
                    return list;
                }
                count++;
            }
        } else {
            return list;
        }
    }

    public boolean checkParent(int gender, ArrayList<Parent> list) {
        if (list.isEmpty()) {
            return true;
        }
        for (Parent x : list) {
            if (gender == x.getGender()) {
                String genderString = null;
                switch (gender) {
                    case 1:
                        genderString = "Father";
                        break;
                    case 2:
                        genderString = "Mother";
                        break;
                }
                System.out.println("You already input " + genderString);
                return false;
            }
        }
        return true;
    }

    public ArrayList<Children> addChildren() {
        System.out.println("---------- Input children ----------");
        ArrayList<Children> list = new ArrayList<>();
        int status;
        boolean choice = l.checkInputYN("Do you want to input children(Y/N)?: ");
        if (choice) {
            while (true) {
                int age = l.getIntNoLimit("Input age: ");
                if (age > 18 && age <= 22) {
                    status = l.getInt("Study(1) - Working(2)?: ", 1, 2);
                } else if (age > 22) {
                    status = 2;
                } else {
                    status = 1;
                }
                Children child = new Children(status, age);
                list.add(child);
                if (!l.checkInputYN("Do you want to continue input children(Y/N): ")) {
                    return list;
                }
            }
        } else {
            return list;
        }

    }

    public HashMap<Integer, ArrayList<Double>> calculateTaxableIncome(ArrayList<WorkingPerson> family, ArrayList<Parent> listParent) {
        HashMap<Integer, ArrayList<Double>> listTax = new LinkedHashMap<>();
        int personCount = family.size();
        double deductionParent = (double) calculatorParent(listParent) / personCount;
        int count = 1;
        for (WorkingPerson x : family) {
            ArrayList<Double> listArr = new ArrayList<>();
            double calculatorChildren = calculatorChildren(x.getDependents());
            double taxIncome = x.getTotalIncome() - 11000000 - calculatorChildren;
            if (x.getTotalIncome() > 4000000) {
                taxIncome -= deductionParent;
            }
            taxIncome = calculatorTax(taxIncome);
            listArr.add(x.getTotalIncome());
            listArr.add(11000000.0);
            listArr.add(calculatorChildren);
            listArr.add(deductionParent);
            listArr.add(taxIncome);
            listTax.put(count, listArr);
            count++;
        }
        return listTax;
    }

    public double calculatorTax(double taxIncome) {
        if (taxIncome <= 0) {
            return 0;
        }
        if (taxIncome < 4000000) {
            return (double) taxIncome * 5 / 100;
        } else if (taxIncome >= 4000000 && taxIncome <= 6000000) {
            return (double) taxIncome * 8 / 100;
        } else if (taxIncome > 6000000 && taxIncome <= 10000000) {
            return (double) taxIncome * 10 / 100;
        } else {
            return (double) taxIncome * 20 / 100;
        }
    }

    public int calculatorParent(ArrayList<Parent> listParent) {
        int parentCount = listParent.size();
        int sum = 0;
        for (Parent parent : listParent) {
            if ((parent.getAge() > 60) || (parent.getGender() == 2 && parent.getAge() > 55)) {
                sum += 4400000;
            }
        }
        return sum;
    }

    public int calculatorChildren(ArrayList<Children> list) {
        if (list.isEmpty()) {
            return 0;
        }
        int total = 0;
        int size = list.size();
        if (size > 2) {
            total += get2GreatChildren(list);
        } else if (size <= 2) {
            for (Children c : list) {
                total += c.getDeductionAmount();
            }
        }
        return total;
    }

    public int get2GreatChildren(ArrayList<Children> list) {
        int firstMax = list.get(0).getDeductionAmount();
        int secondMax = list.get(1).getDeductionAmount();

        for (int i = 1; i < list.size(); i++) {
            if (list.get(i).getDeductionAmount() > firstMax) {
                secondMax = firstMax;
                firstMax = list.get(i).getDeductionAmount();
            } else if (list.get(i).getDeductionAmount() > secondMax && list.get(i).getDeductionAmount() <= firstMax) {
                secondMax = list.get(i).getDeductionAmount();
            }
        }
        return firstMax + secondMax;
    }

    public void printTaxDetails(HashMap<Integer, ArrayList<Double>> list) {
        System.out.printf("%-15s%-25s%-25s%-25s%-25s%-25s\n", " ", "Income", "Deduction for self", "Children", "Parents", "Tax");
        for (int i = 1; i <= list.size(); i++) {
            ArrayList<Double> arrlist = list.get(i);
            double value = arrlist.get(0);
            long int1 = (long) value;
            value = arrlist.get(1);
            long int2 = (long) value;
            value = arrlist.get(2);
            long int3 = (long) value;
            value = arrlist.get(3);
            long int4 = (long) value;
            value = arrlist.get(4);
            long int5 = (long) value;
            System.out.printf("%-15s%-25s%-25s%-25s%-25s%-25s\n", "Person " + i + ":", int1, int2, int3, int4, int5);
        }
    }
}
