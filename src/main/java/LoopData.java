/**
 * Created by y.golota on 30.12.2016.
 */

public class LoopData {

    private String Revision;
    private String LoopNumber;
    private String PIDNumber;
    private String LoopDescription;
    private String Indication;
    private String Summ;
    private String Control;
    private String Alarm;
    private String Safety;
    private String LLL_Al;
    private String LL_Al;
    private String L_Al;
    private String H_Al;
    private String HH_Al;
    private String HHH_Al;
    private String GeneralNotes;

    private String[][] UDF = new String[DataContainer.N][2];
    private boolean[] visibilityUDF = new boolean[DataContainer.N];
    private boolean[] editabilityUDF = new boolean[DataContainer.N];

    public LoopData () {}

    public LoopData(String LoopNumber) {
        this.Revision = "";
        this.LoopNumber = LoopNumber;
        this.PIDNumber = "";
        this.LoopDescription = "";
        this.Indication = "";
        this.Summ = "";
        this.Control = "";
        this.Alarm = "";
        this.Safety = "";
        this.LLL_Al = "";
        this.LL_Al = "";
        this.L_Al = "";
        this.H_Al = "";
        this.HH_Al = "";
        this.HHH_Al = "";
        this.GeneralNotes = "";

        for (int i = 1; i < DataContainer.N; i ++) {
            this.UDF[i][0] = "";
            this.UDF[i][1] = "";
        }
    }

    public LoopData(String[][] data) {
        this.Revision = data[0][2];
        this.LoopNumber = data[1][2];
        this.PIDNumber = data[2][2];
        this.LoopDescription = data[3][2];
        this.Indication = data[4][2];
        this.Summ = data[5][2];
        this.Control = data[6][2];
        this.Alarm = data[7][2];
        this.Safety = data[8][2];
        this.LLL_Al = data[9][2];
        this.LL_Al = data[10][2];
        this.L_Al = data[11][2];
        this.H_Al = data[12][2];
        this.HH_Al = data[13][2];
        this.HHH_Al = data[14][2];
        this.GeneralNotes = data[15][2];
        
        for (int i = 1; i < DataContainer.N; i ++)
            try {
                if (data[i + 15][1] != null) {
                    this.UDF[i][0] = data[i + 15][1];
                    this.UDF[i][1] = data[i + 15][2];
                }
            }
            catch (NullPointerException npe) {}
    }

    public void setRevision(String Revision) {
        this.Revision = Revision;
    }

    public void setLoopNumber(String LoopNumber) {
        this.LoopNumber = LoopNumber;
    }

    public void setPIDNumber(String PIDNumber) {
        this.PIDNumber = PIDNumber;
    }

    public void setLoopDescription(String LoopDescription) {
        this.LoopDescription = LoopDescription;
    }

    public void setIndication(String Indication) {
        this.Indication = Indication;
    }

    public void setSumm(String Summ) {
        this.Summ = Summ;
    }

    public void setControl(String Control) {
        this.Control = Control;
    }

    public void setAlarm(String Alarm) {
        this.Alarm = Alarm;
    }

    public void setSafety(String Safety) {
        this.Safety = Safety;
    }

    public void setLLL_Al(String LLL_Al) {
        this.LLL_Al = LLL_Al;
    }

    public void setLL_Al(String LL_Al) {
        this.LL_Al = LL_Al;
    }

    public void setL_Al(String L_Al) {
        this.L_Al = L_Al;
    }

    public void setH_Al(String H_Al) {
        this.H_Al = H_Al;
    }

    public void setHH_Al(String HH_Al) {
        this.HH_Al = HH_Al;
    }

    public void setHHH_Al(String HHH_Al) {
        this.HHH_Al = HHH_Al;
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

    public String getLoopNumber() {
        return this.LoopNumber;
    }

    public String getPIDNumber() {
        return this.PIDNumber;
    }

    public String getLoopDescription() {
        return this.LoopDescription;
    }

    public String getIndication() {
        return this.Indication;
    }

    public String getSumm() {
        return this.Summ;
    }

    public String getControl() {
        return this.Control;
    }

    public String getAlarm() {
        return this.Alarm;
    }

    public String getSafety() {
        return this.Safety;
    }

    public String getLLL_Al() {
        return this.LLL_Al;
    }

    public String getLL_Al() {
        return this.LL_Al;
    }

    public String getL_Al() {
        return this.L_Al;
    }

    public String getH_Al() {
        return this.H_Al;
    }

    public String getHH_Al() {
        return this.HH_Al;
    }

    public String getHHH_Al() {
        return this.HHH_Al;
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
        String[][] dataTable = new String[DataContainer.N + 16][3];

        dataTable[0][0] = "constant";
        dataTable[0][1] = "Revision";
        dataTable[0][2] = Revision;
        dataTable[1][0] = "constant";
        dataTable[1][1] = "LoopNumber";
        dataTable[1][2] = LoopNumber;
        dataTable[2][0] = "constant";
        dataTable[2][1] = "PIDNumber";
        dataTable[2][2] = PIDNumber;
        dataTable[3][0] = "constant";
        dataTable[3][1] = "LoopDescription";
        dataTable[3][2] = LoopDescription;
        dataTable[4][0] = "constant";
        dataTable[4][1] = "Indication";
        dataTable[4][2] = Indication;
        dataTable[5][0] = "constant";
        dataTable[5][1] = "Summ";
        dataTable[5][2] = Summ;
        dataTable[6][0] = "constant";
        dataTable[6][1] = "Control";
        dataTable[6][2] = Control;
        dataTable[7][0] = "constant";
        dataTable[7][1] = "Alarm";
        dataTable[7][2] = Alarm;
        dataTable[8][0] = "constant";
        dataTable[8][1] = "Safety";
        dataTable[8][2] = Safety;
        dataTable[9][0] = "constant";
        dataTable[9][1] = "LLL_Al";
        dataTable[9][2] = LLL_Al;
        dataTable[10][0] = "constant";
        dataTable[10][1] = "LL_Al";
        dataTable[10][2] = LL_Al;
        dataTable[11][0] = "constant";
        dataTable[11][1] = "L_Al";
        dataTable[11][2] = L_Al;
        dataTable[12][0] = "constant";
        dataTable[12][1] = "H_Al";
        dataTable[12][2] = H_Al;
        dataTable[13][0] = "constant";
        dataTable[13][1] = "HH_Al";
        dataTable[13][2] = HH_Al;
        dataTable[14][0] = "constant";
        dataTable[14][1] = "HHH_Al";
        dataTable[14][2] = HHH_Al;
        dataTable[15][0] = "constant";
        dataTable[15][1] = "GeneralNotes";
        dataTable[15][2] = GeneralNotes;

        for (int i = 1; i < DataContainer.N; i ++) {
            dataTable[i + 15][0] = "UDF[" + i + "] field";
            if (UDF[i][0] != null) {
                dataTable[i + 15][1] = UDF[i][0];
                if (UDF[i][1] != null) dataTable[i + 15][2] = UDF[i][1];
            }
        }

        return dataTable;
    }
}