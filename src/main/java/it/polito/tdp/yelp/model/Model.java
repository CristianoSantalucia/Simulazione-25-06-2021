package it.polito.tdp.yelp.model;

import java.util.*;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.yelp.db.Adiacenza;
import it.polito.tdp.yelp.db.YelpDao;

public class Model
{
	private YelpDao dao; 
	private Map<String, Business> vertici; 
	private Graph<Business, DefaultWeightedEdge> grafo; 
	
	public Model()
	{
		this.dao = new YelpDao();
	}
	public Collection<String> getAllCities()
	{
		return this.dao.getAllCities();
	} 
	
	public void creaGrafo(String city, Integer year)
	{
		// ripulisco mappa e grafo
		this.vertici = new HashMap<>(); 
		this.grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class); // 
		
		/// vertici 
		this.dao.getVertici(vertici, city, year); //riempio la mappa
		Graphs.addAllVertices(this.grafo, this.vertici.values()); 
		
		/// archi
		List<Adiacenza> adiacenze = new ArrayList<>(this.dao.getAdiacenze(city, year));
		for (Adiacenza a : adiacenze)
		{
			//recupero gli Oggetti dalla chiave della mappa e faccio controlli
			Business b1 = this.vertici.get(a.getbId1());
			Business b2 = this.vertici.get(a.getbId2());
			if (b1 != null && b2 != null)
			{
				double diff = a.getDiff(); 
				if(diff > 0)
					Graphs.addEdge(this.grafo, b2, b1, Math.abs(diff));
				else 
					Graphs.addEdge(this.grafo, b1, b2, Math.abs(diff));
			}
		}
	}
	public int getNumVertici()
	{
		return this.grafo.vertexSet().size();
	}
	public int getNumArchi()
	{
		return this.grafo.edgeSet().size();
	}
	public Collection<Business> getVertici()
	{
		List<Business> list = new ArrayList<>(this.grafo.vertexSet()); 
		list.sort((b1,b2)->b1.getBusinessName().compareTo(b2.getBusinessName()));
		return list;
	}
	public Collection<DefaultWeightedEdge> getArchi()
	{
		return this.grafo.edgeSet();
	}
	
	Business migliorLocale = null;
	public String cercaBest()
	{
		double bestPeso = 0; 
		for (Business b : this.grafo.vertexSet())
		{
			double entranti = 0; 
			double uscenti = 0; 
			double valore; 
			for(DefaultWeightedEdge e : this.grafo.incomingEdgesOf(b))
				entranti += this.grafo.getEdgeWeight(e); 
			for(DefaultWeightedEdge e : this.grafo.outgoingEdgesOf(b))
				uscenti += this.grafo.getEdgeWeight(e); 
			valore = entranti - uscenti; 
			if(valore > bestPeso)
			{
				bestPeso = valore; 
				migliorLocale = b; 
			}
		}
		return String.format("LOCALE MIGLIORE : %s (%.2f)", migliorLocale.toString(),bestPeso);
	}
	
	//RICORSIONE 
	
	List<Business> percorso;
	Double X; //soglia minima peso arco
	public List<Business> trovaPercorso(Business partenza, Double X)
	{
		this.cercaBest(); //per assegnare valore al best
		
		percorso = new ArrayList<>(); //dimensione massima
		
		List<Business> parziale = new ArrayList<>(); 
		parziale.add(partenza); 
		
		this.X = X;
		
		this.cerca(parziale);
		 
		return percorso; 
	}
	
	public void cerca(List<Business> parziale)
	{
		//terminale
		if(parziale.get(parziale.size()-1).equals(migliorLocale))
		{
			if(parziale.size() > percorso.size())
				percorso = new ArrayList<>(parziale); 
			return; 
		}
		//chiamata 
		for(Business b : Graphs.neighborListOf(this.grafo, parziale.get(parziale.size()-1)))
		{
			DefaultWeightedEdge e = this.grafo.getEdge(parziale.get(parziale.size()-1), b); 
			if(e != null && this.grafo.getEdgeWeight(e)>=this.X)
			{
				parziale.add(b); 
				this.cerca(parziale);
				parziale.remove(b); 
			}
		}
	} 
}
