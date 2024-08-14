package au.com.telstra.simcardactivator;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
public class SimCardController {

    @PostMapping("/activate-sim")
    public ResponseEntity<String> activateSim(@RequestBody Map<String, String> request) {
        String iccid = request.get("iccid");
        String customerEmail = request.get("customerEmail");

        if (iccid == null || customerEmail == null) {
            return new ResponseEntity<>("Invalid request payload", HttpStatus.BAD_REQUEST);
        }

        Map<String, String> actuatorPayload = new HashMap<>();
        actuatorPayload.put("iccid", iccid);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> actuatorResponse = restTemplate.postForEntity(
                "http://localhost:8444/actuate",
                actuatorPayload,
                Map.class
        );

        Boolean success = (Boolean) Objects.requireNonNull(actuatorResponse.getBody()).get("success");
        if (success != null && success) {
            return new ResponseEntity<>("SIM card activated successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Failed to activate SIM card", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
