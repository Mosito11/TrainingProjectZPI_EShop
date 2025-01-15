package eshop.su.common.report;

import eshop.bo.ciselniky.Dodavatel;
import eshop.bo.ciselniky.Objednavka;
import eshop.bo.ciselniky.ObjednavkaPolozka;
import eshop.bo.ciselniky.StavObjednavky;
import eshop.bo.ciselniky.Tovar;
import eshop.bo.ciselniky.TovarDruh;
import eshop.bo.ciselniky.Zakaznik;
import eshop.su.common.report.prmt.PRObjednavkaMySumator;
import eshop.su.common.report.prmt.PRObjednavkaTovarMySumator;
import eshop.su.db.DBCatalog;
import netball.server.component.renderer.ComponentRenderer;
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
import netframework.report.grouping.GroupSummarizer;
import netframework.report.grouping.GroupTotalRules;
import netframework.report.grouping.NumberGroupSummarizer;
import netframework.sql.SQLExpression;
import netframework.sql.SQLExpressionBuilder;
import netframework.sql.SQLJoinCondition;
import netframework.sql.SQLQuery;
import netframework.view.ViewCursor;
import netframework.view.ViewRow;

public class ReportObjednavkaTovarMySumator implements Report {
	
	public static final String DATUM = Objednavka.DATUM.getId();

	@Override
	public ReportBuilder execute(ReportParameters prmt, SessionObject session) throws Exception {
		
		Parameters prmts = createParameters(prmt, session);        
        ViewCursor cursor = createCursor(prmts, session);
        
        ViewCursorReportSource report = new ViewCursorReportSource();             
        ReportColumnProperties[] cols = new ReportColumnProperties[] {
        		ComponentBuilder.createReportColumn(Tovar.KOD.getId(), Tovar.KOD, false, session),
        		ComponentBuilder.createReportColumn(Tovar.NAZOV.getId(), Tovar.NAZOV, false, session), 
                ComponentBuilder.createReportColumn(ObjednavkaPolozka.MNOZSTVO.getId(), ObjednavkaPolozka.MNOZSTVO, false, session),
                ComponentBuilder.createReportColumn(Objednavka.CISLO_OBJEDNAVKY.getId(), Objednavka.CISLO_OBJEDNAVKY, false, session),
            	
        };
        report.setCursor(cursor);
        report.setColumnProperty(cols);
        report.setUserName(session.getUser() != null ? session.getUser().getName() : null);
        report.setCompanyName((String)((EclipseLinkSession) session).getProperty("ESHOP"));
        report.setHeaderText(session.translateText(getName()) + prmts.headerText);
        
        //vyskusam mat dve grupovania, podla Druhu Tovaru a podla Dodavatela
        GroupColumn groupColumns[] = new GroupColumn[1];
        //alt. TovarDruh.NAZOV, ci?
         
        int indexDodavatel = cursor.getColumnIndex(Dodavatel.ICO.getId());
        groupColumns[0] = new GroupColumn(indexDodavatel);
        System.out.println("toto je indexDodavatel " + indexDodavatel);
        System.out.println("toto je ComponentBuilder.createRenderer(Dodavatel.NAZOV, session) " + ComponentBuilder.createRenderer(Dodavatel.NAZOV, session));
        
        
        groupColumns[0].setHeaderItem(new GroupColumnItem(indexDodavatel, ComponentBuilder.createRenderer(Dodavatel.NAZOV, session), Dodavatel.NAZOV.getLongCaption()));
        groupColumns[0].setFooterItem(new GroupColumnItem(indexDodavatel, ComponentBuilder.createRenderer(Dodavatel.ICO, session), Dodavatel.ICO.getLongCaption()));
        groupColumns[0].setFooterText(session.translateText("Spolu za"));
        /*
        System.out.println("toto je cursor.getColumnIndex(TovarDruh.KOD.getId()) " + cursor.getColumnIndex(TovarDruh.KOD.getId()));
        int indexTovarDruh = cursor.getColumnIndex(TovarDruh.KOD.getId());
        groupColumns[1] = new GroupColumn(indexTovarDruh);
        groupColumns[1].setHeaderItem(new GroupColumnItem(indexTovarDruh, ComponentBuilder.createRenderer(TovarDruh.KOD, session), TovarDruh.KOD.getLongCaption()));
        groupColumns[1].setFooterItem(new GroupColumnItem(indexTovarDruh, ComponentBuilder.createRenderer(TovarDruh.KOD, session), TovarDruh.KOD.getLongCaption()));
        groupColumns[1].setFooterText(session.translateText(TovarDruh.KOD.getCaption()));
        */
        //NumberGroupSummarizer musi byt number, skusim MySummarizer
        MySummarizer[] sumators = new MySummarizer[] {
        //polozka dodavatel.ico sa nenchadza v zozname
            new MySummarizer(cursor.getColumnIndex(ObjednavkaPolozka.MNOZSTVO.getId()), report.getColumnIndex(ObjednavkaPolozka.MNOZSTVO.getId()), report.getRenderer(ObjednavkaPolozka.MNOZSTVO.getId())),
        };
        report.setGroupRules(new GroupRules(groupColumns, sumators));
        report.setGroupTotalRules(new GroupTotalRules(sumators, session.translateText("Spolu: ")));
		
		
		return new ReportBuilder(report);
	}
	
	   private ViewCursor createCursor(Parameters prmts, SessionObject session) throws Exception {
	    	DBCatalog c = DBCatalog.getInstance();
	        SQLQuery query = new SQLQuery();
	        
	        query.addTable(new SQLJoinCondition(c.TOVAR));
	        query.addTable(new SQLJoinCondition(c.OBCHODNY_PARTNER, c.OBCHODNY_PARTNER.ID, c.TOVAR.DODAVATEL));
	        query.addTable(new SQLJoinCondition(c.TOVAR_DRUH, c.TOVAR_DRUH.ID, c.TOVAR.TOVAR_DRUH));
	        query.addTable(new SQLJoinCondition(c.OBJEDNAVKA_POLOZKA, c.OBJEDNAVKA_POLOZKA.TOVAR, c.TOVAR.ID));
	        query.addTable(new SQLJoinCondition(c.OBJEDNAVKA, c.OBJEDNAVKA.ID, c.OBJEDNAVKA_POLOZKA.OBJEDNAVKA));
	        
	        
	        query.addField(Tovar.KOD.getId(), c.TOVAR.KOD);
	        query.addField(Tovar.NAZOV.getId(), c.TOVAR.NAZOV);
	    	query.addField(ObjednavkaPolozka.MNOZSTVO.getId(), c.OBJEDNAVKA_POLOZKA.MNOZSTVO);
	    	query.addField(Dodavatel.KOD.getId(), c.OBCHODNY_PARTNER.KOD);
	    	query.addField(Dodavatel.NAZOV.getId(), c.OBCHODNY_PARTNER.NAZOV);
	    	query.addField(Dodavatel.ICO.getId(), c.OBCHODNY_PARTNER.ICO);
	    	query.addField(Objednavka.CISLO_OBJEDNAVKY.getId(), c.OBJEDNAVKA.CISLO_OBJEDNAVKY);
	        query.addOrdering(c.OBCHODNY_PARTNER.KOD);
	        query.addOrdering(c.OBJEDNAVKA.CISLO_OBJEDNAVKY);
	        SQLExpression exp1 = SQLExpressionBuilder.get(c.OBJEDNAVKA.STAV_OBJEDNAVKY).equal(StavObjednavky.STORNOVANA.getKey());
	        
	        query.setExpression(exp1);
	        if (prmts.datum != null) {
	        	SQLExpression exp = FrameworkUtilities.createSQLExpression(c.OBJEDNAVKA.DATUM, prmts.datum); 
	            query.setExpression(query.getExpression() != null ? query.getExpression().and(exp) : exp);
	        }
	        
	        if (prmts.zakaznikKod != null) {
	        	SQLExpression exp = FrameworkUtilities.createSQLExpression(c.OBCHODNY_PARTNER.KOD, prmts.zakaznikKod); 
	            query.setExpression(query.getExpression() != null ? query.getExpression().and(exp) : exp);
	        }
	        System.out.println("This is query " + query);
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
	
	@Override
	public String getDescription() {
		return "Report typu MySummator";
	}

	@Override
	public String getName() {
		return "Prehlad tovarov v stornovanych objednavkach";
	}

	@Override
	public Class<PRObjednavkaTovarMySumator> getParameterMediatorClass() {
		
		return PRObjednavkaTovarMySumator.class;
	} 
	
	private static class Parameters {
		
	    public Object datum;
	    public Object zakaznikKod;
	    public FilterTextBuilder headerText = new FilterTextBuilder();
	    
	}
	
	//toto ako?
	private class MySummarizer implements GroupSummarizer {
		
		  private int columnIndex;
		  private int reportColumnIndex;
		  private ComponentRenderer renderer;
		  
		  public MySummarizer(int columnIndex, int reportColumnIndex, ComponentRenderer renderer) {
		      this.columnIndex = columnIndex;
		      this.reportColumnIndex = reportColumnIndex;
		      this.renderer = renderer;
		  }
		  
		  @Override
		  public Object sum(Object previousValue, ViewRow row) {
		      Number value = (Number) row.getValueAt(columnIndex);
		      DoubleWrapper holder = (DoubleWrapper) previousValue;
		      holder.sum += (value == null ? 0 : value.doubleValue());      
		      return previousValue;
		  }
		 
		  @Override
		  public Object getStartValue() {
		  	  return new DoubleWrapper();   	  
		  } 
		  
		  @Override
		  public int getReportColumnIndex() {
		     return reportColumnIndex;
		  } 
		  
		  @Override
		  public String convertValueToString(Object sumValue) {
		  	 if (renderer != null)
		  	    return renderer.convertValueToString(new Double(((DoubleWrapper) sumValue).sum)); 
		  	 return "" + ((DoubleWrapper) sumValue).sum;
		  }  
	}
	
	private class DoubleWrapper {    
	    public double sum; 
	}

}
