package UI;

import code.HammingCoder;
import exception.DoubleBitErrorException;
import exception.InvalidInputFormatException;
import exception.SingleBitErrorException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
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
    private Text output;

    private Stage myStage;
    private Alert alert;
    private FileChooser fileChooser;

    private HammingCoder coder;
    private Boolean encode;
    private Boolean decode;
    private String inputString;


    public ViewController(){
        coder = new HammingCoder();
        encode = false;
        decode = false;
        setupAlert();
        setupFileChooser();
    }

    public void setStage(Stage stage) {
        myStage = stage;
    }

    public void setEncode() {
        this.encode = true;
        this.decode = false;
        decodeCheckbox.setSelected(false);
    }

    public void setDecode() {
        this.decode = true;
        this.encode = false;
        encodeCheckbox.setSelected(false);
    }


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

    public void invokeFileChooser(){
        readInputFromFile(fileChooser.showOpenDialog(myStage));
    }

    private void readInputFromFile(File file){
        try {
            input.setText(new String(Files.readAllBytes(Paths.get(file.getAbsolutePath()))));
        } catch (IOException e) {
            return;
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

    private void setupFileChooser(){
        fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
    }

    private void setupAlert(){
        this.alert = new Alert(Alert.AlertType.ERROR);
        this.alert.setTitle("Invalid input");
    }

    private String fixCorruptedBit(String string, int index){
        char[] chars = string.toCharArray();
        chars[index] = chars[index]=='1'? '0':'1';
        return new String(chars);
    }



}
