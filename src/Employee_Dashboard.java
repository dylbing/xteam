import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Calendar;
public class Employee_Dashboard extends Copy_of_Admin_Dashboard{
    private static int user_index;
    private final int legal_meal_period = 30;
    Employee_Dashboard(int user_index) throws NoSuchAlgorithmException, InterruptedException, IOException {
        super();
        super.Welcome(user_index);
        this.user_index = user_index;
    }
    public void Menu() throws NoSuchAlgorithmException, IOException, InterruptedException {
        int selection;
        int lower_bound = 1;
        int higher_bound = 2;
        System.out.println("What would you like to do?");
        System.out.println("[1] View My Pay History");
        System.out.println("[2] Logout");
        if (!employees[user_index].get_salary_or_hourly()){
            System.out.println("[3] Clocking Functions");
            higher_bound = 3;
        }
        if (Objects.equals(employees[user_index].get_access_type(), "ADMIN")){
            System.out.println("[4] Return to Admin Dashboard");
            higher_bound = 4;
        }
        selection = get_valid_int_response(lower_bound, higher_bound);
        switch (selection){
            case 1:
                //employees[user_index].get_withholding_object(). TODO finish so that can access pay history
                break;
            case 2:
                System.out.println("You have successfully logged out.");
                Clear.cls();
                Main.Login_Page();
                break;
            case 3:
                if (!employees[user_index].get_salary_or_hourly())
                    time_clock();
                else{
                    System.out.println("Invalid Entry");
                    Menu();
                }
                break;
            case 4:
                super.Menu();
        }
    }
    private void time_clock() throws NoSuchAlgorithmException, IOException, InterruptedException {
        System.out.println("The current time is: " + LocalTime.now());
        System.out.println("[1] Clock In");
        System.out.println("[2] Going to Meal");
        System.out.println("[3] Back from Meal");
        System.out.println("[4] Clock Out");
        System.out.println("[5] Cancel/Menu");
        System.out.print("Your selection: ");
        int selection;
        int lower_bound = 1;
        int higher_bound = 5;
        selection = get_valid_int_response(lower_bound, higher_bound);
        switch (selection){
            case 1:
                if (employees[user_index].get_clock_in_time() != null &&
                    employees[user_index].get_clock_out_time() == null){
                    System.out.println("You have already clocked in.");
                    time_clock();
                }
                else{
                    employees[user_index].set_clock_in_time(LocalTime.now());
                    System.out.println("You have clocked in at " + LocalTime.now());
                }
                Thread.sleep(1500);
                time_clock();
                break;
            case 2:
                if (employees[user_index].get_clock_in_time() != null && employees[user_index].get_clock_out_time() == null){
                    employees[user_index].set_meal_start_time(LocalTime.now());
                    System.out.println("You have started your meal break at " + LocalTime.now());
                }
                else
                    System.out.println("You have either not clocked in yet or have clocked out already.");
                Thread.sleep(1500);
                time_clock();
                break;
            case 3:
                if (employees[user_index].get_meal_start_time() != null){
                    LocalTime start = (LocalTime)employees[user_index].get_meal_start_time();
                    long time_between = employees[user_index].time_between(start, LocalTime.now());
                    if (time_between < legal_meal_period){
                        System.out.println("You cannot clock in yet because you have not been on" +
                                " your meal period for long enough or have clocked out.");
                    }
                    else{
                        employees[user_index].set_meal_end_time(LocalTime.now());
                        System.out.println("You have ended your meal period.");
                    }
                }
                else
                    System.out.println("You have not started your meal break yet.");
                Thread.sleep(1500);
                time_clock();
                break;
            case 4:
                if (employees[user_index].get_clock_out_time() != null || employees[user_index].get_clock_in_time() == null){
                    System.out.println("You have either already clocked out " +
                            "or have not clocked in");
                }
                else {
                    System.out.println("Are you sure you would like to clock out? (Enter " +
                            " \"Y\" or \"N\".)");
                    String decision = get_valid_letter_response("Y", "N");
                    if (Objects.equals("Y", decision)){
                        if (employees[user_index].get_meal_start_time() != null &&
                                employees[user_index].get_meal_end_time() == null){
                            System.out.println("You have not clocked back in from your meal period." +
                                    " Enter the time you returned from your meal period: ");
                            LocalTime start = (LocalTime) employees[user_index].get_meal_start_time();
                            int hour_limit = start.getHour();
                            int current_hour = LocalTime.now().getHour();
                            System.out.print("Enter Hour (" + hour_limit +  "-"+ current_hour + "): ");
                            int hour = get_valid_int_response(hour_limit, hour_limit);
                            int minute_limit = 0;
                            int current_minute = 0;
                            int minute;
                            if (hour == hour_limit){
                                minute_limit = start.getMinute();
                                current_minute = LocalTime.now().getMinute();
                                System.out.print("Enter Minute (" + minute_limit + "-" + current_minute + "): ");
                                minute = get_valid_int_response(minute_limit, current_minute);
                            }
                            else{
                                System.out.print("Enter Minute (0-59)");
                                minute = get_valid_int_response(minute_limit, 59);
                            }

                            int second = 0;
                            if ((employees[user_index].time_between((LocalTime)employees[user_index].get_meal_start_time(), LocalTime.of(hour, minute, second)) <= 0)){
                                if (minute < 59){
                                    while (((LocalTime) employees[user_index].get_meal_start_time()).getSecond() > LocalTime.of(hour, minute, second).getSecond())
                                        second++;
                                    second++;
                                }
                                else{
                                    hour++;
                                    minute = 0;
                                }
                            }
                            else
                                while (hour < ((LocalTime) employees[user_index].get_meal_start_time()).getHour() ||
                                    (hour == ((LocalTime)employees[user_index].get_meal_start_time()).getHour() &&
                                            ((LocalTime) employees[user_index].get_meal_start_time()).getMinute() > minute)){
                                    System.out.println("Your meal end time cannot be before your meal start time.");
                                    System.out.println();

                                }
                            employees[user_index].set_meal_end_time(LocalTime.of(hour, minute, second));
                            System.out.println("Your meal period has ended at " +
                                    employees[user_index].get_meal_end_time());
                        }
                        employees[user_index].set_clock_out_time(LocalTime.now());
                        System.out.println("You have successful clocked out at " + LocalTime.now());
                        System.out.println("Summary of Today's hours");
                        employees[user_index].print_punches(LocalDate.now(), null);
                        System.out.println("Total hours worked: " + employees[user_index].calculate_hours_worked(LocalDate.now(), null));
                    }
                    else{
                        System.out.println("Action aborted. You will now return to the clocking functions" +
                                " menu");
                        time_clock();
                    }
                }
                Thread.sleep(1500);
                time_clock();
                break;
            case 5:
                Menu();
        }
    }


}
