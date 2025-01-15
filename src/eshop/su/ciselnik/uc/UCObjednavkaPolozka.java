package eshop.su.ciselnik.uc;

import java.math.BigDecimal;
import java.util.Vector;

import eshop.bo.ciselniky.Objednavka;
import eshop.bo.ciselniky.ObjednavkaPolozka;
import eshop.bo.ciselniky.Tovar;
import eshop.bo.ciselniky.Zakaznik;
import eshop.bo.common.Helper;
import eshop.su.komponenty.ComponentTovar;
import netball.server.comparator.StringComparator;
import netball.server.component.XClientTable;
import netball.server.component.table.TableContainer;
import netball.server.pack.EnabledPack;
import netball.server.pack.Item;
import netball.server.pack.RequiredPack;
import netball.server.pack.ValuePack;
import netframework.eclipselink.InsertedItemSession;
import netframework.eclipselink.UCPersistentObjectItem;
import netframework.eclipselink.UpdatedItemSession;
import netframework.mediator.ComponentBuilder;
import netframework.mediator.SessionObject;

public class UCObjednavkaPolozka extends UCPersistentObjectItem {
	
	//ma tu byt aj Suma?
	public static final String JEDNOTKOVA_CENA = ObjednavkaPolozka.JEDNOTKOVA_CENA.getId();
	public static final String MNOZSTVO = ObjednavkaPolozka.MNOZSTVO.getId();
	public static final String SUMA = ObjednavkaPolozka.SUMA.getId();
	public static final String TOVAR_KOD = Tovar.KOD.getId();
	public static final String TOVAR_NAZOV = Tovar.NAZOV.getId();
	
	//toto ked potrebujem novy objekt dostat cez konstruktor?
    public UCObjednavkaPolozka(InsertedItemSession modifyObject) {
    	super(modifyObject);
        ObjednavkaPolozka polozka = (ObjednavkaPolozka) getObject();
        Objednavka objednavka = (Objednavka) modifyObject.getCloneObject();
        polozka.setObjednavka(objednavka);
    }
    
    public UCObjednavkaPolozka(UpdatedItemSession modifyObject) {
    	super(modifyObject);
    }

    //tie polozky, co pocitam tu davam tiez? jednotkovu cenu tiez taham z tovaru
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
        	ObjednavkaPolozka polozka = (ObjednavkaPolozka) getObject();
        	if(id.equals(MNOZSTVO)) {
        		 polozka.setMnozstvo((BigDecimal)value);
        	}else if(id.equals(TOVAR_KOD)) {
        		if (polozka.getTovar() == null || !(polozka.getTovar().getKod().equals(value))) {
	            	Tovar tovar = (Tovar) Helper.readObject(value, Tovar.KOD.getLongCaption(), Tovar.class, Tovar.KOD.getName(), getUnitOfWork(), getSessionObject());
	              	polozka.setTovar(tovar);
        		}
        	}
        }
    }
    
    @Override
    public ValuePack getValuePack() {
    	ObjednavkaPolozka polozka = (ObjednavkaPolozka) getObject();
        ValuePack packet = new ValuePack();
    	packet.put(JEDNOTKOVA_CENA, polozka.getJednotkovaCena());
    	packet.put(MNOZSTVO, polozka.getMnozstvo());
    	//packet.put(SUMA, polozka.getSuma());
    	packet.put(TOVAR_KOD, polozka.getTovar() != null ? polozka.getTovar().getKod() : null);
    	packet.put(TOVAR_NAZOV, polozka.getTovar() != null ? polozka.getTovar().getNazov() : null);
        return packet;
    }

	public ValuePack setTovarKod(Object tovarKod) throws Exception {
    	ObjednavkaPolozka polozka = (ObjednavkaPolozka) getObject();
  	    Tovar tovar = polozka.getTovar();
        if (tovar == null || !(tovar.getKod().equals(tovarKod))) {        
           ValuePack packet = new ValuePack();
           packet.put(TOVAR_KOD, tovarKod);
           setValuePack(packet);
           packet.clear();
           tovar = polozka.getTovar();
           if (tovar != null) {
	       	  packet.put(TOVAR_NAZOV, tovar.getNazov());
	       	  packet.put(JEDNOTKOVA_CENA, tovar.getCena());
           }else{
        	  packet.put(TOVAR_NAZOV, null);
        	  packet.put(JEDNOTKOVA_CENA, null);
           }
           return packet;
        }
        return null;
    } 
    
    
    @Override
    public EnabledPack getEnabledPack() {
    	EnabledPack packet = new EnabledPack();
    	//nechcem aby bolo editovatelne
    	packet.put(TOVAR_NAZOV, false);
    	packet.put(JEDNOTKOVA_CENA, false);
    	return packet;
    }
	
    @Override
    public RequiredPack getRequiredPack() {
    	RequiredPack packet = new RequiredPack();
    	packet.put(MNOZSTVO, ObjednavkaPolozka.MNOZSTVO.isRequired());
    	packet.put(TOVAR_KOD, ObjednavkaPolozka.TOVAR.isRequired());
    	return packet;
    }     
    
    @Override
	public void validate() throws Exception {
    	ObjednavkaPolozka polozka = (ObjednavkaPolozka) getObject();
        polozka.validate(getSession().getSessionObject());
        if (getSession().isNew()) {
             polozka.getObjednavka().getPolozky().add(polozka);
        }   
	}
     
	public static XClientTable createTableProperty(String id, SessionObject session) {
        XClientTable table = new XClientTable(id);
        table.addColumn(ComponentBuilder.createTableColumn(ObjednavkaPolozka.ID.getId(), ObjednavkaPolozka.ID, session));  
        //mozem pracovat aj s tovarom
        table.addColumn(ComponentBuilder.createTableColumn(Tovar.KOD.getId(), Tovar.KOD, false, session));
        table.addColumn(ComponentBuilder.createTableColumn(Tovar.NAZOV.getId(), Tovar.NAZOV, session));
        table.addColumn(ComponentBuilder.createTableColumn(ObjednavkaPolozka.JEDNOTKOVA_CENA.getId(), ObjednavkaPolozka.JEDNOTKOVA_CENA, session));
        table.addColumn(ComponentBuilder.createTableColumn(ObjednavkaPolozka.MNOZSTVO.getId(), ObjednavkaPolozka.MNOZSTVO, session));
        table.addColumn(ComponentBuilder.createTableColumn(ObjednavkaPolozka.SUMA.getId(), ObjednavkaPolozka.SUMA, session));        
        table.getColumn(ObjednavkaPolozka.ID.getId()).setVisible(false);
        table.setPrimaryKey(ObjednavkaPolozka.ID.getId());
        return table;
    }
	
	public static TableContainer createDataContainer(Vector<ObjednavkaPolozka> polozky) {
   	    String columns[] = new String[] {ObjednavkaPolozka.ID.getId(),
   	    								//pracujem s tovarom
   	    								Tovar.KOD.getId(),
   	    								Tovar.NAZOV.getId(),
								        ObjednavkaPolozka.JEDNOTKOVA_CENA.getId(),
								        ObjednavkaPolozka.MNOZSTVO.getId(),
								        ObjednavkaPolozka.SUMA.getId(),
								        };
   	    TableContainer container = new TableContainer(columns);  
		
		for (int i = 0; i < polozky.size(); i++) {
		   ObjednavkaPolozka polozka = polozky.get(i);
		   container.addNewRow();
		   int rowIndex = container.getRowCount() - 1;
		   container.setValueAt(new Integer(polozka.hashCode()), rowIndex,  ObjednavkaPolozka.ID.getId());
		   //pracujem s tovarom
		   container.setValueAt(polozka.getTovar().getKod(), rowIndex, Tovar.KOD.getId());
		   container.setValueAt(polozka.getTovar().getNazov(), rowIndex, Tovar.NAZOV.getId());
		   container.setValueAt(polozka.getJednotkovaCena(), rowIndex, ObjednavkaPolozka.JEDNOTKOVA_CENA.getId());
		   container.setValueAt(polozka.getMnozstvo(), rowIndex, ObjednavkaPolozka.MNOZSTVO.getId());
		   container.setValueAt(polozka.getSuma(), rowIndex, ObjednavkaPolozka.SUMA.getId());
		   }
		//index dam kde?
		int columnIndex = container.getColumnIndex(Tovar.NAZOV.getId());
		container.sort(columnIndex, true, new StringComparator());
		return container;
	}
}
