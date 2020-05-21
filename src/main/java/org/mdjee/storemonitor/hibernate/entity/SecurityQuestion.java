package org.mdjee.storemonitor.hibernate.entity;

import javax.persistence.*;

@Entity
@Table(name = "security_questions")
public class SecurityQuestion {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "question1", nullable = false)
    private int question1;

    @Column(name = "answer1", nullable = false)
    private String answer1;

    @Column(name = "question2", nullable = false)
    private int question2;

    @Column(name = "answer2", nullable = false)
    private String answer2;

    @Column(name = "question3", nullable = false)
    private int question3;

    @Column(name = "answer3", nullable = false)
    private String answer3;

    @OneToOne(mappedBy = "securityQuestion")
    private User user;

    public SecurityQuestion(){

    }

    public SecurityQuestion(int question1, String answer1, int question2, String answer2, int question3, String answer3) {
        this.question1 = question1;
        this.answer1 = answer1;
        this.question2 = question2;
        this.answer2 = answer2;
        this.question3 = question3;
        this.answer3 = answer3;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuestion1() {
        return question1;
    }

    public void setQuestion1(int question1) {
        this.question1 = question1;
    }

    public String getAnswer1() {
        return answer1;
    }

    public void setAnswer1(String answer1) {
        this.answer1 = answer1;
    }

    public int getQuestion2() {
        return question2;
    }

    public void setQuestion2(int question2) {
        this.question2 = question2;
    }

    public String getAnswer2() {
        return answer2;
    }

    public void setAnswer2(String answer2) {
        this.answer2 = answer2;
    }

    public int getQuestion3() {
        return question3;
    }

    public void setQuestion3(int question3) {
        this.question3 = question3;
    }

    public String getAnswer3() {
        return answer3;
    }

    public void setAnswer3(String answer3) {
        this.answer3 = answer3;
    }
}
