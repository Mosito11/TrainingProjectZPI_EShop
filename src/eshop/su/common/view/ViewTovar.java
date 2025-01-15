package eshop.su.common.view;

import eshop.bo.ciselniky.Objednavka;
import eshop.bo.ciselniky.Tovar;
import eshop.bo.ciselniky.TovarDruh;
import eshop.bo.ciselniky.Dodavatel;
import eshop.su.db.DBCatalog;
import netframework.eclipselink.EclipseLinkSession;
import netframework.mediator.SessionObject;
import netframework.sql.SQLJoinCondition;
import netframework.sql.SQLQuery;
import netframework.view.SQLView;
import netframework.view.ViewCursor;
import netframework.view.ViewQuery;

public class ViewTovar extends SQLView {
	
	public static final String ID = Tovar.ID.getId();
	public static final String KOD = Tovar.KOD.getId();
	public static final String NAZOV = Tovar.NAZOV.getId();
	public static final String CENA = Tovar.CENA.getId();
	public static final String VELKOST = Tovar.VELKOST.getId();
	public static final String SKLADOVA_ZASOBA = Tovar.SKLADOVA_ZASOBA.getId();
	
	public static final String TOVAR_DRUH_KOD = TovarDruh.KOD.getId();
	public static final String TOVAR_DRUH_NAZOV = TovarDruh.NAZOV.getId();
		
	public static final String DODAVATEL_ID = Dodavatel.ID.getId();
	public static final String DODAVATEL_KOD = Dodavatel.KOD.getId();
	public static final String DODAVATEL_NAZOV = Dodavatel.NAZOV.getId();
	public static final String DODAVATEL_ICO = Dodavatel.ICO.getId();
	
	public ViewTovar() {
		DBCatalog c = DBCatalog.getInstance();
		put(ID, Tovar.ID, c.TOVAR.ID);
		put(KOD, Tovar.KOD, c.TOVAR.KOD);
		put(NAZOV, Tovar.NAZOV, c.TOVAR.NAZOV);
		put(CENA, Tovar.CENA, c.TOVAR.CENA);
		put(VELKOST, Tovar.VELKOST, c.TOVAR.VELKOST);
		put(SKLADOVA_ZASOBA, Tovar.SKLADOVA_ZASOBA, c.TOVAR.SKLADOVA_ZASOBA);
		
		put(TOVAR_DRUH_KOD, TovarDruh.KOD, c.TOVAR_DRUH.KOD);
		put(TOVAR_DRUH_NAZOV, TovarDruh.NAZOV, c.TOVAR_DRUH.NAZOV);

		//DBSkolenieCatalog tu uz idem podla tabulky zrejme, tj. OBCHODNY_PARTNER a nie ZAKAZNIK (taka tabulka neexistuje)
		put(DODAVATEL_ID, Dodavatel.ID, c.OBCHODNY_PARTNER.ID);
		put(DODAVATEL_KOD, Dodavatel.KOD, c.OBCHODNY_PARTNER.KOD);
		put(DODAVATEL_NAZOV, Dodavatel.NAZOV, c.OBCHODNY_PARTNER.NAZOV);
		put(DODAVATEL_ICO, Dodavatel.ICO, c.OBCHODNY_PARTNER.ICO);	
	}
	
	@Override
	public ViewCursor execute(SessionObject sessionObject, ViewQuery query) throws Exception {
		DBCatalog c = DBCatalog.getInstance();
		SQLQuery sqlQuery = new SQLQuery();
		sqlQuery.addTable(new SQLJoinCondition(c.TOVAR));
		sqlQuery.addTable(new SQLJoinCondition(c.OBCHODNY_PARTNER, SQLJoinCondition.LEFT_OUTER_JOIN, c.OBCHODNY_PARTNER.ID, c.TOVAR.DODAVATEL));
		sqlQuery.addTable(new SQLJoinCondition(c.TOVAR_DRUH, SQLJoinCondition.LEFT_OUTER_JOIN, c.TOVAR_DRUH.ID, c.TOVAR.TOVAR_DRUH));
				setViewQuery(sqlQuery, query);
		return ((EclipseLinkSession) sessionObject).execute(sqlQuery);
	}

}
