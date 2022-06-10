import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.List;

public class letturaJson extends JFrame implements ActionListener{

	private JPanel contentPane;
	private JTextField textFieldCitta;
	private JLabel lblCitta;
	private JButton btnCitta;
	private JButton btnImg;
	private static List listInfo;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					letturaJson frame = new letturaJson();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public letturaJson() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 410);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		textFieldCitta = new JTextField();
		textFieldCitta.setBounds(145, 11, 86, 20);
		contentPane.add(textFieldCitta);
		textFieldCitta.setColumns(10);
		
		lblCitta = new JLabel("Citt\u00E0 : ");
		lblCitta.setBounds(47, 14, 46, 14);
		contentPane.add(lblCitta);
		
		btnCitta = new JButton("Premi");
		btnCitta.setBounds(287, 10, 89, 23);
		btnCitta.addActionListener(this);
		contentPane.add(btnCitta);
		
		btnImg = new JButton(" ");
		btnImg.setBounds(57, 42, 324, 208);
		btnImg.setVisible(false);
		contentPane.add(btnImg);
		
		listInfo = new List();
		listInfo.setBounds(127, 256, 164, 105);
		contentPane.add(listInfo);
	}
	
	
	public static String leggiFile() throws FileNotFoundException, IOException, ParseException {
		Object obj = new JSONParser().parse(new FileReader("Meteo.json")); 
        
        JSONObject jsonObject = (JSONObject) obj; 
          
        listInfo.removeAll();
        Map info1 = ((Map)jsonObject.get("coord")); 
        
        // Iterator Map (info1)
        Iterator<Map.Entry> iter1 = info1.entrySet().iterator();
        while (iter1.hasNext()) {
        	// valore
            Map.Entry pair = iter1.next(); 
            listInfo.add(pair.getKey() + " : " + pair.getValue());  
        } 
        
        Map info2 = ((Map)jsonObject.get("main")); 
        
        // Iterator Map (info2)
        Iterator<Map.Entry> iter2 = info2.entrySet().iterator();
        while (iter2.hasNext()) {
        	// valore
            Map.Entry pair = iter2.next(); 
            if(pair.getKey().toString().startsWith("temp")) {
            	double temp = Double.parseDouble(pair.getValue().toString()) - 273.15;
            	listInfo.add(pair.getKey() + " : " + temp);
            }
            else
            	listInfo.add(pair.getKey() + " : " + pair.getValue());  
        } 
       
        // Oggetti Array 
        JSONArray jsonArray = (JSONArray) jsonObject.get("weather"); 
        
        // Iteratore su Array 
        Iterator iter3 = jsonArray.iterator(); 
        String ritorno = "";
        
        while (iter3.hasNext())  
        { 
            iter1 = ((Map) iter3.next()).entrySet().iterator(); 
            while (iter1.hasNext()) { 
                Map.Entry pair = iter1.next();
                if(!(pair.getKey().equals("id") || pair.getKey().equals("icon")))
                	listInfo.add(pair.getKey() + " : " + pair.getValue());
                if(pair.getKey().equals("main"))
                	ritorno = (String) pair.getValue();
            } 
        } 
        
        return ritorno;
	}
	
	public String callURL(String myURL) {
		
		System.out.println("URL: " + myURL);
		
		StringBuilder sb = new StringBuilder();
		URLConnection urlConn = null;
		FileOutputStream file = null;
		
		try {
			 file = new FileOutputStream("Meteo.json");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		InputStreamReader in = null;
		
		try {
			// crea URL e apre connessione
			URL url = new URL(myURL);
			urlConn = url.openConnection();
			
			// imposta timeout
			if (urlConn != null)
				urlConn.setReadTimeout(60 * 1000);
						
			// stream di input dal socket
			if (urlConn != null && urlConn.getInputStream() != null) {
				
				in = new InputStreamReader(urlConn.getInputStream(),Charset.defaultCharset());
				BufferedReader bufferedReader = new BufferedReader(in);
				
				// costruisce stringa (di stringhe)
				if (bufferedReader != null) {
					int cp;
					
					while ((cp = bufferedReader.read()) != -1) {
						sb.append((char) cp);
						file.write(cp);	
					}
					bufferedReader.close();
				}
			}
		in.close();
		
		} catch (Exception e) {throw new RuntimeException(e); } 
 
		return sb.toString();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String citta;
		String tempo = "";
		btnImg.setVisible(true);
		
		citta = textFieldCitta.getText();
		
		//System.out.println("\nResponse: \n" + callURL("https://api.openweathermap.org/data/2.5/weather?lat=44&lon=10&appid=3b66ecae84daf62a574518b1a1332260"));
		System.out.println("\nResponse: \n" + callURL("https://api.openweathermap.org/data/2.5/weather?q="+citta+ ",it&appid=3b66ecae84daf62a574518b1a1332260"));
		try {
			tempo = letturaJson.leggiFile();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		
		switch(tempo) {
		case "Rain":
			Icon icon = new ImageIcon("raining.gif");	
			btnImg.setIcon(icon);
			break;
		case "Fog":
		case "Mist":
			Icon icon1 = new ImageIcon("nebbia.gif");	
			btnImg.setIcon(icon1);
			break;
		case "Clear":
			Icon icon2 = new ImageIcon("chiaro.gif");	
			btnImg.setIcon(icon2);
			break;
		case "Snow":
			Icon icon3 = new ImageIcon("neve.gif");	
			btnImg.setIcon(icon3);
			break;
		case "Clouds":
			Icon icon4 = new ImageIcon("clouds.gif");	
			btnImg.setIcon(icon4);
			break;
		}
	}
}
