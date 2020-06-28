package it.polito.tdp.crimes.model;

import java.util.*;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.crimes.Arco;
import it.polito.tdp.crimes.db.EventsDao;

public class Model {
	
	EventsDao dao;
	Map<Long,Event> idMap;
	List<String> reati;
	private Graph<String,DefaultWeightedEdge> graph;
	private List<String> bestPath;
	private int minPeso;

	
	
	public Model() {
		dao=new EventsDao();
		idMap=new HashMap<>();
		dao.listAllEvents(idMap);
	}
	public List<Integer> getAnno(){
		return dao.getAnno();
	}
	
	public List<String> getCategory(){
		return dao.getCategory();
	}
	public void creaGrafo(String category, int anno) {
		this.graph=new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		this.idMap=new HashMap<>();
		
		reati=dao.getVertici(category,anno,idMap);
		Graphs.addAllVertices(graph, reati);
		
		for(Arco a: dao.getArchi(category,anno)) {
			Graphs.addEdgeWithVertices(graph, a.getR1(), a.getR2(),a.getPeso());
		}
	}
	
	public List<Arco> getArchiPesoMax(){
		List<Arco> result=new ArrayList<>();
		
		Double pMax=0.0;
		
		//cerco prima il massimo peso 
		for(DefaultWeightedEdge edge : this.graph.edgeSet()) {
			if(this.graph.getEdgeWeight(edge) > pMax) {
				pMax = this.graph.getEdgeWeight(edge);
			}
		}
		
		//se trovo un arco con lo stesso peso massimo lo aggiungo
		for(DefaultWeightedEdge edge : this.graph.edgeSet()) {
			if(this.graph.getEdgeWeight(edge) == pMax) {
				result.add(new Arco(this.graph.getEdgeSource(edge), this.graph.getEdgeTarget(edge), pMax));
			}
		}
		
		return result;
	}
	
	public List<String> trovaPercorsoMin(Arco a) {
		this.bestPath = new ArrayList<String>();
		this.minPeso = 0;
		
		List<String> parziale = new ArrayList<String>();
		parziale.add(a.getR1());
		this.ricorsiva(parziale, a.getR2(), 0);
		
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
	public Integer nVertici() {
		// TODO Auto-generated method stub
		return this.graph.vertexSet().size();
	}
	public Integer nArchi() {
		// TODO Auto-generated method stub
		return this.graph.edgeSet().size();
	}
	
	
	
	
}
