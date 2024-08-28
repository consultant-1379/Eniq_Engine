/*
 * Created on 25.1.2005
 * TODO: better exceptions
 */
package com.distocraft.dc5000.etl.engine.priorityqueue;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

import com.distocraft.dc5000.etl.engine.common.ExceptionHandler;
import com.distocraft.dc5000.etl.engine.executionslots.ExecutionSlot;
import com.distocraft.dc5000.etl.engine.executionslots.ExecutionSlotProfile;
import com.distocraft.dc5000.etl.engine.executionslots.ExecutionSlotProfileHandler;
import com.distocraft.dc5000.etl.engine.main.EngineThread;

/**
 * @author savinen
 * 
 */
public class PriorityQueueThread extends Thread {

  private PriorityQueue pQueue;

  private ExecutionSlotProfileHandler profileHolder;

  private final Logger log;

  /* Constructor */
  public PriorityQueueThread(PriorityQueue pq, ExecutionSlotProfileHandler esph) {
    super("PriorityQueue");

    this.pQueue = pq;
    this.profileHolder = esph;

    log = Logger.getLogger("etlengine.PriorityQueueThread");

  }

  /**
   * 
   * 
   * 
   * 
   */
  public void run() {

    ExecutionSlot exeSlot;

    /* loop untill we drop */
    while (true) {
      try {

        /* loop while priority queue is active (not on hold) */
        if (pQueue.isActive()) {

          /* Sleep between pols */
          sleep(pQueue.getPollIntervall());

          profileHolder.cleanActiveProfile();

          /* Retrieve all free execution slots */
          Iterator freeExecSlotIterator = profileHolder.getActiveExecutionProfile().getAllFreeExecutionSlots();

          /* Retrieve the number of execution slots */
          int ExecSlotNumber = profileHolder.getActiveExecutionProfile().getNumberOfExecutionSlots();

          /* Retrieve the number of sets in priority queue */
          int priorityQueueNumber = pQueue.getNumberOfAvailable();

          /* Retrieve the number of free execution slots */
          int freeExecSlotNumber = profileHolder.getActiveExecutionProfile().getNumberOfFreeExecutionSlots();

          log.finest("Running. Slots: " + freeExecSlotNumber + "/" + ExecSlotNumber + " PriorityQueue: "
              + priorityQueueNumber);

          /*
           * checks priority queue for sets that have been hanging around too
           * long
           */
          pQueue.checkTimeLimit();

          /*
           * if there is no free execution slots or no sets in the priority
           * queue no need to loop anything
           */
          if ((freeExecSlotNumber != 0) && (priorityQueueNumber != 0)) {

            /* Loop all free execution slots */
            while (freeExecSlotIterator.hasNext()) {
              /* free execution slot */
              exeSlot = (ExecutionSlot) freeExecSlotIterator.next();
              log.finest("Free executionSlot received.");

              try {

                /* Sort priority queue */
                pQueue.sort();

                /* Retrieve available sets from priority queue */
                Enumeration priorityQueueIterator = pQueue.getAvailable();

                /* loop all sets in priority queue */
                do {

                  /* get set from priority queue */
                  EngineThread set = (EngineThread) priorityQueueIterator.nextElement();

                  /*
                   * Check that the set is not executed in the slots and that
                   * sets table is not in one of the execution slots.
                   * Check also that has the maximum number of concurrent workers/sets exceeded.
                   */
                  final ExecutionSlotProfile activeExecutionProfile = profileHolder.getActiveExecutionProfile();
                  if (activeExecutionProfile.notInExecution(set)
                      && !activeExecutionProfile.checkTable(set)
                      && !activeExecutionProfile.hasMaxMemoryUsageExceeded(set)) {
                    /* check if slot acceptes this set */
                    if (exeSlot.isAccepted(set)) {

                      /*
                       * Rock engine does not keep up if we do not wait between
                       * executing sets
                       */
                      sleep(500);

                      /* execute set in this execution slot */
                      set.addSlotId(exeSlot.getSlotId());
                      exeSlot.execute(set);
                      
                      /* remove set from priority queue */
                      pQueue.RemoveSet(set);

                      break;
                    } else {
                      log.finest("Set " + set.getSetName() + " is not accepted.");
                    }
                  } else {
                    log.finest("ActiveExecutionProfile checks failed.");
                  }
                } while (priorityQueueIterator.hasMoreElements());

              } catch (NoSuchElementException e) {
                /*
                 * No sets in queue (queue is empty), this is OK so nothing is
                 * done
                 */
                log.finest("PriorityQueue was empty.");

              } catch (NullPointerException e) {

                /* queue is null, this is OK so nothing is done */

                log.finest("PriorityQueue was null. Nothing is done.");
              }

            }
          }
        } else {
          /* if Priority queue is on hold wait the normal polltime */

          /* DEBUG */

          /* Retrieve the number of execution slots */
          int ExecSlotNumber = profileHolder.getActiveExecutionProfile().getNumberOfExecutionSlots();

          /* Retrieve the number of sets in priority queue */
          int prorityQueueNumber = pQueue.getNumberOfAvailable();

          /* Retrieve the number of free execution slots */
          int freeExecSlotNumber = profileHolder.getActiveExecutionProfile().getNumberOfFreeExecutionSlots();

          log.finest("PRIORITY QUEUE ON HOLD: Slots: " + freeExecSlotNumber + "/" + ExecSlotNumber
              + ") PriorityQueue: " + prorityQueueNumber);

          sleep(pQueue.getPollIntervall());
        }

      } catch (InterruptedException ie) {
        ExceptionHandler.handleException(ie, "Error in Priority Queue");
      } catch (Exception e) {
        ExceptionHandler.handleException(e, "Error in Priority Queue");
      }

    }

  }

}
