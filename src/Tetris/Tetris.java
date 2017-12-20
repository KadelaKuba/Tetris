
package Tetris;

import java.awt.Point;
import java.util.Random;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.swing.*;

public class Tetris extends Application {

    private final Point[][][] Shapes = {
            // I
            {
                    {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(3, 1)},
                    {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(1, 3)},
                    {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(3, 1)},
                    {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(1, 3)}
            },

            // J
            {
                    {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(2, 0)},
                    {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(2, 2)},
                    {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(0, 2)},
                    {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(0, 0)}
            },

            // L
            {
                    {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(2, 2)},
                    {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(0, 2)},
                    {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(0, 0)},
                    {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(2, 0)}
            },

            // O
            {
                    {new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1)},
                    {new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1)},
                    {new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1)},
                    {new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1)}
            },

            // S
            {
                    {new Point(1, 0), new Point(2, 0), new Point(0, 1), new Point(1, 1)},
                    {new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2)},
                    {new Point(1, 0), new Point(2, 0), new Point(0, 1), new Point(1, 1)},
                    {new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2)}
            },

            // T
            {
                    {new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(2, 1)},
                    {new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2)},
                    {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(1, 2)},
                    {new Point(1, 0), new Point(1, 1), new Point(2, 1), new Point(1, 2)}
            },

            // Z
            {
                    {new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(2, 1)},
                    {new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(0, 2)},
                    {new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(2, 1)},
                    {new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(0, 2)}
            }
    };

    private final Color[] colors = {
            Color.DARKCYAN, Color.DARKBLUE, Color.DARKORANGE, Color.YELLOW, Color.DARKGREEN, Color.DEEPPINK, Color.DARKRED
    };

    private final int grid_x = 15;
    private final int grid_y = 20;
    private Timeline action;
    private Rectangle[][] grid;
    private Rectangle[][] well;

    private Point pieceOrigin;
    private int currentPiece;
    private int rotation;

    private Button btnStart = new Button();

    /**
     * Tetris constructor
     */
    public Tetris() {

        Random randomPiece = new Random();
        Random randomRotation = new Random();

        pieceOrigin = new Point(5, 0);
        currentPiece = randomPiece.nextInt(7);
        rotation = randomRotation.nextInt(4);
    }

    @Override
    public void start(Stage primaryStage) {
        AnchorPane basePane = new AnchorPane();
        btnStart.setText("Start game");
        btnStart.setOnAction(event -> {
            if (action.getStatus() == Animation.Status.RUNNING) {
                action.stop();
                btnStart.setText("Start game");
            } else {
                action.play();
                btnStart.setText("Stop game");
            }
        });
        basePane.getChildren().add(btnStart);
        AnchorPane.setTopAnchor(btnStart, 1.0);
        AnchorPane.setLeftAnchor(btnStart, 1.0);
        AnchorPane.setRightAnchor(btnStart, 1.0);

        Pane root = new Pane();
        basePane.getChildren().add(root);
        AnchorPane.setBottomAnchor(root, 1.0);
        AnchorPane.setLeftAnchor(root, 1.0);
        AnchorPane.setRightAnchor(root, 1.0);
        AnchorPane.setTopAnchor(root, 30.0);

        grid = new Rectangle[grid_x][grid_y];
        well = new Rectangle[grid_x][grid_y];

        // this binding will find out which parameter is smaller: height or width
        NumberBinding rectSize = Bindings.min(root.heightProperty().divide(grid_y), root.widthProperty().divide(grid_x));

        for (int x = 0; x < grid_x; x++) {
            for (int y = 0; y < grid_y; y++) {
                grid[x][y] = new Rectangle();
                grid[x][y].setStroke(Color.WHITE);
                grid[x][y].setFill(Color.BLACK);

                well[x][y] = new Rectangle();
                well[x][y].setFill(Color.BLACK);

                // here we position rects (this depends on pane size as well)
                grid[x][y].xProperty().bind(rectSize.multiply(x));
                grid[x][y].yProperty().bind(rectSize.multiply(y));

                // here we bind rectangle size to pane size
                grid[x][y].heightProperty().bind(rectSize);
                grid[x][y].widthProperty().bind(grid[x][y].heightProperty());

                root.getChildren().add(grid[x][y]);
            }
        }
        Scene scene = new Scene(basePane, 400, 560);
        scene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.UP) {
                rotate(-1);
            }
            if (event.getCode() == KeyCode.DOWN) {
                rotate(+1);
            }
            if (event.getCode() == KeyCode.LEFT) {
                move(-1);
            }
            if (event.getCode() == KeyCode.RIGHT) {
                move(+1);
            }
            if (event.getCode() == KeyCode.ENTER) {
                dropDown();
            }
        });

        action = new Timeline(new KeyFrame(Duration.millis(300), event -> {
            drawWell();
            dropDown();
        }));
        action.setCycleCount(Timeline.INDEFINITE);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Inicializace nového tvaru
     */
    private void newShape() {
        Random randomPiece = new Random();
        Random randomRotation = new Random();

        pieceOrigin = new Point(5, 0);
        currentPiece = randomPiece.nextInt(7);
        rotation = randomRotation.nextInt(4);

        if (isCollision(pieceOrigin.x, pieceOrigin.y + 1, rotation)) {
            action.stop();
            btnStart.setDisable(true);
            JOptionPane.showMessageDialog(null, "Game over. To start again reopen the app");
        } else {
            drawWell();
        }
    }

    /**
     * Funkce pro změnu pohybu
     *
     * @param i posunutí
     */
    private void move(int i) {
        if (!isOutOfGrid(pieceOrigin.x + i, rotation) && !isCollision(pieceOrigin.x + i, pieceOrigin.y, rotation)) {
            pieceOrigin.x += i;
            drawWell();
        }
    }

    /**
     * Funkce pro změnu rotace
     *
     * @param i rotace
     */
    private void rotate(int i) {
        int newRotation = (rotation + i) % 4;

        if (newRotation < 0) {
            newRotation = 3;
        }

        if (!isOutOfGrid(pieceOrigin.x, newRotation)) {
            rotation = newRotation;
        }
        drawWell();
    }

    /**
     * Funkce pro pohyb tvaru směrem dolů
     */
    private void dropDown() {
        if (!isBottomOfGrid(pieceOrigin.y + 1, rotation) && !isCollision(pieceOrigin.x, pieceOrigin.y + 1, rotation)) {
            pieceOrigin.y += 1;
            drawWell();
        } else {
            saveToWell();
            clearRows();
            newShape();
        }
    }

    /**
     * Funkce pro zjištění, zda se při dalším pohybu tvaru stane kolize
     *
     * @param x        x-ová souřadnice
     * @param y        y-ová souřadnice
     * @param rotation rotace
     * @return boolean
     */
    private boolean isCollision(int x, int y, int rotation) {
        for (Point p : Shapes[currentPiece][rotation]) {
            if (well[p.x + x][p.y + y].getFill() != Color.BLACK) {

                return true;
            }
        }
        return false;
    }

    /**
     * Funkce pro zjištění, zda se při dalším pohybu tvaru nedostane mimo pole
     *
     * @param x        x-ová souřadnice
     * @param rotation rotace
     * @return boolean
     */
    private boolean isOutOfGrid(int x, int rotation) {
        for (Point point : Shapes[currentPiece][rotation]) {
            if ((x + point.x) < 0 || (x + point.x) > 14) {
                return true;
            }
        }
        return false;
    }

    /**
     * Funkce pro zjištění, zda se při dalším pohybu tvaru nedostane pod pole
     *
     * @param y        y-ová souřadnice
     * @param rotation rotace
     * @return boolean
     */
    private boolean isBottomOfGrid(int y, int rotation) {
        for (Point point : Shapes[currentPiece][rotation]) {
            if ((y + point.y) > 19) {
                return true;
            }
        }
        return false;
    }

    /**
     * Funkce pro vykreslení jednoho tvaru
     */
    private void drawShape() {
        for (Point point : Shapes[currentPiece][rotation]) {
            grid[pieceOrigin.x + point.x][pieceOrigin.y + point.y].setFill(colors[currentPiece]);
        }
    }

    /**
     * Funkce pro vykreslení uložených tvarů
     */
    private void drawWell() {
        for (int x = 0; x < grid_x; x++) {
            for (int y = 0; y < grid_y; y++) {
                grid[x][y].setFill(well[x][y].getFill());
            }
        }
        drawShape();
    }

    /**
     * Funkce pro uložení tvaru do pole
     */
    private void saveToWell() {
        for (Point point : Shapes[currentPiece][rotation]) {
            well[pieceOrigin.x + point.x][pieceOrigin.y + point.y].setFill(colors[currentPiece]);
        }
    }

    /**
     * Funkce pro vymazání řádku a posunutí celho gridu o jeden dolů
     *
     * @param row řádek
     */
    private void deleteRow(int row) {
        for (int j = row; j > 0; j--) {
            for (int i = 0; i < grid_x; i++) {
                well[i][j].setFill(well[i][j - 1].getFill());
            }
        }
    }

    /**
     * Funkce pro zjištění, zda se má řádek smazat
     */
    private void clearRows() {
        boolean emptyBlock;

        for (int j = grid_y - 1; j >= 0; j--) {
            emptyBlock = false;
            for (int i = 0; i < grid_x - 1; i++) {
                if (well[i][j].getFill() == Color.BLACK) {
                    emptyBlock = true;
                    break;
                }
            }
            if (!emptyBlock) {
                deleteRow(j);
                j += 1;
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
