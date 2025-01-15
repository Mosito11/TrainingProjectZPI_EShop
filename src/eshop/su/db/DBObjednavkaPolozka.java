package eshop.su.db;

import java.sql.Types;

import eshop.bo.ciselniky.ObjednavkaPolozka;
import netframework.sql.SQLField;
import netframework.sql.SQLTable;

public class DBObjednavkaPolozka extends SQLTable {
	
	public static final SQLField ID = new SQLField("id", Types.INTEGER, 11);
	public static final SQLField JEDNOTKOVA_CENA = new SQLField("jedn_cena", Types.DECIMAL, 8, 2);
	public static final SQLField MNOZSTVO = new SQLField("mnozstvo", Types.DECIMAL, 8);
	public static final SQLField SUMA = new SQLField("suma", Types.DECIMAL, 8, 2);
	public static final SQLField TOVAR = new SQLField("tovar_id", Types.INTEGER, 11);
	public static final SQLField OBJEDNAVKA = new SQLField("objednavka_id", Types.INTEGER, 11);

	public DBObjednavkaPolozka() {
		super("md1_objednavka_polozka");
		addField(ObjednavkaPolozka.ID.getId(), ID);
		addField(ObjednavkaPolozka.JEDNOTKOVA_CENA.getId(), JEDNOTKOVA_CENA);
		addField(ObjednavkaPolozka.MNOZSTVO.getId(), MNOZSTVO);
		addField(ObjednavkaPolozka.SUMA.getId(), SUMA);
		addField(ObjednavkaPolozka.TOVAR.getId(), TOVAR);
		addField(ObjednavkaPolozka.OBJEDNAVKA.getId(), OBJEDNAVKA);
	}

}
