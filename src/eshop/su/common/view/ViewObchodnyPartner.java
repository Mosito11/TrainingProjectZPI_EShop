package eshop.su.common.view;

import java.util.ArrayList;
import java.util.List;

import eshop.bo.ciselniky.Dodavatel;
import eshop.bo.ciselniky.ObchodnyPartner;
import eshop.su.db.DBCatalog;
import netframework.eclipselink.EclipseLinkSession;
import netframework.mediator.SessionObject;
import netframework.sql.SQLJoinCondition;
import netframework.sql.SQLQuery;
import netframework.view.SQLView;
import netframework.view.ViewCursor;
import netframework.view.ViewQuery;

public class ViewObchodnyPartner extends SQLView {
	
	public static final String ID = ObchodnyPartner.ID.getId();
	public static final String KOD = ObchodnyPartner.KOD.getId();
	public static final String NAZOV = ObchodnyPartner.NAZOV.getId();
	public static final String ICO = Dodavatel.ICO.getId();
	public static final String TYP = ObchodnyPartner.TYP.getId();

	public ViewObchodnyPartner() {
		DBCatalog c = DBCatalog.getInstance(); 
		put(ID, ObchodnyPartner.ID, c.OBCHODNY_PARTNER.ID);
		put(KOD, ObchodnyPartner.KOD, c.OBCHODNY_PARTNER.KOD);
		put(NAZOV, ObchodnyPartner.NAZOV, c.OBCHODNY_PARTNER.NAZOV);
		put(TYP, ObchodnyPartner.TYP, c.OBCHODNY_PARTNER.TYP);
		put(ICO, Dodavatel.ICO, c.OBCHODNY_PARTNER.ICO);
	}

	@Override
	public ViewCursor execute(SessionObject sessionObject, ViewQuery query) throws Exception {
		DBCatalog c = DBCatalog.getInstance(); 
		SQLQuery sqlQuery = new SQLQuery();
		
		sqlQuery.addTable(new SQLJoinCondition(c.OBCHODNY_PARTNER));
		
		//taktok?
		//sqlQuery.addTable(new SQLJoinCondition(c.KONTAKT, SQLJoinCondition.RIGHT_OUTER_JOIN, c.OBCHODNY_PARTNER.ID, c.KONTAKT.OBCHODNY_PARTNER));
        /* otazka, ci tu potrebujem joinovat tabulky, ak chcem vidiet kontakty 
		sqlQuery.addTable(new SQLJoinCondition(c.MENA, SQLJoinCondition.LEFT_OUTER_JOIN, c.MENA.ID, c.FAKTURA.MENA));
        sqlQuery.addTable(new SQLJoinCondition(c.ZAKAZNIK, SQLJoinCondition.LEFT_OUTER_JOIN, c.ZAKAZNIK.ID, c.FAKTURA.ZAKAZNIK));
		*/
		
		setViewQuery(sqlQuery, query);
		return ((EclipseLinkSession) sessionObject).execute(sqlQuery);
	}
	
	/*public Object[][] transformCursorToObject(ViewCursor cursor) {
		
		
		
		return result;
	}*/

}
