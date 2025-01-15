package eshop.su.db;

import java.sql.Types;

import eshop.bo.ciselniky.Tovar;
import netframework.sql.SQLField;
import netframework.sql.SQLTable;

public class DBTovar extends SQLTable {
	
	public static final SQLField ID = new SQLField("id", Types.INTEGER, 11);
	public static final SQLField KOD = new SQLField("kod", Types.CHAR, 10);
	public static final SQLField NAZOV = new SQLField("nazov", Types.VARCHAR, 30);
	public static final SQLField CENA = new SQLField("cena", Types.DECIMAL, 8, 2);
	public static final SQLField VELKOST = new SQLField("velkost", Types.CHAR, 3);
	public static final SQLField SKLADOVA_ZASOBA = new SQLField("skl_zasoba", Types.DECIMAL, 8);
	public static final SQLField TOVAR_DRUH = new SQLField("tovar_druh_id", Types.INTEGER, 11);
	public static final SQLField DODAVATEL = new SQLField("obch_par_id", Types.INTEGER, 11);

	public DBTovar() {
		super("md1_tovar");
		addField(Tovar.ID.getId(), ID);
		addField(Tovar.KOD.getId(), KOD);
		addField(Tovar.NAZOV.getId(), NAZOV);
		addField(Tovar.CENA.getId(), CENA);
		addField(Tovar.VELKOST.getId(), VELKOST);
		addField(Tovar.SKLADOVA_ZASOBA.getId(), SKLADOVA_ZASOBA);
		addField(Tovar.TOVAR_DRUH.getId(), TOVAR_DRUH);
		addField(Tovar.DODAVATEL.getId(), DODAVATEL);
	
	}
}
