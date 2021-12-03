import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;

public class Copy_of_Access_Control {
    boolean credentials_found = false;
    private static int stored_count = 0;
    private int user_index = 0;
    protected static Copy_of_Admin_Dashboard admin = null;
    protected static Employee_Dashboard empl = null;
    public Copy_of_Access_Control() throws NoSuchAlgorithmException, InterruptedException {}

    public void Login(String username, String password) throws InterruptedException, IOException, NoSuchAlgorithmException {
        Scanner scanner = new Scanner(System.in);
        if (Validate(username, password)){
            user_index= Search(username);
            Options(user_index);
        }
        else{
            int attempts = 0;
            int attempt_threshold = 2;
            while(!Validate(username, password)) {
                Clear.cls();
                System.out.println("The username or password combination is incorrect. Please try again");
                System.out.print("Username: ");
                username = scanner.nextLine();
                username = username.trim();
                username = username.toUpperCase();
                System.out.print("Password: ");
                password = scanner.nextLine();
                attempts++;
                if (attempts > attempt_threshold){
                    System.out.println("You have exceeded the maximum number of allowed attempts. Please contact an administrator to have" +
                            " your password reset.");
                    Thread.sleep(3500);
                    Clear.cls();
                    Main.Login_Page();
                }
            }
            user_index = Search(username);
            Options(user_index);
        }
    }
    public boolean supreme_validation() throws NoSuchAlgorithmException, InterruptedException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Username: ");
        String username = scanner.nextLine();
        username = username.trim();
        username = username.toUpperCase();
        System.out.print("Password: ");
        String password;
        admin.get_supreme_users();
        password = scanner.nextLine();
        for (int x = 0; x < Copy_of_Admin_Dashboard.supreme_count; x++){
            int user_num = Copy_of_Admin_Dashboard.supreme_users[x];
            if (Objects.equals(admin.employees[user_num].get_user(), username)
                && Objects.equals(admin.employees[user_num].get_password(), Hash.toHexString(Hash.getSHA(password)))){
                return true;
            }
        }
        System.out.println("Invalid credentials. You may not proceed. ");
        return false;
    }
    private void Options(int user_index) throws NoSuchAlgorithmException, InterruptedException, IOException {
        switch (admin.employees[user_index].get_access_type()) {
            case "ADMIN":
                admin.Welcome(user_index);
                break;
            case "EMPLOYEE":
                System.out.print("The Employee type works");
                empl = new Employee_Dashboard(user_index);
                empl.Menu();
                //TODO class call break;
        }
    }
    public void Options() throws NoSuchAlgorithmException, InterruptedException, IOException {
        admin = new Copy_of_Admin_Dashboard();
        admin.Add_User(true);
    }
    public void Options(String current_user) throws NoSuchAlgorithmException, IOException, InterruptedException {
        System.out.println("You are being directed to your employee dashboard... ");
        if (empl == null){
            empl = new Employee_Dashboard(user_index);
        }
        empl.Menu();
        //Employee emp = new Employee();
    }
    private Boolean Validate(String username, String password) throws NoSuchAlgorithmException {
        credentials_found = false;
        String hash_password = Hash.toHexString(Hash.getSHA(password));
        for (int i = 0; i < stored_count; i++){
            if ((Objects.equals(admin.employees[i].get_user(), username)) && (Objects.equals(admin.employees[i].get_password(), hash_password))){
                credentials_found = true;
                this.user_index = i;
            }
        }
        return credentials_found;
    }
    public int Search(String username) {
        for (int i = 0; i < stored_count; i++){
            if ((Objects.equals(admin.employees[i].get_user(), username))){
                this.user_index = i;
                return user_index;
            }
        }
        return -1;
    }
    public int get_user_count(){
        return stored_count;
    }
    public String create_new_user_password(String username, String access_type) throws NoSuchAlgorithmException {
        String password_var;
        Random rand = new Random();
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; //letter 'z'
        int targetStringLength = 10;
        StringBuilder build = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++){
            int randomLimitedInt = leftLimit + (int) (rand.nextFloat() * (rightLimit - leftLimit + 1));
            build.append((char) randomLimitedInt);
        }
        password_var = build.toString();
        String hashed_password = Hash.toHexString(Hash.getSHA(password_var));
        admin.employees[stored_count].set_password(hashed_password);
        stored_count++;
        return password_var;
    }

    public int get_user_index(){return user_index;}
    public void set_stored_count(int stored_count){
        Copy_of_Access_Control.stored_count = stored_count;}
    public void set_object(Copy_of_Admin_Dashboard admin) throws NoSuchAlgorithmException, InterruptedException {
        Copy_of_Admin_Dashboard.admin = admin;}
    public String get_username(int user_index){
        return admin.employees[user_index].get_user();
    }

}

