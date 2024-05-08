package com.database_project.UI.factory;

import java.awt.Color;

import com.database_project.UI.Panels.ButtonPanel;
import com.database_project.UI.Panels.FieldsPanel;
import com.database_project.UI.Panels.HeaderPanel;
import com.database_project.UI.Panels.NavigationPanel;
import com.database_project.UI.Panels.ProfilePlaceholderPanel;

public abstract class UIfactory {
    public abstract ButtonPanel createButtonPanel();
    public abstract FieldsPanel createFieldsPanel();
    public abstract HeaderPanel createHeaderPanel();
    public abstract NavigationPanel createNavigationPanel();
    public abstract ProfilePlaceholderPanel cretProfilePlaceholderPanel(String filePath);
    public abstract Color getMainBackground();
    public abstract Color getSecondaryColor();
    public abstract Color getFollowBtnColor();
    public abstract Color getPrimaryColor();
    public abstract Color getNormalTextColor();
    public abstract Color getHeaderBackground();
    public abstract Color getHeaderForeground();
}