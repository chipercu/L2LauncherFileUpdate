module com.fuzzy {

    requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.httpcore;
    requires com.google.gson;
    requires org.apache.commons.io;
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.desktop;
    requires net.minidev.jsonsmart;
    requires java.prefs;
    requires jackson.annotations;
    requires jackson.databind;

    requires javafx.swing;
    requires java.logging;
    requires commons.lang3;
    requires com.opencsv;


    opens com.fuzzy to javafx.fxml;
    opens com.fuzzy.clientmod.l2smr to javafx.graphics, javafx.fxml;
    opens com.fuzzy.clientmod.l2tool to javafx.graphics, javafx.fxml;
    opens com.fuzzy.clientmod.l2smr.smview to javafx.graphics, javafx.fxml;
    opens com.fuzzy.fx_controllers to javafx.fxml;
    opens com.fuzzy.windows to javafx.fxml;
    exports com.fuzzy;
    exports com.fuzzy.fx_controllers;
}