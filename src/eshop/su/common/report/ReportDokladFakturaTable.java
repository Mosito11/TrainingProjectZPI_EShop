package eshop.su.common.report;

import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingConstants;

import eshop.bo.ciselniky.Adresa;
import eshop.bo.ciselniky.Faktura;
import eshop.bo.ciselniky.ObchodnyPartner;
import eshop.bo.ciselniky.Objednavka;
import eshop.bo.ciselniky.ObjednavkaPolozka;
import eshop.bo.ciselniky.Tovar;
import eshop.bo.ciselniky.Zakaznik;
import eshop.su.db.DBCatalog;
import netball.server.print.PRBorderPanel;
import netball.server.print.PRComponent;
import netball.server.print.PRDecoratePage;
import netball.server.print.PREmptyComponent;
import netball.server.print.PRFlowCaptionComp;
import netball.server.print.PRFlowPanel;
import netball.server.print.PRFont;
import netball.server.print.PRLabel;
import netball.server.print.PRMultiLineLabel;
import netball.server.print.PRPage;
import netball.server.print.PRPageFormat;
import netball.server.print.PRReport;
import netball.server.print.PRTable;
import netball.server.print.table.PRGroupTableFooter;
import netball.server.print.table.PRTableCell;
import netball.server.print.table.PRTableHeader;
import netball.server.print.table.PRTableHeaderColumn;
import netball.server.print.table.PRValueTableRow;
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

public class ReportDokladFakturaTable {
	
	private SessionObject session;
    private Object dokladId;
    
    public PRPage[] execute(SessionObject session, Object dokladId) throws Exception {        
        this.session = session;
        this.dokladId = dokladId;        
        ViewRow headerRow = readHeader().next();
        ViewCursor polozky = readItems();        
        PRPageFormat pageFormat = getPageFormat();
        
        PRReport report = createPage(headerRow);        
        PRTable grid = (PRTable) report.getBody();
        //computeBodyHeight tu ma byt, preklep
        int pocetRiadkovNaStranu = report.camputeBodyHeight(pageFormat.createPageFormat());
        pocetRiadkovNaStranu -= grid.getHeader().getSize().getHeight();
        pocetRiadkovNaStranu = pocetRiadkovNaStranu / grid.getRowHeight();
        pocetRiadkovNaStranu --;  // koli suma na konci ?
        List<PRPage> pages = new ArrayList<PRPage>(1);
        pages.add(report);      
        int pocetRiadkov = 0;
        while(polozky.hasNext()) {
           pocetRiadkov ++;
           if (pocetRiadkov > pocetRiadkovNaStranu) {
              pocetRiadkov = 0;
              report = createPage(headerRow);
              grid = (PRTable) report.getBody();
              pages.add(report);        
           }
           grid.addRow(createGridRow(polozky.next(), pocetRiadkov));
        } 
        // celkova suma
        int indexSuma = 4;
        PRGroupTableFooter groupGridFooter = new PRGroupTableFooter(session.translateText(Objednavka.SUMA.getCaption()), javax.swing.SwingConstants.RIGHT);
        //Objednavka.SUMA tu ma byt?
        PRTableCell cell = new PRTableCell(convertValueToString(headerRow, Objednavka.SUMA));
        cell.setFont(grid.getFont());
        groupGridFooter.addItem(cell, indexSuma);        
        grid.addRow(groupGridFooter);
        return addPageNumbers(pages);
    }
    
    private PRPage[] addPageNumbers(List<PRPage> pages) {     
        PRDecoratePage reports[] = new PRDecoratePage[pages.size()];
        for (int i = 0; i < pages.size(); i++) {
           reports[i] = new PRDecoratePage((PRPage) pages.get(i));
           reports[i].setRightTopCorner(new PRLabel(session.translateText("Strana") + (i + 1) + " / " + pages.size() + "  "));
        }       
        return reports;        
    }
    
    public PRPageFormat getPageFormat() {
        return new PRPageFormat();
    }    
    
    //preco je niekedt ViewCursor a niekedy ViewRow?
    private ViewCursor readHeader() throws Exception {
    	DBCatalog c = DBCatalog.getInstance();
        SQLQuery query = new SQLQuery();
        query.addTable(new SQLJoinCondition(c.FAKTURA)); 
        
        query.addTable(new SQLJoinCondition(c.OBJEDNAVKA, SQLJoinCondition.LEFT_OUTER_JOIN, c.FAKTURA.OBJEDNAVKA, c.OBJEDNAVKA.ID));
        query.addTable(new SQLJoinCondition(c.UZIVATEL, SQLJoinCondition.LEFT_OUTER_JOIN, c.OBJEDNAVKA.VYSTAVIL, c.UZIVATEL.ID));
        query.addTable(new SQLJoinCondition(c.OBCHODNY_PARTNER, SQLJoinCondition.LEFT_OUTER_JOIN, c.OBCHODNY_PARTNER.ID, c.OBJEDNAVKA.ZAKAZNIK));
        query.addTable(new SQLJoinCondition(c.OBCHODNY_PARTNER_ADRESA, SQLJoinCondition.LEFT_OUTER_JOIN, c.OBCHODNY_PARTNER.ID, c.OBCHODNY_PARTNER_ADRESA.OBCHODNY_PARTNER));
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
        return cursor;   
    }
    
    private ViewCursor readItems() throws Exception {
    	DBCatalog c = DBCatalog.getInstance();
    	SQLQuery query = new SQLQuery();
    	query.addTable(new SQLJoinCondition(c.OBJEDNAVKA));
        
    	query.addTable(new SQLJoinCondition(c.FAKTURA, SQLJoinCondition.JOIN, c.FAKTURA.OBJEDNAVKA, c.OBJEDNAVKA.ID));
        query.addTable(new SQLJoinCondition(c.OBJEDNAVKA_POLOZKA, SQLJoinCondition.JOIN, c.OBJEDNAVKA.ID, c.OBJEDNAVKA_POLOZKA.OBJEDNAVKA));
        query.addTable(new SQLJoinCondition(c.TOVAR, SQLJoinCondition.LEFT_OUTER_JOIN, c.TOVAR.ID, c.OBJEDNAVKA_POLOZKA.TOVAR));
        
        query.addField(Tovar.NAZOV.getId(), c.TOVAR.NAZOV);
        query.addField(ObjednavkaPolozka.JEDNOTKOVA_CENA.getId(), c.OBJEDNAVKA_POLOZKA.JEDNOTKOVA_CENA);
        query.addField(ObjednavkaPolozka.MNOZSTVO.getId(), c.OBJEDNAVKA_POLOZKA.MNOZSTVO);
        query.addField(ObjednavkaPolozka.SUMA.getId(), c.OBJEDNAVKA_POLOZKA.SUMA);
        query.addOrdering(c.TOVAR.NAZOV);
        query.setExpression(SQLExpressionBuilder.get(c.FAKTURA.ID).equal(this.dokladId));
        return ((EclipseLinkSession) session).execute(query);
    }
    
    private PRTableHeader createGridHeader() {
    	PRTableHeader gridHeader = new PRTableHeader();       
        gridHeader.setFont(new PRFont("Dialog", java.awt.Font.PLAIN, 8));
        gridHeader.setRowHeight(14);
        gridHeader.add(new PRTableHeaderColumn(session.translateText("Por.C."), 20, SwingConstants.LEFT));
        gridHeader.add(new PRTableHeaderColumn(session.translateText(Tovar.NAZOV.getColumnName()), 300, SwingConstants.LEFT));
   		gridHeader.add(new PRTableHeaderColumn(session.translateText(ObjednavkaPolozka.JEDNOTKOVA_CENA.getColumnName()), 55, SwingConstants.RIGHT));
		gridHeader.add(new PRTableHeaderColumn(session.translateText(ObjednavkaPolozka.MNOZSTVO.getColumnName()), 30, SwingConstants.LEFT));
		gridHeader.add(new PRTableHeaderColumn(session.translateText(ObjednavkaPolozka.SUMA.getColumnName()), 55, SwingConstants.RIGHT));
		return gridHeader;
    }
    
    private PRValueTableRow createGridRow(ViewRow row, int pc) {
    	PRValueTableRow gridRow = new PRValueTableRow();
        gridRow.add("" + pc + ".");
        gridRow.add(convertValueToString(row, Tovar.NAZOV));
        gridRow.add(convertValueToString(row, ObjednavkaPolozka.JEDNOTKOVA_CENA));
        gridRow.add(convertValueToString(row, ObjednavkaPolozka.MNOZSTVO));
        gridRow.add(convertValueToString(row, ObjednavkaPolozka.SUMA));
        return gridRow;
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
    
    private PRReport createPage(ViewRow headerRow) throws Exception {
        PRFlowPanel titleForm = new PRFlowPanel(javax.swing.SwingConstants.VERTICAL);       
        titleForm.add(new PREmptyComponent(0, 10));
        titleForm.add(createTitle(headerRow));
        titleForm.addGap(20);

        PRFlowCaptionComp zakaznikForm = new PRFlowCaptionComp(SwingConstants.VERTICAL);
        zakaznikForm.addItem(session.translateText(ObchodnyPartner.NAZOV.getCaption()), convertValueToString(headerRow, ObchodnyPartner.NAZOV), 300);
        zakaznikForm.addItem(session.translateText(ObchodnyPartner.KOD.getCaption()), convertValueToString(headerRow, ObchodnyPartner.KOD), 300);
        zakaznikForm.addItem(session.translateText(ObchodnyPartner.ADRESY.getCaption()), convertValueToString(headerRow, Adresa.ULICA) + " " + convertValueToString(headerRow, Adresa.CISLO) + ", " + convertValueToString(headerRow, Adresa.MESTO) + " " + convertValueToString(headerRow, Adresa.PSC), 300);
           
        PRBorderPanel zakaznikPanel = new PRBorderPanel(zakaznikForm);
        zakaznikPanel.setPaintBorder(true);
        zakaznikPanel.setTitle(session.translateText("Zakaznik"));
        zakaznikPanel.setInsets(new Insets(20, 10, 10, 10));
        titleForm.add(zakaznikPanel);
        
        PRFlowCaptionComp fakturaForm = new PRFlowCaptionComp(SwingConstants.VERTICAL);
        fakturaForm.addItem(session.translateText(Objednavka.CISLO_OBJEDNAVKY.getCaption()), convertValueToString(headerRow, Objednavka.CISLO_OBJEDNAVKY), 361);
        fakturaForm.addItem(session.translateText(Faktura.DATUM_DODANIA.getCaption()), convertValueToString(headerRow, Faktura.DATUM_DODANIA), 361);
        fakturaForm.addItem(session.translateText(Faktura.DATUM_VYSTAVENIA.getCaption()), convertValueToString(headerRow, Faktura.DATUM_VYSTAVENIA), 361);
        fakturaForm.addItem(session.translateText(Faktura.DATUM_SPLATNOSTI.getCaption()), convertValueToString(headerRow, Faktura.DATUM_SPLATNOSTI), 361);
           
        PRBorderPanel fakturaPanel = new PRBorderPanel(fakturaForm);
        zakaznikPanel.setPaintBorder(true);
        zakaznikPanel.setTitle(session.translateText("Udaje k fakture"));
        zakaznikPanel.setInsets(new Insets(20, 10, 10, 10));
        titleForm.add(fakturaPanel);
        
        PRReport page = new PRReport(); 
        page.setVerticalGap(10);
        page.setHeader(titleForm);
        page.setFooter(createFooter(headerRow));
        
        PRTable table = new PRTable(this.createGridHeader());
        table.setRowHeight(table.getHeader().getRowHeight());
        table.setFont(table.getHeader().getFont());
        table.setColumnMargin(1);
        table.setPaintCellBorders(true);
        page.setBody(table);
        return page; 
    }
    
    private PRComponent createFooter(ViewRow headerRow) {
    	String meno = convertValueToString(headerRow, Uzivatel.MENO);
    	
 	   PRMultiLineLabel label = new PRMultiLineLabel();
        label.setRowCount(3);
        label.setInsets(new Insets(2,2,2,2));
        label.setWidth(520);
        label.setPaintBorder(true);
        label.setText(session.translateText("Vystavil " + meno));
        return label;              
     }


}
