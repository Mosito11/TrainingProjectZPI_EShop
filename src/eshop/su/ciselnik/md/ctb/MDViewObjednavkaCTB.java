package eshop.su.ciselnik.md.ctb;

import java.awt.Color;
import java.util.List;

import eshop.bo.ciselniky.StavObjednavky;
import eshop.bo.ciselniky.TypObchodnehoPartnera;
import eshop.su.ciselnik.md.MDObjednavka;
import eshop.su.ciselnik.md.MDObjednavka.CallBack;
import eshop.su.ciselnik.md.MDObjednavka.Parameters;
import eshop.su.ciselnik.uc.UCObjednavka;
import eshop.su.common.MDHelper;
import eshop.su.common.report.ReportDokladObjednavkaFlowPanel;
import eshop.su.common.report.ReportDokladObjednavkaSimpleMultiLineGrid;
import eshop.su.common.report.ReportDokladObjednavkaTable;
import eshop.su.common.view.ViewObjednavka;
import netball.server.component.XClientServerTable;
import netball.server.component.XComponent;
import netball.server.component.XMenu;
import netball.server.component.XToolBarPopupMenuButton;
import netball.server.component.table.ValueTableRowColorModel;
import netball.server.event.ClientActionEvent;
import netball.server.event.ServerActionEvent;
import netball.server.event.alert.YesNoAlert;
import netball.server.pack.EnabledPack;
import netball.server.pack.ServerPack;
import netframework.access.AccessAction;
import netframework.access.AccessControlObject;
import netframework.eclipselink.EclipseLinkSession;
import netframework.mediator.BasicMediator;
import netframework.mediator.MDReportPreview;
import netframework.mediator.MDUtilities;
import netframework.mediator.MediatorCallBackObject;
import netframework.mediator.ViewTableDataSource;
import netframework.mediator.ctb.MDViewClientServerTableCTB;
import netframework.mediator.resource.MediatorResourceBuilder;

public class MDViewObjednavkaCTB extends MDViewClientServerTableCTB {

	private final String TLAC_ACTION = createId(MediatorResourceBuilder.PRINT_BUTTON);
    private final String VYBER_ACTION = createId(MediatorResourceBuilder.READ_BUTTON);
    
    //private final String DETAIL_ACTION = createId(MediatorResourceBuilder.DETAIL_BUTTON);
    private final String STAV_ACTION = createId(MDHelper.STAV_BUTTON.getId());
    private final String TLAC_DOKLADU_ACTION = createId(MDHelper.TLAC_DOKLADU_BUTTON.getId());
    
    //buttony na rozkliknutie menu v tlac dokladu
    private final String TLAC_DOKLADU_FLOW_LAYOUT_PANEL_ACTION = "FlowLayoutPanel - moznost 1";
    private final String TLAC_DOKLADU_SIMPLE_MULTI_LINE_GRID_ACTION = "SimpleMultiLineGrid - moznost 2";
    private final String TLAC_DOKLADU_GRID_ACTION = "Grid - moznost 3";
    
    private StavObjednavkyActions stavObjednavkyActions = new StavObjednavkyActions();
	
    @Override
	public AccessAction[] getAccessActions() {
		return new AccessAction[] {
	               //getAccessAction(PRIDAJ_ACTION, MediatorResourceBuilder.ADD_BUTTON),
	               //getAccessAction(OPRAVA_ACTION, MediatorResourceBuilder.CORRECT_BUTTON),
	               //getAccessAction(VYMAZ_ACTION, MediatorResourceBuilder.DELETE_BUTTON),
	               //getAccessAction(DETAIL_ACTION, MediatorResourceBuilder.DETAIL_BUTTON),
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
	protected void insert(ServerPack pack) throws Exception {
		runNext(MDObjednavka.class, null, pack);
		
	}

	@Override
	protected void update(ServerPack pack, Object id) throws Exception {
		runNext(MDObjednavka.class, new MDObjednavka.Parameters(id), pack);
		
	}
	
	@Override
	protected void detail(ServerPack pack, Object id) throws Exception {
    	runNext(MDObjednavka.class, new MDObjednavka.Parameters(id), pack);
	}

	@Override
	protected boolean installDetailButton() {
		return true;
	}

	@Override
	protected void delete(Object id) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected boolean installDeleteButton() {
		return false;
	}
	
	@Override
	protected void addButtonsToToolBar(List<XComponent> components) {
    	ServerActionEvent event = new ServerActionEvent(getId());
        event.addReturnValue(TABLE);
        event.addReturnProperty(STAV_ACTION);
        components.add(createStavButton());

        event = new ServerActionEvent(getId());
        event.addReturnValue(TABLE);
        
        components.add(createTlacDokladButton());
        event.addReturnProperty(TLAC_DOKLADU_ACTION);
    }

	private XToolBarPopupMenuButton createStavButton() {
		XToolBarPopupMenuButton button = MDUtilities.createToolBarPopupMenuButton(STAV_ACTION, MDHelper.STAV_BUTTON, null, getSessionObject());

        XMenu menuItem = new XMenu(stavObjednavkyActions.DOKONCENA_ACTION, translateText(StavObjednavky.DOKONCENA.getText()));
        ServerActionEvent event = new ServerActionEvent(getId());
        event.addReturnValue(TABLE);
        event.addAlert(new YesNoAlert(translateText(MDHelper.MESSAGE_DOKONCENA)));                         
        menuItem.addActionEvent(event);
        button.addMenuItem(menuItem);
    	
        menuItem = new XMenu(stavObjednavkyActions.STORNOVANA_ACTION, translateText(StavObjednavky.STORNOVANA.getText()));
        event = new ServerActionEvent(getId());
        event.addReturnValue(TABLE);
        event.addAlert(new YesNoAlert(translateText(MDHelper.MESSAGE_STORNO)));                         
        menuItem.addActionEvent(event);
        button.addMenuItem(menuItem);
    	
    	return button;
    }
	
	private XToolBarPopupMenuButton createTlacDokladButton() {
    	XToolBarPopupMenuButton button = MDUtilities.createToolBarPopupMenuButton(TLAC_DOKLADU_ACTION, MDHelper.TLAC_DOKLADU_BUTTON, null, getSessionObject());
    	
    	button.addMenuItem(new XMenu(TLAC_DOKLADU_FLOW_LAYOUT_PANEL_ACTION, "FlowLayoutPanel"));
    	button.addMenuItem(new XMenu(TLAC_DOKLADU_SIMPLE_MULTI_LINE_GRID_ACTION, "SimpleMultiLineGrid"));
    	button.addMenuItem(new XMenu(TLAC_DOKLADU_GRID_ACTION, "Grid"));        
        ServerActionEvent event = new ServerActionEvent(getId());
        event.addReturnValue(TABLE);
        List<XMenu> menuItems = button.getMenu();
        for (int i = 0; i < menuItems.size(); i++) {
        	menuItems.get(i).addActionEvent(event);
        }
        return button;
    }	
	
	@Override
    protected EnabledPack createButtonEnabledPack() {
        EnabledPack pack = super.createButtonEnabledPack();
        if (getRowCount() > 0) {
            pack.put(STAV_ACTION, true);
            pack.put(TLAC_DOKLADU_ACTION, true);
        }else{
            pack.put(STAV_ACTION, false);
            pack.put(TLAC_DOKLADU_ACTION, false);
        }           
        return pack;  
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
        			 UCObjednavka.stornuj(id, (EclipseLinkSession) this.getSessionObject());
               	     this.addUpdatedPack(id, index, pack);
        		}
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
        		addUpdatedPack(callBack.objednavkaId, getSelectedIndex(), pack);          
        	} 
        }else {
        	return super.receiveCallBack(mediator, obj, pack);
        }  
        return false;        
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
