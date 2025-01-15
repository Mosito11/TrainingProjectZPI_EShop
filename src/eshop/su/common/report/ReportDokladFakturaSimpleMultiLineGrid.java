package eshop.su.common.report;

import java.awt.Font;
import java.awt.Insets;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.swing.SwingConstants;

import eshop.bo.ciselniky.Adresa;
import eshop.bo.ciselniky.Faktura;
import eshop.bo.ciselniky.ObchodnyPartner;
import eshop.bo.ciselniky.Objednavka;
import eshop.bo.ciselniky.ObjednavkaPolozka;
import eshop.bo.ciselniky.Tovar;
import eshop.bo.ciselniky.Zakaznik;
import eshop.su.common.report.ReportDokladSimpleMultiLineGrid.GridBuilder;
import eshop.su.common.view.ViewObjednavka;
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
import zelpo.eclipselink.autorizacia.Uzivatel;

public class ReportDokladFakturaSimpleMultiLineGrid extends ReportDokladSimpleMultiLineGrid {

	@Override
    public PRPage[] execute(SessionObject session, Object dokladId) throws Exception {        
        return super.execute(session, dokladId);
    }
	
	@Override
    protected ViewRow readHeader() throws Exception {
		DBCatalog c = DBCatalog.getInstance();
        SQLQuery query = new SQLQuery();
        query.addTable(new SQLJoinCondition(c.FAKTURA));   
        
        query.addTable(new SQLJoinCondition(c.OBJEDNAVKA, SQLJoinCondition.LEFT_OUTER_JOIN, c.FAKTURA.OBJEDNAVKA, c.OBJEDNAVKA.ID));
        query.addTable(new SQLJoinCondition(c.OBCHODNY_PARTNER, SQLJoinCondition.LEFT_OUTER_JOIN, c.OBCHODNY_PARTNER.ID, c.OBJEDNAVKA.ZAKAZNIK));
        query.addTable(new SQLJoinCondition(c.UZIVATEL, SQLJoinCondition.LEFT_OUTER_JOIN, c.OBJEDNAVKA.VYSTAVIL, c.UZIVATEL.ID));
        
        //takto?
        query.addTable(new SQLJoinCondition(c.OBCHODNY_PARTNER_ADRESA, SQLJoinCondition.LEFT_OUTER_JOIN, c.OBCHODNY_PARTNER.ID, c.OBCHODNY_PARTNER_ADRESA.OBCHODNY_PARTNER));
        //query.addTable(new SQLJoinCondition(c.ADRESA, SQLJoinCondition.LEFT_OUTER_JOIN, c.ADRESA.ID, c.OBCHODNY_PARTNER_ADRESA.ADRESA));
        //query.addTable(new SQLJoinCondition(c.OBCHODNY_PARTNER_ADRESA));
        query.addTable(new SQLJoinCondition(c.ADRESA, c.OBCHODNY_PARTNER_ADRESA.ADRESA, c.ADRESA.ID));        
        
        query.addField(Faktura.CISLO_FAKTURY.getId(), c.FAKTURA.CISLO_FAKTURY);
        query.addField(Faktura.DATUM_DODANIA.getId(), c.FAKTURA.DATUM_DODANIA);
        query.addField(Faktura.DATUM_VYSTAVENIA.getId(), c.FAKTURA.DATUM_VYSTAVENIA);
        query.addField(Faktura.DATUM_SPLATNOSTI.getId(), c.FAKTURA.DATUM_SPLATNOSTI);
                
        query.addField(Objednavka.CISLO_OBJEDNAVKY.getId(), c.OBJEDNAVKA.CISLO_OBJEDNAVKY);
        query.addField(Objednavka.SUMA.getId(), c.OBJEDNAVKA.SUMA);
        query.addField(Zakaznik.NAZOV.getId(), c.OBCHODNY_PARTNER.NAZOV);
        query.addField(Zakaznik.KOD.getId(), c.OBCHODNY_PARTNER.KOD);
              
        query.addField(Adresa.ULICA.getId(), c.ADRESA.ULICA);
        query.addField(Adresa.CISLO.getId(), c.ADRESA.CISLO);
        query.addField(Adresa.MESTO.getId(), c.ADRESA.MESTO);
        query.addField(Adresa.PSC.getId(), c.ADRESA.PSC);
        
        query.addField(Uzivatel.MENO.getId(), c.UZIVATEL.MENO);
                
        query.setExpression(SQLExpressionBuilder.get(c.FAKTURA.ID).equal(this.dokladId));
        ViewCursor cursor = ((EclipseLinkSession) session).execute(query);
        
        if (!cursor.hasNext())
        	throw new IllegalArgumentException("NEPODARILO_SA_NACITAT_DOKLAD_S_ID, " + dokladId);
        return cursor.next();   
    }

	@Override
    protected ViewCursor readItems() throws Exception {
    	DBCatalog c = DBCatalog.getInstance();
        SQLQuery query = new SQLQuery();
        
        query.addTable(new SQLJoinCondition(c.OBJEDNAVKA));
        
        query.addTable(new SQLJoinCondition(c.FAKTURA, SQLJoinCondition.JOIN, c.FAKTURA.OBJEDNAVKA, c.OBJEDNAVKA.ID));
        query.addTable(new SQLJoinCondition(c.OBJEDNAVKA_POLOZKA, SQLJoinCondition.JOIN, c.OBJEDNAVKA.ID, c.OBJEDNAVKA_POLOZKA.OBJEDNAVKA));
        query.addTable(new SQLJoinCondition(c.TOVAR, SQLJoinCondition.LEFT_OUTER_JOIN, c.OBJEDNAVKA_POLOZKA.TOVAR, c.TOVAR.ID));
        
        query.addField(ObjednavkaPolozka.ID.getId(), c.OBJEDNAVKA_POLOZKA.ID);
        query.addField(Tovar.ID.getId(), c.TOVAR.ID);
        query.addField(Tovar.NAZOV.getId(), c.TOVAR.NAZOV);
        query.addField(ObjednavkaPolozka.MNOZSTVO.getId(), c.OBJEDNAVKA_POLOZKA.MNOZSTVO);
        query.addField(ObjednavkaPolozka.JEDNOTKOVA_CENA.getId(), c.OBJEDNAVKA_POLOZKA.JEDNOTKOVA_CENA);
        query.addField(ObjednavkaPolozka.SUMA.getId(), c.OBJEDNAVKA_POLOZKA.SUMA);
        query.addOrdering(c.OBJEDNAVKA_POLOZKA.TOVAR);
                      
        query.setExpression(SQLExpressionBuilder.get(c.FAKTURA.ID).equal(dokladId));
        
        return ((EclipseLinkSession) session).execute(query);
    }
	
	private String convertValueToString(ViewRow row, Attribute prop) {
		return ComponentBuilder.convertValueToString(prop, row.getValueAt(row.getColumnIndex(prop.getId())), session);
    }
	
	private PRComponent createTitle(ViewRow headerRow) {
        String cislo = convertValueToString(headerRow, Faktura.CISLO_FAKTURY);
        PRLabel title = new PRLabel();       
        title.setFont(new PRFont("Dialog", java.awt.Font.BOLD, 14));
        title.setText(session.translateText("Faktura ") + cislo);
        title.setWidth(500);
        title.setHeight(18);
    	return title;
    }
	
	@Override
    protected PRReport createFirstPage(ViewRow headerRow) throws Exception {
    	PRFlowPanel titleForm = new PRFlowPanel(javax.swing.SwingConstants.VERTICAL);       
        titleForm.add(new PREmptyComponent(0, 30));
        titleForm.add(createTitle(headerRow));
        titleForm.addGap(20);

        PRFlowPanel labelsForm = new PRFlowPanel(javax.swing.SwingConstants.HORIZONTAL);
        labelsForm.add(new PREmptyComponent(0, 30));
               
        PRMultiLineLabel zakaznikLabel = new PRMultiLineLabel();
        zakaznikLabel.setText(new String[] {convertValueToString(headerRow, ObchodnyPartner.NAZOV),
        		                            convertValueToString(headerRow, ObchodnyPartner.KOD), 
        		                            convertValueToString(headerRow, Adresa.ULICA), 
        		                            convertValueToString(headerRow, Adresa.CISLO), 
        		                            convertValueToString(headerRow, Adresa.MESTO), 
        		                            convertValueToString(headerRow, Adresa.PSC) 
        		                            });
        zakaznikLabel.setRowHeight(10);
        zakaznikLabel.setWidth(210);
        
        PRBorderPanel zakaznikPanel = new PRBorderPanel(zakaznikLabel);
        zakaznikPanel.setPaintBorder(true);
        zakaznikPanel.setTitle(session.translateText("Zakaznik "));
        zakaznikPanel.setInsets(new Insets(20, 10, 10, 10));
        labelsForm.add(zakaznikPanel);
        
        PRMultiLineLabel fakturaLabel = new PRMultiLineLabel();
        fakturaLabel.setText(new String[] { Objednavka.CISLO_OBJEDNAVKY.getCaption() + ": " + convertValueToString(headerRow, Objednavka.CISLO_OBJEDNAVKY),
        									Faktura.DATUM_DODANIA.getCaption() + ": " + convertValueToString(headerRow, Faktura.DATUM_DODANIA), 
        									Faktura.DATUM_VYSTAVENIA.getCaption() + ": " + convertValueToString(headerRow, Faktura.DATUM_VYSTAVENIA), 
        									Faktura.DATUM_SPLATNOSTI.getCaption() + ": " + convertValueToString(headerRow, Faktura.DATUM_SPLATNOSTI), 
        		                           });
        fakturaLabel.setRowHeight(10);
        fakturaLabel.setWidth(210);
        
        PRBorderPanel fakturaPanel = new PRBorderPanel(fakturaLabel);
        fakturaPanel.setPaintBorder(true);
        fakturaPanel.setTitle(session.translateText("Udaje k fakture "));
        fakturaPanel.setInsets(new Insets(20, 10, 30, 10));
        labelsForm.add(fakturaPanel);
        
        titleForm.add(labelsForm);
        
        PRReport page = new PRReport(); 
        page.setVerticalGap(10);
        page.setHeader(titleForm);
        return page; 
    }

	@Override
    protected GridBuilder createGridBuilder() {
    	return new MaterialGridBuilder();
   	}

	@Override
    protected PRMultiLineLabel createDolnyPopisPanel() throws Exception {
         PRMultiLineLabel popisComp = new PRMultiLineLabel();
         popisComp.setFont(new PRFont("Dialog", java.awt.Font.PLAIN, 7));
         popisComp.setRowHeight(11);
         popisComp.setWidth(530);       
         //dolny popis, vytlaci sa za poslednou polozkou
         popisComp.setText(session.translateText("Nie sme platcovia DPH"));
   	     return popisComp;
    }

	@Override
    protected PRComponent createFooterPanel(ViewRow headerRow) {
		String meno = convertValueToString(headerRow, Uzivatel.MENO);
        PRSignature signature = new PRSignature(meno, session.translateText("PECIATKA_A_PODPIS")); 
        signature.setLabelFont(new PRFont("Dialog", java.awt.Font.PLAIN, 8));
        signature.setWidth(200);
        signature.setInsets(new Insets(300, 0, 0, 0));
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
