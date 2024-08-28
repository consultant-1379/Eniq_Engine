package com.distocraft.dc5000.etl.engine.common;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Vector;

/**
 *
 * Sends e-mail.
 * Create an instance of eMail using default constructor
 * or other constructors. If used default constructor set
 * needed values (mailserver, port, recipients, message)
 * If mail is sent out, set domain to be same as the end
 * in e-mail adress. 
 * Then just call sendMail().
 * 
 * original author Kari Hippolin
 * @author $Author: savinen $
 * @since JDK1.2
 */

public class eMail {

    private String   myMailServer    =  null;
    private int      myPort          =  0;
    private String   myDomain		 =  null;
    private String   mySenderName    =  null;
    private String   mySenderAddress =  null;
    private Vector   myRecipients    =  new Vector();
    private String   mySubject   	 =  null;
    private String   myMessage       =  null;


    /*
     * Default constructor
     */

    public eMail() {
	
    }

    /*
     * Constructor with spcefied values
     *
     * @param server		name of senders mailserver
     * @param port		mail servers port
     * @param domain		what is senders domain (same as end in senders e-mail)
     * @param sender		senders name
     * @param address	senders e-mail address
     * @param recipient	to whom mail is sent to
     * @param subject	mails subject
     * @param message	mails body
     *
     */
    public eMail(String server, int port, String domain, String sender,
		 String address, String recipient,
		 String subject, String message) {
	
	myMailServer = server;
	myPort = port;
	myDomain = domain;
	mySenderName = sender;
	mySenderAddress = address;
	myRecipients.addElement(recipient);
	mySubject = subject;
	myMessage = message;	
    }
    
    /*
     * Constructor with spcefied values
     *
     * @param server		name of senders mailserver
     * @param port		mail servers port
     * @param domain		what is senders domain (same as end in senders e-mail)
     * @param sender		senders name
     * @param address	senders e-mail address
     * @param recipient	to whose mail is sent to
     * @param subject	mails subject
     * @param message	mails body
     *
     */
    public eMail(String server, int port, String domain, String sender,
		 String address, Vector recipients,
		 String subject, String message) {
	
	myMailServer = server;
	myPort = port;
	myDomain = domain;
	mySenderName = sender;
	mySenderAddress = address;
	myRecipients = recipients;
	mySubject = subject;
	myMessage = message;	
    }
    
    
    /*
     * Sets mailserver
     *
     * @param server senders mailserver
     */
    public void setMailServer( String server) {
	myMailServer = server;
    }
    
    
    /*
     * Sets mailservers port
     *
     * @param port mailservers port
     */
    public void setPort( int port ) {
	myPort = port;
    }
    
    
    /*
     * Sets domain
     *
     * @param domain senders domain
     */
    public void setDomain( String domain ) {
	myDomain = domain;
    }
    
    /*
     * Sets senders name
     *
     * @param sender senders name
     */
    public void setSenderName( String sender ) {
	mySenderName = sender;
    }
    
    /*
     * Sets senders e-mail address
     *
     * @param address senders e-mail address
     */
    public void setSenderAddress( String address ) {
	mySenderAddress = address;
    }
    
    /*
     * Sets recipient
     *
     * @param recipient to whom mail is sent to
     */
    public void setRecipient( String recipient ) {
	myRecipients = new Vector();
	myRecipients.addElement(recipient);
    }
    
    /*
     * Sets recipients
     *
     * @param recipients to who mail is sent to
     */
    public void setRecipients( Vector recipients ) {
	myRecipients = recipients;
    }
    
    /*
     * Adds recipient to recipients list
     *
     * @param recipient to who mail is sent to
     */
    public void addRecipient( String recipient ) {
	myRecipients.addElement( recipient );
    }
    
    /*
     * Sets mails subject
     *
     * @param subject sobject of mail
     */
    public void setSubject( String subject ) {
	mySubject = subject;
    }
    
    /*
     * Sets message
     *
     * @param message mails body
     */
    public void setMessage( String message ) {
	myMessage = message;
    }
    
	/*
	* Sends mail. Constructs mail to be sent
	* and sends it.
	*
	* @returns true if mail is sent
	* @returns false if all necessary parameters are not set
	*/
	public boolean sendMail() {

		String str = null;

		if( !( ( myMailServer == null ) || ( myPort == 0 ) || ( myMessage == null ) || myRecipients.isEmpty() ) ) {

			try {

				int i = 0;

				// Opens connection
				Socket s = new Socket( myMailServer, myPort );
					
				BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream(), "8859_1"));
				PrintWriter ps = new PrintWriter(s.getOutputStream(), true); 
				// PrintStream ps = new PrintStream(s.getOutputStream(), true);

				ps.println( "HELO " + myDomain );
				str = in.readLine();
			
				ps.println( "MAIL FROM: <" + mySenderAddress + ">" );
				str = in.readLine();

				myRecipients.trimToSize();
				while(i < myRecipients.size()) {
					ps.println( "RCPT TO: " + myRecipients.elementAt(i) );
					str = in.readLine();	
					i++;
				}

				ps.println( "DATA" );
				str = in.readLine();
				ps.println( "Subject: " + mySubject );
				ps.println( "From: " + mySenderName + " <" + mySenderAddress + ">" );
				ps.println( "\n" );
				ps.println( myMessage );
				ps.println( "\n.\n" );
			
				ps.println( "QUIT" );
				str = in.readLine();
			
				// Closes connection
				s.close();

			}

			catch( Exception e ) {
				e.printStackTrace();
				return false;
			}
		}

		else {
			return false;
		}

		return true;

	}
    
}
