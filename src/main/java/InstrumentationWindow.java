/**
 * Created by y.golota on 03.01.2017.
 */

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;

public class InstrumentationWindow extends JFrame {

    static int L = 1280;
    static int H = 720;

    static Connection connection;

    Menu menuBar = new Menu();
    static Console console = new Console(0, L - 340, H - 20, 300);

    public static boolean showQueries = true;
    public static boolean showExceptions = true;

    final static boolean DONT_SHOW_JOPTION = false;

    static ConnectionWindow connectionWindow = new ConnectionWindow();

    public static void CreateConnection() {
        try {

            connectionWindow.setVisible(true);
            connection = DriverManager.getConnection(Instrumentation2.url, Instrumentation2.user, Instrumentation2.password);
            connectionWindow.setVisible(false);
        }
        catch (SQLException sqle) {
            console.Echo("SQL Exception!\n" + sqle.getMessage(), DONT_SHOW_JOPTION);
            try {
                connection.close();
                if (connection.isClosed()) InstrumentationWindow.console.Echo("\nConnection closed!", DONT_SHOW_JOPTION);
                connectionWindow.setVisible(false);
            }
            catch (SQLException sqlee) {
                InstrumentationWindow.console.Echo("\nSQL closing exception:\n" + sqlee.toString(), DONT_SHOW_JOPTION);
                connectionWindow.setVisible(false);
            }
            catch (NullPointerException npe) {
                InstrumentationWindow.console.Echo("\nNull Pointer Exception:\n" + npe.toString(), DONT_SHOW_JOPTION);
                connectionWindow.setVisible(false);
            }
        }
    }

    public static void CheckConnection() {
        try {
            if (connection.isClosed()) CreateConnection();
        }
        catch (NullPointerException npe) {
            console.Echo("Connection does not exist. Trying to reconnect...", DONT_SHOW_JOPTION);
            CreateConnection();
        }
        catch (SQLException sqle) {
            console.Echo("SQL Exception!\n" + sqle.getMessage(), DONT_SHOW_JOPTION);
        }
    }

    public InstrumentationWindow() {
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                L = getWidth();
                H = getHeight();
                InitWindow();
            }
            public void componentHidden(ComponentEvent e) {
                Instrumentation2.ShowMainWindow();
            }
        });
        CreateConnection();
        InitWindow();
    }

    private void InitWindow() {
        this.setName("Instrumentation project");
        this.setSize(L, H);
        this.setLocation((Instrumentation2.scrWidth - L) / 2, (Instrumentation2.scrHeight - H) / 2);
        this.setResizable(true);

        menuBar.setBounds(0, 0, L, 25);
        console.setBounds(0, H - 340, L - 20, 300);

        this.setLayout(null);
        this.add(menuBar);
        this.add(console);
        this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        this.setVisible(true);
    }

    private class Menu extends JMenuBar implements ActionListener {
        Menu() {
            String[][] m = {
                    {"Instrumentation", "Exit"},
                    {"Import", "Import pipes from Excel", "Import equipment from Excel", "Import instruments from Excel", "Import hookups mapping from Excel",
                               "Show imported pipe list", "Show imported equipment list", "Show imported instrument list", "Show imported loop list"},
                    {"Export", "Export pipe data to database", "Export equipment data to database", "Export instruments to database", "Export loop data to database"},
                    {"Show", "Show pipe list", "Show equipment list", "Show instrument list", "Show loop list", "Show cable list", "Show junction box list"},
                    {"Edit", "Edit pipes", "Edit equipment", "Edit instruments", "Edit loops", "Edit cables", "Edit junction boxes", "Edit instrument hookups", "Edit hookups mapping"},
                    {"Create", "Create pipe", "Create equipment", "Create instrument", "Create loop", "Create cable", "Create junction box", "<Create instrument hookups>"},
                    {"Delete", "Delete pipes", "Delete equipment", "Delete instruments", "Delete loops", "Delete cables", "Delete junction boxes"},
                    {"Make", "Instrument datasheet list"},
                    {"About", "About..."}
            };

            JMenu[] menu = new JMenu[m.length];

            for (int i = 0; i < m.length; i ++) {
                menu[i] = new JMenu(m[i][0]);
                for (int j = 1; j < m[i].length; j ++) {
                    JMenuItem mi = new JMenuItem(m[i][j]);
                    if (m[i][j].contains("Show imported pipe list")
                            | m[i][j].contains("Create instrument hookups")
                            | m[i][j].contains("Import hookups mapping from Excel")
                            | m[i][j].contains("Edit hookups mapping from Excel")
                            | m[i][j].contains("Edit hookups mapping")) menu[i].addSeparator();
                    mi.addActionListener(this);
                    menu[i].add(mi);
                }
                this.add(menu[i]);
            }
        }

        public void actionPerformed(ActionEvent ae) {
            switch (ae.getActionCommand()) {

                case "Import pipes from Excel": {
                    JFileChooser openFile = new JFileChooser();
                    FileNameExtensionFilter filter = new FileNameExtensionFilter("MS Excel files", "xls");
                    openFile.setFileFilter(filter);
                    int result = openFile.showDialog(null, "Choose Excel file for pipe data import:");
                    if (result == JFileChooser.APPROVE_OPTION) Instrumentation2.pipeImportXLSFilename = openFile.getSelectedFile().getAbsolutePath();
                    Instrumentation2.pipeData = Import.ImportPipeDataFromXLS(Instrumentation2.pipeImportXLSFilename);
                    Instrumentation2.jointData.PutContainerData(Instrumentation2.pipeData.GetContainerData(new PipeData()), new PipeData());
                }
                break;
                case "Import equipment from Excel": {
                    JFileChooser openFile = new JFileChooser();
                    FileNameExtensionFilter filter = new FileNameExtensionFilter("MS Excel files", "xls");
                    openFile.setFileFilter(filter);
                    int result = openFile.showDialog(null, "Choose Excel file equipment data import:");
                    if (result == JFileChooser.APPROVE_OPTION) Instrumentation2.equipmentImportXLSFilename = openFile.getSelectedFile().getAbsolutePath();
                    Instrumentation2.equipmentData = Import.ImportEquipmentDataFromXLS(Instrumentation2.equipmentImportXLSFilename);
                    Instrumentation2.jointData.PutContainerData(Instrumentation2.equipmentData.GetContainerData(new EquipmentData()), new EquipmentData());
                }
                break;
                case "Import instruments from Excel": {
                    JFileChooser openFile = new JFileChooser();
                    FileNameExtensionFilter filter = new FileNameExtensionFilter("MS Excel files", "xls");
                    openFile.setFileFilter(filter);
                    int result = openFile.showDialog(null, "Choose Excel file instruments data import:");
                    if (result == JFileChooser.APPROVE_OPTION) Instrumentation2.instrumentImportXLSFilename = openFile.getSelectedFile().getAbsolutePath();
                    Instrumentation2.instrumentsData = Import.ImportInstrumentsFromXLS(Instrumentation2.instrumentImportXLSFilename);
                    Instrumentation2.jointData.PutContainerData(Instrumentation2.instrumentsData.GetContainerData(new InstrumentData()), new InstrumentData());
                    Instrumentation2.jointData.GenerateLoopsFromInstruments();
                }
                break;
                case "Import hookups mapping from Excel": {
                    JFileChooser openFile = new JFileChooser();
                    FileNameExtensionFilter filter = new FileNameExtensionFilter("MS Excel files", "xls");
                    openFile.setFileFilter(filter);
                    int result = openFile.showDialog(null, "Choose Excel file instruments data import:");
                    if (result == JFileChooser.APPROVE_OPTION) Instrumentation2.instrumentImportXLSFilename = openFile.getSelectedFile().getAbsolutePath();
                    Import.ImportHookupFormFromXLS(Instrumentation2.hookupMappingFilename);
                }
                break;


                case "Show imported pipe list":
                    new ItemEditor(new PipeData()) {
                        @Override
                        public boolean setExcelImported() {
                            return true;
                        }
                        @Override
                        public boolean setEditable() {
                            return false;
                        }
                    }.start();
                    break;
                case "Show imported equipment list":
                    new ItemEditor(new EquipmentData()) {
                        @Override
                        public boolean setExcelImported() {
                            return true;
                        }
                        @Override
                        public boolean setEditable() {
                            return false;
                        }
                    }.start();
                    break;
                case "Show imported instrument list": {
                    new ItemEditor(new InstrumentData()) {
                        @Override
                        public boolean setExcelImported() {
                            return true;
                        }
                        @Override
                        public boolean setEditable() {
                            return false;
                        }
                    }.start();
                }
                break;
                case "Show imported loop list": {
                    new ItemEditor(new LoopData()) {
                        @Override
                        public boolean setExcelImported() {
                            return true;
                        }
                        @Override
                        public boolean setEditable() {
                            return false;
                        }
                    }.start();
                }
                break;


                case "Show pipe list": {
                    new ItemEditor(new PipeData()) {
                        @Override
                        public boolean setEditable() {
                            return false;
                        }
                    }.start();
                }
                break;
                case "Show equipment list": {
                    new ItemEditor(new EquipmentData()) {
                        @Override
                        public boolean setEditable() {
                            return false;
                        }
                    }.start();
                }
                break;
                case "Show instrument list": {
                    new ItemEditor(new InstrumentData()) {
                        @Override
                        public boolean setEditable() {
                            return false;
                        }
                    }.start();
                }
                break;
                case "Show loop list": {
                    new ItemEditor(new LoopData()) {
                        @Override
                        public boolean setEditable() {
                            return false;
                        }
                    }.start();
                }
                break;
                case "Show cable list": {
                    new ItemEditor(new CableData()) {
                        @Override
                        public boolean setEditable() {
                            return false;
                        }
                    }.start();
                }
                break;
                case "Show junction box list": {
                    new ItemEditor(new JBoxData()) {
                        @Override
                        public boolean setEditable() {
                            return false;
                        }
                    }.start();
                }
                break;

                case "Edit pipes": {
                    Thread Editor = new ItemEditor(new PipeData());
                    Editor.start();
                }
                break;
                case "Edit equipment": {
                    Thread Editor = new ItemEditor(new EquipmentData());
                    Editor.start();
                }
                break;
                case "Edit instruments": {
                    Thread Editor = new ItemEditor(new InstrumentData());
                    Editor.start();
                }
                break;
                case "Edit loops": {
                    Thread Editor = new ItemEditor(new LoopData());
                    Editor.start();
                }
                break;
                case "Edit cables": {
                    Thread Editor = new ItemEditor(new CableData());
                    Editor.start();
                }
                break;
                case "Edit junction boxes": {
                    Thread Editor = new ItemEditor(new JBoxData());
                    Editor.start();
                }
                break;
                case "Edit instrument hookups": {
                    Thread Editor = new ItemEditor(new HookupData());
                    Editor.start();
                }
                break;
                case "Edit hookups mapping": {
                    Thread Editor = new ItemEditor(new HookupData()) {
                        @Override
                        public boolean setHookupMappingEditing() {
                            return true;
                        }
                    };
                    Editor.start();
                }
                break;

                case "Create pipe": {
                    Thread Editor = new ItemEditor(new PipeData()){
                        @Override
                        public boolean setNew() {
                            return true;
                        }
                    };
                    Editor.start();
                }
                break;
                case "Create equipment": {
                    Thread Editor = new ItemEditor(new EquipmentData()){
                        @Override
                        public boolean setNew() {
                            return true;
                        }
                    };
                    Editor.start();
                }
                break;
                case "Create instrument": {
                    Thread Editor = new ItemEditor(new InstrumentData()) {
                        @Override
                        public boolean setNew() {
                            return true;
                        }
                    };
                    Editor.start();
                }
                break;
                case "Create loop": {
                    Thread Editor = new ItemEditor(new LoopData()) {
                        @Override
                        public boolean setNew() {
                            return true;
                        }
                    };
                    Editor.start();
                }
                break;
                case "Create cable": {
                    Thread Editor = new ItemEditor(new CableData()) {
                        @Override
                        public boolean setNew() {
                            return true;
                        }
                    };
                    Editor.start();
                }
                break;
                case "Create junction box": {
                    Thread Editor = new ItemEditor(new JBoxData()) {
                        @Override
                        public boolean setNew() {
                            return true;
                        }
                    };
                    Editor.start();
                }
                break;

                case "Delete pipes": {
                    Thread Editor = new ItemEditor(new PipeData()){
                        @Override
                        public boolean setDelete() {
                            return true;
                        }
                    };
                    Editor.start();
                }
                break;
                case "Delete equipment": {
                    Thread Editor = new ItemEditor(new EquipmentData()){
                        @Override
                        public boolean setDelete() {
                            return true;
                        }
                    };
                    Editor.start();
                }
                break;
                case "Delete instruments": {
                    Thread Editor = new ItemEditor(new InstrumentData()) {
                        @Override
                        public boolean setDelete() {
                            return true;
                        }
                    };
                    Editor.start();
                }
                break;
                case "Delete loops": {
                    Thread Editor = new ItemEditor(new LoopData()) {
                        @Override
                        public boolean setDelete() {
                            return true;
                        }
                    };
                    Editor.start();
                }
                break;
                case "Delete cables": {
                    Thread Editor = new ItemEditor(new CableData()) {
                        @Override
                        public boolean setDelete() {
                            return true;
                        }
                    };
                    Editor.start();
                }
                break;
                case "Delete junction boxes": {
                    Thread Editor = new ItemEditor(new JBoxData()) {
                        @Override
                        public boolean setDelete() {
                            return true;
                        }
                    };
                    Editor.start();
                }
                break;


                case "Instrument datasheet list": {
                    JFileChooser openFile = new JFileChooser();
                    openFile.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    int result = openFile.showDialog(null, "Choose folder for export:");
                    if (result == JFileChooser.APPROVE_OPTION)
                        if (openFile.getSelectedFile().isDirectory())
                            Instrumentation2.instrumentExportXLSFilename = openFile.getSelectedFile().getAbsolutePath() + "/InstrumentExport.xls";
                        else
                            Instrumentation2.instrumentExportXLSFilename = openFile.getSelectedFile().getAbsolutePath();
                    System.out.println(Instrumentation2.instrumentExportXLSFilename);
                    Set<String> instruments = Instrumentation2.jointData.getList(new InstrumentData());
                    Export.SaveInstrumentsToXLS(Instrumentation2.instrumentExportXLSFilename, instruments, Instrumentation2.jointData);
                }
                break;

                case "Export instruments to database": {
                    Export.AddInstrumentsToDatabase(Instrumentation2.jointData.getList(new InstrumentData()), Instrumentation2.jointData);
                }
                break;
                case "Export pipe data to database": {
                    Export.AddPipesToDatabase(Instrumentation2.jointData.getList(new PipeData()), Instrumentation2.jointData);
                }
                break;
                case "Export equipment data to database": {
                    Export.AddEquipmentToDatabase(Instrumentation2.jointData.getList(new EquipmentData()), Instrumentation2.jointData);
                }
                break;
                case "Export loop data to database": {
                    Instrumentation2.jointData.GenerateLoopsFromInstruments();
                    Export.AddLoopsToDatabase(Instrumentation2.jointData.getList(new LoopData()), Instrumentation2.jointData);
                }
                break;


                case "Exit":
                    InstrumentationWindow.this.setVisible(false);
                    break;
                case "About...":
                    About();
                    break;
            }
        }
    }

    private static class ConnectionWindow extends JFrame {
        ConnectionWindow() {
            super("Connecting to DataBase, wait...");
            this.setSize(320, 80);
            this.setLocation((Instrumentation2.scrWidth - 160)/2, (Instrumentation2.scrHeight - 100)/2);
            this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
            this.setResizable(false);
            JLabel label = new JLabel(" Connecting to DataBase, wait...");
            this.getContentPane().add(label, BorderLayout.CENTER);
            this.setVisible(false);
        }
    }

    private void About() {
        JFrame Frame = new JFrame("About");
        Frame.setSize(240, 160);
        Frame.setLocation((Instrumentation2.scrWidth - 240) / 2, (Instrumentation2.scrHeight - 160) / 2);
        Frame.setResizable(false);
        Frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        JLabel l = new JLabel("Created by Y.Golota on 07.09.2016.");
        Frame.add(l);
        Frame.setVisible(true);
    }
}