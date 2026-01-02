package com.example.course4;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * CheckersGame.java
 * 
 * Features:
 * - Ukrainian Localization
 * - Game Over / Win Detection
 * - Multi-Jump (Chain Captures)
 * - Game Timer
 * - Pleasant UI
 */
public class CheckersGame extends Application {

    public static final int TILE_SIZE = 80;
    public static final int BOARD_SIZE = 8;
    public static final int WIDTH = BOARD_SIZE * TILE_SIZE;
    public static final int HEIGHT = BOARD_SIZE * TILE_SIZE;

    // --- Theme Colors ---
    private static final Color COL_LIGHT = Color.web("#F5DEB3"); // Wheat
    private static final Color COL_DARK = Color.web("#8B4513");  // SaddleBrown
    private static final Color P1_COLOR = Color.WHITE;
    private static final Color P2_COLOR = Color.web("#D32F2F");  // Red
    private static final Color BG_DARK = Color.web("#121212");
    private static final Color PANEL_BG = Color.web("#1E1E1E");
    private static final Color HIGHLIGHT_FORCE = Color.GOLD;
    private static final Color HIGHLIGHT_MOVE = Color.web("#7CFC00");

    private Tile[][] board = new Tile[BOARD_SIZE][BOARD_SIZE];
    private Group tileGroup = new Group();
    private Group pieceGroup = new Group();
    private Group highlightGroup = new Group();

    // Game State
    private boolean whiteTurn = true;
    private Piece selectedPiece = null;
    private List<Piece> forcedPieces = new ArrayList<>();
    private Map<String, Move> activeMoveMap = new HashMap<>(); 

    // Timer
    private Timeline gameTimer;
    private int secondsElapsed = 0;

    // Player Data
    private String roomNameStr = "Кімната 1";
    private String p1NameStr = "Гравець 1";
    private String p2NameStr = "Гравець 2";

    private Stage window;
    private Scene gameScene, startScene;
    private Label turnLabel;
    private Label roomLabel;
    private Label statusLabel;
    private Label timerLabel;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        window = primaryStage;
        window.setTitle("Шашки");

        createStartScene();
        createGameScene();

        window.setScene(startScene);
        window.show();
    }

    // ==========================================
    //               SCENES
    // ==========================================

    private void createStartScene() {
        VBox root = new VBox(25);
        root.setAlignment(Pos.CENTER);
        
        // Pleasant Gradient Background
        Stop[] stops = new Stop[] { new Stop(0, Color.web("#2b5876")), new Stop(1, Color.web("#4e4376")) };
        LinearGradient lg = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, stops);
        root.setBackground(new Background(new BackgroundFill(lg, CornerRadii.EMPTY, Insets.EMPTY)));
        
        root.setPadding(new Insets(40));

        Text title = new Text("ШАШКИ");
        title.setFont(Font.font("Impact", 60));
        title.setFill(Color.WHITE);
        title.setEffect(new DropShadow(20, Color.BLACK));

        // Form
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);

        TextField roomField = styleField(new TextField("Кімната №1"));
        TextField p1Field = styleField(new TextField("Гравець 1"));
        TextField p2Field = styleField(new TextField("Гравець 2"));

        addFormRow(grid, "Назва кімнати:", roomField, 0);
        addFormRow(grid, "Білі (Гравець 1):", p1Field, 1);
        addFormRow(grid, "Червоні (Гравець 2):", p2Field, 2);

        Button startBtn = new Button("РОЗПОЧАТИ ГРУ");
        styleBtn(startBtn, Color.SEAGREEN);
        startBtn.setPrefWidth(220);
        startBtn.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        Label errorLbl = new Label();
        errorLbl.setTextFill(Color.ORANGE);
        errorLbl.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        startBtn.setOnAction(e -> {
            if (roomField.getText().isEmpty() || p1Field.getText().isEmpty() || p2Field.getText().isEmpty()) {
                errorLbl.setText("Будь ласка, заповніть всі поля!");
                return;
            }
            roomNameStr = roomField.getText();
            p1NameStr = p1Field.getText();
            p2NameStr = p2Field.getText();
            
            resetGame();
            window.setScene(gameScene);
        });

        root.getChildren().addAll(title, grid, startBtn, errorLbl);
        startScene = new Scene(root, 600, 500);
    }

    private void addFormRow(GridPane grid, String labelText, TextField field, int row) {
        Label lbl = new Label(labelText);
        lbl.setTextFill(Color.WHITE);
        lbl.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
        lbl.setEffect(new DropShadow(2, Color.BLACK));
        grid.add(lbl, 0, row);
        grid.add(field, 1, row);
    }

    private void createGameScene() {
        BorderPane root = new BorderPane();
        root.setBackground(new Background(new BackgroundFill(BG_DARK, CornerRadii.EMPTY, Insets.EMPTY)));

        // --- Board Area ---
        Pane boardPane = new Pane();
        boardPane.setPrefSize(WIDTH, HEIGHT);
        boardPane.getChildren().addAll(tileGroup, highlightGroup, pieceGroup);
        
        StackPane boardContainer = new StackPane(boardPane);
        boardContainer.setPadding(new Insets(20));
        boardPane.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 20, 0, 0, 0);");
        
        root.setCenter(boardContainer);

        // --- Sidebar ---
        VBox sidebar = new VBox(20);
        sidebar.setPrefWidth(260);
        sidebar.setBackground(new Background(new BackgroundFill(PANEL_BG, CornerRadii.EMPTY, Insets.EMPTY)));
        sidebar.setPadding(new Insets(25));
        sidebar.setAlignment(Pos.TOP_CENTER);
        sidebar.setStyle("-fx-border-color: #333; -fx-border-width: 0 0 0 2;");

        // Room Info
        roomLabel = new Label("КІМНАТА");
        roomLabel.setTextFill(Color.GRAY);
        roomLabel.setFont(Font.font("Arial", 12));
        
        Label roomVal = new Label();
        roomVal.textProperty().bind(new javafx.beans.property.SimpleStringProperty(roomNameStr)); 
        roomVal.setTextFill(Color.WHITE);
        roomVal.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        // Timer
        Label timeTitle = new Label("ЧАС ГРИ");
        timeTitle.setTextFill(Color.GRAY);
        timeTitle.setFont(Font.font("Arial", 10));
        
        timerLabel = new Label("00:00");
        timerLabel.setTextFill(Color.LIGHTGREEN);
        timerLabel.setFont(Font.font("Monospaced", FontWeight.BOLD, 24));

        // Player Info
        Label turnTitle = new Label("ЗАРАЗ ХІД");
        turnTitle.setTextFill(Color.GRAY);
        turnTitle.setFont(Font.font("Arial", 12));

        turnLabel = new Label("БІЛІ");
        turnLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        statusLabel = new Label("ОБОВ'ЯЗКОВИЙ БІЙ!");
        statusLabel.setTextFill(HIGHLIGHT_FORCE);
        statusLabel.setVisible(false);

        Button backBtn = new Button("МЕНЮ");
        styleBtn(backBtn, Color.CRIMSON);
        backBtn.setOnAction(e -> {
            stopTimer();
            window.setScene(startScene);
        });

        sidebar.getChildren().addAll(
            roomLabel, roomVal, timeTitle, timerLabel, new Separator(),
            turnTitle, turnLabel, statusLabel, new Region(),
            backBtn
        );
        
        VBox.setVgrow(sidebar.getChildren().get(8), Priority.ALWAYS); // Push button down

        root.setRight(sidebar);
        gameScene = new Scene(root, WIDTH + 280, HEIGHT + 40);
    }

    private TextField styleField(TextField tf) {
        tf.setPrefWidth(200);
        tf.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-padding: 8; -fx-background-radius: 4; -fx-font-weight: bold;");
        return tf;
    }

    private void styleBtn(Button b, Color c) {
        b.setMaxWidth(Double.MAX_VALUE);
        b.setStyle(String.format("-fx-background-color: #%02X%02X%02X; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 5;",
                (int)(c.getRed()*255), (int)(c.getGreen()*255), (int)(c.getBlue()*255)));
        b.setPadding(new Insets(10, 0, 10, 0));
    }

    // ==========================================
    //               GAME LOGIC
    // ==========================================

    private void startTimer() {
        stopTimer();
        secondsElapsed = 0;
        timerLabel.setText("00:00");
        gameTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondsElapsed++;
            int m = secondsElapsed / 60;
            int s = secondsElapsed % 60;
            timerLabel.setText(String.format("%02d:%02d", m, s));
        }));
        gameTimer.setCycleCount(Animation.INDEFINITE);
        gameTimer.play();
    }
    
    private void stopTimer() {
        if (gameTimer != null) gameTimer.stop();
    }

    private void resetGame() {
        tileGroup.getChildren().clear();
        pieceGroup.getChildren().clear();
        highlightGroup.getChildren().clear();
        board = new Tile[BOARD_SIZE][BOARD_SIZE];
        whiteTurn = true;
        selectedPiece = null;
        activeMoveMap.clear();
        
        if(roomLabel != null) roomLabel.setText(roomNameStr);
        startTimer();

        for (int y = 0; y < BOARD_SIZE; y++) {
            for (int x = 0; x < BOARD_SIZE; x++) {
                boolean isLight = (x + y) % 2 == 0;
                Tile tile = new Tile(isLight, x, y);
                
                tile.setOnMousePressed(e -> {
                    String key = tile.boardX + "," + tile.boardY;
                    if (activeMoveMap.containsKey(key)) {
                        executeMove(activeMoveMap.get(key));
                    }
                });

                board[x][y] = tile;
                tileGroup.getChildren().add(tile);

                Piece piece = null;
                if (!isLight) {
                    if (y <= 2) piece = makePiece(PieceType.RED, x, y);
                    if (y >= 5) piece = makePiece(PieceType.WHITE, x, y);
                }

                if (piece != null) {
                    tile.setPiece(piece);
                    pieceGroup.getChildren().add(piece);
                }
            }
        }
        updateGameState();
    }

    private Piece makePiece(PieceType type, int x, int y) {
        Piece piece = new Piece(type, x, y);
        piece.setOnMousePressed(e -> handlePieceClick(piece));
        return piece;
    }

    private void handlePieceClick(Piece piece) {
        if (whiteTurn != (piece.type == PieceType.WHITE)) return;

        if (!forcedPieces.isEmpty() && !forcedPieces.contains(piece)) {
            return;
        }

        selectPiece(piece);
    }

    private void selectPiece(Piece piece) {
        selectedPiece = piece;
        highlightGroup.getChildren().clear();
        activeMoveMap.clear();
        
        for(int y=0; y<BOARD_SIZE; y++)
            for(int x=0; x<BOARD_SIZE; x++)
                if(board[x][y].hasPiece()) {
                    Piece p = board[x][y].getPiece();
                    if(forcedPieces.contains(p)) {
                         p.setEffect(new DropShadow(20, HIGHLIGHT_FORCE));
                    } else {
                         p.setEffect(new DropShadow(5, Color.BLACK));
                    }
                }
        
        piece.setEffect(new DropShadow(15, Color.WHITE));

        List<Move> moves = getValidMoves(piece);
        
        if (!forcedPieces.isEmpty()) {
            moves.removeIf(m -> m.capture == null);
        }

        for (Move m : moves) {
            activeMoveMap.put(m.newX + "," + m.newY, m);

            Circle indicator = new Circle(TILE_SIZE / 6);
            indicator.setFill(HIGHLIGHT_MOVE);
            indicator.setTranslateX(m.newX * TILE_SIZE + TILE_SIZE / 2.0);
            indicator.setTranslateY(m.newY * TILE_SIZE + TILE_SIZE / 2.0);
            indicator.setOpacity(0.6);
            indicator.setOnMousePressed(e -> executeMove(m));
            highlightGroup.getChildren().add(indicator);
        }
    }

    private void executeMove(Move move) {
        Piece piece = board[move.oldX][move.oldY].getPiece();
        
        piece.move(move.newX, move.newY);
        board[move.oldX][move.oldY].setPiece(null);
        board[move.newX][move.newY].setPiece(piece);

        boolean wasCapture = (move.capture != null);

        if (wasCapture) {
            pieceGroup.getChildren().remove(move.capture);
            board[move.captureX][move.captureY].setPiece(null);
        }

        if (!piece.isKing) {
            if ((piece.type == PieceType.WHITE && move.newY == 0) ||
                (piece.type == PieceType.RED && move.newY == BOARD_SIZE - 1)) {
                piece.promote();
            }
        }

        selectedPiece = null;
        highlightGroup.getChildren().clear();
        activeMoveMap.clear();
        
        if (wasCapture) {
            List<Move> followUpMoves = getValidMoves(piece);
            followUpMoves.removeIf(m -> m.capture == null);
            
            if (!followUpMoves.isEmpty()) {
                forcedPieces.clear();
                forcedPieces.add(piece);
                selectPiece(piece);
                return;
            }
        }

        whiteTurn = !whiteTurn;
        updateGameState();
    }

    private void updateGameState() {
        forcedPieces.clear();
        boolean hasCapture = false;
        boolean hasMoves = false;
        
        PieceType turnType = whiteTurn ? PieceType.WHITE : PieceType.RED;
        
        // Scan for moves and captures
        for(int y=0; y<BOARD_SIZE; y++) {
            for(int x=0; x<BOARD_SIZE; x++) {
                if(board[x][y].hasPiece()) {
                    Piece p = board[x][y].getPiece();
                    if(p.type == turnType) {
                        List<Move> moves = getValidMoves(p);
                        if (!moves.isEmpty()) hasMoves = true;
                        
                        for(Move m : moves) {
                            if(m.capture != null) {
                                if(!forcedPieces.contains(p)) forcedPieces.add(p);
                                hasCapture = true;
                            }
                        }
                    }
                }
            }
        }

        // --- GAME OVER CHECK ---
        if (!hasMoves) {
            String winner = whiteTurn ? p2NameStr : p1NameStr; // Current player has no moves, other wins
            showGameOver(winner);
            return;
        }

        // Update Visuals
        for(int y=0; y<BOARD_SIZE; y++) {
            for(int x=0; x<BOARD_SIZE; x++) {
                if(board[x][y].hasPiece()) {
                    Piece p = board[x][y].getPiece();
                    p.setEffect(new DropShadow(5, Color.BLACK));
                    if(forcedPieces.contains(p)) {
                        p.setEffect(new DropShadow(25, HIGHLIGHT_FORCE));
                    }
                }
            }
        }

        String playerName = whiteTurn ? p1NameStr : p2NameStr;
        String colorName = whiteTurn ? "(Білі)" : "(Червоні)";
        turnLabel.setText(playerName + "\n" + colorName);
        turnLabel.setTextFill(whiteTurn ? Color.WHITE : Color.web("#ff5555"));
        statusLabel.setVisible(hasCapture);
    }
    
    private void showGameOver(String winnerName) {
        stopTimer();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Гру завершено");
        alert.setHeaderText("У нас є переможець!");
        alert.setContentText(winnerName + " переміг у цій партії!");
        
        // Customizing the alert if possible or just standard
        alert.showAndWait().ifPresent(rs -> {
            window.setScene(startScene);
        });
    }

    private List<Move> getValidMoves(Piece piece) {
        List<Move> moves = new ArrayList<>();
        int[][] dirs = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};

        for (int[] d : dirs) {
            if (piece.isKing) {
                // Flying King
                for (int dist = 1; dist < BOARD_SIZE; dist++) {
                    int nx = piece.x + d[0] * dist;
                    int ny = piece.y + d[1] * dist;
                    if (!isValid(nx, ny)) break;
                    
                    if (!board[nx][ny].hasPiece()) {
                        moves.add(new Move(piece.x, piece.y, nx, ny, null, -1, -1));
                    } else {
                        Piece other = board[nx][ny].getPiece();
                        if (other.type != piece.type) {
                            for (int landDist = 1; landDist < BOARD_SIZE; landDist++) {
                                int landX = nx + d[0] * landDist;
                                int landY = ny + d[1] * landDist;
                                if (!isValid(landX, landY)) break;
                                if (board[landX][landY].hasPiece()) break;
                                moves.add(new Move(piece.x, piece.y, landX, landY, other, nx, ny));
                            }
                        }
                        break;
                    }
                }
            } else {
                // Regular Man
                int forwardY = (piece.type == PieceType.WHITE) ? -1 : 1;
                
                // Normal
                if (d[1] == forwardY) {
                    int nx = piece.x + d[0];
                    int ny = piece.y + d[1];
                    if (isValid(nx, ny) && !board[nx][ny].hasPiece()) {
                        moves.add(new Move(piece.x, piece.y, nx, ny, null, -1, -1));
                    }
                }
                
                // Capture
                int jumpX = piece.x + d[0] * 2;
                int jumpY = piece.y + d[1] * 2;
                int midX = piece.x + d[0];
                int midY = piece.y + d[1];
                
                if (isValid(jumpX, jumpY) && !board[jumpX][jumpY].hasPiece()) {
                    if (board[midX][midY].hasPiece()) {
                        Piece mid = board[midX][midY].getPiece();
                        if (mid.type != piece.type) {
                            moves.add(new Move(piece.x, piece.y, jumpX, jumpY, mid, midX, midY));
                        }
                    }
                }
            }
        }
        return moves;
    }

    private boolean isValid(int x, int y) {
        return x >= 0 && x < BOARD_SIZE && y >= 0 && y < BOARD_SIZE;
    }

    // ==========================================
    //               ENTITIES
    // ==========================================

    private enum PieceType { RED, WHITE }

    private class Move {
        int oldX, oldY, newX, newY;
        Piece capture;
        int captureX, captureY;
        public Move(int ox, int oy, int nx, int ny, Piece c, int cx, int cy) {
            oldX = ox; oldY = oy; newX = nx; newY = ny; capture = c; captureX = cx; captureY = cy;
        }
    }

    private class Piece extends StackPane {
        PieceType type;
        int x, y;
        boolean isKing = false;
        Text kingIcon;

        public Piece(PieceType type, int x, int y) {
            this.type = type;
            setPrefSize(TILE_SIZE, TILE_SIZE);
            setAlignment(Pos.CENTER);
            move(x, y);

            Circle bg = new Circle(TILE_SIZE * 0.38);
            Color base = (type == PieceType.RED) ? P2_COLOR : P1_COLOR;
            Color dark = (type == PieceType.RED) ? Color.web("#800000") : Color.GRAY;
            
            RadialGradient gradient = new RadialGradient(
                -45, 0.0, 0.3, 0.3, 0.6, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.WHITE.deriveColor(0,1,1,0.4)),
                new Stop(0.3, base),
                new Stop(1.0, dark)
            );
            
            bg.setFill(gradient);
            bg.setEffect(new DropShadow(5, Color.BLACK));

            Circle inner = new Circle(TILE_SIZE * 0.25);
            inner.setFill(Color.TRANSPARENT);
            inner.setStroke(Color.BLACK);
            inner.setStrokeWidth(1);
            inner.setOpacity(0.3);

            kingIcon = new Text("♔");
            kingIcon.setFont(Font.font(30));
            kingIcon.setFill(type == PieceType.RED ? Color.WHITE : Color.BLACK);
            kingIcon.setVisible(false);

            getChildren().addAll(bg, inner, kingIcon);
        }

        public void move(int x, int y) {
            this.x = x;
            this.y = y;
            relocate(x * TILE_SIZE, y * TILE_SIZE);
        }

        public void promote() {
            isKing = true;
            kingIcon.setVisible(true);
        }
    }

    private class Tile extends Rectangle {
        private Piece piece;
        int boardX, boardY;

        public Tile(boolean light, int x, int y) {
            this.boardX = x;
            this.boardY = y;
            setWidth(TILE_SIZE);
            setHeight(TILE_SIZE);
            relocate(x * TILE_SIZE, y * TILE_SIZE);
            setFill(light ? COL_LIGHT : COL_DARK);
        }
        public boolean hasPiece() { return piece != null; }
        public Piece getPiece() { return piece; }
        public void setPiece(Piece piece) { this.piece = piece; }
    }
}
