import java.security.NoSuchAlgorithmException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Timer;

public class Employee extends Copy_of_Admin_Dashboard{
    final int milli_sec_day = 86400000;
    private String access_type;
    private boolean paid_lunch = false;
    private String name;
    private String password;
    private int id;
    private int tax_exemptions;
    private String marital_status;
    private String position;
    private double salary;
    private boolean sal_or_hourly;
    private boolean supreme_leader;
    private boolean high_level_manager;
    private static Withholding pay;
    private static LocalDate current_day;
    private static ArrayList<Object> punch_log = new ArrayList<Object>();


    public void restore(String access_type, String name, String password, int id, int tax_exemptions, String position,
                        double salary, boolean sal_or_hourly, boolean supreme_leader, boolean high_level_manager, String marital_status,
                        ArrayList<Object> punch_log, LocalDate current_day){
        this.access_type = access_type;
        this.name = name;
        this.password = password;
        this.id = id;
        this.tax_exemptions = tax_exemptions;
        this.position = position;
        this.salary = salary;
        this.sal_or_hourly = sal_or_hourly;
        this.supreme_leader = supreme_leader;
        this.high_level_manager = high_level_manager;
        this.marital_status = marital_status;
        this.punch_log = punch_log;
        this.pay = pay;
        this.current_day = current_day;
    }
    Employee() throws NoSuchAlgorithmException, InterruptedException {
    }

    public void temp_admin(int days) {
        String temp_access_type = access_type;
        access_type = "Admin";
        days = days * milli_sec_day;
        new Timer().schedule(
                new java.util.TimerTask(){
                    @Override
                    public void run(){
                        access_type = temp_access_type;
                    }
                },
                days
        );
    }
    public void temp_high_level(int days){
        String temp_access_type = access_type;
        boolean temp_high_level = high_level_manager;
        access_type = "Admin";
        high_level_manager = true;
        days = days * milli_sec_day;
        new Timer().schedule(
                new java.util.TimerTask(){
                    @Override
                    public void run(){
                        access_type = temp_access_type;
                        high_level_manager = temp_high_level;
                    }
                },
                days
        );
    }
    public void temp_supreme_leader(int days){
        boolean temp_supreme = supreme_leader;
        String temp_access_type = access_type;
        boolean temp_high_level = high_level_manager;
        supreme_leader = true;
        high_level_manager = true;
        access_type = "Admin";
        new Timer().schedule(
                new java.util.TimerTask(){
                    @Override
                    public void run(){
                        access_type = temp_access_type;
                        high_level_manager = temp_high_level;
                        supreme_leader = temp_supreme;
                    }
                },
                days
        );
    }
    public void temp_demotion_employee(int days){
        boolean temp_supreme = supreme_leader;
        String temp_access_type = access_type;
        boolean temp_high_level = high_level_manager;
        supreme_leader = false;
        high_level_manager = false;
        access_type = "Employee";
        new Timer().schedule(
                new java.util.TimerTask(){
                    @Override
                    public void run(){
                        access_type = temp_access_type;
                        high_level_manager = temp_high_level;
                        supreme_leader = temp_supreme;
                    }
                },
                days
        );
    }
    public void set_supreme_leader(boolean supreme){
        supreme_leader = supreme;
    }
    public void set_high_level_status(boolean high_level_manager){
        this.high_level_manager = high_level_manager;
    }
    public void set_access_level(String access_type){
        this.access_type = access_type;
    }

    public void set_user(String user){
        this.name = user;
    }
    public void set_password(String password){
        this.password = password;
    }
    public void set_id(int id){
        this.id = id;
    }
    public void set_tax_exemptions(int tax_exemptions){
        this.tax_exemptions = tax_exemptions;
    }
    public void set_position(String position){
        this.position = position;
    }
    public void set_salary(double salary){
        this.salary = salary;
    }
    public void set_hourly_or_hourly(boolean hourly_salary){
        sal_or_hourly = hourly_salary;
    }
    public void set_marital_status(String marital_status){
        this.marital_status = marital_status;
    }
    public String get_user(){
        return name;
    }
    public void set_withholding_object() throws NoSuchAlgorithmException, InterruptedException {
        pay = new Withholding(employees[get_user_count()]);
    }
    public void set_clock_in_time(LocalTime time){
        if (current_day == null){
            current_day = LocalDate.now();
            punch_log.add(current_day);
        }
        else if (!LocalDate.now().equals(current_day))
            punch_log.add(LocalDate.now());
        punch_log.add("CLOCK IN");
        punch_log.add(time);
    }
    public void set_meal_start_time(LocalTime time){
        if (!LocalDate.now().equals(current_day))
            punch_log.add(LocalDate.now());
        punch_log.add("MEAL START");
        punch_log.add(time);
    }
    public long time_between(LocalTime from, LocalTime to){
        return ChronoUnit.MINUTES.between(from, to);
    }
    public float calculate_hours_worked(LocalDate start_date, LocalDate end_date){
        LocalDate date = null;
        LocalTime clock_in = null;
        LocalTime clock_out = null;
        LocalTime meal_start = null;
        LocalTime meal_end = null;
        float total_time = 0;
        float hours_worked = 0;
        for (int i = 0; i < punch_log.size(); i++){
            if (punch_log.get(i).getClass() == LocalDate.class){
                date = (LocalDate)punch_log.get(i);
                i++;
            }
            if (Objects.equals(date, start_date) && !Objects.equals(date, end_date)){
                if ("CLOCK IN".equals(punch_log.get(i))){
                    i++;
                    clock_in = (LocalTime)punch_log.get(i);
                    while (!"CLOCK OUT".equals(punch_log.get(i)) && i < punch_log.size())
                        i++;
                    if ("CLOCK OUT".equals(punch_log.get(i))) {
                        i++;
                        clock_out = (LocalTime) punch_log.get(i);
                        total_time += ChronoUnit.MINUTES.between(clock_in, clock_out);
                    }
                }

            }
            if (!paid_lunch){
                for (int d = 0; d < punch_log.size(); d++){
                    if ("MEAL START".equals(punch_log.get(d))){
                        d++;
                        meal_start = (LocalTime)punch_log.get(d);
                        d++;
                        try {
                            if ("MEAL END".equals((LocalTime)punch_log.get(d))){
                                meal_end = (LocalTime)punch_log.get(d);
                                total_time -= ChronoUnit.MINUTES.between(meal_start, meal_end);
                            }
                        }catch (Exception e){}
                    }
                }
            }
        }
        total_time = total_time / 60;
        return total_time;
    }
    public void print_punches(LocalDate start_date, LocalDate end_date){
        LocalDate date = null;
        LocalTime clock_in = null;
        LocalTime clock_out = null;
        LocalTime meal_start = null;
        LocalTime meal_end = null;
        for (int i = 0; i < punch_log.size(); i++){
            if (punch_log.get(i).getClass() == LocalDate.class){
                date = (LocalDate)punch_log.get(i);
                i++;
            }
            if (Objects.equals(date, start_date) && !Objects.equals(date, end_date)){
                if ("CLOCK IN".equals(punch_log.get(i))){
                    i++;
                    System.out.println("CLOCK IN: " + punch_log.get(i));
                    i++;
                    if ("MEAL START".equals(punch_log.get(i))){
                        i++;
                        System.out.println("MEAL START: " + punch_log.get(i));
                        i++;
                    }
                    if (punch_log.get(i).getClass() == LocalDate.class)
                        i++;
                    if ("MEAL END".equals(punch_log.get(i))){
                        i++;
                        System.out.println("MEAL END: " + punch_log.get(i));
                        i++;
                    }
                    while (!"CLOCK OUT".equals(punch_log.get(i)) && i < punch_log.size())
                        i++;
                    if ("CLOCK OUT".equals(punch_log.get(i))) {
                        i++;
                        System.out.println("CLOCK OUT: " + punch_log.get(i));
                    }
                }

            }
        }
    }
    public void set_meal_end_time(LocalTime time){
        if (!LocalDate.now().equals(current_day))
            punch_log.add(LocalDate.now());
        punch_log.add("MEAL END");
        punch_log.add(time);
    }
    public void set_paid_lunch(boolean paid_lunch){
        this.paid_lunch = paid_lunch;
    }

    public void set_clock_out_time(LocalTime time){
        if (!LocalDate.now().equals(current_day))
            punch_log.add(LocalDate.now());
        punch_log.add("CLOCK OUT");
        punch_log.add(time);
    }
    public ArrayList<Object> get_punch_log(){
        return punch_log;
    }
    public Object get_clock_in_time() {
        LocalTime clock_in_nano_seconds;
        LocalTime time = null;
        for (int i = 0; i < punch_log.size(); i++) {
            if ("CLOCK IN".equals(punch_log.get(i))) {
                i++;
                time = (LocalTime)punch_log.get(i);
            }
        }
        return time;
    }
    public Object get_meal_start_time(){
        LocalTime time = null;
        for (int i = 0; i < punch_log.size(); i++){
            if ("MEAL START".equals(punch_log.get(i))){
                i++;
                time = (LocalTime)punch_log.get(i);
            }
        }
        return time;
    }
    public Object get_meal_end_time(){
        LocalTime time = null;
        LocalTime start;
        int start_hour, start_minute, start_second;
        int end_hour, end_minute, end_second;
        for (int i = 0; i < punch_log.size(); i++){
            if ("MEAL END".equals(punch_log.get(i))){
                i++;
                time = (LocalTime)punch_log.get(i);
            }
        }
        start = (LocalTime) get_meal_start_time();
        if (start != null && time != null){
            start_hour = start.getHour();
            start_minute= start.getMinute();
            start_second = start.getSecond();
            end_hour = time.getHour();
            end_minute = time.getMinute();
            end_second = time.getSecond();
            if (end_hour < start_hour){
                return null;
            }
            else if (end_hour == start_hour && end_minute == start_minute &&
                    end_second < start_second){
                return null;
            }
        }
        return time;
    }
    public LocalDate get_current_day(){
        return current_day;
    }
    public Object get_clock_out_time(){
        LocalTime temp_time_store;
        int clock_out_hour, clock_out_minute, clock_out_seconds;
        int clock_in_seconds, clock_in_minutes, clock_in_hours;
        LocalTime time = null;
        for (int i = 0; i < punch_log.size(); i++){
            if ("CLOCK OUT".equals(punch_log.get(i))){
                i++;
                time =  (LocalTime)punch_log.get(i);
            }
        }
        temp_time_store = (LocalTime)get_clock_in_time();
        if (temp_time_store != null && time != null){
            clock_out_hour = time.getHour();
            clock_out_minute = time.getMinute();
            clock_out_seconds = time.getSecond();
            clock_in_hours = temp_time_store.getHour();
            clock_in_minutes = temp_time_store.getMinute();
            clock_in_seconds = temp_time_store.getSecond();
            if (clock_out_hour < clock_in_hours){
                return null;
            }
            else if (clock_out_hour == clock_in_hours && clock_out_minute <= clock_in_minutes &&
                    clock_out_seconds < clock_in_seconds){
                return null;
            }
        }
        return time;
    }
    public String get_access_type(){
        return access_type;
    }
    public Withholding get_withholding_object(){
        return pay;
    }
    public int get_id(){
        return id;
    }
    public int get_tax_exemptions(){
        return tax_exemptions;
    }
    public String get_position(){
        return position;
    }
    public double get_salary(){
        return salary;
    }
    public boolean get_salary_type(){
        return sal_or_hourly;
    }
    public String get_marital_status(){
        return marital_status;
    }
    public boolean get_high_level_status(){
        return high_level_manager;
    }
    public boolean get_supreme_status(){
        return supreme_leader;
    }
    public boolean get_salary_or_hourly(){
        return sal_or_hourly;
    }
    public String get_password(){
        return password;
    }
}
