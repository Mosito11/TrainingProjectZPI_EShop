package eshop.su.db;

import eshop.bo.ciselniky.Adresa;
import eshop.bo.ciselniky.Faktura;
import eshop.bo.ciselniky.Kontakt;
import eshop.bo.ciselniky.ObchodnyPartner;
import eshop.bo.ciselniky.Objednavka;
import eshop.bo.ciselniky.ObjednavkaPolozka;
import eshop.bo.ciselniky.Tovar;
import eshop.bo.ciselniky.TovarDruh;
import netframework.FrameworkUtilities;
import netframework.sql.SQLCatalog;
import zelpo.eclipselink.autorizacia.Uzivatel;
import zelpo.eclipselink.autorizacia.db.DBUzivatel;

public class DBCatalog extends SQLCatalog {
	
	public static final DBAdresa ADRESA = new DBAdresa(); 
	public static final DBKontakt KONTAKT = new DBKontakt(); 
	public static final DBObchodnyPartner OBCHODNY_PARTNER = new DBObchodnyPartner(); 
	public static final DBObjednavka OBJEDNAVKA = new DBObjednavka(); 
	public static final DBObjednavkaPolozka OBJEDNAVKA_POLOZKA = new DBObjednavkaPolozka(); 
	public static final DBTovar TOVAR = new DBTovar(); 
	public static final DBTovarDruh TOVAR_DRUH = new DBTovarDruh();
	public static final DBFaktura FAKTURA = new DBFaktura();
	//do katalogu musim pridat uzivatela
	public static final DBUzivatel UZIVATEL = new DBUzivatel();
	//kvoli many to many joinu 
	public static final DBObchodnyPartnerAdresa OBCHODNY_PARTNER_ADRESA = new DBObchodnyPartnerAdresa();
	
	private static DBCatalog catalog = new DBCatalog();
	
	private DBCatalog(){
		addTable(FrameworkUtilities.getShortClassName(Adresa.class), ADRESA);
		addTable(FrameworkUtilities.getShortClassName(Kontakt.class), KONTAKT);
		addTable(FrameworkUtilities.getShortClassName(ObchodnyPartner.class), OBCHODNY_PARTNER);
		addTable(FrameworkUtilities.getShortClassName(Objednavka.class), OBJEDNAVKA);
		addTable(FrameworkUtilities.getShortClassName(ObjednavkaPolozka.class), OBJEDNAVKA_POLOZKA);
		addTable(FrameworkUtilities.getShortClassName(Tovar.class), TOVAR);
		addTable(FrameworkUtilities.getShortClassName(TovarDruh.class), TOVAR_DRUH);
		addTable(FrameworkUtilities.getShortClassName(Faktura.class), FAKTURA);
		addTable(FrameworkUtilities.getShortClassName(Uzivatel.class), UZIVATEL);
		addTable(FrameworkUtilities.getShortClassName(DBObchodnyPartnerAdresa.class), OBCHODNY_PARTNER_ADRESA);
	}
	   
	public static DBCatalog getInstance() {
	    return catalog;
    }

}
