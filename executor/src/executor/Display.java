package executor;

import java.util.Random;

public class Display {
	
final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

public static String bytesToHex( byte[] bytes, int length) {
	StringBuffer sb = new StringBuffer();
	int count = 0;
	int last16[] = new int[16];
	
    for ( int j = 0; j < length; j++ ) {

    	if( count == 0) {
    		sb.append( String.valueOf( j + 1000).substring(1));
    		sb.append( ": ");
    	}
    	
        int v = bytes[j] & 0xFF;
        last16[count] = v;
        sb.append( ' ');
        sb.append( hexArray[v >>> 4]);
        sb.append( hexArray[v & 0x0F]);
        if( ++count == 8) {
        	sb.append( " -");
        } else if( count == 16) {
        	sb.append( " | ");
        	for( int index = 0; index < 16; index++) {
        		if( index == 8) sb.append( " - ");
        		if( last16[index] < 32 || last16[index] > 126) {
        			sb.append('.');
        		} else {
        			sb.append( (char)last16[index]);
        		}
        	}
        	sb.append( "\n");
        	count = 0;
        	
        }
    }
    
    // complete last line textual representation
    if( count < 16) {
    	int remainder = count;
	    while( remainder++ < 16) {
	    	if( remainder == 9) sb.append( " -");
	    	sb.append( "   ");
	    }
	    
	    sb.append( " | ");
    	for( int index = 0; index < count; index++) {
    		if( index == 8) sb.append( " - ");
    		if( last16[index] < 32) {
    			sb.append('.');
    		} else {
    			sb.append( (char)last16[index]);
    		}
    	}
    }
    
    
    
    return sb.toString();
}

public static String randomString( int length) {
	char[] chars = "0123456789".toCharArray();
	StringBuilder sb = new StringBuilder();
	Random random = new Random();
	for (int i = 0; i < length; i++) {
	    char c = chars[random.nextInt(chars.length)];
	    sb.append(c);
	}
	return sb.toString();	
}

}
