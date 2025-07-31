package felleskap.punkt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/* 
@Data
@AllArgsConstructor
@NoArgsConstructor*/
public class AuthenticationResponse {
    private String token;

    public AuthenticationResponse(String token) {
    this.token = token;
}

}
