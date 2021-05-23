package com.intershala.JAVAFX;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable {

    private static final int COLUMNS = 7;
    private static final int ROWS = 7;
    private static final int CIRCLE_DIAMETER = 80;
    private static final String discColor1 = "#24303E";
    private static final String discColor2 = "#4CAA88";

    private static String PLAYER_ONE = "Player One";
    private static String PLAYER_TWO = "Player Two";

    private boolean isPlayerOneturn = true;

    private Disc[][] insertDiscArray = new Disc[ROWS][COLUMNS]; //for structural changes

    @FXML
    public GridPane rootgridpane;

    @FXML
    public Pane insertedDiscpane;

    @FXML
    public Label playerIdlabel;

    @FXML
    public TextField playeronetextfeild , playertwotextfeild;

    @FXML
    public Button setbutton;

    private boolean isAllowed=true; //to avoid the same colour disc insert

    public void createPG() {

        Platform.runLater(() -> setbutton.requestFocus());

        Shape rectangleWithholes = createGSG();
        rootgridpane.add(rectangleWithholes, 0, 1);

        List<Rectangle> rectangleList = createClickableColumn();

        for (Rectangle rectangle : rectangleList) {
            rootgridpane.add(rectangle, 0, 1);
        }
        setbutton.setOnAction(event ->{
          PLAYER_ONE=playeronetextfeild.getText();
          PLAYER_TWO=playertwotextfeild.getText();
          playerIdlabel.setText(isPlayerOneturn ? PLAYER_ONE : PLAYER_TWO);
        });
    }

    private Shape createGSG() {

        Shape rectangleWithholes = new Rectangle((COLUMNS + 1) * CIRCLE_DIAMETER, (ROWS+1) * CIRCLE_DIAMETER);

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {

                Circle circle = new Circle();
                circle.setRadius(CIRCLE_DIAMETER / 2);
                circle.setCenterX(CIRCLE_DIAMETER / 2);
                circle.setCenterY(CIRCLE_DIAMETER / 2);
                circle.setSmooth(true);

                circle.setTranslateX(col * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
                circle.setTranslateY(row * (CIRCLE_DIAMETER + 6) + CIRCLE_DIAMETER / 4);

                rectangleWithholes = Shape.subtract(rectangleWithholes, circle);
            }
        }

        rectangleWithholes.setFill(Color.WHITE);
        return rectangleWithholes;
    }

    private List<Rectangle> createClickableColumn() {

        List<Rectangle> rectangleList = new ArrayList<>();

        for (int col = 0; col < COLUMNS; col++) {
            Rectangle rectangle = new Rectangle(CIRCLE_DIAMETER, (ROWS+1) * CIRCLE_DIAMETER);
            rectangle.setFill(Color.TRANSPARENT);
            rectangle.setTranslateX(col * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);

            rectangle.setOnMouseEntered(event -> rectangle.setFill(Color.valueOf("#eeeeee26")));
            rectangle.setOnMouseExited(event -> rectangle.setFill(Color.TRANSPARENT));

            final int column = col;
            rectangle.setOnMouseClicked(event -> {
                if(isAllowed) {
                    isAllowed=false; //when the disc is being dropped no more disc will be inserted
                    insertDisc(new Disc(isPlayerOneturn), column);
                }
            });

            rectangleList.add(rectangle);
        }

        return rectangleList;
    }

    private void insertDisc(Disc disc, int column) {

        int row = ROWS-1;
        while (row >= 0) {
            if (getDiscIfPresent(row,column) == null)
                break;

            row--;
        }

        if (row < 0) { //if column is full(can't insert more disc)
            return;
        }

        insertDiscArray[row][column] = disc;
        insertedDiscpane.getChildren().add(disc); //in the second pane

        disc.setTranslateX(column * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);

        int currentrow = row;
        TranslateTransition translatetransition = new TranslateTransition(Duration.seconds(0.3), disc);
        translatetransition.setToY(row * (CIRCLE_DIAMETER + 7) + CIRCLE_DIAMETER / 4);
        translatetransition.setOnFinished(event -> {

            isAllowed=true;
            if (gameEnd(currentrow, column)) {
                gameOver();
                return;
            }

            isPlayerOneturn = !isPlayerOneturn;
            playerIdlabel.setText(isPlayerOneturn ? PLAYER_ONE : PLAYER_TWO);
        });

        translatetransition.play();
    }

    private boolean gameEnd(int row, int column) {

        List<Point2D> verticalPoints = IntStream.rangeClosed(row - 3, row + 3) //range of row values=0,1,2,3..
                .mapToObj(r -> new Point2D(r, column)) //0,3 1,3 2,3...
                .collect(Collectors.toList());

        List<Point2D> horizontalPoints = IntStream.rangeClosed(column - 3, column + 3)
                .mapToObj(col -> new Point2D(row, col))
                .collect(Collectors.toList());

        Point2D startPoint1=new Point2D(row-3,column+3);
        List<Point2D> diagonalPoint=IntStream.rangeClosed(0,6)
                                    .mapToObj(i ->startPoint1.add(i,-i))
                                    .collect(Collectors.toList());

        Point2D startPoint2=new Point2D(row-3,column-3);
        List<Point2D> diagonal2Point=IntStream.rangeClosed(0,6)
                                    .mapToObj(i ->startPoint2.add(i,i))
                                    .collect(Collectors.toList());

        boolean isEnded = checkCombination(verticalPoints) || checkCombination(horizontalPoints) ||
                            checkCombination(diagonalPoint) || checkCombination(diagonal2Point);

        return isEnded;
    }

    private boolean checkCombination(List<Point2D> Points) {

        int chain = 0;

        for (Point2D point : Points) {

            int rowIndexForArray = (int) point.getX();
            int columnIndexForArray = (int) point.getY();

            Disc disc = getDiscIfPresent(rowIndexForArray,columnIndexForArray);

            if (disc != null && disc.isplayerone == isPlayerOneturn) { //if the last inserted disc belongs to the current player

                chain++;
                if (chain == 4) {
                    return true;
                }
            } else {
                chain = 0;
            }
        }
        return false;
    }

    private Disc getDiscIfPresent(int row,int column){

        if(row>=ROWS || row<0 || column>=COLUMNS || column<0)
            return null;

        return insertDiscArray[row][column];
    }

    private void gameOver() {

        String winner = isPlayerOneturn ? PLAYER_ONE : PLAYER_TWO;
        System.out.println("Winner is: " + winner);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Connect Four");
        alert.setHeaderText("The Winner is: " + winner);
        alert.setContentText("Want to Play Again? ");

        ButtonType yesbt = new ButtonType("Yes");
        ButtonType nobt = new ButtonType("No, Exit");
        alert.getButtonTypes().setAll(yesbt, nobt);

        Platform.runLater(() -> { //helps us to resolve IllegalStateException

            Optional<ButtonType> btnclicked = alert.showAndWait();
            if (btnclicked.isPresent() && btnclicked.get() == yesbt) {
                resetGame();
            } else {
                Platform.exit();
                System.exit(0);
            }
        });
    }

    public void resetGame() {

        insertedDiscpane.getChildren().clear();

        for (int row = 0; row < insertDiscArray.length; row++) {

            for(int col=0;col< insertDiscArray[row].length;col++){
                insertDiscArray[row][col] = null;
            }
        }
        isPlayerOneturn=true;
        playerIdlabel.setText(PLAYER_ONE);

        createPG();
    }

    private static class Disc extends Circle{

        private final boolean isplayerone;

        public Disc(boolean isplayerone){

            this.isplayerone=isplayerone;
            setRadius(CIRCLE_DIAMETER/2);
            setCenterX(CIRCLE_DIAMETER/2);
            setCenterY(CIRCLE_DIAMETER/2);

            setFill(isplayerone ? Color.valueOf(discColor1)  : Color.valueOf(discColor2));
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}