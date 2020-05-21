package org.mdjee.storemonitor.utils;

import org.mdjee.storemonitor.hibernate.entity.SampleSecurityQuestion;

import java.util.ArrayList;
import java.util.List;

public class SampleSecurityQuestionGenerator {


    public static List<SampleSecurityQuestion> getSampleSecurityQuestions(){
        List<SampleSecurityQuestion> sampleSecurityQuestions = new ArrayList<>();

        sampleSecurityQuestions.add(new SampleSecurityQuestion("What is your best friend name?"));
        sampleSecurityQuestions.add(new SampleSecurityQuestion("What is your spouse or partner's mother's maiden name?"));
        sampleSecurityQuestions.add(new SampleSecurityQuestion(" Who is your childhood sports hero?"));
        sampleSecurityQuestions.add(new SampleSecurityQuestion("What was your favorite food as a child?"));
        sampleSecurityQuestions.add(new SampleSecurityQuestion("What is your favorite color?"));
        sampleSecurityQuestions.add(new SampleSecurityQuestion("What is your unforgettable day/event?"));
        sampleSecurityQuestions.add(new SampleSecurityQuestion("What was the house number and street name you lived in as a child?"));
        sampleSecurityQuestions.add(new SampleSecurityQuestion("What primary school did you attend?"));
        sampleSecurityQuestions.add(new SampleSecurityQuestion("In what town or city was your first full time job?"));
        sampleSecurityQuestions.add(new SampleSecurityQuestion("In what town or city did you meet your spouse/partner?"));
        sampleSecurityQuestions.add(new SampleSecurityQuestion("In what town or city did your mother and father meet?"));
        sampleSecurityQuestions.add(new SampleSecurityQuestion("What time of the day were you born? (hh:mm)"));

        return sampleSecurityQuestions;
    }
}
