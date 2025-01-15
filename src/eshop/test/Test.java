package eshop.test;

import java.io.FileWriter;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.Vector;

import org.eclipse.persistence.zpi.expressions.Expression;
import org.eclipse.persistence.zpi.expressions.ExpressionBuilder;
import org.eclipse.persistence.zpi.sessions.DatabaseSession;
import org.eclipse.persistence.zpi.sessions.UnitOfWork;

import eshop.bo.ciselniky.Adresa;
import eshop.bo.ciselniky.Dodavatel;
import eshop.bo.ciselniky.Kontakt;
import eshop.bo.ciselniky.ObchodnyPartner;
import eshop.bo.ciselniky.Objednavka;
import eshop.bo.ciselniky.ObjednavkaPolozka;
import eshop.bo.ciselniky.StavObjednavky;
import eshop.bo.ciselniky.Tovar;
import eshop.bo.ciselniky.TovarDruh;
import eshop.bo.ciselniky.Zakaznik;
import eshop.mapping.EShopProjekt;
import eshop.mapping.VyvojLogin;
import netframework.eclipselink.EclipseLinkSession;

public class Test {

	public static void main(String...strings) {
		
		try {
			//hlavna metoda, vytvaram instanciu projektu z package mapping cez VyvojLogin a getLogin null
			EShopProjekt projekt = new EShopProjekt(new VyvojLogin().getLogin(null));
			
			//EclipseLinkSession zpi objekt, vytvara session a pod nim aj DatabaseSession
			EclipseLinkSession session = new EclipseLinkSession(); 
			session.setSession(projekt.createDatabaseSession());
			((DatabaseSession) session.getSession()).login();
						
			//zapisovanie poznamok
			session.getSession().setLog(new FileWriter("TopLink.log"));
			session.getSession().setLogLevel(2);
			
			//new Test().insertTovarDruh(session);
			//Test.insertTovarDruh(session);
			//insertDodavatel(session);
			//updateDodavatel(21, session);
			//deleteDodavatel(11, session);
			//insertTovar(343, 11, session);
			//updateTovar(11, session);
			//updateTovarDruh(1, session);
			//updateTovarDruh1(1, session);
			//Test.deleteTovarDruh(363, session);
			//insertKontakt(61, session);
			//updateKontakt(12, session);
			//insertZakaznik(2, 12, session);
			//insertAdresa(session);
			//insertObjednavka(101, session);
			//insertObjednavkaPolozka(31, 101, session);
			//upDateObjednavka(371, session);
			//setObjednavkaDokoncena(371, session);
			//setObjednavkaStornovana(351, session);
			//readDodavatel(21, session);
			
					
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 
	
	
	//metoda na insertovanie do tabulky danej triedy, potrebujem session
	private static void insertTovarDruh(EclipseLinkSession session) throws Exception {
		//objekt UnitOfWork mi umoznuje pracovat s tabulkou, najprv sa pripojim		
		UnitOfWork unitOfWork = session.getSession().acquireUnitOfWork();
		//vytvorim si instanciu, ktoru chcem insertnut
		TovarDruh druh = new TovarDruh();
		druh.setKod("N17");
		druh.setNazov("NohaviceDalsie 2  ");
		//validujem
		druh.validate(session);
		//vkladam instanciu
		unitOfWork.registerNewObject(druh);
		//ukoncujem pracu s UnitOfWork
		unitOfWork.commit();
	}
	
	//metoda na updateovanie do tabulky danej triedy, potrebujem session a id instancie, ktoru chcem updateovat
	private static void updateTovarDruh(Integer id, DatabaseSession session) {
		//opat sa pripojim cez UnitOfWork (kod nizsie je pre DatabaseSession, nie EclipseLinkSession)
		UnitOfWork unitOfWork = session.acquireUnitOfWork();
		//vytvaram si instanciu ExpressionBuilder aby som vedel precitat riadok v tabulke podla id
		ExpressionBuilder builder = new ExpressionBuilder();
		Expression exp = builder.get("id").equal(id);
		//vytvorim si instanciu triedy a do nej vlozim precitany riadok
		TovarDruh druh = (TovarDruh) session.readObject(TovarDruh.class, exp);
		//prejdem do kopie cez UnitOfWork
		TovarDruh druhClone = (TovarDruh) unitOfWork.readObject(druh);
		//zadam zmenu
		druhClone.setKod("X");
		//ukoncujem pracu s UnitOfWork
		unitOfWork.commit();
	}
	
	//metoda na updateovanie do tabulky danej triedy, potrebujem session a id instancie, ktoru chcem updateovat
	private static void updateTovarDruh1(Integer id, DatabaseSession session) {
		//opat sa pripojim cez UnitOfWork (kod nizsie je pre DatabaseSession, nie EclipseLinkSession)
		UnitOfWork unitOfWork = session.acquireUnitOfWork();
		//vytvaram si instanciu ExpressionBuilder aby som vedel precitat riadok v tabulke podla id
		ExpressionBuilder builder = new ExpressionBuilder();
		Expression exp = builder.get("id").equal(id);
		//to iste ako v kode vyssie, akurat pracujem iba s jednou instanciou triedy
		TovarDruh druh = (TovarDruh) session.readObject(TovarDruh.class, exp);
		druh = (TovarDruh) unitOfWork.readObject(druh);
		druh.setKod("U");
		unitOfWork.commit();
	}	

	private static void updateTovarDruh2(Integer id, DatabaseSession session) {
		UnitOfWork unitOfWork = session.acquireUnitOfWork();
		ExpressionBuilder builder = new ExpressionBuilder();
		Expression exp = builder.get("id").equal(id);
		TovarDruh druh = (TovarDruh) unitOfWork.readObject(TovarDruh.class, exp);
		//druh = (TovarDruh) unitOfWork.readObject(druh);
		druh.setKod("U");
		unitOfWork.commit();
	}	

	//metoda na deletovanie do tabulky danej triedy, potrebujem session a id instancie, ktoru chcem vymazat
	private static void deleteTovarDruh(Integer id, EclipseLinkSession session) {
		UnitOfWork unitOfWork = session.getSession().acquireUnitOfWork();
		ExpressionBuilder builder = new ExpressionBuilder();
		Expression exp = builder.get("id").equal(id);
		//precitam a vymazem napriamo, nie kopiu
		TovarDruh druh = (TovarDruh) session.getSession().readObject(TovarDruh.class, exp);
		unitOfWork.deleteObject(druh);
		unitOfWork.commit();
	}	
	
	private static void insertTovar(Integer idTovarDruh, Integer idDodavatel, EclipseLinkSession session) throws Exception {
				
			UnitOfWork unitOfWork = session.getSession().acquireUnitOfWork();
			//nemusim teoreticky robit klon
			unitOfWork.addReadOnlyClass(TovarDruh.class);
			unitOfWork.addReadOnlyClass(Dodavatel.class);
			
			Tovar tovar = new Tovar();
			
			ExpressionBuilder builder = new ExpressionBuilder();
			Expression exp1 = builder.get("id").equal(idTovarDruh);
			Expression exp2 = builder.get("id").equal(idDodavatel);
			
			TovarDruh druh = (TovarDruh) session.getSession().readObject(TovarDruh.class, exp1);
			Dodavatel dodavatel = (Dodavatel) session.getSession().readObject(Dodavatel.class, exp2);
			//TovarDruh druhClone = (TovarDruh) unitOfWork.readObject(druh);
			
			tovar.setKod("T17");
			tovar.setNazov("Gate s dodavatelom  ");
			tovar.setCena(new BigDecimal("22.78")); 
			tovar.setVelkost("XS");
			tovar.setSkladovaZasoba(new BigDecimal(10.0));
			tovar.setTovarDruh(druh);
			tovar.setDodavatel(dodavatel);
			
			tovar.validate(session);
			
			unitOfWork.registerNewObject(tovar);
			
			unitOfWork.commit();
		}
		
		//zmena stavu objednavky Otvorena -> Dokoncena 
		private static void setObjednavkaDokoncena (Integer id, EclipseLinkSession session) throws Exception {
			
			UnitOfWork unitOfWork = session.getSession().acquireUnitOfWork();
			ExpressionBuilder builder = new ExpressionBuilder();
			Expression exp = builder.get("id").equal(id);
			Objednavka objednavka = (Objednavka) unitOfWork.readObject(Objednavka.class, exp);
						
			if (!objednavka.getStavObjednavky().equals(StavObjednavky.OTVORENA)) {
				throw new IllegalArgumentException("Len otvorena objednavka moze byt dokoncena!");
			}
			
			//zmena stavu skladovych zasob v pripade, ze objednavka meni stav z Otvorena na Dokoncena
			Vector<ObjednavkaPolozka> polozky = objednavka.getPolozky();
						
			for (int i=0; i < polozky.size(); i++) {
				Tovar tovarVPolozke = polozky.get(i).getTovar();
				//tu sa musim pripojit do DB a zapisat zmenu
				BigDecimal novaSkladovaZasoba = tovarVPolozke.getSkladovaZasoba().subtract(polozky.get(i).getMnozstvo());
				updateTovar(tovarVPolozke.getId(), novaSkladovaZasoba, session);
				}
			
			objednavka.setStavObjednavkyTL("D");
			
			unitOfWork.commit();
		}
		
		//zmena stavu objednavky -> Stornovana
				private static void setObjednavkaStornovana (Integer id, EclipseLinkSession session) throws Exception {
					
					UnitOfWork unitOfWork = session.getSession().acquireUnitOfWork();
					ExpressionBuilder builder = new ExpressionBuilder();
					Expression exp = builder.get("id").equal(id);
					Objednavka objednavka = (Objednavka) unitOfWork.readObject(Objednavka.class, exp);
													
					if (!((objednavka.getStavObjednavky().equals(StavObjednavky.OTVORENA)) || (objednavka.getStavObjednavky().equals(StavObjednavky.DOKONCENA)))) {
						throw new IllegalArgumentException("Len otvorena alebo dokoncena objednavka moze byt stornovana!");
					}
					
					if (objednavka.getStavObjednavky().equals(StavObjednavky.DOKONCENA)) {
						
						//zmena stavu skladovych zasob v pripade, ze objednavka meni stav z Dokoncena na Stornovana
						Vector<ObjednavkaPolozka> polozky = objednavka.getPolozky();
						
						for (int i=0; i < polozky.size(); i++) {
							Tovar tovarVPolozke = polozky.get(i).getTovar();
							//tu sa musim pripojit do DB a zapisat zmenu
							BigDecimal novaSkladovaZasoba = tovarVPolozke.getSkladovaZasoba().add(polozky.get(i).getMnozstvo());
							updateTovar(tovarVPolozke.getId(), novaSkladovaZasoba, session);
							}
					}
												
					objednavka.setStavObjednavkyTL("S");
					
					unitOfWork.commit();
				}
	
		private static void updateTovar(Integer id, BigDecimal novaSkladovaZasoba, EclipseLinkSession session) {
			UnitOfWork unitOfWork = session.getSession().acquireUnitOfWork();
			ExpressionBuilder builder = new ExpressionBuilder();
			Expression exp = builder.get("id").equal(id);
			Tovar tovar = (Tovar) unitOfWork.readObject(Tovar.class, exp);
			tovar.setSkladovaZasoba(novaSkladovaZasoba);
			unitOfWork.commit();
		}
		
		private static void insertDodavatel(/*Integer idAdresa1, Integer idAdresa2, */ EclipseLinkSession session) throws Exception {
					
			UnitOfWork unitOfWork = session.getSession().acquireUnitOfWork();
			/*
			unitOfWork.addReadOnlyClass(Adresa.class);
			*/
			Dodavatel dodavatel = new Dodavatel();
			dodavatel.setKod("DOD0000007");
			dodavatel.setNazov("Dodavatel s adresami3");
			dodavatel.setIco("00345679");
			/*			
			ExpressionBuilder builder = new ExpressionBuilder();
			Expression exp1 = builder.get("id").equal(idAdresa1);
			Expression exp2 = builder.get("id").equal(idAdresa2);
									
			Adresa adresa1 = (Adresa) session.getSession().readObject(Adresa.class, exp1);
			Adresa adresa2 = (Adresa) session.getSession().readObject(Adresa.class, exp2);
			
			dodavatel.getAdresy().add(adresa1);
			dodavatel.getAdresy().add(adresa2);
			*/
			dodavatel.validate(session);
			//validacia duplicitneho ICa
			dodavatel.validateIcoDuplicity(session);
			
			unitOfWork.registerNewObject(dodavatel);
			
			unitOfWork.commit();
		}
		
		private static void updateDodavatel(Integer id, EclipseLinkSession session) {
			UnitOfWork unitOfWork = session.getSession().acquireUnitOfWork();
			ExpressionBuilder builder = new ExpressionBuilder();
			Expression exp = builder.get("id").equal(id);
			Dodavatel dodavatel = (Dodavatel) unitOfWork.readObject(Dodavatel.class, exp);
			dodavatel.setIco("00456789");
			unitOfWork.commit();
		}
		
		private static void readDodavatel(Integer id, EclipseLinkSession session) {
			UnitOfWork unitOfWork = session.getSession().acquireUnitOfWork();
			ExpressionBuilder builder = new ExpressionBuilder();
			Expression exp = builder.get("id").equal(id);
			Dodavatel dodavatel = (Dodavatel) unitOfWork.readObject(Dodavatel.class, exp);
			System.out.println("nacital som dodavatela " + dodavatel.getNazov() + " s tymi kontaktami " + dodavatel.getKontakty().toString());
			unitOfWork.commit();
		}
		
		private static void deleteDodavatel(Integer id, EclipseLinkSession session) {
			UnitOfWork unitOfWork = session.getSession().acquireUnitOfWork();
			ExpressionBuilder builder = new ExpressionBuilder();
			Expression exp = builder.get("id").equal(id);
			//precitam a vymazem napriamo, nie kopiu
			Dodavatel dodavatel = (Dodavatel) unitOfWork.readObject(Dodavatel.class, exp);
			unitOfWork.deleteObject(dodavatel);
			unitOfWork.commit();
		}	
		
		private static void insertKontakt(Integer id, EclipseLinkSession session) throws Exception {
				
			UnitOfWork unitOfWork = session.getSession().acquireUnitOfWork();
			//nemusim teoreticky robit klon
			unitOfWork.addReadOnlyClass(ObchodnyPartner.class);
			
			Kontakt kontakt = new Kontakt();
			
			ExpressionBuilder builder = new ExpressionBuilder();
			Expression exp = builder.get("id").equal(id);
						
			ObchodnyPartner obchodnyPartner = (ObchodnyPartner) session.getSession().readObject(ObchodnyPartner.class, exp);
						
			kontakt.setMeno("Jozef Baca");
			kontakt.setEmail("jozef@ovecka.sk");
			kontakt.setTelefon("+421911001002");
			kontakt.setObchodnyPartner(obchodnyPartner);
			
			kontakt.validate(session);
			
			unitOfWork.registerNewObject(kontakt);
			
			unitOfWork.commit();
		}
		
		private static void updateKontakt(Integer id, EclipseLinkSession session) {
			UnitOfWork unitOfWork = session.getSession().acquireUnitOfWork();
			ExpressionBuilder builder = new ExpressionBuilder();
			Expression exp = builder.get("id").equal(id);
			Kontakt kontakt = (Kontakt) unitOfWork.readObject(Kontakt.class, exp);
			kontakt.setEmail("jozef.baca@salas.sk");
			unitOfWork.commit();
		}
		
		private static void insertAdresa(EclipseLinkSession session) throws Exception {
				
			UnitOfWork unitOfWork = session.getSession().acquireUnitOfWork();
			
			Adresa adresa = new Adresa();
					
			adresa.setUlica("Bananova");
			adresa.setCislo("48");
			adresa.setMesto("Zvolen");
			adresa.setPsc("91102");
			
			adresa.validate(session);
			
			unitOfWork.registerNewObject(adresa);
			
			unitOfWork.commit();
		}
		
		private static void insertZakaznik(Integer idAdresa1, Integer idAdresa2, EclipseLinkSession session) throws Exception {
				
			UnitOfWork unitOfWork = session.getSession().acquireUnitOfWork();
			
			//doplnil som kod iba na preverenie funkcnosti ManyToMany Mapping
			unitOfWork.addReadOnlyClass(Adresa.class);
			
			Zakaznik zakaznik = new Zakaznik();
			zakaznik.setKod("ZAK0000005");
			zakaznik.setNazov("Zakaznik s adresami");
			
			//nacitanie adries
			ExpressionBuilder builder = new ExpressionBuilder();
			Expression exp1 = builder.get("id").equal(idAdresa1);
			Expression exp2 = builder.get("id").equal(idAdresa2);
									
			Adresa adresa1 = (Adresa) session.getSession().readObject(Adresa.class, exp1);
			Adresa adresa2 = (Adresa) session.getSession().readObject(Adresa.class, exp2);
			/*
			Vector<Adresa> adresy = new Vector<Adresa>();
			adresy.add(adresa1);
			adresy.add(adresa2);
			*/
			zakaznik.getAdresy().add(adresa1);
			zakaznik.getAdresy().add(adresa2);
			//zakaznik.setAdresy(adresy);
			//kontrola inputu
			//System.out.print(zakaznik.getAdresy());
						
			zakaznik.validate(session);
			
			unitOfWork.registerNewObject(zakaznik);
			
			unitOfWork.commit();
		}
		
		private static void insertObjednavka(Integer id, EclipseLinkSession session) throws Exception {
					
			UnitOfWork unitOfWork = session.getSession().acquireUnitOfWork();
		
			unitOfWork.addReadOnlyClass(Zakaznik.class);
			unitOfWork.addReadOnlyClass(Tovar.class);
			
			Objednavka objednavka = new Objednavka();
						
			ExpressionBuilder builder = new ExpressionBuilder();
			Expression exp = builder.get("id").equal(id);
						
			Zakaznik zakaznik = (Zakaznik) session.getSession().readObject(Zakaznik.class, exp);
																					
			objednavka.setCisloObjednavky("OBJ2023009");
			objednavka.setZakaznik(zakaznik);
			
			//vytvaram polozky do objednavky
			ObjednavkaPolozka polozka1 = new ObjednavkaPolozka();
			Expression expTovar1 = builder.get("id").equal(21);
			Tovar tovar1 = (Tovar) session.getSession().readObject(Tovar.class, expTovar1);
			polozka1.setTovar(tovar1);
			polozka1.setMnozstvo(new BigDecimal("3"));
			polozka1.setObjednavka(objednavka);    // priradim objednavku k polozke
			objednavka.getPolozky().add(polozka1); // priradim polozku do novej objednavky
			
			polozka1.validate(session);
						
			ObjednavkaPolozka polozka2 = new ObjednavkaPolozka();
			Expression expTovar2 = builder.get("id").equal(31);
			Tovar tovar2 = (Tovar) session.getSession().readObject(Tovar.class, expTovar2);
			polozka2.setTovar(tovar2);
			polozka2.setMnozstvo(new BigDecimal("3"));
			polozka2.setObjednavka(objednavka);    // priradim objednavku k polozke
			objednavka.getPolozky().add(polozka2); // priradim polozku do novej objednavky
			
			polozka2.validate(session);
			
			objednavka.validate(session, unitOfWork);
			
			unitOfWork.registerNewObject(objednavka);
			
			unitOfWork.commit();
						
		}
	
		private static void insertObjednavkaPolozka(Integer idTovar, Integer idObjednavka, EclipseLinkSession session) throws Exception {
				
			UnitOfWork unitOfWork = session.getSession().acquireUnitOfWork();
			
			unitOfWork.addReadOnlyClass(Tovar.class);
			unitOfWork.addReadOnlyClass(Objednavka.class);
					
			ObjednavkaPolozka polozka = new ObjednavkaPolozka();
			
			ExpressionBuilder builder = new ExpressionBuilder();
			Expression exp1 = builder.get("id").equal(idTovar);
			Expression exp2 = builder.get("id").equal(idObjednavka);
						
			Tovar tovar = (Tovar) session.getSession().readObject(Tovar.class, exp1);
			Objednavka objednavka = (Objednavka) session.getSession().readObject(Objednavka.class, exp2);
				
			//polozka.setJednotkovaCena(tovar.getCena());
			polozka.setMnozstvo(new BigDecimal("8"));
			//polozka.setSuma(polozka.getJednotkovaCena().multiply(polozka.getMnozstvo()));
			polozka.setTovar(tovar);
			polozka.setObjednavka(objednavka);
				
			polozka.validate(session);
			
			unitOfWork.registerNewObject(polozka);
			
			unitOfWork.commit();
		}
		/*
		private static void upDateObjednavka(Integer id, EclipseLinkSession session) throws Exception {
			
			UnitOfWork unitOfWork = session.getSession().acquireUnitOfWork();
		
			unitOfWork.addReadOnlyClass(Objednavka.class);
						
			ExpressionBuilder builder = new ExpressionBuilder();
			Expression exp = builder.get("id").equal(id);
						
			Objednavka objednavka = (Objednavka) session.getSession().readObject(Objednavka.class, exp);
																					
			objednavka.setDokoncena(session);
						
			//objednavka.validate(session);
			
			unitOfWork.registerNewObject(objednavka);
			
			unitOfWork.commit();
						
		}
		*/
}
