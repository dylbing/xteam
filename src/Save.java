import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

//TODO allow an user to add as many new files as they want and store those file in an array
//TODO also consider the possibility of deleting these files
//TODO the modularity for the way data is retrieved is already present and is versatile given any file
public class Save implements Serializable {
    //Organization
    int max_data = 50;
    static int max_static = 50;
    private static String[] files = new String[max_static];
    private static int file_count;
    private ArrayList<Object> employee_data = new ArrayList<Object>();
    private ArrayList<Object> file_data = new ArrayList<Object>();
    //TODO look at the files array and figure out to to store and retrieve
    //Access_Control data
    private Copy_of_Access_Control user;
    private static int stored_count;
    private Copy_of_Admin_Dashboard admin;
    //Admin_Dashboard Data
    private static String[] access_type = new String[max_static];
    private static String[] name = new String[max_static];
    private static String[] password = new String[max_static];
    private static int[] id = new int[max_static];
    private static int[] tax_exemptions = new int[max_static];
    private Employee[] employees = new Employee[max_data];
    private static String[] marital_status = new String[max_static];
    private static String[] position = new String[max_static];
    private static double[] salary = new double[max_static];
    private static boolean[] sal_or_hourly = new boolean[max_static];
    private static boolean[] supreme_leader = new boolean[max_static];
    private static boolean[] high_level_manager = new boolean[max_static];
    private static ArrayList<Object>  punch_logs = new ArrayList<Object>();
    private static LocalDate[] current_days = new LocalDate[max_static];
    private static boolean[] w4_checkbox = new boolean[max_static];
    private static boolean[] head_of_household = new boolean[max_static];

    private static float suta_rate;
    private static float futa_rate;
    private static int suta_threshold;
    private static int futa_threshold;
    private static int social_security_threshold;
    private static float social_security_rate;
    private static float medicare_rate;
    private static String payroll_frequency;

    private static ArrayList<Object> pay_summary = new ArrayList<Object>();

    Save(Copy_of_Admin_Dashboard admin, Copy_of_Access_Control user) throws FileNotFoundException {
        this.admin = admin;
        this.user = user;
        get_class_data();
        save_to_file();

        // only put here for test purposes
        //retrieve_saved_data();
    }
    Save() throws NoSuchAlgorithmException, InterruptedException {
        restore_information();
    }
    Save(boolean file_management) throws FileNotFoundException {
        File_Management();
    }
    public void print_file_array(){
        for (int i = 0; i < file_count; i++){
            System.out.println("[" + i + "]" + files[i]);
        }
    }
    public void File_Management() throws FileNotFoundException {
        int selection = -1;
        int num_options = 2;
        Scanner scanner = new Scanner(System.in);
        get_files();
        print_file_array();
        System.out.println("[1] Add New File");
        System.out.println("[2] Delete a File");
        System.out.println("Enter the number corresponding to the action you would like to perform:");
        if (scanner.hasNextInt()){
            selection = scanner.nextInt();
            scanner.nextLine();
        }
        else
            while (!scanner.hasNextInt() || selection >= num_options || selection < 1){
                System.out.print("Invalid Input, please enter a number corresponding to the file you wish to delete: ");
                if (scanner.hasNextInt())
                    selection = scanner.nextInt();
                scanner.nextLine();
            }
        if (selection == 1){
            Create_New_File();
        }
        else
            Delete_File();
    }
    public void Delete_File() throws FileNotFoundException {
        int file_s = -1;
        Scanner scanner = new Scanner(System.in);

        print_file_array();
        System.out.print("Enter the number corresponding to the number of the file you would like to delete: ");
        if (scanner.hasNextInt()){
            file_s = scanner.nextInt();
            scanner.nextLine();
        }
        else
            while (!scanner.hasNextInt() || file_s >= file_count || file_s < 0){
                System.out.print("Invalid Input, please enter a number corresponding to the file you wish to delete: ");
                if (scanner.hasNextInt())
                    file_s = scanner.nextInt();
                scanner.nextLine();
            }
            String filepath = files[file_s];
        for (int i = file_s; i < file_count; i++){
            files[i] = files[i + 1];
        }
        if (file_count > 1){
            file_count--;
        }

        file_data.add(files);
        file_data.add(file_count);
        write_file_names();
        System.out.println("File: " + filepath + " has been deleted successfully.");
    }
    public String Create_New_File(){
        Scanner scanner = new Scanner(System.in);
        System.out.print("enter a name for the new file: ");
        String filepath = scanner.nextLine();
        filepath = filepath + ".ser";
        while (search_files(filepath)){
            System.out.print("File name already exists. Please enter a new one: ");
                    filepath = scanner.nextLine();
        }
        System.out.println("Successfully saved data in file: " + filepath);
        files[file_count] = filepath;
        file_count++;
        return filepath;
    }
    public boolean search_files(String filepath){
        for (int i = 0; i < file_count; i++){
            if (Objects.equals(files[i], filepath))
                return true;
        }
        return false;
    }
    public void write_file_names(){
        try{
            FileOutputStream fileOut = new FileOutputStream("file_names.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(file_data);
            out.close();
            fileOut.close();
        } catch (IOException i){
            i.printStackTrace();
        }
    }
    public void get_files(){
        ArrayList<Object> deserialized_3 = new ArrayList<Object>();
        try {
            //FileInputStream fileIn = new FileInputStream("payroll_data.ser");
            FileInputStream fileIn = new FileInputStream("file_names.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            deserialized_3 = (ArrayList<Object>)in.readObject();
            in.close();
            fileIn.close();
        }catch (IOException i){
            i.printStackTrace();
            return;
        }catch (ClassNotFoundException c){
            c.printStackTrace();
            return;
        }
        //separate all the objects in the one deserialized object
        try{
            files = (String[])deserialized_3.get(0);
            file_count = (int)deserialized_3.get(1);
        }catch (Exception e){
            e.printStackTrace();
        }




    }
    public String File_Selection(){
        get_files();
        int file_s = -1;
        String filepath = "";
        Scanner scanner = new Scanner(System.in);
        if (file_count > 0){
            System.out.println("Enter the number corresponding to the number you would like to load: ");
            print_file_array();
            System.out.print("Your selection: ");
            if (scanner.hasNextInt()){
                file_s = scanner.nextInt();
                scanner.nextLine();
            }
            else
                while (!scanner.hasNextInt()){
                    System.out.print("Invalid Input, please enter a number corresponding to the file you wish to open: ");
                    if (scanner.hasNextInt())
                        file_s = scanner.nextInt();
                    scanner.nextLine();
                }
            while (file_s >= file_count || file_s < 0) {
                System.out.println("Invalid input, enter a number corresponding to the file you wish to open: ");
                if (scanner.hasNextInt())
                    file_s = scanner.nextInt();
                scanner.nextLine();
            }
            filepath = files[file_s];
            return filepath;
        }
        else {
            System.out.println("There are no saved files.");
            return "";
        }

    }
    public Copy_of_Access_Control get_access_object(){return user;}
    private void restore_information() throws NoSuchAlgorithmException, InterruptedException {
        String file_path = File_Selection();
        if (!Objects.equals(file_path, "")){
            retrieve_saved_data(file_path);
            admin = new Copy_of_Admin_Dashboard();
            admin.set_employee_list(employees);

            user = new Copy_of_Access_Control();
            user.set_object(admin);
            user.set_stored_count(stored_count);
            admin.set_payroll_object();
            admin.get_payroll_object().set_futa(futa_rate);
            admin.get_payroll_object().set_suta(suta_rate);
            admin.get_payroll_object().set_social_security_threshold(social_security_threshold);
            admin.get_payroll_object().set_social_security_rate(social_security_rate);
            admin.get_payroll_object().set_futa_threshold(futa_threshold);
            admin.get_payroll_object().set_suta_threshold(suta_threshold);
            admin.get_payroll_object().set_medicare_rate(medicare_rate);
            admin.get_payroll_object().set_payroll_frequency(payroll_frequency);
            //TODO add the employee list to admin
            for (int x = 0; x < stored_count; x++){
                employees[x] = new Employee();
                employees[x].restore(access_type[x], name[x], password[x], id[x], tax_exemptions[x],
                             position[x], salary[x], sal_or_hourly[x], supreme_leader[x], high_level_manager[x], marital_status[x],
                        (ArrayList<Object>) punch_logs.get(x), current_days[x]);
                employees[x].set_withholding_object();
                employees[x].set_head_of_household(head_of_household[x]);
                employees[x].set_w4_box(w4_checkbox[x]);
                employees[x].get_withholding_object().set_pay_summary((ArrayList<Object>) pay_summary.get(x));
                employees[x].get_withholding_object().set_employee_object(x);
            }
        }
    }
    private void get_class_data(){
        //Access_Control Data
        this.stored_count = user.get_user_count();
        this.employees = admin.get_employee_list();
        for (int i = 0; i < stored_count; i++){
            access_type[i] = employees[i].get_access_type();
            name[i] = employees[i].get_user();
            password[i] = employees[i].get_password();
            id[i] = employees[i].get_id();
            tax_exemptions[i] = employees[i].get_id();
            position[i] = employees[i].get_position();
            salary[i] = employees[i].get_salary();
            sal_or_hourly[i] = employees[i].get_salary_or_hourly();
            supreme_leader[i] = employees[i].get_supreme_status();
            high_level_manager[i] = employees[i].get_high_level_status();
            marital_status[i] = employees[i].get_marital_status();
            punch_logs.add(employees[i].get_punch_log());
            current_days[i] = employees[i].get_current_day();
            w4_checkbox[i] = employees[i].get_w4check_box();
            head_of_household[i] = employees[i].get_head_of_household();

            suta_rate = admin.get_payroll_object().get_suta();
            futa_rate = admin.get_payroll_object().get_futa();
            suta_threshold = admin.get_payroll_object().get_suta_threshold();
            futa_threshold = admin.get_payroll_object().get_futa_threshold();
            social_security_threshold = admin.get_payroll_object().get_social_security_threshold();
            social_security_rate = admin.get_payroll_object().get_social_security_rate();
            medicare_rate = admin.get_payroll_object().get_medicare_rate();
            payroll_frequency = admin.get_payroll_object().get_payroll_frequency();
            pay_summary.add(employees[i].get_withholding_object().get_pay_summary());

            /*
            suta_threshold;
    private static int futa_threshold;
    private static int social_security_threshold;
    private static float social_security_rate;
    private static float medicare_rate;
    private static String payroll_frequency;

    private static ArrayList<Object> pay_summary = new ArrayList<Object>();
             */
        }

        //Admin_Dashboard Data

    }
    public void add_data_to_list(){
        employee_data.add(stored_count);
        employee_data.add(access_type);
        employee_data.add(name);
        employee_data.add(password);
        employee_data.add(id);
        employee_data.add(tax_exemptions);
        employee_data.add(position);
        employee_data.add(salary);
        employee_data.add(sal_or_hourly);
        employee_data.add(supreme_leader);
        employee_data.add(high_level_manager);
        employee_data.add(marital_status);
        employee_data.add(punch_logs);
        employee_data.add(current_days);

        employee_data.add(w4_checkbox);
        employee_data.add(head_of_household);
        employee_data.add(suta_rate);
        employee_data.add(futa_rate);
        employee_data.add(suta_threshold);
        employee_data.add(futa_threshold);
        employee_data.add(social_security_threshold);
        employee_data.add(social_security_rate);
        employee_data.add(medicare_rate);
        employee_data.add(payroll_frequency);
        employee_data.add(pay_summary);
        file_data.add(files);
        file_data.add(file_count);

    }
    public void save_to_file() throws FileNotFoundException {
        int selection = -1;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the number corresponding to the option you would like to perform. ");
        System.out.println("[1] Select existing file to save to");
        System.out.println("[2] Create a new file to save to");
        System.out.print("Your selection: ");
        if (scanner.hasNextInt()){
            selection = scanner.nextInt();
            scanner.nextLine();
        }
        else
            while (!scanner.hasNextInt() || (selection != 1 && selection != 2)){
                System.out.print("Invalid Input, please enter a number corresponding to the action you wish to perform: ");
                if (scanner.hasNextInt())
                    selection = scanner.nextInt();
                scanner.nextLine();
            }
            String file_path = "";
        if (selection == 1)
            file_path = File_Selection();
        else
            file_path = Create_New_File();
        if (!Objects.equals(file_path, "")){
            try{
                add_data_to_list();
                write_file_names();
                FileOutputStream fileOut = new FileOutputStream(file_path);
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(employee_data);
                out.writeObject(file_data);
                out.close();
                fileOut.close();
                Clear.cls();
                System.out.println("Saved Successfully");
            } catch (IOException i){
                i.printStackTrace();
            }
        }
    }
    public void retrieve_saved_data(String filepath){
        // create array list to store deserialized objects
        ArrayList<Object> deserialized = new ArrayList<Object>();
        ArrayList<Object> deserialized_2 = new ArrayList<Object>();
        //ArrayList<Object> deserialized_3 = new ArrayList<Object>();
        try {
            //FileInputStream fileIn = new FileInputStream("payroll_data.ser");
            FileInputStream fileIn = new FileInputStream(filepath);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            deserialized = (ArrayList<Object>)in.readObject();
            //deserialized_2 = (ArrayList<Object>)in.readObject();
            //deserialized_3 = (ArrayList<Object>)in.readObject();
            in.close();
            fileIn.close();
        }catch (IOException i){
            i.printStackTrace();
            return;
        }catch (ClassNotFoundException c){
            c.printStackTrace();
            return;
        }
        stored_count = (int)deserialized.get(0);
        access_type = (String[])deserialized.get(1);
        name = (String[])deserialized.get(2);
        password = (String[])deserialized.get(3);
        id = (int[])deserialized.get(4);
        tax_exemptions = (int[])deserialized.get(5);
        position = (String[])deserialized.get(6);
        salary = (double[])deserialized.get(7);
        sal_or_hourly = (boolean[])deserialized.get(8);
        supreme_leader = (boolean[])deserialized.get(9);
        high_level_manager = (boolean[])deserialized.get(10);
        marital_status = (String[])deserialized.get(11);
        punch_logs = (ArrayList<Object>) deserialized.get(12);
        current_days = (LocalDate[]) deserialized.get(13);

        w4_checkbox = (boolean[]) deserialized.get(14);
        head_of_household = (boolean[]) deserialized.get(15);
        suta_rate = (float) deserialized.get(16);
        futa_rate = (float)deserialized.get(17);
        suta_threshold = (int)deserialized.get(18);
        futa_threshold = (int)deserialized.get(19);
        social_security_threshold = (int)deserialized.get(20);
        social_security_rate = (float)deserialized.get(21);
        medicare_rate = (float)deserialized.get(22);
        payroll_frequency = (String) deserialized.get(23);
        pay_summary = (ArrayList<Object>) deserialized.get(24);
    }

}
