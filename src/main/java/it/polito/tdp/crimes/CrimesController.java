/**
 * Sample Skeleton for 'Crimes.fxml' Controller Class
 */

package it.polito.tdp.crimes;

import java.util.List;
import java.net.URL;
import java.util.ResourceBundle;

import it.polito.tdp.crimes.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

public class CrimesController {
	
	private Model model;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="boxCategoria"
    private ComboBox<String> boxCategoria; // Value injected by FXMLLoader

    @FXML // fx:id="boxAnno"
    private ComboBox<Integer> boxAnno; // Value injected by FXMLLoader

    @FXML // fx:id="btnAnalisi"
    private Button btnAnalisi; // Value injected by FXMLLoader

    @FXML // fx:id="boxArco"
    private ComboBox<Arco> boxArco; // Value injected by FXMLLoader

    @FXML // fx:id="btnPercorso"
    private Button btnPercorso; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

    @FXML
    void doCreaGrafo(ActionEvent event) {
    	txtResult.clear();
    	String cat=this.boxCategoria.getValue();
    	if(cat==null) {
        	txtResult.appendText("Seleziona una categoria\n");;
        	return ;
    	}
    	
    	
       Integer anno=this.boxAnno.getValue();
       if(anno==null) {
       	txtResult.appendText("Seleziona un anno\n");;
       	return ;
   	}
        this.model.creaGrafo(cat, anno);
        
        txtResult.appendText("Grafo Creato!\n");
    	txtResult.appendText("# Vertici: " + model.nVertici()+ "\n");
    	txtResult.appendText("# Archi: " + model.nArchi() + "\n");
       
        List<Arco> aList= this.model.getArchiPesoMax();
       
        txtResult.appendText("Peso max: "+aList.get(0).getPeso()+"\n\n");
        for(Arco a:aList) {
        	txtResult.appendText(a.toString()+"\n");
        }
        
        this.boxArco.getItems().addAll(aList);
    }

    @FXML
    void doCalcolaPercorso(ActionEvent event) {
    	txtResult.clear();
    	Arco a=this.boxArco.getValue();
    	
    	if(a == null) {
    		txtResult.appendText("Selezionare un arco");
    		return;
    	}
    	
    	List<String> path = this.model.trovaPercorsoMin(a);
    	for(String s : path)
    		txtResult.appendText(s+"\n");
    	
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert boxCategoria != null : "fx:id=\"boxCategoria\" was not injected: check your FXML file 'Crimes.fxml'.";
        assert boxAnno != null : "fx:id=\"boxAnno\" was not injected: check your FXML file 'Crimes.fxml'.";
        assert btnAnalisi != null : "fx:id=\"btnAnalisi\" was not injected: check your FXML file 'Crimes.fxml'.";
        assert boxArco != null : "fx:id=\"boxArco\" was not injected: check your FXML file 'Crimes.fxml'.";
        assert btnPercorso != null : "fx:id=\"btnPercorso\" was not injected: check your FXML file 'Crimes.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Crimes.fxml'.";

    }
    
    public void setModel(Model model) {
    	this.model = model;
    	this.boxCategoria.getItems().addAll(model.getCategory());
    	this.boxAnno.getItems().addAll(model.getAnno());
    }
}
