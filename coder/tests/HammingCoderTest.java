import java.util.Random;

import code.HammingCoder;
import exception.DoubleBitErrorException;
import exception.InvalidInputFormatException;
import exception.SingleBitErrorException;
import org.junit.Assert;
import org.junit.Test;


public class HammingCoderTest {

    private HammingCoder coder = new HammingCoder();

    @Test
    public void testEncoding() throws InvalidInputFormatException {
        String encoded = coder.encode("00011000");
        String expected = "1010100111000";
        Assert.assertEquals(encoded,expected);
    }

    @Test
    public void testDecoding() throws InvalidInputFormatException{
        String decoded = coder.decode("00011000");
        String expected = "1000";
        Assert.assertEquals(decoded,expected);
    }

    @Test
    public void testRecoverBits() throws InvalidInputFormatException{
        Random rand = new Random();
        for (int i = 0; i<100; i++){
            String original = Integer.toBinaryString(rand.nextInt(Integer.MAX_VALUE));
            String encoded = coder.encode(original);
            String decoded = coder.decode(encoded);
            Assert.assertEquals(original,decoded);
        }
    }

    @Test
    public void testSingleBitCorruptionDetection() throws InvalidInputFormatException,DoubleBitErrorException {
        Random rand = new Random();
        for (int i = 0; i<10000; i++){
            String original = Integer.toBinaryString(rand.nextInt(Integer.MAX_VALUE));
            String encoded = coder.encode(original);
            char[] chars = encoded.toCharArray();
            int corruptedIdx = rand.nextInt(encoded.length());
            chars[corruptedIdx] = chars[corruptedIdx]=='1'? '0':'1';
            String corrupted = new String(chars);
            try {
                coder.validateCode(corrupted);
                Assert.fail("Should have thrown SingleBitErrorException.\n" +
                        "Encoded String: "+encoded+"\n corrupted at index "+ corruptedIdx +": "+corrupted);
            }catch (SingleBitErrorException e){
                Assert.assertEquals(corruptedIdx,e.getErrorBitIndex());
            }
        }
    }

    @Test(expected = DoubleBitErrorException.class)
    public void testDoubleBitCorruptionDetection() throws InvalidInputFormatException,SingleBitErrorException,DoubleBitErrorException {
        Random rand = new Random();
        for (int i = 0; i<10000; i++){
            String original = Integer.toBinaryString(rand.nextInt(Integer.MAX_VALUE));
            String encoded = coder.encode(original);
            char[] chars = encoded.toCharArray();
            int corruptedIdxA = rand.nextInt(encoded.length());
            int corruptedIdxB = rand.nextInt(encoded.length());
            chars[corruptedIdxA] = chars[corruptedIdxA]=='1'? '0':'1';
            chars[corruptedIdxB] = chars[corruptedIdxB]=='1'? '0':'1';
            String corrupted = new String(chars);
            coder.validateCode(corrupted);
        }
    }
}
