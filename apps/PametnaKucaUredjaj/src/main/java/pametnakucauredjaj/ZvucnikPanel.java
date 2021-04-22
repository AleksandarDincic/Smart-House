/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pametnakucauredjaj;

import java.awt.BorderLayout;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author adinc
 */
public class ZvucnikPanel extends JPanel{
    
    private final MainWindow parentWindow;
    
    private final JLabel nazivLabel = new JLabel("Naziv:");
    private JTextField nazivField = new JTextField(16);
    private final JButton pustiButton = new JButton("Pusti");
    private final JButton istorijaButton = new JButton("Istorija");
    private final JList<String> istorijaList = new JList<>();
    
    private void addComponents(){
        JPanel flowPanel = new JPanel();
        flowPanel.add(nazivLabel);
        flowPanel.add(nazivField);
        flowPanel.add(pustiButton);
        flowPanel.add(istorijaButton);
        add(flowPanel, BorderLayout.NORTH);
        add(istorijaList, BorderLayout.CENTER);
    }
    
    public ZvucnikPanel(MainWindow parentWindow){
        super(new BorderLayout());
        
        this.parentWindow = parentWindow;
        addComponents();
        
        pustiButton.addActionListener(e->{
            String url = "http://localhost:8080/PametnaKucaServis/resources/zvucnik/pusti?naziv=" + URLEncoder.encode(nazivField.getText(), StandardCharsets.UTF_8);
            this.parentWindow.setStatusMsg(HttpClient.handleGetRequest(url, parentWindow.getUsername(), parentWindow.getPassword()));
        });
        
        istorijaButton.addActionListener(e->{
            String url = "http://localhost:8080/PametnaKucaServis/resources/zvucnik/istorija";
            String reply = HttpClient.handleGetRequest(url, parentWindow.getUsername(), parentWindow.getPassword());
            String[] values = reply.split("\\r?\\n");
            
            istorijaList.setListData(values);
            this.parentWindow.setStatusMsg("Zahtev poslat");
        });
    }
}
