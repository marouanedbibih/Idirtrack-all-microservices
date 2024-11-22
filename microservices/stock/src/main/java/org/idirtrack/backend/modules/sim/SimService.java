// package org.idirtrack.backend.modules.sim;

// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
// import java.util.stream.Collectors;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.PageRequest;
// import org.springframework.data.domain.Pageable;
// import org.springframework.http.HttpStatus;
// import org.springframework.stereotype.Service;

// import com.idirtrack.stock_service.basics.BasicException;
// import com.idirtrack.stock_service.basics.BasicResponse;
// import com.idirtrack.stock_service.basics.MessageType;
// import com.idirtrack.stock_service.basics.MetaData;
// import com.idirtrack.stock_service.errors.AlreadyExistException;
// import com.idirtrack.stock_service.errors.NotFoundException;
// import com.idirtrack.stock_service.operator.Operator;
// import com.idirtrack.stock_service.operator.OperatorRepository;
// import com.idirtrack.stock_service.sim.https.SimRequest;
// import com.idirtrack.stock_service.stock.Stock;
// import com.idirtrack.stock_service.stock.StockRepository;
// import com.idirtrack.stock_service.utils.ErrorResponse;
// import com.idirtrack.stock_service.utils.FieldErrorDTO;
// import com.idirtrack.stock_service.utils.MyResponse;

// import lombok.RequiredArgsConstructor;

// @Service
// @RequiredArgsConstructor
// public class SimService {

//         private final SimRepository simRepository;
//         private final SimStockRepository simStockRepository;
//         private final StockRepository stockRepository;
//         private final OperatorRepository operatorRepository;

//         /**
//          * Service: Create a new sim
//          * 
//          * @param simRequest
//          * @return
//          * @throws BasicException
//          * @throws AlreadyExistException
//          */
//         public MyResponse createSim(SimRequest simRequest) throws AlreadyExistException, NotFoundException {
//                 // Check if the phone number already exists
//                 this.ifPhoneNumberAlreadyExist(simRequest.getPhone());
//                 // Check if the CCID already exists
//                 this.ifCCIDAlreadyExist(simRequest.getCcid());
//                 // Check if the operator exists
//                 Operator operator = this.findOperatorById(simRequest.getOperatorId());
//                 // Build the Sim Entity
//                 Sim sim = this.tansformRequestToEntity(simRequest, operator);
//                 // Save the sim
//                 sim = simRepository.save(sim);
//                 // Update the stock
//                 this.updateSimStock(sim);
//                 // Return the response
//                 return MyResponse.builder()
//                                 .message("SIM created successfully")
//                                 .status(HttpStatus.CREATED)
//                                 .build();
//         }

//         // Update the stock
//         private void updateSimStock(Sim sim) {
//                 // Convert LocalDateTime to Date
//                 java.sql.Date dateEntree = java.sql.Date.valueOf(sim.getCreatedAt().toLocalDate());
//                 List<Stock> stocks = stockRepository.findByDateEntree(dateEntree);
//                 Stock stock = null;
//                 SimStock simStock = null;

//                 for (Stock s : stocks) {
//                         simStock = simStockRepository.findByStockAndOperator(s, sim.getOperator());
//                         if (simStock != null) {
//                                 stock = s;
//                                 break;
//                         }
//                 }

//                 if (stock == null) {
//                         stock = Stock.builder()
//                                         .dateEntree(dateEntree)
//                                         .quantity(1)
//                                         .build();
//                         stock = stockRepository.save(stock);

//                         simStock = SimStock.builder()
//                                         .operator(sim.getOperator())
//                                         .stock(stock)
//                                         .build();
//                         simStockRepository.save(simStock);
//                 } else {
//                         stock.setQuantity(stock.getQuantity() + 1);
//                         stockRepository.save(stock);
//                 }
//         }

//         // Update the stock on delete
//         private void updateSimStockOnDelete(Sim sim) {
//                 java.sql.Date dateEntree = java.sql.Date.valueOf(sim.getCreatedAt().toLocalDate());

//                 List<Stock> stocks = stockRepository.findByDateEntree(dateEntree);
//                 Stock stock = null;
//                 SimStock simStock = null;

//                 for (Stock s : stocks) {
//                         simStock = simStockRepository.findByStockAndOperator(s, sim.getOperator());
//                         if (simStock != null) {
//                                 stock = s;
//                                 break;
//                         }
//                 }

//                 if (stock != null) {
//                         stock.setQuantity(stock.getQuantity() - 1);
//                         stockRepository.save(stock);

//                         if (stock.getQuantity() <= 0) {
//                                 simStockRepository.delete(simStock);
//                                 stockRepository.delete(stock);
//                         }
//                 }
//         }

//         /**
//          * GET SIM BY ID
//          * 
//          * @param id
//          * @return
//          * @throws NotFoundException
//          */
//         public MyResponse getSimById(Long id) throws NotFoundException {
//                 // Find the sim by id
//                 Sim sim = this.findSimById(id);

//                 // Build the entity to DTO
//                 SimDTO simDTO = this.transformEntityToDTO(sim);

//                 // Return the response
//                 return MyResponse.builder()
//                                 .data(simDTO)
//                                 .message("Sim retrieved successfully")
//                                 .status(HttpStatus.OK)
//                                 .build();
//         }

//         /**
//          * Service : Get all SIMs with pagination
//          * 
//          * @param page
//          * @param size
//          * @return
//          */
//         public MyResponse getAllSimsWithPagination(int page, int size) {
//                 // Create a pageable object
//                 Pageable pageable = PageRequest.of(page - 1, size);
//                 // Get all the sims with pagination
//                 Page<Sim> simPage = simRepository.findAll(pageable);
//                 // Check if the page is empty
//                 if (simPage.isEmpty()) {
//                         return MyResponse.builder()
//                                         .message("No SIMs found")
//                                         .status(HttpStatus.NOT_FOUND)
//                                         .build();
//                 }
//                 // Else, build the response
//                 else {
//                         List<SimDTO> simDTOs = simPage.getContent().stream().map(sim -> this.transformEntityToDTO(sim))
//                                         .collect(Collectors.toList());
//                         // Build the Metadata
//                         Map<String, Object> metadata = new HashMap<>();
//                         metadata.put("currentPage", simPage.getNumber() + 1);
//                         metadata.put("totalPages", simPage.getTotalPages());
//                         metadata.put("size", simPage.getSize());
//                         // Return the response
//                         return MyResponse.builder()
//                                         .data(simDTOs)
//                                         .metadata(metadata)
//                                         .status(HttpStatus.OK)
//                                         .build();
//                 }

//         }

//         public MyResponse searchSIMs(String query, int page, int size) {

//                 // Create a pageable object
//                 Pageable pageable = PageRequest.of(page - 1, size);
//                 // Search by any field
//                 Page<Sim> simPage = simRepository.search(query, pageable);
//                 // Check if the page is empty
//                 if (simPage.isEmpty()) {
//                         return MyResponse.builder()
//                                         .message("No SIMs found")
//                                         .status(HttpStatus.OK)
//                                         .build();
//                 }
//                 // Else, build the response
//                 else {
//                         List<SimDTO> simDTOs = simPage.getContent().stream().map(sim -> this.transformEntityToDTO(sim))
//                                         .collect(Collectors.toList());
//                         // Build the Metadata
//                         Map<String, Object> metadata = new HashMap<>();
//                         metadata.put("currentPage", simPage.getNumber() + 1);
//                         metadata.put("totalPages", simPage.getTotalPages());
//                         metadata.put("size", simPage.getSize());
//                         // Return the response
//                         return MyResponse.builder()
//                                         .data(simDTOs)
//                                         .metadata(metadata)
//                                         .status(HttpStatus.OK)
//                                         .build();
//                 }
                
//         }

//         /**
//          * Service: Update SIM
//          * 
//          */
//         public MyResponse updateSim(Long id, SimRequest request) throws NotFoundException, AlreadyExistException {

//                 // Find the sim
//                 Sim sim = this.findSimById(id);
//                 // Check if the phone number already exists except the current sim
//                 this.ifPhoneNumberAlreadyExistExceptCurrentSim(request.getPhone(), id);
//                 // Check if the CCID already exists except the current sim
//                 this.ifCCIDAlreadyExistExceptCurrentSim(request.getCcid(), id);
//                 // Find the operator
//                 Operator operator = this.findOperatorById(request.getOperatorId());
//                 // Update the sim data
//                 sim.setPin(request.getPin());
//                 sim.setPuk(request.getPuk());
//                 sim.setCcid(request.getCcid());
//                 sim.setOperator(operator);
//                 sim.setPhone(request.getPhone());
//                 // Save sim
//                 sim = simRepository.save(sim);
//                 // Build SimDTO
//                 SimDTO simDTO = this.transformEntityToDTO(sim);
//                 // Return response
//                 return MyResponse.builder()
//                                 .data(simDTO)
//                                 .message("Sim update successfully")
//                                 .build();

//         }

//         /**
//          * Service: Delete SIM
//          * 
//          * @param id
//          * @return MyResponse
//          * @throws NotFoundException
//          */
//         public MyResponse deleteSim(Long id) throws NotFoundException {
//                 // Find sim by id
//                 Sim sim = this.findSimById(id);
//                 // Update the stock on delete
//                 updateSimStockOnDelete(sim);
//                 // Delete the sim
//                 simRepository.delete(sim);
//                 // Return the response
//                 return MyResponse.builder()
//                                 .message("Sim deleted successfully")
//                                 .status(HttpStatus.OK)
//                                 .build();
//         }

//         public BasicResponse countNonInstalledSims() {
//                 long count = simRepository.countByStatus(SimStatus.NON_INSTALLED);
//                 return BasicResponse.builder()
//                                 .content(count)
//                                 .message("Non-installed SIMs count retrieved successfully")
//                                 .messageType(MessageType.SUCCESS)
//                                 .status(HttpStatus.OK)
//                                 .build();
//         }

//         public BasicResponse getAllNonInstalledSims(int page, int size) throws BasicException {
//                 Pageable pageRequest = PageRequest.of(page - 1, size);
//                 Page<Sim> simPage = simRepository.findAllByStatus(SimStatus.NON_INSTALLED, pageRequest);

//                 List<SimBoitierDTO> simDTOs = simPage.getContent().stream()
//                                 .map(sim -> SimBoitierDTO.builder()
//                                                 .simMicroserviceId(sim.getId())
//                                                 .phone(sim.getPhone())
//                                                 .ccid(sim.getCcid())
//                                                 .operatorName(sim.getOperator().getName())
//                                                 .build())
//                                 .collect(Collectors.toList());

//                 MetaData metaData = MetaData.builder()
//                                 .currentPage(simPage.getNumber() + 1)
//                                 .totalPages(simPage.getTotalPages())
//                                 .size(simPage.getSize())
//                                 .build();

//                 return BasicResponse.builder()
//                                 .content(simDTOs)
//                                 .message("SIMs retrieved successfully")
//                                 .messageType(MessageType.SUCCESS)
//                                 .status(HttpStatus.OK)
//                                 .metadata(metaData)
//                                 .build();
//         }

//         public BasicResponse searchNonInstalledSims(String query, int page, int size) throws BasicException {
//                 Pageable pageable = PageRequest.of(page - 1, size);
//                 Page<Sim> simPage = simRepository.findAllByStatusAndPhoneContainingOrCcidContaining(
//                                 SimStatus.NON_INSTALLED, query, pageable);

//                 if (simPage.isEmpty()) {
//                         throw new BasicException(BasicResponse.builder()
//                                         .content(null)
//                                         .message("No non-installed SIMs found")
//                                         .messageType(MessageType.ERROR)
//                                         .status(HttpStatus.NOT_FOUND)
//                                         .build());
//                 }

//                 List<SimBoitierDTO> simDTOs = simPage.getContent().stream()
//                                 .map(sim -> SimBoitierDTO.builder()
//                                                 .simMicroserviceId(sim.getId())
//                                                 .phone(sim.getPhone())
//                                                 .ccid(sim.getCcid())
//                                                 .operatorName(sim.getOperator().getName())
//                                                 .build())
//                                 .collect(Collectors.toList());

//                 MetaData metadata = MetaData.builder()
//                                 .currentPage(simPage.getNumber() + 1)
//                                 .totalPages(simPage.getTotalPages())
//                                 .size(simPage.getSize())
//                                 .build();

//                 return BasicResponse.builder()
//                                 .content(simDTOs)
//                                 .message("SIMs retrieved successfully")
//                                 .messageType(MessageType.SUCCESS)
//                                 .status(HttpStatus.OK)
//                                 .metadata(metadata)
//                                 .build();
//         }

//         public BasicResponse changeSimStatusInstalled(Long id) throws BasicException {
//                 Sim sim = simRepository.findById(id).orElse(null);
//                 if (sim == null) {
//                         throw new BasicException(BasicResponse.builder()
//                                         .content(null)
//                                         .message("Sim not found")
//                                         .messageType(MessageType.ERROR)
//                                         .status(HttpStatus.NOT_FOUND)
//                                         .build());
//                 }

//                 sim.setStatus(SimStatus.INSTALLED);
//                 sim = simRepository.save(sim);

//                 return BasicResponse.builder()
//                                 .content(sim)
//                                 .message("Device status changed to installed successfully")
//                                 .messageType(MessageType.SUCCESS)
//                                 .status(HttpStatus.OK)
//                                 .build();
//         }

//         /**
//          * CHANGE SIM STATUS
//          * 
//          * This service changes the status of a SIM card
//          * First, he check if the status is valid
//          * Then, he check if the sim exists, if not, he throws an exception
//          * If the sim exists, he changes the status of the sim
//          * Finally, he returns a BasicResponse with the sim
//          * 
//          * @param id
//          * @param status
//          * @return
//          * @throws BasicException
//          */

//         public BasicResponse changeSimStatus(Long id, String status) throws BasicException {
//                 // Find the sim
//                 Sim sim = simRepository.findById(id).orElseThrow(
//                                 () -> new BasicException(BasicResponse.builder()
//                                                 .content(null)
//                                                 .message("Sim not found")
//                                                 .messageType(MessageType.ERROR)
//                                                 .status(HttpStatus.NOT_FOUND)
//                                                 .build()));

//                 // Check if the status is valid by checking the enum
//                 try {
//                         SimStatus simStatus = SimStatus.valueOf(status.toUpperCase());
//                         sim.setStatus(simStatus);
//                         sim = simRepository.save(sim);
//                         return BasicResponse.builder()
//                                         .message("Sim status changed successfully")
//                                         .messageType(MessageType.SUCCESS)
//                                         .status(HttpStatus.OK)
//                                         .content(sim) // include the updated sim in the response content
//                                         .build();
//                 } catch (IllegalArgumentException e) {
//                         throw new BasicException(BasicResponse.builder()
//                                         .content(null)
//                                         .message("Invalid status")
//                                         .messageType(MessageType.ERROR)
//                                         .status(HttpStatus.BAD_REQUEST)
//                                         .build());
//                 }
//         }

//         /**
//          * Utils: Check if the phone number already exists except the current sim
//          * 
//          * @param phone
//          * @param id
//          * @throws AlreadyExistException
//          * @return void
//          */

//         public void ifPhoneNumberAlreadyExistExceptCurrentSim(String phone, Long id) throws AlreadyExistException {
//                 if (simRepository.existsByPhoneAndIdNot(phone, id)) {
//                         throw new AlreadyExistException(ErrorResponse.builder()
//                                         .fieldError(FieldErrorDTO.builder()
//                                                         .field("phone")
//                                                         .message("Phone number already exists")
//                                                         .build())
//                                         .build());
//                 }
//         }

//         /**
//          * Utils: Check if the CCID already exists except the current sim
//          * 
//          * @param ccid
//          * @param id
//          * @throws AlreadyExistException
//          * @return void
//          */

//         public void ifCCIDAlreadyExistExceptCurrentSim(String ccid, Long id) throws AlreadyExistException {
//                 if (simRepository.existsByCcidAndIdNot(ccid, id)) {
//                         throw new AlreadyExistException(ErrorResponse.builder()
//                                         .fieldError(FieldErrorDTO.builder()
//                                                         .field("ccid")
//                                                         .message("CCID already exists")
//                                                         .build())
//                                         .build());
//                 }
//         }

//         /**
//          * Utils: Check if the phone number already exists
//          * 
//          * @param phone
//          * @throws AlreadyExistException
//          */
//         public void ifPhoneNumberAlreadyExist(String phone) throws AlreadyExistException {
//                 if (simRepository.existsByPhone(phone)) {
//                         throw new AlreadyExistException(ErrorResponse.builder()
//                                         .fieldError(FieldErrorDTO.builder()
//                                                         .field("phone")
//                                                         .message("Phone number already exists")
//                                                         .build())
//                                         .build());
//                 }

//         }

//         /**
//          * Utils: Check if the CCID already exists
//          * 
//          * @param ccid
//          * @throws AlreadyExistException
//          */
//         public void ifCCIDAlreadyExist(String ccid) throws AlreadyExistException {
//                 if (simRepository.existsByCcid(ccid)) {
//                         throw new AlreadyExistException(ErrorResponse.builder()
//                                         .fieldError(FieldErrorDTO.builder()
//                                                         .field("ccid")
//                                                         .message("CCID already exists")
//                                                         .build())
//                                         .build());
//                 }
//         }

//         /**
//          * Utils: Find an operator by id
//          * 
//          * @param id
//          * @return Operator
//          * @throws NotFoundException
//          */
//         public Operator findOperatorById(Long id) throws NotFoundException {
//                 return operatorRepository.findById(id).orElseThrow(
//                                 () -> new NotFoundException(ErrorResponse.builder()
//                                                 .fieldError(FieldErrorDTO.builder()
//                                                                 .field("operatorId")
//                                                                 .message("Operator not found")
//                                                                 .build())
//                                                 .build()));
//         }

//         /**
//          * Utils: Transform a SimRequest to a Sim entity
//          * 
//          * @param request
//          * @param operator
//          * @return Sim
//          */
//         public Sim tansformRequestToEntity(SimRequest request, Operator operator) {
//                 return Sim.builder()
//                                 .pin(request.getPin())
//                                 .puk(request.getPuk())
//                                 .ccid(request.getCcid())
//                                 .operator(operator)
//                                 .phone(request.getPhone())
//                                 .status(SimStatus.NON_INSTALLED)
//                                 .build();
//         }

//         /**
//          * Utils : Find Sim by id
//          */

//         public Sim findSimById(Long id) throws NotFoundException {
//                 return simRepository.findById(id).orElseThrow(
//                                 () -> new NotFoundException(ErrorResponse.builder()
//                                                 .message("Sim not found with id: " + id)
//                                                 .build()));
//         }

//         /**
//          * Utils: Transform a Sim entity to a SimDTO
//          */

//         public SimDTO transformEntityToDTO(Sim sim) {
//                 return SimDTO.builder()
//                                 .id(sim.getId())
//                                 .pin(sim.getPin())
//                                 .puk(sim.getPuk())
//                                 .ccid(sim.getCcid())
//                                 .phone(sim.getPhone())
//                                 .status(sim.getStatus())
//                                 .createdAt(sim.getCreatedAt())
//                                 .operatorId(sim.getOperator().getId())
//                                 .operatorName(sim.getOperator().getName())
//                                 .build();

//         }
// }