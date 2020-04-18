package coder;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HammingCoder {

    // mapping of parity bit's index to coverage indices in the encoded bit stream
    private Map<Integer, List<Integer>> parityMap;
    private ResourceBundle resourceBundle;

    public HammingCoder(){
        parityMap = new HashMap<>();
    }

    // TODO: factor down this method
    public BinaryCode encode(BinaryCode code){
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
            List<Boolean> coverage = new ArrayList<>();
            for(int index : parityMap.get(parityIndex)){
                if(bits.size()<=index) break;
                coverage.add(bits.get(index));
            }
            bits.set(parityIndex,setParityBit(coverage));
            parityIndex*=2;
        }
        // determine value for overall parity bit
        List<Boolean> coverage = new ArrayList<>();
        for(int index = 1; index<bits.size(); index++){
            coverage.add(bits.get(index));
        }
        bits.set(0,setParityBit(coverage));
        // construct a new coder.BinaryCode object based on encoded bit stream
        return new BinaryCode(bits);
    }

    public BinaryCode decode(BinaryCode code){
        int numPBits = numParityBitsDecode(code.length());
        if(parityMap.size() < numPBits){
            expandParityMap(numPBits);
        }
        List<Boolean> bits = code.getBits();
        List<Boolean> decodedBits = new ArrayList<>();
        for(int i = 1; i<bits.size(); i++){
            if(!parityMap.containsKey(i)){
                decodedBits.add(bits.get(i));
            }
        }
        return new BinaryCode(decodedBits);
    }

    /**
     * @param code bit stream to validate against single/double bit corruption
     *             set code.singleError as true if detecting a single bit corruption,
     *             and set code.singleErrorBit as the corrupted bit index;
     *             set code.doubleError as true if detecting double bit corruptions.
     */
    public void validateCode(BinaryCode code){
        Integer singleErrorBit = detectSingleError(code);
        code.setSingleErrorBit(singleErrorBit);
        code.setSingleError((singleErrorBit!=-1 && !validateOverall(code)));
        code.setDoubleError((singleErrorBit==-1 && validateOverall(code)));
    }


    /**
     * @param code bit stream to validate against single bit corruption
     * @return bit index of the detected single error. Return -1 if no single error detected.
     */
    private Integer detectSingleError(BinaryCode code){
        int numPBits = numParityBitsDecode(code.length());
        if(parityMap.size() < numPBits){
            expandParityMap(numPBits);
        }

        Boolean flag = false;
        List<Boolean> bits = code.getBits();
        List<Integer> corruption = IntStream.rangeClosed(1,bits.size()).boxed().collect(Collectors.toList());
        int parityIndex = 1;
        while(parityIndex <= Math.pow(2,numPBits-1)){
            List<Boolean> coverage = new ArrayList<>();
            for(int index : parityMap.get(parityIndex)){
                coverage.add(bits.get(index));
            }
            if(validateParityBit(bits.get(parityIndex),coverage)){
                flag = true;
                corruption.retainAll(parityMap.get(parityIndex));
            }
            parityIndex*=2;
        }
        if(flag){
            assert (corruption.size()==1);
            return corruption.get(0);
        }
        return -1;
    }

    private Boolean validateOverall(BinaryCode code){
        List<Boolean> bits = new ArrayList<>();
        for(int i = 1; i<code.getBits().size(); i++){
            if(!parityMap.containsKey(i)){
                bits.add(bits.get(i));
            }
        }
        return validateParityBit(bits.get(0),bits);
    }

    private Integer numParityBitsEncode(Integer wordLength){
        int numBits = 0;
        while(Math.pow(2,numBits) < wordLength+numBits+1){
            numBits++;
        }
        return numBits;
    }

    private Integer numParityBitsDecode(Integer wordLength){
        int numBits = 0;
        while(Math.pow(2,numBits) < wordLength){
            numBits++;
        }
        return numBits;
    }

    private void expandParityMap(Integer targetMapSize){
        int maxWordLength = (int) Math.pow(2,targetMapSize)-targetMapSize-1;
        for(int parity = parityMap.size()+1; parity<=targetMapSize; parity++){
            parityMap.put((int)Math.pow(2,parity-1),new ArrayList<>());
            for(int index = (int) Math.pow(2,parity-1)+1; index<maxWordLength; index++){
                String binary = Integer.toBinaryString(index);
                if(binary.charAt(binary.length()-parity)=='1'){
                    parityMap.get((int)Math.pow(2,parity-1)).add(index);
                }
            }
        }
    }

    /**
     * @param parityBit a specific parity bit's value
     * @param bits values of bits covered by the parity bit
     * @return true if even-parity is maintained, false otherwise
     */
    private Boolean validateParityBit(Boolean parityBit, Collection<Boolean> bits){
        Boolean expected = setParityBit(bits);
        return parityBit==expected;
    }

    private Boolean setParityBit(Collection<Boolean> bits){
        int count = 0;
        for(Boolean bit : bits){
            count = bit? count+1 : count;
        }
        return count%2!=0;
    }
}
