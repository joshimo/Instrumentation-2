/**
 * Created by y.golota on 03.01.2017.
 */

import java.io.*;
import java.sql.*;
import java.util.*;
import java.lang.*;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;

import javax.swing.*;

public class Import {

    private static Statement stm;
    private static ResultSet rset;
    private static int updateResult;

    static final boolean DONT_SHOW_JOPTION = false;
    static final boolean SHOW_JOPTION = true;

    private static void CloseConnection(Statement statement) {
        try {
            statement.close();
            System.out.println("Connection closed...");
        }
        catch (SQLException sqle) {
            InstrumentationWindow.console.Echo("\nSQL closing exception:\n" + sqle.toString(), DONT_SHOW_JOPTION);
        }
        catch (NullPointerException npe) {
            InstrumentationWindow.console.Echo("\nNull Pointer Exception:\n" + npe.toString(), DONT_SHOW_JOPTION);
        }
    }

    static boolean tryPreffix(String name) {
        boolean b = true;
        try {
            int i = Integer.parseInt(name.split("-")[0]);
        }
        catch(NumberFormatException e) {
            b = false;
        }
        return b;
    }

    static DataContainer ImportPipeDataFromXLS(String filename) {
        File file = new File(filename);
        DataContainer pd = new DataContainer();

        InstrumentationWindow.console.Echo("Starting pipe data import from xls", DONT_SHOW_JOPTION);

        try {
            FileInputStream stream = new FileInputStream(file);
            HSSFWorkbook Book = new HSSFWorkbook(stream);
            HSSFSheet sheet = Book.getSheetAt(0);
            HSSFRow row = sheet.getRow(sheet.getFirstRowNum());

            int firstRN = sheet.getFirstRowNum();
            int firstCN = row.getFirstCellNum();
            int lastCN = row.getLastCellNum();
            int lastRN = sheet.getLastRowNum();

            for (int s = firstRN + 2; s <= lastRN; s ++) {

                String pipename = sheet.getRow(s).getCell(firstCN + 2).toString();
                PipeData pipe = new PipeData(pipename);
                InstrumentationWindow.console.Echo("Importing pipe " + pipename + ", " + (s - firstRN - 1) + " from " + (lastRN - firstRN - 1), DONT_SHOW_JOPTION);

                pipe.setRevision(sheet.getRow(s).getCell(firstCN + 1).toString());
                pipe.setGeneralNotes(sheet.getRow(s).getCell(lastCN - 1).toString());
                for (int i = firstCN + 3; i < lastCN - 1; i ++) {
                    String UDF = sheet.getRow(firstRN).getCell(i).toString();
                    int UDFnum = 0;
                    try {
                        UDFnum = Integer.parseInt(UDF.split("DF")[1]);
                    }
                    catch (ArrayIndexOutOfBoundsException e) {
                        InstrumentationWindow.console.Echo("Field " + UDF + " for pipe " + sheet.getRow(s).getCell(firstCN + 1).toString() + " is ignored!", SHOW_JOPTION);
                    }
                    if (UDFnum < DataContainer.N) {
                        pipe.setUDF(UDFnum, sheet.getRow(firstRN + 1).getCell(i).toString(), sheet.getRow(s).getCell(i).toString());
                        if (sheet.getRow(s).getCell(i).toString().isEmpty())
                            pipe.setUDFVisibility(UDFnum, false);
                        else pipe.setUDFVisibility(UDFnum, true);
                    }
                    else {
                        InstrumentationWindow.console.Echo("Field " + UDF + " for pipe " + sheet.getRow(s).getCell(firstCN + 1).toString() + " is out of bounds and ignored!", SHOW_JOPTION);
                    }
                }
                pd.PutData(pipe);
            }
        }
        catch (FileNotFoundException e) {
            InstrumentationWindow.console.Echo("Pipe import file " + filename.split("/")[filename.split("/").length - 1] + " not found!", SHOW_JOPTION);
        }
        catch (OfficeXmlFileException e) {
            InstrumentationWindow.console.Echo("Wrong pipe import file format!", SHOW_JOPTION);
        }
        catch (Exception e) {
            InstrumentationWindow.console.Echo("Unknown error in ImportPipeDataFromXLS!", SHOW_JOPTION);
        }
        return pd;
    }

    static DataContainer ImportEquipmentDataFromXLS(String filename) {
        File file = new File(filename);
        DataContainer pd = new DataContainer();

        InstrumentationWindow.console.Echo("Starting equipment data import from xls", DONT_SHOW_JOPTION);

        try {
            FileInputStream stream = new FileInputStream(file);
            HSSFWorkbook Book = new HSSFWorkbook(stream);
            HSSFSheet sheet = Book.getSheetAt(0);
            HSSFRow row = sheet.getRow(sheet.getFirstRowNum());

            int firstRN = sheet.getFirstRowNum();
            int firstCN = row.getFirstCellNum();
            int lastCN = row.getLastCellNum();
            int lastRN = sheet.getLastRowNum();

            for (int s = firstRN + 2; s <= lastRN; s++) {

                String eqName = sheet.getRow(s).getCell(firstCN + 2).toString();
                EquipmentData equipment = new EquipmentData(eqName);
                InstrumentationWindow.console.Echo("Importing equipment " + eqName + ", " +  (s - firstRN - 1) + " from " + (lastRN - firstRN - 1), DONT_SHOW_JOPTION);

                equipment.setRevision(sheet.getRow(s).getCell(firstCN + 1).toString());
                equipment.setGeneralNotes(sheet.getRow(s).getCell(lastCN - 1).toString());

                for (int i = firstCN + 3; i < lastCN - 1; i ++) {
                    String UDF = sheet.getRow(firstRN).getCell(i).toString();
                    int UDFnum = 0;
                    try {
                        UDFnum = Integer.parseInt(UDF.split("DF")[1]);
                    }
                    catch (ArrayIndexOutOfBoundsException e) {
                        InstrumentationWindow.console.Echo("Field " + UDF + " for equipment " + sheet.getRow(s).getCell(firstCN + 2).toString() + " is ignored!", SHOW_JOPTION);
                    }
                    if (UDFnum < DataContainer.N) {
                        equipment.setUDF(UDFnum, sheet.getRow(firstRN + 1).getCell(i).toString(), sheet.getRow(s).getCell(i).toString());
                        if (sheet.getRow(s).getCell(i).toString().isEmpty())
                            equipment.setUDFVisibility(UDFnum, false);
                        else equipment.setUDFVisibility(UDFnum, true);
                    }
                    else {
                        InstrumentationWindow.console.Echo("Field " + UDF + " for equipment " + sheet.getRow(s).getCell(firstCN + 1).toString() + " is out of bounds and ignored!", SHOW_JOPTION);
                    }
                }
                pd.PutData(equipment);
            }
        }
        catch (FileNotFoundException e) {
            InstrumentationWindow.console.Echo("Pipe import file " + filename.split("/")[filename.split("/").length - 1] + " not found!", SHOW_JOPTION);
        }
        catch (OfficeXmlFileException e) {
            InstrumentationWindow.console.Echo("Wrong pipe import file format!", SHOW_JOPTION);
        }
        catch (Exception e) {
            InstrumentationWindow.console.Echo("Unknown error in ImportPipeDataFromXLS!", SHOW_JOPTION);
        }
        return pd;
    }

    static DataContainer ImportInstrumentsFromXLS (String filename) {

        DataContainer dc = new DataContainer();
        File file = new File(filename);

        InstrumentationWindow.console.Echo("Starting instrument data import from xls", DONT_SHOW_JOPTION);

        try {
            FileInputStream stream = new FileInputStream(file);
            HSSFWorkbook Book = new HSSFWorkbook(stream);
            HSSFSheet sheet = Book.getSheetAt(0);
            HSSFRow row = sheet.getRow(sheet.getFirstRowNum());

            int firstRN = sheet.getFirstRowNum();
            int firstCN = row.getFirstCellNum();
            int lastCN = row.getLastCellNum();
            int lastRN = sheet.getLastRowNum();

            for (int i = firstRN + 2; i <= lastRN; i ++) {

                String tag = sheet.getRow(i).getCell(firstCN + 2).toString();

                InstrumentationWindow.console.Echo("Importing instrument " + tag + ", " + (i - firstRN - 1) + " from " + (lastRN - firstRN - 1), DONT_SHOW_JOPTION);

                String instrumentFunction;
                if (tryPreffix(tag)) instrumentFunction = tag.split("-")[1];
                else instrumentFunction = tag.split("-")[0];

                String revision = sheet.getRow(i).getCell(firstCN + 1).toString();
                String tagNumber = sheet.getRow(i).getCell(firstCN + 2).toString();
                String loopNumber = sheet.getRow(i).getCell(firstCN + 3).toString();
                String PIDnumber = sheet.getRow(i).getCell(firstCN + 4).toString();
                String pipeName = sheet.getRow(i).getCell(firstCN + 5).toString();
                String eqName = sheet.getRow(i).getCell(firstCN + 6).toString();
                String GN = sheet.getRow(i).getCell(lastCN - 1).toString();

                InstrumentData Instrument = new InstrumentData(tagNumber);
                Instrument.setRevision(revision);
                Instrument.setPipeName(pipeName);
                Instrument.setLoopNumber(loopNumber);
                Instrument.setPIDnumber(PIDnumber);
                Instrument.setEquipmentName(eqName);
                Instrument.setGeneralNotes(GN);

                for (int cn = firstCN + 7; cn < lastCN - 1; cn ++) {
                    String UDF = sheet.getRow(firstRN).getCell(cn).toString();
                    int UDFnum = 0;

                    try {
                        UDFnum = Integer.parseInt(UDF.split("DF")[1]);
                    }
                    catch (ArrayIndexOutOfBoundsException e) {
                        InstrumentationWindow.console.Echo("Field " + UDF + " for instrument " + sheet.getRow(i).getCell(firstCN + 1).toString() + " is ignored!", SHOW_JOPTION);
                    }

                    String UDFName = sheet.getRow(firstRN + 1).getCell(cn).toString();
                    String UDFValue = sheet.getRow(i).getCell(cn).toString();

                    if (UDFnum < DataContainer.N) {
                        Instrument.setUDF(UDFnum, UDFName, UDFValue);
                        if (UDFValue.isEmpty()) Instrument.setUDFVisibility(UDFnum, false);
                        else Instrument.setUDFVisibility(UDFnum, true);
                    }
                    else {
                        InstrumentationWindow.console.Echo("Field " + UDF + " for instrument " + sheet.getRow(i).getCell(firstCN + 1).toString() + " is out of bounds and ignored!", SHOW_JOPTION);
                    }
                }

                switch (instrumentFunction) {
                    case "TI":
                    case "TG":
                        Instrument.setType(Instrument.TG);
                        break;
                    case "PI":
                    case "PG":
                        Instrument.setType(Instrument.PG);
                        break;
                    case "TE":
                    case "TET":
                    case "TTE":
                        Instrument.setType(Instrument.TE);
                        break;
                    case "TT":
                    case "TTI":
                    case "TIT":
                        Instrument.setType(Instrument.TT);
                        break;
                    case "TS":
                        Instrument.setType(Instrument.TS);
                        break;
                    case "PT":
                    case "PTI":
                    case "PIT":
                        Instrument.setType(Instrument.PT);
                        break;
                    case "PDT":
                        Instrument.setType(Instrument.PDT);
                        break;
                    case "PDI":
                        Instrument.setType(Instrument.PDG);
                        break;
                    case "PS":
                        Instrument.setType(Instrument.PS);
                        break;
                    case "LT":
                    case "LTI":
                    case "LIT":
                        Instrument.setType(Instrument.LT);
                        break;
                    case "LS":
                        Instrument.setType(Instrument.LS);
                        break;
                    case "LG":
                    case "LI":
                        Instrument.setType(Instrument.LG);
                        break;
                    case "FE":
                        Instrument.setType(Instrument.FE);
                        break;
                    case "FT":
                    case "FTI":
                    case "FIT":
                    case "FQT":
                    case "FET":
                        Instrument.setType(Instrument.FT);
                        break;
                    case "FS":
                        Instrument.setType(Instrument.FS);
                        break;
                    case "FG":
                        Instrument.setType(Instrument.FG);
                        break;
                    case "MT":
                        Instrument.setType(Instrument.MT);
                        break;
                    case "AT":
                    case "AE":
                    case "QT":
                    case "QE":
                        Instrument.setType(Instrument.QT);
                        break;
                    case "LV":
                    case "FV":
                    case "PV":
                    case "PDV":
                    case "TV":
                    case "HCV":
                        Instrument.setType(Instrument.CV);
                        break;
                    case "LZV":
                    case "FZV":
                    case "PZV":
                    case "PDZV":
                    case "TZV":
                    case "UV":
                    case "UZV":
                    case "XV":
                    case "HV":
                        Instrument.setType(Instrument.UV);
                        break;
                    case "SV":
                        Instrument.setType(Instrument.SV);
                        break;
                    default:
                        Instrument.setType(JOptionPane.showInputDialog(null, "Please define instrument type for" + Instrument.getTagNumber() + ": "));
                        break;
                }
                dc.PutData(Instrument);
            }
        }
        catch (FileNotFoundException e) {
            InstrumentationWindow.console.Echo("Instrument import file " + filename.split("/")[filename.split("/").length - 1] + " not found!", SHOW_JOPTION);
        }
        catch (OfficeXmlFileException e) {
            InstrumentationWindow.console.Echo("Wrong instrument import file format!", SHOW_JOPTION);
        }
        catch (Exception e) {
            InstrumentationWindow.console.Echo("Unknown error in ImportInstrumentsFromXLS!", SHOW_JOPTION);
        }
        return dc;
    }

    static void ImportHookupFormFromXLS(String filename) {
        File file = new File(filename);
        try {
            FileInputStream stream = new FileInputStream(file);
            HSSFWorkbook Book = new HSSFWorkbook(stream);
            HSSFSheet sheet = Book.getSheetAt(0);
            HSSFRow row = sheet.getRow(sheet.getFirstRowNum());

            int firstRN = sheet.getFirstRowNum();
            int firstCN = row.getFirstCellNum();
            int lastCN = row.getLastCellNum();
            int lastRN = sheet.getLastRowNum();

            String query;
            int cnt = 0;
            ArrayList<String> udfNumber = new ArrayList<>();
            ArrayList<String> udfName = new ArrayList<>();

            for (int i = firstRN + 1; i <= lastRN; i ++) {
                try {
                    udfNumber.add(cnt, sheet.getRow(i).getCell(firstCN).toString());
                    udfName.add(cnt, sheet.getRow(i).getCell(firstCN + 1).toString());
                    System.out.println(udfNumber.get(cnt) + " = " + udfName.get(cnt));
                    cnt++;
                }
                catch (NullPointerException npe) {}
            }

            query = "INSERT INTO UDFs (ItemName";
            for (int i = 0; i < udfNumber.size(); i ++) query += ", " + udfNumber.get(i);
            query += ") VALUES ('hookups";
            for (int i = 0; i < udfNumber.size(); i ++) query += "', '" + udfName.get(i);
            query += "');";

            System.out.println(query);
            try {
                //conn = DriverManager.getConnection(Instrumentation2.url, Instrumentation2.user, Instrumentation2.password);
                stm = InstrumentationWindow.connection.createStatement();
                if (InstrumentationWindow.showQueries) InstrumentationWindow.console.Echo("Sending request: " + query, DONT_SHOW_JOPTION);
                updateResult = stm.executeUpdate(query);
                if (InstrumentationWindow.showQueries) InstrumentationWindow.console.Echo("Server answer: " + updateResult, DONT_SHOW_JOPTION);
            }
            catch (SQLException se) {
                InstrumentationWindow.console.Echo("SQL Exception!\n" + se.getMessage(), DONT_SHOW_JOPTION);
            }
            finally {
                CloseConnection(stm);
            }
        }
        catch (FileNotFoundException e) {
            InstrumentationWindow.console.Echo("Hookup mapping file " + filename.split("/")[filename.split("/").length - 1] + " not found!", SHOW_JOPTION);
        }
        catch (OfficeXmlFileException e) {
            InstrumentationWindow.console.Echo("Wrong hookup mapping file format!", SHOW_JOPTION);
        }
        catch (IOException e) {
            InstrumentationWindow.console.Echo("IO exception in 'ImportHookupFormFromXLS()'!", SHOW_JOPTION);
        }
        catch (Exception e) {
            InstrumentationWindow.console.Echo("Unknown error in 'ImportHookupFormFromXLS()'!", SHOW_JOPTION);
        }
    }

    synchronized static <T> Set<String> getListFromDatabase (T p) {

        String query = "";

        if (p instanceof PipeData)
            query = "SELECT PipeName FROM pipelines;";
        else
        if (p instanceof EquipmentData)
            query = "SELECT EqName FROM equipment;";
        else
        if (p instanceof InstrumentData)
            query = "SELECT TagNumber FROM instruments;";
        else
        if (p instanceof LoopData)
            query = "SELECT LoopNumber FROM loops;";
        else
        if (p instanceof CableData)
            query = "SELECT CableName FROM cables;";
        else
        if (p instanceof JBoxData)
            query = "SELECT JBoxName FROM jboxes;";
        if (p instanceof HookupData)
            query = "SELECT TagNumber FROM hookups;";

        Set<String> items = new HashSet<String>();

        try {
            InstrumentationWindow.CheckConnection();
            stm = InstrumentationWindow.connection.createStatement();

            if (InstrumentationWindow.showQueries) InstrumentationWindow.console.Echo("Sending request: " + query, DONT_SHOW_JOPTION);
            rset = stm.executeQuery(query);

            if (rset.next()) {
                rset.absolute(0);
                while (rset.next()) items.add(rset.getString(1));
            }
        }
        catch (SQLException se) {
            InstrumentationWindow.console.Echo("SQL Exception!\n" + se.getMessage(), DONT_SHOW_JOPTION);
        }
        finally {
            CloseConnection(stm);
        }
        return items;
    }

    synchronized static <T> ArrayList<String[]> getItemListFromDatabase (T p) {

        String queryItem = "";

        if (p instanceof PipeData)
            queryItem = "SELECT Revision, PipeName FROM pipelines ORDER BY PipeName;";
        else
        if (p instanceof EquipmentData)
            queryItem = "SELECT Revision, EqName FROM equipment ORDER BY EqName;";
        else
        if (p instanceof InstrumentData)
            queryItem = "SELECT Revision, TagNumber FROM instruments ORDER BY TagNumber;";
        else
        if (p instanceof LoopData)
            queryItem = "SELECT Revision, LoopNumber FROM loops ORDER BY LoopNumber;";
        else
        if (p instanceof CableData)
            queryItem = "SELECT Revision, CableName FROM cables ORDER BY CableName;";
        else
        if (p instanceof JBoxData)
            queryItem = "SELECT Revision, JBoxName FROM jboxes ORDER BY JBoxName;";
        if (p instanceof HookupData)
            queryItem = "SELECT Revision, TagNumber FROM hookups ORDER BY TagNumber;";

        ArrayList<String[]> itemList = new ArrayList<>();

        try {
            InstrumentationWindow.CheckConnection();
            stm = InstrumentationWindow.connection.createStatement();
            if (InstrumentationWindow.showQueries) InstrumentationWindow.console.Echo("Sending request: " + queryItem, DONT_SHOW_JOPTION);
            rset = stm.executeQuery(queryItem);

            if (rset.next()) {
                rset.absolute(0);
                int count = 0;
                while (rset.next()) {
                    String[] result = new String[2];
                    result[0] = rset.getString(1);
                    result[1] = rset.getString(2);
                    itemList.add(count, result);
                    count ++;
                }
            }
        }
        catch (SQLException se) {
            InstrumentationWindow.console.Echo("SQL Exception!\n" + se.getMessage(), DONT_SHOW_JOPTION);
        }
        finally {
            CloseConnection(stm);
        }

        return itemList;
    }

    synchronized static <T> T getItemFromDatabase(String itemName, T p) {

        String query;

        Requestable GetUDFs = (String... s) -> {

            String queryUDFValues;
            ResultSet rset = null;
            String[] result = new String[DataContainer.N];

            queryUDFValues = "SELECT UDF01";
            for (int i = 2; i < DataContainer.N; i ++) {
                if (i < 10) queryUDFValues += ", UDF0" + i;
                if (i >= 10) queryUDFValues += ", UDF" + i;
            }
            if (s[1].equals("pipelines")) queryUDFValues += ", UDF" + DataContainer.N + " FROM pipelines WHERE PipeName='" + s[0]+"';";
            if (s[1].equals("equipment")) queryUDFValues += ", UDF" + DataContainer.N + " FROM equipment WHERE EqName='" + s[0]+"';";
            if (s[1].equals("instruments")) queryUDFValues += ", UDF" + DataContainer.N + " FROM instruments WHERE TagNumber='" + s[0]+"';";
            if (s[1].equals("loops")) queryUDFValues += ", UDF" + DataContainer.N + " FROM loops WHERE LoopNumber='" + s[0]+"';";
            if (s[1].equals("cables")) queryUDFValues += ", UDF" + DataContainer.N + " FROM cables WHERE CableName='" + s[0]+"';";
            if (s[1].equals("jboxes")) queryUDFValues += ", UDF" + DataContainer.N + " FROM jboxes WHERE JBoxName='" + s[0]+"';";
            if (s[1].equals("UDFs")) queryUDFValues += ", UDF" + DataContainer.N + " FROM UDFs WHERE ItemName='" + s[0]+"';";

            try {
                if (InstrumentationWindow.showQueries) InstrumentationWindow.console.Echo("Sending request: " + queryUDFValues, DONT_SHOW_JOPTION);
                rset = stm.executeQuery(queryUDFValues);
                if (rset.next())
                for (int count = 1; count < DataContainer.N; count ++)
                    result[count] = rset.getString(count);
            }
            catch (SQLException se) {
                InstrumentationWindow.console.Echo("SQL Exception in GetUDFs!\n" + se.getMessage(), DONT_SHOW_JOPTION);
            }
            catch (NullPointerException npe) {}

            return result;
        };

        if (p instanceof PipeData) {
            PipeData pipeData = new PipeData(itemName);
            try {
                InstrumentationWindow.CheckConnection();
                stm = InstrumentationWindow.connection.createStatement();

                try {
                    query = "SELECT Revision, GeneralNotes FROM pipelines WHERE PipeName='" + itemName + "';";
                    if (InstrumentationWindow.showQueries) InstrumentationWindow.console.Echo("Sending request: " + query, DONT_SHOW_JOPTION);
                    ResultSet rset = stm.executeQuery(query);
                    if (rset.next()) {
                        pipeData.setRevision(rset.getString(1));
                        pipeData.setGeneralNotes(rset.getString(2));
                    }
                }
                catch (SQLException se) {
                    InstrumentationWindow.console.Echo("SQL Exception in getItemFromDatabase/pipedata!\n" + se.getMessage(), DONT_SHOW_JOPTION);
                }
                catch (NullPointerException npe) {}

                try {
                    String[] UDFValues = GetUDFs.Execute(itemName, "pipelines");
                    String[] UDFNames = GetUDFs.Execute(itemName, "UDFs");
                        try {
                            for (int count = 1; count < DataContainer.N; count++)
                                pipeData.setUDF(count, UDFNames[count], UDFValues[count]);
                        }
                        catch (NullPointerException npe) {}
                }
                catch (NullPointerException npe) {}

            }
            catch (SQLException se) {
                InstrumentationWindow.console.Echo("SQL Exception!\n" + se.getMessage(), DONT_SHOW_JOPTION);
            }
            finally {
                CloseConnection(stm);
            }
            return (T) pipeData;
        }

        if (p instanceof EquipmentData) {
            EquipmentData equipmentData = new EquipmentData(itemName);
            try {
                InstrumentationWindow.CheckConnection();
                stm = InstrumentationWindow.connection.createStatement();
                try {
                    query = "SELECT Revision, GeneralNotes FROM equipment WHERE EqName='" + itemName + "';";
                    if (InstrumentationWindow.showQueries) InstrumentationWindow.console.Echo("Sending request: " + query, DONT_SHOW_JOPTION);
                    ResultSet rset = stm.executeQuery(query);
                    if (rset.next()) {
                        equipmentData.setRevision(rset.getString(1));
                        equipmentData.setGeneralNotes(rset.getString(2));
                    }
                }
                catch (SQLException se) {
                    InstrumentationWindow.console.Echo("SQL Exception!\n" + se.getMessage(), DONT_SHOW_JOPTION);
                }
                catch (NullPointerException npe) {}

                try {
                    String[] UDFValues = GetUDFs.Execute(itemName, "equipment");
                    String[] UDFNames = GetUDFs.Execute(itemName, "UDFs");
                    try {
                        for (int count = 1; count < DataContainer.N; count++)
                            equipmentData.setUDF(count, UDFNames[count], UDFValues[count]);
                    }
                    catch (NullPointerException npe) {}
                }
                catch (NullPointerException npe) {}

            }
            catch (SQLException se) {
                InstrumentationWindow.console.Echo("SQL Exception!\n" + se.getMessage(), DONT_SHOW_JOPTION);
            }
            finally {
                CloseConnection(stm);
            }
            return (T) equipmentData;
        }

        if (p instanceof InstrumentData) {
            InstrumentData instrumentData = new InstrumentData(itemName);
            try {
                InstrumentationWindow.CheckConnection();
                stm = InstrumentationWindow.connection.createStatement();

                String[] data = new String[7];

                try {
                    query = "SELECT Revision, LoopNumber, PIDnumber, EqName, PipeName, InstrumentType, GeneralNotes FROM instruments WHERE TagNumber='" + itemName + "';";

                    if (InstrumentationWindow.showQueries) InstrumentationWindow.console.Echo("Sending request: " + query, DONT_SHOW_JOPTION);
                    ResultSet rset = stm.executeQuery(query);

                    if (rset.next()) {
                        try {
                            for (int count = 1; count <= 7; count++) data[count - 1] = rset.getString(count);
                        }
                        catch (NullPointerException npe) {}

                        instrumentData.setRevision(data[0]);
                        instrumentData.setLoopNumber(data[1]);
                        instrumentData.setPIDnumber(data[2]);
                        instrumentData.setEquipmentName(data[3]);
                        instrumentData.setPipeName(data[4]);
                        instrumentData.setType(data[5]);
                        instrumentData.setGeneralNotes(data[6]);
                    }
                }
                catch (SQLException se) {
                    InstrumentationWindow.console.Echo("SQL Exception!\n" + se.getMessage(), DONT_SHOW_JOPTION);
                }
                catch (NullPointerException npe) {}

                try {
                    String[] UDFValues = GetUDFs.Execute(itemName, "instruments");
                    String[] UDFNames = GetUDFs.Execute(itemName, "UDFs");
                    try {
                        for (int count = 1; count < DataContainer.N; count++)
                            instrumentData.setUDF(count, UDFNames[count], UDFValues[count]);
                    }
                    catch (NullPointerException npe) {}
                }
                catch (NullPointerException npe) {}

            }
            catch (SQLException se) {
                InstrumentationWindow.console.Echo("SQL Exception!\n" + se.getMessage(), DONT_SHOW_JOPTION);
            }
            finally {
                CloseConnection(stm);
            }
            return (T) instrumentData;
        }

        if (p instanceof LoopData) {
            LoopData loopData = new LoopData(itemName);
            try {
                InstrumentationWindow.CheckConnection();
                stm = InstrumentationWindow.connection.createStatement();

                String[] data = new String[16];

                try {
                    query = "SELECT Revision, LoopNumber, PIDNumber, LoopDescription, Indication, Summ, Control, Alarm, Safety, " +
                            "LLL, LL, L, H, HH, HHH, GeneralNotes FROM loops WHERE LoopNumber='" + itemName + "';";

                    if (InstrumentationWindow.showQueries) InstrumentationWindow.console.Echo("Sending request: " + query, DONT_SHOW_JOPTION);
                    ResultSet rset = stm.executeQuery(query);

                    if (rset.next()) {
                        try {
                            for (int count = 1; count <= 16; count++) data[count - 1] = rset.getString(count);
                        }
                        catch (NullPointerException npe) {}

                        loopData.setRevision(data[0]);
                        loopData.setLoopNumber(data[1]);
                        loopData.setPIDNumber(data[2]);
                        loopData.setLoopDescription(data[3]);
                        loopData.setIndication(data[4]);
                        loopData.setSumm(data[5]);
                        loopData.setControl(data[6]);
                        loopData.setAlarm(data[7]);
                        loopData.setSafety(data[8]);
                        loopData.setLLL_Al(data[9]);
                        loopData.setLL_Al(data[10]);
                        loopData.setL_Al(data[11]);
                        loopData.setH_Al(data[12]);
                        loopData.setHH_Al(data[13]);
                        loopData.setHHH_Al(data[14]);
                        loopData.setGeneralNotes(data[15]);
                    }
                }
                catch (SQLException se) {
                    InstrumentationWindow.console.Echo("SQL Exception!\n" + se.getMessage(), DONT_SHOW_JOPTION);
                }
                catch (NullPointerException npe) {}

                try {
                    String[] UDFValues = GetUDFs.Execute(itemName, "loops");
                    String[] UDFNames = GetUDFs.Execute(itemName, "UDFs");
                    try {
                        for (int count = 1; count < DataContainer.N; count++)
                            loopData.setUDF(count, UDFNames[count], UDFValues[count]);
                    }
                    catch (NullPointerException npe) {}
                }
                catch (NullPointerException npe) {}

            }
            catch (SQLException se) {
                InstrumentationWindow.console.Echo("SQL Exception!\n" + se.getMessage(), DONT_SHOW_JOPTION);
            }
            finally {
                CloseConnection(stm);
            }
            return (T) loopData;
        }
           else return null;
    }

    synchronized static <T> DataContainer getItemsFromDatabase(Set<String> itemSet, T p) {

        String query;
        DataContainer dataContainer = new DataContainer();

        Requestable GetUDFs = (String... s) -> {

            String queryUDFValues;
            ResultSet rset = null;
            String[] result = new String[DataContainer.N];

            queryUDFValues = "SELECT UDF01";
            for (int i = 2; i < DataContainer.N; i ++) {
                if (i < 10) queryUDFValues += ", UDF0" + i;
                if (i >= 10) queryUDFValues += ", UDF" + i;
            }
            if (s[1].equals("pipelines")) queryUDFValues += ", UDF" + DataContainer.N + " FROM pipelines WHERE PipeName='" + s[0]+"';";
            if (s[1].equals("equipment")) queryUDFValues += ", UDF" + DataContainer.N + " FROM equipment WHERE EqName='" + s[0]+"';";
            if (s[1].equals("instruments")) queryUDFValues += ", UDF" + DataContainer.N + " FROM instruments WHERE TagNumber='" + s[0]+"';";
            if (s[1].equals("loops")) queryUDFValues += ", UDF" + DataContainer.N + " FROM loops WHERE LoopNumber='" + s[0]+"';";
            if (s[1].equals("cables")) queryUDFValues += ", UDF" + DataContainer.N + " FROM cables WHERE CableName='" + s[0]+"';";
            if (s[1].equals("jboxes")) queryUDFValues += ", UDF" + DataContainer.N + " FROM jboxes WHERE JBoxName='" + s[0]+"';";
            if (s[1].equals("hookups")) queryUDFValues += ", UDF" + DataContainer.N + " FROM hookups WHERE TagNumber='" + s[0]+"';";
            if (s[1].equals("UDFs")) queryUDFValues += ", UDF" + DataContainer.N + " FROM UDFs WHERE ItemName='" + s[0]+"';";

            try {
                if (InstrumentationWindow.showQueries) InstrumentationWindow.console.Echo("Sending request: " + queryUDFValues, DONT_SHOW_JOPTION);
                rset = stm.executeQuery(queryUDFValues);
                if (rset.next())
                    for (int count = 1; count < DataContainer.N; count ++)
                        result[count] = rset.getString(count);
            }
            catch (SQLException se) {
                InstrumentationWindow.console.Echo("SQL Exception in GetUDFs!\n" + se.getMessage(), DONT_SHOW_JOPTION);
            }
            catch (NullPointerException npe) {}

            return result;
        };

        if (p instanceof PipeData) {
            try {
                InstrumentationWindow.CheckConnection();
                stm = InstrumentationWindow.connection.createStatement();

                for (String itemName : itemSet) {
                    PipeData pipeData = new PipeData(itemName);
                    try {
                        query = "SELECT Revision, GeneralNotes FROM pipelines WHERE PipeName='" + itemName + "';";
                        if (InstrumentationWindow.showQueries)
                            InstrumentationWindow.console.Echo("Sending request: " + query, DONT_SHOW_JOPTION);
                        ResultSet rset = stm.executeQuery(query);
                        if (rset.next()) {
                            pipeData.setRevision(rset.getString(1));
                            pipeData.setGeneralNotes(rset.getString(2));
                        }
                    }
                    catch (SQLException se) {
                        InstrumentationWindow.console.Echo("SQL Exception in getItemFromDatabase/pipedata!\n" + se.getMessage(), DONT_SHOW_JOPTION);
                    }
                    catch (NullPointerException npe) {       }

                    try {
                        String[] UDFValues = GetUDFs.Execute(itemName, "pipelines");
                        String[] UDFNames = GetUDFs.Execute(itemName, "UDFs");
                        try {
                            for (int count = 1; count < DataContainer.N; count++)
                                pipeData.setUDF(count, UDFNames[count], UDFValues[count]);
                        }
                        catch (NullPointerException npe) {                         }
                    }
                    catch (NullPointerException npe) {                    }

                    dataContainer.PutData(pipeData);
                }
            }
            catch (SQLException se) {
                InstrumentationWindow.console.Echo("SQL Exception!\n" + se.getMessage(), DONT_SHOW_JOPTION);
            }
            finally {
                CloseConnection(stm);
            }

            return dataContainer;
        }

        if (p instanceof EquipmentData) {
            try {
                InstrumentationWindow.CheckConnection();
                stm = InstrumentationWindow.connection.createStatement();

                for (String itemName : itemSet) {
                    EquipmentData equipmentData = new EquipmentData(itemName);
                    try {
                        query = "SELECT Revision, GeneralNotes FROM equipment WHERE EqName='" + itemName + "';";
                        if (InstrumentationWindow.showQueries)
                            InstrumentationWindow.console.Echo("Sending request: " + query, DONT_SHOW_JOPTION);
                        ResultSet rset = stm.executeQuery(query);
                        if (rset.next()) {
                            equipmentData.setRevision(rset.getString(1));
                            equipmentData.setGeneralNotes(rset.getString(2));
                        }
                    } catch (SQLException se) {
                        InstrumentationWindow.console.Echo("SQL Exception!\n" + se.getMessage(), DONT_SHOW_JOPTION);
                    } catch (NullPointerException npe) {
                    }

                    try {
                        String[] UDFValues = GetUDFs.Execute(itemName, "equipment");
                        String[] UDFNames = GetUDFs.Execute(itemName, "UDFs");
                        try {
                            for (int count = 1; count < DataContainer.N; count++)
                                equipmentData.setUDF(count, UDFNames[count], UDFValues[count]);
                        }
                        catch (NullPointerException npe) { }
                    }
                    catch (NullPointerException npe) { }

                    dataContainer.PutData(equipmentData);
                }
            }
            catch (SQLException se) {
                InstrumentationWindow.console.Echo("SQL Exception!\n" + se.getMessage(), DONT_SHOW_JOPTION);
            }
            finally {
                CloseConnection(stm);
            }
            return dataContainer;
        }

        if (p instanceof InstrumentData) {

            try {
                InstrumentationWindow.CheckConnection();
                stm = InstrumentationWindow.connection.createStatement();
                for (String itemName : itemSet) {
                    InstrumentData instrumentData = new InstrumentData(itemName);
                    String[] data = new String[7];

                    try {
                        query = "SELECT Revision, LoopNumber, PIDnumber, EqName, PipeName, InstrumentType, GeneralNotes FROM instruments WHERE TagNumber='" + itemName + "';";

                        if (InstrumentationWindow.showQueries)
                            InstrumentationWindow.console.Echo("Sending request: " + query, DONT_SHOW_JOPTION);
                        ResultSet rset = stm.executeQuery(query);

                        if (rset.next()) {
                            try {
                                for (int count = 1; count <= 7; count++) data[count - 1] = rset.getString(count);
                            } catch (NullPointerException npe) {
                            }

                            instrumentData.setRevision(data[0]);
                            instrumentData.setLoopNumber(data[1]);
                            instrumentData.setPIDnumber(data[2]);
                            instrumentData.setEquipmentName(data[3]);
                            instrumentData.setPipeName(data[4]);
                            instrumentData.setType(data[5]);
                            instrumentData.setGeneralNotes(data[6]);
                        }
                    } catch (SQLException se) {
                        InstrumentationWindow.console.Echo("SQL Exception!\n" + se.getMessage(), DONT_SHOW_JOPTION);
                    } catch (NullPointerException npe) {
                    }

                    try {
                        String[] UDFValues = GetUDFs.Execute(itemName, "instruments");
                        String[] UDFNames = GetUDFs.Execute(itemName, "UDFs");
                        try {
                            for (int count = 1; count < DataContainer.N; count++)
                                instrumentData.setUDF(count, UDFNames[count], UDFValues[count]);
                        }
                        catch (NullPointerException npe) { }
                    }
                    catch (NullPointerException npe) { }

                    dataContainer.PutData(instrumentData);
                }
            }
            catch (SQLException se) {
                InstrumentationWindow.console.Echo("SQL Exception!\n" + se.getMessage(), DONT_SHOW_JOPTION);
            }
            finally {
                CloseConnection(stm);
            }
            return dataContainer;
        }

        if (p instanceof LoopData) {

            try {
                InstrumentationWindow.CheckConnection();
                stm = InstrumentationWindow.connection.createStatement();

                for (String itemName : itemSet) {
                    LoopData loopData = new LoopData(itemName);
                    String[] data = new String[16];

                    try {
                        query = "SELECT Revision, LoopNumber, PIDNumber, LoopDescription, Indication, Summ, Control, Alarm, Safety, " +
                                "LLL, LL, L, H, HH, HHH, GeneralNotes FROM loops WHERE LoopNumber='" + itemName + "';";

                        if (InstrumentationWindow.showQueries)
                            InstrumentationWindow.console.Echo("Sending request: " + query, DONT_SHOW_JOPTION);
                        ResultSet rset = stm.executeQuery(query);

                        if (rset.next()) {
                            try {
                                for (int count = 1; count <= 16; count++) data[count - 1] = rset.getString(count);
                            } catch (NullPointerException npe) {
                            }

                            loopData.setRevision(data[0]);
                            loopData.setLoopNumber(data[1]);
                            loopData.setPIDNumber(data[2]);
                            loopData.setLoopDescription(data[3]);
                            loopData.setIndication(data[4]);
                            loopData.setSumm(data[5]);
                            loopData.setControl(data[6]);
                            loopData.setAlarm(data[7]);
                            loopData.setSafety(data[8]);
                            loopData.setLLL_Al(data[9]);
                            loopData.setLL_Al(data[10]);
                            loopData.setL_Al(data[11]);
                            loopData.setH_Al(data[12]);
                            loopData.setHH_Al(data[13]);
                            loopData.setHHH_Al(data[14]);
                            loopData.setGeneralNotes(data[15]);
                        }
                    }
                    catch (SQLException se) {
                        InstrumentationWindow.console.Echo("SQL Exception!\n" + se.getMessage(), DONT_SHOW_JOPTION);
                    }
                    catch (NullPointerException npe) {  }

                    try {
                        String[] UDFValues = GetUDFs.Execute(itemName, "loops");
                        String[] UDFNames = GetUDFs.Execute(itemName, "UDFs");
                        try {
                            for (int count = 1; count < DataContainer.N; count++)
                                loopData.setUDF(count, UDFNames[count], UDFValues[count]);
                        }
                        catch (NullPointerException npe) { }
                    }
                    catch (NullPointerException npe) {  }

                    dataContainer.PutData(loopData);
                }
            }
            catch (SQLException se) {
                InstrumentationWindow.console.Echo("SQL Exception!\n" + se.getMessage(), DONT_SHOW_JOPTION);
            }
            finally {
                CloseConnection(stm);
            }
            return dataContainer;
        }

        if (p instanceof HookupData) {
            try {
                InstrumentationWindow.CheckConnection();
                stm = InstrumentationWindow.connection.createStatement();

                for (String itemName : itemSet) {
                    HookupData hookupData = new HookupData(itemName);
                    try {
                        query = "SELECT Revision, GeneralNotes FROM hookups WHERE TagNumber='" + itemName + "';";
                        if (InstrumentationWindow.showQueries)
                            InstrumentationWindow.console.Echo("Sending request: " + query, DONT_SHOW_JOPTION);
                        ResultSet rset = stm.executeQuery(query);
                        if (rset.next()) {
                            hookupData.setRevision(rset.getString(1));
                            hookupData.setGeneralNotes(rset.getString(2));
                        }
                    } catch (SQLException se) {
                        InstrumentationWindow.console.Echo("SQL Exception!\n" + se.getMessage(), DONT_SHOW_JOPTION);
                    } catch (NullPointerException npe) {
                    }

                    try {
                        String[] UDFValues = GetUDFs.Execute(itemName, "hookups");
                        String[] UDFNames = GetUDFs.Execute("hookups", "UDFs");
                        try {
                            for (int count = 1; count < DataContainer.N; count++)
                                hookupData.setUDF(count, UDFNames[count], UDFValues[count]);
                        }
                        catch (NullPointerException npe) { }
                    }
                    catch (NullPointerException npe) { }

                    dataContainer.PutData(hookupData);
                }
            }
            catch (SQLException se) {
                InstrumentationWindow.console.Echo("SQL Exception!\n" + se.getMessage(), DONT_SHOW_JOPTION);
            }
            finally {
                CloseConnection(stm);
            }
            return dataContainer;
        }
        else return null;
    }

    synchronized static String[][] getHookupMappingFromDatabase() {

        String query;
        String[][] hookupUDFs = new String[DataContainer.N][3];

        query = "SELECT UDF01";
        for (int count = 2; count < DataContainer.N; count ++) {
            if (count < 10) query += ", UDF0" + count;
            else query += ", UDF" + count;
        }
        query += " FROM UDFs WHERE ItemName='hookups';";

        try {
            InstrumentationWindow.CheckConnection();
            stm = InstrumentationWindow.connection.createStatement();
            if (InstrumentationWindow.showQueries) InstrumentationWindow.console.Echo("Sending request: " + query, DONT_SHOW_JOPTION);
            rset = stm.executeQuery(query);
            if (rset.next())
                for (int count = 1; count < DataContainer.N; count ++) {
                    if (count < 10) hookupUDFs[count][0] = "UDF0" + count;
                    else hookupUDFs[count][0] = "UDF" + count;
                    hookupUDFs[count][1] = rset.getString(count);
                    hookupUDFs[count][2] = "";
                }
            hookupUDFs[0][0] = "";
            hookupUDFs[0][1] = "";
            hookupUDFs[0][2] = "";
        }
        catch (SQLException se) {
            InstrumentationWindow.console.Echo("SQL Exception!\n" + se.getMessage(), DONT_SHOW_JOPTION);
        }
        finally {
            CloseConnection(stm);
        }

        return hookupUDFs;
    }
}