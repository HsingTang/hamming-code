package UI;

import code.HammingCoder;
import exception.DoubleBitErrorException;
import exception.InvalidInputFormatException;
import exception.SingleBitErrorException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextFormatter;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ViewController {

    @FXML
    private CheckBox encodeCheckbox;
    @FXML
    private CheckBox decodeCheckbox;
    @FXML
    private TextArea input;
    @FXML
    private TextArea output;

    private Stage myStage;
    private Alert alert;
    private FileChooser fileChooser;

    private HammingCoder coder;
    private Boolean encode;
    private Boolean decode;
    private String inputString;
    private final int MAX_INPUT_LENGTH = Integer.MAX_VALUE - 32;


    public ViewController(){
        coder = new HammingCoder();
        encode = false;
        decode = false;
    }

    @FXML
    public void initialize() {
        setupAlert();
        setupInputTextField();
        setupFileChooser();
    }

    public void setStage(Stage stage) {
        myStage = stage;
    }

    /**
     * Update Controller mode for preparing to encode input bits and unselect decoding checkbox.
     */
    public void setEncode() {
        this.encode = true;
        this.decode = false;
        decodeCheckbox.setSelected(false);
    }

    /**
     * Update Controller mode for preparing to decode input bits and unselect encoding checkbox.
     */
    public void setDecode() {
        this.decode = true;
        this.encode = false;
        encodeCheckbox.setSelected(false);
    }

    /**
     * Invoke HammingCoder instance to encode/decode input bits when OK button is pressed.
     * Pop up alert dialogue when detecting invalid inputs or bit corruption.
     * Display result in output TextArea after correcting single-bit corruption (if applicable).
     */
    public void run(){
        inputString = input.getText();
        try {
            if (encode) {
                output.setText(coder.encode(inputString));
            } else if (decode) {
                validateDecodeInput();
                output.setText(coder.decode(inputString));
            }
        }catch (InvalidInputFormatException e){
            this.alert.setContentText("Input must be a binary sequence.");
            this.alert.showAndWait();
        }
    }

    /**
     * Pop up a file-choosing window for loading a binary string
     * from a .txt file into the input TextArea
     */
    public void invokeLoadFile(){
        readInputFromFile(fileChooser.showOpenDialog(myStage));
    }

    /**
     * Pop up a file-choosing window for saving the output binary string
     * into a target local .txt file
     */
    public void invokeSaveFile(){
        saveOutputToFile(fileChooser.showSaveDialog(myStage));
    }

    private void readInputFromFile(File file){
        try {
            String inputFromFile = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
            input.setText(inputFromFile);
        } catch (IOException e) {
            return;
        } catch (OutOfMemoryError e){
            this.alert.setContentText("File is too large");
            this.alert.showAndWait();
        }
    }

    private void saveOutputToFile(File file){
        try {
            PrintWriter writer;
            writer = new PrintWriter(file);
            writer.println(output.getText());
            writer.close();
        } catch (IOException ex) {
            this.alert.setContentText("File saving failed");
            this.alert.showAndWait();
        }
    }

    private void validateDecodeInput() throws InvalidInputFormatException{
        try {
            coder.validateCode(input.getText());
        }catch (InvalidInputFormatException e){
            throw e;
        } catch (SingleBitErrorException e){
            inputString = fixCorruptedBit(input.getText(),e.getErrorBitIndex());
            this.alert.setContentText("Single-bit corruption detected at bit index "+e.getErrorBitIndex());
            this.alert.showAndWait();
        } catch (DoubleBitErrorException e){
            this.alert.setContentText("Double-bit corruption detected");
            this.alert.showAndWait();
        }
    }

    private void setupAlert(){
        this.alert = new Alert(Alert.AlertType.ERROR);
        this.alert.setTitle("Error");
    }

    private void setupInputTextField(){
        input.setWrapText(true);
        input.setTextFormatter(new TextFormatter<String>(change ->
                change.getControlNewText().length() <= MAX_INPUT_LENGTH ? change : null));
    }

    private void setupFileChooser(){
        fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
    }

    private String fixCorruptedBit(String string, int index){
        char[] chars = string.toCharArray();
        chars[index] = chars[index]=='1'? '0':'1';
        return new String(chars);
    }
    
}
