package eshop.su.common.view;


import eshop.bo.ciselniky.Faktura;
import eshop.bo.ciselniky.ObchodnyPartner;
import eshop.bo.ciselniky.Objednavka;
import eshop.bo.ciselniky.Zakaznik;
import eshop.su.db.DBCatalog;
import netframework.bo.attributes.Attribute;
import netframework.eclipselink.EclipseLinkSession;
import netframework.mediator.SessionObject;
import netframework.sql.SQLAliasField;
import netframework.sql.SQLAliasTable;
import netframework.sql.SQLJoinCondition;
import netframework.sql.SQLQuery;
import netframework.view.SQLView;
import netframework.view.ViewCursor;
import netframework.view.ViewQuery;
import zelpo.eclipselink.autorizacia.Uzivatel;

public class ViewObjednavka extends SQLView {
	
	public static final String ID = Objednavka.ID.getId();
	public static final String CISLO_OBJEDNAVKY = Objednavka.CISLO_OBJEDNAVKY.getId();
	public static final String DATUM = Objednavka.DATUM.getId();
	public static final String STAV_OBJEDNAVKY = Objednavka.STAV_OBJEDNAVKY.getId();
	public static final String SUMA = Objednavka.SUMA.getId();
	//nove
	public static final String POVODNA_OBJEDNAVKA_CISLO = Objednavka.POVODNA_OBJEDNAVKA.getId();
	
	public static final String ZAKAZNIK_ID = Zakaznik.ID.getId();
	public static final String ZAKAZNIK_KOD = Zakaznik.KOD.getId();
	public static final String ZAKAZNIK_NAZOV = Zakaznik.NAZOV.getId();
	public static final String VYSTAVIL = Objednavka.VYSTAVIL.getId();

	//v pripade, ze referencujem v ramci jednej tabulky, musim si vyrobit SQL Alias table
	private SQLAliasTable povodnaObjednavkaTable;
	
	public ViewObjednavka() {
		DBCatalog c = DBCatalog.getInstance();
		povodnaObjednavkaTable = new SQLAliasTable(c.OBJEDNAVKA, "POVOBJ");
		put(ID, Objednavka.ID, c.OBJEDNAVKA.ID);
		put(CISLO_OBJEDNAVKY, Objednavka.CISLO_OBJEDNAVKY, c.OBJEDNAVKA.CISLO_OBJEDNAVKY);
		put(DATUM, Objednavka.DATUM, c.OBJEDNAVKA.DATUM);
		put(STAV_OBJEDNAVKY, Objednavka.STAV_OBJEDNAVKY, c.OBJEDNAVKA.STAV_OBJEDNAVKY);
		put(SUMA, Objednavka.SUMA, c.OBJEDNAVKA.SUMA);
		
		//put(String, Attribute, SQLFieldAbst)
		//nove, tu si uz musim vyrobit, tj. naklonovat atribut z povodnej tabulky s captions, aby som mal aj nazov v tabulke spravne
		Attribute attr = Objednavka.CISLO_OBJEDNAVKY.cloneWithCaptions(Objednavka.POVODNA_OBJEDNAVKA);
		//a SQLField zadavam new SQLAliasField kde davam field ale zaroven definujem, ze z SQL Alias table
		put(POVODNA_OBJEDNAVKA_CISLO, attr, new SQLAliasField(c.OBJEDNAVKA.CISLO_OBJEDNAVKY, povodnaObjednavkaTable));
				
		//DBSkolenieCatalog tu uz idem podla tabulky zrejme, tj. OBCHODNY_PARTNER a nie ZAKAZNIK (taka tabulka neexistuje)
		put(ZAKAZNIK_ID, Zakaznik.ID, c.OBCHODNY_PARTNER.ID);
		put(ZAKAZNIK_KOD, Zakaznik.KOD, c.OBCHODNY_PARTNER.KOD);
		put(ZAKAZNIK_NAZOV, Zakaznik.NAZOV, c.OBCHODNY_PARTNER.NAZOV);
		
		attr = Uzivatel.MENO.cloneWithCaptions(Objednavka.VYSTAVIL);
		//attr = Objednavka.VYSTAVIL.cloneWithCaptions(Uzivatel.UZIVATELSKE_MENO);
		put(VYSTAVIL, attr, c.UZIVATEL.MENO);
		
	}
	
	@Override
	public ViewCursor execute(SessionObject sessionObject, ViewQuery query) throws Exception {
		DBCatalog c = DBCatalog.getInstance();
		SQLQuery sqlQuery = new SQLQuery();
		sqlQuery.addTable(new SQLJoinCondition(c.OBJEDNAVKA));
		sqlQuery.addTable(new SQLJoinCondition(c.OBCHODNY_PARTNER, SQLJoinCondition.LEFT_OUTER_JOIN, c.OBCHODNY_PARTNER.ID, c.OBJEDNAVKA.ZAKAZNIK));
		//do query, ked chcem najst uzivatela musim pridat join
		sqlQuery.addTable(new SQLJoinCondition(c.UZIVATEL, SQLJoinCondition.LEFT_OUTER_JOIN, c.UZIVATEL.ID, c.OBJEDNAVKA.VYSTAVIL));
		//tu musim spravit join s SQL Alias Table, cize nazov, podmienka, SQL Field (z tabulky, ktoru pridavam) a SQL Field v tomto pripade z aliasu!)
		//TODO - este si pozriet toto
		sqlQuery.addTable(new SQLJoinCondition(povodnaObjednavkaTable, SQLJoinCondition.LEFT_OUTER_JOIN, c.OBJEDNAVKA.POVODNA_OBJEDNAVKA, new SQLAliasField(c.OBJEDNAVKA.ID, povodnaObjednavkaTable)));
		setViewQuery(sqlQuery, query);
		return ((EclipseLinkSession) sessionObject).execute(sqlQuery);
	}

}
