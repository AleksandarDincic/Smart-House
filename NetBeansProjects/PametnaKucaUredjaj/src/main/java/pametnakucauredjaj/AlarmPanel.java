/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pametnakucauredjaj;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import pametnakucauredjaj.alarm.BasicPicker;
import pametnakucauredjaj.alarm.DatetimePicker;
import pametnakucauredjaj.alarm.PremadePicker;
import pametnakucauredjaj.alarm.PeriodPanel;

/**
 *
 * @author adinc
 */
public class AlarmPanel extends JPanel {

    private final MainWindow parentWindow;
    
    
    private BasicPicker basicPicker = new BasicPicker();
    private PremadePicker premadePicker = new PremadePicker();

    private JCheckBox periodCheckbox = new JCheckBox("Periodican alarm");
    private PeriodPanel periodPanel = new PeriodPanel();

    private JTabbedPane pickersPane = new JTabbedPane();
    private JButton napraviAlarmButton = new JButton("Napravi alarm");

    private JTextField zvonoField = new JTextField(16);
    private JButton postaviZvonoButton = new JButton("Postavi zvono");

    private SimpleDateFormat sdf = new SimpleDateFormat("dd:MM:yyyy:HH:mm");

    
    private void addComponents() {

        premadePicker.addChoice("8 ujutru", "08:00");
        premadePicker.addChoice("Ponoc", "00:00");
        premadePicker.addChoice("Slagalica", "19:00");

        pickersPane.addTab("Slobodan izbor", basicPicker);
        pickersPane.addTab("Izbor iz ponudjenih", premadePicker);

        JPanel zvonoFlowPanel = new JPanel();
        zvonoFlowPanel.add(zvonoField);
        zvonoFlowPanel.add(postaviZvonoButton);

        JPanel pickerBorderPanel = new JPanel(new BorderLayout());
        pickerBorderPanel.add(pickersPane, BorderLayout.CENTER);
        pickerBorderPanel.add(napraviAlarmButton, BorderLayout.SOUTH);

        JPanel periodGridPanel = new JPanel(new GridLayout(0, 1));
        periodGridPanel.add(periodCheckbox);
        periodGridPanel.add(periodPanel);

        add(pickerBorderPanel, BorderLayout.CENTER);
        add(periodGridPanel, BorderLayout.EAST);
        add(zvonoFlowPanel, BorderLayout.SOUTH);
    }

    public AlarmPanel(MainWindow parentWindow) {
        super(new BorderLayout());
        this.parentWindow = parentWindow;

        napraviAlarmButton.addActionListener(e -> {
            DatetimePicker selectedPicker = (DatetimePicker) pickersPane.getSelectedComponent();
            String alarmString = sdf.format(selectedPicker.getSelectedDate());

            String periodString = null;

            if (periodCheckbox.isSelected()) {
                periodString = periodPanel.getPeriod();
                if (periodString == null) {
                    this.parentWindow.setStatusMsg("Neispravna perioda");
                    return;
                }
            }

            String url = "http://localhost:8080/PametnaKucaServis/resources/alarm/napravi?vreme=" + URLEncoder.encode(alarmString, StandardCharsets.UTF_8);

            if (periodString != null) {
                url = url.concat("&perioda=" + URLEncoder.encode(periodString, StandardCharsets.UTF_8));
            }

            this.parentWindow.setStatusMsg(HttpClient.handlePostRequest(url, parentWindow.getUsername(), parentWindow.getPassword()));
        });

        postaviZvonoButton.addActionListener(e -> {
            String naziv = zvonoField.getText();

            String url = "http://localhost:8080/PametnaKucaServis/resources/alarm/zvono?naziv=" + URLEncoder.encode(naziv, StandardCharsets.UTF_8);

            this.parentWindow.setStatusMsg(HttpClient.handlePostRequest(url, parentWindow.getUsername(), parentWindow.getPassword()));
        });

        addComponents();
    }
}
