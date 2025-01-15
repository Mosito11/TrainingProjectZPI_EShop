package eshop.bo.ciselniky;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.persistence.zpi.expressions.Expression;
import org.eclipse.persistence.zpi.expressions.ExpressionBuilder;
import org.eclipse.persistence.zpi.indirection.ValueHolder;
import org.eclipse.persistence.zpi.indirection.ValueHolderInterface;
import org.eclipse.persistence.zpi.queries.ReportQuery;
import org.eclipse.persistence.zpi.queries.ReportQueryResult;

import netframework.bo.PersistentObject;
import netframework.bo.attributes.NumberAttribute;
import netframework.bo.attributes.RelationAttribute;
import netframework.bo.attributes.TextAttribute;
import netframework.eclipselink.EclipseLinkSession;

public class Kontakt extends PersistentObject {
	
	public static final NumberAttribute ID = new NumberAttribute("Kontakt.id", "id");
	public static final TextAttribute MENO = new TextAttribute("Kontakt.meno", "meno");
    public static final TextAttribute EMAIL = new TextAttribute("Kontakt.email", "email");
    public static final TextAttribute TELEFON = new TextAttribute("Kontakt.telefon", "telefon");
    public static final RelationAttribute OBCHODNY_PARTNER = new RelationAttribute("Tovar.obchodnyPartner", "obchodnyPartner");
	
	private String meno;
	private String email;
	private String telefon;
	private ValueHolderInterface obchodnyPartner = new ValueHolder();
	
	static{
    	MENO.setCaption("Meno");
		/*
		KOD.setLongCaption();
		KOD.setColumnName();
		KOD.setLongColumnName();
		*/
		MENO.setLimit(50);
		MENO.setRequired(true);
		
		EMAIL.setCaption("Email");
		/*
		NAZOV.setLongCaption();
		NAZOV.setColumnName(NAZOV.getCaption());
		NAZOV.setLongColumnName(NAZOV.getLongCaption());
		*/
		EMAIL.setLimit(20);
		EMAIL.setRequired(true);
			
		TELEFON.setCaption("Telefon");
		/*
		VELKOST.setLongCaption();
		VELKOST.setColumnName();
		VELKOST.setLongColumnName();
		*/
		TELEFON.setLimit(13);
		TELEFON.setFixedLimit(true);
		TELEFON.setRequired(true);
		
		OBCHODNY_PARTNER.setRequired(true);		
    }
	
	public String getMeno() {
		return meno;
	}
	
	public void setMeno(String meno) {
		if (meno != null)
			meno = meno.trim();
		this.meno = meno;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		if (email != null)
			email = email.trim();
		this.email = email;
	}
	
	public String getTelefon() {
		return telefon;
	}
	
	public void setTelefon(String telefon) {
		if (telefon != null)
			telefon = telefon.trim();		
		this.telefon = telefon;
	}
	
	public ObchodnyPartner getObchodnyPartner() {
		return (ObchodnyPartner ) obchodnyPartner.getValue();
	}
	
	public void setObchodnyPartner(ObchodnyPartner obchodnyPartner) {
		this.obchodnyPartner.setValue(obchodnyPartner);
	}

	public void validate(EclipseLinkSession session) throws Exception {
        MENO.checkValue(getMeno(), session);
        validateEmailFormat(getEmail());
        EMAIL.checkValue(getEmail(), session);
        TELEFON.checkValue(getTelefon(), session);
        OBCHODNY_PARTNER.checkValue(getObchodnyPartner(), session);
    }
	
	//funkcia na vymazanie kontaktu, zatial nepotrebujem
	/*
	public void delete(EclipseLinkSession session) throws Exception {
 		ExpressionBuilder builder = new ExpressionBuilder();
		Expression exp = builder.anyOf(ObchodnyPartner.KONTAKTY.getName()).equal(this);
		ReportQuery query = new ReportQuery();
		query.setReferenceClass(ObchodnyPartner.class);
		query.addAttribute(ObchodnyPartner.KOD.getName());
		query.setSelectionCriteria(exp);
		
		Vector<ReportQueryResult> result = (Vector<ReportQueryResult>) session.getSession().executeQuery(query);
		if (result.size() == 0)
			return;
		StringBuilder buffer = new StringBuilder(); 
		for (int i = 0; i < result.size(); i++) {
			ReportQueryResult row = result.get(i);
			buffer.append("\n\t");
			buffer.append(row.getByIndex(0));
		}
		throw new IllegalArgumentException("Kontakt nie je mozne vymazat, pretoze je priradeny k nasledovnym partnerom " + buffer);
    }
    */
	
	//kontrola, ci email je naozaj email
	private void validateEmailFormat(String email) throws Exception {
	    if (email == null)
	    	return;
	        
	    String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
	    Pattern pattern = Pattern.compile(regex);
	    Matcher matcher = pattern.matcher(email);
	    	
	    if (!matcher.matches()) 
	        throw new Exception(getEmail() + " nie je spravny format mailovej adresy!");              
	        
	}  
	
}
