package eshop.bo.ciselniky;

import java.util.Vector;

import org.eclipse.persistence.zpi.expressions.Expression;
import org.eclipse.persistence.zpi.expressions.ExpressionBuilder;
import org.eclipse.persistence.zpi.queries.ReportQuery;

import netframework.bo.ValueException;
import netframework.bo.attributes.TextAttribute;
import netframework.eclipselink.EclipseLinkSession;

public class Dodavatel extends ObchodnyPartner {

	public static final TextAttribute ICO = new TextAttribute("Dodavatel.ico", "ico");
	
	static{
    	ICO.setCaption("Ico");
		ICO.setTypeLetters(TextAttribute.CAPITAL_LETTERS);
		ICO.setLimit(8);
		ICO.setRequired(true);
		ICO.setFixedLimit(true);
    }
	
	private String ico;
	
	public Dodavatel() {
		this.setTyp(TypObchodnehoPartnera.DODAVATEL);
	}

	public String getIco() {
		return ico;
	}

	public void setIco(String ico) {
		if (ico != null)
			ico = ico.trim();
		this.ico = ico;
	}
	
	public void validate(EclipseLinkSession session) throws Exception {
		super.validate(session);
		validateIcoDuplicity(session);
    }
	
	//kontrola, ci nie je duplicitne ICO
	public void validateIcoDuplicity(EclipseLinkSession session) throws Exception {
    	if (this.getIco() == null)
    		return;
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression exp = builder.get("ico").equal(getIco());
        
        //funguje ale nerozumiem tomuto kodu
        if (isPersisted()) 
                exp = exp.and(builder.get(ID.getName()).notEqual(getId()));       
        
        ReportQuery query = new ReportQuery();                
        query.setReferenceClass(getClass());
        query.addAttribute(ID.getName());
        query.setSelectionCriteria(exp);
        Vector vector = (Vector) session.getSession().executeQuery(query);
        
        if (vector.size() > 0) {
        
            throw new Exception("Dodavatel s ICOm " + getIco() + " uz existuje!");              
        }
     }  
		
}
