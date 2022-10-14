/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uuu.tf.entity;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 *
 * @author Rapunzel_PC
 */
public class Member {
    public static final char MALE = 'M';//代表gender的MALE
    public static final char FEMALE = 'F';//代表gender的FEMALE
    
    private int uid; //ROC ID, PKey, 必要屬性(Attribute Field)
    private String name;//必要屬性 
    private String email;//必要屬性 
    private String password; //必要屬性 
    private char gender; //必要屬性 男=M,女=F
    private LocalDate birthday; //必要屬性 

    public Member() {
    }
    
    public int getUid() {
        return uid;
    }

    public void setUid(int uid) throws TFException{
        if(uid>0)
            this.uid = uid;
        else{
            throw new TFException("uid不得為0或負值");
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws TFException{
        if(name !=null && name.length()>0)
            this.name = name;
        else{
            throw new TFException("請輸入名字");
        }
    }

    public String getEmail() {
        return email;
    }

    private static final String emailRegExp = "^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z]+$";
    public void setEmail(String email) throws TFException{
        if(email != null && email.matches(emailRegExp))
            this.email = email;
        else{
            throw new TFException("email格式錯誤");
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) throws TFException{
        if(password != null && password.length() >= 6 && password.length() <= 20)
            this.password = password;
        else{            
            throw new TFException("密碼長度需設定6~20");
        }
    }

    public char getGender() {
        return gender;
    }

    public void setGender(char gender) throws TFException{
        if(gender == MALE || gender == FEMALE)
    		this.gender = gender;
        else{
            throw new TFException("請設定性別");
        }
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    //多個名稱相同的方法為多載方法(overloading method)
    public void setBirthday(LocalDate birthday) throws TFException{		
	if(birthday != null && birthday.isBefore(LocalDate.now()))//檢查生日是否小於今天
            this.birthday = birthday;
        else{ 
            throw new TFException("客戶還未出生");
        }
		
    }
	
    public void setBirthday(int year, int month, int day) throws TFException{
    	try{
            setBirthday(LocalDate.of(year, month, day));    	
        }catch(DateTimeException ex){
            throw new TFException("客戶生日日期格式不正確");//拋出錯誤告知前端
        }
    }
    
    public void setBirthday(String date) throws TFException{ //throws VGBException 宣告程式此方法會引發例外狀況
        if(date != null) {
            date = date.replace('/', '-');
        }
        try{
            setBirthday((date!=null)?LocalDate.parse(date):null);
        }catch(DateTimeParseException ex){
            throw new TFException("客戶生日日期格式不正確");//拋出錯誤告知前端
        }
    }
    public int getAge(){
        //計算年齡
    	if(getBirthday() == null)
    		return 0;
    	return getBirthday().until(LocalDate.now()).getYears();
        //  return Period.between(birthday, LocalDate.now()).getYears(); 
    }
    @Override
    public String toString() {
        return "客戶資料 " + "id=" + uid + ", 姓名=" + name + ", 密碼=" + password + ", email=" + email 
                + ", 性別=" + gender + ", 生日=" + birthday + ", 年齡=" + getAge();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + this.uid;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Member other = (Member) obj;
        if (this.uid != other.uid) {
            return false;
        }
        return true;
    }
    
    
}
