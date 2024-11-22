// package com.idirtrack.stock_service.operator;

// import static org.assertj.core.api.Assertions.assertThat;
// import static org.junit.jupiter.api.Assertions.assertThrows;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.anyLong;
// import static org.mockito.ArgumentMatchers.anyString;
// import static org.mockito.Mockito.*;

// import java.util.ArrayList;
// import java.util.List;
// import java.util.Optional;

// import org.idirtrack.backend.modules.operator.Operator;
// import org.idirtrack.backend.modules.operator.OperatorRepository;
// import org.idirtrack.backend.modules.operator.OperatorRequest;
// import org.idirtrack.backend.modules.operator.OperatorService;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.MockitoAnnotations;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.PageImpl;
// import org.springframework.data.domain.PageRequest;
// import org.springframework.data.domain.Sort;
// import org.springframework.http.HttpStatus;

// import com.idirtrack.stock_service.errors.AlreadyExistException;
// import com.idirtrack.stock_service.errors.NotFoundException;
// import com.idirtrack.stock_service.utils.MyResponse;

// class OperatorServiceTest {

//     @InjectMocks
//     private OperatorService operatorService;

//     @Mock
//     private OperatorRepository operatorRepository;

//     @BeforeEach
//     void setUp() {
//         MockitoAnnotations.openMocks(this);
//     }

//     /**
//      * Tests the successful retrieval of operators with pagination.
//      * Verifies that the response status is OK, data is not null, and metadata is
//      * included.
//      */
//     @Test
//     void testGetOperatorsWithPagination_Success() {
//         List<Operator> operators = new ArrayList<>();
//         operators.add(Operator.builder().id(1L).name("Operator 1").build());

//         Page<Operator> operatorPage = new PageImpl<>(operators, PageRequest.of(0, 10, Sort.by("id").descending()), 1);

//         when(operatorRepository.findAll(any(PageRequest.class))).thenReturn(operatorPage);
//         when(operatorRepository.countSimsByOperatorId(anyLong())).thenReturn(100L);

//         MyResponse response = operatorService.getOperatorsWithPagination(1, 10);

//         assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
//         assertThat(response.getData()).isNotNull();
//         assertThat(response.getMetadata()).isNotNull();
//     }

//     /**
//      * Tests the scenario where no operators are found with pagination.
//      * Verifies that the response status is OK and an appropriate message is
//      * returned.
//      */
//     @Test
//     void testGetOperatorsWithPagination_NoOperatorsFound() {
//         Page<Operator> operatorPage = new PageImpl<>(new ArrayList<>(),
//                 PageRequest.of(0, 10, Sort.by("id").descending()), 0);

//         when(operatorRepository.findAll(any(PageRequest.class))).thenReturn(operatorPage);

//         MyResponse response = operatorService.getOperatorsWithPagination(1, 10);

//         assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
//         assertThat(response.getMessage()).isEqualTo("We don't found any operator");
//     }

//     /**
//      * Tests the successful creation of a new operator.
//      * Verifies that the response status is CREATED and the success message is
//      * returned.
//      */
//     @Test
//     void testCreateOperator_Success() throws AlreadyExistException {
//         OperatorRequest request = new OperatorRequest();
//         request.setName("New Operator");

//         when(operatorRepository.existsByName(anyString())).thenReturn(false);
//         when(operatorRepository.save(any(Operator.class)))
//                 .thenReturn(Operator.builder().id(1L).name("New Operator").build());

//         MyResponse response = operatorService.createOperator(request);

//         assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED);
//         assertThat(response.getMessage()).isEqualTo("Operator created successfully");
//     }

//     /**
//      * Tests the scenario where an operator with the same name already exists.
//      * Verifies that the AlreadyExistException is thrown.
//      */
//     @Test
//     void testCreateOperator_AlreadyExist() {
//         OperatorRequest request = new OperatorRequest();
//         request.setName("Existing Operator");

//         when(operatorRepository.existsByName(anyString())).thenReturn(true);

//         assertThrows(AlreadyExistException.class, () -> {
//             operatorService.createOperator(request);
//         });
//     }

//     /**
//      * Tests the successful update of an existing operator.
//      * Verifies that the response status is OK and the success message is returned.
//      */
//     @Test
//     void testUpdateOperator_Success() throws AlreadyExistException, NotFoundException {
//         OperatorRequest request = new OperatorRequest();
//         request.setName("Updated Operator");

//         when(operatorRepository.existsByNameAndIdNot(anyString(), anyLong())).thenReturn(false);
//         when(operatorRepository.findById(anyLong()))
//                 .thenReturn(Optional.of(Operator.builder().id(1L).name("Operator").build()));
//         when(operatorRepository.save(any(Operator.class)))
//                 .thenReturn(Operator.builder().id(1L).name("Updated Operator").build());

//         MyResponse response = operatorService.updateOperator(1L, request);

//         assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
//         assertThat(response.getMessage()).isEqualTo("Your update the operator succussfuly");
//     }

//     /**
//      * Tests the scenario where an operator with the same name already exists during
//      * the update.
//      * Verifies that the AlreadyExistException is thrown.
//      */
//     @Test
//     void testUpdateOperator_AlreadyExist() {
//         OperatorRequest request = new OperatorRequest();
//         request.setName("Existing Operator");

//         when(operatorRepository.existsByNameAndIdNot(anyString(), anyLong())).thenReturn(true);

//         assertThrows(AlreadyExistException.class, () -> {
//             operatorService.updateOperator(1L, request);
//         });
//     }

//     /**
//      * Tests the scenario where the operator to be updated is not found.
//      * Verifies that the NotFoundException is thrown.
//      */
//     @Test
//     void testUpdateOperator_NotFound() {
//         OperatorRequest request = new OperatorRequest();
//         request.setName("Nonexistent Operator");

//         when(operatorRepository.existsByNameAndIdNot(anyString(), anyLong())).thenReturn(false);
//         when(operatorRepository.findById(anyLong())).thenReturn(Optional.empty());

//         assertThrows(NotFoundException.class, () -> {
//             operatorService.updateOperator(1L, request);
//         });
//     }

//     /**
//      * Tests the successful retrieval of all operators.
//      * Verifies that the response status is OK and data is not null.
//      */
//     @Test
//     void testGetAllOperators_Success() {
//         List<Operator> operators = new ArrayList<>();
//         operators.add(Operator.builder().id(1L).name("Operator 1").build());

//         when(operatorRepository.findAll()).thenReturn(operators);

//         MyResponse response = operatorService.getAllOperators();

//         assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
//         assertThat(response.getData()).isNotNull();
//     }

//     /**
//      * Tests the scenario where no operators are found.
//      * Verifies that the response status is OK and an appropriate message is
//      * returned.
//      */
//     @Test
//     void testGetAllOperators_NoOperatorsFound() {
//         when(operatorRepository.findAll()).thenReturn(new ArrayList<>());

//         MyResponse response = operatorService.getAllOperators();

//         assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
//         assertThat(response.getMessage()).isEqualTo("We dont found any operator");
//     }

//     /**
//      * Tests the successful retrieval of an operator by its ID.
//      * Verifies that the response status is OK and data is not null.
//      */
//     @Test
//     void testGetOperatorById_Success() throws NotFoundException {
//         Operator operator = Operator.builder().id(1L).name("Operator 1").build();

//         when(operatorRepository.findById(anyLong())).thenReturn(Optional.of(operator));

//         MyResponse response = operatorService.getOperatorById(1L);

//         assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
//         assertThat(response.getData()).isNotNull();
//     }

//     /**
//      * Tests the scenario where the operator to be retrieved by ID is not found.
//      * Verifies that the NotFoundException is thrown.
//      */
//     @Test
//     void testGetOperatorById_NotFound() {
//         when(operatorRepository.findById(anyLong())).thenReturn(Optional.empty());

//         assertThrows(NotFoundException.class, () -> {
//             operatorService.getOperatorById(1L);
//         });
//     }

//     /**
//      * Tests the successful deletion of an operator by its ID.
//      * Verifies that the response status is OK and the success message is returned.
//      */
//     @Test
//     void testDeleteOperatorById_Success() throws NotFoundException {
//         Operator operator = Operator.builder().id(1L).name("Operator 1").build();

//         when(operatorRepository.findById(anyLong())).thenReturn(Optional.of(operator));

//         MyResponse response = operatorService.deleteOperatorById(1L);

//         assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
//         assertThat(response.getMessage()).isEqualTo("Operator deleted successfully");
//     }

//     /**
//      * Tests the scenario where the operator to be deleted by ID is not found.
//      * Verifies that the NotFoundException is thrown.
//      */
//     @Test
//     void testDeleteOperatorById_NotFound() {
//         when(operatorRepository.findById(anyLong())).thenReturn(Optional.empty());

//         assertThrows(NotFoundException.class, () -> {
//             operatorService.deleteOperatorById(1L);
//         });
//     }

//     /**
//      * Tests the scenario where an operator with the same name already exists.
//      * Verifies that the AlreadyExistException is thrown.
//      */
//     @Test
//     void testIfOperatorExistThrowError_AlreadyExist() {
//         when(operatorRepository.existsByName(anyString())).thenReturn(true);

//         assertThrows(AlreadyExistException.class, () -> {
//             operatorService.ifOperatorExistThrowError("Existing Operator");
//         });
//     }

//     /**
//      * Tests the scenario where an operator with the same name but a different ID
//      * already exists.
//      * Verifies that the AlreadyExistException is thrown.
//      */
//     @Test
//     void testIsOperatorAlreadyExistExceptHerSelf_AlreadyExist() {
//         when(operatorRepository.existsByNameAndIdNot(anyString(), anyLong())).thenReturn(true);

//         assertThrows(AlreadyExistException.class, () -> {
//             operatorService.isOperatorAlreadyExistExceptHerSelf(1L, "Existing Operator");
//         });
//     }

//     /**
//      * Tests the successful finding of an operator by its ID.
//      * Verifies that the found operator is not null and has the correct ID.
//      */
//     @Test
//     void testFindOperatorById_Success() throws NotFoundException {
//         Operator operator = Operator.builder().id(1L).name("Operator 1").build();

//         when(operatorRepository.findById(anyLong())).thenReturn(Optional.of(operator));

//         Operator foundOperator = operatorService.findOperatorById(1L);

//         assertThat(foundOperator).isNotNull();
//         assertThat(foundOperator.getId()).isEqualTo(1L);
//     }

//     /**
//      * Tests the scenario where the operator to be found by ID is not found.
//      * Verifies that the NotFoundException is thrown.
//      */
//     @Test
//     void testFindOperatorById_NotFound() {
//         when(operatorRepository.findById(anyLong())).thenReturn(Optional.empty());

//         assertThrows(NotFoundException.class, () -> {
//             operatorService.findOperatorById(1L);
//         });
//     }
// }
