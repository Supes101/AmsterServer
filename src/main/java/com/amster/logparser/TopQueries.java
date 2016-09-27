package com.amster.logparser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class TopQueries implements Comparator<ClfyQuery> {

	private int m_MaxListSize=20;

	private List<ClfyQuery> m_QueryList = new ArrayList<ClfyQuery>();
	
	
	public TopQueries(int list_size){
		m_MaxListSize=list_size;
	}
	
	public void AddQuery(String query, float elapsed_time, Date currentTime){
		
	
		if(m_QueryList.size() <m_MaxListSize){
			m_QueryList.add(new ClfyQuery(elapsed_time,query, currentTime));
			SortList();
		}else{
			//Get the last item in the list
			ClfyQuery cq = m_QueryList.get(m_QueryList.size()-1);
			//Is this query eligible for our list?
			if(cq.getElapsedTime()<elapsed_time){
				m_QueryList.add(new ClfyQuery(elapsed_time,query,currentTime));
				SortList();
				//Trim the list
				if(m_QueryList.size()>m_MaxListSize){
					m_QueryList.remove(m_MaxListSize);
				}
			}
		}
	}
	
	public List<ClfyQuery> getList(){
		return m_QueryList;
	}
	
	
	public void SortList(){
		Collections.sort(m_QueryList, this); 
	}
	
	public int compare(ClfyQuery q1, ClfyQuery q2) {
		
		return Float.compare(q2.getElapsedTime(), q1.getElapsedTime());

	}
	
	public void showContents(){
		for (ClfyQuery cq : m_QueryList) {
			System.out.println("Elasped time: "+cq.getElapsedTime());
		}
		
	}
}
