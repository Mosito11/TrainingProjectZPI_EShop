package eshop.bo.ciselniky;

import java.util.Vector;

import org.eclipse.persistence.zpi.expressions.Expression;
import org.eclipse.persistence.zpi.expressions.ExpressionBuilder;
import org.eclipse.persistence.zpi.indirection.ValueHolder;
import org.eclipse.persistence.zpi.indirection.ValueHolderInterface;
import org.eclipse.persistence.zpi.queries.ReportQuery;
import org.eclipse.persistence.zpi.queries.ReportQueryResult;

import netframework.bo.PersistentObject;
import netframework.bo.attributes.ContainerAttribute;
import netframework.bo.attributes.NumberAttribute;
import netframework.bo.attributes.TextAttribute;
import netframework.eclipselink.EclipseLinkSession;

public class TovarDruh extends PersistentObject {
	
	//vytvaram triedu a extendujem PersistentObject
	//vytvaram konstanty na jednotlive atributy triedy kvoli validacii(pozri aj v netball demo)
	public static final NumberAttribute ID = new NumberAttribute("TovarDruh.id", "id");
	public static final TextAttribute KOD = new TextAttribute("TovarDruh.kod", "kod");
    public static final TextAttribute NAZOV = new TextAttribute("TovarDruh.nazov", "nazov");
	
    //staticky blok, kde nastavujem parametre atributu
    static{
		KOD.setCaption("Kod\ndruh tovaru");
		KOD.setLongCaption("Kod druh tovaru");
		KOD.setColumnName(KOD.getCaption());
		KOD.setLongColumnName(KOD.getLongCaption());
		KOD.setTypeLetters(TextAttribute.CAPITAL_LETTERS);
		KOD.setLimit(3);
		KOD.setRequired(true);
		KOD.setFixedLimit(true);
		
		NAZOV.setCaption("Druh tovaru");
		NAZOV.setLongCaption("Druh tovaru nazov");
		NAZOV.setColumnName(NAZOV.getCaption());
		NAZOV.setLongColumnName(NAZOV.getLongCaption());
		NAZOV.setLimit(50);
		NAZOV.setRequired(true);
    }
	
    //original atributy 
	private String kod;
	private String nazov;
	private ValueHolderInterface tovary = new ValueHolder();
	
	//gettery a settery (v setteroch trimujem)
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
	
	//zakladny kod na validaciu, potrebujem EclipseLinkSession
	public void validate(EclipseLinkSession session) throws Exception {
        KOD.checkValue(getKod(), session);
        NAZOV.checkValue(getNazov(), session);
	}
	
	public void delete(EclipseLinkSession session) throws Exception {
		validateTovarDruhBeforeDelete(session);
	}
	
	public void validateTovarDruhBeforeDelete(EclipseLinkSession session) throws Exception {
		
		ExpressionBuilder builder = new ExpressionBuilder();
		Expression exp = builder.get(Tovar.TOVAR_DRUH.getName()).equal(this);
		Vector tovary = session.getSession().readAllObjects(Tovar.class, exp);
		if (tovary.size() == 0)
			       return;
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < tovary.size(); i++) {
		Tovar tovar = (Tovar) tovary.get(i);
		buffer.append("\n\t");
		buffer.append(tovar.getNazov());
		}
			throw new IllegalArgumentException("Nie je mozne vymazat tento druh tovaru, pretoze patri k nasledujucim tovarom: " + buffer.toString());
	}
}
