package com.database_project.UI.factory;

import java.awt.Color;

import com.database_project.UI.Panels.ButtonPanel;
import com.database_project.UI.Panels.FieldsPanel;
import com.database_project.UI.Panels.HeaderPanel;
import com.database_project.UI.Panels.NavigationPanel;
import com.database_project.UI.Panels.ProfilePlaceholderPanel;

public class LightUIFactory extends UIfactory {
    
    private Color mainBackground = new Color(238, 238, 238);
    private Color mainsecondaryBackground = new Color(255, 255, 255);
    private Color inputsBackground = new Color(255, 255, 255);

    private Color followBtnColor = new Color(225, 228, 232);

    //ButtonPanel
    private Color primaryColor = new Color(249,249,249);
    private Color secondaryColor = Color.BLACK;
    private Color MainButtonBackground = new Color(255,90,95);

    //FieldsPanel
    private Color TextForeground = Color.GRAY;

    //NavigationPanel
    private Color NavigationBackground = new Color(249,249,249);

    //HeaderPanel
    private Color HeaderBackground = new Color(51,51,51);
    private Color HeaderForeground = Color.WHITE;

    @Override
    public ButtonPanel createButtonPanel(){
        return new ButtonPanel(primaryColor, secondaryColor, MainButtonBackground);
    }

    @Override
    public FieldsPanel createFieldsPanel(){
        return new FieldsPanel(TextForeground, mainBackground, inputsBackground);
    }

    @Override
    public NavigationPanel createNavigationPanel(){
        return new NavigationPanel(NavigationBackground);
    }

    @Override
    public HeaderPanel createHeaderPanel(){
        return new HeaderPanel("Quackstagram 🐥", HeaderForeground, HeaderBackground);
    }

    @Override
    public ProfilePlaceholderPanel cretProfilePlaceholderPanel(String filePath) {
        return new ProfilePlaceholderPanel("./assets/logos/DACS.png", mainBackground);
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
        return TextForeground;
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
