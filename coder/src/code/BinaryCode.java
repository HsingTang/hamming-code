package code;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Wrapper class for memory word to encode/decode.
 */
public class BinaryCode {

    private ArrayList<Boolean> myBitContent;
    private Boolean singleError;
    private Boolean doubleError;
    private Integer singleErrorBit;

    public BinaryCode(String content){
        /*
        if(!validateBinary(content)){
            throw new coder.exception.HammingException();
        }
        */
        myBitContent = new ArrayList<>();
        for(int i = 0; i<content.length(); i++){
            myBitContent.add(content.charAt(i)=='1'? true:false);
        }
        this.singleError = false;
        this.doubleError = false;
    }

    public BinaryCode(Collection <Boolean> content){
        this.myBitContent = new ArrayList<>(content);
    }

    public String toString(){
        StringBuilder sb =new StringBuilder();
        for(int i = 0; i<myBitContent.size(); i++){
            sb.append(myBitContent.get(i)?'1':'0');
        }
        return sb.toString();
    }

    public void setSingleErrorBit(Integer singleErrorBit) {
        this.singleErrorBit = singleErrorBit;
    }

    public void setSingleError(Boolean error){
        this.singleError = error;
    }

    public void setDoubleError(Boolean error){
        this.doubleError = error;
    }

    public Integer length(){
        return this.myBitContent.size();
    }

    public List<Boolean> getBits(){
        return new ArrayList<>(this.myBitContent);
    }

    public Boolean getBitAt(Integer index){
        return this.myBitContent.get(index);
    }

    public Integer getSingleErrorBit() {
        return singleErrorBit;
    }

    public Boolean getSingleError() {
        return this.singleError;
    }

    public Boolean getDoubleError() {
        return this.doubleError;
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
