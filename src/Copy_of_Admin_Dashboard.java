import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.Scanner;

public class Copy_of_Admin_Dashboard extends Copy_of_Access_Control{
    private final float non_reasonable_pay = 1e11f;
    private final int one_hundred_years = 36525;
    private static int max_data = 50;
    protected static Employee[] employees = new Employee[max_data];
    private static int current_user_index;
    private static String current_user_name;
    protected static int[] supreme_users = new int[max_data];
    protected static int supreme_count = 0;
    Payroll_Calculations payroll = null;
    public Copy_of_Admin_Dashboard() throws NoSuchAlgorithmException, InterruptedException {
    }
    public void Welcome(int user_index) throws IOException, NoSuchAlgorithmException, InterruptedException {
        current_user_index = user_index;
        current_user_name = get_username(user_index);
        Date date = new java.util.Date();
        int hours = date.getHours();
        if (hours >= 0 && hours < 12)
            System.out.println("Good morning, " + current_user_name + "!");
        else if (hours >= 12 && hours <= 16)
            System.out.println("Good afternoon, " + current_user_name + "!");
        else
            System.out.println("Good evening, " + current_user_name + "!");
        Menu();
    }
    public void Menu() throws NoSuchAlgorithmException, InterruptedException, IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("What would you like to do?");
        System.out.println("[1] View Employee List");
        System.out.println("[2] Enter Payroll Information");
        System.out.println("[3] Save Data");
        System.out.println("[4] Enter My Employee Dashboard");
        System.out.println("[5] Edit Files");
        System.out.println("[6] Logout");
        System.out.print("Enter your selection: ");
        int selection = 0;
        int lower_bound = 1;
        int upper_bound = 6;
        selection = get_valid_int_response(lower_bound,upper_bound);
        switch (selection) {
            case 1:
                Employee_List();
                break;
            case 2:
                if (payroll == null){
                    payroll = new Payroll_Calculations(employees);
                    payroll.menu();
                }
                else{
                    payroll.menu();
                }

                Menu();
            case 3:
                Save save = new Save(admin, Main.get_access_control_object());
                this.Menu();
                break;
            case 4:
                Options(current_user_name);
                break;
            case 5:
                if (employees[current_user_index].get_high_level_status()) {
                    save = Main.get_save_object();
                    if (save == null)
                        save = new Save(true);
                    else
                        save.File_Management();
                    Menu();
                } else {
                    System.out.println("You do not have the requisite access level to make changes to files. " +
                            "Please seek a higher level of management to make changes.");
                    Thread.sleep(2800);
                    Menu();
                }

            case 6:
                System.out.println("You have successfully logged out.");
                Clear.cls();
                Main.Login_Page();
                break;
        }
    }
    public void Employee_List() throws NoSuchAlgorithmException, InterruptedException, IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Name Username ID Position"
                + " Rate/Salary Tax Exemptions Marital Status");
        System.out.print("\n");
        for (int i = 0; i < get_user_count(); i++) {
            System.out.println((i+1) + ". " + employees[i].get_user() + " " + employees[i].get_id()+ " " + employees[i].get_position() + " " +
                    employees[i].get_salary() + " " + employees[i].get_tax_exemptions() + " " + employees[i].get_marital_status());
        }
        System.out.println("---------------------");
        System.out.println("[1] Add new user");
        System.out.println("[2] Edit Employee Information");
        System.out.println("[3] Return to menu");
        int selection = 0;
        int upper_bound = 3;
        int lower_bound = 1;
        if (scanner.hasNextInt()){
            selection = scanner.nextInt();
            while (selection > upper_bound || selection < lower_bound){
                System.out.print("Invalid range entered. Enter a number that corresponds to the selection (" + lower_bound + " to " + upper_bound + ")" + " that you wish to make");
                if (scanner.hasNextInt()){
                    selection = scanner.nextInt();
                    scanner.next();
                }
                else
                    scanner.next();
            }
        }
        else{
            System.out.print("Invalid Input! You must enter a number that corresponds to the selection you wish to make: ");
        }
        switch (selection) {
            case 1:
                Add_User(false);
                break;
            case 2:
                Edit_User();
                break;
            case 3:
                Clear.cls();
                this.Menu();
                break;
            default:
                System.out.print("Invalid selection");
        }
    }
    private void Edit_User() throws NoSuchAlgorithmException, InterruptedException, IOException {
        Scanner scanner = new Scanner(System.in);
        int i = 0;
        int threshold = 3;
        System.out.println("Enter the first and last name of the employee: ");
        String name = scanner.nextLine();
        name = name.toUpperCase();
        //scanner.nextLine();
        int user_index = Search(name);
        if (user_index < 0) {
            while (Search(name) < 0 && i < threshold){
                System.out.println("User not found");
                System.out.print("Enter the first and last name of the employee: ");
                name = scanner.nextLine();
                name = name.toUpperCase();
                i++;
                if (i >= threshold){
                    System.out.print("Return to Employee List? (Y/N) ");
                    String input = scanner.nextLine();
                    input = input.toUpperCase();
                    if (Objects.equals(input, "N")) {
                        i = 0;
                    }
                    else {
                        Employee_List();
                    }
                }
            }
        }
        user_index = Search(name);
        if ((!employees[current_user_index].get_high_level_status()) && employees[user_index].get_access_type().equals("Admin")
                && !Objects.equals(current_user_index, employees[user_index].get_user())){
            System.out.println("You do not have the requisite access level to change this user's information.");
            Thread.sleep(3200);
            Employee_List();
        }
        else if (employees[current_user_index].get_high_level_status() && employees[user_index].get_high_level_status() &&
                !Objects.equals(employees[user_index].get_user(), employees[current_user_index].get_user())){
            System.out.println("You are attempting to make changes to a high level manager. " +
                    "You must enter supreme authority credentials to proceed.");
            boolean result = supreme_validation();
            if (!result){
                System.out.println("Returning to main menu ... ");
                Employee_List();
            }
            else if (result && (employees[user_index].get_supreme_status() == employees[current_user_index].get_supreme_status())){
                System.out.println("You may not make changes to the supreme authority's account. They must " +
                        "transfer supreme authority over to you for you to make changes to their account. ");
                Employee_List();
            }
        }
        System.out.println((i+1) + ". " + employees[user_index].get_user() + " " + employees[user_index].get_id()+ " " + employees[user_index].get_position() + " " +
                employees[user_index].get_salary() + " " + employees[user_index].get_tax_exemptions() + " " + employees[i].get_marital_status());
        int upper_bound = 7;
        int lower_bound = 1;
        System.out.println("Enter the number corresponding to what you would you like to change.");
        System.out.println("[1] name");
        System.out.println("[2] password");
        System.out.println("[3] position");
        System.out.println("[4] salary");
        System.out.println("[5] tax exemptions");
        System.out.println("[6] delete user");
        System.out.println("[7] access type");
        System.out.print("Your selection: ");
        int input;
        input = get_valid_int_response(lower_bound, upper_bound);
        String user_change;
        Calendar cal = Calendar.getInstance();
        cal.getTime();
        int days = 0;
        switch (input) {
            //TODO ther is a formating error preventing the change to nextLine which mean next only reads the next token
            case 1:
                System.out.println("Please enter the desired first and last name: ");
                user_change = scanner.nextLine();
                user_change = user_change.toUpperCase();
                employees[user_index].set_user(user_change);
                System.out.println("Name successfully changed to " + user_change);
                break;
            case 2:
                System.out.print("Please enter the desired password: ");
                user_change = scanner.next();
                employees[user_index].set_password(Hash.toHexString(Hash.getSHA(user_change)));
                System.out.println("Password successfully changed to " + user_change);
                break;
            case 3:
                System.out.print("Please enter the desired position title: ");
                user_change = scanner.nextLine();
                employees[user_index].set_position(user_change);
                System.out.println("Position title successfully changed to " + user_change);
                break;
            case 4:
                System.out.print("Enter annual salary or hourly rate: ");
                double value = 0;
                value = get_valid_double_response();
                employees[user_index].set_salary(value);
                System.out.print("Is the amount: " + value + " a salary or hourly rate (S or H)?: ");
                user_change = get_valid_letter_response("S", "H");
                if (Objects.equals(user_change, "S")) {
                    employees[user_index].set_hourly_or_hourly(true);
                } else {
                    employees[user_index].set_hourly_or_hourly(false);
                }
                if (employees[user_index].get_salary_or_hourly())
                    System.out.println("The salary of " + value + " has sucussfully been implemented.");
                else
                    System.out.println("The hourly rate of " + value + " has sucussfully been implemented.");
                break;
            case 5:
                int tax_e;
                System.out.print("Enter a tax exemption allowance number: ");
                tax_e = get_valid_int_response(0, 10);
                employees[user_index].set_tax_exemptions(tax_e);
                break;
            case 6:
                System.out.println("Are you sure you would like to delete: ");
                System.out.println((i+1) + ". " + employees[i].get_user() + " " + employees[i].get_id()+ " " + employees[i].get_position() + " " +
                        employees[i].get_salary() + " " + employees[i].get_tax_exemptions() + " " + employees[i].get_marital_status());
                System.out.print("Enter \"Y\" to confirm or \"N\" to abort operation: ");
                String user_user_decision = get_valid_letter_response("Y", "N");
                if (Objects.equals(user_user_decision, "Y")) {
                    String deleted_user = employees[user_index].get_user();
                    if (employees[user_index].get_high_level_status() && get_high_level_count() <= 1){
                        System.out.println("Action cannot be performed because there are not enough " +
                                "admins with privileges to modify other admins. ");
                        Thread.sleep(2300);
                        Employee_List();
                    }
                    else if (Objects.equals(current_user_name, employees[user_index].get_user())){
                        System.out.println("You cannot delete yourself. You must have another individual " +
                                "with admin privileges perform a delete operation for you. ");
                        Thread.sleep(2300);
                        Employee_List();
                    }
                    else {
                        for (int g = user_index; g < get_user_count(); g++){
                            employees[g] = employees[g + 1];
                        }
                        set_stored_count(get_user_count() - 1);
                    }
                    System.out.println(deleted_user + " has successfully been deleted.");
                }
                else{
                    System.out.println("Action aborted.");
                }

                break;
            case 7:
                int low_bound;
                int high_boundary;
                if (employees[current_user_index].get_supreme_status()){
                    System.out.println("[1] Add user to high level manager group");
                    System.out.println("[2] Remove user from high level manager group");
                    System.out.println("[3] Promote employee user to standard level admin");
                    System.out.println("[4] Demote user to employee only");
                    System.out.println("[5] Give supreme authority");
                    low_bound = 1;
                    high_boundary = 5;
                }
                else
                {
                    System.out.println("[1] Add user to high level manager group");
                    System.out.println("[2] Remove user from high level manager group");
                    System.out.println("[3] Make user standard level admin");
                    System.out.println("[4] Demote user to employee only");
                    low_bound = 1;
                    high_boundary = 4;
                }
                int admin_select = -1;
                System.out.print("Enter your selection: ");
                if (scanner.hasNextInt()){
                    admin_select = scanner.nextInt();
                    while (admin_select > high_boundary || admin_select < low_bound){
                        System.out.print("Selection must be a number between " + low_bound + " and " + high_boundary + ", inclusive.");
                        System.out.print("Enter your selection: ");
                        if (scanner.hasNext()){
                            admin_select = scanner.nextInt();
                        }
                        scanner.nextLine();
                    }
                }
                else{
                    while (admin_select > high_boundary || admin_select < low_bound){
                        System.out.print("Selection must be a number between " + low_bound + " and " + high_boundary + ", inclusive.");
                        System.out.print("Enter your selection: ");
                        if (scanner.hasNext()){
                            admin_select = scanner.nextInt();
                        }
                        scanner.nextLine();
                    }
                }
                scanner.nextLine();
                if (Objects.equals(employees[user_index].get_user(), current_user_name) && !employees[current_user_index].get_supreme_status()){
                    System.out.println("You are not allowed to change your own access level.");
                    Employee_List();
                }
                else if (employees[user_index].get_supreme_status()){
                    System.out.println("You cannot modify this access level. The user must give supreme authority to another" +
                            " user first and have them change their access level. ");
                    Employee_List();
                }
                //TODO currently the supreme leader can demote themselves without designating a supreme leader
                switch (admin_select){
                    case 1:
                        if (!employees[user_index].get_high_level_status()) {
                            System.out.print("Enter \"P\" if this is a permanent change or \"T\" if this a a temporary change. ");
                            String decision = get_valid_letter_response("T", "P");
                            if (Objects.equals(decision, "T")){
                                System.out.print("Enter the duration of this change in days: ");
                                days = get_valid_int_response(1, one_hundred_years);
                                employees[user_index].temp_high_level(days);
                                cal.add(Calendar.DATE, days);
                                System.out.println(employees[user_index].get_user() + "is a high level manager until " + cal.getTime());
                            }
                            else{
                                employees[user_index].set_high_level_status(true);
                                employees[user_index].set_access_level("Admin");
                                System.out.println("User has been made a high level manager.");
                            }

                        }
                        else
                            System.out.println("User is already a high level manager. ");
                        break;
                    case 2:
                        if (employees[user_index].get_high_level_status()){
                            employees[user_index].set_high_level_status(false);
                            System.out.print("User removed from high level management. ");
                        }
                        else{
                            System.out.println("User is already not a high level manager.");
                        }
                        break;
                    case 3:
                        System.out.print("Enter \"P\" if this is a permanent change or \"T\" if this a a temporary change. ");
                        String decision = get_valid_letter_response("T", "P");
                        if (Objects.equals(decision, "T")){
                            System.out.print("Enter the duration of this change in days: ");
                            days = get_valid_int_response(1, one_hundred_years);
                            cal.add(Calendar.DATE, days);
                            employees[user_index].temp_admin(days);
                            System.out.println(employees[user_index].get_user() + "is an admin until " + cal.getTime());
                        }
                        else{
                            employees[user_index].set_high_level_status(false);
                            employees[user_index].set_access_level("Admin");
                            System.out.println("User has been made a standard admin.");
                        }
                        if (Objects.equals(employees[user_index].get_access_type(), "Employee")){
                            employees[user_index].set_access_level("Admin");
                            System.out.println("User promoted to standard level admin.");
                        }
                        else
                            System.out.println("User is already an admin");
                        break;
                    case 4:
                        System.out.print("Enter \"T\" if this is a permanent change or \"P\" if this a a temporary change. ");
                        String decision_a = get_valid_letter_response("T", "P");
                        if ((employees[user_index].get_supreme_status() && get_supreme_count() >= 1) ||
                            !employees[user_index].get_supreme_status()){
                            if (Objects.equals(decision_a, "T")){
                                System.out.print("Enter the duration of this change in days: ");
                                days = get_valid_int_response(1, one_hundred_years);
                                cal.add(Calendar.DATE, days);
                                employees[user_index].temp_demotion_employee(days);
                                System.out.println(employees[user_index].get_user() + "is demoted to employee until " + cal.getTime());
                            }
                            else {
                                employees[user_index].set_supreme_leader(false);
                                employees[user_index].set_access_level("Employee");
                                employees[user_index].set_high_level_status(false);
                                System.out.println(employees[user_index].get_user() + "has been demoted to employee.");
                            }
                        }
                        else {
                            System.out.println("Cannot demote user to employee because there are not enough supreme leaders.");
                        }
                        break;
                    case 5:
                        System.out.println("Are you sure you would like to give supreme authority to " +
                                employees[user_index].get_user() + "?");
                        System.out.print("Enter \"Y\" or \"N\": ");
                        String selection = get_valid_letter_response("Y", "N");
                        selection = selection.toUpperCase();
                        if (Objects.equals(selection, "Y")){
                            System.out.print("Enter \"T\" if this is a permanent change or \"P\" if this a a temporary change. ");
                            System.out.print("Enter the duration of this change in days: ");
                            String decision_z = get_valid_letter_response("T", "P");
                            if (Objects.equals(decision_z, "T")){
                                days = get_valid_int_response(1, one_hundred_years);
                                cal.add(Calendar.DATE, days);
                                employees[user_index].temp_supreme_leader(days);
                                System.out.println(employees[user_index].get_user() + " is a supreme leader until " + cal.getTime());
                            }
                            else{
                                employees[user_index].set_supreme_leader(true);
                                employees[user_index].set_access_level("Admin");
                                employees[user_index].set_high_level_status(true);
                                System.out.println(employees[user_index].get_user() + " has been made a supreme leader. ");
                            }

                        }
                        else{
                            System.out.println("Action aborted. ");
                        }
                        break;
                }
        }
        Employee_List();
    }
    public double get_valid_double_response(){
        Scanner scanner = new Scanner(System.in);
        double value = 0;
        if (scanner.hasNextDouble()) {
            value = scanner.nextDouble();
            while (value < 0 || value >= non_reasonable_pay) {
                System.out.println("Invalid entry. Number cannot be negative and must be in numerical form and be a reasonable" +
                        " amount");
                System.out.print("Enter new number: ");
                if (scanner.hasNextFloat()) {
                    value = scanner.nextFloat();
                }
            }
        } else {
            scanner.next();
            value = -1;
            while (value < 0 || value >= non_reasonable_pay) {
                System.out.println("Invalid entry. Number cannot be negative and must be in numerical form and be " +
                        "a reasonable amount. ");
                System.out.print("Enter new number: ");
                if (scanner.hasNextDouble()) {
                    value = scanner.nextDouble();
                } else
                    scanner.next();
            }
        }
        return value;
    }
    public void Add_User(boolean first_use) throws NoSuchAlgorithmException, IOException, InterruptedException {
        employees[get_user_count()] = new Employee();
        Scanner scanner = new Scanner(System.in);
        String user, acc_type;
        System.out.print("Enter the first and last name of the employee: ");
        user = scanner.nextLine();
        while (Search(user) >= 0){
            System.out.print("This name is already taken, please include a middle name or another distinguishing component: ");
            user = scanner.nextLine();
        }
        user = user.trim();
        user = user.toUpperCase();
        System.out.print("Enter marital status (M or S): ");
        String marital_status = get_valid_letter_response("S", "M");
        employees[get_user_count()].set_marital_status(marital_status);
        employees[get_user_count()].set_user(user);
        if (first_use){
            System.out.println("Your account type has automatically been selected as Admin.");
            acc_type = "ADMIN";
        }
        else{
            System.out.print("Enter either \"Employee\" or \"Admin\" for access type: ");
            acc_type = get_valid_letter_response("ADMIN", "EMPLOYEE");
        }
        employees[get_user_count()].set_access_level(acc_type);
        if ((acc_type.equals("ADMIN") && employees[current_user_index].get_high_level_status())){ // only allows asks this question if the person creating the account is a high level manager
            System.out.print("Is this user allowed to make changes to other admin accounts? (Y/N): ");
            String input;
            input = get_valid_letter_response("Y", "N");
            if (input.equals("Y")) {
                employees[get_user_count()].set_high_level_status(true);
            }
        }
        if (first_use){
            System.out.println("You have automatically been given the ability to make changes" +
                    " to any future admin accounts. ");
            employees[get_user_count()].set_high_level_status(true);
            employees[get_user_count()].set_supreme_leader(true);
            System.out.println("You have also been given supreme authority over all users. This can be changed" +
                    " later if desired.");
        }
        String position = "";
        employees[get_user_count()].set_id(get_user_count());
        System.out.print("Enter position title: ");
        position = scanner.nextLine();
        employees[get_user_count()].set_position(position);
        System.out.print("Enter annual salary or hourly rate: ");
        double value = 0;
        value = get_valid_double_response();
        employees[get_user_count()].set_salary(value);
        System.out.print("Enter \"S\" if the above entered number is a salary or \"H\" if the above entered number is an hourly rate: ");
        String temp = "";
        temp = get_valid_letter_response("S", "H");
        if (Objects.equals(temp, "S"))
            employees[get_user_count()].set_hourly_or_hourly(true);
        else{
            employees[get_user_count()].set_hourly_or_hourly(false);
            System.out.print("Is this user paid for lunch? (Enter \"Y\" or \"N\"): ");
            String paid_lunch = get_valid_letter_response("Y", "N");
            if (Objects.equals(paid_lunch, "Y"))
                employees[get_user_count()].set_paid_lunch(true);
            else
                employees[get_user_count()].set_paid_lunch(false);
        }



        System.out.print("Enter a tax exemption allowance number: ");
        int tax_exemptions = get_valid_int_response(0, 10);
        System.out.print("Did the employee check the checkbox in step two of the W-4? (\"Y\" or \"N\"): ");
        String response = get_valid_letter_response("Y", "N");
        if (Objects.equals(response, "Y"))
            employees[get_user_count()].set_w4_box(true);
        else
            employees[get_user_count()].set_w4_box(false);
        System.out.println("Is the employee the head of their household? \"Y\" or \"N\"): ");
        response = get_valid_letter_response("Y", "N");
        if (Objects.equals(response, "Y"))
            employees[get_user_count()].set_head_of_household(true);
        else
            employees[get_user_count()].set_head_of_household(false);
        employees[get_user_count()].set_withholding_object();
        String new_pass = create_new_user_password(user, acc_type);
        Clear.cls();
        System.out.println("User: " + employees[get_user_count()-1].get_user() + ", with password: " + new_pass +
                " with access type: " + employees[get_user_count()-1].get_access_type() + ", has been created successfully.");
        if (first_use){
            System.out.print("Type \"Y\" when you are ready to return to login. Remember to record your password. ");
            String input = get_valid_letter_response("Y", "Y");
            if ("Y".equals(input))
                Main.Login_Page();
        }
        else
            Employee_List();
    }

    public String get_valid_letter_response(String valid1, String valid2) {
        Scanner scanner = new Scanner(System.in);
        String response = scanner.nextLine();
        response = response.toUpperCase();
        if (Objects.equals(response, valid1))
            return valid1;
        else if (Objects.equals(response, valid2))
            return valid2;
        else {
            while (!Objects.equals(response, valid1) && !Objects.equals(response, valid2)) {
                System.out.print("Invalid input, Enter " + valid1 + " or " + valid2 + ": ");
                response = scanner.nextLine();
                response = response.toUpperCase();
            }
        }
        return response;
    }
    public int get_valid_int_response(int lower_bound, int higher_bound){
        Scanner scanner = new Scanner(System.in);
        int response = 0;
        if (scanner.hasNextInt()){
            response = scanner.nextInt();
            if (response < lower_bound || response > higher_bound){
                while (response < lower_bound || response > higher_bound){
                    System.out.print("Invalid input. Enter an integer number between " + lower_bound + " and " +
                    higher_bound + ", inclusive: ");
                    if (scanner.hasNextInt()){
                        response = scanner.nextInt();
                    }

                }
            }
        }
        else{
            scanner.next();
            response = -1;
            while (response < lower_bound || response > higher_bound){
                System.out.print("Invalid input. Enter an integer number of days between " + lower_bound + " and " +
                        higher_bound + ", inclusive: ");
                if (scanner.hasNextInt()){
                    response = scanner.nextInt();
                }
                else
                    scanner.next();
            }
        }
        return response;

    }
    public void get_supreme_users(){
        for (int i = 0; i < get_user_count(); i++){
            if (employees[i].get_supreme_status()){
                supreme_users[supreme_count] = i;
                supreme_count++;
            }
        }
    }
    public int[] get_supreme_list(){
        return supreme_users;
    }
    public int get_supreme_count(){
        return supreme_count;
    }
    public int get_high_level_count(){
        int high_level_count = 0;
        for (int i = 0; i < get_user_count(); i++){
            if (employees[i].get_high_level_status()){
                high_level_count++;
            }
        }
        return high_level_count;
    }
    public Employee[] get_employee_list(){
        return employees;
    }
    public void set_employee_list(Employee[] employees){
        this.employees = employees;
    }
    public Payroll_Calculations get_payroll_object(){
        return payroll;
    }
    public void set_payroll_object() throws NoSuchAlgorithmException, InterruptedException {
        payroll = new Payroll_Calculations();
    }
}
