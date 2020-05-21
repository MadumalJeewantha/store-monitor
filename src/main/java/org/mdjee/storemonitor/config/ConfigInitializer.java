package org.mdjee.storemonitor.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mdjee.storemonitor.hibernate.entity.SampleSecurityQuestion;
import org.mdjee.storemonitor.hibernate.entity.User;
import org.mdjee.storemonitor.hibernate.utils.HibernateUtil;
import org.mdjee.storemonitor.utils.HibernateConfigFileGenerator;
import org.mdjee.storemonitor.utils.SampleSecurityQuestionGenerator;

import java.io.File;
import java.util.List;

public class ConfigInitializer {

    private static final Logger logger = LogManager.getLogger(ConfigInitializer.class);

    private static String programDataPath = System.getenv("ALLUSERSPROFILE");
    public final static File hibernateConfigFile = new File(programDataPath + "\\StoreMonitor\\hibernate.cfg.xml");

    public static boolean checkHibernateConfigFileStatus(){
        // return file status
        return hibernateConfigFile.exists();
    }

    public static void createHibernateConfigFile(String mySqlClientPassword, String mySqlClientUsername,String host,
                                                 String port){
        // Create directory
        new File(programDataPath + "\\StoreMonitor").mkdir();
        logger.info("Created directory: " + programDataPath + "\\StoreMonitor");

        // Create Hibernate XML configuration file
        HibernateConfigFileGenerator hibernateConfigFileGenerator = new HibernateConfigFileGenerator();
        hibernateConfigFileGenerator.generateXML(hibernateConfigFile.getPath(), mySqlClientPassword, mySqlClientUsername,
                host, port);
    }

    public static void populateSampleSecurityQuestion(){
        List<SampleSecurityQuestion> sampleSecurityQuestions = SampleSecurityQuestionGenerator.getSampleSecurityQuestions();

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            // start a transaction
            transaction = session.beginTransaction();

            // save SampleSecurityQuestion objects
            for(SampleSecurityQuestion sampleSecurityQuestion : sampleSecurityQuestions){
                session.save(sampleSecurityQuestion);
            }

            // commit transaction
            transaction.commit();

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
                logger.warn("Transaction is null, rolling back.");
            }
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }

    public static boolean checkUserStatus() {
        // Check user table
        Transaction transaction = null;
        boolean status = false;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<User> users = session.createQuery("from User", User.class).list();
            // users.forEach(s -> System.out.println(s.getUserName()));

            status = users.isEmpty();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
                logger.warn("Transaction is null, rolling back.");
            }
            e.printStackTrace();
            logger.error(e.getMessage());

        }
        return status;
    }
}
