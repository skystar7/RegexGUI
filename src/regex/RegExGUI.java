package regex;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class RegExGUI extends Application {

    /*
     App settings
     */

    private final double width = 800;
    private final double height = 500;
    private final double spacing = 15;
    private final double pref_width = 600;
    private final double nodeHeight = 20;
    private final String propertiesFileName = "regex_properties.properties";
    private final String errorTextFieldStyle = "-fx-text-box-border: red ; -fx-focus-color: red ;";
    private final String redFillColor = "-fx-text-fill: red;";
    private final String darkGreenBackGround = "-fx-background-color: #165016a9;";
    private final String lightGreenBackGround = "-fx-background-color: #16501646;";
    private final String ultraLightGreenBackGround = "-fx-background-color: #16501620;";
    private final String whiteTextStyle = "-fx-text-fill: #ffffff;";
    private final String app_title = "Regex GUI";
    /*
    Controls
     */
    private Label inputLbl, regexLbl, inputLengthLbl, findsLbl, groupCountLbl, patternLbl;
    private TextField inputField, regExField;
    private TextArea errorArea, splitArea;
    private Button quoteBtn;
    private Button matchesBtn;
    private Button caseInsensitiveBtn;
    private Button splitBtn;
    private ImageView close;
    private ImageView minimize;
    private TableView<Result> resultTableView;

    /*
    Menus
     */
    private ContextMenu contextMenu;
    private MenuItem regexHelpMenuItem;

    /*
    Layout
     */
    private GridPane gridPaneLay;
    private VBox mainVBoxLay;
    private HBox hBoxLay;
    private HBox windowFrameLay;
    private HBox buttonsGroupLay;
    private Insets insets = new Insets(spacing, spacing, spacing, spacing);

    /*
    Properties
     */
    private Properties savedTxtProperties;

    /*
    Stage
     */
    private Scene mainScene;
    private Stage stage;

    /*
    Coordinates
     */
    private double mainX;
    private double mainY;
    private double helpX;
    private double helpY;
    private double inputDiaX;
    private double inputDiaY;


    @Override
    public void start(Stage primaryStage) throws Exception {
        init(primaryStage);
    }

    private void init(Stage primaryStage) {
        stage = primaryStage;
        inputField = new TextField();
        inputField.setPrefWidth(pref_width);
        inputField.setOnKeyReleased(event -> regexUtil(inputField.getText(), regExField.getText(), true, false));
        inputField.focusedProperty().addListener(observable -> regexUtil(inputField.getText(), regExField.getText(), true, false));
        regExField = new TextField();
        regExField.setOnKeyReleased(event -> regexUtil(inputField.getText(), regExField.getText(), true, false));
        regExField.focusedProperty().addListener(observable -> regexUtil(inputField.getText(), regExField.getText(), true, false));
        if (isFileExists()) loadProperties();
        inputLbl = new Label("Input");
        regexLbl = new Label("Regex");
        inputLbl.setStyle(whiteTextStyle);
        regexLbl.setStyle(whiteTextStyle);
        findsLbl = new Label();
        patternLbl = new Label();
        groupCountLbl = new Label();
        inputLengthLbl = new Label();
        minimize = new ImageView(new Image(getClass().getResource("minimize.png").toExternalForm()));
        minimize.setOnMousePressed(event -> primaryStage.setIconified(true));
        close = new ImageView(new Image(getClass().getResource("close.png").toExternalForm()));
        close.setOnMousePressed(event -> primaryStage.fireEvent(new WindowEvent(primaryStage, WindowEvent.WINDOW_CLOSE_REQUEST)));
        resultTableView = new TableView<>();
        resultTableView.setMinHeight(nodeHeight * 4);
        tableInit();
        errorArea = new TextArea();
        errorArea.setStyle(redFillColor);
        errorArea.setEditable(false);
        errorArea.setVisible(false);
        errorArea.setMinHeight(nodeHeight * 4);
        splitArea = new TextArea();
        splitArea.setEditable(false);
        splitArea.setMinHeight(nodeHeight * 4);
        quoteBtn = new Button("Quote");
        quoteBtn.setOnAction(event -> regexUtil(inputField.getText(), Pattern.quote(regExField.getText()), true, false));
        matchesBtn = new Button("Matches");
        matchesBtn.setOnAction(event -> regexUtil(inputField.getText(), "^" + regExField.getText() + "$", true, false));
        caseInsensitiveBtn = new Button("Case Insensitive");
        caseInsensitiveBtn.setOnAction(event -> regexUtil(inputField.getText(), regExField.getText(), false, false));
        splitBtn = new Button("Split");
        splitBtn.setOnAction(event -> regexUtil(inputField.getText(), regExField.getText(), false, true));
        gridPaneLay = new GridPane();
        gridPaneLay.setStyle(darkGreenBackGround);
        gridPaneLay.setHgap(spacing);
        gridPaneLay.setVgap(spacing / 4);
        gridPaneLay.setPadding(insets);
        gridPaneLay.add(inputLbl, 0, 0);
        gridPaneLay.add(inputField, 1, 0);
        gridPaneLay.add(regexLbl, 0, 1);
        gridPaneLay.add(regExField, 1, 1);
        buttonsGroupLay = new HBox();
        buttonsGroupLay.setSpacing(spacing / 4);
        buttonsGroupLay.getChildren().addAll(quoteBtn, matchesBtn, caseInsensitiveBtn, splitBtn);
        gridPaneLay.add(buttonsGroupLay, 1, 3);
        mainVBoxLay = new VBox();
        mainVBoxLay.setAlignment(Pos.CENTER);
        mainVBoxLay.setSpacing(spacing);
        mainVBoxLay.setPadding(insets);
        mainVBoxLay.setStyle(lightGreenBackGround);
        windowFrameLay = new HBox();
        windowFrameLay.setAlignment(Pos.TOP_RIGHT);
        windowFrameLay.setSpacing(spacing / 4);
        windowFrameLay.getChildren().addAll(minimize, close);
        hBoxLay = new HBox();
        hBoxLay.setAlignment(Pos.CENTER);
        hBoxLay.setSpacing(spacing);
        hBoxLay.setPadding(insets);
        hBoxLay.setStyle(ultraLightGreenBackGround);
        hBoxLay.getChildren().addAll(inputLengthLbl, findsLbl, groupCountLbl, patternLbl);
        mainVBoxLay.getChildren().addAll(windowFrameLay, gridPaneLay, resultTableView, errorArea, hBoxLay);
        contextMenu = new ContextMenu();
        regexHelpMenuItem = new MenuItem("Regex Help...");
        regexHelpMenuItem.setOnAction(event -> {
            showRegexHelpWindow();
        });
        contextMenu.getItems().add(regexHelpMenuItem);
        regExField.setTooltip(new Tooltip("Right click to get regex help"));
        regExField.setContextMenu(contextMenu);
        mainScene = new Scene(mainVBoxLay, width, height);
        mainScene.setOnMousePressed((event -> {
            mainX = event.getSceneX();
            mainY = event.getSceneY();
        }));
        mainScene.setOnMouseDragged((event -> {
            primaryStage.setX(event.getScreenX() - mainX);
            primaryStage.setY(event.getScreenY() - mainY);
        }));
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(mainScene);
        primaryStage.setOnCloseRequest(event -> saveProperties());
        primaryStage.setTitle(app_title);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void showRegexHelpWindow() {
        Stage regexHelpWindow = new Stage();
        regexHelpWindow.initModality(Modality.APPLICATION_MODAL);
        try {
            URI toURI = RegExGUI.class.getResource("regex.txt").toURI();
            String regexHelp = new String(Files.readAllBytes(Paths.get(toURI)), StandardCharsets.UTF_8);
            VBox vBox = new VBox();
            vBox.setAlignment(Pos.CENTER);
            vBox.setSpacing(spacing);
            vBox.setPadding(insets);
            vBox.setStyle(ultraLightGreenBackGround);
            Button hide = new Button("Hide");
            hide.setOnAction(e -> regexHelpWindow.hide());
            TextArea helpArea = new TextArea();
            helpArea.setEditable(false);
            helpArea.setWrapText(true);
            helpArea.appendText(regexHelp);
            vBox.getChildren().addAll(helpArea, hide);
            Scene regexHelpScene = new Scene(vBox);
            regexHelpScene.setOnMousePressed((mousePresses -> {
                helpX = mousePresses.getSceneX();
                helpY = mousePresses.getSceneY();
            }));
            regexHelpScene.setOnMouseDragged((mouseDragged -> {
                regexHelpWindow.setX(mouseDragged.getScreenX() - helpX);
                regexHelpWindow.setY(mouseDragged.getScreenY() - helpY);
            }));
            regexHelpWindow.initStyle(StageStyle.TRANSPARENT);
            regexHelpWindow.setScene(regexHelpScene);
            regexHelpWindow.showAndWait();
        } catch (IOException e) {
            errorMsg("IO Error", e.getMessage());
        } catch (URISyntaxException e) {
            errorMsg("URI Syntax Error", e.getMessage());
        }
    }

    private void regexUtil(String input, String regex, boolean caseSensitive, boolean split) {
        /* UI logic */
        uiLogic();
        Pattern pattern = null;
        try {
            if (caseSensitive) pattern = Pattern.compile(regex);
            else if (split) {
                hBoxLay.setVisible(false);
                pattern = Pattern.compile(regex);
                int limit = showInputDialog();
                String[] splitResult = pattern.split(input, limit);
                mainVBoxLay.getChildren().set(2, splitArea);
                splitArea.clear();
                splitArea.appendText("Split Array: \n");
                for (int i = 0; i < splitResult.length; i++) splitArea.appendText(i + ": " + splitResult[i] + "\n");
            } else pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        } catch (PatternSyntaxException e) {
            errorMsg("Pattern syntax error", e.getMessage());
        }
        if (pattern != null) {
            Matcher matcher = pattern.matcher(input);
            int foundIncrement = 0;
            boolean found = false;
            ObservableList<Result> results = createObservableList();
            while (matcher.find()) {
                found = true;
                String group = matcher.group();
                Result result = new Result(group, matcher.groupCount(), group.codePointCount(0, group.length()), getCodePoints(group), matcher.start(), matcher.end());
                results.add(result);
                foundIncrement++;
            }
            /* view */
            resultTableView.setItems(results);
            inputLengthLbl.setText("Input Length: " + input.length());
            findsLbl.setText("Finds: " + foundIncrement);
            patternLbl.setText("Pattern: " + pattern.pattern());
            groupCountLbl.setText("Group count: " + matcher.groupCount());

            if (!found) resultTableView.setItems(null);
        }
    }

    private int showInputDialog() {
        Stage inputDialog = new Stage();
        inputDialog.initModality(Modality.APPLICATION_MODAL);
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(spacing);
        vBox.setPadding(insets);
        vBox.setStyle(ultraLightGreenBackGround);
        Label limitTxt = new Label();
        Label limitLbl = new Label();
        limitLbl.setText("Your limit: ");
        limitTxt.setText("If limit n is positive the array size would be n-1,\nif limit is negative array would have no limit, \nif limit is zero the result would be as many as possible and trailing empty strings will be discarded.");
        Button split = new Button("Split");
        TextField limitField = new TextField();
        limitField.setText("0");
        HBox hBox = new HBox();
        hBox.setSpacing(spacing / 4);
        hBox.getChildren().addAll(limitLbl, limitField);
        vBox.getChildren().addAll(limitTxt, hBox, split);
        Scene inputDialogScene = new Scene(vBox);
        inputDialog.setScene(inputDialogScene);
        inputDialogScene.setOnMousePressed((mousePresses -> {
            inputDiaX = mousePresses.getSceneX();
            inputDiaY = mousePresses.getSceneY();
        }));
        inputDialogScene.setOnMouseDragged((mouseDragged -> {
            inputDialog.setX(mouseDragged.getScreenX() - inputDiaX);
            inputDialog.setY(mouseDragged.getScreenY() - inputDiaY);
        }));
        inputDialog.initStyle(StageStyle.TRANSPARENT);
        limitField.setOnKeyReleased(event -> validateLimit(limitField));
        limitField.focusedProperty().addListener(observable -> validateLimit(limitField));
        split.setOnAction(event -> {
            if (validateLimit(limitField)) {
                inputDialog.hide();
            }
        });
        inputDialog.showAndWait();
        return Integer.parseInt(limitField.getText());
    }

    private boolean validateLimit(TextField limitField) {
        boolean validLimit = isValidLimit(limitField);
        if (!validLimit) limitField.setStyle(errorTextFieldStyle);
        else limitField.setStyle(null);
        return validLimit;
    }

    private boolean isValidLimit(TextField limitField) {
        if (limitField.getText().length() == 0) return false;
        else return Pattern.matches("-?\\d+", limitField.getText());
    }

    private ObservableList<Result> createObservableList() {
        ObservableList<Result> results = null;
        if (results == null) {
            results = FXCollections.observableArrayList();
            return results;
        } else {
            results.clear();
            return results;
        }
    }

    private String getCodePoints(String group) {
        StringBuilder codePoints = new StringBuilder();
        for (int i = 0; i < group.length(); i++) {
            int codePointAtIndex = group.codePointAt(group.offsetByCodePoints(0, i));
            String hexString = Integer.toHexString(codePointAtIndex);
            while (hexString.length() < 4) hexString = 0 + hexString;
            codePoints.append("U+" + hexString + " ");
        }
        return codePoints.toString();
    }

    private boolean isFileExists() {
        File file = new File(propertiesFileName);
        return file.exists();
    }

    private void loadProperties() {
        savedTxtProperties = new Properties();
        try (InputStream inputStream = Files.newInputStream(Paths.get(propertiesFileName))) {
            savedTxtProperties.load(inputStream);
            inputField.setText(savedTxtProperties.getProperty("input", ""));
            regExField.setText(savedTxtProperties.getProperty("regex", ""));
        } catch (IOException e) {
            errorMsg("IO Error", e.getMessage());
        }
    }

    private void saveProperties() {
        savedTxtProperties = new Properties();
        try (OutputStream outputStream = Files.newOutputStream(Paths.get(propertiesFileName))) {
            savedTxtProperties.put("input", inputField.getText());
            savedTxtProperties.put("regex", regExField.getText());
            savedTxtProperties.store(outputStream, "Regex GUI App");
        } catch (IOException e) {
            errorMsg("IO Error", e.getMessage());
        }
    }

    private void errorMsg(String title, String message) {
        errorArea.setVisible(true);
        stage.sizeToScene();
        errorArea.clear();
        errorArea.appendText(title + ": \n");
        errorArea.appendText(message);
        if (title.equals("Pattern syntax error")) regExField.setStyle(errorTextFieldStyle);
    }

    private void uiLogic() {
        splitArea.clear();
        errorArea.clear();
        hBoxLay.setVisible(true);
        mainVBoxLay.getChildren().set(2, resultTableView);
        regExField.setStyle(null);
        resultTableView.setItems(null);
        errorArea.setVisible(false);
    }

    private void tableInit() {
        TableColumn<Result, String> foundCol = new TableColumn("Found");
        foundCol.setMinWidth(200);
        foundCol.setCellValueFactory(new PropertyValueFactory<>("found"));

        TableColumn<Result, Integer> groupCountCol = new TableColumn<>("Group Count");
        groupCountCol.setCellValueFactory(new PropertyValueFactory<>("groupCount"));

        TableColumn<Result, Integer> lengthCol = new TableColumn<>("Length");
        lengthCol.setCellValueFactory(new PropertyValueFactory<>("length"));

        TableColumn<Result, Integer> codePointsCol = new TableColumn<>("Code Points");
        codePointsCol.setMinWidth(200);
        codePointsCol.setCellValueFactory(new PropertyValueFactory<>("codePoints"));

        TableColumn<Result, Integer> startIndexCol = new TableColumn<>("Start Index");
        startIndexCol.setCellValueFactory(new PropertyValueFactory<>("startIndex"));

        TableColumn<Result, Integer> endIndexCol = new TableColumn<>("End Index");
        endIndexCol.setCellValueFactory(new PropertyValueFactory<>("endIndex"));

        resultTableView.getColumns().addAll(foundCol, groupCountCol, lengthCol, codePointsCol, startIndexCol, endIndexCol);
    }

    public static class Result {

        private String found;
        private int groupCount;
        private int length;
        private String codePoints;
        private int startIndex;
        private int endIndex;

        public Result(String found, int groupCount, int length, String codePoints, int startIndex, int endIndex) {
            this.found = found;
            this.groupCount = groupCount;
            this.length = length;
            this.codePoints = codePoints;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }

        public String getFound() {
            return found;
        }

        public void setFound(String found) {
            this.found = found;
        }

        public int getGroupCount() {
            return groupCount;
        }

        public void setGroupCount(int groupCount) {
            this.groupCount = groupCount;
        }

        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }

        public String getCodePoints() {
            return codePoints;
        }

        public void setCodePoints(String codePoints) {
            this.codePoints = codePoints;
        }

        public int getStartIndex() {
            return startIndex;
        }

        public void setStartIndex(int startIndex) {
            this.startIndex = startIndex;
        }

        public int getEndIndex() {
            return endIndex;
        }

        public void setEndIndex(int endIndex) {
            this.endIndex = endIndex;
        }

        @Override
        public String toString() {
            return "Result{" +
                    "found='" + found + '\'' +
                    ", length=" + length +
                    ", codePoints='" + codePoints + '\'' +
                    ", startIndex=" + startIndex +
                    ", endIndex=" + endIndex +
                    '}';
        }
    }

}
