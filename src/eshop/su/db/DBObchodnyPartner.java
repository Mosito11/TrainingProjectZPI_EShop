package eshop.su.db;

import java.sql.Types;

import eshop.bo.ciselniky.Dodavatel;
import eshop.bo.ciselniky.ObchodnyPartner;
import netframework.sql.SQLField;
import netframework.sql.SQLTable;

public class DBObchodnyPartner extends SQLTable {
	
	public static final SQLField ID = new SQLField("id", Types.INTEGER, 11);
	public static final SQLField KOD = new SQLField("kod", Types.CHAR, 10);
	public static final SQLField NAZOV = new SQLField("nazov", Types.CHAR, 30);
	public static final SQLField ICO = new SQLField("ico", Types.VARCHAR, 8);
	public static final SQLField TYP = new SQLField("class_id", Types.CHAR, 1);

	public DBObchodnyPartner() {
		super("md1_obchodny_partner");
		addField(ObchodnyPartner.ID.getId(), ID);
		addField(ObchodnyPartner.KOD.getId(), KOD);
		addField(ObchodnyPartner.NAZOV.getId(), NAZOV);
		addField(Dodavatel.ICO.getId(), ICO);
		addField(ObchodnyPartner.TYP.getId(), TYP);
	}
}
