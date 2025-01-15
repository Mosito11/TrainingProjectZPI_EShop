package eshop.su.ciselnik.uc;

import org.eclipse.persistence.zpi.sessions.UnitOfWork;

import eshop.bo.ciselniky.TovarDruh;
import netball.server.pack.EnabledPack;
import netball.server.pack.Item;
import netball.server.pack.RequiredPack;
import netball.server.pack.ValuePack;
import netframework.bo.PersistentObject;
import netframework.eclipselink.EclipseLinkSession;
import netframework.eclipselink.UCPersistentObject;


public class UCTovarDruh extends UCPersistentObject {
    
    public static final String KOD = TovarDruh.KOD.getId();
    public static final String NAZOV = TovarDruh.NAZOV.getId();
    
    private UCTovarDruh(TovarDruh object, EclipseLinkSession session) {
        super(object, session); 
    }

    @Override
    protected void setValuePack(ValuePack pack) throws Exception { 
        if (pack == null)
           return;
        TovarDruh tovarDruh = (TovarDruh) getObject();   
        EnabledPack enabledPack = getEnabledPack(); 
        for (int i = 0; i < pack.size(); i++) {
           Item item = pack.get(i);                
           Object id = item.getId();
           Object value = item.getValue();                
           if (enabledPack != null && !enabledPack.isEnabled(id))
              continue;
           if (id.equals(KOD)) {
              tovarDruh.setKod((String) value);
           }else if (id.equals(NAZOV)) {
              tovarDruh.setNazov((String) value); 
           }   
        }   
    }
    
    @Override
    public ValuePack getValuePack() {
        TovarDruh tovarDruh = (TovarDruh) getObject();
        ValuePack pack = new ValuePack();
        pack.put(KOD, tovarDruh.getKod());
        pack.put(NAZOV, tovarDruh.getNazov());
        return pack;
    }
    
    @Override
    public RequiredPack getRequiredPack() {
        RequiredPack pack = new RequiredPack();
        pack.put(KOD, TovarDruh.KOD.isRequired());
        pack.put(NAZOV, TovarDruh.NAZOV.isRequired());
        return pack;
    } 
    
    @Override
    public EnabledPack getEnabledPack() {
        return null;
    }

	@Override
	public void validate() throws Exception {
		((TovarDruh) getObject()).validate(getSessionObject());
	}        
    
    // vytvori novy
    public static UCTovarDruh create(EclipseLinkSession session) {
        return new UCTovarDruh(new TovarDruh(), session);
    }

    // nacita 
    public static UCTovarDruh read(Object id, EclipseLinkSession session) throws Exception {
        TovarDruh tovarDruh = (TovarDruh) read(TovarDruh.class, id, session);
        return new UCTovarDruh(tovarDruh, session);
    }   
    
    public static void delete(Object id, EclipseLinkSession session) throws Exception {
    	DeleteController controller = new DeleteController() {
			@Override
			public void delete(PersistentObject object,	EclipseLinkSession session, UnitOfWork uow)	throws Exception {
				((TovarDruh) object).delete(session);
			}
    	};  
        delete(TovarDruh.class, id, controller, session);
    }
}          
