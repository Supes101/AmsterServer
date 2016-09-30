package com.amster.servlet.utils;

import org.apache.commons.fileupload.ProgressListener;

public class UploadProgressListener implements ProgressListener {


	private long num100Ks = 0;
	

	private long theBytesRead = 0;
	private long theContentLength = -1;
	private int whichItem = 0;
	private int percentDone = 0;
	private boolean contentLengthKnown = false;
	
	private long theBytesParsed =0;
	private long parse100ks=0;
	private int parsePercentDone;

	
	public void update(long bytesRead, long contentLength, int items) {

		
		if (contentLength > -1) {
			contentLengthKnown = true;
		}
		theBytesRead = bytesRead;
		theContentLength = contentLength;
		whichItem = items;

		long nowNum100Ks = bytesRead / 100000;
		// Only run this code once every 100K
		if (nowNum100Ks > num100Ks) {
			num100Ks = nowNum100Ks;
			if (contentLengthKnown) {
				percentDone = (int) Math.round(100.00 * bytesRead / contentLength);
			}

		}
	}
	
	public void updateParse(long bytes_in) {

		theBytesParsed= theBytesParsed+bytes_in;


		long nowNum100Ks = theBytesParsed / 100000;
		// Only run this code once every 100K
		if (nowNum100Ks > parse100ks) {
			parse100ks = nowNum100Ks;
			if (contentLengthKnown) {
				parsePercentDone = (int) Math.round(100.00 * theBytesParsed / theContentLength);
			}
			//System.out.println(getJSON());
		}
	}

	public void clear(){

		theBytesRead = 0;
		theContentLength = -1;
		whichItem = 0;
		percentDone = 0;
		contentLengthKnown = false;
		num100Ks=0;
		theBytesParsed =0;
		parse100ks=0;
		parsePercentDone=0;
	}
	
	public String getMessage() {
		if (theContentLength == -1) {
			return "" + theBytesRead + " of Unknown-Total bytes have been uploaded." + theBytesParsed + " have been processed";
		} else {
			return "" + theBytesRead + " of " + theContentLength + " bytes have been uploaded (" + percentDone + "% done). " + theBytesParsed + " of " + theContentLength + " bytes have been processed (" + parsePercentDone + "% done)." ;
		}

	}
	
	public String getJSON() {
		
		String message = "";
		
		if (theContentLength == -1) {
			message= "" + theBytesRead + " of Unknown-Total bytes have been uploaded." + theBytesParsed + " have been processed";
		} else {
			message= "" + theBytesRead + " of " + theContentLength + " bytes have been uploaded (" + percentDone + "% done). " + theBytesParsed + " of " + theContentLength + " bytes have been processed (" + parsePercentDone + "% done)." ;
		}
		
		return "{ \"total_bytes\" : \""+theContentLength+"\", \"bytes_uploaded\" : \""+theBytesRead+"\", \"bytes_parsed\" : \""+theBytesParsed+"\", \"upload_percent\" : \""+percentDone+ "\", \"parse_percent\" : \""+parsePercentDone+"\", \"message\" : \""+message+"\"}";

	}

	public long getNum100Ks() {
		return num100Ks;
	}

	public void setNum100Ks(long num100Ks) {
		this.num100Ks = num100Ks;
	}

	public long getTheBytesRead() {
		return theBytesRead;
	}

	public void setTheBytesRead(long theBytesRead) {
		this.theBytesRead = theBytesRead;
	}

	public long getTheContentLength() {
		return theContentLength;
	}

	public void setTheContentLength(long theContentLength) {
		this.theContentLength = theContentLength;
	}

	public int getWhichItem() {
		return whichItem;
	}

	public void setWhichItem(int whichItem) {
		this.whichItem = whichItem;
	}

	public int getPercentDone() {
		return percentDone;
	}

	public void setPercentDone(int percentDone) {
		this.percentDone = percentDone;
	}

	public boolean isContentLengthKnown() {
		return contentLengthKnown;
	}

	public void setContentLengthKnown(boolean contentLengthKnown) {
		this.contentLengthKnown = contentLengthKnown;
	}
	
}
