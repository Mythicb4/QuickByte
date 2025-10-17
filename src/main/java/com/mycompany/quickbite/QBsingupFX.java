package com.mycompany.quickbite;

import com.mycompany.quickbite.util.Navigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class QBsingupFX {

    @FXML
    private Button btnBack;

    @FXML
    private Button btnCrear;

    @FXML
    private Label lblLogin;

    @FXML
    private ImageView logoImage;

    @FXML
    private PasswordField tfPassword;

    @FXML
    private TextField txtBussines;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtLocation;

    @FXML
    private ImageView userImage;

    @FXML
    private ImageView userImage1;

    @FXML
    private ImageView userImage2;

    @FXML
    private VBox vbLogin;

    @FXML
    private ImageView visibilityImage;

    @FXML
    private void onBackClick(ActionEvent event) {
        Navigator.navigateTo("/views/type.fxml", "type", event);
    }
    
}
