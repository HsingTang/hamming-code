package exception;

public class SingleBitErrorException extends Exception{

    private Integer errorBitIndex;

    public SingleBitErrorException(Integer errorBitIndex){
        super();
        this.errorBitIndex = errorBitIndex;
    }

    public Integer getErrorBitIndex(){
        return this.errorBitIndex;
    }
}
