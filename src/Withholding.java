import org.apache.poi.xssf.usermodel.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Calendar;

public class Withholding extends Payroll_Calculations{
    private static final int max_data = 50;
    private static double withholding_amount;
    private static double gross_pay;
    private static int col_depth;
    private static double[][] array;
    private static double state_withholding;
    private static double federal_withholding;
    private static double social_security_withholding;
    private static double medicare_withholding;
    private static double earned_pre_tax_ytd = 0;
    private static double earned_post_tax_ytd = 0;
    //private static double[] unpayable_deductions = new double[10];
    private static ArrayList<Object> pay_summary = new ArrayList<Object>();
    private static int add_pre_tax_count = 0;
    private static int add_post_tax_count = 0;
    private static double net_pay = 0;
    private static int spaces_past_normal = 0;
    private static List<Object> history = new ArrayList<Object>();
    private static int payroll_history_count = 0;
    private Object[] temp_storage = new Object[15];
    private static Employee object;
    private float overtime_multiplier = 1.5f;
    private int salary_work_week = 40;

    Withholding(Employee object) throws NoSuchAlgorithmException, InterruptedException {
        super();
        this.object = object;
    }
    public void print_paystub(){
        System.out.println("Pay Period ended: "); // TODO add date in here when figured out

        for (int i = 0; i < pay_summary.size(); i++){

        }
    }
    public void calculate_withholding(){
        adjust_gross();
        if ((earned_post_tax_ytd) > social_security_threshold){
            social_security_withholding = gross_pay * social_security_rate;
        }
        else if ((earned_post_tax_ytd + gross_pay) > social_security_withholding){
            social_security_withholding = social_security_rate * (social_security_threshold - earned_pre_tax_ytd);
        }
        else
            social_security_withholding = social_security_rate * gross_pay;
        medicare_withholding = gross_pay * medicare_rate;
        net_pay = gross_pay - social_security_withholding - medicare_withholding - federal_withholding - state_withholding;
    }
    public void adjust_gross(){
        float overtime_hours = 0;
        float pre_tax_deductions = 0;
        float pre_tax_gifts = 0;
        if (!object.get_salary_or_hourly()){
            gross_pay = object.calculate_hours_worked(null, null);
            if (gross_pay > 40){
                overtime_hours = (float)(gross_pay - 40);
                gross_pay = gross_pay - overtime_hours;
            }
            gross_pay = (gross_pay * object.get_salary()) + (overtime_hours * object.get_salary()
                    * overtime_multiplier); //TODO make the
        }
        else{
            gross_pay = salary_work_week * object.get_salary();
        }
        for (int i = 0; i < pay_summary.size(); i++){
            if (pay_summary.get(i) == "PRE TAX GIFT")
                pre_tax_gifts = (float) (pay_summary.get(i + 2));
            else if (pay_summary.get(i) == "PRE TAX DEDUCTION")
                pre_tax_deductions = (float) (gross_pay - (gross_pay * ((float)pay_summary.get(i + 2))));
        }
        gross_pay += pre_tax_gifts;
        gross_pay -= pre_tax_deductions;
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
        pay_summary.remove(index + 1);
        pay_summary.remove(index + 2);
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
        pay_summary.remove(index + 1);
        pay_summary.remove(index + 2);
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
            case "FEDERAL":
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
                        System.out.println(cell.getNumericCellValue());
                        witholding[r][c] = cell.getNumericCellValue();
                        break;
                    default:
                        System.out.print("Data not aggregated successfully");
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
            if (gross_pay >= array[i][1] && gross_pay < array[i][2]){
                if (Objects.equals(withholding_type, "State")){
                    state_withholding = array[i][object.get_tax_exemptions() + 2];
                }
                else{
                    federal_withholding = array[i][object.get_tax_exemptions() + 2];
                }
            }
        }
    }

}


