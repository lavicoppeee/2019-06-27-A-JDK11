package it.polito.tdp.crimes.model;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.crimes.db.EventsDao;

public class Model {
	
	private Graph<String, DefaultWeightedEdge> graph;
	private EventsDao dao;
	private List<String> bestPath;
	private Integer minPeso;
	
	
	public Model() {
		this.dao = new EventsDao();
	}
	
	public List<String> getOffenceCategory() {
		return this.dao.getAllOffenceCategory();
	}
	
	public List<Integer> getYears() {
		return this.dao.getAllYears();
	}
	
	public void buildGraph(String category, Integer year) {
		this.graph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		Graphs.addAllVertices(this.graph, this.dao.getOffenceType(category, year));
		for(Adiacenza a : this.dao.getAdiacenze(category, year)) {
			Graphs.addEdge(this.graph, a.getType1(), a.getType2(), a.getPeso());
		}
	}
	
	public List<Adiacenza> getArchiPesoMax() {
		Integer max = 0;
		for(DefaultWeightedEdge edge : this.graph.edgeSet()) {
			if(this.graph.getEdgeWeight(edge) > max) {
				max = (int) this.graph.getEdgeWeight(edge);
			}
		}
		
		List<Adiacenza> result = new ArrayList<Adiacenza>();
		for(DefaultWeightedEdge edge : this.graph.edgeSet()) {
			if(this.graph.getEdgeWeight(edge) == max) {
				result.add(new Adiacenza(this.graph.getEdgeSource(edge), this.graph.getEdgeTarget(edge), max));
			}
		}	
		return result;
	}
	
	
	public List<String> trovaPercorsoMin(Adiacenza a) {
		this.bestPath = new ArrayList<String>();
		this.minPeso = 0;
		
		List<String> parziale = new ArrayList<String>();
		parziale.add(a.getType1());
		this.ricorsiva(parziale, a.getType2(), 0);
		
		return this.bestPath;
	}

	private void ricorsiva(List<String> parziale, String target, Integer peso) {
		String last = parziale.get(parziale.size()-1);
		
		if(parziale.size() == this.graph.vertexSet().size() && last.equals(target)) {
			if(this.bestPath.size() == 0 || peso < this.minPeso) {
				this.bestPath = new ArrayList<String>(parziale);
				this.minPeso = peso;
			}
			return;
		}
		
		for(String vicino : Graphs.neighborListOf(this.graph, last)) {
			if(!parziale.contains(vicino)) {
				parziale.add(vicino);
				Integer p = (int) this.graph.getEdgeWeight(this.graph.getEdge(last, vicino));
				peso += p;
				this.ricorsiva(parziale, target, peso);
				
				peso -= p;
				parziale.remove(parziale.size()-1);
			}
		}
		
	}
	
}
