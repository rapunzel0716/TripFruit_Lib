/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uuu.tf.model;

import java.sql.SQLException;
import uuu.tf.entity.Member;
import uuu.tf.entity.TFException;

/**
 *
 * @author Rapunzel_PC
 */
public class MemberService {

    private MemberDAO dao = new MemberDAO();

    public Member login(String email, String password) throws TFException {
        if (email != null) {
            Member m = dao.selectByEmail(email);
            if(m != null && password != null && password.equals(m.getPassword())) {
                return m;
            } else {
                throw new TFException("登入失敗，Email或密碼錯誤");
            }
        }else
            throw new TFException("登入失敗，請輸入Email");
    }
    
    public void register(Member m) throws TFException{
        try{
            dao.insert(m);
        }catch(TFException ex){
            if(ex.getCause() instanceof SQLException){
                SQLException cause = (SQLException)ex.getCause();
                if(cause.getErrorCode() == 1062){
                    if(cause.getMessage().indexOf("PRIMARY") > 0)
                        throw new TFException("uid已被使用",cause);
                    else if(cause.getMessage().indexOf("email_UNIQUE") > 0)
                        throw new TFException("e-mail已被使用",cause);
                }
            }
            throw ex;
        }
    }

    public void update(Member m) throws TFException{
        dao.update(m);
    }
}
