package eshop.su.db;

import java.sql.Types;

import eshop.bo.ciselniky.Objednavka;
import netframework.sql.SQLField;
import netframework.sql.SQLTable;

public class DBObjednavka extends SQLTable {
	
	public static final SQLField ID = new SQLField("id", Types.INTEGER, 11);
	public static final SQLField CISLO_OBJEDNAVKY = new SQLField("cislo_obj", Types.CHAR, 10);
	public static final SQLField DATUM = new SQLField("datum", Types.DATE, 10);
	public static final SQLField SUMA = new SQLField("suma", Types.DECIMAL, 8, 2);
	public static final SQLField STAV_OBJEDNAVKY = new SQLField("stav", Types.CHAR, 1);
	public static final SQLField ZAKAZNIK = new SQLField("obch_par_id", Types.INTEGER, 11);
	public static final SQLField POVODNA_OBJEDNAVKA = new SQLField("povodna_obj_id", Types.INTEGER, 11);
	//pridavam stlpec do tabulky tu
	public static final SQLField VYSTAVIL = new SQLField("vystavil", Types.CHAR, 10);
	

	public DBObjednavka() {
		super("md1_objednavka");
		addField(Objednavka.ID.getId(), ID);
		addField(Objednavka.CISLO_OBJEDNAVKY.getId(), CISLO_OBJEDNAVKY);
		addField(Objednavka.DATUM.getId(), DATUM);
		addField(Objednavka.SUMA.getId(), SUMA);
		addField(Objednavka.STAV_OBJEDNAVKY.getId(), STAV_OBJEDNAVKY);
		addField(Objednavka.ZAKAZNIK.getId(), ZAKAZNIK);
		addField(Objednavka.POVODNA_OBJEDNAVKA.getId(), POVODNA_OBJEDNAVKA);
		//a tu
		addField(Objednavka.VYSTAVIL.getId(), VYSTAVIL);
		
	}

}
