package com.distocraft.dc5000.etl.engine.connect;

import java.util.Vector;
import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.engine.common.EngineMetaDataException;
import com.distocraft.dc5000.etl.engine.structure.TransferActionBase;

/**
 * This class stores all of the db connections used by one transfer collection
 * if a connection is already created no new one is created, instead the address
 * is returned
 * 
 */
public class ConnectionPool {

	// Created connections.
	final private Vector vecConnections;

	// The database connection for the metadata
	final private RockFactory rockFact;

	private static Logger log = Logger
			.getLogger("etlengine.engine.ConnectionPool");

	public ConnectionPool(RockFactory rockFact) {
		this.rockFact = rockFact;
		this.vecConnections = new Vector();
	}

	public RockFactory getConnect(final TransferActionBase trActionBase,
			final String versionNumber, final Long connectId)
			throws EngineMetaDataException {

		for (int i = 0; i < this.vecConnections.size(); i++) {

			final DbConnection conn = (DbConnection) this.vecConnections.elementAt(i);
			if (conn.getConnectId().equals(connectId)) {
				log.finest(" retrieved connection from pool " + connectId);
				return conn.getRockFactory();
			}
		}

		final DbConnection conn = new DbConnection(trActionBase, this.rockFact,
				versionNumber, connectId);
		this.vecConnections.addElement(conn);
		log.finest(" Connection created " + connectId);

		return conn.getRockFactory();
	}

	public int cleanPool() {

		int count = 0;

		for (int i = 0; i < this.vecConnections.size(); i++) {

			try {

				final DbConnection conn = (DbConnection) this.vecConnections
						.elementAt(i);

				if (conn != null && conn.getRockFactory() != null) {
					if (!conn.getRockFactory().getConnection().isClosed()) {
						conn.getRockFactory().getConnection().close();
						log.finest(" Connection closed " + conn.getConnectId());
						count++;
					}
				}

			} catch (Exception e) {
				log
						.warning("error while closing connection in connection pool.");
			}

		}

		log.finest(" Connections cleared: " + count);
		this.vecConnections.clear();

		return count;

	}

	public int count() {

		int count = 0;

		for (int i = 0; i < this.vecConnections.size(); i++) {

			try {

				final DbConnection conn = (DbConnection) this.vecConnections
						.elementAt(i);
				if (conn != null) {
					count++;
				}

			} catch (Exception e) {
				log
						.warning("error while counting connection in connection pool.");

			}

		}

		return count;

	}

}