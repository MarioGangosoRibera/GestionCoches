package org.example.gestioncoches;

import com.mongodb.ErrorCategory;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.ErrorCategory;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import javax.print.Doc;
import java.util.ArrayList;
import java.util.List;

public class CocheDAO {

    public static MongoCollection<Document> collectionCoches;
    public static MongoCollection<Document> collectionTipos;

    public static String crearCoche(Coche coche) {
        String mensaje="";
        Document doc = new Document("matricula", coche.getMatricula())
                .append("marca", coche.getMarca())
                .append("modelo", coche.getModelo())
                .append("tipo", coche.getTipo());

        try {
            collectionCoches.insertOne(doc);
            mensaje = "Coche creado correctamente";
        }catch (MongoWriteException mwe) {
            if (mwe.getError().getCategory().equals(ErrorCategory.DUPLICATE_KEY)) {
                mensaje="El documento con esa identificación ya existe";
            }
        }
        return mensaje;
    }//crear coche

    public static List<Coche> listarCoches() {
        List<Coche> listaCoches = new ArrayList<>();
        try (MongoCursor<Document> cursor = collectionCoches.find().iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                String matricula= (String) doc.get("matricula");
                String marca = (String) doc.get("marca");
                String modelo= (String) doc.get("modelo");
                String tipo = (String) doc.get("tipo");
                Coche c =  new Coche(matricula,marca,modelo,tipo);
                listaCoches.add(c);

            }
        } catch (MongoWriteException mwe) {
            if (mwe.getError().getCategory().equals(ErrorCategory.DUPLICATE_KEY)) {
                mwe.printStackTrace();
            }
        }
        return listaCoches;
    }

    public static String actualizarCoche(Coche coche) {
        String mensaje="";
        try{
            collectionCoches.updateOne(new Document("matricula", coche.getMatricula()),
                new Document("$set", new Document("marca", coche.getMarca())
                        .append("modelo", coche.getModelo())
                        .append("tipo", coche.getTipo())));
            mensaje="Coche actualizado";
        } catch (MongoWriteException mwe) {
            mensaje="El coche no existe";
        }
        return mensaje;
    }

    public static Coche obtenerCoche(String matricula) {
        Coche coche = null;
        try (MongoCursor<Document> cursor = collectionCoches.find(new Document("matricula", matricula)).iterator()){
            if (cursor.hasNext()) {
                Document doc = cursor.next();
                String marca = (String) doc.get("marca");
                String modelo = (String) doc.get("modelo");
                String tipo = (String) doc.get("tipo");
                coche = new Coche(matricula,marca,modelo,tipo);
            }
        } catch (MongoWriteException mwe){
            mwe.printStackTrace();
        }
        return coche;
    }




}