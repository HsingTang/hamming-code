package code;

import exception.DoubleBitErrorException;
import exception.InvalidInputFormatException;
import exception.SingleBitErrorException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HammingCoder {

    // mapping of parity bit's index to coverage indices in the encoded bit stream
    private Map<Integer, List<Integer>> parityMap;

    public HammingCoder(){
        parityMap = new HashMap<>();
    }

    /**
     * Applies Hamming coding to encode a bit sequence in binary form
     * @param code String containing the input bit sequence to encode following Hamming Encoding rules
     * @return String containing the encoded bit sequence
     * @throws InvalidInputFormatException if the input String contains characters other than 0 or 1
     */
    public String encode(String code) throws InvalidInputFormatException {
        BinaryCode encodingResult;
        try {
            encodingResult = this.encode(new BinaryCode(code));
        }catch (InvalidInputFormatException e){
            throw e;
        }
        return encodingResult.toString();
    }

    /**
     * Recovers the original data from a Hamming-encoded binary bit sequence
     * @param code String containing the input bit sequence to decode following Hamming Encoding rules
     * @return String containing the decoded bit sequence after trimming off all parity bits
     * @throws InvalidInputFormatException if the input String contains characters other than 0 or 1
     */
    public String decode(String code) throws InvalidInputFormatException {
        BinaryCode decodingResult;
        try {
            decodingResult = this.decode(new BinaryCode(code));
        }catch (InvalidInputFormatException e){
            throw e;
        }
        return decodingResult.toString();
    }

    /**
     * Validate a bit sequence against single- or double-bit corruption.
     * @param codeContent String containing the bit sequence to validate
     * @throws InvalidInputFormatException if the input String contains characters other than 0 or 1
     * @throws SingleBitErrorException if a single-bit corruption is detected.
     *          Set SingleBitErrorException.errorBitIndex as the index of corrupted bit.
     * @throws DoubleBitErrorException if a double-bit corruption is detected.
     */
    public void validateCode(String codeContent) throws InvalidInputFormatException,SingleBitErrorException,DoubleBitErrorException{
        BinaryCode code = new BinaryCode(codeContent);
        int singleErrorBit = detectSingleError(code);
        boolean overAllParity = validateOverallParity(code);

        if(singleErrorBit!=-1 && singleErrorBit!=-2 && !overAllParity){
            throw new SingleBitErrorException(singleErrorBit);
        }else if(singleErrorBit==-1 && !overAllParity){
            throw new SingleBitErrorException(0);
        }else if((singleErrorBit!=-1 && overAllParity) || singleErrorBit==-2){
            throw new DoubleBitErrorException();
        }
    }

    /**
     * @param code bit stream to validate against single bit corruption
     * @return bit index of the detected single error.
     *      Return -1 if no single error detected.
     *      Return -2 if more than one errors detected.
     */
    private int detectSingleError(BinaryCode code){
        int numPBits = numParityBitsDecode(code.length());
        if(parityMap.size() < numPBits){
            expandParityMap(numPBits);
        }

        boolean flag = false;
        List<Boolean> bits = code.getBits();
        List<Integer> corruption = IntStream.rangeClosed(1,bits.size()).boxed().collect(Collectors.toList());
        int parityIndex = 1;
        while(parityIndex <= Math.pow(2,numPBits-1)){
            List<Boolean> coverage = collectCoveredBits(parityIndex, bits);
            if(!validateParityBit(bits.get(parityIndex),coverage)){
                flag = true;
                corruption.retainAll(parityMap.get(parityIndex));
            }else{
                corruption.removeAll(parityMap.get(parityIndex));
            }
            parityIndex*=2;
        }
        if(flag){
            if(corruption.size()==1) {
                return corruption.get(0);
            }else{
                return -2;
            }
        }
        return -1;
    }

    private boolean validateOverallParity(BinaryCode code){
        List<Boolean> bits = new ArrayList<>();
        List<Boolean> codeBits = code.getBits();
        for(int i = 1; i<codeBits.size(); i++){
                bits.add(codeBits.get(i));
        }
        return validateParityBit(codeBits.get(0),bits);
    }

    private BinaryCode encode(BinaryCode code){
        int numPBits = numParityBitsEncode(code.length());
        if(parityMap.size() < numPBits){
            expandParityMap(numPBits);
        }
        // set up encoded bit stream
        List<Boolean> bits = code.getBits();
        // initialize overall parity bit
        bits.add(0,false);
        // initialize index-based parity bits
        int parityIndex = 1;
        while(parityIndex <= Math.pow(2,numPBits-1)){
            bits.add(parityIndex,false);
            parityIndex*=2;
        }
        // determine values for parity bits
        parityIndex = 1;
        while(parityIndex <= Math.pow(2,numPBits-1)){
            List<Boolean> coverage = collectCoveredBits(parityIndex, bits);
            bits.set(parityIndex,setParityBit(coverage));
            parityIndex*=2;
        }
        // determine value for overall parity bit
        List<Boolean> coverage = new ArrayList<>();
        for(int index = 1; index<bits.size(); index++){
            coverage.add(bits.get(index));
        }
        bits.set(0,setParityBit(coverage));
        // construct a new coder.code.BinaryCode object based on encoded bit stream
        return new BinaryCode(bits);
    }

    private BinaryCode decode(BinaryCode code) {
        int numPBits = numParityBitsDecode(code.length());
        if(parityMap.size() < numPBits){
            expandParityMap(numPBits);
        }
        List<Boolean> bits = code.getBits();
        return extractDecodedBits(bits);
    }

    private BinaryCode extractDecodedBits(List<Boolean> bits){
        List<Boolean> decodedBits = new ArrayList<>();
        for(int i = 1; i<bits.size(); i++){
            if(!parityMap.containsKey(i)){
                decodedBits.add(bits.get(i));
            }
        }
        return new BinaryCode(decodedBits);
    }

    private int numParityBitsEncode(int wordLength){
        int numBits = 0;
        while(Math.pow(2,numBits) < wordLength+numBits+1){
            numBits++;
        }
        return numBits;
    }

    private int numParityBitsDecode(int wordLength){
        int numBits = 0;
        while(Math.pow(2,numBits) < wordLength){
            numBits++;
        }
        return numBits;
    }

    private void expandParityMap(int targetMapSize){
        int maxIndex = (int) Math.pow(2,targetMapSize)-1;
        for(int parity = parityMap.size()+1; parity<=targetMapSize; parity++){
            parityMap.put((int)Math.pow(2,parity-1),new ArrayList<>());
            for(int index = (int) Math.pow(2,parity-1); index<maxIndex; index++){
                String binary = Integer.toBinaryString(index);
                if(binary.charAt(binary.length()-parity)=='1'){
                    parityMap.get((int)Math.pow(2,parity-1)).add(index);
                }
            }
        }
    }

    private List<Boolean> collectCoveredBits(int parityIndex, List<Boolean> bits){
        List<Boolean> coverage = new ArrayList<>();
        for(int index : parityMap.get(parityIndex)){
            if(bits.size()<=index) break;
            if(index!=parityIndex) {
                coverage.add(bits.get(index));
            }
        }
        return coverage;
    }

    private boolean validateParityBit(boolean parityBit, Collection<Boolean> bits){
        boolean expected = setParityBit(bits);
        return parityBit==expected;
    }

    private boolean setParityBit(Collection<Boolean> bits){
        int count = 0;
        for(Boolean bit : bits){
            count = bit? count+1 : count;
        }
        return count%2!=0;
    }
}
