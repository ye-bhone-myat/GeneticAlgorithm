import FactoryFloor.Floor;
import FactoryFloor.Tile;
import Machine.Shapes;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class GAApplication extends Application {

    private static final double boxOuterSize = 40;
    private static final double boxInnerSize = 38;

    private static GridPane controlsContainer = new GridPane();
    private static HBox root = new HBox();
    private static Group tilesGroup = new Group();
    private Rectangle[][] rectangles = new Rectangle[16][16];


    @Override
    public void start(Stage mainStage) throws Exception {

        controlsContainer.setHgap(5);
        controlsContainer.setVgap(5);
        root.setSpacing(10);
        controlsContainer.setPadding(new Insets(10, 10, 10, 10));
        double sceneWidth = 1000;
        double sceneHeight = 640;
        Scene applicationScene = new Scene(root, sceneWidth, sceneHeight, Color.GRAY);

//        try {
////            applicationScene = new Scene(controlsContainer, sceneWidth, sceneHeight, Color.BLACK);
//            applicationScene.addEventHandler(EventType.ROOT, (EventHandler<? super Event>) this);
////            applicationScene.setFill(Color.BLACK);
//
//        } catch (Exception exception) {
//            System.out.println("exception : " + exception.getMessage());
//        }

        mainStage.setScene(applicationScene);
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; ++j) {
                Rectangle r = new Rectangle();
                r.setFill(Color.WHITE);
                r.setX(i * boxOuterSize);
                r.setY(j * boxOuterSize);
                r.setWidth(boxInnerSize);
                r.setHeight(boxInnerSize);
                rectangles[i][j] = r;
                tilesGroup.getChildren().add(rectangles[i][j]);
            }
        }
        Label lbl_min_score = new Label("Minimum Score");
        Label lbl_threshold = new Label("Threshold");
        TextField tf_min_score = new TextField();
        TextField tf_threshold = new TextField();
        Text text = new Text();
        controlsContainer.add(text, 0, 4, 2, 1);
        controlsContainer.add(lbl_min_score, 0, 0);
        controlsContainer.add(lbl_threshold, 0, 1);
        controlsContainer.add(tf_min_score, 1, 0);
        controlsContainer.add(tf_threshold, 1, 1);
        root.getChildren().add(tilesGroup);
        root.getChildren().add(controlsContainer);
        Button startButton = new Button("Start");

        int processorCount = Runtime.getRuntime().availableProcessors();
        final int NSOLUTIONS = (processorCount >= 32) ? processorCount : 3;
        final int nThreads = NSOLUTIONS;
        startButton.setOnAction(event -> {
            Thread t = new Thread(() -> {
                int roomWidth = 16;
                int roomHeight = 16;

                ArrayList<Floor> floors = new ArrayList<>();
                for (int j = 0; j < NSOLUTIONS; ++j) {
                    floors.add(new Floor(roomWidth, roomHeight));
                }
                FloorsCallable c = new FloorsCallable(floors, roomHeight, roomWidth, nThreads,
                        Integer.parseInt(String.valueOf(tf_min_score.getCharacters()))
                        , Integer.parseInt(String.valueOf(tf_threshold.getCharacters()))
                        , false);
                ExecutorService executor = Executors.newSingleThreadExecutor();
                Future<Result> result = executor.submit(c);
                Result interimResult = c.getUpdatedResult();
                int generation = interimResult.getGeneration();
                int score = interimResult.getScore();
                ArrayList<Tile> tiles;
//                    System.out.println("Generation " + generation + " score " + score);
                CountDownLatch latch;
                while (!result.isDone()) {
//                        tiles = interimResult.getMachines().stream()
////                                .filter(Objects::nonNull)
//                                .flatMap(machine -> machine.getGrids().stream())
//                                .collect(Collectors.toCollection(ArrayList::new));

                    latch = new CountDownLatch(interimResult.getTileCount());
                    CountDownLatch finalLatch = latch;
                    int finalGeneration = generation;
                    int finalScore = score;
                    Platform.runLater(() -> {
                        text.setText("Generation: " + finalGeneration + "\n"
                                + "Score: " + finalScore);
                    });
                    Platform.runLater(new tileColorSetter(interimResult, finalLatch));
//                        tiles.forEach(result ->
//                                Platform.runLater(new tileColorSetter(result, finalLatch)));
                    try {
                        Thread.sleep(500);
                        latch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    interimResult = c.getUpdatedResult();
                    generation = interimResult.getGeneration();
                    score = interimResult.getScore();
                    for (Rectangle[] ra : rectangles) {
                        for (Rectangle r : ra) {
                            Platform.runLater(() -> r.setFill(Color.WHITE));
                        }
                    }
                }
                executor.shutdown();
                Result r = null;

                try {
                    r = result.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }

                latch = new CountDownLatch(r.getTileCount());
                CountDownLatch finalLatch1 = latch;
                Platform.runLater(new tileColorSetter(r, finalLatch1));
                generation = r.getGeneration();
                score = r.getScore();
                double durationSeconds = ((double) r.getDuration()) / 1000;
                int finalGeneration1 = generation;
                int finalScore1 = score;
                Platform.runLater(() -> {
                    text.setText("=== Completed ===\n"
                            + "Generation: " + finalGeneration1 + "\n"
                            + "Score: " + finalScore1 + "\n"
                            + "Duration: " + durationSeconds + " seconds");
                });
//                    System.out.println("Score: " + r.getScore());
//                    System.out.println("Generation " + generation);
//                    System.out.println("Time elapsed: " + durationSeconds + " seconds");
            });
            t.setDaemon(true);
            t.start();
        });
        controlsContainer.add(startButton, 0, 2);

        mainStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    class tileColorSetter implements Runnable {

        private Result result;
        private CountDownLatch latch;

        tileColorSetter(Result result, CountDownLatch latch) {
            this.result = result;
            this.latch = latch;
        }

        @Override
        public void run() {
            for (int i = 0; i < result.getTileCount(); ++i) {
                int x = result.getxVals()[i];
                int y = result.getyVals()[i];
                rectangles[y][x].setFill(getColorForShape(result.getShapes()[i]));
                latch.countDown();
            }
        }

        private Color getColorForShape(Shapes s) {
            Color color = null;
            switch (s) {
                case ElbowL:
                    color = Color.CORAL;
                    break;
                case ElbowR:
                    color = Color.CADETBLUE;
                    break;
                case KinkL:
                    color = Color.DARKGRAY;
                    break;
                case KinkR:
                    color = Color.DARKOLIVEGREEN;
                    break;
                case Rod:
                    color = Color.DARKKHAKI;
                    break;
                case Square:
                    color = Color.DEEPPINK;
                    break;
            }
            return color;
        }

    }
}
