package eshop.su.ciselnik.uc;

import java.util.Vector;

import org.eclipse.persistence.zpi.expressions.Expression;
import org.eclipse.persistence.zpi.expressions.ExpressionBuilder;
import org.eclipse.persistence.zpi.queries.ReportQuery;
import org.eclipse.persistence.zpi.queries.ReportQueryResult;
import org.eclipse.persistence.zpi.sessions.UnitOfWork;

import eshop.bo.ciselniky.CisloDokladu;
import eshop.bo.ciselniky.ObchodnyPartner;
import eshop.bo.ciselniky.Objednavka;
import eshop.bo.ciselniky.ObjednavkaPolozka;
import eshop.bo.ciselniky.Zakaznik;
import eshop.bo.common.Helper;
import netball.server.component.table.TableContainer;
import netball.server.pack.EnabledPack;
import netball.server.pack.Item;
import netball.server.pack.RequiredPack;
import netball.server.pack.ValuePack;
import netframework.bo.PersistentObject;
import netframework.eclipselink.EclipseLinkSession;
import netframework.eclipselink.InsertedItemSession;
import netframework.eclipselink.ModifiedItemSession;
import netframework.eclipselink.UCPersistentObject;
import netframework.eclipselink.UpdatedItemSession;
import zelpo.eclipselink.autorizacia.Uzivatel;
import netframework.eclipselink.UCPersistentObject.DeleteController;

public class UCObjednavka extends UCPersistentObject {
	
	//iba tie, s ktorymi idem pracovat tuna, tj. co potrebujem povedat zvonku
	public static final String CISLO_OBJEDNAVKY = Objednavka.CISLO_OBJEDNAVKY.getId();
	//public static final String DATUM = Objednavka.DATUM.getId();
	public static final String POLOZKY = Objednavka.POLOZKY.getId();
	public static final String POVODNA_OBJEDNAVKA_CISLO = Objednavka.POVODNA_OBJEDNAVKA.getId();
	//public static final String STAV_OBJEDNAVKY = Objednavka.STAV_OBJEDNAVKY.getId();
	public static final String ZAKAZNIK_KOD = Zakaznik.KOD.getId();
	//budem potrebovat aj nazov?
	public static final String ZAKAZNIK_NAZOV = Zakaznik.NAZOV.getId();
	//budem potreboval vystavil
	public static final String VYSTAVIL = Objednavka.VYSTAVIL.getId();
		

	protected UCObjednavka(Objednavka objednavka, EclipseLinkSession session) {
		super(objednavka, session);
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
          Objednavka objednavka = (Objednavka) getObject(); 
	      if (id.equals(ZAKAZNIK_KOD)) {
	    	  	//
	            if (objednavka.getZakaznik() == null || !(objednavka.getZakaznik().getKod().equals(value))) {
	            	Zakaznik zakaznik = (Zakaznik) Helper.readObject(value, Zakaznik.KOD.getLongCaption(), Zakaznik.class, Zakaznik.KOD.getName(), getUnitOfWork(), getSessionObject());
	              	objednavka.setZakaznik(zakaznik);
	            }
           }else if(id.equals(VYSTAVIL)) {
        	   //meno? nie ale uzivatelske meno!
        	   //if (objednavka.getVystavil() == null || !(objednavka.getVystavil().getMeno().equals(value))) {
	            	Uzivatel vystavil = (Uzivatel) Helper.readObject(value, Uzivatel.UZIVATELSKE_MENO.getLongCaption(), Uzivatel.class, Uzivatel.UZIVATELSKE_MENO.getName(), getUnitOfWork(), getSessionObject());
	            	//System.out.println("vystavil.getUzivatelskeMeno() " + vystavil.getUzivatelskeMeno());
	            	objednavka.setVystavil(vystavil);
	            	//System.out.println("objednavka.getVystavil().getUzivatelskeMeno(); " + objednavka.getVystavil().getUzivatelskeMeno());
	            	//objednavka.getVystavil().getUzivatelskeMeno();
	            //} 
        	    
           }
          
        } 
	}

	@Override
	public ValuePack getValuePack() {
		Objednavka objednavka = (Objednavka) getObject();
		ValuePack packet = new ValuePack();
		packet.put(CISLO_OBJEDNAVKY, objednavka.getCisloObjednavky());
		packet.put(POLOZKY, this.createDataContainerPolozka());
		//malo by byt ok takto, nie getStavObjednavkyTL
		packet.put(POVODNA_OBJEDNAVKA_CISLO, objednavka.getPovodnaObjednavka() != null ? objednavka.getPovodnaObjednavka().getCisloObjednavky() : null);
		packet.put(ZAKAZNIK_KOD, objednavka.getZakaznik() != null ? objednavka.getZakaznik().getKod() : null);
		packet.put(ZAKAZNIK_NAZOV, objednavka.getZakaznik() != null ? objednavka.getZakaznik().getNazov() : null);
		packet.put(VYSTAVIL, objednavka.getVystavil() != null ? objednavka.getVystavil().getUzivatelskeMeno() : null);
		return packet;
	}

	@Override
	public RequiredPack getRequiredPack() {
		RequiredPack packet = new RequiredPack();
		//cislo objednavky sa uz bude generovat automaticky
		//packet.put(CISLO_OBJEDNAVKY, Objednavka.CISLO_OBJEDNAVKY.isRequired());
		//packet.put(STAV_OBJEDNAVKY, Objednavka.STAV_OBJEDNAVKY.isRequired());
		//tento zakaznik tu bude OK?
		packet.put(ZAKAZNIK_KOD, Objednavka.ZAKAZNIK.isRequired());
		packet.put(VYSTAVIL, Objednavka.VYSTAVIL.isRequired());
		return packet;
	}
	
	@Override
	public void validate() throws Exception {
		Objednavka objednavka = (Objednavka) getObject();
		objednavka.validate(getSessionObject(), getUnitOfWork());
		//cislo k objednavke priradujem az po validacii, tj. ked viem, ze vseto je v poriadku a iba pre nove objednavky
		if (objednavka.getCisloObjednavky() == null)
			CisloDokladu.setCislo(objednavka, getUnitOfWork());
	}
	
	@Override
	public EnabledPack getEnabledPack() {
		EnabledPack packet = new EnabledPack();
		//a uz ho nemozem upravovat
		packet.put(CISLO_OBJEDNAVKY, false);
		packet.put(POVODNA_OBJEDNAVKA_CISLO, false);
		return packet;
	}

	//nastavenie kodu zakaznika
	public ValuePack setZakaznikKod(Object zakaznikKod) throws Exception {
		Objednavka objednavka = (Objednavka) getObject();
  	    Zakaznik zakaznik = objednavka.getZakaznik();
        if (zakaznik == null || !(zakaznik.getKod().equals(zakaznikKod))) {        
           ValuePack packet = new ValuePack();
           packet.put(ZAKAZNIK_KOD, zakaznikKod);
           setValuePack(packet);
           packet.clear();
           zakaznik = objednavka.getZakaznik();
           if (zakaznik != null) {
	       	  packet.put(ZAKAZNIK_NAZOV, zakaznik.getNazov());
           }else{
        	  packet.put(ZAKAZNIK_NAZOV, null);
           }
           return packet;
        }
        return null;
    } 
	
	//nastavenie mena vystavil - potrebujem to?
		public ValuePack setVystavilUzivatelskeMeno(Object vystavilUzivatelskeMeno) throws Exception {
			Objednavka objednavka = (Objednavka) getObject();
	  	    Uzivatel vystavil = objednavka.getVystavil();
	        if (vystavil == null || !(vystavil.getUzivatelskeMeno().equals(vystavilUzivatelskeMeno))) {        
	           ValuePack packet = new ValuePack();
	           packet.put(VYSTAVIL, vystavilUzivatelskeMeno);
	           setValuePack(packet);
	           packet.clear();
	           vystavil = objednavka.getVystavil();
	           
	           return packet;
	        }
	        return null;
	    }
	
	// -------------------- zaciatok prace s polozkami ------------------------------------------------
	// UCObjednavkaPolozka pripravit!
	
    public UCObjednavkaPolozka getPolozka(Object id) {
    	Objednavka objednavka = (Objednavka) getObject();
        Object polozka = ModifiedItemSession.getItem(objednavka.getPolozky(), id);
        return new UCObjednavkaPolozka(new UpdatedItemSession(polozka, getUnitOfWork(), getSessionObject()));
     }  
    
    public void deletePolozka(Object id) throws Exception {
    	Objednavka objednavka = (Objednavka) getObject();
        int index = ModifiedItemSession.getItemIndex(objednavka.getPolozky(), id);
        objednavka.getPolozky().remove(index);
    }
   
    public UCObjednavkaPolozka createPolozka() throws Exception {
    	Objednavka objednavka = (Objednavka) getObject();
        return new UCObjednavkaPolozka(new InsertedItemSession(objednavka, ObjednavkaPolozka.class, Objednavka.POLOZKY.getName(), getUnitOfWork(), getSessionObject()));
    }
    
    public int getPolozkaPocet() {
       return ((Objednavka) getObject()).getPolozky().size();
    }

    //preverit, ci je tato metoda v UCObjednavkaPolozka ok
    public TableContainer createDataContainerPolozka() {
       return UCObjednavkaPolozka.createDataContainer(((Objednavka) getObject()).getPolozky());
    }
    
    // -------------------- koniec prace s polozkami ------------------------------------------------
    
    public static void setDokoncena(Object id, EclipseLinkSession session) throws Exception {      
        UnitOfWork work = session.getSession().acquireUnitOfWork();
        Objednavka objednavka = (Objednavka) read(Objednavka.class, id, session);
        objednavka = (Objednavka) work.registerObject(objednavka);
        objednavka.setDokoncena(session);
        work.commit();
    }
    
    public static void setStornovana(Object id, EclipseLinkSession session) throws Exception {      
        UnitOfWork work = session.getSession().acquireUnitOfWork();
        Objednavka objednavka = (Objednavka) read(Objednavka.class, id, session);
        objednavka = (Objednavka) work.registerObject(objednavka);
        objednavka.setStornovana(session);
        work.commit();
    }
    
    //vytvori
    public static UCObjednavka create(EclipseLinkSession session) {
        return new UCObjednavka (new Objednavka(), session);
    }

    // nacita 
    public static UCObjednavka read(Object id, EclipseLinkSession session) throws Exception {
    	Objednavka objednavka = (Objednavka) read(Objednavka.class, id, session);
        return new UCObjednavka(objednavka, session);
    }
    
    //TODO - ceknut a pripadne dokoncit (vracia cislo stornovaneho dokladu, cize de facto vytvori novu objednavku so status stornovana)
    // vymaze
    public static Object stornuj(Object id, EclipseLinkSession session) throws Exception {
        UnitOfWork work = session.getSession().acquireUnitOfWork();
    	Objednavka objednavka = (Objednavka) read(Objednavka.class, id, session);
    	
    	//TODO kontrola ci bola objednavka uz stornovana
    	
 		ExpressionBuilder builder = new ExpressionBuilder();
		Expression exp = builder.get(Objednavka.POVODNA_OBJEDNAVKA.getName()).equal(objednavka);
		
		ReportQuery query = new ReportQuery();
		query.setReferenceClass(Objednavka.class);
		query.addCount();
		query.setSelectionCriteria(exp);
		//System.out.println("query " + query.toString());
		Vector<ReportQueryResult> result = (Vector<ReportQueryResult>) session.getSession().executeQuery(query);
		//System.out.println("result " + result);
				
		if (result.size() != 0) {
			ReportQueryResult row = result.get(0);
			int pocet = ((Number) row.getByIndex(0)).intValue();
			if (pocet > 0) throw new IllegalArgumentException("Objednavka " + id + " uz bola stornovana!"); 
		}
		    	
        objednavka = (Objednavka) work.registerObject(objednavka);
    	Objednavka stornoObjednavka = objednavka.stornuj();
		CisloDokladu.setCislo(stornoObjednavka, work);
		
		work.registerNewObject(stornoObjednavka);
		work.commit();
		return stornoObjednavka.getId();
    }
    
    
}
