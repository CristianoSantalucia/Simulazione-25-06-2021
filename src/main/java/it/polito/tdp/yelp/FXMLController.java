/**
 * Sample Skeleton for 'Scene.fxml' Controller Class
 */

package it.polito.tdp.yelp;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.yelp.model.Business;
import it.polito.tdp.yelp.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController
{
	private Model model;

	@FXML // ResourceBundle that was given to the FXMLLoader
	private ResourceBundle resources;

	@FXML // URL location of the FXML file that was given to the FXMLLoader
	private URL location;

	@FXML // fx:id="btnCreaGrafo"
	private Button btnCreaGrafo; // Value injected by FXMLLoader

	@FXML // fx:id="btnLocaleMigliore"
	private Button btnLocaleMigliore; // Value injected by FXMLLoader

	@FXML // fx:id="btnPercorso"
	private Button btnPercorso; // Value injected by FXMLLoader

	@FXML // fx:id="cmbCitta"
	private ComboBox<String> cmbCitta; // Value injected by FXMLLoader

	@FXML // fx:id="txtX"
	private TextField txtX; // Value injected by FXMLLoader

	@FXML // fx:id="cmbAnno"
	private ComboBox<Integer> cmbAnno; // Value injected by FXMLLoader

	@FXML // fx:id="cmbLocale"
	private ComboBox<Business> cmbLocale; // Value injected by FXMLLoader

	@FXML // fx:id="txtResult"
	private TextArea txtResult; // Value injected by FXMLLoader

	private final String ERRORE = "\nERRORE! controllare che i dati inseriti siano corretti";

	@FXML void doCreaGrafo(ActionEvent event)
	{
		try
		{
			String city = this.cmbCitta.getValue();
			Integer year = this.cmbAnno.getValue();

			if (city != null && year != null)
			{
				// resetto testo
				this.txtResult.clear();
				this.txtResult.appendText("Crea grafo...\n");

				// creo grafo
				this.model.creaGrafo(city, year);
				txtResult.appendText(String.format("\nGRAFO CREATO CON:\n#Vertici: %d\n#Archi: %d",
						this.model.getNumVertici(), this.model.getNumArchi()));

				// cliccabili
				this.btnLocaleMigliore.setDisable(false);
				this.btnPercorso.setDisable(false);
				this.cmbLocale.setDisable(false);
				this.cmbLocale.getItems().clear();
				this.txtX.setDisable(false);

				// aggiungo risultati alla combobox
				this.cmbLocale.getItems().addAll(this.model.getVertici());
			}
			else 
			{
				txtResult.appendText(this.ERRORE);
				return; 
			}
		}
		catch (Exception e)
		{
			txtResult.appendText(this.ERRORE);
			e.printStackTrace();
		}
	}

	@FXML void doLocaleMigliore(ActionEvent event)
	{
		try
		{
			txtResult.appendText("\n\nCalcolo locale migliore...\n\n" + this.model.cercaBest());
		}
		catch (Exception e)
		{
			txtResult.appendText(this.ERRORE);
			return; 
		}
	}

	@FXML void doCalcolaPercorso(ActionEvent event)
	{
		//controlli
		Double X = .5; 
		Business partenza = null;
		try
		{
			X = Double.parseDouble(this.txtX.getText());
			if(X < 0 || X > 1)
			{
				this.txtResult.appendText("\nErrore, inserire un numero corretto");
				return;
			}
			
			partenza = this.cmbLocale.getValue(); 
			if (partenza == null)
			{
				this.txtResult.appendText("\nErrore, inserire un numero corretto");
				return;
			}
		}
		catch(NumberFormatException nfe)
		{
			this.txtResult.appendText("\nErrore, inserire un numero corretto");
			return;
		} 
		
		StringBuilder s = new StringBuilder();
		s.append("\n\nPERCORSO CALCOLATO: \n\n"); 
		List<Business> percorso = this.model.trovaPercorso(partenza, X); 
		if(!percorso.isEmpty())
		{
			for (Business b : percorso)
				s.append("- " + b + ";\n"); 
			this.txtResult.appendText(s.toString());
		}
		else 
		{
			this.txtResult.appendText("\n\nPERCORSO NON TROVATO!");
		}
	}

	@FXML // This method is called by the FXMLLoader when initialization is complete
	void initialize()
	{
		assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'Scene.fxml'.";
		assert btnLocaleMigliore != null
				: "fx:id=\"btnLocaleMigliore\" was not injected: check your FXML file 'Scene.fxml'.";
		assert btnPercorso != null : "fx:id=\"btnPercorso\" was not injected: check your FXML file 'Scene.fxml'.";
		assert cmbCitta != null : "fx:id=\"cmbCitta\" was not injected: check your FXML file 'Scene.fxml'.";
		assert txtX != null : "fx:id=\"txtX\" was not injected: check your FXML file 'Scene.fxml'.";
		assert cmbAnno != null : "fx:id=\"cmbAnno\" was not injected: check your FXML file 'Scene.fxml'.";
		assert cmbLocale != null : "fx:id=\"cmbLocale\" was not injected: check your FXML file 'Scene.fxml'.";
		assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";
	}

	public void setModel(Model model)
	{
		this.model = model;

		for (int i = 2005; i <= 2013; i++)
			this.cmbAnno.getItems().add(i);
		this.cmbCitta.getItems().addAll(this.model.getAllCities());
	}
}
