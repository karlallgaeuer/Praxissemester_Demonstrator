var app = angular.module('UI', []);	//Creation of the Module

		app.controller('UIController', function($scope, $http) {	//Controller
			/** Tool list */
			$scope.tools;
			/** Currently selected tool */
			$scope.selectedInputs = new Array('Sequence feature source');
			/** Data type Array*/
			$scope.dataTypes;
			/** Format type array */
			$scope.formatTypes;
			/** Select element containing types */
			$scope.typeOptions;
			/** Select element containing formats */
			$scope.formatOptions;
			/** select element containing tools */
			$scope.toolOptions;
			/** Constraints introduced */
			$scope.constraintRows = [];
			/** Counter for input dropboxes */
			$scope.counterInputs = 1;
			/** Counter for output dropboxes */
			$scope.counterOutputs = 1;
			/** Number of solutions to be used for APE */
			$scope.solutionNumber = 100;
			/** Minimum length that the workflows should have */
			$scope.minWorkflowLength = 1;
			/** Maximum length that the workflows should have */
			$scope.maxWorkflowLength= 20;
			/** APE-results in a very simple text form */
			$scope.simpleResults;
			/** Data Flow image array (results) */
			$scope.dataFlowImages;
			/** Control Flow image array (results) */
			$scope.controlFlowImages;
			/** Includes simpleResults, dataFlowImages and controlFlowImages in one array (so that looping with ng-repeats in index.html is possible) */
			$scope.mappedResults;

			$scope.constraints;
			/** Boolean to check if results table should be shown or not **/
			$scope.showTable = false;
			
			
			
			/** Set up use case 1 */
			$scope.fillUseCase1 = function(){
				//document.getElementById("dataMenuInputs0").children[0].value = "string:Mass spectrometry data format";
				//$scope.dropdownModelNameDataInput = {label: 'Mass spectrometry', value: 'object:10' };
				//$scope.dropdownModelNameDataInput = "object:10";
				//$scope.useCase1Selects = true;
				document.getElementById("dataMenuInputs0").children[0].selected = true;
			}
			
			/** Set up use case 2 */
			$scope.fillUseCase2 = function(){
				
			}
			
			/** Set up use case 3 */
			$scope.fillUseCase3 = function(){
				
			}
			
			/** Set up use case 4 */
			$scope.fillUseCase4 = function(){
				
			}
			
			/** Combines the simpleResults, dataFlowImages and controlFlowImages arrays in one array */
			$scope.mapResultArray = function(){
				var mappedArray = [];
				for(var i=0;i<$scope.simpleResults.length;i++){
					mappedArray.push({
						'simpleResults':$scope.simpleResults[i],
						'dataFlowImages':$scope.dataFlowImages[i],
						'controlFlowImages':$scope.controlFlowImages[i]
					});
				}
				return mappedArray;
			}
			
			/** Fetches the data and format types*/
			$scope.getTypes = function(){
				$http.get("http://localhost:8090/getTypes")
				.then(function(response) {
					$scope.dataTypes = response.data.dataTypes;
					$scope.formatTypes = response.data.formatTypes;
					$scope.tools = response.data.tools;
				});	
			}
			
			/** Fetches the constraint descriptions */
			$scope.getConstraintDescriptions = function(){
				$http.get("http://localhost:8090/getConstraintDescriptions")
				.then(function(response) {
					$scope.constraints = response.data;
				});	
			}
			
			/**
			 * Send post request to APE to run it
			 */
			$scope.writeConstraints = function(constraints){
				$http.post("http://localhost:8090/writeConstraints", constraints);
			}


			/**
			 * Send post request to APE to run it
			 */
			$scope.runApe = function(userInputsStringified){
				$http.post("http://localhost:8090/run", userInputsStringified)
	            .then(function(response) {
	            	$scope.getResults();
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
				/** GET-request for the simple results */
				$http.get("http://localhost:8090/getSimpleResults")
				.then(function(response) {
					$scope.simpleResults = response.data;
					/** GET-request for the data flow images */
					$http.get("http://localhost:8090/getDataFlowImg") // old full results
					.then(function(response) {
						$scope.dataFlowImages = response.data;
						/** GET request for the control flow images */
						$http.get("http://localhost:8090/getControlFlowImg")
						.then(function(response) {
							$scope.controlFlowImages = response.data;
							$scope.mappedResults = $scope.mapResultArray();
						});	
					});	
				});
			}
			
			/**
			 * Add another input dropdown box-pair (data type and data format)
			 */
			$scope.addInputs = function(){
				/** Copy the parent div */
				var toCopy = document.getElementById("inputSection0");
				var copy = angular.copy(toCopy);
				
		        /** Change the ids of the copied parent-div, the copied data type dropdwon box and the copied data format dropdown box */
		        copy.id = copy.id.substring(0,copy.id.length-1) + $scope.counterInputs;
		        copy.children[0].id = copy.children[0].id.substring(0,copy.children[0].id.length-1) + $scope.counterInputs;
		        copy.children[1].id = copy.children[1].id.substring(0,copy.children[1].id.length-1) + $scope.counterInputs;
		        $scope.counterInputs++;
		  
		        /** Append the copy to the cloneDiv container */
		        angular.element(document.getElementById("inputs")).append(copy);
			}

			/**
			 * Add another output dropdown box-pair (data type and data format)
			 */
			$scope.addOutputs = function(){
				/** Copy the parent div */
				var toCopy = document.getElementById("outputSection0");
				var copy = angular.copy(toCopy);
				
		        /** Change the ids of the copied parent-div, the copied data type dropdwon box and the copied data format dropdown box */
		        copy.id = copy.id.substring(0,copy.id.length-1) + $scope.counterOutputs;
		        copy.children[0].id = copy.children[0].id.substring(0,copy.children[0].id.length-1) + $scope.counterOutputs;
		        copy.children[1].id = copy.children[1].id.substring(0,copy.children[1].id.length-1) + $scope.counterOutputs;
		        $scope.counterOutputs++;
		  
		        /** Insert new row into the outputs table body */
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
			$scope.counter = 0;
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
				cell.setAttribute("id", "constr_"+ $scope.counter);
				cell.setAttribute("class", selectedConstraint.constraintID);
				cell.setAttribute("params", paramSize);
				
				
				for(i = 0; i<paramSize; i++){
					cell.append(templateParts[i]);
					 if(selectedConstraint.parameters[i].length == 1){
						var newToolSelect = angular.copy($scope.toolsOptions);
						newToolSelect.setAttribute("id", "constr_" + $scope.counter + "_param_"+ i + 0);
						newToolSelect.setAttribute("dimensionNo", 1);
						cell.append(newToolSelect); 
					 } else {
						var newTypeSelect = angular.copy($scope.typeOptions);
						newTypeSelect.setAttribute("id", "constr_" + $scope.counter + "_param_"+ i + 0);
						newTypeSelect.setAttribute("dimensionNo", 2);
						cell.append(newTypeSelect); 
						var newFormatSelect = angular.copy($scope.formatOptions);
						newFormatSelect.setAttribute("id", "constr_" + $scope.counter + "_param_"+ i + 1);
						newFormatSelect.setAttribute("dimensionNo", 2);
						cell.append(newFormatSelect); 
					 }
				}
				cell.append(templateParts[paramSize]);


				$scope.counter++;
			}
			
			/**
			 *  Gets the data from the type dropdown boxes and saves it and then runs APE
			 */
			$scope.fetchUserInputData = function(){
				/** Initial JSON-Object to be filled with inputs, outputs and the config data */
				var toSend = {
						"inputs": [],
						"outputs": [],
						"solution_min_length": "",
						"solution_max_length": "",
						"max_solutions": ""
				};
				/** Iterate through the input dropdown-boxes */
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
				/** Iterate through the output dropdown-boxes */
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
				/** Get all the constraints. */
				var allConstraints = {
						"constraints": [],
				};
				for(i = 0; i < $scope.counter ; i++){
					var currConstraint = {
						"constraintid" : "",
						"parameters" : []
					};
					var constraintCell = document.getElementById("constr_" + i);
					currConstraint.constraintid = constraintCell.getAttribute("class");
					var constrParams = constraintCell.getAttribute("params");

					for(j = 0; j < constrParams; j++){
						var parameterDimensions = [];
						var currParam = document.getElementById("constr_" + i + "_param_"+ j + 0);
						var dimensionNo = 0;
						if(currParam != null){
							parameterDimensions.push(currParam.value);
							dimensionNo = currParam.getAttribute("dimensionNo");
						}
						for(dim = 1; dim < dimensionNo; dim++){
							currParam = document.getElementById("constr_" + i + "_param_"+ j + dim);
							if(currParam != null){
								parameterDimensions.push(currParam.value);
							}
						}
						currConstraint["parameters"].push(parameterDimensions);
					}
					allConstraints["constraints"].push(currConstraint);
				}
				var allConstraintsStr = JSON.stringify(allConstraints);
				$scope.writeConstraints(allConstraintsStr);

				/** Fill the config field */
				toSend.solution_min_length = $scope.minWorkflowLength;
				toSend.solution_max_length = $scope.maxWorkflowLength;
				toSend.max_solutions = $scope.solutionNumber;
				/** Serialize JSON */
				var toSendStringified = JSON.stringify(toSend);
				/** Running APE */
				$scope.runApe(toSend);
			}
		});