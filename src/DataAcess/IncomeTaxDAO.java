
package DataAcess;


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
        System.out.println("\n=========== Family Tax ===========");
        ArrayList<WorkingPerson> family = new ArrayList<>();
        while (true) {
            ArrayList<Parent> ParentList = addParent();
            int i = 1;
            while (true) {
                double income = addIncome(i);
                i++;
                ArrayList<Children> listChildren = addChildren();
                WorkingPerson wp = new WorkingPerson(income, listChildren);
                family.add(wp);
                System.out.println("---------- Input income ----------");
                if (!l.checkInputYN("\nDo you want to continue input Income for another person in your family(Y/N): ")) {
                    HashMap<Integer, ArrayList<Double>> taxList = calculateTaxableIncome(family, ParentList);
                    printTaxDetails(taxList);
                    return;
                } 
            }
        }
    }
    public double addIncome(int i){
        System.out.println("\n---------- Input income ----------");
        int incomeNum = l.getIntNoLimit("Enter number of income source of Person " + i + ": " );
        double income = 0;
        int count = 1;
        while(count <= incomeNum){
            income += l.checkInputDouble("Input the " + count + "th Income of Person " + i + ": ");  
            count++;
        }
        return income;
    }
    
    public ArrayList<Parent> addParent() {
        System.out.println("---------- Input parents ----------");
        ArrayList<Parent> ParentList = new ArrayList<>();
        boolean choice = l.checkInputYN("Do you want to input parent(Y/N)?: ");
        int count = 1;
        if (choice) {
            while (true) {
                int gender = l.getInt("Father(1) - Mother(2)?", 1, 2);
                while (!checkParent(gender, ParentList)) {
                    gender = l.getInt("Father(1) - Mother(2)?", 1, 2);
                }
                int age = l.getIntNoLimit("Input age: ");
                Parent parent = new Parent(gender, age);
                ParentList.add(parent);
                if (count == 2) {
                    return ParentList;
                }
                if (!l.checkInputYN("\nDo you want to continue input parent(Y/N): ")) {
                    return ParentList;
                }
                count++;
            }
        } else {
            return ParentList;
        }
    }

    public boolean checkParent(int gender, ArrayList<Parent> parentList) {
        if (parentList.isEmpty()) {
            return true;
        }
        for (Parent x : parentList) {
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
                System.out.println("You already input " + genderString + "!!!");
                return false;
            }
        }
        return true;
    }

    public ArrayList<Children> addChildren() {
        System.out.println("\n---------- Input children ----------");
        ArrayList<Children> childrenList = new ArrayList<>();
        int status;
        boolean choice = l.checkInputYN("Do you want to input children(Y/N)?: ");
        if (choice) {
            while (true) {
                int age = l.getIntNoLimit("Input age: ");
                if (age > 18 && age <= 22) {
                    status = l.getInt("Study(1) - Working(2)?", 1, 2);
                } else if (age > 22) {
                    status = 2;
                } else {
                    status = 1;
                }
                Children child = new Children(status, age);
                childrenList.add(child);
                if (!l.checkInputYN("\nDo you want to continue input children(Y/N): ")) {
                    return childrenList;
                }
            }
        } else {
            return childrenList;
        }
    }

    public HashMap<Integer, ArrayList<Double>> calculateTaxableIncome(ArrayList<WorkingPerson> family, ArrayList<Parent> listParent) {
        HashMap<Integer, ArrayList<Double>> listTax = new LinkedHashMap<>();
        int personCount = family.size();
        double deductionParent = (double) calculatorParent(listParent) / personCount;
        int count = 1;
        for (WorkingPerson x : family) {
            ArrayList<Double> workingPersonInfoList = new ArrayList<>();
            double calculatorChildren = calculatorChildren(x.getDependents());
            double taxIncome = x.getTotalIncome() - 11000000 - calculatorChildren;
            if (x.getTotalIncome() > 4000000) {
                taxIncome -= deductionParent;
            }
            taxIncome = calculatorTax(taxIncome);
            workingPersonInfoList.add(x.getTotalIncome());
            workingPersonInfoList.add(11000000.0);
            workingPersonInfoList.add(calculatorChildren);
            workingPersonInfoList.add(deductionParent);
            workingPersonInfoList.add(taxIncome);

            listTax.put(count, workingPersonInfoList);
            count++;
        }
        return listTax;
    }

    public double calculatorTax(double taxIncome) {
        if (taxIncome <= 0) {
            return 0;
        } else if (taxIncome < 4000000) {
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

    public int calculatorChildren(ArrayList<Children> ChildrenList) {
        if (ChildrenList.isEmpty()) {
            return 0;
        }
        int total = 0;
        int size = ChildrenList.size();
        if (size > 2) {
            total += get2GreatChildren(ChildrenList);
        } else if (size <= 2) {
            for (Children c : ChildrenList) {
                total += c.getDeductionAmount();
            }
        }
        return total;
    }

    public int get2GreatChildren(ArrayList<Children> ChildrenList) {
        int firstMax = ChildrenList.get(0).getDeductionAmount();
        int secondMax = ChildrenList.get(1).getDeductionAmount();

        for (int i = 1; i < ChildrenList.size(); i++) {
            if (ChildrenList.get(i).getDeductionAmount() > firstMax) {
                secondMax = firstMax;
                firstMax = ChildrenList.get(i).getDeductionAmount();
            } else if (ChildrenList.get(i).getDeductionAmount() > secondMax && ChildrenList.get(i).getDeductionAmount() <= firstMax) {
                secondMax = ChildrenList.get(i).getDeductionAmount();
            }
        }
        return firstMax + secondMax;
    }

    public void printTaxDetails(HashMap<Integer, ArrayList<Double>> TaxList) {
        System.out.printf("%-15s%-25s%-25s%-25s%-25s%-25s\n", " ", "Total Income", "Deduction for self", "Deduction for children", "Deduction for parents", "Tax");
        for (int i = 1; i <= TaxList.size(); i++) {
            ArrayList<Double> arrList = TaxList.get(i);
            double value = arrList.get(0);
            long totalIncome = (long) value;
            value = arrList.get(1);
            long deductionForSelf = (long) value;
            value = arrList.get(2);
            long deductionForChildren = (long) value;
            value = arrList.get(3);
            long deductionForParent  = (long) value;
            value = arrList.get(4);
            long tax = (long) value;
            System.out.printf("%-15s%-25s%-25s%-25s%-25s%-25s\n", "Person " + i + ":", totalIncome, deductionForSelf, deductionForChildren, deductionForParent, tax);
        }
    }
    
}