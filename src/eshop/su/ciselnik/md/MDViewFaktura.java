package eshop.su.ciselnik.md;

import java.util.List;

import javax.swing.SwingConstants;

import eshop.bo.ciselniky.StavObjednavky;
import eshop.su.ciselnik.uc.UCObjednavka;
import eshop.su.common.MDHelper;
import eshop.su.common.report.ReportDokladFakturaFlowPanel;
import eshop.su.common.report.ReportDokladFakturaSimpleMultiLineGrid;
import eshop.su.common.report.ReportDokladFakturaTable;
import eshop.su.common.report.ReportDokladObjednavkaFlowPanel;
import eshop.su.common.report.ReportDokladObjednavkaSimpleMultiLineGrid;
import eshop.su.common.report.ReportDokladObjednavkaTable;
import eshop.su.common.view.ViewFaktura;
import eshop.su.common.view.ViewObjednavka;
import netball.server.component.XBoxPanel;
import netball.server.component.XMenu;
import netball.server.component.XPanel;
import netball.server.component.XPopupMenuButton;
import netball.server.event.ClientActionEvent;
import netball.server.event.ServerActionEvent;
import netball.server.pack.EnabledPack;
import netball.server.pack.ServerPack;
import netframework.access.AccessAction;
import netframework.eclipselink.EclipseLinkSession;
import netframework.mediator.BasicMediator;
import netframework.mediator.MDReportPreview;
import netframework.mediator.MDTablePreview;
import netframework.mediator.MDUtilities;
import netframework.mediator.MDViewBasicClientServerTable;
import netframework.mediator.MediatorCallBackObject;
import netframework.mediator.MediatorParameters;
import netframework.mediator.ViewTableDataSource;
import netframework.mediator.resource.MediatorResourceBuilder;

public class MDViewFaktura extends MDViewBasicClientServerTable {
	
	private final String PRIDAJ_ACTION = createId(MediatorResourceBuilder.ADD_BUTTON);
    private final String OPRAVA_ACTION = createId(MediatorResourceBuilder.CORRECT_BUTTON);
    //nebude moznost vymazavat faktury
    //private final String VYMAZ_ACTION = createId(MediatorResourceBuilder.DELETE_BUTTON);
    private final String TLAC_ACTION = createId(MediatorResourceBuilder.PRINT_BUTTON);
    private final String VYBER_ACTION = createId(MediatorResourceBuilder.READ_BUTTON);
    
    private final String DETAIL_ACTION = createId(MediatorResourceBuilder.DETAIL_BUTTON);
    //stav faktury neriesim
    //private final String STAV_ACTION = createId(MDHelper.STAV_BUTTON.getId());
    private final String TLAC_DOKLADU_ACTION = createId(MDHelper.TLAC_DOKLADU_BUTTON.getId());
    
    //buttony na rozkliknutie menu v tlac dokladu
    private final String TLAC_DOKLADU_FLOW_LAYOUT_PANEL_ACTION = "FlowLayoutPanel - moznost 1";
    private final String TLAC_DOKLADU_SIMPLE_MULTI_LINE_GRID_ACTION = "SimpleMultiLineGrid - moznost 2";
    private final String TLAC_DOKLADU_GRID_ACTION = "Grid - moznost 3";

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
	               new AccessAction(TLAC_DOKLADU_ACTION, MDHelper.TLAC_DOKLADU_BUTTON.getText(), MDHelper.TLAC_DOKLADU_BUTTON.getDescription()),
	               };
	}
	
	private AccessAction getAccessAction(String actionCode, String buttonCode) {
		return MediatorResourceBuilder.createAccessAction(actionCode, buttonCode, getLocale());
	}
	
	@Override
	protected ViewTableDataSource createDataSource() {
		String[] columns = new String[] {ViewFaktura.ID,
				ViewFaktura.CISLO_FAKTURY,
				ViewFaktura.OBJEDNAVKA,
				ViewFaktura.DATUM_DODANIA,
				ViewFaktura.DATUM_VYSTAVENIA,
				ViewFaktura.DATUM_SPLATNOSTI,
				};
		return new ViewTableDataSource(new ViewFaktura(), columns, ViewFaktura.ID, ViewFaktura.CISLO_FAKTURY);		
	}
		
	@Override
	protected String getTitleText() {
		return "Faktury";
	}
	
	@Override
	protected EnabledPack createButtonEnabledPack() {
		EnabledPack pack = new EnabledPack();
        if (getRowCount() > 0) {
            pack.put(OPRAVA_ACTION, true);
            pack.put(TLAC_ACTION, true);
            pack.put(DETAIL_ACTION, true);
            pack.put(TLAC_DOKLADU_ACTION, true);
        }else{
            pack.put(OPRAVA_ACTION, false);
            pack.put(TLAC_ACTION, false);
            pack.put(DETAIL_ACTION, false);
            pack.put(TLAC_DOKLADU_ACTION, false);
        }        
		return pack;
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
        
        panel.add(createButton(TLAC_ACTION, MediatorResourceBuilder.PRINT_BUTTON, MDUtilities.PRINT_ICON, new ServerActionEvent()));
        
        event = new ServerActionEvent();
        event.addReturnValue(TABLE);
        event.addReturnProperty(TLAC_DOKLADU_ACTION);
        panel.add(createTlacDokladButton());
        
        panel.add(createButton(CANCEL_ACTION, MediatorResourceBuilder.CANCEL_BUTTON, MDUtilities.HOME_ICON, new ServerActionEvent()));
        addButtonEnabledPack(pack);
        return panel;
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
		public void actionEventExecuted(ClientActionEvent event, ServerPack pack) {
	        try {
	        	if (event.getSourceId().equals(TLAC_DOKLADU_SIMPLE_MULTI_LINE_GRID_ACTION)) {
	        		int index = getSelectedIndexFromEvent(event, pack);
	        		if (index != -1) {
	        			ReportDokladFakturaSimpleMultiLineGrid report = new ReportDokladFakturaSimpleMultiLineGrid();
	        			runNext(MDReportPreview.class, new MDReportPreview.ArrayPageParameters(report.execute(getSessionObject(), getPrimaryKeyValue(index)), report.getPageFormat()), pack);
	        		}
	        	}else if (event.getSourceId().equals(TLAC_DOKLADU_FLOW_LAYOUT_PANEL_ACTION)) {
	        		int index = getSelectedIndexFromEvent(event, pack);
	        		if (index != -1) {
	        			ReportDokladFakturaFlowPanel report = new ReportDokladFakturaFlowPanel();
	        			runNext(MDReportPreview.class, new MDReportPreview.ArrayPageParameters(report.execute(getSessionObject(), getPrimaryKeyValue(index)), report.getPageFormat()), pack);
	        		}
	        	}else if (event.getSourceId().equals(TLAC_DOKLADU_GRID_ACTION)) {
	        		int index = getSelectedIndexFromEvent(event, pack);
	        		if (index != -1) {
	        			ReportDokladFakturaTable report = new ReportDokladFakturaTable();
	        			runNext(MDReportPreview.class, new MDReportPreview.ArrayPageParameters(report.execute(getSessionObject(), getPrimaryKeyValue(index)), report.getPageFormat()), pack);
	        		}
	        	}else if (event.getSourceId().equals(OPRAVA_ACTION)) {            
	        		int index = this.getSelectedIndexFromEvent(event, pack);
	        		if (index != -1) {
	        			Object id = this.getPrimaryKeyValue(index);
	        			/*Object stav = this.getData().getRow(index).getValueAt(this.getData().getColumnIndex(ViewObjednavka.STAV_OBJEDNAVKY));
	        			if (stav.equals(StavObjednavky.DOKONCENA.getKey()) || stav.equals(StavObjednavky.STORNOVANA.getKey())) {
	        				throw new IllegalArgumentException("Dokoncenu alebo stornovanu objednavku nie je mozne editovat!"); 
	        			}        			
	        			//funguje, akurat nefunguje zo starymi objednavkmi, kvoli maske*/
	        			runNext(MDFaktura.class, new MDFaktura.Parameters(id), pack);
	        			this.setSelectedIndex(index);
	        		}
	        	}else if (event.getSourceId().equals(PRIDAJ_ACTION)) {
	        		runNext(MDFaktura.class, null, pack);
	        		        	
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
	                	runNext(MDDetailFaktura.class, new MDDetailFaktura.Parameters(id), pack);
	             	  this.setSelectedIndex(index);
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
			
			if (obj instanceof MDFaktura.CallBack) {
				MDFaktura.CallBack callBack = (MDFaktura.CallBack) obj;
	        	if (callBack.isNew) {
	        		addInsertedPack(callBack.fakturaId, pack);          
	        		addButtonEnabledPack(pack);
	        	}else {
	        		addUpdatedPack(callBack.fakturaId, this.getSelectedIndex(), pack);          
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

}
