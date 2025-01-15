package eshop.su.ciselnik.uc;

import java.util.Vector;

import eshop.bo.ciselniky.Adresa;
import netball.server.comparator.StringComparator;
import netball.server.component.XClientTable;
import netball.server.component.table.TableContainer;
import netframework.mediator.ComponentBuilder;
import netframework.mediator.SessionObject;

//pomocne UC trieda na zapisovanie adries do tabulky, nic NEEXTENDUJE!
//TODO - vyskusat umiestnenie kodu aj v UCObchodnyPartner (podobne ako kontakty?)
public class UCObchodnyPartnerAdresa {

	
	public static XClientTable createTableProperty(String id, SessionObject session) {
		XClientTable table = new XClientTable(id);
        table.addColumn(ComponentBuilder.createTableColumn(Adresa.ID.getId(), Adresa.ID, session));
        table.addColumn(ComponentBuilder.createTableColumn(Adresa.ULICA.getId(), Adresa.ULICA, session));
        table.addColumn(ComponentBuilder.createTableColumn(Adresa.CISLO.getId(), Adresa.CISLO, session));
        table.addColumn(ComponentBuilder.createTableColumn(Adresa.MESTO.getId(), Adresa.MESTO, session));
        table.addColumn(ComponentBuilder.createTableColumn(Adresa.PSC.getId(),  Adresa.PSC, session));
        table.getColumn(Adresa.ID.getId()).setVisible(false);
        table.setPrimaryKey(Adresa.ID.getId());
        return table;
    }
	
	public static TableContainer createDataContainer(Vector<Adresa> zakazky) {
   	    String columns[] = new String[] {Adresa.ID.getId(),
   	    								 Adresa.ULICA.getId(),	
   	    		                         Adresa.CISLO.getId(),
   	    		                         Adresa.MESTO.getId(),
   	    		                         Adresa.PSC.getId()
   	    		                         };
   	    TableContainer container = new TableContainer(columns);
		// naplni kontajner
		for (int i = 0; i < zakazky.size(); i++) {
		   Adresa adresa = zakazky.get(i);
		   container.addNewRow();
		   int rowIndex = container.getRowCount() - 1;
		   container.setValueAt(adresa.getId(), rowIndex, Adresa.ID.getId());
		   container.setValueAt(adresa.getUlica(), rowIndex, Adresa.ULICA.getId());
		   container.setValueAt(adresa.getCislo(), rowIndex, Adresa.CISLO.getId());
		   container.setValueAt(adresa.getMesto(), rowIndex, Adresa.MESTO.getId());
		   container.setValueAt(adresa.getPsc(), rowIndex, Adresa.PSC.getId());
		}
		int columnIndex = container.getColumnIndex(Adresa.MESTO.getId());
		container.sort(columnIndex, true, new StringComparator());
		return container;
	}
}
