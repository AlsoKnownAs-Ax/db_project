package com.database_project.UI.factory;

import java.awt.Color;

import com.database_project.UI.Panels.ButtonPanel;
import com.database_project.UI.Panels.FieldsPanel;
import com.database_project.UI.Panels.HeaderPanel;
import com.database_project.UI.Panels.NavigationPanel;
import com.database_project.UI.Panels.ProfilePlaceholderPanel;

public class DarkUIFactory extends UIfactory
{
    private Color mainBackground = new Color(20, 22, 23);   //Dark gray
    private Color inputsBackground = new Color(23, 27, 28);
    private Color mainsecondaryBackground = new Color(23, 27, 28);

    private Color followBtnColor = new Color(33, 33, 33);

    // Config for Button Panel
    private Color primaryColor              = new Color(20, 22, 23);     //Dark Gray
    private Color mainButtonBackground      = new Color(172,0,5);       //red

    // Config for Fields Panel
    private Color textForeground = new Color(173, 173, 173);    //Light blue

    //HeaderPanel
    private Color HeaderBackground = new Color(23, 27, 28);
    private Color HeaderForeground = Color.WHITE;

    //Config for Navigation Panel
    private Color NavigationBackground = new Color(51,51,51);

    @Override
    public ButtonPanel createButtonPanel() {
        return new ButtonPanel(primaryColor, textForeground, mainButtonBackground);
    }

    @Override
    public FieldsPanel createFieldsPanel() {
        return new FieldsPanel(textForeground, mainBackground, inputsBackground);
    }

    @Override
    public HeaderPanel createHeaderPanel() {
        return new HeaderPanel("Quackstagram 🐥", textForeground, mainsecondaryBackground);
    }

    @Override
    public NavigationPanel createNavigationPanel() {
        return new NavigationPanel(NavigationBackground);
    }

    @Override
    public ProfilePlaceholderPanel cretProfilePlaceholderPanel(String filePath) {
        return new ProfilePlaceholderPanel(filePath, mainBackground);
    }

    @Override
    public Color getMainBackground() {
        return mainBackground;
    }

    @Override
    public Color getSecondaryColor() {
        return mainsecondaryBackground;
    }

    @Override
    public Color getFollowBtnColor() {
        return followBtnColor;
    }

    @Override
    public Color getPrimaryColor() {
        return primaryColor;
    }

    @Override
    public Color getNormalTextColor() {
        return textForeground;
    }

    @Override
    public Color getHeaderBackground() {
        return HeaderBackground;
    }

    @Override
    public Color getHeaderForeground() {
        return HeaderForeground;
    }
}