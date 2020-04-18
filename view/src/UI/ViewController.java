package UI;

import code.HammingCoder;
import exception.HammingException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;

public class ViewController {

    @FXML
    private CheckBox encodeCheckbox;
    @FXML
    private CheckBox decodeCheckbox;
    @FXML
    private TextArea input;
    @FXML
    private Text output;

    private Alert alert;

    private HammingCoder coder;
    private Boolean encode;
    private Boolean decode;


    public ViewController(){
        this.coder = new HammingCoder();
        this.encode = false;
        this.decode = false;
        setupAlert();
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
        try {
            if (encode) {
                output.setText(coder.encode(input.getText()));
            } else if (decode) {
                output.setText(coder.decode(input.getText()));
            }
        }catch (HammingException e){
            this.alert.showAndWait();
        }
    }

    private void setupAlert(){
        this.alert = new Alert(Alert.AlertType.ERROR);
        this.alert.setTitle("Invalid input");
        this.alert.setContentText("Input must be a binary sequence.");
    }


}
