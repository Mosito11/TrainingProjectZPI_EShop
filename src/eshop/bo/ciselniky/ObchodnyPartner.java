package eshop.bo.ciselniky;

import java.util.Vector;

import org.eclipse.persistence.zpi.expressions.Expression;
import org.eclipse.persistence.zpi.expressions.ExpressionBuilder;
import org.eclipse.persistence.zpi.indirection.ValueHolder;
import org.eclipse.persistence.zpi.indirection.ValueHolderInterface;
import org.eclipse.persistence.zpi.queries.ReportQuery;
import org.eclipse.persistence.zpi.queries.ReportQueryResult;

import netframework.bo.PersistentObject;
import netframework.bo.attributes.ConstantAttribute;
import netframework.bo.attributes.ContainerAttribute;
import netframework.bo.attributes.NumberAttribute;
import netframework.bo.attributes.TextAttribute;
import netframework.eclipselink.EclipseLinkSession;
import zelpo.eclipselink.autorizacia.PridelenieRole;

public abstract class ObchodnyPartner extends PersistentObject {
	
	public static final NumberAttribute ID = new NumberAttribute("ObchodnyPartner.id", "id");
	public static final TextAttribute KOD = new TextAttribute("ObchodnyPartner.kod", "kod");
    public static final TextAttribute NAZOV = new TextAttribute("ObchodnyPartner.nazov", "nazov");
    public static final ConstantAttribute TYP = new ConstantAttribute("ObchodnyPartner.typ", "typ", TypObchodnehoPartnera.ZOZNAM);
    
    public static final ContainerAttribute ADRESY = new ContainerAttribute("ObchodnyPartner.adresy", "adresy");
    //doplnene pri tvorbe UCKontakt
    public static final ContainerAttribute KONTAKTY = new ContainerAttribute("ObchodnyPartner.kontakty", "kontakty");
	
    static {
    	KOD.setCaption("Kod \nobchodneho \npartnera");
		KOD.setLongCaption("Kod obchodneho partnera");
		KOD.setColumnName(KOD.getCaption());
		KOD.setLongColumnName(KOD.getLongCaption());
		KOD.setTypeLetters(TextAttribute.CAPITAL_LETTERS);
		KOD.setLimit(10);
		KOD.setRequired(true);
		KOD.setFixedLimit(true);
		
		NAZOV.setCaption("Nazov \nobchodneho \npartnera");
		NAZOV.setLongCaption("Nazov obchodneho partnera");
		NAZOV.setColumnName(NAZOV.getCaption());
		NAZOV.setLongColumnName(NAZOV.getLongCaption());
		NAZOV.setLimit(30);
		NAZOV.setRequired(true);
		
		TYP.setCaption("Typ");
		TYP.setRequired(true);
		
		ADRESY.setCaption("Adresa");
		ADRESY.setLongCaption("Adresa obchodneho partnera");
		ADRESY.setColumnName(ADRESY.getCaption());
		ADRESY.setLongColumnName(ADRESY.getLongCaption());
		
		KONTAKTY.setCaption("Kontakt");
		KONTAKTY.setLongCaption("Kontakt obchodneho partnera");
		KONTAKTY.setColumnName(KONTAKTY.getCaption());
		KONTAKTY.setLongColumnName(KONTAKTY.getLongCaption());
	}
    
	private String kod;
	private String nazov;
	private TypObchodnehoPartnera typ;
	
	private ValueHolderInterface adresy = new ValueHolder();
	private ValueHolderInterface kontakty = new ValueHolder();
	
	public String getKod() {
		return kod;
	}
	
	public void setKod(String kod) {
		this.kod = kod;
	}
	
	public String getNazov() {
		return nazov;
	}
	
	public void setNazov(String nazov) {
		this.nazov = nazov;
	}
	
	public TypObchodnehoPartnera getTyp() {
		return typ;
	}
	
	protected void setTyp(TypObchodnehoPartnera typ) {
		this.typ = typ;
	}
	
	//transformacne pre TopLink
	
	public String getTypTL() {
        if (typ == null)
            return null;
        return typ.getKey();
    }

    public void setTypTL(String typ) {
        try {
            this.typ = TypObchodnehoPartnera.convert(typ);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public Vector<Adresa> getAdresy() {
		if (adresy.getValue() == null)
			adresy.setValue(new Vector<Adresa>());
		return (Vector<Adresa>) adresy.getValue();
	}
    
    public Vector<Kontakt> getKontakty() {
		if (kontakty.getValue() == null)
			kontakty.setValue(new Vector<Kontakt>());
		return (Vector<Kontakt>) kontakty.getValue();
	}
    /*
    //doplneny setter na kontrolu funkcnosti ManyToMany Mapping - setter nepotrebujem, pouzivam .getAdresy().add();
    public void setAdresy(Vector<Adresa> adresy) {
		this.adresy.setValue(adresy);
	}
	*/

	public void validate(EclipseLinkSession session) throws Exception {
        KOD.checkValue(getKod(), session);
        NAZOV.checkValue(getNazov(), session);
        validateDuplicitnaAdresa(session);
    }
	
	public void validateDuplicitnaAdresa(EclipseLinkSession session) throws Exception {
		Vector<Adresa> adresy = this.getAdresy();
		if (adresy.size() > 0) {
			// kontrola ci adresa sa nenachadza viackrat
			for (int i = 0; i < adresy.size(); i++) {
			   Adresa adresa = adresy.get(i);
			   for (int k = i + 1; k < adresy.size(); k++) {
				   Adresa adresa1 = adresy.get(k);
				   if (adresa.equals(adresa1)) {
					   throw new IllegalArgumentException("Adresa " + adresa.toString() + " u obchodneho partnera " + this.getNazov() + " uz existuje!");
				   }
			   }
			}
		}
	}
	
	
	public void delete(EclipseLinkSession session) throws Exception {
		validateObchodnyPartnerBeforeDelete(session);
	}
	
	//validacia pred vymazanim podla typu
	public void validateObchodnyPartnerBeforeDelete(EclipseLinkSession session) throws Exception {
		
		ExpressionBuilder builder = new ExpressionBuilder();
		
		if (this.getTypTL().equals("Z")) {
			Expression exp = builder.get(Objednavka.ZAKAZNIK.getName()).equal(this);
			Vector objednavky = session.getSession().readAllObjects(Objednavka.class, exp);
			if (objednavky.size() == 0)
			       return;
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < objednavky.size(); i++) {
			Objednavka objednavka = (Objednavka)objednavky.get(i);
			buffer.append("\n\t");
			buffer.append(objednavka.getCisloObjednavky());
			}
			throw new IllegalArgumentException("Nie je mozne vymazat obchodneho partnera, pretoze sa nachadza v nasledovnych objednavkach: " + buffer.toString());
		}else if (this.getTypTL().equals("D")) {
			Expression exp = builder.get(Tovar.DODAVATEL.getName()).equal(this);
			Vector tovary = session.getSession().readAllObjects(Tovar.class, exp);
			if (tovary.size() == 0)
			       return;
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < tovary.size(); i++) {
			Tovar tovar = (Tovar)tovary.get(i);
			buffer.append("\n\t");
			buffer.append(tovar.getNazov());
			}
			throw new IllegalArgumentException("Nie je mozne vymazat obchodneho partnera, pretoze sa nachadza v nasledovnych tovaroch " + buffer.toString());
		
		}
		
		System.out.println("Zle zadany typ.");
			
	}
		
}
