/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uuu.tf.model;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import uuu.tf.entity.Member;
import uuu.tf.entity.TFException;

/**
 *
 * @author Rapunzel_PC
 */
class MemberDAO {

    private static final String SELECT_BY_Email = "SELECT uid,name,email,password,gender,birthday "
            + "FROM  members  "
            + "WHERE email=?";

    Member selectByEmail(String email) throws TFException {
        Member m = null;
        try (
                Connection connection = RDBConnection.getConnection();
                PreparedStatement ptmt = connection.prepareStatement(SELECT_BY_Email);) {
            ptmt.setString(1, email);
            try (
                    ResultSet rs = ptmt.executeQuery();) {
                while (rs.next()) {
                    m = new Member();
                    m.setUid(rs.getInt("uid"));
                    m.setName(rs.getString("name"));
                    m.setEmail(rs.getString("email"));
                    m.setPassword(rs.getString("password"));
                    m.setGender(rs.getString("gender").charAt(0));
                    m.setBirthday(rs.getString("birthday"));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(MemberDAO.class.getName()).log(Level.SEVERE, "無法查詢客戶", ex);
            throw new TFException("無法查詢客戶", ex);//丟給前端 顯示錯誤
        }
        return m;
    }

    private static final String INSERT_Member = "INSERT INTO members "
            + "(uid,name,email,password,gender,birthday) "
            + "values(0,?,?,?,?,?);";

    void insert(Member m) throws TFException {
        if (m == null) {
            throw new IllegalArgumentException("新增客戶時Customer物件不得為null");
        }
        try (
                Connection connection = RDBConnection.getConnection();//1.2 建立連線
                PreparedStatement pstmt = connection.prepareStatement(INSERT_Member, Statement.RETURN_GENERATED_KEYS);//3. 準備指令pstmt
                ) {
            pstmt.setString(1, m.getName());
            pstmt.setString(2, m.getEmail());
            pstmt.setString(3, m.getPassword());
            pstmt.setString(4, String.valueOf(m.getGender()));
            pstmt.setString(5, m.getBirthday() != null ? String.valueOf(m.getBirthday()) : "");
            //4.執行指令pstmt
            pstmt.executeUpdate();//會回傳筆數
            //5. 處理rs
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                while (rs.next()) {
                    int id = rs.getInt(1);
                    m.setUid(id);
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(MemberDAO.class.getName()).log(Level.SEVERE, "新增客戶失敗", ex);
            throw new TFException("新增客戶失敗" + ex.getMessage(), ex);
        }
    }

    public static final String UPDATE_Member = "UPDATE members "
            + "SET name=?,email=?,password=?,gender=?,birthday=? "
            + "WHERE uid=?";

    void update(Member m) throws TFException {
        if (m == null) {
            throw new IllegalArgumentException("修改客戶時Customer物件不得為null");
        }
        try (
                //1.2.取得連線
                Connection connection = RDBConnection.getConnection();
                //3.準備指令
                PreparedStatement pstmt = connection.prepareStatement(UPDATE_Member);) {
            //3.1傳入?的值
            pstmt.setString(6, String.valueOf(m.getUid()));
            pstmt.setString(1, m.getName());
            pstmt.setString(2, m.getEmail());
            pstmt.setString(3, m.getPassword());
            pstmt.setString(4, String.valueOf(m.getGender()));
            pstmt.setString(5, m.getBirthday() != null ? String.valueOf(m.getBirthday()) : "");

            //4.執行指令                        
            pstmt.executeUpdate();//會傳回insert筆數
        } catch (SQLException ex) {
            Logger.getLogger(MemberDAO.class.getName()).log(Level.SEVERE, "執行指令失敗", ex);
            throw new TFException("修改客戶失敗" + ex.getMessage(), ex);
        }

    }
}
