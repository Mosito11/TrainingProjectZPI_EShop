package eshop.su.ciselnik.md;

import java.awt.Color;
import java.util.List;

import javax.swing.SwingConstants;

import eshop.su.common.view.ViewAdresa;
import netball.server.component.XBoxPanel;
import netball.server.component.XClientTable;
import netball.server.component.XPanel;
import netball.server.component.setting.ClientTableSettings;
import netball.server.component.table.ValueTableRowColorModel;
import netball.server.event.ClientActionEvent;
import netball.server.event.ServerActionEvent;
import netball.server.pack.EnabledPack;
import netball.server.pack.ServerPack;
import netball.server.pack.UpdatedPack;
import netball.server.pack.ValuePack;
import netframework.access.AccessAction;
import netframework.mediator.MDOkCancel;
import netframework.mediator.MDUtilities;
import netframework.mediator.MDViewBasicClientTable;
import netframework.mediator.MediatorCallBackObject;
import netframework.mediator.MediatorParameters;
import netframework.mediator.ViewTableDataSource;
import netframework.mediator.resource.MediatorResourceBuilder;

public class MDVyberAdresa extends MDViewBasicClientTable {

	private final String POTVRD_ACTION = createId(MediatorResourceBuilder.OK_BUTTON);
	
	
	@Override
	public AccessAction[] getAccessActions() {
		return null;
	}
	
	//v podstate jasne
	@Override
	public void init(MediatorParameters parameters, ServerPack pack) throws Exception {
		super.init(parameters, pack);
		this.read(pack, null, null, false);
	}

	//zdroj udajov do tabulky je ViewAdresa
	@Override
	protected ViewTableDataSource createDataSource() {
		String[] columns = new String[] {
										ViewAdresa.ID,
										ViewAdresa.ULICA,
										ViewAdresa.CISLO,
										ViewAdresa.MESTO,
										ViewAdresa.PSC
										};
		return new ViewTableDataSource(new ViewAdresa(), columns, ViewAdresa.ID, ViewAdresa.MESTO);
	}
	
	@Override
	protected String getTitleText() {
		return "Adresy - MDVyberAdresa";
	}
	
	//dizajn tabulky s dvomi buttonmi
	@Override
	protected XPanel createEastPanel(ServerPack pack) {
    	XBoxPanel panel = new XBoxPanel(SwingConstants.VERTICAL);
    	panel.setSameSizeForAllComponents(true);
    	panel.setGapForAll(5);
    	ServerActionEvent event = new ServerActionEvent();
    	event.addReturnValue(TABLE);
    	panel.add(createButton(POTVRD_ACTION, MediatorResourceBuilder.OK_BUTTON, MDUtilities.OK_ICON, event));
        panel.add(createButton(CANCEL_ACTION, MediatorResourceBuilder.CANCEL_BUTTON, MDUtilities.HOME_ICON, new ServerActionEvent()));
        addButtonEnabledPack(pack);
        return panel;
    }
	
	//pocet riadkov na POTVRD_ACTION musi byt viac ako 0
	@Override
	protected EnabledPack createButtonEnabledPack() {
		EnabledPack pack = new EnabledPack();
		pack.put(POTVRD_ACTION, getRowCount() > 0);
		return pack;
	}
	
	//klasika, co sa stane po stlaceni buttonu POTVRD_ACTION, vyberam viac Objectov, tj. Objects ids[] hladam
	@Override
	public void actionEventExecuted(ClientActionEvent event, ServerPack pack) {
        try {
            if (event.getSourceId().equals(POTVRD_ACTION)) {      
                Object ids[] = getSelectedPrimaryKeysFromEvent(event, pack);
                if(ids != null){
               	   if (this.sendCallBack(new CallBack(ids), pack))
	                  close(pack);
                }
            }else{
            	super.actionEventExecuted(event, pack);
            }
        }catch(Exception e) {
        	addExceptionToPack(e, pack);
        }                           
    }
	
	//tabulka na oznacovanie vyberu
	@Override
    protected XClientTable createTable(ServerPack serverPack) {
    	XClientTable table = super.createTable(serverPack);
    	table.setSelectionMode(XClientTable.MULTIPLE_INTERVAL_SELECTION);
		return table;
    }
	
	//metoda na oznacovanie riadkov
	public void oznacRiadky(List<Object> ids, ServerPack pack) {
    	ValueTableRowColorModel tableRowColor = new ValueTableRowColorModel();
        if (ids != null && ids.size() > 0) { 
           for (int i = 0; i < ids.size(); i++) {
         	  Object id = ids.get(i);
           	  if (id != null) {
 	             tableRowColor.add(ViewAdresa.ID, id, null, Color.red);
           	  }
 	       } 
        }  
        //a supnem ich do ValuePacku
    	ValuePack valuePack = new ValuePack();
        ClientTableSettings tablePack = new ClientTableSettings();
        tablePack.setTableRowColorModel(tableRowColor);
        //TODO - co je toto?
        tablePack.setClearSelection(false);
        valuePack.put(TABLE, tablePack);
        pack.addUpdatedPack(new UpdatedPack(getId(), valuePack));           	      	      	
    }
	
	//CallBack je array Objectov
	public class CallBack implements MediatorCallBackObject {
		public Object[] adresyIds;
		
		public CallBack(Object[] adresyIds) {
			this.adresyIds = adresyIds;
		}
	}
	
	@Override
	public int getMaxRows() {
		return 0;
	}

}
