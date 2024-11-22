// package org.idirtrack.backend.modules.sim;


// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.validation.BindingResult;
// import org.springframework.web.bind.annotation.DeleteMapping;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.PutMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;

// import com.idirtrack.stock_service.basics.BasicException;
// import com.idirtrack.stock_service.basics.BasicResponse;
// import com.idirtrack.stock_service.basics.MessageType;
// import com.idirtrack.stock_service.errors.AlreadyExistException;
// import com.idirtrack.stock_service.errors.NotFoundException;
// import com.idirtrack.stock_service.sim.https.SimRequest;
// import com.idirtrack.stock_service.utils.MyResponse;

// import jakarta.validation.Valid;

// @RestController
// @RequestMapping("/stock-api/sim")
// public class SimController {

//     @Autowired
//     private SimService simService;

//     /**
//      * Endpoint API to get List of SIMs with pagination
//      * 
//      * @param page
//      * @param size
//      * @return ResponseEntity<MyResponse>
//      */
//     @GetMapping("/")
//     public ResponseEntity<MyResponse> getAllSims(@RequestParam(defaultValue = "1") int page,
//             @RequestParam(defaultValue = "5") int size) {
//         MyResponse response = simService.getAllSimsWithPagination(page, size);
//         return ResponseEntity.status(response.getStatus()).body(response);
//     }

//     /**
//      * Endpoint API to get SIM by ID
//      * 
//      * @param id
//      * @return ResponseEntity<MyResponse>
//      * @throws NotFoundException
//      */
//     @GetMapping("/{id}/")
//     public ResponseEntity<MyResponse> getSimById(@PathVariable Long id) throws NotFoundException {
//         MyResponse response = simService.getSimById(id);
//         return ResponseEntity.status(response.getStatus()).body(response);

//     }

//     /**
//      * Endpoint API to create SIM
//      * 
//      * @param simRequest
//      * @param bindingResult
//      * @return
//      * @throws BasicException
//      */
//     @PostMapping("/")
//     public ResponseEntity<MyResponse> createSim(@Valid @RequestBody SimRequest request,
//             BindingResult bindingResult) throws AlreadyExistException, NotFoundException {
//         MyResponse response = simService.createSim(request);
//         return ResponseEntity.status(response.getStatus()).body(response);

//     }

//     /**
//      * Endpoint API to update SIM
//      * 
//      * @param id
//      * @param request
//      * @throws NotFoundException
//      */
//     @PutMapping("/{id}/")
//     public ResponseEntity<MyResponse> updateSim(
//             @PathVariable Long id,
//             @Valid @RequestBody SimRequest request)
//             throws NotFoundException, AlreadyExistException {

//         MyResponse response = simService.updateSim(id, request);
//         return ResponseEntity.status(response.getStatus()).body(response);

//     }

//     /**
//      * Endpoint API to delete SIM
//      * 
//      * @param id
//      * @return ResponseEntity<MyResponse>
//      * @throws NotFoundException
//      */
//     @DeleteMapping("/{id}/")
//     public ResponseEntity<MyResponse> deleteSim(@PathVariable Long id) throws NotFoundException {
//         MyResponse response = simService.deleteSim(id);
//         return ResponseEntity.status(response.getStatus()).body(response);
//     }

//     /**
//      * GET PENDING SIMS
//      * 
//      * @param page
//      * @param size
//      * @return
//      */
//     @GetMapping("/pending/")
//     public ResponseEntity<BasicResponse> getNonInstalledSimsApi(
//             @RequestParam(defaultValue = "1") int page,
//             @RequestParam(defaultValue = "5") int size) {
//         try {
//             BasicResponse response = simService.getAllNonInstalledSims(page, size);
//             return ResponseEntity.status(response.getStatus()).body(response);
//         } catch (BasicException e) {
//             return ResponseEntity.status(e.getResponse().getStatus()).body(e.getResponse());
//         } catch (Exception e) {
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BasicResponse.builder()
//                     .content(null)
//                     .message(e.getMessage())
//                     .messageType(MessageType.ERROR)
//                     .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                     .build());
//         }
//     }

//     @GetMapping("/pending/search/")
//     public ResponseEntity<BasicResponse> searchNonInstalledSimsApi(
//             @RequestParam(value = "query", required = false) String query,
//             @RequestParam(value = "page", defaultValue = "1") int page,
//             @RequestParam(value = "size", defaultValue = "10") int size) {
//         try {
//             BasicResponse response = simService.searchNonInstalledSims(query, page, size);

//             return ResponseEntity.status(response.getStatus()).body(response);

//         } catch (BasicException e) {
//             return ResponseEntity.status(e.getResponse().getStatus()).body(e.getResponse());
//         } catch (Exception e) {
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BasicResponse.builder()
//                     .content(null)
//                     .message(e.getMessage())
//                     .messageType(MessageType.ERROR)
//                     .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                     .build());
//         }
//     }

//     @GetMapping("/search")
//     public ResponseEntity<MyResponse> searchSIMs(
//             @RequestParam(value = "term", required = true) String term,
//             @RequestParam(defaultValue = "1") int page,
//             @RequestParam(defaultValue = "5") int size) {

//         MyResponse response = simService.searchSIMs(term, page, size);
//         return ResponseEntity.status(response.getStatus()).body(response);

//     }

//     @PutMapping("/status/installed/{id}/")
//     public ResponseEntity<BasicResponse> changeSimStatusToInstalledApi(@PathVariable Long id) {
//         try {
//             BasicResponse response = simService.changeSimStatusInstalled(id);
//             return ResponseEntity.status(response.getStatus()).body(response);
//         } catch (BasicException e) {
//             return ResponseEntity.status(e.getResponse().getStatus()).body(e.getResponse());
//         } catch (Exception e) {
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BasicResponse.builder()
//                     .content(null)
//                     .message(e.getMessage())
//                     .messageType(MessageType.ERROR)
//                     .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                     .build());
//         }
//     }

//     /**
//      * CHANGE SIM STATUS
//      */
//     @PutMapping("/status/")
//     public ResponseEntity<BasicResponse> changeSimStatusApi(@RequestParam Long id, @RequestParam String status) {
//         try {
//             BasicResponse response = simService.changeSimStatus(id, status);
//             return ResponseEntity.status(response.getStatus()).body(response);
//         } catch (BasicException e) {
//             return ResponseEntity.status(e.getResponse().getStatus()).body(e.getResponse());
//         } catch (Exception e) {
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BasicResponse.builder()
//                     .content(null)
//                     .message(e.getMessage())
//                     .messageType(MessageType.ERROR)
//                     .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                     .build());
//         }

//     }
// }
