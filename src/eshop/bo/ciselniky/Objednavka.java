package eshop.bo.ciselniky;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Vector;

import org.eclipse.persistence.zpi.indirection.ValueHolder;
import org.eclipse.persistence.zpi.indirection.ValueHolderInterface;
import org.eclipse.persistence.zpi.sessions.UnitOfWork;

import eshop.bo.common.Helper;
import netframework.bo.PersistentObject;
import netframework.bo.attributes.ConstantAttribute;
import netframework.bo.attributes.ContainerAttribute;
import netframework.bo.attributes.DateAttribute;
import netframework.bo.attributes.FormattedTextAttribute;
import netframework.bo.attributes.NumberAttribute;
import netframework.bo.attributes.RelationAttribute;
import netframework.eclipselink.EclipseLinkSession;
import zelpo.eclipselink.autorizacia.Uzivatel;

public class Objednavka extends PersistentObject {
	
	public static final NumberAttribute ID = new NumberAttribute("Objednavka.id", "id");
	public static final FormattedTextAttribute CISLO_OBJEDNAVKY = new FormattedTextAttribute("Objednavka.cisloObjednavky", "cisloObjednavky");
	public static final DateAttribute DATUM = new DateAttribute("Objednavka.datum", "datum");
	public static final NumberAttribute SUMA = new NumberAttribute("Objednavka.suma", "suma");
	public static final ConstantAttribute STAV_OBJEDNAVKY = new ConstantAttribute("Objednavka.stavObjedavky", "stavObjednavky", StavObjednavky.ZOZNAM);
    public static final RelationAttribute ZAKAZNIK = new RelationAttribute("Objednavka.zakaznik", "zakaznik");
    public static final ContainerAttribute POLOZKY = new ContainerAttribute("Objednavka.polozky", "polozky");
    //nove 
    public static final RelationAttribute POVODNA_OBJEDNAVKA = new RelationAttribute("Objednavka.povodnaObjednavka", "povodnaObjednavka");
    public static final RelationAttribute VYSTAVIL = new RelationAttribute("Objednavka.vystavil", "vystavil");
    
	
    static {
    	CISLO_OBJEDNAVKY.setCaption("Cislo objednavky");
    	CISLO_OBJEDNAVKY.setLongCaption("Cislo objednavky");
    	CISLO_OBJEDNAVKY.setColumnName(CISLO_OBJEDNAVKY.getCaption());
    	CISLO_OBJEDNAVKY.setLongColumnName(CISLO_OBJEDNAVKY.getLongCaption());
    	CISLO_OBJEDNAVKY.setRequired(true);
    	CISLO_OBJEDNAVKY.setMask("####-^-####");
    	
    	DATUM.setCaption("Datum");
    	DATUM.setColumnName("Datum prijatia");
		DATUM.setRequired(true);
		
		SUMA.setCaption("Suma");
		SUMA.setColumnName("Celkova suma objednavky");
		SUMA.setRequired(true);
		SUMA.setZeroAccepted(true);
		SUMA.setLength(8);
		SUMA.setScale(2);
		SUMA.setMask("###,##0.00");
		
		STAV_OBJEDNAVKY.setCaption("Stav objednavky");
		STAV_OBJEDNAVKY.setRequired(true);
		
		ZAKAZNIK.setCaption("Zakaznik");
		ZAKAZNIK.setLongCaption("Zakaznik");
		ZAKAZNIK.setColumnName(ZAKAZNIK.getCaption());
		ZAKAZNIK.setLongColumnName(ZAKAZNIK.getLongCaption());
		ZAKAZNIK.setRequired(true);
		
		POLOZKY.setCaption("Polozky");
		POLOZKY.setRequired(true);
		
		POVODNA_OBJEDNAVKA.setCaption("Povodna objednavka");
		POVODNA_OBJEDNAVKA.setLongCaption("Povodna objednavka");
		POVODNA_OBJEDNAVKA.setColumnName(POVODNA_OBJEDNAVKA.getCaption());
		POVODNA_OBJEDNAVKA.setLongColumnName(POVODNA_OBJEDNAVKA.getLongCaption());
		
		VYSTAVIL.setCaption("Vystavil");
		VYSTAVIL.setRequired(true);
	}
	
	private String cisloObjednavky;
	//nastavenie datumu objednavky na datum vytvorenia
	private Date datum = new Date(System.currentTimeMillis());
	private BigDecimal suma = Helper.DEFAULT_VALUE;
	private StavObjednavky stavObjednavky = StavObjednavky.OTVORENA;
	private ValueHolderInterface zakaznik = new ValueHolder();
	private ValueHolderInterface polozky = new ValueHolder();
	private ValueHolderInterface vystavil = new ValueHolder();
	private ValueHolderInterface povodnaObjednavka = new ValueHolder();
	
	//getter a setter, pracujem uz s existujucou triedou Uzivatel
	public Uzivatel getVystavil() {
		return (Uzivatel) vystavil.getValue();
	}

	public void setVystavil(Uzivatel vystavil) {
		this.vystavil.setValue(vystavil);
	}

	public Vector<ObjednavkaPolozka> getPolozky() {
		if (polozky.getValue() == null)
			polozky.setValue(new Vector<ObjednavkaPolozka>());
		return (Vector<ObjednavkaPolozka>) polozky.getValue();
	}
		
	public String getCisloObjednavky() {
		return cisloObjednavky;
	}
	
	public void setCisloObjednavky(String cisloObjednavky) {
		if (cisloObjednavky != null)
			cisloObjednavky = cisloObjednavky.trim();
		this.cisloObjednavky = cisloObjednavky;
	}
	
	public Date getDatum() {
		return datum;
	}
	/*
	public void setDatum(Date datum) {
		this.datum = datum;
	}
	*/
	public BigDecimal getSuma() {
		return suma;
	}
	
	private void setSuma(BigDecimal suma) {
		this.suma = suma;
	}
	
	public StavObjednavky getStavObjednavky() {
		return stavObjednavky;
	}
	
	protected void setStavObjednavky(StavObjednavky stavObjednavky) {
		this.stavObjednavky = stavObjednavky;
	}
	
	//transformacne pre TopLink	
	public String getStavObjednavkyTL() {
        if (stavObjednavky == null)
            return null;
        return stavObjednavky.getKey();
    }

    public void setStavObjednavkyTL(String stavObjednavky) {
        try {
            this.stavObjednavky = StavObjednavky.convert(stavObjednavky);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
			
	public Zakaznik getZakaznik() {
		return (Zakaznik) zakaznik.getValue();
	}
	
	public void setZakaznik(Zakaznik zakaznik) {
		this.zakaznik.setValue(zakaznik); 
	}
	
	public Objednavka getPovodnaObjednavka() {
		return (Objednavka) povodnaObjednavka.getValue();
	}
	
	public void setPovodnaObjednavka(Objednavka povodnaObjednavka) {
		this.povodnaObjednavka.setValue(povodnaObjednavka); 
	}

	//v metode validate musim pridat parameter UnitOfWork, kvoli atributu vystavil (zaroven potom vsade, kde pouzivam metodu) 
	public void validate(EclipseLinkSession session, UnitOfWork unitOfWork) throws Exception {
       // CISLO_OBJEDNAVKY.checkValue(getCisloObjednavky(), session);
        DATUM.checkValue(getDatum(), session);
        calculateSuma(session);
        SUMA.checkValue(getSuma(), session);
        ZAKAZNIK.checkValue(getZakaznik(), session);
        validateTovarDuplicity(session);
        POLOZKY.checkValue(getPolozky(), session);
        
        //cekujem, ci je pritomny alebo nie je null, ak ano, tak cez UnitOfWork a session pridavam
        //nastavenie uzivatela natvrdo! momentalne mam v appke povedane, ze si mozem vybrat uzivatela cez formular
        if (!isPersisted() || getVystavil() == null) {
        	Uzivatel uzivatel = (Uzivatel) unitOfWork.registerObject(session.getUser());
        	setVystavil(uzivatel);
        }
        //cekujem
        VYSTAVIL.checkValue(getVystavil(), session);
    }
	
	//vypocet sumy v objednavke
	public void calculateSuma(EclipseLinkSession session) throws Exception {
		
		Vector<ObjednavkaPolozka> polozky = this.getPolozky();
		BigDecimal suma = Helper.DEFAULT_VALUE;
		for (int i = 0; i < polozky.size(); i++) {
			ObjednavkaPolozka polozka = polozky.get(i);
			polozka.validate(session);
			suma = suma.add(polozka.getSuma());
		} 
		suma = suma.setScale(Objednavka.SUMA.getScale(), BigDecimal.ROUND_HALF_UP);
		this.setSuma(suma);
	}
	
	//validacia tovarov, aby neboli duplicitne v polozkach
	public void validateTovarDuplicity(EclipseLinkSession session) throws Exception {
		
		Vector<ObjednavkaPolozka> polozky = this.getPolozky();
		
		if (polozky.size() > 0) {
			for (int i = 0; i < polozky.size(); i++) {
				Tovar tovar1 = polozky.get(i).getTovar();
				for (int k = i + 1; k < polozky.size(); k++) {
					Tovar tovar2 = polozky.get(k).getTovar();
				   if (tovar1.equals(tovar2)) {
					   throw new IllegalArgumentException("V objednavke sa nachadza duplicitny tovar!");
				   }
			   }
			}
		}
	}
	
	//delete nastavenie vynimky pre (ne)mazanie dokoncenych objednavok
	public void delete(EclipseLinkSession session) throws Exception {
		if (this.getStavObjednavky().equals(StavObjednavky.DOKONCENA)) {
			throw new IllegalArgumentException("Dokoncenu objednavku nie je mozne vymazat!");
		}
	}
	
	//metody na zmenu stavu a zaroven aj na upravu stavu skladovych zasob tovaru
	public void setDokoncena(EclipseLinkSession session) throws Exception {

		if (!this.getStavObjednavky().equals(StavObjednavky.OTVORENA)) {
			throw new IllegalArgumentException("Len otvorena objednavka moze byt dokoncena!");
		}
		
		Vector<ObjednavkaPolozka> polozky = this.getPolozky();
		
		for (ObjednavkaPolozka objednavkaPolozka : polozky) {
			Tovar tovar = objednavkaPolozka.getTovar();
			if (objednavkaPolozka.getMnozstvo().compareTo(tovar.getSkladovaZasoba()) > 0) {
				throw new IllegalArgumentException("Mnozstvo tovaru " + tovar.getNazov() + " v objednavke je vyssie ako skladova zasoba, objednavka nemoze byt dokoncena!");
			}
			//malo by to teda fungovat aj v jednom cykle
			tovar.setSkladovaZasoba(tovar.getSkladovaZasoba().subtract(objednavkaPolozka.getMnozstvo()));
		} 
				
		this.setStavObjednavky(StavObjednavky.DOKONCENA);
	}

	public void setStornovana(EclipseLinkSession session) throws Exception {
		
		if (!this.getStavObjednavky().equals(StavObjednavky.DOKONCENA)) {
				throw new IllegalArgumentException("Len dokoncena objednavka moze byt stornovana!");
			}	
		/*	
			Vector<ObjednavkaPolozka> polozky = this.getPolozky();
			
			for (ObjednavkaPolozka objednavkaPolozka : polozky) {
				Tovar tovar = objednavkaPolozka.getTovar();
				tovar.setSkladovaZasoba(tovar.getSkladovaZasoba().add(objednavkaPolozka.getMnozstvo()));
			}
		}
		*/
		this.setStavObjednavky(StavObjednavky.STORNOVANA);
	}
	
	//TODO - ked stornujem objednavku, tak sa vytvori dobropis, kod na navysenie skladovych zasob je tu
	public Objednavka stornuj() {
		//System.out.println("this.getStavObjednavky() " + this.getStavObjednavky());
		if (!this.getStavObjednavky().equals(StavObjednavky.DOKONCENA)) {
			throw new IllegalArgumentException("Len dokoncena objednavka moze byt stornovana!");
		}
		
		Objednavka stornoObjednavka = new Objednavka();
		stornoObjednavka.datum = new Date(System.currentTimeMillis());
		stornoObjednavka.setStavObjednavky(StavObjednavky.STORNOVANA);
		stornoObjednavka.setZakaznik(getZakaznik());
		stornoObjednavka.setSuma(getSuma().multiply(new BigDecimal("-1")));
		stornoObjednavka.setPovodnaObjednavka(this);
		Vector<ObjednavkaPolozka> polozky = this.getPolozky();
		for (ObjednavkaPolozka objednavkaPolozka : polozky) {
			stornoObjednavka.getPolozky().add(objednavkaPolozka.stornuj(stornoObjednavka));	
			Tovar tovar = objednavkaPolozka.getTovar();
			tovar.setSkladovaZasoba(tovar.getSkladovaZasoba().add(objednavkaPolozka.getMnozstvo()));
		}
		
		return stornoObjednavka;
	}
	
}
