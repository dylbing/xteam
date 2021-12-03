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
    private static double[] unpayable_deductions = new double[10];
    private static double[] additional_deductions_pre_tax = new double[10];
    private static String[] pre_tax_deduct_name = new String[10];
    private static String[] post_tax_deduct_name = new String[10];
    private static double[] additional_deductions_post_tax = new double[10];
    private static int add_pre_tax_count = 0;
    private static int add_post_tax_count = 0;
    private static double net_pay = 0;
    private static int spaces_past_normal = 0;
    private static List<Object> history = new ArrayList<Object>();
    private static int payroll_history_count = 0;
    private Object[] temp_storage = new Object[15];
    private static Employee object;

    Withholding(Employee object) throws NoSuchAlgorithmException, InterruptedException {
        super();
        this.object = object;
    }
    public void calculate_withholding(){
        if (earned_post_tax_ytd > social_security_threshold){
            social_security_withholding = gross_pay * social_security_rate;
        }
        medicare_withholding = gross_pay * medicare_rate;
        net_pay = gross_pay - social_security_withholding - medicare_withholding - federal_withholding - state_withholding;
        if (add_post_tax_count > 0){
            for (int i = 0; i < additional_deductions_post_tax.length; i++){
                if ((net_pay - additional_deductions_post_tax[i]) >= 0)
                    net_pay = net_pay - additional_deductions_post_tax[i];
                else {

                }
            }
        }
    }
    public void adjust_gross(double gross_wage){
        //gross_pay =
        for (int i = 0; i < additional_deductions_pre_tax.length; i++){
            gross_pay = gross_pay - additional_deductions_pre_tax[i];
        }
    }
    public void add_pre_tax_deduction(String title, double amount){
        pre_tax_deduct_name[add_pre_tax_count] = title;
        additional_deductions_pre_tax[add_pre_tax_count] = amount;
        add_pre_tax_count++;
    }
    public void add_post_tax_deduction(String title, double amount){
        post_tax_deduct_name[add_post_tax_count] = title;
        additional_deductions_post_tax[add_post_tax_count] = amount;
        add_post_tax_count++;
    }
    public boolean delete_pre_tax_deduct(String title, double amount){
        int index = -1;
        for (int i = 0; i < add_post_tax_count; i++){
            if (additional_deductions_pre_tax[i] == amount &&
                    Objects.equals(pre_tax_deduct_name[i], title)){
                index = i;
            }
        }
        if (index != -1){
            for (int i = index; i < get_user_index(); i++){
                additional_deductions_pre_tax[i] = additional_deductions_pre_tax[i + 1];
            }
            if (add_pre_tax_count == 1){
                add_pre_tax_count--;
            }
            return true;
        }
        else
            return false;
    }
    public boolean delete_post_tax_deduct(String title, double amount){
        int index = -1;
        for (int i = 0; i < add_post_tax_count; i++){
            if (additional_deductions_post_tax[i] == amount &&
                    Objects.equals(post_tax_deduct_name[i], title)){
                index = i;
            }
        }
        if (index != -1){
            for (int i = index; i < index; i++){
                additional_deductions_post_tax[i] = additional_deductions_post_tax[i + 1];
                post_tax_deduct_name[i] = post_tax_deduct_name[i + 1];
            }
            if (add_post_tax_count == 1){
                add_post_tax_count--;
            }
            return true;
        }
        else
            return false;
    }
    public void add_pre_tax_gift(String title, double amount){

    }
    public void add_post_tax_gift(String title, double amount){

    }

    public void delete_gift(String title, double amount){

    }
    public void print_extra_deduction_information(){
        System.out.println("Pre-Tax Deductions");
        System.out.println("Deduction     Amount");
        System.out.println("-------------------");
        for (int i = 0; i < add_pre_tax_count; i++){
            System.out.println("[" + i + "]" + pre_tax_deduct_name[i] + "  " +
                    additional_deductions_pre_tax[i]);
        }
        System.out.println("Post-Tax Deductions");
        System.out.println("Deduction     Amount");
        System.out.println("-------------------");
        for (int i = 0; i < add_post_tax_count; i++){
            System.out.println("[" + i + "]" + post_tax_deduct_name[i] + " " +
                    additional_deductions_post_tax[i]);
        }
    }
    public void store_to_history(){
        Calendar cal = Calendar.getInstance();
        temp_storage[0] = cal.getTime();
        temp_storage[1] = gross_pay;
        temp_storage[2] = state_withholding;
        temp_storage[3] = federal_withholding;
        temp_storage[4] = social_security_withholding;
        temp_storage[5] = medicare_withholding;
        temp_storage[6] = withholding_amount;
        if (add_pre_tax_count == 0 && add_post_tax_count == 0){
            temp_storage[7] = net_pay;
        }
        else
            if (add_pre_tax_count > 0 && add_post_tax_count > 0){
                for (int i = 0; i < add_pre_tax_count; i++){
                    temp_storage[i + 7] = additional_deductions_pre_tax[i];
                    spaces_past_normal++;
                }
                for (int x = 0; x < add_post_tax_count; x++){
                    temp_storage[x + 7 + spaces_past_normal] = additional_deductions_post_tax[x]; // TODO check this, it may be one off
                    spaces_past_normal++;
                }
                temp_storage[spaces_past_normal + 7] = net_pay;
            }
            else if (add_pre_tax_count > 0 && add_post_tax_count <= 0){
                for (int i = 0; i < add_pre_tax_count; i++){
                    temp_storage[i + 7] = additional_deductions_pre_tax[i];
                    spaces_past_normal++;
                }
                temp_storage[spaces_past_normal + 7] = net_pay;
            }
            else{
                for (int x = 0; x < add_post_tax_count; x++){
                    temp_storage[x + 7 + spaces_past_normal] = additional_deductions_post_tax[x]; // TODO check this, it may be one off
                    spaces_past_normal++;
                }
                temp_storage[spaces_past_normal + 7] = net_pay;
            }
            temp_storage[temp_storage.length + 1] = spaces_past_normal;
            history.add(temp_storage);
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


