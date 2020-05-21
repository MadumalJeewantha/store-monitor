package org.mdjee.storemonitor.utils;

import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mdjee.storemonitor.config.ConfigInitializer;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class BackupRestore {
    private ResultSet resultSet;
    private Connection connection;
    private Statement statement;
    private int BUFFER = 99999;

    /**
     * Logger instance for AddToStoreController class
     */
    private static final Logger logger = LogManager.getLogger(BackupRestore.class);


    /**
     * Initialize backup process.
     *
     * @param backupPath backup file writing path
     * @return
     */
    public Task backupWorker(String backupPath) {
        return new Task() {
            @Override
            protected Object call() throws Exception {

                updateMessage("Initializing  backup process.");

                try {
                    LocalDateTime dateTime = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");
                    String now = formatter.format(dateTime);

                    updateMessage("Generated file name.");
                    updateProgress(10, 100);

                    updateMessage("Reading xml file.");
                    HashMap<String, String> databaseConfigurations = getDatabaseConfigurations();
                    updateProgress(20, 100);

                    // Get data
                    updateMessage("Started reading data from database.");
                    updateProgress(30, 100);
                    byte[] data = getData(databaseConfigurations.get("host"), databaseConfigurations.get("port"),
                            databaseConfigurations.get("username"), databaseConfigurations.get("password"),
                            databaseConfigurations.get("database")).getBytes();

                    updateMessage("Done reading data from database.");
                    updateProgress(80, 100);

                    updateMessage("Started writing to file.");
                    File fileDestination = new File(backupPath + "\\store-monitor-app-database-backup-" + now + ".zip");
                    FileOutputStream dest = new FileOutputStream(fileDestination);
                    ZipOutputStream zip = new ZipOutputStream(new BufferedOutputStream(dest));
                    zip.setMethod(ZipOutputStream.DEFLATED);
                    zip.setLevel(Deflater.BEST_COMPRESSION);
                    zip.putNextEntry(new ZipEntry("store-monitor-app-database-backup-" + now + ".sql"));
                    zip.write(data);
                    zip.close();
                    dest.close();

                    updateMessage("Done writing to file.");
                    updateMessage("Backup completed successfully.");
                    updateProgress(100, 100);

                    // Notification
                    Notification.showInfoNotification("Backup completed successfully.\n" +
                            "Saved path: " + fileDestination, "Security center");

                } catch (Exception e) {
                    logger.info(e.getMessage());
                }

                return true;
            }
        };
    }


    /**
     * Restore database with selected file.
     *
     * @param sourceFilePath file path to be restore
     */
    public Task restoreWorker(String sourceFilePath) {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                updateMessage("Initializing  backup process.");

                updateMessage("Started reading configuration file.");
                HashMap<String, String> databaseConfigurations = getDatabaseConfigurations();
                updateMessage("Done reading configuration file.");
                updateProgress(20, 100);

                restore(databaseConfigurations.get("username"), databaseConfigurations.get("password"),
                        databaseConfigurations.get("database"), databaseConfigurations.get("host"),
                        databaseConfigurations.get("port"), sourceFilePath);

                updateMessage("Restore completed successfully.");
                updateProgress(100, 100);

                // Notification
                Notification.showInfoNotification("Restore completed successfully.", "Security center");

                return true;
            }
        };
    }

    /**
     * Read hibernate configuration file and get details.
     *
     * @return HashMap<String, String> contains configuration details
     */
    public HashMap<String, String> getDatabaseConfigurations() {
        // To store configuration details
        HashMap<String, String> configurations = new HashMap<>();

        try {

            //Get Document Builder
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            // To avoid parser to downloading external DTD
            factory.setValidating(false);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            DocumentBuilder builder = factory.newDocumentBuilder();

            //Build Document
            Document document = builder.parse(new FileInputStream(ConfigInitializer.hibernateConfigFile));

            //Normalize the XML Structure; It's just too important
            document.getDocumentElement().normalize();

            //Here comes the root node
            Element root = document.getDocumentElement();
            System.out.println(root.getNodeName());

            //Get session-factory element
            NodeList nList = document.getElementsByTagName("session-factory");

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node node = nList.item(temp);
                System.out.println("");    //Just a separator

                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) node;

                    // Get property elements
                    NodeList propertyNodeList = eElement.getElementsByTagName("property");

                    for (int i = 0; i < propertyNodeList.getLength(); i++) {

                        // Get attributes
                        String attributeName = "";
                        NamedNodeMap attributes = propertyNodeList.item(i).getAttributes();
                        for (int j = 0; j < attributes.getLength(); j++) {
                            attributeName = attributes.item(j).getTextContent();
                        }

                        if (attributeName.equals("connection.url")) {
                            // jdbc:mysql://localhost:3306/store_monitor_db
                            // host
                            // database
                            // port
                            String[] split = propertyNodeList.item(i).getTextContent().split("//");
                            String host = split[1].split(":")[0];
                            String port = split[1].split(":")[1].split("/")[0];
                            String database = split[1].split(":")[1].split("/")[1];

                            configurations.put("host", host);
                            configurations.put("port", port);
                            configurations.put("database", database);

                        } else if (attributeName.equals("connection.password")) {
                            Encryptor encryptor = new Encryptor();
                            configurations.put("password", encryptor.decrypt(propertyNodeList.item(i).getTextContent()));
                        } else if (attributeName.equals("connection.username")) {
                            configurations.put("username", propertyNodeList.item(i).getTextContent());
                        }

                    }

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return configurations;
    }

    /**
     * Get backup
     *
     * @param host     mysql server host
     * @param port     mysql server port
     * @param user     mysql server user name
     * @param password mysql server password
     * @param dataBase database name
     * @return string whole database
     */
    public String getData(String host, String port, String user, String password, String dataBase) {

        String mySqlPath = getMysqlBinPath(user, password, dataBase, host, port);

        Process run = null;
        try {
            run = Runtime.getRuntime().exec(mySqlPath + "mysqldump --host=" + host + " --port=" + port +
                    " --user=" + user + " --password=" + password + " --add-drop-database -B " + "  " +
                    "--skip-comments --skip-triggers " + dataBase);
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }

        InputStream in = run.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        StringBuffer temp = new StringBuffer();

        int count;
        char[] cbuf = new char[BUFFER];
        try {

            while ((count = br.read(cbuf, 0, BUFFER)) != -1) {
                temp.append(cbuf, 0, count);
            }

            br.close();
            in.close();

        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }

        return temp.toString();
    }

    /**
     * Find MySql path
     * Mysql path is required to locate the bin folder inside it because it contains the Mysqldump which performs a
     * main roal while taking backup.
     *
     * @param user     mysql server user name
     * @param password mysql server password
     * @param dataBase database name
     * @param host     mysql server host
     * @param port     mysql server port
     * @return string MySql bin path
     */
    public String getMysqlBinPath(String user, String password, String dataBase, String host, String port) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + dataBase, user, password);
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String path = "";

        try {
            resultSet = statement.executeQuery("select @@basedir");
            while (resultSet.next()) {
                path = resultSet.getString(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        path = path + "bin\\";

        //System.println("Mysql path is :" + path);
        return path;
    }

    /**
     * Restore from selected file.
     *
     * @param user     mysql server username
     * @param password mysql serve password
     * @param database database name
     * @param host     mysql server host
     * @param port     mysql server port
     * @param source   string selected file path
     */
    public void restore(String user, String password, String database, String host, String port, String source) {
        String mySqlBinPath = getMysqlBinPath(user, password, database, host, port);

        String[] executeCmd = new String[]{mySqlBinPath + "mysql", "--user=" + user, "--password=" + password,
                "-e", "source " + source};

        Process runtimeProcess;
        try {
            runtimeProcess = Runtime.getRuntime().exec(executeCmd);
            int processComplete = runtimeProcess.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
