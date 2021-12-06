import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Main {
    private static boolean entered = false;
    private static int i = 0;
    private static Copy_of_Access_Control user = null;
    private static Save save = null;
    public static void main(String[] args) throws NoSuchAlgorithmException, InterruptedException, IOException {
        System.out.print("Enter \"New\" to create a new company or \"Saved\" to retrieve a previous file.: ");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        input = input.toUpperCase();
        if (!input.equals("NEW") && !input.equals("SAVED")){
            while (!input.equals("NEW") && !input.equals("SAVED")){
                System.out.print("Invalid input! You must enter either \"New\" or \"Saved\": ");
                input = scanner.nextLine();
                input = input.toUpperCase();
            }
        }
        if (Objects.equals(input, "NEW")) {
            get_object();
            entered = true;
            user.Options();
        }
        else if (Objects.equals(input, "SAVED")){
            save = new Save();
            user = save.get_access_object();
            if (user != null)
                Login_Page();
            else{
                System.out.println("Since there are no files, your only option is \"New\"");
                get_object();
                entered = true;
                user.Options();
            }
        }
    }
    public static void Login_Page() throws NoSuchAlgorithmException, InterruptedException, IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to X Team Payroll Software. Please enter your credentials.");
        String username, password;
        System.out.print("Username: ");
        username = scanner.nextLine();
        username = username.trim();
        username = username.toUpperCase();
        System.out.print("Password: ");
        password = scanner.nextLine();
        get_object().Login(username, password);
    }
    private static Copy_of_Access_Control get_object() throws InterruptedException, NoSuchAlgorithmException {
        if (!entered && user == null) {
            if (i < 1) {
                user = new Copy_of_Access_Control();
                i++;
            }
        }
        return user;
    }
    public static Copy_of_Access_Control get_access_control_object(){
        return user;
    }
    public static Save get_save_object(){
        return save;
    }
}
