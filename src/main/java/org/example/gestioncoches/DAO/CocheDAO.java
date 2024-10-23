package org.example.gestioncoches.DAO;

import com.mongodb.ErrorCategory;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.example.gestioncoches.Clase.Coche;

import java.util.ArrayList;
import java.util.List;

public class CocheDAO {
    //Colección de documentos que representa la tabla de coches en la BBDD
    public static MongoCollection<Document> collectionCoches;
    //Colección de documentos que representa la tabla de tipos en la BBDD
    public static MongoCollection<Document> collectionTipos;

    //Metodo para crear un nuevo coche en la BBDD
    public static String crearCoche(Coche coche) {
        String mensaje="";
        //Crear un nuevo documento que representa al coche
        Document doc = new Document("matricula", coche.getMatricula())
                .append("marca", coche.getMarca())
                .append("modelo", coche.getModelo())
                .append("tipo", coche.getTipo());

        try {
            //intentar insertar el documento en la coleccion coches
            collectionCoches.insertOne(doc);
            //Si se inserta bien devolver el mensaje de exito
            mensaje = "Coche creado correctamente";
        }catch (MongoWriteException mwe) {
            //Manejar la excepción si se intenta insertar un documento con una clave duplicada
            if (mwe.getError().getCategory().equals(ErrorCategory.DUPLICATE_KEY)) {
                //Decolver el mensaje que indica que el documento ya exite
                mensaje="El documento con esa identificación ya existe";
            }
        }
        return mensaje; //Devolver el mensaje
    }

    //Metodo que devuelve una lista de coches desde la bbdd
    public static List<Coche> listaCoches() {
        //Crear una lista para almacenar los coches
        List<Coche> listaCoches = new ArrayList<>();

        //Usar un cursor para iterar sobre los documentos en la colección de coches
        try (MongoCursor<Document> cursor = collectionCoches.find().iterator()) {
            // Iterar mientras haya documentos en el cursor
            while (cursor.hasNext()) {
                // Obtener el siguiente documento
                Document doc = cursor.next();
                // Extraer los atributos del documento
                String matricula= (String) doc.get("matricula");
                String marca = (String) doc.get("marca");
                String modelo= (String) doc.get("modelo");
                String tipo = (String) doc.get("tipo");

                // Crear un nuevo objeto Coche con los atributos extraídos
                Coche c =  new Coche(matricula,marca,modelo,tipo);
                // Agregar el coche a la lista
                listaCoches.add(c);
            }
        } catch (MongoWriteException mwe) {
            // Manejar la excepción si ocurre un error de escritura
            if (mwe.getError().getCategory().equals(ErrorCategory.DUPLICATE_KEY)) {
                mwe.printStackTrace();
            }
        }
        //Devolver la lista de coches
        return listaCoches;
    }

    //Metodo que actualiza un coche en la bbdd
    public static String actualizarCoche(Coche cocheModificado, Coche cocheSeleccionado) {
        String mensaje="";
        try {
            //Crear un documento con los nuevos datos del coche
            Document nuevoCoche = new Document("matricula", cocheModificado.getMatricula())
                    .append("marca", cocheModificado.getMarca())
                    .append("modelo", cocheModificado.getModelo())
                    .append("tipo", cocheModificado.getTipo());

            // Obtener la matrícula del coche seleccionado
            String matriculaSeleccionada = cocheSeleccionado.getMatricula();
            //Actualizar el documento en la colección de coches
            collectionCoches.updateOne(Filters.eq("matricula", matriculaSeleccionada), new Document("$set", nuevoCoche));

            mensaje = "Coche actualizado";
        } catch (Exception e) {
            e.printStackTrace();
            mensaje = "Error al actualizar el coche: " + e.getMessage();
        }
        //Devolver el mensaje
        return mensaje;
    }
}