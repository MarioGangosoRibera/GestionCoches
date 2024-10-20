package org.example.gestioncoches;

import com.mongodb.ErrorCategory;
import com.mongodb.MongoClient;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.bson.Document;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

import static org.example.gestioncoches.CocheDAO.*;

public class HelloController implements Initializable {

    MongoClient con;
    ArrayList<String> listaTipos = new ArrayList<>(Arrays.asList("Gasolina", "Diesel", "Híbrido", "Eléctrico"));

    @FXML
    private TextField txtMatricula;

    @FXML
    private Button btnNuevo;

    @FXML
    private TextField txtMarca;

    @FXML
    private TextField txtModelo;

    @FXML
    private Button btnInsertar;

    @FXML
    private Button btnModificar;

    @FXML
    private Button btnEliminar;

    @FXML
    private TableView<?> tvTabla;

    @FXML
    private TableColumn<?, ?> tcMatricula;

    @FXML
    private TableColumn<?, ?> tcMarca;

    @FXML
    private TableColumn<?, ?> tcModelo;

    @FXML
    private TableColumn<?, ?> tcTipo;

    @FXML
    private ComboBox<String> cbTipo;

    @FXML
    void clicEliminar(ActionEvent event) {
        Coche cocheSeleccionado = tvTabla.getSelectionModel().getSelectedItem();

        if(cocheSeleccionado != null) {
            CocheDAO.eliminarCoche(cocheSeleccionado.getMatricula());
            actualizarTabla();
        }
    }

    @FXML
    void clicInsertar(ActionEvent event) {
        String matricula = txtMatricula.getText();
        String marca = txtMarca.getText();
        String modelo = txtModelo.getText();
        String tipo = cbTipo.getValue();
        Coche coche = new Coche(matricula, marca, modelo, tipo);
        tvTabla.setItems();
        Alerta.mostrarAlerta(crearCoche(coche));
    }

    @FXML
    void clicModificar(ActionEvent event) {
        Coche cocheSeleccionado = tvTabla.getSelectionModel().getSelectedItems();

        if(cocheSeleccionado != null) {
            String matricula = txtMatricula.getText();
            String marca = txtMarca.getText();
            String modelo = txtModelo.getText();
            String tipo = cbTipo.getValue();
            Coche coche = new Coche(matricula, marca, modelo, tipo);
            CocheDAO.actualizarCoche(coche);
            actualizarTabla();
        }
    }

    @FXML
    void clicLimpiar(ActionEvent event) {
        txtMatricula.clear();
        txtMarca.clear();
        txtModelo.clear();
        cbTipo.getSelectionModel().clearSelection();
    }

    private void actualizarTabla(){
        ObservableList<Coche> listaCoches= FXCollections.observableArrayList(listarCoches());
        tvTabla.setItems(listaCoches);
        tcMatricula.setCellValueFactory(new PropertyValueFactory<>("matricula"));
        tcMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
        tcModelo.setCellValueFactory(new PropertyValueFactory<>("modelo"));
        tcTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        con = ConnectionBD.getConexion();
        MongoDatabase database = con.getDatabase("CrudCoches");
        // Me devuelve una colección si no existe la crea
        CocheDAO.collectionCoches = database.getCollection("coches");
        collectionTipos = database.getCollection("tipos");
        Document tipos = new Document();
        tipos.append("tipos", listaTipos);
        cbTipo.getItems().addAll(listaTipos);
        try {
            //La función ".insertOne()" se utiliza para insertar el documento en la colección.
            collectionTipos.insertOne(tipos);
        } catch (MongoWriteException mwe) {
            if (mwe.getError().getCategory().equals(ErrorCategory.DUPLICATE_KEY)) {
                System.out.println("El documento con esa identificación ya existe");
            }
        }

    }
}
