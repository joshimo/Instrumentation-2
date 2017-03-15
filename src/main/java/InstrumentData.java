/**
 * Created by y.golota on 30.12.2016.
 */

public class InstrumentData {

    public final String TG = "Temperature guage";
    public final String TS = "Temperature switch";
    public final String TE = "Thermo element";
    public final String TT = "Temperature transmitter";
    public final String PG = "Pressure guage";
    public final String PT = "Pressure transmitter";
    public final String PDT = "dPressure transmitter";
    public final String PDG = "dPressure guage";
    public final String PS = "Pressure switch";
    public final String FT = "Flowmeter";
    public final String FG = "Flow guage";
    public final String FS = "Flow switch";
    public final String FE = "Orifice plate";
    public final String LT = "Level meter";
    public final String LG = "Level guage (level glass)";
    public final String LS = "Level switch";
    public final String CV = "Control valve";
    public final String UV = "On-Off valve";
    public final String QT = "Analyzer";
    public final String SV = "Solenoid valve";
    public final String MT = "Moisture transmitter";

    private String Revision;
    private String TagNumber;
    private String LoopNumber;
    private String PIDnumber;
    private String EquipmentName;
    private String PipeName;
    private String Type;
    private String GeneralNotes = "";
    private String[][] UDF = new String[DataContainer.N][2];
    private boolean[] visibilityUDF = new boolean[DataContainer.N];
    private boolean[] editabilityUDF = new boolean[DataContainer.N];
    private String dataTable;

    public InstrumentData() {}

    public InstrumentData(String TagNumber) {
        this.Revision = "";
        this.TagNumber = TagNumber;
        this.LoopNumber = "";
        this.PIDnumber = "";
        this.EquipmentName = "";
        this.PipeName = "";
        this.Type = "";
        this.GeneralNotes = "";

        for (int i = 1; i < DataContainer.N; i ++) {
            this.UDF[i][0] = "";
            this.UDF[i][1] = "";
        }
    }

    public InstrumentData (String[][] data) {
        this.Revision = data[0][2];
        this.TagNumber = data[1][2];
        this.LoopNumber = data[2][2];
        this.PIDnumber = data[3][2];
        this.EquipmentName = data[4][2];
        this.PipeName = data[5][2];
        this.Type = data[6][2];
        this.GeneralNotes = data[7][2];

        for (int i = 1; i < DataContainer.N; i ++)
            try {
                if (data[i + 7][0] != null) {
                    this.UDF[i][0] = data[i + 7][1];
                    this.UDF[i][1] = data[i + 7][2];
                }
            }
            catch (NullPointerException npe) {}
    }

    public void setRevision(String Revision) {
        this.Revision = Revision;
    }

    public void setTagNumber(String TagNumber) {
        this.TagNumber = TagNumber;
    }

    public void setLoopNumber(String LoopNumber) {
        this.LoopNumber = LoopNumber;
    }

    public void setPIDnumber(String PIDnumber) {
        this.PIDnumber = PIDnumber;
    }

    public void setEquipmentName(String EquipmentName) {
        this.EquipmentName = EquipmentName;
    }

    public void setPipeName(String PipeName) {
        this.PipeName = PipeName;
    }

    public void setType(String Type) {
        this.Type = Type;
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

    public String getTagNumber() {
        return this.TagNumber;
    }

    public String getLoopNumber() {
        return this.LoopNumber;
    }

    public String getPIDnumber() {
        return this.PIDnumber;
    }

    public String getEquipmentName() {
        return this.EquipmentName;
    }

    public String getPipeName() {
        return this.PipeName;
    }

    public String getType() {
        return this.Type;
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
        String[][] dataTable = new String[DataContainer.N + 7][3];
        dataTable[0][0] = "constant";
        dataTable[0][1] = "Revision";
        dataTable[0][2] = Revision;
        dataTable[1][0] = "constant";
        dataTable[1][1] = "TagNumber";
        dataTable[1][2] = TagNumber;
        dataTable[2][0] = "constant";
        dataTable[2][1] = "LoopNumber";
        dataTable[2][2] = LoopNumber;
        dataTable[3][0] = "constant";
        dataTable[3][1] = "PIDnumber";
        dataTable[3][2] = PIDnumber;
        dataTable[4][0] = "constant";
        dataTable[4][1] = "EquipmentName";
        dataTable[4][2] = EquipmentName;
        dataTable[5][0] = "constant";
        dataTable[5][1] = "PipeName";
        dataTable[5][2] = PipeName;
        dataTable[6][0] = "constant";
        dataTable[6][1] = "Type";
        dataTable[6][2] = Type;
        dataTable[7][0] = "constant";
        dataTable[7][1] = "GeneralNotes";
        dataTable[7][2] = GeneralNotes;

        for (int i = 1; i < DataContainer.N; i ++) {
            dataTable[i + 7][0] = "UDF[" + i + "] field";
            if (UDF[i][0] != null) {
                dataTable[i + 7][1] = UDF[i][0];
                if (UDF[i][1] != null) dataTable[i + 7][2] = UDF[i][1];
            }
        }

        return dataTable;
    }
}