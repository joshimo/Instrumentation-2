/**
 * Created by y.golota on 30.12.2016.
 */

import java.io.Serializable;
import java.util.*;
import java.util.Map;

public class EquipmentData {

    private String Revision;
    private String EquipmentName;
    private String GeneralNotes;
    private String[][] UDF = new String[DataContainer.N][2];
    private boolean[] visibilityUDF = new boolean[DataContainer.N];
    private boolean[] editabilityUDF = new boolean[DataContainer.N];

    public EquipmentData() {}

    public EquipmentData(String EquipmentName) {
        this.Revision = "";
        this.EquipmentName = EquipmentName;
        this.GeneralNotes = "";

        for (int i = 1; i < DataContainer.N; i ++) {
            this.UDF[i][0] = "";
            this.UDF[i][1] = "";
        }
    }

    public EquipmentData(String[][] data) {
        this.Revision = data[0][2];
        this.EquipmentName = data[1][2];
        this.GeneralNotes = data[2][2];

        for (int i = 1; i < DataContainer.N; i ++)
            try {
                if (data[i + 2][1] != null) {
                    this.UDF[i][0] = data[i + 2][1];
                    this.UDF[i][1] = data[i + 2][2];
                }
            }
            catch (NullPointerException npe) {}
    }

    public void setRevision(String Revision) {
        this.Revision = Revision;
    }

    public void setEquipmentName(String EquipmentName) {
        this.EquipmentName = EquipmentName;
    }

    public void setGeneralNotes(String GeneralNotes) {
        this.GeneralNotes = GeneralNotes;
    }

    public void setUDF(int i, String UDFName, String UDFValue) {
        this.UDF[i][0] = UDFName;
        this.UDF[i][1] = UDFValue;
    }

    public void setUDFVisibility(int i, boolean UDFVisibility) {
        this.visibilityUDF[i] = UDFVisibility;
    }

    public void setUDFEditability(int i, boolean UDFEditability) {
        this.editabilityUDF[i] = UDFEditability;
    }

    public String getRevision() {
        return this.Revision;
    }

    public String getEquipmentName() {
        return this.EquipmentName;
    }

    public String getGeneralNotes() {
        return this.GeneralNotes;
    }

    public String[] getUDF(int i) {
        return this.UDF[i];
    }

    public boolean getUDFVisibility(int i) {
        return this.visibilityUDF[i];
    }

    public boolean getUDFEditability(int i) {
        return this.editabilityUDF[i];
    }

    public String[][] getDataTable() {
        String[][] dataTable = new String[DataContainer.N + 2][3];
        dataTable[0][0] = "constant";
        dataTable[0][1] = "Revision";
        dataTable[0][2] = Revision;
        dataTable[1][0] = "constant";
        dataTable[1][1] = "EquipmentName";
        dataTable[1][2] = EquipmentName;
        dataTable[2][0] = "constant";
        dataTable[2][1] = "GeneralNotes";
        dataTable[2][2] = GeneralNotes;

        for (int i = 1; i < DataContainer.N; i ++) {
            dataTable[i + 2][0] = "UDF[" + i + "] field";
            if (UDF[i][0] != null) {
                dataTable[i + 2][1] = UDF[i][0];
                if (UDF[i][1] != null) dataTable[i + 2][2] = UDF[i][1];
            }
        }

        return dataTable;
    }
}