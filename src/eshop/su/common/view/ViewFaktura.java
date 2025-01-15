package eshop.su.common.view;

import eshop.bo.ciselniky.Faktura;
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

public class ViewFaktura extends SQLView {
	
	public static final String ID = Objednavka.ID.getId();
	public static final String CISLO_FAKTURY = Faktura.CISLO_FAKTURY.getId();
	public static final String DATUM_VYSTAVENIA = Faktura.DATUM_VYSTAVENIA.getId();
	public static final String DATUM_DODANIA = Faktura.DATUM_DODANIA.getId();
	public static final String DATUM_SPLATNOSTI = Faktura.DATUM_SPLATNOSTI.getId();

	public static final String OBJEDNAVKA = Faktura.OBJEDNAVKA.getId();
	
	//TODO - prepojene tabulky, budem postupne pridavat, co potrebujem
	public static final String ZAKAZNIK_ID = Zakaznik.ID.getId();
	public static final String ZAKAZNIK_KOD = Zakaznik.KOD.getId();
	public static final String ZAKAZNIK_NAZOV = Zakaznik.NAZOV.getId();
	public static final String VYSTAVIL = Objednavka.VYSTAVIL.getId();

	public ViewFaktura() {
		DBCatalog c = DBCatalog.getInstance();
		put(ID, Objednavka.ID, c.FAKTURA.ID);
		put(CISLO_FAKTURY, Faktura.CISLO_FAKTURY, c.FAKTURA.CISLO_FAKTURY);
		put(DATUM_VYSTAVENIA, Faktura.DATUM_VYSTAVENIA, c.FAKTURA.DATUM_VYSTAVENIA);
		put(DATUM_DODANIA, Faktura.DATUM_DODANIA, c.FAKTURA.DATUM_DODANIA);
		put(DATUM_SPLATNOSTI, Faktura.DATUM_SPLATNOSTI, c.FAKTURA.DATUM_SPLATNOSTI);
		/*
		//put(String, Attribute, SQLFieldAbst)
		//nove, tu si uz musim vyrobit, tj. naklonovat atribut z povodnej tabulky s captions, aby som mal aj nazov v tabulke spravne
		Attribute attr = Objednavka.CISLO_OBJEDNAVKY.cloneWithCaptions(Objednavka.POVODNA_OBJEDNAVKA);
		//a SQLField zadavam new SQLAliasField kde davam field ale zaroven definujem, ze z SQL Alias table
		put(POVODNA_OBJEDNAVKA_CISLO, attr, new SQLAliasField(c.OBJEDNAVKA.CISLO_OBJEDNAVKY, povodnaObjednavkaTable));
		*/		
		put(ZAKAZNIK_ID, Zakaznik.ID, c.OBCHODNY_PARTNER.ID);
		put(ZAKAZNIK_KOD, Zakaznik.KOD, c.OBCHODNY_PARTNER.KOD);
		put(ZAKAZNIK_NAZOV, Zakaznik.NAZOV, c.OBCHODNY_PARTNER.NAZOV);
		
		//musim nakolnovat, preco?
		Attribute attr = Uzivatel.UZIVATELSKE_MENO.cloneWithCaptions(Objednavka.VYSTAVIL);
		put(VYSTAVIL, attr, c.UZIVATEL.MENO);
		
		//musim nakolnovat, preco? v zasade sem posielam meno z ID
		attr = Objednavka.CISLO_OBJEDNAVKY.cloneWithCaptions(Faktura.OBJEDNAVKA);
		//attr = Objednavka.VYSTAVIL.cloneWithCaptions(Uzivatel.UZIVATELSKE_MENO);
		put(OBJEDNAVKA, attr, c.OBJEDNAVKA.CISLO_OBJEDNAVKY);
		
	}
	
	@Override
	public ViewCursor execute(SessionObject sessionObject, ViewQuery query) throws Exception {
		DBCatalog c = DBCatalog.getInstance();
		SQLQuery sqlQuery = new SQLQuery();
		sqlQuery.addTable(new SQLJoinCondition(c.FAKTURA));
		sqlQuery.addTable(new SQLJoinCondition(c.OBJEDNAVKA, SQLJoinCondition.LEFT_OUTER_JOIN, c.OBJEDNAVKA.ID, c.FAKTURA.OBJEDNAVKA));
		sqlQuery.addTable(new SQLJoinCondition(c.UZIVATEL, SQLJoinCondition.LEFT_OUTER_JOIN, c.UZIVATEL.ID, c.OBJEDNAVKA.VYSTAVIL));
		//tu musim spravit join s SQL Alias Table, cize nazov, podmienka, SQL Field (z tabulky, ktoru pridavam) a SQL Field v tomto pripade z aliasu!)
		//TODO - este si pozriet toto
		//sqlQuery.addTable(new SQLJoinCondition(povodnaObjednavkaTable, SQLJoinCondition.LEFT_OUTER_JOIN, c.OBJEDNAVKA.POVODNA_OBJEDNAVKA, new SQLAliasField(c.OBJEDNAVKA.ID, povodnaObjednavkaTable)));
		setViewQuery(sqlQuery, query);
		return ((EclipseLinkSession) sessionObject).execute(sqlQuery);
	}

}
