package eshop.su.ciselnik.uc;

import eshop.bo.ciselniky.Dodavatel;
import eshop.bo.ciselniky.ObchodnyPartner;
import netball.server.pack.EnabledPack;
import netball.server.pack.Item;
import netball.server.pack.RequiredPack;
import netball.server.pack.ValuePack;
import eshop.bo.ciselniky.Dodavatel;
import netframework.eclipselink.EclipseLinkSession;

public class UCDodavatel extends UCObchodnyPartner {
	
	public static final String ICO = Dodavatel.ICO.getId();

	protected UCDodavatel(Dodavatel dodavatel, EclipseLinkSession session) {
		super(dodavatel, session);
	}
	
	//editacia ICO
	@Override
	protected void setValuePack(ValuePack packet) throws Exception {
		super.setValuePack(packet);
		EnabledPack enabledPack = getEnabledPack();
	    for (int i = 0; i < packet.size(); i++) {
	           Item item = packet.get(i);                
	           Object id = item.getId();
	           Object value = item.getValue();                
	           if (enabledPack != null && !enabledPack.isEnabled(id))
	              continue;
	          Dodavatel dodavatel = (Dodavatel) getObject(); 
	          if (id.equals(ICO)) {
	        	  dodavatel.setIco((String) value);
	          }
	    }         
	}
	
	
	@Override
    public ValuePack getValuePack() {
		Dodavatel dodavatel = (Dodavatel) getObject();
        ValuePack packet = super.getValuePack();
        packet.put(ICO, dodavatel.getIco());
        return packet;
    }
	
	@Override
	public EnabledPack getEnabledPack() {
		EnabledPack packet = new EnabledPack();
		/*packet.put(KOD, false);
		packet.put(NAZOV, false);
		packet.put(TYP, false);
		packet.put(ICO, false);
		*/
		return packet;
	}
	
	//podsvieti mi nazlto
	@Override
	public RequiredPack getRequiredPack() {
		RequiredPack packet = super.getRequiredPack();
		packet.put(ICO, Dodavatel.ICO.isRequired());
		return packet;
	}
	
	public static UCDodavatel create(EclipseLinkSession session) {
        return new UCDodavatel(new Dodavatel(), session);
    }

    // nacita 
    public static UCDodavatel read(Object id, EclipseLinkSession session) throws Exception {
    	Dodavatel dodavatel = (Dodavatel) read(Dodavatel.class, id, session);
        return new UCDodavatel(dodavatel, session);
    }

}
