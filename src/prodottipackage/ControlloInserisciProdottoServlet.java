package prodottipackage;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import utentipackage.Utente;
import utentipackage.UtentiManager;

/**
 * Servlet implementation class ControlloInserisciProdottoServlet
 */
@WebServlet("/ControlloInserisciProdottoServlet")
public class ControlloInserisciProdottoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ControlloInserisciProdottoServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 * Questo metodo permette l'inserimento di un nuovo prodotto
	 *     
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		boolean flag = true;
		
		String nome = request.getParameter("nome");
		if (nome.length() > 30)
			flag = false;
		for (int i = 0; i < nome.length(); i++) {
			if (!(Character.isWhitespace(nome.charAt(i))
					||Character.isLetter(nome.charAt(i)))) {
				flag = false;
			}
		} request.removeAttribute("nome");
		
		String descrizione = request.getParameter("descrizione");
		if (descrizione.length() > 300)
			flag = false;
		for (int i = 0; i < descrizione.length(); i++) {
			if (!(Character.isWhitespace(descrizione.charAt(i))
					||Character.isLetter(descrizione.charAt(i)))) {
				flag = false;
			}
		} request.removeAttribute("descrizione");

		int prezzo = Integer.parseInt(request.getParameter("prezzo"));
		if (prezzo < 0)
			flag = false;
		request.removeAttribute("prezzo");
		
		int quantita = Integer.parseInt(request.getParameter("quantita"));
		if (quantita < 0)
			flag = false;
		request.removeAttribute("quantita");
		
		Prodotto bean = new Prodotto();
		bean.setDescrizione(descrizione);
		bean.setNome(nome);
		bean.setPrezzo(prezzo);
		bean.setQuantita(quantita);
		/*manca l'url Immagine*/

		ProdottiManager model = new ProdottiManager();
		if (flag) {
			try {
				model.aggiungiProdotto(bean);
			} catch (SQLException e) {
				/*arriva qui se non ha potuto aggiungere il prodotto*/
				response.setStatus(HttpServletResponse.
						SC_INTERNAL_SERVER_ERROR);
				RequestDispatcher dispatcher = getServletContext().
						getRequestDispatcher("/Pages/Negozio.jsp");
				dispatcher.forward(request, response);	
			}
			/*arriva qui se � andato tutto bene*/
			RequestDispatcher dispatcher = getServletContext().
					getRequestDispatcher("/pages/Negozio.jsp");
			dispatcher.forward(request, response);	
		}
		/*arriva qui se ci sono parametri formalmente scorretti*/
		response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
		RequestDispatcher dispatcher = getServletContext().
				getRequestDispatcher("/pages/Negozio.jsp");
		dispatcher.forward(request, response);	
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}