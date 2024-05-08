package com.database_project.UI.Panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ButtonPanel extends JPanel {
    private Color backgroundColor;
    private Color foregroundColor;
    private Color secondaryBackgroundColor;

    public ButtonPanel(Color backgroundColor, Color foregroundColor, Color secondaryBackgroundColor) {
        this.backgroundColor = backgroundColor;
        this.foregroundColor = foregroundColor;
        this.secondaryBackgroundColor = secondaryBackgroundColor;
        setLayout(new GridLayout(2, 1, 10, 10));
        setBackground(backgroundColor);
    }

    public void CreateLoginButtons(ActionListener FirstButtonListener, ActionListener SecondButtonListener){
        JButton firstButton = new JButton("Sign-In");
        firstButton.addActionListener(FirstButtonListener);
        firstButton.setBackground(this.secondaryBackgroundColor);
        firstButton.setForeground(this.foregroundColor);
        firstButton.setFocusPainted(false);
        firstButton.setBorderPainted(false);
        firstButton.setFont(new Font("Arial", Font.BOLD, 14));

        JButton secondaryButton = new JButton("No Account? Register Now");
        secondaryButton.addActionListener(SecondButtonListener);
        secondaryButton.setBackground(this.backgroundColor);
        secondaryButton.setForeground(this.foregroundColor);
        secondaryButton.setFocusPainted(false);
        secondaryButton.setBorderPainted(false);

        add(firstButton);
        add(secondaryButton);
    }
    

    public void CreateRegisterButtons(ActionListener FirstButtonListener, 
                                      ActionListener SecondButtonListener,
                                      ActionListener ThirdButtonListener)
    {
        setLayout(new GridLayout(3, 1, 10, 10));
        
        // Register button with black text
        JButton registerBTN = new JButton("Register");
        registerBTN.addActionListener(SecondButtonListener);
        registerBTN.setBackground(this.secondaryBackgroundColor); // Use a red color that matches the mockup
        registerBTN.setForeground(this.foregroundColor); // Set the text color to black
        registerBTN.setFocusPainted(false);
        registerBTN.setBorderPainted(false);
        registerBTN.setFont(new Font("Arial", Font.BOLD, 14));
        
        JButton uploadPhotoBTN = new JButton("Upload Photo");
        uploadPhotoBTN.addActionListener(FirstButtonListener);
        JPanel photoUploadPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        photoUploadPanel.setBackground(this.backgroundColor); // Use a red color that matches the mockup
        photoUploadPanel.setForeground(this.foregroundColor); // Set the text color to black
        photoUploadPanel.add(uploadPhotoBTN);
       
        JButton secondaryButton = new JButton("Already have an account? Sign In");
        secondaryButton.addActionListener(ThirdButtonListener);
        secondaryButton.setBackground(this.backgroundColor);
        secondaryButton.setForeground(this.foregroundColor);
        secondaryButton.setFocusPainted(false);
        secondaryButton.setBorderPainted(false);

        add(photoUploadPanel);
        add(registerBTN);
        add(secondaryButton, BorderLayout.CENTER);
    }
}
