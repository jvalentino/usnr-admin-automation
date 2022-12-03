
import java.io.Console;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;

import javax.smartcardio.ATR;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;

public class PCSC {
       
        private byte[] atr = null;
        private String protocol = null;
        private byte[] historical = null;
       
        public CardTerminal selectCardTerminal()
        {
                try
                {
                        // show the list of available terminals
                        TerminalFactory factory = TerminalFactory.getDefault();
                        List<CardTerminal> terminals = factory.terminals().list();
                        ListIterator<CardTerminal> terminalsIterator = terminals.listIterator();
                        CardTerminal terminal = null;
                        CardTerminal defaultTerminal = null;
                        if(terminals.size() > 1)
                        {
                                System.out.println("Please choose one of these card terminals (1-" + terminals.size() + "):");
                                int i = 1;
                                while(terminalsIterator.hasNext())
                                {
                                        terminal = terminalsIterator.next();
                                        System.out.print("["+ i + "] - " + terminal + ", card present: "+terminal.isCardPresent());
                                        if(i == 1)
                                        {
                                                defaultTerminal = terminal;
                                                System.out.println(" [default terminal]");
                                        }
                                        else
                                        {
                                                System.out.println();
                                        }                                      
                                        i++;
                                }
                                Scanner in = new Scanner(System.in);
                                try
                                {
                                        int option = in.nextInt();
                                        terminal = terminals.get(option-1);                                    
                                }
                                catch(Exception e2)
                                {
                                        //System.err.println("Wrong value, selecting default terminal!");
                                        terminal = defaultTerminal;
                                       
                                }
                                System.out.println("Selected: "+terminal.getName());
                                //Console console = System.console();
                                return terminal;
                        } else {
                        	
                        	return terminals.get(0);
                        }
                       

                }
                catch(Exception e)
                {
                        System.err.println("Error occured:");
                        e.printStackTrace();
                }
                return null;
        }

        public String byteArrayToHexString(byte[] b)
        {
            StringBuffer sb = new StringBuffer(b.length * 2);
            for (int i = 0; i < b.length; i++) {
              int v = b[i] & 0xff;
              if (v < 16) {
                sb.append('0');
              }
              sb.append(Integer.toHexString(v));
            }
            return sb.toString().toUpperCase();
         }

        public static byte[] hexStringToByteArray(String s)
        {    
                int len = s.length();    
                byte[] data = new byte[len / 2];    
                for (int i = 0; i < len; i += 2)
                {        
                        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));    
                }    
                return data;
        }      

        public Card establishConnection(CardTerminal ct)
        {
                this.atr = null;
                this.historical = null;
                this.protocol = null;

                System.out.println("To establish connection, please choose one of these protocols (1-4):");
                System.out.println("[1] - T=0");
                System.out.println("[2] - T=1");
                System.out.println("[3] - T=CL");
                System.out.println("[4] - * [default]");
               
                String p = "*";
                Scanner in = new Scanner(System.in);
               
                try
                {
                        int option = in.nextInt();
                       
                        if(option == 1) p = "T=0";
                        if(option == 2) p = "T=1";
                        if(option == 3) p = "T=CL";
                        if(option == 4) p = "*";                        
                }
                catch(Exception e)
                {
                        //System.err.println("Wrong value, selecting default protocol!");
                        p = "*";
                }
               
                System.out.println("Selected: "+p);
               
                Card card = null;
                try
                {
                        card = ct.connect(p);
                }
                catch (CardException e)
                {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        return null;
                }
                ATR atr = card.getATR();
                System.out.println("Connected:");      
                System.out.println(" - ATR:  "+ byteArrayToHexString(atr.getBytes()));
                System.out.println(" - Historical: "+ byteArrayToHexString(atr.getHistoricalBytes()));
                System.out.println(" - Protocol: "+card.getProtocol());
               
                this.atr = atr.getBytes();
                this.historical = atr.getHistoricalBytes();
                this.protocol = card.getProtocol();
               
                return card;            
               
        }
       
        /**
         * @param args
         */
        public static void main(String[] args) {
                // TODO Auto-generated method stub
               System.out.println("Running");
                PCSC pcsc = new PCSC();
                CardTerminal ct = pcsc.selectCardTerminal();
                Card c = null;
                if(ct != null)
                {
                        c = pcsc.establishConnection(ct);
                        CardChannel cc = c.getBasicChannel();
                    //byte[] SELECT = {(byte) 0x00, (byte) 0xA4, (byte) 0x04, (byte) 0x00, (byte) 0x09, (byte) 0x74, (byte) 0x69, (byte) 0x63, (byte) 0x6B, (byte) 0x65, (byte) 0x74, (byte) 0x69, (byte) 0x6E, (byte) 0x67, (byte) 0x00};
                        //byte[] baCommandAPDU = {(byte) 0x00, (byte) 0xA4, (byte) 0x04, (byte) 0x00, (byte) 0x08, (byte) 0xA0, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00};
                        //byte[] baCommandAPDU = {(byte) 0x00, (byte) 0xA4, (byte) 0x04, (byte) 0x00, (byte) 0x07, (byte) 0xA0, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x51, (byte) 0x00, (byte) 0x00};
                        
                        // 0xB0 0x04 0x00 0x00 0x00 0x04
                        byte[] aid = {(byte) 0xA0, 0x00, 0x00, 0x03, 0x08, 0x00, 0x00, 0x10, 0x00};
                        byte[] SELECT  = {(byte) 0xB0, (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x04 };
                        byte[] apdu = {0x00, (byte)0xCB, 0x3F, 
                                (byte)0xFF, 0x05, 0x5C, 0x03, 0x5F, (byte)0xC1, 0x05};
                        
                        try
                        {
                                System.out.println("TRANSMIT: "+pcsc.byteArrayToHexString(apdu));      
                                ResponseAPDU r = cc.transmit(new CommandAPDU(apdu));
                                System.out.println("RESPONSE: "+pcsc.byteArrayToHexString(r.getBytes()));
                        }
                        catch (CardException e)
                        {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                        }
                }
        }

}

