package view;

import coder.HammingCoder;

public class ViewController {

    private HammingCoder coder;
    private Boolean encode;
    private Boolean decode;
    private String input;
    private String result;

    public ViewController(){
        this.coder = new HammingCoder();
        this.encode = false;
        this.decode = false;
    }

    public void toggleEncode() {
        this.encode = !this.encode;
        this.decode = !this.encode;
    }

    public void toggleDecode() {
        this.decode = !this.decode;
        this.encode = !this.decode;
    }

    public void setInput(String input){
        this.input = input;
    }

    public String getResult(){
        return this.result;
    }

    public void run(){
        if(encode){
            result = coder.encode(input);
        }else if(decode){
            result = coder.decode(input);
        }
    }


}
