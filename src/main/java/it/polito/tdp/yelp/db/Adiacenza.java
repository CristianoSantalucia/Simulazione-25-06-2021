package it.polito.tdp.yelp.db;

public class Adiacenza
{
	private String bId1;
	private String bId2;
	private Double diff;
	
	public String getbId1()
	{
		return bId1;
	}
	public String getbId2()
	{
		return bId2;
	}
	public Double getDiff()
	{
		return diff;
	}
	public Adiacenza(String bId1, String bId2, Double diff)
	{
		this.bId1 = bId1;
		this.bId2 = bId2;
		this.diff = diff;
	}
	@Override public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bId1 == null) ? 0 : bId1.hashCode());
		result = prime * result + ((bId2 == null) ? 0 : bId2.hashCode());
		return result;
	}
	@Override public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Adiacenza other = (Adiacenza) obj;
		if (bId1 == null)
		{
			if (other.bId1 != null) return false;
		}
		else if (!bId1.equals(other.bId1)) return false;
		if (bId2 == null)
		{
			if (other.bId2 != null) return false;
		}
		else if (!bId2.equals(other.bId2)) return false;
		return true;
	}
}
