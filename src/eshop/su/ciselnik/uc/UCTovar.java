package eshop.su.ciselnik.uc;

import java.math.BigDecimal;

import org.eclipse.persistence.zpi.sessions.UnitOfWork;

import eshop.bo.ciselniky.Dodavatel;
import eshop.bo.ciselniky.Objednavka;
import eshop.bo.ciselniky.Tovar;
import eshop.bo.ciselniky.TovarDruh;
import eshop.bo.ciselniky.Zakaznik;
import eshop.bo.common.Helper;
import netball.server.pack.EnabledPack;
import netball.server.pack.Item;
import netball.server.pack.RequiredPack;
import netball.server.pack.ValuePack;
import netframework.bo.PersistentObject;
import netframework.eclipselink.EclipseLinkSession;
import netframework.eclipselink.UCPersistentObject;
import netframework.eclipselink.UCPersistentObject.DeleteController;

public class UCTovar extends UCPersistentObject {
	
	public static final String KOD = Tovar.KOD.getId();
	public static final String NAZOV = Tovar.NAZOV.getId();
	public static final String CENA = Tovar.CENA.getId();
	public static final String VELKOST = Tovar.VELKOST.getId();
	public static final String SKLADOVA_ZASOBA = Tovar.SKLADOVA_ZASOBA.getId();
	
	public static final String TOVAR_DRUH_KOD = TovarDruh.KOD.getId();
	public static final String TOVAR_DRUH_NAZOV = TovarDruh.NAZOV.getId();
			
	public static final String DODAVATEL_KOD = Dodavatel.KOD.getId();
	public static final String DODAVATEL_NAZOV = Dodavatel.NAZOV.getId();
	public static final String DODAVATEL_ICO = Dodavatel.ICO.getId();
		

	protected UCTovar(Tovar tovar, EclipseLinkSession session) {
		super(tovar, session);
		// TODO pridat overenia na editaciu a pridavanie, zatial neriesim
	}

	@Override
	protected void setValuePack(ValuePack packet) throws Exception {
		if (packet == null)
	           return;
		EnabledPack enabledPack = getEnabledPack(); 
		for (int i = 0; i < packet.size(); i++) {
			Item item = packet.get(i);                
			Object id = item.getId();
			Object value = item.getValue();                
			if (enabledPack != null && !enabledPack.isEnabled(id))
				continue;
			Tovar tovar = (Tovar) getObject();
			if (id.equals(KOD)) {
        	    tovar.setKod((String) value);
			}else if (id.equals(NAZOV)) {
	            tovar.setNazov((String) value);
			}else if (id.equals(CENA)) {
	            tovar.setCena((BigDecimal) BigDecimal.valueOf((Integer) value));    
			}else if (id.equals(VELKOST)) {
	            tovar.setVelkost((String) value);       
			}else if (id.equals(SKLADOVA_ZASOBA)) {
	            tovar.setSkladovaZasoba((BigDecimal) BigDecimal.valueOf((Integer) value));  
			}else if (id.equals(TOVAR_DRUH_KOD)) {
	            
	            if (tovar.getTovarDruh() == null || !(tovar.getTovarDruh().getKod().equals(value))) {
	            	TovarDruh tovarDruh = (TovarDruh) Helper.readObject(value, TovarDruh.KOD.getLongCaption(), TovarDruh.class, TovarDruh.KOD.getName(), getUnitOfWork(), getSessionObject());
	              	tovar.setTovarDruh(tovarDruh);
	            }
			}else if(id.equals(TOVAR_DRUH_NAZOV)) {
	        	  tovar.getTovarDruh().getNazov();
			}else if (id.equals(DODAVATEL_KOD)) {	    	  
	            if (tovar.getDodavatel() == null || !(tovar.getDodavatel().getKod().equals(value))) {
	            	Dodavatel dodavatel = (Dodavatel) Helper.readObject(value, Dodavatel.KOD.getLongCaption(), Dodavatel.class, Dodavatel.KOD.getName(), getUnitOfWork(), getSessionObject());
	              	tovar.setDodavatel(dodavatel);
	            }
	      	}else if(id.equals(DODAVATEL_NAZOV)) {
        	     	tovar.getDodavatel().getNazov();
	      	}else if(id.equals(DODAVATEL_ICO)) {
	      		   	tovar.getDodavatel().getIco();
		    }         
        } 
	}

	@Override
	public ValuePack getValuePack() {
		Tovar tovar = (Tovar) getObject();
		ValuePack packet = new ValuePack();
		packet.put(KOD, tovar.getKod());
		packet.put(NAZOV, tovar.getNazov());
		packet.put(CENA, tovar.getCena());
		packet.put(VELKOST, tovar.getVelkost());
		packet.put(SKLADOVA_ZASOBA, tovar.getSkladovaZasoba());
		packet.put(TOVAR_DRUH_KOD, tovar.getTovarDruh() != null ? tovar.getTovarDruh().getKod() : null);
		packet.put(TOVAR_DRUH_NAZOV, tovar.getTovarDruh() != null ? tovar.getTovarDruh().getNazov() : null);
		packet.put(DODAVATEL_KOD, tovar.getDodavatel() != null ? tovar.getDodavatel().getKod() : null);
		packet.put(DODAVATEL_NAZOV, tovar.getDodavatel() != null ? tovar.getDodavatel().getNazov() : null);
		packet.put(DODAVATEL_ICO, tovar.getDodavatel() != null ? tovar.getDodavatel().getIco() : null);
		return packet;
	}

	@Override
	public EnabledPack getEnabledPack() {
		return null;
	}

	@Override
	public RequiredPack getRequiredPack() {
		RequiredPack packet = new RequiredPack();
		packet.put(KOD, Tovar.KOD.isRequired());
		packet.put(NAZOV, Tovar.NAZOV.isRequired());
		packet.put(CENA, Tovar.CENA.isRequired());
		packet.put(VELKOST, Tovar.VELKOST.isRequired());
		packet.put(SKLADOVA_ZASOBA, Tovar.SKLADOVA_ZASOBA.isRequired());
		packet.put(TOVAR_DRUH_KOD, Tovar.TOVAR_DRUH.isRequired());
		packet.put(DODAVATEL_KOD, Tovar.DODAVATEL.isRequired());
		return packet;
	}

	@Override
	public void validate() throws Exception {
		Tovar tovar = (Tovar) getObject();
		tovar.validate(getSessionObject());		
	}
	
	//nastavenie kodu dodavatela 
	public ValuePack setDodavatelKod(Object dodavatelKod) throws Exception {
		Tovar tovar = (Tovar) getObject();
	    Dodavatel dodavatel = tovar.getDodavatel();
	    if (dodavatel == null || !(dodavatel.getKod().equals(dodavatelKod))) {        
	       ValuePack packet = new ValuePack();
	       packet.put(DODAVATEL_KOD, dodavatelKod);
	       setValuePack(packet);
	       packet.clear();
	       dodavatel = tovar.getDodavatel();
	       //TODO - zistit, ci to potrebujem, zatial je to tu 
	       if (dodavatel != null) {
	       	  packet.put(DODAVATEL_NAZOV, dodavatel.getNazov());
	       	  packet.put(DODAVATEL_ICO, dodavatel.getIco());
	       }else{
	        	  packet.put(DODAVATEL_NAZOV, null);
	        	  packet.put(DODAVATEL_ICO, null);
	       }
	           return packet;
	       }
	    return null;
	} 
	/*
	//pokus o nastavenie kodu druhu tovaru 
		public ValuePack setTovarDruhKod(Object tovarDruhKod) throws Exception {
			Tovar tovar = (Tovar) getObject();
		    TovarDruh tovarDruh = tovar.getTovarDruh();
		    if (tovarDruh == null || !(tovarDruh.getKod().equals(tovarDruhKod))) {        
		       ValuePack packet = new ValuePack();
		       packet.put(TOVAR_DRUH_KOD, tovarDruhKod);
		       setValuePack(packet);
		       packet.clear();
		       tovarDruh = tovar.getTovarDruh();
		       //TODO - zistit, ci to potrebujem, zatial je to tu 
		       if (tovarDruh != null) {
		       	  packet.put(TOVAR_DRUH_NAZOV, tovarDruh.getNazov());
		       }else{
		        	  packet.put(TOVAR_DRUH_NAZOV, null);
		       }
		           return packet;
		       }
		    return null;
		} 
	*/
	//vytvori
	public static UCTovar create(EclipseLinkSession session) {
	        return new UCTovar (new Tovar(), session);
	    }

	// nacita 
	public static UCTovar read(Object id, EclipseLinkSession session) throws Exception {
	    	Tovar tovar = (Tovar) read(Tovar.class, id, session);
	    	
	    	//toto funguje dobre
	        return new UCTovar(tovar, session);
	    }
	    
	// vymaze
	public static void delete(Object id, EclipseLinkSession session) throws Exception {
	    	DeleteController controller = new DeleteController() {
				@Override
				public void delete(PersistentObject object,	EclipseLinkSession session, UnitOfWork uow)	throws Exception {
					((Tovar) object).delete(session);
				}
	    	};  
	        delete(Tovar.class, id, controller, session);
	    }

}
