package eshop.su.ciselnik.uc;

import java.sql.Date;

import eshop.bo.ciselniky.CisloDokladu;
import eshop.bo.ciselniky.Faktura;
import eshop.bo.ciselniky.Objednavka;
import eshop.bo.ciselniky.Zakaznik;
import eshop.bo.common.Helper;
import netball.server.component.table.TableContainer;
import netball.server.pack.EnabledPack;
import netball.server.pack.Item;
import netball.server.pack.RequiredPack;
import netball.server.pack.ValuePack;
import netframework.bo.PersistentObject;
import netframework.eclipselink.EclipseLinkSession;
import netframework.eclipselink.UCPersistentObject;
import zelpo.eclipselink.autorizacia.Uzivatel;

public class UCFaktura extends UCPersistentObject {
	
	public static final String CISLO_FAKTURY = Faktura.CISLO_FAKTURY.getId();
	public static final String DATUM_DODANIA = Faktura.DATUM_DODANIA.getId();
	public static final String CISLO_OBJEDNAVKY = Objednavka.CISLO_OBJEDNAVKY.getId();
	
	protected UCFaktura(Faktura faktura, EclipseLinkSession session) {
		super(faktura, session);
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
       Faktura faktura = (Faktura) getObject(); 
	      if (id.equals(DATUM_DODANIA)) {
	    	 	faktura.setDatumDodania((Date) value);
	      }else if(id.equals(CISLO_OBJEDNAVKY)) {
	    	  	if (faktura.getObjednavka() == null || !(faktura.getObjednavka().getCisloObjednavky().equals(value))) {
	            	Objednavka objednavka = (Objednavka) Helper.readObject(value, Objednavka.CISLO_OBJEDNAVKY.getLongCaption(), Objednavka.class, Objednavka.CISLO_OBJEDNAVKY.getName(), getUnitOfWork(), getSessionObject());
	            	faktura.setObjednavka(objednavka);
	            } 
        }
     } 
	}

	@Override
	public ValuePack getValuePack() {
		Faktura faktura = (Faktura) getObject();
		ValuePack packet = new ValuePack();
		packet.put(CISLO_FAKTURY, faktura.getCisloFaktury());
		//packet.put(POLOZKY, this.createDataContainerPolozka());
		packet.put(CISLO_OBJEDNAVKY, faktura.getObjednavka() != null ? faktura.getObjednavka().getCisloObjednavky() : null);
		packet.put(DATUM_DODANIA, faktura.getDatumDodania());
		return packet;
	}
	
	@Override
	public EnabledPack getEnabledPack() {
		EnabledPack packet = new EnabledPack();
		//packet.put(CISLO_OBJEDNAVKY, false);
		//packet.put(POVODNA_OBJEDNAVKA_CISLO, false);
		return packet;
	}

	@Override
	public RequiredPack getRequiredPack() {
		RequiredPack packet = new RequiredPack();
		packet.put(CISLO_OBJEDNAVKY, Faktura.OBJEDNAVKA.isRequired());
		packet.put(DATUM_DODANIA, Faktura.DATUM_DODANIA.isRequired());
		return packet;
	}

	@Override
	public void validate() throws Exception {
		Faktura faktura = (Faktura) getObject();
		faktura.validate(getSessionObject(), getUnitOfWork());
		if (faktura.getCisloFaktury() == null)
			CisloDokladu.setCislo(faktura, getUnitOfWork());
	}
	
	//nastavenie cisla objednavky
		public ValuePack setCisloObjednavky(Object cisloObjednavky) throws Exception {
			Faktura faktura = (Faktura) getObject();
	  	    Objednavka objednavka = faktura.getObjednavka();
	        if (objednavka == null || !(objednavka.getCisloObjednavky().equals(cisloObjednavky))) {        
	           ValuePack packet = new ValuePack();
	           packet.put(CISLO_OBJEDNAVKY, cisloObjednavky);
	           setValuePack(packet);
	           packet.clear();
	           objednavka = faktura.getObjednavka();
	           /*if (zakaznik != null) {
		       	  packet.put(ZAKAZNIK_NAZOV, zakaznik.getNazov());
	           }else{
	        	  packet.put(ZAKAZNIK_NAZOV, null);
	           }
	           */
	           return packet;
	        }
	        return null;
	    }
		/*
		public int getPolozkaPocet() {
			System.out.println("toto je (Faktura) getObject() " + (Faktura) getObject());
		       return ((Faktura) getObject()).getObjednavka().getPolozky().size();
		    }
*//*
		    //preverit, ci je tato metoda v UCObjednavkaPolozka ok
		    public TableContainer createDataContainerPolozka() {
		       return UCObjednavkaPolozka.createDataContainer(((Objednavka) getObject()).getPolozky());
		    }
*/		
		//vytvori
	    public static UCFaktura create(EclipseLinkSession session) {
	        return new UCFaktura (new Faktura(), session);
	    }

	    // nacita 
	    public static UCFaktura read(Object id, EclipseLinkSession session) throws Exception {
	    	Faktura faktura = (Faktura) read(Faktura.class, id, session);
	        return new UCFaktura(faktura, session);
	    }

}
