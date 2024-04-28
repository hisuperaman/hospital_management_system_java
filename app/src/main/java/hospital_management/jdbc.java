/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hospital_management;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;

/**
 *
 * @author Aman
 */
public class jdbc {
    public static Connection conn;
    public static ResultSet rs;
    public static Statement st;
    public static PreparedStatement add_patient_st, get_patient_st, add_medicine_st, get_medicine_st,
            add_bill_st, add_bill_medicines_st, update_med_quantity_st,
            update_doctor_earning_st, get_medicine_price_st, get_bills_by_patient_id_st,
            get_bills_by_id_st, get_medicines_by_billID_st,
            delete_patient_st, delete_medicine_st, edit_patient_st, edit_medicine_st,
            is_admin_st, change_password_st,
            delete_bill_st, delete_bill_medicines_st;
    
    public static void createConnection() throws Exception{
        String url = "jdbc:mysql://localhost:3306/hospital";
        String username = "root";
        String password = "aman433";
        
        Class.forName("com.mysql.cj.jdbc.Driver");
        
        conn = DriverManager.getConnection(url, username, password);
        
        st = conn.createStatement();
        add_patient_st = conn.prepareStatement("insert into hospital.patient_details(name, age, gender, address, mobile_no) values(?,?,?,?,?)");
        get_patient_st = conn.prepareStatement("select name, age, gender, address, mobile_no from hospital.patient_details where id=?");
        
        add_medicine_st = conn.prepareStatement("insert into hospital.medicine_details(name, selling_price, buying_price, quantity, description) values(?,?,?,?,?)");
        get_medicine_st = conn.prepareStatement("select name, selling_price, buying_price, quantity, description from hospital.medicine_details where id=?");
        
        add_bill_st = conn.prepareStatement("insert into hospital.bill_details(patient_id, recommendations, fee_price, medicines_price, total_amount, bill_date) values(?, ?, ?, ?, ?, now())");
        add_bill_medicines_st = conn.prepareStatement("insert into hospital.bill_medicines(bill_id, patient_id, medicine_id, medicine_name, quantity, price) values(?, ?, ?, ?, ?, ?)");
        update_med_quantity_st = conn.prepareStatement("update hospital.medicine_details set quantity=quantity-? where id=?");
        update_doctor_earning_st = conn.prepareStatement("update hospital.doctor_details set medicine_earning=medicine_earning+?, fee_earning=fee_earning+?, net_earning=net_earning+? where id=1");
        get_medicine_price_st = conn.prepareStatement("select selling_price, buying_price from hospital.medicine_details where id=?");
        
        get_bills_by_patient_id_st = conn.prepareStatement("select id, patient_id, fee_price, medicines_price, total_amount, bill_date from hospital.bill_details where patient_id=? order by bill_date desc");
        get_bills_by_id_st = conn.prepareStatement("select patient_id, recommendations, fee_price, medicines_price, total_amount, bill_date from hospital.bill_details where id=?");
        get_medicines_by_billID_st = conn.prepareStatement("select medicine_id, medicine_name, quantity, price from hospital.bill_medicines where bill_id=?");
    
        delete_patient_st = conn.prepareStatement("delete from hospital.patient_details where id=?");
        delete_medicine_st = conn.prepareStatement("delete from hospital.medicine_details where id=?");
        edit_patient_st = conn.prepareStatement("update hospital.patient_details set name=?, age=?, gender=?, address=?, mobile_no=? where id=?");
        edit_medicine_st = conn.prepareStatement("update hospital.medicine_details set name=?, selling_price=?, buying_price=?, quantity=?, description=? where id=?");
    
        is_admin_st = conn.prepareStatement("select * from hospital.doctor_details where password=?");
        change_password_st = conn.prepareStatement("update hospital.doctor_details set password=? where id=1");
    
        delete_bill_st = conn.prepareStatement("delete from hospital.bill_details where patient_id=?");
        delete_bill_medicines_st = conn.prepareStatement("delete from hospital.bill_medicines where patient_id=?");
    }
    
    public static int addPatient(String name, String age, String gender, String address, String mobileNo) throws Exception{
        add_patient_st.setString(1, name);
        add_patient_st.setString(2, age);
        add_patient_st.setString(3, gender);
        add_patient_st.setString(4, address);
        add_patient_st.setString(5, mobileNo);
        
        add_patient_st.executeUpdate();
        
        rs = st.executeQuery("select max(id) as id from hospital.patient_details");
        
        if(rs.next()){
            int id = rs.getInt("id");
            return id;
        }
        return 0;
    }
    
    public static void deletePatient(String id) throws Exception{
        delete_patient_st.setInt(1, Integer.parseInt(id));
        
        delete_bill_st.setInt(1, Integer.parseInt(id));
        delete_bill_st.executeUpdate();
        
        delete_bill_medicines_st.setInt(1, Integer.parseInt(id));
        delete_bill_medicines_st.executeUpdate();
        
        delete_patient_st.executeUpdate();
    }
    
    public static void editPatient(String id, String name, String age, String gender, String address, String mobileNo) throws Exception{
        edit_patient_st.setString(1, name);
        edit_patient_st.setString(2, age);
        edit_patient_st.setString(3, gender);
        edit_patient_st.setString(4, address);
        edit_patient_st.setString(5, mobileNo);
        edit_patient_st.setInt(6, Integer.parseInt(id));
        
        edit_patient_st.executeUpdate();
    }
    
    public static String[] getPatient(int id) throws Exception{
        get_patient_st.setInt(1, id);
        
        rs = get_patient_st.executeQuery();
        
        String[] row = new String[5];
        if(rs.next()){
            row[0] = rs.getString("name");
            row[1] = rs.getString("age");
            row[2] = rs.getString("gender");
            row[3] = rs.getString("address");
            row[4] = rs.getString("mobile_no");
        }
        
        return row;
    }
    
    public static String[][] getAllPatients() throws Exception{
        rs = st.executeQuery("select id, name, age, gender, address, mobile_no from hospital.patient_details");
        
        String[][] rows = {{}};
        
        int i = 0;
        while(rs.next()){
            String[] row = new String[6];
            
            row[0] = rs.getString("id");
            row[1] = rs.getString("name");
            row[2] = rs.getString("age");
            row[3] = rs.getString("gender");
            row[4] = rs.getString("address");
            row[5] = rs.getString("mobile_no");
            
            rows = Arrays.copyOf(rows, i+1);
            
            rows[i] = row;
            
            i++;
        }
        
        return rows;
    }
    
    public static int addMedicine(String name, String selling_price, String buying_price, String quantity, String description) throws Exception{
        add_medicine_st.setString(1, name);
        add_medicine_st.setInt(2, Integer.parseInt(selling_price));
        add_medicine_st.setInt(3, Integer.parseInt(buying_price));
        add_medicine_st.setInt(4, Integer.parseInt(quantity));
        add_medicine_st.setString(5, description);
        
        add_medicine_st.executeUpdate();
        
        rs = st.executeQuery("select max(id) as id from hospital.medicine_details");
        
        if(rs.next()){
            int id = rs.getInt("id");
            return id;
        }
        return 0;
    }
    
    public static void deleteMedicine(String id) throws Exception{
        delete_medicine_st.setInt(1, Integer.parseInt(id));
        
        delete_medicine_st.executeUpdate();
    }
    
    public static void editMedicine(String id, String name, String selling_price, String buying_price, String quantity, String description) throws Exception{
        edit_medicine_st.setString(1, name);
        edit_medicine_st.setInt(2, Integer.parseInt(selling_price));
        edit_medicine_st.setInt(3, Integer.parseInt(buying_price));
        edit_medicine_st.setInt(4, Integer.parseInt(quantity));
        edit_medicine_st.setString(5, description);
        edit_medicine_st.setInt(6, Integer.parseInt(id));
        
        edit_medicine_st.executeUpdate();
    }
    
    public static String[] getMedicine(int id) throws Exception{
        get_medicine_st.setInt(1, id);
        
        rs = get_medicine_st.executeQuery();
        
        String[] row = new String[5];
        if(rs.next()){
            row[0] = rs.getString("name");
            row[1] = ""+rs.getInt("selling_price");
            row[2] = ""+rs.getInt("buying_price");
            row[3] = ""+rs.getInt("quantity");
            row[4] = ""+rs.getString("description");
        }
        
        return row;
    }
    
    public static String[][] getAllMedicines() throws Exception{
        rs = st.executeQuery("select id, name, selling_price, buying_price, quantity, description from hospital.medicine_details");
        
        String[][] rows = {{}};
        
        int i = 0;
        while(rs.next()){
            String[] row = new String[6];
            
            row[0] = rs.getString("id");
            row[1] = rs.getString("name");
            row[2] = rs.getString("selling_price");
            row[3] = rs.getString("buying_price");
            row[4] = rs.getString("quantity");
            row[5] = rs.getString("description");
            
            rows = Arrays.copyOf(rows, i+1);
            
            rows[i] = row;
            
            i++;
        }
        
        return rows;
    }
    
    public static int addBill(String patient_id, String recommendations, String fee_price, String medicines_price, String[] medicines_list) throws Exception{
        add_bill_st.setInt(1, Integer.parseInt(patient_id));
        add_bill_st.setString(2, recommendations);
        add_bill_st.setInt(3, Integer.parseInt(fee_price));
        add_bill_st.setInt(4, Integer.parseInt(medicines_price));
        add_bill_st.setInt(5, Integer.parseInt(fee_price)+Integer.parseInt(medicines_price));
        
        add_bill_st.executeUpdate();
        
        int bill_id=0;
        rs = st.executeQuery("select max(id) as id from hospital.bill_details");
        if(rs.next()){
            bill_id = rs.getInt("id");
        }
         
        
        int med_profit = 0;
        for(String item: medicines_list){
            String[] item_tokens = item.split(",");
            String med = item_tokens[0];
            String med_quantity = item_tokens[1];
            
            String[] med_tokens = med.split(";");
            String med_id = med_tokens[0];
            String med_name = med_tokens[1];
            String med_price = med_tokens[2];
            
            add_bill_medicines_st.setInt(1, bill_id);
            add_bill_medicines_st.setInt(2, Integer.parseInt(patient_id));
            add_bill_medicines_st.setInt(3, Integer.parseInt(med_id));
            add_bill_medicines_st.setString(4, med_name);
            add_bill_medicines_st.setInt(5, Integer.parseInt(med_quantity));
            add_bill_medicines_st.setInt(6, Integer.parseInt(med_price));
            
            add_bill_medicines_st.executeUpdate();
            
            update_med_quantity_st.setInt(1, Integer.parseInt(med_quantity));
            update_med_quantity_st.setInt(2, Integer.parseInt(med_id));
            
            update_med_quantity_st.executeUpdate();
            
            
            get_medicine_price_st.setInt(1, Integer.parseInt(med_id));
            rs = get_medicine_price_st.executeQuery();
            if(rs.next()){
                med_profit += (Integer.parseInt(med_quantity)*(rs.getInt("selling_price")-rs.getInt("buying_price")));
            }
        }
        
//        System.out.println(med_profit+"->"+medicines_price);
        
        update_doctor_earning_st.setInt(1, med_profit);
        update_doctor_earning_st.setInt(2, Integer.parseInt(fee_price));
        update_doctor_earning_st.setInt(3, Integer.parseInt(medicines_price)+Integer.parseInt(fee_price));
        
        update_doctor_earning_st.executeUpdate();
        
        return bill_id;
    }
    
    public static String[] getDoctorEarnings() throws Exception{
        rs = st.executeQuery("select medicine_earning, fee_earning, net_earning from hospital.doctor_details where id=1");
        
        String row[] = new String[3];
        if(rs.next()){
            row[0] = ""+rs.getInt("fee_earning");
            row[1] = ""+rs.getInt("medicine_earning");
            row[2] = ""+rs.getInt("net_earning");
        }
        
        return row;
    }
    
    public static String[][] getBills() throws Exception{
        rs = st.executeQuery("select id, patient_id, fee_price, medicines_price, total_amount, bill_date from hospital.bill_details order by bill_date desc");
        
        String[][] rows = {{}};
        
        int i = 0;
        while(rs.next()){
            String[] row = new String[6];
            
            row[0] = rs.getString("id");
            row[1] = rs.getString("patient_id");
            row[2] = rs.getString("fee_price");
            row[3] = rs.getString("medicines_price");
            row[4] = rs.getString("total_amount");
            row[5] = rs.getString("bill_date");
            
            rows = Arrays.copyOf(rows, i+1);
            
            rows[i] = row;
            
            i++;
        }
        
        return rows;
    }
    
    public static String[][] getBillsByPatientID(String id) throws Exception{
        get_bills_by_patient_id_st.setInt(1, Integer.parseInt(id));
        
        rs = get_bills_by_patient_id_st.executeQuery();
        
        
        String[][] rows = {{}};
        
        int i = 0;
        while(rs.next()){
            String[] row = new String[6];
            
            row[0] = rs.getString("id");
            row[1] = rs.getString("patient_id");
            row[2] = rs.getString("fee_price");
            row[3] = rs.getString("medicines_price");
            row[4] = rs.getString("total_amount");
            row[5] = rs.getString("bill_date");
            
            rows = Arrays.copyOf(rows, i+1);
            
            rows[i] = row;
            
            i++;
        }
        
        return rows;
    }
    
    public static String[] getBillByID(String id) throws Exception{
        get_bills_by_id_st.setInt(1, Integer.parseInt(id));
        
        rs = get_bills_by_id_st.executeQuery();
        
        
        String[] row = new String[6];
        
        if(rs.next()){
            
            row[0] = rs.getString("patient_id");
            row[1] = rs.getString("recommendations");
            row[2] = rs.getString("fee_price");
            row[3] = rs.getString("medicines_price");
            row[4] = rs.getString("total_amount");
            row[5] = rs.getString("bill_date");
            
        }
        
        return row;
    }
    
    public static String[][] getMedicinesByBillID(String id) throws Exception{
        get_medicines_by_billID_st.setInt(1, Integer.parseInt(id));
        
        rs = get_medicines_by_billID_st.executeQuery();
        
        
        String[][] rows = {{}};
        
        int i = 0;
        while(rs.next()){
            String[] row = new String[4];
            
            row[0] = rs.getString("medicine_id");
            row[1] = rs.getString("medicine_name");
            row[2] = rs.getString("quantity");
            row[3] = rs.getString("price");
            
            rows = Arrays.copyOf(rows, i+1);
            
            rows[i] = row;
            
            i++;
        }
        
        return rows;
    }
    
    public static boolean isAdmin(String password) throws Exception{
        is_admin_st.setString(1, password);
        
        rs = is_admin_st.executeQuery();
        if(rs.next()){
            return true;
        }
        return false;
    }
    
    public static void changePassword(String password) throws Exception{
        change_password_st.setString(1, password);
        
        change_password_st.executeUpdate();
    }
            
}