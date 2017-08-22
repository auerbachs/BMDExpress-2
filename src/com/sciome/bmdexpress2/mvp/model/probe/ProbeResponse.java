package com.sciome.bmdexpress2.mvp.model.probe;

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisRow;

@JsonTypeInfo(use = Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@ref")
public class ProbeResponse extends BMDExpressAnalysisRow implements Serializable
{

	/**
	 * 
	 */
	private static final long			serialVersionUID	= -8622735939960087903L;

	private Probe						probe;

	// this is what gets filled with data after deserialization
	// serializing large lists of responses takes too long which is why
	// I opted to seralize a byte[] blob instead
	private transient List<Float>		responses;

	// This is what gets persisted because serializing a big list of floats ain't pretty.
	private byte[]						responsesBlob;

	// This array is used for efficiency purposes. Each time a method
	// needs an array of floats, rather than converting a list to array per call
	// I'm opting to have the array ready to go up front. uses a more memory, but faster.
	private transient float[]			responseArray;

	// row data for the table view.
	@JsonIgnore
	protected transient List<Object>	row;

	private Long						id;

	public Probe getProbe()
	{
		return probe;
	}

	@JsonIgnore
	public Long getID()
	{
		return id;
	}

	public void setID(Long id)
	{
		this.id = id;
	}

	public void setProbe(Probe probe)
	{
		this.probe = probe;
	}

	public List<Float> getResponses()
	{
		return responses;
	}

	public void setResponses(List<Float> responses)
	{
		this.responses = responses;
		// let's store an array so that it is quicker to get this data when needed by bmds models.
		responseArray = new float[responses.size()];

		// while we are at it put these into the byte array for serialization
		ByteBuffer byteBuffer = ByteBuffer.allocate(Float.BYTES * responses.size());

		for (int i = 0; i < responses.size(); i++)
		{
			responseArray[i] = responses.get(i);
			byteBuffer.putFloat(i * Float.BYTES, responses.get(i));
		}
		responsesBlob = byteBuffer.array();
	}

	@JsonIgnore
	public byte[] getResponsesBlob()
	{
		return responsesBlob;
	}

	public void setResponsesBlob(byte[] responsesBlob)
	{
		this.responsesBlob = responsesBlob;
	}

	@JsonIgnore
	public float[] getResponseArray()
	{
		return responseArray;
	}

	/*
	 * deserializing this object requires translating the byte array into a list of doses.
	 */
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		// read the object so that the responsesBlob gets filled up
		in.defaultReadObject();

		// now let's populate the responseArray and responseBuffer with the data.
		responses = new ArrayList<>();
		ByteBuffer byteBuffer = ByteBuffer.wrap(responsesBlob);
		responseArray = new float[responsesBlob.length / 4];
		for (int i = 0; i < responsesBlob.length; i += Float.BYTES)
		{
			Float response = byteBuffer.getFloat(i);
			responses.add(response);
			responseArray[i / 4] = response;
		}

	}

	@Override
	@JsonIgnore
	public List<Object> getRow()
	{
		if (row == null)
		{
			createRowData();
		}
		return row;
	}

	private void createRowData()
	{
		if (row != null)
		{
			return;
		}

		row = new ArrayList<>();

		row.add(getProbe().getId());

		for (Float response : responses)
		{
			row.add(response);
		}

	}

	@Override
	public String toString()
	{
		return this.probe.toString();
	}

}
