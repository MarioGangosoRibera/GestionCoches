package org.example.gestioncoches.Controller;

import com.mongodb.ErrorCategory;
import com.mongodb.MongoClient;
import com.mongodb.MongoWriteException;
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
import org.example.gestioncoches.Clase.Coche;
import org.example.gestioncoches.Util.Alerta;
import org.example.gestioncoches.Util.ConnectionBD;
import org.example.gestioncoches.DAO.CocheDAO;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

import static org.example.gestioncoches.DAO.CocheDAO.*;

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
            //Añadir el coche a la lista observable
            coches.add(coche);
            //Actualizar la tabla
            tvTabla.setItems(coches);
            actualizarTabla();
        }
    }

    //En este boton al hacer clic sobre el coche que queremos modificar debemos meter la informacion de nuevo
    @FXML
    void clicModificar(ActionEvent event) {
        //Obtener el coche seleccionado de la tabla
        Coche cocheSeleccionado = tvTabla.getSelectionModel().getSelectedItem();

        //Coger los datos de los campos de texto
        String matricula = txtMatricula.getText();
        String marca = txtMarca.getText();
        String modelo = txtModelo.getText();
        String tipo = cbTipo.getValue();
        //Crear un objeto con los datos modificados
        Coche cocheModificado = new Coche(matricula, marca, modelo, tipo);

        //Insertar el coche en la BBDD
        String mensaje = CocheDAO.actualizarCoche(cocheModificado, cocheSeleccionado);
        Alerta.mostrarAlerta(mensaje);

        //Si el mensaje es el siguiente
        if (mensaje.equals("Coche actualizado")) {
            //Buscar el indice del coche seleccionado en la lista observable
            int index = coches.indexOf(cocheSeleccionado);
            //Si el coche se encontro en la ljsta
            if (index != -1) {
                //Cambiar el coche en la lista
                coches.set(index, cocheModificado);
                tvTabla.setItems(coches); // Actualiza la tabla
            }
            //Mostrar el mensaje de error si no se ha seleccionado ningun coche
        } else {
            Alerta.mostrarAlerta("Por favor, selecciona un coche de la tabla para modificar.");
        }
    }

    @FXML
    void clicLimpiar(ActionEvent event) {
        //Vaciar los campos
        txtMatricula.clear();
        txtMarca.clear();
        txtModelo.clear();
        cbTipo.getSelectionModel().clearSelection();
    }

    @FXML
    void clicEliminar(ActionEvent event) {
        //Obtener el coche seleccionado de la tabla
        Coche cocheSeleccionado = tvTabla.getSelectionModel().getSelectedItem();
        //Si el coche seleccionado es distinto a nulo
        if (cocheSeleccionado!=null){
            //Eliminar el coche que esta seleccionado
            coches.remove(cocheSeleccionado);
        }
    }

    private void actualizarTabla(){
        //Obtener la lista de coches desde la BBDD
        ObservableList<Coche> listaCoches= FXCollections.observableArrayList(listaCoches());

        // Establecer las columnas de la tabla con los valores correspondientes
        // tcMatricula, tcMarca, tcModelo y tcTipo son las columnas de la tabla
        // PropertyValueFactory es una clase que proporciona una forma de obtener el valor de una propiedad de un objeto
        tcMatricula.setCellValueFactory(new PropertyValueFactory<>("matricula"));
        tcMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
        tcModelo.setCellValueFactory(new PropertyValueFactory<>("modelo"));
        tcTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Establece la conexion con la BBDD
        con = ConnectionBD.getConexion();
        //Obtener la base de datos CrudCoches
        MongoDatabase database = con.getDatabase("CrudCoches");
        //Devuelve la colección coches, si no existe, la crea
        CocheDAO.collectionCoches = database.getCollection("coches");
        //Devuelve la colecion tipos de la BBDD
        collectionTipos = database.getCollection("tipos");
        //Crear un nuevo documento para insertar en la coleccion tipos
        Document tipos = new Document();
        tipos.append("tipos", listaTipos); //Agragar la lista tipos al documento

        //Agregar todos los tipos al comboBox para la seleccion
        cbTipo.getItems().addAll(listaTipos);

        try {
            //Intentar uinsertar el documento en la coleccion tipos
            collectionTipos.insertOne(tipos);
        } catch (MongoWriteException mwe) {
            //Manejar la excepción si se intenta insertar un documento con una clave duplicada
            if (mwe.getError().getCategory().equals(ErrorCategory.DUPLICATE_KEY)) {
                System.out.println("El documento con esa identificación ya existe");
            }
        }
    }
}
