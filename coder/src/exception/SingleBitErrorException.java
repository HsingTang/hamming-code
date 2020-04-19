package exception;

public class SingleBitErrorException extends Exception{

    private Integer errorBitIndex;
    private String correctedBits;

    public SingleBitErrorException(Integer errorBitIndex){
        super();
        this.errorBitIndex = errorBitIndex;
    }

    public Integer getErrorBitIndex(){
        return this.errorBitIndex;
    }

    public String getCorrectedBits() {
        return correctedBits;
    }

    public void setCorrectedBits(String correctedBits) {
        this.correctedBits = correctedBits;
    }
}
