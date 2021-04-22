/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pametnakucauredjaj;

import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 *
 * @author adinc
 */
public class MainWindow extends JFrame {

    private final JTextField usernameField = new JTextField(16);
    private final JPasswordField passwordField = new JPasswordField(16);
    private final JButton testButton = new JButton("Test");
    private final JButton registerButton = new JButton("Registracija");
    private final JLabel statusLabel = new JLabel("Status: ");

    private final JTabbedPane appTabs = new JTabbedPane();
    private final ZvucnikPanel zvucnikPanel = new ZvucnikPanel(this);
    private final AlarmPanel alarmPanel = new AlarmPanel(this);
    private final PlanerPanel planerPanel = new PlanerPanel(this);

    public String getUsername() {
        return usernameField.getText();
    }

    public String getPassword() {
        return String.valueOf(passwordField.getPassword());
    }

    public void setStatusMsg(String msg) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("Status: " + msg);
        });
    }

    private void addComponents() {
        JPanel credentialsPanel = new JPanel();
        credentialsPanel.add(new JLabel("Korisnicko ime:"));
        credentialsPanel.add(usernameField);
        credentialsPanel.add(new JLabel("Lozinka:"));
        credentialsPanel.add(passwordField);
        credentialsPanel.add(testButton);
        credentialsPanel.add(registerButton);
        credentialsPanel.add(statusLabel);

        add(credentialsPanel, BorderLayout.SOUTH);

        appTabs.addTab("Zvucnik", zvucnikPanel);
        appTabs.addTab("Alarm", alarmPanel);
        appTabs.add("Planer", planerPanel);
        add(appTabs, BorderLayout.CENTER);
    }

    public MainWindow() {
        super("Pametna kuca");
        addComponents();
        setSize(1366, 768);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        testButton.addActionListener(e -> {
            setStatusMsg(HttpClient.handleGetRequest("http://localhost:8080/PametnaKucaServis/resources/korisnik",
                    getUsername(), getPassword()));
        });
        registerButton.addActionListener(e -> {
            setStatusMsg(HttpClient.handlePostRequest("http://localhost:8080/PametnaKucaServis/resources/korisnik",
                    getUsername(), getPassword()));
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        new MainWindow();
    }
}
