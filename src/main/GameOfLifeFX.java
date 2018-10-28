package main;

import java.util.Random;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GameOfLifeFX extends Application {
	private Timeline tl;
	private Canvas c;
	private GraphicsContext gc;
	
	private boolean[][] cells = new boolean[100][100];
	
	private double w, h;

	public static void main(String[] args) {
		launch(args);
	}

	public void init() throws Exception {
		tl = new Timeline(new KeyFrame(Duration.millis(1000.0 / 60.0), e -> {
			draw();
		}));
		tl.setCycleCount(Timeline.INDEFINITE);
		tl.play();
	}

	@Override
	public void start(Stage stage) throws Exception {
		BorderPane p = new BorderPane();
		Scene s = new Scene(p, 1000, 500);
		c = new Canvas(s.getWidth(), s.getHeight());
		gc = c.getGraphicsContext2D();
		
		p.setCenter(c);
		
		s.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent e) {
				if (e.getCode() == KeyCode.SPACE) {
					rebuild();
				}
			}
		});
		
		s.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				final int i = (int) (e.getX() / w);
				final int n = (int) (e.getY() / h);
				cells[i][n] = !cells[i][n];
			}
		});

		s.widthProperty().addListener((obsv, oldVal, newVal) -> {
			c.setWidth(newVal.doubleValue());
			w = c.getWidth() / cells.length;
			
		});

		s.heightProperty().addListener((obsv, oldVal, newVal) -> {
			c.setHeight(newVal.doubleValue());
			h = c.getHeight() / cells[0].length;
		});

		stage.setScene(s);
		stage.show();
		
		//setup
		setup();
	}
	
	private void setup() {
		w = c.getWidth() / cells.length;
		h = c.getHeight() / cells[0].length;
		rebuild();
	}

	private void draw() {
		gc.clearRect(0, 0, c.getWidth(), c.getHeight());
		for (int i = 0; i < cells.length; i++) {
			for (int n = 0; n < cells[i].length; n++) {
				if (cells[i][n] == true) {
					gc.setFill(Color.YELLOW);
				} else {
					gc.setFill(Color.BLUE);
				}
				gc.fillRect(i * w, n * h, w, h);
				gc.strokeLine(i * w, n * h, i * w + w, n * h); //oben
				gc.strokeLine(i * w + w, n * h, i * w + w, n * h + h); //rechts
				gc.strokeLine(i * w + w, n * h + h, i * w, n * h + h); //unten
				gc.strokeLine(i * w, n * h + h, i * w, n * h); //links
			}
		}
		changeLife();
	}
	
	private void rebuild() {
		Random r = new Random();
		for (int i = 0; i < cells.length; i++) {
			for (int n = 0; n < cells[i].length; n++) {
				cells[i][n] = r.nextDouble() < 0.5 ? true : false;
			}
		}
	}
	
	private boolean[][] changeLife() {
		boolean altZellen [] [] = new boolean [cells.length] [];
		for (int i = 0;i < cells.length;i++) {
			altZellen[i] = cells[i].clone();
		}
		
		for (int i = 0;i < cells.length;i++) {
			for (int n = 0;n < cells[0].length;n++) {
				cells[i][n] = checkLife(altZellen, i, n);
			}
		}
		return cells;
	}
	
	private boolean checkLife(boolean zellen[][], int x, int y) {
		byte leben = 0;
		for (int i = x - 1;i <= x + 1;i++) {
			for (int n = y - 1;n <= y + 1;n++) {
				try {
					if (zellen[i][n] == true && (i != x || n != y)) {
						leben++;
					}
				} catch(Exception ex) { }
			}
		}
		
		if (leben == 2 ) {
			return zellen[x][y];
		} else if (leben == 3) {
			return true;
		}
		return false;
	}
}