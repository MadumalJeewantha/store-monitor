package org.mdjee.storemonitor.hibernate.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.mdjee.storemonitor.config.ConfigInitializer;
import org.mdjee.storemonitor.utils.Encryptor;

public class HibernateUtil {

    private static ServiceRegistry registry;
    private static SessionFactory sessionFactory;

    private static final Logger logger = LogManager.getLogger(HibernateUtil.class);


    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {

                // Set configurations instead of using XML file - only for Password
                 Configuration configuration = new Configuration();
                // hibernate.cfg.xml file path to get Encrypted Password
                 configuration.configure(ConfigInitializer.hibernateConfigFile);
                 configuration.setProperty("hibernate.connection.password",  new Encryptor().getEncryptor()
                         .decrypt(configuration.getProperty("hibernate.connection.password")));
                 //Create registry with Decrypted Password
                 registry = new StandardServiceRegistryBuilder().configure(ConfigInitializer.hibernateConfigFile)
                         .applySettings(configuration.getProperties()).build();


                // Create registry - without Encrypted Password
                // registry = new StandardServiceRegistryBuilder().configure(ConfigInitializer.hibernateConfigFile).build();

                // Create MetadataSources
                MetadataSources sources = new MetadataSources(registry);
                // Create Metadata
                Metadata metadata = sources.getMetadataBuilder().build();
                // Create SessionFactory
                sessionFactory = metadata.getSessionFactoryBuilder().build();

            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e.getMessage());

                if (registry != null) {
                    StandardServiceRegistryBuilder.destroy(registry);
                }
            }
        }
        return sessionFactory;
    }

    public static void shutdown() {
        if (registry != null) {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }
}
