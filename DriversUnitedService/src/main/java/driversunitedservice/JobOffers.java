package driversunitedservice;

public class JobOffers {
    static class SalaryRange {
    	
        public SalaryRange(double min, double max) {

        }
    }
    static JobOffer[] getJobOffers() {
    	SalaryRange salaryRange1 = new SalaryRange(30000, 50000);
        SalaryRange salaryRange2 = new SalaryRange(40000, 60000);
        SalaryRange salaryRange3 = new SalaryRange(35000, 55000);
        SalaryRange salaryRange4 = new SalaryRange(45000, 70000);
        SalaryRange salaryRange5 = new SalaryRange(50000, 80000);
        
        JobOffer[] jobOffers = {
                new JobOffer("1", "Delivery Driver", "Fast Feast Express", salaryRange1, "5 Shires Ln, Leicester LE1 4AN", "University Rd, Leicester LE1 7RH", "1.6 miles","e-bike"),
                new JobOffer("2", "Delivery Specialist", "Hungry Hustlers Crew", salaryRange2, "115 Bath Ln, Leicester LE3 5EU", "Blaby golf centre, Lutterworth Rd, Blaby, Leicester LE8 4DP", "6.5 miles","car"),
                new JobOffer("3", "Delivery Associate", "Speedy Eats Squad", salaryRange3, "1 Kildare St, Leicester LE1 3YH", "Exploration Dr, Leicester LE4 5NS", "2.1 miles","motorbike"),
                new JobOffer("4", "Food Courier", "Food Express Squad", salaryRange4, "7 Peacock Ln, Leicester LE1 5PZ", "Leicester Rd, Oadby, Leicester LE2 4AL", "3.7 miles","bicycle"),
                new JobOffer("5", "Delivery Operations Manager", "Taste Transporters", salaryRange5, "Market Pl, Leicester LE1 5GG", "45 High St, Lutterworth LE17 4AY", "16.4 miles","e-bike")
        };

    	
		return jobOffers;
    	
    }
    

    static class JobOffer {
        private String id;
        public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public SalaryRange getSalaryRange() {
			return salaryRange;
		}

		public void setSalaryRange(SalaryRange salaryRange) {
			this.salaryRange = salaryRange;
		}

		public String getStartLocation() {
			return startLocation;
		}

		public void setStartLocation(String startLocation) {
			this.startLocation = startLocation;
		}

		public String getEndLocation() {
			return endLocation;
		}

		public void setEndLocation(String endLocation) {
			this.endLocation = endLocation;
		}

		public String getDistance() {
			return distance;
		}

		public void setDistance(String distance) {
			this.distance = distance;
		}

		private String title;
        private String description;
        private SalaryRange salaryRange;
        private String startLocation;
        private String endLocation;
        private String transport;
        private String distance;

        public JobOffer(String id, String title, String description, SalaryRange salaryRange, String startLocation, String endLocation, String distance,String transport) {
            this.setId(id);
            this.title = title;
            this.description = description;
            this.salaryRange = salaryRange;
            this.startLocation = startLocation;
            this.endLocation = endLocation;
            this.distance = distance;
            this.transport = transport;
        }

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getTransport() {
			return transport;
		}

		public void setTransport(String transport) {
			this.transport = transport;
		}
    }

    public static void main(String[] args) {
        // Creating instances of SalaryRange
        SalaryRange salaryRange1 = new SalaryRange(30000, 50000);
        SalaryRange salaryRange2 = new SalaryRange(40000, 60000);
        SalaryRange salaryRange3 = new SalaryRange(35000, 55000);
        SalaryRange salaryRange4 = new SalaryRange(45000, 70000);
        SalaryRange salaryRange5 = new SalaryRange(50000, 80000);

        // Creating instances of JobOffer
        JobOffer[] jobOffers = {
        		new JobOffer("1", "Delivery Driver", "Fast Feast Express", salaryRange1, "5 Shires Ln, Leicester LE1 4AN", "University Rd, Leicester LE1 7RH", "1.6 miles","e-bike"),
                new JobOffer("2", "Delivery Specialist", "Hungry Hustlers Crew", salaryRange2, "115 Bath Ln, Leicester LE3 5EU", "Blaby golf centre, Lutterworth Rd, Blaby, Leicester LE8 4DP", "6.5 miles","car"),
                new JobOffer("3", "Delivery Associate", "Speedy Eats Squad", salaryRange3, "1 Kildare St, Leicester LE1 3YH", "Exploration Dr, Leicester LE4 5NS", "2.1 miles","motorbike"),
                new JobOffer("4", "Food Courier", "Food Express Squad", salaryRange4, "7 Peacock Ln, Leicester LE1 5PZ", "Leicester Rd, Oadby, Leicester LE2 4AL", "3.7 miles","bicycle"),
                new JobOffer("5", "Delivery Operations Manager", "Taste Transporters", salaryRange5, "Market Pl, Leicester LE1 5GG", "45 High St, Lutterworth LE17 4AY", "16.4 miles","e-bike")
        };

        // Printing job offers
        for(int i = 0; i< jobOffers.length;i++) {
        	if(jobOffers[i].id.equals("1")) {
        		System.out.println(i);
        		System.out.println(jobOffers[i].title);
        	}
        	
        	
        	
        	
        }
    }
}