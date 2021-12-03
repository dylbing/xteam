import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.Scanner;

public class Payroll_Calculations extends Copy_of_Admin_Dashboard{
    private String state;
    private String frequency;
    protected static float suta_rate;
    protected static float futa_rate;
    protected static int suta_threshold = 14000;
    protected static int futa_threshold = 7000;
    protected static int social_security_threshold = 142800;
    protected static float social_security_rate = .0602f;
    protected static float medicare_rate = .0145f;
    private static Withholding[] empl_pay_data;
    private static Employee[] employees;
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

    public void menu() throws IOException, InterruptedException {
        Scanner scanner = new Scanner(System.in);
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
        System.out.print("Enter a number corresponding to the operation you would like to perform: ");
        int selection = get_valid_int_response(1, 9);
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
                //delete_gift();

        }

    }
    public void process_all_payroll() throws IOException {
        for (int i = 0; i < employees.length; i++){

            employees[i].get_withholding_object().get_excel_data("State", employees[i].get_marital_status(), frequency);
            employees[i].get_withholding_object().get_excel_data("Federal", employees[i].get_marital_status(), frequency);
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
        int index = -1;
        System.out.print("Enter \"O\" to add a deduction for one employee, or \"A\" to add" +
                " a deduction for all employees: ");
        Scanner scanner = new Scanner(System.in);
        String response = get_valid_letter_response("O", "A");
        System.out.print("Enter \"A\" for pre tax deduction or \"B\" for post tax deduction: ");
        String type = get_valid_letter_response("A", "B");
        System.out.print("Enter the name of the deduction: ");
        String title = scanner.nextLine();
        System.out.print("Enter the amount to be withheld every pay period: ");
        double amount = get_valid_double_response();
        if (Objects.equals("O", response)){
            System.out.print("Enter the name of the employee: ");
            scanner.nextLine();
            String name = scanner.nextLine();
            name = name.toUpperCase();
            for (int i = 0; i < get_user_count(); i++){
                if (Objects.equals(name, employees[i].get_user()))
                    index = i;
            }
            if (index >= 0){

                if (Objects.equals(type, "A"))
                    employees[index].get_withholding_object().add_pre_tax_deduction(title, amount);
                else
                    employees[index].get_withholding_object().add_post_tax_deduction(title, amount);
                System.out.println("Operation performed successfully.");
                Thread.sleep(1500);
            }
            else
                System.out.println("User not found.");
        }
        else{
            for (int i = 0; i < employees.length; i++){
                if (Objects.equals(type, "A"))
                    employees[i].get_withholding_object().add_pre_tax_deduction(title, amount);
                else
                    employees[i].get_withholding_object().add_post_tax_deduction(title, amount);
            }
            System.out.println("Operation performed successfully.");
        }
    }
    public void delete_deduction(){
        int index = -1;
        System.out.print("Enter \"O\" to delete a deduction for one employee, or \"A\" to delete" +
                " a deduction for all employees: ");
        String type = get_valid_letter_response("O", "A");
        System.out.print("Enter \"A\" if this is a pretax deduction or \"B\" if this is a post tax deduction: ");
        String selection = get_valid_letter_response("A", "B");
        Scanner scanner = new Scanner(System.in);
        boolean successful = false;
        if (Objects.equals("O", type)){
            System.out.print("Enter the name of the employee: ");
            String name = scanner.nextLine();
            name = name.toUpperCase();
            for (int i = 0; i < get_user_count(); i++){
                if (Objects.equals(name, employees[i].get_user())){
                    index = i;
                }
            }
            if (index >= 0){
                System.out.println("Extra withholding for: " + employees[index].get_user());
                employees[index].get_withholding_object().print_extra_deduction_information();
                System.out.print("Enter the name of the deduction: ");
                String title = scanner.nextLine();
                System.out.print("Enter the amount of the deduction;");
                double amount = get_valid_double_response();
                if (Objects.equals(selection, "A"))
                    successful = employees[index].get_withholding_object().delete_pre_tax_deduct(title, amount);
                else
                    successful = employees[index].get_withholding_object().delete_post_tax_deduct(title, amount);
                if (successful)
                    System.out.println("Operation performed successfully.");
                else
                    System.out.println("Operation could not be performed because the deduction could not be found. ");
            }
            else
                System.out.println("User not found.");
        }
        else{
            System.out.print("Enter the name of the deduction: ");
            String title = scanner.nextLine();
            System.out.print("Enter the amount of the deduction;");
            double amount = get_valid_double_response();
            for (int i = 0; i < get_user_count(); i++){
                if (Objects.equals(selection, "A")){
                    employees[i].get_withholding_object().delete_pre_tax_deduct(title, amount);
                }
                else
                    employees[i].get_withholding_object().delete_post_tax_deduct(title, amount);
                System.out.println("If the deduction: " + title + ", with amount: " + amount +
                        " existed, it has been deleted for all.");
            }
        }
    }
    public void add_gift(){
        int index = -1;
        System.out.print("Enter \"O\" to add a gift for one employee, or \"A\" to add" +
                " a gift for all employees: ");
        Scanner scanner = new Scanner(System.in);
        String response = get_valid_letter_response("O", "A");
        System.out.print("Enter \"A\" if this is a pretax gift or \"B\" if this is a post tax gift: ");
        String selection = get_valid_letter_response("A", "B");
        System.out.print("Enter the name of the gift: ");
        String title = scanner.nextLine();
        System.out.print("Enter the amount of the gift: ");
        double amount = get_valid_double_response();
        if (Objects.equals("O", response)){
            System.out.print("Enter the name of the employee: ");
            String name = scanner.nextLine();
            name = name.toUpperCase();
            for (int i = 0; i < get_user_count(); i++){
                if (Objects.equals(name, employees[i].get_user()))
                    index = i;
            }
            if (index >= 0){
                if (Objects.equals("A", selection))
                    employees[index].get_withholding_object().add_pre_tax_gift(title, amount);
                else
                    employees[index].get_withholding_object().add_post_tax_gift(title, amount);
                System.out.println("Operation performed successfully.");
            }
            else
                System.out.println("User not found.");
        }
        else{
            for (int i = 0; i < employees.length; i++){
                if (Objects.equals(selection, "A"))
                    employees[i].get_withholding_object().add_pre_tax_gift(title, amount);
                else
                    employees[i].get_withholding_object().add_post_tax_gift(title, amount);
            }
            System.out.println("Operation performed successfully.");
        }
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
