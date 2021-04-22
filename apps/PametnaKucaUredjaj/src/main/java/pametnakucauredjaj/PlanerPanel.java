/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pametnakucauredjaj;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import pametnakucauredjaj.alarm.BasicPicker;
import pametnakucauredjaj.planer.PlanerEntry;
import pametnakucauredjaj.planer.PlanerParser;

/**
 *
 * @author adinc
 */
public class PlanerPanel extends JPanel {

    private MainWindow parentWindow;

    private BasicPicker datePicker = new BasicPicker();
    private SimpleDateFormat sdf = new SimpleDateFormat("dd:MM:yyyy:HH:mm");

    private JButton dodajButton = new JButton("Dodaj obavezu");
    private JButton izmeniButton = new JButton("Izmeni obavezu");
    private JButton obrisiButton = new JButton("Obrisi obavezu");
    private JButton listajButton = new JButton("Listaj obaveze");
    private JButton alarmButton = new JButton("Navij alarm");

    private JLabel destinacijaLabel = new JLabel("Destinacija:");
    private JTextField destinacijaField = new JTextField(24);
    private JButton postaviKucuButton = new JButton("Postavi kao kucnu adresu");

    private JList<PlanerEntry> entryList = new JList<>();

    private JLabel trajanjeLabel = new JLabel("Trajanje:");
    private JLabel trajanjeSatLabel = new JLabel("Sat:");
    private JTextField trajanjeSatField = new JTextField("0", 2);
    private JLabel trajanjeMinutLabel = new JLabel("Minut:");
    private JTextField trajanjeMinutField = new JTextField("0", 2);

    private JLabel kalkStartLabel = new JLabel("Mesto A:");
    private JTextField kalkStartField = new JTextField(24);
    private JLabel kalkEndLabel = new JLabel("Mesto B:");
    private JTextField kalkEndField = new JTextField(24);
    private JButton kalkButton = new JButton("Izracunaj");

    private void addComponents() {
        JPanel destinacijaFlowPanel = new JPanel();

        destinacijaFlowPanel.add(destinacijaLabel);
        destinacijaFlowPanel.add(destinacijaField);
        destinacijaFlowPanel.add(postaviKucuButton);

        JPanel trajanjeFlowPanel = new JPanel();
        trajanjeFlowPanel.add(trajanjeLabel);
        trajanjeFlowPanel.add(trajanjeSatLabel);
        trajanjeFlowPanel.add(trajanjeSatField);
        trajanjeFlowPanel.add(trajanjeMinutLabel);
        trajanjeFlowPanel.add(trajanjeMinutField);

        JPanel pickerGridPanel = new JPanel(new GridLayout(0, 1));
        pickerGridPanel.add(datePicker);
        pickerGridPanel.add(trajanjeFlowPanel);
        pickerGridPanel.add(dodajButton);
        pickerGridPanel.add(izmeniButton);
        pickerGridPanel.add(obrisiButton);
        pickerGridPanel.add(listajButton);
        pickerGridPanel.add(alarmButton);

        JPanel kalkStartFlowPanel = new JPanel();
        kalkStartFlowPanel.add(kalkStartLabel);
        kalkStartFlowPanel.add(kalkStartField);

        JPanel kalkEndFlowPanel = new JPanel();
        kalkEndFlowPanel.add(kalkEndLabel);
        kalkEndFlowPanel.add(kalkEndField);

        JPanel kalkButtonPanel = new JPanel();
        kalkButtonPanel.add(kalkButton);

        JPanel kalkGridPanel = new JPanel(new GridLayout(0, 1));
        kalkGridPanel.add(kalkStartFlowPanel);
        kalkGridPanel.add(kalkEndFlowPanel);
        kalkGridPanel.add(kalkButtonPanel);

        add(destinacijaFlowPanel, BorderLayout.SOUTH);
        add(pickerGridPanel, BorderLayout.WEST);
        add(kalkGridPanel, BorderLayout.EAST);
        add(entryList, BorderLayout.CENTER);
    }

    private void addListeners() {
        dodajButton.addActionListener(e -> {
            try {
                String pocetak = sdf.format(datePicker.getSelectedDate());
                int trajanjeSat = Integer.parseInt(trajanjeSatField.getText());
                int trajanjeMinut = Integer.parseInt(trajanjeMinutField.getText());

                String url = "http://localhost:8080/PametnaKucaServis/resources/planer?pocetak=";
                url = url.concat(URLEncoder.encode(pocetak, StandardCharsets.UTF_8)).concat("&trajanjeSat=").concat(Integer.toString(trajanjeSat)).concat("&trajanjeMinut=")
                        .concat(Integer.toString(trajanjeMinut));

                String destinacija = destinacijaField.getText().trim();

                if (!destinacija.equals("")) {
                    url = url.concat("&destinacija=").concat(URLEncoder.encode(destinacija, StandardCharsets.UTF_8));
                }

                this.parentWindow.setStatusMsg(HttpClient.handlePostRequest(url, parentWindow.getUsername(), parentWindow.getPassword()));
            } catch (NumberFormatException ex) {
                this.parentWindow.setStatusMsg("Neispravno trajanje");
            }
        });

        listajButton.addActionListener(e -> {
            try {

                String url = "http://localhost:8080/PametnaKucaServis/resources/planer";

                String response = HttpClient.handleGetRequest(url, parentWindow.getUsername(), parentWindow.getPassword());

                List<PlanerEntry> parsedList = PlanerParser.parseXml(response);
                PlanerEntry[] parsedArray = new PlanerEntry[parsedList.size()];
                parsedList.toArray(parsedArray);
                SwingUtilities.invokeLater(() -> {
                    entryList.setListData(parsedArray);
                });

            } catch (SAXException | IOException | ParserConfigurationException | ParseException ex) {
                parentWindow.setStatusMsg("Neispravan XML");
            }
        });

        postaviKucuButton.addActionListener(e -> {
            String adresa = destinacijaField.getText().trim();
            if (adresa.equals("")) {
                parentWindow.setStatusMsg("Unesite adresu");
            } else {
                String url = "http://localhost:8080/PametnaKucaServis/resources/planer/adresa?adresa=";
                url = url.concat(URLEncoder.encode(adresa, StandardCharsets.UTF_8));

                this.parentWindow.setStatusMsg(HttpClient.handlePostRequest(url, parentWindow.getUsername(), parentWindow.getPassword()));
            }
        });

        izmeniButton.addActionListener(e -> {
            try {
                PlanerEntry selectedEntry = entryList.getSelectedValue();
                if (selectedEntry == null) {
                    this.parentWindow.setStatusMsg("Izaberite obavezu");
                    return;
                }

                int idOb = selectedEntry.getIdOb();

                String pocetak = sdf.format(datePicker.getSelectedDate());
                int trajanjeSat = Integer.parseInt(trajanjeSatField.getText());
                int trajanjeMinut = Integer.parseInt(trajanjeMinutField.getText());

                String url = "http://localhost:8080/PametnaKucaServis/resources/planer?pocetak=";
                url = url.concat(URLEncoder.encode(pocetak, StandardCharsets.UTF_8)).concat("&trajanjeSat=").concat(Integer.toString(trajanjeSat)).concat("&trajanjeMinut=")
                        .concat(Integer.toString(trajanjeMinut)).concat("&idOb=").concat(Integer.toString(idOb));

                String destinacija = destinacijaField.getText().trim();

                if (!destinacija.equals("")) {
                    url = url.concat("&destinacija=").concat(URLEncoder.encode(destinacija, StandardCharsets.UTF_8));
                }

                this.parentWindow.setStatusMsg(HttpClient.handlePutRequest(url, parentWindow.getUsername(), parentWindow.getPassword()));
            } catch (NumberFormatException ex) {
                this.parentWindow.setStatusMsg("Neispravno trajanje");
            }
        });

        obrisiButton.addActionListener(e -> {
            PlanerEntry selectedEntry = entryList.getSelectedValue();
            if (selectedEntry == null) {
                this.parentWindow.setStatusMsg("Izaberite obavezu");
                return;
            }

            int idOb = selectedEntry.getIdOb();

            String url = "http://localhost:8080/PametnaKucaServis/resources/planer?idOb=";
            url = url.concat(Integer.toString(idOb));

            this.parentWindow.setStatusMsg(HttpClient.handleDeleteRequest(url, parentWindow.getUsername(), parentWindow.getPassword()));

        });

        alarmButton.addActionListener(e -> {
            PlanerEntry selectedEntry = entryList.getSelectedValue();
            if (selectedEntry == null) {
                this.parentWindow.setStatusMsg("Izaberite obavezu");
                return;
            }

            int idOb = selectedEntry.getIdOb();

            String url = "http://localhost:8080/PametnaKucaServis/resources/planer/alarm?idOb=";
            url = url.concat(Integer.toString(idOb));

            this.parentWindow.setStatusMsg(HttpClient.handlePostRequest(url, parentWindow.getUsername(), parentWindow.getPassword()));

        });

        kalkButton.addActionListener(e -> {

            String start = kalkStartField.getText().trim();
            String end = kalkEndField.getText().trim();
            if (end.equals("")) {
                parentWindow.setStatusMsg("Unesite lokaciju B");
            } else {
                String url = "http://localhost:8080/PametnaKucaServis/resources/planer/kalkulator?end=";
                url = url.concat(URLEncoder.encode(end, StandardCharsets.UTF_8));

                if (!start.equals("")) {
                    url = url.concat("&start=").concat(URLEncoder.encode(start, StandardCharsets.UTF_8));
                }

                String reply = HttpClient.handleGetRequest(url, parentWindow.getUsername(), parentWindow.getPassword());

                int s = 0;
                try {
                    s = Integer.parseInt(reply);
                } catch (NumberFormatException ex) {
                    this.parentWindow.setStatusMsg(reply);
                }

                int h = s / 3600;
                s %= 3600;

                int m = s / 60;
                s %= 60;

                reply = Integer.toString(s) + "s";

                if (m != 0) {
                    reply = Integer.toString(m) + "m " + reply;
                }

                if (h != 0) {
                    reply = Integer.toString(h) + "h " + reply;
                }

                this.parentWindow.setStatusMsg(reply);
            }

        });
    }

    public PlanerPanel(MainWindow parentWindow) {
        super(new BorderLayout());
        this.parentWindow = parentWindow;
        addComponents();
        addListeners();
    }

}
