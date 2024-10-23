package org.example.gestioncoches.Util;

import javafx.scene.control.Alert;

public class Alerta {
    public static void mostrarAlerta(String texto) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(texto);
        alert.showAndWait();
    }
}
