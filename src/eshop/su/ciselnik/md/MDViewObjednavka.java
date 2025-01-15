package eshop.su.ciselnik.md;

import java.awt.Color;
import java.util.List;

import javax.swing.SwingConstants;

import eshop.bo.ciselniky.StavObjednavky;
import eshop.bo.ciselniky.TypObchodnehoPartnera;
import eshop.su.ciselnik.uc.UCObchodnyPartner;
import eshop.su.ciselnik.uc.UCObjednavka;
import eshop.su.ciselnik.uc.UCTovarDruh;
import eshop.su.common.MDHelper;
import eshop.su.common.report.ReportDokladObjednavkaFlowPanel;
import eshop.su.common.report.ReportDokladObjednavkaSimpleMultiLineGrid;
import eshop.su.common.report.ReportDokladObjednavkaTable;
import eshop.su.common.view.ViewObchodnyPartner;
import eshop.su.common.view.ViewObjednavka;
import netball.server.component.XBoxPanel;
import netball.server.component.XClientServerTable;
import netball.server.component.XMenu;
import netball.server.component.XPanel;
import netball.server.component.XPopupMenuButton;
import netball.server.component.table.ValueTableRowColorModel;
import netball.server.event.ClientActionEvent;
import netball.server.event.ServerActionEvent;
import netball.server.event.alert.YesNoAlert;
import netball.server.pack.EnabledPack;
import netball.server.pack.ServerPack;
import netframework.FrameworkUtilities;
import netframework.access.AccessAction;
import netframework.access.AccessControlObject;
import netframework.eclipselink.EclipseLinkSession;
import netframework.mediator.BasicMediator;
import netframework.mediator.MDReportPreview;
import netframework.mediator.MDTablePreview;
import netframework.mediator.MDUtilities;
import netframework.mediator.MDViewBasicClientServerTable;
import netframework.mediator.MDViewClientServerTable;
import netframework.mediator.MediatorCallBackObject;
import netframework.mediator.MediatorParameters;
import netframework.mediator.ViewTableDataSource;
import netframework.mediator.resource.MediatorResourceBuilder;

public class MDViewObjednavka extends MDViewBasicClientServerTable {

	private final String PRIDAJ_ACTION = createId(MediatorResourceBuilder.ADD_BUTTON);
    private final String OPRAVA_ACTION = createId(MediatorResourceBuilder.CORRECT_BUTTON);
    private final String VYMAZ_ACTION = createId(MediatorResourceBuilder.DELETE_BUTTON);
    private final String TLAC_ACTION = createId(MediatorResourceBuilder.PRINT_BUTTON);
    private final String VYBER_ACTION = createId(MediatorResourceBuilder.READ_BUTTON);
    
    private final String DETAIL_ACTION = createId(MediatorResourceBuilder.DETAIL_BUTTON);
    private final String STAV_ACTION = createId(MDHelper.STAV_BUTTON.getId());
    private final String TLAC_DOKLADU_ACTION = createId(MDHelper.TLAC_DOKLADU_BUTTON.getId());
    
    //buttony na rozkliknutie menu v tlac dokladu
    private final String TLAC_DOKLADU_FLOW_LAYOUT_PANEL_ACTION = "FlowLayoutPanel - moznost 1";
    private final String TLAC_DOKLADU_SIMPLE_MULTI_LINE_GRID_ACTION = "SimpleMultiLineGrid - moznost 2";
    private final String TLAC_DOKLADU_GRID_ACTION = "Grid - moznost 3";
        
    //TODO - tento stav objednavky mozem skusit do MDHelpera, kedze sa mi uz opakuje v dvoch triedach 
    private StavObjednavkyActions stavObjednavkyActions = new StavObjednavkyActions();
	
	public void init(MediatorParameters parameters, ServerPack pack) throws Exception {
		super.init(parameters, pack);
		read(pack, null, null, false);
		addButtonEnabledPack(pack);
	}
	
	@Override
	public AccessAction[] getAccessActions() {
		return new AccessAction[] {
	               getAccessAction(PRIDAJ_ACTION, MediatorResourceBuilder.ADD_BUTTON),
	               getAccessAction(OPRAVA_ACTION, MediatorResourceBuilder.CORRECT_BUTTON),
	               //getAccessAction(VYMAZ_ACTION, MediatorResourceBuilder.DELETE_BUTTON),
	               getAccessAction(DETAIL_ACTION, MediatorResourceBuilder.DETAIL_BUTTON),
	               //musim vytvorit novu - STAV_ACTION si vyberam zo stavObjednavkyActions
	               new AccessAction(STAV_ACTION, MDHelper.STAV_BUTTON.getText(), MDHelper.STAV_BUTTON.getDescription(), stavObjednavkyActions),
	               new AccessAction(TLAC_DOKLADU_ACTION, MDHelper.TLAC_DOKLADU_BUTTON.getText(), MDHelper.TLAC_DOKLADU_BUTTON.getDescription()),
	               };
	}
	
	private AccessAction getAccessAction(String actionCode, String buttonCode) {
    	return MediatorResourceBuilder.createAccessAction(actionCode, buttonCode, getLocale());
    }

	@Override
	protected ViewTableDataSource createDataSource() {
		String[] columns = new String[] {ViewObjednavka.ID,
				ViewObjednavka.CISLO_OBJEDNAVKY,
				ViewObjednavka.DATUM,
				ViewObjednavka.STAV_OBJEDNAVKY,
				ViewObjednavka.ZAKAZNIK_NAZOV,
				ViewObjednavka.SUMA,
				ViewObjednavka.POVODNA_OBJEDNAVKA_CISLO,
				ViewObjednavka.VYSTAVIL
				};
		return new ViewTableDataSource(new ViewObjednavka(), columns, ViewObjednavka.ID, ViewObjednavka.CISLO_OBJEDNAVKY);		
	}

	@Override
	protected String getTitleText() {
		return "Objednavky";
	}

	@Override
	protected XPanel createEastPanel(ServerPack pack) {
    	XBoxPanel panel = new XBoxPanel(SwingConstants.VERTICAL);
    	panel.setSameSizeForAllComponents(true);
    	panel.setGapForAll(5);
    	
        panel.add(createButton(VYBER_ACTION, MediatorResourceBuilder.READ_BUTTON, MDUtilities.READ_ICON, new ServerActionEvent()));
        
        ServerActionEvent event = new ServerActionEvent();
        panel.add(createButton(PRIDAJ_ACTION, MediatorResourceBuilder.ADD_BUTTON, MDUtilities.ADD_ICON, new ServerActionEvent()));

        event = new ServerActionEvent();
    	event.addReturnValue(TABLE);
        panel.add(createButton(OPRAVA_ACTION, MediatorResourceBuilder.CORRECT_BUTTON, MDUtilities.CORRECT_ICON, event));
        
        event = new ServerActionEvent();
    	event.addReturnValue(TABLE);
        panel.add(createButton(DETAIL_ACTION, MediatorResourceBuilder.DETAIL_BUTTON, MDUtilities.DETAIL_ICON, event));
        /*
        event = new ServerActionEvent();
        event.addReturnValue(TABLE);
        event.addAlert(new YesNoAlert(getSessionObject().translateText(eshop.su.common.MDHelper.MESSAGE_VYMAZ_ZAZNAM)));
        panel.add(createButton(VYMAZ_ACTION, MediatorResourceBuilder.DELETE_BUTTON, MDUtilities.DELETE_ICON, event));
        */
        event = new ServerActionEvent();
        event.addReturnValue(TABLE);
        event.addReturnProperty(STAV_ACTION);
        panel.add(createStavButton());
        
        panel.add(createButton(TLAC_ACTION, MediatorResourceBuilder.PRINT_BUTTON, MDUtilities.PRINT_ICON, new ServerActionEvent()));
        
        event = new ServerActionEvent();
        event.addReturnValue(TABLE);
        event.addReturnProperty(TLAC_DOKLADU_ACTION);
        panel.add(createTlacDokladButton());
        
        panel.add(createButton(CANCEL_ACTION, MediatorResourceBuilder.CANCEL_BUTTON, MDUtilities.HOME_ICON, new ServerActionEvent()));
        addButtonEnabledPack(pack);
        return panel;
    }
	
	@Override
	protected EnabledPack createButtonEnabledPack() {
		EnabledPack pack = new EnabledPack();
        if (getRowCount() > 0) {
            pack.put(OPRAVA_ACTION, true);
            pack.put(TLAC_ACTION, true);
            pack.put(VYMAZ_ACTION, true);
            pack.put(DETAIL_ACTION, true);
            pack.put(STAV_ACTION, true);
            pack.put(TLAC_DOKLADU_ACTION, true);
        }else{
            pack.put(OPRAVA_ACTION, false);
            pack.put(TLAC_ACTION, false);
            pack.put(VYMAZ_ACTION, false);
            pack.put(DETAIL_ACTION, false);
            pack.put(STAV_ACTION, false);
            pack.put(TLAC_DOKLADU_ACTION, false);
        }        
		return pack;
	}
	
	private XPopupMenuButton createStavButton() {
    	XPopupMenuButton button = MDUtilities.createPopupMenuButton(STAV_ACTION, MDHelper.STAV_BUTTON, null, getSessionObject());

        XMenu menuItem = new XMenu(stavObjednavkyActions.DOKONCENA_ACTION, "Dokoncena");
        ServerActionEvent event = new ServerActionEvent();
        event.addReturnValue(TABLE);
        event.addAlert(new YesNoAlert(translateText(MDHelper.MESSAGE_DOKONCENA)));                         
        menuItem.addActionEvent(event);
        button.addMenuItem(menuItem);
    	
        menuItem = new XMenu(stavObjednavkyActions.STORNOVANA_ACTION, "Stornovana");
        event = new ServerActionEvent();
        event.addReturnValue(TABLE);
        event.addAlert(new YesNoAlert(translateText(MDHelper.MESSAGE_STORNO)));                         
        menuItem.addActionEvent(event);
        button.addMenuItem(menuItem);
    	
    	return button;
    }
	
	private XPopupMenuButton createTlacDokladButton() {
    	XPopupMenuButton button = MDUtilities.createPopupMenuButton(TLAC_DOKLADU_ACTION, MDHelper.TLAC_DOKLADU_BUTTON, null, getSessionObject());

    	//tu je troska iny sposob pridavanie buttonov do menu ale v zasade to iste ako STAV_BUTTON
    	button.addMenuItem(new XMenu(TLAC_DOKLADU_FLOW_LAYOUT_PANEL_ACTION, "FlowLayoutPanel - moznost 1"));
    	button.addMenuItem(new XMenu(TLAC_DOKLADU_SIMPLE_MULTI_LINE_GRID_ACTION, "SimpleMultiLineGrid - moznost 2"));
    	button.addMenuItem(new XMenu(TLAC_DOKLADU_GRID_ACTION, "Grid - moznost 3"));        
        ServerActionEvent event = new ServerActionEvent();
        event.addReturnValue(TABLE);
        List<XMenu> menuItems = button.getMenu();
        for (int i = 0; i < menuItems.size(); i++) {
        	menuItems.get(i).addActionEvent(event);
        }
        return button;
    }
	
	@Override
	protected XClientServerTable createTable(ServerPack serverPack) {
    	XClientServerTable table = super.createTable(serverPack);
    	ValueTableRowColorModel tableRowColor = new ValueTableRowColorModel();
        tableRowColor.add(ViewObjednavka.STAV_OBJEDNAVKY, StavObjednavky.DOKONCENA.getKey(), null, Color.blue);
        tableRowColor.add(ViewObjednavka.STAV_OBJEDNAVKY, StavObjednavky.STORNOVANA.getKey(), null, Color.red.darker());
        table.setTableCellColorModel(tableRowColor);
        table.setFreezeColumnsAllowed(true);
        table.setVisibilityOfColumnsAllowed(true);
    	return table;
	} 
	/*
	protected void detail(ServerPack pack, Object id) throws Exception {
    	runNext(MDDetailObjednavka.class, new MDDetailObjednavka.Parameters(id), pack);
	}
	*/
	//TODO - poobede doplnit buttony na tlacenie
	@Override
	public void actionEventExecuted(ClientActionEvent event, ServerPack pack) {
        try {
        	if (event.getSourceId().equals(TLAC_DOKLADU_SIMPLE_MULTI_LINE_GRID_ACTION)) {
        		int index = getSelectedIndexFromEvent(event, pack);
        		if (index != -1) {
        			ReportDokladObjednavkaSimpleMultiLineGrid report = new ReportDokladObjednavkaSimpleMultiLineGrid();
        			runNext(MDReportPreview.class, new MDReportPreview.ArrayPageParameters(report.execute(getSessionObject(), getPrimaryKeyValue(index)), report.getPageFormat()), pack);
        		}
        	}else if (event.getSourceId().equals(TLAC_DOKLADU_FLOW_LAYOUT_PANEL_ACTION)) {
        		int index = getSelectedIndexFromEvent(event, pack);
        		if (index != -1) {
        			ReportDokladObjednavkaFlowPanel report = new ReportDokladObjednavkaFlowPanel();
        			runNext(MDReportPreview.class, new MDReportPreview.ArrayPageParameters(report.execute(getSessionObject(), getPrimaryKeyValue(index)), report.getPageFormat()), pack);
        		}
        	}else if (event.getSourceId().equals(TLAC_DOKLADU_GRID_ACTION)) {
        		int index = getSelectedIndexFromEvent(event, pack);
        		if (index != -1) {
        			ReportDokladObjednavkaTable report = new ReportDokladObjednavkaTable();
        			runNext(MDReportPreview.class, new MDReportPreview.ArrayPageParameters(report.execute(getSessionObject(), getPrimaryKeyValue(index)), report.getPageFormat()), pack);
        		}
        	}else if (event.getSourceId().equals(OPRAVA_ACTION)) {            
        		int index = this.getSelectedIndexFromEvent(event, pack);
        		if (index != -1) {
        			Object id = this.getPrimaryKeyValue(index);
        			Object stav = this.getData().getRow(index).getValueAt(this.getData().getColumnIndex(ViewObjednavka.STAV_OBJEDNAVKY));
        			if (stav.equals(StavObjednavky.DOKONCENA.getKey()) || stav.equals(StavObjednavky.STORNOVANA.getKey())) {
        				throw new IllegalArgumentException("Dokoncenu alebo stornovanu objednavku nie je mozne editovat!"); 
        			}        			
        			//funguje, akurat nefunguje zo starymi objednavkmi, kvoli maske
        			runNext(MDObjednavka.class, new MDObjednavka.Parameters(id), pack);
        			this.setSelectedIndex(index);
        		}
        	}else if (event.getSourceId().equals(PRIDAJ_ACTION)) {
        		runNext(MDObjednavka.class, null, pack);
        		        	
        	}/*else if (event.getSourceId().equals(VYMAZ_ACTION)) {             
        		int index = this.getSelectedIndexFromEvent(event, pack);
        		if (index != -1) {
        			Object id = this.getPrimaryKeyValue(index);
        			//TODO tu nemoze byt funkcia na delete
        			UCObjednavka.delete(id, (EclipseLinkSession) this.getSessionObject());
        			this.addDeletedPack(index, pack);
        			this.addButtonEnabledPack(pack);
        		}   
        	}*/else if (event.getSourceId().equals(VYBER_ACTION)) {        	  
        		this.read(pack, null, null, false);
        		this.addButtonEnabledPack(pack);
        	}else if ((event.getSourceId().equals(DETAIL_ACTION))){
        		int index = this.getSelectedIndexFromEvent(event, pack);
        		if (index != -1) {
        			Object id = this.getPrimaryKeyValue(index);
                	runNext(MDDetailObjednavka.class, new MDDetailObjednavka.Parameters(id), pack);
             	  this.setSelectedIndex(index);
                }
        	}else if (event.getSourceId().equals(stavObjednavkyActions.DOKONCENA_ACTION)) {
        		int index = getSelectedIndexFromEvent(event, pack);
        		if (index != -1) {
        			 Object id = getPrimaryKeyValue(index);
               		 UCObjednavka.setDokoncena(getPrimaryKeyValue(index), (EclipseLinkSession) this.getSessionObject());
            		 this.addUpdatedPack(id, index, pack);
        		}
        	}else if (event.getSourceId().equals(stavObjednavkyActions.STORNOVANA_ACTION)) {
        		int index = getSelectedIndexFromEvent(event, pack);
        		if (index != -1) {
        			 Object id = getPrimaryKeyValue(index);
        			 Object stornoId = UCObjednavka.stornuj(id, (EclipseLinkSession) this.getSessionObject());
        			 
        			 //UCObjednavka.setStornovana(id, (EclipseLinkSession) this.getSessionObject());
        			 
               	     this.addUpdatedPack(id, index, pack);
        		}
        	}else if (event.getSourceId().equals(TLAC_ACTION)) {
        		runNext(MDTablePreview.class, new MDTablePreview.Parameters(TABLE, getId()), pack);
        	}else{
            	super.actionEventExecuted(event, pack); 
        	}    
        }catch(Exception e) {
        	addExceptionToPack(e, pack);
        }                           
    }
	
	@Override
    protected boolean receiveCallBack(BasicMediator mediator,  MediatorCallBackObject obj, ServerPack pack) {
		
		if (obj instanceof MDObjednavka.CallBack) {
			MDObjednavka.CallBack callBack = (MDObjednavka.CallBack) obj;
        	if (callBack.isNew) {
        		addInsertedPack(callBack.objednavkaId, pack);          
        		addButtonEnabledPack(pack);
        	}else {
        		addUpdatedPack(callBack.objednavkaId, this.getSelectedIndex(), pack);          
        	}
		}else {
        	return super.receiveCallBack(mediator, obj, pack);
        }  
        return false;        
    }
	
	@Override
    public int getMaxRows() {
    	return 0;
    }
	
	private class StavObjednavkyActions implements AccessControlObject {
		
		final String DOKONCENA_ACTION = createId(StavObjednavky.DOKONCENA.getKey());
		final String STORNOVANA_ACTION = createId(StavObjednavky.STORNOVANA.getKey());
		
	   	public AccessAction[] getAccessActions() {
	   		return new AccessAction[] {
	   				new AccessAction(DOKONCENA_ACTION, StavObjednavky.DOKONCENA.getText()),
	   				new AccessAction(STORNOVANA_ACTION, StavObjednavky.STORNOVANA.getText()),
	   		};
	   	}
    }

}
