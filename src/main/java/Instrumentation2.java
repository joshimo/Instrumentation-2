/**
 * Created by y.golota on 13.12.2016.
 *
 */

import java.awt.event.*;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.*;
import java.util.Date;

public class Instrumentation2 {
    static String url;
    static String user;
    static String password;
    String query;

    static int scrWidth;
    static int scrHeight;

    Connection connection;
    Statement statement;
    ResultSet resultSet;
    int updateResult;

    static JFrame StartWindow;

    static Console console = new Console(10, 270, 680, 200);

    static DataContainer pipeData;
    static DataContainer equipmentData;
    static DataContainer instrumentsData;
    static DataContainer jointData;

    public static String pipeImportXLSFilename = "src\\main\\resources\\Import\\PipeImport.xls";
    public static String equipmentImportXLSFilename = "src\\main\\resources\\Import\\EquipmentImport.xls";
    public static String instrumentImportXLSFilename = "src\\main\\resources\\Import\\InstrumentImport.xls";
    public static String hookupMappingFilename = "src\\main\\resources\\Import\\HookupMapping.xls";
    public static String instrumentExportXLSFilename = "src\\main\\resources\\Import\\InstrumentExport.xls";

    static {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        scrWidth = (int) screen.getWidth();
        scrHeight = (int) screen.getHeight();

        url = "jdbc:mysql://www.db4free.net:3306/instrumentation?useSSL=false";
        user = "joshimo";
        password = "joshimo@list.ru";
    }

    public Instrumentation2() {
        new Login();
    }

    private class Login extends JFrame {
        int w = 320;
        int h = 240;

        Login() {
            super("Instrumentation login");
            this.setSize(w, h);
            this.setLocation((int)(0.5*(scrWidth - w)), (int) (0.5*(scrHeight - h)));
            this.setResizable(false);
            this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            this.setLayout(null);

            JLabel loginLabel = new JLabel("User name:");
            loginLabel.setBounds(50, 10, w - 100, 20);

            JTextField loginField = new JTextField();
            loginField.setBounds(50, 35, w - 100, 20);

            JLabel passLabel = new JLabel("Password:");
            passLabel.setBounds(50, 75, w - 100, 20);

            JPasswordField passField = new JPasswordField();
            passField.setBounds(50, 100, w - 100, 20);

            JButton okButton = new JButton("Log In");
            okButton.setBounds(w/2 - 90, h - 80, 80, 30);
            JButton cancelButton = new JButton("Cancel");
            cancelButton.setBounds(w/2 + 10, h - 80, 80, 30);

            okButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (loginField.getText().length() > 0)
                        user = loginField.getText();
                    if (passField.getPassword().length > 0) {
                        password = "";
                        for (char c : passField.getPassword()) password += c;
                    }
                    Login.this.setVisible(false);
                    new StartWindow();
                    System.out.println("login: " + user);
                    System.out.println("password: " + password);
                }
            });

            cancelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });

            this.add(loginLabel);
            this.add(loginField);
            this.add(passLabel);
            this.add(passField);
            this.add(okButton);
            this.add(cancelButton);
            this.setVisible(true);
        }
    }

    private class StartWindow {

        int L = 700;
        int H = 500;

        JButton RunInstrumentation;
        JButton NewProject;
        JButton DeleteProject;
        JButton RunSQLClient;

        StartWindow() {
            StartWindow = new JFrame("Instrumentation");
            StartWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            StartWindow.setSize(L, H);
            StartWindow.setLocation((scrWidth - L)/2, (scrHeight - H)/2);
            StartWindow.setResizable(false);

            RunInstrumentation = new JButton("Run Instrumentation");
            RunInstrumentation.setBounds(160, 50, 380, 40);
            RunInstrumentation.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Instrumentation2.console.Echo("Running Instrumentation", console.DONT_SHOW_JOPTION);
                    pipeData = new DataContainer();
                    equipmentData = new DataContainer();
                    instrumentsData = new DataContainer();
                    jointData = new DataContainer();
                    Instrumentation2.StartWindow.setVisible(false);
                    new InstrumentationWindow();
                }
            });

            NewProject = new JButton("Create new project");
            NewProject.setBounds(160, 100, 380, 40);
            NewProject.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (JOptionPane.showConfirmDialog(null, "Create new project?") == JOptionPane.OK_OPTION) {
                        CreateNewProjectThread t = new CreateNewProjectThread();
                        t.start();
                    }
                }
            });

            DeleteProject = new JButton("Delete current project");
            DeleteProject.setBounds(160, 150, 380, 40);
            DeleteProject.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (JOptionPane.showConfirmDialog(null, "Are You sure to delete current project?") == JOptionPane.OK_OPTION) {
                        DeleteCurrentProjectThread t = new DeleteCurrentProjectThread();
                        t.start();
                    }
                }
            });

            RunSQLClient = new JButton("Run SQL Database Client");
            RunSQLClient.setBounds(160, 200, 380, 40);
            RunSQLClient.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Instrumentation2.console.Echo("Running SQLClient", console.DONT_SHOW_JOPTION);
                    Instrumentation2.StartWindow.setVisible(false);
                    new SQLClient();
                }
            });

            StartWindow.setLayout(null);

            StartWindow.add(RunInstrumentation);
            StartWindow.add(NewProject);
            StartWindow.add(DeleteProject);
            StartWindow.add(RunSQLClient);
            StartWindow.add(console);

            StartWindow.validate();
            StartWindow.repaint();
            StartWindow.setVisible(true);
        }

        void enableButtons(boolean b) {
            RunInstrumentation.setEnabled(b);
            NewProject.setEnabled(b);
            DeleteProject.setEnabled(b);
            RunSQLClient.setEnabled(b);
        }

        private class CreateNewProjectThread extends Thread {
            @Override
            public void run() {
                enableButtons(false);
                Instrumentation2.console.Echo("Creating new project, please wait...", console.DONT_SHOW_JOPTION);
                CreateNewProject();
                enableButtons(true);
            }

            private void CreateNewProject() {

                /** Executing following queries for database table creation:
                 *
                 *  CREATE TABLE instruments (Number SMALLINT NOT NULL AUTO_INCREMENT KEY, Revision VARCHAR(2), TagNumber VARCHAR(32), LoopNumber VARCHAR(32),
                 *  InstrumentType VARCHAR(32), PIDNumber VARCHAR(32), PipeName VARCHAR(32), EqName VARCHAR(32), GeneralNotes VARCHAR(256),
                 *  UDF01 VARCHAR(128), UDF02 VARCHAR(128), UDF03 VARCHAR(128), ..., UDF[count] VARCHAR(128)) ENGINE MyISAM;
                 *  ALTER TABLE instruments ADD INDEX (Revision(2));
                 *  ALTER TABLE instruments ADD INDEX (TagNumber(32));
                 *  ALTER TABLE instruments ADD INDEX (LoopNumber(32));
                 *  ALTER TABLE instruments ADD INDEX (InstrumentType(32));
                 *  ALTER TABLE instruments ADD INDEX (PIDNumber(64));
                 *  ALTER TABLE instruments ADD INDEX (PipeName(32));
                 *  ALTER TABLE instruments ADD INDEX (EqName(32));
                 *
                 *  CREATE TABLE loops (Number SMALLINT NOT NULL AUTO_INCREMENT KEY, Revision VARCHAR(2), LoopNumber VARCHAR(32), PIDNumber VARCHAR(32), LoopDescription VARCHAR(128),
                 *  Indication VARCHAR(6), Summ VARCHAR(6), Control VARCHAR(6), Alarm VARCHAR(6), Safety VARCHAR(6),
                 *  LLL VARCHAR(12), LL VARCHAR(12), L VARCHAR(12), H VARCHAR(12), HH VARCHAR(12), HHH VARCHAR(12), GeneralNotes VARCHAR(256),
                 *  UDF01 VARCHAR(128), UDF02 VARCHAR(128), UDF03 VARCHAR(128), ..., UDF[count] VARCHAR(128)) ENGINE MyISAM;
                 *  ALTER TABLE loops ADD INDEX (Revision(2));
                 *  ALTER TABLE loops ADD INDEX (LoopNumber(32));
                 *  ALTER TABLE loops ADD INDEX (PIDNumber(32));
                 *  ALTER TABLE loops ADD INDEX (Indication VARCHAR(6));
                 *  ALTER TABLE loops ADD INDEX (Summ VARCHAR(6));
                 *  ALTER TABLE loops ADD INDEX (Control VARCHAR(6));
                 *  ALTER TABLE loops ADD INDEX (Alarm VARCHAR(6));
                 *  ALTER TABLE loops ADD INDEX (Safety VARCHAR(6));
                 *
                 *  CREATE TABLE pipelines (Number SMALLINT NOT NULL AUTO_INCREMENT KEY, Revision VARCHAR(2), PipeName VARCHAR(32), GeneralNotes VARCHAR(256),
                 *  UDF01 VARCHAR(128), UDF02 VARCHAR(128), UDF03 VARCHAR(128), ..., UDF[count] VARCHAR(128)) ENGINE MyISAM;
                 *  ALTER TABLE pipelines ADD INDEX (PipeName(32));
                 *  ALTER TABLE pipelines ADD INDEX (Revision(2));
                 *
                 *  CREATE TABLE equipment (Number SMALLINT NOT NULL AUTO_INCREMENT KEY, Revision VARCHAR(2), EqName VARCHAR(32), GeneralNotes VARCHAR(256),
                 *  UDF01 VARCHAR(128), UDF02 VARCHAR(128), UDF03 VARCHAR(128), ..., UDF[count] VARCHAR(128)) ENGINE MyISAM;
                 *  ALTER TABLE equipment ADD INDEX (EqName(32))
                 *  ALTER TABLE equipment ADD INDEX (Revision(2));
                 *
                 *  CREATE TABLE cables (Number SMALLINT NOT NULL AUTO_INCREMENT KEY, Revision VARCHAR(2), CableName VARCHAR(32), StartPoint VARCHAR(32), FinishPoint VARCHAR(32),
                 *  SignalType VARCHAR(32), IS_Proof VARCHAR(6), CableType VARCHAR(32), CableLength VARCHAR(6), GeneralNotes VARCHAR(256),
                 *  UDF01 VARCHAR(128), UDF02 VARCHAR(128), UDF03 VARCHAR(128), ..., UDF[count] VARCHAR(128)) ENGINE MyISAM;
                 *  ALTER TABLE cables ADD INDEX (Revision(2));
                 *  ALTER TABLE cables ADD INDEX (CableName(32));
                 *  ALTER TABLE cables ADD INDEX (StartPoint(32));
                 *  ALTER TABLE cables ADD INDEX (FinishPoint(32));
                 *  ALTER TABLE cables ADD INDEX (SignalType(32));
                 *  ALTER TABLE cables ADD INDEX (IS_Proof(32));
                 *  ALTER TABLE cables ADD INDEX (CableType(32));
                 *
                 *  CREATE TABLE jboxes (Number SMALLINT NOT NULL AUTO_INCREMENT KEY, Revision VARCHAR(2), JBoxName VARCHAR(32), Signaltype VARCHAR(32),
                 *  IS_proof VARCHAR(6), InputsNumber VARCHAR(2), OutputsNumber VARCHAR(2), GeneralNotes VARCHAR(256),
                 *  UDF01 VARCHAR(128), UDF02 VARCHAR(128), UDF03 VARCHAR(128), ..., UDF[count] VARCHAR(128)) ENGINE MyISAM;
                 *  ALTER TABLE jboxes ADD INDEX (Revision(2));
                 *  ALTER TABLE jboxes ADD INDEX (JBoxName(32));
                 *  ALTER TABLE jboxes ADD INDEX (Signaltype(32));
                 *  ALTER TABLE jboxes ADD INDEX (IS_proof(32));
                 *  ALTER TABLE jboxes ADD INDEX (InputsNumber(2));
                 *  ALTER TABLE jboxes ADD INDEX (OutputsNumber(2));
                 *
                 *  CREATE TABLE hookups (Number SMALLINT NOT NULL AUTO_INCREMENT KEY, Revision VARCHAR(2), TagNumber VARCHAR(32), GeneralNotes VARCHAR(256),
                 *  UDF01 VARCHAR(128), UDF02 VARCHAR(128), UDF03 VARCHAR(128), ..., UDF[count] VARCHAR(128)) ENGINE MyISAM;
                 *  ALTER TABLE equipment ADD INDEX (TagNumber(32))
                 *  ALTER TABLE equipment ADD INDEX (Revision(2));
                 *
                 *  CREATE TABLE UDFs (ItemName VARCHAR(32), UDF01 VARCHAR(32), UDF02 VARCHAR(32), ..., UDF64 VARCHAR(32)) ENGINE MyISAM;
                 */

                int count = DataContainer.N;

                try {
                    connection = DriverManager.getConnection(url, user, password);
                    statement = connection.createStatement();

                    ExecutableString SendQuery = (qry) -> {
                        try {
                            Instrumentation2.console.Echo("SQL query = " + qry, console.DONT_SHOW_JOPTION);
                            updateResult = statement.executeUpdate(qry);
                            Instrumentation2.console.Echo("SQL server return = " + updateResult, console.DONT_SHOW_JOPTION);
                        } catch (SQLException se) {
                            Instrumentation2.console.Echo("SQL Exception!\n" + se.getMessage(), console.DONT_SHOW_JOPTION);
                        }
                    };

                    query = "CREATE TABLE instruments (Number SMALLINT NOT NULL AUTO_INCREMENT KEY, Revision VARCHAR(2), TagNumber VARCHAR(32), LoopNumber VARCHAR(32), " +
                            "InstrumentType VARCHAR(32), PIDNumber VARCHAR(64), PipeName VARCHAR(32), EqName VARCHAR(32), GeneralNotes VARCHAR(256), ";
                    for (int i = 1; i < count; i++)
                        if (i < 10) query += "UDF0" + i + " VARCHAR(128), ";
                        else query += "UDF" + i + " VARCHAR(128), ";
                    query += "UDF" + count + " VARCHAR(128)) ENGINE MyISAM;";
                    SendQuery.Execute(query);
                    SendQuery.Execute("ALTER TABLE instruments ADD INDEX (Revision(2))");
                    SendQuery.Execute("ALTER TABLE instruments ADD INDEX (TagNumber(32))");
                    SendQuery.Execute("ALTER TABLE instruments ADD INDEX (LoopNumber(32))");
                    SendQuery.Execute("ALTER TABLE instruments ADD INDEX (InstrumentType(32))");
                    SendQuery.Execute("ALTER TABLE instruments ADD INDEX (PIDNumber(64))");
                    SendQuery.Execute("ALTER TABLE instruments ADD INDEX (PipeName(32))");
                    SendQuery.Execute("ALTER TABLE instruments ADD INDEX (EqName(32))");

                    query = "CREATE TABLE loops (Number SMALLINT NOT NULL AUTO_INCREMENT KEY, Revision VARCHAR(2), LoopNumber VARCHAR(32), PIDNumber VARCHAR(32), LoopDescription VARCHAR(128), " +
                            "Indication VARCHAR(6), Summ VARCHAR(6), Control VARCHAR(6), Alarm VARCHAR(6), Safety VARCHAR(6), " +
                            "LLL VARCHAR(12), LL VARCHAR(12), L VARCHAR(12), H VARCHAR(12), HH VARCHAR(12), HHH VARCHAR(12), GeneralNotes VARCHAR(256), ";
                    for (int i = 1; i < count; i++)
                        if (i < 10) query += "UDF0" + i + " VARCHAR(128), ";
                        else query += "UDF" + i + " VARCHAR(128), ";
                    query += "UDF" + count + " VARCHAR(128)) ENGINE MyISAM;";
                    SendQuery.Execute(query);
                    SendQuery.Execute("ALTER TABLE loops ADD INDEX (Revision(2))");
                    SendQuery.Execute("ALTER TABLE loops ADD INDEX (LoopNumber(32))");
                    SendQuery.Execute("ALTER TABLE loops ADD INDEX (PIDNumber(32))");
                    SendQuery.Execute("ALTER TABLE loops ADD INDEX (Indication(6))");
                    SendQuery.Execute("ALTER TABLE loops ADD INDEX (Summ(6))");
                    SendQuery.Execute("ALTER TABLE loops ADD INDEX (Control(6))");
                    SendQuery.Execute("ALTER TABLE loops ADD INDEX (Alarm(6))");
                    SendQuery.Execute("ALTER TABLE loops ADD INDEX (Safety(6))");

                    query = "CREATE TABLE pipelines (Number SMALLINT NOT NULL AUTO_INCREMENT KEY, Revision VARCHAR(2), PipeName VARCHAR(32), GeneralNotes VARCHAR(256), ";
                    for (int i = 1; i < count; i++)
                        if (i < 10) query += "UDF0" + i + " VARCHAR(128), ";
                        else query += "UDF" + i + " VARCHAR(128), ";
                    query += "UDF" + count + " VARCHAR(128)) ENGINE MyISAM;";
                    SendQuery.Execute(query);
                    SendQuery.Execute("ALTER TABLE pipelines ADD INDEX (Revision(2))");
                    SendQuery.Execute("ALTER TABLE pipelines ADD INDEX (PipeName(32))");

                    query = "CREATE TABLE equipment (Number SMALLINT NOT NULL AUTO_INCREMENT KEY, Revision VARCHAR(2), EqName VARCHAR(32), GeneralNotes VARCHAR(256), ";
                    for (int i = 1; i < count; i++)
                        if (i < 10) query += "UDF0" + i + " VARCHAR(128), ";
                        else query += "UDF" + i + " VARCHAR(128), ";
                    query += "UDF" + count + " VARCHAR(128)) ENGINE MyISAM;";
                    SendQuery.Execute(query);
                    SendQuery.Execute("ALTER TABLE equipment ADD INDEX (Revision(2))");
                    SendQuery.Execute("ALTER TABLE equipment ADD INDEX (EqName(32))");

                    query = "CREATE TABLE cables (Number SMALLINT NOT NULL AUTO_INCREMENT KEY, Revision VARCHAR(2), CableName VARCHAR(32), StartPoint VARCHAR(32), FinishPoint VARCHAR(32), " +
                            "SignalType VARCHAR(32), IS_Proof VARCHAR(6), CableType VARCHAR(32), CableLength VARCHAR(6), GeneralNotes VARCHAR(256),";
                    for (int i = 1; i < count; i++)
                        if (i < 10) query += "UDF0" + i + " VARCHAR(128), ";
                        else query += "UDF" + i + " VARCHAR(128), ";
                    query += "UDF" + count + " VARCHAR(128)) ENGINE MyISAM;";
                    SendQuery.Execute(query);
                    SendQuery.Execute("ALTER TABLE cables ADD INDEX (Revision(2))");
                    SendQuery.Execute("ALTER TABLE cables ADD INDEX (CableName(32))");
                    SendQuery.Execute("ALTER TABLE cables ADD INDEX (StartPoint(32))");
                    SendQuery.Execute("ALTER TABLE cables ADD INDEX (FinishPoint(32))");
                    SendQuery.Execute("ALTER TABLE cables ADD INDEX (SignalType(32))");
                    SendQuery.Execute("ALTER TABLE cables ADD INDEX (IS_Proof(6))");
                    SendQuery.Execute("ALTER TABLE cables ADD INDEX (CableType(6))");

                    query = "CREATE TABLE jboxes (Number SMALLINT NOT NULL AUTO_INCREMENT KEY, Revision VARCHAR(2), JBoxName VARCHAR(32), SignalType VARCHAR(32), " +
                            "IS_Proof VARCHAR(6), InputsNumber VARCHAR(2), OutputsNumber VARCHAR(2), GeneralNotes VARCHAR(256), ";
                    for (int i = 1; i < count; i++)
                        if (i < 10) query += "UDF0" + i + " VARCHAR(128), ";
                        else query += "UDF" + i + " VARCHAR(128), ";
                    query += "UDF" + count + " VARCHAR(128)) ENGINE MyISAM;";
                    SendQuery.Execute(query);
                    SendQuery.Execute("ALTER TABLE jboxes ADD INDEX (Revision(2))");
                    SendQuery.Execute("ALTER TABLE jboxes ADD INDEX (JBoxName(32))");
                    SendQuery.Execute("ALTER TABLE jboxes ADD INDEX (SignalType(32))");
                    SendQuery.Execute("ALTER TABLE jboxes ADD INDEX (IS_Proof(6))");
                    SendQuery.Execute("ALTER TABLE jboxes ADD INDEX (InputsNumber(2))");
                    SendQuery.Execute("ALTER TABLE jboxes ADD INDEX (OutputsNumber(2))");

                    query = "CREATE TABLE hookups (Number SMALLINT NOT NULL AUTO_INCREMENT KEY, Revision VARCHAR(2), TagNumber VARCHAR(32), GeneralNotes VARCHAR(256), ";
                    for (int i = 1; i < count; i++)
                        if (i < 10) query += "UDF0" + i + " VARCHAR(128), ";
                        else query += "UDF" + i + " VARCHAR(128), ";
                    query += "UDF" + count + " VARCHAR(128)) ENGINE MyISAM;";
                    SendQuery.Execute(query);
                    SendQuery.Execute("ALTER TABLE hookups ADD INDEX (Revision(2))");
                    SendQuery.Execute("ALTER TABLE hookups ADD INDEX (TagNumber(32))");

                    query = "CREATE TABLE UDFs (ItemName VARCHAR(32), ";
                    for (int i = 1; i < count; i++)
                        if (i < 10) query += "UDF0" + i + " VARCHAR(128), ";
                        else query += "UDF" + i + " VARCHAR(128), ";
                    query += "UDF" + count + " VARCHAR(32)) ENGINE MyISAM;";
                    SendQuery.Execute(query);
                } catch (SQLException se) {
                    System.out.println("SQL Exception!\n" + se.getMessage());
                    Instrumentation2.console.Echo("SQL Exception!\n" + se.getMessage(), console.DONT_SHOW_JOPTION);
                } finally {
                    try {
                        connection.close();
                        statement.close();
                        System.out.println("Connection closed...");
                    } catch (SQLException sqle) {
                        Instrumentation2.console.Echo("SQL closing exception: \n" + sqle.toString(), console.DONT_SHOW_JOPTION);
                    } catch (NullPointerException npe) {
                        Instrumentation2.console.Echo("Null Pointer Exception:\n" + npe.toString(), console.DONT_SHOW_JOPTION);
                    }
                }
            }
        }

        private class DeleteCurrentProjectThread extends Thread {
            @Override
            public void run() {
                enableButtons(false);
                Instrumentation2.console.Echo("\nDeleting project, please wait...", console.DONT_SHOW_JOPTION);
                DeleteCurrentProject();
                enableButtons(true);
            }

            private void DeleteCurrentProject() {
                try {
                    connection = DriverManager.getConnection(url, user, password);
                    statement = connection.createStatement();

                    ExecutableString DropTable = (qry) -> {
                        try {
                            Instrumentation2.console.Echo("SQL query = " + qry, console.DONT_SHOW_JOPTION);
                            updateResult = statement.executeUpdate(qry);
                            Instrumentation2.console.Echo("SQL server return = " + updateResult, console.DONT_SHOW_JOPTION);
                        } catch (SQLException se) {
                            Instrumentation2.console.Echo("SQL Exception!\n" + se.getMessage(), console.DONT_SHOW_JOPTION);
                        }
                    };

                    DropTable.Execute("DROP TABLE instruments;");
                    DropTable.Execute("DROP TABLE loops;");
                    DropTable.Execute("DROP TABLE pipelines;");
                    DropTable.Execute("DROP TABLE equipment;");
                    DropTable.Execute("DROP TABLE cables;");
                    DropTable.Execute("DROP TABLE jboxes;");
                    DropTable.Execute("DROP TABLE hookups;");
                    DropTable.Execute("DROP TABLE UDFs;");
                } catch (SQLException se) {
                    Instrumentation2.console.Echo("SQL Exception!\n" + se.getMessage(), console.DONT_SHOW_JOPTION);
                } finally {
                    try {
                        connection.close();
                        statement.close();
                        Instrumentation2.console.Echo("Connection closed...", console.DONT_SHOW_JOPTION);
                    } catch (SQLException sqle) {
                        Instrumentation2.console.Echo("SQL closing exception:\n" + sqle.toString(), console.DONT_SHOW_JOPTION);
                    } catch (NullPointerException npe) {
                        Instrumentation2.console.Echo("Null Pointer Exception:\n" + npe.toString(), console.DONT_SHOW_JOPTION);
                    }
                }
            }
        }
    }

    public static void ShowMainWindow() {
        StartWindow.setVisible(true);
    }

    public static void main(String[] args) {
        new Instrumentation2();
    }

    private void tryConnection() {
        Connection conn = null;
        Statement stm = null;

        Date date1 = null;
        Date date2 = null;
        Date date3 = null;
        Date date4 = null;
        Date date5 = null;
        try {
            date1 = new Date();
            System.out.println("Starting at " + date1.getTime());

            conn = DriverManager.getConnection(Instrumentation2.url, Instrumentation2.user, Instrumentation2.password);
            date2 = new Date();
            System.out.println("Connection created at " + date2.getTime());

            stm = conn.createStatement();
            date3 = new Date();
            System.out.println("Statement created at " + date3.getTime());

            ResultSet rset = stm.executeQuery("SELECT * FROM instruments;");
            date4 = new Date();
            System.out.println("Query prepared at " + date4.getTime());

            System.out.println("\n\nConnection time is: " + (date2.getTime() - date1.getTime()) + " ms;");
            System.out.println("Statiment creation time is: " + (date3.getTime() - date2.getTime()) + " ms;");
            System.out.println("Query preparation time is: " + (date4.getTime() - date3.getTime()) + " ms;");

        }
        catch (SQLException se) {
        }
        finally {
            try {
                stm.close();
                conn.close();
                date5 = new Date();
                System.out.println("Connection closing time is: " + (date5.getTime() - date4.getTime()) + " ms;");
            }
            catch (SQLException sql) {}
        }
    }
}