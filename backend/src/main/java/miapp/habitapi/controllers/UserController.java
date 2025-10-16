package miapp.habitapi.controllers;

import miapp.habitapi.dto.UserSummary;
import miapp.habitapi.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    // GET /api/users/others?myId=1
    @GetMapping("/others")
    public ResponseEntity<?> others(
            @RequestParam Long myId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        try {
            if (page != null && size != null) {
                List<UserSummary> data = service.listOthers(myId, page, size);
                return ResponseEntity.ok(data);
            }
            return ResponseEntity.ok(service.listOthers(myId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }
    
    
    @GetMapping("/{id}/name")
    public ResponseEntity<?> getName(@PathVariable Long id) {
        try {
            String name = service.getNameById(id);
            return ResponseEntity.ok(Map.of("name", name));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
    
}
