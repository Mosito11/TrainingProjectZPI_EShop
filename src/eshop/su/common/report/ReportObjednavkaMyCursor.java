package eshop.su.common.report;

import java.math.BigDecimal;

import eshop.bo.ciselniky.Objednavka;
import eshop.bo.ciselniky.ObjednavkaPolozka;
import eshop.bo.ciselniky.StavObjednavky;
import eshop.bo.ciselniky.Tovar;
import eshop.bo.ciselniky.Zakaznik;
import eshop.bo.common.Helper;
import eshop.su.common.report.prmt.PRObjednavkaMyCursor;
import eshop.su.db.DBCatalog;
import netball.server.component.table.TableContainer;
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
import netframework.sql.SQLItem;
import netframework.sql.SQLJoinCondition;
import netframework.sql.SQLQuery;
import netframework.sql.SQLValueConvertor;
import netframework.view.ViewCursor;
import netframework.view.ViewRow;

public class ReportObjednavkaMyCursor implements Report {
	
	public static final String DATUM = Objednavka.DATUM.getId();
	
	@Override 
    public ReportBuilder execute(ReportParameters prmt, SessionObject session) throws Exception{
        Parameters prmts = createParameters(prmt, session);        
        ViewCursor cursor = createCursor(prmts, session);
        ViewCursorReportSource report = new ViewCursorReportSource();             
        
       ReportColumnProperties[] cols = new ReportColumnProperties[] {
    		   
    		   //teraz skusam s nazvom tovaru
                ComponentBuilder.createReportColumn(Tovar.NAZOV.getId(), Tovar.NAZOV, false, session), 
                ComponentBuilder.createReportColumn(ObjednavkaPolozka.MNOZSTVO.getId(), ObjednavkaPolozka.MNOZSTVO, false, session),
                ComponentBuilder.createReportColumn(ObjednavkaPolozka.JEDNOTKOVA_CENA.getId(), ObjednavkaPolozka.JEDNOTKOVA_CENA, false, session),
                ComponentBuilder.createReportColumn(MyCursor.SUMA, ObjednavkaPolozka.JEDNOTKOVA_CENA, "Celkova suma", session),
        };
        /*
        ReportColumnProperties[] cols = new ReportColumnProperties[] {
            	ComponentBuilder.createReportColumn(Objednavka.CISLO_OBJEDNAVKY.getId(), Objednavka.CISLO_OBJEDNAVKY, true, session),
            	ComponentBuilder.createReportColumn(Objednavka.DATUM.getId(), Objednavka.DATUM, true, session),
            	ComponentBuilder.createReportColumn(Objednavka.SUMA.getId(), Objednavka.SUMA, true, session),
            	ComponentBuilder.createReportColumn(MyCursor.SUMA, Objednavka.SUMA, "Celkova suma", session)
            	//ComponentBuilder.createReportColumn(Objednavka.ZAKAZNIK.getId(), Zakaznik.NAZOV, false, session),
        };
        */
        report.setCursor(new MyCursor(cursor));
        report.setColumnProperty(cols);
        report.setUserName(session.getUser() != null ? session.getUser().getName() : null);
        report.setCompanyName((String)((EclipseLinkSession) session).getProperty("ESHOP"));
        report.setHeaderText(session.translateText(getName()) +  prmts.headerText);
        
        GroupColumn groupColumns[] = new GroupColumn[1];
        int indexCislo = cursor.getColumnIndex(Objednavka.CISLO_OBJEDNAVKY.getId());
        groupColumns[0] = new GroupColumn(indexCislo);
        groupColumns[0].setHeaderItem(new GroupColumnItem(indexCislo, ComponentBuilder.createRenderer(Objednavka.CISLO_OBJEDNAVKY, session), session.translateText(Objednavka.CISLO_OBJEDNAVKY.getCaption())));
        groupColumns[0].setFooterItem(new GroupColumnItem(indexCislo, ComponentBuilder.createRenderer(Objednavka.CISLO_OBJEDNAVKY, session), session.translateText(Objednavka.CISLO_OBJEDNAVKY.getCaption())));
        groupColumns[0].setFooterText(session.translateText("Celkovo "));
        
        NumberGroupSummarizer[] sumators = new NumberGroupSummarizer[] {
        	new NumberGroupSummarizer(cursor.getColumnIndex(MyCursor.SUMA), report.getColumnIndex(MyCursor.SUMA), report.getRenderer(MyCursor.SUMA)),
        };
        report.setGroupRules(new GroupRules(groupColumns, sumators));
        report.setGroupTotalRules(new GroupTotalRules(sumators, session.translateText("Spolu")));
        
        return new ReportBuilder(report);
    }
	
	private ViewCursor createCursor(Parameters prmts, SessionObject session) throws Exception {
    	DBCatalog c = DBCatalog.getInstance();
        SQLQuery query = new SQLQuery();
        /*
        query.addTable(new SQLJoinCondition(c.OBJEDNAVKA));
        query.addTable(new SQLJoinCondition(c.OBCHODNY_PARTNER, c.OBCHODNY_PARTNER.ID, c.OBJEDNAVKA.ZAKAZNIK));        
        query.addField(Objednavka.DATUM.getId(), c.OBJEDNAVKA.DATUM);
    	query.addField(Objednavka.SUMA.getId(), c.OBJEDNAVKA.SUMA);
    	query.addField(Zakaznik.KOD.getId(), c.OBCHODNY_PARTNER.KOD);
    	query.addField(Zakaznik.NAZOV.getId(), c.OBCHODNY_PARTNER.NAZOV);
    	query.addField(Objednavka.CISLO_OBJEDNAVKY.getId(), c.OBJEDNAVKA.CISLO_OBJEDNAVKY);
    	query.addField(MyCursor.SUMA, new EmptyField());
        query.addOrdering(c.OBCHODNY_PARTNER.KOD);
        query.addOrdering(c.OBJEDNAVKA.CISLO_OBJEDNAVKY);
        SQLExpression exp1 = SQLExpressionBuilder.get(c.OBJEDNAVKA.STAV_OBJEDNAVKY).equal(StavObjednavky.DOKONCENA.getKey());
        */
        
        query.addTable(new SQLJoinCondition(c.OBJEDNAVKA));
        query.addTable(new SQLJoinCondition(c.OBCHODNY_PARTNER, c.OBCHODNY_PARTNER.ID, c.OBJEDNAVKA.ZAKAZNIK));
        query.addTable(new SQLJoinCondition(c.OBJEDNAVKA_POLOZKA, c.OBJEDNAVKA_POLOZKA.OBJEDNAVKA, c.OBJEDNAVKA.ID));
        query.addTable(new SQLJoinCondition(c.TOVAR, c.TOVAR.ID, c.OBJEDNAVKA_POLOZKA.TOVAR));
    	query.addField(Objednavka.DATUM.getId(), c.OBJEDNAVKA.DATUM);
    	query.addField(Objednavka.CISLO_OBJEDNAVKY.getId(), c.OBJEDNAVKA.CISLO_OBJEDNAVKY);
    	query.addField(ObjednavkaPolozka.TOVAR.getId(), c.OBJEDNAVKA_POLOZKA.TOVAR);
    	query.addField(Tovar.NAZOV.getId(), c.TOVAR.NAZOV);
    	
    	query.addField(ObjednavkaPolozka.MNOZSTVO.getId(), c.OBJEDNAVKA_POLOZKA.MNOZSTVO);
    	query.addField(ObjednavkaPolozka.JEDNOTKOVA_CENA.getId(), c.OBJEDNAVKA_POLOZKA.JEDNOTKOVA_CENA);
    	query.addField(MyCursor.SUMA, new EmptyField()); 
        query.addOrdering(c.OBJEDNAVKA.CISLO_OBJEDNAVKY);
        SQLExpression exp1 = SQLExpressionBuilder.get(c.OBJEDNAVKA.STAV_OBJEDNAVKY).equal(StavObjednavky.DOKONCENA.getKey());
        //SQLExpression exp2 = SQLExpressionBuilder.get(c.FAKTURA.TYP).equal(TypFaktury.ODBERATELSKA.getKey());
       
        query.setExpression(exp1);
        if (prmts.datum != null) {
        	SQLExpression exp = FrameworkUtilities.createSQLExpression(c.OBJEDNAVKA.DATUM, prmts.datum); 
            query.setExpression(query.getExpression() != null ? query.getExpression().and(exp) : exp);
        }
        if (prmts.zakaznik != null) {
        	SQLExpression exp = FrameworkUtilities.createSQLExpression(c.OBCHODNY_PARTNER.KOD, prmts.zakaznik); 
            query.setExpression(query.getExpression() != null ? query.getExpression().and(exp) : exp);
        }
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
        }
        return parameteres;
    }
	/*
	private Parameters createParameters(ReportParameters prmt, SessionObject session) throws Exception {
        Parameters parameters = new Parameters();        
        
        if (prmt != null) {
           Object value = prmt.getValue(DATUM);
           if (value != null) {
              parameters.datum = value;
              parameters.headerText.add(Objednavka.DATUM.getLongCaption(), value, session);
           }else{
        	  throw new IllegalArgumentException(session.translateText("DATUM_MUSI_BYT_VYPLNENY")); 
           }
           value = prmt.getValue(ZAKAZNIK_KOD);
           if (value != null) {
              parameters.zakaznik = value;
              parameters.headerText.add(Zakaznik.KOD.getLongCaption(), value, session);
           }
        }
        return parameters;
    }
	*/
	@Override
	public String getName() {
		return "Zoznam dokoncenych objednavok podla tovarovych poloziek";
	}
	
	@Override
	public String getDescription() {
		return "Report typu My Cursor";
	}
	
	@Override
	public Class<PRObjednavkaMyCursor> getParameterMediatorClass() {
		return PRObjednavkaMyCursor.class;
	}
	
	private static class EmptyField implements SQLItem {

		@Override
		public String toSqlSyntax(int sqlType, SQLValueConvertor convertor) {
			return "0";
		}
	}
	
	private static class MyCursor implements ViewCursor {

		private ViewCursor cursor;
		private static final String SUMA = "suma";
		private int celkovaSumaIndex;
		private int mnozstvoIndex;
		private int jednotkovaCenaIndex;
		
		public MyCursor (ViewCursor cursor) {
			this.cursor = cursor;
			celkovaSumaIndex = cursor.getColumnIndex(SUMA);
			mnozstvoIndex = cursor.getColumnIndex(ObjednavkaPolozka.MNOZSTVO.getId());
			jednotkovaCenaIndex = cursor.getColumnIndex(ObjednavkaPolozka.JEDNOTKOVA_CENA.getId());
			
		}
		
		@Override
		public void close() throws Exception {
			cursor.close();
		}

		@Override
		public int getColumnIndex(Object colIndex) {
			return this.cursor.getColumnIndex(colIndex);
		}

		@Override
		public String[] getColumnNames() {
			return this.getColumnNames();
		}

		@Override
		public boolean hasNext() throws Exception {
			return cursor.hasNext();
		}

		@Override
		public ViewRow next() throws Exception {
			ViewRow row = this.cursor.next();
			BigDecimal mnozstvo = (BigDecimal) row.getValueAt(mnozstvoIndex);
			BigDecimal jednotkovaCena = (BigDecimal) row.getValueAt(jednotkovaCenaIndex);
			if (mnozstvo != null && jednotkovaCena != null) {
				BigDecimal celkovaCena = mnozstvo.multiply(jednotkovaCena);
				celkovaCena = celkovaCena.setScale(2, BigDecimal.ROUND_HALF_UP);
				row.setValueAt(celkovaCena, celkovaSumaIndex);
			}else{
				row.setValueAt(Helper.DEFAULT_VALUE, celkovaSumaIndex);
			}
			return row;
		}

		@Override
		public TableContainer readToContainer() throws Exception {
			return null;
		}
	}    
	
	private static class Parameters {
	    public Object datum;
	    public Object zakaznik;
	    public FilterTextBuilder headerText = new FilterTextBuilder();
	} 

}
