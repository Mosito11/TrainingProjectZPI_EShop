package eshop.su.ciselnik.uc;

import java.math.BigDecimal;
import java.util.Vector;

import eshop.bo.ciselniky.Kontakt;
import eshop.bo.ciselniky.ObchodnyPartner;
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

public class UCKontakt extends UCPersistentObjectItem {

	public static final String MENO = Kontakt.MENO.getId();
	public static final String EMAIL = Kontakt.EMAIL.getId();
	public static final String TELEFON = Kontakt.TELEFON.getId();
	//public static final String OBCHODNY_PARTNER = Kontakt.OBCHODNY_PARTNER.getId();
	
	public UCKontakt(InsertedItemSession modifyObject) {
		super(modifyObject);
		Kontakt kontakt = (Kontakt) getObject();
		ObchodnyPartner obchodnyPartner = (ObchodnyPartner) modifyObject.getCloneObject();
		kontakt.setObchodnyPartner(obchodnyPartner);
	}
	
    public UCKontakt(UpdatedItemSession modifyObject) {
    	super(modifyObject);
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
			Kontakt kontakt = (Kontakt) getObject();
			if (id.equals(MENO)) {
				kontakt.setMeno((String) value);
			}else if(id.equals(EMAIL)) {
				kontakt.setEmail((String) value);
			}else if(id.equals(TELEFON)) {
				kontakt.setTelefon((String) value);
			}
		}
	}
	
	@Override
	public ValuePack getValuePack() {
		Kontakt kontakt = (Kontakt) getObject();
		ValuePack packet = new ValuePack();
		packet.put(MENO, kontakt.getMeno());
		packet.put(EMAIL, kontakt.getEmail());
		packet.put(TELEFON, kontakt.getTelefon());
		//packet.put(OBCHODNY_PARTNER, kontakt.getObchodnyPartner());
		return packet;
	}

	@Override
	public EnabledPack getEnabledPack() {
		return null;
	}

	@Override
	public RequiredPack getRequiredPack() {
		RequiredPack packet = new RequiredPack();
		packet.put(MENO, Kontakt.MENO.isRequired());
		packet.put(EMAIL, Kontakt.EMAIL.isRequired());
		packet.put(TELEFON, Kontakt.TELEFON.isRequired());
		//packet.put(OBCHODNY_PARTNER, Kontakt.OBCHODNY_PARTNER.isRequired());
		return packet;
	}
	
	@Override
	public void validate() throws Exception {
		Kontakt kontakt = (Kontakt) getObject();
		kontakt.validate(getSession().getSessionObject());
		if (getSession().isNew()) {
			kontakt.getObchodnyPartner().getKontakty().add(kontakt);
		}
	}
	
	public static XClientTable createTableProperty(String id, SessionObject session) {
        XClientTable table = new XClientTable(id);
        table.addColumn(ComponentBuilder.createTableColumn(Kontakt.ID.getId(), Kontakt.ID, session));
        table.addColumn(ComponentBuilder.createTableColumn(Kontakt.MENO.getId(), Kontakt.MENO, true, session));
        table.addColumn(ComponentBuilder.createTableColumn(Kontakt.EMAIL.getId(), Kontakt.EMAIL, true, session));
        table.addColumn(ComponentBuilder.createTableColumn(Kontakt.TELEFON.getId(), Kontakt.TELEFON, true, session));
        //table.addColumn(ComponentBuilder.createTableColumn(Kontakt.OBCHODNY_PARTNER.getId(), Kontakt.OBCHODNY_PARTNER, true, session));
        table.getColumn(Kontakt.ID.getId()).setVisible(false);
        table.setPrimaryKey(Kontakt.ID.getId());
        return table;
    }
	
	public static TableContainer createDataContainer(Vector<Kontakt> kontakty) {
   	    String columns[] = new String[] {Kontakt.ID.getId(),
								        Kontakt.MENO.getId(),
								        Kontakt.EMAIL.getId(),
								        Kontakt.TELEFON.getId(),
								        //Kontakt.OBCHODNY_PARTNER.getId()
								        };
   	    TableContainer container = new TableContainer(columns);  
		// naplni kontajner
   	    for (int i = 0; i < kontakty.size(); i++) {
		   Kontakt kontakt = kontakty.get(i);
		   container.addNewRow();
		   int rowIndex = container.getRowCount() - 1;
		   Integer checkInt = new Integer(kontakt.hashCode());
		   container.setValueAt(checkInt, rowIndex,  Kontakt.ID.getId());
		   container.setValueAt(kontakt.getMeno(), rowIndex, Kontakt.MENO.getId());
		   container.setValueAt(kontakt.getEmail(), rowIndex, Kontakt.EMAIL.getId());
		   container.setValueAt(kontakt.getTelefon(), rowIndex, Kontakt.TELEFON.getId());
		   //container.setValueAt(kontakt.getObchodnyPartner(), rowIndex, Kontakt.OBCHODNY_PARTNER.getId());
		}
		//nastavenie indexu
		int columnIndex = container.getColumnIndex(Kontakt.MENO.getId());
		container.sort(columnIndex, true, new StringComparator());
		return container;
	}
	
}
