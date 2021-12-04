import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class Payroll_Calculations extends Copy_of_Admin_Dashboard{
    private String state;
    protected static float suta_rate;
    protected static float futa_rate;
    protected static int suta_threshold = 14000;
    protected static int futa_threshold = 7000;
    protected static int social_security_threshold = 142800;
    protected static float social_security_rate = .0602f;
    protected static float medicare_rate = .0145f;
    private static Withholding[] empl_pay_data;
    protected static String payroll_frequency;
    Payroll_Calculations() throws NoSuchAlgorithmException, InterruptedException {
        super();
    }
    Payroll_Calculations(Employee[] employees) throws NoSuchAlgorithmException, InterruptedException {
        super();
        this.employees = employees;
    }
    public void update_employee_list(Employee[] employees){
        this.employees = employees;
    }

    public void menu() throws IOException, InterruptedException, NoSuchAlgorithmException {
        Scanner scanner = new Scanner(System.in);
        int selection;
        if (payroll_frequency == null){
            selection = 11;
        }
        else{
            System.out.println("What would you like to do?");
            System.out.println("[1] Process Payroll Calculations for all Employees");
            System.out.println("[2] Set SUTA rate");
            System.out.println("[3] Set FUTA rate");
            System.out.println("[4] Set Social Security Threshold");
            System.out.println("[5] Set Medicare Rate");
            System.out.println("[6] Set Social Security Rate");
            System.out.println("[7] Add Deduction");
            System.out.println("[8] Add Gift / Bonus");
            System.out.println("[9] Delete Deduction");
            System.out.println("[10] Delete Gift");
            System.out.println("[11] Change Payroll Frequency");
            System.out.println("[12] Return to Menu");
            System.out.print("Enter a number corresponding to the operation you would like to perform: ");
            selection = get_valid_int_response(1, 12);
        }

        switch(selection){
            case 1:
                process_all_payroll();
                menu();
            case 2:
                set_suta();
                menu();
            case 3:
                set_futa();
                menu();
            case 4:
                set_ss_threshold();
                menu();
            case 5:
                set_medicare_rate();
                menu();
            case 6:
                set_social_security_rate();
                menu();
            case 7:
                add_deduction();
                menu();
            case 8:
                add_gift();
                menu();
            case 9:
                delete_deduction();
                menu();
            case 10:
                delete_gift();
            case 11:
                set_payroll_frequency();
                menu();
            case 12:
                super.Menu();
                break;
        }
    }
    private void set_payroll_frequency(){
        if (payroll_frequency != null){
            System.out.println("The current payroll frequency is: " + payroll_frequency);
        }
        System.out.println("What would you like to change the payroll frequency to?");
        System.out.println("[1] Daily");
        System.out.println("[2] Weekly");
        System.out.println("[3] Bi-weekly");
        System.out.println("[4] Semi-monthly");
        System.out.println("[5] Monthly");
        System.out.print("Enter the number corresponding to the frequency you would like to set: ");
        int selection = get_valid_int_response(1, 5);
        switch (selection){
            case 1:
                payroll_frequency = "Daily";
                break;
            case 2:
                payroll_frequency = "Weekly";
                break;
            case 3:
                payroll_frequency = "Biweekly";
                break;
            case 4:
                payroll_frequency = "Semimonthly";
                break;
            case 5:
                payroll_frequency = "Monthly";
                break;
        }
        System.out.println("Payroll frequency has been changed to: " + payroll_frequency + ".");
    }

    public void process_all_payroll() throws IOException, NoSuchAlgorithmException, InterruptedException {
        System.out.print("Enter the pay period you would like to process payroll for: ");
        for (int i = 0; i < get_user_count(); i++){
            employees[i].get_withholding_object().set_employee_object(employees[i]);
            employees[i].get_withholding_object().get_excel_data("State", employees[i].get_marital_status(), payroll_frequency);
            employees[i].get_withholding_object().get_excel_data("Federal", employees[i].get_marital_status(), payroll_frequency);
        }
        System.out.println("Payroll has been processed.");
    }
    public void set_suta(){
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the new SUTA rate as a percent (e.g. 5.6): ");
        suta_rate = get_valid_float_response(0, 20);
        suta_rate = suta_rate/100;
        System.out.println("The new SUTA rate is " + (suta_rate * 100) + "%. Is this correct? Enter \"Y\" or" +
                " \"N\": ");
        String response = get_valid_letter_response("Y", "N");
        if (Objects.equals(response, "N"))
            set_suta();
    }
    public void set_futa(){
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the new FUTA rate as a percent (e.g. 5.6): ");
        futa_rate = get_valid_float_response(0, 20);
        futa_rate = futa_rate/100;
        System.out.println("The new FUTA rate is " + (futa_rate * 100) + "%. Is this correct? Enter \"Y\" or" +
                " \"N\": ");
        String response = get_valid_letter_response("Y", "N");
        if (Objects.equals(response, "N"))
            set_futa();
    }
    public void set_ss_threshold(){
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the new social security threshold as an integer (e.g. 142,800)");
        social_security_threshold = get_valid_int_response(0, 1000000);
        System.out.println("The new social security threshold is " + social_security_threshold + " Is this correct? Enter \"Y\" or" +
                " \"N\": ");
        String response = get_valid_letter_response("Y", "N");
        if (Objects.equals(response, "N"))
            set_ss_threshold();
    }
    public void set_medicare_rate(){
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the new medicare rate as a percent (e.g. 1.45)");
        medicare_rate = get_valid_float_response(0, 20);
        medicare_rate = medicare_rate/100;
        System.out.println("The new medicare rate is " + (medicare_rate * 100) + "%. Is this correct? Enter \"Y\" or" +
                " \"N\": ");
        String response = get_valid_letter_response("Y", "N");
        if (Objects.equals(response, "N"))
            set_medicare_rate();
    }
    public void set_social_security_rate(){
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the new social security rate as a percent (e.g. 6.2)");
        social_security_rate = get_valid_float_response(0, 20);
        social_security_rate = social_security_rate/100;
        System.out.println("The new social secuirty rate is " + (social_security_rate * 100) + "%. Is this correct? Enter \"Y\" or" +
                " \"N\": ");
        String response = get_valid_letter_response("Y", "N");
        if (Objects.equals(response, "N"))
            set_social_security_rate();
    }
    public void add_deduction() throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        for (int i = 0; i < employees.length; i++){
            System.out.println("[" + i + "] " +employees[get_user_index()].get_user());
        }
        System.out.println("Enter the number of the employee that you would like to delete a deduction for: ");
        int response = get_valid_int_response(0, employees.length - 1);
        String title;
        double amount;
        System.out.print("Enter the title of the deduction: ");
        title = scanner.nextLine();
        System.out.print("Enter the amount of the deduction: ");
        amount = get_valid_double_response();
        System.out.println("Enter \"P\" if this is a pre tax deduction, or \"T\" if this is a " +
                "post tax deduction");
        String letter = get_valid_letter_response("P", "T");
        if (Objects.equals("P", letter))
            employees[response].get_withholding_object().add_pre_tax_deduction(title, amount);
        else
            employees[response].get_withholding_object().add_post_tax_deduction(title, amount);
    }
    public void delete_deduction(){
        ArrayList<Integer> valid_numbers = new ArrayList<Integer>();
        for (int i = 0; i < employees.length; i++){
            System.out.println("[" + i + "] " +employees[get_user_index()].get_user());
        }
        System.out.println("Enter the number of the employee that you would like to delete a deduction for: ");
        int response = get_valid_int_response(0, employees.length - 1);
        valid_numbers = employees[response].get_withholding_object().print_extra_deduction_information();
        int index = get_valid_number_in_array(valid_numbers);
        employees[response].get_withholding_object().delete_deduction(index);
    }
    public int get_valid_number_in_array(ArrayList<Integer> valid_numbers){
        Boolean valid_number_found = false;
        int number = -1;
        while (!valid_number_found){
            System.out.println("Enter the number in parenthesis of the deduction you would like to delete: ");
            number = get_valid_int_response(0, 50);
            for (int i = 0; i < valid_numbers.size(); i++){
                if (number == valid_numbers.get(i)){
                    valid_number_found = true;
                }
            }
            if (valid_number_found == false){
                System.out.print("Invalid Entry! ");
            }
        }
        return number;
    }
    public void add_gift() {
        Scanner scanner = new Scanner(System.in);
        for (int i = 0; i < employees.length; i++){
            System.out.println("[" + i + "] " +employees[get_user_index()].get_user());
        }
        System.out.println("Enter the number of the employee that you would like to add a flat rate bonus for: ");
        int response = get_valid_int_response(0, employees.length - 1);
        System.out.println("Enter the title of the gift: ");
        String title = scanner.nextLine();
        System.out.print("Enter the amount of the gift: ");
        double gift_amount = get_valid_double_response();
        System.out.print("Enter \"P\" is this is a pretax gift or \"T\" if this is a post tax gift: ");
        String letter = get_valid_letter_response("P", "T");
        if (Objects.equals("P", letter)){
            employees[response].get_withholding_object().add_pre_tax_gift(title, gift_amount);
        }
        else{
            employees[response].get_withholding_object().add_post_tax_gift(title, gift_amount);
        }
    }
    public void delete_gift(){
        ArrayList<Integer> valid_numbers = new ArrayList<Integer>();
        for (int i = 0; i < employees.length; i++){
            System.out.println("[" + i + "] " +employees[get_user_index()].get_user());
        }
        System.out.println("Enter the number of the employee that you would like to delete a gift for: ");
        int response = get_valid_int_response(0, employees.length - 1);
        valid_numbers = employees[response].get_withholding_object().print_gift_information();
        int index = get_valid_number_in_array(valid_numbers);
        employees[response].get_withholding_object().delete_gift(index);

    }
    public float get_valid_float_response(int lower_bound, int higher_bound){
        Scanner scanner = new Scanner(System.in);
        float response = 0f;
        if (scanner.hasNextFloat()) {
            response = scanner.nextFloat();
            if (response < lower_bound || response > higher_bound) {
                while (response < lower_bound || response > higher_bound) {
                    System.out.print("Invalid input. Enter a number as a percent between " + lower_bound + " and " +
                            higher_bound + ", inclusive: ");
                    if (scanner.hasNextFloat()) {
                        response = scanner.nextFloat();
                    }

                }
            }
        } else {
            scanner.next();
            response = -1;
            while (response < lower_bound || response > higher_bound) {
                System.out.print("Invalid input. Enter a number as a percent between " + lower_bound + " and " +
                        higher_bound + ", inclusive: ");
                if (scanner.hasNextFloat()) {
                    response = scanner.nextFloat();
                } else
                    scanner.next();
            }
        }
        return response;
    }
}
