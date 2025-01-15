package eshop.su.db;

import java.sql.Types;

import netframework.sql.SQLField;
import netframework.sql.SQLTable;

public class DBObchodnyPartnerAdresa extends SQLTable {
	
	public static final SQLField OBCHODNY_PARTNER = new SQLField("obch_par_id", Types.INTEGER, 11);
	public static final SQLField ADRESA = new SQLField("adresa_id", Types.INTEGER, 11);

	public DBObchodnyPartnerAdresa() {
		super("md1_obchodny_partner_adresa");
		addField("DBObchodnyPartnerAdresa.obchodny_partner", OBCHODNY_PARTNER);
		addField("DBObchodnyPartnerAdresa.adresa", ADRESA);
	}
}
