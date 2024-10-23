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


    @FXML
    private ComboBox<String> cbTipo;



    @FXML
    void clicInsertar(ActionEvent event) {
        // Coger los datos de los campos de texto
        String matricula = txtMatricula.getText(); //Mete los datos
        String marca = txtMarca.getText();
        String modelo = txtModelo.getText();
        String tipo = cbTipo.getValue();
        //Meterlos en un nuevo objeto coche
        Coche coche = new Coche(matricula, marca, modelo, tipo);

        //Si la lista de coches esta inicializada
        if (coches==null){
            coches = FXCollections.observableArrayList();
        }

        //Insertar el coche en la BBDD
        String mensaje = CocheDAO.crearCoche(coche);
        Alerta.mostrarAlerta(mensaje);

        //Si el mensaje es el siguiente
        if (mensaje.equals("Coche creado correctamente")){
            coches.add(coche); //Añadir el coche a la lista observable
            tvTabla.setItems(coches);
            actualizarTabla(); //Actualizar la tabla
        }
    }

    @FXML
    void clicModificar(ActionEvent event) {
        Coche cocheSeleccionado = tvTabla.getSelectionModel().getSelectedItem();

        String matricula = txtMatricula.getText();
        String marca = txtMarca.getText();
        String modelo = txtModelo.getText();
        String tipo = cbTipo.getValue();
        Coche cocheModificado = new Coche(matricula, marca, modelo, tipo);

        String mensaje = CocheDAO.actualizarCoche(cocheModificado, cocheSeleccionado);
        Alerta.mostrarAlerta(mensaje);

        if (mensaje.equals("Coche actualizado")) {
            int index = coches.indexOf(cocheSeleccionado);
            if (index != -1) {
                coches.set(index, cocheModificado); // Reemplaza el coche en la lista
                tvTabla.setItems(coches); // Actualiza la tabla
            }
            clicLimpiar(null); // Limpiar los campos de texto
        } else {
        Alerta.mostrarAlerta("Por favor, selecciona un coche de la tabla para modificar.");
    }
    }

    @FXML
    void clicLimpiar(ActionEvent event) {
        txtMatricula.clear();
        txtMarca.clear();
        txtModelo.clear();
        cbTipo.getSelectionModel().clearSelection();
    }

    @FXML
    void clicEliminar(ActionEvent event) {
        Coche cocheSeleccionado = tvTabla.getSelectionModel().getSelectedItem();
        if (cocheSeleccionado!=null){
            coches.remove(cocheSeleccionado);
        }
    }

    private void actualizarTabla(){
        ObservableList<Coche> listaCoches= FXCollections.observableArrayList(listaCoches());

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

    public void cocheTabla(MouseEvent mouseEvent) {
        Coche c = tvTabla.getSelectionModel().getSelectedItem();
        if (c!=null){
            txtMatricula.setText(c.getMatricula());
            txtMarca.setText(c.getMarca());
            txtModelo.setText(c.getModelo());
            cbTipo.setValue(c.getTipo());
        }
    }

    //Crear un metodo para que una variable tenga la informacion que hay seleccionada en la tabla
    private Coche cocheTabla(){
        return tvTabla.getSelectionModel().getSelectedItem();
    }

}
