package com.samuel.bind.smppind;


import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ie.omk.smpp.Address;
import ie.omk.smpp.Connection;
import ie.omk.smpp.message.BindResp;
import ie.omk.smpp.message.SMPPPacket;
import ie.omk.smpp.message.SubmitSM;
import ie.omk.smpp.message.SubmitSMResp;
import ie.omk.smpp.message.UnbindResp;
import ie.omk.smpp.version.SMPPVersion; 


/** A synchronous transceiver example. Using sync mode for either a 
transceiver 
 * or receiver connection is less useful than using async mode as 
your 
 * application must now poll the connection continuously for incoming 
delivery 
 * messages from the SMSC. 
 * 
 * @see ie.omk.smpp.examples.ParseArgs ParseArgs for details on 
running 
 * this class. 
 */ 
public class App { 


    private HashMap myArgs = new HashMap(); 


    private Connection myConnection = null; 


    private Log logger = LogFactory.getLog(App.class); 


    public App() { 
    } 


    private void init(String[] args) { 
        try { 
            //myArgs = ParseArgs.parse(args); 


            //int port = Integer.parseInt((String)myArgs.get(ParseArgs.PORT)); 


            myConnection = new Connection("212.83.163.254", 2875); 
            myConnection.setVersion(SMPPVersion.V34); 
            myConnection.autoAckLink(false); 
            myConnection.autoAckMessages(true); 


        } catch (Exception x) { 
            logger.info("Bad command line arguments."); 
        } 
    } 


    private void run() { 
        try { 
            logger.info("Binding to the SMSC"); 
            // Bind the short way: 
            BindResp resp = myConnection.bind(Connection.TRANSCEIVER, 
                    (String)myArgs.get("bankai"), 
                    (String)myArgs.get("bankai01"), 
                    (String)myArgs.get("NONE"), 
                    Integer.parseInt("0"), 
                    Integer.parseInt("0"), 
                    (String)myArgs.get("0")); 


            if (resp.getCommandStatus() != 0) { 
                logger.info("SMSC bind failed."); 
                System.exit(1); 
            } 


            logger.info("Bind successful...submitting a message."); 


            // Submit a simple message 
            SubmitSM sm = (SubmitSM)myConnection.newInstance(SMPPPacket.SUBMIT_SM); 
            sm.setDestination(new Address(0, 0, "18.217.239.81")); 
            sm.setMessageText("This is an example short message."); 
            SubmitSMResp smr = (SubmitSMResp)myConnection.sendRequest 
(sm); 


            logger.info("Submitted message ID: " + smr.getMessageId 
()); 


            try { 
                // Wait a while, see if the SMSC delivers anything to us... 
                SMPPPacket p = myConnection.readNextPacket(); 
                logger.info("Received a packet!"); 
                logger.info(p.toString()); 


                // API should be automatically acking deliver_sm and 
                // enquire_link packets... 
            } catch (java.net.SocketTimeoutException x) { 
                // ah well... 
            } 


            // Unbind. 
            UnbindResp ubr = myConnection.unbind(); 


            if (ubr.getCommandStatus() == 0) { 
                logger.info("Successfully unbound from the SMSC"); 
            } else { 
                logger.info("There was an error unbinding."); 
            } 
        } catch (Exception x) { 
            logger.info("An exception occurred."); 
            x.printStackTrace(System.err); 
        } 
    } 


    public static final void main(String[] args) { 
        App t = new App(); 
        t.init(args); 
        t.run(); 
    } 
} 