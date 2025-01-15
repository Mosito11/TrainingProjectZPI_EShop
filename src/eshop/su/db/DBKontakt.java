package eshop.su.db;

import java.sql.Types;

import eshop.bo.ciselniky.Kontakt;
import netframework.sql.SQLField;
import netframework.sql.SQLTable;

public class DBKontakt extends SQLTable {
	
	public static final SQLField ID = new SQLField("id", Types.INTEGER, 11);
	public static final SQLField MENO = new SQLField("meno", Types.CHAR, 20);
	public static final SQLField EMAIL = new SQLField("email", Types.CHAR, 20);
	public static final SQLField TELEFON = new SQLField("telefon", Types.CHAR, 13);
	public static final SQLField OBCHODNY_PARTNER = new SQLField("obch_par_id", Types.INTEGER, 11);

	public DBKontakt() {
		super("md1_kontakt");
		addField(Kontakt.ID.getId(), ID);
		addField(Kontakt.MENO.getId(), MENO);
		addField(Kontakt.EMAIL.getId(), EMAIL);
		addField(Kontakt.TELEFON.getId(), TELEFON);
		addField(Kontakt.OBCHODNY_PARTNER.getId(), OBCHODNY_PARTNER);
	}

}
