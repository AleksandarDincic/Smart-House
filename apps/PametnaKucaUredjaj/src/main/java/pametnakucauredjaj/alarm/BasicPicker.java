/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pametnakucauredjaj.alarm;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;

/**
 *
 * @author adinc
 */
public class BasicPicker extends JPanel implements DatetimePicker {

    private JLabel datumLabel = new JLabel("Datum:");
    private JSpinner datumSpinner = new JSpinner(new SpinnerDateModel());
    private JLabel vremeLabel = new JLabel("Vreme:");
    private JSpinner vremeSpinner = new JSpinner(new SpinnerDateModel());

    private void addComponents(){
        JSpinner.DateEditor datumEditor = new JSpinner.DateEditor(datumSpinner, "dd.MM.yyyy");
        datumSpinner.setEditor(datumEditor);
        
        JSpinner.DateEditor vremeEditor = new JSpinner.DateEditor(vremeSpinner,"HH:mm");
        vremeSpinner.setEditor(vremeEditor);
        
        add(datumLabel);
        add(datumSpinner);
        add(vremeLabel);
        add(vremeSpinner);
    }
    
    @Override
    public Date getSelectedDate() {
        Calendar dateCal = Calendar.getInstance();
        dateCal.setTime((Date)datumSpinner.getValue());
        
        Calendar clockCal = Calendar.getInstance();
        clockCal.setTime((Date)vremeSpinner.getValue());
        
        dateCal.set(Calendar.HOUR_OF_DAY, clockCal.get(Calendar.HOUR_OF_DAY));
        dateCal.set(Calendar.MINUTE, clockCal.get(Calendar.MINUTE));
        dateCal.set(Calendar.SECOND, 0);
        dateCal.set(Calendar.MILLISECOND, 0);
        
        return dateCal.getTime();
    }

    public BasicPicker(){
        super();
        addComponents();
    }
}
