package org.mdjee.storemonitor.hibernate.entity;

import javax.persistence.*;

@Entity
@Table(name = "sample_security_question")
public class SampleSecurityQuestion {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int questionId;

    @Column(name = "question", nullable = false)
    private String question;

    public SampleSecurityQuestion(){

    }

    public SampleSecurityQuestion(String question) {
        this.question = question;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
