import org.apache.poi.xssf.usermodel.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Objects;

public class Withholding extends Payroll_Calculations{
    private static final int max_data = 50;
    //private static double withholding_amount;
    private static double gross_pay;
    private static double original_gross_pay;
    private static int col_depth;
    private static double[][] array;
    private static double state_withholding;
    private static double federal_withholding;
    private static double social_security_withholding;
    private static double medicare_withholding;
    private double earned_pre_tax_ytd = 0;
    private double earned_post_tax_ytd = 0;
    //private static double[] unpayable_deductions = new double[10];
    private ArrayList<Object> pay_summary = new ArrayList<Object>();
    private static double net_pay = 0;
    //private static List<Object> history = new ArrayList<Object>();
    private static Employee object;
    private float overtime_multiplier = 1.5f;
    private int salary_work_week = 40;

    Withholding(Employee object) throws NoSuchAlgorithmException, InterruptedException {
        super();
        this.object = object;
    }
    public ArrayList<Object> get_pay_summary(){
        return pay_summary;
    }
    public void set_pay_summary(ArrayList<Object> pay_summary){
        this.pay_summary = pay_summary;
    }
    public void set_employee_object(Employee object){
        this.object = object;
    }
    public void set_employee_object(int x){
        pay_summary = new ArrayList<Object>();
        object = employees[x];
    }
    public void print_paystub(){
        System.out.println("Pay Period ended: "); // TODO add date in here when figured out

        for (int i = 0; i < pay_summary.size(); i++){
            if (pay_summary.get(i) == "GROSS PAY"){
                System.out.println("GROSS PAY: " +  pay_summary.get(i + 1));
                i++;
            }
            else if (pay_summary.get(i) == "REGULAR HOURS" && !object.get_salary_or_hourly()){
                System.out.println("REGULAR HOURS: " + pay_summary.get(i + 1));
                i++;
            }
            else if (pay_summary.get(i) == "OVERTIME HOURS" && !object.get_salary_or_hourly()){
                System.out.println("OVERTIME HOURS: " + pay_summary.get(i + 1));
                i++;
            }
            else if (pay_summary.get(i) == "SOCIAL SECURITY"){
                System.out.println("SOCIAL SECURITY: " + pay_summary.get(i + 1));
                i++;
            }
            else if (pay_summary.get(i) == "MEDICARE"){
                System.out.println("MEDICARE: " + pay_summary.get(i + 1));
                i++;
            }
            else if (pay_summary.get(i) == "STATE"){
                System.out.println("STATE: " + pay_summary.get(i + 1));
                i++;
            }
            else if (pay_summary.get(i) == "FEDERAL"){
                System.out.println("FEDERAL: " + pay_summary.get(i + 1));
                i++;
            }
            else if (pay_summary.get(i) == "NET PAY"){
                System.out.println("NET PAY: " + pay_summary.get(i + 1));
                i++;
            }
            else if (pay_summary.get(i) == "PRE TAX EARNED YTD"){
                System.out.println("PRE TAX EARNED YTD: " + pay_summary.get(i + 1));
                i++;
            }
            else if (pay_summary.get(i) == "POST TAX EARNED YTD"){
                System.out.println("POST TAX EARNED YTD: " + pay_summary.get(i + 1));
                i++;
            }
            else if (pay_summary.get(i) == "POST TAX GIFT"){
                System.out.println(pay_summary.get(i = 1) + " " + pay_summary.get(i + 2));
                i += 2;
            }
            else if (pay_summary.get(i) == "PRE TAX GIFT"){
                System.out.println(pay_summary.get(i + 1) + " " + pay_summary.get(i + 2));
                i += 2;
            }
            else if (pay_summary.get(i) == "PRE TAX DEDUCTION"){
                System.out.println(pay_summary.get(i + 1) + " " + pay_summary.get(i + 2));
                i += 2;
            }
        }
        System.out.println();
        System.out.println();
        System.out.print("Enter \"Y\" when you would like to exit: ");
        String response = get_valid_letter_response("Y", "Y");
        if (Objects.equals(response, "Y")){
            System.out.println();
        }
    }
    public void calculate_withholding(){
        if ((earned_post_tax_ytd) > social_security_threshold){
            social_security_withholding = net_pay * social_security_rate;
        }
        else if ((earned_post_tax_ytd + gross_pay) > social_security_threshold){
            social_security_withholding = social_security_rate * (social_security_threshold - earned_pre_tax_ytd);
        }
        else
            social_security_withholding = social_security_rate * gross_pay;
        medicare_withholding = net_pay * medicare_rate;
        net_pay = net_pay - social_security_withholding - medicare_withholding - federal_withholding - state_withholding;
    }
    public double round_value(double number){
        BigDecimal bd = new BigDecimal(number);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    public void add_to_list(){
        pay_summary.add("GROSS PAY");
        pay_summary.add(gross_pay);
        pay_summary.add("REGULAR HOURS");
        float hours = object.calculate_hours_worked(null, null);
        float overtime;
        if (hours > 40)
            overtime = hours - 40;
        else
            overtime = 0;
        pay_summary.add(hours - (overtime));
        pay_summary.add("OVERTIME HOURS");
        overtime = (float)round_value(overtime);
        pay_summary.add(overtime);
        pay_summary.add("SOCIAL SECURITY");
        //social_security_withholding = (float)round_value(social_security_withholding);
        pay_summary.add(social_security_withholding);
        //medicare_withholding = (float)round_value(medicare_withholding);
        pay_summary.add("MEDICARE");
        pay_summary.add(medicare_withholding);
        pay_summary.add("STATE");
        //state_withholding = (float)round_value(state_withholding);
        pay_summary.add(state_withholding);
        pay_summary.add("FEDERAL");
        //federal_withholding = (float)round_value(federal_withholding);
        pay_summary.add(federal_withholding);
        //net_pay = (float)round_value(net_pay);
        pay_summary.add("NET PAY");
        pay_summary.add(net_pay);
        pay_summary.add("PRE TAX EARNED YTD");
        earned_pre_tax_ytd += gross_pay;
        //earned_pre_tax_ytd = (float)round_value(earned_pre_tax_ytd);
        pay_summary.add(earned_pre_tax_ytd);
        pay_summary.add("POST TAX EARNED YTD");
        earned_post_tax_ytd += net_pay;
        //earned_post_tax_ytd = (float)round_value(earned_post_tax_ytd);
        pay_summary.add(earned_post_tax_ytd);
    }
    public void adjust_gross(){
        int tax_divisor = 0;
        double overtime_hours = 0;
        double pre_tax_deductions = 0;
        double pre_tax_gifts = 0;
        if (!object.get_salary_or_hourly()){
            gross_pay = object.calculate_hours_worked(null, null);
            if (gross_pay > 40){
                overtime_hours = (float)(gross_pay - 40);
                net_pay = gross_pay - overtime_hours;
            }
            gross_pay = (gross_pay * object.get_salary()) + (overtime_hours * object.get_salary()
                    * overtime_multiplier);
            net_pay = gross_pay;
        }
        else{
            switch (payroll_frequency){
                case "Daily":
                    tax_divisor = 260;
                    break;
                case "Weekly":
                    tax_divisor = 52;
                    break;
                case "Biweekly":
                    tax_divisor = 26;
                    break;
                case "Semimonthly":
                    tax_divisor = 24;
                    break;
                case "Monthly":
                    tax_divisor = 12;
            }
            gross_pay = object.get_salary()/tax_divisor;
            net_pay = gross_pay;
        }
        for (int i = 0; i < pay_summary.size(); i++){
            if (pay_summary.get(i) == "PRE TAX GIFT"){
                pre_tax_gifts = gross_pay * (double)(pay_summary.get(i + 2));
                pay_summary.remove(i + 2);
                pay_summary.add(i+2,pre_tax_deductions);
            }
            else if (pay_summary.get(i) == "PRE TAX DEDUCTION"){
                pre_tax_deductions = (gross_pay * (double)pay_summary.get(i + 2));
                pay_summary.remove(i + 2);
                pay_summary.add(pre_tax_gifts);
            }
        }
        gross_pay = (double)Math.round(gross_pay * 100) /100;
        pre_tax_gifts = (double)Math.round(pre_tax_gifts * 100) / 100;
        pre_tax_deductions = (double)Math.round(pre_tax_deductions * 100) / 100;
        gross_pay += pre_tax_gifts;
        net_pay += pre_tax_gifts;
        net_pay -= pre_tax_deductions;
    }
    public void add_pre_tax_deduction(String title, double amount){
        amount = amount / 100;
        pay_summary.add("PRE TAX DEDUCTION");
        pay_summary.add(title);
        pay_summary.add(amount);
        System.out.println("Successfully added pre tax deduction: " + title + " in the amount of: " + amount * 100 + "%");
    }
    public void add_post_tax_deduction(String title, double amount){
        amount = amount / 100;
        pay_summary.add("POST TAX DEDUCTION");
        pay_summary.add(title);
        pay_summary.add(amount);
        System.out.println("Successfully added post tax deduction: " + title + " in the amount of: " + amount * 100 + "%");
    }
    public void delete_deduction(int index){
        pay_summary.remove(index);
        pay_summary.remove(index);
        pay_summary.remove(index);
        System.out.println("Deduction has successfully been deleted.");
    }
    public void add_pre_tax_gift(String title, double amount){
        pay_summary.add("PRE TAX GIFT");
        pay_summary.add(title);
        pay_summary.add(amount);
        System.out.println("Successfully added pre tax gift: " + title + " in the amount of: " + amount + ".");
    }
    public void add_post_tax_gift(String title, double amount){
        pay_summary.add("POST TAX GIFT");
        pay_summary.add(title);
        pay_summary.add(amount);
        System.out.println("Successfully added post tax gift: " + title + " in the amount of: " + amount + ".");
    }
    public void delete_gift(int index){
        pay_summary.remove(index);
        pay_summary.remove(index);
        pay_summary.remove(index);
        System.out.println("Gift/Bonus has successfully been deleted.");
    }
    public ArrayList<Integer> print_extra_deduction_information(){
        System.out.println("DEDUCTION TYPE   TITLE   AMOUNT");
        ArrayList<Integer> valid_numbers = new ArrayList<Integer>();
        for (int i = 0; i < pay_summary.size(); i++){
            if (pay_summary.get(i) == "PRE TAX DEDUCTION"){
                valid_numbers.add(i);
                i++;
                System.out.println("(" + (i-1) + ")  PRE TAX DEDUCTION: " + pay_summary.get(i) + " " + pay_summary.get(i + 1));
                i += 2;
            }
            else if (pay_summary.get(i) == "POST TAX DEDUCTION"){
                valid_numbers.add(i);
                i++;
                System.out.println("(" + (i-1) + ") POST TAX DEDUCTION " + pay_summary.get(i) + " " + pay_summary.get(i + 1));
            }
        }
        return valid_numbers;
    }
    public ArrayList<Integer> print_gift_information(){
        ArrayList<Integer> valid_numbers = new ArrayList<Integer>();
        System.out.println("GIFT TYPE  TITLE  AMOUNT");
        for (int i = 0; i < pay_summary.size(); i++){
            if (pay_summary.get(i) == "PRE TAX GIFT"){
                valid_numbers.add(i);
                i++;
                System.out.println("(" + (i-1) + ")  PRE TAX GIFT: " + pay_summary.get(i) + " " + pay_summary.get(i + 1));
                i += 2;
            }
            else if (pay_summary.get(i) == "POST TAX GIFT"){
                valid_numbers.add(i);
                i++;
                System.out.println("(" + (i-1) + ") POST TAX GIFT " + pay_summary.get(i) + " " + pay_summary.get(i + 1));
            }
        }
        return valid_numbers;
    }
    public static void get_excel_data(String withholding_type, String civil_status, String frequency) throws IOException {
        String excelFilePath = "";
        char marital_status = civil_status.charAt(0);
        switch(withholding_type) {
            case "State":
                switch (frequency) {
                    case "Daily":
                        if (marital_status == 'M')
                            excelFilePath = ".\\datafile\\Wisconsin Daily Witholding Married .xlsx";
                        else
                            excelFilePath = ".\\datafile\\Wisconsin Daily Witholding Single .xlsx";
                        break;
                    case "Weekly":
                        if (marital_status == 'M')
                            excelFilePath = ".\\datafile\\Wisconsin Weekly Witholding Married.xlsx";
                        else
                            excelFilePath = ".\\datafile\\Wisconsin Weekly Witholding Single.xlsx";
                        break;
                    case "Biweekly":
                        if (marital_status == 'M')
                            excelFilePath = ".\\datafile\\Wisconsin Biweekly Witholding Married.xlsx";
                        else
                            excelFilePath = ".\\datafile\\Wisconsin Biweekly Witholding Single.xlsx";
                        break;
                    case "Semimonthly":
                        if (marital_status == 'M')
                            excelFilePath = ".\\datafile\\Wisconsin Semimonthly Witholding Married.xlsx";
                        else
                            excelFilePath = ".\\datafile\\Wisconsin Semimonthly Witholding Single.xlsx";
                        break;
                    case "Monthly":
                        if (marital_status == 'M')
                            excelFilePath = ".\\datafile\\Wisconsin Monthly Witholding Married.xlsx";
                        else
                            excelFilePath = ".\\datafile\\Wisconsin Monthly Witholdings Single.xlsx";
                        break;
                    default:
                        System.out.print("File not found with entered combination");
                }
                break;
            case "Federal":
                switch (frequency){
                    case "Daily":
                        excelFilePath = ".\\datafile\\Wage Bracket Daily.xlsx";
                        break;
                    case "Weekly":
                        excelFilePath = ".\\datafile\\Wage Bracket Weekly.xlsx";
                        break;
                    case "Biweekly":
                        excelFilePath = ".\\datafile\\Wage Bracket BiWeekly.xlsx";
                        break;
                    case "Semimonthly":
                        excelFilePath = ".\\datafile\\2021 Wage Bracket Method SemiMonthly.xlsx";
                    case "Monthly":
                        excelFilePath = ".\\datafile\\Wage Bracket Monthly.xlsx";
                }
        }
        FileInputStream inputstream = new FileInputStream(excelFilePath);
        XSSFWorkbook workbook = new XSSFWorkbook(inputstream);
        XSSFSheet sheet = workbook.getSheet("Sheet1");
        int rows = sheet.getLastRowNum();
        int cols = sheet.getRow(1).getLastCellNum();
        int size1 = rows + 1;
        int size2 = cols + 1;
        double[][] witholding = new double[size1][size2];
        for (int r = 0; r <= rows; r++){
            XSSFRow row = sheet.getRow(r);
            for (int c = 0; c < cols; c++){
                XSSFCell cell = row.getCell(c);
                switch (cell.getCellType()){
                    case NUMERIC:
                        witholding[r][c] = cell.getNumericCellValue();
                        break;
                    default:
                        //System.out.print("Data not aggregated successfully");
                }
            }
            System.out.println();
        }
        array = witholding;
        col_depth = size1;
        get_withholding_amount(withholding_type);
    }
    public static void get_withholding_amount(String withholding_type) throws IOException {
        for (int i = 0; i < col_depth; i++){
            if ((gross_pay >= array[i][0] && gross_pay < array[i][1]) || (gross_pay >= array[i][0] &&
                array[i][1] == 0 && array[i][0] != 0)){
                if (Objects.equals(withholding_type, "State")){
                    state_withholding = array[i][object.get_tax_exemptions() + 2];
                }
                else{
                    int index = 0;
                    if (object.get_w4check_box() && object.get_head_of_household())
                        index = 5;
                    else if (object.get_head_of_household() && !object.get_w4check_box())
                        index = 4;
                    else if (Objects.equals(object.get_marital_status(), "M") && object.get_w4check_box())
                        index = 3;
                    else if (object.equals(object.get_marital_status()) && !object.get_w4check_box())
                        index = 2;
                    else if (object.equals(object.get_marital_status() == "S" && !object.get_w4check_box()))
                        index = 6;
                    else
                        index = 7;
                    federal_withholding = array[i][index];
                    System.out.println(federal_withholding);
                }
            }
        }
    }

}


