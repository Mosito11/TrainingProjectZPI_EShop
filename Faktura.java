package eshop.bo.ciselniky;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Vector;

import org.eclipse.persistence.zpi.expressions.Expression;
import org.eclipse.persistence.zpi.expressions.ExpressionBuilder;
import org.eclipse.persistence.zpi.indirection.ValueHolder;
import org.eclipse.persistence.zpi.indirection.ValueHolderInterface;
import org.eclipse.persistence.zpi.queries.ReportQuery;
import org.eclipse.persistence.zpi.queries.ReportQueryResult;
import org.eclipse.persistence.zpi.sessions.UnitOfWork;

import eshop.bo.common.Helper;
import netframework.bo.PersistentObject;
import netframework.bo.attributes.ConstantAttribute;
import netframework.bo.attributes.ContainerAttribute;
import netframework.bo.attributes.DateAttribute;
import netframework.bo.attributes.FormattedTextAttribute;
import netframework.bo.attributes.NumberAttribute;
import netframework.bo.attributes.RelationAttribute;
import netframework.eclipselink.EclipseLinkSession;
import zelpo.eclipselink.autorizacia.Uzivatel;

public class Faktura extends PersistentObject {
	
	public static final NumberAttribute ID = new NumberAttribute("Faktura.id", "id");
	//automaticky, podobne ako cislo objednavky
	public static final FormattedTextAttribute CISLO_FAKTURY = new FormattedTextAttribute("Faktura.cisloFaktury", "cisloFaktury");
	
	public static final DateAttribute DATUM_VYSTAVENIA = new DateAttribute("Faktura.datumVystavenia", "datumVystavenia");
	public static final DateAttribute DATUM_DODANIA = new DateAttribute("Faktura.datumDodania", "datumDodania");
	public static final DateAttribute DATUM_SPLATNOSTI = new DateAttribute("Faktura.datumSplatnosti", "datumSplatnosti");
	
	public static final RelationAttribute OBJEDNAVKA = new RelationAttribute("Faktura.objednavka", "Objednavka");
	
	//public static final ConstantAttribute STAV_FAKTURY = new ConstantAttribute("Faktura.stavFaktury", "stavFaktury", StavFaktury.ZOZNAM);
    
	//TODO - stornovana objednavka by mala vytvarat dobropis nejako! ked stornujem objednavku, musim vytvori dobropis, ak k danej objednavke existuje faktura
	
	static {
		CISLO_FAKTURY.setCaption("Cislo faktury");
		CISLO_FAKTURY.setLongCaption("Cislo faktury");
		CISLO_FAKTURY.setColumnName(CISLO_FAKTURY.getCaption());
		CISLO_FAKTURY.setLongColumnName(CISLO_FAKTURY.getLongCaption());
		CISLO_FAKTURY.setRequired(true);
		CISLO_FAKTURY.setMask("####-^-####");
    	
		DATUM_VYSTAVENIA.setCaption("Datum vystavenia");
		DATUM_VYSTAVENIA.setColumnName("Datum vystavenia");
		DATUM_VYSTAVENIA.setRequired(true);
		
		DATUM_DODANIA.setCaption("Datum dodania");
		DATUM_DODANIA.setColumnName("Datum dodania");
		DATUM_DODANIA.setRequired(true);
		
		DATUM_SPLATNOSTI.setCaption("Datum splatnosti");
		DATUM_SPLATNOSTI.setColumnName("Datum splatnosti");
		DATUM_SPLATNOSTI.setRequired(true);
		
		OBJEDNAVKA.setCaption("Objednavka");
		OBJEDNAVKA.setLongCaption("Objednavka");
		OBJEDNAVKA.setColumnName(OBJEDNAVKA.getCaption());
		OBJEDNAVKA.setLongColumnName(OBJEDNAVKA.getLongCaption());
		OBJEDNAVKA.setRequired(true);	
}
	
	private String cisloFaktury;
	private Date datumVystavenia = new Date(System.currentTimeMillis());
	private Date datumDodania;
	private Date datumSplatnosti = nastavSplatnost(datumVystavenia);
	private ValueHolderInterface objednavka = new ValueHolder();
	
	private Date nastavSplatnost(Date date) {
		LocalDate localDate = datumVystavenia.toLocalDate();
		LocalDate novyLocalDate = localDate.plusDays(Helper.DEFAULT_SPLATNOST);
		Date datum = Date.valueOf(novyLocalDate);
		return datum;
	}
	
	public String getCisloFaktury() {
		return cisloFaktury;
	}
	
	public void setCisloFaktury(String cisloFaktury) {
		if (cisloFaktury != null)
			cisloFaktury = cisloFaktury.trim();
		this.cisloFaktury = cisloFaktury;
	}
	
	public Date getDatumVystavenia() {
		return datumVystavenia;
	}
	
	//TODO - nebude setter ale pravidlo
	//public void setDatumVystavenia(Date datumVystavenia) {
	//	this.datumVystavenia = datumVystavenia;
	//}
	
	public Date getDatumDodania() {
		return datumDodania;
	}
	
	//TODO - nebude setter ale pravidlo
	public void setDatumDodania(Date datumDodania) {
		this.datumDodania = datumDodania;
	}
	
	public Date getDatumSplatnosti() {
		return datumSplatnosti;
	}
	
	//TODO - nebude setter ale pravidlo
	public void setDatumSplatnosti(Date datumSplatnosti) {
		this.datumSplatnosti = datumSplatnosti;
	}

	public Objednavka getObjednavka() {
		return (Objednavka) objednavka.getValue();
	}
	
	public void setObjednavka(Objednavka objednavka) {
		this.objednavka.setValue(objednavka);;
	}
	
	public void validate(EclipseLinkSession session, UnitOfWork unitOfWork) throws Exception {
	    validateDatumy(session);
	    validateDuplicity(session);
	    validateStavObjednavky(session);
	    validateStornoObjednavky(session);
	    //kontrola pritomnosti Objednavky, bude tu?
	    /*if (!isPersisted() || getObjednavka() == null) {
	      	Objednavka objednavka = (Objednavka) unitOfWork.registerObject(session.getUser());
	       	setObjednavka(objednavka);
	    }*/
	    OBJEDNAVKA.checkValue(getObjednavka(), session);
	}
	
	public void validateDatumy(EclipseLinkSession session) throws Exception {
		DATUM_VYSTAVENIA.checkValue(getDatumVystavenia(), session);
		DATUM_DODANIA.checkValue(getDatumDodania(), session);
		DATUM_SPLATNOSTI.checkValue(getDatumSplatnosti(), session);
	}
	
	public void validateDuplicity(EclipseLinkSession session) throws Exception {
		if (this.getObjednavka() == null)
    		return;
        ExpressionBuilder builder = new ExpressionBuilder();
        
        Expression exp = builder.get("objednavka").equal(getObjednavka());
        
        if (isPersisted()) 
                exp = exp.and(builder.get(ID.getName()).notEqual(getId()));       
        
        ReportQuery query = new ReportQuery();                
        query.setReferenceClass(getClass());
        query.addAttribute(ID.getName());
        query.setSelectionCriteria(exp);
        Vector vector = (Vector) session.getSession().executeQuery(query);
        
        if (vector.size() > 0) {
        
            throw new Exception("K objednavke " + getObjednavka().getCisloObjednavky() + " uz faktura bola vytvorena!");              
        }
        
	}
	
	public void validateStavObjednavky(EclipseLinkSession session) throws Exception {
		if (this.getObjednavka() == null)
    		return;
		if (!this.getObjednavka().getStavObjednavkyTL().equals("D")) {
        
            throw new Exception("Fakturu je mozne vystavit iba k dokoncenej objednavke!");              
        }
	}
	
	public void validateStornoObjednavky(EclipseLinkSession session) throws Exception {
		//UnitOfWork work = session.getSession().acquireUnitOfWork();
    	Objednavka objednavka = this.getObjednavka();
    	
    	//TODO kontrola ci bola objednavka uz stornovana
    	
 		ExpressionBuilder builder = new ExpressionBuilder();
		Expression exp = builder.get(Objednavka.POVODNA_OBJEDNAVKA.getName()).equal(objednavka);
		
		ReportQuery query = new ReportQuery();
		query.setReferenceClass(Objednavka.class);
		query.addCount();
		query.setSelectionCriteria(exp);
		//System.out.println("query " + query.toString());
		Vector<ReportQueryResult> result = (Vector<ReportQueryResult>) session.getSession().executeQuery(query);
		//System.out.println("result " + result);
				
		if (result.size() != 0) {
			ReportQueryResult row = result.get(0);
			int pocet = ((Number) row.getByIndex(0)).intValue();
			if (pocet > 0) throw new IllegalArgumentException("Objednavka " + objednavka.getId() + " bola stornovana, nie je mozne vytvorit fakturu!"); 
		}
		    	
        
	}

}
