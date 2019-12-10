var app = angular.module('UI', []);	//Creation of the Module
app.config( [
    '$compileProvider',
    function( $compileProvider )
    {   
        $compileProvider.aHrefSanitizationWhitelist(/^\s*(https?|ftp|mailto|chrome-extension):/);
        $compileProvider.imgSrcSanitizationWhitelist(/^\s*(https?|ftp|file|content|blob|chrome-extension)|data:image\/|\/?img\//);
    }
]);

		app.controller('UIController', function($scope, $http) {	//Controller
			$scope.tools;	//Tool list
			$scope.selectedInputs = new Array('Sequence feature source');	//Currently selected tool
			$scope.api;	//Api GET data is saved here
			$scope.dataTypes; // Data types
			$scope.formatTypes;	// Format types
			$scope.tools; // Tools
			$scope.typeOptions //select element containing types
			$scope.formatOptions //select element containing formats
			$scope.toolOptions //select element containing tools
			$scope.constraintRows = [];; // Constraints introduced
			$scope.counterInputs = 1;	// Counter for input dropboxes
			$scope.counterOutputs = 1;	// Counter for output dropboxes
			$scope.solutionNumber = 10;
			$scope.minWorkflowLength = 1;
			$scope.maxWorkflowLength= 5;
			$scope.results;	// APE results
			$scope.dataFlowImages; 
			$scope.mappedResults;	// Includes results and "full results" in one array (so that looping with one ng-repeat is possible)
			$scope.controlFlowImages;	// Array of the result-images
			$scope.constraints;
			$scope.dataString = "Data";
			$scope.formatString = "Format";
			/** boolean to check if results table should be shown or not **/
			$scope.showTable = false;

			$scope.mapResultArray = function(){
				var mappedArray = [];
				for(var i=0;i<$scope.results.length;i++){
					mappedArray.push({
						'results':$scope.results[i],
						'fullResults':$scope.fullResults[i],
						'resultImages':$scope.resultImages[i]
					});
				}
				return mappedArray;
			}
			$scope.getApi = function(selectedTool){	//Pulls api data of the selected tool
				$http.get("https://bio.tools/api/biotools:".concat($scope.selectedTool,"?format=json")) 
				.then(function(response){
					$scope.api = response.data;	
				});
			}
			$scope.getTypes = function(){
				$http.get("http://localhost:8090/getTypes")
				.then(function(response) {
					$scope.dataTypes = response.data.dataTypes;
					$scope.formatTypes = response.data.formatTypes;
					$scope.tools = response.data.tools;
					console.log(response.data);
				});	
			}
			
			$scope.getConstraintDescriptions = function(){
				$http.get("http://localhost:8090/getConstraintDescriptions")
				.then(function(response) {
					$scope.constraints = response.data; // CHange this to data.constraints
				});	
			}
			
			/**
			 * Send post request to APE
			 */
			$scope.runApe = function(toSend){
				//var data = param(toSend);	// Serialize JSON
				var data = JSON.stringify(toSend);
				$http.post("http://localhost:8090/run", data)
	            .then(function(response) {
	            	$scope.getResults();
	            	// First run results is null, 2nd run the first results are used, 3rd run the 2nd results are used etc.
	            	$scope.showTable = true;
	                console.log(response.data);
	            }, function(error){
	            	console.log("Post request failed"); 
	            });
			}
			
			/**
			 * Get results from APE
			 */
			$scope.getResults = function(){
				$http.get("http://localhost:8090/getResults")
				.then(function(response) {
					$scope.results = response.data;
					$http.get("http://localhost:8090/getDataFlowImg")	// Get request for the more detailed results
					.then(function(response) {
						$scope.dataflowImages = response.data;
						$http.get("http://localhost:8090/getControlFlowImg")
						.then(function(response) {
							$scope.controlFlowImages = response.data;
							$scope.mappedResults = $scope.mapResultArray();	// See variable initialisation comment
						});	
					});	
				});
			}
			
			/**
			 * Add another input dropdown box-pair (data type and data format)
			 */
			$scope.addInputs = function(){
				// Copy the parent div
				var toCopy = document.getElementById("inputSection0");
				var copy = angular.copy(toCopy);
				
		        //Change the ids of the copied parent-div, the copied data type dropdwon box and the copied data format dropdown box
		        copy.id = copy.id.substring(0,copy.id.length-1) + $scope.counterInputs;
		        copy.children[0].id = copy.children[0].id.substring(0,copy.children[0].id.length-1) + $scope.counterInputs;
		        copy.children[1].id = copy.children[1].id.substring(0,copy.children[1].id.length-1) + $scope.counterInputs;
		        $scope.counterInputs++;
		  
		        // Append the copy to the cloneDiv container
		        angular.element(document.getElementById("inputs")).append(copy);
			}
			
			/**
			 * Add another output dropdown box-pair (data type and data format)
			 */
			// $scope.constraintRows 
			// $scope.counter = 1;
			// $scope.addConstraint = function(){
			// 	// Copy the parent div
			// 	 var constraintDropdown = document.getElementById("constraintsSection").children[0];
			// 	 $scope.constraintRows.push('Row ' + $scope.counter +  constraintDropdown.options[formatSelect.selectedIndex].text) ;
			// 	 $scope.counter ++;
				
			// }

			/**
			 * Add another output dropdown box-pair (data type and data format)
			 */
			$scope.addOutputs = function(){
				// Copy the parent div
				var toCopy = document.getElementById("outputSection0");
				var copy = angular.copy(toCopy);
				
		        //Change the ids of the copied parent-div, the copied data type dropdwon box and the copied data format dropdown box
		        copy.id = copy.id.substring(0,copy.id.length-1) + $scope.counterOutputs;
		        copy.children[0].id = copy.children[0].id.substring(0,copy.children[0].id.length-1) + $scope.counterOutputs;
		        copy.children[1].id = copy.children[1].id.substring(0,copy.children[1].id.length-1) + $scope.counterOutputs;
		        $scope.counterOutputs++;
		  
		        // Insert new row into the outputs table body
		        angular.element(document.getElementById("outputs")).append(copy);
			}

			function getTypeOptions() {
                    var typeOptions = document.createElement('select');
					for(i = 0; i < $scope.dataTypes.length; i++){
						var newOption = document.createElement("option");
						newOption.text = $scope.dataTypes[i].label;
						newOption.value = $scope.dataTypes[i].value;
						typeOptions.appendChild(newOption);
					}  
					return typeOptions;
			}

			function getFormatOptions() {
                    var formatOptions = document.createElement('select');
					for(i = 0; i < $scope.formatTypes.length; i++){
						var newOption = document.createElement("option");
						newOption.text = $scope.formatTypes[i].label;
						newOption.value = $scope.formatTypes[i].value;
						formatOptions.appendChild(newOption);
					}  
					return formatOptions;
			}

			function getToolOptions() {
                    var toolOptions = document.createElement('select');
					for(i = 0; i < $scope.tools.length; i++){
						var newOption = document.createElement("option");
						newOption.text = $scope.tools[i].label;
						newOption.value = $scope.tools[i].value;
						toolOptions.appendChild(newOption);
					}  
					return toolOptions;
			}
			
			/**
			 * Add a row for each constraint that's specified
			 */
			$scope.constraintRows 
			$scope.counter = 1;
			$scope.addConstraint = function(){

				    if($scope.typeOptions == null){
						$scope.typeOptions = getTypeOptions();
					}
					if($scope.formatOptions == null){
						$scope.formatOptions = getFormatOptions();
					}
					if($scope.toolsOptions == null){
						$scope.toolsOptions = getToolOptions();
					}
                    

				
				var constraintDropdown = document.getElementById("constraintsSection").children[0];
				var selectedConstraint =  JSON.parse(constraintDropdown.value.replace("string:",""));
				var paramSize = selectedConstraint.parameters.length;
				var description = selectedConstraint.description
				
				for(i =1; i<=paramSize; i++){
					description = description.replace("${parameter_" + i + "}", "$");
				}
				var templateParts = description.split("$");

				var tableBody = document.getElementById("constraints");
				var row = document.createElement('tr');
				tableBody.appendChild(row);
				var cell = document.createElement('td');
				row.appendChild(cell);

				for(i = 0; i<paramSize; i++){
					cell.append(templateParts[i]);
					 if(selectedConstraint.parameters[i].length == 1){
						
						var copy = angular.copy($scope.toolsOptions);
						cell.append(copy); 
					 } else {
						var copy1 = angular.copy($scope.typeOptions);
						cell.append(copy1); 
						var copy2 = angular.copy($scope.formatOptions);
						cell.append(copy2); 
					 }
				}
				cell.append(templateParts[paramSize]);
				
			}
			
			/**
			 *  Gets the data from the type dropdown boxes and saves it
			 */
			
			$scope.fetchTypes = function(){
				var toSend = {	// Initial JSON-Object to be filled with inputs, outputs and the config data
						"inputs": [],
						"outputs": [],
						"solution_min_length": "",
						"solution_max_length": "",
						"max_solutions": ""
				};
				// Iterate through the input dropdown-boxes
				var index1 = 0;
				for(i = 0; i < $scope.counterInputs; i++){
					var typeSelect = document.getElementById("dataMenuInputs" + i).children[0];
					var formatSelect = document.getElementById("formatMenuInputs" + i).children[0];
					if(typeSelect.options[typeSelect.selectedIndex].text != ""){
						if(formatSelect.options[formatSelect.selectedIndex].text != ""){
							toSend.inputs[index1++] = {
									"Data": typeSelect.options[typeSelect.selectedIndex].text,
									"Format": formatSelect.options[formatSelect.selectedIndex].text
							}
						}else{
							toSend.inputs[index1++] = {
									"Data": typeSelect.options[typeSelect.selectedIndex].text
							}
						}
					}else{
						if(formatSelect.options[formatSelect.selectedIndex].text != ""){
							toSend.inputs[index1++] = {
									"Format": formatSelect.options[formatSelect.selectedIndex].text
							}
						}
					}
				}
				// Iterate through the output dropdown-boxes
				var index2 = 0;
				for(i = 0; i < $scope.counterOutputs; i++){
					var typeSelect = document.getElementById("dataMenuOutputs" + i).children[0];
					var formatSelect = document.getElementById("formatMenuOutputs" + i).children[0];
					if(typeSelect.options[typeSelect.selectedIndex].text != ""){
						if(formatSelect.options[formatSelect.selectedIndex].text != ""){
							toSend.outputs[index2++] = {
									"Data": typeSelect.options[typeSelect.selectedIndex].text,
									"Format": formatSelect.options[formatSelect.selectedIndex].text
							}
						}else{
							toSend.outputs[index2++] = {
									"Data": typeSelect.options[typeSelect.selectedIndex].text
							}
						}
					}else{
						if(formatSelect.options[formatSelect.selectedIndex].text != ""){
							toSend.outputs[index2++] = {
									"Format": formatSelect.options[formatSelect.selectedIndex].text
							}
						}
					}
				}
				// Fill the config field
				toSend.solution_min_length = $scope.minWorkflowLength;
				toSend.solution_max_length = $scope.maxWorkflowLength;
				toSend.max_solutions = $scope.solutionNumber;
				$scope.testData = toSend;
				$scope.runApe(toSend);
			}
		});