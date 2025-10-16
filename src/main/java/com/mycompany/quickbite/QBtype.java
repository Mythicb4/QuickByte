package com.mycompany.quickbite;

import com.mycompany.quickbite.util.Navigator;

import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class QBtype {

    @FXML
    private Button btnBack;

    @FXML
    private ImageView logoImage;

    @FXML
    private RadioButton rbEstudiante;

    @FXML
    private RadioButton rbNegocio;

    @FXML
    private ToggleGroup typeGroup;
    
    public void onBackClick(ActionEvent event){
        Navigator.navigateTo("/views/login.fxml", "login", event);
    }
    
    public void onNegocioSelected(ActionEvent event){
        if(rbNegocio.isSelected()){
            System.out.print("Cargar vista de negocio");
        }
    }
    
    public void onEstudianteSelected(ActionEvent event){
        if(rbEstudiante.isSelected()){
            System.out.print("Cargar vista de estudiante");
        }
    }

}

