/**
 * Created by y.golota on 30.12.2016.
 */

import java.io.Serializable;
import java.util.*;

public class DataContainer implements Serializable, Cloneable {

    private String ProjectNumber = "Testing";
    static int N = 65;

    private HashMap <String, PipeData> Pipe_Container = new HashMap <String, PipeData>();
    private HashMap <String, EquipmentData> EquipmentData_Container = new HashMap <String, EquipmentData>();
    private HashMap <String, InstrumentData> InstrumentData_Container = new HashMap <String, InstrumentData>();
    private HashMap <String, LoopData> LoopData_Container = new HashMap <String, LoopData>();
    private HashMap <String, CableData> CableData_Container = new HashMap <String, CableData>();
    private HashMap <String, JBoxData> JBoxData_Container = new HashMap <String, JBoxData>();
    private HashMap <String, HookupData> HookupData_Container = new HashMap <String, HookupData>();

    public void setProjectNumber(String pn) {
        this.ProjectNumber = pn;
    }

    public String getProjectNumber() {
        return this.ProjectNumber;
    }

    <T> void PutData(T data) {
        if (data instanceof InstrumentData)
            InstrumentData_Container.put(((InstrumentData) data).getTagNumber(), (InstrumentData) data);
        else
            if (data instanceof EquipmentData)
                EquipmentData_Container.put(((EquipmentData) data).getEquipmentName(), (EquipmentData) data);
            else
                if (data instanceof PipeData)
                    Pipe_Container.put(((PipeData) data).getPipeName(), (PipeData) data);
                else
                    if (data instanceof LoopData)
                        LoopData_Container.put(((LoopData) data).getLoopNumber(), (LoopData) data);
                    else
                        if (data instanceof CableData)
                            CableData_Container.put(((CableData) data).getCableName(), (CableData) data);
                        else
                            if (data instanceof JBoxData)
                                JBoxData_Container.put(((JBoxData) data).getJBoxName(), (JBoxData) data);
                            else
                                if (data instanceof HookupData)
                                    HookupData_Container.put(((HookupData) data).getTagNumber(), (HookupData) data);
    }

    <T> void PutContainerData(HashMap<String, T> data, T p) {
        if (p instanceof PipeData) Pipe_Container.putAll((HashMap <String, PipeData>) data);
        if (p instanceof EquipmentData) EquipmentData_Container.putAll((HashMap <String, EquipmentData>) data);
        if (p instanceof InstrumentData) InstrumentData_Container.putAll((HashMap <String, InstrumentData>) data);
        if (p instanceof LoopData) LoopData_Container.putAll((HashMap <String, LoopData>) data);
        if (p instanceof CableData) CableData_Container.putAll((HashMap <String, CableData>) data);
        if (p instanceof JBoxData) JBoxData_Container.putAll((HashMap <String, JBoxData>) data);
        if (p instanceof HookupData) HookupData_Container.putAll((HashMap <String, HookupData>) data);
    }

    void GenerateLoopsFromInstruments() {
        Set<String> instrumentList = getList(new InstrumentData());
        try {
            for (String tagNumber : instrumentList) {
                InstrumentData instrumentData = InstrumentData_Container.get(tagNumber);

                if (instrumentData.getLoopNumber().length() > 3) {

                    LoopData loopData = new LoopData(instrumentData.getLoopNumber());
                    loopData.setRevision(instrumentData.getRevision());
                    loopData.setPIDNumber(instrumentData.getPIDnumber());

                    String loopFunction;

                    if (Import.tryPreffix(loopData.getLoopNumber()))
                        loopFunction = loopData.getLoopNumber().split("-")[1];
                    else
                        loopFunction = loopData.getLoopNumber().split("-")[0];

                    switch (loopFunction.charAt(0)) {
                        case 'T':
                            loopData.setLoopDescription("Temperature");
                            break;
                        case 'F':
                            loopData.setLoopDescription("Flow");
                            break;
                        case 'L':
                            loopData.setLoopDescription("Level");
                            break;
                        case 'M':
                            loopData.setLoopDescription("Moisture");
                            break;
                        case 'P':
                            loopData.setLoopDescription("Pressure");
                            if (loopFunction.charAt(1) == 'D') loopData.setLoopDescription("Differential pressure");
                            break;
                        case 'Q':
                            loopData.setLoopDescription("Concentration");
                            break;
                        case 'A':
                            loopData.setLoopDescription("Analyze");
                            break;
                        case 'U':
                            loopData.setLoopDescription("Multi parameter loop");
                            break;
                        default:
                            break;
                    }

                    if (loopFunction.length() == 2) {
                        switch (loopFunction.charAt(1)) {
                            case 'I':
                                loopData.setLoopDescription(loopData.getLoopDescription() + " indication");
                                loopData.setIndication("yes");
                                break;
                            case 'Y':
                                loopData.setLoopDescription(loopData.getLoopDescription() + " calculation");
                                break;
                            case 'C':
                                loopData.setLoopDescription(loopData.getLoopDescription() + " control");
                                loopData.setControl("yes");
                                break;
                            case 'A':
                                loopData.setLoopDescription(loopData.getLoopDescription() + " alarm");
                                loopData.setAlarm("yes");
                                break;
                            case 'S':
                                loopData.setLoopDescription(loopData.getLoopDescription() + " interlock");
                                loopData.setSafety("yes");
                                break;
                            default:
                                break;
                        }
                    }

                    if (loopFunction.length() == 3) {
                        switch (loopFunction.substring(1,3)) {
                            case "DI":
                                loopData.setLoopDescription(loopData.getLoopDescription() + " indication");
                                loopData.setIndication("yes");
                                break;
                            case "IA":
                                loopData.setLoopDescription(loopData.getLoopDescription() + " indication and alarm");
                                loopData.setIndication("yes");
                                loopData.setAlarm("yes");
                                break;
                            case "QI":
                            case "IQ":
                                loopData.setLoopDescription(loopData.getLoopDescription() + " indication and calculation");
                                loopData.setIndication("yes");
                                loopData.setSumm("yes");
                                break;
                            case "YI":
                            case "IY":
                                loopData.setLoopDescription(loopData.getLoopDescription() + " indication and calculation");
                                loopData.setIndication("yes");
                                break;
                            case "IC":
                                loopData.setLoopDescription(loopData.getLoopDescription() + " indication and control");
                                loopData.setIndication("yes");
                                loopData.setControl("yes");
                                break;
                            case "AL":
                                loopData.setLoopDescription(loopData.getLoopDescription() + " low threshold alarm");
                                loopData.setAlarm("yes");
                                loopData.setL_Al("yes");
                                break;
                            case "AH":
                                loopData.setLoopDescription(loopData.getLoopDescription() + " high threshold alarm");
                                loopData.setAlarm("yes");
                                loopData.setH_Al("yes");
                                break;
                        }
                    }

                    if (loopFunction.length() == 4) {
                        switch (loopFunction.charAt(1)) {
                            case 'I':
                                loopData.setLoopDescription(loopData.getLoopDescription() + " indication and");
                                loopData.setIndication("yes");
                                break;
                            case 'Q':
                                loopData.setLoopDescription(loopData.getLoopDescription() + " calculation and");
                                loopData.setSumm("yes");
                                break;
                            case 'Y':
                                loopData.setLoopDescription(loopData.getLoopDescription() + " calculation and");
                                break;
                            case 'C':
                                loopData.setLoopDescription(loopData.getLoopDescription() + " control and");
                                loopData.setControl("yes");
                                break;
                            default:
                                break;
                        }

                        if (loopFunction.contains("CA")) {
                            loopData.setLoopDescription(loopData.getLoopDescription() + " control and alarm");
                            loopData.setControl("yes");
                            loopData.setAlarm("yes");
                        }
                        if (loopFunction.contains("SA") | loopFunction.contains("AS")) {
                            loopData.setLoopDescription(loopData.getLoopDescription() + " alarm and safety interlock");
                            loopData.setAlarm("yes");
                            loopData.setSafety("yes");
                        }
                        if (loopFunction.contains("AL")) {
                            loopData.setLoopDescription(loopData.getLoopDescription() + " low threshold alarm");
                            loopData.setAlarm("yes");
                            loopData.setL_Al("yes");
                        }
                        if (loopFunction.contains("AH")) {
                            loopData.setLoopDescription(loopData.getLoopDescription() + " high threshold alarm");
                            loopData.setAlarm("yes");
                            loopData.setH_Al("yes");
                        }
                        if (loopFunction.contains("ALL")) {
                            loopData.setLoopDescription(loopData.getLoopDescription() + " low-low threshold alarm");
                            loopData.setAlarm("yes");
                            loopData.setSafety("yes");
                            loopData.setLL_Al("yes");
                        }
                        if (loopFunction.contains("AHH")) {
                            loopData.setLoopDescription(loopData.getLoopDescription() + " high-high threshold alarm");
                            loopData.setSafety("yes");
                            loopData.setAlarm("yes");
                            loopData.setHH_Al("yes");
                        }
                        if (loopFunction.contains("SLL")) {
                            loopData.setLoopDescription(loopData.getLoopDescription() + " low-low threshold safety interlock");
                            loopData.setSafety("yes");
                            loopData.setLL_Al("yes");
                        }
                        if (loopFunction.contains("SHH")) {
                            loopData.setLoopDescription(loopData.getLoopDescription() + " high-high threshold safety interlock");
                            loopData.setSafety("yes");
                            loopData.setHH_Al("yes");
                        }
                    }

                    if (loopFunction.length() >= 5) {
                        switch (loopFunction.charAt(1)) {
                            case 'I':
                                loopData.setLoopDescription(loopData.getLoopDescription() + " indication,");
                                loopData.setIndication("yes");
                                break;
                            case 'Q':
                                loopData.setLoopDescription(loopData.getLoopDescription() + " calculation,");
                                loopData.setSumm("yes");
                                break;
                            case 'Y':
                                loopData.setLoopDescription(loopData.getLoopDescription() + " calculation,");
                                break;
                            case 'C':
                                loopData.setLoopDescription(loopData.getLoopDescription() + " control,");
                                loopData.setControl("yes");
                                break;
                            default:
                                break;
                        }
                        switch (loopFunction.charAt(2)) {
                            case 'I':
                                loopData.setLoopDescription(loopData.getLoopDescription() + " indication and");
                                loopData.setIndication("yes");
                                break;
                            case 'Q':
                                loopData.setLoopDescription(loopData.getLoopDescription() + " calculation and");
                                loopData.setSumm("yes");
                                break;
                            case 'Y':
                                loopData.setLoopDescription(loopData.getLoopDescription() + " calculation and");
                                break;
                            case 'C':
                                loopData.setLoopDescription(loopData.getLoopDescription() + " control and");
                                loopData.setControl("yes");
                                break;
                            default:
                                break;
                        }
                        if (loopFunction.contains("AL") & ! loopFunction.contains("ALL") & ! loopFunction.contains("SALL")) {
                            loopData.setLoopDescription(loopData.getLoopDescription() + " low threshold alarm");
                            loopData.setAlarm("yes");
                            loopData.setL_Al("yes");
                        }
                        if (loopFunction.contains("AH") & ! loopFunction.contains("AHH") & ! loopFunction.contains("SAHH")) {
                            loopData.setLoopDescription(loopData.getLoopDescription() + " high threshold alarm");
                            loopData.setAlarm("yes");
                            loopData.setH_Al("yes");
                        }
                        if (loopFunction.contains("ALL") & ! loopFunction.contains("SALL")) {
                            loopData.setLoopDescription(loopData.getLoopDescription() + " low-low threshold alarm");
                            loopData.setAlarm("yes");
                            loopData.setLL_Al("yes");
                        }
                        if (loopFunction.contains("AHH") & ! loopFunction.contains("SAHH")) {
                            loopData.setLoopDescription(loopData.getLoopDescription() + " high-high threshold alarm");
                            loopData.setSafety("yes");
                            loopData.setHH_Al("yes");
                        }
                        if (loopFunction.contains("SLL")) {
                            loopData.setLoopDescription(loopData.getLoopDescription() + " low-low threshold safety interlock");
                            loopData.setSafety("yes");
                            loopData.setLL_Al("yes");
                        }
                        if (loopFunction.contains("SHH")) {
                            loopData.setLoopDescription(loopData.getLoopDescription() + " high-high threshold safety interlock");
                            loopData.setSafety("yes");
                            loopData.setHH_Al("yes");
                        }
                        if (loopFunction.contains("SALL")) {
                            loopData.setLoopDescription(loopData.getLoopDescription() + " low-low threshold safety interlock and alarm");
                            loopData.setSafety("yes");
                            loopData.setAlarm("yes");
                            loopData.setLL_Al("yes");
                        }
                        if (loopFunction.contains("SAHH")) {
                            loopData.setLoopDescription(loopData.getLoopDescription() + " high-high threshold safety interlock and alarm");
                            loopData.setSafety("yes");
                            loopData.setAlarm("yes");
                            loopData.setHH_Al("yes");
                        }
                    }

                    if (instrumentData.getPipeName().length() > 3) loopData.setLoopDescription(loopData.getLoopDescription() + " in " + instrumentData.getPipeName());
                    if (instrumentData.getEquipmentName().length() >= 3) loopData.setLoopDescription(loopData.getLoopDescription() + " in " + instrumentData.getEquipmentName());

                    LoopData_Container.put(loopData.getLoopNumber(), loopData);
                    //System.out.println(loopData.getLoopNumber() + ", " + loopData.getLoopDescription());
                }
            }
        }
        catch (NullPointerException npe) {
            System.out.println("Null pointer Exception in 'void GenerateLoopsFromInstruments()'");
        }
        catch (Exception e) {
            System.out.println("Exception in 'void GenerateLoopsFromInstruments()'");
        }

    }

    <T> T GetData(String name, T p) {
        if (p instanceof  PipeData)
            return (T) Pipe_Container.get(name);
        else
            if (p instanceof  EquipmentData)
                return(T) EquipmentData_Container.get(name);
            else
                if (p instanceof  InstrumentData)
                    return (T) InstrumentData_Container.get(name);
                else
                    if (p instanceof  LoopData)
                        return (T) LoopData_Container.get(name);
                    else
                        if (p instanceof  CableData)
                            return (T) CableData_Container.get(name);
                        else
                            if (p instanceof  JBoxData)
                                return (T) JBoxData_Container.get(name);
                            else
                                if (p instanceof  HookupData)
                                    return (T) HookupData_Container.get(name);
                                else
                                    return null;
    }

    <T> HashMap<String, T> GetContainerData(T p) {
        if (p instanceof PipeData) return (HashMap <String, T>) Pipe_Container;
        else
            if (p instanceof EquipmentData) return(HashMap <String, T>) EquipmentData_Container;
            else
                if (p instanceof InstrumentData) return (HashMap <String, T>) InstrumentData_Container;
                else
                    if (p instanceof LoopData) return (HashMap <String, T>) LoopData_Container;
                    else
                        if (p instanceof CableData) return (HashMap <String, T>) CableData_Container;
                        else
                            if (p instanceof JBoxData) return (HashMap <String, T>) JBoxData_Container;
                            else
                                if (p instanceof HookupData) return (HashMap <String, T>) HookupData_Container;
                                else
                                    return null;
    }

    <T> Set<String> getList(T p) {
        if (p instanceof PipeData) return Pipe_Container.keySet();
        else
            if (p instanceof EquipmentData) return EquipmentData_Container.keySet();
            else
                if (p instanceof InstrumentData) return InstrumentData_Container.keySet();
                else
                    if (p instanceof LoopData) return LoopData_Container.keySet();
                    else
                        if (p instanceof CableData) return CableData_Container.keySet();
                        else
                            if (p instanceof JBoxData) return JBoxData_Container.keySet();
                            else return null;
    }

    <T> ArrayList<String[]> getItemList(T p) {
        ArrayList<String[]> itemList = new ArrayList<>();
        Set<String> list = getList(p);
        for (String itemNumber : list) {
            String[] s = new String[2];
            if (p instanceof PipeData) {
                PipeData pd = GetData(itemNumber, new PipeData());
                s[0] = pd.getRevision();
                s[1] = itemNumber;
                itemList.add(s);
            }
            else
            if (p instanceof EquipmentData) {
                EquipmentData ed = GetData(itemNumber, new EquipmentData());
                s[0] = ed.getRevision();
                s[1] = itemNumber;
                itemList.add(s);
            }
            else
            if (p instanceof InstrumentData) {
                InstrumentData id = GetData(itemNumber, new InstrumentData());
                s[0] = id.getRevision();
                s[1] = itemNumber;
                itemList.add(s);
            }
            else
            if (p instanceof LoopData) {
                LoopData ld = GetData(itemNumber, new LoopData());
                s[0] = ld.getRevision();
                s[1] = itemNumber;
                itemList.add(s);
            }
            else
            if (p instanceof CableData) {
                CableData cd = GetData(itemNumber, new CableData());
                s[0] = cd.getRevision();
                s[1] = itemNumber;
                itemList.add(s);
            }
            else
            if (p instanceof JBoxData) {
                JBoxData jd = GetData(itemNumber, new JBoxData());
                s[0] = jd.getRevision();
                s[1] = itemNumber;
                itemList.add(s);
            }
            if (p instanceof HookupData) {
                HookupData hd = GetData(itemNumber, new HookupData());
                s[0] = hd.getRevision();
                s[1] = itemNumber;
                itemList.add(s);
            }
        }
        return itemList;
    }

    public Set<String> getList() {
        Set<String> All = new HashSet<String>();
        All.addAll(Pipe_Container.keySet());
        All.addAll(EquipmentData_Container.keySet());
        All.addAll(InstrumentData_Container.keySet());
        All.addAll(LoopData_Container.keySet());
        return All;
    }
}