package org.example.gestioncoches.Util;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import java.util.Properties;

//Clase que hace la conexión a la base de datos MongoDB.
public class ConnectionBD {
    public static MongoClient getConexion() {

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
