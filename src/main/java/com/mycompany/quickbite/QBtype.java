package com.mycompany.quickbite;

import com.mycompany.quickbite.util.AppState;
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
            AppState.setUserType("negocio");
            Navigator.navigateTo("/views/singupN.fxml", "singup_negocio", event);
        }
    }
    
    public void onEstudianteSelected(ActionEvent event){
        if(rbEstudiante.isSelected()){
            AppState.setUserType("estudiante");
            Navigator.navigateTo("/views/singupE.fxml", "singup_estudiante", event);
        }
    }

}
