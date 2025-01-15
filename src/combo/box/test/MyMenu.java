package combo.box.test;

import javax.swing.JFrame;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MyMenu extends JFrame {
	
	private MenuBar nav;
	private Menu blokacie, farby, posuny, velkosti;
	private MenuItem povolit_zakazat;
	private MenuItem ruzovePozadie, zltePozadie, bielePozadie;
	private MenuItem cierneKomponenty, hnedeKomponenty, darkKomponenty;
	private MenuItem vlavo, vpravo, hore, dole;
	private MenuItem male, velke;
	private JButton hlavne;
	private int k = 1;  // koeficient udává velikost komponenty
	private Container pane;

	public MyMenu()
	{
	        this.setSize(700, 500);
	        this.setTitle("MenuBar");
	        this.setKomponents();
	        this.setListeners();
	}

	public static Frame nastav()
	{
	        MyMenu m = new MyMenu();
	        m.setLocationRelativeTo(null);
	        m.setVisible(true);
	        return m;
	}
	
	public void setKomponents()
	{
	       /**
	        * Layout a komponenta
	        */

	       this.pane = this.getContentPane();
	       this.pane.setLayout(null);
	       this.hlavne = new JButton();
	       this.hlavne.setLayout(null);
	       this.hlavne.setLocation(325, 200);
	       this.hlavne.setSize(50, 50);
	       this.hlavne.setBackground(Color.BLACK);
	       this.pane.add(this.hlavne);

	       /**
	        * Hlavní èást menu
	        */

	       this.nav = new MenuBar();
	       this.setMenuBar(this.nav);
	       this.blokacie = new Menu("blokacie");
	       this.farby = new Menu("farby");
	       this.posuny = new Menu("posuny");
	       this.velkosti = new Menu("velkosti");
	       this.nav.add(this.blokacie);
	       this.nav.add(this.farby);
	       this.nav.add(this.posuny);
	       this.nav.add(this.velkosti);

	       /**
	        * Polo¾ky podmenu
	        */

	       this.povolit_zakazat = new MenuItem("Povolit/Zakazat");
	       this.ruzovePozadie = new MenuItem("Ruzove pozadie");
	       this.zltePozadie = new MenuItem("Zlte pozadie");
	       this.bielePozadie = new MenuItem("Biele pozadie");
	       this.cierneKomponenty = new MenuItem("Cierne komponenty");
	       this.hnedeKomponenty = new MenuItem("Hnede komponenty");
	       this.darkKomponenty = new MenuItem("Dark_Gray komponenty");
	       this.vlavo = new MenuItem("Vlevo");
	       this.vpravo = new MenuItem("Vpravo");
	       this.hore = new MenuItem("Hore");
	       this.dole = new MenuItem("Dole");
	       this.male = new MenuItem("Male");
	       this.velke = new MenuItem("Velke");

	       this.blokacie.add(this.povolit_zakazat);
	       this.farby.add(this.ruzovePozadie);
	       this.farby.add(this.zltePozadie);
	       this.farby.add(this.bielePozadie);
	       this.farby.addSeparator(); //dìlící èára
	       this.farby.add(this.cierneKomponenty);
	       this.farby.add(this.hnedeKomponenty);
	       this.farby.add(this.darkKomponenty);
	       this.posuny.add(this.vlavo);
	       this.posuny.add(this.vpravo);
	       this.posuny.add(this.hore);
	       this.posuny.add(this.dole);
	       this.velkosti.add(this.male);
	       this.velkosti.add(this.velke);
	}
	
	public void setListeners()
	{
	       //první výbìr
	        this.povolit_zakazat.addActionListener(new ActionListener(){
	        public void actionPerformed(ActionEvent e){
	           hlavne.setEnabled(!hlavne.isEnabled());
	        }});

	        /**
	         * Druhý výbìr
	         */

	        this.ruzovePozadie.addActionListener(new ActionListener(){
	        public void actionPerformed(ActionEvent e){
	          pane.setBackground(Color.PINK);
	        }});
	        this.zltePozadie.addActionListener(new ActionListener(){
	        public void actionPerformed(ActionEvent e){
	           pane.setBackground(Color.YELLOW);
	        }});
	        this.bielePozadie.addActionListener(new ActionListener(){
	        public void actionPerformed(ActionEvent e){
	           pane.setBackground(Color.WHITE);
	        }});

	        this.cierneKomponenty.addActionListener(new ActionListener(){
	        public void actionPerformed(ActionEvent e){
	          hlavne.setBackground(Color.BLACK);
	        }});
	        this.hnedeKomponenty.addActionListener(new ActionListener(){
	        public void actionPerformed(ActionEvent e){
	            Color hneda = new Color(102,51,0);
	          hlavne.setBackground(hneda);
	        }});
	        this.darkKomponenty.addActionListener(new ActionListener(){
	        public void actionPerformed(ActionEvent e){
	          hlavne.setBackground(Color.DARK_GRAY);
	        }});

	        /**
	         * Tøetí výbìr
	         */

	        this.vlavo.addActionListener(new ActionListener(){
	        public void actionPerformed(ActionEvent e){
	          hlavne.setSize((k*50), 500);
	          hlavne.setLocation(0,0);
	        }});

	        this.vpravo.addActionListener(new ActionListener(){
	        public void actionPerformed(ActionEvent e){
	           hlavne.setSize((k*50), 500);
	           if(k == 1){
	          hlavne.setLocation(650,0);}
	          else{
	           hlavne.setLocation(600,0);
	            }
	        }});

	        this.hore.addActionListener(new ActionListener(){
	        public void actionPerformed(ActionEvent e){
	          hlavne.setSize(700, (k*50));
	          hlavne.setLocation(0,0);
	        }});

	        this.dole.addActionListener(new ActionListener(){
	        public void actionPerformed(ActionEvent e){
	          hlavne.setSize(700, (k*50));
	          if(k == 1){
	          hlavne.setLocation(0,400); }
	          else
	          {
	             hlavne.setLocation(0,355);
	          }
	        }});

	        /**
	         * ètvrtý výbìr
	         */

	        this.velke.addActionListener(new ActionListener(){
	        public void actionPerformed(ActionEvent e){
	         k = 6;
	        }});
	        this.male.addActionListener(new ActionListener(){
	        public void actionPerformed(ActionEvent e){
	         k = 1;
	        }});
	    }
	
}
