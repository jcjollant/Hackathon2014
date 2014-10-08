package executor;

import static org.junit.Assert.*;

import org.junit.Test;

import executor.Endianness;
import executor.Message;

public class MessageTest {

	@Test
	public void test32() {
		Message msgL = new Message( Endianness.LITTLE, 4);
		Message msgB = new Message( Endianness.BIG, 4);
		int testValue = 0x01234567;
		
		msgL.encode( testValue, 0);
		assertEquals( (byte)0x01, msgL.getByte(3));
		assertEquals( (byte)0x23, msgL.getByte(2));
		assertEquals( (byte)0x45, msgL.getByte(1));
		assertEquals( (byte)0x67, msgL.getByte(0));
		
		int decodedValue = msgL.decodeInt32(0);
		assertEquals( testValue, decodedValue);
		
		msgB.encode( testValue, 0);
		assertEquals( (byte)0x67, msgB.getByte(3));
		assertEquals( (byte)0x45, msgB.getByte(2));
		assertEquals( (byte)0x23, msgB.getByte(1));
		assertEquals( (byte)0x01, msgB.getByte(0));
		
		decodedValue = msgB.decodeInt32(0);
		assertEquals( testValue, decodedValue);
		
		testValue = 0xFEDCBA98;
		
		msgL.encode( testValue, 0);
		assertEquals( (byte)0xFE, msgL.getByte(3));
		assertEquals( (byte)0xDC, msgL.getByte(2));
		assertEquals( (byte)0xBA, msgL.getByte(1));
		assertEquals( (byte)0x98, msgL.getByte(0));
		
		decodedValue = msgL.decodeInt32(0);
		assertEquals( testValue, decodedValue);
		
		msgB.encode( testValue, 0);
		assertEquals( (byte)0x98, msgB.getByte(3));
		assertEquals( (byte)0xBA, msgB.getByte(2));
		assertEquals( (byte)0xDC, msgB.getByte(1));
		assertEquals( (byte)0xFE, msgB.getByte(0));
		
		decodedValue = msgB.decodeInt32(0);
		assertEquals( testValue, decodedValue);
	}

	@Test
	public void test64() {
		Message msgL = new Message( Endianness.LITTLE, 8);
		Message msgB = new Message( Endianness.BIG, 8);
		long testValue = 0x0123456789ABCDEFL;
		
		msgL.encode( testValue, 0);
		assertEquals( (byte)0x01, msgL.getByte(7));
		assertEquals( (byte)0x23, msgL.getByte(6));
		assertEquals( (byte)0x45, msgL.getByte(5));
		assertEquals( (byte)0x67, msgL.getByte(4));
		assertEquals( (byte)0x89, msgL.getByte(3));
		assertEquals( (byte)0xAB, msgL.getByte(2));
		assertEquals( (byte)0xCD, msgL.getByte(1));
		assertEquals( (byte)0xEF, msgL.getByte(0));
		
		long decodedValue = msgL.decodeInt64(0);
		assertEquals( testValue, decodedValue);
		
		msgB.encode( testValue, 0);
		assertEquals( (byte)0xEF, msgB.getByte(7));
		assertEquals( (byte)0xCD, msgB.getByte(6));
		assertEquals( (byte)0xAB, msgB.getByte(5));
		assertEquals( (byte)0x89, msgB.getByte(4));
		assertEquals( (byte)0x67, msgB.getByte(3));
		assertEquals( (byte)0x45, msgB.getByte(2));
		assertEquals( (byte)0x23, msgB.getByte(1));
		assertEquals( (byte)0x01, msgB.getByte(0));
		
		decodedValue = msgB.decodeInt64(0);
		assertEquals( testValue, decodedValue);
	}

	@Test
	public void test8() {
		Message msgL = new Message( Endianness.LITTLE, 1);
		Message msgB = new Message( Endianness.BIG, 1);
		byte testValue = (byte)0xEF;
		
		msgL.encode( testValue, 0);
		assertEquals( (byte)0xEF, msgL.getByte(0));
		
		byte decodedValue = msgL.decodeInt8(0);
		assertEquals( testValue, decodedValue);
		
		msgB.encode( testValue, 0);
		assertEquals( (byte)0xEF, msgB.getByte(0));
		
		decodedValue = msgB.decodeInt8(0);
		assertEquals( testValue, decodedValue);
	}

	@Test
	public void testString() {
		Message msg = new Message( Endianness.LITTLE, 30);
		String testValue = Display.randomString(20);
		msg.encode( testValue, 5, testValue.length());
		String decodedValue = msg.decodeString(5, testValue.length());

		assertEquals( testValue, decodedValue);
		
		// Test with a string that's incomplete
		msg.reset();
		
//		testValue = Display.randomString(10);
//		msg.encode( testValue, 0, msg.getSize());
//		decodedValue = msg.decodeString(0, msg.getSize());
//		
//		assertEquals( testValue, decodedValue);
	}

}
