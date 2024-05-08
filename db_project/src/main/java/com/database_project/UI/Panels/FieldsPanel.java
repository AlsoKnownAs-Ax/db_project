package com.database_project.UI.Panels;

import javax.swing.*;
import java.awt.*;

public class FieldsPanel extends JPanel {
    
    private Color foregroundColor;
    private Color inputsBackground;

    private JTextField txtUsername;
    private JTextField txtPassword;
    private JTextField txtBio;

    public FieldsPanel(Color foregroundColor, Color backgroundColor, Color inputsBackground) {
        this.foregroundColor = foregroundColor;
        this.inputsBackground = inputsBackground;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
        setBackground(backgroundColor);
    }

    private void CreateUsernameField(){
        txtUsername = new JTextField("Username");
        txtUsername.setForeground(foregroundColor);
        txtUsername.setBackground(inputsBackground);
        add(Box.createVerticalStrut(10));
        add(txtUsername);
    }

    private void CreatePasswordField(){
        txtPassword = new JTextField("Password");
        txtPassword.setForeground(foregroundColor);
        txtPassword.setBackground(inputsBackground);
        add(Box.createVerticalStrut(10));
        add(txtPassword);
    }

    private void CreateBioField(){
        txtBio = new JTextField("Bio");
        txtBio.setForeground(foregroundColor);
        txtBio.setBackground(inputsBackground);
        add(Box.createVerticalStrut(10));
        add(txtBio);
    }
    
    public void CreateLoginFileds(){
        CreateUsernameField();
        CreatePasswordField();
    }

    public void CreateRegisterFields(){
        CreateUsernameField();
        CreatePasswordField();
        CreateBioField();
    }

    public String getUsername() {
        return txtUsername.getText();
    }

    public String getPassword() {
        return txtPassword.getText();
    }

    public String getBio() {
        return txtBio.getText();
    }
}