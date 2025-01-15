package eshop.su.db;

import java.sql.Types;

import eshop.bo.ciselniky.TovarDruh;
import netframework.sql.SQLField;
import netframework.sql.SQLTable;

public class DBTovarDruh extends SQLTable {
	
	public static final SQLField ID = new SQLField("id", Types.INTEGER, 11);
	public static final SQLField KOD = new SQLField("kod", Types.CHAR, 10);
	public static final SQLField NAZOV = new SQLField("nazov", Types.VARCHAR, 30);

	public DBTovarDruh() {
		super("md1_tovar_druh");
		addField(TovarDruh.ID.getId(), ID);
		addField(TovarDruh.KOD.getId(), KOD);
		addField(TovarDruh.NAZOV.getId(), NAZOV);
	}

}
