package com.database_project.UI.Panels;

import javax.swing.*;

import java.awt.*;
import java.net.URL;

public class ProfilePlaceholderPanel extends JPanel {
    private Color backgrounColor;

    private JLabel lblPhoto;

    public ProfilePlaceholderPanel(String imagePath,Color backgrounColor) {
        this.backgrounColor = backgrounColor;
        setLayout(new BorderLayout());
        lblPhoto = new JLabel();
        add(lblPhoto, BorderLayout.CENTER);

        lblPhoto.setHorizontalAlignment(JLabel.CENTER); // horizontal alignment
        lblPhoto.setVerticalAlignment(JLabel.CENTER); // vertical alignment

        loadImage(imagePath);
        setBackground(this.backgrounColor);
    }

    private void loadImage(String imagePath) {
        URL imageUrl = getClass().getResource(imagePath);
        
        if (imageUrl != null) {
            ImageIcon imageIcon = new ImageIcon(new ImageIcon(imageUrl).getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH));
            lblPhoto.setIcon(imageIcon);
        } else {
            System.err.println("Image not found: " + imagePath);
        }

        lblPhoto.revalidate();
        lblPhoto.repaint();
    }
}