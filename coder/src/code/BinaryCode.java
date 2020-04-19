package code;

import exception.InvalidInputFormatException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Wrapper class for memory word to encode/decode.
 */
public class BinaryCode {

    private ArrayList<Boolean> myBitContent;

    public BinaryCode(String content) throws InvalidInputFormatException {
        if(!validateBinary(content)){
            throw new InvalidInputFormatException();
        }
        myBitContent = new ArrayList<>();
        for(int i = 0; i<content.length(); i++){
            myBitContent.add(content.charAt(i)=='1');
        }
    }

    BinaryCode(Collection <Boolean> content){
        this.myBitContent = new ArrayList<>(content);
    }

    public String toString(){
        StringBuilder sb =new StringBuilder();
        for(boolean b:this.myBitContent){
            sb.append(b?'1':'0');
        }
        return sb.toString();
    }

    public Integer length(){
        return this.myBitContent.size();
    }

    public List<Boolean> getBits(){
        return new ArrayList<>(this.myBitContent);
    }

    private boolean validateBinary(String str){
        for(char c : str.toCharArray()){
            if(c != '0' && c!='1'){
                return false;
            }
        }
        return true;
    }

}
