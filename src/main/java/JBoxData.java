/**
 * Created by y.golota on 05.01.2017.
 */

public class JBoxData {

    private String Revision;
    private String JBoxName;
    private String Signaltype;
    private String IS_proof;
    private String InputsNumber;
    private String OutputsNumber;
    private String GeneralNotes = "";

    private String[][] UDF = new String[DataContainer.N][2];
    private boolean[] visibilityUDF = new boolean[DataContainer.N];
    private boolean[] editabilityUDF = new boolean[DataContainer.N];

    public JBoxData() { }

    public JBoxData(String JBoxName) {
        this.Revision = "";
        this.JBoxName = JBoxName;
        this.Signaltype = "";
        this.IS_proof = "";
        this.InputsNumber = "";
        this.OutputsNumber = "";
        this.GeneralNotes = "";

        for (int i = 1; i < DataContainer.N; i ++) {
            this.UDF[i][0] = "";
            this.UDF[i][1] = "";
        }
    }

    public JBoxData(String[][] data) {
        this.Revision = data[0][2];
        this.JBoxName = data[1][2];
        this.Signaltype = data[2][2];
        this.IS_proof = data[3][2];
        this.InputsNumber = data[4][2];
        this.OutputsNumber = data[5][2];
        this.GeneralNotes = data[6][2];

        for (int i = 1; i < DataContainer.N; i ++)
            try {
                if (data[i + 6][1] != null) {
                    this.UDF[i][0] = data[i + 6][1];
                    this.UDF[i][1] = data[i + 6][2];
                }
            }
            catch (NullPointerException npe) {}
    }

    public void setRevision(String Revision) {
        this.Revision = Revision;
    }

    public void setJBoxName(String JBoxName) {
        this.JBoxName = JBoxName;
    }

    public void setSignaltype(String Signaltype) {
        this.Signaltype = Signaltype;
    }

    public void setIS_proof(String IS_proof) {
        this.IS_proof = IS_proof;
    }

    public void setInputsNumber(String InputsNumber) {
        this.InputsNumber = InputsNumber;
    }

    public void setOutputsNumber(String OutputsNumber) {
        this.OutputsNumber = OutputsNumber;
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

    public String getJBoxName() {
        return this.JBoxName;
    }

    public String getSignaltype() {
        return this.Signaltype;
    }

    public String getIS_proof() {
        return this.IS_proof;
    }

    public String getInputsNumber() {
        return this.InputsNumber;
    }

    public String getOutputsNumber() {
        return this.OutputsNumber;
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
        dataTable[1][1] = "JBoxName";
        dataTable[1][2] = JBoxName;
        dataTable[2][0] = "constant";
        dataTable[2][1] = "Signaltype";
        dataTable[2][2] = Signaltype;
        dataTable[3][0] = "constant";
        dataTable[3][1] = "IS_proof";
        dataTable[3][2] = IS_proof;
        dataTable[4][0] = "constant";
        dataTable[4][1] = "InputsNumber";
        dataTable[4][2] = InputsNumber;
        dataTable[5][0] = "constant";
        dataTable[5][1] = "OutputsNumber";
        dataTable[5][2] = OutputsNumber;
        dataTable[6][0] = "constant";
        dataTable[6][1] = "GeneralNotes";
        dataTable[6][2] = GeneralNotes;

        for (int i = 1; i < DataContainer.N; i ++) {
            dataTable[i + 6][0] = "UDF[" + i + "] field";
            if (UDF[i][0] != null) {
                dataTable[i + 6][1] = UDF[i][0];
                if (UDF[i][1] != null) dataTable[i + 6][2] = UDF[i][1];
            }
        }

        return dataTable;
    }
}