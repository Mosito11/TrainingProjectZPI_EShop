package combo.box.test;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

//public class Test2 {    
//
//	JFrame f;    
//
//	Test2(){    
//		
//		f = new JFrame("ComboBox Example");   
//		
//		final JLabel label = new JLabel();          
//		label.setHorizontalAlignment(JLabel.CENTER);  
//		label.setSize(400,100);  
//		
//		JButton b=new JButton("Show");  
//		b.setBounds(200,100,75,20);  
//		
//		String languages[] = {"C","C++","C#","Java","PHP"};        
//		final JComboBox cb = new JComboBox(languages);    
//		cb.setBounds(50, 100,90,20);    
//		
//		f.add(cb); 
//		f.add(label); 
//		f.add(b);    
//		f.setLayout(null);    
//		f.setSize(350,350);    
//		f.setVisible(true);       
//		
//		b.addActionListener(new ActionListener() {  
//        public void actionPerformed(ActionEvent e) {       
//        		String data = "Programming language Selected: " + cb.getItemAt(cb.getSelectedIndex());  
//        		label.setText(data);  
//        }  
//	});           
//}

/*import java.sql.Timestamp;
import java.util.Date;

public class Test2 {
    public static void main(String[] args) {
        // Vytvoøení Timestamp z aktuálního èasu
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        
        // Pøevod na Date
        Date date = new Date(timestamp.getTime());
        
        System.out.println("Timestamp: " + timestamp);
        System.out.println("Date: " + date);
    }
}*/

import javax.xml.namespace.QName;

public class Test2 {
    public static void main(String[] args) {
        // Vytvorenie in¹tancie QName s názvom a namespace'om
        QName qName = new QName("http://example.com", "elementName");

        // Pou¾itie in¹tancie QName
        System.out.println("Local Part: " + qName.getLocalPart());
        System.out.println("Namespace URI: " + qName.getNamespaceURI());
        System.out.println("Qualified Name: " + qName);
    }
}

/*public static void main(String[] args) {    
    	new Test2();         
	}    
} */ 
