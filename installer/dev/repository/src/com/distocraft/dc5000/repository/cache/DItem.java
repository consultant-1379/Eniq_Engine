package com.distocraft.dc5000.repository.cache;

public class DItem implements Comparable {

	private String dataName;
	private int colNumber;
	private String dataID;
	private String pi;
	private String dataType;
	private int dataSize;
	private int dataScale;
	private int isCounter = 0;

	public DItem(String dataName, int colNumber, String dataID, String pi,
			String datatype, int datasize, int datascale) {
		super();
		this.dataName = dataName;
		this.colNumber = colNumber;
		this.dataID = dataID;
		this.pi = pi;
		this.dataType = datatype;
		this.dataSize = datasize;
		this.dataScale = datascale;
	}
	
	public DItem(String dataName, int colNumber, String dataID, String pi,
			String datatype, int datasize, int datascale, int isCounter) {
		super();
		this.dataName = dataName;
		this.colNumber = colNumber;
		this.dataID = dataID;
		this.pi = pi;
		this.dataType = datatype;
		this.dataSize = datasize;
		this.dataScale = datascale;
		this.isCounter = isCounter;
	}

	public DItem(String dname, int colno, String did, String pinst) {
		dataName = dname;
		colNumber = colno;
		dataID = did;
		pi = pinst;
	}

	public int getColNumber() {
		return colNumber;
	}

	public String getDataID() {
		return dataID;
	}

	public String getDataName() {
		return dataName;
	}

	public String getProcessInstruction() {
		return pi;
	}

	public String getDataType() {
		return dataType;
	}

	public int getDataSize() {
		return dataSize;
	}

	public int getDataScale() {
		return dataScale;
	}
	
	public int getIsCounter() {
		return isCounter;
	}

	public int compareTo(Object o) {
		if (o instanceof DItem) {
			DItem k = (DItem) o;

			if (colNumber < k.getColNumber())
				return -1;
			else if (colNumber > k.getColNumber())
				return 1;
		}

		return 0;

	}

}
