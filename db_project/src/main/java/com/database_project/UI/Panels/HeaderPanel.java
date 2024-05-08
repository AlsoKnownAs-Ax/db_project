package com.database_project.UI.Panels;

import javax.swing.*;

import com.database_project.UI.Config.GlobalConfig;

import java.awt.*;

public class HeaderPanel extends JPanel {
    private Color backgroundColor;
    private Color foregroundColor;

    public HeaderPanel(String title, Color foregroundColor, Color backgroundColor) {
        this.foregroundColor = foregroundColor;
        this.backgroundColor = backgroundColor;
        setLayout(new FlowLayout(FlowLayout.CENTER));
        setBackground(this.backgroundColor);
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setForeground(this.foregroundColor);
        add(lblTitle);
        setPreferredSize(new Dimension(GlobalConfig.getWidth(), 40));
    }
    
}
