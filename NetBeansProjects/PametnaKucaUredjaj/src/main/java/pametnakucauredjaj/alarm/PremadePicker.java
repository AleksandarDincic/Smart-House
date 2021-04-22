/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pametnakucauredjaj.alarm;

import java.util.Calendar;
import java.util.Date;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 * @author adinc
 */
public class PremadePicker extends JPanel implements DatetimePicker {

    private class Choice {

        private String name;
        private int hours, minutes;

        public Choice(String name, int hours, int minutes) {
            this.name = name;
            this.hours = hours;
            this.minutes = minutes;
        }

        public int getHours() {
            return hours;
        }

        public int getMinutes() {
            return minutes;
        }

        public String toString() {
            return name;
        }
    }

    private JLabel vremenaLabel = new JLabel("Ponudjena vremena");
    private JComboBox timeComboBox = new JComboBox();

    private void addComponents() {
        add(vremenaLabel);
        add(timeComboBox);
    }

    public PremadePicker() {
        super();
        addComponents();
    }

    public boolean addChoice(String choiceName, String choiceTime) {
        try {
            String[] timeStrings = choiceTime.split(":");
            if (timeStrings.length == 2) {
                int hours = Integer.parseInt(timeStrings[0]);
                int minutes = Integer.parseInt(timeStrings[1]);
                if (hours >= 0 && hours <= 23 && minutes >= 0 && minutes <= 59) {
                    Choice newChoice = new Choice(choiceName, hours, minutes);
                    SwingUtilities.invokeLater(() -> {
                        timeComboBox.addItem(newChoice);
                    });
                    return true;
                }
            }
        } catch (Exception ex) {
        }
        return false;
    }

    @Override
    public Date getSelectedDate() {

        Choice selectedChoice = (Choice) timeComboBox.getSelectedItem();
        
        if (selectedChoice != null) {
            Calendar currentCal = Calendar.getInstance();
            Calendar targetCal = Calendar.getInstance();

            targetCal.set(Calendar.HOUR_OF_DAY, selectedChoice.getHours());
            targetCal.set(Calendar.MINUTE, selectedChoice.getMinutes());
            targetCal.set(Calendar.SECOND, 0);
            targetCal.set(Calendar.MILLISECOND, 0);

            if (!targetCal.after(currentCal)) {
                targetCal.roll(Calendar.DAY_OF_MONTH, true);
            }

            return targetCal.getTime();
        }

        return null;
    }

}
