import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays.*;

public class Tokenizer {
    final int buffSize = 20000;
    
    String fileContent = new String("");
    int    pos = 0;

    public Tokenizer(String fileName)
        throws FileNotFoundException,
               UnsupportedEncodingException,
               IOException {
        // Constructor - reads and loads in memory.
        char[] cbuf = new char[buffSize];
        int    charsRead;
        InputStreamReader isr = new InputStreamReader(new
                       FileInputStream(fileName), "UTF-8");
        //System.out.println("call initializer");
        while ((charsRead = isr.read(cbuf, 0, buffSize)) != -1) {
          fileContent += new String(java.util.Arrays.copyOfRange(cbuf,
                                    0, charsRead));
        }
        isr.close();
    }

    public String nextToken() {
        String  tok = "";
        char    c;
        boolean last_was_quote = false;
        char newLine = System.getProperty("line.separator").toCharArray()[0];

        try {
            while ((fileContent.subSequence(pos, pos+1).charAt(0)) == ',') {
              pos++;
            }
            c = fileContent.subSequence(pos,pos+1).charAt(0);

            while (((c != ',') )|| (last_was_quote = (c == '\"')) ) {
              tok += fileContent.substring(pos,pos+1);
              pos++;
              c = fileContent.subSequence(pos,pos+1).charAt(0);

              if (c == newLine ) break;
            }
            if (last_was_quote) {
              // Remove ending quote
              tok = tok.substring(0, tok.length()-1);
            }
        } catch (IndexOutOfBoundsException e) {
            return null;
        }

        //System.out.println("!!!"+tok.toLowerCase());
        return tok;
    }

}
