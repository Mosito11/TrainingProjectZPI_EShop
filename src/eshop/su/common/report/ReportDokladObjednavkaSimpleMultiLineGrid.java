package eshop.su.common.report;

import java.awt.Font;
import java.awt.Insets;

import javax.swing.SwingConstants;

import eshop.bo.ciselniky.Adresa;
import eshop.bo.ciselniky.ObchodnyPartner;
import eshop.bo.ciselniky.Objednavka;
import eshop.bo.ciselniky.ObjednavkaPolozka;
import eshop.bo.ciselniky.Tovar;
import eshop.bo.ciselniky.Zakaznik;
import eshop.su.db.DBCatalog;
import netball.server.print.PRBorderPanel;
import netball.server.print.PRComponent;
import netball.server.print.PREmptyComponent;
import netball.server.print.PRFlowPanel;
import netball.server.print.PRFont;
import netball.server.print.PRLabel;
import netball.server.print.PRMultiLineLabel;
import netball.server.print.PRPage;
import netball.server.print.PRReport;
import netball.server.print.PRSignature;
import netball.server.print.PRSimpleMultiLineGrid;
import netball.server.utilities.TextCutter;
import netframework.bo.attributes.Attribute;
import netframework.eclipselink.EclipseLinkSession;
import netframework.mediator.ComponentBuilder;
import netframework.mediator.SessionObject;
import netframework.sql.SQLExpressionBuilder;
import netframework.sql.SQLJoinCondition;
import netframework.sql.SQLQuery;
import netframework.view.ViewCursor;
import netframework.view.ViewRow;

public class ReportDokladObjednavkaSimpleMultiLineGrid extends ReportDokladSimpleMultiLineGrid {

	@Override
    public PRPage[] execute(SessionObject session, Object dokladId) throws Exception {        
        return super.execute(session, dokladId);
    }
	
	@Override
    protected ViewCursor readItems() throws Exception {
    	DBCatalog c = DBCatalog.getInstance();
        SQLQuery query = new SQLQuery();
        query.addTable(new SQLJoinCondition(c.OBJEDNAVKA_POLOZKA));
        query.addTable(new SQLJoinCondition(c.TOVAR, SQLJoinCondition.LEFT_OUTER_JOIN, c.TOVAR.ID, c.OBJEDNAVKA_POLOZKA.TOVAR));
        query.addField(Tovar.NAZOV.getId(), c.TOVAR.NAZOV);
        query.addField(ObjednavkaPolozka.JEDNOTKOVA_CENA.getId(), c.OBJEDNAVKA_POLOZKA.JEDNOTKOVA_CENA);
        query.addField(ObjednavkaPolozka.MNOZSTVO.getId(), c.OBJEDNAVKA_POLOZKA.MNOZSTVO);
        query.addField(ObjednavkaPolozka.SUMA.getId(), c.OBJEDNAVKA_POLOZKA.SUMA);
        query.addOrdering(c.TOVAR.NAZOV);
        query.setExpression(SQLExpressionBuilder.get(c.OBJEDNAVKA_POLOZKA.OBJEDNAVKA).equal(this.dokladId));
        return ((EclipseLinkSession) session).execute(query);
    }
	
	@Override
    protected ViewRow readHeader() throws Exception {
		DBCatalog c = DBCatalog.getInstance();
        SQLQuery query = new SQLQuery();
        query.addTable(new SQLJoinCondition(c.OBJEDNAVKA));   
        query.addTable(new SQLJoinCondition(c.OBCHODNY_PARTNER, SQLJoinCondition.LEFT_OUTER_JOIN, c.OBCHODNY_PARTNER.ID, c.OBJEDNAVKA.ZAKAZNIK));
        //takto?
        query.addTable(new SQLJoinCondition(c.OBCHODNY_PARTNER_ADRESA, SQLJoinCondition.LEFT_OUTER_JOIN, c.OBCHODNY_PARTNER.ID, c.OBCHODNY_PARTNER_ADRESA.OBCHODNY_PARTNER));
        //query.addTable(new SQLJoinCondition(c.ADRESA, SQLJoinCondition.LEFT_OUTER_JOIN, c.ADRESA.ID, c.OBCHODNY_PARTNER_ADRESA.ADRESA));
        //query.addTable(new SQLJoinCondition(c.OBCHODNY_PARTNER_ADRESA));
        query.addTable(new SQLJoinCondition(c.ADRESA, c.OBCHODNY_PARTNER_ADRESA.ADRESA, c.ADRESA.ID));        
        
        query.addField(Objednavka.CISLO_OBJEDNAVKY.getId(), c.OBJEDNAVKA.CISLO_OBJEDNAVKY);
        query.addField(Objednavka.DATUM.getId(), c.OBJEDNAVKA.DATUM);
        query.addField(Objednavka.STAV_OBJEDNAVKY.getId(), c.OBJEDNAVKA.STAV_OBJEDNAVKY);
        query.addField(Objednavka.SUMA.getId(), c.OBJEDNAVKA.SUMA);
        query.addField(Zakaznik.NAZOV.getId(), c.OBCHODNY_PARTNER.NAZOV);
        query.addField(Zakaznik.KOD.getId(), c.OBCHODNY_PARTNER.KOD);
        
        query.addField(Adresa.ULICA.getId(), c.ADRESA.ULICA);
        query.addField(Adresa.CISLO.getId(), c.ADRESA.CISLO);
        query.addField(Adresa.MESTO.getId(), c.ADRESA.MESTO);
        query.addField(Adresa.PSC.getId(), c.ADRESA.PSC);
                        
        query.setExpression(SQLExpressionBuilder.get(c.OBJEDNAVKA.ID).equal(this.dokladId));
        ViewCursor cursor = ((EclipseLinkSession) session).execute(query);
        if (!cursor.hasNext())
        	throw new IllegalArgumentException("NEPODARILO_SA_NACITAT_DOKLAD_S_ID, " + dokladId);
        return cursor.next();   
    }
	
	private String convertValueToString(ViewRow row, Attribute prop) {
    	return ComponentBuilder.convertValueToString(prop, row.getValueAt(row.getColumnIndex(prop.getId())), session);
    }
	
	
	private PRComponent createTitle(ViewRow headerRow) {
        String cislo = convertValueToString(headerRow, Objednavka.CISLO_OBJEDNAVKY);
        PRLabel title = new PRLabel();       
        title.setFont(new PRFont("Dialog", java.awt.Font.BOLD, 14));
        title.setText(session.translateText("Objednavka ") + cislo);
        title.setWidth(500);
        title.setHeight(18);
    	return title;
    }

	@Override
    protected PRReport createFirstPage(ViewRow headerRow) throws Exception {
    	PRFlowPanel titleForm = new PRFlowPanel(javax.swing.SwingConstants.VERTICAL);       
        titleForm.add(new PREmptyComponent(0, 80));
        titleForm.add(createTitle(headerRow));
        titleForm.addGap(20);

        PRMultiLineLabel zakaznikLabel = new PRMultiLineLabel();
        zakaznikLabel.setText(new String[] {convertValueToString(headerRow, ObchodnyPartner.NAZOV),
        		                            convertValueToString(headerRow, ObchodnyPartner.KOD), 
        		                            convertValueToString(headerRow, Adresa.ULICA), 
        		                            convertValueToString(headerRow, Adresa.CISLO), 
        		                            convertValueToString(headerRow, Adresa.MESTO), 
        		                            convertValueToString(headerRow, Adresa.PSC) 
        		                            });
        zakaznikLabel.setRowHeight(10);
        zakaznikLabel.setWidth(200);
        
        PRBorderPanel zakaznikPanel = new PRBorderPanel(zakaznikLabel);
        zakaznikPanel.setPaintBorder(true);
        zakaznikPanel.setTitle(session.translateText("Zakaznik "));
        zakaznikPanel.setInsets(new Insets(20, 10, 10, 10));
        titleForm.add(zakaznikPanel);
        
        PRReport page = new PRReport(); 
        page.setVerticalGap(10);
        page.setHeader(titleForm);
        return page; 
    }

	@Override
    protected PRMultiLineLabel createDolnyPopisPanel() throws Exception {
         PRMultiLineLabel popisComp = new PRMultiLineLabel();
         popisComp.setFont(new PRFont("Dialog", java.awt.Font.PLAIN, 9));
         popisComp.setRowHeight(11);
         popisComp.setWidth(530);       
         //dolny popis, vytlaci sa za poslednou polozkou
         popisComp.setText(session.translateText(""));
   	     return popisComp;
    }

	@Override
    protected PRComponent createFooterPanel(ViewRow headerRow) {
        PRSignature signature = new PRSignature(null, session.translateText("PECIATKA_A_PODPIS")); 
        signature.setLabelFont(new PRFont("Dialog", java.awt.Font.PLAIN, 8));
        signature.setWidth(200);
        signature.setInsets(new Insets(10, 0, 0, 0));
        return signature;	
    }

	@Override
    protected PRReport createOtherPage(ViewRow headerRow) throws Exception {    
       PRFlowPanel titleForm = new PRFlowPanel(javax.swing.SwingConstants.VERTICAL);       
       titleForm.add(new PREmptyComponent(0, 80));
       titleForm.add(createTitle(headerRow));
       titleForm.addGap(20);
       
       PRReport page = new PRReport(); 
       page.setVerticalGap(10);
       page.setHeader(titleForm);
       return page; 
    }

	@Override
    protected GridBuilder createGridBuilder() {
    	return new MaterialGridBuilder();
   	}
	
	private class MaterialGridBuilder implements ReportDokladSimpleMultiLineGrid.GridBuilder {

	    private int widthNazov = 300;
	    private PRSimpleMultiLineGrid.Header header;
	    private int headerHeight;

	    public MaterialGridBuilder() {
	    	header = new PRSimpleMultiLineGrid.Header();
	    	header.setFont(new PRFont("Dialog", Font.PLAIN, 8));
	    	header.setRowHeight(12);
	    	header.addColumn(new PRSimpleMultiLineGrid.ColumnHeader(session.translateText("Por. c."), 20, SwingConstants.RIGHT));
	    	header.addColumn(new PRSimpleMultiLineGrid.ColumnHeader(session.translateText(Tovar.NAZOV.getColumnName()), widthNazov, SwingConstants.LEFT));
	    	header.addColumn(new PRSimpleMultiLineGrid.ColumnHeader(session.translateText(ObjednavkaPolozka.JEDNOTKOVA_CENA.getColumnName()), 55, SwingConstants.RIGHT));
	        header.addColumn(new PRSimpleMultiLineGrid.ColumnHeader(session.translateText(ObjednavkaPolozka.MNOZSTVO.getColumnName()), 55, SwingConstants.RIGHT));
	        header.addColumn(new PRSimpleMultiLineGrid.ColumnHeader(session.translateText(ObjednavkaPolozka.SUMA.getColumnName()), 30, SwingConstants.LEFT));
	        headerHeight = header.getSize().height;
	    }

	    public PRSimpleMultiLineGrid.Row createRow(ViewRow row, int pc) throws Exception {
	        PRSimpleMultiLineGrid.Row gridRow = new PRSimpleMultiLineGrid.Row();
	        gridRow.add("" + pc + ".");
	        gridRow.add(TextCutter.parse(convertValueToString(row, Tovar.NAZOV), widthNazov - 20, header.getFont()));
	        gridRow.add(convertValueToString(row, ObjednavkaPolozka.JEDNOTKOVA_CENA));
	        gridRow.add(convertValueToString(row, ObjednavkaPolozka.MNOZSTVO));
	        gridRow.add(convertValueToString(row, ObjednavkaPolozka.SUMA));
	        return gridRow;
	    }

	    public PRSimpleMultiLineGrid.Row createSumarnyRiadok(ViewRow headerRow) throws Exception {
	        PRSimpleMultiLineGrid.Row gridRow = new PRSimpleMultiLineGrid.Row();
	        gridRow.add("");
	        gridRow.add("Celkova " + Objednavka.SUMA.getCaption() + " objednavky v EUR ");
	        gridRow.add("");
	        gridRow.add("");
	        //gridRow.add("");
	        gridRow.add(convertValueToString(headerRow, Objednavka.SUMA));
	        return gridRow;
	    }

	    public PRSimpleMultiLineGrid createGrid() {
	    	PRSimpleMultiLineGrid grid = new PRSimpleMultiLineGrid(header);
	    	grid.setFont(header.getFont());
	    	grid.setRowHeight(header.getRowHeight());
	    	return grid;
	  	}

	    public PRSimpleMultiLineGrid.Header getHeader() {
	    	return header;
	    }

	    public int getHeaderHeight() {
	    	return headerHeight;
	   	}
	}
}
