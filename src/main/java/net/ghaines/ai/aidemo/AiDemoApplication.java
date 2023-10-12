package net.ghaines.ai.aidemo;

import org.springframework.ai.client.AiClient;
import org.springframework.ai.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@SpringBootApplication
public class AiDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(AiDemoApplication.class, args);
	}

}

@RestController
class AiController {
	private final AiService aiService;

	public AiController(AiService aiService) {
		this.aiService = aiService;
	}

	@GetMapping
	ResponseEntity<String> performAI(@RequestParam(value = "q") String q,
									 @RequestParam(value = "acct") String acct) {
		return ResponseEntity.ok(aiService.performAI(q));
	}

}

@Service
class AiService {
	private final AiClient aiClient;

	public AiService(AiClient aiClient) {
		this.aiClient = aiClient;
	}

	String performAI(String request) {

		var promptQ = """
				Given the following options, which option most closely matches the request? If none of the
				options are similar, respond "Sorry, I cannot help with that."
								
				1. Update an address
				2. Update a phone number
				3. Transfer client to EJC
				4. Enter a branch note
								
				Request: {request}
					""";
		var pt = new PromptTemplate(promptQ);

		/* Some sample questions that tested as expected:
			I just moved and would like to update my account.
			Move an account to EJC
			Enter a note for John Smith
			I switched to Verizon and got a new number
			What is the weather today?
		*/
		var prompt = pt.create(Map.of("request", request));
		var answer = aiClient.generate(prompt).getGeneration().getText();

		var page = switch(answer) {
			case "Option 1. Update an address" -> "addressPage/?conId=1234";
			default -> throw new IllegalStateException("Unexpected value: " + answer);
		};
		return page;
	}
}

@Controller
@RequestMapping
class AiWeb {

	@Value("${speechKey}")
	private String speechKey;

	@GetMapping("/sf")
	String getDemo() {
		return "sf";
	}
	@GetMapping("/acct/{acctId}")
	String getAccount(@PathVariable("acctId") String acctId, Model model) {
		System.out.println(speechKey);
		model.addAttribute(acctId);
		model.addAttribute("speechKey", speechKey);
		return "account";
	}
}
