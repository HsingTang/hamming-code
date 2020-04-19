package exception;

public class SingleBitErrorException extends Exception{

    public SingleBitErrorException(int errorBitIndex){
        super(Integer.toString(errorBitIndex));
    }

    public int getErrorBitIndex(){
        return Integer.valueOf(this.getMessage());
    }

}
