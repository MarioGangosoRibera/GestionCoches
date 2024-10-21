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
import javafx.scene.input.MouseEvent;
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
    private TableView<Coche> tvTabla;

    @FXML
    private TableColumn<?, ?> tcMatricula;

    @FXML
    private TableColumn<?, ?> tcMarca;

    @FXML
    private TableColumn<?, ?> tcModelo;

    @FXML
    private TableColumn<?, ?> tcTipo;

    ObservableList<Coche> coches;

    private int posicionCocheTabla;

    @FXML
    private ComboBox<String> cbTipo;

    @FXML
    void clicEliminar(ActionEvent event) {
        Coche cocheSeleccionado = tvTabla.getSelectionModel().getSelectedItem();
        if (cocheSeleccionado!=null){
            coches.remove(cocheSeleccionado);
        }
    }

    @FXML
    void clicInsertar(ActionEvent event) {
        String matricula = txtMatricula.getText();
        String marca = txtMarca.getText();
        String modelo = txtModelo.getText();
        String tipo = cbTipo.getValue();
        Coche coche = new Coche(matricula, marca, modelo, tipo);

        if (coches==null){
            coches = FXCollections.observableArrayList();
        }
        coches.add(coche);
        tvTabla.setItems(coches);
        actualizarTabla();
        Alerta.mostrarAlerta(crearCoche(coche));
    }

    @FXML
    void clicModificar(ActionEvent event) {
        Coche cocheSeleccionado = tvTabla.getSelectionModel().getSelectedItem();
        if (cocheSeleccionado!=null){
            // Copia los datos del coche seleccionado a los campos
            txtMatricula.setText(cocheSeleccionado.getMatricula());
            txtMarca.setText(cocheSeleccionado.getMarca());
            txtModelo.setText(cocheSeleccionado.getModelo());
            cbTipo.setValue(cocheSeleccionado.getTipo());
        }

        // Actualiza el coche en la base de datos
        if (cocheSeleccionado!=null){
            Coche cocheActualizado = new Coche(txtMatricula.getText(), txtMarca.getText(), txtModelo.getText(), cbTipo.getValue());
            String mensaje = CocheDAO.actualizarCoche(cocheActualizado);
            Alerta.mostrarAlerta(mensaje);
            // Actualiza la tabla con los datos actualizados
            coches.set(coches.indexOf(cocheSeleccionado), cocheActualizado);
            tvTabla.refresh();
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
