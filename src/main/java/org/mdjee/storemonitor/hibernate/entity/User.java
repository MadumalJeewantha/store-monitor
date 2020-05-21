package org.mdjee.storemonitor.hibernate.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "user")
public class User {

    @Id
    @Column(name = "user_name")
    private String userName;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "last_login")
    private Date lastLogin;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "security_question_id", nullable = false)
    private SecurityQuestion securityQuestion;

    public User(){

    }

    public User(String userName, String password, Date lastLogin, SecurityQuestion securityQuestion){
        this.userName = userName;
        this.password = password;
        this.lastLogin = lastLogin;
        this.securityQuestion = securityQuestion;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public SecurityQuestion getSecurityQuestion() {
        return securityQuestion;
    }

    public void setSecurityQuestion(SecurityQuestion securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }


}
