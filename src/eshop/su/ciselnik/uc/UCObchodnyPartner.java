package eshop.su.ciselnik.uc;


import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.eclipse.persistence.zpi.expressions.Expression;
import org.eclipse.persistence.zpi.expressions.ExpressionBuilder;
import org.eclipse.persistence.zpi.sessions.UnitOfWork;

import eshop.bo.ciselniky.Adresa;
import eshop.bo.ciselniky.Dodavatel;
import eshop.bo.ciselniky.Kontakt;
import eshop.bo.ciselniky.ObchodnyPartner;
import eshop.bo.ciselniky.TypObchodnehoPartnera;
import eshop.bo.ciselniky.Zakaznik;
import eshop.su.common.view.ViewObchodnyPartner;
import netball.server.component.table.TableContainer;
import netball.server.pack.EnabledPack;
import netball.server.pack.Item;
import netball.server.pack.RequiredPack;
import netball.server.pack.ValuePack;
import netframework.FrameworkUtilities;
import netframework.bo.PersistentObject;
import netframework.eclipselink.EclipseLinkSession;
import netframework.eclipselink.InsertedItemSession;
import netframework.eclipselink.ModifiedItemSession;
import netframework.eclipselink.UCPersistentObject;
import netframework.eclipselink.UpdatedItemSession;
import netframework.eclipselink.UCPersistentObject.DeleteController;

public abstract class UCObchodnyPartner extends UCPersistentObject {
	
	//nastavim konstanty pre triedu UC? 
	public static final String KOD = ObchodnyPartner.KOD.getId();
	public static final String NAZOV = ObchodnyPartner.NAZOV.getId();
	public static final String TYP = ObchodnyPartner.TYP.getId();
	public static final String KONTAKTY = ObchodnyPartner.KONTAKTY.getId();
	public static final String ICO = Dodavatel.ICO.getId();
	public static final String ADRESY = ObchodnyPartner.ADRESY.getId();

	protected UCObchodnyPartner(ObchodnyPartner obchodnyPartner, EclipseLinkSession session) {
		super(obchodnyPartner, session);
		//Overenie vykomentovane zatial Helper.getObmedzenia(session).getTypFaktury().isMozneEditovat(faktura, session);
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
	          ObchodnyPartner obchodnyPartner = (ObchodnyPartner) getObject();           
	          if (id.equals(KOD)) {
	        	  obchodnyPartner.setKod((String) value);
	          }else if (id.equals(NAZOV)) {
	        	  obchodnyPartner.setNazov((String) value);  
		      }
	    }         
	}	
	
	@Override
	public ValuePack getValuePack() {
		ObchodnyPartner obchodnyPartner = (ObchodnyPartner) getObject();	
		ValuePack packet = new ValuePack();
		packet.put(KOD, obchodnyPartner.getKod());
		packet.put(NAZOV, obchodnyPartner.getNazov());
		packet.put(TYP, obchodnyPartner.getTyp());
		packet.put(KONTAKTY, this.createDataContainerKontakt());
		packet.put(ADRESY, createDataContainerAdresa());
		return packet;
	}

	@Override
	public EnabledPack getEnabledPack() {
		EnabledPack packet = new EnabledPack();
		/*packet.put(KOD, false);
		packet.put(NAZOV, false);
		packet.put(TYP, false);
		*/
		return packet;
	}
	
	//podsvieti mi nazlto
	@Override
	public RequiredPack getRequiredPack() {
		RequiredPack packet = new RequiredPack();
		packet.put(KOD, ObchodnyPartner.KOD.isRequired());
		packet.put(NAZOV, ObchodnyPartner.NAZOV.isRequired());
		//packet.put(TYP, ObchodnyPartner.TYP.isRequired());
		return packet;
	}

	@Override
	public void validate() throws Exception {
		ObchodnyPartner obchodnyPartner = (ObchodnyPartner) getObject();
		obchodnyPartner.validate(getSessionObject());
	}
	
    // -------------------- zaciatok prace s kontaktami ------------------------------------------------

    public UCKontakt getKontakt(Object id) {
    	ObchodnyPartner obchodnyPartner = (ObchodnyPartner) getObject();
        Object kontakt = ModifiedItemSession.getItem(obchodnyPartner.getKontakty(), id);
        return new UCKontakt(new UpdatedItemSession(kontakt, getUnitOfWork(), getSessionObject()));
     }  
    
    public void deleteKontakt(Object id) throws Exception {
    	ObchodnyPartner obchodnyPartner = (ObchodnyPartner) getObject();
        int index = ModifiedItemSession.getItemIndex(obchodnyPartner.getKontakty(), id);
        obchodnyPartner.getKontakty().remove(index);
    }
   
    public UCKontakt createKontakt() {
    	ObchodnyPartner obchodnyPartner = (ObchodnyPartner) getObject();
        return new UCKontakt(new InsertedItemSession(obchodnyPartner, Kontakt.class, ObchodnyPartner.KONTAKTY.getName(), getUnitOfWork(), getSessionObject()));
    }
     
    public int getKontaktPocet() {
       return ((ObchodnyPartner) getObject()).getKontakty().size();
    }

    public TableContainer createDataContainerKontakt() {
    	return UCKontakt.createDataContainer(((ObchodnyPartner) getObject()).getKontakty());
    }
    
    // -------------------- koniec prace s kontaktami ------------------------------------------------
    
    // -------------------- zaciatok prace s adresami ------------------------------------------------

    //podobne ako praca s kontaktom, akurat nerobim s jednym objektom ale so zoznamom
        
    public List<Object> getAdresyIds() {	   	
    	ObchodnyPartner obchodnyPartner = (ObchodnyPartner) getObject();
	    List<Adresa> adresy = obchodnyPartner.getAdresy();
		List<Object> ids = new ArrayList<Object>(adresy.size());
		for (int k = 0; k < adresy.size(); k++) {
		    Adresa adresa = adresy.get(k);
			ids.add(adresa.getId());
		}
		return ids;
	}
    
    public void deleteAdresa(Object id) throws Exception {
    	ObchodnyPartner obchodnyPartner = (ObchodnyPartner) getObject();
    	//tu som OK
        int index = FrameworkUtilities.getItemIndex(obchodnyPartner.getAdresy(), id);
        obchodnyPartner.getAdresy().remove(index);
    }
    
    public void addAdresaIds(Object[] ids) throws Exception {
		if (ids == null)
			return;
		ObchodnyPartner obchodnyPartner  = (ObchodnyPartner) getObject();
		ExpressionBuilder builder = null; 
		for (int i = 0; i < ids.length; i++) {
		   boolean found = false;
		   Vector<Adresa> adresy = obchodnyPartner.getAdresy();
		   for (int k = 0; k < adresy.size(); k++) {
			   Adresa adresa = adresy.get(k);
			   if (adresa.getId().equals(ids[i])) {
				   found = true;
				   break;
			   }
		   }
		   if (!found) {
			   if (builder == null)
				   builder = new ExpressionBuilder();
			   Expression exp = builder.get(Adresa.ID.getName()).equal(ids[i]);
			   Adresa adresa = (Adresa) getUnitOfWork().readObject(Adresa.class, exp);
			   if (adresa != null) {
				   obchodnyPartner.getAdresy().add(adresa);
			   }
		   }
		}
	}   
     
    public int getAdresaPocet() {
       return ((ObchodnyPartner) getObject()).getAdresy().size();
    }

    public TableContainer createDataContainerAdresa() {
    	return UCObchodnyPartnerAdresa.createDataContainer(((ObchodnyPartner) getObject()).getAdresy());
    }
    
    
    // -------------------- koniec prace s adresami ------------------------------------------------ 
    
    
    public static void delete(Object id, EclipseLinkSession session) throws Exception {
    	
    	//tu ale vytvaram Anonymous Inner Class, tj. iba jednu konkretnu instanciu triedy bez mena, ktora implementuje interface DeleteController pod menom controller
    	DeleteController controller = new DeleteController() {
			@Override
			public void delete(PersistentObject object,	EclipseLinkSession session, UnitOfWork uow)	throws Exception {
				
				((ObchodnyPartner) object).delete(session);
			}
    	};
    	   	
    	//funkcia zdedena z PersistentObjectu
        delete(ObchodnyPartner.class, id, controller, session);
    }  
    
}
