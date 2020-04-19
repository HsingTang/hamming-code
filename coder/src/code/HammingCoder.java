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

    public String encode(String code) throws InvalidInputFormatException {
        BinaryCode encodingResult;
        try {
            encodingResult = this.encode(new BinaryCode(code));
        }catch (InvalidInputFormatException e){
            throw e;
        }
        return encodingResult.toString();
    }

    public String decode(String code) throws InvalidInputFormatException, SingleBitErrorException, DoubleBitErrorException {
        BinaryCode decodingResult;
        try {
            decodingResult = this.decode(new BinaryCode(code));
        }catch (InvalidInputFormatException | SingleBitErrorException | DoubleBitErrorException e){
            throw e;
        }
        return decodingResult.toString();
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

    private BinaryCode decode(BinaryCode code) throws SingleBitErrorException, DoubleBitErrorException{
        int numPBits = numParityBitsDecode(code.length());
        if(parityMap.size() < numPBits){
            expandParityMap(numPBits);
        }
        List<Boolean> bits = code.getBits();

        try{
            validateCode(code);
        }catch (SingleBitErrorException e){
            int correctingIndex = e.getErrorBitIndex();
            bits.set(correctingIndex,!bits.get(correctingIndex));
            e.setCorrectedBits(extractDecodedBits(bits).toString());
            throw e;
        } catch(DoubleBitErrorException e){
            throw e;
        }

        return extractDecodedBits(bits);
    }

    /**
     * @param code bit stream to validate against single/double bit corruption
     *             set code.singleError as true if detecting a single bit corruption,
     *             and set code.singleErrorBit as the corrupted bit index;
     *             set code.doubleError as true if detecting double bit corruptions.
     */
    private void validateCode(BinaryCode code) throws SingleBitErrorException,DoubleBitErrorException{
        int singleErrorBit = detectSingleError(code);
        boolean overAllParity = validateOverall(code);
        if(singleErrorBit!=-1 && !overAllParity){
            throw new SingleBitErrorException(singleErrorBit);
        }else if(singleErrorBit!=-1 && overAllParity){
            throw new DoubleBitErrorException();
        }
    }

    /**
     * @param code bit stream to validate against single bit corruption
     * @return bit index of the detected single error. Return -1 if no single error detected.
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
            assert (corruption.size()==1);
            return corruption.get(0);
        }
        return -1;
    }

    private boolean validateOverall(BinaryCode code){
        List<Boolean> bits = new ArrayList<>();
        List<Boolean> codeBits = code.getBits();
        for(int i = 1; i<codeBits.size(); i++){
                bits.add(codeBits.get(i));
        }
        return validateParityBit(codeBits.get(0),bits);
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
