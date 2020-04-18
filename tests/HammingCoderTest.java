import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.Test;

public class HammingCoderTest {

    HammingCoder coder = new HammingCoder();
    BinaryCode dummyOriginalBits = new BinaryCode("00011000");

    @Test
    public void testEncoding(){
        BinaryCode encodedDummyBits = coder.encode(dummyOriginalBits);
        BinaryCode expectedEncoding = new BinaryCode("1010100111000");
        List<Boolean> encodedBits = encodedDummyBits.getBits();
        List<Boolean> expectedBits = expectedEncoding.getBits();
        Assert.assertEquals(encodedBits.size(),expectedBits.size());
        Assert.assertArrayEquals(encodedBits.toArray(),expectedBits.toArray());
    }

    @Test
    public void testDecoding(){
        BinaryCode encodedDummyBits = coder.decode(dummyOriginalBits);
        BinaryCode expectedEncoding = new BinaryCode("1000");
        List<Boolean> encodedBits = encodedDummyBits.getBits();
        List<Boolean> expectedBits = expectedEncoding.getBits();
        Assert.assertEquals(encodedBits.size(),expectedBits.size());
        Assert.assertArrayEquals(encodedBits.toArray(),expectedBits.toArray());
    }

    @Test
    public void testRecoverBits(){
        for (int it = 0; it<10; it++){
            IntStream is = new Random().ints(it*10, 0, 2);
            String content = is.collect(
                    StringBuilder::new,
                    (sb, i) -> sb.append((char)i),
                    StringBuilder::append
            ).toString();
            BinaryCode dummyBits = new BinaryCode(content);
            BinaryCode encodedDummyBits = coder.encode(dummyBits);
            BinaryCode decodedDummyBits = coder.decode(encodedDummyBits);
            Assert.assertArrayEquals(dummyBits.getBits().toArray(),decodedDummyBits.getBits().toArray());
        }
    }
}
