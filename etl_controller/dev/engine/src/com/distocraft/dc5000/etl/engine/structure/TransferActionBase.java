package com.distocraft.dc5000.etl.engine.structure;

import java.util.Date;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;
import sun.rmi.runtime.Log;

import com.distocraft.dc5000.etl.engine.common.EngineConstants;
import com.distocraft.dc5000.etl.engine.common.EngineException;
import com.distocraft.dc5000.etl.engine.common.EngineMetaDataException;
import com.distocraft.dc5000.etl.engine.common.ExceptionHandler;
import com.distocraft.dc5000.etl.engine.common.RemoveDataException;
import com.distocraft.dc5000.etl.engine.common.eMail;
import com.distocraft.dc5000.etl.engine.system.SetListener;
import com.distocraft.dc5000.etl.engine.system.StatusEvent;
import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_versions;

public class TransferActionBase implements ITransferAction {

  // metadata version
  private Meta_versions version;

  // primary key for collection set
  private Long collectionSetId;

  // primary key for collection
  private Meta_collections collection;

  // primary key for transfer action
  private Long transferActionId;

  // name of the transfer action
  private String transferActionName;

  // primary key for transfer batch
  private Long transferBatchId;

  // primary key for database connections
  private Long connectionId;

  // metadata repository connection object
  private RockFactory rockFact;

  // object that holds transfer action information (db contents)
  private Meta_transfer_actions trActions;

  // The version information
  private Meta_versions dbVersion;

  // Collection set
  private Meta_collection_sets collSet;

  private Logger log;
  // Set listener. It can be accessed through
  // The default non-functional set listener is temporarily replaced during the 
  // method call execute(SetListener setListener). 
  protected SetListener setListener = SetListener.NULL;
  
  /**
   * Empty protected constructor
   * 
   */
  protected TransferActionBase() {
  }

  /**
   * Constructor
   * 
   * @param versionNumber
   *          metadata version
   * @param collectionSetId
   *          primary key for collection set
   * @param collectionId
   *          primary key for collection
   * @param transferActionId
   *          primary key for transfer action
   * @param transferBatchId
   *          primary key for transfer batch
   * @param connectionId
   *          primary key for database connections
   * @param rockFact
   *          metadata repository connection object
   * @param trActions
   *          object that holds transfer action information (db contents)
   * 
   * @author Jukka J‰‰heimo
   * @since JDK1.1
   */
  public TransferActionBase(Meta_versions version, Long collectionSetId, Meta_collections collection,
      Long transferActionId, Long transferBatchId, Long connectionId, RockFactory rockFact,
      Meta_transfer_actions trActions) {

    this.version = version;
    this.collectionSetId = collectionSetId;
    this.collection = collection;
    this.transferActionId = transferActionId;
    this.transferBatchId = transferBatchId;
    this.connectionId = connectionId;
    this.rockFact = rockFact;
    this.trActions = trActions;
    this.transferActionName = trActions.getTransfer_action_name();

    try {
      // Get collection set name
      Meta_collection_sets whereCollSet = new Meta_collection_sets(rockFact);
      whereCollSet.setEnabled_flag("Y");
      whereCollSet.setCollection_set_id(collectionSetId);
      this.collSet = new Meta_collection_sets(rockFact, whereCollSet);
    } catch (Exception e) {
    	log.warning("Cannot run " + this.collSet.getCollection_set_name() + "." + collection.getSettype() + "."
                + collection.getCollection_name() + "." + this.transferActionName);
    	log.warning(e.getMessage());
    	ExceptionHandler.handleException(e);
    }

  }
  
  /**
   * Just to implement the method, the real operation is implemented through
   * inheritance
   * 
   * 
   */
  public void execute() throws Exception {
	  
  }
  
  /**
   * Sets the member variable TransferActionBase.setListener before calling 
   * the execute() method.
   * 
   * The classes that inherit and override the execute() method can use 
   * TransferActionBase.setListener to report their progress.
   * 
   * @param setListener
   * @throws Exception
   */
  public final void execute(SetListener setListener) throws Exception {
	try {
		this.setListener = setListener;
    	execute();
    }
    finally {
    	this.setListener = SetListener.NULL;
    }
  }

  /**
   * Just to implement the method, the real operation is implemented through
   * inheritance
   * 
   */
  public void removeDataFromTarget() throws EngineMetaDataException, RemoveDataException {
  }

  /**
   * A method that is overwritten in SQLFkFactory
   * 
   */
  public int executeFkCheck() throws EngineException {
    return -1;
  }

  /**
   * A method that is overwritten in SQLColConstraint
   * 
   */
  public int executeColConstCheck() throws EngineException {
    return -1;
  }

  /**
   * 
   */
  public boolean isGateClosed() {
    return false;
  }

  /**
   * Writes debug information into the database
   * 
   * @param debugText
   *          text to write into db.
   */
  public void writeDebug(String debugText) throws EngineMetaDataException {
    try {
      if (debugText == null)
        debugText = EngineConstants.NO_DEBUG_TEXT;

      Logger.getLogger(
          "etl." + this.collSet.getCollection_set_name() + "." + collection.getSettype() + "."
              + collection.getCollection_name() + "." + this.transferActionName + ".Engine").log(Level.FINE,
          "DEBUG: " + debugText);

    } catch (Exception e) {
      throw new EngineMetaDataException(EngineConstants.CANNOT_WRITE_DEBUG, e, this.getClass().getName());
    }
  }

  /**
   * Protected method to write error information into the database
   * 
   * @param errorText
   *          text to write into db.
   * @param errType
   *          the type of the error.
   */
  protected void writeErrorProt(String errorText, String methodName, String errType) throws EngineMetaDataException {
    try {
      if (errorText == null) {
        errorText = EngineConstants.NO_ERROR_TEXT;
      }

      Logger.getLogger(
          "etl." + this.collSet.getCollection_set_name() + "." + collection.getSettype() + "."
              + collection.getCollection_name() + "." + this.transferActionName + ".TransferActionBase").log(
          Level.SEVERE, "ERROR in action: " + methodName + " " + errType + " " + errorText);

    } catch (Exception e) {
      throw new EngineMetaDataException(EngineConstants.CANNOT_WRITE_ERROR, e, this.getClass().getName());
    }

    if ((version.getMail_server() != null) && (version.getMail_server_port() != null)) {

      eMail em = new eMail();

      // Your own mailserver
      em.setMailServer("" + this.version.getMail_server() + "");
      em.setPort(this.version.getMail_server_port().intValue());

      em.setSenderName(EngineConstants.ERR_MAIL_SENDER);

      // Something about sender
      em.setDomain("" + this.version.getMail_server() + "");
      em.setSenderAddress(EngineConstants.ERR_MAIL_SENDER + "@" + this.version.getMail_server() + "");

      // Recipient
      Vector v = new Vector();
      if (errType.equals(EngineConstants.ERR_TYPE_DEFINITION)) {
        v.addElement("" + this.collection.getMail_bug_addr());
      } else if (errType.equals(EngineConstants.ERR_TYPE_EXECUTION)) {
        v.addElement("" + this.collection.getMail_bug_addr());
      } else if (errType.equals(EngineConstants.ERR_TYPE_VALIDATION)) {
        v.addElement("" + this.collection.getMail_fail_addr());
      } else if (errType.equals(EngineConstants.ERR_TYPE_SQL)) {
        v.addElement("" + this.collection.getMail_bug_addr());
      } else if (errType.equals(EngineConstants.ERR_TYPE_WARNING)) {
        v.addElement("" + this.collection.getMail_error_addr());
      } else if (errType.equals(EngineConstants.ERR_TYPE_SYSTEM)) {
        v.addElement("" + this.collection.getMail_bug_addr());
      }

      em.setRecipients(v);

      String errStr = "COLLECTION:       " + this.collection.getCollection_name() + "\n";
      errStr += "TRANSFER ACTION:  " + this.transferActionName + "\n";
      errStr += "ERROR MESSAGE:   " + "\n";
      errStr += errorText;
      em.setSubject(EngineConstants.ERR_MAIL_SUBJECT + errType);
      em.setMessage(errStr);

      if (em.sendMail() == false) {
        throw new EngineMetaDataException(EngineConstants.CANNOT_SEND_MAIL, new String[] {
            this.version.getMail_server(), this.version.getMail_server_port().toString() }, null, this.getClass()
            .getName());
      }

    }
  }

  /**
   * Writes error information into the database
   * 
   * @param errorText
   *          text to write into db.
   * @param errType
   *          the type of the error.
   */
  public void writeError(String errorText, String errType) throws EngineMetaDataException {
    writeErrorProt(errorText, null, errType);
  }

  /**
   * Writes error information into the database
   * 
   * @param errorText
   *          text to write into db.
   * @param methodName
   *          method that caused the error.
   * @param errType
   *          the type of the error.
   */
  public void writeError(String errorText, String methodName, String errType) throws EngineMetaDataException {
    writeErrorProt(errorText, methodName, errType);
  }

  public String getVersionNumber() {
    return this.version.getVersion_number();
  }

  public Long getCollectionSetId() {
    return this.collectionSetId;
  }

  public Long getCollectionId() {
    return this.collection.getCollection_id();
  }

  public Long getTransferActionId() {
    return this.transferActionId;
  }

  public String getTransferActionName() {
    return this.transferActionName;
  }

  public Long getTransferBatchId() {
    return this.transferBatchId;
  }

  public Long getConnectionId() {
    return this.connectionId;
  }

  public RockFactory getRockFact() {
    return this.rockFact;
  }

  public Meta_transfer_actions getTrActions() {
    return this.trActions;
  }
  
  /**
   * Creates a status event, and sends it to this action's listener.
   */
  protected final void sendEventToListener(String message) {
    String dispatcher = this.transferActionName;
    Date currentTime = new Date(System.currentTimeMillis());
    
    StatusEvent statusEvent = new StatusEvent(dispatcher, currentTime, message);
    this.setListener.addStatusEvent(statusEvent);
  }

}
