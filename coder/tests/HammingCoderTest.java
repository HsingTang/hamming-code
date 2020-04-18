import java.util.Random;

import code.HammingCoder;
import exception.HammingException;
import org.junit.Assert;
import org.junit.Test;

public class HammingCoderTest {

    HammingCoder coder = new HammingCoder();
    String dummyOriginalBits = "00011000";

    @Test
    public void testEncoding() throws HammingException {
        String encodedDummyBits = coder.encode(dummyOriginalBits);
        String expectedEncoding = "1010100111000";
        Assert.assertEquals(encodedDummyBits,expectedEncoding);
    }

    @Test
    public void testDecoding() throws HammingException{
        String decodedDummyBits = coder.decode(dummyOriginalBits);
        String expectedDecoding = "1000";
        Assert.assertEquals(decodedDummyBits,expectedDecoding);
    }

    @Test
    public void testRecoverBits() throws HammingException{
        Random rand = new Random();
        for (int i = 0; i<100; i++){
            String content = Integer.toBinaryString(rand.nextInt(Integer.MAX_VALUE));
            String encodedContent = coder.encode(content);
            String decodedContent = coder.decode(encodedContent);
            Assert.assertEquals(content,decodedContent);
        }
    }
}