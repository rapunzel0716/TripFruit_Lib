/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uuu.tf.model;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import uuu.tf.entity.TFException;
/**
 *
 * @author Rapunzel_PC
 */
class RDBConnection {
    private static final String driver = "com.mysql.jdbc.Driver";
    private static final String url = "jdbc:mysql://localhost:3306/tf?zeroDateTimeBehavior=convertToNull&characterEncoding=utf8";
    private static final String userid = "userid";
    private static final String pwd = "password";
    
    static Connection getConnection() throws TFException{
        
        try {
            Class.forName(driver);
            try {
                Connection connection = DriverManager.getConnection(url, userid, pwd);
                return connection;
            } catch (SQLException ex) {
                Logger.getLogger(RDBConnection.class.getName()).log(Level.SEVERE, "建立連線失敗", ex);
                throw new TFException("建立連線失敗", ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(RDBConnection.class.getName()).log(Level.SEVERE, "載入DBC Driver失敗", ex);
            throw new TFException("載入JDBC Driver失敗", ex);//丟給前端
        }
    }
}
