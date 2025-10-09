package miApp.habit.auth;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {

  @Autowired JwtService jwt;

  @PostMapping("/login")
  public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest req) {
    // TODO: validar contra BD. Por ahora demo/demo
    if ("demo".equals(req.username) && "demo".equals(req.password)) {
      String token = jwt.generateToken(req.username);
      return ResponseEntity.ok(new TokenResponse(token, req.username));
    }
    return ResponseEntity.status(401).build();
  }

  public static class LoginRequest { public String username; public String password; }
  public static class TokenResponse { public String token; public String username;
    public TokenResponse(String t, String u){ this.token=t; this.username=u; } }
  
  @GetMapping("/me")
  public ResponseEntity<?> me(org.springframework.security.core.Authentication auth) {
    if (auth == null) return ResponseEntity.status(401).build();
    return ResponseEntity.ok(java.util.Map.of("username", auth.getName()));
  }

}

