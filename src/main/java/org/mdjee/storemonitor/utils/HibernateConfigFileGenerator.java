package org.mdjee.storemonitor.utils;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class HibernateConfigFileGenerator{

    private static final Logger logger = LogManager.getLogger(HibernateConfigFileGenerator.class);

    public void generateXML(String xmlFilePath, String mySqlClientPassword, String mySqlClientUsername, String host, String port){

        try {

            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

            Document document = documentBuilder.newDocument();

            // root element
            Element root = document.createElement("hibernate-configuration");
            document.appendChild(root);

            // session-factory element
            Element sessionFactoryElement = document.createElement("session-factory");

            root.appendChild(sessionFactoryElement);

            // connection.url element
            Element property1 = document.createElement("property");
            property1.setAttribute("name","connection.url");
            String connectionUrl = "jdbc:mysql://" + host + ":" + port +"/" + Commons.DATABASE_NAME;
            property1.appendChild(document.createTextNode(connectionUrl));
            sessionFactoryElement.appendChild(property1);

            // connection.driver_class element
            Element property2 = document.createElement("property");
            property2.setAttribute("name","connection.driver_class");
            property2.appendChild(document.createTextNode("com.mysql.cj.jdbc.Driver"));
            sessionFactoryElement.appendChild(property2);

            // connection.username element
            Element property3 = document.createElement("property");
            property3.setAttribute("name","connection.username");
            property3.appendChild(document.createTextNode(mySqlClientUsername));
            sessionFactoryElement.appendChild(property3);

            // connection.password element
            Element property4 = document.createElement("property");
            property4.setAttribute("name","connection.password");
            property4.appendChild(document.createTextNode(getHashedPassword(mySqlClientPassword)));
            sessionFactoryElement.appendChild(property4);

            // show_sql element
            // Echo the SQL to stdout
            Element property5 = document.createElement("property");
            property5.setAttribute("name","show_sql");
            property5.appendChild(document.createTextNode("true"));
            sessionFactoryElement.appendChild(property5);

            // hibernate.hbm2ddl.auto element
            // DB schema will be updated if needed
            Element property6 = document.createElement("property");
            property6.setAttribute("name","hibernate.hbm2ddl.auto");
            property6.appendChild(document.createTextNode("update"));
            sessionFactoryElement.appendChild(property6);

            // hibernate.dialect element
            // Select our SQL dialect [MySQLDialect | MySQL8Dialect]
            Element property7 = document.createElement("property");
            property7.setAttribute("name","hibernate.dialect");
            property7.appendChild(document.createTextNode("org.hibernate.dialect.MySQL8Dialect"));
            sessionFactoryElement.appendChild(property7);

            // Entity Mappings -----------------------------------------------------------------------------------------
            // User entity
            Element property8 = document.createElement("mapping");
            property8.setAttribute("class","org.mdjee.storemonitor.hibernate.entity.User");
            sessionFactoryElement.appendChild(property8);

            // Loss entity
            Element property9 = document.createElement("mapping");
            property9.setAttribute("class","org.mdjee.storemonitor.hibernate.entity.Loss");
            sessionFactoryElement.appendChild(property9);

            // Product entity
            Element property10 = document.createElement("mapping");
            property10.setAttribute("class","org.mdjee.storemonitor.hibernate.entity.Product");
            sessionFactoryElement.appendChild(property10);

            // Profit entity
            Element property11 = document.createElement("mapping");
            property11.setAttribute("class","org.mdjee.storemonitor.hibernate.entity.Profit");
            sessionFactoryElement.appendChild(property11);

            // SampleSecurityQuestion entity
            Element property12 = document.createElement("mapping");
            property12.setAttribute("class","org.mdjee.storemonitor.hibernate.entity.SampleSecurityQuestion");
            sessionFactoryElement.appendChild(property12);

            // SecurityQuestion entity
            Element property13 = document.createElement("mapping");
            property13.setAttribute("class","org.mdjee.storemonitor.hibernate.entity.SecurityQuestion");
            sessionFactoryElement.appendChild(property13);

            // Store entity
            Element property14 = document.createElement("mapping");
            property14.setAttribute("class","org.mdjee.storemonitor.hibernate.entity.Store");
            sessionFactoryElement.appendChild(property14);

            // Vehicle entity
            Element property15 = document.createElement("mapping");
            property15.setAttribute("class","org.mdjee.storemonitor.hibernate.entity.Vehicle");
            sessionFactoryElement.appendChild(property15);

            // VehicleStore entity
            Element property16 = document.createElement("mapping");
            property16.setAttribute("class","org.mdjee.storemonitor.hibernate.entity.VehicleStore");
            sessionFactoryElement.appendChild(property16);

            // VehicleStoreProduct entity
            Element property17 = document.createElement("mapping");
            property17.setAttribute("class","org.mdjee.storemonitor.hibernate.entity.VehicleStoreProduct");
            sessionFactoryElement.appendChild(property17);

            // BalanceHistory entity
            Element property18 = document.createElement("mapping");
            property18.setAttribute("class","org.mdjee.storemonitor.hibernate.entity.BalanceHistory");
            sessionFactoryElement.appendChild(property18);

            // BalanceHistoryReturn entity
            Element property19 = document.createElement("mapping");
            property19.setAttribute("class","org.mdjee.storemonitor.hibernate.entity.BalanceHistoryReturn");
            sessionFactoryElement.appendChild(property19);

            // BalanceHistorySoldProduct entity
            Element property20 = document.createElement("mapping");
            property20.setAttribute("class","org.mdjee.storemonitor.hibernate.entity.BalanceHistorySoldProduct");
            sessionFactoryElement.appendChild(property20);

            // BalanceHistoryLoadingProduct entity
            Element property21 = document.createElement("mapping");
            property21.setAttribute("class","org.mdjee.storemonitor.hibernate.entity.BalanceHistoryLoadingProduct");
            sessionFactoryElement.appendChild(property21);

            // BalanceHistoryNotSoldProduct entity
            Element property22 = document.createElement("mapping");
            property22.setAttribute("class","org.mdjee.storemonitor.hibernate.entity.BalanceHistoryNotSoldProduct");
            sessionFactoryElement.appendChild(property22);

            // create the xml file
            //transform the DOM Object to an XML File
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            DOMImplementation domImpl = document.getImplementation();
            DocumentType doctype = domImpl.createDocumentType("hibernate-configuration",
                    "-//Hibernate/Hibernate Configuration DTD//EN",
                    "http://www.hibernate.org/dtd/hibernate-configuration-3.0.dtd");

            transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, doctype.getPublicId());
            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, doctype.getSystemId());

            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File(xmlFilePath));

            // If you use
            // StreamResult result = new StreamResult(System.out);
            // the output will be pushed to the standard output ...
            // You can use that for debugging

            transformer.transform(domSource, streamResult);

            logger.info("Done creating XML configuration file for Hibernate.");

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
    }

    private String getHashedPassword(String plainText) {
        Encryptor encryptor = new Encryptor();
        String hashedPassword = encryptor.encrypt(plainText);
        return hashedPassword;
    }


}

