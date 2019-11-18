package model;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

import java.net.URL;
import java.net.UnknownHostException;


public class Ontology {
	private final URL ONTOLOGY_URL;
	private final String dataTaxonomyRoot;
	private final String formatTaxonomyRoot;
	/** List of two String pairs data type label and data type id */
	private List<Map<String, String>> allDataTypes;
	/** List of two String pairs format type label and format type id */
	private List<Map<String, String>> allFormatTypes;
	
	public Ontology() throws IOException {
		allDataTypes = new ArrayList<Map<String, String>>();
		allFormatTypes = new ArrayList<Map<String, String>>();
		this.ONTOLOGY_URL = new URL("http://edamontology.org/EDAM.owl");
		this.formatTaxonomyRoot = "http://edamontology.org/format_1915";
		this.dataTaxonomyRoot = "http://edamontology.org/data_0006";
		this.readOntology();
	}
	
	/*public void postTypes() throws IOException {	// Post the types as json to the server
		URL url = new URL("http://localhost:8080/setTypes"); // Server address
		URLConnection con = url.openConnection();
		HttpURLConnection http = (HttpURLConnection)con;
		http.setRequestMethod("POST"); // PUT is another valid option
		http.setDoOutput(true);
		
		byte[] out = allDataTypesJson.getBytes(StandardCharsets.UTF_8);
		int length = out.length;
		http.setFixedLengthStreamingMode(length);
		http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		http.connect();
		try(OutputStream os = http.getOutputStream()) {
		    os.write(out);
		}
	}*/
	
	/**
	 * Method used to read separately <b>FormatTaxonomy</b> and
	 * <b>TypesTaxonomy</b> part of the ontology.
	 * 
	 * @throws OWLOntologyCreationException
	 * 
	 * @return {@code true} is the ontology was read correctly, {@code false}
	 *         otherwise.
	 * @throws IOException 
	 */
	
	private boolean readOntology() throws IOException{	// Loads ontology

		final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = null;
		try {
			File ontologyFile = new File("apeInputs/ontology.owl"); //temp file
			FileUtils.copyURLToFile(ONTOLOGY_URL, ontologyFile); // Get ontology and copy to the temp file
			if (ontologyFile.exists()) {
				ontology = manager.loadOntologyFromOntologyDocument(ontologyFile);
			} else {
				System.err.println("Provided ontology does not exist.");
				return false;
			}
		} catch (OWLOntologyCreationException e) {
			System.err.println("Ontology is not properly provided.");
			return false;
		}
		catch (UnknownHostException e) {
			System.err.println("Internet connection not working properly");
			return false;
		}
		OWLClass thingClass = manager.getOWLDataFactory().getOWLThing();
		OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
		OWLReasoner reasoner = reasonerFactory.createNonBufferingReasoner(ontology);

		Set<OWLClass> subClasses = reasoner.getSubClasses(thingClass, true).getFlattened();

		OWLClass formatClass = getFormatClass(subClasses);
		OWLClass typeClass = getTypeClass(subClasses);

		if (formatClass != null) {
			exploreFormatOntologyRec(reasoner, ontology, formatClass, thingClass, thingClass, manager);
		} else {
			System.err.println("Provided ontology does not contain the "+formatTaxonomyRoot+" class.");
		}

		if (typeClass != null) {
			exploreTypeOntologyRec(reasoner, ontology, typeClass, thingClass, thingClass, manager);
		} else {
			System.err.println("Provided ontology does not contain the "+dataTaxonomyRoot+" class.");
		}

		if (formatClass == null || typeClass == null) {
			System.err.println("Ontology was not loaded because of the bad formatting.");
			return false;
		}
		return true;
	}
	
	/**
	 * Recursively exploring the hierarchy of the ontology and defining objects
	 * ({@ling AbstractModule}) on each step of the way.
	 * 
	 * @param reasoner
	 *            - reasoner used to provide subclasses
	 * @param ontology
	 *            - our current ontology
	 * @param currClass
	 *            - the class (node) currently explored
	 * @param superClass
	 *            - the superclass of the currClass
	 */
	private void exploreFormatOntologyRec(OWLReasoner reasoner, OWLOntology ontology, OWLClass currClass,	// Save the types to allFormatTypes/allDataTypes
			OWLClass superClass, OWLClass rootClass, OWLOntologyManager manager) {
		OWLDataFactory factory = manager.getOWLDataFactory();
		for (OWLClass child : reasoner.getSubClasses(currClass, true).getFlattened()) {
			if (reasoner.isSatisfiable(child)) { 		// in case that the child is not node owl:Nothing
				exploreFormatOntologyRec(reasoner, ontology, child, currClass, rootClass, manager);
			} else { 	
				System.out.println(currClass);
				String label = (currClass.getAnnotations(ontology,factory.getRDFSLabel())).toString();	// make the module a tool in case of not having subModules
				label = label.substring(label.indexOf("\"")+1, label.lastIndexOf("\""));
				Map<String, String> labels = new HashMap<String, String>();
				labels.put("value", label);
				labels.put("label", label);
				allFormatTypes.add(labels);
			}
		}
	}
	
	private void exploreTypeOntologyRec(OWLReasoner reasoner, OWLOntology ontology, OWLClass currClass,
			OWLClass superClass, OWLClass rootClass, OWLOntologyManager manager) {
		OWLDataFactory factory = manager.getOWLDataFactory();
		for (OWLClass child : reasoner.getSubClasses(currClass, true).getFlattened()) {
			if (reasoner.isSatisfiable(child)) { 		// in case that the child is not node owl:Nothing
				exploreTypeOntologyRec(reasoner, ontology, child, currClass, rootClass, manager);
			} else { 	
				String label = (currClass.getAnnotations(ontology,factory.getRDFSLabel())).toString();	// make the module a tool in case of not having subModules
				label = label.substring(label.indexOf("\"")+1, label.lastIndexOf("\""));
				Map<String, String> labels = new HashMap<String, String>();
				labels.put("value", label);
				labels.put("label", label);
				allDataTypes.add(labels);
			}
		}
	}
	
	/**
	 * Method returns the <b>ModulesTaxonomy</b> class from the set of OWL classes.
	 * 
	 * @param subClasses
	 *            - set of OWL classes
	 * @return <b>ModulesTaxonomy</b> OWL class.
	 */
	private OWLClass getFormatClass(Set<OWLClass> subClasses) {
		OWLClass moduleClass = null;	
		for (OWLClass currClass : subClasses) {
			if (getLabel(currClass).matches(formatTaxonomyRoot)) {
				moduleClass = currClass;
			}
		}
		return moduleClass;
	}
	
	/**
	 * Method returns the <b>TypesTaxonomy</b> class from the set of OWL classes.
	 * 
	 * @param subClasses
	 *            - set of OWL classes
	 * @return <b>TypesTaxonomy</b> OWL class.
	 */
	private OWLClass getTypeClass(Set<OWLClass> subClasses) {
		OWLClass typeClass = null;
		for (OWLClass currClass : subClasses) {
			if (getLabel(currClass).matches(dataTaxonomyRoot)) {
				typeClass = currClass;
			}
		}
		return typeClass;
	}
	
	/**
	 * Printing the label of the provided OWL class.
	 * 
	 * @param currClass
	 *            - provided OWL class
	 * @return String representation of the class name.
	 */
	private String getLabel(OWLClass currClass) {
		if(currClass == null) {
			return null;
		}
		String classID = currClass.toStringID();
		String label = classID.substring(classID.indexOf('#') + 1);
		label = label.replace(" ", "_");
		return label;
	}
	
	/**
	 * Getter for allFormatTypes
	 */
	public List<Map<String, String>> getFormatTypes() {
		return this.allFormatTypes;
	}
	
	/**
	 * Getter for allDataTypes
	 */
	public List<Map<String, String>> getDataTypes() {
		return this.allDataTypes;
	}
}