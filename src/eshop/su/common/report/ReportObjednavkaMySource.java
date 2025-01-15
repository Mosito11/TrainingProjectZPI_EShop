package eshop.su.common.report;

import java.math.BigDecimal;

import javax.swing.SwingConstants;

import eshop.bo.ciselniky.Objednavka;
import eshop.bo.ciselniky.StavObjednavky;
import eshop.bo.ciselniky.Zakaznik;
import eshop.bo.common.Helper;
import eshop.su.common.report.prmt.PRObjednavkaMySource;
import eshop.su.db.DBCatalog;
import netball.server.print.table.PRBlankTableRow;
import netball.server.print.table.PRGroupTableFooter;
import netball.server.print.table.PRGroupTableHeader;
import netball.server.print.table.PRValueTableRow;
import netframework.FrameworkUtilities;
import netframework.eclipselink.EclipseLinkSession;
import netframework.mediator.ComponentBuilder;
import netframework.mediator.MDReportTreeItemPrmts;
import netframework.mediator.SessionObject;
import netframework.report.BasicReportSource;
import netframework.report.Report;
import netframework.report.ReportBuilder;
import netframework.report.ReportColumnProperties;
import netframework.report.ReportParameters;
import netframework.report.RowBuilder;
import netframework.sql.SQLExpression;
import netframework.sql.SQLExpressionBuilder;
import netframework.sql.SQLJoinCondition;
import netframework.sql.SQLQuery;
import netframework.view.ViewCursor;
import netframework.view.ViewRow;

public class ReportObjednavkaMySource extends BasicReportSource implements Report {
	
	public static final String DATUM = Objednavka.DATUM.getId();
    public static final String ZAKAZNIK_KOD = Zakaznik.KOD.getId();

    private Parameters prmts;
    private SessionObject session;
	private ViewCursor cursor;

	@Override
    public ReportBuilder execute(ReportParameters prmt, SessionObject session) throws Exception{
        
		//zbytocna duplicita?
		//prmts = createParameters(prmt, session);
        this.session = session;
        prmts = createParameters(prmt, session);
        
    	cursor = createCursor();
    	if (!cursor.hasNext()) {
            throw new IllegalArgumentException(session.translateText("NEPODARILO_SA_NACITAT_DATA"));	
        }
        
    	ReportColumnProperties[] cols = new ReportColumnProperties[] {
            	ComponentBuilder.createReportColumn(Objednavka.CISLO_OBJEDNAVKY.getId(), Objednavka.CISLO_OBJEDNAVKY, true, session),
            	ComponentBuilder.createReportColumn(Objednavka.DATUM.getId(), Objednavka.DATUM, true, session),
            	ComponentBuilder.createReportColumn(Objednavka.SUMA.getId(), Objednavka.SUMA, true, session),
            	//ComponentBuilder.createReportColumn(Objednavka.ZAKAZNIK.getId(), Zakaznik.NAZOV, false, session),
        };
    	setColumnProperty(cols);    	
        // nastavi indexy zdroja (cursora) do stlpcov reportu, koli rychlejsiemu spracovaniu
        for (int i = 0; i < cols.length; i++) { 
            cols[i].setSourceIndex(cursor.getColumnIndex(cols[i].getId()));
        }
        setHeaderText(session.translateText(getName()) + prmts.headerText.toString());
        setUserName(session.getUser() != null ? session.getUser().getName() : null);
        setCompanyName((String)((EclipseLinkSession) session).getProperty("ESHOP"));
        return new ReportBuilder(this);         
    }
	
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
              parameters.zakaznikKod = value;
              parameters.headerText.add(Zakaznik.KOD.getLongCaption(), value, session);
           }
        }
        return parameters;
    }
	
	@Override
	public String getName() {
		return "Zoznam dokoncenych objednavok podla zakaznikov";
	}
	
	@Override
	public String getDescription() {
		return "Report typu MySource = extends BasicReportSource implements Report";
	}

	private ViewCursor createCursor() throws Exception {
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
        
        //vyskusat viac podmienok
        //SQLExpression exp2 = SQLExpressionBuilder.get(c.FAKTURA.TYP).equal(TypFaktury.ODBERATELSKA.getKey());
        query.setExpression(exp1); //.and(exp2)?
        if (prmts.datum != null) {
        	SQLExpression exp = FrameworkUtilities.createSQLExpression(c.OBJEDNAVKA.DATUM, prmts.datum); 
            query.setExpression(query.getExpression() != null ? query.getExpression().and(exp) : exp);
        }
        if (prmts.zakaznikKod != null) {
        	SQLExpression exp = FrameworkUtilities.createSQLExpression(c.OBCHODNY_PARTNER.KOD, prmts.zakaznikKod); 
            query.setExpression(query.getExpression() != null ? query.getExpression().and(exp) : exp);
        }
        return ((EclipseLinkSession) session).execute(query); 
    }

	//toto je vlastne trieda, kde si definujem, co zadavam pri reportoch
	@Override
	public Class<PRObjednavkaMySource> getParameterMediatorClass() {
		return PRObjednavkaMySource.class;
	}

	@Override
    public void addRowsToBuilder(RowBuilder rowBuilder) throws Exception {
      	ViewRow row = cursor.next();
       	int indexKod = cursor.getColumnIndex(Zakaznik.KOD.getId());
       	int indexNazov = cursor.getColumnIndex(Zakaznik.NAZOV.getId());
       	int indexSuma = cursor.getColumnIndex(Objednavka.SUMA.getId());
       	int reportSumaIndex = this.getColumnIndex(Objednavka.SUMA.getId());
       	
       	BigDecimal celkovaSuma = Helper.DEFAULT_VALUE;
       	while(row != null) {
       		Object kod = row.getValueAt(indexKod);
       		Object nazov = row.getValueAt(indexNazov);
       		BigDecimal kodSuma = Helper.DEFAULT_VALUE;
       		PRGroupTableHeader header = new PRGroupTableHeader(session.translateText("Kod zakaznika ") + kod + session.translateText(" ,Nazov zakaznika ") + nazov);
       		rowBuilder.writeRow(header);
       		while (row != null && row.getValueAt(indexKod).equals(kod)) {
       	    	 PRValueTableRow gridRow = new PRValueTableRow();
                 for (int k = 0; k < getColumnProperty().length; k++) {
                     if (getColumnProperty()[k].getSourceIndex() != -1) {
                         Object value = row.getValueAt(getColumnProperty()[k].getSourceIndex());
                         gridRow.add(convertValueToString(value, k)); 
                     }
                  }
                  rowBuilder.writeRow(gridRow);
                  kodSuma = kodSuma.add((BigDecimal) row.getValueAt(indexSuma));
        		  row = cursor.hasNext() ? cursor.next() : null;
        	}
            celkovaSuma = celkovaSuma.add(kodSuma);
           	PRGroupTableFooter footer = new PRGroupTableFooter(session.translateText("Kod zakaznika ") + kod);
           	footer.setTextLocation(SwingConstants.RIGHT);
           	footer.addItem(convertValueToString(kodSuma, reportSumaIndex), reportSumaIndex);
           	rowBuilder.writeRow(footer);
        }
        rowBuilder.writeRow(new PRBlankTableRow());
        // celkova suma
        PRGroupTableFooter footer = new PRGroupTableFooter(session.translateText("Objednavky spolu "));
       	footer.setTextLocation(SwingConstants.RIGHT);
       	footer.addItem(convertValueToString(celkovaSuma, reportSumaIndex), reportSumaIndex);
       	rowBuilder.writeRow(footer);
    }
	
	@Override
    public void close() {
      	try {
      	  if (cursor != null)
      		  cursor.close();
      	  }catch(Exception e) {
       	  e.printStackTrace(); 	
      	  }  
    }

	private class Parameters {
	     public Object datum;
	     public Object zakaznikKod;
	     public FilterTextBuilder headerText = new FilterTextBuilder();
	     
	 } 
	
}
