package com.intershala.JAVAFX;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {
        private Controller controller;

        @Override
        public void start(Stage primaryStage) throws Exception {

                FXMLLoader loader = new FXMLLoader(getClass().getResource("AppLayout.fxml"));
                GridPane rootGridpane = loader.load();

                controller = loader.getController();
                controller.createPG();

                MenuBar menubar=createMenu();
                menubar.prefWidthProperty().bind(primaryStage.widthProperty());

                Pane menupane= (Pane) rootGridpane.getChildren().get(0);
                menupane.getChildren().add(menubar);

                Scene scene=new Scene(rootGridpane);

                primaryStage.setScene(scene);
                primaryStage.setTitle("Connect Four");
                primaryStage.setResizable(false);
                primaryStage.show();
        }

        private MenuBar createMenu(){
                Menu filemenu=new Menu("File");

                MenuItem newgame=new MenuItem("New Game");
                newgame.setOnAction(event ->controller.resetGame());

                MenuItem resetgame=new MenuItem("Reset Game");
                resetgame.setOnAction(event ->controller.resetGame());

                SeparatorMenuItem smi=new SeparatorMenuItem();
                MenuItem exitgame=new MenuItem("Exit Game");
                exitgame.setOnAction(event ->exitGame());

                filemenu.getItems().addAll(newgame,resetgame,smi,exitgame);

                Menu helpmenu=new Menu("Help");

                MenuItem aboutgame=new MenuItem("About Connect4");
                aboutgame.setOnAction(event ->aboutGame());

                SeparatorMenuItem seprator=new SeparatorMenuItem();
                MenuItem aboutme=new MenuItem("About Me");
                aboutme.setOnAction(event ->aboutMe());
                

                helpmenu.getItems().addAll(aboutgame,seprator,aboutme);

                MenuBar menubar=new MenuBar();
                menubar.getMenus().addAll(filemenu,helpmenu);

                return menubar;
        }

        private void aboutMe() {
                Alert alert=new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("About The Developer");
                alert.setHeaderText("Sanskriti Agrawal");
                alert.setContentText("I love to play this Game.Connect four is one of the most entertaining game!");

                alert.show();
        }

        private void aboutGame() {
                Alert alert=new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("About Connect Four Game");
                alert.setHeaderText("How To Play?");
                alert.setContentText("Connect Four is a two-player connection game in which the players first choose a color and then take turns dropping colored discs from the top into a seven-column, six-row vertically suspended grid. The pieces fall straight down, occupying the next available space within the column. The objective of the game is to be the first to form a horizontal, vertical, or diagonal line of four of one's own discs. Connect Four is a solved game." +
                        " The first player can always win by playing the right moves.");

                alert.show();
        }

        private void exitGame() {
                Platform.exit();
                System.exit(0);
        }

        private void resetgame() {

        }

        public static void main(String[] args) {
                launch(args);
        }
}



