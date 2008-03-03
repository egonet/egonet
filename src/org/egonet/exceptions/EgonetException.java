package org.egonet.exceptions;

public class EgonetException extends Exception
{
	public EgonetException()
	{
		super();
	}
	
	public EgonetException(String s)
	{
		super(s);
	}
	
	public EgonetException(Throwable t)
	{
		super(t);
	}
	
	public EgonetException(String s, Throwable t)
	{
		super(s,t);
	}
}
