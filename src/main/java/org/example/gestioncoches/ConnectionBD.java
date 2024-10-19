package org.example.gestioncoches;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import java.util.Properties;

public class ConnectionBD {
    public static MongoClient getConexion() {
       /* try {

            final MongoClient conexion = new MongoClient(new MongoClientURI("mongodb://admin:1234@localhost:27017/?authSource=admin"));

            System.out.println("Conectada correctamente a la BD");

            return conexion;
        } catch (Exception e) {
            System.out.println("Conexion Fallida");
            System.out.println(e);
            return null;
        }
        */


        try {
            Properties prop = new Properties();
            prop.load(R.getProperties("database.properties"));
            String host = prop.getProperty("host");
            int port = Integer.parseInt(prop.getProperty("port"));
            String username = prop.getProperty("username");
            String password = prop.getProperty("password");
            return new MongoClient(new MongoClientURI("mongodb://" + username + ":" + password + "@" + host + ":" + port + "/?authSource=admin"));

        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }
}
