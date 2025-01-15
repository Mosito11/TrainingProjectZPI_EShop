package eshop.su.common.report;

import eshop.bo.ciselniky.Dodavatel;
import eshop.bo.ciselniky.Objednavka;
import eshop.bo.ciselniky.ObjednavkaPolozka;
import eshop.bo.ciselniky.StavObjednavky;
import eshop.bo.ciselniky.Tovar;
import eshop.bo.ciselniky.TovarDruh;
import eshop.bo.ciselniky.Zakaznik;
import eshop.su.common.report.prmt.PRObjednavkaMySumator;
import eshop.su.db.DBCatalog;
import netframework.FrameworkUtilities;
import netframework.eclipselink.EclipseLinkSession;
import netframework.mediator.ComponentBuilder;
import netframework.mediator.MDReportTreeItemPrmts;
import netframework.mediator.SessionObject;
import netframework.report.Report;
import netframework.report.ReportBuilder;
import netframework.report.ReportColumnProperties;
import netframework.report.ReportParameters;
import netframework.report.ViewCursorReportSource;
import netframework.report.grouping.GroupColumn;
import netframework.report.grouping.GroupColumnItem;
import netframework.report.grouping.GroupRules;
import netframework.report.grouping.GroupTotalRules;
import netframework.report.grouping.NumberGroupSummarizer;
import netframework.sql.SQLExpression;
import netframework.sql.SQLExpressionBuilder;
import netframework.sql.SQLJoinCondition;
import netframework.sql.SQLQuery;
import netframework.view.ViewCursor;

//TODO / zamysliet sa poriadne a nastavit tie stlpce logicky a prehladne
public class ReportObjednavkaMySumator implements Report {
	
	public static final String DATUM = Objednavka.DATUM.getId();
    public static final String ZAKAZNIK_KOD = Zakaznik.KOD.getId();

    @Override
    public ReportBuilder execute(ReportParameters prmt, SessionObject session) throws Exception {
    	
    	Parameters prmts = createParameters(prmt, session);        
        ViewCursor cursor = createCursor(prmts, session);
        
        ViewCursorReportSource report = new ViewCursorReportSource();             
        ReportColumnProperties[] cols = new ReportColumnProperties[] {
            	ComponentBuilder.createReportColumn(Objednavka.CISLO_OBJEDNAVKY.getId(), Objednavka.CISLO_OBJEDNAVKY, true, session),
            	ComponentBuilder.createReportColumn(Objednavka.DATUM.getId(), Objednavka.DATUM, true, session),
            	ComponentBuilder.createReportColumn(Objednavka.SUMA.getId(), Objednavka.SUMA, true, session),
            	/*
            	ComponentBuilder.createReportColumn(Tovar.KOD.getId(), Tovar.KOD, false, session),
            	ComponentBuilder.createReportColumn(Tovar.NAZOV.getId(), Tovar.NAZOV, false, session),
            	ComponentBuilder.createReportColumn(ObjednavkaPolozka.OBJEDNAVKA.getId(), ObjednavkaPolozka.OBJEDNAVKA, false, session),
            	ComponentBuilder.createReportColumn(Tovar.CENA.getId(), Tovar.CENA, false, session),
            	ComponentBuilder.createReportColumn(Tovar.DODAVATEL.getId(), Tovar.DODAVATEL, false, session),
            	ComponentBuilder.createReportColumn(Tovar.TOVAR_DRUH.getId(), Tovar.TOVAR_DRUH, false, session),
            	ComponentBuilder.createReportColumn(TovarDruh.NAZOV.getId(), TovarDruh.NAZOV, false, session),
            	ComponentBuilder.createReportColumn(Dodavatel.NAZOV.getId(), Dodavatel.NAZOV, false, session),
            	ComponentBuilder.createReportColumn(Dodavatel.ICO.getId(), Dodavatel.ICO, false, session),
            	*/
        };
        report.setCursor(cursor);
        report.setColumnProperty(cols);
        report.setUserName(session.getUser() != null ? session.getUser().getName() : null);
        report.setCompanyName((String)((EclipseLinkSession) session).getProperty("ESHOP"));
        report.setHeaderText(session.translateText(getName()) +  prmts.headerText);
        
        //vyskusam mat dve grupovania, podla Druhu Tovaru a podla Dodavatela
        GroupColumn groupColumns[] = new GroupColumn[1];
        //alt. TovarDruh.NAZOV, ci?
        int indexZakaznik = cursor.getColumnIndex(Zakaznik.KOD.getId());
        groupColumns[0] = new GroupColumn(indexZakaznik);
        groupColumns[0].setHeaderItem(new GroupColumnItem(indexZakaznik, ComponentBuilder.createRenderer(Zakaznik.KOD, session), Zakaznik.KOD.getLongCaption()));
        groupColumns[0].setFooterItem(new GroupColumnItem(indexZakaznik, ComponentBuilder.createRenderer(Zakaznik.KOD, session), Zakaznik.KOD.getLongCaption()));
        groupColumns[0].setFooterText(session.translateText(Zakaznik.KOD.getCaption()));
        /*
        int indexDodavatel = cursor.getColumnIndex(Tovar.DODAVATEL.getId());
        groupColumns[1] = new GroupColumn(indexDodavatel);
        groupColumns[1].setHeaderItem(new GroupColumnItem(indexDodavatel, ComponentBuilder.createRenderer(Tovar.DODAVATEL, session), Tovar.DODAVATEL.getLongCaption()));
        groupColumns[1].setFooterItem(new GroupColumnItem(indexDodavatel, ComponentBuilder.createRenderer(Tovar.DODAVATEL, session), Tovar.DODAVATEL.getLongCaption()));
        groupColumns[1].setFooterText(session.translateText(Tovar.DODAVATEL.getCaption()));
        */
        //zatial skusim bez Mysummarizer ale NumberGroupSummarizer
        NumberGroupSummarizer[] sumators = new NumberGroupSummarizer[] {
        //necham sumu ale teoreticky mozem skusit nejako aj COUNT vymysliet?
            new NumberGroupSummarizer(cursor.getColumnIndex(Objednavka.SUMA.getId()), report.getColumnIndex(Objednavka.SUMA.getId()), report.getRenderer(Objednavka.SUMA.getId())),
        };
        report.setGroupRules(new GroupRules(groupColumns, sumators));
        report.setGroupTotalRules(new GroupTotalRules(sumators, session.translateText("Spolu: ")));
        
        return new ReportBuilder(report);
    }
    
    private ViewCursor createCursor(Parameters prmts, SessionObject session) throws Exception {
    	DBCatalog c = DBCatalog.getInstance();
        SQLQuery query = new SQLQuery();
        
        query.addTable(new SQLJoinCondition(c.OBJEDNAVKA));
        query.addTable(new SQLJoinCondition(c.OBCHODNY_PARTNER, c.OBCHODNY_PARTNER.ID, c.OBJEDNAVKA.ZAKAZNIK));        
        query.addField(Objednavka.DATUM.getId(), c.OBJEDNAVKA.DATUM);
    	query.addField(Objednavka.SUMA.getId(), c.OBJEDNAVKA.SUMA);
    	query.addField(Zakaznik.KOD.getId(), c.OBCHODNY_PARTNER.KOD);
    	query.addField(Zakaznik.NAZOV.getId(), c.OBCHODNY_PARTNER.NAZOV);
    	query.addField(Objednavka.CISLO_OBJEDNAVKY.getId(), c.OBJEDNAVKA.CISLO_OBJEDNAVKY);
        query.addOrdering(c.OBCHODNY_PARTNER.KOD);
        query.addOrdering(c.OBJEDNAVKA.CISLO_OBJEDNAVKY);
        SQLExpression exp1 = SQLExpressionBuilder.get(c.OBJEDNAVKA.STAV_OBJEDNAVKY).equal(StavObjednavky.DOKONCENA.getKey());
        
        /*
        //potrebujem vsetky joiny tu?
        query.addTable(new SQLJoinCondition(c.OBJEDNAVKA_POLOZKA));
        //query.addTable(new SQLJoinCondition(c.OBJEDNAVKA_POLOZKA, SQLJoinCondition.LEFT_OUTER_JOIN, c.OBJEDNAVKA.ID, c.OBJEDNAVKA_POLOZKA.OBJEDNAVKA));
        query.addTable(new SQLJoinCondition(c.TOVAR, c.TOVAR.ID, c.OBJEDNAVKA_POLOZKA.TOVAR));  
        //query.addTable(new SQLJoinCondition(c.TOVAR_DRUH, c.TOVAR_DRUH.ID, c.TOVAR.TOVAR_DRUH));
        //query.addTable(new SQLJoinCondition(c.OBCHODNY_PARTNER, c.OBCHODNY_PARTNER.ID, c.TOVAR.DODAVATEL));
        
        //query.addField(Objednavka.DATUM.getId(), c.OBJEDNAVKA.DATUM);
    	//query.addField(Objednavka.SUMA.getId(), c.OBJEDNAVKA.SUMA);
    	//query.addField(Objednavka.CISLO_OBJEDNAVKY.getId(), c.OBJEDNAVKA.CISLO_OBJEDNAVKY);
    	
    	//query.addField(ObjednavkaPolozka.OBJEDNAVKA.getId(), c.OBJEDNAVKA_POLOZKA.OBJEDNAVKA);
    	query.addField(ObjednavkaPolozka.TOVAR.getId(), c.OBJEDNAVKA_POLOZKA.TOVAR);
    	query.addField(Tovar.KOD.getId(), c.TOVAR.KOD);
    	query.addField(Tovar.NAZOV.getId(), c.TOVAR.NAZOV);
    	query.addField(Tovar.CENA.getId(), c.TOVAR.CENA);
    	query.addField(Tovar.TOVAR_DRUH.getId(), c.TOVAR.TOVAR_DRUH);
    	//query.addField(TovarDruh.NAZOV.getId(), c.TOVAR_DRUH.NAZOV);
    	//query.addField(Dodavatel.NAZOV.getId(), c.OBCHODNY_PARTNER.NAZOV);
    	//query.addField(Dodavatel.ICO.getId(), c.OBCHODNY_PARTNER.ICO);
    	    	
        //query.addOrdering(c.OBCHODNY_PARTNER.ICO);
        query.addOrdering(c.OBJEDNAVKA_POLOZKA.TOVAR);
        //SQLExpression exp1 = SQLExpressionBuilder.get(c.OBJEDNAVKA_POLOZKA.TOVAR).equal(value);
        
        //SQLExpression exp1 = SQLExpressionBuilder.get(c.OBJEDNAVKA.STAV_OBJEDNAVKY).equal(StavObjednavky.DOKONCENA.getKey());
        //SQLExpression exp2 = SQLExpressionBuilder.get(c.FAKTURA.TYP).equal(TypFaktury.ODBERATELSKA.getKey());
         *
         */
        query.setExpression(exp1);
        if (prmts.datum != null) {
        	SQLExpression exp = FrameworkUtilities.createSQLExpression(c.OBJEDNAVKA.DATUM, prmts.datum); 
            query.setExpression(query.getExpression() != null ? query.getExpression().and(exp) : exp);
        }
        
        if (prmts.zakaznikKod != null) {
        	SQLExpression exp = FrameworkUtilities.createSQLExpression(c.OBCHODNY_PARTNER.KOD, prmts.zakaznikKod); 
            query.setExpression(query.getExpression() != null ? query.getExpression().and(exp) : exp);
        }
        //System.out.println("This is query " + query);
        return ((EclipseLinkSession) session).execute(query); 
    }
    
    private Parameters createParameters(ReportParameters prmt, SessionObject session) throws Exception {
        Parameters parameteres = new Parameters();
        if (prmt != null) {
           Object value = prmt.getValue(DATUM);
           if (value != null) {
              parameteres.datum = value;
              parameteres.headerText.add(Objednavka.DATUM.getLongCaption(), value, session);
           }else{
        	  throw new IllegalArgumentException(session.translateText("DATUM_MUSI_BYT_VYPLNENY")); 
           }
           value = prmt.getValue(ZAKAZNIK_KOD);
           if (value != null) {
              parameteres.zakaznikKod = value;
              parameteres.headerText.add(Zakaznik.KOD.getLongCaption(), value, session);
           }
        }
        return parameteres;
    }
    
	@Override
	public String getName() {
		return "Zoznam dokoncenych objednavok podla zakaznikov";
	}

	@Override
	public String getDescription() {
		return "Report typu MySummator";
	}

	@Override
	public Class<PRObjednavkaMySumator> getParameterMediatorClass() {
		return PRObjednavkaMySumator.class;
	}

	private static class Parameters {
		
	    public Object datum;
	    public Object zakaznikKod;
	    public FilterTextBuilder headerText = new FilterTextBuilder();
	    
	}  	

}
