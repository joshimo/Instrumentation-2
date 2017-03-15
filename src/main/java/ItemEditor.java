/**
 * Created by y.golota on 30.01.2017.
 */

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.*;


public class ItemEditor <T> extends Thread {

    T item;

    String itemName;
    String itemCopyName;
    String newItemName;

    boolean isEditable;
    boolean isExcelImported;
    boolean isNew;
    boolean isForDelete;
    boolean isHookupMappingEditing;

    static final boolean DONT_SHOW_JOPTION = false;
    static final boolean SHOW_JOPTION = true;

    Vector data;

    ItemEditor(T item) {
        this.item = item;
        isEditable = setEditable();
        isExcelImported = setExcelImported();
        isNew = setNew();
        isForDelete = setDelete();
        isHookupMappingEditing = setHookupMappingEditing();

        if (isNew) {
            newItemName = JOptionPane.showInputDialog(null, "New item name:");
            if (item instanceof PipeData) {
                this.item = (T) new PipeData(itemName);
            }
            if (item instanceof EquipmentData) {
                this.item = (T) new EquipmentData(itemName);
            }
            if (item instanceof InstrumentData) {
                this.item = (T) new InstrumentData(itemName);
            }
            if (item instanceof LoopData) {
                this.item = (T) new LoopData(itemName);
            }
            if (item instanceof CableData) {
                this.item = (T) new CableData(itemName);
            }
            if (item instanceof JBoxData) {
                this.item = (T) new JBoxData(itemName);
            }
        }
    }

    @Override
    public void run() {
        String listName = "";
        if (isEditable) {
            if (item instanceof PipeData) listName = "Select pipe for edit";
            else if (item instanceof EquipmentData) listName = "Select equipment for edit";
            else if (item instanceof InstrumentData) listName = "Select instrument for edit";
            else if (item instanceof LoopData) listName = "Select loop for edit";
            else if (item instanceof CableData) listName = "Select cable for edit";
            else if (item instanceof JBoxData) listName = "Select junction box for edit";
            else if (item instanceof HookupData) listName = "Select instrument hookup for edit";
        }
        else {
            if (item instanceof PipeData) listName = "Pipe list";
            else if (item instanceof EquipmentData) listName = "Equipment list";
            else if (item instanceof InstrumentData) listName = "Instrument list";
            else if (item instanceof LoopData) listName = "Loop list";
            else if (item instanceof CableData) listName = "Cable list";
            else if (item instanceof JBoxData) listName = "Junction box list";
            else if (item instanceof HookupData) listName = "Hookup list";
        }

        if (isNew) {
            if (item instanceof PipeData) listName = "Select pipe to copy process data:";
            else if (item instanceof EquipmentData) listName = "Select equipment to copy process data:";
            else if (item instanceof InstrumentData) listName = "Select instrument to copy instrument data:";
            else if (item instanceof LoopData) listName = "Select loop to copy loop data:";
            else if (item instanceof CableData) listName = "Select cable to copy cable data:";
            else if (item instanceof JBoxData) listName = "Select junction box to copy jbox data:";
        }

        if (isForDelete) {
            if (item instanceof PipeData) listName = "Select pipes to delete:";
            else if (item instanceof EquipmentData) listName = "Select equipment to delete:";
            else if (item instanceof InstrumentData) listName = "Select instruments to delete:";
            else if (item instanceof LoopData) listName = "Select loops to delete:";
            else if (item instanceof CableData) listName = "Select cables to delete:";
            else if (item instanceof JBoxData) listName = "Select junction boxes to delete:";
        }

        CreateList(listName, item);
    }

    public boolean setEditable() {
        return true;
    }

    public boolean setExcelImported() {
        return false;
    }

    public boolean setNew() {
        return false;
    }

    public boolean setDelete() {
        return false;
    }

    public boolean setHookupMappingEditing() {
        return false;
    }

    void CreateList(String listName, T item) {
        int L = 600;
        int H = 600;

        if (isHookupMappingEditing) {
            System.out.println("hookup");
            Set<String> itemSet = new HashSet<String>();
            itemSet.add("hookup");
            new ItemEditorWindow(itemSet, new HookupData());
            return;
        }

        JFrame frame = new JFrame(listName);
        frame.setSize(L, H);
        frame.setResizable(false);
        frame.setLocation((Instrumentation2.scrWidth - L) / 2, (Instrumentation2.scrHeight - H) / 2);
        frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

        JPanel buttonPanel;
        JButton okButton;
        JButton cancelButton;

        ArrayList<String[]> itemList = new ArrayList<>();

        if (isExcelImported)
            itemList = Instrumentation2.jointData.getItemList(item);
        else {
            InstrumentationWindow.console.Echo("Getting list from database, wait...", DONT_SHOW_JOPTION);
            itemList = Import.getItemListFromDatabase(item);
            InstrumentationWindow.console.Echo("Done", DONT_SHOW_JOPTION);
        }

        Object[] tblHeader;
        Object[][] itemListArray;

        if (isEditable) {
            tblHeader = new Object[]{"Revision", "Item Number", "Add"};
            itemListArray = new Object[itemList.size()][3];
            for (int i = 0; i < itemList.size(); i++) {
                String[] s = itemList.get(i);
                itemListArray[i][0] = s[0];
                itemListArray[i][1] = s[1];
                itemListArray[i][2] = false;
            }
        }
        else {
            tblHeader = new Object[]{"Revision", "Item Number"};
            itemListArray = new Object[itemList.size()][2];
            for (int i = 0; i < itemList.size(); i++) {
                String[] s = itemList.get(i);
                itemListArray[i][0] = s[0];
                itemListArray[i][1] = s[1];
            }
        }

        DefaultTableModel dtm = new DefaultTableModel(itemListArray, tblHeader) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                if (colIndex > 1) return true;
                else return false;
            }
            @Override
            public Class<?> getColumnClass(int col) {
                Vector row = (Vector<Vector<?>>)dataVector.get(0);
                return row.get(col).getClass();
            }
        };

        JTable table = new JTable(dtm);
        table.setAutoResizeMode(0);

        JLabel label = new JLabel(listName);
        label.setBounds(10, 10, L - 20, 25);

        JScrollPane jsp = new JScrollPane(table);
        jsp.setBounds(10, 40, L - 30, H - 150);

        if (isEditable) {
            table.getColumnModel().getColumn(0).setPreferredWidth((int) ((jsp.getWidth() - 20) * 0.15));
            table.getColumnModel().getColumn(1).setPreferredWidth((int) ((jsp.getWidth() - 20) * 0.7));
            table.getColumnModel().getColumn(2).setPreferredWidth((int) ((jsp.getWidth() - 20) * 0.15));
            table.setRowHeight(20);

            buttonPanel = new JPanel();
            okButton = new JButton("Ok");
            cancelButton = new JButton("Cancel");
            buttonPanel.setBounds(10, H - 80, L - 20, 50);
            buttonPanel.setLayout(new FlowLayout());
            buttonPanel.add(okButton);
            buttonPanel.add(cancelButton);
            frame.add(buttonPanel);

            okButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Set<String> itemSet = new HashSet<String>();
                    for (int i = 0; i < table.getRowCount(); i++)
                        if ((boolean) table.getValueAt(i, 2)) {
                            itemSet.add((String) table.getValueAt(i, 1));
                            itemCopyName = (String) table.getValueAt(i, 1);
                        }

                    if (isForDelete) {
                        InstrumentationWindow.console.Echo("Requesting item deleting confirmation", DONT_SHOW_JOPTION);
                        int choice = JOptionPane.showConfirmDialog(null, "Selected items will be deleted. Are you sure?");
                        if (choice == JOptionPane.OK_OPTION) {
                            InstrumentationWindow.console.Echo("User choice is OK", DONT_SHOW_JOPTION);
                            frame.setVisible(false);
                            Export.DeleteItemsFromDatabase(itemSet, item);
                        }
                        else {
                            InstrumentationWindow.console.Echo("User choice is CANCEL", DONT_SHOW_JOPTION);
                        }
                    }
                    else {
                        if (isNew) {
                            if (itemSet.size() > 1) {
                                JOptionPane.showMessageDialog(null, "Please select only one item to copy data");
                            } else {
                                frame.setVisible(false);
                                new ItemEditorWindow(itemSet, item);
                            }
                        } else {
                            frame.setVisible(false);
                            new ItemEditorWindow(itemSet, item);
                        }
                    }
                }
            });
            cancelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    frame.setVisible(false);
                }
            });
        }
        else {
            table.getColumnModel().getColumn(0).setPreferredWidth((int) ((jsp.getWidth() - 20) * 0.25));
            table.getColumnModel().getColumn(1).setPreferredWidth((int) ((jsp.getWidth() - 20) * 0.75));
            table.setRowHeight(22);
        }

        frame.setLayout(null);
        frame.add(label);
        frame.add(jsp);
        frame.setVisible(true);
    }

    private class ItemEditorWindow extends JFrame {
        int L = 1024;
        int H = 820;

        JTable table;
        JScrollPane tablePanel;
        JButton saveButton;
        JButton cancelButton;
        JPanel buttonPanel;
        JTextPane textPanel;

        String[][] tableDataOrigin;
        String[][] tableDataEdited;
        String[][] itemData;
        DataContainer dc;
        ArrayList<String[][]> itemDataMatrix = new ArrayList<>();
        ArrayList<String[][]> itemDataMatrixEdited = new ArrayList<>();

        Set<String> itemSet;
        Set<String> newItemSet;

        <T> ItemEditorWindow (Set<String> itemSet, T item) {
            super("Item Editor");
            this.setSize(L, H);
            this.setLocation((Instrumentation2.scrWidth - L) / 2, (Instrumentation2.scrHeight - H) / 2);
            this.setDefaultCloseOperation(HIDE_ON_CLOSE);

            Resizable window = (L, H) -> {
                textPanel.setBounds(10, 10, L - 30, 60);
                buttonPanel.setBounds(10, H - 80, L - 20, 50);
                tablePanel.setBounds(10, 100, L - 30, H - 200);
                table.getColumnModel().getColumn(0).setPreferredWidth((int) (0.15 * (L - 50)));
                table.getColumnModel().getColumn(1).setPreferredWidth((int) (0.35 * (L - 50)));
                table.getColumnModel().getColumn(2).setPreferredWidth((int) (0.50 * (L - 50)));
                table.setRowHeight(24);
            };

            this.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    super.componentResized(e);
                    L = getWidth();
                    H = getHeight();
                    window.Resize(L, H);
                }
                public void componentHidden(ComponentEvent e) {

                }
            });

            this.itemSet = itemSet;
            for(String s : this.itemSet) System.out.println("ItemEditorWindow: " + s);

            String[] tableHeader = {"Field information", "Field name", "Field Value"};
            getItemFields(this.itemSet, item);

            DefaultTableModel dtm = new DefaultTableModel(tableDataOrigin, tableHeader) {
                @Override
                public boolean isCellEditable(int rowIndex, int colIndex) {
                    if (colIndex == 0) return false;
                    if (getValueAt(rowIndex, 0).equals("constant") & (colIndex != 2)) return false;
                    else return true;
                }
            };

            table = new JTable(dtm);
            table.setDefaultRenderer(Object.class, new TableRenderer(tableDataOrigin));
            table.setAutoResizeMode(0);

            table.getModel().addTableModelListener(new TableModelListener() {
                @Override
                public void tableChanged(TableModelEvent e) {
                    int row = table.getEditingRow();
                    int col = table.getEditingColumn();
                    String value = (String) table.getValueAt(row, col);
                    tableDataEdited[row][col] = value;
                }
            });

            saveButton = new JButton("Save");
            cancelButton = new JButton("Cancel");

            saveButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    saveButton.setEnabled(false);
                    cancelButton.setEnabled(false);
                    InstrumentationWindow.console.Echo("Asking for saving items changes", InstrumentationWindow.console.DONT_SHOW_JOPTION);
                    int choice = JOptionPane.showConfirmDialog(null, "The items data will be changed. Are you sure?");
                    if (choice == JOptionPane.OK_OPTION) {
                        InstrumentationWindow.console.Echo("User`s choice is OK", InstrumentationWindow.console.DONT_SHOW_JOPTION);
                        ApplyItemChanges(item);
                    }
                    else InstrumentationWindow.console.Echo("User`s choice is CANCEL", InstrumentationWindow.console.DONT_SHOW_JOPTION);
                    saveButton.setEnabled(true);
                    cancelButton.setEnabled(true);
                }
            });

            cancelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ItemEditorWindow.this.setVisible(false);
                }
            });

            Object[] s = itemSet.toArray();
            Arrays.sort(s);
            String label = "";
            for (Object tag : s) label += tag + ";  ";

            textPanel = new JTextPane();
            textPanel.setEditable(false);
            textPanel.setFont(new Font(null, Font.BOLD, 14));
            textPanel.setText(label);

            tablePanel = new JScrollPane(table);

            buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout());
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);

            window.Resize(L, H);

            this.setDefaultCloseOperation(HIDE_ON_CLOSE);
            this.setLayout(null);
            this.add(tablePanel);
            this.add(textPanel);
            this.add(buttonPanel);
            this.setVisible(true);

        }

        private <T> void getItemFields(Set<String> itemSet, T item) {

            T itemData;

            if (isHookupMappingEditing) {
                this.itemData = Import.getHookupMappingFromDatabase();
                this.tableDataOrigin = this.itemData.clone();
                this.tableDataEdited = this.itemData.clone();

                for (int i=0; i<=64; i++) System.out.println(this.itemData[i][0] + ", " + this.itemData[i][1]+ ", " + this.itemData[i][2]);
                itemDataMatrix.add(this.itemData);
                return;
            }

            if (isNew) {

                dc = new DataContainer();

                itemData = Import.getItemFromDatabase(itemCopyName, item);
                if (item instanceof PipeData) {
                    PipeData pd = (PipeData) itemData;
                    pd.setPipeName(newItemName);
                    this.itemData = pd.getDataTable();
                    dc.PutData(pd);
                }
                if (item instanceof EquipmentData) {
                    EquipmentData ed = (EquipmentData) itemData;
                    ed.setEquipmentName(newItemName);
                    this.itemData = ed.getDataTable();
                    for (String[] s: this.itemData) {
                        System.out.println(s[0] + ", " + s[1] + ", " + s[2]);
                    }
                    dc.PutData(ed);
                }
                if (item instanceof InstrumentData) {
                    InstrumentData id = (InstrumentData) itemData;
                    id.setTagNumber(newItemName);
                    System.out.println(newItemName);
                    this.itemData = id.getDataTable();
                    for (String[] s: this.itemData) {
                        System.out.println(s[0] + ", " + s[1] + ", " + s[2]);
                    }
                    dc.PutData(id);
                }
                if (item instanceof LoopData) {
                    LoopData ld = (LoopData) itemData;
                    ld.setLoopNumber(newItemName);
                    this.itemData = ld.getDataTable();
                    dc.PutData(ld);
                }
                if (item instanceof CableData) {
                    CableData cd = (CableData) itemData;
                    cd.setCableName(newItemName);
                    this.itemData = cd.getDataTable();
                    dc.PutData(cd);
                }
                if (item instanceof JBoxData) {
                    JBoxData jd = (JBoxData) itemData;
                    jd.setJBoxName(newItemName);
                    this.itemData = jd.getDataTable();
                    dc.PutData(jd);
                }

                itemDataMatrix.add(this.itemData);
            }

            else {

                dc = Import.getItemsFromDatabase(itemSet, item);

                if (itemSet != null)
                    for (String in : itemSet) {
                        if (item instanceof PipeData) {
                            this.itemDataMatrix.add(dc.GetData(in, new PipeData()).getDataTable());
                        } else if (item instanceof EquipmentData) {
                            this.itemDataMatrix.add(dc.GetData(in, new EquipmentData()).getDataTable());
                        } else if (item instanceof InstrumentData) {
                            this.itemDataMatrix.add(dc.GetData(in, new InstrumentData()).getDataTable());
                        } else if (item instanceof LoopData) {
                            this.itemDataMatrix.add(dc.GetData(in, new LoopData()).getDataTable());
                        } else if (item instanceof CableData) {
                            this.itemDataMatrix.add(dc.GetData(in, new CableData()).getDataTable());
                        } else if (item instanceof JBoxData) {
                            this.itemDataMatrix.add(dc.GetData(in, new JBoxData()).getDataTable());
                        } else if (item instanceof HookupData) {
                            this.itemDataMatrix.add(dc.GetData(in, new HookupData()).getDataTable());
                        }
                    }

                try {
                    this.itemData = this.itemDataMatrix.get(0).clone();
                } catch (NullPointerException npe) {
                    InstrumentationWindow.console.Echo("Null pointer exception in 'getItemFields()'");
                } catch (IndexOutOfBoundsException obe) {
                    InstrumentationWindow.console.Echo("Items not selected!", InstrumentationWindow.console.SHOW_JOPTION);
                }
            }

            this.tableDataOrigin = new String[this.itemData.length][3];
            this.tableDataEdited = new String[this.itemData.length][3];

            for (int counter = 0; counter < this.itemDataMatrix.size(); counter++) {
                for (int i = 0; i < this.tableDataOrigin.length; i++) {
                    String[][] currentItem = this.itemDataMatrix.get(counter);
                    try {
                        this.tableDataOrigin[i][0] = this.itemData[i][0];
                        this.tableDataEdited[i][0] = this.itemData[i][0];

                        if (this.itemData[i][1].equals(currentItem[i][1])) {
                            this.tableDataOrigin[i][1] = this.itemData[i][1];
                            this.tableDataEdited[i][1] = this.itemData[i][1];
                        } else {
                            this.tableDataOrigin[i][1] = "[different values]";
                            this.tableDataEdited[i][1] = "[different values]";
                        }
                    } catch (NullPointerException npe) {
                    }

                    try {
                        if (this.itemData[i][2].equals(currentItem[i][2])) {
                            this.tableDataOrigin[i][2] = this.itemData[i][2];
                            this.tableDataEdited[i][2] = this.itemData[i][2];
                        } else {
                            this.tableDataOrigin[i][2] = "[different values]";
                            this.tableDataEdited[i][2] = "[different values]";
                        }
                    }
                    catch (NullPointerException npe) {
                    }
                }
            }

        }

        private <T> void ApplyItemChanges(T item) {

            for (int ic = 0; ic < itemDataMatrix.size(); ic ++) {
                String[][] itemData = new String[tableDataEdited.length][3];

                for (int i = 0; i < tableDataEdited.length; i ++) {
                    try {
                        itemData[i][0] = tableDataEdited[i][0];

                        if (!tableDataEdited[i][1].equals("[different values]"))
                            itemData[i][1] = tableDataEdited[i][1];
                        else
                            itemData[i][1] = itemDataMatrix.get(ic)[i][1];

                        if (!tableDataEdited[i][2].equals("[different values]"))
                            itemData[i][2] = tableDataEdited[i][2];
                        else
                            itemData[i][2] = itemDataMatrix.get(ic)[i][2];
                    }
                    catch (NullPointerException npe) {}
                }

                itemDataMatrixEdited.add(itemData);
            }

            DataContainer data = new DataContainer();

            if (item instanceof PipeData) {
                if (!isNew) {
                    for (String[][] s : itemDataMatrixEdited) {
                        PipeData pd = new PipeData(s);
                        data.PutData(pd);
                    }
                    Export.AddPipesToDatabase(itemSet, data);
                }
                else {
                    Set<String> set = new HashSet<>();
                    set.add(newItemName);
                    PipeData pd = new PipeData(itemDataMatrixEdited.get(0));
                    data.PutData(pd);
                    Export.AddPipesToDatabase(set, data);
                }
            }
            if (item instanceof EquipmentData) {
                if (!isNew) {
                    for (String[][] s : itemDataMatrixEdited) {
                        EquipmentData ed = new EquipmentData(s);
                        data.PutData(ed);
                    }
                    Export.AddEquipmentToDatabase(itemSet, data);
                }
                else {
                    Set<String> set = new HashSet<>();
                    set.add(newItemName);
                    EquipmentData ed = new EquipmentData(itemDataMatrixEdited.get(0));
                    data.PutData(ed);
                    Export.AddEquipmentToDatabase(set, data);
                }
            }
            if (item instanceof InstrumentData) {
                if (!isNew) {
                    for (String[][] s : itemDataMatrixEdited) {
                        InstrumentData id = new InstrumentData(s);
                        data.PutData(id);
                    }
                    Export.AddInstrumentsToDatabase(itemSet, data);
                }
                else {
                    Set<String> set = new HashSet<>();
                    set.add(newItemName);
                    InstrumentData id = new InstrumentData(itemDataMatrixEdited.get(0));
                    data.PutData(id);
                    Export.AddInstrumentsToDatabase(set, data);
                }
            }
            if (item instanceof LoopData) {
                if (!isNew) {
                    for (String[][] s : itemDataMatrixEdited) {
                        LoopData ld = new LoopData(s);
                        data.PutData(ld);
                    }
                    Export.AddLoopsToDatabase(itemSet, data);
                }
                else {
                    Set<String> set = new HashSet<>();
                    set.add(newItemName);
                    LoopData ld = new LoopData(itemDataMatrixEdited.get(0));
                    data.PutData(ld);
                    Export.AddLoopsToDatabase(set, data);
                }
            }
            if (item instanceof CableData) {
                if (!isNew) {
                    for (String[][] s : itemDataMatrixEdited) {
                        CableData cd = new CableData(s);
                        data.PutData(cd);
                    }
                    Export.AddCablesToDatabase(itemSet, data);
                }
                else {
                    Set<String> set = new HashSet<>();
                    set.add(newItemName);
                    CableData cd = new CableData(itemDataMatrixEdited.get(0));
                    data.PutData(cd);
                    Export.AddCablesToDatabase(set, data);
                }
            }
            if (item instanceof JBoxData) {
                if (!isNew) {
                    for (String[][] s : itemDataMatrixEdited) {
                        JBoxData jd = new JBoxData(s);
                        data.PutData(jd);
                    }
                    Export.AddJBoxesToDatabase(itemSet, data);
                }
                else {
                    Set<String> set = new HashSet<>();
                    set.add(newItemName);
                    JBoxData jd = new JBoxData(itemDataMatrixEdited.get(0));
                    data.PutData(jd);
                    Export.AddJBoxesToDatabase(set, data);
                }
            }
            if (item instanceof HookupData) {
                for (String[][] s : itemDataMatrixEdited) {
                    HookupData hd = new HookupData(s);
                    data.PutData(hd);
                }
                Export.AddHookupsToDatabase(itemSet, data);
            }
        }
    }

    class TableRenderer extends JLabel implements TableCellRenderer {

        String[][] data;

        public TableRenderer(String[][] data) {
            this.data = data;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

            if (isSelected) {
                setOpaque(true);
                setBackground(new Color(200, 200, 250));
            }
            else
                setOpaque(false);

            setText((String) value);

            try {
                if (table.isCellEditable(row, column)) {
                    setOpaque(false);
                    try {
                        if (!data[row][column].equals(value)) {
                            setOpaque(true);
                            setBackground(new Color(225, 200, 200));
                        }
                    }
                    catch (NullPointerException npe) {
                        if (value != null) {
                            setOpaque(true);
                            setBackground(new Color(225, 200, 200));
                        }
                    }
                }
                else {
                    setOpaque(true);
                    setBackground(new Color(225, 225, 225));
                }
                if (value.toString().equals("[different values]")) {
                    setFont(new Font(null, Font.ITALIC, 12));
                    setForeground(new Color(150, 150, 150));
                }
                else {
                    setFont(new Font(null, Font.PLAIN, 12));
                    setForeground(new Color(0, 0, 0));
                }
            }
            catch (NullPointerException npe) {}

            return this;
        }
    }
}