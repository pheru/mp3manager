/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.eru.mp3manager;

import de.eru.mp3manager.gui.applicationwindow.application.ApplicationPresenter;
import de.eru.mp3manager.gui.applicationwindow.application.ApplicationView;
import de.eru.pherufx.cdi.StartApplication;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

/**
 *
 * @author Philipp
 */
public class ApplicationStarter {

    @Inject
    private ApplicationView applicationView;
    
    private void launchJavaFXApplication(@Observes @StartApplication Stage primaryStage) {
        ApplicationPresenter applicationPresenter = (ApplicationPresenter) applicationView.getPresenter();
        applicationPresenter.setPrimaryStage(primaryStage); //TODO Kann nun hier erledigt werden, statt im Presenter

        //TODO Test-Stage
        Rectangle rect = new Rectangle(400, 100);
        rect.setFill(Color.RED);
        rect.setArcHeight(15.0);
        rect.setArcWidth(50.0);
        Group group = new Group(rect);
        Stage stage = new Stage(StageStyle.TRANSPARENT);
        stage.setScene(new Scene(group, Color.TRANSPARENT));
//        stage.show();
    }
}
