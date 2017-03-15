/**
 * Created by y.golota on 30.12.2016.
 */


public class CableData {
    private String Revision;
    private String CableName;
    private String StartPoint;
    private String FinishPoint;
    private String SignalType;
    private String IS_Proof;
    private String CableType;
    private String CableLength;
    private String GeneralNotes;

    private String[][] UDF = new String[DataContainer.N][2];
    private boolean[] visibilityUDF = new boolean[DataContainer.N];
    private boolean[] editabilityUDF = new boolean[DataContainer.N];

    public CableData() {}
    
    public CableData (String CableName) {
        this.Revision = "";
        this.CableName = CableName;
        this.StartPoint = "";
        this.FinishPoint = "";
        this.SignalType = "";
        this.IS_Proof = "";
        this.CableType = "";
        this.CableLength = "";
        this.GeneralNotes = "";

        for (int i = 1; i < DataContainer.N; i ++) {
            this.UDF[i][0] = "";
            this.UDF[i][1] = "";
        }
    }
    
    public CableData(String[][] data) {
        this.Revision = data[0][2];
        this.CableName = data[1][2];
        this.StartPoint = data[2][2];
        this.FinishPoint = data[3][2];
        this.SignalType = data[4][2];
        this.IS_Proof = data[5][2];
        this.CableType = data[6][2];
        this.CableLength = data[7][2];
        this.GeneralNotes = data[8][2];
        
        for (int i = 1; i < DataContainer.N; i ++)
            try {
                if (data[i + 2][1] != null) {
                    this.UDF[i][0] = data[i + 8][1];
                    this.UDF[i][1] = data[i + 8][2];
                }
            }
            catch (NullPointerException npe) {}
    }

    public void setRevision(String Revision) {
        this.Revision = Revision;
    }

    public void setCableName(String CableName) {
        this.CableName = CableName;
    }

    public void setStartPoint(String StartPoint) {
        this.StartPoint = StartPoint;
    }

    public void setFinishPoint(String FinishPoint) {
        this.FinishPoint = FinishPoint;
    }

    public void setSignalType(String SignalType) {
        this.SignalType = SignalType;
    }

    public void setIS_Proof(String IS_Proof) {
        this.IS_Proof = IS_Proof;
    }

    public void setCableType(String CableType) {
        this.CableType = CableType;
    }

    public void setCableLength(String CableLength) {
        this.CableLength = CableLength;
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

    public String getCableName() {
        return this.CableName;
    }

    public String getStartPoint() {
        return this.StartPoint;
    }

    public String getFinishPoint() {
        return this.FinishPoint;
    }

    public String getSignalType() {
        return this.SignalType;
    }

    public String getIS_Proof() {
        return this.IS_Proof;
    }

    public String getCableType() {
        return this.CableType;
    }

    public String getCableLength() {
        return this.CableLength;
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
        String[][] dataTable = new String[DataContainer.N + 9][3];

        dataTable[0][0] = "constant";
        dataTable[0][1] = "Revision";
        dataTable[0][2] = Revision;
        dataTable[1][0] = "constant";
        dataTable[1][1] = "CableName";
        dataTable[1][2] = CableName;
        dataTable[2][0] = "constant";
        dataTable[2][1] = "StartPoint";
        dataTable[2][2] = StartPoint;
        dataTable[3][0] = "constant";
        dataTable[3][1] = "FinishPoint";
        dataTable[3][2] = FinishPoint;
        dataTable[4][0] = "constant";
        dataTable[4][1] = "SignalType";
        dataTable[4][2] = SignalType;
        dataTable[5][0] = "constant";
        dataTable[5][1] = "IS_Proof";
        dataTable[5][2] = IS_Proof;
        dataTable[6][0] = "constant";
        dataTable[6][1] = "CableType";
        dataTable[6][2] = CableType;
        dataTable[7][0] = "constant";
        dataTable[7][1] = "CableLength";
        dataTable[7][2] = CableLength;
        dataTable[8][0] = "constant";
        dataTable[8][1] = "GeneralNotes";
        dataTable[8][2] = GeneralNotes;

        for (int i = 1; i < DataContainer.N; i ++) {
            dataTable[i + 8][0] = "UDF[" + i + "] field";
            if (UDF[i][0] != null) {
                dataTable[i + 8][1] = UDF[i][0];
                if (UDF[i][1] != null) dataTable[i + 8][2] = UDF[i][1];
            }
        }

        return dataTable;
    }
}