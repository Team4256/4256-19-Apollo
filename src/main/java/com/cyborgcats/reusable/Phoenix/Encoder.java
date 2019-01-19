package com.cyborgcats.reusable.Phoenix;//COMPLETE 2018

import com.ctre.phoenix.motorcontrol.FeedbackDevice;

public enum Encoder {
	CTRE_MAG_ABSOLUTE(FeedbackDevice.CTRE_MagEncoder_Absolute, 4096),
	CTRE_MAG_RELATIVE(FeedbackDevice.CTRE_MagEncoder_Relative, 4096),
	RS7_QUAD(FeedbackDevice.QuadEncoder, 12),
	OEM_QUAD(FeedbackDevice.QuadEncoder, 1440);
	
	private final FeedbackDevice feedbackDevice;
	private final int countsPerRev;
	
	Encoder(final FeedbackDevice feedbackDevice, final int countsPerRev) {
		this.feedbackDevice = feedbackDevice;
		this.countsPerRev = countsPerRev;
	}
	
	public FeedbackDevice type() {return feedbackDevice;}
	
	public int countsPerRev() {return countsPerRev;}
}
