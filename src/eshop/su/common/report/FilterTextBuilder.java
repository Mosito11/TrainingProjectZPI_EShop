package eshop.su.common.report;

import java.text.SimpleDateFormat;

import netball.server.component.expressionfield.ValueExpression;
import netball.server.utilities.AccountDate;
import netframework.bo.attributes.DateAttribute;
import netframework.mediator.SessionObject;

public class FilterTextBuilder {
	
	private StringBuffer buffer = new StringBuffer();
    
    public void add(String label, Object value, SessionObject session) {
        String text = session.translateText(label) + ": " + convertValueToString(value);
        add(text);
    }
           
    public void add(String text) {
        int len = buffer.length() + text.length();
        if (len > 100) {
           buffer.append('\n');
        }else {   
          if (buffer.length() != 0)
             buffer.append("   ");
        }     
        buffer.append(text);        
    }    
    
    private String convertValueToString(Object value) {
       if (value == null)
          return "";
       try { 
          if (value instanceof AccountDate) {            
        	 SimpleDateFormat format = new SimpleDateFormat(DateAttribute.DEFAULT_ACCOUNT_DATE_MASK);
             return format.format((String) value);
          }else if (value instanceof ValueExpression) {   
             return ((ValueExpression) value).getExpressionText();
          }else if (value instanceof java.util.Date) {
             SimpleDateFormat format = new SimpleDateFormat(DateAttribute.DEFAULT_DATE_MASK);
             return format.format(value);           
          } 
       }catch(Exception e) {            
          e.printStackTrace();
       } 
       return value.toString();
    }
    
    public String toString() {
        return "\n" + buffer.toString();
    }

}
