package com.distocraft.dc5000.repository.cache;

public class AggregationRule {

		String aggregation = "";

		String target_type = "";

		String target_level = "";

		String aggregationscope = "";

	   String source_type = "";
	   
	   String source_level = "";

		
	   AggregationRule(String aggregation, String target_type,
				String target_level,String source_type, String source_level, String aggregationscope) {

			this.aggregation = aggregation;
			this.target_type = target_type;
			this.target_level = target_level;
			this.source_type = source_type;
			this.source_level = source_level;
			this.aggregationscope = aggregationscope;

		}


		public String getAggregation() {
			return aggregation;
		}


		public void setAggregation(String aggregation) {
			this.aggregation = aggregation;
		}


		public String getAggregationscope() {
			return aggregationscope;
		}


		public void setAggregationscope(String aggregationscope) {
			this.aggregationscope = aggregationscope;
		}


		public String getSource_level() {
			return source_level;
		}


		public void setSource_level(String source_level) {
			this.source_level = source_level;
		}


		public String getSource_type() {
			return source_type;
		}


		public void setSource_type(String source_type) {
			this.source_type = source_type;
		}


		public String getTarget_level() {
			return target_level;
		}


		public void setTarget_level(String target_level) {
			this.target_level = target_level;
		}


		public String getTarget_type() {
			return target_type;
		}


		public void setTarget_type(String target_type) {
			this.target_type = target_type;
		}

	}

