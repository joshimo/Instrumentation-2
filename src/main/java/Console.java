/**
 * Created by y.golota on 27.01.2017.
 */

import sun.util.calendar.CalendarUtils;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.*;
import java.util.Date;


public class Console extends JScrollPane {

    static JTextPane ConText = new JTextPane();

    static boolean firstRun = true;

    final boolean SHOW_JOPTION = true;
    final boolean SHOW_CONSOLE = true;

    final boolean DONT_SHOW_JOPTION = false;
    final boolean DONT_SHOW_CONSOLE = false;

    Console(int x0, int y0, int L, int H) {
        super(ConText);
        this.setBounds(x0, y0, L, H);
        TitledBorder consoleBorder = new TitledBorder("Console");
        this.setBorder(consoleBorder);
        ConText.setFont(new Font("Arial", Font.ITALIC, 12));
        ConText.setBackground(new Color(236, 236, 236));
        ConText.setEditable(false);
        log("");
        firstRun = false;
    }

    void Echo(String s, boolean showJOptionMessage, boolean showConsoleMessage) {
        String str;
        Date date = new Date() {
            @SuppressWarnings("deprecation")
            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder(28);
                CalendarUtils.sprintf0d(sb, super.getHours(), 2).append(':');   // HH
                CalendarUtils.sprintf0d(sb, super.getMinutes(), 2).append(':'); // mm
                CalendarUtils.sprintf0d(sb, super.getSeconds(), 2); // ss
                return sb.toString();
            }
        };
        str = "\n" + date.toString() + " -> " + s;
        ConText.setText(ConText.getText() + str);
        if (showJOptionMessage) JOptionPane.showMessageDialog(null, s);
        log(str);
        if (showConsoleMessage) ConText.setCaretPosition(ConText.getText().length() - 1);
        System.out.println(s);
    }

    void Echo(String s, boolean showJOptionMessage) {
        String str;
        Date date = new Date() {
            @SuppressWarnings("deprecation")
            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder(28);
                CalendarUtils.sprintf0d(sb, super.getHours(), 2).append(':');   // HH
                CalendarUtils.sprintf0d(sb, super.getMinutes(), 2).append(':'); // mm
                CalendarUtils.sprintf0d(sb, super.getSeconds(), 2); // ss
                return sb.toString();
            }
        };
        str = "\n" + date.toString() + " -> " + s;
        ConText.setText(ConText.getText() + str);
        if (showJOptionMessage) JOptionPane.showMessageDialog(null, s);
        log(str);
        ConText.setCaretPosition(ConText.getText().length() - 1);
        System.out.println(s);
    }

    void Echo(String s) {
        String str;
        Date date = new Date() {
            @SuppressWarnings("deprecation")
            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder(28);
                CalendarUtils.sprintf0d(sb, super.getHours(), 2).append(':');   // HH
                CalendarUtils.sprintf0d(sb, super.getMinutes(), 2).append(':'); // mm
                CalendarUtils.sprintf0d(sb, super.getSeconds(), 2); // ss
                return sb.toString();
            }
        };
        str = "\n" + date.toString() + " -> " + s;
        JOptionPane.showMessageDialog(null, s);
        ConText.setText(ConText.getText() + str);
        log(str);
        ConText.setCaretPosition(ConText.getText().length() - 1);
        System.out.println(s);
    }


    @SuppressWarnings("deprecation")
    void log(String str) {
        Date date = new Date();
        try {
            //System.out.printf(new File("").getAbsolutePath());
            FileWriter fw = new FileWriter("src\\main\\resources\\log\\log.log", true);

            if (firstRun) fw.write("\n\n********* " + date.toGMTString() + " *********\n");
            else fw.write(str);
            fw.close();
        }
        catch (FileNotFoundException ffe) {
            System.out.println("File not found in 'void log(String str)'");
        }
        catch (IOException ioe) {
            System.out.println("IO exception in 'void log(String str)'");
        }
    }
}