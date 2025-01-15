package eshop.su.common.report;


import java.awt.Font;
import java.awt.Insets;

import javax.swing.SwingConstants;

import eshop.bo.ciselniky.Kontakt;
import eshop.bo.ciselniky.ObchodnyPartner;
import eshop.bo.ciselniky.Objednavka;
import eshop.bo.ciselniky.ObjednavkaPolozka;
import eshop.bo.ciselniky.Tovar;
import eshop.bo.ciselniky.Zakaznik;
import eshop.su.db.DBCatalog;
import netball.server.print.PRBorderPanel;
import netball.server.print.PRComponent;
import netball.server.print.PREmptyComponent;
import netball.server.print.PRFlowCaptionComp;
import netball.server.print.PRFlowPanel;
import netball.server.print.PRFont;
import netball.server.print.PRLabel;
import netball.server.print.PRMultiLineLabel;
import netball.server.print.PRReport;
import netframework.bo.attributes.Attribute;
import netframework.eclipselink.EclipseLinkSession;
import netframework.mediator.ComponentBuilder;
import netframework.sql.SQLAliasField;
import netframework.sql.SQLAliasTable;
import netframework.sql.SQLExpressionBuilder;
import netframework.sql.SQLJoinCondition;
import netframework.sql.SQLQuery;
import netframework.view.ViewCursor;
import netframework.view.ViewRow;
import zelpo.eclipselink.autorizacia.Uzivatel;

public class ReportDokladObjednavkaFlowPanel extends ReportDokladFlowPanel {

	//otazka, ci toto potrebujem aj pri tlaci
	//private SQLAliasTable povodnaObjednavkaTable;
	
	@Override
	protected ViewRow readHeader() throws Exception {
		DBCatalog c = DBCatalog.getInstance();
        SQLQuery query = new SQLQuery();
        query.addTable(new SQLJoinCondition(c.OBJEDNAVKA)); 
        query.addTable(new SQLJoinCondition(c.OBCHODNY_PARTNER, SQLJoinCondition.LEFT_OUTER_JOIN, c.OBCHODNY_PARTNER.ID, c.OBJEDNAVKA.ZAKAZNIK));
        query.addTable(new SQLJoinCondition(c.UZIVATEL, SQLJoinCondition.LEFT_OUTER_JOIN, c.OBJEDNAVKA.VYSTAVIL, c.UZIVATEL.ID));
        //nove, takto?
        query.addTable(new SQLJoinCondition(c.KONTAKT, SQLJoinCondition.LEFT_OUTER_JOIN, c.OBCHODNY_PARTNER.ID, c.KONTAKT.OBCHODNY_PARTNER));
        query.addField(Objednavka.CISLO_OBJEDNAVKY.getId(), c.OBJEDNAVKA.CISLO_OBJEDNAVKY);
        query.addField(Objednavka.DATUM.getId(), c.OBJEDNAVKA.DATUM);
        query.addField(Objednavka.STAV_OBJEDNAVKY.getId(), c.OBJEDNAVKA.STAV_OBJEDNAVKY);
        query.addField(Objednavka.SUMA.getId(), c.OBJEDNAVKA.SUMA);
        
        query.addField(Uzivatel.MENO.getId(), c.UZIVATEL.MENO);
        
        query.addField(Zakaznik.NAZOV.getId(), c.OBCHODNY_PARTNER.NAZOV);
        query.addField(Zakaznik.KOD.getId(), c.OBCHODNY_PARTNER.KOD);
        
        query.addField(Kontakt.MENO.getId(), c.KONTAKT.MENO);
        query.addField(Kontakt.TELEFON.getId(), c.KONTAKT.TELEFON);
        query.addField(Kontakt.EMAIL.getId(), c.KONTAKT.EMAIL);
        
        query.setExpression(SQLExpressionBuilder.get(c.OBJEDNAVKA.ID).equal(this.dokladId));
        ViewCursor cursor = ((EclipseLinkSession) session).execute(query);
        if (!cursor.hasNext())
           throw new IllegalArgumentException("NEPODARILO_SA_NACITAT_DOKLAD_S_ID, " + dokladId);
        return cursor.next();   
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
	protected ItemBuilder createItemBuilder() {
	       return new PolozkyBuilder(); 	
	    }

	@Override
    protected PRReport createPage(ViewRow headerRow, int pageIndex) throws Exception {
        PRFlowPanel titleForm = new PRFlowPanel(javax.swing.SwingConstants.VERTICAL);       
        titleForm.add(new PREmptyComponent(0, 80));
        titleForm.add(createTitle(headerRow));
        titleForm.addGap(20);

        PRFlowCaptionComp zakaznikForm = new PRFlowCaptionComp(SwingConstants.VERTICAL);
        zakaznikForm.addItem(session.translateText(ObchodnyPartner.NAZOV.getCaption()), convertValueToString(headerRow, ObchodnyPartner.NAZOV), 370);
        zakaznikForm.addItem(session.translateText(ObchodnyPartner.KOD.getCaption()), convertValueToString(headerRow, ObchodnyPartner.KOD), 370);
        zakaznikForm.addItem(session.translateText(ObchodnyPartner.KONTAKTY.getCaption()), convertValueToString(headerRow, Kontakt.MENO) + ", " + convertValueToString(headerRow, Kontakt.TELEFON) + ", " + convertValueToString(headerRow, Kontakt.EMAIL), 370);
                
        PRBorderPanel zakaznikPanel = new PRBorderPanel(zakaznikForm);
        zakaznikPanel.setPaintBorder(true);
        zakaznikPanel.setTitle(session.translateText("Zakaznik"));
        zakaznikPanel.setInsets(new Insets(20, 10, 10, 10));
        titleForm.add(zakaznikPanel);
        
        PRReport page = new PRReport(); 
        page.setVerticalGap(10);
        page.setHeader(titleForm);
        page.setFooter(createFooter(headerRow));
        return page; 
    }
	
	private PRComponent createFooter(ViewRow headerRow) {
		String meno = convertValueToString(headerRow, Uzivatel.MENO);	
		
		PRMultiLineLabel label = new PRMultiLineLabel();
	    label.setRowCount(3);
	    label.setInsets(new Insets(2,2,2,2));
	    label.setWidth(520);
	    label.setPaintBorder(true);
	    label.setText(session.translateText("Vystavil: " + meno));
	    return label;              
	}
	
	protected class PolozkyBuilder implements ItemBuilder {
		
	    private int widthPC = 40;
	    private int widthNazov = 120;
	    private int widthMnozstvo = 120;
	    private int widthJednotkovaCena = 120;
	    private int widthSuma = 120;
	    private int rowHeight = 14;
	    private int widthTotal = 520; 
	    private PRFont font = new PRFont("Dialog", Font.PLAIN, 9);
	    private PRFlowPanel header;
	    
	    public PolozkyBuilder() {
	        PRFlowPanel headerForm1 = new PRFlowPanel(javax.swing.SwingConstants.HORIZONTAL);               
	        headerForm1.add(createHeaderLabel(session.translateText("Por. c."), widthPC, 1));
	        //tu chcem nazov tovaru
	        headerForm1.add(createHeaderLabel(session.translateText(Tovar.NAZOV.getCaption()), widthNazov, 1));        
	        
	        //PRFlowPanel headerForm2 = new PRFlowPanel(javax.swing.SwingConstants.HORIZONTAL);       
	        headerForm1.add(createHeaderLabel(session.translateText(ObjednavkaPolozka.MNOZSTVO.getCaption()), widthMnozstvo, 1));
	        headerForm1.add(createHeaderLabel(session.translateText(ObjednavkaPolozka.JEDNOTKOVA_CENA.getCaption()), widthJednotkovaCena, 1));
	        headerForm1.add(createHeaderLabel(session.translateText(ObjednavkaPolozka.SUMA.getCaption()), widthSuma, 1));
	        
	        header = new PRFlowPanel(javax.swing.SwingConstants.VERTICAL);       
	        header.add(headerForm1);
	        //header.add(headerForm2);
	    } 
	    
	    private PRMultiLineLabel createHeaderLabel(String text, int width, int rowCount) {
	        PRMultiLineLabel label = new PRMultiLineLabel(text);
	        label.setWidth(width);
	        label.setRowHeight(rowHeight);        
	        label.setRowCount(rowCount);        
	        label.setHorizontalAlign(javax.swing.SwingConstants.CENTER);
	        label.setVerticalAlign(javax.swing.SwingConstants.CENTER);
	        label.setPaintBorder(true);       
	        label.setFont(font);
	        return label;
	    }
	    
	    private PRLabel createItemLabel(String text, int width, int horizontalAlign) {
	        PRLabel label = new PRLabel(text);
	        label.setWidth(width);
	        label.setHeight(rowHeight);
	        label.setHorizontalAlign(horizontalAlign);
	        label.setVerticalAlign(javax.swing.SwingConstants.CENTER);
	        label.setPaintBorder(true);       
	        label.setFont(font);
	        return label;
	    }
	    
	    @Override
	    public PRComponent createRow(ViewRow row, ViewRow headerRow, boolean isLastRow, int pc) throws Exception {     
	    	String nazovTovaru = convertValueToString(row, Tovar.NAZOV); 
	    	String mnozstvo = convertValueToString(row, ObjednavkaPolozka.MNOZSTVO);
	    	String jednotkovaCena = convertValueToString(row, ObjednavkaPolozka.JEDNOTKOVA_CENA);
	    	String suma = convertValueToString(row, ObjednavkaPolozka.SUMA);
	    	
	        PRFlowPanel rowForm1 = new PRFlowPanel(javax.swing.SwingConstants.HORIZONTAL);
	        rowForm1.add(createItemLabel(""  + pc + ".", widthPC, javax.swing.SwingConstants.LEFT));
	        rowForm1.add(createItemLabel(nazovTovaru, widthNazov, javax.swing.SwingConstants.LEFT));
	
	        //PRFlowPanel rowForm2 = new PRFlowPanel(javax.swing.SwingConstants.HORIZONTAL);
	        rowForm1.add(createItemLabel(mnozstvo, widthMnozstvo, javax.swing.SwingConstants.RIGHT));
	        rowForm1.add(createItemLabel(jednotkovaCena, widthJednotkovaCena, javax.swing.SwingConstants.RIGHT));
	        rowForm1.add(createItemLabel(suma, widthSuma, javax.swing.SwingConstants.RIGHT));
	        
	        PRFlowPanel rowForm = new PRFlowPanel(javax.swing.SwingConstants.VERTICAL);               
	        rowForm.add(rowForm1);
	        //rowForm.add(rowForm2);
	        
	        if (isLastRow) {        	
		        PRLabel celkovaCenaComp = new PRLabel();
		        celkovaCenaComp.setHeight(rowHeight);
		        celkovaCenaComp.setText(session.translateText(Objednavka.SUMA.getCaption()) + ':' + convertValueToString(headerRow, Objednavka.SUMA));
		        celkovaCenaComp.setFont(font);        
		        celkovaCenaComp.setHorizontalAlign(javax.swing.SwingConstants.RIGHT);	        
		        celkovaCenaComp.setWidth(widthTotal);  
		        celkovaCenaComp.setPaintBorder(true);
		        PRFlowPanel rowForm4 = new PRFlowPanel(javax.swing.SwingConstants.VERTICAL);       
		        rowForm4.add(rowForm);
		        rowForm4.add(celkovaCenaComp);
		        return rowForm4;
	        }  
	        return rowForm;
	    }    
	    
	    @Override
		public int getHeaderHeight() {
			return header.getSize().height;
		}	
		
	    @Override
		public PRComponent getHeader() {
			return header;
		}	
	}
	
}
