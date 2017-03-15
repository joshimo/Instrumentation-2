/**
 * Created by y.golota on 04.01.2017.
 *
 *
 */

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;
import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.*;

class Export {

    static Statement statement;
    static int updateResult;

    static final boolean DONT_SHOW_JOPTION = false;
    static final boolean SHOW_JOPTION = true;

    synchronized private static void CloseConnection(Statement statement) {
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

    private static ExecutableSet RequestUpdate = (query) -> {
        try {
            InstrumentationWindow.CheckConnection();
            statement = InstrumentationWindow.connection.createStatement();
            try {
                for (String q : query) {
                    if (InstrumentationWindow.showQueries) InstrumentationWindow.console.Echo("Sending request: " + q, DONT_SHOW_JOPTION);
                    updateResult = statement.executeUpdate(q);
                    if (InstrumentationWindow.showQueries) InstrumentationWindow.console.Echo("Server answer: " + updateResult, DONT_SHOW_JOPTION);
                }
            }
            catch (NullPointerException npe) {
                InstrumentationWindow.console.Echo("The request is empty", DONT_SHOW_JOPTION);
            }
        }
        catch (SQLException se) {
            InstrumentationWindow.console.Echo("SQL Exception!\n" + se.getMessage(), DONT_SHOW_JOPTION);
        }
        finally {
            CloseConnection(statement);
        }
    };

    static void SaveInstrumentsToXLS(String filename, Collection<String> set, DataContainer DC) {
        File file = new File(filename);
        try {
            FileOutputStream stream = new FileOutputStream(file);
            HSSFWorkbook Book = new HSSFWorkbook();

            HSSFFont header_font = Book.createFont();
            header_font.setBold(true);
            header_font.setFontHeight((short) 225);

            HSSFFont subheader_font = Book.createFont();
            subheader_font.setBold(true);
            subheader_font.setItalic(true);
            subheader_font.setFontHeight((short) 225);

            HSSFFont UDFvalue_font = Book.createFont();
            UDFvalue_font.setItalic(true);
            UDFvalue_font.setFontHeight((short) 200);

            HSSFCellStyle style_header = Book.createCellStyle();
            style_header.setFillForegroundColor(HSSFColor.YELLOW.index);
            style_header.setFillPattern((short) 1);
            style_header.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            style_header.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            style_header.setFont(header_font);
            style_header.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            style_header.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            style_header.setBorderRight(HSSFCellStyle.BORDER_THIN);
            style_header.setBorderTop(HSSFCellStyle.BORDER_THIN);

            HSSFCellStyle style_subheader = Book.createCellStyle();
            style_subheader.setFillForegroundColor(HSSFColor.DARK_YELLOW.index);
            style_subheader.setFillPattern((short) 1);
            style_subheader.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            style_subheader.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            style_subheader.setFont(subheader_font);
            style_subheader.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            style_subheader.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            style_subheader.setBorderRight(HSSFCellStyle.BORDER_THIN);
            style_subheader.setBorderTop(HSSFCellStyle.BORDER_THIN);

            HSSFCellStyle style_UDFname = Book.createCellStyle();
            style_UDFname.setFillForegroundColor(HSSFColor.GREY_40_PERCENT.index);
            style_UDFname.setFillPattern((short) 1);
            style_UDFname.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            style_UDFname.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            style_UDFname.setBorderRight(HSSFCellStyle.BORDER_THIN);
            style_UDFname.setBorderTop(HSSFCellStyle.BORDER_THIN);

            HSSFCellStyle style_UDFvalue = Book.createCellStyle();
            style_UDFvalue.setFont(UDFvalue_font);
            style_UDFvalue.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
            style_UDFvalue.setFillPattern((short) 1);
            style_UDFvalue.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            style_UDFvalue.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            style_UDFvalue.setBorderRight(HSSFCellStyle.BORDER_THIN);
            style_UDFvalue.setBorderTop(HSSFCellStyle.BORDER_THIN);

            HSSFSheet i_list = Book.createSheet("Instruments List");
            int cnt = 0;
            i_list.setColumnWidth(0, 1500);
            i_list.setColumnWidth(1, 5000);
            i_list.setColumnWidth(2, 10000);
            i_list.createRow(cnt).setHeight((short) 300);
            i_list.addMergedRegion(new CellRangeAddress(0,0,0,2));
            i_list.getRow(cnt).createCell(0).setCellStyle(style_header);
            i_list.getRow(cnt).createCell(1).setCellStyle(style_header);
            i_list.getRow(cnt).getCell(0).setCellValue("INSTRUMENT LIST");

            for (String tagNumber : set) {
                try {
                    InstrumentData instrument = DC.GetData(tagNumber, new InstrumentData());
                    cnt ++;
                    i_list.createRow(cnt).setHeight((short) 260);
                    i_list.getRow(cnt).createCell(0).setCellStyle(style_UDFname);
                    i_list.getRow(cnt).getCell(0).setCellValue(cnt);
                    i_list.getRow(cnt).createCell(1).setCellStyle(style_UDFname);
                    i_list.getRow(cnt).getCell(1).setCellValue(tagNumber);
                    i_list.getRow(cnt).createCell(2).setCellStyle(style_UDFname);
                    i_list.getRow(cnt).getCell(2).setCellValue(instrument.getType());
                }
                catch (NullPointerException e) {
                    InstrumentationWindow.console.Echo("Instrument "+tagNumber+" not found in database!", SHOW_JOPTION);
                }
            }

            for (String tagNumber : set) {
                HSSFSheet sheet = Book.createSheet(tagNumber);
                int rowCount;

                try {
                    InstrumentData instrument = DC.GetData(tagNumber, new InstrumentData());

                    sheet.setColumnWidth(0, 10000);
                    sheet.setColumnWidth(1, 7500);
                    sheet.createRow(0).setHeight((short) 400);

                    sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));
                    sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 1));

                    sheet.getRow(0).createCell(0).setCellStyle(style_header);
                    sheet.getRow(0).createCell(1).setCellStyle(style_header);
                    sheet.getRow(0).getCell(0).setCellValue(instrument.getType().toUpperCase() + " DATASHEET");

                    sheet.createRow(1).setHeight((short) 400);
                    sheet.getRow(1).createCell(0).setCellStyle(style_subheader);
                    sheet.getRow(1).createCell(1).setCellStyle(style_subheader);
                    sheet.getRow(1).getCell(0).setCellValue("Instrument data:");

                    sheet.createRow(2).setHeight((short) 320);
                    sheet.getRow(2).createCell(0).setCellStyle(style_UDFname);
                    sheet.getRow(2).getCell(0).setCellValue("Tag number: ");
                    sheet.getRow(2).createCell(1).setCellStyle(style_UDFvalue);
                    sheet.getRow(2).getCell(1).setCellValue(instrument.getTagNumber());

                    sheet.createRow(3).setHeight((short) 300);
                    sheet.getRow(3).createCell(0).setCellStyle(style_UDFname);
                    sheet.getRow(3).getCell(0).setCellValue("PID number: ");
                    sheet.getRow(3).createCell(1).setCellStyle(style_UDFvalue);
                    sheet.getRow(3).getCell(1).setCellValue(instrument.getPIDnumber());

                    sheet.createRow(4).setHeight((short) 300);
                    sheet.getRow(4).createCell(0).setCellStyle(style_UDFname);
                    sheet.getRow(4).getCell(0).setCellValue("Pipe number: ");
                    sheet.getRow(4).createCell(1).setCellStyle(style_UDFvalue);
                    sheet.getRow(4).getCell(1).setCellValue(instrument.getPipeName());

                    sheet.createRow(5).setHeight((short) 300);
                    sheet.getRow(5).createCell(0).setCellStyle(style_UDFname);
                    sheet.getRow(5).getCell(0).setCellValue("Equippment number: ");
                    sheet.getRow(5).createCell(1).setCellStyle(style_UDFvalue);
                    sheet.getRow(5).getCell(1).setCellValue(instrument.getEquipmentName());

                    rowCount = sheet.getLastRowNum();
                    for (int i = 1; i < DataContainer.N; i ++) {
                        if (instrument.getUDFVisibility(i)) {
                            rowCount ++;
                            sheet.createRow(rowCount).setHeight((short) 300);
                            sheet.getRow(rowCount).createCell(0).setCellStyle(style_UDFname);
                            sheet.getRow(rowCount).getCell(0).setCellValue(instrument.getUDF(i)[0] + ":");
                            sheet.getRow(rowCount).createCell(1).setCellStyle(style_UDFvalue);
                            sheet.getRow(rowCount).getCell(1).setCellValue(instrument.getUDF(i)[1]);
                        }
                    }

                    rowCount ++;
                    sheet.createRow(rowCount).setHeight((short) 300);
                    sheet.getRow(rowCount).createCell(0).setCellStyle(style_UDFname);
                    sheet.getRow(rowCount).getCell(0).setCellValue("General notes: ");
                    sheet.getRow(rowCount).createCell(1).setCellStyle(style_UDFvalue);
                    sheet.getRow(rowCount).getCell(1).setCellValue(instrument.getGeneralNotes());

                    rowCount ++;
                    sheet.createRow(rowCount).setHeight((short) 400);
                    sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, 0, 1));
                    sheet.getRow(rowCount).createCell(0).setCellStyle(style_subheader);
                    sheet.getRow(rowCount).createCell(1).setCellStyle(style_subheader);
                    sheet.getRow(rowCount).getCell(0).setCellValue("Pipe and process data:");

                    PipeData pipe = DC.GetData(instrument.getPipeName(), new PipeData());
                    try {
                        for (int i = 1; i < DataContainer.N; i++) {
                            if (pipe.getUDFVisibility(i)) {
                                rowCount++;
                                sheet.createRow(rowCount).setHeight((short) 300);
                                sheet.getRow(rowCount).createCell(0).setCellStyle(style_UDFname);
                                sheet.getRow(rowCount).getCell(0).setCellValue(pipe.getUDF(i)[0] + ":");
                                sheet.getRow(rowCount).createCell(1).setCellStyle(style_UDFvalue);
                                sheet.getRow(rowCount).getCell(1).setCellValue(pipe.getUDF(i)[1]);
                            }
                        }
                        rowCount ++;
                        sheet.createRow(rowCount);
                        sheet.getRow(rowCount).createCell(0).setCellStyle(style_UDFname);
                        sheet.getRow(rowCount).getCell(0).setCellValue("Pipe general notes:");
                        sheet.getRow(rowCount).createCell(1).setCellStyle(style_UDFvalue);
                        sheet.getRow(rowCount).getCell(1).setCellValue(pipe.getGeneralNotes());
                    }
                    catch (NullPointerException e) {
                        if ((instrument.getEquipmentName().isEmpty())|(instrument.getEquipmentName().length() < 2)) {
                            rowCount ++;
                            sheet.createRow(rowCount);
                            sheet.getRow(rowCount).createCell(0).setCellValue("PIPE NOT FOUND IN DATABASE!");
                        }
                    }

                    if ((instrument.getPipeName().isEmpty())|(instrument.getPipeName().length()<=1)&(instrument.getEquipmentName().length() > 2)) {
                        EquipmentData equipment = DC.GetData(instrument.getEquipmentName(), new EquipmentData());
                        try {
                            sheet.getRow(rowCount).getCell(0).setCellValue("Equipment and process data:");
                            for (int i = 1; i < DataContainer.N; i++) {
                                if (equipment.getUDFVisibility(i)) {
                                    rowCount ++;
                                    sheet.createRow(rowCount).setHeight((short) 300);
                                    sheet.getRow(rowCount).createCell(0).setCellStyle(style_UDFname);
                                    sheet.getRow(rowCount).getCell(0).setCellValue(equipment.getUDF(i)[0] + ":");
                                    sheet.getRow(rowCount).createCell(1).setCellStyle(style_UDFvalue);
                                    sheet.getRow(rowCount).getCell(1).setCellValue(equipment.getUDF(i)[1]);
                                }
                            }
                            rowCount ++;
                            sheet.createRow(rowCount);
                            sheet.getRow(rowCount).createCell(0).setCellStyle(style_UDFname);
                            sheet.getRow(rowCount).getCell(0).setCellValue("Equipment general notes:");
                            sheet.getRow(rowCount).createCell(1).setCellStyle(style_UDFvalue);
                            sheet.getRow(rowCount).getCell(1).setCellValue(equipment.getGeneralNotes());
                        }
                        catch (NullPointerException e) {
                            rowCount ++;
                            sheet.createRow(rowCount);
                            sheet.getRow(rowCount).createCell(0).setCellValue("PIPE AND EQUIPMENT NOT FOUND IN DATABASE!");
                        }
                    }
                }
                catch (NullPointerException e) {
                    InstrumentationWindow.console.Echo("Instrument " + tagNumber + " not found in database!", SHOW_JOPTION);
                }
            }

            Book.write(stream);
            stream.flush();
            stream.close();
        }
        catch(FileNotFoundException e) {
            InstrumentationWindow.console.Echo("File " + filename.split("/")[filename.split("/").length - 1] + " not found or still opened by another application!", SHOW_JOPTION);
        }
        catch(IOException e) {
            InstrumentationWindow.console.Echo("IO Exception!", SHOW_JOPTION);
        }
    }

    static void SaveInstrumentsToXLS(String filename, String tagNumber, DataContainer DC) {
        File file = new File(filename);
        try {
            FileOutputStream stream = new FileOutputStream(file);
            HSSFWorkbook Book = new HSSFWorkbook();

            HSSFSheet sheet = Book.createSheet(tagNumber);
            int rowCount;
            InstrumentData instrument = DC.GetData(tagNumber, new InstrumentData());

            HSSFFont header_font = Book.createFont();
            header_font.setBold(true);
            header_font.setFontHeight((short) 225);

            HSSFFont subheader_font = Book.createFont();
            subheader_font.setBold(true);
            subheader_font.setItalic(true);
            subheader_font.setFontHeight((short) 225);

            HSSFFont UDFvalue_font = Book.createFont();
            UDFvalue_font.setItalic(true);
            UDFvalue_font.setFontHeight((short) 200);

            HSSFCellStyle style_header = Book.createCellStyle();
            style_header.setFillForegroundColor(HSSFColor.YELLOW.index);
            style_header.setFillPattern((short) 1);
            style_header.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            style_header.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            style_header.setFont(header_font);
            style_header.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            style_header.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            style_header.setBorderRight(HSSFCellStyle.BORDER_THIN);
            style_header.setBorderTop(HSSFCellStyle.BORDER_THIN);

            HSSFCellStyle style_subheader = Book.createCellStyle();
            style_subheader.setFillForegroundColor(HSSFColor.DARK_YELLOW.index);
            style_subheader.setFillPattern((short) 1);
            style_subheader.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            style_subheader.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            style_subheader.setFont(subheader_font);
            style_subheader.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            style_subheader.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            style_subheader.setBorderRight(HSSFCellStyle.BORDER_THIN);
            style_subheader.setBorderTop(HSSFCellStyle.BORDER_THIN);

            HSSFCellStyle style_UDFname = Book.createCellStyle();
            style_UDFname.setFillForegroundColor(HSSFColor.GREY_40_PERCENT.index);
            style_UDFname.setFillPattern((short) 1);
            style_UDFname.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            style_UDFname.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            style_UDFname.setBorderRight(HSSFCellStyle.BORDER_THIN);
            style_UDFname.setBorderTop(HSSFCellStyle.BORDER_THIN);

            HSSFCellStyle style_UDFvalue = Book.createCellStyle();
            style_UDFvalue.setFont(UDFvalue_font);
            style_UDFvalue.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
            style_UDFvalue.setFillPattern((short) 1);
            style_UDFvalue.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            style_UDFvalue.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            style_UDFvalue.setBorderRight(HSSFCellStyle.BORDER_THIN);
            style_UDFvalue.setBorderTop(HSSFCellStyle.BORDER_THIN);

            sheet.setColumnWidth(0, 10000);
            sheet.setColumnWidth(1, 7500);
            sheet.createRow(0).setHeight((short) 400);

            sheet.addMergedRegion(new CellRangeAddress(0,0,0,1));
            sheet.addMergedRegion(new CellRangeAddress(1,1,0,1));

            sheet.getRow(0).createCell(0).setCellStyle(style_header);
            sheet.getRow(0).createCell(1).setCellStyle(style_header);
            sheet.getRow(0).getCell(0).setCellValue(instrument.getType().toUpperCase());

            sheet.createRow(1).setHeight((short) 400);
            sheet.getRow(1).createCell(0).setCellStyle(style_subheader);
            sheet.getRow(1).createCell(1).setCellStyle(style_subheader);
            sheet.getRow(1).getCell(0).setCellValue("Instrument data:");

            sheet.createRow(2).setHeight((short) 320);
            sheet.getRow(2).createCell(0).setCellStyle(style_UDFname);
            sheet.getRow(2).getCell(0).setCellValue("Tag number: ");
            sheet.getRow(2).createCell(1).setCellStyle(style_UDFvalue);
            sheet.getRow(2).getCell(1).setCellValue(instrument.getTagNumber());

            sheet.createRow(3).setHeight((short) 300);
            sheet.getRow(3).createCell(0).setCellStyle(style_UDFname);
            sheet.getRow(3).getCell(0).setCellValue("PID number: ");
            sheet.getRow(3).createCell(1).setCellStyle(style_UDFvalue);
            sheet.getRow(3).getCell(1).setCellValue(instrument.getPIDnumber());

            sheet.createRow(4).setHeight((short) 300);
            sheet.getRow(4).createCell(0).setCellStyle(style_UDFname);
            sheet.getRow(4).getCell(0).setCellValue("Pipe number: ");
            sheet.getRow(4).createCell(1).setCellStyle(style_UDFvalue);
            sheet.getRow(4).getCell(1).setCellValue(instrument.getPipeName());

            sheet.createRow(5).setHeight((short) 300);
            sheet.getRow(5).createCell(0).setCellStyle(style_UDFname);
            sheet.getRow(5).getCell(0).setCellValue("Equippment number: ");
            sheet.getRow(5).createCell(1).setCellStyle(style_UDFvalue);
            sheet.getRow(5).getCell(1).setCellValue(instrument.getEquipmentName());

            rowCount = sheet.getLastRowNum();
            for (int i = 1; i < DataContainer.N; i ++) {
                if (instrument.getUDFVisibility(i)) {
                    rowCount ++;
                    sheet.createRow(rowCount).setHeight((short) 300);
                    sheet.getRow(rowCount).createCell(0).setCellStyle(style_UDFname);
                    sheet.getRow(rowCount).getCell(0).setCellValue(instrument.getUDF(i)[0] + ":");
                    sheet.getRow(rowCount).createCell(1).setCellStyle(style_UDFvalue);
                    sheet.getRow(rowCount).getCell(1).setCellValue(instrument.getUDF(i)[1]);
                }
            }

            rowCount ++;
            sheet.createRow(rowCount).setHeight((short) 300);
            sheet.getRow(rowCount).createCell(0).setCellStyle(style_UDFname);
            sheet.getRow(rowCount).getCell(0).setCellValue("General notes: ");
            sheet.getRow(rowCount).createCell(1).setCellStyle(style_UDFvalue);
            sheet.getRow(rowCount).getCell(1).setCellValue(instrument.getGeneralNotes());

            rowCount ++;
            sheet.createRow(rowCount).setHeight((short) 400);
            sheet.addMergedRegion(new CellRangeAddress(rowCount,rowCount,0,1));
            sheet.getRow(rowCount).createCell(0).setCellStyle(style_subheader);
            sheet.getRow(rowCount).createCell(1).setCellStyle(style_subheader);
            sheet.getRow(rowCount).getCell(0).setCellValue("Pipe and process data:");

            PipeData pipe = DC.GetData(instrument.getPipeName(), new PipeData());
            try {
                for (int i = 1; i < DataContainer.N; i++) {
                    if (pipe.getUDFVisibility(i)) {
                        rowCount ++;
                        sheet.createRow(rowCount).setHeight((short) 300);
                        sheet.getRow(rowCount).createCell(0).setCellStyle(style_UDFname);
                        sheet.getRow(rowCount).getCell(0).setCellValue(pipe.getUDF(i)[0] + ":");
                        sheet.getRow(rowCount).createCell(1).setCellStyle(style_UDFvalue);
                        sheet.getRow(rowCount).getCell(1).setCellValue(pipe.getUDF(i)[1]);
                    }
                }
                rowCount ++;
                sheet.createRow(rowCount);
                sheet.getRow(rowCount).createCell(0).setCellStyle(style_UDFname);
                sheet.getRow(rowCount).getCell(0).setCellValue("Pipe general notes:");
                sheet.getRow(rowCount).createCell(1).setCellStyle(style_UDFvalue);
                sheet.getRow(rowCount).getCell(1).setCellValue(pipe.getGeneralNotes());
            }
            catch (NullPointerException e) {
                rowCount ++;
                sheet.createRow(rowCount);
                sheet.getRow(rowCount).createCell(0).setCellValue("PIPE NOT FOUND IN DATABASE!");
            }

            Book.write(stream);
            stream.flush();
            stream.close();
        }
        catch(FileNotFoundException e) {
            InstrumentationWindow.console.Echo("File " + filename.split("/")[filename.split("/").length - 1] + " not found or still opened by another application!", SHOW_JOPTION);
        }
        catch(IOException e) {
            InstrumentationWindow.console.Echo("IO Exception!", SHOW_JOPTION);
        }
    }

    synchronized static void AddInstrumentsToDatabase(Collection<String> instrumentsSet, DataContainer DC) {

        Set<String> querySet = new HashSet<String>();
        Set<String> existInstruments = new HashSet<String>();
        Set<String> newInstruments = new HashSet<String>();
        String queryInstrument = "";
        String queryUDF = "";
        String queryHookup = "";

        Statement stm = null;
        ResultSet rset;

        try {
            InstrumentationWindow.CheckConnection();
            stm = InstrumentationWindow.connection.createStatement();
            if (InstrumentationWindow.showQueries) InstrumentationWindow.console.Echo("Sending request: " + "SELECT TagNumber FROM instruments;", DONT_SHOW_JOPTION);
            rset = stm.executeQuery("SELECT TagNumber FROM instruments;");

            if (rset.next()) {
                int j = 1;
                rset.absolute(0);
                while (rset.next()) {
                    if (instrumentsSet.contains(rset.getString(j))) existInstruments.add(rset.getString(j));
                }
            }

        }
        catch (SQLException se) {
            InstrumentationWindow.console.Echo("SQL Exception!\n" + se.getMessage(), DONT_SHOW_JOPTION);
        }
        finally {
            CloseConnection(stm);
        }

        try {
            for (String s : instrumentsSet)
                if (!existInstruments.contains(s)) newInstruments.add(s);
        }
        catch (NullPointerException npe) {}

        try {
            for (String tagNumber : newInstruments) {
                InstrumentData instrumentData = DC.GetData(tagNumber, new InstrumentData());
                queryInstrument = "INSERT INTO instruments (Revision, TagNumber, LoopNumber, InstrumentType, PIDNumber, PipeName, EqName, GeneralNotes";
                queryUDF = "INSERT INTO UDFs (ItemName";
                queryHookup = "INSERT INTO hookups (Revision, TagNumber, GeneralNotes";

                for (int n = 1; n < DataContainer.N; n++)
                    if (!(instrumentData.getUDF(n)[0] == null))
                        if (n < 10) {
                            if (!(instrumentData.getUDF(n)[1] == null)) queryInstrument += ", UDF0" + n;
                            queryUDF += ", UDF0" + n;
                            queryHookup += ", UDF0" + n;
                        }
                        else {
                            if (!(instrumentData.getUDF(n)[1] == null)) queryInstrument += ", UDF" + n;
                            queryUDF += ", UDF" + n;
                            queryHookup += ", UDF" + n;
                        }

                queryInstrument += ") VALUES ('" +
                        instrumentData.getRevision() + "', '" +
                        instrumentData.getTagNumber() + "', '" +
                        instrumentData.getLoopNumber() + "', '" +
                        instrumentData.getType() + "', '" +
                        instrumentData.getPIDnumber() + "', '" +
                        instrumentData.getPipeName() + "', '" +
                        instrumentData.getEquipmentName() + "', '" +
                        instrumentData.getGeneralNotes();
                queryUDF += ") VALUES ('" + tagNumber;
                queryHookup += ") VALUES ('" +
                        instrumentData.getRevision() + "', '" +
                        instrumentData.getTagNumber() + "', '";

                for (int n = 1; n < DataContainer.N; n++)
                    if (!(instrumentData.getUDF(n)[0] == null)) {
                        if (!(instrumentData.getUDF(n)[1] == null)) queryInstrument += "', '" + instrumentData.getUDF(n)[1];
                        queryUDF += "', '" + instrumentData.getUDF(n)[0];
                        queryHookup += "', '";
                    }
                queryInstrument += "');";
                queryUDF += "');";
                queryHookup += "');";
                querySet.add(queryInstrument);
                querySet.add(queryUDF);
                querySet.add(queryHookup);
            }
        }
        catch (NullPointerException npe) {}

        try {
            for (String tagNumber : existInstruments) {
                InstrumentationWindow.console.Echo("Instrument " + tagNumber + " already exist. Dou you want to update it?", DONT_SHOW_JOPTION);
                int answer = JOptionPane.showConfirmDialog(null, "Instrument " + tagNumber + " already exist. Dou you want to update it?");

                if (answer == JOptionPane.OK_OPTION) {
                    InstrumentationWindow.console.Echo("User choice is OK", DONT_SHOW_JOPTION);
                    InstrumentData instrumentData = DC.GetData(tagNumber, new InstrumentData());

                    queryInstrument = "UPDATE instruments SET ";
                    queryUDF = "UPDATE UDFs SET UDF01='" + instrumentData.getUDF(1)[0];
                    queryInstrument += "Revision='" + instrumentData.getRevision();
                    queryInstrument += "', LoopNumber='" + instrumentData.getLoopNumber();
                    queryInstrument += "', InstrumentType='" + instrumentData.getType();
                    queryInstrument += "', PIDNumber='" + instrumentData.getPIDnumber();
                    queryInstrument += "', PipeName='" + instrumentData.getPipeName();
                    queryInstrument += "', EqName='" + instrumentData.getEquipmentName();
                    queryInstrument += "', GeneralNotes='" + instrumentData.getGeneralNotes();

                    for (int n = 1; n < DataContainer.N; n++)
                        if (!(instrumentData.getUDF(n)[0] == null))
                            if (n < 10) {
                                if (n > 1) queryUDF += "', UDF0" + n + "='" + instrumentData.getUDF(n)[0];
                                queryInstrument += "', UDF0" + n + "='" + instrumentData.getUDF(n)[1];
                            }
                            else {
                                queryUDF += "', UDF" + n + "='" + instrumentData.getUDF(n)[0];
                                queryInstrument += "', UDF" + n + "='" + instrumentData.getUDF(n)[1];
                            }

                    queryInstrument += "' WHERE TagNumber='" + instrumentData.getTagNumber() + "';";
                    queryUDF += "' WHERE ItemName='" + instrumentData.getTagNumber() + "';";
                    querySet.add(queryInstrument);
                    querySet.add(queryUDF);
                }
                else
                    InstrumentationWindow.console.Echo("User choice is CANSEL", DONT_SHOW_JOPTION);
            }
        }
        catch (NullPointerException npe) {}

        try {
            int count = 0;
            InstrumentationWindow.CheckConnection();
            statement = InstrumentationWindow.connection.createStatement();

            if (!queryInstrument.isEmpty())
            for (String q : querySet) {
                try {
                    count ++;
                    if (InstrumentationWindow.showQueries) InstrumentationWindow.console.Echo("Sending request (" + count + "/" + querySet.size() + "): " + q, DONT_SHOW_JOPTION);
                    updateResult = statement.executeUpdate(q);
                    if (InstrumentationWindow.showQueries) InstrumentationWindow.console.Echo("Server answer: " + updateResult, DONT_SHOW_JOPTION);
                }
                catch (SQLException se) {
                    InstrumentationWindow.console.Echo("SQL Exception!\n" + se.getMessage(), DONT_SHOW_JOPTION);
                }
            }
        }
        catch (SQLException se) {
            InstrumentationWindow.console.Echo("SQL Exception!\n" + se.getMessage(), DONT_SHOW_JOPTION);
        }
        finally {
            CloseConnection(statement);
        }
    }

    synchronized static void AddPipesToDatabase(Collection<String> pipeSet, DataContainer DC) {

        Set<String> querySet = new HashSet<String>();
        Set<String> newPipeSet = new HashSet<String>();
        Set<String> existPipeSet = new HashSet<String>();

        String queryPipe = "";
        String queryUDF = "";

        Statement stm = null;
        ResultSet rset;

        try {
            InstrumentationWindow.CheckConnection();
            stm = InstrumentationWindow.connection.createStatement();
            if (InstrumentationWindow.showQueries) InstrumentationWindow.console.Echo("Sending request: " + "SELECT PipeName FROM pipelines;", DONT_SHOW_JOPTION);
            rset = stm.executeQuery("SELECT PipeName FROM pipelines;");
            if (rset.next()) {
                int j = 1;
                rset.absolute(0);
                while (rset.next()) {
                    if (pipeSet.contains(rset.getString(j))) existPipeSet.add(rset.getString(j));
                }
            }
        }
        catch (SQLException se) {
            InstrumentationWindow.console.Echo("SQL Exception!\n" + se.getMessage(), DONT_SHOW_JOPTION);
        }
        finally {
            CloseConnection(stm);
        }

        try {
            for (String s : pipeSet)
                if (!existPipeSet.contains(s)) newPipeSet.add(s);
        }
        catch (NullPointerException npe) {}

        try {
            for (String PipeName : existPipeSet) {
                PipeData pipeData = DC.GetData(PipeName, new PipeData());

                queryPipe = "UPDATE pipelines SET ";
                queryUDF = "UPDATE UDFs SET UDF01='" + pipeData.getUDF(1)[0];
                queryPipe += "Revision='" + pipeData.getRevision();
                queryPipe += "', PipeName='" + pipeData.getPipeName();
                queryPipe += "', GeneralNotes='" + pipeData.getGeneralNotes();

                for (int n = 1; n < DataContainer.N; n ++)
                    if (!(pipeData.getUDF(n)[0] == null))
                        if (n < 10) {
                            if (n > 1) queryUDF += "', UDF0" + n + "='" + pipeData.getUDF(n)[0];
                            queryPipe += "', UDF0" + n + "='" + pipeData.getUDF(n)[1];
                        }
                        else {
                            queryUDF += "', UDF" + n + "='" + pipeData.getUDF(n)[0];
                            queryPipe += "', UDF" + n + "='" + pipeData.getUDF(n)[1];
                        }
                queryPipe += "' WHERE PipeName='" + pipeData.getPipeName() + "';";
                queryUDF += "' WHERE ItemName='" + pipeData.getPipeName() + "';";

                querySet.add(queryPipe);
                querySet.add(queryUDF);
            }
        }
        catch (NullPointerException npe) {}

        try {
            for (String PipeName : newPipeSet) {
                PipeData pipeData = DC.GetData(PipeName, new PipeData());
                queryPipe = "INSERT INTO pipelines (Revision, PipeName, GeneralNotes";
                queryUDF = "INSERT INTO UDFs (ItemName";
                for (int n = 1; n < DataContainer.N; n ++)
                    if (!(pipeData.getUDF(n)[0] == null))
                        if (n < 10) {
                            queryPipe += ", UDF0" + n;
                            queryUDF += ", UDF0" + n;
                        } else {
                            queryPipe += ", UDF" + n;
                            queryUDF += ", UDF" + n;
                        }

                queryPipe += ") VALUES ('" + pipeData.getRevision() + "', '" + pipeData.getPipeName() + "', '" + pipeData.getGeneralNotes();
                queryUDF += ") VALUES ('" + pipeData.getPipeName();

                for (int n = 1; n < DataContainer.N; n ++)
                    if (!(pipeData.getUDF(n)[1] == null)) {
                        queryPipe += "', '" + pipeData.getUDF(n)[1];
                        queryUDF += "', '" + pipeData.getUDF(n)[0];
                    }
                queryPipe += "');";
                queryUDF += "');";
                querySet.add(queryPipe);
                querySet.add(queryUDF);
            }
        }
        catch (NullPointerException npe) {}

        try {
            int count = 0;
            InstrumentationWindow.CheckConnection();
            statement = InstrumentationWindow.connection.createStatement();

            if (!querySet.isEmpty())
            for (String q : querySet) {
                try {
                    count ++;
                    if (InstrumentationWindow.showQueries) InstrumentationWindow.console.Echo("Sending request (" + count + "/" + querySet.size() + "): " + q, DONT_SHOW_JOPTION);
                    updateResult = statement.executeUpdate(q);
                    if (InstrumentationWindow.showQueries) InstrumentationWindow.console.Echo("Server answer: " + updateResult, DONT_SHOW_JOPTION);
                }
                catch (SQLException se) {
                    InstrumentationWindow.console.Echo("SQL Exception!\n" + se.getMessage(), DONT_SHOW_JOPTION);
                }
            }
        }
        catch (SQLException se) {
            InstrumentationWindow.console.Echo("SQL Exception!\n" + se.getMessage(), DONT_SHOW_JOPTION);
        }
        finally {
            CloseConnection(statement);
        }
    }

    synchronized static void AddEquipmentToDatabase(Collection<String>eqSet, DataContainer DC) {

        Set<String> querySet = new HashSet<String>();
        Set<String> newEqSet = new HashSet<String>();
        Set<String> existEqSet = new HashSet<String>();

        String queryEquipment = "";
        String queryUDF = "";

        Statement stm = null;
        ResultSet rset;

        try {
            InstrumentationWindow.CheckConnection();
            stm = InstrumentationWindow.connection.createStatement();
            if (InstrumentationWindow.showQueries) InstrumentationWindow.console.Echo("Sending request: " + "SELECT EqName FROM equipment;", DONT_SHOW_JOPTION);
            rset = stm.executeQuery("SELECT EqName FROM equipment;");
            if (rset.next()) {
                int j = 1;
                rset.absolute(0);
                while (rset.next()) {
                    if (eqSet.contains(rset.getString(j))) existEqSet.add(rset.getString(j));
                }
            }
        }
        catch (SQLException se) {
            InstrumentationWindow.console.Echo("SQL Exception!\n" + se.getMessage(), DONT_SHOW_JOPTION);
        }
        finally {
            CloseConnection(stm);
        }

        try {
            for (String s : eqSet)
                if (!existEqSet.contains(s)) newEqSet.add(s);
        }
        catch (NullPointerException npe) {}

        try {
            for (String EqName : existEqSet) {
                EquipmentData eqData = DC.GetData(EqName, new EquipmentData());

                queryEquipment = "UPDATE equipment SET ";
                queryUDF = "UPDATE UDFs SET UDF01='" + eqData.getUDF(1)[0];
                queryEquipment += "Revision='" + eqData.getRevision();
                queryEquipment += "', EqName='" + eqData.getEquipmentName();
                queryEquipment += "', GeneralNotes='" + eqData.getGeneralNotes();

                for (int n = 1; n < DataContainer.N; n ++)
                    if (!(eqData.getUDF(n)[0] == null))
                        if (n < 10) {
                            if (n > 1) queryUDF += "', UDF0" + n + "='" + eqData.getUDF(n)[0];
                            queryEquipment += "', UDF0" + n + "='" + eqData.getUDF(n)[1];
                        }
                        else {
                            queryUDF += "', UDF" + n + "='" + eqData.getUDF(n)[0];
                            queryEquipment += "', UDF" + n + "='" + eqData.getUDF(n)[1];
                        }
                queryEquipment += "' WHERE EqName='" + eqData.getEquipmentName() + "';";
                queryUDF += "' WHERE ItemName='" + eqData.getEquipmentName() + "';";

                querySet.add(queryEquipment);
                querySet.add(queryUDF);
            }
        }
        catch (NullPointerException npe) {}

        try {
            for (String eqName : newEqSet) {
                EquipmentData eqData = DC.GetData(eqName, new EquipmentData());
                queryEquipment = "INSERT INTO equipment (Revision, EqName, GeneralNotes";
                queryUDF = "INSERT INTO UDFs (ItemName";
                for (int n = 1; n < DataContainer.N; n++)
                    if (!(eqData.getUDF(n)[0] == null))
                        if (n < 10) {
                            queryEquipment += ", UDF0" + n;
                            queryUDF += ", UDF0" + n;
                        } else {
                            queryEquipment += ", UDF" + n;
                            queryUDF += ", UDF" + n;
                        }

                queryEquipment += ") VALUES ('" + eqData.getRevision() + "', '" + eqData.getEquipmentName() + "', '" + eqData.getGeneralNotes();
                queryUDF += ") VALUES ('" + eqData.getEquipmentName();

                for (int n = 1; n < DataContainer.N; n++)
                    if (!(eqData.getUDF(n)[1] == null)) {
                        queryEquipment += "', '" + eqData.getUDF(n)[1];
                        queryUDF += "', '" + eqData.getUDF(n)[0];
                    }
                queryEquipment += "');";
                queryUDF += "');";

                querySet.add(queryEquipment);
                querySet.add(queryUDF);
            }
        }
        catch (NullPointerException npe) {}

        try {
            int count = 0;
            InstrumentationWindow.CheckConnection();
            statement = InstrumentationWindow.connection.createStatement();

            if (!queryEquipment.isEmpty())
            for (String q : querySet) {
                try {
                    count ++;
                    if (InstrumentationWindow.showQueries) InstrumentationWindow.console.Echo("Sending request (" + count + "/" + querySet.size() + "): " + q, DONT_SHOW_JOPTION);
                    updateResult = statement.executeUpdate(q);
                    if (InstrumentationWindow.showQueries) InstrumentationWindow.console.Echo("Server answer: " + updateResult, DONT_SHOW_JOPTION);
                }
                catch (SQLException se) {
                    InstrumentationWindow.console.Echo("SQL Exception!\n" + se.getMessage(), DONT_SHOW_JOPTION);
                }
            }
        }
        catch (SQLException se) {
            InstrumentationWindow.console.Echo("SQL Exception!\n" + se.getMessage(), DONT_SHOW_JOPTION);
        }
        finally {
            CloseConnection(statement);
        }
    }

    synchronized static void AddLoopsToDatabase(Collection<String> loopsSet, DataContainer DC) {

        Set<String> querySet = new HashSet<String>();
        Set<String> existLoops = new HashSet<String>();
        Set<String> newLoops = new HashSet<String>();
        String queryLoop = "";
        String queryUDF = "";

        Statement stm = null;
        ResultSet rset;

        try {
            InstrumentationWindow.CheckConnection();
            stm = InstrumentationWindow.connection.createStatement();
            if (InstrumentationWindow.showQueries) InstrumentationWindow.console.Echo("Sending request: " + "SELECT LoopNumber FROM loops;", DONT_SHOW_JOPTION);
            rset = stm.executeQuery("SELECT LoopNumber FROM loops;");

            if (rset.next()) {
                int j = 1;
                rset.absolute(0);
                while (rset.next()) {
                    if (loopsSet.contains(rset.getString(j))) existLoops.add(rset.getString(j));
                }
            }
        }
        catch (SQLException se) {
            InstrumentationWindow.console.Echo("SQL Exception!\n" + se.getMessage(), DONT_SHOW_JOPTION);
        }
        finally {
            CloseConnection(stm);
        }

        try {
            for (String s : loopsSet)
                if (!existLoops.contains(s)) newLoops.add(s);
        }
        catch (NullPointerException npe) {}

        try {
            for (String loopNumber : newLoops) {
                LoopData loopData = DC.GetData(loopNumber, new LoopData());
                queryLoop = "INSERT INTO loops (Revision, LoopNumber, PIDNumber, LoopDescription, Indication, Summ, Control, " +
                                                            "Alarm, Safety, LLL, LL, L, H, HH, HHH, GeneralNotes";
                queryUDF = "INSERT INTO UDFs (ItemName";
                for (int n = 1; n < DataContainer.N; n++)
                    if (!(loopData.getUDF(n)[0] == null))
                        if (n < 10) {
                            queryLoop += ", UDF0" + n;
                            queryUDF += ", UDF0" + n;
                        }
                        else {
                            queryLoop += ", UDF" + n;
                            queryUDF += ", UDF" + n;
                        }

                queryLoop += ") VALUES ('" +
                        loopData.getRevision() + "', '" +
                        loopData.getLoopNumber() + "', '" +
                        loopData.getPIDNumber() + "', '" +
                        loopData.getLoopDescription() + "', '" +
                        loopData.getIndication() + "', '" +
                        loopData.getSumm() + "', '" +
                        loopData.getControl() + "', '" +
                        loopData.getAlarm() + "', '" +
                        loopData.getSafety() + "', '" +
                        loopData.getLLL_Al() + "', '" +
                        loopData.getLL_Al() + "', '" +
                        loopData.getL_Al() + "', '" +
                        loopData.getH_Al() + "', '" +
                        loopData.getHH_Al() + "', '" +
                        loopData.getHHH_Al() + "', '" +
                        loopData.getGeneralNotes();
                queryUDF += ") VALUES ('" + loopNumber;
                for (int n = 1; n < DataContainer.N; n++)
                    if (!(loopData.getUDF(n)[1] == null)) {
                        queryLoop += "', '" + loopData.getUDF(n)[1];
                        queryUDF += "', '" + loopData.getUDF(n)[0];
                    }
                queryLoop += "');";
                queryUDF += "');";
                querySet.add(queryLoop);
                querySet.add(queryUDF);
            }
        }
        catch (NullPointerException npe) {}

        try {
            for (String loopNumber : existLoops) {
                int answer = JOptionPane.showConfirmDialog(null, "Loop " + loopNumber + " already exist. Dou you want to update it?");
                InstrumentationWindow.console.Echo("Loop " + loopNumber + " already exist. Dou you want to update it?", DONT_SHOW_JOPTION);
                if (answer == JOptionPane.OK_OPTION) {
                    InstrumentationWindow.console.Echo("User choice is OK", DONT_SHOW_JOPTION);
                    LoopData loopData = DC.GetData(loopNumber, new LoopData());

                    queryLoop = "UPDATE loops SET ";
                    queryUDF = "UPDATE UDFs SET UDF01='" + loopData.getUDF(1)[0];
                    queryLoop += "Revision='" + loopData.getRevision();
                    queryLoop += "', LoopNumber='" + loopData.getLoopNumber();
                    queryLoop += "', PIDNumber='" + loopData.getPIDNumber();
                    queryLoop += "', LoopDescription='" + loopData.getLoopDescription();
                    queryLoop += "', Indication='" + loopData.getIndication();
                    queryLoop += "', Summ='" + loopData.getSumm();
                    queryLoop += "', Control='" + loopData.getControl();
                    queryLoop += "', Alarm='" + loopData.getAlarm();
                    queryLoop += "', Safety='" + loopData.getSafety();
                    queryLoop += "', LLL='" + loopData.getLLL_Al();
                    queryLoop += "', LL='" + loopData.getLL_Al();
                    queryLoop += "', L='" + loopData.getL_Al();
                    queryLoop += "', H='" + loopData.getH_Al();
                    queryLoop += "', HH='" + loopData.getHH_Al();
                    queryLoop += "', HHH='" + loopData.getHHH_Al();
                    queryLoop += "', GeneralNotes='" + loopData.getGeneralNotes();

                    for (int n = 1; n < DataContainer.N; n++)
                        if (!(loopData.getUDF(n)[0] == null))
                            if (n < 10) {
                                if (n > 1) queryUDF += "', UDF0" + n + "='" + loopData.getUDF(n)[0];
                                queryLoop += "', UDF0" + n + "='" + loopData.getUDF(n)[1];
                            }
                            else {
                                queryUDF += "', UDF" + n + "='" + loopData.getUDF(n)[0];
                                queryLoop += "', UDF" + n + "='" + loopData.getUDF(n)[1];
                            }

                    queryLoop += "' WHERE TagNumber='" + loopData.getLoopNumber() + "';";
                    queryUDF += "' WHERE ItemName='" + loopData.getLoopNumber() + "';";
                    querySet.add(queryLoop);
                    querySet.add(queryUDF);
                }
                else
                    InstrumentationWindow.console.Echo("User choice is CANCEL");
            }
        }
        catch (NullPointerException npe) {}

        try {
            int count = 0;
            InstrumentationWindow.CheckConnection();
            statement = InstrumentationWindow.connection.createStatement();

            if (!queryLoop.isEmpty())
                for (String q : querySet) {
                    try {
                        count ++;
                        if (InstrumentationWindow.showQueries) InstrumentationWindow.console.Echo("Sending request (" + count + "/" + querySet.size() + "): " + q, DONT_SHOW_JOPTION);
                        updateResult = statement.executeUpdate(q);
                        if (InstrumentationWindow.showQueries) InstrumentationWindow.console.Echo("Server answer: " + updateResult, DONT_SHOW_JOPTION);
                    }
                    catch (SQLException se) {
                        InstrumentationWindow.console.Echo("SQL Exception!\n" + se.getMessage(), DONT_SHOW_JOPTION);
                    }
                }
        }
        catch (SQLException se) {
            InstrumentationWindow.console.Echo("SQL Exception!\n" + se.getMessage(), DONT_SHOW_JOPTION);
        }
        finally {
            CloseConnection(statement);
        }
    }

    synchronized static void AddCablesToDatabase(Collection<String> cablesSet, DataContainer DC) {

    }

    synchronized static void AddJBoxesToDatabase(Collection<String> jboxesSet, DataContainer DC) {

    }

    synchronized static void AddHookupsToDatabase(Collection<String> hookupsSet, DataContainer DC) {
        Set<String> querySet = new HashSet<String>();
        String queryHookup = "";

        try {
            for (String TagNumber : hookupsSet) {
                HookupData hookupData = DC.GetData(TagNumber, new HookupData());

                queryHookup = "UPDATE hookups SET ";
                queryHookup += "Revision='" + hookupData.getRevision();
                queryHookup += "', TagNumber='" + hookupData.getTagNumber();
                queryHookup += "', GeneralNotes='" + hookupData.getGeneralNotes();

                for (int n = 1; n < DataContainer.N; n ++)
                    if (!(hookupData.getUDF(n)[0] == null))
                        if (n < 10) queryHookup += "', UDF0" + n + "='" + hookupData.getUDF(n)[1];
                        else queryHookup += "', UDF" + n + "='" + hookupData.getUDF(n)[1];
                queryHookup += "' WHERE TagNumber='" + hookupData.getTagNumber() + "';";
                querySet.add(queryHookup);
            }
        }
        catch (NullPointerException npe) {}

        try {
            int count = 0;
            InstrumentationWindow.CheckConnection();
            statement = InstrumentationWindow.connection.createStatement();

            if (!queryHookup.isEmpty())
                for (String q : querySet) {
                    try {
                        count ++;
                        if (InstrumentationWindow.showQueries) InstrumentationWindow.console.Echo("Sending request (" + count + "/" + querySet.size() + "): " + q, DONT_SHOW_JOPTION);
                        updateResult = statement.executeUpdate(q);
                        if (InstrumentationWindow.showQueries) InstrumentationWindow.console.Echo("Server answer: " + updateResult, DONT_SHOW_JOPTION);
                    }
                    catch (SQLException se) {
                        InstrumentationWindow.console.Echo("SQL Exception!\n" + se.getMessage(), DONT_SHOW_JOPTION);
                    }
                }
        }
        catch (SQLException se) {
            InstrumentationWindow.console.Echo("SQL Exception!\n" + se.getMessage(), DONT_SHOW_JOPTION);
        }
        finally {
            CloseConnection(statement);
        }
    }

    synchronized static void UpdateInstrumentData(InstrumentData instrumentData) {
        String revision;
        Set<String> querySet = new HashSet<>();

        InstrumentationWindow.console.Echo("Ask Update revision", DONT_SHOW_JOPTION);
        if (JOptionPane.showConfirmDialog(null, "Update revision") == JOptionPane.OK_OPTION) {
            revision = JOptionPane.showInputDialog("New revision number");
            InstrumentationWindow.console.Echo("User choice is OK", DONT_SHOW_JOPTION);
        }
        else {
            revision = instrumentData.getRevision();
            InstrumentationWindow.console.Echo("User choice is CANCEL", DONT_SHOW_JOPTION);
        }

        String query = "UPDATE instruments SET ";
        String queryUDF = "UPDATE UDFs SET UDF01='" + instrumentData.getUDF(1)[0];
        query += "Revision='" + revision;
        query += "', LoopNumber='" + instrumentData.getLoopNumber();
        query += "', InstrumentType='" + instrumentData.getType();
        query += "', PIDNumber='" + instrumentData.getPIDnumber();
        query += "', PipeName='" + instrumentData.getPipeName();
        query += "', EqName='" + instrumentData.getEquipmentName();
        query += "', GeneralNotes='" + instrumentData.getGeneralNotes();
        for (int n = 1; n < DataContainer.N; n ++)
            if (instrumentData.getUDF(n)[0] != null)
                if (n < 10) {
                    if (n > 1) queryUDF += "', UDF0" + n + "='" + instrumentData.getUDF(n)[0];
                    if (instrumentData.getUDF(n)[1] != null) query += "', UDF0" + n + "='" + instrumentData.getUDF(n)[1];
                }
                else {
                    queryUDF += "', UDF" + n + "='" + instrumentData.getUDF(n)[0];
                    if (instrumentData.getUDF(n)[1] != null) query += "', UDF" + n + "='" + instrumentData.getUDF(n)[1];
                }
        query += "' WHERE TagNumber='" + instrumentData.getTagNumber() + "';";
        queryUDF += "' WHERE ItemName='" + instrumentData.getTagNumber() + "';";

        querySet.add(query);
        querySet.add(queryUDF);
        RequestUpdate.Execute(querySet);
    }

    synchronized static void UpdatePipeData(PipeData pipeData) {
        String revision;
        Set<String> querySet = new HashSet<>();

        InstrumentationWindow.console.Echo("Ask Update revision", DONT_SHOW_JOPTION);
        if (JOptionPane.showConfirmDialog(null, "Update revision") == JOptionPane.OK_OPTION) {
            revision = JOptionPane.showInputDialog("New revision number");
            InstrumentationWindow.console.Echo("User choice is OK", DONT_SHOW_JOPTION);
        }
        else {
            revision = pipeData.getRevision();
            InstrumentationWindow.console.Echo("User choice is CANCEL", DONT_SHOW_JOPTION);
        }

        String query = "UPDATE pipelines SET ";
        String queryUDF = "UPDATE UDFs SET UDF01='" + pipeData.getUDF(1)[0];
        query += "Revision='" + revision;
        query += "', PipeName='" + pipeData.getPipeName();
        query += "', GeneralNotes='" + pipeData.getGeneralNotes();
        for (int n = 1; n < DataContainer.N; n ++)
            if (pipeData.getUDF(n)[0] != null)
                if (n < 10) {
                    if (n > 1) queryUDF += "', UDF0" + n + "='" + pipeData.getUDF(n)[0];
                    if (pipeData.getUDF(n)[1] != null) query += "', UDF0" + n + "='" + pipeData.getUDF(n)[1];
                }
                else {
                    queryUDF += "', UDF" + n + "='" + pipeData.getUDF(n)[0];
                    if (pipeData.getUDF(n)[1] != null) query += "', UDF" + n + "='" + pipeData.getUDF(n)[1];
                }
        query += "' WHERE PipeName='" + pipeData.getPipeName() + "';";
        queryUDF += "' WHERE ItemName='" + pipeData.getPipeName() + "';";

        querySet.add(query);
        querySet.add(queryUDF);
        RequestUpdate.Execute(querySet);
    }

    synchronized static void UpdateEquipmentData(EquipmentData equipmentData) {
        String revision;
        Set<String> querySet = new HashSet<>();

        InstrumentationWindow.console.Echo("Ask Update revision", DONT_SHOW_JOPTION);
        if (JOptionPane.showConfirmDialog(null, "Update revision") == JOptionPane.OK_OPTION) {
            InstrumentationWindow.console.Echo("User choice is OK", DONT_SHOW_JOPTION);
            revision = JOptionPane.showInputDialog("New revision number");
        }
        else {
            revision = equipmentData.getRevision();
            InstrumentationWindow.console.Echo("User choice is CANCEL", DONT_SHOW_JOPTION);
        }

        String query = "UPDATE equipment SET ";
        String queryUDF = "UPDATE UDFs SET UDF01='" + equipmentData.getUDF(1)[0];
        query += "Revision='" + revision;
        query += "', EqName='" + equipmentData.getEquipmentName();
        query += "', GeneralNotes='" + equipmentData.getGeneralNotes();
        for (int n = 1; n < DataContainer.N; n ++)
            if (equipmentData.getUDF(n)[0] != null)
                if (n < 10) {
                    if (n > 1) queryUDF += "', UDF0" + n + "='" + equipmentData.getUDF(n)[0];
                    if (equipmentData.getUDF(n)[1] != null) query += "', UDF0" + n + "='" + equipmentData.getUDF(n)[1];
                }
                else {
                    queryUDF += "', UDF" + n + "='" + equipmentData.getUDF(n)[0];
                    if (equipmentData.getUDF(n)[1] != null) query += "', UDF" + n + "='" + equipmentData.getUDF(n)[1];
                }
        query += "' WHERE EqName='" + equipmentData.getEquipmentName() + "';";
        queryUDF += "' WHERE ItemName='" + equipmentData.getEquipmentName() + "';";

        querySet.add(query);
        querySet.add(queryUDF);
        RequestUpdate.Execute(querySet);
    }

    synchronized static void UpdateLoopData(LoopData loopData) {
        String revision;
        Set<String> querySet = new HashSet<>();

        InstrumentationWindow.console.Echo("Ask Update revision", DONT_SHOW_JOPTION);
        if (JOptionPane.showConfirmDialog(null, "Update revision") == JOptionPane.OK_OPTION) {
            InstrumentationWindow.console.Echo("User choice is OK", DONT_SHOW_JOPTION);
            revision = JOptionPane.showInputDialog("New revision number");
        }
        else {
            InstrumentationWindow.console.Echo("User choice is CANCEL", DONT_SHOW_JOPTION);
            revision = loopData.getRevision();
        }

        String query = "UPDATE loops SET ";
        String queryUDF = "UPDATE UDFs SET UDF01='" + loopData.getUDF(1)[0];
        query += "Revision='" + revision;
        query += "', LoopNumber='" + loopData.getLoopNumber();
        query += "', PIDNumber='" + loopData.getPIDNumber();
        query += "', LoopDescription='" + loopData.getLoopDescription();
        query += "', Indication='" + loopData.getIndication();
        query += "', Summ='" + loopData.getSumm();
        query += "', Control='" + loopData.getControl();
        query += "', Alarm='" + loopData.getAlarm();
        query += "', Safety='" + loopData.getSafety();
        query += "', LLL='" + loopData.getLLL_Al();
        query += "', LL='" + loopData.getLL_Al();
        query += "', L='" + loopData.getL_Al();
        query += "', H='" + loopData.getH_Al();
        query += "', HH='" + loopData.getHH_Al();
        query += "', HHH='" + loopData.getHHH_Al();
        query += "', GeneralNotes='" + loopData.getGeneralNotes();

        for (int n = 1; n < DataContainer.N; n ++)
            if (loopData.getUDF(n)[0] != null)
                if (n < 10) {
                    if (n > 1) queryUDF += "', UDF0" + n + "='" + loopData.getUDF(n)[0];
                    if (loopData.getUDF(n)[1] != null) query += "', UDF0" + n + "='" + loopData.getUDF(n)[1];
                }
                else {
                    queryUDF += "', UDF" + n + "='" + loopData.getUDF(n)[0];
                    if (loopData.getUDF(n)[1] != null) query += "', UDF" + n + "='" + loopData.getUDF(n)[1];
                }
        query += "' WHERE LoopNumber='" + loopData.getLoopNumber() + "';";
        queryUDF += "' WHERE ItemName='" + loopData.getLoopNumber() + "';";

        querySet.add(query);
        querySet.add(queryUDF);
        RequestUpdate.Execute(querySet);
    }

    synchronized static void UpdateCableData(CableData cableData) {
        String revision;
        Set<String> querySet = new HashSet<>();

        InstrumentationWindow.console.Echo("Ask Update revision", DONT_SHOW_JOPTION);
        if (JOptionPane.showConfirmDialog(null, "Update revision") == JOptionPane.OK_OPTION) {
            InstrumentationWindow.console.Echo("User choice is OK", DONT_SHOW_JOPTION);
            revision = JOptionPane.showInputDialog("New revision number");
        }
        else {
            InstrumentationWindow.console.Echo("User choice is CANCEL", DONT_SHOW_JOPTION);
            revision = cableData.getRevision();
        }

        String query = "UPDATE cables SET ";
        String queryUDF = "UPDATE UDFs SET UDF01='" + cableData.getUDF(1)[0];
        query += "Revision='" + revision;

        query += "', CableName='" + cableData.getCableName();
        query += "', StartPoint='" + cableData.getStartPoint();
        query += "', FinishPoint='" + cableData.getFinishPoint();
        query += "', SignalType='" + cableData.getSignalType();
        query += "', IS_Proof='" + cableData.getIS_Proof();
        query += "', CableType='" + cableData.getCableType();
        query += "', CableLength='" + cableData.getCableLength();
        query += "', GeneralNotes='" + cableData.getGeneralNotes();

        for (int n = 1; n < DataContainer.N; n ++)
            if (cableData.getUDF(n)[0] != null)
                if (n < 10) {
                    if (n > 1) queryUDF += "', UDF0" + n + "='" + cableData.getUDF(n)[0];
                    if (cableData.getUDF(n)[1] != null) query += "', UDF0" + n + "='" + cableData.getUDF(n)[1];
                }
                else {
                    queryUDF += "', UDF" + n + "='" + cableData.getUDF(n)[0];
                    if (cableData.getUDF(n)[1] != null) query += "', UDF" + n + "='" + cableData.getUDF(n)[1];
                }
        query += "' WHERE CableName='" + cableData.getCableName() + "';";
        queryUDF += "' WHERE ItemName='" + cableData.getCableName() + "';";

        querySet.add(query);
        querySet.add(queryUDF);
        RequestUpdate.Execute(querySet);
    }

    synchronized static void UpdateJBoxData(JBoxData jboxData) {
        String revision;
        Set<String> querySet = new HashSet<>();

        InstrumentationWindow.console.Echo("Ask Update revision", DONT_SHOW_JOPTION);
        if (JOptionPane.showConfirmDialog(null, "Update revision") == JOptionPane.OK_OPTION) {
            InstrumentationWindow.console.Echo("User choice is OK", DONT_SHOW_JOPTION);
            revision = JOptionPane.showInputDialog("New revision number");
        }
        else {
            InstrumentationWindow.console.Echo("User choice is CANCEL", DONT_SHOW_JOPTION);
            revision = jboxData.getRevision();
        }

        String query = "UPDATE jboxes SET ";
        String queryUDF = "UPDATE UDFs SET UDF01='" + jboxData.getUDF(1)[0];
        query += "Revision='" + revision;
        query += "', JBoxName='" + jboxData.getJBoxName();
        query += "', SignalType='" + jboxData.getSignaltype();
        query += "', IS_proof='" + jboxData.getIS_proof();
        query += "', InputsNumber='" + jboxData.getInputsNumber();
        query += "', OutputsNumber='" + jboxData.getOutputsNumber();
        query += "', GeneralNotes='" + jboxData.getGeneralNotes();

        for (int n = 1; n < DataContainer.N; n ++)
            if (jboxData.getUDF(n)[0] != null)
                if (n < 10) {
                    if (n > 1) queryUDF += "', UDF0" + n + "='" + jboxData.getUDF(n)[0];
                    if (jboxData.getUDF(n)[1] != null) query += "', UDF0" + n + "='" + jboxData.getUDF(n)[1];
                }
                else {
                    queryUDF += "', UDF" + n + "='" + jboxData.getUDF(n)[0];
                    if (jboxData.getUDF(n)[1] != null) query += "', UDF" + n + "='" + jboxData.getUDF(n)[1];
                }
        query += "' WHERE CableName='" + jboxData.getJBoxName() + "';";
        queryUDF += "' WHERE ItemName='" + jboxData.getJBoxName() + "';";

        querySet.add(query);
        querySet.add(queryUDF);
        RequestUpdate.Execute(querySet);
    }

    synchronized static void UpdateHookupData(HookupData hookupData) {
        String revision;
        Set<String> querySet = new HashSet<>();

        InstrumentationWindow.console.Echo("Ask Update revision", DONT_SHOW_JOPTION);
        if (JOptionPane.showConfirmDialog(null, "Update revision") == JOptionPane.OK_OPTION) {
            InstrumentationWindow.console.Echo("User choice is OK", DONT_SHOW_JOPTION);
            revision = JOptionPane.showInputDialog("New revision number");
        }
        else {
            InstrumentationWindow.console.Echo("User choice is CANCEL", DONT_SHOW_JOPTION);
            revision = hookupData.getRevision();
        }

        String query = "UPDATE hookups SET ";
        query += "Revision='" + revision;
        query += "', TagNumber='" + hookupData.getTagNumber();
        query += "', GeneralNotes='" + hookupData.getGeneralNotes();

        for (int n = 1; n < DataContainer.N; n ++)
            if (hookupData.getUDF(n)[0] != null)
                if (n < 10) {
                    if (hookupData.getUDF(n)[1] != null) query += "', UDF0" + n + "='" + hookupData.getUDF(n)[1];
                }
                else {
                    if (hookupData.getUDF(n)[1] != null) query += "', UDF" + n + "='" + hookupData.getUDF(n)[1];
                }
        query += "' WHERE TagNumber='" + hookupData.getTagNumber() + "';";

        querySet.add(query);
        RequestUpdate.Execute(querySet);
    }

    synchronized static void UpdateHookupMapping(HookupData hookupData) {
        Set<String> querySet = new HashSet<>();

        String query = "UPDATE UDfs SET UDF01='" + hookupData.getUDF(0)[0];

        for (int n = 2; n < DataContainer.N; n ++)
            if (hookupData.getUDF(n)[0] != null)
                if (n < 10) {
                    if (hookupData.getUDF(n)[1] != null) query += "', UDF0" + n + "='" + hookupData.getUDF(n)[0];
                }
                else {
                    if (hookupData.getUDF(n)[1] != null) query += "', UDF" + n + "='" + hookupData.getUDF(n)[0];
                }
        query += "' WHERE ItemName='hookups';";

        querySet.add(query);
        RequestUpdate.Execute(querySet);
    }

    synchronized static <T> void DeleteItemsFromDatabase(Collection<String> itemsSet, T item) {
        Set<String> querySet = new HashSet<>();
        for (String itemName : itemsSet) {
            if (item instanceof PipeData) {
                querySet.add("DELETE FROM pipelines WHERE PipeName='" + itemName + "';");
                querySet.add("DELETE FROM UDFs WHERE ItemName='" + itemName + "';");
            }
            if (item instanceof EquipmentData) {
                querySet.add("DELETE FROM equipment WHERE EqName='" + itemName + "';");
                querySet.add("DELETE FROM UDFs WHERE ItemName='" + itemName + "';");
            }
            if (item instanceof InstrumentData) {
                querySet.add("DELETE FROM instruments WHERE TagNumber='" + itemName + "';");
                querySet.add("DELETE FROM UDFs WHERE ItemName='" + itemName + "';");
                querySet.add("DELETE FROM hookups WHERE TagNumber='" + itemName + "';");
            }
            if (item instanceof LoopData) {
                querySet.add("DELETE FROM loops WHERE LoopNumber='" + itemName + "';");
                querySet.add("DELETE FROM UDFs WHERE ItemName='" + itemName + "';");
            }
            if (item instanceof CableData) {
                querySet.add("DELETE FROM cables WHERE CableName='" + itemName + "';");
                querySet.add("DELETE FROM UDFs WHERE ItemName='" + itemName + "';");
            }
            if (item instanceof JBoxData) {
                querySet.add("DELETE FROM jboxes WHERE JBoxName='" + itemName + "';");
                querySet.add("DELETE FROM UDFs WHERE ItemName='" + itemName + "';");
            }
        }
        RequestUpdate.Execute(querySet);
    }
}