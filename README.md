# SERVICE ORIENTED ARCHITECTURES

To design a service and app, called DriversUnited, that helps delivery drivers working for platforms such as UberEats, Deliveroo, DoorDash, etc. to compare job offers across platforms and choose the most convenient or profitable offer. This has the potential to change the dynamics of the market, which is currently one-sided in favour of the platforms, by increasing the bargaining power of drivers so that platforms must compete for drivers to deliver their jobs.

## Service Modelling

We assume that a driver is signed up with at least one DeliveryService platform app and has an account with the DriversUnited app. Then, the app should support the following scenario:

1.	Driver login: The driver logs into their DeliveryService and DriversUnited apps.
2.	Show offers: The DeliveryService app displays available jobs.
3.	Enter offers: The driver enters DeliveryService job offers they find attractive into the DriversUnited app.
4.	Fetch job offers: The DriversUnited app, upon authentication with the backend DriversUnited service, fetches current job offers similar to the ones entered by the driver.
5.	Display offers: Job offers are displayed on the DriversUnited app, with basic details.
6.	Select offer: The driver using the DriversUnited app selects a job offer that looks appealing. The DriversUnited app submits the job data to its backend service as well as to a suitable route planner to return 
•	detailed payment information
•	a route and estimate of the of the distance and time required 
7.	Driver confirms job offer: The driver, making an informed decision, confirms the job offer with the DriversUnited app.
8.	Job acceptance and execution: Once the job is confirmed, the driver uses the delivery service app to formally accept the job and starts the delivery process.
9.	Feedback and reporting: After completing the job, the driver can provide feedback or report any discrepancies via the DriversUnited app.


Task is to capture these requirements and propose a design for the DriversUnited app and its backend service. Find an API for route planning to estimate the travel time to pick-up and drop-off locations and incorporate it in your model. You should create:
1.	a use case diagram;
2.	one or more sequence diagrams; 
3.	a component diagram;
4.	interfaces with detailed operation signatures; 
5.	a class diagram as conceptual data model;
6.	textual descriptions of the preconditions and effects of operations.

## Data Mapping to XML or JSON
This task continues the development of Task 1 on Service Modelling by considering the requirements for data exchange and their implementation using XML or JSON. The DriversUnited interface provides operations for the app to submit job offers and retrieve related offers and to estimate the time for a given offer, among others. Your task is to map the relevant parts of the data model to a DTD or a JSON schema, to specify the XML or JSON data required for the following operations.

•	authenticateAndRequestSimilarOffers(username: String, password: String, jobOffers: JobOffer[]): JobOffer[]
Preconditions: 
username and password must be correct; jobOffers must not be empty.
Postconditions: 
Returns a list of job offers similar to jobOffers if authentication is successful.

•	requestRouteEstimates(startLocation: Address, targetLocation: Address, transportMeans: String): RouteDetails
Preconditions: 
	startLocation and targetLocation must be valid addresses; 
	transportMeans must be one of “car”, “bicycle”, “e-bike”, or “motorbike”.
Postconditions: 
Returns RouteDetails including estimated time (other details optional).
![DataModel](task-images/data-model.png)

Design a data representation in XML and/or JSON to support the operations 1 and 2 above. You can either consider them separately, designing two independent solutions for 1 and 2, or propose a common solution to both. 

In the case of independent mappings, you must design two DTDs or JSON schemas describing XML or JSON representations of your data. For an integrated solution, only one DTD or schema is required. In each case you should follow and submit the following steps. 

1.	Create a reduced class diagram containing only the elements relevant to this task. 
2.	Create an XML/JSON-specific class diagram for the given requirements.
3.	Derive DTD/JSON schemas and state the chosen mapping style for the attributes. 
4.	Create simple instances of your DTD/JSON schemas to demonstrate/test how they support the operations 1 and 2 above.
5.	Validate your instances, and include screenshots of the valid codes from the web form or Eclipse console, using either
-	the online JSON https://www.jsonschemavalidator.net or XML validator to check your JSON objects against your schema 
-	the online XML validator https://www.truugo.com/xml_validator/ to check your XML documents against your DTD
-	if you want to see how to validate XML documents in Java, the Java XML validator •  archive with validation tool to be used 
•  companion document with instructions on how to use the tool
  
### Service Implementation and Testing

This assignment continues the development of Groupwork 1 and 2 by implementing the DriversUnited service, designing test cases and executing in Postman. You should consider the models and schemata from GW1 and GW2 as requirements; where your implementation deviates, please explain how and why this is necessary.

You can use your own GW1 and GW2 solutions, but below we describe the process to follow based on our model solutions, where the DriversUnited Service implements interface IDUAppDUService and consumes interface IDUServiceRoutePlanner:

interface IDUAppDUService 
    authenticateAndRequestSimilarOffers(username: String, password: String, 
jobOffers: JobOffer[]): JobOffer[]
	Preconditions: username and password must be correct; jobOffers must not be empty.
	Postconditions: Returns a list of job offers similar to jobOffers if authentication succceds.

    submitSelectedJob(offerId: String)
	Preconditions: offerId must correspond to a valid job offer.
	Postconditions: The job offer identified by offerId is marked as selected by the driver.

interface IDUServiceRoutePlanner
    requestRouteAndEstimates(startLocation: Address, targetLocation: Address, 
transportMeans: String): RouteDetails
	Preconditions: 
		startLocation and targetLocation must be valid addresses; 
		transportMeans must be one of “car”, “bicycle”, “e-bike”, or “motorbike”.
	Postconditions: Returns RouteDetails including estimated time (other details optional).

Please follow these steps: 

1.	Find a suitable route planning service and implement a Route Planner adapter that, using this service, implements the required interface IDUService-RoutePlanner describing a generic route planner. An OpenAPI Spec (OAS) for the generic interface can be found here. You can follow the same approach to create a similar OAS from your own interface and validate it in the Swagger editor.
 
2.	Implement the Drivers United service such that it uses the interface defined by the IDUServiceRoutePlanner.yml OAS and realises the interface defined by the IDUAppDUService.yml OAS derived from the IDUAppDUService interface here. Again, you are free to use your own interface to derive a suitable OAS. 

3.	To test the service, create three concrete REST-specific sequence diagrams describing the execution of test cases with their concrete inputs and outputs covering different interactions. You should include a standard success case and potential failure cases, for example:
a.	Username and/or password are incorrect.
b.	The list of JobOffers is empty.
c.	An addresses in one of the Jobs submitted is not valid.
d.	The offerID does not refer to an existing JobOffer. 

An example of a concrete REST-specific sequence diagram is the one illus-trating the POST, GET, PUT and DELETE operations on a user record for Robert/Bob in the lecture on REST services. 

4.	Following the scenarios defined in your sequence diagrams, test your services using Postman. Document your tests using screen shots.

### Technologies and Tools Used
Java, Maven
Postman
