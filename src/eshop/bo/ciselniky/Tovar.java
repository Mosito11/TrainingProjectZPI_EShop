package eshop.bo.ciselniky;

import java.math.BigDecimal;
import java.util.Vector;

import org.eclipse.persistence.zpi.expressions.Expression;
import org.eclipse.persistence.zpi.expressions.ExpressionBuilder;
import org.eclipse.persistence.zpi.indirection.ValueHolder;
import org.eclipse.persistence.zpi.indirection.ValueHolderInterface;
import org.eclipse.persistence.zpi.queries.ReportQuery;
import org.eclipse.persistence.zpi.queries.ReportQueryResult;

import eshop.bo.common.Helper;
import netframework.bo.PersistentObject;
import netframework.bo.attributes.NumberAttribute;
import netframework.bo.attributes.RelationAttribute;
import netframework.bo.attributes.TextAttribute;
import netframework.eclipselink.EclipseLinkSession;

public class Tovar extends PersistentObject {

	public static final NumberAttribute ID = new NumberAttribute("Tovar.id", "id");
	public static final TextAttribute KOD = new TextAttribute("Tovar.kod", "kod");
    public static final TextAttribute NAZOV = new TextAttribute("Tovar.nazov", "nazov");
    public static final NumberAttribute CENA = new NumberAttribute("Tovar.cena", "cena");
    public static final TextAttribute VELKOST = new TextAttribute("Tovar.velkost", "velkost");
    public static final NumberAttribute SKLADOVA_ZASOBA = new NumberAttribute("Tovar.skladovaZasoba", "skladovaZasoba");
    public static final RelationAttribute DODAVATEL = new RelationAttribute("Tovar.dodavatel", "dodavatel");
    public static final RelationAttribute TOVAR_DRUH = new RelationAttribute("Tovar.tovarDruh", "tovarDruh");
    
    static{
    	ID.setCaption("Id tovaru");
    	ID.setLongCaption("Id tovaru");
    	ID.setColumnName(ID.getCaption());
    	ID.setLongColumnName(ID.getLongCaption());
    	
    	KOD.setCaption("Kod");
		KOD.setLongCaption("Kod tovaru");
		KOD.setColumnName(KOD.getCaption());
		KOD.setLongColumnName(KOD.getLongCaption());
		KOD.setTypeLetters(TextAttribute.CAPITAL_LETTERS);
		KOD.setLimit(3);
		KOD.setRequired(true);
		KOD.setFixedLimit(true);
		
		NAZOV.setCaption("Nazov tovaru");
		NAZOV.setLongCaption("Nazov tovaru");
		NAZOV.setColumnName(NAZOV.getCaption());
		NAZOV.setLongColumnName(NAZOV.getLongCaption());
		NAZOV.setLimit(50);
		NAZOV.setRequired(true);
		
		CENA.setCaption("Cena");
		/*
		CENA.setColumnName();
		*/
		CENA.setRequired(true);
		CENA.setZeroAccepted(false);
		CENA.setNegativeAccepted(false);
		CENA.setLength(8);
		CENA.setScale(2);
		CENA.setMask("###,##0.00");
		
		VELKOST.setCaption("Velkost");
		/*
		VELKOST.setLongCaption();
		VELKOST.setColumnName();
		VELKOST.setLongColumnName();
		*/
		VELKOST.setTypeLetters(TextAttribute.CAPITAL_LETTERS);
		VELKOST.setLimit(3);
		VELKOST.setRequired(true);
		
		SKLADOVA_ZASOBA.setCaption("Skladova zasoba");
		SKLADOVA_ZASOBA.setLongCaption("Skladova\nzasoba");
		SKLADOVA_ZASOBA.setColumnName(SKLADOVA_ZASOBA.getCaption());
		SKLADOVA_ZASOBA.setLongColumnName(SKLADOVA_ZASOBA.getLongCaption());
		SKLADOVA_ZASOBA.setRequired(true);
		SKLADOVA_ZASOBA.setZeroAccepted(false);
		SKLADOVA_ZASOBA.setLength(8);
		SKLADOVA_ZASOBA.setScale(0);
		SKLADOVA_ZASOBA.setMask("##,###,##0");
		
		DODAVATEL.setRequired(true);
		
		TOVAR_DRUH.setCaption("Druh tovaru");
		TOVAR_DRUH.setLongCaption("Druh tovaru");
		TOVAR_DRUH.setColumnName(TOVAR_DRUH.getCaption());
		TOVAR_DRUH.setLongColumnName(TOVAR_DRUH.getLongCaption());
		TOVAR_DRUH.setRequired(true);
    }
    
	private String kod;
	private String nazov;
	private BigDecimal cena = Helper.DEFAULT_VALUE;
	private String velkost;
	private BigDecimal skladovaZasoba = Helper.DEFAULT_VALUE;
	private ValueHolderInterface dodavatel = new ValueHolder();
	private ValueHolderInterface tovarDruh = new ValueHolder();
	
	public String getKod() {
		return kod;
	}
	public void setKod(String kod) {
		if (kod != null)
			kod = kod.trim();
		this.kod = kod;
	}
	public String getNazov() {
		return nazov;
	}
	public void setNazov(String nazov) {
		if (nazov != null)
			nazov = nazov.trim();
		this.nazov = nazov;
	}
	public BigDecimal getCena() {
		return cena;
	}
	public void setCena(BigDecimal cena) {
		if (cena == null)
			cena = Helper.DEFAULT_VALUE;
		this.cena = cena;
	}
	public String getVelkost() {
		return velkost;
	}
	public void setVelkost(String velkost) {
		if (velkost != null)
			velkost = velkost.trim();
		this.velkost = velkost;
	}
	public BigDecimal getSkladovaZasoba() {
		return skladovaZasoba;
	}
	public void setSkladovaZasoba(BigDecimal skladovaZasoba) {
		if (skladovaZasoba == null)
			skladovaZasoba = Helper.DEFAULT_VALUE;
		this.skladovaZasoba = skladovaZasoba;
	}
	
	public Dodavatel getDodavatel() {
		return (Dodavatel) dodavatel.getValue();
	}
	
	public void setDodavatel(Dodavatel dodavatel) {
		this.dodavatel.setValue(dodavatel);
	}
		
	public TovarDruh getTovarDruh() {
		return (TovarDruh) tovarDruh.getValue();
	}
	
	public void setTovarDruh(TovarDruh tovarDruh) {
		this.tovarDruh.setValue(tovarDruh);
	}
	
	public void validate(EclipseLinkSession session) throws Exception {
        KOD.checkValue(getKod(), session);
        NAZOV.checkValue(getNazov(), session);
        CENA.checkValue(getCena(), session);
        VELKOST.checkValue(getVelkost(), session);
        SKLADOVA_ZASOBA.checkValue(getSkladovaZasoba(), session);
        DODAVATEL.checkValue(getDodavatel(), session);
        TOVAR_DRUH.checkValue(getTovarDruh(), session);
	}
	
	//neviem, ci vyuzijem
	public void delete(EclipseLinkSession session) throws Exception {
		
		ExpressionBuilder builder = new ExpressionBuilder();
		Expression exp = builder.get(ObjednavkaPolozka.TOVAR.getName()).equal(this);
		Vector polozky = session.getSession().readAllObjects(ObjednavkaPolozka.class, exp);
		if (polozky.size() == 0)
		       return;
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < polozky.size(); i++) {
		ObjednavkaPolozka polozka = (ObjednavkaPolozka) polozky.get(i);
		Objednavka objednavka = (Objednavka) polozka.getObjednavka();
		buffer.append("\n\t");
		buffer.append(objednavka.getCisloObjednavky());
		}
		throw new IllegalArgumentException("Nie je mozne vymazat tovar, pretoze sa nachadza v nasledovnych objednavkach: " + buffer.toString());
	
	}
		
}
