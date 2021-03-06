package ordinepackage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;


import carrelloPackage.Carrello;
import prodottipackage.Prodotto;
import utentipackage.Utente;

/**Questa classe č il gestore degli oggetti "Ordini".
 * Si occupa di effettuare tutte le operazioni sul database che 
 * coinvolgono gli ordini*/
public class OrdineManager {
	private static DataSource ds;

	static {
		try {
			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup("java:comp/env");

			ds = (DataSource) envCtx.lookup("jdbc/fiorazon");

		} catch (NamingException e) {
			System.out.println("Error:" + e.getMessage());
		}
	}
	
	private static Connection getConnection () throws SQLException {
		if (ds != null)
			return ds.getConnection();
		else if (ds == null) {
			String URL ="jdbc:mysql://localhost:3306";
			String database ="fiorazon";
				String driver = "com.mysql.jdbc.Driver";
			String user ="root";
			String password = "root";
			
			
			try {
				Class.forName(driver);
				return  DriverManager.getConnection(URL + "/"+database,user,password);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
		}
		return null;
		
	}
	// __________________________________________________________________________________________________
	// username ritorna ordini associati amministratore
	/**Questo metodo ha come valore di ritorno una lista contenente 
	 * ogni ordine presente nel database*/
	public ArrayList<Ordine> returnOrdiniAmministratore() throws SQLException {
		Connection conn = null;
		PreparedStatement preparedStatement1 = null, preparedStatement2 = null;
		ArrayList<Ordine> listaOrdine = new ArrayList<Ordine>();
		ArrayList<Prodotto> listaProdotto = new ArrayList<Prodotto>();

		String SQL1 = "select * from Ordine";
		String SQL2 = "select idProdottoOrdine,quantitąProdottoOrdine,prezzo,nomeProdottiOrdine from prodottiordine where idOrdine = ?";
		

		try {
			conn = getConnection();

			preparedStatement1 = conn.prepareStatement(SQL1);
			ResultSet rs = preparedStatement1.executeQuery();

			while (rs.next()) {
				Ordine ord = new Ordine();
				ord.setId(rs.getInt("id"));
				ord.setUtenteOrdine(rs.getString("utenteOrdine"));
				ord.setPrezzoTotale(rs.getDouble("prezzoTotale"));
				ord.setStato(rs.getString("stato"));
				ord.setIban(rs.getString("iban"));
				listaOrdine.add(ord);
			}
				for (Ordine or : listaOrdine) {
					preparedStatement2 = conn.prepareStatement(SQL2);
					preparedStatement2.setInt(1, or.getId());
					ResultSet rst = preparedStatement2.executeQuery();
					
					while (rst.next()) {
						Prodotto prd = new Prodotto();
						prd.setIdProdotto(rst.getInt("idProdottoOrdine"));
						prd.setQuantita(rst.getInt("quantitąProdottoOrdine"));
						prd.setPrezzo(rst.getDouble("prezzo"));
						prd.setNome(rst.getString("nomeProdottiOrdine"));
						
						

						// aggiunge prodotto all' array
						
						listaProdotto.add(prd);
					}
					// aggiunge prodotto all' ordine
					or.setProdotto(listaProdotto);
					listaProdotto = new ArrayList<Prodotto>();
				}

			} finally {
				try {
					if (preparedStatement1 != null && preparedStatement2 != null ) {
						
						preparedStatement2.close();
						preparedStatement1.close();
					}
				} finally {
					if (conn != null)
						conn.close();
				}
			}
			return listaOrdine;
		}
	// __________________________________________________________________________________________________
	// username ritorna ordini associati Utente
	/**Questo metodo ha come parametro un username riferito ad un utente
	 * e ha come valore di ritorno una lista di ordini riferiti all'username
	 * passato come parametro*/
	public ArrayList<Ordine> returnOrdiniUtente(String username) throws SQLException {
		Connection conn = null;
		PreparedStatement preparedStatement1 = null, preparedStatement2 = null;
		ArrayList<Ordine> listaOrdine = new ArrayList<Ordine>();
		ArrayList<Prodotto> listaProdotto = new ArrayList<Prodotto>();
	
		String SQL1 = "select * from Ordine where utenteOrdine = ?";
		String SQL2 = "select idProdottoOrdine,quantitąProdottoOrdine,prezzo,nomeProdottiOrdine from prodottiordine where idOrdine = ?";
		

		try {
			conn = getConnection();

			preparedStatement1 = conn.prepareStatement(SQL1);
			preparedStatement1.setString(1, username);
			ResultSet rs = preparedStatement1.executeQuery();

			while (rs.next()) {
				Ordine ord = new Ordine();
				ord.setId(rs.getInt("id"));
				ord.setUtenteOrdine(rs.getString("utenteOrdine"));
				ord.setPrezzoTotale(rs.getDouble("prezzoTotale"));
				ord.setStato(rs.getString("stato"));
				ord.setIban(rs.getString("iban"));
				listaOrdine.add(ord);
			}
			
			for (Ordine or : listaOrdine) {
				preparedStatement2 = conn.prepareStatement(SQL2);
				preparedStatement2.setInt(1, or.getId());
				ResultSet rst = preparedStatement2.executeQuery();

				while (rst.next()) {
					Prodotto prd = new Prodotto();
					prd.setIdProdotto(rst.getInt("idProdottoOrdine"));
					prd.setQuantita(rst.getInt("quantitąProdottoOrdine"));
					prd.setPrezzo(rst.getDouble("prezzo"));
					prd.setNome(rst.getString("nomeProdottiOrdine"));
					
					

					// aggiunge prodotto all' array
					
					listaProdotto.add(prd);
				}
				// aggiunge prodotto all' ordine
				or.setProdotto(listaProdotto);
				listaProdotto = new ArrayList<Prodotto>();
			}

		} finally {
			try {
				if (preparedStatement1 != null && preparedStatement2 != null ) {
				
					preparedStatement2.close();
					preparedStatement1.close();
				}
			} finally {
				if (conn != null)
					conn.close();
			}
		}
		return listaOrdine;
	}

	// __________________________________________________________________________________________________
	// avanza stato
	/**Questo metodo cambia lo stato di spedizione di un ordine. Ha 
	 * come parametri il nuovo stato e l'id dell'ordine da modificare*/
	public boolean avanzaStato(String Stato, int idOrdine) throws SQLException {
		Connection conn = null;
		PreparedStatement preparedStatement1 = null,ps2=null;
		String prova= "select * from ordine where id = ?";
		String SQL1 = "update ordine set stato = ? where id = ? ";
		try {
			conn = getConnection();
			ps2 = conn.prepareStatement(prova);
			
			ps2.setInt(1, idOrdine);
			ResultSet rs = ps2.executeQuery();
			if(!rs.next()) return false;
			preparedStatement1 = conn.prepareStatement(SQL1);
			preparedStatement1.setString(1, Stato);
			preparedStatement1.setInt(2, idOrdine);
			preparedStatement1.executeUpdate();
		} finally {
			try {
				if (preparedStatement1 != null && ps2 != null) {
					ps2.close();
					preparedStatement1.close();
				}
			} finally {
				if (conn != null)
					conn.close();
			}
		}
		return true;
	}
	/**
	 * Questo metodo crea un ordine a partire da un carrello.
	 * @param carrello č il carrello dal quale creiamo l'ordine.
	 * @throws SQLException
	 * @return restituisce l'ordine creato. 
	 */
	public Ordine creaOrdine(Carrello carrello, Utente utente) throws SQLException {
		Connection conn = null;
		PreparedStatement preparedStatement1 = null, ps2 = null,ps3 = null,
				ps4=null,ps5=null;
		
		String SQL1 = "select * from prodotticarrello where numeroCarrello = ?";
		String SQL2= "select nome, prezzo from prodotto where idProdotto = ?";
		String SQL3 = "insert into ordine(utenteOrdine,stato,prezzoTotale)"
				+ " values(?,'Da Spedire','0')";
		String SQL4 = "select id from ordine where utenteOrdine = ? and "
				+ "prezzoTotale = '0'";
		String SQL5 = "insert into prodottiordine(idOrdine,idProdottoOrdine,"
				+ "quantitąProdottoOrdine,prezzo,nomeProdottiOrdine) values(?,?,?,?,?)";
		String username = utente.getUsername();
		int idOrdine = 0;
		double prezzoTot = 0;
	
		try {
			conn = getConnection();
			
				ps3 = conn.prepareStatement(SQL3);
				ps3.setString(1, username);
				ps3.executeUpdate(); //inserisco l'ordine nel database
				ps4 = conn.prepareStatement(SQL4);
				ps4.setString(1, username);
				ResultSet rs4 = ps4.executeQuery();
				
				if(rs4.next()) { //mi prendo l'id dell'ordine
					idOrdine = rs4.getInt("id");
				}
				preparedStatement1 = conn.prepareStatement(SQL1);
				
				preparedStatement1.setInt(1, carrello.getId());
				ResultSet rs1 = preparedStatement1.executeQuery();
				int idProdottoCarrello = -1;//mi serve per testare se un ordine non esiste
				while(rs1.next()) { //mi prendo i prodotti dal carrello
					ps2 = conn.prepareStatement(SQL2);
					
					idProdottoCarrello = rs1.getInt("idProdottoCarrello");
					
					
					int quantitą = rs1.getInt("quantitąCarrello");
					ps2.setInt(1, idProdottoCarrello);
					ResultSet rs2 = ps2.executeQuery(); //mi prendo il nome del prodotto
					if(rs2.next()) {
						ps5 = conn.prepareStatement(SQL5);
						ps5.setInt(1, idOrdine);
						ps5.setInt(2, idProdottoCarrello);
						ps5.setInt(3, quantitą);
						double prezzo = rs2.getDouble("prezzo");
						prezzoTot += prezzo * quantitą;
						ps5.setDouble(4, prezzo);
						String nome = rs2.getString("nome");
						ps5.setString(5, nome);
						ps5.executeUpdate();
					
					}
				}
				if(idProdottoCarrello == -1) return null;
		} finally {
			try {
				if (preparedStatement1 != null && ps2 != null && ps3 != null && ps4 != null 
						&& ps5 != null ) {
					
					ps5.close();
					ps4.close();
					ps3.close();
					ps2.close();
					preparedStatement1.close();
				}
			} finally {
				if (conn != null)
					conn.close();
			}
		}
		
		Ordine ordine = new Ordine();
		ordine.setId(idOrdine);
		ordine.setStato("Da spedire");
		ordine.setPrezzoTotale(0);
		ordine.setUtenteOrdine(username);
		ordine.setPrezzoTotale(prezzoTot);
		return ordine;
		
	}
	// __________________________________________________________________________________________________
	// iserici iban
	/**
	 * Questo metodo permette di completare il pagamento con l'inserimento dell'iban
	 * @param ordine che deve essere completato
	 * @param carrello che deve essere cancellato
	 * @param iban da inserire nell'ordine
	 * @return l'esito dell'operazione
	 * @throws SQLException
	 */
	public boolean inserisciIban(Ordine ordine, Carrello carrello, String iban) throws SQLException {
		boolean flag = true;
		Connection conn = null;
		PreparedStatement ps3 = null,ps1=null,ps2=null,ps4=null;
		String SQL1 ="select * from prodottiordine, prodotto where idOrdine=? and"
				+ " idProdottoOrdine =idProdotto";
		String SQL2 = "update prodotto set quantita = ? where idProdotto = ?";
		String SQL3 = "update ordine set iban = ?, prezzoTotale = ? where id = ? ";
		String SQL4 = "delete from carrello where numeroCarrello = ?";
		String SQL4bis = "select * from carrello where numeroCarrello = ?";
		String SQL5 = "delete from ordine where id = ?";
		try {
			conn = getConnection();
			ps1 = conn.prepareStatement(SQL1);
			ps1.setInt(1, ordine.getId());
			ResultSet rs1 = ps1.executeQuery();
			while(rs1.next()) {
				int quantitąProdottoOrdine = rs1.getInt("quantitąProdottoOrdine");
				int quantitąProdottoCatalogo = rs1.getInt("quantita");
				int idProdotto =rs1.getInt("idProdotto");
				
				if(quantitąProdottoCatalogo < quantitąProdottoOrdine) {
					flag = false; //quantitą indisponibile
				}
				else { //diminuisco quantitą del prodotto in negozio
					ps2 = conn.prepareStatement(SQL2);
					ps2.setInt(1, (quantitąProdottoCatalogo - quantitąProdottoOrdine));
					ps2.setInt(2,idProdotto );
					ps2.executeUpdate();
				}
			}
			if(iban.length() != 16){
				flag = false; //iban non conforme
			}
			for (int i=0; i < iban.length(); i ++) {
				if(!Character.isDigit(iban.charAt(i))) {
					flag = false; 
					
				}
			}
			if(flag) {
				double prezzo = ordine.getPrezzoTotale(); //arrivi qui se č tutto ok
				
				ps3 = conn.prepareStatement(SQL3); //aggiorna prezzo e iban dell'ordine
				ps3.setString(1, iban);
				ps3.setDouble(2, prezzo);
				ps3.setInt(3, ordine.getId());
				ps3.executeUpdate();
				
				ps4 = conn.prepareStatement(SQL4); //cancella il carrello
				ps4.setInt(1, carrello.getId());;
				ps4.executeUpdate();
			}
			else {
				ps3 = conn.prepareStatement(SQL5);
				ps3.setInt(1, ordine.getId());
				ps3.executeUpdate();
			}
			
		} finally {
			try {
				if (ps3 != null ) {

					ps3.close();
				}
			} finally {
				if (conn != null)
					conn.close();
			}
		}
		return flag;
	}
	// __________________________________________________________________________________________________
}
