package com.blogspot.jvalentino.usnrauto.reportmerger.data.manual

class History {

	List<Date> dates = new ArrayList<Date>()
	
	private Map<DataCategory, List<HistoryElement>> list =
		new HashMap<DataCategory, List<HistoryElement>>()
		
	public History() {
		for (DataCategory category : DataCategory.values()) {
			list.put(category, new ArrayList<HistoryElement>())
		}
	}
	
	List<HistoryElement> getResults(DataCategory category) {
		return list.get(category)
	}
		
}
