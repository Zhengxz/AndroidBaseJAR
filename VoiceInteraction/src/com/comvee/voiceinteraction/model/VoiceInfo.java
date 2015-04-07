package com.comvee.voiceinteraction.model;

import java.io.Serializable;

public class VoiceInfo implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6052067108175536641L;
	
	private String attUrl;
	private String isPrivate;
	private String voiceLength;
	private boolean isPlaying;// �Ƿ����ڲ��š����ڿ���PlayImageView״̬

	public String getAttUrl()
	{
		return attUrl;
	}

	public void setAttUrl(String attUrl)
	{
		this.attUrl = attUrl;
	}

	public String getIsPrivate()
	{
		return isPrivate;
	}

	public void setIsPrivate(String isPrivate)
	{
		this.isPrivate = isPrivate;
	}

	public String getVoiceLength()
	{
		return voiceLength;
	}

	public void setVoiceLength(String voiceLength)
	{
		this.voiceLength = voiceLength;
	}

	public boolean isPlaying()
	{
		return isPlaying;
	}

	public void setPlaying(boolean isPlaying)
	{
		this.isPlaying = isPlaying;
	}

}