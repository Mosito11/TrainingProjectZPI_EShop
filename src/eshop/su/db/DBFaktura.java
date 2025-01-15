package eshop.su.db;

import java.sql.Types;

import eshop.bo.ciselniky.Faktura;
import netframework.sql.SQLField;
import netframework.sql.SQLTable;

public class DBFaktura extends SQLTable {
	
	public static final SQLField ID = new SQLField("id", Types.INTEGER, 11);
	public static final SQLField CISLO_FAKTURY = new SQLField("cislo_fak", Types.CHAR, 10);
	public static final SQLField DATUM_VYSTAVENIA = new SQLField("datum_vyst", Types.DATE, 10);
	public static final SQLField DATUM_DODANIA = new SQLField("datum_dod", Types.DATE, 10);
	public static final SQLField DATUM_SPLATNOSTI = new SQLField("datum_splat", Types.DATE, 10);
	public static final SQLField OBJEDNAVKA = new SQLField("obj_id", Types.INTEGER, 11);
	
	public DBFaktura() {
		super("md1_faktura");

		addField(Faktura.ID.getId(), ID);
		addField(Faktura.CISLO_FAKTURY.getId(), CISLO_FAKTURY);
		addField(Faktura.DATUM_VYSTAVENIA.getId(), DATUM_VYSTAVENIA);
		addField(Faktura.DATUM_DODANIA.getId(), DATUM_DODANIA);
		addField(Faktura.DATUM_SPLATNOSTI.getId(), DATUM_SPLATNOSTI);
		addField(Faktura.OBJEDNAVKA.getId(), OBJEDNAVKA);
	}

}
