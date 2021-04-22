/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pametnakucauredjaj.alarm;

import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author adinc
 */
public class PeriodPanel extends JPanel {

    private JLabel godinaLabel = new JLabel("Godina:");
    private JTextField godinaField = new JTextField("0", 2);

    private JLabel mesecLabel = new JLabel("Mesec:");
    private JTextField mesecField = new JTextField("0", 2);

    private JLabel danLabel = new JLabel("Dan:");
    private JTextField danField = new JTextField("0", 2);

    private JLabel satLabel = new JLabel("Sat:");
    private JTextField satField = new JTextField("0", 2);

    private JLabel minutLabel = new JLabel("Minut:");
    private JTextField minutField = new JTextField("0", 2);

    private void addComponents() {

        JPanel fieldsGrid = new JPanel(new GridLayout(0, 1));

        JPanel dateFlow = new JPanel();
        JPanel clockFlow = new JPanel();

        dateFlow.add(godinaLabel);
        dateFlow.add(godinaField);

        dateFlow.add(mesecLabel);
        dateFlow.add(mesecField);

        dateFlow.add(danLabel);
        dateFlow.add(danField);

        clockFlow.add(satLabel);
        clockFlow.add(satField);

        clockFlow.add(minutLabel);
        clockFlow.add(minutField);

        fieldsGrid.add(dateFlow);
        fieldsGrid.add(clockFlow);

        add(fieldsGrid);
    }

    public String getPeriod() {
        try {
            String godinaVal = godinaField.getText().strip();
            String mesecVal = mesecField.getText().strip();
            String danVal = danField.getText().strip();
            String satVal = satField.getText().strip();
            String minutVal = minutField.getText().strip();
            
            Integer.parseInt(godinaVal);
            Integer.parseInt(mesecVal);
            Integer.parseInt(danVal);
            Integer.parseInt(satVal);
            Integer.parseInt(minutVal);
            
            return godinaVal+":"+mesecVal+":"+danVal+":"+satVal+":"+minutVal;
            
        } catch (NumberFormatException ex) {
            
        }
        return null;
    }

    public PeriodPanel() {
        super();

        addComponents();
    }
}
