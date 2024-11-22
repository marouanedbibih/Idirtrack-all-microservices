// package org.idirtrack.backend.modules.operator;

// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
// import java.util.stream.Collectors;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.PageRequest;
// import org.springframework.data.domain.Sort;
// import org.springframework.http.HttpStatus;
// import org.springframework.stereotype.Service;



// @Service
// public class OperatorService {

//         @Autowired
//         private OperatorRepository operatorRepository;

//         /**
//          * Service: Get all operators with pagination
//          * 
//          * @param request
//          * @return
//          * @throws AlreadyExistException
//          */

//         public MyResponse getOperatorsWithPagination(int page, int size) {
//                 // Create the page request (page index is zero-based)
//                 PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by("id").descending());

//                 // Find all operators with pagination
//                 Page<Operator> operatorPage = operatorRepository.findAll(pageRequest);
//                 List<Operator> operators = operatorPage.getContent();

//                 // Check if the operators list is empty
//                 if (operators.isEmpty()) {
//                         return MyResponse.builder()
//                                         .message("We don't found any operator")
//                                         .status(HttpStatus.OK)
//                                         .build();
//                 } else {
//                         // Map operators to DTOs
//                         List<OperatorDTO> operatorDTOs = operators.stream()
//                                         .map(operator -> OperatorDTO.builder()
//                                                         .id(operator.getId())
//                                                         .name(operator.getName())
//                                                         .totalSims((int) operatorRepository
//                                                                         .countSimsByOperatorId(operator.getId()))
//                                                         .build())
//                                         .collect(Collectors.toList());

//                         // Create the metadata
//                         Map<String, Object> metadata = new HashMap<>();
//                         metadata.put("totalPages", operatorPage.getTotalPages());
//                         metadata.put("totalElements", operatorPage.getTotalElements());
//                         metadata.put("size", size);
//                         metadata.put("currentPage", page);

//                         // Build the response with data and metadata
//                         return MyResponse.builder()
//                                         .data(operatorDTOs)
//                                         .metadata(metadata)
//                                         .status(HttpStatus.OK)
//                                         .build();
//                 }
//         }

//         /**
//          * Create a new SIM type
//          * 
//          * This method creates a new SIM type by checking if the SIM type already
//          * exists,
//          * 
//          * @param request
//          * @return
//          * @throws NotFoundException
//          */
//         public MyResponse createOperator(OperatorRequest request) throws AlreadyExistException {

//                 // Check if the Operator already exists
//                 this.ifOperatorExistThrowError(request.getName());

//                 // Transform the request to entity
//                 Operator operator = Operator.builder()
//                                 .name(request.getName())
//                                 .build();

//                 // Save the SIM type entity
//                 operator = operatorRepository.save(operator);

//                 // Return a success response
//                 return MyResponse.builder()
//                                 .message("Operator created successfully")
//                                 .status(HttpStatus.CREATED)
//                                 .build();
//         }

//         /**
//          * Update a operator
//          * 
//          * @param id
//          * @param request
//          * @return
//          * @throws AlreadyExistException
//          * @throws NotFoundException
//          */
//         public MyResponse updateOperator(Long id, OperatorRequest request)
//                         throws AlreadyExistException, NotFoundException {

//                 // Check if the operator exists except her self
//                 this.isOperatorAlreadyExistExceptHerSelf(id, request.getName());

//                 // Find the operator by ID
//                 Operator operator = this.findOperatorById(id);

//                 // Set the new data
//                 operator.setName(request.getName());

//                 // Save the updated SIM type
//                 operator = operatorRepository.save(operator);

//                 // Return a response
//                 return MyResponse.builder()
//                                 .message("Your update the operator succussfuly")
//                                 .status(HttpStatus.OK)
//                                 .build();
//         }

//         /**
//          * Get all operators
//          */
//         public MyResponse getAllOperators() {
//                 // Find all operators
//                 List<Operator> operators = operatorRepository.findAll();

//                 // Check if the operators list is empty
//                 if (operators.isEmpty()) {
//                         return MyResponse.builder()
//                                         .message("We dont found any operator")
//                                         .status(HttpStatus.OK)
//                                         .build();
//                 }
//                 // Else build the response
//                 else {
//                         List<OperatorDTO> operatorDTOs = operators.stream()
//                                         .map(operator -> OperatorDTO.builder()
//                                                         .id(operator.getId())
//                                                         .name(operator.getName())
//                                                         .build())
//                                         .collect(Collectors.toList());
//                         return MyResponse.builder()
//                                         .data(operatorDTOs)
//                                         .status(HttpStatus.OK)
//                                         .build();
//                 }

//         }

//         /**
//          * Get a operator by ID
//          * 
//          * @param id
//          * @return
//          * @throws NotFoundException
//          */
//         public MyResponse getOperatorById(Long id) throws NotFoundException {
//                 // Find the operator by ID
//                 Operator operator = this.findOperatorById(id);
//                 // Build the OperatorDTO
//                 OperatorDTO operatorDTO = OperatorDTO.builder()
//                                 .id(operator.getId())
//                                 .name(operator.getName())
//                                 .build();
//                 // Return the response
//                 return MyResponse.builder()
//                                 .data(operatorDTO)
//                                 .status(HttpStatus.OK)
//                                 .build();
//         }

//         /**
//          * Delete a operator by ID
//          * 
//          * @param id
//          * @return
//          * @throws NotFoundException
//          */
//         public MyResponse deleteOperatorById(Long id) throws NotFoundException {
//                 // Find the operator by ID
//                 Operator operator = this.findOperatorById(id);
//                 // Delete the operator
//                 operatorRepository.delete(operator);
//                 // Return a success response
//                 return MyResponse.builder()
//                                 .message("Operator deleted successfully")
//                                 .status(HttpStatus.OK)
//                                 .build();
//         }

//         /**
//          * Utils: Check if the operator already exists
//          * 
//          * @param name
//          * @throws AlreadyExistException
//          */
//         public void ifOperatorExistThrowError(String name) throws AlreadyExistException {
//                 if (operatorRepository.existsByName(name)) {
//                         throw new AlreadyExistException(ErrorResponse.builder()
//                                         .status(HttpStatus.CONFLICT)
//                                         .fieldError(FieldErrorDTO.builder()
//                                                         .field("name")
//                                                         .message("Operator with this name already exists")
//                                                         .build())
//                                         .build());
//                 }
//         }

//         /**
//          * Utils: Check if the operator already exists except her self
//          * 
//          * @param id
//          * @param name
//          * @throws AlreadyExistException
//          */
//         public void isOperatorAlreadyExistExceptHerSelf(Long id, String name) throws AlreadyExistException {
//                 if (operatorRepository.existsByNameAndIdNot(name, id)) {
//                         throw new AlreadyExistException(ErrorResponse.builder()
//                                         .fieldError(FieldErrorDTO.builder()
//                                                         .field("name")
//                                                         .message("This operator already exists")
//                                                         .build())
//                                         .status(HttpStatus.CONFLICT)
//                                         .build());
//                 }

//         }

//         /**
//          * Utils: Find a operator by ID
//          * 
//          * @param id
//          * @return
//          * @throws NotFoundException
//          */
//         public Operator findOperatorById(Long id) throws NotFoundException {
//                 return operatorRepository.findById(id)
//                                 .orElseThrow(() -> new NotFoundException(
//                                                 ErrorResponse.builder()
//                                                                 .message("Operator with the id: " + id + " not found")
//                                                                 .status(HttpStatus.NOT_FOUND)
//                                                                 .build()));
//         }

// }
